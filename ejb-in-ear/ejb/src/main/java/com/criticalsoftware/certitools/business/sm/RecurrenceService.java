package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.entities.sm.RecurrenceType;

import java.util.List;

/**
 * Recurrence Service
 *
 * @author miseabra
 * @version $Revision$
 */
public interface RecurrenceService {

    /**
     * Returns the list of recurrence types.
     *
     * @return The list of recurrence types.
     */
    List<RecurrenceType> findRecurrenceTypes();

    /**
     * Creates scheduled activities or maintenances.
     */
    void createScheduledEvents() throws BusinessException;
}
