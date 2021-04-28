package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Corrective Action DAO EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(CorrectiveActionDAO.class)
@LocalBinding(jndiBinding = "certitools/CorrectiveActionDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CorrectiveActionDAOEJB extends GenericDAOEJB<CorrectiveAction, Long> implements CorrectiveActionDAO {

    private static final Logger LOGGER = Logger.getInstance(CorrectiveActionDAOEJB.class);

    public <T> T getEntityReference(Class<T> clasz, Long id) {
        return manager.getReference(clasz, id);
    }

    public CorrectiveAction findCorrectiveAction(Long actionId) {
        Query query = manager.createQuery("select a from CorrectiveAction a " +
                "where a.id = :actionId and a.deleted = false");
        query.setParameter("actionId", actionId);
        try {
            return (CorrectiveAction) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CorrectiveAction findCorrectiveActionComplete(Long actionId) {
        Query query = manager.createQuery("select distinct a from CorrectiveAction a " +
                "left join fetch a.contract c " +
                "left join fetch c.company " +
                "left join fetch a.activity " +
                "left join fetch a.anomaly " +
                "left join fetch a.securityImpactWork " +
                "left join fetch a.maintenance " +
                "left join fetch a.changedBy " +
                "where a.id = :actionId and a.deleted = false");
        query.setParameter("actionId", actionId);
        try {
            return (CorrectiveAction) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CorrectiveAction findCorrectiveActionWithContract(Long actionId) {
        Query query = manager.createQuery("select a from CorrectiveAction a " +
                "left join fetch a.contract ct " +
                "left join fetch ct.company " +
                "where a.id = :actionId and a.deleted = false");
        query.setParameter("actionId", actionId);
        try {
            return (CorrectiveAction) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<Chat> findCorrectiveActionChatMessages(Long actionId) {
        Query query = manager.createQuery("select c from Chat c where c.correctiveAction.id = :actionId ");
        query.setParameter("actionId", actionId);
        return query.getResultList();
    }

    public int countOpenCorrectiveAction(long contractId) {
        Query query = manager.createQuery("SELECT count(a) FROM CorrectiveAction a " +
                "where a.contract.id = :contractId and a.deleted = false " +
                "and (a.closedDate is null or a.closedDate > :currentDate) ");
        query.setParameter("contractId", contractId);
        query.setParameter("currentDate", new Date());
        return ((Long) (query.getSingleResult())).intValue();
    }

    public int countAll(Long contractId, String filterYear, Boolean isOpen) {
        String sql = "SELECT count(a) FROM CorrectiveAction a " +
                "where a.contract.id = :contractId and a.deleted = false ";

        //apply filters
        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and (year(a.startDate) = :filterYear or year(a.closedDate) = :filterYear) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (isOpen != null) {
            query.setParameter("currentDate", new Date());
        }
        if (filterYear != null) {
            query.setParameter("filterYear", Integer.parseInt(filterYear));
        }
        return ((Long) (query.getSingleResult())).intValue();
    }

    @SuppressWarnings({"unchecked"})
    public List<CorrectiveAction> findAll(Long contractId, int currentPage, int resultPerPage, String sortCriteria,
                                          String sortDirection, String filterYear, Boolean isOpen) {
        if (sortCriteria.equals("closed")) {
            sortCriteria = "closedDate";
        }
        String sql = "select a from CorrectiveAction a " +
                "where a.contract.id = :contractId and a.deleted = false ";

        //apply filters
        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and (year(a.startDate) = :filterYear or year(a.closedDate) = :filterYear) ";
        }

        sql += "order by a." + sortCriteria + " " + sortDirection + ", a.id " + sortDirection;

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
    public List<CorrectiveAction> findAllComplete(Long contractId, String filterYear, String filterSemester, Boolean isOpen) {
        String sql = "select a from CorrectiveAction a " +
                "left join fetch a.activity " +
                "left join fetch a.anomaly " +
                "left join fetch a.securityImpactWork " +
                "left join fetch a.maintenance " +
                "left join fetch a.changedBy " +
                "where a.contract.id = :contractId and a.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null && filterSemester != null) {
            sql += "and ((cast(extract(year from a.startDate) as integer) = :filterYear " +
                    "and (((cast(extract(month from a.startDate) as integer)-1)/6)+1) = :filterSemester) " +
                    "or (cast(extract(year from a.closedDate) as integer) = :filterYear " +
                    "and (((cast(extract(month from a.closedDate) as integer)-1)/6)+1) = :filterSemester)) ";
        } else if (filterYear != null) {
            sql += "and (cast(extract(year from a.startDate) as integer) = :filterYear " +
                    "or cast(extract(year from a.closedDate) as integer) = :filterYear) ";
        }
        sql += "order by a.startDate desc, a.id desc";

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

    @SuppressWarnings({"unchecked"})
    public List<String> findCorrectiveActionYears(Long contractId) {
        Query query = manager.createNativeQuery("SELECT DISTINCT cast(years AS VARCHAR) " +
                "FROM " +
                "  (SELECT extract(YEAR FROM startdate) AS years " +
                "   FROM sm_correctiveaction " +
                "   WHERE contract_id = ?1 AND startdate IS NOT NULL AND deleted = FALSE " +
                "   UNION ALL " +
                "   SELECT extract(YEAR FROM closeddate) AS years " +
                "   FROM sm_correctiveaction " +
                "   WHERE contract_id = ?1 AND closeddate IS NOT NULL AND deleted = FALSE " +
                "  ) x " +
                "ORDER BY 1 DESC");
        query.setParameter(1, contractId);
        return query.getResultList();
    }

    public Long countCorrectiveActionChatMessages(Long actionId) {
        Query query = manager.createQuery("select count(c) from Chat c where c.correctiveAction.id = :actionId ");
        query.setParameter("actionId", actionId);
        return (Long) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<CorrectiveAction> findCorrectiveActionsWithChatsAfterDate(Date date) {
        Query query = manager.createQuery("select distinct a from CorrectiveAction a " +
                "left join fetch a.chatMessages c " +
                "where c.datetime > :initialDate and a.deleted = false");
        query.setParameter("initialDate", date);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> findDocuments(Long actionId) {
        Query query = manager.createQuery("select new com.criticalsoftware.certitools.entities.sm.Document(" +
                "d.id, d.displayName, d.name, d.contentType, d.contentLength" +
                ") from Document d where d.correctiveAction.id = :actionId ");
        query.setParameter("actionId", actionId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> findDocumentsWithContent(Long actionId) {
        Query query = manager.createNativeQuery("SELECT id, displayname, name, contenttype, contentlength, content " +
                "FROM sm_document d WHERE d.correctiveaction_id = ?1");
        query.setParameter(1, actionId);
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
    public List<Long> findCorrectiveActionsIds(long contractId, String filterYear, Boolean isOpen) {
        String sql = "select a.id from CorrectiveAction a " +
                "where a.contract.id = :contractId and a.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and ('" + filterYear + "' is year(a.startDate) or  '" + filterYear + "' is year(a.closedDate)) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (isOpen != null) {
            query.setParameter("currentDate", new Date());
        }
        return query.getResultList();
    }

    public int deleteCorrectiveActions(List<Long> ids, User loggedUser) {
        Query query = manager.createQuery("update CorrectiveAction a " +
                "set a.deleted = true, a.changedDate = :currentDate, a.changedBy = :loggedUser " +
                "where a.id in (:ids)");
        query.setParameter("ids", ids);
        query.setParameter("currentDate", new Date());
        query.setParameter("loggedUser", loggedUser);
        return query.executeUpdate();
    }

    public int deleteCorrectiveActionsDocuments(List<Long> ids) {
        Query query = manager.createQuery("delete from Document d where d.correctiveAction.id in (:ids)");
        query.setParameter("ids", ids);
        return query.executeUpdate();
    }

    public int deleteCorrectiveActionDocuments(Long actionId) {
        Query query = manager.createQuery("delete from Document d where d.correctiveAction.id = :actionId");
        query.setParameter("actionId", actionId);
        return query.executeUpdate();
    }
}
