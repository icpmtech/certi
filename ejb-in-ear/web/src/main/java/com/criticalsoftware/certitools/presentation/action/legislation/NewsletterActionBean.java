/*
 * $Id: NewsletterActionBean.java,v 1.23 2009/09/04 16:23:28 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/09/04 16:23:28 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.legislation;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.legislation.NewsletterService;
import com.criticalsoftware.certitools.entities.Configuration;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.util.File;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.ValidationState;

import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * NewsLetter action bean
 *
 * @author : lt-rico
 */
@Secure(roles = "legislationAccess")
public class NewsletterActionBean extends AbstractActionBean {

    @EJBBean(value = "certitools/NewsletterService")
    private NewsletterService newsletterService;

    private ArrayList<Configuration> configurations;

    @Validate(required = true, on = {"subscrive", "unsubscribe"})
    private Long legalDocumentCategoryId;

    private FileBean logoFile;

    @Secure(roles = "legislationmanager,administrator")
    @DontValidate
    @DefaultHandler
    public Resolution list() throws ObjectNotFoundException {
        configurations = new ArrayList<Configuration>();
        configurations.addAll(newsletterService.findNewsletterConfigurations());
        configurations.add(new Configuration("certitools.legal.document.newsletter.logo", ""));

        setHelpId("#legislation-subscrition");
        return new ForwardResolution("/WEB-INF/jsps/legislation/newsletterAdminList.jsp");
    }

    @Secure(roles = "legislationmanager,administrator")
    @DontValidate
    public Resolution updateForm() {

        setConfigurations((ArrayList<Configuration>) newsletterService.findNewsletterConfigurations());
        setHelpId("#legislation-edit-subscrition");
        return new ForwardResolution("/WEB-INF/jsps/legislation/newsletterAdminUpdate.jsp");
    }

    @Secure(roles = "legislationmanager,administrator")
    @Validate
    public Resolution update() throws BusinessException, ObjectNotFoundException, IOException {
        newsletterService.update(configurations, logoFile != null ? logoFile.getContentType() : null,
                logoFile != null ? logoFile.getInputStream() : null);
        getContext().getMessages().add(new LocalizableMessage("message.configuration.success"));
        return new RedirectResolution(this.getClass());
    }

    @Validate
    @Secure(roles = "user")
    public Resolution subscrive() throws ObjectNotFoundException, BusinessException {
        newsletterService.subscribe(getUserInSession().getId(), legalDocumentCategoryId);

        getContext().getMessages().add(new LocalizableMessage("message.newsletter.subscrive.success"));
        return new RedirectResolution(this.getClass());
    }

    @Validate
    @Secure(roles = "user")
    public Resolution unsubscrive() throws ObjectNotFoundException, BusinessException {
        newsletterService.unsubscribe(getUserInSession().getId(), legalDocumentCategoryId);

        getContext().getMessages().add(new LocalizableMessage("message.newsletter.unsubscrive.success"));
        return new RedirectResolution(this.getClass());
    }

    @Secure(roles = "legislationmanager,administrator")
    @DontValidate
    public Resolution cancel() {
        return new RedirectResolution(this.getClass());
    }

    @ValidationMethod(on = "update", when = ValidationState.NO_ERRORS)
    public void validate() {
        ResourceBundle resources = ResourceBundle.getBundle("StripesResources", getContext().getLocale());
        if (configurations != null && configurations.size() > 0) {
            int index = 0;
            for (Configuration conf : configurations) {
                if (conf.getValue() == null || conf.getValue().length() == 0) {
                    getContext().getValidationErrors().add("configurations[" + index + "].value",
                            new LocalizableError("custom.validation.required.valueNotPresent",
                                    resources.getString("configuration." + conf.getKey())));
                } else {
                    try {
                        if (conf.getClassName().equals(Integer.class.getCanonicalName())) {
                            Integer.parseInt(conf.getValue());
                        }
                        if (conf.getClassName().equals(InternetAddress.class.getCanonicalName())) {
                            InternetAddress address = new InternetAddress(conf.getValue());
                            String result = address.getAddress();
                            if (!result.contains("@")) {
                                throw new Exception();
                            }
                        }
                    } catch (Exception e) {
                        getContext().getValidationErrors().add("configurations[" + index + "].value",
                                new LocalizableError("error.configuration.invalid.value",
                                        resources.getString("configuration." + conf.getKey())));
                    }

                    if (conf.getValue().length() > 2048) {
                        getContext().getValidationErrors().add("configurations[" + index + "].value",
                                new LocalizableError("error.configuration.maxlenght.exceed",
                                        resources.getString("configuration." + conf.getKey())));
                    }
                }
                index++;
            }
        }
    }

    @DontValidate
    @Secure(roles = "legislationmanager,administrator")
    public Resolution getLogo() {
        try {
            File logo = newsletterService.findNewsletterLogo();
            return new StreamingResolution(logo.getContentType(), logo.getData()).setFilename("logo");
        } catch (ObjectNotFoundException ignored) {
        }
        return null;
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(MenuItem.Item.MENU_LEGISLATION, MenuItem.Item.SUB_MENU_LEGISLATION_NEWSLETTER_ADMIN);
    }

    public NewsletterService getNewsletterService() {
        return newsletterService;
    }

    public void setNewsletterService(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    public ArrayList<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(ArrayList<Configuration> configurations) {
        this.configurations = configurations;
    }

    public FileBean getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(FileBean logoFile) {
        this.logoFile = logoFile;
    }

    public Long getLegalDocumentCategoryId() {
        return legalDocumentCategoryId;
    }

    public void setLegalDocumentCategoryId(Long legalDocumentCategoryId) {
        this.legalDocumentCategoryId = legalDocumentCategoryId;
    }

}