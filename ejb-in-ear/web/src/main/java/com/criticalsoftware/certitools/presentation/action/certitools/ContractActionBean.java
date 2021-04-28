/*
 * $Id: ContractActionBean.java,v 1.35 2012/05/28 16:50:38 d-marques Exp $
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
package com.criticalsoftware.certitools.presentation.action.certitools;

import com.criticalsoftware.certitools.business.certitools.CompanyService;
import com.criticalsoftware.certitools.business.certitools.ContractService;
import com.criticalsoftware.certitools.business.exception.*;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.Permission;
import com.criticalsoftware.certitools.entities.sm.SubModule;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PTDateTypeConverter;
import com.criticalsoftware.certitools.util.ConfigurationProperties;
import com.criticalsoftware.certitools.util.File;
import com.criticalsoftware.certitools.util.ModuleType;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Contract Action Bean
 *
 * @author pjfsilva
 */
public class ContractActionBean extends AbstractActionBean implements ValidationErrorHandler {

    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    private Company company;

    // Contract related fields
    @ValidateNestedProperties({
            @Validate(field = "number", maxlength = 128, required = true, on = "insertContract"),
            @Validate(field = "contractDesignation", maxlength = 255, required = true, on = "insertContract"),
            @Validate(field = "company.id", required = true, on = "insertContract"),
            @Validate(field = "module.moduleType", maxlength = 255, required = true, on = "insertContract"),
            @Validate(field = "licenses", minvalue = 1, maxvalue = Integer.MAX_VALUE, required = true,
                    on = "insertContract"),
            @Validate(field = "validityStartDate", required = true,
                    on = "insertContract"),
            @Validate(field = "validityEndDate", required = true, converter = PTDateTypeConverter.class,
                    on = "insertContract"),
            @Validate(field = "value", minvalue = 0, on = "insertContract"),
            @Validate(field = "contractDesignationMaintenance", maxlength = 255, on = "insertContract"),
            @Validate(field = "valueMaintenance", minvalue = 0, on = "insertContract"),
            @Validate(field = "contactName", required = true, maxlength = 128, on = "insertContract"),
            @Validate(field = "contactPosition", maxlength = 128, on = "insertContract"),
            @Validate(field = "contactEmail", converter = EmailTypeConverter.class, maxlength = 255,
                    on = "insertContract"),
            @Validate(field = "userRegisterCode", maxlength = 128, on = "insertContract"),
            @Validate(field = "userRegisterDomains", maxlength = 256, on = "insertContract"),
            @Validate(field = "contactPhone", maxlength = 32, on = "insertContract"),
            @Validate(field = "firstInactivityMessageTerm", required = true, converter = IntegerTypeConverter.class,
                    on = "insertContractInactivitySettings"),
            @Validate(field = "secondInactivityMessageTerm", required = true, converter = IntegerTypeConverter.class,
                    on = "insertContractInactivitySettings"),
            @Validate(field = "deleteUserTerm", required = true, converter = IntegerTypeConverter.class,
                    on = "insertContractInactivitySettings"),
            @Validate(field = "menuLabel", maxlength = 64, required = false, on = "insertContract")
    })
    private Contract contract;
    private List<Module> modules;
    private List<SubModule> subModules;
    private List<String> selectedSubModules;

    private List<Long> userRegisterPermissions;

    private boolean edit;

    private String letter;

    private FileBean file;

    private Collection<Contract> contracts;

    @DefaultHandler
    @DontValidate
    public Resolution viewCompanies() {
        return new RedirectResolution(CompanyActionBean.class).addParameter("company.id", company.getId())
                .addParameter("letter", letter);
    }

    @Secure(roles = "contractmanager")
    public Resolution deleteContract()
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException, BusinessException {

        try {
            contractService.deleteContract(contract);
            getContext().getMessages().add(new LocalizableMessage("contract.del.sucess"));
        } catch (IsReferencedException e) {
            if (e.getType() == IsReferencedException.Type.USER) {
                return new RedirectResolution(CompanyActionBean.class, "viewCompanies")
                        .addParameter("company.id", company.getId()).addParameter("letter", letter)
                        .addParameter("error", "contract.del.isReferenced.user");
            } else if (e.getType() == IsReferencedException.Type.PLAN) {
                return new RedirectResolution(CompanyActionBean.class, "viewCompanies")
                        .addParameter("company.id", company.getId()).addParameter("letter", letter)
                        .addParameter("error", "contract.del.isReferenced.plan");
            }
        }

        return new RedirectResolution(CompanyActionBean.class).addParameter("letter", letter)
                .addParameter("company.id", company.getId());
    }

