package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.EmergencyAction;
import com.criticalsoftware.certitools.entities.sm.EmergencyUser;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;

import java.util.List;

/**
 * Emergency Action Service
 *
 * @author miseabra
 * @version $Revision$
 */
public interface EmergencyActionService {

    /**
     * Returns the list of emergency users of the given contract.
     *
     * @param contractId The contract id.
     * @return The list of emergency users of the given contract.
     */
    List<EmergencyUser> findEmergencyUsers(Long contractId);

    /**
     * Creates a emergency user for the given contract.
     *
     * @param contractId    The contract id.
     * @param emergencyUser The emergency user to create.
     * @param loggedUser    The logged user.
     * @return The emergency user created.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract doesn't exist.
     */
    EmergencyUser createEmergencyUser(Long contractId, EmergencyUser emergencyUser, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Deletes the given emergency user.
     *
     * @param contractId      The contract id.
     * @param emergencyUserId The emergency user id.
     * @param loggedUser      The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the emergency user doesn't exist.
     */
    void deleteEmergencyUser(Long contractId, Long emergencyUserId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Checks if the given email already exists in an emergency user of this contract.
     *
     * @param contractId The contract id.
     * @param email      The email.
     * @return true, if the email already exists.
     */
    boolean existsEmergencyUserByEmailAndContract(Long contractId, String email);

    /**
     * Creates an emergency action.
     *
     * @param contractId      The contract id.
     * @param emergencyAction The emergency action to create.
     * @param loggedUser      The logged user.
     * @return The id of the emergency action created.
     * @throws CertitoolsAuthorizationException If the logged user is not basic.
     * @throws ObjectNotFoundException          If the contract doesn't exist.
     * @throws BusinessException                If occurs an error generating the code sequence.
     */
    Long createEmergencyAction(Long contractId, EmergencyAction emergencyAction, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Updates the given emergency action.
     *
     * @param contractId      The contract id.
     * @param emergencyAction The emergency action to update.
     * @param loggedUser      The logged user.
     * @param token           The access token.
     * @throws CertitoolsAuthorizationException If the user is not expert.
     * @throws ObjectNotFoundException          If the contract or the emergency action don't exist.
     * @throws BusinessException                When updating an emergency action that is already closed.
     */
    void updateEmergencyAction(Long contractId, EmergencyAction emergencyAction, User loggedUser, String token)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Closes an emergency action.
     *
     * @param contractId  The contract id.
     * @param emergencyId The emergency action id.
     * @param loggedUser  The logged user.
     * @param token       The access token.
     * @throws CertitoolsAuthorizationException If the user doesn't have permission to close the action.
     * @throws ObjectNotFoundException          If the emergency action doesn't exist.
     */
    void closeEmergencyAction(Long contractId, Long emergencyId, User loggedUser, String token)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Deletes an emergency action.
     *
     * @param contractId  The contract id.
     * @param emergencyId The emergency action id.
     * @param loggedUser  The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the emergency action doesn't exist.
     */
    void deleteEmergencyAction(Long contractId, Long emergencyId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Adds a chat message to the given emergency action.
     *
     * @param contractId  The contract id.
     * @param emergencyId The emergency action id.
     * @param message     The chat message.
     * @param loggedUser  The logged user.
     * @param token       The access token.
     * @throws ObjectNotFoundException          If the emergency action doesn't exist.
     * @throws CertitoolsAuthorizationException If the user doesn't have permission or a valid access token.
     * @throws BusinessException                If the emergency action is already closed.
     */
    void addChatMessage(Long contractId, Long emergencyId, String message, User loggedUser, String token)
            throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException;

    /**
     * Returns the list of chat messages of the given emergency action.
     *
     * @param contractId  The contract id.
     * @param emergencyId The emergency action id.
     * @param loggedUser  The logged user.
     * @param token       The access token.
     * @return The list of chat messages found.
     * @throws CertitoolsAuthorizationException If the user doesn't have permission or a valid access token.
     */
    List<Chat> findChatMessages(Long contractId, Long emergencyId, User loggedUser, String token)
            throws CertitoolsAuthorizationException;

    /**
     * Returns the emergency action with the given id.
     *
     * @param contractId  The contract id.
     * @param emergencyId The emergency action id.
     * @param loggedUser  The logged user.
     * @param token       The access token.
     * @return The emergency action found.
     * @throws ObjectNotFoundException          If the emergency action doesn't exist.
     * @throws CertitoolsAuthorizationException If the user doesn't have permission or a valid access token.
     */
    EmergencyAction findEmergencyAction(Long contractId, Long emergencyId, User loggedUser, String token)
            throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Returns the emergency action with the given id, for the report pdf. Includes the contract and the chat messages.
     *
     * @param emergencyId The emergency action id.
     * @return The emergency action found.
     * @throws ObjectNotFoundException If the emergency action doesn't exist.
     */
    EmergencyAction findEmergencyActionForReportPdf(Long emergencyId) throws ObjectNotFoundException;

    /**
     * Returns a list of emergency actions according to the params in paginatedListWrapper.
     *
     * @param contractId           The contract id.
     * @param paginatedListWrapper The paginatedListWrapper.
     * @param filterYear           The year for the filter.
     * @return The list of emergency actions found.
     * @throws BusinessException If the paginatedListWrapper is null.
     */
    PaginatedListWrapper<EmergencyAction> findEmergencyActionsByContract(long contractId,
                                                                         PaginatedListWrapper<EmergencyAction> paginatedListWrapper,
                                                                         String filterYear, Boolean isOpen)
            throws BusinessException;

    /**
     * Returns all emergency actions according to the given parameters.
     *
     * @param contractId     The contract id.
     * @param filterYear     The year of the emergency actions. If null, all years will be considered.
     * @param filterSemester The semester of the emergency actions. If null, both semesters will be considered. Must be null when year is also null.
     * @param isOpen         Indicates the status of the emergency actions. If null, all statuses will be considered.
     * @return The list of emergency actions found.
     */
    List<EmergencyAction> findEmergencyActionsByContract(long contractId, String filterYear, String filterSemester,
                                                         Boolean isOpen);

    /**
     * Returns all emergency actions years.
     *
     * @param contractId The contract id.
     * @return The list of emergency actions.
     */
    List<String> findEmergencyActionYears(long contractId);

    /**
     * Checks if the given token is valid.
     *
     * @param emergencyId The emergency action id.
     * @param token       The access token.
     * @return true if the given token is valid, false otherwise.
     */
    boolean isValidToken(Long emergencyId, String token);

    /**
     * Checks if the given user has permission to close the emergency action.
     *
     * @param contractId  The contract id.
     * @param emergencyId The emergency action id.
     * @param user        The user.
     * @param token       The access token.
     * @return true if the user has permission to close the emergency action, false otherwise.
     */
    boolean hasPermissionToClose(Long contractId, Long emergencyId, User user, String token);

    /**
     * Checks if the user with the given access token has permission to edit the emergency action.
     *
     * @param contractId  The contract id.
     * @param emergencyId The emergency action id.
     * @param token       The access token.
     * @return true if the user has permission to edit, false otherwise.
     */
    boolean hasPermissionToEdit(Long contractId, Long emergencyId, String token);
}
