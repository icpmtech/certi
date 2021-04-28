package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aurelio-santos on 7/10/15.
 */
public class SecurityMenu {

    private ArrayList<TreeMenuItem> menuItems;

    public SecurityMenu(int[] openItems, List<SubModuleType> subModules, boolean isBasic, boolean isIntermediate,
                        boolean isExpert, boolean isAdministrator) {
        menuItems = new ArrayList<TreeMenuItem>();

        boolean enableACTV = subModules.contains(SubModuleType.ACTV),
                enableANOM = subModules.contains(SubModuleType.ANOM),
                enableSIW = subModules.contains(SubModuleType.SIW),
                enableAPC = subModules.contains(SubModuleType.APC),
                enableMNT = subModules.contains(SubModuleType.MNT),
                enableEMRG = subModules.contains(SubModuleType.EMRG);

        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean",
                "activityPlanningGrid", 1, "menu.security.activityPlanning", openItems[0], enableACTV));
        if (isExpert || isAdministrator) {
            menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean",
                    "activityPlanningAdd", 2, "menu.security.activityPlanning.add", enableACTV));
        }
        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean",
                "activityPlanningGrid", 2, "menu.security.activityPlanning.edit", enableACTV));

        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean",
                "anomaliesRecordGrid", 1, "menu.security.anomalies", openItems[1], enableANOM));
        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean",
                "anomaliesRecordAdd", 2, "menu.security.anomalies.equipment.add", enableANOM));
        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean",
                "occurrencesRecordAdd", 2, "menu.security.anomalies.accidents.add", enableANOM));
        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean",
                "anomaliesRecordGrid", 2, "menu.security.anomalies.edit", enableANOM));

        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean",
                "impactWorkGrid", 1, "menu.security.modifications", openItems[2], enableSIW));
        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean",
                "modificationsChangesAdd", 2, "menu.security.modifications.changes", enableSIW));
        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean",
                "authorizationAdd", 2, "menu.security.modifications.authorization", enableSIW));
        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean",
                "impactWorkGrid", 2, "menu.security.modifications.view.edit", enableSIW));

        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean",
                "actionsPlanningGrid", 1, "menu.security.actionsPlanning", openItems[3], enableAPC));
        if (isExpert || isIntermediate || isAdministrator) {
            menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean",
                    "actionsPlanningAdd", 2, "menu.security.actionsPlanning.add", enableAPC));
        }
        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean",
                "actionsPlanningGrid", 2, "menu.security.actionsPlanning.edit", enableAPC));

        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean",
                "maintenanceGrid", 1, "menu.security.maintenance", openItems[4], enableMNT));
        if (isExpert || isAdministrator) {
            menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean",
                    "maintenanceEquipmentsDefine", 2, "menu.security.maintenance.define", enableMNT));
        }
        if (isIntermediate || isAdministrator) {
            menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean",
                    "maintenanceAdd", 2, "menu.security.maintenance.add", enableMNT));
        }
        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean",
                "maintenanceGrid", 2, "menu.security.maintenance.edit", enableMNT));

        menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean",
                "emergencyActionGrid", 1, "menu.security.emergencyActions", openItems[5], enableEMRG));
        if (isExpert || isAdministrator) {
            menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean",
                    "emergencyUsersDefine", 2, "menu.security.emergencyActions.define", enableEMRG));
        }
        if (isBasic || isAdministrator) {
            menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean",
                    "emergencyActionAdd", 2, "menu.security.emergencyActions.add", enableEMRG));
            menuItems.add(new TreeMenuItem("com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean",
                    "emergencyActionGrid", 2, "menu.security.emergencyActions.edit", enableEMRG));
        }
    }

    public ArrayList<TreeMenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(ArrayList<TreeMenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
