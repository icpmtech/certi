package com.criticalsoftware.certitools.persistence.sm.dto;

import com.criticalsoftware.certitools.entities.sm.enums.UpcomingEventType;

import java.util.Date;

/**
 * Created by aurhe on 6/24/15.
 */
public class UpcomingEvent {

    private Long id;
    private String name;

    private Date dateScheduled;
    private UpcomingEventType upcomingEventType;

    public UpcomingEvent(Long id, String name, Date dateScheduled, String upcomingEventType) {
        this.id = id;
        this.name = name;
        this.dateScheduled = dateScheduled;
        this.upcomingEventType = UpcomingEventType.valueOf(upcomingEventType);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateScheduled() {
        return dateScheduled;
    }

    public void setDateScheduled(Date dateScheduled) {
        this.dateScheduled = dateScheduled;
    }

    public UpcomingEventType getUpcomingEventType() {
        return upcomingEventType;
    }

    public void setUpcomingEventType(UpcomingEventType upcomingEventType) {
        this.upcomingEventType = upcomingEventType;
    }
}
