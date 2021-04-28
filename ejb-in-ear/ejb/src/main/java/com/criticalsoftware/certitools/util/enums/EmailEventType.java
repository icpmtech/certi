package com.criticalsoftware.certitools.util.enums;

/**
 * Email Event Type Enum
 *
 * @author miseabra
 * @version $Revision$
 */
public enum EmailEventType {
    ACTIVITY("security.email.activity"),
    CORRECTIVE_ACTION("security.email.correctiveAction"),
    ANOMALY("security.email.anomaly"),
    OCCURRENCE("security.email.occurrence"),
    MODIFICATION("security.email.modification"),
    WORK_AUTHORIZATION("security.email.workAuthorization"),
    MAINTENANCE("security.email.maintenance"),
    EMERGENCY_ACTION("security.email.emergencyAction");

    private String resourceKey;

    EmailEventType(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }
}
