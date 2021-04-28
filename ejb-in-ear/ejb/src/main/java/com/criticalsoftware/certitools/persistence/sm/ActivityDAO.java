package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Activity;
import com.criticalsoftware.certitools.entities.sm.ActivityType;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.persistence.GenericDAO;
import com.criticalsoftware.certitools.persistence.sm.dto.UpcomingEvent;

import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public interface ActivityDAO extends GenericDAO<Activity, Long> {

    /**
     * Find the 5 Activities that have the closest scheduled date
     *
     * @return - UpcomingEvent List
     */
    List<UpcomingEvent> findUpcomingEvents(Long contractId);

    List<ActivityType> findActivityTypes(long contractId);

    ActivityType findActivityType(Long typeId);

    ActivityType insertActivityType(ActivityType activityType);

    Activity getLatestActivityByRecurrence(Long recurrenceId);

    boolean isLatestActivityByRecurrence(Long recurrenceId, Long activityId);

    Activity findActivity(Long activityId);

    Activity findActivityWithRecurrence(Long activityId);

    Activity findActivityWithContract(Long activityId);

    List<Document> findActivityDocuments(Long activityId);

    List<Document> findDocumentsWithContent(Long activityId);

    List<Chat> findActivityChatMessages(Long activityId);

    int countOpenActivity(long contractId);

    int countAll(long contractId, Long activityTypeId, String filterYear, Boolean isOpen);

    List<Activity> findAll(long contractId, int currentPage, int resultPerPage, String sortCriteria,
                           String sortDirection, Long activityTypeId, String filterYear, Boolean isOpen);

    List<Activity> findAll(long contractId, String filterYear, String filterSemester, Boolean isOpen);

    boolean hasOpenCorrectiveActions(Long activityId);

    List<String> findActivityYears(Long contractId);

    List<CorrectiveAction> findActivityCorrectiveActions(Long activityId);

    List<CorrectiveAction> findActivityOpenCorrectiveActions(Long activityId);

    Long countActivityChatMessages(Long activityId);

    List<Activity> findActivitiesWithChatsAfterDate(Date date);

    List<Long> findActivitiesIds(long contractId, String filterYear, Boolean isOpen);

    int deleteActivities(List<Long> ids, User loggedUser);

    int deleteActivitiesDocuments(List<Long> ids);

    int deleteActivityDocuments(Long activityId);

    int deactivateActivitiesRecurrences(List<Long> ids);
}
