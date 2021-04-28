/*
 * $Id: CompanyDAOEJB.java,v 1.16 2009/10/14 14:34:42 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/10/14 14:34:42 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
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
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Company DAO
 *
 * @author jp-gomes
 */

@Stateless
@Local(CompanyDAO.class)
@LocalBinding(jndiBinding = "certitools/CompanyDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CompanyDAOEJB extends GenericDAOEJB<Company, Long> implements CompanyDAO {
    @PersistenceContext(unitName = "certitoolsEntityManager")
    private EntityManager manager;

    private static final Logger LOGGER = Logger.getInstance(CompanyDAOEJB.class);

    @SuppressWarnings({"unchecked"})
    public Collection<Company> findAll() {
        return manager.createQuery("SELECT c FROM Company c "
                + "WHERE c.deleted = false ORDER BY c.name ASC").getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Company> findAllWithPlan(List<ModuleType> modulesType) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT c.company FROM Contract c WHERE c.deleted = false AND (");
        for (ModuleType moduleType : modulesType) {
            if (modulesType.indexOf(moduleType) != 0) {
                sb.append(" OR ");
            }
            sb.append("c.module.moduleType = ?").append(modulesType.indexOf(moduleType) + 1);
        }
        sb.append(")");
        Query query = manager.createQuery(sb.toString());
        for (ModuleType moduleType : modulesType) {
            query.setParameter(modulesType.indexOf(moduleType) + 1, moduleType);
        }
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Company> findAllWithPlan(ModuleType moduleType) {
        Query query = manager.createQuery(
                "SELECT DISTINCT c.company FROM Contract c WHERE c.deleted = false AND c.module.moduleType = ?1 "
                        + " ORDER BY c.company.name ");
        query.setParameter(1, moduleType);
        return query.getResultList();
    }

    public Company findById(Long id) {
        Query query = manager.createQuery(
                "SELECT c FROM Company c WHERE c.id = :companyId AND c.deleted = false");
        query.setParameter("companyId", id);

        try {
            return (Company) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No company found");
            return null;
        }
    }

    @SuppressWarnings({"JpaQueryApiInspection"})
    public Company findByIdWithContracts(long companyId, String sort) {
        Query query = manager.createQuery(
                "SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.contracts AS contracts "
                        + "WHERE c.id = :companyId "
                        + "AND c.deleted = false "
                        + "ORDER BY contracts.active ASC");
        query.setParameter("companyId", companyId);

        Company company;

        try {
            company = (Company) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No company found");
            return null;
        }


        ArrayList<Contract> contracts = new ArrayList<Contract>();
        for (Contract contract : company.getContracts()) {
            if (!contract.isDeleted()) {
                contract.getUserContract().size();
                contracts.add(contract);
            }
        }

        Collections.sort(contracts);

        company.setContracts(contracts);
        return company;
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Company> findAllByStartLetter(String letter) {
        return findAllByStartLetterAndCompanyId(letter, null);
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Company> findAllByStartLetterAndCompanyId(String letter, Long companyId) {
        Query query;
        String limitCompany = "";
        if (companyId != null) {
            limitCompany = " AND c.id = :companyId ";
        }

        if (letter.equals("#")) {
            query = manager.createNativeQuery(
                    "SELECT * FROM Company c WHERE UPPER(to_ascii(convert_to(c.name, 'latin1'), 'latin1')) ~ '^[^a-zA-Z]' "
                            + "AND c.deleted = false " + limitCompany,
                    Company.class);
        } else {
            query = manager.createQuery(
                    "SELECT new com.criticalsoftware.certitools.entities.Company(c.id , c.name, c.country) "
                            + "FROM Company c LEFT JOIN c.country "
                            + "WHERE UPPER(to_ascii(convert_to(c.name, 'latin1'), 'latin1')) LIKE :letter "
                            + "AND c.deleted = false "
                            + limitCompany
                            + "ORDER BY c.name ASC");
            query.setParameter("letter", letter + "%");
        }

        if (companyId != null) {
            query.setParameter("companyId", companyId);
        }

        return query.getResultList();

    }

    @SuppressWarnings({"unchecked"})
    public Collection<Company> findByName(String name) {
        return findByNameAndCompanyId(name, null);
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Company> findByNameAndCompanyId(String name, Long companyId) {
        name = name.trim();
        name = name.replaceAll(" ", "%");
        name = "%" + name + "%";

        String limitCompany = "";
        if (companyId != null) {
            limitCompany = " AND c.id = :companyId ";
        }

        Query query = manager.createQuery("SELECT c FROM Company c "
                + " WHERE c.deleted = false "
                + limitCompany
                + " AND UPPER(to_ascii(convert_to(c.name, 'latin1'), 'latin1')) LIKE "
                + " UPPER(to_ascii(convert_to(:name, 'latin1'), 'latin1')) "
                + " ORDER BY c.name ASC");

        query.setParameter("name", name);

        if (companyId != null) {
            query.setParameter("companyId", companyId);
        }

        return query.getResultList();
    }
}
