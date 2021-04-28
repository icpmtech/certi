/*
 * $Id: PlanCMDeletePlanReferencesActionBean.java,v 1.2 2009/10/29 13:48:25 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/29 13:48:25 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.certitools.ContractService;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.ModuleType;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;

import java.util.Collection;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class PlanCMDeletePlanReferencesActionBean extends AbstractActionBean {

    private static final Logger LOGGER = Logger.getInstance(PlanCMDeletePlanReferencesActionBean.class);

    public enum DeleteType {
        BOTH, ONLINE, OFFLINE
    }

    @Validate(on = "deletePlanReferences", required = true)
    private Long contractId;
    @Validate(on = "deletePlanReferences", required = true)
    private DeleteType deleteType;

    private Collection<Contract> contracts;

    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @DefaultHandler
    @Secure(roles = "administrator")
    public Resolution deletePlanReferencesForm() {
        return new ForwardResolution("/deletePlanReferences.jsp");
    }

    @Secure(roles = "administrator")
    public Resolution deletePlanReferences() throws ObjectNotFoundException, JackrabbitException {

        LOGGER.info("Process to Delete Folder References started....");

        Contract contract = contractService.findById(contractId);
        if (!contract.getModule().getModuleType().equals(ModuleType.LEGISLATION)) {

            planService.deletePlanFolderReferences(contractId, contract.getModule().getModuleType(),
                    deleteType.toString());

            LOGGER.info("Process to Delete Folder References finished successfully....");
            getContext().getMessages().add(new LocalizableMessage("common.sucess"));
        } else {
            getContext().getValidationErrors().addGlobalError(new SimpleError("Contract to delete is LEGISLATION"));
        }
        return new RedirectResolution(PlanCMDeletePlanReferencesActionBean.class).flash(this);
    }

    @After(stages = LifecycleStage.BindingAndValidation)
    public void setVars() {
        contracts = contractService.findAll();
        setAttribute("firstVisit", false);
    }

    public void fillLookupFields() {

    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Collection<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(Collection<Contract> contracts) {
        this.contracts = contracts;
    }

    public DeleteType getDeleteType() {
        return deleteType;
    }

    public void setDeleteType(DeleteType deleteType) {
        this.deleteType = deleteType;
    }

    public ContractService getContractService() {
        return contractService;
    }

    public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public PlanService getPlanService() {
        return planService;
    }

    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }
}
