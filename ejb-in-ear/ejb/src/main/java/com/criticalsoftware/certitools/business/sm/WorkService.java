package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Risk;
import com.criticalsoftware.certitools.entities.sm.SecurityImpactWork;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.enums.WorkType;
import com.criticalsoftware.certitools.persistence.sm.dto.DocumentDTO;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;

import java.util.List;

/**
 * Work Service
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("unused")
public interface WorkService {

    /**
     * Returns the list of risks.
     *
     * @return The list of risks.
     */
    List<Risk> findRisks();

    /**
     * Returns the risk with the given id.
     *
     * @param riskId The risk id.
     * @return The risk found.
     * @throws ObjectNotFoundException If the risk doesn't exist.
     */
    Risk findRisk(Long riskId) throws ObjectNotFoundException;

    /**
     * Creates a modification/work authorization.
     *
     * @param contractId         The contract id.
     * @param securityImpactWork The modification/work authorization to be created.
     * @param workType           The modification/work authorization type.
     * @param loggedUser         The logged user.
     * @return The modification/work authorization id.
     * @throws CertitoolsAuthorizationException If the logged user is not basic.
     * @throws ObjectNotFoundException          If the contract doesn't exist.
     * @throws BusinessException                If occurs an error generating the code sequence.
     */
    Long createSecurityImpactWork(Long contractId, SecurityImpactWork securityImpactWork, WorkType workType, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Updates the given modification/work authorization main fields.
     *
     * @param contractId         The contract id.
     * @param securityImpactWork The modification/work authorization to be updated.
     * @param workType           The modification/work authorization type.
     * @param loggedUser         The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract or the modification/work authorization don't exist.
     * @throws BusinessException                When updating a modification/work authorization that is already closed.
     */
    void updateSecurityImpactWorkMainFields(Long contractId, SecurityImpactWork securityImpactWork, WorkType workType, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Edits the given modification/work authorization.
     *
     * @param contractId         The contract id.
     * @param securityImpactWork The modification/work authorization to be updated. If the closed date is not null, it will close the record and only the expert will be able to open it again.
     * @param workType           The modification/work authorization type.
     * @param newDocuments       The list of new documents that will be associated with this record.
     * @param loggedUser         The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the modification/work authorization don't exist.
     * @throws BusinessException                When closing a record with open corrective actions or updating a record that is already closed.
     */
    void editSecurityImpactWork(Long contractId, SecurityImpactWork securityImpactWork, WorkType workType,
                                List<DocumentDTO> newDocuments, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Reopens the given modification/work authorization.
     *
     * @param contractId The contract id.
     * @param workId     The modification/work authorization id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract or the modification/work authorization don't exist.
     */
    void reopenSecurityImpactWork(Long contractId, Long workId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Deletes the given modification/work authorization. The record is only marked as deleted. The documents are not deleted.
     *
     * @param contractId The contract id.
     * @param workId     The modification/work authorization id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract or the modification/work authorization don't exist.
     */
    void deleteSecurityImpactWork(Long contractId, Long workId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Deletes the given document, associated with the given modification/work authorization.
     *
     * @param contractId The contract id.
     * @param workId     The modification/work authorization id.
     * @param documentId The document id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the document don't exist.
     * @throws BusinessException                When updating a modification/work authorization that is already closed.
     */
    void deleteDocument(Long contractId, Long workId, Long documentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Adds a chat message to the given modification/work authorization.
     *
     * @param contractId The contract id.
     * @param workId     The modification/work authorization id.
     * @param message    The chat message.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the modification/work authorization don't exist.
     * @throws BusinessException                When updating a modification/work authorization that is already closed.
     */
    void addChatMessage(Long contractId, Long workId, String message, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Returns the list of chat messages of the given modification/work authorization.
     *
     * @param workId The modification/work authorization id.
     * @return The list of chat messages found.
     */
    List<Chat> findChatMessages(Long workId);

    /**
     * Returns the list of open corrective actions of the given modification/work authorization.
     *
     * @param workId The modification/work authorization id.
     * @return The list of corrective actions found.
     */
    List<CorrectiveAction> findOpenCorrectiveActions(Long workId);

    /**
     * Returns the modification/work authorization with the given id. Includes the risks and the documents.
     *
     * @param workId The modification/work authorization id.
     * @return The modification/work authorization found.
     * @throws ObjectNotFoundException If the modification/work authorization doesn't exist.
     */
    SecurityImpactWork findSecurityImpactWork(Long workId) throws ObjectNotFoundException;

    /**
     * Returns the modification/work authorization with the given id. Includes the contract and the chat messages.
     *
     * @param workId The modification/work authorization id.
     * @return The modification/work authorization found.
     * @throws ObjectNotFoundException If the modification/work authorization doesn't exist.
     */
    SecurityImpactWork findSecurityImpactWorkForChatPdf(Long workId) throws ObjectNotFoundException;

    /**
     * Returns the modification/work authorization with the given id. Includes the contract, the risks and the documents.
     *
     * @param workId The modification/work authorization id.
     * @return The modification/work authorization found.
     * @throws ObjectNotFoundException If the modification/work authorization doesn't exist.
     */
    SecurityImpactWork findSecurityImpactWorkForReportPdf(Long workId) throws ObjectNotFoundException;

    /**
     * Returns a list of modifications/work authorizations according to the params in paginatedListWrapper.
     *
     * @param contractId           The contract id.
     * @param paginatedListWrapper The paginatedListWrapper.
     * @return The list of modifications/work authorizations according to the params in paginatedListWrapper.
     * @throws BusinessException If the paginatedListWrapper is null.
     */
    PaginatedListWrapper<SecurityImpactWork> findSecurityImpactWorksByContract(long contractId,
                                                                               PaginatedListWrapper<SecurityImpactWork> paginatedListWrapper,
                                                                               WorkType workType, String filterYear,
                                                                               Boolean isOpen)
            throws BusinessException;

    /**
     * Returns all modifications/work authorizations according to the given parameters.
     *
     * @param contractId     The contract id.
     * @param filterYear     The year of the modifications/work authorizations. If null, all years will be considered.
     * @param filterSemester The semester of the modifications/work authorizations. If null, both semesters will be considered. Must be null when year is also null.
     * @param isOpen         Indicates the status of the modifications/work authorizations. If null, all statuses will be considered.
     * @return The list of modifications/work authorizations found.
     */
    List<SecurityImpactWork> findSecurityImpactWorksByContract(long contractId, String filterYear,
                                                               String filterSemester, Boolean isOpen);

    /**
     * Returns all the years of the modifications/work authorizations of the given contract.
     *
     * @param contractId The contract id.
     * @return The list of years.
     */
    List<String> findSecurityImpactWorkYears(long contractId);
}
