package com.criticalsoftware.certitools.business.sm;

import javax.ejb.Timer;

/**
 * Recurrence Timer Service
 *
 * @author miseabra
 * @version $Revision$
 */
public interface RecurrenceTimerService {

    /**
     * Daily interval to run the timer
     */
    long DAILY_INTERVAL = 86400000L;

    /**
     * Starts the timer
     */
    void scheduleTimer();

    /**
     * Timeout handler
     *
     * @param timer ejb timer
     */
    void timeout(Timer timer);

    /**
     * Cancels the timer
     */
    void clearSchedule();
}
