/*
 * $Id: Permission.java,v 1.7 2009/06/29 18:04:04 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/06/29 18:04:04 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * Folder permission object
 *
 * @author : lt-rico
 */
@Node(extend = HierarchyNode.class)
public class Permission extends HierarchyNode {

    @Field
    private Long permissionId;

    public Permission() {
    }

    public Permission(String path, String name, Long permissionId) {
        super(path, name);
        this.permissionId = permissionId;
    }

    public Permission(String path, String name) {
        super(path, name);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Permission that = (Permission) o;

        return permissionId.equals(that.permissionId);

    }

    public int hashCode() {
        return permissionId.hashCode();
    }
}
