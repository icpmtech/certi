/*
 * $Id: ContractDAO.java,v 1.10 2010/05/26 15:33:24 jp-gomes Exp $
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
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.Collection;

/**
 * Contract DAO
 *
 * @author pjfsilva
 */
public interface ContractDAO extends GenericDAO<Contract, Long> {

    /**
     * Finds the contract with the specified id (not deleted)
     *
     * @param id of the contract
     * @return contract with the specified id (not deleted)
     */
    public Contract findById(Long id);

    /**
     * Finds the contract with the specified id (not deleted) and the associated user contracts
     *
     * @param id of the contract
     * @return contract with
     */
    public Contract findByIdWithUserContract(Long id);

    /**
     * Find all contracts by company id
     *
     * @param companyId id of the company
     * @return all contracts of the specified company
     */
    public Collection<Contract> findAll(long companyId);


    /**
     * Find all contracts by company id and maps the userContracts association
     *
     * @param companyId    id of the company
     * @param sortCriteria
     * @return all contracts of the specified company with the userContract
     */
    public Collection<Contract> findAllWithUserContract(long companyId, String sortCriteria);


    /**
     * Returns the count of the contracts of the specified company
     *
     * @param companyId id of the company to find
     * @return count of contracts of the specified company
     */
    public long countAllByCompanyId(long companyId);

    /**
     * Counts the number of users in the specified contract
     *
     * @param contractId id of contract
     * @return number of users in the specified contract
     */
    public long countUsersInContract(long contractId);

    public long countLicensesInUse(long contractId);

    /**
     * Finds the usercontract for the specified user and contract
     *
     * @param userId     id of the user
     * @param contractId id of the contract
     * @return userContract for the specified user and contract
     */
    public UserContract findUserContract(long userId, long contractId);

    /**
     * Deletes the specified user contract
     *
     * @param userContract user contract to delete
     */
    public void deleteUserContract(UserContract userContract);

    /**
     * Returns all contracts of the specified company and of the type specified by the module
     *
     * @param companyId id of the company
     * @param module    type of contract
     * @return all contracts of the specified company and of the type specified by the module
     */
    Collection<Contract> findAllByCompanyAndModule(long companyId, Module module);

    Collection<Contract> findAllByCompany(long companyId);

    /**
     * Returns the designation of the contract with the given id.
     *
     * @param id The contract id.
     * @return The designation of the contract found.
     */
    String getContractDesignation(Long id);

    /**
     * Returns the contract with the given id including the information of it's company.
     *
     * @param id The contract id.
     * @return The contract found.
     */
    Contract findContractWithCompany(Long id);

    /**
     * Returns all users of the specified contract that have not been sent the activation email
     *
     * @param contractId id of the contract
     *
     * @return all UserContracts of users of the specified contract that have not been sent the activation email
     */
    Collection<UserContract> findByIDInactivatedUsersInContract(Long id);
}