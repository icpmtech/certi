package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.Risk;
import com.criticalsoftware.certitools.entities.sm.SecurityImpactWork;
import com.criticalsoftware.certitools.entities.sm.enums.WorkType;
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
 * Work DAO EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(WorkDAO.class)
@LocalBinding(jndiBinding = "certitools/WorkDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class WorkDAOEJB extends GenericDAOEJB<SecurityImpactWork, Long> implements WorkDAO {

    private static final Logger LOGGER = Logger.getInstance(WorkDAOEJB.class);

    public <T> T getEntityReference(Class<T> clasz, Long id) {
        return manager.getReference(clasz, id);
    }

    @SuppressWarnings({"unchecked"})
    public List<Risk> findRisks() {
        Query query = manager.createQuery("select r from Risk r");
        return query.getResultList();
    }

    public Risk findRisk(Long riskId) {
        Query query = manager.createQuery("select r from Risk r where r.id = :riskId");
        query.setParameter("riskId", riskId);
        try {
            return (Risk) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public SecurityImpactWork findSecurityImpactWork(Long workId) {
        Query query = manager.createQuery("select w from SecurityImpactWork w where w.id = :workId and w.deleted = false");
        query.setParameter("workId", workId);
        try {
            return (SecurityImpactWork) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public SecurityImpactWork findSecurityImpactWorkWithContract(Long workId) {
        Query query = manager.createQuery("select w from SecurityImpactWork w " +
                "left join fetch w.changedBy " +
                "left join fetch w.contract ct " +
                "left join fetch ct.company " +
                "where w.id = :workId and w.deleted = false");
        query.setParameter("workId", workId);
        try {
            return (SecurityImpactWork) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> findDocuments(Long workId) {
        Query query = manager.createQuery("select new com.criticalsoftware.certitools.entities.sm.Document(" +
                "d.id, d.displayName, d.name, d.contentType, d.contentLength" +
                ") from Document d where d.securityImpactWork.id = :workId ");
        query.setParameter("workId", workId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> findDocumentsWithContent(Long workId) {
        Query query = manager.createNativeQuery("SELECT id, displayname, name, contenttype, contentlength, content " +
                "FROM sm_document d WHERE d.securityimpactwork_id = ?1");
        query.setParameter(1, workId);
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
    public List<CorrectiveAction> findCorrectiveActions(Long workId) {
        Query query = manager.createQuery("select c from CorrectiveAction c " +
                "where c.securityImpactWork.id = :workId and c.deleted = false");
        query.setParameter("workId", workId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<CorrectiveAction> findOpenCorrectiveActions(Long workId) {
        Query query = manager.createQuery("select c from CorrectiveAction c " +
                "where c.securityImpactWork.id = :workId and c.deleted = false " +
                "and (c.closedDate is null or c.closedDate > :currentDate) ");
        query.setParameter("workId", workId);
        query.setParameter("currentDate", new Date());
        return query.getResultList();
    }

    public boolean hasOpenCorrectiveActions(Long workId) {
        Query query = manager.createQuery("select count(c) from CorrectiveAction c " +
                "where c.securityImpactWork.id = :workId and c.deleted = false " +
                "and (c.closedDate is null or c.closedDate > :currentDate) ");
        query.setParameter("workId", workId);
        query.setParameter("currentDate", new Date());
        return (Long) (query.getSingleResult()) != 0;
    }

    @SuppressWarnings({"unchecked"})
    public List<Risk> findSecurityImpactWorkRisks(Long workId) {
        Query query = manager.createQuery("select r from Risk r left join r.securityImpactWorks w " +
                "where w.id = :workId and w.deleted = false ");
        query.setParameter("workId", workId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Chat> findChatMessages(Long workId) {
        Query query = manager.createQuery("select c from Chat c where c.securityImpactWork.id = :workId ");
        query.setParameter("workId", workId);
        return query.getResultList();
    }

    public Long countChatMessages(Long workId) {
        Query query = manager.createQuery("select count(c) from Chat c where c.securityImpactWork.id = :workId ");
        query.setParameter("workId", workId);
        return (Long) query.getSingleResult();
    }

    public int countOpenSecurityImpactWorks(long contractId) {
        Query query = manager.createQuery("SELECT count(w) FROM SecurityImpactWork w " +
                "where w.contract.id = :contractId and w.deleted = false " +
                "and (w.closedDate is null or w.closedDate > :currentDate) ");
        query.setParameter("contractId", contractId);
        query.setParameter("currentDate", new Date());
        return ((Long) (query.getSingleResult())).intValue();
    }

    public int countAll(long contractId, WorkType workType, String filterYear, Boolean isOpen) {
        String sql = "SELECT count(w) FROM SecurityImpactWork w " +
                "where w.contract.id = :contractId and w.deleted = false ";

        //apply filters
        if (workType != null) {
            sql += "and w.workType = :workType ";
        }
        if (isOpen != null) {
            if (isOpen) {
                sql += "and (w.closedDate is null or w.closedDate > :currentDate) ";
            } else {
                sql += "and (w.closedDate is not null and w.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and (year(w.startDate) = :filterYear or year(w.closedDate) = :filterYear) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (workType != null) {
            query.setParameter("workType", workType);
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
    public List<SecurityImpactWork> findAll(long contractId, int currentPage, int resultPerPage, String sortCriteria,
                                            String sortDirection, WorkType workType, String filterYear, Boolean isOpen) {
        if (sortCriteria.equals("closed")) {
            sortCriteria = "closedDate";
        }
        String sql = "SELECT w FROM SecurityImpactWork w join fetch w.changedBy " +
                "where w.contract.id = :contractId and w.deleted = false ";

        //apply filters
        if (workType != null) {
            sql += "and w.workType = :workType ";
        }
        if (isOpen != null) {
            if (isOpen) {
                sql += "and (w.closedDate is null or w.closedDate > :currentDate) ";
            } else {
                sql += "and (w.closedDate is not null and w.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and (year(w.startDate) = :filterYear or year(w.closedDate) = :filterYear) ";
        }

        sql += "order by w." + sortCriteria + " " + sortDirection + ", w.id " + sortDirection;

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (workType != null) {
            query.setParameter("workType", workType);
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
    public List<SecurityImpactWork> findAll(long contractId, String filterYear, String filterSemester, Boolean isOpen) {
        String sql = "SELECT w FROM SecurityImpactWork w join fetch w.changedBy " +
                "where w.contract.id = :contractId and w.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (w.closedDate is null or w.closedDate > :currentDate) ";
            } else {
                sql += "and (w.closedDate is not null and w.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null && filterSemester != null) {
            sql += "and ((cast(extract(year from w.startDate) as integer) = :filterYear " +
                    "and (((cast(extract(month from w.startDate) as integer)-1)/6)+1) = :filterSemester) " +
                    "or (cast(extract(year from w.closedDate) as integer) = :filterYear " +
                    "and (((cast(extract(month from w.closedDate) as integer)-1)/6)+1) = :filterSemester)) ";
        } else if (filterYear != null) {
            sql += "and (cast(extract(year from w.startDate) as integer) = :filterYear " +
                    "or cast(extract(year from w.closedDate) as integer) = :filterYear) ";
        }
        sql += "order by w.startDate desc, w.id desc";

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
    public List<String> findSecurityImpactWorkYears(Long contractId) {
        Query query = manager.createNativeQuery("SELECT DISTINCT cast(years AS VARCHAR) " +
                "FROM " +
                "  (SELECT extract(YEAR FROM startdate) AS years " +
                "   FROM sm_securityimpactwork " +
                "   WHERE contract_id = ?1 AND startdate IS NOT NULL AND deleted = FALSE " +
                "   UNION ALL " +
                "   SELECT extract(YEAR FROM closeddate) AS years " +
                "   FROM sm_securityimpactwork " +
                "   WHERE contract_id = ?1 AND closeddate IS NOT NULL AND deleted = FALSE " +
                "  ) x " +
                "ORDER BY 1 DESC");
        query.setParameter(1, contractId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<SecurityImpactWork> findSecurityImpactWorksWithChatsAfterDate(Date date) {
        Query query = manager.createQuery("select distinct w from SecurityImpactWork w " +
                "left join fetch w.chatMessages c " +
                "where c.datetime > :initialDate and w.deleted = false");
        query.setParameter("initialDate", date);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Long> findSecurityImpactWorksIds(long contractId, String filterYear, Boolean isOpen) {
        String sql = "SELECT w.id FROM SecurityImpactWork w  " +
                "where w.contract.id = :contractId and w.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (w.closedDate is null or w.closedDate > :currentDate) ";
            } else {
                sql += "and (w.closedDate is not null and w.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and ('" + filterYear + "' is year(w.startDate) or  '" + filterYear + "' is year(w.closedDate)) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (isOpen != null) {
            query.setParameter("currentDate", new Date());
        }
        return query.getResultList();
    }

    public int deleteSecurityImpactWorks(List<Long> ids, User loggedUser) {
        Query query = manager.createQuery("update SecurityImpactWork w " +
                "set w.deleted = true, w.changedDate = :currentDate, w.changedBy = :loggedUser " +
                "where w.id in (:ids)");
        query.setParameter("ids", ids);
        query.setParameter("currentDate", new Date());
        query.setParameter("loggedUser", loggedUser);
        return query.executeUpdate();
    }

    public int deleteSecurityImpactWorksDocuments(List<Long> ids) {
        Query query = manager.createQuery("delete from Document d where d.securityImpactWork.id in (:ids)");
        query.setParameter("ids", ids);
        return query.executeUpdate();
    }

    public int deleteSecurityImpactWorkDocuments(Long workId) {
        Query query = manager.createQuery("delete from Document d where d.securityImpactWork.id = :workId");
        query.setParameter("workId", workId);
        return query.executeUpdate();
    }
}
