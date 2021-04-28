package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.persistence.sm.dto.DocumentDTO;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;

import java.util.List;

/**
 * Corrective Action Service
 *
 * @author miseabra
 * @version $Revision$
 */
public interface CorrectiveActionService {

    /**
     * Creates a corrective action.
     *
     * @param contractId       The contract id.
     * @param correctiveAction The corrective action to be persisted.
     * @param activityId       The activity to associate with this corrective action.
     * @param anomalyId        The anomaly to associate with this corrective action.
     * @param workId           The security impact work to associate with this corrective action.
     * @param maintenanceId    The maintenance to associate with this corrective action.
     * @param loggedUser       The logged user.
     * @return The corrective action id.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract does not exist.
     * @throws BusinessException                If occurs an error generating the code sequence.
     */
    Long createCorrectiveAction(Long contractId, CorrectiveAction correctiveAction, Long activityId,
                                Long anomalyId, Long workId, Long maintenanceId, List<User> notificationUsers, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Updates the main fields of a corrective action (the same fields of the corrective action creation).
     *
     * @param contractId       The contract id.
     * @param correctiveAction The corrective action to be updated.
     * @param loggedUser       The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract or the corrective action don't exist.
     * @throws BusinessException                When updating a corrective action that is already closed.
     */
    boolean updateCorrectiveActionMainFields(Long contractId, CorrectiveAction correctiveAction, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Edits a corrective action.
     *
     * @param contractId       The contract id.
     * @param correctiveAction The corrective action to be updated. If the closed date is not null, it will close the corrective action and only the expert will be able to open it again.
     * @param newDocuments     The list of new documents that will be associated with this corrective action.
     * @param loggedUser       The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the corrective action don't exist.
     * @throws BusinessException                When updating a corrective action that is already closed.
     */
    void editCorrectiveAction(Long contractId, CorrectiveAction correctiveAction, List<DocumentDTO> newDocuments,
                              List<User> notificationUsers, User loggedUser, boolean changed) throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Deletes a corrective action. The corrective action is only marked as deleted. The documents are not deleted.
     *
     * @param contractId The contract id.
     * @param actionId   The corrective action id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract or the corrective action don't exist.
     */
    void deleteCorrectiveAction(Long contractId, Long actionId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Reopens a corrective action. The corrective action closed date is set to null.
     *
     * @param contractId The contract id.
     * @param actionId   The corrective action id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the corrective action don't exist.
     */
    void reopenCorrectiveAction(Long contractId, Long actionId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Deletes the given document, associated with a corrective action.
     *
     * @param contractId The contract id.
     * @param actionId   The corrective action id.
     * @param documentId The document id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the corrective action don't exist.
     * @throws BusinessException                When updating a corrective action that is already closed.
     */
    void deleteDocument(Long contractId, Long actionId, Long documentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Adds a chat message to the given corrective action.
     *
     * @param contractId The contract id.
     * @param actionId   The corrective action id.
     * @param message    The chat message.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the corrective action don't exist.
     * @throws BusinessException                When updating a corrective action that is already closed.
     */
    void addChatMessage(Long contractId, Long actionId, String message, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Returns the list of chat messages of the given corrective action.
     *
     * @param actionId The corrective action id.
     * @return The list of chat messages found.
     */
    List<Chat> findChatMessages(Long actionId);

    /**
     * Returns the corrective action with the given id. Includes the contract, the related record and the documents.
     *
     * @param actionId The corrective action id.
     * @return The corrective action found.
     * @throws ObjectNotFoundException If the corrective action doesn't exist.
     */
    CorrectiveAction findCorrectiveAction(Long actionId) throws ObjectNotFoundException;

    /**
     * Returns the corrective action with the given id for the chat pdf. Includes the contract and the chat messages.
     *
     * @param actionId The corrective action id.
     * @return The corrective action found.
     * @throws ObjectNotFoundException If the corrective action doesn't exist.
     */
    CorrectiveAction findCorrectiveActionForChatPdf(Long actionId) throws ObjectNotFoundException;

    /**
     * Returns a list of corrective actions according to the params in paginatedListWrapper.
     *
     * @param contractId           The contract id.
     * @param paginatedListWrapper The paginatedListWrapper.
     * @return The list of corrective actions according to the params in paginatedListWrapper.
     * @throws BusinessException If the paginatedListWrapper is null.
     */
    PaginatedListWrapper<CorrectiveAction> findCorrectiveActionsByContract(
            long contractId, PaginatedListWrapper<CorrectiveAction> paginatedListWrapper, String filterYear,
            Boolean isOpen) throws BusinessException;

    /**
     * Returns all corrective actions according to the given parameters.
     *
     * @param contractId     The contract id.
     * @param filterYear     The year of the corrective actions. If null, all years will be considered.
     * @param filterSemester The semester of the corrective actions. If null, both semesters will be considered. Must be null when year is also null.
     * @param isOpen         Indicates the status of the corrective actions. If null, all statuses will be considered.
     * @return The list of corrective actions found.
     */
    List<CorrectiveAction> findCorrectiveActionsByContract(long contractId, String filterYear, String filterSemester,
                                                           Boolean isOpen);

    /**
     * Returns all corrective actions years
     *
     * @param contractId the contract that the activities belong
     * @return list of years
     */
    List<String> findCorrectiveActionYears(long contractId);
}
