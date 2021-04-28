/*
 * $Id: LoginActionBean.java,v 1.5 2009/04/13 09:02:01 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/13 09:02:01 $
 * Last changed by : $Author: lt-rico $
 */

package com.criticalsoftware.certitools.presentation.action;

import com.criticalsoftware.certitools.business.certitools.UserService;
import com.criticalsoftware.certitools.util.Logger;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/** Login action bean */
public class LoginActionBean extends AbstractActionBean {
    private static final Logger LOGGER = Logger.getInstance(LoginActionBean.class);

    private String j_username;
    private String j_password;

    @EJBBean(value = "certitools/UserService")
    private UserService userService;

    @DefaultHandler
    public Resolution login() {
        if (getUserInSession() != null) {
            return new RedirectResolution(HomeActionBean.class);
        } else {
            return new ForwardResolution("/WEB-INF/login.jsp");
        }
    }

    public void fillLookupFields() {
    }

    public String getJ_username() {
        return j_username;
    }

    public void setJ_username(String j_username) {
        this.j_username = j_username;
    }

    public String getJ_password() {
        return j_password;
    }

    public void setJ_password(String j_password) {
        this.j_password = j_password;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private class MyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
}
