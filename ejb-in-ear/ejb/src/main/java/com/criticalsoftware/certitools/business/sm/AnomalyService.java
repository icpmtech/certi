package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.sm.Anomaly;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.enums.AnomalyType;
import com.criticalsoftware.certitools.persistence.sm.dto.DocumentDTO;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;

import java.util.List;

/**
 * Anomaly Service
 *
 * @author miseabra
 * @version $Revision$
 */
public interface AnomalyService {


    /**
     * Creates an anomaly.
     *
     * @param contractId  The contract id.
     * @param anomaly     The anomaly to be created.
     * @param anomalyType The anomaly type.
     * @param loggedUser  The logged user.
     * @return The anomaly id.
     * @throws CertitoolsAuthorizationException If the logged user is not basic.
     * @throws ObjectNotFoundException          If the contract doesn't exist.
     * @throws BusinessException                If occurs an error generating the code sequence.
     */
    Long createAnomaly(Long contractId, Anomaly anomaly, AnomalyType anomalyType, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Updates the main fields of an anomaly (the same fields of the anomaly creation).
     *
     * @param contractId  The contract id.
     * @param anomaly     The anomaly to be updated.
     * @param anomalyType The anomaly type.
     * @param loggedUser  The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract doesn't exist.
     * @throws BusinessException                When updating an anomaly that is already closed.
     */
    void updateAnomalyMainFields(Long contractId, Anomaly anomaly, AnomalyType anomalyType, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Edits an anomaly.
     *
     * @param contractId   The contract id.
     * @param anomaly      The anomaly to be updated. If the closed date is not null, it will close the anomaly and only the expert will be able to open it again.
     * @param newDocuments The list of new documents that will be associated with this activity.
     * @param loggedUser   The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the anomaly don't exist.
     * @throws BusinessException                When closing an anomaly with open corrective actions or updating an anomaly that is already closed.
     */
    void editAnomaly(Long contractId, Anomaly anomaly, AnomalyType anomalyType, List<DocumentDTO> newDocuments, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Reopens an anomaly. The anomaly closed date is set to null.
     *
     * @param contractId The contract id.
     * @param anomalyId  The anomaly id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract or the anomaly don't exist.
     */
    void reopenAnomaly(Long contractId, Long anomalyId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Deletes an anomaly. The anomaly is only marked as deleted. The documents are not deleted.
     *
     * @param contractId The contract id.
     * @param anomalyId  The anomaly id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract or the anomaly don't exist.
     */
    void deleteAnomaly(Long contractId, Long anomalyId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Deletes the given document, associated with an anomaly.
     *
     * @param contractId The contract id.
     * @param anomalyId  The anomaly id.
     * @param documentId The document id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract, the anomaly or the document don't exist.
     * @throws BusinessException                When updating an anomaly that is already closed.
     */
    void deleteDocument(Long contractId, Long anomalyId, Long documentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Adds a chat message to the given anomaly.
     *
     * @param contractId The contract id.
     * @param anomalyId  The anomaly id.
     * @param message    The chat message.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the anomaly don't exist.
     * @throws BusinessException                When updating an anomaly that is already closed.
     */
    void addChatMessage(Long contractId, Long anomalyId, String message, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Returns the list of chat messages of the given anomaly.
     *
     * @param anomalyId The anomaly id.
     * @return The list of chat messages found.
     */
    List<Chat> findChatMessages(Long anomalyId);

    /**
     * Returns the list of open corrective actions for the given anomaly.
     *
     * @param anomalyId The anomaly id.
     * @return The list of open corrective actions found.
     */
    List<CorrectiveAction> findOpenCorrectiveActions(Long anomalyId);

    /**
     * Returns the anomaly with the given id. Includes the documents.
     *
     * @param anomalyId The anomaly id.
     * @return The anomaly found.
     * @throws ObjectNotFoundException If the anomaly doesn't exist.
     */
    Anomaly findAnomaly(Long anomalyId) throws ObjectNotFoundException;

    /**
     * Finds the anomaly with the given id, for the chat pdf. Includes the contract and the chat messages.
     *
     * @param anomalyId The anomaly id.
     * @return The anomaly found.
     * @throws ObjectNotFoundException If the anomaly doesn't exist.
     */
    Anomaly findAnomalyForChatPdf(Long anomalyId) throws ObjectNotFoundException;

    /**
     * Finds the anomaly with the given id, for the report pdf. Includes the contract, the documents and the corrective actions.
     *
     * @param anomalyId The anomaly id.
     * @return The anomaly found.
     * @throws ObjectNotFoundException If the anomaly doesn't exist.
     */
    Anomaly findAnomalyForReportPdf(Long anomalyId) throws ObjectNotFoundException;

    /**
     * Returns a list of anomalies according to the params in paginatedListWrapper.
     *
     * @param contractId           The contract id.
     * @param paginatedListWrapper The paginatedListWrapper.
     * @return The list of anomalies according to the params in paginatedListWrapper.
     * @throws BusinessException If the paginatedListWrapper is null.
     */
    PaginatedListWrapper<Anomaly> findAnomaliesByContract(long contractId,
                                                          PaginatedListWrapper<Anomaly> paginatedListWrapper,
                                                          AnomalyType anomalyType, String filterYear,
                                                          Boolean isOpen) throws BusinessException;

    /**
     * Returns all anomalies according to the given parameters.
     *
     * @param contractId     The contract id.
     * @param filterYear     The year of the anomalies. If null, all years will be considered.
     * @param filterSemester The semester of the anomalies. If null, both semesters will be considered. Must be null when year is also null.
     * @param isOpen         Indicates the status of the anomalies. If null, all statuses will be considered.
     * @return The list of anomalies found.
     */
    List<Anomaly> findAnomaliesByContract(long contractId, String filterYear, String filterSemester, Boolean isOpen);

    /**
     * Returns the years of the anomalies of the given contract.
     *
     * @param contractId The contract id.
     * @return The list of years.
     */
    List<String> findAnomalyYears(long contractId);

}
