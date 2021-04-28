package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.sm.enums.RecurrenceEntityType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Recurrence Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_recurrence")
public class Recurrence {

    @Id
    @SequenceGenerator(name = "RECURRENCE_ID_GENERATOR", sequenceName = "sm_recurrence_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECURRENCE_ID_GENERATOR")
    private Long id;
    @ManyToOne
    private RecurrenceType recurrenceType;
    private Integer warningDays;
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextScheduledDate;
    private Boolean active;
    @Enumerated(EnumType.STRING)
    private RecurrenceEntityType entityType;
    @OneToMany(mappedBy = "recurrence", fetch = FetchType.LAZY)
    private List<Activity> activities;
    @OneToMany(mappedBy = "recurrence", fetch = FetchType.LAZY)
    private List<Maintenance> maintenances;
    @OneToMany(mappedBy = "recurrence", fetch = FetchType.LAZY)
    private List<RecurrenceNotification> notifications;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public Integer getWarningDays() {
        return warningDays;
    }

    public void setWarningDays(Integer warningDays) {
        this.warningDays = warningDays;
    }

    public Date getNextScheduledDate() {
        return nextScheduledDate;
    }

    public void setNextScheduledDate(Date nextScheduledDate) {
        this.nextScheduledDate = nextScheduledDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public RecurrenceEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(RecurrenceEntityType entityType) {
        this.entityType = entityType;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Maintenance> getMaintenances() {
        return maintenances;
    }

    public void setMaintenances(List<Maintenance> maintenances) {
        this.maintenances = maintenances;
    }

    public List<RecurrenceNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<RecurrenceNotification> notifications) {
        this.notifications = notifications;
    }
}
