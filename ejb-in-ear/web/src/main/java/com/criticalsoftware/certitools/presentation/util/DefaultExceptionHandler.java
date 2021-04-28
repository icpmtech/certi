/*
 * $Id: DefaultExceptionHandler.java,v 1.23 2012/08/01 17:00:55 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/08/01 17:00:55 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.DocumentNotExistsException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.presentation.action.DisplayErrorsActionBean;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.*;
import net.sourceforge.stripes.exception.ActionBeanNotFoundException;
import net.sourceforge.stripes.exception.AutoExceptionHandler;
import net.sourceforge.stripes.exception.StripesServletException;
import net.sourceforge.stripes.security.exception.StripesAuthorizationException;
import net.sourceforge.stripes.validation.LocalizableError;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

/**
 * DefaultExceptionHandler
 *
 * @author : lt-rico
 * @version : $version $
 */
public class DefaultExceptionHandler implements AutoExceptionHandler {

    private static final Logger LOGGER = Logger.getInstance(DefaultExceptionHandler.class);

    /**
     * Send the user to the global error page.
     *
     * @param exception A Exception exception
     * @param request   The HttpServletRequest
     * @param response  The HttpServletResponse
     *
     * @return A ForwardResolution
     */
    public Resolution handle(Exception exception, HttpServletRequest request, HttpServletResponse response) {
        if (exception.getClass().getName().equals("org.apache.catalina.connector.ClientAbortException")) {
            LOGGER.warn("org.apache.catalina.connector.ClientAbortException");
            return null;
        }

        LOGGER.error(exception.getMessage(), exception);

        request.setAttribute("error",
                getMessage(request, "error.internal"));
        request.setAttribute("subtitle",
                getMessage(request, "error.internal.subtitle"));

        return new ForwardResolution("/WEB-INF/error.jsp");
    }

    /**
     * Sends the user to the 404 error page
     *
     * @param exception A Exception exception
     * @param request   The HttpServletRequest
     * @param response  The HttpServletResponse
     *
     * @return A ForwardResolution
     */
    public Resolution handle(ActionBeanNotFoundException exception, HttpServletRequest request,
                             HttpServletResponse response) {
        LOGGER.warn("[ActionBeanNotFoundException]: " + exception.getMessage());

        request.setAttribute("httpError", "404");
        return new ForwardResolution("/WEB-INF/error.jsp?httpError=404");
    }

    /**
     * Send the user to the global error page with a document does not exist in database.
     *
     * @param exception DocumentNotExistsException exception
     * @param request   The HttpServletRequest
     * @param response  The HttpServletResponse
     *
     * @return A ForwardResolution\
     */
    public Resolution handle(DocumentNotExistsException exception, HttpServletRequest request,
                             HttpServletResponse response) {
        LOGGER.warn("[DocumentNotExistsException]: " + exception.getMessage());

        request.setAttribute("error", getMessage(request, "error.DocumentNotExistsException.subtitle"));
        request.setAttribute("subtitle",
                getMessage(request, "error.DocumentNotExistsException.title"));

        return new ForwardResolution("/WEB-INF/error.jsp");
    }


    /**
     * Send the user to the global error page with an authorization error message.
     *
     * @param exception CertitoolsAuthorizationException exception
     * @param request   The HttpServletRequest
     * @param response  The HttpServletResponse
     *
     * @return A ForwardResolution\
     */
    public Resolution handle(CertitoolsAuthorizationException exception, HttpServletRequest request,
                             HttpServletResponse response) {
        LOGGER.warn("[CertitoolsAuthorizationException]: " + exception.getMessage());

        request.setAttribute("error", getMessage(request, "error.StripesAuthorizationException"));
        request.setAttribute("subtitle",
                getMessage(request, "error.StripesAuthorizationException.subtitle"));

        return new ForwardResolution("/WEB-INF/error.jsp");
    }

