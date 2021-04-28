/*
 * $Id: ContextListener.java,v 1.18 2013/12/13 08:20:45 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2013/12/13 08:20:45 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.business.certitools.UserChangePassNotificationTimerService;
import com.criticalsoftware.certitools.business.certitools.UserInactivityTimerService;
import com.criticalsoftware.certitools.business.certitools.UserService;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.legislation.NewsletterTimerService;
import com.criticalsoftware.certitools.business.sm.ChatTimerService;
import com.criticalsoftware.certitools.business.sm.RecurrenceTimerService;
import com.criticalsoftware.certitools.persistence.certitools.RepositoryDAO;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Context listener put configuration in application scope
 *
 * @author unknown
 */
public class ContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getInstance(ContextListener.class);

    private ServletContext context;

    public void contextInitialized(ServletContextEvent event) {
        context = event.getServletContext();
        context.setAttribute("configuration", Configuration.getInstance());

        InitialContext ctx = null;
        try {
            ctx = new InitialContext();
        } catch (NamingException e) {
            LOGGER.error("Error while getting initial context", e);
        }

        if (!Configuration.getInstance().getLocalInstallation()) {
            // BPI installation doesn't have timers
            startTimer(ctx);
        }

        configureRepository(ctx);
        cleanUserSessions(ctx);
    }

    private void configureRepository(InitialContext ctx) {
        try {
            LOGGER.info("Registering new nodes on repository...");
            RepositoryDAO repositoryDAO =
                    (RepositoryDAO) ctx.lookup("certitools/RepositoryDAO");
            repositoryDAO.registerNodeTypes();
        } catch (NamingException e) {
            LOGGER.error("Error while looking up for Repository DAO ", e);
        } catch (JackrabbitException e) {
            LOGGER.error("Error while configure new nodes on repository", e);
        }
    }

    private void startTimer(InitialContext ctx) {
        try {
            NewsletterTimerService timer =
                    (NewsletterTimerService) ctx.lookup("certitools/NewletterTimerService");
            timer.cancelTimers();
            timer.startTimer();
        } catch (NamingException e) {
            LOGGER.error("Error while looking up for Newsletter Timer", e);
        }
        try {
            UserInactivityTimerService timer =
                    (UserInactivityTimerService) ctx.lookup("certitools/UserInactivityTimerService");
            timer.clearSchedule();
            timer.scheduleTimer();
        } catch (NamingException e) {
            LOGGER.error("Error while looking up for User Inactivity Timer", e);
        }
        try {
            UserChangePassNotificationTimerService timer =
                    (UserChangePassNotificationTimerService) ctx
                            .lookup("certitools/UserChangePassNotificationTimerService");
            timer.clearSchedule();
            timer.scheduleTimer();
        } catch (NamingException e) {
            LOGGER.error("Error while looking up for User Change Pass Notification Timer", e);
        }
        try {
            RecurrenceTimerService timer =
                    (RecurrenceTimerService) ctx
                            .lookup("certitools/RecurrenceTimerService");
            timer.clearSchedule();
            timer.scheduleTimer();
        } catch (NamingException e) {
            LOGGER.error("Error while looking up for Recurrence Timer", e);
        }
        try {
            ChatTimerService timer =
                    (ChatTimerService) ctx
                            .lookup("certitools/ChatTimerService");
            timer.clearSchedule();
            timer.scheduleTimer();
        } catch (NamingException e) {
            LOGGER.error("Error while looking up for Chat Timer", e);
        }
    }


    private void cleanUserSessions(InitialContext ctx) {
        try {
            UserService userService =
                    (UserService) ctx.lookup("certitools/UserService");
            userService.cleanUserSessions();
        } catch (NamingException e) {
            LOGGER.error("[cleanUserSessions] Error while trying to clean the user Sessions");
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        context = null;
        try {
            InitialContext ctx = new InitialContext();
            /*RepositoryDAO repositoryDAO =
                    (RepositoryDAO) ctx.lookup("certitools/RepositoryDAO");
            repositoryDAO.shutdown();*/
            LOGGER.info("[contextDestroyed] Repository SHUTDOWN complete...");

            NewsletterTimerService timer = (NewsletterTimerService) ctx.lookup("certitools/NewletterTimerService");
            timer.cancelTimers();
            LOGGER.info("[contextDestroyed] Newsletter timers canceled");

            UserInactivityTimerService userInactivityTimer =
                    (UserInactivityTimerService) ctx.lookup("certitools/UserInactivityTimerService");
            userInactivityTimer.clearSchedule();
            LOGGER.info("[contextDestroyed] User Inactivity Timer canceled");

            UserChangePassNotificationTimerService userChangePassNotificationTimer =
                    (UserChangePassNotificationTimerService) ctx
                            .lookup("certitools/UserChangePassNotificationTimerService");
            userChangePassNotificationTimer.clearSchedule();
            LOGGER.info("[contextDestroyed] User Change Pass Notification Timer canceled");

            RecurrenceTimerService recurrenceTimerService =
                    (RecurrenceTimerService) ctx
                            .lookup("certitools/RecurrenceTimerService");
            recurrenceTimerService.clearSchedule();
            LOGGER.info("[contextDestroyed] Recurrence Timer canceled");

            ChatTimerService chatTimerService =
                    (ChatTimerService) ctx
                            .lookup("certitools/ChatTimerService");
            chatTimerService.clearSchedule();
            LOGGER.info("[contextDestroyed] Chat Timer canceled");

        } catch (Exception e) {
            LOGGER.error("Error while shutting down repository", e);
        }
    }
}
