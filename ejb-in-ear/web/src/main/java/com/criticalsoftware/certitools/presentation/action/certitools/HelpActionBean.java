/*
 * $Id: HelpActionBean.java,v 1.9 2009/10/23 10:38:15 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/23 10:38:15 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.action.certitools;

import com.criticalsoftware.certitools.business.certitools.HelpService;
import com.criticalsoftware.certitools.entities.HelpSearchableContent;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.LifecycleStage;

import javax.annotation.security.RolesAllowed;
import java.util.List;

/**
 * Help Action Bean
 *
 * @author haraujo
 */
public class HelpActionBean extends AbstractActionBean {
    private String helpId;
    private String helpModule;
    private String searchPhrase;
    private List<HelpSearchableContent> helpSearchableContents;

    @EJBBean(value = "certitools/HelpService")
    private HelpService helpService;

    @DefaultHandler
    @RolesAllowed(value = "user")
    public Resolution view() {
        if (helpId == null) {
            helpId = "user-profile";
        }
        helpModule = findHelpModule(helpId);
        return new ForwardResolution("/WEB-INF/jsps/help.jsp");
    }

    @RolesAllowed(value = "user")
    public Resolution searchHelp() {
        helpSearchableContents = helpService.search(searchPhrase, getUserInSession());
        return new ForwardResolution("/WEB-INF/jsps/help.jsp");
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(null, null);
    }

    private String findHelpModule(String helpId) {
        if (helpId == null) {
            return "certitools";
        }
        if (helpId.startsWith("pei")) {
            return "pei";
        }
        if (helpId.startsWith("legislation")) {
            return "legislation";
        }
        return "certitools";
    }

    public String getHelpId() {
        return helpId;
    }

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

    public String getHelpModule() {
        return helpModule;
    }

    public void setHelpModule(String helpModule) {
        this.helpModule = helpModule;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }

    public HelpService getHelpService() {
        return helpService;
    }

    public void setHelpService(HelpService helpService) {
        this.helpService = helpService;
    }

    public List<HelpSearchableContent> getHelpSearchableContents() {
        return helpSearchableContents;
    }

    public void setHelpSearchableContents(List<HelpSearchableContent> helpSearchableContents) {
        this.helpSearchableContents = helpSearchableContents;
    }
}
