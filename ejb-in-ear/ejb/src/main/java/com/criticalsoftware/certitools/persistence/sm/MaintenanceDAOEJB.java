package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.Equipment;
import com.criticalsoftware.certitools.entities.sm.Maintenance;
import com.criticalsoftware.certitools.entities.sm.MaintenanceType;
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
@Local(MaintenanceDAO.class)
@LocalBinding(jndiBinding = "certitools/MaintenanceDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MaintenanceDAOEJB extends GenericDAOEJB<Maintenance, Long> implements MaintenanceDAO {

    private static final Logger LOGGER = Logger.getInstance(MaintenanceDAOEJB.class);

    public <T> T getEntityReference(Class<T> clasz, Long id) {
        return manager.getReference(clasz, id);
    }

    @SuppressWarnings({"unchecked"})
    public List<UpcomingEvent> findUpcomingEvents(Long contractId) {
        Query query = manager.createQuery("select new com.criticalsoftware.certitools.persistence.sm.dto.UpcomingEvent(" +
                "m.id, m.designation, m.dateScheduled, 'MAINTENANCE') " +
                "from Maintenance m" +
                " where m.dateScheduled >= CURRENT_DATE and m.contract.id = :contractId and m.deleted = false " +
                "order by m.dateScheduled ASC");
        query.setParameter("contractId", contractId);
        query.setMaxResults(5);
        return query.getResultList();
    }

    public Maintenance getLatestMaintenanceByRecurrence(Long recurrenceId) {
        Query query = manager.createQuery("select m from Maintenance m " +
                "where m.recurrence.id = :recurrenceId and m.deleted = false " +
                "order by m.creationDate desc");
        query.setParameter("recurrenceId", recurrenceId);
        query.setMaxResults(1);
        try {
            return (Maintenance) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean isLatestMaintenanceByRecurrence(Long recurrenceId, Long maintenanceId) {
        Query query = manager.createQuery("select m.id from Maintenance m " +
                "where m.recurrence.id = :recurrenceId and m.deleted = false " +
                "order by m.creationDate desc");
        query.setParameter("recurrenceId", recurrenceId);
        query.setMaxResults(1);
        try {
            return query.getSingleResult().equals(maintenanceId);
        } catch (NoResultException e) {
            return false;
        }
    }

    public int countOpenMaintenance(long contractId) {
        Query query = manager.createQuery("SELECT count(m) FROM Maintenance m " +
                "where m.contract.id = :contractId and m.deleted = false " +
                "and (m.closedDate is null or m.closedDate > :currentDate) ");
        query.setParameter("contractId", contractId);
        query.setParameter("currentDate", new Date());
        return ((Long) (query.getSingleResult())).intValue();
    }

    @SuppressWarnings({"unchecked"})
    public List<MaintenanceType> findMaintenanceTypes(long contractId) {
        Query query = manager.createQuery("select mt from MaintenanceType mt " +
                "where mt.contract is null or mt.contract.id = :contractId");
        query.setParameter("contractId", contractId);
        return query.getResultList();
    }

    public MaintenanceType findMaintenanceType(Long typeId) {
        Query query = manager.createQuery("select mt from MaintenanceType mt " +
                "where mt.id = :typeId");
        query.setParameter("typeId", typeId);
        try {
            return (MaintenanceType) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public MaintenanceType insertMaintenanceType(MaintenanceType maintenanceType) {
        manager.persist(maintenanceType);
        return maintenanceType;
    }

    @SuppressWarnings({"unchecked"})
    public List<Equipment> findEquipments(long contractId) {
        Query query = manager.createQuery("select e from Equipment e " +
                "where e.contract is null or e.contract.id = :contractId");
        query.setParameter("contractId", contractId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Equipment> findEquipmentsByContract(long contractId) {
        Query query = manager.createQuery("select e from Equipment e " +
                "where e.contract.id = :contractId");
        query.setParameter("contractId", contractId);
        return query.getResultList();
    }

    public Equipment findEquipment(Long equipmentId) {
        Query query = manager.createQuery("select e from Equipment e " +
                "where e.id = :equipmentId");
        query.setParameter("equipmentId", equipmentId);
        try {
            return (Equipment) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Equipment insertEquipment(Equipment equipment) {
        manager.persist(equipment);
        return equipment;
    }

    public int deleteEquipment(Long equipmentId) {
        Query query = manager.createQuery("delete from Equipment e " +
                "where e.id = :equipmentId");
        query.setParameter("equipmentId", equipmentId);
        return query.executeUpdate();
    }

    public int deleteEquipmentDocument(Long equipmentId) {
        Query query = manager.createQuery("delete from Document d " +
                "where d.equipment.id = :equipmentId");
        query.setParameter("equipmentId", equipmentId);
        return query.executeUpdate();
    }

    public Long countMaintenanceActionsByEquipment(Long equipmentId) {
        Query query = manager.createQuery("select count(m) from Maintenance m " +
                "join m.equipment e where e.id = :equipmentId ");
        query.setParameter("equipmentId", equipmentId);
        return (Long) query.getSingleResult();
    }

    public Document findEquipmentDocument(Long equipmentId) {
        Query query = manager.createQuery("select d from Document d where d.equipment.id = :equipmentId ");
        query.setParameter("equipmentId", equipmentId);
        try {
            return (Document) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Maintenance findMaintenance(Long maintenanceId) {
        Query query = manager.createQuery("select m from Maintenance m " +
                "where m.id = :maintenanceId and m.deleted = false");
        query.setParameter("maintenanceId", maintenanceId);
        try {
            return (Maintenance) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Maintenance findMaintenanceWithRecurrence(Long maintenanceId) {
        Query query = manager.createQuery("select distinct m from Maintenance m " +
                "left join fetch m.recurrence r " +
                "left join fetch r.notifications " +
                "where m.id = :maintenanceId and m.deleted = false");
        query.setParameter("maintenanceId", maintenanceId);
        try {
            return (Maintenance) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Maintenance findMaintenanceWithContract(Long maintenanceId) {
        Query query = manager.createQuery("select m from Maintenance m " +
                "left join fetch m.contract ct " +
                "left join fetch ct.company " +
                "where m.id = :maintenanceId and m.deleted = false");
        query.setParameter("maintenanceId", maintenanceId);
        try {
            return (Maintenance) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> findDocuments(Long maintenanceId) {
        Query query = manager.createQuery("select new com.criticalsoftware.certitools.entities.sm.Document(" +
                "d.id, d.displayName, d.name, d.contentType, d.contentLength" +
                ") from Document d where d.maintenance.id = :maintenanceId ");
        query.setParameter("maintenanceId", maintenanceId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> findDocumentsWithContent(Long maintenanceId) {
        Query query = manager.createNativeQuery("SELECT id, displayname, name, contenttype, contentlength, content " +
                "FROM sm_document d WHERE d.maintenance_id = ?1");
        query.setParameter(1, maintenanceId);
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
    public List<Chat> findChatMessages(Long maintenanceId) {
        Query query = manager.createQuery("select c from Chat c where c.maintenance.id = :maintenanceId ");
        query.setParameter("maintenanceId", maintenanceId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<CorrectiveAction> findCorrectiveActions(Long maintenanceId) {
        Query query = manager.createQuery("select c from CorrectiveAction c " +
                "where c.maintenance.id = :maintenanceId and c.deleted = false");
        query.setParameter("maintenanceId", maintenanceId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<CorrectiveAction> findOpenCorrectiveActions(Long maintenanceId) {
        Query query = manager.createQuery("select c from CorrectiveAction c " +
                "where c.maintenance.id = :maintenanceId and c.deleted = false " +
                "and (c.closedDate is null or c.closedDate > :currentDate) ");
        query.setParameter("maintenanceId", maintenanceId);
        query.setParameter("currentDate", new Date());
        return query.getResultList();
    }

    public boolean hasOpenCorrectiveActions(Long maintenanceId) {
        Query query = manager.createQuery("select count(c) from CorrectiveAction c " +
                "where c.maintenance.id = :maintenanceId and c.deleted = false " +
                "and (c.closedDate is null or c.closedDate > :currentDate)");
        query.setParameter("maintenanceId", maintenanceId);
        query.setParameter("currentDate", new Date());
        return (Long) (query.getSingleResult()) != 0;
    }

    public Long countChatMessages(Long maintenanceId) {
        Query query = manager.createQuery("select count(c) from Chat c where c.maintenance.id = :maintenanceId ");
        query.setParameter("maintenanceId", maintenanceId);
        return (Long) query.getSingleResult();
    }

    public int countAll(long contractId, Long maintenanceTypeId, String filterYear, Boolean isOpen) {
        String sql = "SELECT count(m) FROM Maintenance m " +
                "where m.contract.id = :contractId and m.deleted = false ";

        //apply filters
        if (maintenanceTypeId != null) {
            sql += "and m.maintenanceType.id = :maintenanceTypeId ";
        }
        if (isOpen != null) {
            if (isOpen) {
                sql += "and (m.closedDate is null or m.closedDate > :currentDate) ";
            } else {
                sql += "and (m.closedDate is not null and m.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and (year(m.dateScheduled) = :filterYear or year(m.closedDate) = :filterYear) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (maintenanceTypeId != null) {
            query.setParameter("maintenanceTypeId", maintenanceTypeId);
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
    public List<Maintenance> findAll(long contractId, int currentPage, int resultPerPage, String sortCriteria,
                                     String sortDirection, Long maintenanceTypeId, String filterYear, Boolean isOpen) {
        if (sortCriteria.equals("closed")) {
            sortCriteria = "closedDate";
        }
        String sql = "SELECT m FROM Maintenance m join fetch m.changedBy " +
                "where m.contract.id = :contractId and m.deleted = false ";

        //apply filters
        if (maintenanceTypeId != null) {
            sql += "and m.maintenanceType.id = :maintenanceTypeId ";
        }
        if (isOpen != null) {
            if (isOpen) {
                sql += "and (m.closedDate is null or m.closedDate > :currentDate) ";
            } else {
                sql += "and (m.closedDate is not null and m.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and (year(m.dateScheduled) = :filterYear or year(m.closedDate) = :filterYear) ";
        }

        sql += "order by m." + sortCriteria + " " + sortDirection + ", m.id " + sortDirection;

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (maintenanceTypeId != null) {
            query.setParameter("maintenanceTypeId", maintenanceTypeId);
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
    public List<Maintenance> findAll(long contractId, String filterYear, String filterSemester, Boolean isOpen) {
        String sql = "SELECT m FROM Maintenance m join fetch m.changedBy " +
                "where m.contract.id = :contractId and m.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (m.closedDate is null or m.closedDate > :currentDate) ";
            } else {
                sql += "and (m.closedDate is not null and m.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null && filterSemester != null) {
            sql += "and ((cast(extract(year from m.dateScheduled) as integer) = :filterYear " +
                    "and (((cast(extract(month from m.dateScheduled) as integer)-1)/6)+1) = :filterSemester) " +
                    "or (cast(extract(year from m.closedDate) as integer) = :filterYear " +
                    "and (((cast(extract(month from m.closedDate) as integer)-1)/6)+1) = :filterSemester)) ";
        } else if (filterYear != null) {
            sql += "and (cast(extract(year from m.dateScheduled) as integer) = :filterYear " +
                    "or cast(extract(year from m.closedDate) as integer) = :filterYear) ";
        }
        sql += "order by m.maintenanceType.name asc, m.id asc";

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
    public List<String> findMaintenanceYears(Long contractId) {
        Query query = manager.createNativeQuery("SELECT DISTINCT cast(years AS VARCHAR) " +
                "FROM " +
                "  (SELECT extract(YEAR FROM datescheduled) AS years " +
                "   FROM sm_maintenance " +
                "   WHERE contract_id = ?1 AND datescheduled IS NOT NULL AND deleted = FALSE " +
                "   UNION ALL " +
                "   SELECT extract(YEAR FROM closeddate) AS years " +
                "   FROM sm_maintenance " +
                "   WHERE contract_id = ?1 AND closeddate IS NOT NULL AND deleted = FALSE " +
                "  ) x " +
                "ORDER BY 1 DESC");
        query.setParameter(1, contractId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Maintenance> findMaintenancesWithChatsAfterDate(Date date) {
        Query query = manager.createQuery("select distinct m from Maintenance m " +
                "left join fetch m.chatMessages c " +
                "where c.datetime > :initialDate and m.deleted = false");
        query.setParameter("initialDate", date);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<Long> findMaintenancesIds(long contractId, String filterYear, Boolean isOpen) {
        String sql = "select m.id from Maintenance m " +
                "where m.contract.id = :contractId and m.deleted = false ";

        if (isOpen != null) {
            if (isOpen) {
                sql += "and (m.closedDate is null or m.closedDate > :currentDate) ";
            } else {
                sql += "and (m.closedDate is not null and m.closedDate <= :currentDate) ";
            }
        }
        if (filterYear != null) {
            sql += "and ('" + filterYear + "' is year(m.dateScheduled) or  '" + filterYear + "' is year(m.closedDate)) ";
        }

        Query query = manager.createQuery(sql);
        query.setParameter("contractId", contractId);
        if (isOpen != null) {
            query.setParameter("currentDate", new Date());
        }
        return query.getResultList();
    }

    public int deleteMaintenances(List<Long> ids, User loggedUser) {
        Query query = manager.createQuery("update Maintenance m " +
                "set m.deleted = true, m.changedDate = :currentDate, m.changedBy = :loggedUser " +
                "where m.id in (:ids)");
        query.setParameter("ids", ids);
        query.setParameter("currentDate", new Date());
        query.setParameter("loggedUser", loggedUser);
        return query.executeUpdate();
    }

    public int deleteMaintenancesDocuments(List<Long> ids) {
        Query query = manager.createQuery("delete from Document d where d.maintenance.id in (:ids)");
        query.setParameter("ids", ids);
        return query.executeUpdate();
    }

    public int deleteMaintenanceDocuments(Long maintenanceId) {
        Query query = manager.createQuery("delete from Document d where d.maintenance.id = :maintenanceId");
        query.setParameter("maintenanceId", maintenanceId);
        return query.executeUpdate();
    }

    public int deactivateMaintenancesRecurrences(List<Long> ids) {
        Query query = manager.createNativeQuery("UPDATE sm_recurrence " +
                "SET active = FALSE " +
                "WHERE active = TRUE " +
                "AND (SELECT sm_maintenance.id " +
                "   FROM sm_maintenance " +
                "   WHERE sm_recurrence.id = sm_maintenance.recurrence_id " +
                "   ORDER BY sm_maintenance.creationdate DESC LIMIT 1) " +
                " IN (?1)");

        query.setParameter(1, ids);
        return query.executeUpdate();
    }
}
