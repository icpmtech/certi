/*
 * $Id: UserChangePassNotificationTimerService.java,v 1.2 2010/03/30 11:00:07 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/03/30 11:00:07 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.certitools;

import javax.ejb.Timer;

/**
 * Description.
 *
 * @author :    Jo�o Gomes
 * @version :   $Revision: 1.2 $
 */

public interface UserChangePassNotificationTimerService {
    /** Daily interval to run the timer */

    long TWO_HOURS_INTERVAL = 7200000L; // 2 hours

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