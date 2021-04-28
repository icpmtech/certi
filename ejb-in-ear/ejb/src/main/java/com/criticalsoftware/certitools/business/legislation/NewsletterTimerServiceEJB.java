/*
 * $Id: NewsletterTimerServiceEJB.java,v 1.6 2010/01/13 17:50:08 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/01/13 17:50:08 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.legislation;

import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.util.Calendar;

/**
 * Newsletter timer service
 *
 * @author : lt-rico
 */
@Stateless
@Local(NewsletterTimerService.class)
@LocalBinding(jndiBinding = "certitools/NewletterTimerService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class NewsletterTimerServiceEJB implements NewsletterTimerService {

    private static final Logger LOGGER = Logger.getInstance(NewsletterTimerServiceEJB.class);

    @Resource
    private TimerService timerService;

    @EJB
    private NewsletterService newsletterService;

    @PermitAll
    public void startTimer() {
        try {
            Calendar whenToFire = Calendar.getInstance();
            if (whenToFire.get(Calendar.HOUR_OF_DAY) > Configuration.getInstance().getNewsletterTimerHourToRun()) {
                whenToFire.add(Calendar.DAY_OF_MONTH, 1);
            }
            whenToFire.set(Calendar.HOUR_OF_DAY, Configuration.getInstance().getNewsletterTimerHourToRun());
            whenToFire.set(Calendar.MINUTE, 0);
            whenToFire.set(Calendar.SECOND, 0);

            timerService.createTimer(whenToFire.getTime(), DAILY_INTERVAL, "Newsletter Timer");
            LOGGER.info("Timer is configured and will run at " + whenToFire.getTime());
        } catch (Exception e) {
            LOGGER.error("Could not create the timer.... please check configuration data in Database");
        }
    }

    @Timeout
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void timeout(Timer timer) {
        LOGGER.info("Working on newsletters.");
        try {
            newsletterService.sendNewsletters();
        } catch (Throwable t) {
            LOGGER.error("ERROR Running Timer: ", t);
        }
        LOGGER.info("Next timeout will be at " + timer.getNextTimeout());
    }

    public void cancelTimers() {
        for (Object timerObj : timerService.getTimers()) {
            Timer timer = (Timer) timerObj;
            if (timer.getInfo().equals("Newsletter Timer")) { // Cancel all our recurring events
                LOGGER.info("[cancelTimers] Timer canceled." + timer.getInfo());
                timer.cancel();
            }
        }
    }
}
