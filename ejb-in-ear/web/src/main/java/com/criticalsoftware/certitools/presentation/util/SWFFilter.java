/*
 * $Id: SWFFilter.java,v 1.1 2009/05/20 10:26:10 haraujo Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/05/20 10:26:10 $
 * Last changed by $Author: haraujo $
 */
package com.criticalsoftware.certitools.presentation.util;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Flash files fix for IE and HTTPS
 *
 * @author haraujo
 */
public class SWFFilter implements Filter {

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        ((HttpServletResponse) servletResponse).setHeader("Pragma", "");
        ((HttpServletResponse) servletResponse).setHeader("Cache-control", "");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
