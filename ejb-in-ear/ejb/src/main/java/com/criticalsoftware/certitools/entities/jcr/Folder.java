/*
 * $Id: Folder.java,v 1.40 2009/10/20 21:42:30 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/10/20 21:42:30 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Folder Object
 *
 * @author : lt-rico
 */
@Node(extend = HierarchyNode.class)
public class Folder extends HierarchyNode implements Comparable<Folder> {

    @Field
    private String help;

    @Field
    private Boolean active;

    @Field
    private Integer order;

    @Field
    private Date lastSaveDate;

    @Field
    private String lastSaveAuthor;

    @Field
    private String folderHeader;

    @Field
    private String folderFooter;

    @Field
    private String publishedRelatedPath;

    @Field
    private Date publishedDate;

    @Field
    private String publishedAuthor;

    @Field
    private Boolean includeInMenu;

    @Bean
    private Template template;

    @Collection
    private List<Permission> permissions;

    @Collection(proxy = true, autoUpdate = false)
    private List<Folder> folders;

    @Collection
    private List<FolderMirrorReference> folderMirrorReferences;

    private String oldName;

    private String parentPath;

    private long depth;

    private Boolean special;

    public Folder() {
    }

    public Folder(String path, String name, String help, Boolean active, int order,
                  Template template, List<Permission> permissions, List<Folder> folders) {
        super(path, name);

        this.help = help;
        this.active = active;
        this.order = order;
        this.template = template;
        this.permissions = permissions;
        this.folders = folders;
        this.lastSaveAuthor = "";
        this.folderFooter = " ";
        this.folderHeader = " ";
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Permission> getPermissions() {
        if (permissions == null) {
            return new ArrayList<Permission>();
        }
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Folder> getFolders() {
        if (folders == null) {
            return new ArrayList<Folder>();
        }
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public Date getLastSaveDate() {
        return lastSaveDate;
    }

    public void setLastSaveDate(Date lastSaveDate) {
        this.lastSaveDate = lastSaveDate;
    }

    public String getLastSaveAuthor() {
        return lastSaveAuthor;
    }

    public void setLastSaveAuthor(String lastSaveAuthor) {
        this.lastSaveAuthor = lastSaveAuthor;
    }

    public String getFolderHeader() {
        return folderHeader;
    }

    public void setFolderHeader(String folderHeader) {
        this.folderHeader = folderHeader;
    }

    public String getFolderFooter() {
        return folderFooter;
    }

    public void setFolderFooter(String folderFooter) {
        this.folderFooter = folderFooter;
    }

    public long getDepth() {
        return depth;
    }

    public void setDepth(long depth) {
        this.depth = depth;
    }

    public Boolean getSpecial() {
        return special;
    }

    public void setSpecial(Boolean special) {
        this.special = special;
    }

    public String getPublishedRelatedPath() {
        return publishedRelatedPath;
    }

    public void setPublishedRelatedPath(String publishedRelatedPath) {
        this.publishedRelatedPath = publishedRelatedPath;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getPublishedAuthor() {
        return publishedAuthor;
    }

    public void setPublishedAuthor(String publishedAuthor) {
        this.publishedAuthor = publishedAuthor;
    }

    public List<FolderMirrorReference> getFolderMirrorReferences() {
        return folderMirrorReferences;
    }

    public void setFolderMirrorReferences(List<FolderMirrorReference> folderMirrorReferences) {
        this.folderMirrorReferences = folderMirrorReferences;
    }

    public Boolean getIncludeInMenu() {
        return includeInMenu;
    }

    public Boolean getIncludeInMenuOrIsNavigable() {
        if (includeInMenu == null) {
            return isNavigable();
        }
        return includeInMenu;
    }

    public void setIncludeInMenu(Boolean includeInMenu) {
        this.includeInMenu = includeInMenu;
    }

    public boolean hasFrontOffice(Integer order) {
        if (this.getTemplate() == null) {
            return false;
        }
        if (this.getTemplate().getName().equals(Template.Type.TEMPLATE_FAQ_ELEMENT.getName())) {
            return false;
        } else if (this.getTemplate().getName().equals(Template.Type.TEMPLATE_CONTACTS_ELEMENT.getName())
                && order == null) {
            return false;
        } else if (this.getTemplate().getName().equals(Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName())
                && order == null) {
            return false;
        } else if (this.getTemplate().getName().equals(Template.Type.TEMPLATE_MEANS_RESOURCES_ELEMENT.getName())
                && order == null) {
            return false;
        }

        // TODO-Templates according to all templates, do more if/else

        return true;
    }

    /**
     * Is the folder navigable (i.e. should appear in the frontoffice menu)
     *
     * @return -
     */
    public boolean isNavigable() {

        if (template.getName().equals(Template.Type.TEMPLATE_MIRROR.getName())){
            return isNavigable(((Template11Mirror)getTemplate()).getSourceTemplateName());
        }

        return isNavigable(null);
    }

    /**
     * Is the folder navigable (i.e. should appear in the frontoffice menu)
     *
     * @param templateName - if not null, use this template name
     * @return -
     */
    public boolean isNavigable(String templateName) {
        if (this.getTemplate() == null) {
            return false;
        }

        if (templateName == null) {
            templateName = this.getTemplate().getName();
        }
        if (templateName.equals(Template.Type.TEMPLATE_RESOURCE.getName())) {
            return false;
        } else if (templateName.equals(Template.Type.TEMPLATE_FAQ_ELEMENT.getName())) {
            return false;
        } else if (templateName.equals(Template.Type.TEMPLATE_CONTACTS_ELEMENT.getName())) {
            return false;
        } else if (templateName.equals(Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName())) {
            return false;
        } else if (templateName.equals(Template.Type.TEMPLATE_MEANS_RESOURCES_ELEMENT.getName())) {
            return false;
        }

        // TODO-Templates according to all templates, do more if/else

        return true;
    }

    /**
     * Checks if this folder allows its children to appear in the menu (are navigable)
     *
     * @return true if folder children should appear in the menu (are navigable)
     */
    @SuppressWarnings({"RedundantIfStatement"})
    public boolean isNavigableChildren() {
        if (this.getTemplate() == null) {
            return true;
        }

        if (this.getTemplate().getName().equals(Template.Type.TEMPLATE_PROCEDURE.getName())) {
            return false;
        }

        // TODO-Templates according to all templates, do more if/else

        return true;
    }

    @SuppressWarnings({"RedundantIfStatement"})
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Folder folder = (Folder) o;

        if (!super.getPath().equals(folder.getPath())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (super.getPath() != null ? super.getPath().hashCode() : 0);
    }

    public int compareTo(Folder o) {
        if (o != null) {
            if (this.order > o.getOrder()) {
                return 1;
            } else if (this.order < o.getOrder()) {
                return -1;
            } else {
                return this.getName().compareTo(o.getName());
            }
        }
        return 1;
    }
}