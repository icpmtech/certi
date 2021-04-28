/*
 * $Id: Menu.java,v 1.46 2013/06/28 17:27:54 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2013/06/28 17:27:54 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Menu
 *
 * @author : lt-rico
 */
public class Menu {
    // Principal Menu

    private Collection<MenuItem> menuItems;

    public Menu(HttpServletRequest request, String peiLabel, String pprevLabel, String psiLabel) {
        menuItems = new ArrayList<MenuItem>();

        MenuItem legislation = new MenuItem(MenuItem.Item.MENU_LEGISLATION, "menu.legislation",
                "/legislation/Legislation.action", "");

        MenuItem searchLeagislation = new MenuItem(MenuItem.Item.SUB_MENU_LEGISLATION_SEARCH, "menu.legislation.search",
                "/legislation/Legislation.action", "Pesquisa.png");
        legislation.addItem(searchLeagislation);

        MenuItem faq = new MenuItem(MenuItem.Item.SUB_MENU_LEGISLATION_FAQ, "menu.legislation.faq",
                "/certitools/FAQ.action?viewModuleFAQ=&amp;moduleType=LEGISLATION", "faqs.png");
        legislation.addItem(faq);

        if (request.isUserInRole("legislationmanager") || request.isUserInRole("administrator") ||
                request.isUserInRole("contractmanager")) {
            MenuItem stats = new MenuItem(MenuItem.Item.SUB_MENU_LEGISLATION_STATISTICS, "menu.legislation.statistics",
                    "/legislation/Statistics.action", "sum.png");
            legislation.addItem(stats);

            if (request.isUserInRole("legislationmanager") || request.isUserInRole("administrator")) {
                MenuItem newsletterAdmin = new MenuItem(MenuItem.Item.SUB_MENU_LEGISLATION_NEWSLETTER_ADMIN,
                        "menu.legislation.newsletter.admin",
                        "/legislation/Newsletter.action", "newspaper.png");
                legislation.addItem(newsletterAdmin);
            }
        }

        menuItems.add(legislation);

        // TODO-MODULE
        MenuItem pei = new MenuItem(MenuItem.Item.MENU_PEI, "menu.emergency",
                "/plan/Plan.action", "");

        if (peiLabel != null && !StringUtils.isEmpty(peiLabel)) {
            pei.setResourceKey(false);
            pei.setKey(peiLabel);
        }

        MenuItem peiView = new MenuItem(MenuItem.Item.SUB_MENU_PEI_VIEW, "menu.pei.view", "/plan/Plan.action",
                "package_editors.png");
        if (peiLabel != null && !StringUtils.isEmpty(peiLabel)) {
            peiView.setResourceKey(false);
            peiView.setKey(peiLabel);
        }
        pei.addItem(peiView);

        if (request.isUserInRole("peimanager") || request.isUserInRole("clientpeimanager")) {
            pei.addItem(new MenuItem(MenuItem.Item.SUB_MENU_PEI_ADMIN, "menu.pei.administration",
                    "/plan/PlanCM.action", "config.png"));
        }

        if (request.isUserInRole("peimanager")) {
            pei.addItem(new MenuItem(MenuItem.Item.SUB_MENU_PEI_COPY, "menu.pei.copy",
                    "/plan/PlanCMCopy.action", "copy.png"));
        }

        if (request.isUserInRole("peimanager") || request.isUserInRole("clientpeimanager")) {
            pei.addItem(new MenuItem(MenuItem.Item.SUB_MENU_PEI_PERMISSIONS, "menu.pei.permissions",
                    "/plan/PlanCMPermissions.action", "kgpg.png", "sub_menu_pei_permissions"));
        }

        if (request.isUserInRole("peimanager")) {
            pei.addItem(new MenuItem(MenuItem.Item.SUB_MENU_PEI_DOCX, "menu.pei.docx",
                    "/plan/PlanCMTemplatesDocx.action", "export.png"));

            pei.addItem(new MenuItem(MenuItem.Item.SUB_MENU_PEI_MIGRATION, "menu.pei.migration",
                    "/plan/PlanCMMigration.action", "migration.png"));
        }

        MenuItem faqPEI = new MenuItem(MenuItem.Item.SUB_MENU_PEI_FAQ, "menu.legislation.faq",
                "/certitools/FAQ.action?viewModuleFAQ=&amp;moduleType=PEI", "faqs.png");
        pei.addItem(faqPEI);

        menuItems.add(pei);

        // Safety plan: PPREV
        MenuItem safety = new MenuItem(MenuItem.Item.MENU_SAFETY, "menu.safety",
                "/plan/Plan.action?planModuleType=PRV", "");

        if (pprevLabel != null && !StringUtils.isEmpty(pprevLabel)) {
            safety.setResourceKey(false);
            safety.setKey(pprevLabel);
        }

        MenuItem safetyView = new MenuItem(MenuItem.Item.SUB_MENU_SAFETY_VIEW, "menu.safety.view",
                "/plan/Plan.action?planModuleType=PRV",
                "package_editors.png");
        if (pprevLabel != null && !StringUtils.isEmpty(pprevLabel)) {
            safetyView.setResourceKey(false);
            safetyView.setKey(pprevLabel);
        }

        safety.addItem(safetyView);

        if (request.isUserInRole("peimanager") || request.isUserInRole("clientpeimanager")) {
            safety.addItem(new MenuItem(MenuItem.Item.SUB_MENU_SAFETY_ADMIN, "menu.safety.administration",
                    "/plan/PlanCM.action?planModuleType=PRV", "config.png"));
        }

        if (request.isUserInRole("peimanager")) {
            safety.addItem(new MenuItem(MenuItem.Item.SUB_MENU_SAFETY_COPY, "menu.safety.copy",
                    "/plan/PlanCMCopy.action?planModuleType=PRV", "copy.png"));
        }

        if (request.isUserInRole("peimanager") || request.isUserInRole("clientpeimanager")) {
            safety.addItem(new MenuItem(MenuItem.Item.SUB_MENU_SAFETY_PERMISSIONS, "menu.safety.permissions",
                    "/plan/PlanCMPermissions.action?planModuleType=PRV", "kgpg.png", "sub_menu_safety_permissions"));
        }

        if (request.isUserInRole("peimanager")) {
            safety.addItem(new MenuItem(MenuItem.Item.SUB_MENU_SAFETY_DOCX, "menu.pei.docx",
                    "/plan/PlanCMTemplatesDocx.action?planModuleType=PRV", "export.png"));

            safety.addItem(new MenuItem(MenuItem.Item.SUB_MENU_SAFETY_MIGRATION, "menu.pei.migration",
                    "/plan/PlanCMMigration.action?planModuleType=PRV", "migration.png"));
        }

        safety.addItem(new MenuItem(MenuItem.Item.SUB_MENU_SAFETY_FAQ, "menu.legislation.faq",
                "/certitools/FAQ.action?viewModuleFAQ=&amp;moduleType=PRV", "faqs.png"));

        menuItems.add(safety);

        //PSI - Module START
        MenuItem psi = new MenuItem(MenuItem.Item.MENU_PSI, "menu.psi",
                "/plan/Plan.action?planModuleType=PSI", "");

        if (psiLabel != null && !StringUtils.isEmpty(psiLabel)) {
            psi.setResourceKey(false);
            psi.setKey(psiLabel);
        }

        MenuItem psiView = new MenuItem(MenuItem.Item.SUB_MENU_PSI_VIEW, "menu.psi.view",
                "/plan/Plan.action?planModuleType=PSI",
                "package_editors.png");
        if (psiLabel != null && !StringUtils.isEmpty(psiLabel)) {
            psiView.setResourceKey(false);
            psiView.setKey(psiLabel);
        }
        psi.addItem(psiView);

        if (request.isUserInRole("peimanager") || request.isUserInRole("clientpeimanager")) {
            psi.addItem(new MenuItem(MenuItem.Item.SUB_MENU_PSI_ADMIN, "menu.psi.administration",
                    "/plan/PlanCM.action?planModuleType=PSI", "config.png"));
        }

        if (request.isUserInRole("peimanager")) {
            psi.addItem(new MenuItem(MenuItem.Item.SUB_MENU_PSI_COPY, "menu.psi.copy",
                    "/plan/PlanCMCopy.action?planModuleType=PSI", "copy.png"));
        }

        if (request.isUserInRole("peimanager") || request.isUserInRole("clientpeimanager")) {
            psi.addItem(new MenuItem(MenuItem.Item.SUB_MENU_PSI_PERMISSIONS, "menu.psi.permissions",
                    "/plan/PlanCMPermissions.action?planModuleType=PSI", "kgpg.png", "sub_menu_psi_permissions"));
        }

        if (request.isUserInRole("peimanager")) {
            psi.addItem(new MenuItem(MenuItem.Item.SUB_MENU_PSI_DOCX, "menu.pei.docx",
                    "/plan/PlanCMTemplatesDocx.action?planModuleType=PSI", "export.png"));

            psi.addItem(new MenuItem(MenuItem.Item.SUB_MENU_PSI_MIGRATION, "menu.pei.migration",
                    "/plan/PlanCMMigration.action?planModuleType=PSI", "migration.png"));
        }

        psi.addItem(new MenuItem(MenuItem.Item.SUB_MENU_PSI_FAQ, "menu.legislation.faq",
                "/certitools/FAQ.action?viewModuleFAQ=&amp;moduleType=PSI", "faqs.png"));

        menuItems.add(psi);
        //PSI - Module END

        MenuItem security =
                new MenuItem(MenuItem.Item.MENU_SECURITY, "menu.security", "/sm/Security.action", "");
        menuItems.add(security);

        MenuItem machinery =
                new MenuItem(MenuItem.Item.MENU_MACHINERY, "menu.machinery", "/machinery/Machinery.action", "");
        //menuItems.add(machinery);

        // Administration Menu
        if (request.isUserInRole("administrator") || request.isUserInRole("contractmanager")
                || request.isUserInRole("clientcontractmanager")) {
            MenuItem admin = new MenuItem(MenuItem.Item.MENU_ADMIN, "menu.administration", null, "");

            admin.addItem(new MenuItem(MenuItem.Item.SUB_MENU_ADMIN_COMPANY, "menu.administration.company",
                    "/certitools/Company.action", "kfm_home.png"));

            if (request.isUserInRole("administrator")) {
                MenuItem configuration = new MenuItem(MenuItem.Item.SUB_MENU_ADMIN_CONFIG,
                        "menu.administration.configuration", "/certitools/Configuration.action", "config.png");
                admin.addItem(configuration);

                admin.addItem(new MenuItem(MenuItem.Item.SUB_MENU_ADMIN_NEWS, "menu.administration.news",
                        "/certitools/News.action", "news.png"));

                admin.addItem(new MenuItem(MenuItem.Item.SUB_MENU_ADMIN_FAQ, "menu.legislation.faq",
                        "/certitools/FAQ.action", "faqs.png"));
            }
            //place here another submenus

            menuItems.add(admin);

        }
    }

