package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.sm.Activity;
import com.criticalsoftware.certitools.entities.sm.ActivityType;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.persistence.sm.dto.DocumentDTO;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;

import java.util.Date;
import java.util.List;

/**
 * Activity Service
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("unused")
public interface ActivityService {

    /**
     * Returns the list of activity types for the given contract.
     *
     * @param contractId The contract id.
     * @return The list of activity types.
     */
    List<ActivityType> findActivityTypes(long contractId);

    /**
     * Returns the activity type with the given id.
     *
     * @param typeId The activity type id.
     * @return The activity type found.
     * @throws ObjectNotFoundException If the activity type doesn't exist.
     */
    ActivityType findActivityType(Long typeId) throws ObjectNotFoundException;

    /**
     * Creates a new activity type that will be associated with the given contract.
     *
     * @param contractId The contract id.
     * @param name       The name of the activity type.
     * @param loggedUser The logged user.
     * @return The activity type created.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     */
    ActivityType createActivityType(Long contractId, String name, User loggedUser) throws CertitoolsAuthorizationException;

    /**
     * Creates an activity.
     *
     * @param contractId        The contract id.
     * @param activity          The activity to be created.
     * @param recurrenceTypeId  The recurrence type to be applied to this activity, or null if the activity isn't recurrent.
     * @param warningDays       The warning days for the recurrence, or null if the recurrence type is also null.
     * @param notificationUsers The users to be notified.
     * @param loggedUser        The logged user.
     * @return The activity id.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract doesn't exist.
     * @throws BusinessException                If occurs an error generating the code sequence.
     */
    Long createActivity(Long contractId, Activity activity, Long recurrenceTypeId,
                        Integer warningDays, List<User> notificationUsers, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Updates the main fields of an activity (the same fields of the activity creation).
     *
     * @param contractId        The contract id.
     * @param activity          The activity to be updated.
     * @param recurrenceTypeId  The recurrence type to be applied to this activity, or null if the activity isn't recurrent.
     * @param warningDays       The warning days for the recurrence, or null if the recurrence type is also null.
     * @param notificationUsers The users to be notified.
     * @param loggedUser        The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the contract or the activity don't exist.
     * @throws BusinessException                When updating an activity that is already closed.
     */
    void updateActivityMainFields(Long contractId, Activity activity, Long recurrenceTypeId,
                                  Integer warningDays, List<User> notificationUsers, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Edits an activity.
     *
     * @param contractId   The contract id.
     * @param activityId   The activity id.
     * @param newDocuments The list of new documents that will be associated with this activity.
     * @param closedDate   The closed date. If this field is not null, it will close the activity and only the expert will be able to open it again.
     * @param loggedUser   The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the activity don't exist.
     * @throws BusinessException                When closing an activity with open corrective actions or updating an activity that is already closed.
     */
    void editActivity(Long contractId, Long activityId, List<DocumentDTO> newDocuments, Date closedDate,
                      User loggedUser) throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Deletes an activity. The activity is only marked as deleted. The documents are not deleted.
     * The recurrence, if any, is marked as inactive if this is the latest activity of this recurrence.
     *
     * @param contractId The contract id.
     * @param activityId The activity id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the activity doesn't exist.
     */
    void deleteActivity(Long contractId, Long activityId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Reopens an activity. The activity closed date is set to null.
     *
     * @param contractId The contract id.
     * @param activityId The activity id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert.
     * @throws ObjectNotFoundException          If the activity doesn't exist.
     */
    void reopenActivity(Long contractId, Long activityId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Deletes the given document.
     *
     * @param contractId The contract id.
     * @param activityId The activity id.
     * @param documentId The document id.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the activity don't exist.
     * @throws BusinessException                When updating an activity that is already closed.
     */
    void deleteDocument(Long contractId, Long activityId, Long documentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Adds a chat message to the given activity.
     *
     * @param contractId The contract id.
     * @param activityId The activity id.
     * @param message    The chat message.
     * @param loggedUser The logged user.
     * @throws CertitoolsAuthorizationException If the logged user is not expert or intermediate.
     * @throws ObjectNotFoundException          If the contract or the activity don't exist.
     * @throws BusinessException                When updating an activity that is already closed.
     */
    void addChatMessage(Long contractId, Long activityId, String message, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException;

    /**
     * Returns the list of chat messages of the given activity.
     *
     * @param activityId The activity id.
     * @return The list of chat messages found.
     */
    List<Chat> findChatMessages(Long activityId);

    /**
     * Returns the list of open corrective actions for the given activity.
     *
     * @param activityId The activity id.
     * @return The list of open corrective actions found.
     */
    List<CorrectiveAction> findOpenCorrectiveActions(Long activityId);

    /**
     * Finds the Activity with the given id. Includes the recurrence and the documents.
     *
     * @param activityId The activity id.
     * @return The Activity complete info.
     * @throws ObjectNotFoundException If the activity doesn't exist.
     */
    Activity findActivity(Long activityId) throws ObjectNotFoundException;

    /**
     * Finds the Activity with the given id, for the chat pdf. Includes the contract and the chat messages.
     *
     * @param activityId The activity id.
     * @return The Activity info.
     * @throws ObjectNotFoundException If the activity doesn't exist.
     */
    Activity findActivityForChatPdf(Long activityId) throws ObjectNotFoundException;

    /**
     * Returns all activities according to the params in paginatedListWrapper
     *
     * @param contractId           the contract that the activities belong
     * @param paginatedListWrapper with the parameters for the search
     * @return activities according to the params in paginatedListWrapper
     * @throws BusinessException if the paginatedListWrapper is null
     */
    PaginatedListWrapper<Activity> findActivitiesByContract(long contractId,
                                                            PaginatedListWrapper<Activity> paginatedListWrapper,
                                                            Long activityTypeId, String filterYear,
                                                            Boolean isOpen)
            throws BusinessException;

    /**
     * Returns all activities according to the given parameters.
     *
     * @param contractId     The contract id.
     * @param filterYear     The year of the activities. If null, all years will be considered.
     * @param filterSemester The semester of the activities. If null, both semesters will be considered. Must be null when year is also null.
     * @param isOpen         Indicates the status of the activities. If null, all statuses will be considered.
     * @return The list of activities found.
     */
    List<Activity> findActivitiesByContract(long contractId, String filterYear, String filterSemester, Boolean isOpen);

    /**
     * Returns all activities years
     *
     * @param contractId the contract that the activities belong
     * @return list of years
     */
    List<String> findActivityYears(long contractId);
}
