/*
 * $Id: NewsletterTimerService.java,v 1.2 2009/03/24 18:40:59 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/24 18:40:59 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.business.legislation;

import javax.ejb.Timer;

/**
 * Timer Service
 *
 * @author : lt-rico
 */
public interface NewsletterTimerService {
    /**
     * Daily interval to run the timer
     */
    long DAILY_INTERVAL = 86400000L;

    /**
     * Starts the timer
     */
    void startTimer();

    /**
     * timeout handler
     * @param timer ejb timer
     */
    void timeout(Timer timer);

    /**
     * Cancel all timers
     */
    void cancelTimers();
}