    /**
     * Send the user to the global error page with an authorization error message.
     *
     * @param exception A StripesAuthorizationException exception
     * @param request   The HttpServletRequest
     * @param response  The HttpServletResponse
     *
     * @return A ForwardResolution
     */
    public Resolution handle(StripesAuthorizationException exception, HttpServletRequest request,
                             HttpServletResponse response) {
        LOGGER.warn("StripesAuthorizationException");

        request.setAttribute("error", getMessage(request, "error.StripesAuthorizationException"));
        request.setAttribute("subtitle",
                getMessage(request, "error.StripesAuthorizationException.subtitle"));

        // TODO-MODULE - when adding a new module, change this accordingly
        if (exception.getMessage().equals("legislationAccess")) {
            LOGGER.info("Unauthorized access to legislation module.");

            ActionBean bean = (ActionBean) request.getAttribute(StripesConstants.REQ_ATTR_ACTION_BEAN);
            Menu menu = (Menu) bean.getContext().getRequest().getSession().getAttribute("menu");
            menu.select(MenuItem.Item.MENU_LEGISLATION, MenuItem.Item.SUB_MENU_LEGISLATION_SEARCH);
            menu.removeSubMenu(MenuItem.Item.MENU_LEGISLATION);

            return new ForwardResolution("/WEB-INF/jsps/legislation/unauthorized.jsp");
        } else if (exception.getMessage().equals("peiAccess")) {
            LOGGER.info("Unauthorized access to PEI module.");
            ActionBean bean = (ActionBean) request.getAttribute(StripesConstants.REQ_ATTR_ACTION_BEAN);
            Menu menu = (Menu) bean.getContext().getRequest().getSession().getAttribute("menu");
            menu.select(MenuItem.Item.MENU_PEI, MenuItem.Item.SUB_MENU_PEI_VIEW);
            menu.removeSubMenu(MenuItem.Item.MENU_PEI);

            return new ForwardResolution("/WEB-INF/jsps/pei/unauthorized.jsp");
        } else if (exception.getMessage().equals("prvAccess")) {
            LOGGER.info("Unauthorized access to PPREV module.");
            ActionBean bean = (ActionBean) request.getAttribute(StripesConstants.REQ_ATTR_ACTION_BEAN);
            Menu menu = (Menu) bean.getContext().getRequest().getSession().getAttribute("menu");
            menu.select(MenuItem.Item.MENU_SAFETY, MenuItem.Item.SUB_MENU_SAFETY_VIEW);
            menu.removeSubMenu(MenuItem.Item.MENU_SAFETY);

            return new ForwardResolution("/WEB-INF/jsps/prv/unauthorized.jsp");
        } else if (exception.getMessage().equals("psiAccess")) {
            LOGGER.info("Unauthorized access to PSI module.");
            ActionBean bean = (ActionBean) request.getAttribute(StripesConstants.REQ_ATTR_ACTION_BEAN);
            Menu menu = (Menu) bean.getContext().getRequest().getSession().getAttribute("menu");
            menu.select(MenuItem.Item.MENU_PSI, MenuItem.Item.SUB_MENU_PSI_VIEW);
            menu.removeSubMenu(MenuItem.Item.MENU_PSI);

            return new ForwardResolution("/WEB-INF/jsps/psi/unauthorized.jsp");
        }

        return new ForwardResolution("/WEB-INF/error.jsp");
    }

