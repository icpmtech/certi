/*
 * $Id: PermissionDAOEJB.java,v 1.3 2010/05/26 16:38:54 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/05/26 16:38:54 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.plan;

import com.criticalsoftware.certitools.entities.Permission;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;

import javax.ejb.Stateless;
import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.annotation.security.RolesAllowed;
import javax.persistence.Query;
import javax.persistence.NoResultException;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import java.util.Collection;
import java.util.List;

/**
 * Permission DAO Implementation
 *
 * @author haraujo
 */
@Stateless
@Local(PermissionDAO.class)
@LocalBinding(jndiBinding = "certitools/PermissionDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PermissionDAOEJB extends GenericDAOEJB<Permission, Long> implements PermissionDAO {
    @SuppressWarnings({"unchecked"})
    public Collection<Permission> find(long contractId) {
        Query query = manager.createQuery("SELECT p FROM Permission p WHERE p.contract.id = ?1 ORDER BY p.name");
        query.setParameter(1, contractId);

        return query.getResultList();
    }

    public Permission findByName(String permission, long contractId) {
        Query query = manager.createQuery("SELECT p FROM Permission p WHERE p.name = ?1 AND p.contract.id = ?2");
        query.setParameter(1, permission);
        query.setParameter(2, contractId);

        try {
            return (Permission) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Permission> findByIdForSelect(
            List<com.criticalsoftware.certitools.entities.jcr.Permission> permissions, long contractId) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT p FROM Permission p WHERE p.contract.id = ?1 AND ");

        for (com.criticalsoftware.certitools.entities.jcr.Permission permission : permissions) {
            sb.append(" p.id = ");
            sb.append(permission.getPermissionId());

            if (permissions.indexOf(permission) != permissions.size() - 1) {
                sb.append(" OR ");
            }
        }

        sb.append(" ORDER BY p.name ");
        Query query = manager.createQuery(sb.toString());

        query.setParameter(1, contractId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Permission> findByIdForSelectEntity(
            Collection<com.criticalsoftware.certitools.entities.Permission> permissions, long contractId) {

        int index = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT p FROM Permission p WHERE p.contract.id = ?1 AND ");

        for (com.criticalsoftware.certitools.entities.Permission permission : permissions) {
            sb.append(" p.id = ");
            sb.append(permission.getId());

            if (index != permissions.size() - 1) {
                sb.append(" OR ");
            }
            index++;
        }

        sb.append(" ORDER BY p.name ");
        Query query = manager.createQuery(sb.toString());

        query.setParameter(1, contractId);
        return query.getResultList();
    }

    public Permission findByIdWithUserContract(long permissionId) {
        Query query = manager.createQuery("SELECT DISTINCT p FROM Permission p LEFT JOIN FETCH p.userContracts "
                + "WHERE p.id = ?1");
        query.setParameter(1, permissionId);
        return (Permission) query.getSingleResult();
    }

}