    public Resolution cancel() {
        return new RedirectResolution(CompanyActionBean.class).addParameter("letter", letter)
                .addParameter("company.id", company.getId());
    }

    @Secure(roles = "clientcontractmanager,contractmanager")
    public Resolution insertContractInactivitySettingsForm() throws ObjectNotFoundException {
        contract = contractService.findById(contract.getId());
        setHelpId("#contract-inactivity");

        return new ForwardResolution("/WEB-INF/jsps/certitools/contractInactivitySettings.jsp");
    }

    @Secure(roles = "clientcontractmanager,contractmanager")
    public Resolution insertContractInactivitySettings() throws ObjectNotFoundException {
        contractService.updateContractInactivitySettings(contract);

        getContext().getMessages().add(new LocalizableMessage("contract.inactivitySetting.sucess"));
        return new RedirectResolution(CompanyActionBean.class).addParameter("letter", letter)
                .addParameter("company.id", company.getId()).flash(this);
    }

    @Secure(roles = "contractmanager")
    public Resolution updateContractForm() throws ObjectNotFoundException, CertitoolsAuthorizationException {
        edit = true;
        contract = contractService.findById(contract.getId());
        contract.setModule(loadModule(contract.getModule().getModuleType()));

        //load submodules
        selectedSubModules = new ArrayList<String>();
        for (SubModule subModule : contract.getSubModules()) {
            selectedSubModules.add(subModule.getSubModuleType().name());
        }

        Collection<Permission> contractPermissions = contractService.findContractPermissions(contract.getId());
        if (contractPermissions != null && contractPermissions.size() > 0) {
            contract.setContractPermissions(contractPermissions);

            userRegisterPermissions = new ArrayList<Long>();
            for (Permission contractPermission : contractPermissions) {
                if (contractPermission.isUserRegisterBasePermission()) {
                    userRegisterPermissions.add(contractPermission.getId());
                }
            }
            setAttribute("specialPermission", ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey());
        }

        return insertContractForm();
    }

    @Secure(roles = "contractmanager")
    public Resolution insertContractForm() throws ObjectNotFoundException, CertitoolsAuthorizationException {
        loadModules();
        company = companyService.findAllowed(company.getId(), getUserInSession());
        Collection<Contract> allContracts = contractService.findByCompanyId(company.getId(), getUserInSession());

        // filter contracts (remove duplicate contacts
        HashMap<String, Contract> contractsHash = new HashMap<String, Contract>();
        for (Contract c : allContracts) {
            contractsHash
                    .put(c.getContactName() + c.getContactPosition() + c.getContactEmail() + c.getContactPhone(), c);
        }
        contracts = new ArrayList<Contract>();
        for (Contract c : contractsHash.values()) {
            contracts.add(c);
        }

        if (edit) {
            setHelpId("#edit-contract");
        } else {
            setHelpId("#add-contract");
        }

        return new ForwardResolution("/WEB-INF/jsps/certitools/contractsInsert.jsp");
    }

    @Secure(roles = "contractmanager")
    public Resolution insertContract() throws ObjectNotFoundException, IOException, JackrabbitException {
        // create the file object
        File f = null;
        if (file != null) {
            f = new File(null, file.getContentType(), file.getInputStream(), file.getFileName());
        }

        if (edit) {
            // Update contract base permissions
            contractService.updateContractBasePermissions(contract.getId(), userRegisterPermissions);
            //set contract submodules
            if (contract.getModule().getModuleType() == ModuleType.GSC && selectedSubModules != null) {
                List<SubModule> subModules = new ArrayList<SubModule>();
                for (String type : selectedSubModules) {
                    subModules.add(new SubModule(SubModuleType.valueOf(type)));
                }
                contract.setSubModules(subModules);
            }
            contractService.updateContract(contract, f);
        } else {
            //set contract submodules
            if (contract.getModule().getModuleType() == ModuleType.GSC && selectedSubModules != null) {
                List<SubModule> subModules = new ArrayList<SubModule>();
                for (String type : selectedSubModules) {
                    subModules.add(new SubModule(SubModuleType.valueOf(type)));
                }
                contract.setSubModules(subModules);
            }
            contractService.insertContract(contract, f);
        }

        getContext().getMessages().add(new LocalizableMessage("contract.add.sucess"));


        return new RedirectResolution(CompanyActionBean.class).addParameter("company.id", company.getId())
                .addParameter("letter", "");
    }

