/*
 * $Id: UserActivationActionBean.java,v 1.9 2010/02/05 15:17:50 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/02/05 15:17:50 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action;

import com.criticalsoftware.certitools.business.certitools.UserService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.InvalidPasswordException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.presentation.action.certitools.LoginRedirectActionBean;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.validation.*;

/**
 * User Activation Action Bean
 *
 * @author pjfsilva
 */
public class UserActivationActionBean extends AbstractActionBean {

    @EJBBean(value = "certitools/UserService")
    private UserService userService;

    private String uid;

    @ValidateNestedProperties({
            @Validate(field = "fiscalNumber", required = true, maxvalue = 999999999999L,
                    on = {"activateUser", "resetPassword"}),
            @Validate(field = "email", required = true, on = "resetPassword")
    })
    private User user;

    @Validate(required = true, minlength = 8, maxlength = 30, on = "activateUser",
            expression = "this == password2")
    private String password1;

    @Validate(required = true, minlength = 8, on = "activateUser")
    private String password2;

    private boolean showForm = true;

    private boolean sucess = false;

    @DefaultHandler
    public Resolution activateUserForm() throws ObjectNotFoundException {
        User userInDb = null;
        if (user != null) {
            userInDb = userService.findById(user.getId());
        }
        if (user == null || userInDb.getActivationKey() == null || !userInDb.getActivationKey().equals(uid)) {
            getContext().getValidationErrors().addGlobalError(new LocalizableError("user.error.invalidActivationLink"));
            return new ForwardResolution("/WEB-INF/jsps/certitools/userActivation.jsp").addParameter("showform", false);
        }

        return new ForwardResolution("/WEB-INF/jsps/certitools/userActivation.jsp");
    }

    public Resolution activateUser() throws ObjectNotFoundException, BusinessException, InvalidPasswordException {
        User userInDb = userService.findById(user.getId());

        if (userInDb.getActivationKey() == null || !userInDb.getActivationKey().equals(uid)) {
            getContext().getValidationErrors().addGlobalError(new LocalizableError("user.error.invalidActivationLink"));
            return new ForwardResolution("/WEB-INF/jsps/certitools/userActivation.jsp");
        }

        if (!user.getFiscalNumber().equals(userInDb.getFiscalNumber())) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("user.activation.error.fiscalNumberWrong"));
            return new ForwardResolution("/WEB-INF/jsps/certitools/userActivation.jsp");
        }
        if (!password1.equals(password2)) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("user.activation.error.passwordMismatch"));
            return new ForwardResolution("/WEB-INF/jsps/certitools/userActivation.jsp");
        }

        userService.activateUser(user, password1, password2, uid);

        return new RedirectResolution(LoginRedirectActionBean.class).addParameter("activationSucess", true);
    }

    public Resolution activateUserSucess() {
        sucess = true;
        return new ForwardResolution("/WEB-INF/jsps/certitools/userActivation.jsp");
    }

    public Resolution resetPasswordForm() {
        return new ForwardResolution("/WEB-INF/jsps/certitools/userResetPassword.jsp");
    }

    public Resolution resetPassword() throws ObjectNotFoundException {
        User userInDb = null;
        try {
            userInDb = userService.findByEmail(user.getEmail());
        } catch (ObjectNotFoundException e) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("user.resetPassword.error.userNotFound"));
            return new ForwardResolution("/WEB-INF/jsps/certitools/userResetPassword.jsp");
        }

        if (!user.getFiscalNumber().equals(userInDb.getFiscalNumber())) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("user.activation.error.fiscalNumberWrong"));
            return new ForwardResolution("/WEB-INF/jsps/certitools/userResetPassword.jsp");
        }

        // all ok, reset password
        userService.resetUserPassword(userInDb);

        getContext().getMessages().add(new LocalizableMessage("user.resetPassword.success"));

        return new RedirectResolution(this.getClass(), "resetPasswordSuccess");
    }

    public Resolution resetPasswordSuccess() {
        showForm = false;
        return new ForwardResolution("/WEB-INF/jsps/certitools/userResetPassword.jsp");
    }

    @ValidationMethod(on = "activateUser", when = ValidationState.NO_ERRORS)
    public void validate() {
        char[] chars = password1.toCharArray();
        boolean hasNumber = false;
        boolean hasLetter = false;

        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(chars[i])) {
                hasNumber = true;
            } else if (Character.isLetter(chars[i])) {
                hasLetter = true;
            }
        }
        if (!hasNumber || !hasLetter) {
            getContext().getValidationErrors()
                    .add("password1", new LocalizableError("error.password1.valueDoesNotMatch"));
        }
    }

    public void fillLookupFields() {

    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public boolean isSucess() {
        return sucess;
    }

    public void setSucess(boolean sucess) {
        this.sucess = sucess;
    }

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }
}
