/*
 * $Id: TableCMPermissionsDecorator.java,v 1.4 2009/10/06 11:07:47 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/06 11:07:47 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import org.displaytag.decorator.TableDecorator;
import com.criticalsoftware.certitools.entities.Permission;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class TableCMPermissionsDecorator extends TableDecorator {

    public String addRowId() {
        Permission permission = (Permission) this.getCurrentRowObject();
        return permission.getId() + "";
    }

    public String addRowClass() {
        return "even";
    }

    public String finishRow() {
        Permission permission = (Permission) this.getCurrentRowObject();
        StringBuilder sb = new StringBuilder();
        //Add new Row for showing permission information
        sb.append("<tr id=\"row");
        sb.append(permission.getId());
        sb.append("\"class=\"peiPermissionDetailsRow\"><td colspan=\"2\" class=\"peiPermissionDetailsColumn\" style=\"padding:0 !important\">");
        sb.append("<div id=\"usedPermissionList");
        sb.append(permission.getId());
        sb.append("\" class=\"peiPermissionDetailsDiv\"></div>");
        sb.append("</td></tr>");

        sb.append("<script type=\"text/javascript\" language=\"javascript\" charset=\"utf-8\">");
        sb.append("addCMPermissionsRowEvent('");
        sb.append(permission.getId());
        sb.append("', '");
        sb.append(getPageContext().getServletContext().getContextPath());
        sb.append("', '");
        sb.append(permission.getContract().getId());
        sb.append("', '");
        sb.append(getPageContext().getRequest().getParameter("planModuleType"));     
        sb.append("');");

        sb.append("$('#");
        sb.append(permission.getId());
        sb.append("').css('cursor', 'pointer');");

        sb.append("</script>");
        return sb.toString();
    }
}