    @ValidationMethod(on = "insertContractInactivitySettings", when = ValidationState.NO_ERRORS)
    public void validateInsertContractInactivitySettings(ValidationErrors errors)
            throws CertitoolsAuthorizationException {
        //Inactivity Settings Validations
        if (contract.getFirstInactivityMessageTerm() != 0 && contract.getSecondInactivityMessageTerm() != 0
                && contract.getDeleteUserTerm() != 0) {

            if (contract.getFirstInactivityMessageTemplateSubject() == null || contract
                    .getFirstInactivityMessageTemplateSubject().isEmpty()) {
                errors.add("contract.firstInactivityMessageTemplateSubject",
                        new LocalizableError("custom.validation.required.valueNotPresent",
                                LocalizationUtility.getLocalizedFieldName(
                                        "contract.firstInactivityMessageTemplateSubject", null, null,
                                        getContext().getLocale())));
            } else if (contract.getFirstInactivityMessageTemplateSubject().length() > 200) {
                errors.add("contract.firstInactivityMessageTemplateSubject",
                        new LocalizableError("validation.maxvalue.valueAboveMaximum", 200));
            }
            if (contract.getFirstInactivityMessageTemplateBody() == null || contract
                    .getFirstInactivityMessageTemplateBody().isEmpty()) {
                errors.add("contract.firstInactivityMessageTemplateBody",
                        new LocalizableError("custom.validation.required.valueNotPresent",
                                LocalizationUtility.getLocalizedFieldName(
                                        "contract.firstInactivityMessageTemplateBody", null, null,
                                        getContext().getLocale())));
            } else if (contract.getFirstInactivityMessageTemplateBody().length() > 500) {
                errors.add("contract.firstInactivityMessageTemplateBody",
                        new LocalizableError("validation.maxvalue.valueAboveMaximum", 500));
            }

            if (contract.getSecondInactivityMessageTemplateSubject() == null || contract
                    .getSecondInactivityMessageTemplateSubject().isEmpty()) {
                errors.add("contract.secondInactivityMessageTemplateSubject",
                        new LocalizableError("custom.validation.required.valueNotPresent",
                                LocalizationUtility.getLocalizedFieldName(
                                        "contract.secondInactivityMessageTemplateSubject", null, null,
                                        getContext().getLocale())));
            } else if (contract.getSecondInactivityMessageTemplateSubject().length() > 200) {
                errors.add("contract.secondInactivityMessageTemplateSubject",
                        new LocalizableError("validation.maxvalue.valueAboveMaximum", 200));
            }
            if (contract.getSecondInactivityMessageTemplateBody() == null || contract
                    .getSecondInactivityMessageTemplateBody().isEmpty()) {
                errors.add("contract.secondInactivityMessageTemplateBody",
                        new LocalizableError("custom.validation.required.valueNotPresent",
                                LocalizationUtility.getLocalizedFieldName(
                                        "contract.secondInactivityMessageTemplateBody", null, null,
                                        getContext().getLocale())));

            } else if (contract.getSecondInactivityMessageTemplateBody().length() > 500) {
                errors.add("contract.secondInactivityMessageTemplateBody",
                        new LocalizableError("validation.maxvalue.valueAboveMaximum", 500));
            }
            /*
            if (contract.getSecondInactivityMessageTerm() <= contract.getFirstInactivityMessageTerm()) {
                errors.addGlobalError(new LocalizableError("contract.secondTermMinusOrEqualtoFirst",
                        LocalizationUtility.getLocalizedFieldName("contract.secondInactivityMessageTerm", null, null,
                                getContext().getLocale()),
                        LocalizationUtility.getLocalizedFieldName("contract.firstInactivityMessageTerm", null, null,
                                getContext().getLocale())));
            }

            Contract toCheckContract =
                    contractService.findByIdWithUserContract(contract.getId(), getUserInSession());

            if (toCheckContract != null && toCheckContract.getUserContract() != null && !toCheckContract
                    .getUserContract().isEmpty()) {
                Date minAccessDate = new Date();
                for (UserContract userContract : toCheckContract.getUserContract()) {
                    Date userAccessDate = userContract.getUser().getLastPlanOrLegislationView();
                    if (userAccessDate.before(minAccessDate)) {
                        minAccessDate = userAccessDate;
                    }
                }
                int differenceBetweenDates = daysBetween(new Date(), minAccessDate);
                if (contract.getFirstInactivityMessageTerm() <= differenceBetweenDates) {
                    errors.addGlobalError(new LocalizableError("contract.invalidFirstInactivityMessageTerm",
                            LocalizationUtility.getLocalizedFieldName("contract.firstInactivityMessageTerm", null, null,
                                    getContext().getLocale()), differenceBetweenDates));

                }
            }*/
        }
    }


