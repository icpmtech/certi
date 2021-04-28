/*
 * $Id: AbstractActionBean.java,v 1.26 2012/05/28 16:50:38 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/05/28 16:50:38 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.presentation.action;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Plan;
import com.criticalsoftware.certitools.presentation.util.CertitoolsActionBeanContext;
import com.criticalsoftware.certitools.presentation.util.Menu;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.TreeNode;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.validation.ValidationErrors;

import java.util.ArrayList;

/**
 * Abstract Class to be used by Actions Beans
 *
 * @author : jp-gomes
 * @version : $version $
 */
public abstract class AbstractActionBean implements ActionBean {
    private CertitoolsActionBeanContext context;
    private String helpId;

    // Module Type to support multiple types of plans (needed in plan action beans)
    private String planModuleType = "PEI";


    public abstract void fillLookupFields();

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

    public String getHelpId() {
        return helpId;
    }

    public void setAttribute(String s, Object o) {
        getContext().getRequest().setAttribute(s, o);
    }

    public void setSessionAttribute(String s, Object o) {
        getContext().getRequest().getSession().setAttribute(s, o);
    }

    public void setContext(ActionBeanContext actionBeanContext) {
        this.context = (CertitoolsActionBeanContext) actionBeanContext;
    }

    public ActionBeanContext getContext() {
        return this.context;
    }

    @SuppressWarnings({"SimplifiableIfStatement"})
    public boolean getUserPEIPreview() {
        Boolean userPEIPreview = (Boolean) getContext().getRequest().getSession().getAttribute("userPEIPreview");
        if (userPEIPreview == null) {
            return false;
        }
        return userPEIPreview;
    }

    public void setValidationErrors(ValidationErrors errors) {
        getContext().getRequest().setAttribute("errors", errors);
    }

    public ValidationErrors getValidationErrors() {
        return (ValidationErrors) getContext().getRequest().getAttribute("errors");
    }

    public void setUserPEIPreview(boolean userPEIPreview) {
        getContext().getRequest().getSession().setAttribute("userPEIPreview", userPEIPreview);
    }

    public void setFolder(Folder folder) {
        getContext().getRequest().setAttribute("folder", folder);
    }

    public Folder getFolder() {
        return (Folder) getContext().getRequest().getAttribute("folder");
    }

    public void setPlanManagerInRequest(boolean peiManager) {
        getContext().getRequest().setAttribute("planManager", peiManager);
    }

    public Boolean getPlanManagerInRequest() {
        return (Boolean) getContext().getRequest().getAttribute("planManager");
    }

    public void setPathCM(String pathCM) {
        getContext().getRequest().setAttribute("pathCMPEI", pathCM);
    }

    public String getPathCM() {
        return (String) getContext().getRequest().getAttribute("pathCMPEI");
    }

    public void setSection(Folder sectionFolder) {
        getContext().getRequest().setAttribute("section", sectionFolder);
    }

    public Folder getSection() {
        return (Folder) getContext().getRequest().getAttribute("section");
    }

    public void setPEITreeNodes(ArrayList<TreeNode> peiTreeNodes) {
        getContext().getRequest().setAttribute("peiTreeNodes", peiTreeNodes);
    }

    @SuppressWarnings({"unchecked"})
    public ArrayList<TreeNode> getPEITreeNodes() {
        return (ArrayList<TreeNode>) getContext().getRequest().getAttribute("peiTreeNodes");
    }

    public void setPEI(Plan pei) {
        getContext().getRequest().setAttribute("pei", pei);
    }

    public Plan getPEI() {
        return (Plan) getContext().getRequest().getAttribute("pei");
    }

    public void setBreadcrumbs(ArrayList<TreeNode> breadcrumbs) {
        getContext().getRequest().setAttribute("breadcrumbs", breadcrumbs);
    }

    @SuppressWarnings({"unchecked"})
    public ArrayList<TreeNode> getBreadcrumbs() {
        return (ArrayList<TreeNode>) getContext().getRequest().getAttribute("breadcrumbs");
    }

    /**
     * Gets the authenticated user in session
     *
     * @return the user
     */
    public User getUserInSession() {
        return (User) getContext().getRequest().getSession().getAttribute("user");
    }

    /**
     * Sets a user in session
     *
     * @param user the user
     */
    public void setUserInSession(User user) {
        getContext().getRequest().getSession().setAttribute("user", user);
    }

    /**
     * Check user in role
     *
     * @param role the role to be checked
     * @return true or false
     */
    public boolean isUserInRole(String role) {
        return getContext().getRequest().isUserInRole(role);
    }

    /**
     * Gets the menu
     *
     * @return the menu
     */
    public Menu getMenu() {
        return (Menu) getContext().getRequest().getSession().getAttribute("menu");
    }

    public ModuleType getModuleTypeFromEnum() {
        return ModuleType.valueOf(planModuleType);
    }

    public String getPlanModuleType() {
        return planModuleType;
    }

    public void setPlanModuleType(String planModuleType) {
        this.planModuleType = planModuleType;
    }

    //TODO-MODULE
    public void setPlanMenu(MenuItem.Item menuItemPEI, MenuItem.Item menuItemPRV, MenuItem.Item menuItemPSI, MenuItem.Item menuItemGSC) {
        getMenu().select(MenuItem.Item.MENU_PEI, menuItemPEI);

        if (getContext().getRequest().getParameter("planModuleType") == null){
            return;
        }
        
        ModuleType moduleTypeTemp = ModuleType.valueOf(getContext().getRequest().getParameter("planModuleType"));
        if (moduleTypeTemp.equals(ModuleType.PRV)) {
            getMenu().select(MenuItem.Item.MENU_SAFETY, menuItemPRV);
        } else if(moduleTypeTemp.equals(ModuleType.PSI)){
            getMenu().select(MenuItem.Item.MENU_PSI,menuItemPSI);
        } else if(moduleTypeTemp.equals(ModuleType.GSC)){
            getMenu().select(MenuItem.Item.MENU_GSC,menuItemGSC);
        }
    }
}
