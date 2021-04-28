/*
 * $Id: CertitoolsSecurityInterceptor.java,v 1.1 2009/04/03 03:50:44 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on $Date: 2009/04/03 03:50:44 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ActionResolver;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.exception.StripesServletException;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.security.controller.StripesSecurityFilter;
import net.sourceforge.stripes.security.controller.StripesSecurityManager;
import net.sourceforge.stripes.security.exception.StripesAuthorizationException;
import net.sourceforge.stripes.util.Log;

import javax.servlet.ServletException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Copied from net.sourceforge.stripes.security.controller.SecurityInterceptor
 * and changed authenticateUser to throw a different exception
 *
 * @author pjfsilva
 */
@Intercepts(value = LifecycleStage.HandlerResolution)
public class CertitoolsSecurityInterceptor extends net.sourceforge.stripes.security.controller.SecurityInterceptor {

    private static Log log = Log.getInstance(CertitoolsSecurityInterceptor.class);

    public Resolution intercept(ExecutionContext ctx) throws Exception {
        final Configuration config = StripesSecurityFilter.getConfiguration();
        final StripesSecurityManager securityManager = StripesSecurityFilter.getSecurityManager();
        final Resolution unauthorizedResolution = StripesSecurityFilter.getUnauthorizedResolution();
        final ActionBeanContext context = ctx.getActionBeanContext();
        final ActionResolver resolver = config.getActionResolver();
        final Class beanClass = resolver.getActionBeanType(getRequestedPath(context.getRequest()));

        try {
            if (beanClass != null) {
                final Secure beanSecure = getSecureAnnotationFromClass(beanClass);

                if (beanSecure != null) {
                    authenticateUser(beanSecure, securityManager, context);
                }

                // Then lookup the event name and handler method etc.
                String eventName = resolver.getEventName(beanClass, context);
                context.setEventName(eventName);

                final Method handler;
                if (eventName != null) {
                    handler = resolver.getHandler(beanClass, eventName);
                } else {
                    handler = resolver.getDefaultHandler(beanClass);
                    if (handler != null) {
                        context.setEventName(resolver.getHandledEvent(handler));
                    }
                }

                // Insist that we have a handler
                if (handler == null) {
                    throw new StripesServletException(
                            "No handler method found for request with  ActionBean [" +
                                    beanClass.getName() + "] and eventName [ " + eventName + "]");
                }

                if (handler != null) {
                    log.debug("Checking the method " + handler.getName());

                    // Check to see if we have a method level security annotation and authenticate
                    // the user if we do.
                    Secure methodSecure = (Secure) handler.getAnnotation(Secure.class);

                    if (methodSecure != null) {
                        authenticateUser(methodSecure, securityManager, context);
                    }
                }
            }
        }
        catch (StripesAuthorizationException ex) {
            if (unauthorizedResolution != null) {
                return unauthorizedResolution;
            }
            throw ex;
        }
        return ctx.proceed();
    }

    private void authenticateUser(final Secure secure, final StripesSecurityManager securityManager,
                                  final ActionBeanContext context)
            throws ServletException {
        // Lets just say that if somebody sets the @Secure annotation on a class and doesn't set any
        // roles it defaults to unauthorized.

        if (secure.roles() == null || secure.roles().trim().length() < 1) {
            throw new StripesAuthorizationException();
        }

        // Now lets go through the any roles. If they have any of these roles and the above 2 have
        // succeeded, let them in.
        if (secure.roles() != null && secure.roles().trim().length() > 0) {

            log.info("Checking requires any Roles[" + secure.roles() + "]");
            List<String> anyRoles = Arrays.asList(secure.roles().trim().split(","));
            if (anyRoles != null && !anyRoles.isEmpty()) {
                if (securityManager.isUserInRole(anyRoles, context)) {
                    return;
                }

                throw new StripesAuthorizationException(secure.roles());
            }
        }
    }

    private Secure getSecureAnnotationFromClass(final Class clazz) {
        log.debug("Checking the class " + clazz.getSimpleName());
        Secure beanSecure = (Secure) clazz.getAnnotation(Secure.class);
        if (beanSecure == null) {
            log.debug("Checking the parent class " + clazz.getSuperclass().getSimpleName());
            Class parent = clazz.getSuperclass();
            if (ActionBean.class.isAssignableFrom(parent)) {
                return getSecureAnnotationFromClass(parent);
            } else {
                return null;
            }
        } else {
            return beanSecure;
        }
    }
}
