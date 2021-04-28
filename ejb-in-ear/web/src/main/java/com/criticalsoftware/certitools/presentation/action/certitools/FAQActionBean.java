/*
 * $Id: FAQActionBean.java,v 1.25 2012/05/28 16:50:38 d-marques Exp $
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
package com.criticalsoftware.certitools.presentation.action.certitools;

import com.criticalsoftware.certitools.business.certitools.FAQService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.FAQ;
import com.criticalsoftware.certitools.entities.FAQCategory;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.Menu;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PaginatedListAdapter;
import com.criticalsoftware.certitools.presentation.util.Utils;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.security.exception.StripesAuthorizationException;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * FAQ Action Bean
 *
 * @author jp-gomes
 */
public class FAQActionBean extends DisplayTagSupportActionBean implements ValidationErrorHandler {

    @EJBBean(value = "certitools/FAQService")
    private FAQService faqService;

    @ValidateNestedProperties(value = {
        @Validate(field = "question", maxlength = 4096, required = true, on = {"insertFAQ", "updateFAQ"}),
        @Validate(field = "answer", maxlength = 4096, required = true, on = {"insertFAQ", "updateFAQ"}),
        @Validate(field = "faqCategory.name", maxlength = 128, required = true, on = {"insertFAQ", "updateFAQ"}),
        @Validate(field = "id", required = true, on = {"updateFAQForm", "updateFAQ", "deleteFAQ"})})
    private FAQ faq;

    private PaginatedListAdapter<FAQ> faqs;

    private List<Module> modulesAllowed;
    private StringBuilder moduleCategories;
    private Boolean edit = false;
    private Module viewModuleFAQ;
    private String searchAutoCompleteField;

    private ModuleType moduleType;
    private String moduleTitle;

