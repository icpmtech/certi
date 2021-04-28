package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.Equipment;
import com.criticalsoftware.certitools.entities.sm.Maintenance;
import com.criticalsoftware.certitools.entities.sm.MaintenanceType;
import com.criticalsoftware.certitools.persistence.GenericDAO;
import com.criticalsoftware.certitools.persistence.sm.dto.UpcomingEvent;

import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public interface MaintenanceDAO extends GenericDAO<Maintenance, Long> {

    <T> T getEntityReference(Class<T> clasz, Long id);

    /**
     * Find the 5 Maintenance that have the closest scheduled date
     *
     * @return - UpcomingEvent List
     */
    List<UpcomingEvent> findUpcomingEvents(Long contractId);

    Maintenance getLatestMaintenanceByRecurrence(Long recurrenceId);

    boolean isLatestMaintenanceByRecurrence(Long recurrenceId, Long maintenanceId);

    int countOpenMaintenance(long contractId);

    List<MaintenanceType> findMaintenanceTypes(long contractId);

    MaintenanceType findMaintenanceType(Long typeId);

    MaintenanceType insertMaintenanceType(MaintenanceType maintenanceType);

    List<Equipment> findEquipments(long contractId);

    List<Equipment> findEquipmentsByContract(long contractId);

    Equipment findEquipment(Long equipmentId);

    Equipment insertEquipment(Equipment equipment);

    int deleteEquipment(Long equipmentId);

    int deleteEquipmentDocument(Long equipmentId);

    Long countMaintenanceActionsByEquipment(Long equipmentId);

    Document findEquipmentDocument(Long equipmentId);

    Maintenance findMaintenance(Long maintenanceId);

    Maintenance findMaintenanceWithRecurrence(Long maintenanceId);

    Maintenance findMaintenanceWithContract(Long maintenanceId);

    List<Document> findDocuments(Long maintenanceId);

    List<Document> findDocumentsWithContent(Long maintenanceId);

    List<Chat> findChatMessages(Long maintenanceId);

    List<CorrectiveAction> findCorrectiveActions(Long maintenanceId);

    List<CorrectiveAction> findOpenCorrectiveActions(Long maintenanceId);

    boolean hasOpenCorrectiveActions(Long maintenanceId);

    Long countChatMessages(Long maintenanceId);

    int countAll(long contractId, Long maintenanceTypeId, String filterYear, Boolean isOpen);

    List<Maintenance> findAll(long contractId, int currentPage, int resultPerPage, String sortCriteria,
                              String sortDirection, Long maintenanceTypeId, String filterYear, Boolean isOpen);

    List<Maintenance> findAll(long contractId, String filterYear, String filterSemester, Boolean isOpen);

    List<String> findMaintenanceYears(Long contractId);

    List<Maintenance> findMaintenancesWithChatsAfterDate(Date date);

    List<Long> findMaintenancesIds(long contractId, String filterYear, Boolean isOpen);

    int deleteMaintenances(List<Long> ids, User loggedUser);

    int deleteMaintenancesDocuments(List<Long> ids);

    int deleteMaintenanceDocuments(Long maintenanceId);

    int deactivateMaintenancesRecurrences(List<Long> ids);
}
