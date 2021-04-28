/*
 * $Id: UserInactivityTimerServiceEJB.java,v 1.2 2010/01/13 17:50:08 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/01/13 17:50:08 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.util.Configuration;
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
 * @author :    João Gomes
 * @version :   $Revision: 1.2 $
 */

@Stateless
@Local(UserInactivityTimerService.class)
@LocalBinding(jndiBinding = "certitools/UserInactivityTimerService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class UserInactivityTimerServiceEJB implements UserInactivityTimerService {

    private static final Logger LOGGER = Logger.getInstance(UserInactivityTimerServiceEJB.class);

    @Resource
    private TimerService timerService;

    @EJB
    private UserService userService;

    @PermitAll
    public void scheduleTimer() {
        try {
            Calendar whenToFire = Calendar.getInstance();
            if (whenToFire.get(Calendar.HOUR_OF_DAY) > Configuration.getInstance().getNewsletterTimerHourToRun()) {
                whenToFire.add(Calendar.DAY_OF_MONTH, 1);
            }
            whenToFire.set(Calendar.HOUR_OF_DAY, Configuration.getInstance().getNewsletterTimerHourToRun());
            whenToFire.set(Calendar.MINUTE, 0);
            whenToFire.set(Calendar.SECOND, 0);

            timerService.createTimer(whenToFire.getTime(), DAILY_INTERVAL, "UserInactivity Timer");
            LOGGER.info("User Inactivity Timer is configured and will run at " + whenToFire
                    .getTime());
        } catch (Exception e) {
            LOGGER.error("Could not create User Inactivity timer.... please check configuration data in Database");
        }
    }

    @Timeout
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void executeJob(Timer timer) {
        LOGGER.info("Working on Users Inactivation");
        try {
            userService.checkAndDeleteOldUsers();
        } catch (Throwable t) {
            LOGGER.error("ERROR Running User Inactivity Timer: ", t);
        }
        LOGGER.info("Next User Inactivity job will be at " + timer.getNextTimeout());
    }

    public void clearSchedule() {
        for (Object timerObj : timerService.getTimers()) {
            Timer timer = (Timer) timerObj;
            if (timer.getInfo().equals("UserInactivity Timer")) {
                LOGGER.info("User Inactivity Timer canceled." + timer.getInfo());
                timer.cancel();
            }
        }
    }
}