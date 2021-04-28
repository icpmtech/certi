package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Anomaly;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.SecurityImpact;
import com.criticalsoftware.certitools.entities.sm.enums.AnomalyType;
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
 * Anomaly DAO EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(AnomalyDAO.class)
@LocalBinding(jndiBinding = "certitools/AnomalyDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AnomalyDAOEJB extends GenericDAOEJB<Anomaly, Long> implements AnomalyDAO {

    private static final Logger LOGGER = Logger.getInstance(AnomalyDAOEJB.class);

    public <T> T getEntityReference(Class<T> clasz, Long id) {
        return manager.getReference(clasz, id);
    }

    @SuppressWarnings({"unchecked"})
    public List<SecurityImpact> findSecurityImpacts() {
        Query query = manager.createQuery("select s from SecurityImpact s");
        return query.getResultList();
    }

    public SecurityImpact findSecurityImpact(Long impactId) {
        Query query = manager.createQuery("select s from SecurityImpact s where s.id = :impactId");
        query.setParameter("impactId", impactId);
        try {
            return (SecurityImpact) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Anomaly findAnomaly(Long anomalyId) {
        Query query = manager.createQuery("select a from Anomaly a where a.id = :anomalyId and a.deleted = false");
        query.setParameter("anomalyId", anomalyId);
        try {
            return (Anomaly) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Anomaly findAnomalyWithContract(Long anomalyId) {
        Query query = manager.createQuery("select a from Anomaly a " +
                "left join fetch a.changedBy " +
                "left join fetch a.contract ct " +
                "left join fetch ct.company " +
                "where a.id = :anomalyId and a.deleted = false");
        query.setParameter("anomalyId", anomalyId);
        try {
            return (Anomaly) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> findAnomalyDocuments(Long anomalyId) {
        Query query = manager.createQuery("select new com.criticalsoftware.certitools.entities.sm.Document(" +
                "d.id, d.displayName, d.name, d.contentType, d.contentLength" +
                ") from Document d where d.anomaly.id = :anomalyId ");
        query.setParameter("anomalyId", anomalyId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> findDocumentsWithContent(Long anomalyId) {
        Query query = manager.createNativeQuery("SELECT id, displayname, name, contenttype, contentlength, content " +
                "FROM sm_document d WHERE d.anomaly_id = ?1");
        query.setParameter(1, anomalyId);
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
    public List<CorrectiveAction> findAnomalyCorrectiveActions(Long anomalyId) {
        Query query = manager.createQuery("select c from CorrectiveAction c " +
                "where c.anomaly.id = :anomalyId and c.deleted = false");
        query.setParameter("anomalyId", anomalyId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<CorrectiveAction> findAnomalyOpenCorrectiveActions(Long anomalyId) {
        Query query = manager.createQuery("select c from CorrectiveAction c " +
                "where c.anomaly.id = :anomalyId and c.deleted = false " +
                "and (c.closedDate is null or c.closedDate > :currentDate) ");
        query.setParameter("anomalyId", anomalyId);
        query.setParameter("currentDate", new Date());
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Chat> findChatMessages(Long anomalyId) {
        Query query = manager.createQuery("select c from Chat c where c.anomaly.id = :anomalyId ");
        query.setParameter("anomalyId", anomalyId);
        return query.getResultList();
    }

    public int countOpenAnomalies(long contractId) {
        Query query = manager.createQuery("SELECT count(a) FROM Anomaly a " +
                "where a.contract.id = :contractId and a.deleted = false " +
                "and (a.closedDate is null or a.closedDate > :currentDate) ");
        query.setParameter("contractId", contractId);
        query.setParameter("currentDate", new Date());
        return ((Long) (query.getSingleResult())).intValue();
    }

    public int countAll(long contractId, AnomalyType anomalyType, String filterYear, Boolean isOpen) {
        String sql = "SELECT count(a) FROM Anomaly a " +
                "where a.contract.id = :contractId and a.deleted = false ";

        //apply filters
        if (anomalyType != null) {
            sql += "and a.anomalyType = :anomalyType ";
        }
        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and (year(a.datetime) = :filterYear or year(a.closedDate) = :filterYear) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (anomalyType != null) {
            query.setParameter("anomalyType", anomalyType);
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
    public List<Anomaly> findAll(long contractId, int currentPage, int resultPerPage, String sortCriteria,
                                 String sortDirection, AnomalyType anomalyType, String filterYear, Boolean isOpen) {
        if (sortCriteria.equals("closed")) {
            sortCriteria = "closedDate";
        }
        String sql = "select a from Anomaly a join fetch a.changedBy " +
                "where a.contract.id = :contractId and a.deleted = false ";

        //apply filters
        if (anomalyType != null) {
            sql += "and a.anomalyType = :anomalyType ";
        }
        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and (year(a.datetime) = :filterYear or year(a.closedDate) = :filterYear) ";
        }

        sql += "order by a." + sortCriteria + " " + sortDirection + ", a.id " + sortDirection;

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (anomalyType != null) {
            query.setParameter("anomalyType", anomalyType);
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
    public List<Anomaly> findAll(long contractId, String filterYear, String filterSemester, Boolean isOpen) {
        String sql = "select a from Anomaly a join fetch a.changedBy " +
                "where a.contract.id = :contractId and a.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null && filterSemester != null) {
            sql += "and ((cast(extract(year from a.datetime) as integer) = :filterYear " +
                    "and (((cast(extract(month from a.datetime) as integer)-1)/6)+1) = :filterSemester) " +
                    "or (cast(extract(year from a.closedDate) as integer) = :filterYear " +
                    "and (((cast(extract(month from a.closedDate) as integer)-1)/6)+1) = :filterSemester)) ";
        } else if (filterYear != null) {
            sql += "and (cast(extract(year from a.datetime) as integer) = :filterYear " +
                    "or cast(extract(year from a.closedDate) as integer) = :filterYear) ";
        }
        sql += "order by a.datetime desc, a.id desc";

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

    public boolean hasOpenCorrectiveActions(Long anomalyId) {
        Query query = manager.createQuery("select count(c) from CorrectiveAction c " +
                "where c.anomaly.id = :anomalyId and c.deleted = false " +
                "and (c.closedDate is null or c.closedDate > :currentDate)");
        query.setParameter("anomalyId", anomalyId);
        query.setParameter("currentDate", new Date());
        return (Long) (query.getSingleResult()) != 0;
    }

    @SuppressWarnings({"unchecked"})
    public List<String> findAnomalyYears(Long contractId) {
        Query query = manager.createNativeQuery("SELECT DISTINCT cast(years AS VARCHAR) " +
                "FROM " +
                "  (SELECT extract(YEAR FROM datetime) AS years " +
                "   FROM sm_anomaly " +
                "   WHERE contract_id = ?1 AND datetime IS NOT NULL AND deleted = FALSE " +
                "   UNION ALL " +
                "   SELECT extract(YEAR FROM closeddate) AS years " +
                "   FROM sm_anomaly " +
                "   WHERE contract_id = ?1 AND closeddate IS NOT NULL AND deleted = FALSE " +
                "  ) x " +
                "ORDER BY 1 DESC");
        query.setParameter(1, contractId);
        return query.getResultList();
    }

    public Long countChatMessages(Long anomalyId) {
        Query query = manager.createQuery("select count(c) from Chat c where c.anomaly.id = :anomalyId ");
        query.setParameter("anomalyId", anomalyId);
        return (Long) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Anomaly> findAnomaliesWithChatsAfterDate(Date date) {
        Query query = manager.createQuery("select distinct a from Anomaly a " +
                "left join fetch a.chatMessages c " +
                "where c.datetime > :initialDate and a.deleted = false");
        query.setParameter("initialDate", date);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Long> findAnomaliesIds(long contractId, String filterYear, Boolean isOpen) {
        String sql = "select a.id from Anomaly a " +
                "where a.contract.id = :contractId and a.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (a.closedDate is null or a.closedDate > :currentDate) ";
            } else {
                sql += "and (a.closedDate is not null and a.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and ('" + filterYear + "' is year(a.datetime) or  '" + filterYear + "' is year(a.closedDate)) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (isOpen != null) {
            query.setParameter("currentDate", new Date());
        }
        return query.getResultList();
    }

    public int deleteAnomalies(List<Long> ids, User loggedUser) {
        Query query = manager.createQuery("update Anomaly a " +
                "set a.deleted = true, a.changedDate = :currentDate, a.changedBy = :loggedUser " +
                "where a.id in (:ids)");
        query.setParameter("ids", ids);
        query.setParameter("currentDate", new Date());
        query.setParameter("loggedUser", loggedUser);
        return query.executeUpdate();
    }

    public int deleteAnomaliesDocuments(List<Long> ids) {
        Query query = manager.createQuery("delete from Document d where d.anomaly.id in (:ids)");
        query.setParameter("ids", ids);
        return query.executeUpdate();
    }

    public int deleteAnomalyDocuments(Long anomalyId) {
        Query query = manager.createQuery("delete from Document d where d.anomaly.id = :anomalyId");
        query.setParameter("anomalyId", anomalyId);
        return query.executeUpdate();
    }
}
