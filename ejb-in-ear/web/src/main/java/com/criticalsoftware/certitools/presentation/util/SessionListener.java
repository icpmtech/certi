/*
 * $Id: SessionListener.java,v 1.6 2009/05/08 15:42:07 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/05/08 15:42:07 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.business.certitools.UserService;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * <insert description here>
 *
 * @author : lt-rico
 * @version : $version $
 */
public class SessionListener implements HttpSessionListener {
    private static final Logger LOGGER = Logger.getInstance(SessionListener.class);

    /**
     * Notification that a session was created.
     * @param event the notification event
     */
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        LOGGER.debug("[SessionListener] Session created");
    }

    /**
     * Notification that a session is about to be invalidated.
     * @param event the notification event
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        LOGGER.debug("[SessionListener] Session destroyed");

        UserService userService;
        try {
            InitialContext ctx = new InitialContext();
            userService = (UserService) ctx.lookup("certitools/UserService");
            User user = (User) event.getSession().getAttribute("user");
            if (user != null){
                userService.updateUserSessionActive(user.getId(), false);
            }
        } catch (NamingException e) {
            LOGGER.error("[sessionDestroyed] Error when trying to lookup userService");
        }
    }

}