    public Collection<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void removeSubMenu(MenuItem.Item item1) {

        for (MenuItem menuItem1 : menuItems) {
            if (menuItem1.getItem().equals(item1)) {
                menuItem1.setMenuItems(null);
            }
        }
    }

    public void select(MenuItem.Item item1, MenuItem.Item item2) {
        if (item1 != null && menuItems != null) {
            for (MenuItem menuItem1 : menuItems) {
                if (menuItem1.getItem().equals(item1)) {
                    menuItem1.setSelected(true);
                } else {
                    menuItem1.setSelected(false);
                }
                if (item2 != null && menuItem1.getMenuItems() != null) {
                    for (MenuItem menuItem2 : menuItem1.getMenuItems()) {
                        if (menuItem2.getItem().equals(item2)) {
                            menuItem2.setSelected(true);
                        } else {
                            menuItem2.setSelected(false);
                        }
                        /*if (item3 != null && menuItem2.getMenuItems() != null) {
                            for (MenuItem menuItem3 : menuItem2.getMenuItems()) {
                                if (menuItem3.getName().equals(item3)) {
                                    menuItem3.setSelected(true);
                                } else {
                                    menuItem3.setSelected(false);
                                }
                            }
                        }*/
                    }
                }
            }
        } else if (menuItems != null) {
            for (MenuItem item : menuItems) {
                item.setSelected(false);
            }
        }
    }

    /**
     * Get selected second level menu
     *
     * @return - Selected MenuItem
     */
    public MenuItem getSelected2LevelMenu() {
        for (MenuItem menuItem : menuItems) {
            if (menuItem.isSelected()) {
                for (MenuItem meuItem2 : menuItem.getMenuItems()) {
                    if (meuItem2.isSelected()) {
                        return meuItem2;
                    }
                }
            }
        }
        return null;
    }

}
