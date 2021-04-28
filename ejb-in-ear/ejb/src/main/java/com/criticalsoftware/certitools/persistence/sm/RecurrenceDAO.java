package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.sm.Recurrence;
import com.criticalsoftware.certitools.entities.sm.RecurrenceNotification;
import com.criticalsoftware.certitools.entities.sm.RecurrenceType;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.List;

/**
 * Recurrence DAO
 *
 * @author miseabra
 * @version $Revision$
 */
public interface RecurrenceDAO extends GenericDAO<Recurrence, Long> {

    RecurrenceNotification insertRecurrenceNotification(RecurrenceNotification notification);

    void deleteRecurrenceNotifications(Long recurrenceId);

    /**
     * Returns the active recurrences that are scheduled for today or a day before today (this was added to prevent timer failures)
     *
     * @return The list of recurrences found.
     */
    List<Recurrence> findScheduledRecurrences();

    List<RecurrenceType> findRecurrenceTypes();

    RecurrenceType findRecurrenceType(Long recurrenceTypeId);
}
