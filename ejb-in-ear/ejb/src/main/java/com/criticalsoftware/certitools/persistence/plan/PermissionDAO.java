/*
 * $Id: PermissionDAO.java,v 1.2 2010/05/26 15:33:24 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/05/26 15:33:24 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.plan;

import com.criticalsoftware.certitools.entities.Permission;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.Collection;
import java.util.List;

/**
 * Permission DAO
 *
 * @author haraujo
 */
public interface PermissionDAO extends GenericDAO<Permission, Long> {
    /**
     * Finds all contract permissions
     *
     * @param contractId Contract ID
     * @return Permissions
     */
    public Collection<Permission> find(long contractId);

    /**
     * Finds a contract permission by name.
     *
     * @param permission Permission name
     * @param contractId Contract ID
     * @return - Permission
     */
    public Permission findByName(String permission, long contractId);

    /**
     * Finds contract permission by Id, for select
     *
     * @param permissions - list to select, Permission is a jcr entity
     * @param contractId  - contract Id
     * @return - permission list
     */
    Collection<Permission> findByIdForSelect(List<com.criticalsoftware.certitools.entities.jcr.Permission> permissions,
                                             long contractId);


    /**
     * Finds contract permission by Id, for select
     *
     * @param permissions - list to select, Permission is a jpa entity
     * @param contractId  - contract Id
     * @return - permission list
     */
    Collection<Permission> findByIdForSelectEntity(
            Collection<com.criticalsoftware.certitools.entities.Permission> permissions, long contractId);

    Permission findByIdWithUserContract(long permissionId);
}
