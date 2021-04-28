package com.criticalsoftware.certitools.entities.sm.enums;

/**
 * SmSubmodule Enum
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
public enum SubModuleType {
    ACTV("security.submodule.activity", "menu.security.activityPlanning"),
    ANOM("security.submodule.anomaly", "menu.security.anomalies"),
    SIW("security.submodule.securityimpactwork", "menu.security.modifications"),
    APC("security.submodule.correctiveaction", "menu.security.actionsPlanning"),
    MNT("security.submodule.maintenance", "menu.security.maintenance"),
    EMRG("security.submodule.emergencyaction", "menu.security.emergencyActions");

    private final String key;
    private final String menuName;

    SubModuleType(String key, String menuName) {
        this.key = key;
        this.menuName = menuName;
    }

    public String getKey() {
        return key;
    }

    public String getMenuName() {
        return menuName;
    }
}
