/*
 * $Id: DefaultSecurityManager.java,v 1.3 2009/05/11 01:30:25 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/05/11 01:30:25 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.entities.User;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.security.controller.StripesSecurityManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Allow for role based security using the @Secure annotation which is specified at a class or method level.
 *
 * @author  $lt-rico $
 * @version $Revision: 1.3 $
 */
public class DefaultSecurityManager implements StripesSecurityManager {

    public boolean isUserInRole(List<String> roles, ActionBeanContext context) {
        return authorized(roles, context.getRequest());
    }

    public boolean isUserInRole(List<String> roles, HttpServletRequest request, HttpServletResponse response) {
        return authorized(roles, request);
    }

    private boolean authorized(List<String> roles, HttpServletRequest request) {
        Collection<String> userRoles = new ArrayList<String>();
        boolean autorized = true;
        boolean checkLegislation = false;
        boolean checkPEI = false;


        if (roles == null || roles.size() < 1) {
            autorized = false;
        } else {
            for (String role : roles) {
                if (role.equals("legislationAccess")){
                    checkLegislation = true;
                }
                else if (role.equals("peiAccess")){
                    checkPEI = true;
                }

                if (request.isUserInRole(role)) {
                    userRoles.add(role);
                }
            }
        }

        if (userRoles.size() < 1) {
            autorized = false;
        }

        // find user module access
        User user = (User) ((HttpServletRequest) request).getSession().getAttribute("user");
        if (user == null){
            autorized = false;
        }
        else{
            if (checkLegislation){
                autorized = user.isAccessLegislation();
            }
            else if(checkPEI){
                autorized = user.isAccessPEI();     
            }
        }

        return autorized;
    }
}