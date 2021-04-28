package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Activity;
import com.criticalsoftware.certitools.entities.sm.ActivityType;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import com.criticalsoftware.certitools.persistence.sm.dto.UpcomingEvent;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(ActivityDAO.class)
@LocalBinding(jndiBinding = "certitools/ActivityDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ActivityDAOEJB extends GenericDAOEJB<Activity, Long> implements ActivityDAO {

    private static final Logger LOGGER = Logger.getInstance(ActivityDAOEJB.class);

    @SuppressWarnings({"unchecked"})
    public List<UpcomingEvent> findUpcomingEvents(Long contractId) {
        Query query = manager.createQuery("select new com.criticalsoftware.certitools.persistence.sm.dto.UpcomingEvent(" +
                "a.id, a.name, a.dateScheduled, 'ACTIVITY') " +
                "from Activity a " +
                "where a.dateScheduled >= CURRENT_DATE and a.contract.id = :contractId and a.deleted = false " +
                "order by a.dateScheduled ASC");
        query.setParameter("contractId", contractId);
        query.setMaxResults(5);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<ActivityType> findActivityTypes(long contractId) {
        Query query = manager.createQuery("select at from ActivityType at " +
                "where at.contract is null or at.contract.id = :contractId");
        query.setParameter("contractId", contractId);
        return query.getResultList();
    }

    public ActivityType findActivityType(Long typeId) {
        Query query = manager.createQuery("select at from ActivityType at " +
                "where at.id = :typeId");
        query.setParameter("typeId", typeId);
        try {
            return (ActivityType) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ActivityType insertActivityType(ActivityType activityType) {
        manager.persist(activityType);
        return activityType;
    }

    public Activity getLatestActivityByRecurrence(Long recurrenceId) {
        Query query = manager.createQuery("select a from Activity a left join fetch a.contract " +
                "left join fetch a.activityType " +
                "where a.recurrence.id = :recurrenceId and a.deleted = false " +
                "order by a.creationDate desc");
        query.setParameter("recurrenceId", recurrenceId);
        query.setMaxResults(1);
        try {
            return (Activity) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean isLatestActivityByRecurrence(Long recurrenceId, Long activityId) {
        Query query = manager.createQuery("select a.id from Activity a " +
                "where a.recurrence.id = :recurrenceId and a.deleted = false " +
                "order by a.creationDate desc");
        query.setParameter("recurrenceId", recurrenceId);
        query.setMaxResults(1);
        try {
            return query.getSingleResult().equals(activityId);
        } catch (NoResultException e) {
            return false;
        }
    }

    public Activity findActivity(Long activityId) {
        Query query = manager.createQuery("select a from Activity a where a.id = :activityId and a.deleted = false");
        query.setParameter("activityId", activityId);
        try {
            return (Activity) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Activity findActivityWithRecurrence(Long activityId) {
        Query query = manager.createQuery("select distinct a from Activity a " +
                "left join fetch a.recurrence r " +
                "left join fetch r.notifications " +
                "where a.id = :activityId and a.deleted = false");
        query.setParameter("activityId", activityId);
        try {
            return (Activity) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Activity findActivityWithContract(Long activityId) {
        Query query = manager.createQuery("select a from Activity a " +
                "left join fetch a.contract ct " +
                "left join fetch ct.company " +
                "where a.id = :activityId and a.deleted = false");
        query.setParameter("activityId", activityId);
        try {
            return (Activity) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> findActivityDocuments(Long activityId) {
        Query query = manager.createQuery("select new com.criticalsoftware.certitools.entities.sm.Document(" +
                "d.id, d.displayName, d.name, d.contentType, d.contentLength" +
                ") from Document d where d.activity.id = :activityId ");
        query.setParameter("activityId", activityId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> findDocumentsWithContent(Long activityId) {
        Query query = manager.createNativeQuery("SELECT id, displayname, name, contenttype, contentlength, content " +
                "FROM sm_document d WHERE d.activity_id = ?1");
        query.setParameter(1, activityId);
        List<Document> documents = new ArrayList<Document>();
        List<Object[]> resultList = query.getResultList();
        for (Object[] object : resultList) {
            Document d = new Document();
            d.setId(((BigInteger) object[0]).longValue());
            d.setDisplayName((String) object[1]);
            d.setName((String) object[2]);
            d.setContentType((String) object[3]);
            d.setContentLength((Integer) object[4]);
            d.setContent((byte[]) object[5]);
            documents.add(d);
        }
        return documents;
    }

    @SuppressWarnings({"unchecked"})
    public List<Chat> findActivityChatMessages(Long activityId) {
        Query query = manager.createQuery("select c from Chat c where c.activity.id = :activityId ");
        query.setParameter("activityId", activityId);
        return query.getResultList();
    }

    public int countOpenActivity(long contractId) {
        Query query = manager.createQuery("SELECT count(a) FROM Activity a " +
                "where a.contract.id = :contractId and a.deleted = false " +
                "and (a.closedDate is null or a.closedDate > :currentDate) ");
        query.setParameter("contractId", contractId);
        query.setParameter("currentDate", new Date());
        return ((Long) (query.getSingleResult())).intValue();
    }

    public int countAll(long contractId, Long activityTypeId, String filterYear, Boolean isOpen) {
        String sql = "SELECT count(a) FROM Activity a " +
                "where a.contract.id = :contractId and a.deleted = false ";

        //apply filters
        if (activityTypeId != null) {
            sql += "and a.activityType.id = :activityId ";
        }
        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and (year(a.dateScheduled) = :filterYear or year(a.closedDate) = :filterYear) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (activityTypeId != null) {
            query.setParameter("activityId", activityTypeId);
        }
        if (isOpen != null) {
            query.setParameter("currentDate", new Date());
        }
        if (filterYear != null) {
            query.setParameter("filterYear", Integer.parseInt(filterYear));
        }
        return ((Long) (query.getSingleResult())).intValue();
    }

    @SuppressWarnings({"unchecked"})
    public List<Activity> findAll(long contractId, int currentPage, int resultPerPage, String sortCriteria,
                                  String sortDirection, Long activityTypeId, String filterYear, Boolean isOpen) {
        if (sortCriteria.equals("closed")) {
            sortCriteria = "closedDate";
        }
        String sql = "select a from Activity a join fetch a.changedBy " +
                "where a.contract.id = :contractId and a.deleted = false ";

        //apply filters
        if (activityTypeId != null) {
            sql += "and a.activityType.id = :activityTypeId ";
        }
        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and (year(a.dateScheduled) = :filterYear or year(a.closedDate) = :filterYear) ";
        }

        sql += "order by a." + sortCriteria + " " + sortDirection + ", a.id " + sortDirection;

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (activityTypeId != null) {
            query.setParameter("activityTypeId", activityTypeId);
        }
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
    public List<Activity> findAll(long contractId, String filterYear, String filterSemester, Boolean isOpen) {
        String sql = "select a from Activity a join fetch a.changedBy " +
                "where a.contract.id = :contractId and a.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null && filterSemester != null) {
            sql += "and ((cast(extract(year from a.dateScheduled) as integer) = :filterYear " +
                    "and (((cast(extract(month from a.dateScheduled) as integer)-1)/6)+1) = :filterSemester) " +
                    "or (cast(extract(year from a.closedDate) as integer) = :filterYear " +
                    "and (((cast(extract(month from a.closedDate) as integer)-1)/6)+1) = :filterSemester)) ";
        } else if (filterYear != null) {
            sql += "and (cast(extract(year from a.dateScheduled) as integer) = :filterYear " +
                    "or cast(extract(year from a.closedDate) as integer) = :filterYear) ";
        }
        sql += "order by a.activityType.name asc, a.id asc";

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

    public boolean hasOpenCorrectiveActions(Long activityId) {
        Query query = manager.createQuery("select count(c) from CorrectiveAction c " +
                "where c.activity.id = :activityId and c.deleted = false " +
                "and (c.closedDate is null or c.closedDate > :currentDate)");
        query.setParameter("activityId", activityId);
        query.setParameter("currentDate", new Date());
        return (Long) (query.getSingleResult()) != 0;
    }

    @SuppressWarnings({"unchecked"})
    public List<String> findActivityYears(Long contractId) {
        Query query = manager.createNativeQuery("SELECT DISTINCT cast(years AS VARCHAR) " +
                "FROM " +
                "  (SELECT extract(YEAR FROM datescheduled) AS years " +
                "   FROM sm_activity " +
                "   WHERE contract_id = ?1 AND datescheduled IS NOT NULL AND deleted = FALSE " +
                "   UNION ALL " +
                "   SELECT extract(YEAR FROM closeddate) AS years " +
                "   FROM sm_activity " +
                "   WHERE contract_id = ?1 AND closeddate IS NOT NULL AND deleted = FALSE " +
                "  ) x " +
                "ORDER BY 1 DESC");
        query.setParameter(1, contractId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<CorrectiveAction> findActivityCorrectiveActions(Long activityId) {
        Query query = manager.createQuery("select c from CorrectiveAction c " +
                "where c.activity.id = :activityId and c.deleted = false");
        query.setParameter("activityId", activityId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<CorrectiveAction> findActivityOpenCorrectiveActions(Long activityId) {
        Query query = manager.createQuery("select c from CorrectiveAction c " +
                "where c.activity.id = :activityId and c.deleted = false " +
                "and (c.closedDate is null or c.closedDate > :currentDate) ");
        query.setParameter("activityId", activityId);
        query.setParameter("currentDate", new Date());
        return query.getResultList();
    }

    public Long countActivityChatMessages(Long activityId) {
        Query query = manager.createQuery("select count(c) from Chat c where c.activity.id = :activityId ");
        query.setParameter("activityId", activityId);
        return (Long) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Activity> findActivitiesWithChatsAfterDate(Date date) {
        Query query = manager.createQuery("select distinct a from Activity a " +
                "left join fetch a.chatMessages c " +
                "where c.datetime > :initialDate and a.deleted = false");
        query.setParameter("initialDate", date);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Long> findActivitiesIds(long contractId, String filterYear, Boolean isOpen) {
        String sql = "select a.id from Activity a " +
                "where a.contract.id = :contractId and a.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and ('" + filterYear + "' is year(a.dateScheduled) or  '" + filterYear + "' is year(a.closedDate)) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (isOpen != null) {
            query.setParameter("currentDate", new Date());
        }
        return query.getResultList();
    }

    public int deleteActivities(List<Long> ids, User loggedUser) {
        Query query = manager.createQuery("update Activity a " +
                "set a.deleted = true, a.changedDate = :currentDate, a.changedBy = :loggedUser " +
                "where a.id in (:ids)");
        query.setParameter("ids", ids);
        query.setParameter("currentDate", new Date());
        query.setParameter("loggedUser", loggedUser);
        return query.executeUpdate();
    }

    public int deleteActivitiesDocuments(List<Long> ids) {
        Query query = manager.createQuery("delete from Document d where d.activity.id in (:ids)");
        query.setParameter("ids", ids);
        return query.executeUpdate();
    }

    public int deleteActivityDocuments(Long activityId) {
        Query query = manager.createQuery("delete from Document d where d.activity.id = :activityId");
        query.setParameter("activityId", activityId);
        return query.executeUpdate();
    }

    public int deactivateActivitiesRecurrences(List<Long> ids) {
        Query query = manager.createNativeQuery("UPDATE sm_recurrence " +
                "SET active = FALSE " +
                "WHERE active = TRUE " +
                "AND (SELECT sm_activity.id " +
                "   FROM sm_activity " +
                "   WHERE sm_recurrence.id = sm_activity.recurrence_id " +
                "   ORDER BY sm_activity.creationdate DESC LIMIT 1) " +
                " IN (?1)");

        query.setParameter(1, ids);
        return query.executeUpdate();
    }
}
