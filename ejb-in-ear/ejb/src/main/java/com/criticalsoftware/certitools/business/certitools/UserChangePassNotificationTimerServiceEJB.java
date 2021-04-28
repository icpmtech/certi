/*
 * $Id: UserChangePassNotificationTimerServiceEJB.java,v 1.3 2010/03/30 14:15:35 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/03/30 14:15:35 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.util.Logger;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Calendar;

/**
 * Description.
 *
 * @author :    Jo�o Gomes
 * @version :   $Revision: 1.3 $
 */

@Stateless
@Local(UserChangePassNotificationTimerService.class)
@LocalBinding(jndiBinding = "certitools/UserChangePassNotificationTimerService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class UserChangePassNotificationTimerServiceEJB implements UserChangePassNotificationTimerService {
    private static final Logger LOGGER = Logger.getInstance(UserInactivityTimerServiceEJB.class);

    @Resource
    private TimerService timerService;

    @EJB
    private UserService userService;

    @PermitAll
    public void scheduleTimer() {
        try {
            Calendar whenToFire = Calendar.getInstance();
            whenToFire.add(Calendar.SECOND, 10);
            timerService
                    .createTimer(whenToFire.getTime(), TWO_HOURS_INTERVAL, "UserChangePassNotificationTimerService");
            LOGGER.info("UserChangePassNotificationTimerService is configured and will run at " + whenToFire
                    .getTime());
        } catch (Exception e) {
            LOGGER.error("Could not create User Inactivity timer.... please check configuration data in Database");
        }
    }

    @Timeout
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void executeJob(Timer timer) {
        LOGGER.info("Working on User Change Pass Notification...");
        try {
            userService.checkAllUsersAndSendNotificationEmail();
        } catch (Throwable t) {
            LOGGER.error("ERROR Running User Change Pass Notification Timer Service: ", t);
        }
        LOGGER.info("Next User Change Pass Notification Job will be at " + timer.getNextTimeout());
    }

    public void clearSchedule() {
        for (Object timerObj : timerService.getTimers()) {
            Timer timer = (Timer) timerObj;
            if (timer.getInfo().equals("UserChangePassNotificationTimerService")) {
                LOGGER.info("UserChangePassNotificationTimerService canceled." + timer.getInfo());
                timer.cancel();
            }
        }
    }
}