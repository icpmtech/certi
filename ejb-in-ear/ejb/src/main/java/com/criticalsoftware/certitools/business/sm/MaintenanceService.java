package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.Equipment;
import com.criticalsoftware.certitools.entities.sm.Maintenance;
import com.criticalsoftware.certitools.entities.sm.MaintenanceType;
import com.criticalsoftware.certitools.persistence.sm.dto.DocumentDTO;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;

import java.util.Date;
import java.util.List;

/**
 * Maintenance Service
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("unused")
public interface MaintenanceService {

    /**
     * Returns the list of maintenance types for the given contract.
     *
     * @param contractId The contract id.
     * @return The list of maintenance types.
     */
    List<MaintenanceType> findMaintenanceTypes(long contractId);

    /**
     * Creates a new maintenance type that will be associated with the given contract.
     *
     * @param contractId The contract id.
     * @param name       The name of the maintenance type.
     * @param loggedUser The logged user.
     * @return The maintenance type created.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract doesn't exist.
     */
    MaintenanceType createMaintenanceType(Long contractId, String name, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Returns the maintenance type with the given id.
     *
     * @param typeId The maintenance type id.
     * @return The maintenance type found.
     * @throws ObjectNotFoundException If the maintenance type doesn't exist.
     */
    MaintenanceType findMaintenanceType(Long typeId) throws ObjectNotFoundException;

    /**
     * Returns the list of equipments for the given contract. Includes the default equipments.
     *
     * @param contractId The contract id.
     * @return The list of equipments.
     */
    List<Equipment> findEquipments(long contractId);

    /**
     * Returns the list of equipments for the given contract.
     *
     * @param contractId The contract id.
     * @return The list of equipments.
     */
    List<Equipment> findEquipmentsByContract(long contractId);

    /**
     * Creates a new equipment that will be associated with the given contract.
     *
     * @param contractId        The contract id.
     * @param name              The name of the equipment.
     * @param loggedUser        The logged user.
     * @param equipmentDocument The document to associate with this equipment.
     * @return The equipment created.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract doesn't exist.
     */
    Equipment createEquipment(Long contractId, String name, User loggedUser, DocumentDTO equipmentDocument)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Deletes the equipment with the given id.
     *
     * @param contractId  The contract id.
     * @param equipmentId The equipment id.
     * @param loggedUser  The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the equipment doesn't exist.
     * @throws BusinessException                If the equipment can't be deleted.
     */
    void deleteEquipment(Long contractId, Long equipmentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Counts the number of maintenance actions associated with the given equipment.
     *
     * @param equipmentId The equipment id.
     * @return The number of maintenance activities associated with the given equipment.
     */
    Long countMaintenanceActionsByEquipment(Long equipmentId);

    /**
     * Returns the equipment with the given id.
     *
     * @param equipmentId The equipment id.
     * @return The equipment found.
     * @throws ObjectNotFoundException If the equipment doesn't exist.
     */
    Equipment findEquipment(Long equipmentId) throws ObjectNotFoundException;

    /**
     * Returns the document associated to a given equipment.
     *
     * @param equipmentId The equipment id.
     * @return The document associated.
     */
    Document findEquipmentDocument(Long equipmentId);

    /**
     * Creates a maintenance.
     *
     * @param contractId        The contract id.
     * @param maintenance       The maintenance to be created.
     * @param recurrenceTypeId  The recurrence type to be applied to this maintenance, or null if the maintenance isn't recurrent.
     * @param warningDays       The warning days for the recurrence, or null if the recurrence type is also null.
     * @param notificationUsers The users to be notified.
     * @param loggedUser        The logged user.
     * @return The maintenance id.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract doesn't exist.
     * @throws BusinessException                If occurs an error generating the code sequence.
     */
    Long createMaintenance(Long contractId, Maintenance maintenance, Long recurrenceTypeId,
                           Integer warningDays, List<User> notificationUsers, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Updates the main fields of an maintenance (the same fields of the maintenance creation).
     *
     * @param contractId        The contract id.
     * @param maintenance       The maintenance to be updated.
     * @param recurrenceTypeId  The recurrence type to be applied to this maintenance, or null if the maintenance isn't recurrent.
     * @param warningDays       The warning days for the recurrence, or null if the recurrence type is also null.
     * @param notificationUsers The users to be notified.
     * @param loggedUser        The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract or the maintenance don't exist.
     * @throws BusinessException                When updating a maintenance that is already closed.
     */
    void updateMaintenanceMainFields(Long contractId, Maintenance maintenance, Long recurrenceTypeId,
                                     Integer warningDays, List<User> notificationUsers, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Edits a maintenance.
     *
     * @param contractId    The contract id.
     * @param maintenanceId The maintenance id.
     * @param newDocuments  The list of new documents that will be associated with this maintenance.
     * @param closedDate    The closed date. If this field is not null, it will close the maintenance and only the expert will be able to open it again.
     * @param loggedUser    The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the maintenance don't exist.
     * @throws BusinessException                When closing a maintenance with open corrective actions or updating a maintenance that is already closed.
     */
    void editMaintenance(Long contractId, Long maintenanceId, List<DocumentDTO> newDocuments, Date closedDate, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Deletes a maintenance. The maintenance is only marked as deleted. The documents are not deleted.
     * The recurrence, if any, is marked as inactive if this is the latest maintenance of this recurrence.
     *
     * @param contractId    The contract id.
     * @param maintenanceId The maintenance id.
     * @param loggedUser    The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract or the maintenance don't exist.
     */
    void deleteMaintenance(Long contractId, Long maintenanceId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Reopens a maintenance. The maintenance closed date is set to null.
     *
     * @param contractId    The contract id.
     * @param maintenanceId The maintenance id.
     * @param loggedUser    The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract or the maintenance don't exist.
     */
    void reopenMaintenance(Long contractId, Long maintenanceId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Deletes the given document.
     *
     * @param contractId    The contract id.
     * @param maintenanceId The maintenance id.
     * @param documentId    The document id.
     * @param loggedUser    The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the document don't exist.
     * @throws BusinessException                When updating a maintenance that is already closed.
     */
    void deleteDocument(Long contractId, Long maintenanceId, Long documentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Adds a chat message to the given maintenance.
     *
     * @param contractId    The contract id.
     * @param maintenanceId The maintenance id.
     * @param message       The chat message.
     * @param loggedUser    The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the maintenance don't exist.
     * @throws BusinessException                When updating a maintenance that is already closed.
     */
    void addChatMessage(Long contractId, Long maintenanceId, String message, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Returns the list of chat messages of the given maintenance.
     *
     * @param maintenanceId The maintenance id.
     * @return The list of chat messages found.
     */
    List<Chat> findChatMessages(Long maintenanceId);

    /**
     * Returns the list of open corrective actions for the given maintenance.
     *
     * @param maintenanceId The maintenance id.
     * @return The list of corrective actions found.
     */
    List<CorrectiveAction> findOpenCorrectiveActions(Long maintenanceId);

    /**
     * Finds the maintenance with the given id. Includes the recurrence and the documents.
     *
     * @param maintenanceId The maintenance id.
     * @return The maintenance found.
     * @throws ObjectNotFoundException If the maintenance doesn't exist.
     */
    Maintenance findMaintenance(Long maintenanceId) throws ObjectNotFoundException;

    /**
     * Finds the maintenance with the given id, for the chat pdf. Includes the contract and the chat messages.
     *
     * @param maintenanceId The maintenance id.
     * @return The maintenance found.
     * @throws ObjectNotFoundException If the maintenance doesn't exist.
     */
    Maintenance findMaintenanceForChatPdf(Long maintenanceId) throws ObjectNotFoundException;

    /**
     * Finds the maintenance with the given id, for the report pdf. Includes the contract, the documents and the corrective actions.
     *
     * @param maintenanceId The maintenance id.
     * @return The maintenance found.
     * @throws ObjectNotFoundException If the maintenance doesn't exist.
     */
    Maintenance findMaintenanceForReportPdf(Long maintenanceId) throws ObjectNotFoundException;

    /**
     * Returns all maintenances according to the params in paginatedListWrapper
     *
     * @param contractId           The contract id.
     * @param paginatedListWrapper The paginatedListWrapper with the parameters for the search.
     * @return The list of maintenances according to the params in paginatedListWrapper.
     * @throws BusinessException If the paginatedListWrapper is null.
     */
    PaginatedListWrapper<Maintenance> findMaintenancesByContract(long contractId,
                                                                 PaginatedListWrapper<Maintenance> paginatedListWrapper,
                                                                 Long maintenanceTypeId, String filterYear,
                                                                 Boolean isOpen)
            throws BusinessException;

    /**
     * Returns all maintenances according to the given parameters.
     *
     * @param contractId     The contract id.
     * @param filterYear     The year of the maintenances. If null, all years will be considered.
     * @param filterSemester The semester of the maintenaces. If null, both semesters will be considered. Must be null when year is also null.
     * @param isOpen         Indicates the status of the maintenances. If null, all statuses will be considered.
     * @return The list of maintenances found.
     */
    List<Maintenance> findMaintenancesByContract(long contractId, String filterYear, String filterSemester,
                                                 Boolean isOpen);

    /**
     * Returns the years of the maintenances of the given contract.
     *
     * @param contractId The contract id.
     * @return The list of years.
     */
    List<String> findMaintenanceYears(long contractId);
}
