/*
 * $Id: PlanCMMigrationActionBean.java,v 1.4 2013/12/16 18:35:05 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2013/12/16 18:35:05 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.certitools.CompanyService;
import com.criticalsoftware.certitools.business.certitools.ContractService;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.jcr.Plan;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.DownloadFileResolution;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.util.PlanUtils;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.jackrabbit.ocm.nodemanagement.impl.RepositoryUtil;

import javax.jcr.ItemExistsException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * PEI Migration (export / import) Action Bean
 *
 * @author pjfsilva
 */
public class PlanCMMigrationActionBean extends AbstractActionBean implements ValidationErrorHandler {
    private List<Contract> contractsSource;
    private List<Company> companiesSource;

    private List<Contract> contractsTarget;
    private List<Company> companiesTarget;

    @Validate(required = true, on = {"exportPlan"})
    private Long companySourceId;
    @Validate(required = true, on = {"importPlan"})
    private Long companyTargetId;

    private Long contractSourceId;
    private Long contractTargetId;
    public FileBean importFile;

    private Long companyId;

    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;


    @DefaultHandler
    @Secure(roles = "peimanager")
    public Resolution migrationForm() throws ObjectNotFoundException, CertitoolsAuthorizationException {
        setHelpId("#pei-migration");

        loadLists();
        if (companiesSource == null || companiesSource.size() == 0) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("pei.permission.add.error.nocontracts"));
        }
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMMigration.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager")
    public Resolution importPlan() throws NamingException, RepositoryException, ItemExistsException, IOException,
            ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {

        planService.importPlan(getUserInSession(), importFile.getInputStream(), contractTargetId,
                getModuleTypeFromEnum());

        planService.processImportedPlan(getUserInSession(), importFile.getInputStream(), contractTargetId,
                getModuleTypeFromEnum());

        getContext().getMessages().add(new LocalizableMessage("pei.migration.importSucess"));
        return new RedirectResolution(PlanCMMigrationActionBean.class).addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager")
    public Resolution exportPlan() throws ObjectNotFoundException, CertitoolsAuthorizationException,
            JackrabbitException, NamingException, RepositoryException, IOException {
        Plan plan = planService.find(getUserInSession(), contractSourceId, true, getModuleTypeFromEnum());
        String planPath = plan.getPath();
        ByteArrayOutputStream baosTemp = new ByteArrayOutputStream();
        ByteArrayOutputStream baosZip = new ByteArrayOutputStream();

        Repository repository = (Repository) new InitialContext().lookup("java:jcr/local");
        Session session = RepositoryUtil.login(repository, "superuser", "");
        session.exportSystemView(planPath, baosTemp, false, false);

        // create zip file
        ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(baosZip);
        zaos.setFallbackToUTF8(true);
        zaos.setUseLanguageEncodingFlag(true);
        zaos.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS);

        // add the jackrabbit plan file
        ZipArchiveEntry ze = new ZipArchiveEntry(PlanUtils.IMPORT_PLAN_FILE);
        zaos.putArchiveEntry(ze);
        zaos.write(baosTemp.toByteArray());
        zaos.closeArchiveEntry();

        // add the properties file
        ZipArchiveEntry ze2 = new ZipArchiveEntry(PlanUtils.IMPORT_PROPERTIES_FILE);
        zaos.putArchiveEntry(ze2);
        zaos.write(createPropertyFile(plan).toByteArray());
        zaos.closeArchiveEntry();

        zaos.flush();
        zaos.close();


        return new DownloadFileResolution("application/zip", baosZip).setFilename(plan.getPlanName() + "-" + plan.getName() + ".zip");
    }

    private ByteArrayOutputStream createPropertyFile(Plan plan) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("oldId", plan.getName());
        properties.setProperty("oldModuleType", plan.getModuleType());
        properties.setProperty("oldPlanName", plan.getPlanName());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        properties.store(baos, "Export plan properties file");
        return baos;
    }


    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        setPlanMenu(MenuItem.Item.SUB_MENU_PEI_MIGRATION, MenuItem.Item.SUB_MENU_SAFETY_MIGRATION,
                MenuItem.Item.SUB_MENU_PSI_MIGRATION, MenuItem.Item.SUB_MENU_GSC_MIGRATION);
    }

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        loadLists();
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMMigration.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    private void loadLists() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        contractsSource = new ArrayList<Contract>();
        contractsTarget = new ArrayList<Contract>();
        companiesSource =
                new ArrayList<Company>(
                        companyService.findAllWithPlan(getUserInSession(), getModuleTypeFromEnum(), false));
        companiesTarget = companiesSource;

        if (companySourceId == null) {
            if (companiesSource.size() > 0) {
                contractsSource = (List<Contract>) contractService
                        .findAllPlanWithUserContractAllowed(companiesSource.get(0).getId(), getUserInSession(),
                                getModuleTypeFromEnum());
            }
        } else {
            contractsSource =
                    (List<Contract>) contractService
                            .findAllPlanWithUserContractAllowed(companySourceId, getUserInSession(),
                                    getModuleTypeFromEnum());
        }

        if (companyTargetId == null) {
            if (companiesTarget.size() > 0) {
                contractsTarget = (List<Contract>) contractService
                        .findAllPlanWithUserContractAllowed(companiesTarget.get(0).getId(), getUserInSession(),
                                getModuleTypeFromEnum());
            }
        } else {
            contractsTarget =
                    (List<Contract>) contractService
                            .findAllPlanWithUserContractAllowed(companyTargetId, getUserInSession(),
                                    getModuleTypeFromEnum());
        }
    }

    public List<Contract> getContractsSource() {
        return contractsSource;
    }

    public void setContractsSource(List<Contract> contractsSource) {
        this.contractsSource = contractsSource;
    }

    public List<Company> getCompaniesSource() {
        return companiesSource;
    }

    public void setCompaniesSource(List<Company> companiesSource) {
        this.companiesSource = companiesSource;
    }

    public List<Contract> getContractsTarget() {
        return contractsTarget;
    }

    public void setContractsTarget(List<Contract> contractsTarget) {
        this.contractsTarget = contractsTarget;
    }

    public List<Company> getCompaniesTarget() {
        return companiesTarget;
    }

    public void setCompaniesTarget(List<Company> companiesTarget) {
        this.companiesTarget = companiesTarget;
    }

    public Long getCompanySourceId() {
        return companySourceId;
    }

    public void setCompanySourceId(Long companySourceId) {
        this.companySourceId = companySourceId;
    }

    public Long getCompanyTargetId() {
        return companyTargetId;
    }

    public void setCompanyTargetId(Long companyTargetId) {
        this.companyTargetId = companyTargetId;
    }

    public Long getContractSourceId() {
        return contractSourceId;
    }

    public void setContractSourceId(Long contractSourceId) {
        this.contractSourceId = contractSourceId;
    }

    public Long getContractTargetId() {
        return contractTargetId;
    }

    public void setContractTargetId(Long contractTargetId) {
        this.contractTargetId = contractTargetId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public ContractService getContractService() {
        return contractService;
    }

    public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public PlanService getPeiService() {
        return planService;
    }

    public void setPeiService(PlanService planService) {
        this.planService = planService;
    }

    public PlanService getPlanService() {
        return planService;
    }

    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }
}