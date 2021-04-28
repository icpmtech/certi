/*
 * $Id: UserDAOEJB.java,v 1.43 2010/03/30 10:28:29 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/03/30 10:28:29 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.Role;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

/**
 * User data access object
 *
 * @author haraujo
 */
@Stateless
@Local(UserDAO.class)
@LocalBinding(jndiBinding = "certitools/UserDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UserDAOEJB extends GenericDAOEJB<User, Long> implements UserDAO {

    private static final Logger LOGGER = Logger.getInstance(UserDAOEJB.class);

    public User findById(Long id) {
        Query query = manager.createQuery("SELECT DISTINCT u FROM User u WHERE u.deleted = false AND u.id = ?1");
        query.setParameter(1, id);

        try {
            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No entity found");
            return null;
        }
    }

    public User findByIdWithContractsRoles(long id) {
        Query query = manager.createQuery("SELECT DISTINCT u FROM User u "
                + " LEFT JOIN FETCH u.roles "
                + " LEFT JOIN FETCH u.userContract "
                + " WHERE u.deleted = false "
                + " AND u.id = ?1");
        query.setParameter(1, id);

        try {
            User user = (User) query.getSingleResult();

            for (UserContract userContract : user.getUserContract()) {
                userContract.getContract();
            }

            // remove duplicate roles
            Set<Role> roles = new HashSet<Role>(user.getRoles());
            user.setRoles(roles);

            return user;

        } catch (NoResultException e) {
            LOGGER.debug("No entity found");
            return null;
        }
    }

    public User findByIdWithContractsPermissionsRoles(long id) {
        Query query = manager.createQuery("SELECT DISTINCT u FROM User u "
                + " LEFT JOIN FETCH u.roles "
                + " LEFT JOIN FETCH u.userContract "
                + " WHERE u.deleted = false "
                + " AND u.id = ?1");
        query.setParameter(1, id);

        try {
            User user = (User) query.getSingleResult();

            for (UserContract userContract : user.getUserContract()) {
                userContract.getContract();
                userContract.getPermissions().size();
            }

            // remove duplicate roles
            Set<Role> roles = new HashSet<Role>(user.getRoles());
            user.setRoles(roles);

            return user;

        } catch (NoResultException e) {
            LOGGER.debug("No entity found");
            return null;
        }
    }

    public User findByIdAndModuleWithContractsRoles(long id, Module module) {
        Query query = manager.createQuery("SELECT DISTINCT u FROM User u "
                + " LEFT JOIN FETCH u.roles "
                + " LEFT JOIN FETCH u.userContract "
                + " WHERE u.deleted = false "
                + " AND u.id = ?1");
        query.setParameter(1, id);

        try {
            User user = (User) query.getSingleResult();
            Set<UserContract> userContractSet = new HashSet<UserContract>();

            // filter user contracts by type
            for (UserContract userContract : user.getUserContract()) {
                if (userContract.getContract().getModule().equals(module)) {
                    userContractSet.add(userContract);
                }
            }
            user.setUserContract(userContractSet);

            // remove duplicate roles
            Set<Role> roles = new HashSet<Role>(user.getRoles());
            user.setRoles(roles);

            return user;
        } catch (NoResultException e) {
            LOGGER.debug("No entity found");
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Collection<User> findAll() {
        return manager.createQuery("SELECT u FROM User u where u.deleted = false").getResultList();
    }


    @SuppressWarnings({"unchecked"})
    public Collection<User> findUsersByStartLetter(String letter, long companyId) {
        Query query;

        if (letter.equals("#")) {
            query = manager.createNativeQuery(
                    "SELECT * "
                            + " FROM Users u, Company c "
                            + " WHERE u.company_id = c.id "
                            + " AND c.id = :companyId "
                            + " AND UPPER(to_ascii(convert_to(u.name, 'latin1'), 'latin1')) ~ '^[^a-zA-Z]' "
                            + " AND u.deleted = false "
                            + " ORDER BY u.active DESC, u.name ASC",
                    User.class);
            query.setParameter("companyId", companyId);
        } else {
            query = manager.createQuery(
                    "SELECT new com.criticalsoftware.certitools.entities.User(u.id, u.email, u.name, u.active) "
                            + "FROM User u "
                            + "WHERE UPPER(to_ascii(convert_to(u.name, 'latin1'), 'latin1')) LIKE :letter "
                            + "AND u.company.id = :companyId "
                            + "AND u.deleted = false "
                            + "ORDER BY u.active DESC, u.name ASC");
            query.setParameter("letter", letter + "%");
            query.setParameter("companyId", companyId);
        }

        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public Collection<User> findUsersByStartLetterContractId(String letter, long contractId) {
        Query query;

        if (letter.equals("#")) {
            query = manager.createNativeQuery(
                    "SELECT * "
                            + " FROM Users u LEFT JOIN user_contract uc ON u.id = uc.user_id"
                            + " WHERE uc.contract_id = ?1"
                            + " AND UPPER(to_ascii(convert_to(u.name, 'latin1'), 'latin1')) ~ '^[^a-zA-Z]' "
                            + " AND u.deleted = false "
                            + " ORDER BY u.active DESC, u.name ASC",
                    User.class);
            query.setParameter(1, contractId);
        } else {
            query = manager.createQuery(
                    "SELECT DISTINCT u "
                            + "FROM User u LEFT JOIN FETCH u.userContract contracts "
                            + "WHERE UPPER(to_ascii(convert_to(u.name, 'latin1'), 'latin1')) LIKE :letter "
                            + "AND contracts.userContractPK.idContract = :contractId "
                            + "AND u.deleted = false "
                            + "ORDER BY u.active DESC, u.name ASC");
            query.setParameter("letter", letter + "%");
            query.setParameter("contractId", contractId);
        }

        return query.getResultList();
    }

    public User findByEmail(String login) {
        Query query = manager.createQuery("SELECT u FROM User u "
                + " where UPPER(u.email) = UPPER(?1) AND u.deleted = false");
        query.setParameter(1, login);

        try {
            User user = (User) query.getSingleResult();

            // remove duplicate roles
            Set<Role> roles = new HashSet<Role>(user.getRoles());
            user.setRoles(roles);

            return user;
        } catch (NoResultException e) {
            LOGGER.debug("No entity found");
            return null;
        }
    }

    public User findByEmailAllStates(String login) {
        Query query = manager.createQuery("SELECT DISTINCT u FROM User u " +
                "LEFT JOIN FETCH u.userContract " +
                "where UPPER(u.email) = UPPER(?1)");
        query.setParameter(1, login);

        try {
            User user = (User) query.getSingleResult();

            // remove duplicate roles
            Set<Role> roles = new HashSet<Role>(user.getRoles());
            user.setRoles(roles);

            return user;
        } catch (NoResultException e) {
            LOGGER.debug("No entity found");
            return null;
        }
    }

    public User findByEmailWithRolesAndUserContracts(String login) {
        Query query = manager.createQuery(
                "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles LEFT JOIN FETCH u.userContract "
                        + "where UPPER(u.email) = UPPER(?1) AND u.deleted = false");
        query.setParameter(1, login);

        try {
            User user = (User) query.getSingleResult();

            // remove duplicate roles
            Set<Role> roles = new HashSet<Role>(user.getRoles());
            user.setRoles(roles);

            return user;
        } catch (NoResultException e) {
            LOGGER.debug("No entity found");
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public User findUserWithRoles(Long userId) {
        Query query = manager.createQuery("select distinct u from User u INNER JOIN FETCH u.roles where u.id =?1");
        query.setParameter(1, userId);

        try {
            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No entity found");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> findAllForNewsletter() {
        Query query = manager.createQuery(
                "SELECT DISTINCT u FROM User as u " +
                        "JOIN u.subscriptionsLegalDocuments as ldc " +
                        "JOIN ldc.legalDocuments as ld " +
                        "WHERE ldc IS NOT NULL " +
                        "AND (ld.sendNotificationNew = ?1 OR ld.sendNotificationChange = ?1) " +
                        " AND u.active = true AND u.deleted = false ");

        query.setParameter(1, true);
        return query.getResultList();
    }

    public User findUserWithSubscriptions(Long userId) {
        Query query = manager.createQuery(
                "SELECT DISTINCT u FROM User u " +
                        "LEFT JOIN FETCH  u.subscriptionsLegalDocuments " +
                        "WHERE u.id = ?1");

        query.setParameter(1, userId);
        return (User) query.getSingleResult();
    }


    public void insertUserContract(UserContract userContract) {
        manager.persist(userContract);
    }

    public void updateUserContract(UserContract userContract) {
        manager.merge(userContract);
    }

    @SuppressWarnings({"unchecked"})
    public List<User> findAllByCompanyId(long companyId) {
        Query query = manager.createQuery("SELECT u FROM User u "
                + "WHERE u.company.id = ?1 AND u.deleted = false "
                + "ORDER BY u.active DESC, u.name ASC");
        query.setParameter(1, companyId);

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<User> findAllByContractId(long contractId) {
        Query query = manager.createQuery(
                "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userContract contracts "
                        + "WHERE contracts.userContractPK.idContract = ?1 AND u.deleted = false "
                        + "ORDER BY u.active DESC, u.name ASC ");
        query.setParameter(1, contractId);
        return query.getResultList();
    }

    public long countAllByCompanyId(long companyId) {
        Query query = manager.createQuery("SELECT count(u.email) FROM User u "
                + "WHERE u.company.id = ?1 AND u.deleted = false");
        query.setParameter(1, companyId);

        return (Long) query.getSingleResult();
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Role> findAllRolesAllowed(boolean isCertitecna) {
        Query query = manager.createQuery("SELECT r FROM Role r WHERE r.isCertitecna = ?1 AND r.role <> ?2");
        query.setParameter(1, isCertitecna);
        query.setParameter(2, Configuration.getInstance().getUserRole());
        return query.getResultList();

    }

    @SuppressWarnings({"unchecked"})
    public Collection<User> findUsersByCompanyIdAndNameEmailNifPhone(long companyId, String name) {
        Long nameLong;
        try {
            nameLong = Long.valueOf(name);
        } catch (NumberFormatException e) {
            nameLong = null;
        }

        name = name.trim();
        name = name.replaceAll(" ", "%");
        name = "%" + name + "%";

        String fiscalNumberPhrase = "";

        if (nameLong != null) {
            fiscalNumberPhrase = " OR u.fiscalNumber = :nameLong ";
        }

        Query query = manager.createQuery("SELECT u FROM User u WHERE u.company.id = :companyId AND u.deleted = false "
                + " AND ("
                + " UPPER(to_ascii(convert_to(u.name, 'latin1'), 'latin1')) LIKE UPPER(to_ascii(convert_to(:name, 'latin1'), 'latin1')) "
                + " OR UPPER(to_ascii(convert_to(u.email , 'latin1'), 'latin1')) LIKE UPPER(to_ascii(convert_to(:name, 'latin1'), 'latin1')) "
                + fiscalNumberPhrase
                + " OR UPPER(to_ascii(convert_to(u.phone , 'latin1'), 'latin1')) LIKE UPPER(to_ascii(convert_to(:name, 'latin1'), 'latin1')) "
                + " ) "
                + " ORDER BY u.active DESC, u.name ASC");
        query.setParameter("companyId", companyId);
        query.setParameter("name", name);

        if (nameLong != null) {
            query.setParameter("nameLong", nameLong);
        }

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public Collection<User> findUsersByContractIdAndNameEmailNifPhone(long contractId, String name) {
        Long nameLong;
        try {
            nameLong = Long.valueOf(name);
        } catch (NumberFormatException e) {
            nameLong = null;
        }

        name = name.trim();
        name = name.replaceAll(" ", "%");
        name = "%" + name + "%";

        String fiscalNumberPhrase = "";

        if (nameLong != null) {
            fiscalNumberPhrase = " OR u.fiscalNumber = :nameLong ";
        }

        Query query = manager.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.userContract contracts WHERE "
                + " contracts.userContractPK.idContract = :contractId "
                + " AND u.deleted = false "
                + " AND ("
                + " UPPER(to_ascii(convert_to(u.name, 'latin1'), 'latin1')) LIKE UPPER(to_ascii(convert_to(:name, 'latin1'), 'latin1')) "
                + " OR UPPER(to_ascii(convert_to(u.email , 'latin1'), 'latin1')) LIKE UPPER(to_ascii(convert_to(:name, 'latin1'), 'latin1')) "
                + fiscalNumberPhrase
                + " OR UPPER(to_ascii(convert_to(u.phone , 'latin1'), 'latin1')) LIKE UPPER(to_ascii(convert_to(:name, 'latin1'), 'latin1')) "
                + " ) "
                + " ORDER BY u.active DESC, u.name ASC ");
        query.setParameter("contractId", contractId);
        query.setParameter("name", name);

        if (nameLong != null) {
            query.setParameter("nameLong", nameLong);
        }

        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public Collection<User> findAllWithRolesByCompanyId(Long companyId) {
        Query query = manager.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles "
                + " WHERE u.company.id = :companyId AND u.deleted = false ");

        query.setParameter("companyId", companyId);

        Collection<User> result = new LinkedHashSet<User>(query.getResultList());
        return new ArrayList<User>(result);
    }

    @SuppressWarnings("unchecked")
    public Collection<User> findAllWithRolesByContractId(Long contractId) {
        Query query = manager.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userContract contracts "
                + " LEFT JOIN FETCH u.roles "
                + " WHERE contracts.userContractPK.idContract = :contractId "
                + " AND u.deleted = false ");

        query.setParameter("contractId", contractId);

        Collection result = new LinkedHashSet(query.getResultList());
        return new ArrayList<User>(result);
    }

    public void deleteUserContract(UserContract userContract) {
        manager.remove(userContract);
    }

    @SuppressWarnings({"unchecked"})
    public Collection<User> findByEmailWithDeleted(String email) {
        Query query = manager.createQuery("SELECT u FROM User u where UPPER(u.email) = UPPER(?1)");
        query.setParameter(1, email);

        try {
            List<User> users = query.getResultList();

            for (User user : users) {
                // remove duplicate roles
                Set<Role> roles = new HashSet<Role>(user.getRoles());
                user.setRoles(roles);

            }

            return users;
        } catch (NoResultException e) {
            LOGGER.debug("No entity found");
            return null;
        }
    }

    public void cleanUserSessions() {
        Query query = manager.createQuery("UPDATE User u SET u.sessionsActive = 0 WHERE u.sessionsActive <> 0");
        query.executeUpdate();
    }

    public void updateUserSessionActive(Long userId, boolean sessionActive) {
        Query query;
        if (sessionActive) {
            query = manager.createQuery("UPDATE User u SET u.sessionsActive = u.sessionsActive + 1 WHERE u.id = ?1");
        } else {
            query = manager.createQuery("UPDATE User u SET u.sessionsActive = u.sessionsActive - 1 WHERE u.id = ?1");
        }
        query.setParameter(1, userId);
        query.executeUpdate();
    }

    public Role findRole(String role) {
        Query query = manager.createQuery("SELECT DISTINCT r FROM Role r WHERE r.role = ?1");
        query.setParameter(1, role);

        try {
            return (Role) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No entity found");
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public Collection<User> findAllForExport(Long companyId, Long contractId) {
        String sql = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userContract contracts WHERE ";
        if (contractId != null) {
            sql += "contracts.userContractPK.idContract = ?1 ";
        } else if (companyId != null) {
            sql += "u.company.id = ?1 ";
        }

        sql += "ORDER BY u.active DESC, u.name ASC";

        Query query = manager.createQuery(sql);

        if (contractId != null) {
            query.setParameter(1, contractId);
        } else if (companyId != null) {
            query.setParameter(1, companyId);
        }

        Collection<User> result = new LinkedHashSet(query.getResultList());

        for (User u : result) {
            u.getRoles().size();
            for (UserContract uc : u.getUserContract()) {
                uc.getPermissions().size();
            }
        }

        return result;
    }

    @SuppressWarnings({"unchecked"})
    public List<User> findAllForPassNotificationSend() {
        Query query = manager.createQuery("SELECT DISTINCT u from User u "
                + "INNER JOIN FETCH u.userContract userC "
                + "INNER JOIN FETCH u.company company "
                + "WHERE u.active = true AND u.deleted = false AND u.activatePassNotificationSend = false "
                + "AND company.id <> ?1 ");
        query.setParameter(1, Long.valueOf(Configuration.getInstance().getCertitecnaId()));
        return query.getResultList();
    }

    public User findForPassNotificationSend(Long userId) {
        Query query = manager.createQuery("SELECT DISTINCT u from User u "
                + "LEFT JOIN FETCH u.userContract "
                + "INNER JOIN FETCH u.company "
                + "WHERE u.id = ?1");
        query.setParameter(1, userId);
        return (User) query.getSingleResult();
    }

    @SuppressWarnings({"unchecked"})
    public List<User> findUsersByContractAndSecurityPermissions(Long contractId) {
        Query query = manager.createQuery("SELECT DISTINCT u FROM UserContract uc " +
                "LEFT JOIN uc.permissions p LEFT JOIN uc.user u " +
                "WHERE uc.contract.id = ?1 AND p.name IN (?2)");

        query.setParameter(1, contractId);
        List<String> permissions = new ArrayList<String>();
        permissions.add("security.permission.basic");
        permissions.add("security.permission.intermediate");
        permissions.add("security.permission.expert");
        query.setParameter(2, permissions);

        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<User> findExpertAndIntermediateUsersByContract(Long contractId) {
        Query query = manager.createQuery("SELECT DISTINCT u FROM UserContract uc " +
                "LEFT JOIN uc.permissions p LEFT JOIN uc.user u " +
                "WHERE uc.contract.id = ?1 AND p.name IN (?2)");

        query.setParameter(1, contractId);
        List<String> permissions = new ArrayList<String>();
        permissions.add("security.permission.intermediate");
        permissions.add("security.permission.expert");
        query.setParameter(2, permissions);

        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<User> findBasicUsersByContract(Long contractId) {
        Query query = manager.createQuery("SELECT DISTINCT u FROM UserContract uc " +
                "LEFT JOIN uc.permissions p LEFT JOIN uc.user u " +
                "WHERE uc.contract.id = ?1 AND p.name IN (?2)");

        query.setParameter(1, contractId);
        List<String> permissions = new ArrayList<String>();
        permissions.add("security.permission.basic");
        query.setParameter(2, permissions);
        LOGGER.info("UserDAO.findBasicUsersByContract Query: " + query.toString());
        LOGGER.info("UserDAO.findBasicUsersByContract Query results: " + query.getResultList());
        return query.getResultList();
    }
}