    /**
     * If there's an ActionBean present, send the user back where they came from with a stern warning, otherwise send
     * them to the global error page.
     *
     * @param exception A FileUploadLimitExceededException exception
     * @param request   The HttpServletRequest
     * @param response  The HttpServletResponse
     *
     * @return A ForwardResolution
     *
     * @throws ServletException when fails to get the action bean context
     */
    public Resolution handle(FileUploadLimitExceededException exception, HttpServletRequest request,
                             HttpServletResponse response) throws ServletException {
        LOGGER.warn("FileUploadLimitExceededException");

        /*
         * This is a hack from Ben Gunter received in the Stripes forum. It is related to Jira issue:
         * http://stripes.mc4j.org/jira/browse/STS-402. The problem is that when the exception handler is called, it
         * always receives the original request passed to the StripesFilter and not the wrapped request. That prevents
         * construction of the FlashScope in which we need to store the error message.
         */
        final StripesRequestWrapper wrapper = new StripesRequestWrapper(request) {
            /**
             * Ignore multipart content and set the locale at the same time.
             *
             * @param request the HTTP request to wrap.
             */
            @Override
            protected void constructMultipartWrapper(HttpServletRequest request) throws StripesServletException {
                Locale locale = StripesFilter.getConfiguration().getLocalePicker().pickLocale(request);
                setLocale(locale);
            }
        };
        // redirect back to referer
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("maxFileSize", "" + exception.getMaximum() / 1024 / 1024);
        parameters.put("fileUploadException", "true");

        return new RedirectResolution(DisplayErrorsActionBean.class) {
            @Override
            public void execute(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                super.execute(wrapper, response);
            }
        }.addParameters(parameters);
    }

    /**
     * If there's an ActionBean present, send the user back where they came from with a stern warning, otherwise send
     * them to the global error page.
     *
     * @param exception A ExampleBusinessException exception
     * @param request   The HttpServletRequest
     * @param response  The HttpServletResponse
     *
     * @return A ForwardResolution
     */
    public Resolution handle(BusinessException exception, HttpServletRequest request,
                             HttpServletResponse response) {
        LOGGER.error(exception.getMessage(), exception);
        ActionBean bean = (ActionBean) request.getAttribute(StripesConstants.REQ_ATTR_ACTION_BEAN);
        Resolution resolution;

        if (bean != null && StringUtils.isNotBlank(bean.getContext().getSourcePage())) {
            bean.getContext().getValidationErrors().addGlobalError(new LocalizableError("error.BusinessException",
                    Calendar.getInstance(request.getLocale()).getTime()));
            resolution = new ForwardResolution(bean.getContext().getSourcePage());
        } else {
            request.setAttribute("error", getMessage(request, "error.BusinessException"));
            request.setAttribute("subtitle", getMessage(request, "error.internal.subtitle"));
            resolution = new ForwardResolution("/WEB-INF/error.jsp");
        }

        return resolution;
    }


    /**
     * If there's an ActionBean present, send the user back where they came from with a stern warning, otherwise send
     * them to the global error page.
     *
     * @param exception A ObjectNotFoundException exception
     * @param request   The HttpServletRequest
     * @param response  The HttpServletResponse
     *
     * @return A ForwardResolution
     */
    public Resolution handle(ObjectNotFoundException exception, HttpServletRequest request,
                             HttpServletResponse response) {
        LOGGER.warn("ObjectNotFoundException - " + exception.getType() + " - " + exception);
        ActionBean bean = (ActionBean) request.getAttribute(StripesConstants.REQ_ATTR_ACTION_BEAN);
        Resolution resolution;

        if (bean != null && StringUtils.isNotBlank(bean.getContext().getSourcePage())) {
            bean.getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("error.ObjectNotFoundException." + exception.getType(),
                            Calendar.getInstance(request.getLocale()).getTime()));
            resolution = new ForwardResolution(bean.getContext().getSourcePage());
        } else {
            request.setAttribute("error", getMessage(request, "error.ObjectNotFoundException." + exception.getType()));
            request.setAttribute("subtitle", getMessage(request, "error.internal.objectnotfound.subtitle"));
            resolution = new ForwardResolution("/WEB-INF/error.jsp");
        }

        return resolution;
    }


    private String getMessage(HttpServletRequest request, String key, Object... arguments) {
        return new MessageFormat(ResourceBundle.getBundle("StripesResources", request.getLocale()).getString(key),
                request.getLocale()).format(arguments);
    }

}