/*
 * $Id: ModuleDAOEJB.java,v 1.6 2009/03/31 05:42:38 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/31 05:42:38 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.sm.SubModule;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.ModuleType;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

/**
 * Module DAO Implementation
 *
 * @author jp-gomes
 */

@Stateless
@Local(ModuleDAO.class)
@LocalBinding(jndiBinding = "certitools/ModuleDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ModuleDAOEJB extends GenericDAOEJB<Module, Long> implements ModuleDAO {

    private static final Logger LOGGER = Logger.getInstance(ModuleDAOEJB.class);

    public Module findModuleByModuleType(ModuleType moduleType) {
        Query query = manager.createQuery("SELECT m FROM Module m WHERE m.moduleType = ?1");
        query.setParameter(1, moduleType);

        try {
            return (Module) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No module found");
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public Module findWithFAQCategory(ModuleType moduleType) {

        StringBuilder sb = new StringBuilder();

        sb.append("select distinct m from Module m LEFT JOIN FETCH m.faqCategories facCat ")
                .append("where m.moduleType =?1 ORDER BY facCat.name ASC");
        Query query = manager.createQuery(sb.toString());

        query.setParameter(1, moduleType);

        try {
            return (Module) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No entity found");
            return null;
        }
    }

    public SubModule insertSubModule(SubModule subModule) {
        manager.persist(subModule);
        return subModule;
    }

    public void deleteContractSubModules(Long contractId) {
        Query query = manager.createQuery("delete from SubModule sm where sm.contract.id = :contractId");
        query.setParameter("contractId", contractId);
        query.executeUpdate();
    }

    public boolean existsSubModuleByContractAndType(Long contractId, SubModuleType subModuleType) {
        Query query = manager.createQuery("select count(sm) from SubModule sm " +
                "where sm.contract.id = :contractId and sm.subModuleType = :subModuleType");
        query.setParameter("contractId", contractId);
        query.setParameter("subModuleType", subModuleType);
        return (Long) query.getSingleResult() != 0;
    }

    @SuppressWarnings("unchecked")
    public List<SubModuleType> findContractSubModules(Long contractId) {
        Query query = manager.createQuery("select sm.subModuleType from SubModule sm " +
                "where sm.contract.id = :contractId");
        query.setParameter("contractId", contractId);
        return query.getResultList();
    }
}
