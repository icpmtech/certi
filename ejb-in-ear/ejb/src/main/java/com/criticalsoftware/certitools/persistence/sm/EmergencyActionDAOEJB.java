package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.EmergencyAction;
import com.criticalsoftware.certitools.entities.sm.EmergencyToken;
import com.criticalsoftware.certitools.entities.sm.EmergencyUser;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
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
import java.util.Date;
import java.util.List;

/**
 * Emergency Action DAO EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(EmergencyActionDAO.class)
@LocalBinding(jndiBinding = "certitools/EmergencyActionDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EmergencyActionDAOEJB extends GenericDAOEJB<EmergencyAction, Long> implements EmergencyActionDAO {

    private static final Logger LOGGER = Logger.getInstance(EmergencyActionDAOEJB.class);

    public int countOpenEmergencyActions(long contractId) {
        Query query = manager.createQuery("SELECT count(e) FROM EmergencyAction e " +
                "where e.contract.id = :contractId and e.deleted = false " +
                "and (e.closedDate is null or e.closedDate > :currentDate) ");
        query.setParameter("contractId", contractId);
        query.setParameter("currentDate", new Date());
        return ((Long) (query.getSingleResult())).intValue();
    }

    public EmergencyAction findEmergencyAction(Long emergencyId) {
        Query query = manager.createQuery("select e from EmergencyAction e " +
                "where e.id = :emergencyId and e.deleted = false");
        query.setParameter("emergencyId", emergencyId);
        try {
            return (EmergencyAction) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public EmergencyAction findEmergencyActionWithContract(Long emergencyId) {
        Query query = manager.createQuery("select e from EmergencyAction e " +
                "left join fetch e.changedBy " +
                "left join fetch e.contract ct " +
                "left join fetch ct.company " +
                "where e.id = :emergencyId and e.deleted = false");
        query.setParameter("emergencyId", emergencyId);
        try {
            return (EmergencyAction) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<Chat> findChatMessages(Long emergencyId) {
        Query query = manager.createQuery("select c from Chat c where c.emergencyAction.id = :emergencyId " +
                "order by c.datetime asc");
        query.setParameter("emergencyId", emergencyId);
        return query.getResultList();
    }

    public Long countChatMessages(Long emergencyId) {
        Query query = manager.createQuery("select count(c) from Chat c where c.emergencyAction.id = :emergencyId ");
        query.setParameter("emergencyId", emergencyId);
        return (Long) query.getSingleResult();
    }

    public int countAll(long contractId, String filterYear) {
        String sql = "SELECT count(e) FROM EmergencyAction e " +
                "where e.contract.id = :contractId and e.deleted = false ";

        //apply filters
        if (filterYear != null) {
            sql += "and (year(e.startDate) = :filterYear or year(e.closedDate) = :filterYear) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (filterYear != null) {
            query.setParameter("filterYear", Integer.parseInt(filterYear));
        }
        return ((Long) (query.getSingleResult())).intValue();
    }

    @SuppressWarnings({"unchecked"})
    public List<EmergencyAction> findAll(long contractId, int currentPage, int resultPerPage, String sortCriteria,
                                         String sortDirection, String filterYear, Boolean isOpen) {
        if (sortCriteria.equals("closed")) {
            sortCriteria = "closedDate";
        }
        String sql = "SELECT e FROM EmergencyAction e join fetch e.changedBy " +
                "where e.contract.id = :contractId and e.deleted = false ";

        //apply filters
        if (filterYear != null) {
            sql += "and (year(e.startDate) = :filterYear or year(e.closedDate) = :filterYear) ";
        }
        if (isOpen != null) {
            if (isOpen) {
                sql += "and (e.closedDate is null or e.closedDate > :currentDate) ";
            } else {
                sql += "and (e.closedDate is not null and e.closedDate <= :currentDate) ";
            }
        }

        sql += "order by e." + sortCriteria + " " + sortDirection + ", e.id " + sortDirection;

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (isOpen != null) {
            query.setParameter("currentDate", new Date());
        }
        if (filterYear != null) {
            query.setParameter("filterYear", Integer.parseInt(filterYear));
        }
        query.setFirstResult(currentPage);
        query.setMaxResults(resultPerPage);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<EmergencyAction> findAll(long contractId, String filterYear, String filterSemester, Boolean isOpen) {
        String sql = "SELECT e FROM EmergencyAction e join fetch e.changedBy " +
                "where e.contract.id = :contractId and e.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (e.closedDate is null or e.closedDate > :currentDate) ";
            } else {
                sql += "and (e.closedDate is not null and e.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null && filterSemester != null) {
            sql += "and ((cast(extract(year from e.startDate) as integer) = :filterYear " +
                    "and (((cast(extract(month from e.startDate) as integer)-1)/6)+1) = :filterSemester) " +
                    "or (cast(extract(year from e.closedDate) as integer) = :filterYear " +
                    "and (((cast(extract(month from e.closedDate) as integer)-1)/6)+1) = :filterSemester)) ";
        } else if (filterYear != null) {
            sql += "and (cast(extract(year from e.startDate) as integer) = :filterYear " +
                    "or cast(extract(year from e.closedDate) as integer) = :filterYear) ";
        }
        sql += "order by e.startDate desc, e.id desc";

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (isOpen != null) {
            query.setParameter("currentDate", new Date());
        }
        if (filterYear != null) {
            query.setParameter("filterYear", Integer.parseInt(filterYear));
            if (filterSemester != null) {
                query.setParameter("filterSemester", Integer.parseInt(filterSemester));
            }
        }
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<EmergencyAction> findOpenEmergencyActions(Long contractId) {
        String sql = "select e from EmergencyAction e " +
                "where e.contract.id = :contractId and e.deleted = false " +
                "and (e.closedDate is null or e.closedDate > :currentDate) " +
                "order by e.id asc";

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        query.setParameter("currentDate", new Date());
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<String> findEmergencyActionYears(Long contractId) {
        Query query = manager.createNativeQuery("SELECT DISTINCT cast(years AS VARCHAR) " +
                "FROM " +
                "  (SELECT extract(YEAR FROM startdate) AS years " +
                "   FROM sm_emergencyaction " +
                "   WHERE contract_id = ?1 AND startdate IS NOT NULL AND deleted = FALSE " +
                "   UNION ALL " +
                "   SELECT extract(YEAR FROM closeddate) AS years " +
                "   FROM sm_emergencyaction " +
                "   WHERE contract_id = ?1 AND closeddate IS NOT NULL AND deleted = FALSE " +
                "  ) x " +
                "ORDER BY 1 DESC");
        query.setParameter(1, contractId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<EmergencyUser> findEmergencyUsers(Long contractId) {
        Query query = manager.createQuery("select eu from EmergencyUser eu " +
                "where eu.contract.id = :contractId and (eu.deleted is null or eu.deleted = false) ");
        query.setParameter("contractId", contractId);
        return query.getResultList();
    }

    public boolean hasEmergencyUsers(Long contractId) {
        Query query = manager.createQuery("select count(eu) from EmergencyUser eu " +
                "where eu.contract.id = :contractId and (eu.deleted is null or eu.deleted = false) ");
        query.setParameter("contractId", contractId);
        return (Long) query.getSingleResult() != 0;
    }

    public EmergencyUser insertEmergencyUser(EmergencyUser emergencyUser) {
        manager.persist(emergencyUser);
        return emergencyUser;
    }

    public int deleteEmergencyUser(Long emergencyUserId) {
        Query query = manager.createQuery("update EmergencyUser eu set eu.deleted = true " +
                "where eu.id = :emergencyUserId");
        query.setParameter("emergencyUserId", emergencyUserId);
        return query.executeUpdate();
    }

    public EmergencyUser findEmergencyUser(Long emergencyUserId) {
        Query query = manager.createQuery("select eu from EmergencyUser eu " +
                "where eu.id = :emergencyUserId and (eu.deleted is null or eu.deleted = false) ");
        query.setParameter("emergencyUserId", emergencyUserId);
        try {
            return (EmergencyUser) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public EmergencyToken findEmergencyToken(Long emergencyId, String token) {
        Query query = manager.createQuery("select et from EmergencyToken et " +
                "join et.emergencyAction e " +
                "where e.id = :emergencyId and e.deleted = false and e.closedDate is null " +
                "and et.accessToken = :token");
        query.setParameter("emergencyId", emergencyId);
        query.setParameter("token", token);
        try {
            return (EmergencyToken) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public EmergencyToken insertEmergencyToken(EmergencyToken emergencyToken) {
        manager.persist(emergencyToken);
        return emergencyToken;
    }

    public int deleteEmergencyActionTokens(Long emergencyId) {
        Query query = manager.createQuery("delete from EmergencyToken et " +
                "where et.emergencyAction.id = :emergencyId");
        query.setParameter("emergencyId", emergencyId);
        return query.executeUpdate();
    }

    public User findEmergencyActionCreatedByUser(Long emergencyId) {
        Query query = manager.createQuery("select e.createdBy from EmergencyAction e " +
                "where e.id = :emergencyId and e.deleted = false");
        query.setParameter("emergencyId", emergencyId);
        try {
            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public int deleteEmergencyUserTokens(Long emergencyUserId) {
        Query query = manager.createQuery("delete from EmergencyToken et " +
                "where et.emergencyUser.id = :emergencyUserId");
        query.setParameter("emergencyUserId", emergencyUserId);
        return query.executeUpdate();
    }

    public boolean existsEmergencyUserByEmailAndContract(Long contractId, String email) {
        Query query = manager.createQuery("select count(eu) from EmergencyUser eu " +
                "where eu.contract.id = :contractId and UPPER(eu.email) = :email " +
                "and (eu.deleted is null or eu.deleted = false) ");
        query.setParameter("contractId", contractId);
        query.setParameter("email", email.toUpperCase());
        return (Long) (query.getSingleResult()) != 0;
    }

    @SuppressWarnings("unchecked")
    public List<EmergencyAction> findEmergencyActionsWithChatsAfterDate(Date date) {
        Query query = manager.createQuery("select distinct e from EmergencyAction e " +
                "left join fetch e.chatMessages c " +
                "where c.datetime > :initialDate and e.deleted = false");
        query.setParameter("initialDate", date);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Long> findEmergencyActionsIds(long contractId, String filterYear, Boolean isOpen) {
        String sql = "select e.id from EmergencyAction e " +
                "where e.contract.id = :contractId and e.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (e.closedDate is null or e.closedDate > :currentDate) ";
            } else {
                sql += "and (e.closedDate is not null and e.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and ('" + filterYear + "' is year(e.startDate) or  '" + filterYear + "' is year(e.closedDate)) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (isOpen != null) {
            query.setParameter("currentDate", new Date());
        }
        return query.getResultList();
    }

    public int deleteEmergencyActions(List<Long> ids, User loggedUser) {
        Query query = manager.createQuery("update EmergencyAction e " +
                "set e.deleted = true, e.changedDate = :currentDate, e.changedBy = :loggedUser " +
                "where e.id in (:ids)");
        query.setParameter("ids", ids);
        query.setParameter("currentDate", new Date());
        query.setParameter("loggedUser", loggedUser);
        return query.executeUpdate();
    }

    public int deleteEmergencyActionTokens(List<Long> ids) {
        Query query = manager.createQuery("delete from EmergencyToken et " +
                "where et.emergencyAction.id in (:ids)");
        query.setParameter("ids", ids);
        return query.executeUpdate();
    }
}
