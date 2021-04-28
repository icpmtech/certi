/*
 * $Id: CompanyDAO.java,v 1.10 2009/10/14 14:34:42 jp-gomes Exp $
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
import com.criticalsoftware.certitools.persistence.GenericDAO;
import com.criticalsoftware.certitools.util.ModuleType;

import java.util.Collection;
import java.util.List;

/**
 * Company DAO
 *
 * @author jp-gomes
 */
public interface CompanyDAO extends GenericDAO<Company, Long> {

    Collection<Company> findAll();

    Collection<Company> findAllWithPlan(List<ModuleType> modulesType);

    /**
     * Retursn all companies with a valid contract of type PEI
     *
     * @param moduleType
     * @return all companies with a valid contract of type PEI
     */
    Collection<Company> findAllWithPlan(ModuleType moduleType);

    /**
     * Returns the company with the specified id (not deleted)
     *
     * @param id id of the company to search
     * @return the company with the specified id (not deleted)
     */
    public Company findById(Long id);

    /**
     * Returns the company with the specified id and fetches the associated contracts
     *
     * @param companyId id of the company
     * @param sort      the sorting we want to have e.g. id ASC, contract.number DESC
     * @return company with the specified id
     */
    public Company findByIdWithContracts(long companyId, String sort);

    /**
     * Returns a collection of Companys with their name starting by the specified letter The search is accent
     * insensitive (there's a hack added for this, works with postgres 8.3.x and may not work in future postgres
     * releases)
     *
     * @param letter to search or # in case of a search for numbers and non-alphabetical chars
     * @return companies that match the specified start letter
     */
    public Collection<Company> findAllByStartLetter(String letter);

    /**
     * Returns a collection of Companys with their name starting by the specified letter The search is accent
     * insensitive (there's a hack added for this, works with postgres 8.3.x and may not work in future postgres
     * releases)
     * <p/>
     * Limited by companyId
     *
     * @param letter    letter to search or # in case of a search for numbers and non-alphabetical chars
     * @param companyId id of the company to limit
     * @return companies that match the specified start letter
     */
    public Collection<Company> findAllByStartLetterAndCompanyId(String letter, Long companyId);

    /**
     * Searches the companies with the specified name
     *
     * @param name name search phrase
     * @return companies with the specified name
     */
    public Collection<Company> findByName(String name);

    /**
     * Searches in the company with the specified id  the specified name
     *
     * @param name      name search phrase
     * @param companyId id of company to search
     * @return companies with the specified name
     */
    public Collection<Company> findByNameAndCompanyId(String name, Long companyId);

}
