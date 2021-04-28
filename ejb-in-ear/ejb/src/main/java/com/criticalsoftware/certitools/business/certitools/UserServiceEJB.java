/*
 * $Id: UserServiceEJB.java,v 1.76 2013/12/18 03:11:49 pjfsilva Exp $
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
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.*;
import com.criticalsoftware.certitools.persistence.certitools.CompanyDAO;
import com.criticalsoftware.certitools.persistence.certitools.ContractDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.legislation.LegalDocumentHistoryDAO;
import com.criticalsoftware.certitools.persistence.plan.PermissionDAO;
import com.criticalsoftware.certitools.util.*;
import com.criticalsoftware.certitools.util.Configuration;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.io.*;
import java.util.*;

import static com.criticalsoftware.certitools.business.exception.ObjectNotFoundException.Type;

/**
 * UserService
 *
 * @author : jp-gomes
 */
@Stateless
@Local(UserService.class)
@LocalBinding(jndiBinding = "certitools/UserService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class UserServiceEJB implements UserService {

    private static final Logger LOGGER = Logger.getInstance(UserServiceEJB.class);

    @EJB
    private UserDAO userDAO;

    @EJB
    private ContractDAO contractDAO;

    @EJB
    private PermissionDAO permissionDAO;

    @EJB
    private CompanyDAO companyDAO;

    @EJB
    private LegalDocumentHistoryDAO legalDocumentHistoryDAO;

    @Resource
    private SessionContext sessionContext;

    @RolesAllowed(value = "user")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public User findUserWithSubscriptions(Long userId) {
        return userDAO.findUserWithSubscriptions(userId);
    }

    // contract manager can get all users
    // client contract manager and administrator can only see their company users

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<User> findUsersByStartLetterAllowed(String letter, long companyId, User userInSession)
            throws CertitoolsAuthorizationException {
        if (sessionContext.isCallerInRole("contractmanager")) {
            // ok
        } else if (sessionContext.isCallerInRole("administrator") ||
                sessionContext.isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId() != companyId) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see users outside their company");
            }
        }

        // if letter empty, return empty collection
        if (letter == null || letter.equals("")) {
            return new ArrayList<User>();
        }

        if (letter.equals("ALL")) {
            return userDAO.findAllByCompanyId(companyId);
        } else {
            return userDAO.findUsersByStartLetter(letter, companyId);
        }

    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<User> findUsersByStartLetterAndContractIdAllowed(String letter, long contractId,
                                                                       User userInSession)
            throws CertitoolsAuthorizationException {

        Contract contract = contractDAO.findById(contractId);
        long companyId = contract.getCompany().getId();

        if (sessionContext.isCallerInRole("contractmanager")) {
            // ok
        } else if (sessionContext.isCallerInRole("administrator") ||
                sessionContext.isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId() != companyId) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see users outside their company");
            }
        }

        // if letter empty, return empty collection
        if (letter == null || letter.equals("")) {
            return new ArrayList<User>();
        }

        if (letter.equals("ALL")) {
            return userDAO.findAllByContractId(contractId);
        } else {
            return userDAO.findUsersByStartLetterContractId(letter, contractId);
        }
    }


    // needs to be open as this is called in the preActionFilter

    @PermitAll
    public User findByEmailWithRoles(String email) throws ObjectNotFoundException {
        // can user access modules ?
        User user = userDAO.findByEmailWithRolesAndUserContracts(email);

        if (user == null) {
            throw new ObjectNotFoundException("Can't find the user with the specified id", Type.USER);
        }

        //TODO-MODULE
        user.setAccessLegislation(user.validateModuleAccess(ModuleType.LEGISLATION));
        user.setAccessPEI(user.validateModuleAccess(ModuleType.PEI));
        user.setAccessPRV(user.validateModuleAccess(ModuleType.PRV));
        user.setAccessPSI(user.validateModuleAccess(ModuleType.PSI));
        user.setAccessGSC(user.validateModuleAccess(ModuleType.GSC));

        // always add the access to certitecna users
        if (sessionContext.isCallerInRole("administrator") || sessionContext.isCallerInRole("contractmanager")) {
            user.setAccessLegislation(true);
            user.setAccessPEI(true);
            user.setAccessPRV(true);
            user.setAccessPSI(true);
            user.setAccessGSC(true);
        } else if (sessionContext.isCallerInRole("legislationmanager")) {
            user.setAccessLegislation(true);
        } else if (sessionContext.isCallerInRole("clientpeimanager") || sessionContext.isCallerInRole("peimanager")) {
            user.setAccessPEI(true);
            user.setAccessPRV(true);
            user.setAccessPSI(true);
            user.setAccessGSC(true);
        }

        return user;
    }

    // needs to be open as this is needed in reset password by the user

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public User findByEmail(String email) throws ObjectNotFoundException {
        User user = userDAO.findByEmail(email);

        if (user == null) {
            throw new ObjectNotFoundException("Can't find the user with the specified id", Type.USER);
        }

        return user;
    }

    // this does not need to be protected, all roles can access this (it's not important)

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<Role> findAllRolesAllowed(boolean isCertitecna, User userInSession) {
        Collection<Role> rolesInDb = userDAO.findAllRolesAllowed(isCertitecna);
        Collection<Role> rolesAllowed = new ArrayList<Role>();

        // contract manager can't add administrator
        if (sessionContext.isCallerInRole("administrator")) {
            rolesAllowed.addAll(rolesInDb);
        } else if (sessionContext.isCallerInRole("contractmanager")) {
            // remove administrator
            for (Role role : rolesInDb) {
                if (!role.getRole().equals("administrator")) {
                    rolesAllowed.add(role);
                }
            }
        } else if (sessionContext.isCallerInRole("clientcontractmanager")) {
            // client contract manager can't assign roles
        }

        // now add the user role
        rolesAllowed.add(new Role(Configuration.getInstance().getUserRole(), "role.user", true));
        return rolesAllowed;
    }

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void insertByUserRegistration(User user, long contractId) throws ObjectNotFoundException {
        // generate activationKey
        UUID uuid = UUID.randomUUID();
        user.setActivationKey(uuid.toString());
        user.setLastPlanOrLegislationView(new Date());
        user.setActive(true);
        user.setUniqueSession(false);

        userDAO.insert(user);

        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            throw new ObjectNotFoundException("Object not found", Type.CONTRACT);
        }

        // get contract base permissions
        Collection<Permission> permissions = permissionDAO.find(contractId);
        Collection<Permission> basePermissions = new ArrayList<Permission>();

        for (Permission permission : permissions) {
            if (permission.isUserRegisterBasePermission()) {
                basePermissions.add(permission);
            }
        }

        // add user to contract
        UserContract userContract =
                new UserContract(new UserContractPK(contractId, user.getId()), user, contract, basePermissions);
        userDAO.insertUserContract(userContract);

        //checkAndSendNotificationEmail(user.getId(), true);
    }

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void insertByLocalInstallation(User user, long companyId) throws ObjectNotFoundException {
        // generate activationKey
        UUID uuid = UUID.randomUUID();
        user.setActivationKey(uuid.toString());
        user.setLastPlanOrLegislationView(new Date());
        user.setActive(true);
        user.setUniqueSession(false);
        user.setPassword(null);

        user.setCompany(companyDAO.findById(companyId));
        userDAO.insert(user);
        /*
        if (contract == null) {
            throw new ObjectNotFoundException("Object not found", Type.CONTRACT);
        }

        // get contract base permissions
        Collection<Permission> permissions = permissionDAO.find(contractId);
        Collection<Permission> basePermissions = new ArrayList<Permission>();

        for (Permission permission : permissions) {
            if (permission.isUserRegisterBasePermission()) {
                basePermissions.add(permission);
            }
        }

        // add user to contract
        UserContract userContract =
                new UserContract(new UserContractPK(contractId, user.getId()), user, contract, basePermissions);
        userDAO.insertUserContract(userContract);
        */
        //checkAndSendNotificationEmail(user.getId(), true);
    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void insert(User user, User userInSession) throws ObjectNotFoundException, CertitoolsAuthorizationException {

        if (sessionContext.isCallerInRole("contractmanager")) {
            // ok
        } else if (sessionContext.isCallerInRole("administrator") ||
                sessionContext.isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId().longValue() != user.getCompany().getId().longValue()) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see users outside their company");
            }
        }

        // generate activationKey
        UUID uuid = UUID.randomUUID();
        user.setActivationKey(uuid.toString());
        user.setLastPlanOrLegislationView(new Date());

        userDAO.insert(user);

        Contract contract;

        for (UserContract userContract : user.getUserContract()) {
            contract = contractDAO.findById(userContract.getUserContractPK().getIdContract());

            if (contract == null) {
                throw new ObjectNotFoundException("Object not found", Type.CONTRACT);
            }

            if (userContract.getPermissions() != null && !userContract.getPermissions().isEmpty()) {
                if (userContract.getPermissions().size() != permissionDAO
                        .findByIdForSelectEntity(userContract.getPermissions(),
                                userContract.getUserContractPK().getIdContract()).size()) {

                    throw new ObjectNotFoundException("Object not found", Type.PERMISSION);
                }
            }
            userContract.getUserContractPK().setIdUser(user.getId());
            userContract.setUser(user);
            userContract.setContract(contract);
            userDAO.insertUserContract(userContract);
        }
        checkAndSendNotificationEmail(user.getId(), true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager", "private"})
    public void checkAndSendNotificationEmail(long userId, boolean isToFlushAndClear) {
        if (isToFlushAndClear) {
            //Sync with database
            userDAO.flush();
            //Clear cache
            userDAO.clear();
        }
        //load User
        User user = userDAO.findForPassNotificationSend(userId);
        if (user != null) {
            if (isToSendActivationEmail(user)) {
                MailSender.sendActivationEmail(user);
                user.setActivatePassNotificationSend(true);
                userDAO.merge(user);
            }
        }

    }

    @RolesAllowed(value = "private")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void checkAllUsersAndSendNotificationEmail() {
        userDAO.flush();
        userDAO.clear();
        List<User> users = userDAO.findAllForPassNotificationSend();
        if (users != null) {
            for (User user : users) {
                if (isToSendActivationEmail(user)) {
                    MailSender.sendActivationEmail(user);
                    user.setActivatePassNotificationSend(true);
                    userDAO.merge(user);
                }
            }
        }
    }

    private boolean isToSendActivationEmail(User user) {
        //User is not Active or already received notification, do not send
        if (user.isDeleted() || !user.isActive() || user.isActivatePassNotificationSend()) {
            return false;
        }
        //If user is Certitecna, receive notification
        if (user.getCompany().getId().equals(Long.valueOf(Configuration.getInstance().getCertitecnaId()).longValue())) {
            return true;
        }
        //Check User contracts
        if (user.getUserContract() != null) {
            if (user.validateModuleAccess(ModuleType.LEGISLATION) || user.validateModuleAccess(ModuleType.PEI) ||
                    user.validateModuleAccess(ModuleType.PRV) || user.validateModuleAccess(ModuleType.GSC)) {
                return true;
            }
        } else {
            //User does not have contracts so do not receive notification
            return false;
        }
        return false;
    }

    // contract manager can get all users
    // client contract manager and administrator can only see their company users

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(User user, User userInSession) throws ObjectNotFoundException, CertitoolsAuthorizationException {
        User userInDb = userDAO.findByIdWithContractsRoles(user.getId());

        if (sessionContext.isCallerInRole("contractmanager")) {
            // ok
        } else if (sessionContext.isCallerInRole("administrator") ||
                sessionContext.isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId().longValue() != userInDb.getCompany().getId().longValue()) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see users outside their company");
            }
        }


        if (userInDb == null) {
            throw new ObjectNotFoundException("Can't find the user with the specified id",
                    Type.USER);
        }

        //Check for user associations to new contracts. If new contract is selected, update user LastPlanOrLegislationView
        Collection<Contract> companyContracts = contractDAO.findAll(user.getCompany().getId());
        for (Contract contract : companyContracts) {
            boolean existsInUserSelection = false;
            boolean existsInDB = true;

            for (UserContract userContract : user.getUserContract()) {
                if (userContract.getUserContractPK().getIdContract() == contract.getId()) {
                    existsInUserSelection = true;
                    break;
                }
            }
            if (userInDb.getUserContract() == null || userInDb.getUserContract().isEmpty()) {
                existsInDB = false;
            } else {
                for (UserContract userContract : userInDb.getUserContract()) {
                    if (userContract.getUserContractPK().getIdContract() == contract.getId()) {
                        existsInDB = true;
                        break;
                    } else {
                        existsInDB = false;
                    }
                }
            }
            if (existsInUserSelection && !existsInDB && contract.isContractInactivityOn()) {
                userInDb.setLastPlanOrLegislationView(new Date());
                break;
            }
        }

        // delete all user contracts
        for (UserContract userContract : userInDb.getUserContract()) {
            userDAO.deleteUserContract(userContract);
        }

        // set the user contracts
        Contract contract;
        for (UserContract userContract : user.getUserContract()) {

            contract = contractDAO.findById(userContract.getUserContractPK().getIdContract());

            if (contract == null) {
                throw new ObjectNotFoundException("Object not found", Type.CONTRACT);
            }

            if (userContract.getPermissions() != null && !userContract.getPermissions().isEmpty()) {
                if (userContract.getPermissions().size() != permissionDAO
                        .findByIdForSelectEntity(userContract.getPermissions(),
                                userContract.getUserContractPK().getIdContract()).size()) {

                    throw new ObjectNotFoundException("Object not found", Type.PERMISSION);
                }
            }

            userContract.getUserContractPK().setIdUser(userInDb.getId());
            userContract.setUser(userInDb);
            userContract.setContract(contract);
            userDAO.insertUserContract(userContract);
        }

        userInDb.setEmail(user.getEmail());
        userInDb.setEmailContact(user.getEmailContact());
        userInDb.setName(user.getName());
        userInDb.setFiscalNumber(user.getFiscalNumber());
        userInDb.setPhone(user.getPhone());
        userInDb.setExternalUser(user.getExternalUser());
        userInDb.setUniqueSession(user.isUniqueSession());
        userInDb.setActive(user.isActive());

        if (!sessionContext.isCallerInRole("clientcontractmanager")) {
            userInDb.setRoles(user.getRoles());
        }
        checkAndSendNotificationEmail(userInDb.getId(), true);
    }

    // Open to all because of the Change Request - reset of password by the users

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void resetUserPassword(User user) throws ObjectNotFoundException {
        User userInDb = userDAO.findById(user.getId());

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found", Type.USER);
        }

        // generate activationKey
        UUID uuid = UUID.randomUUID();
        userInDb.setActivationKey(uuid.toString());
        userInDb.setPassword(null);
        userInDb.setActivatePassNotificationSend(true);

        MailSender.sendActivationEmail(userInDb);
    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void resetUserPasswordAllowed(User user, User userInSession)
            throws ObjectNotFoundException, CertitoolsAuthorizationException {
        User userInDb = userDAO.findById(user.getId());

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found", Type.USER);
        }

        if (sessionContext.isCallerInRole("contractmanager")) {
            // ok
        } else if (sessionContext.isCallerInRole("administrator") ||
                sessionContext.isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId().longValue() != userInDb.getCompany().getId().longValue()) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see users outside their company");
            }
        }

        // generate activationKey
        UUID uuid = UUID.randomUUID();
        userInDb.setActivationKey(uuid.toString());
        userInDb.setPassword(null);
        userInDb.setActivatePassNotificationSend(true);

        MailSender.sendActivationEmail(userInDb);
    }

    // this has to be opened for all 3 roles, because this is needed when trying to insert a new user (and has to check
    // in all users in the DB)

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Collection<User> findByEmailWithDeletedAllowed(String email)
            throws ObjectNotFoundException, CertitoolsAuthorizationException {
        Collection<User> usersInDb = userDAO.findByEmailWithDeleted(email);

        if (usersInDb == null) {
            throw new ObjectNotFoundException("Object not found", Type.USER);
        }

        return usersInDb;
    }

    @PermitAll
    public void cleanUserSessions() {
        userDAO.cleanUserSessions();
    }

    @PermitAll
    public void updateUserSessionActive(Long userId, boolean sessionActive) {
        userDAO.updateUserSessionActive(userId, sessionActive);
    }

    @PermitAll
    public boolean validateLoginUserByEmail(String email) throws ObjectNotFoundException {
        User user = userDAO.findByEmail(email);

        if (user == null) {
            throw new ObjectNotFoundException("User not found", Type.USER);
        }

        if (!user.isUniqueSession()) {
            LOGGER.info("User has uniquesession = false. LOGIN OK");
            return true;
        } else if (user.isUniqueSession() && user.getSessionsActive() >= 1) {
            LOGGER.info(
                    "User has uniquesession = true and active sessions " + user.getSessionsActive() + " . LOGIN FAILS");
            return false;
        } else if (user.isUniqueSession()) {
            LOGGER.info("User has uniquesession = true and NO active session. LOGIN OK");
            return true;
        }

        return false;
    }

    // needed to do user validation

    @PermitAll
    public User findById(Long id) throws ObjectNotFoundException {
        User userInDb = userDAO.findById(id);

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found", Type.USER);
        }

        return userInDb;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PermitAll
    public void activateUser(User user, String password1, String password2, String uid)
            throws ObjectNotFoundException, InvalidPasswordException, BusinessException {
        User userInDb = userDAO.findById(user.getId());

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found", Type.USER);
        }

        if (userInDb.getFiscalNumber() == null || !user.getFiscalNumber().equals(userInDb.getFiscalNumber())) {
            throw new InvalidPasswordException("User fiscal number doesn't match DB number");
        }

        if (!password1.equals(password2)) {
            throw new InvalidPasswordException("User passwords doesn't match");
        }

        if (userInDb.getActivationKey() == null || !uid.equals(userInDb.getActivationKey())) {
            throw new InvalidPasswordException("Activation key invalid");
        }

        userInDb.setPassword(Utils.encryptMD5(password1));
        userInDb.setActivationKey(null);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"user"})
    public void updateUserLoginStatistics(User user) throws ObjectNotFoundException {
        User userInDb = userDAO.findById(user.getId());

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found", Type.USER);
        }

        userInDb.setLastLoginDate(new Date());
        userInDb.setNumberLogins(userInDb.getNumberLogins() + 1);
    }

    // TESTED ALL BRANCHES OK

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public User findByIdAllowed(Long id, User userInSession)
            throws ObjectNotFoundException, CertitoolsAuthorizationException {
        User userInDb = userDAO.findById(id);

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found", Type.USER);
        }

        if (sessionContext.isCallerInRole("contractmanager")) {
            return userInDb;

        } else if (sessionContext.isCallerInRole("administrator") && userInDb.getCompany().getId().longValue() !=
                Long.valueOf(Configuration.getInstance().getCertitecnaId()).longValue()) {
            // user is administrator but tried to see an user outside Certitecna
            throw new CertitoolsAuthorizationException("Administrator role can't see users outside Certitecna");

        } else if (sessionContext.isCallerInRole("clientcontractmanager") &&
                userInDb.getCompany().getId().longValue() != userInSession.getCompany().getId().longValue()) {
            // client contract manager tried to see an outside user
            throw new CertitoolsAuthorizationException(
                    "clientcontractmanager role can't see users outside their company");
        }

        return userInDb;
    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public User findByIdWithContractsRolesAllowed(long id, User userInSession)
            throws ObjectNotFoundException, CertitoolsAuthorizationException {
        User userInDb = userDAO.findByIdWithContractsRoles(id);

        for (UserContract userContract : userInDb.getUserContract()) {
            Collection<Permission> permissions = userContract.getPermissions();
            if (permissions != null) {
                permissions.size();
            }
        }

        if (userInDb == null) {
            throw new ObjectNotFoundException("User not found", Type.USER);
        }

        if (sessionContext.isCallerInRole("contractmanager")) {
            return userInDb;

        } else if (sessionContext.isCallerInRole("administrator") && userInDb.getCompany().getId().longValue() !=
                Long.valueOf(Configuration.getInstance().getCertitecnaId()).longValue()) {
            // user is administrator but tried to see an user outside Certitecna
            throw new CertitoolsAuthorizationException("Administrator role can't see users outside Certitecna");

        } else if (sessionContext.isCallerInRole("clientcontractmanager") &&
                userInDb.getCompany().getId().longValue() != userInSession.getCompany().getId().longValue()) {
            // client contract manager tried to see an outside user
            throw new CertitoolsAuthorizationException(
                    "clientcontractmanager role can't see users outside their company");
        }

        return userInDb;
    }

    // everybody can change its password

    @RolesAllowed(value = "user")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateProfile(Long userId, String oldPassword, String newPassword
    )
            throws BusinessException, ObjectNotFoundException, InvalidPasswordException {
        if (userId == null) {
            throw new BusinessException("Incorrect parameter");
        }

        User toBeUpdated = userDAO.findById(userId);

        if (toBeUpdated == null) {
            throw new ObjectNotFoundException("User not found", Type.USER);
        }

        if (!toBeUpdated.getPassword().equals(Utils.encryptMD5(oldPassword))) {
            throw new InvalidPasswordException("Error password mismatch");
        }

        toBeUpdated.setPassword(Utils.encryptMD5(newPassword));
    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(User user, User userInSession)
            throws ObjectNotFoundException, AdminDeleteException, CertitoolsAuthorizationException {
        // user exists?
        user = userDAO.findByIdWithContractsRoles(user.getId());

        if (user == null) {
            throw new ObjectNotFoundException("Can't find the user with the specified id.",
                    Type.USER);
        }

        if (user.getId().equals(Configuration.getInstance().getAdminId())) {
            throw new AdminDeleteException("Can't delete the administrator");
        }


        if (sessionContext.isCallerInRole("contractmanager")) {
            // ok
        } else if (sessionContext.isCallerInRole("administrator") ||
                sessionContext.isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId().longValue() != user.getCompany().getId().longValue()) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see users outside their company");
            }
        }

        // delete user and user contract associations
        user.setDeleted(true);
        for (UserContract userContract : user.getUserContract()) {
            contractDAO.deleteUserContract(userContract);
        }
    }

    // contract manager can get all users
    // client contract manager and administrator can only see their company users

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<User> findUsersByCompanyIdAndNameAllowed(long companyId, String name, User userInSession)
            throws CertitoolsAuthorizationException {

        if (sessionContext.isCallerInRole("contractmanager")) {
            return userDAO.findUsersByCompanyIdAndNameEmailNifPhone(companyId, name);
        }

        if (sessionContext.isCallerInRole("administrator") || sessionContext.isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId() != companyId) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see users outside their company");
            }
        }

        return userDAO.findUsersByCompanyIdAndNameEmailNifPhone(companyId, name);
    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<User> findUsersByContractIdAndNameAllowed(long contractId, String name, User userInSession)
            throws CertitoolsAuthorizationException {

        Contract contract = contractDAO.findById(contractId);

        if (sessionContext.isCallerInRole("contractmanager")) {
            return userDAO.findUsersByContractIdAndNameEmailNifPhone(contractId, name);
        }

        if (sessionContext.isCallerInRole("administrator") || sessionContext.isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId().longValue() != contract.getCompany().getId().longValue()) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see users outside their company");
            }
        }

        return userDAO.findUsersByContractIdAndNameEmailNifPhone(contractId, name);
    }

    // contract manager can get all users
    // client contract manager can only see it's company users
    // administrator can only see Certitecna users

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<User> findAllWithRolesByCompanyIdAllowed(Long companyId, User userInSession)
            throws CertitoolsAuthorizationException {

        if (sessionContext.isCallerInRole("contractmanager")) {
            return userDAO.findAllWithRolesByCompanyId(companyId);
        }

        if (sessionContext.isCallerInRole("administrator") || sessionContext.isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId().longValue() != companyId.longValue()) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see users outside their company");
            }
        }

        return userDAO.findAllWithRolesByCompanyId(companyId);
    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<User> findAllWithRolesByContractIdAllowed(Long contractId, User userInSession)
            throws CertitoolsAuthorizationException {

        Contract contract = contractDAO.findById(contractId);

        if (sessionContext.isCallerInRole("contractmanager")) {
            return userDAO.findAllWithRolesByContractId(contractId);
        }

        if (sessionContext.isCallerInRole("administrator") || sessionContext.isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId().longValue() != contract.getCompany().getId().longValue()) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see users outside their company");
            }
        }

        return userDAO.findAllWithRolesByContractId(contractId);

    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public ByteArrayOutputStream exportUsers(Long companyId, Long contractId) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));

        Collection<User> users = userDAO.findAllForExport(companyId, contractId);
        int i = 0;
        for (User u : users) {
            StringBuilder builder = CertitoolsCsvWorker.parseUserToCSV(u);
            writer.write(builder.toString());
            if (++i < users.size()) {
                writer.newLine();
            }
        }
        writer.close();
        return baos;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"user"})
    public void updateUserSeenPEI(long userId, boolean seenPEI) throws ObjectNotFoundException {
        User userInDb = userDAO.findById(userId);

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found", Type.USER);
        }

        userInDb.setSeenPEI(seenPEI);
    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int importUsers(Long companyId, InputStream stream) throws ImportException, ObjectNotFoundException {
        int numberOfRecordsImported = 0;

        Company c = companyDAO.findById(companyId);
        if (c == null) {
            throw new ObjectNotFoundException("Company not found", ObjectNotFoundException.Type.COMPANY);
        }
        Map<String, Object[]> users;
        try {
            users = CertitoolsCsvWorker.readImportFile(stream);
        } catch (ImportException e) {
            LOGGER.warn(e.getMessage());
            throw e;
        }

        List<User> toSendEmail = new ArrayList<User>();

        if (!users.isEmpty()) {
            Iterator<String> it = users.keySet().iterator();
            while (it.hasNext()) {
                boolean toUpdate = true;
                String userEmail = it.next();
                User u = userDAO.findByEmailAllStates(userEmail);
                if (u == null) {
                    u = new User();
                    u.setActivationKey(UUID.randomUUID().toString());
                    toUpdate = false;
                }

                u.setLastPlanOrLegislationView(new Date());
                u.setCompany(c);

                Object[] values = users.get(userEmail);
                u.setName((String) values[0]);
                u.setEmail(userEmail);
                u.setEmailContact(((String) values[2]));
                u.setFiscalNumber((Long) values[3]);
                u.setPhone((String) values[4]);
                u.setExternalUser((String) values[5]);

                u.setActive((Boolean) values[6]);
                u.setUniqueSession((Boolean) values[7]);
                u.setDeleted((Boolean) values[8]);

                Collection<Role> roles = new ArrayList<Role>();
                roles.add(userDAO.findRole("user"));
                if (values.length > 9) {
                    String[] aux = (String[]) values[9];
                    if (aux != null) {
                        for (int z = 0; z < aux.length; z++) {
                            String role = aux[z];
                            if (role != null) {
                                Role r = userDAO.findRole(role);
                                if (r == null) {
                                    LOGGER.info("Error importing users, role not found");
                                    throw new ImportException("Error Role not found",
                                            "error.users.import.file.csv.exception.insert.role.not.found", userEmail,
                                            role, ImportException.Type.INSERT);
                                }
                                roles.add(r);
                            }
                        }
                    }
                }
                u.setRoles(roles);
                toSendEmail.add(u);

                if (!toUpdate) {
                    userDAO.insert(u);
                } else {
                    // delete all user contracts
                    if (u.getUserContract() != null) {
                        for (UserContract userContract : u.getUserContract()) {
                            userDAO.deleteUserContract(userContract);
                        }
                    }
                }
                // set the user contracts
                if (values.length > 10) {
                    for (int i = 10; i < values.length; i++) {
                        Object[] contracts = (Object[]) values[i];
                        if (contracts != null) {
                            for (int z = 0; z < contracts.length; z++) {
                                Long contractId = (Long) contracts[0];
                                Contract contract = contractDAO.findById(contractId);

                                if (contract == null) {
                                    LOGGER.info("Error importing users, contract not found");
                                    throw new ImportException("Error Contract not found",
                                            "error.users.import.file.csv.exception.insert.contract.not.found",
                                            userEmail, "" + contractId, ImportException.Type.INSERT);
                                }

                                // check if contract is of the company of the user
                                if (!contract.getCompany().getId().equals(companyId)) {
                                    LOGGER.info("Error importing users, contract is not from the users company");
                                    throw new ImportException("Error Contract is not from the users company",
                                            "error.users.import.file.csv.exception.insert.contract.companyInvalid",
                                            userEmail, "" + contractId, ImportException.Type.INSERT);
                                }

                                UserContract userContract = new UserContract();
                                userContract.setUserContractPK(new UserContractPK(contractId, u.getId()));

                                if (contracts.length > 1) {
                                    if (contracts[1] != null) {
                                        userContract.setValidityStartDate((Date) contracts[1]);
                                    }
                                    if (contracts[2] != null) {
                                        userContract.setValidityEndDate((Date) contracts[2]);
                                    }
                                }

                                if (contracts.length > 3) {
                                    List<Permission> permissions = new ArrayList<Permission>();
                                    for (int y = 3; y < contracts.length; y++) {
                                        Permission p = permissionDAO.findByName((String) contracts[y], contractId);
                                        if (p == null) {
                                            LOGGER.info(
                                                    "Error importing users, permission not found on this contract");
                                            throw new ImportException("Error Permission not found on this contract",
                                                    "error.users.import.file.csv.exception.insert.permission.not.found",
                                                    userEmail, (String) contracts[y], ImportException.Type.INSERT);
                                        }
                                        permissions.add(p);
                                    }
                                    userContract.setPermissions(permissions);
                                }
                                userDAO.updateUserContract(userContract);
                            }
                        }
                    }
                }
                numberOfRecordsImported++;
            }
        }
        //Sync with database
        userDAO.flush();
        //Clear cache
        userDAO.clear();
        for (User u : toSendEmail) {
            checkAndSendNotificationEmail(u.getId(), false);
        }
        return numberOfRecordsImported;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"user"})
    public void updateUserLastPlanOrLegislationView(long userId, ModuleType moduleType, Long contractId)
            throws BusinessException {

        User user = userDAO.findById(userId);
        if (user == null) {
            throw new BusinessException("Cannot find user with id: " + userId);
        }

        //Legislation Manager
        if (moduleType.equals(ModuleType.LEGISLATION) && sessionContext.isCallerInRole("legislationmanager")) {
            user.setLastPlanOrLegislationView(new Date());
            return;
        }

        //TODO-MODULE
        //clientepeimanager normally doesnt have contracts.
        if (moduleType.equals(ModuleType.PEI) || moduleType.equals(ModuleType.PRV) || moduleType
                .equals((ModuleType.PSI)) || moduleType.equals(ModuleType.GSC)) {
            if (sessionContext.isCallerInRole("clientpeimanager") || sessionContext.isCallerInRole("peimanager")) {
                user.setLastPlanOrLegislationView(new Date());
                return;
            }
        }
        //Validation for simple users
        if (user.getUserContract() != null) {
            for (UserContract userContract : user.getUserContract()) {
                Contract contract = userContract.getContract();
                if (contract.getModule().getModuleType().equals(moduleType) && contract.isContractInactivityOn()) {
                    if (moduleType.equals(ModuleType.LEGISLATION)) {
                        user.setLastPlanOrLegislationView(new Date());
                        return;
                    } else {
                        if (contractId != null && contractId == userContract.getContract().getId()) {
                            user.setLastPlanOrLegislationView(new Date());
                            return;
                        }
                    }
                }
            }
        }
    }

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateUserLanguage(long userId, String language) throws ObjectNotFoundException {
        User userInDb = userDAO.findById(userId);

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found", Type.USER);
        }

        userInDb.setLanguage(language);
    }

    private List<Contract> getActiveUserContracts(Collection<UserContract> userContracts) {
        List<Contract> contracts = new ArrayList<Contract>();

        if (userContracts != null) {
            for (UserContract userContract : userContracts) {
                if (userContract.getContract().isActive()) {
                    contracts.add(userContract.getContract());
                }
            }
        }
        return contracts;
    }

    private enum UserInactivityActionProtocol {
        CANNOT_DELETE_USER, CAN_DELETE_USER
    }

    private UserInactivityActionProtocol checkUserInactivityType(List<Contract> userContracts) {
        for (Contract contract : userContracts) {
            if (!contract.isContractInactivityOn() && userContracts.size() >= 2) {
                return UserInactivityActionProtocol.CANNOT_DELETE_USER;
            }
        }
        return UserInactivityActionProtocol.CAN_DELETE_USER;
    }

    private List<UserInactivityAction> scheduleUserInactivityActions(User user) {

        List<UserInactivityAction> actions = new ArrayList<UserInactivityAction>();

        //Get Active User Contracts
        List<Contract> activeContracts = getActiveUserContracts(user.getUserContract());

        //There are no contracts so do nothing
        if (activeContracts.isEmpty()) {
            return actions;
        }

        UserInactivityActionProtocol type = checkUserInactivityType(activeContracts);

        //Remove inactive contracts
        List<Contract> contractsToWork = new ArrayList<Contract>();
        for (Contract contract : activeContracts) {
            if (contract.isContractInactivityOn()) {
                contractsToWork.add(contract);
            }
        }

        if (contractsToWork.isEmpty()) {
            return actions;
        }

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar lastUserAccessDate = Calendar.getInstance();
        lastUserAccessDate.setTime(user.getLastPlanOrLegislationView());
        lastUserAccessDate.set(Calendar.HOUR_OF_DAY, 0);
        lastUserAccessDate.set(Calendar.MINUTE, 0);
        lastUserAccessDate.set(Calendar.SECOND, 0);
        lastUserAccessDate.set(Calendar.MILLISECOND, 0);

        int daysBetween = daysBetween(lastUserAccessDate, today);

        if (type.equals(UserInactivityActionProtocol.CAN_DELETE_USER)) {
            //In normal protocol, first search for users to delete... in this case do not send warning messages in other contracts
            for (Contract contract : contractsToWork) {
                if (daysBetween >= contract.getSecondInactivityMessageTerm() + contract
                        .getFirstInactivityMessageTerm() + contract.getDeleteUserTerm()) {
                    actions.add(new UserInactivityAction(contract, UserInactivityAction.Type.DELETE_USER));
                    return actions;
                }
            }
            for (Contract contract : contractsToWork) {
                //No users to delete so lets find first and second warning messages to send
                if (daysBetween == contract.getFirstInactivityMessageTerm()) {
                    actions.add(new UserInactivityAction(contract, UserInactivityAction.Type.SEND_FIRST_MESSAGE));
                } else if (daysBetween == contract.getSecondInactivityMessageTerm() + contract
                        .getSecondInactivityMessageTerm()) {
                    actions.add(new UserInactivityAction(contract, UserInactivityAction.Type.SEND_SECOND_MESSAGE));
                }
            }
        } else {
            //Ony send first Message
            for (Contract contract : contractsToWork) {
                if (daysBetween == contract.getFirstInactivityMessageTerm()) {
                    //Send first Message
                    actions.add(new UserInactivityAction(contract, UserInactivityAction.Type.SEND_FIRST_MESSAGE));
                } else if (daysBetween == contract.getFirstInactivityMessageTerm() + contract
                        .getSecondInactivityMessageTerm()) {
                    actions.add(new UserInactivityAction(contract, UserInactivityAction.Type.SEND_SECOND_MESSAGE));
                } else if (daysBetween >= contract.getSecondInactivityMessageTerm() + contract
                        .getFirstInactivityMessageTerm() + contract.getDeleteUserTerm()) {
                    actions.add(new UserInactivityAction(contract, UserInactivityAction.Type.REMOVE_CONTRACT_LICENSE));
                }
            }
        }
        return actions;
    }

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void checkAndDeleteOldUsers() {
        LOGGER.info("[deleteOldUsers] Starting method...");

        Collection<User> users = userDAO.findAll();

        for (User user : users) {
            LOGGER.info("[deleteOldUsers] ** Working on user id " + user.getId());
            if (isUserCandidateForDelete(user)) {
                LOGGER.info("[deleteOldUsers] ** User id " + user.getId()
                        + " is candidate for send 1�/2� message or delete... Checking contracts");
                List<UserInactivityAction> actions = scheduleUserInactivityActions(user);
                if (!actions.isEmpty()) {
                    //Delete User
                    if (actions.size() == 1 && actions.get(0).getActionType()
                            .equals(UserInactivityAction.Type.DELETE_USER)) {
                        //Delete User
                        LOGGER.info(
                                "[scheduleUserInactivityActions] *** Working on contract " + actions.get(0)
                                        .getContract().getId());
                        LOGGER.info("[deleteOldUsers] ** Preparing for delete user id " + user.getId());
                        deleteUserFromDB(user);
                    } else {
                        for (UserInactivityAction action : actions) {
                            LOGGER.info(
                                    "[scheduleUserInactivityActions] *** Working on contract " + action.getContract()
                                            .getId());
                            if (action.getActionType().equals(UserInactivityAction.Type.SEND_FIRST_MESSAGE)) {
                                //Send First Message
                                LOGGER.info("[deleteOldUsers] ** Preparing for send first message ...");
                                MailSender.sendUserInactivityWarningEmail(user.getEmailContact(),
                                        action.getContract().getFirstInactivityMessageTemplateSubject(),
                                        action.getContract().getFirstInactivityMessageTemplateBody());
                            } else if (action.getActionType().equals(UserInactivityAction.Type.SEND_SECOND_MESSAGE)) {
                                //Send Second Message
                                LOGGER.info("[deleteOldUsers] ** Preparing for send second message ...");
                                MailSender.sendUserInactivityWarningEmail(user.getEmailContact(),
                                        action.getContract().getSecondInactivityMessageTemplateSubject(),
                                        action.getContract().getSecondInactivityMessageTemplateBody());
                            } else if (action.getActionType()
                                    .equals(UserInactivityAction.Type.REMOVE_CONTRACT_LICENSE)) {
                                //Remove user from contract
                                LOGGER.info("[deleteOldUsers] ** Preparing to remove user " + user.getId()
                                        + " from contract " + action.getContract().getId());
                                deleteUserContract(action.getContract().getId(), user);
                            }
                        }
                    }
                }
            }
        }
        LOGGER.info("[deleteOldUsers] Method ended...");
    }

    private void deleteUserContract(long contractId, User user) {
        for (UserContract userContract : user.getUserContract()) {
            if (userContract.getContract().getId() == contractId) {
                for (Permission permission : userContract.getPermissions()) {
                    permissionDAO.delete(permission);
                }
                userDAO.deleteUserContract(userContract);
                LOGGER.info("[deleteUserContract] ** User " + user.getId() + " removed from contract " + contractId);
                return;
            }
        }
    }

    /**
     * Delete User and all relationships
     *
     * @param user to delete
     */

    private void deleteUserFromDB(User user) {
        try {
            //Delete User Categories Subscription
            user.getSubscriptionsLegalDocuments().size();
            if (user.getSubscriptionsLegalDocuments() != null && !user.getSubscriptionsLegalDocuments().isEmpty()) {
                user.setSubscriptionsLegalDocuments(null);
            }

            //Delete User Roles
            user.getRoles().size();
            user.setRoles(null);

            //Delete User Legal Documents History
            List<LegalDocumentHistory> legalDocumentHistories =
                    legalDocumentHistoryDAO.findAllHistoryActiveByUser(user.getId(), null);
            if (legalDocumentHistories != null) {
                for (LegalDocumentHistory legalDocumentHistory : legalDocumentHistories) {
                    legalDocumentHistoryDAO.delete(legalDocumentHistory);
                }
            }
            //Delete User Contract and User Contract Permissions  and Update Contract licenses
            for (UserContract userContract : user.getUserContract()) {
                userContract.getPermissions().size();
                for (Permission permission : userContract.getPermissions()) {
                    permissionDAO.delete(permission);
                }
                userDAO.deleteUserContract(userContract);
            }
            userDAO.delete(user);
            LOGGER.info("[deleteUserFromDB] User Deleted!");
        } catch (Exception e) {
            LOGGER.error("[deleteUserFromDB]: Error deleting user " + user.getId(), e);
        }
    }

    public static int daysBetween(Calendar startDate, Calendar endDate) {
        Calendar date = (Calendar) startDate.clone();
        int daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    private boolean isUserCandidateForDelete(User user) {

        //For Certitecna users,we must not delete users
        if (user.getCompany().getId().equals(Long.valueOf(Configuration.getInstance().getCertitecnaId()).longValue())) {
            return false;
        }

        user.getRoles().size();
        if (user.getRoles() != null) {
            Role userRole = new Role("user");
            Role userguestRole = new Role("userguest");

            //Role is only user
            if (user.getRoles().size() == 1 && user.getRoles().contains(userRole)) {
                return true;
            }
            //Role is user and userguest
            if (user.getRoles().size() == 2 && user.getRoles().contains(userRole) && user.getRoles()
                    .contains(userguestRole)) {
                return true;
            }
        }
        return false;
    }

}
