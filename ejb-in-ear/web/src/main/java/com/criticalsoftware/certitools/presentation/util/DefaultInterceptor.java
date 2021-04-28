/*
 * $Id: DefaultInterceptor.java,v 1.1 2009/03/09 15:29:57 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/09 15:29:57 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.util.Logger;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.security.Principal;

/**
 * Intercepts execution and creates information log.
 *
 * @author : jp-gomes
 */

@Intercepts(LifecycleStage.ResolutionExecution)
public class DefaultInterceptor implements Interceptor {
    private static final Map<Class<? extends ActionBean>, Logger> logMap =
            new ConcurrentHashMap<Class<? extends ActionBean>, Logger>();

    public Resolution intercept(ExecutionContext ctx) throws Exception {
        Logger log = getLog(ctx.getActionBean().getClass());
        HttpServletRequest httpRequest = ctx.getActionBeanContext().getRequest();

        Principal principal = ctx.getActionBeanContext().getRequest().getUserPrincipal();
        log.info(principal == null ? "guest" : principal.getName(), ctx.getHandler(), httpRequest.getRequestURI());

        if (log.isDebugEnabled()) {
            log.debugMethodExecutionStart(principal == null ? "guest" : principal.getName(), ctx.getHandler());
            log.debugParameters(httpRequest);
        }

        Resolution resolution = ctx.proceed();

        if (log.isDebugEnabled()) {
            log.debugMethodExecutionEnd(principal == null ? "guest" : principal.getName(), ctx.getHandler(),
                    "Resolution: Add Resolution here");
            log.debugAttributes(httpRequest);
        }
        return resolution;
    }

    private Logger getLog(Class<? extends ActionBean> klass) {
        Logger log = logMap.get(klass);
        if (log != null) {
            return log;
        } else {
            log = Logger.getInstance(klass);
            logMap.put(klass, log);
            return log;
        }
    }
}

