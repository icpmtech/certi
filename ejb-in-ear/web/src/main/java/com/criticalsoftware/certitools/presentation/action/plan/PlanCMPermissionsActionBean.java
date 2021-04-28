/*
 * $Id: PlanCMPermissionsActionBean.java,v 1.11 2012/06/12 19:13:53 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2012/06/12 19:13:53 $
 * Last changed by $Author: pjfsilva $
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
import com.criticalsoftware.certitools.entities.Permission;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PlanUtils;
import com.criticalsoftware.certitools.util.ConfigurationProperties;
import com.criticalsoftware.certitools.util.TreeNode;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.ajax.JavaScriptResolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Permissions Management
 *
 * @author haraujo
 */
public class PlanCMPermissionsActionBean extends AbstractActionBean {
    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;
    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;
    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    private List<Company> companies;
    private Collection<Contract> contracts;
    private Long companyId;
    private Long contractId;
    private Collection<Permission> permissions;
    private String permission;
    private List<TreeNode> nodes;
    private Long permissionId;
    private List<TreeNode> permissionsFullSchemaUsage;

    @DefaultHandler
    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution view() throws CertitoolsAuthorizationException, ObjectNotFoundException, JackrabbitException {
        setHelpId("#pei-permissions");
        loadLists();

        if (companies != null && companies.size() > 0) {
            permissions = planService.findPermissions(getUserInSession(), contractId, getModuleTypeFromEnum());
        } else {
            getContext().getValidationErrors()
                    .add("permission", new LocalizableError("pei.permission.add.error.nocontracts"));
        }

        return new ForwardResolution("/WEB-INF/jsps/plan/planCMPermissions.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution findPermissionUsages()
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException {
        nodes = planService.findPermissionUsages(getUserInSession(), permissionId, getModuleTypeFromEnum());
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMPermissionUsages.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution deletePermission() throws CertitoolsAuthorizationException, ObjectNotFoundException,
            JackrabbitException {
        setHelpId("#pei-permissions");

        loadLists();
        permissions = planService.findPermissions(getUserInSession(), contractId, getModuleTypeFromEnum());

        //Cannot remove this Permission
        if (permission.equals(ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey())) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("pei.permission.delete.error.permissionPeiManager",
                            "\"" + ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey() + "\""));
            return new ForwardResolution("/WEB-INF/jsps/plan/planCMPermissions.jsp")
                    .addParameter("planModuleType", getPlanModuleType());
        }
        //Permission used in user contract
        if (planService.isPermissionInActiveUserContract(permission, contractId, getUserInSession(),
                getModuleTypeFromEnum())) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("pei.permission.delete.error.inuse.userContract"));
        }
        if (planService.isPermissionInActivePEI(permission, contractId, getUserInSession(), getModuleTypeFromEnum())) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("pei.permission.delete.error.inuse.pei"));
        }
        if (getContext().getValidationErrors() != null && !getContext().getValidationErrors().isEmpty()) {
            return new ForwardResolution("/WEB-INF/jsps/plan/planCMPermissions.jsp")
                    .addParameter("planModuleType", getPlanModuleType());
        } else {
            planService.deletePermission(permission, contractId, getUserInSession(), getModuleTypeFromEnum());
            getContext().getMessages().add(new LocalizableMessage("pei.permission.delete.success"));
            return new RedirectResolution(PlanCMPermissionsActionBean.class).flash(this)
                    .addParameter("planModuleType", getPlanModuleType());
        }
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution addPermission()
            throws CertitoolsAuthorizationException, BusinessException, ObjectNotFoundException, JackrabbitException {
        setHelpId("#pei-permissions");

        loadLists();

        if (!StringUtils.isBlank(permission)) {

            if (permission.length() > 255) {
                getContext().getValidationErrors()
                        .add("permission", new LocalizableError("pei.permission.add.error.maxlength"));
                return new ForwardResolution("/WEB-INF/jsps/plan/planCMPermissions.jsp")
                        .addParameter("planModuleType", getPlanModuleType());
            }
            try {
                planService.insertPermission(permission, contractId, getUserInSession(), getModuleTypeFromEnum());
                getContext().getMessages().add(new LocalizableMessage("pei.permission.add.success"));
            } catch (BusinessException e) {
                getContext().getValidationErrors()
                        .add("permission", new LocalizableError("pei.permission.add.error.exists"));
                permissions = planService.findPermissions(getUserInSession(), contractId, getModuleTypeFromEnum());
                return new ForwardResolution("/WEB-INF/jsps/plan/planCMPermissions.jsp")
                        .addParameter("planModuleType", getPlanModuleType());
            }
        } else {
            getContext().getValidationErrors()
                    .add("permission", new LocalizableError("pei.permission.add.error.empty"));
            permissions = planService.findPermissions(getUserInSession(), contractId, getModuleTypeFromEnum());
            return new ForwardResolution("/WEB-INF/jsps/plan/planCMPermissions.jsp")
                    .addParameter("planModuleType", getPlanModuleType());
        }
        permissions = planService.findPermissions(getUserInSession(), contractId, getModuleTypeFromEnum());
        return new RedirectResolution(PlanCMPermissionsActionBean.class).flash(this)
                .addParameter("planModuleType", getPlanModuleType())
                .addParameter("contractId", contractId)
                .addParameter("companyId", companyId);
    }

    @Secure(roles = "peimanager,clientpeimanager")
    public Resolution loadCompanyContracts() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        contracts = new ArrayList<Contract>();

        if (companyId != null) {
            contracts = contractService
                    .findAllPlanWithUserContractAllowed(companyId, getUserInSession(), getModuleTypeFromEnum());
            contracts = PlanUtils.cleanContractsForJavascriptResolution(contracts);
        }

        return new JavaScriptResolution(contracts, Company.class, Date.class, UserContract.class, Module.class);
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        setPlanMenu(MenuItem.Item.SUB_MENU_PEI_PERMISSIONS, MenuItem.Item.SUB_MENU_SAFETY_PERMISSIONS,
                MenuItem.Item.SUB_MENU_PSI_PERMISSIONS, MenuItem.Item.SUB_MENU_GSC_PERMISSIONS);
    }

    // TODO update according to PEI CM (list already is filtered)
    private void loadLists() throws CertitoolsAuthorizationException, ObjectNotFoundException, JackrabbitException {
        Collection<Contract> allContractAllowed = new ArrayList<Contract>();
        contracts = new ArrayList<Contract>();
        companies = new ArrayList<Company>(companyService.findAllWithPlan(getUserInSession(), getModuleTypeFromEnum(),
                false));

        if (companyId == null) {
            if (companies.size() > 0) {
                allContractAllowed = contractService
                        .findAllPlanWithUserContractAllowed(companies.get(0).getId(), getUserInSession(),
                                getModuleTypeFromEnum());
            }
        } else {
            allContractAllowed = contractService.findAllPlanWithUserContractAllowed(companyId, getUserInSession(),
                    getModuleTypeFromEnum());
        }

        boolean first = true;
        for (Contract c : allContractAllowed) {
            if (c.getModule().getModuleType().equals(getModuleTypeFromEnum())) {
                if (contractId == null) {
                    if (first) {
                        contractId = c.getId();
                        first = false;
                    }
                }
                contracts.add(c);
            }
        }

        // if contracts list is empty, remove the company
        // TODO jp-gomes remove this and do it in companyService.findAllWithPlan
        if (companies.size() == 1 && (contracts == null || contracts.size() <= 0)) {
            companies.remove(0);
        }

        if (contractId != null) {
            permissionsFullSchemaUsage =
                    planService.findPlanPermissionFullSchema(getUserInSession(), contractId, getModuleTypeFromEnum());
        }
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public void setPeiService(PlanService planService) {
        this.planService = planService;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public Collection<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(Collection<Contract> contracts) {
        this.contracts = contracts;
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

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<TreeNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<TreeNode> nodes) {
        this.nodes = nodes;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public PlanService getPlanService() {
        return planService;
    }

    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }

    public List<TreeNode> getPermissionsFullSchemaUsage() {
        return permissionsFullSchemaUsage;
    }

    public void setPermissionsFullSchemaUsage(List<TreeNode> permissionsFullSchemaUsage) {
        this.permissionsFullSchemaUsage = permissionsFullSchemaUsage;
    }
}