    @DefaultHandler
    @Secure(roles = "administrator,legislationmanager,peimanager")
    public Resolution viewFAQs() throws BusinessException, ObjectNotFoundException {

        PaginatedListWrapper<FAQ> wrapper =
                new PaginatedListWrapper<FAQ>(getPage(),
                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(), getDirOrder(),
                        isExportRequest());

        faqs = new PaginatedListAdapter<FAQ>(faqService.findAllFAQ(wrapper, getUserInSession().getId()));

        Locale locale = getContext().getLocale();

        setExportXLS(LocalizationUtility
                .getLocalizedFieldName("table.faq.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility
                .getLocalizedFieldName("table.faq.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility
                .getLocalizedFieldName("table.faq.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility
                .getLocalizedFieldName("table.faq.filename.pdf", null, null, locale));

        setHelpId("#faq-management");

        return new ForwardResolution("/WEB-INF/jsps/certitools/faqs.jsp");
    }

    public Resolution viewModuleFAQ() throws ObjectNotFoundException, StripesAuthorizationException {

        try {
            viewModuleFAQ = faqService.findModuleFAQCategories(getUserInSession(), moduleType, true);
        } catch (CertitoolsAuthorizationException e) {
            // TODO-MODULE
            if (moduleType.equals(ModuleType.LEGISLATION)) {
                throw new StripesAuthorizationException("legislationAccess");
            } else if (moduleType.equals(ModuleType.PEI)) {
                throw new StripesAuthorizationException("peiAccess");
            } else if (moduleType.equals(ModuleType.PRV)) {
                throw new StripesAuthorizationException("prvAccess");
            } else if (moduleType.equals(ModuleType.PSI)) {
                throw new StripesAuthorizationException("psiAccess");
            }
        }

        //TODO-MODULE
        Menu menu = (Menu) (getContext().getRequest().getSession().getAttribute("menu"));
        if (moduleType.equals(ModuleType.LEGISLATION)) {
            setHelpId("#faq");
        } else if (moduleType.equals(ModuleType.PEI)) {
            setHelpId("#faq");

            moduleTitle = Utils.getMenuLabelForPlans(ModuleType.PEI, getUserInSession(), getContext().getRequest());
            if (moduleTitle == null) {
                moduleTitle = LocalizationUtility.getLocalizedFieldName("menu.emergency", null, null, getContext().getLocale());
            }
        } else if (moduleType.equals(ModuleType.PRV)) {
            setHelpId("#faq");

            moduleTitle = Utils.getMenuLabelForPlans(ModuleType.PRV, getUserInSession(), getContext().getRequest());
            if (moduleTitle == null) {
                moduleTitle = LocalizationUtility.getLocalizedFieldName("menu.safety", null, null, getContext().getLocale());
            }
        } else if (moduleType.equals(ModuleType.PSI)) {
            setHelpId("#faq");

            moduleTitle = Utils.getMenuLabelForPlans(ModuleType.PSI, getUserInSession(), getContext().getRequest());
            if (moduleTitle == null) {
                moduleTitle = LocalizationUtility.getLocalizedFieldName("menu.psi", null, null, getContext().getLocale());
            }
        }

        return new ForwardResolution("/WEB-INF/jsps/certitools/faqsModule.jsp");
    }

    @Secure(roles = "administrator,legislationmanager,peimanager")
    public Resolution autoCompleteFAQCategory() throws CertitoolsAuthorizationException, ObjectNotFoundException {

        moduleCategories = new StringBuilder();

        List<FAQCategory> faqCategories = faqService.findFaqCategoryByNameToAutoComplete(searchAutoCompleteField,
                getUserInSession().getId(), moduleType);

        if (faqCategories == null || faqCategories.size() == 0) {
            return new StreamingResolution("text/plain", "\n");
        }

        for (FAQCategory fCat : faqCategories) {
            if (fCat.getFaqs() != null && !fCat.getFaqs().isEmpty()) {
                moduleCategories.append(fCat.getName()).append("\n");
            }
        }

        return new StreamingResolution("text/plain", moduleCategories.toString());
    }

    @Secure(roles = "administrator,legislationmanager,peimanager")
    public Resolution insertFAQ() throws ObjectNotFoundException, CertitoolsAuthorizationException {

        faqService.insertFAQ(getUserInSession().getId(), faq);
        getContext().getMessages().add(new LocalizableMessage("faq.add.sucess"));
        return new RedirectResolution(FAQActionBean.class).flash(this);
    }

    @Secure(roles = "administrator,legislationmanager,peimanager")
    public Resolution updateFAQ() throws CertitoolsAuthorizationException, ObjectNotFoundException {

        faqService.updateFAQ(faq, getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("faq.add.sucess"));
        return new RedirectResolution(FAQActionBean.class).flash(this);
    }

    @Secure(roles = "administrator,legislationmanager,peimanager")
    public Resolution insertOrUpdateFAQForm() throws ObjectNotFoundException {

        if (edit != null && edit) {

            setHelpId("#edit-faq");
        } else {
            setHelpId("#add-faq");
        }

        loadModulesAllowed();
        return new ForwardResolution("/WEB-INF/jsps/certitools/faqInsert.jsp");
    }

    @Secure(roles = "administrator,legislationmanager,peimanager")
    public Resolution updateFAQForm() throws ObjectNotFoundException, CertitoolsAuthorizationException {

        edit = true;
        faq = faqService.findFAQWithCategoryAndModule(faq.getId(), getUserInSession().getId());
        return insertOrUpdateFAQForm();
    }

    @Secure(roles = "administrator,legislationmanager,peimanager")
    public Resolution deleteFAQ() throws ObjectNotFoundException, CertitoolsAuthorizationException {
        faqService.deleteFAQ(faq.getId(), getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("faq.delete.sucess"));
        return new RedirectResolution(FAQActionBean.class).flash(this);
    }

    @Secure(roles = "administrator,legislationmanager,peimanager")
    @DontValidate
    public Resolution cancel() throws BusinessException {
        return new RedirectResolution(FAQActionBean.class);
    }


    @After(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        //TODO-MODULE
        if (moduleType == null) {
            //Admin Menu
            getMenu().select(MenuItem.Item.MENU_ADMIN, MenuItem.Item.SUB_MENU_ADMIN_FAQ);
        } else if (moduleType.equals(ModuleType.LEGISLATION)) {
            if (isUserInRole("legislationmanager") || getContext().getEventName().equals("viewModuleFAQ")) {
                getMenu().select(MenuItem.Item.MENU_LEGISLATION, MenuItem.Item.SUB_MENU_LEGISLATION_FAQ);

            } else {
                getMenu().select(MenuItem.Item.MENU_ADMIN, MenuItem.Item.SUB_MENU_ADMIN_FAQ);
            }
        } else if (moduleType.equals(ModuleType.PEI)) {
            getMenu().select(MenuItem.Item.MENU_PEI, MenuItem.Item.SUB_MENU_PEI_FAQ);
        }
        else if (moduleType.equals(ModuleType.PRV)) {
            getMenu().select(MenuItem.Item.MENU_SAFETY, MenuItem.Item.SUB_MENU_SAFETY_FAQ);
        }
    }

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertFAQ")) {
            return insertOrUpdateFAQForm();
        }

        if (getContext().getEventName().equals("updateFAQ")) {
            return insertOrUpdateFAQForm();
        }

        if (getContext().getEventName().equals("updateFAQForm")) {
            return viewFAQs();
        }

        if (getContext().getEventName().equals("deleteFAQ")) {
            return viewFAQs();
        }

        return null;
    }

    private void loadModulesAllowed() throws ObjectNotFoundException {
        modulesAllowed = new ArrayList<Module>();

        List<ModuleType> modulesTypeAllowed = faqService.findUserModulesAllowed(getUserInSession().getId());

        for (ModuleType mType : modulesTypeAllowed) {
            modulesAllowed.add(new Module(mType,
                    LocalizationUtility.getLocalizedFieldName(mType.getKey(), null, null, getContext().getLocale())));
        }
    }

    public FAQ getFaq() {
        return faq;
    }

    public void setFaq(FAQ faq) {
        this.faq = faq;
    }

    public FAQService getFaqService() {
        return faqService;
    }

    public void setFaqService(FAQService faqService) {
        this.faqService = faqService;
    }

    public String getSearchAutoCompleteField() {
        return searchAutoCompleteField;
    }

    public void setSearchAutoCompleteField(String searchAutoCompleteField) {
        this.searchAutoCompleteField = searchAutoCompleteField;
    }

    public List<Module> getModulesAllowed() {
        return modulesAllowed;
    }

    public void setModulesAllowed(List<Module> modulesAllowed) {
        this.modulesAllowed = modulesAllowed;
    }

    public StringBuilder getModuleCategories() {
        return moduleCategories;
    }

    public void setModuleCategories(StringBuilder moduleCategories) {
        this.moduleCategories = moduleCategories;
    }

    public PaginatedListAdapter<FAQ> getFaqs() {
        return faqs;
    }

    public void setFaqs(PaginatedListAdapter<FAQ> faqs) {
        this.faqs = faqs;
    }

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

    public Module getViewModuleFAQ() {
        return viewModuleFAQ;
    }

    public void setViewModuleFAQ(Module viewModuleFAQ) {
        this.viewModuleFAQ = viewModuleFAQ;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
    }
}
