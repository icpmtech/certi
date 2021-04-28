/*
 * $Id: PlanCMActionBean.java,v 1.11 2012/05/28 16:50:38 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/05/28 16:50:38 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.certitools.CompanyService;
import com.criticalsoftware.certitools.business.certitools.ContractService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.entities.jcr.Plan;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PlanUtils;
import com.criticalsoftware.certitools.util.TreeNode;
import com.criticalsoftware.certitools.util.TreeNodeComparatorByOrder;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.ajax.JavaScriptResolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Pei Backoffice Action Bean
 *
 * @author jp-gomes
 */

public class PlanCMActionBean extends AbstractActionBean {

    private Plan pei;
    private List<TreeNode> sections;
    private List<Company> companies;
    private List<Contract> contracts;
    private Long companyId;
    private Long contractId;
    private Boolean openTreeDirectFolder;
    private String path;

    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @DefaultHandler
    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution viewPeiCM()
            throws JackrabbitException, CertitoolsAuthorizationException, BusinessException, ObjectNotFoundException {
        setHelpId("#pei-cm");

        loadLists();
        if (companies != null && companies.size() > 0) {
            pei = planService.find(getUserInSession(), contractId, getModuleTypeFromEnum());

            //Open PEI with only first 9 sections open
            if (openTreeDirectFolder == null || !openTreeDirectFolder) {
                sections = planService
                        .findOfflineSectionsForMenu(getUserInSession(), pei.getPath(), getModuleTypeFromEnum());
                Collections.sort(sections, new TreeNodeComparatorByOrder());
            } else {
                //Open PEI with requested path selected
                sections = planService.findOpenTreeToPath(path, getUserInSession(), getModuleTypeFromEnum());
                setAttribute("paths", PlanUtils.findAllPathsToFolder(path));
            }
        } else {
            getContext().getValidationErrors()
                    .add("permission", new LocalizableError("pei.permission.add.error.nocontracts"));
        }
        return new ForwardResolution("/WEB-INF/jsps/plan/planCM.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution viewPeiCMFromPreview()
            throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException, JackrabbitException {
        if (contractId == null) {
            throw new BusinessException("error trying to view pei. Cause: contractId is null");
        }

        if (path == null) {
            path = "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + getModuleTypeFromEnum() + contractId;
        }

        Contract contract = contractService.findById(contractId);
        companyId = contract.getCompany().getId();
        openTreeDirectFolder = true;

        return viewPeiCM();
    }


    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution loadCompanyContracts() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        contracts = new ArrayList<Contract>();

        if (companyId != null) {
            contracts = new ArrayList<Contract>(contractService
                    .findAllPlanWithUserContractAllowed(companyId, getUserInSession(), getModuleTypeFromEnum()));
            contracts = (List<Contract>) PlanUtils.cleanContractsForJavascriptResolution(contracts);
        }

        return new JavaScriptResolution(contracts, Company.class, Date.class, UserContract.class, Module.class);
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        setPlanMenu(MenuItem.Item.SUB_MENU_PEI_ADMIN, MenuItem.Item.SUB_MENU_SAFETY_ADMIN,
                MenuItem.Item.SUB_MENU_PSI_ADMIN, MenuItem.Item.SUB_MENU_GSC_ADMIN);
    }

    private void loadLists() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        contracts = new ArrayList<Contract>();
        companies = new ArrayList<Company>(companyService.findAllWithPlan(getUserInSession(), getModuleTypeFromEnum(),
                false));

        if (companyId == null) {
            if (companies.size() > 0) {
                contracts = (List<Contract>) contractService
                        .findAllPlanWithUserContractAllowed(companies.get(0).getId(), getUserInSession(),
                                getModuleTypeFromEnum());
            }
        } else {
            contracts =
                    (List<Contract>) contractService.findAllPlanWithUserContractAllowed(companyId, getUserInSession(),
                            getModuleTypeFromEnum());
        }

        // if contracts list is empty, remove the company
        // TODO jp-gomes remove this and do it in companyService.findAllWithPlan
        if (companies.size() == 1 && (contracts == null || contracts.size() <= 0)) {
            companies.remove(0);
        }

        if (contractId == null && contracts.size() > 0) {
            contractId = contracts.get(0).getId();
        }
    }

    public Plan getPei() {
        return pei;
    }

    public void setPei(Plan pei) {
        this.pei = pei;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public PlanService getPeiService() {
        return planService;
    }

    public void setPeiService(PlanService planService) {
        this.planService = planService;
    }

    public List<TreeNode> getSections() {
        return sections;
    }

    public void setSections(List<TreeNode> sections) {
        this.sections = sections;
    }

    public Boolean getOpenTreeDirectFolder() {
        return openTreeDirectFolder;
    }

    public void setOpenTreeDirectFolder(Boolean openTreeDirectFolder) {
        this.openTreeDirectFolder = openTreeDirectFolder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
