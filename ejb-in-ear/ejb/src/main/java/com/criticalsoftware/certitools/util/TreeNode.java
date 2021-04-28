/*
 * $Id: TreeNode.java,v 1.21 2009/10/16 13:29:09 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/16 13:29:09 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.util;

import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Permission;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.TreeSet;

/**
 * Implements a Node of a Tree (with this we can also have a tree, because of the reference to children nodes)
 *
 * @author pjfsilva
 */
public class TreeNode implements Comparable<TreeNode> {

    private String name;
    private String path;

    private int depth;
    private int order;

    private String cssToApply;
    private String pathToUpdate;
    private Boolean toShowInExpand;
    private String templateName;

    // path visible to end-user (not the full one), chopped the pei_root folder, pei folder, etc
    private String pathURL = null;

    private TreeSet<TreeNode> children;

    private Collection<Permission> permissions;

    private Boolean special;

    private boolean accessAllowed = true;

    private Folder folder;

    public TreeNode(String name, String path, int depth, int order, Collection<Permission> permissions,
                    String cssToApply) {
        this.name = name;
        this.path = path;
        this.depth = depth;
        this.order = order;
        this.permissions = permissions;
        this.cssToApply = cssToApply;
    }

    public TreeNode(String name, String path, int depth, int order, Collection<Permission> permissions,
                    String cssToApply, String templateName) {
        this.name = name;
        this.path = path;
        this.depth = depth;
        this.order = order;
        this.permissions = permissions;
        this.cssToApply = cssToApply;
        this.templateName = templateName;
    }

    public TreeNode(String name, String path, int depth, int order, Collection<Permission> permissions) {
        this.name = name;
        this.path = path;
        this.depth = depth;
        this.order = order;
        this.permissions = permissions;
    }

    public TreeNode(String name, String path, int depth, int order) {
        this.name = name;
        this.path = path;
        this.depth = depth;
        this.order = order;
    }

    public TreeNode(String name, String path, int depth) {
        this.name = name;
        this.path = path;
        this.depth = depth;
    }

    public TreeNode(String name, String path) {
        this.path = path;
        this.name = name;
    }

    public TreeNode(String name, String path, String cssToApply, String pathToUpdate, boolean toShowInExpand) {
        this.path = path;
        this.name = name;
        this.cssToApply = cssToApply;
        this.pathToUpdate = pathToUpdate;
        this.toShowInExpand = toShowInExpand;
    }

    public TreeNode(int order, String name, String path) {
        this.order = order;
        this.path = path;
        this.name = name;
    }

    public TreeNode(int order, String name, String path, String cssToApply) {
        this.order = order;
        this.path = path;
        this.name = name;
        this.cssToApply = cssToApply;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getPathURL() {
        if (pathURL == null) {
            pathURL = path.substring(StringUtils.ordinalIndexOf(path, "%2F", 4));
        }
        return pathURL;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public TreeSet<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(TreeSet<TreeNode> children) {
        this.children = children;
    }

    public void addChildren(TreeNode child) {
        if (children == null) {
            children = new TreeSet<TreeNode>(new TreeNodeComparatorByOrder());
        }
        children.add(child);
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }

    public int compareTo(TreeNode o) {
        return this.getPath().compareTo(o.getPath());
    }

    public Boolean getSpecial() {
        return special;
    }

    public void setSpecial(Boolean special) {
        this.special = special;
    }

    public boolean isAccessAllowed() {
        return accessAllowed;
    }

    public void setAccessAllowed(boolean accessAllowed) {
        this.accessAllowed = accessAllowed;
    }

    public String getCssToApply() {
        if (cssToApply == null) {
            return "";
        }
        return cssToApply;
    }

    public void setCssToApply(String cssToApply) {
        this.cssToApply = cssToApply;
    }

    public String getPathToUpdate() {
        return pathToUpdate;
    }

    public void setPathToUpdate(String pathToUpdate) {
        this.pathToUpdate = pathToUpdate;
    }

    public Boolean getToShowInExpand() {
        return toShowInExpand;
    }

    public void setToShowInExpand(Boolean toShowInExpand) {
        this.toShowInExpand = toShowInExpand;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", depth=" + depth +
                ", order=" + order +
                "}\n";
    }
}