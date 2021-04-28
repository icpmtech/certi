/*
 * $Id: LoginRedirectActionBean.java,v 1.6 2013/12/18 03:16:28 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2013/12/18 03:16:28 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.certitools;

import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean;
import com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean;
import com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean;
import com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean;
import com.criticalsoftware.certitools.util.ModuleType;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;

import java.util.Map;

/**
 * Responsable for redirecting the user (after login) to the correct module homepage according to the user
 * permissions
 * Modules priority (if user can access multiple modules): PEI, Prevention Plan, Protection Plan, Legislation, Machines
 *
 * @author pjfsilva
 */
public class LoginRedirectActionBean extends AbstractActionBean {
    public void fillLookupFields() {
        // not needed, all handlers redirect to another action bean
    }

    @DefaultHandler
    public Resolution redirectToModule() {

        //this is to redirect the user to the security emergency action of the GSC module
        //because that url is not protected by the realm
        Map parameters = getContext().getRequest().getParameterMap();
        String[] isEmergencyEdit = (String[]) parameters.get("securityEmergencyEdit");
        String[] contractId = (String[]) parameters.get("contractId");
        String[] emergencyId = (String[]) parameters.get("emergencyId");
        if (isEmergencyEdit != null && contractId != null && emergencyId != null
                && isEmergencyEdit.length > 0 && contractId.length > 0 && emergencyId.length > 0
                && isEmergencyEdit[0].equals("true")) {
            return new RedirectResolution(SecurityEmergencyActionBean.class, "emergencyActionEdit")
                    .addParameter("contractId", contractId[0])
                    .addParameter("emergencyId", emergencyId[0]);
        }

        //TODO-MODULE
        // priority by the client request: PEI, PPrev, PProt, Legislacaoo, Maquinas
        if (getUserInSession().isAccessPEI()) {
            return new RedirectResolution(PlanActionBean.class).addParameter("planModuleType", ModuleType.PEI);
        }
        if (getUserInSession().isAccessPRV()) {
            return new RedirectResolution(PlanActionBean.class).addParameter("planModuleType", ModuleType.PRV);
        }
        if (getUserInSession().isAccessPSI()) {
            return new RedirectResolution(PlanActionBean.class).addParameter("planModuleType", ModuleType.PSI);
        }
        if (getUserInSession().isAccessGSC()) {
            return new RedirectResolution(SecurityActionBean.class).addParameter("planModuleType", ModuleType.GSC);
        }
        if (getUserInSession().isAccessLegislation()) {
            return new RedirectResolution(LegislationActionBean.class);
        }

        return new RedirectResolution(PlanActionBean.class);
    }
}