/* * $Id: UserService.java,v 1.33 2013/12/18 03:11:49 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2013/12/18 03:11:49 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.business.exception.*;
import com.criticalsoftware.certitools.entities.Role;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.ModuleType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author : jp-gomes
 * @version : $version $
 */
public interface UserService {

    public Collection<User> findUsersByStartLetterAllowed(String letter, long companyId, User userInSession)
            throws CertitoolsAuthorizationException;

    public Collection<User> findUsersByStartLetterAndContractIdAllowed(String letter, long contractId,
                                                                       User userInSession)
            throws CertitoolsAuthorizationException;

    public User findByEmailWithRoles(String email) throws ObjectNotFoundException;

    /**
     * Returns the user with the specified email
     *
     * @param email email of the user
     * @return user with the specified email
     *
     * @throws ObjectNotFoundException user not found
     */
    public User findByEmail(String email) throws ObjectNotFoundException;

    /**
     * Returns all the roles the user can select according if the User is in Certitecna company or not
     *
     * @param isCertitecna  true if user belongs to Certitecna
     * @param userInSession user in session
     * @return all roles the user can be associated to
     */
    public Collection<Role> findAllRolesAllowed(boolean isCertitecna, User userInSession);

    /**
     * Find User with Legal Document Category Subscriptions loaded
     *
     * @param userId - userId
     * @return User
     */
    User findUserWithSubscriptions(Long userId);

    /**
     * Inserts a new User coming from the user registration (different logic)
     *
     * @param user       user to insert
     * @param contractId contractId from the user
     * @throws ObjectNotFoundException user, contract not found
     */
    public void insertByUserRegistration(User user, long contractId) throws ObjectNotFoundException;

    public void insertByLocalInstallation(User user, long companyId) throws ObjectNotFoundException;

    /**
     * Inserts a new User
     *
     * @param user          object to insert
     * @param userInSession user in session
     * @throws CertitoolsAuthorizationException
     *                                 permission exception
     * @throws ObjectNotFoundException user not found
     */
    public void insert(User user, User userInSession) throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Finds a user by id checking if the userInSession can get this user
     *
     * @param id            id of the user to find
     * @param userInSession userInSession that is trying to execute this
     * @return user found
     *
     * @throws ObjectNotFoundException when user not found
     * @throws CertitoolsAuthorizationException
     *                                 when there is no access to this user
     */
    public User findByIdAllowed(Long id, User userInSession)
            throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Find a user by his id and associated contracts
     *
     * @param id            user id
     * @param userInSession user in session
     * @return the user and associated contracts
     *
     * @throws ObjectNotFoundException when user not found
     * @throws CertitoolsAuthorizationException
     *                                 no permission
     */
    User findByIdWithContractsRolesAllowed(long id, User userInSession)
            throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Update user profile
     *
     * @param userId
     * @param oldPassword old password
     * @param newPassword new passwor
     * @throws ObjectNotFoundException when user not found
     */
    void updateProfile(Long userId, String oldPassword, String newPassword)
            throws BusinessException, ObjectNotFoundException, InvalidPasswordException;

    /**
     * Deletes a user (marks the deleted field as true)
     *
     * @param user          the user to delete
     * @param userInSession
     */
    public void delete(User user, User userInSession)
            throws ObjectNotFoundException, AdminDeleteException, CertitoolsAuthorizationException;

    public Collection<User> findUsersByCompanyIdAndNameAllowed(long companyId, String name, User userInSession)
            throws CertitoolsAuthorizationException;

    public Collection<User> findUsersByContractIdAndNameAllowed(long contractId, String name, User userInSession)
            throws CertitoolsAuthorizationException;

    public Collection<User> findAllWithRolesByCompanyIdAllowed(Long companyId, User userInSession)
            throws CertitoolsAuthorizationException;

    public Collection<User> findAllWithRolesByContractIdAllowed(Long contractId, User userInSession)
            throws CertitoolsAuthorizationException;

    public void update(User user, User userInSession) throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Resets the user password, re-sending the activationemail, allowed for all users to call it
     *
     * @param user user to reset the password
     * @throws ObjectNotFoundException user not found
     */
    public void resetUserPassword(User user) throws ObjectNotFoundException;

    /**
     * Resets the user password, re-sending the activationemail
     *
     * @param user          user to reset the password
     * @param userInSession
     */
    public void resetUserPasswordAllowed(User user, User userInSession)
            throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Returns the user with the specified email even if it is deleted
     *
     * @param email email of the user
     * @return user with the specified email even if it is deleted
     *
     * @throws ObjectNotFoundException user not found
     * @throws CertitoolsAuthorizationException
     *                                 not authorized
     */
    public Collection<User> findByEmailWithDeletedAllowed(String email)
            throws ObjectNotFoundException, CertitoolsAuthorizationException;

    public void cleanUserSessions();

    public void updateUserSessionActive(Long userId, boolean sessionActive);

    public boolean validateLoginUserByEmail(String email) throws ObjectNotFoundException;

    public User findById(Long id) throws ObjectNotFoundException;

    void activateUser(User user, String password1, String password2, String uid)
            throws ObjectNotFoundException, InvalidPasswordException, BusinessException;

    void updateUserLoginStatistics(User user) throws ObjectNotFoundException;

    /**
     * Import users from a inputStream
     *
     * @param companyId the company id
     * @param stream    from the file
     * @return number of records imported
     *
     * @throws ImportException         some exception
     * @throws ObjectNotFoundException some exception
     */
    int importUsers(Long companyId, InputStream stream) throws ImportException, ObjectNotFoundException;

    /**
     * Export users from database
     *
     * @param companyId  the company id
     * @param contractId the contract id
     * @return a outputstream
     *
     * @throws IOException e
     */
    ByteArrayOutputStream exportUsers(Long companyId, Long contractId) throws IOException;

    void updateUserSeenPEI(long userId, boolean seenPEI) throws ObjectNotFoundException;

    /**
     * Update user last Plan or Legislation Search
     *
     * @param userId     - user Id
     * @param moduleType - Module
     * @param contractId - contractId
     * @throws BusinessException when user does not exists
     */
    void updateUserLastPlanOrLegislationView(long userId, ModuleType moduleType, Long contractId)
            throws BusinessException;

    void checkAndDeleteOldUsers();

    void updateUserLanguage(long userId, String language) throws ObjectNotFoundException;

    void checkAllUsersAndSendNotificationEmail();

    void checkAndSendNotificationEmail(long userId, boolean isToFlushAndClear);
}
