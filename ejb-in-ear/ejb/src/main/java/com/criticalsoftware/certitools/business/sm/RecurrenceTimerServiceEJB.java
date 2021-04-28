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

/**
 * Recurrence Timer
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(RecurrenceTimerService.class)
@LocalBinding(jndiBinding = "certitools/RecurrenceTimerService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class RecurrenceTimerServiceEJB implements RecurrenceTimerService {

    private static final Logger LOGGER = Logger.getInstance(RecurrenceTimerServiceEJB.class);

    @Resource
    private TimerService timerService;
    @EJB
    private RecurrenceService recurrenceService;

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

            timerService.createTimer(whenToFire.getTime(), DAILY_INTERVAL, "Recurrence Timer");
            LOGGER.info("Recurrence Timer is configured and will run at " + whenToFire.getTime());
        } catch (Exception e) {
            LOGGER.error("Could not create the Recurrence Timer.... please check configuration data in Database");
        }
    }

    @Timeout
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void timeout(Timer timer) {
        LOGGER.info("Checking scheduled recurrences...");
        try {
            //create scheduled activities or maintenance actions
            recurrenceService.createScheduledEvents();

        } catch (Throwable t) {
            LOGGER.error("ERROR Running Recurrence Timer: ", t);
        }
        LOGGER.info("Next timeout will be at " + timer.getNextTimeout());
    }

    public void clearSchedule() {
        for (Object timerObj : timerService.getTimers()) {
            Timer timer = (Timer) timerObj;
            if (timer.getInfo().equals("Recurrence Timer")) { // Cancel all our recurring events
                LOGGER.info("[cancelTimers] Recurrence Timer canceled." + timer.getInfo());
                timer.cancel();
            }
        }
    }
}
