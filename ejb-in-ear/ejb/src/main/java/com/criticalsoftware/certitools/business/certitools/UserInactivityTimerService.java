/*
 * $Id: UserInactivityTimerService.java,v 1.1 2010/01/12 17:55:25 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/01/12 17:55:25 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.certitools;

import javax.ejb.Timer;

/**
 * Timer service for users
 *
 * @author :    João Gomes
 * @version :   $Revision: 1.1 $
 */

public interface UserInactivityTimerService {

    /** Daily interval to run the timer */
    long DAILY_INTERVAL = 86400000L;

    /** Starts the timer */
    void scheduleTimer();

    /**
     * timeout handler
     *
     * @param timer ejb timer
     */
    void executeJob(Timer timer);

    /** Cancel all timers */
    void clearSchedule();
}