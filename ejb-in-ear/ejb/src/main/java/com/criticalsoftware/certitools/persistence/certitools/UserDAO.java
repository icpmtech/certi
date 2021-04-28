/*
 * $Id: UserDAO.java,v 1.30 2010/03/29 17:14:40 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/03/29 17:14:40 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.Role;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.Collection;
import java.util.List;

/**
 * <description>
 *
 * @author haraujo
 */
public interface UserDAO extends GenericDAO<User, Long> {

    public User findById(Long id);

    public User findByIdWithContractsRoles(long id);

    public User findByIdWithContractsPermissionsRoles(long id);

    /**
     * Returns the specified user with the roles and contracts loaded (contracts of type indicated by the module)
     *
     * @param id     id of user
     * @param module contracts type to return
     * @return the specified user with the roles and contracts loaded (contracts of type indicated by the module)
     */
    public User findByIdAndModuleWithContractsRoles(long id, Module module);

    /**
     * Returns all the non-deleted users
     *
     * @return all  non-deleted users
     */
    Collection<User> findAll();

    /**
     * Returns all users whose names start with the specified letter
     *
     * @param letter    start letter
     * @param companyId company Id
     * @return all users whose names start with the specified letter
     */
    public Collection<User> findUsersByStartLetter(String letter, long companyId);

    /**
     * Returns all users whose names start with the specified letter and belong to the specified contract
     *
     * @param letter     start letter
     * @param contractId id of the contract
     * @return all users whose names start with the specified letter and belong to the specified contract
     */
    Collection<User> findUsersByStartLetterContractId(String letter, long contractId);

    /**
     * Finds a user by login
     *
     * @param login string
     * @return User
     */
    User findByEmail(String login);

    /**
     * Finds a user not caring if he his deleted or not loads user contracts
     *
     * @param login string
     * @return user
     */
    User findByEmailAllStates(String login);

    /**
     * Finds a user by login with the associated roles
     *
     * @param login email of the user to find
     * @return User
     */
    User findByEmailWithRolesAndUserContracts(String login);

    /**
     * Find user Roles
     *
     * @param userId- userId
     * @return - user with roles loaded
     */
    User findUserWithRoles(Long userId);

    /**
     * Finds all users to send newsletter
     *
     * @return List of users
     */
    List<User> findAllForNewsletter();

    /**
     * Find user with Legal Document Subscriptions loaded
     *
     * @param userId - userId
     * @return - User
     */
    User findUserWithSubscriptions(Long userId);


    public void insertUserContract(UserContract userContract);

    public void updateUserContract(UserContract userContract);

    /**
     * Returns all the users (not deleted) from the specified company
     *
     * @param companyId id of the company
     * @return - User
     */
    public List<User> findAllByCompanyId(long companyId);

    /**
     * Returns all users from the specified contract
     *
     * @param contractId id of the contract
     * @return all users from the specified contract
     */
    public List<User> findAllByContractId(long contractId);

    /**
     * Returns the number of users in this company
     *
     * @param companyId id of the company
     * @return count of the users in the specified company
     */
    public long countAllByCompanyId(long companyId);

    /**
     * Returns all the roles the user can select according if the User is in Certitecna company or not
     *
     * @param isCertitecna true if user belongs to Certitecna
     * @return all roles the user can be associated to
     */
    Collection<Role> findAllRolesAllowed(boolean isCertitecna);


    /**
     * Searches users in the specified company and by the search "name" in the name, email, nif and phone fields
     *
     * @param companyId id of the company
     * @param name      search phrase
     * @return users that match the search phrase
     */
    public Collection<User> findUsersByCompanyIdAndNameEmailNifPhone(long companyId, String name);

    /**
     * Searches users in the specified company and by the search "name" in the name, email, nif and phone fields
     *
     * @param contractId id of the company
     * @param name       search phrase
     * @return users that match the search phrase
     */
    Collection<User> findUsersByContractIdAndNameEmailNifPhone(long contractId, String name);

    public Collection<User> findAllWithRolesByCompanyId(Long companyId);

    public Collection<User> findAllWithRolesByContractId(Long contractId);

    public void deleteUserContract(UserContract userContract);

    /**
     * Searchs users with the specified email. Searches in both deleted and undeleted status
     *
     * @param email email of the user to search
     * @return users with the specified email in both deleted and undeleted status
     */
    public Collection<User> findByEmailWithDeleted(String email);

    public void cleanUserSessions();

    /**
     * Update the number of active sessions
     *
     * @param userId        id of the user to update
     * @param sessionActive true if you want to add 1 session active, false to subtract
     */
    public void updateUserSessionActive(Long userId, boolean sessionActive);

    Role findRole(String role);

    Collection<User> findAllForExport(Long companyId, Long contractId);

    List<User> findAllForPassNotificationSend();

    User findForPassNotificationSend(Long userId);

    /**
     * Returns the users of the given contract that have any security permissions (expert, intermediate or basic).
     *
     * @param contractId The contract id.
     * @return The list of users found.
     */
    List<User> findUsersByContractAndSecurityPermissions(Long contractId);

    /**
     * Returns the users of the given contract that have expert or intermediate permissions.
     *
     * @param contractId The contract id.
     * @return The list of users found.
     */
    List<User> findExpertAndIntermediateUsersByContract(Long contractId);

    /**
     * Returns the users of the given contract that have basic permission.
     *
     * @param contractId The contract id.
     * @return The list of users found.
     */
    List<User> findBasicUsersByContract(Long contractId);
}
