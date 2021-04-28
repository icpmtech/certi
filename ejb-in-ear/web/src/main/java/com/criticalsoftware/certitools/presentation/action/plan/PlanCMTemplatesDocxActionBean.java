/*
 * $Id: PlanCMTemplatesDocxActionBean.java,v 1.10 2012/06/12 14:31:28 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2012/06/12 14:31:28 $
 * Last changed by $Author: d-marques $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.certitools.CompanyService;
import com.criticalsoftware.certitools.business.certitools.ContractService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.IsReferencedException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.TemplateDocxService;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.TemplateDocx;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.ValidationUtils;
import com.criticalsoftware.certitools.util.File;
import com.criticalsoftware.certitools.util.ModuleType;
import com.samaxes.stripejb3.EJBBean;

import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.*;
import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Management zone for DOCX templates
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.10 $
 */
@Secure(roles = "peimanager")
public class PlanCMTemplatesDocxActionBean extends AbstractActionBean implements ValidationErrorHandler {

    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    @EJBBean(value = "certitools/TemplateDocxService")
    private TemplateDocxService templateDocxService;

    @ValidateNestedProperties({
            @Validate(field = "title", required = true, maxlength = 255, on = "insertTemplateDocx"),
            @Validate(field = "description", maxlength = 2048, on = "insertTemplateDocx"),
            @Validate(field = "module.moduleType", required = true, on = "insertTemplateDocx")
    })
    private TemplateDocx template;

    @Validate(on = "insertTemplateDocx", required = true)
    private FileBean templateFile;

