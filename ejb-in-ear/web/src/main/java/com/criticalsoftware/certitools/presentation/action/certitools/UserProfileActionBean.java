/*
 * $Id: UserProfileActionBean.java,v 1.9 2009/09/10 18:04:47 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/09/10 18:04:47 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.certitools;

import com.criticalsoftware.certitools.business.certitools.UserService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.InvalidPasswordException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.ValidationState;


/**
 * User profile action bean
 *
 * @author : lt-rico
 */
public class UserProfileActionBean extends AbstractActionBean {

    @Validate(required = true, maxlength = 30)
    private String oldPassword;
    @Validate(required = true, minlength = 8, maxlength = 30,
              expression = "this == newPasswordConfirm")
    private String newPassword;
    @Validate(required = true, minlength = 8)
    private String newPasswordConfirm;

    @EJBBean(value = "certitools/UserService")
    private UserService userService;

    @DontValidate
    @DefaultHandler
    @Secure(roles = "user")
    public Resolution main() throws ObjectNotFoundException, BusinessException{

        setHelpId("#user-profile");
        return new ForwardResolution("/WEB-INF/jsps/certitools/userProfile.jsp");
    }

    @Validate
    @Secure(roles = "user")
    public Resolution update() throws ObjectNotFoundException, BusinessException {
        try {
            userService.updateProfile(getUserInSession().getId(), oldPassword, newPassword);
        } catch (InvalidPasswordException e) {
            getContext().getValidationErrors().add("oldPassword", new LocalizableError("error.user.profile.password.mismatch"));
            return new ForwardResolution("/WEB-INF/jsps/certitools/userProfile.jsp");
        }

        getContext().getMessages().add(new LocalizableMessage("message.user.profile.password.update.success"));
        return new RedirectResolution(UserProfileActionBean.class);
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(null, null);
    }

    @ValidationMethod(on = "update", when = ValidationState.NO_ERRORS)
    public void validate() {
        char[] chars = newPassword.toCharArray();
        boolean hasNumber = false;
        boolean hasLetter = false;

        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(chars[i])) {
                hasNumber = true;
            } else if (Character.isLetter(chars[i])) {
                hasLetter = true;
            }
        }
        if(!hasNumber || !hasLetter) {
            getContext().getValidationErrors().add("newPassword", new LocalizableError("error.password1.valueDoesNotMatch"));
        }
    }
}
