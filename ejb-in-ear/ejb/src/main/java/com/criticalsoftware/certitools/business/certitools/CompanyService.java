/*
 * $Id: CompanyService.java,v 1.17 2010/05/26 16:55:29 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/05/26 16:55:29 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.IsReferencedException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.ModuleType;

import java.util.Collection;
import java.util.List;

/**
 * Company Service
 *
 * @author jp-gomes
 */
@SuppressWarnings({"JavaDoc"})
public interface CompanyService {

    /**
     * Find Company By Id
     *
     * @param id            - company id
     * @param userInSession
     * @return - company
     *
     * @throws com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException
     *
     * @throws com.criticalsoftware.certitools.business.exception.ObjectNotFoundException
     *
     */
    Company findAllowed(Long id, User userInSession) throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Returns the company with the specified id and fetches the associated contracts
     *
     * @param companyId     id of the company
     * @param sort          the sorting we want to have e.g. id ASC, contract.number DESC
     * @param userInSession session user
     * @return company with the specified id
     *
     * @throws com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException
     *
     * @throws com.criticalsoftware.certitools.business.exception.ObjectNotFoundException
     *
     */
    Company findByIdWithContractsAllowed(long companyId, String sort, User userInSession)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Find All Companies in the Database
     *
     * @return - All Companies
     */
    Collection<Company> findAll();

    /**
     * Searches for all the companies that start with the specified letter and that the user has permissions to access
     *
     * @param letter        start letter of the companies to search
     * @param userInSession
     * @return List of the companies that start with the specified letter and that the user has permissions to access
     */
    Collection<Company> findAllAuthorizedByStartLetter(String letter, User userInSession);

    /**
     * Inserts a new company
     *
     * @param company object to insert
     * @return the object inserted
     *
     * @throws ObjectNotFoundException if country not found
     */
    public Company insertCompany(Company company) throws ObjectNotFoundException;


    public void deleteCompany(Company company) throws IsReferencedException, ObjectNotFoundException;

    public void updateCompany(Company company) throws ObjectNotFoundException, JackrabbitException;

    /**
     * Returns all the companies this user is allowed to access (needed for export)
     *
     * @param userInSession Current user
     * @return all the companies this user is allowed to access (needed for export)
     */
    public Collection<Company> findAllAuthorized(User userInSession);

     /**
     * Returns all the companies this user is allowed to access with contracts loaded
     *
     * @return all the companies this user is allowed to access  with contracts loaded
     */
    public Collection<Company> findAllWithContractsLoaded();

    public Collection<Company> findAllWithPlan(List<ModuleType> modulesType);

    /**
     * Retuens all the companies this user is allowed to access with a valid PEI contract
     *
     * @param userInSession Current user
     * @param moduleType
     * @param frontoffice   - called from the plan view frontoffice?
     * @return all the companies this user is allowed to access with a valid PEI contract
     */
    public Collection<Company> findAllWithPlan(User userInSession, ModuleType moduleType, boolean frontoffice);

    /**
     * Searches the companies with the specified name
     *
     * @param name          name search phrase
     * @param userInSession
     * @return companies with the specified name
     */
    Collection<Company> findByName(String name, User userInSession);
}