    private List<Module> modules;
    private boolean edit;
    private String error = null;
    private String letterTemplate = "ALL";
    private String[] alphabet =
            {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
             "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private String searchPhrase;

    private Collection<Company> companies;
    private Collection<TemplateDocx> templateList;
    private Collection<Long> selectedContracts;

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertTemplateDocx")) {
            return insertTemplateDocxForm();
        }
        return null;
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        setHelpId("#pei-exportdocx");
        setPlanMenu(MenuItem.Item.SUB_MENU_PEI_DOCX, MenuItem.Item.SUB_MENU_SAFETY_DOCX,
                MenuItem.Item.SUB_MENU_PSI_DOCX,  MenuItem.Item.SUB_MENU_GSC_DOCX);
    }

    @DefaultHandler
    public Resolution viewPlanCMTemplatesDocx() {

        if (!ValidationUtils.validateNavigationLetter(letterTemplate)) {
            letterTemplate = "";
        }
        if(template != null && template.getId()!= null && (letterTemplate == null || letterTemplate.equals(""))){
            template = templateDocxService.findTemplateDocx(template.getId());
            templateList = new ArrayList<TemplateDocx>();
            templateList.add(template);
        }else{
            templateList = templateDocxService.findAllTemplateDocxByStartLetter(letterTemplate);
            Collections.sort((List<TemplateDocx>) templateList);
            for (TemplateDocx templateDocx : templateList) {
                templateDocx.setModule(loadModule(templateDocx.getModule().getModuleType()));
            }
        }


        // set errors if they exist
        // input sanitization: check if errors is in a pre-defined whitelist
        if (error != null) {
            String[] whitelistErrors = {"templateDocx.del.isReferenced"};
            if (ArrayUtils.contains(whitelistErrors, error)) {
                getContext().getValidationErrors().addGlobalError(new LocalizableError(error));
            }
        }

        return new ForwardResolution("/WEB-INF/jsps/plan/planCMTemplatesDocx.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    public Resolution viewTemplateDocxFragment() {
        selectedContracts = new ArrayList<Long>();
        template = templateDocxService.findTemplateDocx(template.getId());
        template.setModule(loadModule(template.getModule().getModuleType()));
        for (Contract contract : template.getContracts()) {
            selectedContracts.add(contract.getId());
        }

        companies = companyService.findAllWithContractsLoaded();
        ArrayList<Contract> contractsTemp;
        for (Company company : companies) {
            contractsTemp = new ArrayList<Contract>();

            for (Contract contract : company.getContracts()) {
                // TODO-MODULE
                if ((contract.getModule().getModuleType().equals(ModuleType.PEI) ||
                        contract.getModule().getModuleType().equals(ModuleType.PRV) ||
                        contract.getModule().getModuleType().equals(ModuleType.PSI)) &&
                        template.getModule().getModuleType().equals(contract.getModule().getModuleType())) {
                    contract.setModule(loadModule(contract.getModule().getModuleType()));
                    contractsTemp.add(contract);
                }
            }
            company.setContracts(contractsTemp);
        }

        getContext().getResponse().setHeader("Stripes-Success", "OK");
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMTemplatesDocxFragment.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    public Resolution searchTemplateDocx() throws BusinessException, ObjectNotFoundException {
        if (searchPhrase == null) {
            searchPhrase = "";
        }

        templateList = templateDocxService.findTemplatesDocx(searchPhrase);

        for (TemplateDocx templateDocx : templateList) {
            templateDocx.setModule(loadModule(templateDocx.getModule().getModuleType()));
        }

        letterTemplate = "";

        return new ForwardResolution("/WEB-INF/jsps/plan/planCMTemplatesDocx.jsp");
    }

    public Resolution insertTemplateDocxContractAssociation() throws ObjectNotFoundException {
        templateDocxService.updateTemplateDocxContracts(template.getId(), selectedContracts);
        return new RedirectResolution(PlanCMTemplatesDocxActionBean.class)
                .addParameter("template.id", template.getId())
                .addParameter("planModuleType", getPlanModuleType());
    }

    public Resolution updateTemplateDocxForm() throws ObjectNotFoundException, JackrabbitException {
        edit = true;
        letterTemplate = "";
        template = templateDocxService.findTemplateDocx(template.getId());
        template.setModule(loadModule(template.getModule().getModuleType()));
        loadModules();

        return new ForwardResolution("/WEB-INF/jsps/plan/planCMTemplatesDocxInsert.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    public Resolution updateTemplateDocx()
            throws ObjectNotFoundException, IOException, BusinessException, JackrabbitException {
        if (templateFile == null) {
            templateDocxService.updateTemplateDocx(template, null, null, null);
        } else {
            templateDocxService.updateTemplateDocx(template, templateFile.getInputStream(), templateFile.getFileName(),
                    templateFile.getContentType());
        }
        getContext().getMessages().add(new LocalizableMessage("templateDocx.add.success"));
        return new RedirectResolution(PlanCMTemplatesDocxActionBean.class)
                .addParameter("template.id", template.getId())
                .addParameter("letterTemplate","")
                .addParameter("planModuleType", getPlanModuleType());
    }

    public Resolution cancelTemplateDocxForm() {
        if (template != null && template.getId() != null) {
            return new RedirectResolution(PlanCMTemplatesDocxActionBean.class)
                    .addParameter("template.id", template.getId());
        }
        return new RedirectResolution(PlanCMTemplatesDocxActionBean.class);
    }

    public Resolution insertTemplateDocxForm() {
        loadModules();
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMTemplatesDocxInsert.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    public Resolution insertTemplateDocx() throws ObjectNotFoundException, IOException, BusinessException {
        templateDocxService.insertTemplateDocx(template, templateFile.getInputStream(), templateFile.getFileName(),
                templateFile.getContentType());
        getContext().getMessages().add(new LocalizableMessage("templateDocx.add.success"));
        return new RedirectResolution(PlanCMTemplatesDocxActionBean.class)
                .addParameter("template.id", template.getId())
                .addParameter("letterTemplate","")
                .addParameter("planModuleType", getPlanModuleType());
    }

    public Resolution downloadTemplateDocx() throws ObjectNotFoundException, JackrabbitException {
        getContext().getResponse().setHeader("Cache-control", "");
        getContext().getResponse().setHeader("Pragma", "");

        File templateFileTemp = templateDocxService.findTemplateDocxFile(template.getId());

        return new StreamingResolution(templateFileTemp.getContentType(), templateFileTemp.getData())
                .setFilename(templateFileTemp.getFileName());
    }

    public Resolution deleteTemplateDocx() throws ObjectNotFoundException, JackrabbitException {
        try {
            templateDocxService.deleteTemplateDocx(template.getId());
        } catch (IsReferencedException e) {
            return new RedirectResolution(PlanCMTemplatesDocxActionBean.class, "viewPlanCMTemplatesDocx")
                    .addParameter("template.id", template.getId())
                    .addParameter("error", "templateDocx.del.isReferenced")
                    .addParameter("planModuleType", getPlanModuleType());
        }

        getContext().getMessages().add(new LocalizableMessage("templateDocx.del.success"));
        return new RedirectResolution(PlanCMTemplatesDocxActionBean.class, "viewPlanCMTemplatesDocx")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @ValidationMethod(on = {"insertTemplateDocx", "updateTemplateDocx"}, when = ValidationState.ALWAYS)
    public void validateInsertLegislation(ValidationErrors errors) throws Exception {
        if (templateFile != null) {
            if (!templateFile.getContentType().equals("application/msword") && !templateFile.getContentType()
                    .equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                errors.add("file", new LocalizableError("templateDocx.invalidDocument"));
            }
        }
    }


    private Module loadModule(ModuleType moduleType) {
        return new Module(moduleType,
                LocalizationUtility.getLocalizedFieldName(moduleType.getKey(), null, null, getContext().getLocale()));
    }

    private void loadModules() {
        modules = new ArrayList<Module>();

        List<ModuleType> moduleAllowed = new ArrayList<ModuleType>();
        // TODO-MODULE
        moduleAllowed.add(ModuleType.PEI);
        moduleAllowed.add(ModuleType.PRV);
        moduleAllowed.add(ModuleType.PSI);

        for (ModuleType mType : moduleAllowed) {
            modules.add(new Module(mType,
                    LocalizationUtility.getLocalizedFieldName(mType.getKey(), null, null, getContext().getLocale())));
        }
    }

    /* Getters & setters */

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

    public TemplateDocx getTemplate() {
        return template;
    }

    public void setTemplate(TemplateDocx template) {
        this.template = template;
    }

    public FileBean getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(FileBean templateFile) {
        this.templateFile = templateFile;
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

    public Collection<TemplateDocx> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(Collection<TemplateDocx> templateList) {
        this.templateList = templateList;
    }

    public Collection<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(Collection<Company> companies) {
        this.companies = companies;
    }

    public Collection<Long> getSelectedContracts() {
        return selectedContracts;
    }

    public void setSelectedContracts(Collection<Long> selectedContracts) {
        this.selectedContracts = selectedContracts;
    }

    public TemplateDocxService getTemplateDocxService() {
        return templateDocxService;
    }

    public void setTemplateDocxService(TemplateDocxService templateDocxService) {
        this.templateDocxService = templateDocxService;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getLetterTemplate() {
        return letterTemplate;
    }

    public void setLetterTemplate(String letterTemplate) {
        this.letterTemplate = letterTemplate;
    }

    public String[] getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(String[] alphabet) {
        this.alphabet = alphabet;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }
}