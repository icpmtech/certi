/*
 * $Id: MenuItem.java,v 1.20 2013/06/28 17:27:54 pjfsilva Exp $
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

import java.util.Collection;
import java.util.ArrayList;

/**
 * Menu Item
 *
 * @author : lt-rico
 */
public class MenuItem {

    public enum Item {
        MENU_LEGISLATION("LEGISLATION"),
        MENU_PEI("PEI"),
        MENU_SAFETY("SAFETY"),
        MENU_PSI("PSI"),
        MENU_GSC("GSC"),
        MENU_SECURITY("SECURITY"),
        MENU_MACHINERY("MACHINERY"),
        MENU_ADMIN("ADMINISTRATION"),

        SUB_MENU_LEGISLATION_SEARCH("LEGISLATION_SEARCH"),
        SUB_MENU_LEGISLATION_NEWSLETTER_ADMIN("LEGISLATION_NEWSLETTER_ADMIN"),
        SUB_MENU_LEGISLATION_FAQ("LEGISLATION_FAQ"),
        SUB_MENU_LEGISLATION_STATISTICS("LEGISLATION_STATISTICS"),
        SUB_MENU_ADMIN_CONFIG("CONFIGURATION"),
        SUB_MENU_ADMIN_NEWS("NEWS"),
        SUB_MENU_ADMIN_COMPANY("COMPANY"),
        SUB_MENU_ADMIN_MASTERPASSWORD("MASTERPASSWORD"),
        SUB_MENU_ADMIN_FAQ("ADMIN_FAQ"),

        SUB_MENU_PEI_VIEW("VIEW_PEI"),
        SUB_MENU_PEI_ADMIN("ADMIN_PEI"),
        SUB_MENU_PEI_COPY("COPY_PEI"),
        SUB_MENU_PEI_PERMISSIONS("PERMISSIONS_PEI"),
        SUB_MENU_PEI_FAQ("FAQ_PEI"),
        SUB_MENU_PEI_DOCX("DOCX_PEI"),
        SUB_MENU_PEI_MIGRATION("MIGRATION_PEI"),

        SUB_MENU_SAFETY_VIEW("VIEW_SAFETY"),
        SUB_MENU_SAFETY_ADMIN("ADMIN_SAFETY"),
        SUB_MENU_SAFETY_COPY("COPY_SAFETY"),
        SUB_MENU_SAFETY_PERMISSIONS("PERMISSIONS_SAFETY"),
        SUB_MENU_SAFETY_FAQ("FAQ_SAFETY"),
        SUB_MENU_SAFETY_DOCX("DOCX_SAFETY"),
        SUB_MENU_SAFETY_MIGRATION("MIGRATION_SAFETY"),

        SUB_MENU_PSI_VIEW("VIEW_PSI"),
        SUB_MENU_PSI_ADMIN("ADMIN_PSI"),
        SUB_MENU_PSI_COPY("COPY_PSI"),
        SUB_MENU_PSI_PERMISSIONS("PERMISSIONS_PSI"),
        SUB_MENU_PSI_FAQ("FAQ_PSI"),
        SUB_MENU_PSI_DOCX("DOCX_PSI"),
        SUB_MENU_PSI_MIGRATION("MIGRATION_PSI"),

        SUB_MENU_GSC_VIEW("VIEW_GSC"),
        SUB_MENU_GSC_ADMIN("ADMIN_GSC"),
        SUB_MENU_GSC_COPY("COPY_GSC"),
        SUB_MENU_GSC_PERMISSIONS("PERMISSIONS_GSC"),
        SUB_MENU_GSC_FAQ("FAQ_GSC"),
        SUB_MENU_GSC_DOCX("DOCX_GSC"),
        SUB_MENU_GSC_MIGRATION("MIGRATION_GSC");

        private String name;

        Item(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private Item item;
    private String key;
    private String link;
    private String image;
    private String id;
    // defines if the key property contains a key for a resource (true) or the real String to print in the menu (false)
    private boolean resourceKey = true;

    private boolean selected = false;
    private Collection<MenuItem> menuItems = null;

    private boolean disabled;

    public MenuItem(Item item, String key, String link, String image) {
        this.item = item;
        this.key = key;
        this.link = link;
        this.image = image;
    }

    public MenuItem(Item item, String key, String link, String image, String id) {
        this.item = item;
        this.key = key;
        this.link = link;
        this.image = image;
        this.id = id;
    }

    public void addItem(MenuItem item) {
        if (menuItems == null) {
            menuItems = new ArrayList<MenuItem>();
        }
        menuItems.add(item);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        if (menuItems != null && !menuItems.isEmpty()) {
            return menuItems.iterator().next().getLink();
        } else {
            return link;
        }
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Collection<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(Collection<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(boolean resourceKey) {
        this.resourceKey = resourceKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MenuItem menuItem = (MenuItem) o;

        return item == menuItem.item;
    }

    public int hashCode() {
        return (item != null ? item.hashCode() : 0);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
