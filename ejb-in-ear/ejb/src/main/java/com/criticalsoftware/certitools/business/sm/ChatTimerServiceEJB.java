package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.util.Calendar;

@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(ChatTimerService.class)
@LocalBinding(jndiBinding = "certitools/ChatTimerService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class ChatTimerServiceEJB implements ChatTimerService {

    private static final Logger LOGGER = Logger.getInstance(ChatTimerServiceEJB.class);

    @Resource
    private TimerService timerService;
    @EJB
    private SecurityManagementService securityManagementService;

    @PermitAll
    public void scheduleTimer() {
        try {
            Calendar whenToFire = Calendar.getInstance();
            if (whenToFire.get(Calendar.HOUR_OF_DAY) > Configuration.getInstance().getRecurrenceTimerHourToRun()) {
                whenToFire.add(Calendar.DAY_OF_MONTH, 1);
            }
            whenToFire.set(Calendar.HOUR_OF_DAY, Configuration.getInstance().getRecurrenceTimerHourToRun());
            whenToFire.set(Calendar.MINUTE, 0);
            whenToFire.set(Calendar.SECOND, 0);
            whenToFire.set(Calendar.MILLISECOND, 0);

            timerService.createTimer(whenToFire.getTime(), DAILY_INTERVAL, "Chat Notifications Timer");
            LOGGER.info("Chat Notifications Timer is configured and will run at " + whenToFire.getTime());
        } catch (Exception e) {
            LOGGER.error("Could not create the Chat Notifications Timer.... please check configuration data in Database");
        }
    }

    @Timeout
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void timeout(Timer timer) {
        LOGGER.info("Checking Chat Notifications...");
        try {
            securityManagementService.sendChatNotifications();
        } catch (Throwable t) {
            LOGGER.error("ERROR Running Chat Notifications Timer: ", t);
        }
        LOGGER.info("Next timeout will be at " + timer.getNextTimeout());
    }

    public void clearSchedule() {
        for (Object timerObj : timerService.getTimers()) {
            Timer timer = (Timer) timerObj;
            if (timer.getInfo().equals("Chat Notifications Timer")) { // Cancel all our recurring events
                LOGGER.info("[cancelTimers] Chat Notifications Timer canceled." + timer.getInfo());
                timer.cancel();
            }
        }
    }
}
