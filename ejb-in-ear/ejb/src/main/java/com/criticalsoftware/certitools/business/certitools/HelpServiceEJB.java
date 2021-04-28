/*
 * $Id: HelpServiceEJB.java,v 1.3 2009/10/20 17:23:35 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/20 17:23:35 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.entities.HelpSearchableContent;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.Utils;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.SessionContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Help Service Implementation
 *
 * @author jp-gomes
 */
@Stateless
@Local(HelpService.class)
@LocalBinding(jndiBinding = "certitools/HelpService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class HelpServiceEJB implements HelpService {

    @EJB
    private HelpDAO helpDAO;

    @Resource
    private SessionContext sessionContext;

    @RolesAllowed(value = "user")
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<HelpSearchableContent> search(String searchPhrase, User userInSession) {
        List<HelpSearchableContent> result = new ArrayList<HelpSearchableContent>();
        if (searchPhrase != null) {
            String[] split = searchPhrase.split(" ");
            if (split != null && split.length > 0) {
                List<String> searchItems = new ArrayList<String>();
                //Remove accents and make string toLowerCase for search
                for (String s : split) {
                    searchItems.add(Utils.removeAccentedChars(s).toLowerCase());
                }
                result = helpDAO.search(searchItems);
            }
        } else {
            result = helpDAO.search(null);
        }
        return filterSearchResultByPermissions(result, userInSession);
    }

    private List<HelpSearchableContent> filterSearchResultByPermissions(
            List<HelpSearchableContent> helpSearchableContents, User userInSession) {

        List<HelpSearchableContent> filteredHelpSearchableContents = new ArrayList<HelpSearchableContent>();
        if (helpSearchableContents == null) {
            return filteredHelpSearchableContents;
        }
        for (HelpSearchableContent helpSearchableContent : helpSearchableContents) {
            if (isUserWithAcess(parseHelpSearchableContentPermissions(helpSearchableContent.getPermissions()),
                    findHelpModule(helpSearchableContent.getFileName()), userInSession)) {
                filteredHelpSearchableContents.add(helpSearchableContent);
            }
        }
        return filteredHelpSearchableContents;
    }

    private List<String> parseHelpSearchableContentPermissions(String permissionPhrase) {
        List<String> permissions = new ArrayList<String>();
        if (permissionPhrase == null) {
            return permissions;
        }
        String[] permissionPhraseSplit = permissionPhrase.split(",");

        permissions.addAll(Arrays.asList(permissionPhraseSplit));
        return permissions;
    }

    private boolean isUserWithAcess(List<String> helpSearchableContentPermissions, String helpModule,
                                    User userInSession) {

        if (helpSearchableContentPermissions == null || helpSearchableContentPermissions.isEmpty()) {
            return true;
        }
        if (helpModule.equals("pei")) {
            if (!userInSession.isAccessPEI()) {
                return false;
            }
        }
        if (helpModule.equals("legislation")) {
            if (!userInSession.isAccessLegislation()) {
                return false;
            }
        }
        for (String helpSearchableContentPermission : helpSearchableContentPermissions) {
            if (sessionContext.isCallerInRole(helpSearchableContentPermission)) {
                return true;
            }
        }
        return false;
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
}
