/*
 * $Id: CompanyServiceEJB.java,v 1.26 2010/06/21 10:45:56 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/06/21 10:45:56 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.IsReferencedException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.persistence.certitools.CompanyDAO;
import com.criticalsoftware.certitools.persistence.certitools.ContractDAO;
import com.criticalsoftware.certitools.persistence.certitools.CountryDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.plan.PlanDAO;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.ModuleType;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Company Service Implementation
 *
 * @author jp-gomes
 */

@Stateless
@Local(CompanyService.class)
@LocalBinding(jndiBinding = "certitools/CompanyService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class CompanyServiceEJB implements CompanyService {

    @EJB
    private CompanyDAO companyDAO;

    @EJB
    private ContractDAO contractDAO;

    @EJB
    private UserDAO userDAO;

    @EJB
    private CountryDAO countryDAO;

    @EJB
    private PlanDAO planDAO;

    @Resource
    private SessionContext sessionContext;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = "administrator")
    public Collection<Company> findAll() {
        return companyDAO.findAll();
    }

    // contract manager can get all companies
    // client contract manager and administrator can only see their company

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Company findAllowed(Long id, User userInSession)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (sessionContext.isCallerInRole("contractmanager")) {
            // ok
        } else if (sessionContext.isCallerInRole("administrator") || sessionContext
                .isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId().longValue() != id.longValue()) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see other companies");
            }
        }
        Company company = companyDAO.findById(id);

        if (company == null) {
            throw new ObjectNotFoundException("Can't find the specified company: " + id,
                    ObjectNotFoundException.Type.COMPANY);
        }

        return company;
    }

    // client contract manager can only see their company
    // administrator can only see certitecna
    // contract manager can see it all
    // TESTED ALL BRANCHES OK

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<Company> findAllAuthorizedByStartLetter(String letter, User userInSession) {

        // if letter empty, return empty collection
        if (letter == null || letter.equals("")) {
            return new ArrayList<Company>();
        }

        if (sessionContext.isCallerInRole("contractmanager")) {
            if (letter.equals("ALL")) {
                return companyDAO.findAll();
            } else {
                return companyDAO.findAllByStartLetter(letter);
            }
        }

        Long companyId;
        Collection<Company> companies = new ArrayList<Company>();

        if (sessionContext.isCallerInRole("administrator") || sessionContext.isCallerInRole("clientcontractmanager")) {
            if (letter.equals("ALL")) {
                companies.add(userInSession.getCompany());
                return companies;
            } else {
                companyId = userInSession.getCompany().getId();
                return companyDAO.findAllByStartLetterAndCompanyId(letter, companyId);
            }
        }

        return companies;
    }

    // TESTED ALL BRANCHES OK

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Company findByIdWithContractsAllowed(long companyId, String sort, User userInSession)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {
        // client contract manager can only see their company
        // administrator can only see certitecna
        // contract manager can see it all

        if (sessionContext.isCallerInRole("contractmanager")) {
            // ok - user can see it all

        } else if (sessionContext.isCallerInRole("administrator") &&
                companyId != Long.valueOf(Configuration.getInstance().getCertitecnaId())) {
            throw new CertitoolsAuthorizationException("Administrator role can't see other companies");

        } else if (sessionContext.isCallerInRole("clientcontractmanager") &&
                companyId != userInSession.getCompany().getId()) {
            throw new CertitoolsAuthorizationException("clientcontractmanager role can't see other companies");
        }

        Company company = companyDAO.findByIdWithContracts(companyId, sort);

        if (company == null) {
            throw new ObjectNotFoundException("Can't find the specified company: " + companyId,
                    ObjectNotFoundException.Type.COMPANY);
        }

        return company;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = "contractmanager")
    public Company insertCompany(Company company) throws ObjectNotFoundException {

        if (company.getCountry() != null && countryDAO.findById(company.getCountry().getIso()) == null) {
            throw new ObjectNotFoundException("Can't find the country specified.",
                    ObjectNotFoundException.Type.COUNTRY);
        } else {
            return companyDAO.insert(company);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = "contractmanager")
    public void deleteCompany(Company company) throws IsReferencedException, ObjectNotFoundException {
        // company exists?
        company = companyDAO.findById(company.getId());

        if (company == null) {
            throw new ObjectNotFoundException("Can't find the company with the specified id.",
                    ObjectNotFoundException.Type.COMPANY);
        }

        // check if there are relationships to users or contracts
        if (contractDAO.countAllByCompanyId(company.getId()) > 0) {
            throw new IsReferencedException("The company has contracts so can't be deleted",
                    IsReferencedException.Type.CONTRACT);
        }

        if (userDAO.countAllByCompanyId(company.getId()) > 0) {
            throw new IsReferencedException("The company has users so can't be deleted",
                    IsReferencedException.Type.USER);
        }

        company.setDeleted(true);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = "contractmanager")
    public void updateCompany(Company company) throws ObjectNotFoundException, JackrabbitException {
        if (company.getCountry() != null && countryDAO.findById(company.getCountry().getIso()) == null) {
            throw new ObjectNotFoundException("Can't find the country specified.",
                    ObjectNotFoundException.Type.COUNTRY);
        } else {

        }
        Company oldCompany = companyDAO.findByIdWithContracts(company.getId(), null);
        if (!oldCompany.getName().equals(company.getName())) {
            //Must update all company contracts folder references to new name
            if (oldCompany.getContracts() != null && !oldCompany.getContracts().isEmpty()) {
                planDAO.updatePlansFolderReferencePath(oldCompany.getContracts(), null, company.getName());
            }
        }
        companyDAO.merge(company);
    }

    // client contract manager can only see their company
    // administrator can only see certitecna
    // contract manager can see it all

    @RolesAllowed(value =
            {"administrator", "contractmanager", "clientcontractmanager", "peimanager", "clientpeimanager"})
    public Collection<Company> findAllAuthorized(User userInSession) {

        if (sessionContext.isCallerInRole("contractmanager") || sessionContext.isCallerInRole("peimanager")) {
            return companyDAO.findAll();
        }

        Collection<Company> companies = new ArrayList<Company>();
        if (sessionContext.isCallerInRole("administrator") || sessionContext.isCallerInRole("clientcontractmanager") ||
                sessionContext.isCallerInRole("clientpeimanager")) {
            companies.add(userInSession.getCompany());
        }

        return companies;
    }

    // client contract manager can only see their company
    // administrator can only see certitecna
    // contract manager can see it all

    @RolesAllowed(value = {"peimanager"})
    public Collection<Company> findAllWithContractsLoaded() {
        Collection<Company> companies = companyDAO.findAll();

        for (Company company : companies) {
            company.setContracts(contractDAO.findAllByCompany(company.getId()));
        }

        return companies;
    }

    @RolesAllowed(value = "peimanager")
    public Collection<Company> findAllWithPlan(List<ModuleType> modulesType) {
        return companyDAO.findAllWithPlan(modulesType);
    }

    @RolesAllowed(value = {"peimanager", "clientpeimanager", "administrator", "contractmanager", "user"})
    public Collection<Company> findAllWithPlan(User userInSession, ModuleType moduleType, boolean frontoffice) {

        /* administrator and contractmanager can view all plans, not manage them. In the case this was called in plan
        * view check if user is contractmanager or administrator. */

        if (sessionContext.isCallerInRole("peimanager") || (frontoffice && (
                sessionContext.isCallerInRole("administrator") || sessionContext.isCallerInRole("contractmanager")))) {
            return companyDAO.findAllWithPlan(moduleType);
        }

        // else, if it's an user or clientpeimanager, add its company if the company has some pei contracts
        Collection<Company> companies = new ArrayList<Company>();

        if (userInSession.getCompany() == null) {
            return companies;
        }

        // check if company has plan contracts
        if (contractDAO.findAllByCompanyAndModule(userInSession.getCompany().getId(), new Module(moduleType)).size()
                > 0) {
            companies.add(userInSession.getCompany());
        }

        return companies;
    }

    // client contract manager can only see their company
    // administrator can only see certitecna
    // contract manager can see it all

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<Company> findByName(String name, User userInSession) {
        if (sessionContext.isCallerInRole("contractmanager")) {
            return companyDAO.findByName(name);
        }

        Long companyId = null;
        if (sessionContext.isCallerInRole("administrator") || sessionContext.isCallerInRole("clientcontractmanager")) {
            companyId = userInSession.getCompany().getId();
        }
        return companyDAO.findByNameAndCompanyId(name, companyId);
    }
}
