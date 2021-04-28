/*
 * $Id: FAQDAOEJB.java,v 1.5 2009/04/14 08:56:50 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/14 08:56:50 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.FAQ;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.Logger;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * FAQ DAO Implementation
 *
 * @author jp-gomes
 */

@Stateless
@Local(FAQDAO.class)
@LocalBinding(jndiBinding = "certitools/FAQDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class FAQDAOEJB extends GenericDAOEJB<FAQ, Long> implements FAQDAO {

    private static final Logger LOGGER = Logger.getInstance(FAQDAOEJB.class);

    @SuppressWarnings({"unchecked"})
    public List<FAQ> findAllFAQs(List<ModuleType> moduleTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append("select f from FAQ f INNER JOIN FETCH f.faqCategory INNER JOIN FETCH f.faqCategory.module ")
                .append("WHERE ");

        for (ModuleType moduleType : moduleTypes) {
            sb.append(" f.faqCategory.module.moduleType=?").append(moduleTypes.indexOf(moduleType) + 1);

            if (moduleTypes.indexOf(moduleType) < moduleTypes.size() - 1) {
                sb.append(" OR ");
            }
        }

        sb.append(" ORDER BY f.").append("faqCategory.module.moduleType, f.faqCategory.name").append(" ")
                .append("ASC");

        Query query = manager.createQuery(sb.toString());

        for (ModuleType moduleType : moduleTypes) {
            query.setParameter(moduleTypes.indexOf(moduleType) + 1, moduleType);
        }

        return (List<FAQ>) query.getResultList();
    }

    public FAQ findWithCategoryAndModule(Long id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT f FROM FAQ f INNER JOIN FETCH f.faqCategory faqCategory ")
                .append("INNER JOIN FETCH faqCategory.module where f.id = ?1");

        Query query = manager.createQuery(sb.toString());
        query.setParameter(1, id);

        try {
            return (FAQ) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No faq found");
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<FAQ> findAllFAQs(int currentPage, int resultPerPage, String sortCriteria,
                                 String sortDirection, List<ModuleType> moduleTypes) {

        StringBuilder sb = new StringBuilder();
        sb.append("select f from FAQ f INNER JOIN FETCH f.faqCategory INNER JOIN FETCH f.faqCategory.module ")
                .append("WHERE ");

        for (ModuleType moduleType : moduleTypes) {
            sb.append(" f.faqCategory.module.moduleType=?").append(moduleTypes.indexOf(moduleType) + 1);

            if (moduleTypes.indexOf(moduleType) < moduleTypes.size() - 1) {
                sb.append(" OR ");
            }
        }

        sb.append(" ORDER BY f.").append(sortCriteria).append(" ").append(sortDirection);

        Query query = manager.createQuery(sb.toString());

        for (ModuleType moduleType : moduleTypes) {
            query.setParameter(moduleTypes.indexOf(moduleType) + 1, moduleType);
        }

        query.setFirstResult(currentPage);
        query.setMaxResults(resultPerPage);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public int countAll(List<ModuleType> moduleTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append("select f from FAQ f INNER JOIN FETCH f.faqCategory INNER JOIN FETCH f.faqCategory.module ")
                .append("WHERE ");

        for (ModuleType moduleType : moduleTypes) {
            sb.append(" f.faqCategory.module.moduleType=?").append(moduleTypes.indexOf(moduleType) + 1);

            if (moduleTypes.indexOf(moduleType) < moduleTypes.size() - 1) {
                sb.append(" OR ");
            }
        }

        Query query = manager.createQuery(sb.toString());

        for (ModuleType moduleType : moduleTypes) {
            query.setParameter(moduleTypes.indexOf(moduleType) + 1, moduleType);
        }

        List<FAQ> countFAQ = (List<FAQ>) query.getResultList();

        if (countFAQ == null) {
            return 0;

        } else {
            return countFAQ.size();
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<FAQ> find(Long faqCategoryId) {
        Query query =
                manager.createQuery("select f from FAQ f where f.faqCategory.id = ?1 ORDER BY f.changedDate DESC");
        query.setParameter(1, faqCategoryId);
        return (List<FAQ>) query.getResultList();
    }
}
