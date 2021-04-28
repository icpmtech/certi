/*
 * $Id: FCKPathBuilderImpl.java,v 1.13 2009/10/07 15:02:43 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/10/07 15:02:43 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import net.fckeditor.requestcycle.UserPathBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * <insert description here>
 *
 * @author : lt-rico
 */
public class FCKPathBuilderImpl implements UserPathBuilder {

    /* Not used*/
    public String getUserFilesAbsolutePath(HttpServletRequest httpServletRequest) {
        String peiId = httpServletRequest.getParameter("folder");
        String planModuleType = httpServletRequest.getParameter("planModuleType");

        if (peiId != null && peiId.length() > 0) {
            int start = peiId.indexOf(PlanUtils.ROOT_PLAN_FOLDER) + PlanUtils.ROOT_PLAN_FOLDER.length() + 4;
            int end = peiId.indexOf("/", start);
            peiId = peiId.substring(start, end);
        }
        return extractContextPathFromReferer(httpServletRequest)
                + "/plan/Plan.action?viewResource=&planModuleType=" + planModuleType + "&peiViewOffline=true&peiId=" + peiId + "&path=";
    }

    public String getUserFilesPath(HttpServletRequest httpServletRequest) {
        String peiId = httpServletRequest.getParameter("folder");
        String planModuleType = httpServletRequest.getParameter("planModuleType");

        if (peiId != null && peiId.length() > 0) {
            int start = peiId.indexOf(PlanUtils.ROOT_PLAN_FOLDER) + PlanUtils.ROOT_PLAN_FOLDER.length() + 4;
            int end = peiId.indexOf("/", start);
            peiId = peiId.substring(start, end);
        }
        String options = "/plan/Plan.action?viewResource=&planModuleType=" + planModuleType;
        if (httpServletRequest.getParameter("Type").equals("Image")) {
            options += "&peiViewOffline=true";
        }
        options += "&peiId=" + peiId + "&path=";
        return extractContextPathFromReferer(httpServletRequest) + options;
    }

    private String extractContextPathFromReferer(HttpServletRequest httpServletRequest) {
        String contextPath = httpServletRequest.getParameter("contextPath");
        if (contextPath != null) {
            return contextPath;
        } else {
            contextPath = httpServletRequest.getHeader("referer");
            return contextPath.substring(0, contextPath.indexOf("/scripts"));
        }
    }
}
