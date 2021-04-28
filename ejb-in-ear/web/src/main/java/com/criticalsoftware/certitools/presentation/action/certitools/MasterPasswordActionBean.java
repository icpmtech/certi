/*
 * $Id: MasterPasswordActionBean.java,v 1.1 2009/11/11 05:26:33 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/11/11 05:26:33 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.certitools;

import com.criticalsoftware.certitools.business.certitools.ConfigurationService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.ValidationState;

/**
 * Form to set a new master password
 *
 * @author pjfsilva
 */
public class MasterPasswordActionBean extends AbstractActionBean {

    @Validate(on = "update", required = true, minlength = 8, maxlength = 30)
    private String masterPassword;

    @EJBBean(value = "certitools/ConfigurationService")
    private ConfigurationService configurationService;

    @Secure(roles = "administrator")
    @DefaultHandler
    public Resolution updateForm() {
        setHelpId("#edit-configuration");
        return new ForwardResolution("/WEB-INF/jsps/certitools/masterPasswordUpdate.jsp");
    }

    public Resolution update() throws BusinessException {
        configurationService.updateMasterPassword(masterPassword, getUserInSession());

        getContext().getMessages().add(new LocalizableMessage("masterpassword.success"));        
        return new RedirectResolution(ConfigurationActionBean.class);
    }

    @DontValidate
    public Resolution cancel(){
        return new RedirectResolution(ConfigurationActionBean.class);
    }

    @ValidationMethod(on = "update", when = ValidationState.NO_ERRORS)
    public void validate() {
        char[] chars = masterPassword.toCharArray();
        boolean hasNumber = false;
        boolean hasLetter = false;

        for (char aChar : chars) {
            if (Character.isDigit(aChar)) {
                hasNumber = true;
            } else if (Character.isLetter(aChar)) {
                hasLetter = true;
            }
        }
        if(!hasNumber || !hasLetter) {
            getContext().getValidationErrors().add("masterPassword", new LocalizableError("masterpassword.error.letterNumber"));
        }
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(MenuItem.Item.MENU_ADMIN, MenuItem.Item.SUB_MENU_ADMIN_CONFIG);
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }
}