/*
 * $Id: PlanCMCopyActionBean.java,v 1.12 2012/05/28 16:50:38 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2012/05/28 16:50:38 $
 * Last changed by $Author: d-marques $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.certitools.CompanyService;
import com.criticalsoftware.certitools.business.certitools.ContractService;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.IsReferencedException;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PlanUtils;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.ajax.JavaScriptResolution;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * PEI Copy Action Bean
 *
 * @author jp-gomes
 */
public class PlanCMCopyActionBean extends AbstractActionBean implements ValidationErrorHandler {

    private List<Contract> contractsSource;
    private List<Company> companiesSource;

    private List<Contract> contractsTarget;
    private List<Company> companiesTarget;

    @Validate(required = true, on = {"copyPEI"})
    private Long companySourceId;
    @Validate(required = true, on = {"copyPEI"})
    private Long companyTargetId;

    private Long contractSourceId;
    private Long contractTargetId;

    private Long companyId;

    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @DefaultHandler
    @Secure(roles = "peimanager")
    public Resolution copyForm() throws ObjectNotFoundException, CertitoolsAuthorizationException {
        setHelpId("#pei-copy");

        loadLists();
        if (companiesSource == null || companiesSource.size() == 0) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("pei.permission.add.error.nocontracts"));
        }
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMCopy.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager")
    public Resolution copyPEI()
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException, BusinessException {
        setHelpId("#pei-copy");

        try {
            planService.copyPlan(contractSourceId, contractTargetId, getModuleTypeFromEnum());
            getContext().getMessages().add(new LocalizableMessage("pei.copy.waitPanel.sucess"));
        } catch (IsReferencedException e) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("pei.copy.error." + e.getType()));
            loadLists();
            return new ForwardResolution("/WEB-INF/jsps/plan/planCMCopy.jsp")
                    .addParameter("planModuleType", getPlanModuleType());
        }
        return new RedirectResolution(PlanCMCopyActionBean.class).addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager")
    public Resolution loadCompanyContracts() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        List<Contract> contracts = new ArrayList<Contract>();

        if (companyId != null) {
            contracts = new ArrayList<Contract>(contractService
                    .findAllPlanWithUserContractAllowed(companyId, getUserInSession(), getModuleTypeFromEnum()));
            contracts = (List<Contract>) PlanUtils.cleanContractsForJavascriptResolution(contracts);
        }

        return new JavaScriptResolution(contracts, Company.class, Date.class, UserContract.class, Module.class);
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        setPlanMenu(MenuItem.Item.SUB_MENU_PEI_COPY, MenuItem.Item.SUB_MENU_SAFETY_COPY,
                MenuItem.Item.SUB_MENU_PSI_COPY, MenuItem.Item.SUB_MENU_GSC_COPY);
    }

    @ValidationMethod(on = "copyPEI")
    public void validateCopyPEI(ValidationErrors errors) {
        if (contractSourceId.equals(contractTargetId)) {
            errors.add("contractTargetId", new LocalizableError("error.pei.copy.equalsSourceAndTarget"));
        }
    }

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        loadLists();
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMCopy.jsp")
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