    @ValidationMethod(on = "insertContract", when = ValidationState.NO_ERRORS)
    public void validateInsertContract(ValidationErrors errors) throws Exception {
        // check if date is OK
        if (contract.getValidityStartDate().after(contract.getValidityEndDate())) {
            errors.add("contract.validityEndDate", new LocalizableError("contract.startDate.afterEndDate"));
        }

        // check if licenses available when editing
        if (edit) {
            long licensesInUse = contractService.countLicensesInUse(contract.getId());
            if (contract.getLicenses() < licensesInUse) {
                errors.add("contract.licenses", new LocalizableError("contract.noLicenses", licensesInUse));
            }
        }
        // check that file is PDF
        /* CERTOOL-484
        if (file != null) {
            if (file.getContentType() == null || !file.getContentType().equals("application/pdf")) {
                errors.add("file", new LocalizableError("contract.invalidDocument"));
            }
        }
        */
    }

    @ValidationMethod(on = "insertContract", when = ValidationState.ALWAYS)
    public void validateContractSubmodules(ValidationErrors errors) throws Exception {
        // check if any security submodule was selected
        if (contract.getModule().getModuleType() == ModuleType.GSC &&
                (selectedSubModules == null || selectedSubModules.size() < 1)) {
            errors.add("contract.licenses", new LocalizableError("security.noSubModuleSelected"));
        }
    }

    // client contract manager can only see their company
    // administrator can only see certitecna
    // contract manager can see it all

    @Secure(roles = "administrator,contractmanager")
    public Resolution downloadContractFile()
            throws JackrabbitException, CertitoolsAuthorizationException, ObjectNotFoundException {
        getContext().getResponse().setHeader("Cache-control", "");
        getContext().getResponse().setHeader("Pragma", "");

        File contractFile = contractService.findContractFileAllowed(contract.getId(), getUserInSession());

        return new StreamingResolution(contractFile.getContentType(), contractFile.getData())
                .setFilename(contractFile.getFileName());
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(MenuItem.Item.MENU_ADMIN, MenuItem.Item.SUB_MENU_ADMIN_COMPANY);
    }

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertContract")) {
            return insertContractForm();
        }

        return null;
    }

    private void loadModules() throws ObjectNotFoundException {
        modules = new ArrayList<Module>();

        //TODO-MODULE
        List<ModuleType> moduleAllowed = new ArrayList<ModuleType>();
        moduleAllowed.add(ModuleType.LEGISLATION);
        moduleAllowed.add(ModuleType.PEI);
        moduleAllowed.add(ModuleType.PRV);
        moduleAllowed.add(ModuleType.PSI);
        moduleAllowed.add(ModuleType.GSC);

        for (ModuleType mType : moduleAllowed) {
            modules.add(new Module(mType,
                    LocalizationUtility.getLocalizedFieldName(mType.getKey(), null, null, getContext().getLocale())));
        }

        //security management sub modules
        subModules = new ArrayList<SubModule>();
        for (SubModuleType smType : SubModuleType.values()) {
            subModules.add(new SubModule(smType,
                    LocalizationUtility.getLocalizedFieldName(smType.getKey(), null, null, getContext().getLocale())));
        }
    }

    private Module loadModule(ModuleType moduleType) {
        return new Module(moduleType,
                LocalizationUtility.getLocalizedFieldName(moduleType.getKey(), null, null, getContext().getLocale()));
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public FileBean getFile() {
        return file;
    }

    public void setFile(FileBean file) {
        this.file = file;
    }

    public Collection<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(Collection<Contract> contracts) {
        this.contracts = contracts;
    }

    public List<Long> getUserRegisterPermissions() {
        return userRegisterPermissions;
    }

    public void setUserRegisterPermissions(List<Long> userRegisterPermissions) {
        this.userRegisterPermissions = userRegisterPermissions;
    }

    public List<SubModule> getSubModules() {
        return subModules;
    }

    public void setSubModules(List<SubModule> subModules) {
        this.subModules = subModules;
    }

    public List<String> getSelectedSubModules() {
        return selectedSubModules;
    }

    public void setSelectedSubModules(List<String> selectedSubModules) {
        this.selectedSubModules = selectedSubModules;
    }
}