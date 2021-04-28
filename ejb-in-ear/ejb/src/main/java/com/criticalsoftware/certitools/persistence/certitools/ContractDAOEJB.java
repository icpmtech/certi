/*
 * $Id: ContractDAOEJB.java,v 1.17 2010/05/26 15:33:24 jp-gomes Exp $
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
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import com.criticalsoftware.certitools.util.Logger;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;

/**
 * ContractDAO Implementation
 *
 * @author pjfsilva
 */

@Stateless
@Local(ContractDAO.class)
@LocalBinding(jndiBinding = "certitools/ContractDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ContractDAOEJB extends GenericDAOEJB<Contract, Long> implements ContractDAO {
    @PersistenceContext(unitName = "certitoolsEntityManager")
    private EntityManager manager;

    private static final Logger LOGGER = Logger.getInstance(ContractDAOEJB.class);

    public Contract findById(Long id) {
        Query query = manager.createQuery(
                "SELECT c FROM Contract c WHERE c.id = :contractId AND c.deleted = false");
        query.setParameter("contractId", id);

        try {
            return (Contract) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No contract found");
            return null;
        }
    }

    public Contract findByIdWithUserContract(Long id) {
        Query query = manager.createQuery(
                "SELECT DISTINCT c FROM Contract c LEFT JOIN FETCH c.userContract "
                        + "WHERE c.id = :contractId AND c.deleted = false");
        query.setParameter("contractId", id);

        try {
            return (Contract) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No contract found");
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Contract> findAll(long companyId) {
        Query query =
                manager.createQuery("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.deleted = false ");
        query.setParameter("companyId", companyId);

        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Contract> findAllWithUserContract(long companyId, String sortCriteria) {
        String sqlQuery = "SELECT DISTINCT c FROM Contract c LEFT JOIN FETCH c.userContract "
                + " WHERE c.company.id = :companyId AND c.deleted = false ";

        if (sortCriteria != null) {
            sqlQuery += " ORDER BY " + sortCriteria;
        }

        Query query = manager.createQuery(sqlQuery);
        query.setParameter("companyId", companyId);

        return query.getResultList();
    }

    public long countAllByCompanyId(long companyId) {
        Query query = manager.createQuery("SELECT count(c.id) FROM Contract c "
                + "WHERE c.company.id = :companyId AND c.deleted = false");
        query.setParameter("companyId", companyId);

        return (Long) query.getSingleResult();
    }

    public long countUsersInContract(long contractId) {
        Query query = manager.createQuery(
                "SELECT count(uc.userContractPK.idContract) FROM UserContract uc "
                        + " WHERE uc.userContractPK.idContract = ?1");
        query.setParameter(1, contractId);

        return (Long) query.getSingleResult();
    }

    public long countLicensesInUse(long contractId) {
        Query query = manager.createQuery("SELECT DISTINCT c FROM Contract c "
                + " LEFT JOIN FETCH c.userContract "
                + " WHERE c.deleted = false AND c.id = ?1");
        query.setParameter(1, contractId);

        return ((Contract) query.getSingleResult()).getUserContract().size();
    }

    public UserContract findUserContract(long userId, long contractId) {
        Query query = manager.createQuery("SELECT DISTINCT uc FROM UserContract uc LEFT JOIN FETCH uc.permissions "
                + "WHERE uc.user.id = ?1 AND uc.contract.id = ?2");
        query.setParameter(1, userId);
        query.setParameter(2, contractId);

        try {
            return ((UserContract) query.getSingleResult());
        } catch (NoResultException e) {
            LOGGER.debug("No user contract found");
            return null;
        }
    }

    public void deleteUserContract(UserContract userContract) {
        manager.remove(userContract);
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Contract> findAllByCompanyAndModule(long companyId, Module module) {
        Query query = manager.createQuery(
                "SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.module.moduleType = :moduleType "
                        + " AND c.deleted = false ");
        query.setParameter("companyId", companyId);
        query.setParameter("moduleType", module.getModuleType());

        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Contract> findAllByCompany(long companyId) {
        Query query = manager.createQuery(
                "SELECT new Contract(c.id, c.number, c.contractDesignation, c.module) "
                        + "FROM Contract c WHERE c.company.id = ?1 AND c.deleted = ?2 "
                        + "ORDER BY c.number ");

        query.setParameter(1, companyId);
        query.setParameter(2, false);
        return query.getResultList();
    }

    public String getContractDesignation(Long id) {
        Query query = manager.createQuery(
                "SELECT c.contractDesignation FROM Contract c WHERE c.id = :contractId AND c.deleted = false");
        query.setParameter("contractId", id);

        try {
            return (String) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No contract found");
            return null;
        }
    }

    public Contract findContractWithCompany(Long id) {
        Query query = manager.createQuery(
                "SELECT c FROM Contract c LEFT JOIN FETCH c.company WHERE c.id = :contractId AND c.deleted = false");
        query.setParameter("contractId", id);

        try {
            return (Contract) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No contract found");
            return null;
        }
    }

    public Collection<UserContract> findByIDInactivatedUsersInContract(Long id) {
        Query query = manager.createQuery(
                "SELECT DISTINCT uc FROM UserContract uc LEFT JOIN uc.user u" +
                        " WHERE uc.userContractPK.idContract = ?1 AND u.activatePassNotificationSend = ?2 AND u.active = ?3");
        query.setParameter(1, id);
        query.setParameter(2, false);
        query.setParameter(3, true);
        return query.getResultList();
    }

}