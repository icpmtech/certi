/*
 * $Id: UTF8Filter.java,v 1.1 2009/04/01 00:21:16 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/01 00:21:16 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.presentation.util;

import javax.servlet.*;
import java.io.IOException;

/**
 * UTF-8 Filter
 *
 * @author : lt-rico
 */
public class UTF8Filter implements Filter {

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        servletRequest.setCharacterEncoding("UTF-8");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}