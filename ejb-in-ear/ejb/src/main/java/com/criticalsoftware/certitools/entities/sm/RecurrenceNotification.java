package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Recurrence Notification Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_recurrencenotification")
public class RecurrenceNotification {

    @Id
    @SequenceGenerator(name = "RECURRENCE_NOTIFICATION_ID_GENERATOR", sequenceName = "sm_recurrencenotification_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECURRENCE_NOTIFICATION_ID_GENERATOR")
    private Long id;
    @ManyToOne
    private Recurrence recurrence;
    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
