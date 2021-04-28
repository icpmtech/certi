package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.InvalidSequenceException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.persistence.certitools.ContractDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.sm.ActivityDAO;
import com.criticalsoftware.certitools.persistence.sm.ChatDAO;
import com.criticalsoftware.certitools.persistence.sm.DocumentDAO;
import com.criticalsoftware.certitools.persistence.sm.RecurrenceDAO;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Activity;
import com.criticalsoftware.certitools.entities.sm.ActivityType;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.Recurrence;
import com.criticalsoftware.certitools.entities.sm.RecurrenceNotification;
import com.criticalsoftware.certitools.entities.sm.RecurrenceType;
import com.criticalsoftware.certitools.entities.sm.enums.RecurrenceEntityType;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.persistence.sm.dto.DocumentDTO;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.MailSender;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.criticalsoftware.certitools.util.enums.EmailEventType;
import com.criticalsoftware.certitools.util.enums.SequenceCode;
import org.apache.commons.io.IOUtils;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity Service EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(ActivityService.class)
@LocalBinding(jndiBinding = "certitools/ActivityService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class ActivityServiceEJB implements ActivityService {

    private static final Logger LOGGER = Logger.getInstance(ActivityServiceEJB.class);

    @EJB
    private ContractDAO contractDAO;
    @EJB
    private ActivityDAO activityDAO;
    @EJB
    private RecurrenceDAO recurrenceDAO;
    @EJB
    private DocumentDAO documentDAO;
    @EJB
    private ChatDAO chatDAO;
    @EJB
    private UserDAO userDAO;
    @EJB
    private SecurityManagementService securityManagementService;
    @EJB
    private SequenceGeneratorService sequenceGenerator;

    @Resource
    private SessionContext sessionContext;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<ActivityType> findActivityTypes(long contractId) {
        return activityDAO.findActivityTypes(contractId);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public ActivityType findActivityType(Long typeId) throws ObjectNotFoundException {
        ActivityType activityType = activityDAO.findActivityType(typeId);
        if (activityType == null) {
            throw new ObjectNotFoundException("Can't find the activity type with the specified id.",
                    ObjectNotFoundException.Type.SM_ACTIVITY_TYPE);
        }
        return activityType;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public ActivityType createActivityType(Long contractId, String name, User loggedUser)
            throws CertitoolsAuthorizationException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get current contract
        Contract contract = contractDAO.findById(contractId);

        ActivityType activityType = new ActivityType();
        activityType.setContract(contract);
        activityType.setName(name);
        return activityDAO.insertActivityType(activityType);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public Long createActivity(Long contractId, Activity activity, Long recurrenceTypeId,
                               Integer warningDays, List<User> notificationUsers, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get current contract
        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id.",
                    ObjectNotFoundException.Type.CONTRACT);
        }

        //create activity
        Activity newActivity = new Activity();
        newActivity.setCode(generateSequenceCode(contractId));
        newActivity.setName(activity.getName());
        newActivity.setDuration(activity.getDuration());
        newActivity.setInternalResponsible(activity.getInternalResponsible());
        newActivity.setExternalEntity(activity.getExternalEntity());
        newActivity.setActivityType(activity.getActivityType());
        newActivity.setDateScheduled(activity.getDateScheduled());
        newActivity.setContract(contract);
        Calendar calendar = Calendar.getInstance();
        newActivity.setCreationDate(calendar.getTime());
        newActivity.setChangedDate(calendar.getTime());
        newActivity.setCreatedBy(loggedUser);
        newActivity.setChangedBy(loggedUser);
        newActivity.setDeleted(false);
        activityDAO.insert(newActivity);

        RecurrenceType recurrenceType = null;
        if (recurrenceTypeId != null) {
            recurrenceType = recurrenceDAO.findRecurrenceType(recurrenceTypeId);
        }
        if (recurrenceType != null) {
            //create recurrence
            Recurrence recurrence = new Recurrence();
            setRecurrenceFields(recurrence, recurrenceType, warningDays, newActivity.getDateScheduled());

            recurrenceDAO.insert(recurrence);

            //create notifications
            for (User user : notificationUsers) {
                RecurrenceNotification notification = new RecurrenceNotification();
                notification.setRecurrence(recurrence);
                notification.setUser(user);
                recurrenceDAO.insertRecurrenceNotification(notification);
            }

            newActivity.setRecurrence(recurrence);
        }

        //send notifications
        try {
            sendEmailsForCreation(newActivity, loggedUser);
        } catch (Exception ex) {
            LOGGER.error("Error while sending notification emails " + ex);
        }
        if (newActivity.getRecurrence() != null) {
            // if the chosen scheduled date minus the warning days is a date before today, send the notification emails now
            sendRecurrenceEmailsNowIfNeeded(newActivity, notificationUsers);
        }

        return newActivity.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void updateActivityMainFields(Long contractId, Activity activity, Long recurrenceTypeId,
                                         Integer warningDays, List<User> notificationUsers, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get current contract
        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id.",
                    ObjectNotFoundException.Type.CONTRACT);
        }
        //get activity from DB
        Activity activityDB = activityDAO.findActivity(activity.getId());
        if (activityDB == null) {
            throw new ObjectNotFoundException("Can't find the activity with the specified id.",
                    ObjectNotFoundException.Type.SM_ACTIVITY);
        }
        if (activityDB.getClosed()) {
            throw new BusinessException("Can't update closed activity.");
        }

        long oldDateScheduled = activityDB.getDateScheduled().getTime();

        //update activity fields
        activityDB.setName(activity.getName());
        activityDB.setDuration(activity.getDuration());
        activityDB.setInternalResponsible(activity.getInternalResponsible());
        activityDB.setExternalEntity(activity.getExternalEntity());
        activityDB.setDateScheduled(activity.getDateScheduled());
        activityDB.setActivityType(activity.getActivityType());

        Calendar calendar = Calendar.getInstance();
        activityDB.setChangedDate(calendar.getTime());
        activityDB.setChangedBy(loggedUser);

        //update recurrence
        Recurrence recurrence = activityDB.getRecurrence();
        RecurrenceType recurrenceType = null;
        if (recurrenceTypeId != null) {
            recurrenceType = recurrenceDAO.findRecurrenceType(recurrenceTypeId);
        }

        Integer oldWarningDays = recurrence != null ? recurrence.getWarningDays() : null;

        if (recurrence != null) {
            if (recurrenceType != null) {
                //update the existing recurrence
                setRecurrenceFields(recurrence, recurrenceType, warningDays, activityDB.getDateScheduled());

                //delete old notifications
                recurrenceDAO.deleteRecurrenceNotifications(recurrence.getId());
                //create new notifications
                for (User user : notificationUsers) {
                    RecurrenceNotification notification = new RecurrenceNotification();
                    notification.setRecurrence(recurrence);
                    notification.setUser(user);
                    recurrenceDAO.insertRecurrenceNotification(notification);
                }
            } else {
                //set the existing recurrence as inactive
                recurrence.setActive(false);
                activityDB.setRecurrence(null);
            }
        } else if (recurrenceType != null) {
            //create the recurrence
            recurrence = new Recurrence();
            setRecurrenceFields(recurrence, recurrenceType, warningDays, activityDB.getDateScheduled());

            recurrenceDAO.insert(recurrence);

            //create notifications
            for (User user : notificationUsers) {
                RecurrenceNotification notification = new RecurrenceNotification();
                notification.setRecurrence(recurrence);
                notification.setUser(user);
                recurrenceDAO.insertRecurrenceNotification(notification);
            }

            activityDB.setRecurrence(recurrence);
        }

        if (activityDB.getRecurrence() != null &&
                (oldDateScheduled != activityDB.getDateScheduled().getTime()
                        || (oldWarningDays == null && warningDays != null && warningDays > 0)
                        || (oldWarningDays != null && warningDays != null && warningDays > 0 && !oldWarningDays.equals(warningDays)))) {
            // if the chosen scheduled date minus the warning days is a date before today, send the notification emails now
            sendRecurrenceEmailsNowIfNeeded(activityDB, notificationUsers);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void editActivity(Long contractId, Long activityId, List<DocumentDTO> newDocuments, Date closedDate, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserIntermediate(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert " +
                    "or intermediate permission");
        }
        //get current contract
        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id.",
                    ObjectNotFoundException.Type.CONTRACT);
        }
        //get activity from DB
        Activity activity = activityDAO.findActivity(activityId);
        if (activity == null) {
            throw new ObjectNotFoundException("Can't find the activity with the specified id.",
                    ObjectNotFoundException.Type.SM_ACTIVITY);
        }
        if (activity.getClosed()) {
            throw new BusinessException("Can't update closed activity.");
        }

        if (closedDate != null) {
            //check if this activity has open corrective actions
            //if so, it can not be closed
            if (activityDAO.hasOpenCorrectiveActions(activityId)) {
                throw new BusinessException("Can't close the activity because it has corrective actions that are still open.");
            }
            activity.setClosedDate(closedDate);

            //send notifications
            try {
                sendEmailsForClosing(activity, loggedUser);
            } catch (Exception ex) {
                LOGGER.error("Error while sending notification emails " + ex);
            }
        }

        Calendar calendar = Calendar.getInstance();
        activity.setChangedDate(calendar.getTime());
        activity.setChangedBy(loggedUser);

        //save documents
        for (DocumentDTO doc : newDocuments) {
            Document document = new Document();
            document.setContract(contract);
            document.setActivity(activity);
            document.setName(doc.getName());
            document.setDisplayName(doc.getDisplayName());
            document.setContentType(doc.getContentType());
            byte[] bytes = new byte[0];
            try {
                bytes = IOUtils.toByteArray(doc.getInputStream());
            } catch (IOException ignore) {
            }
            if (bytes.length > 0) {
                document.setContent(bytes);
                document.setContentLength(document.getContent().length);
                documentDAO.insert(document);
            }
        }

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteActivity(Long contractId, Long activityId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get activity from DB
        Activity activity = activityDAO.findActivity(activityId);
        if (activity == null) {
            throw new ObjectNotFoundException("Can't find the activity with the specified id.",
                    ObjectNotFoundException.Type.SM_ACTIVITY);
        }
        if (activity.getRecurrence() != null) {
            //check if this is the latest activity of it's recurrence and, if so, sets the recurrence as inactive
            if (activityDAO.isLatestActivityByRecurrence(activity.getRecurrence().getId(), activityId)) {
                activity.getRecurrence().setActive(false);
            }
        }
        //set activity as deleted
        activity.setDeleted(true);
        Calendar calendar = Calendar.getInstance();
        activity.setChangedDate(calendar.getTime());
        activity.setChangedBy(loggedUser);
        //delete related documents
        activityDAO.deleteActivityDocuments(activityId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void reopenActivity(Long contractId, Long activityId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get activity from DB
        Activity activity = activityDAO.findActivity(activityId);
        if (activity == null) {
            throw new ObjectNotFoundException("Can't find the activity with the specified id.",
                    ObjectNotFoundException.Type.SM_ACTIVITY);
        }
        activity.setClosedDate(null);
        Calendar calendar = Calendar.getInstance();
        activity.setChangedDate(calendar.getTime());
        activity.setChangedBy(loggedUser);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteDocument(Long contractId, Long activityId, Long documentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserIntermediate(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert " +
                    "or intermediate permission");
        }
        Document document = documentDAO.findById(documentId);
        if (document == null || document.getActivity() == null || !document.getActivity().getId().equals(activityId)) {
            throw new ObjectNotFoundException("Can't find the document with the specified id.",
                    ObjectNotFoundException.Type.SM_DOCUMENT);
        }
        if (document.getActivity().getClosed()) {
            throw new BusinessException("Can't update closed activity.");
        }

        //delete the document
        documentDAO.delete(document);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void addChatMessage(Long contractId, Long activityId, String message, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserBasic(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert " +
                    "or intermediate permission");
        }
        //get activity from DB
        Activity activity = activityDAO.findActivity(activityId);
        if (activity == null) {
            throw new ObjectNotFoundException("Can't find the activity with the specified id.",
                    ObjectNotFoundException.Type.SM_ACTIVITY);
        }
        if (activity.getClosed()) {
            throw new BusinessException("Can't update closed activity.");
        }

        Chat chat = new Chat();
        chat.setActivity(activity);
        chat.setMessage(message);
        chat.setUser(loggedUser);
        chat.setDatetime(new Date());
        chatDAO.insert(chat);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Chat> findChatMessages(Long activityId) {
        return activityDAO.findActivityChatMessages(activityId);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<CorrectiveAction> findOpenCorrectiveActions(Long activityId) {
        return activityDAO.findActivityOpenCorrectiveActions(activityId);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Activity findActivity(Long activityId) throws ObjectNotFoundException {
        Activity activity = activityDAO.findActivityWithRecurrence(activityId);
        if (activity == null) {
            throw new ObjectNotFoundException("Can't find the activity with the specified id.",
                    ObjectNotFoundException.Type.SM_ACTIVITY);
        }
        //set documents
        activity.setDocuments(activityDAO.findActivityDocuments(activityId));
        return activity;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Activity findActivityForChatPdf(Long activityId) throws ObjectNotFoundException {
        Activity activity = activityDAO.findActivityWithContract(activityId);
        if (activity == null) {
            throw new ObjectNotFoundException("Can't find the activity with the specified id.",
                    ObjectNotFoundException.Type.SM_ACTIVITY);
        }
        //set chat messages
        activity.setChatMessages(activityDAO.findActivityChatMessages(activityId));
        return activity;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public PaginatedListWrapper<Activity> findActivitiesByContract(long contractId,
                                                                   PaginatedListWrapper<Activity> paginatedListWrapper,
                                                                   Long activityTypeId, String filterYear,
                                                                   Boolean isOpen)
            throws BusinessException {

        if (paginatedListWrapper == null) {
            throw new BusinessException("No PaginatedListWrapper initialized ");
        }

        if (paginatedListWrapper.getSortCriterion() == null) {
            paginatedListWrapper.setSortCriterion("activityType.name");
        }
        if (paginatedListWrapper.getSortDirection() == null) {
            paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.DESC);
        }

        int count = activityDAO.countAll(contractId, activityTypeId, filterYear, isOpen);
        paginatedListWrapper.setFullListSize(count);

        List<Activity> activities;
        if (paginatedListWrapper.getExport()) {
            activities = activityDAO.findAll(contractId, 0, count,
                    paginatedListWrapper.getSortCriterion(), paginatedListWrapper.getSortDirection().value(),
                    activityTypeId, filterYear, isOpen);
        } else {
            activities = activityDAO.findAll(contractId, paginatedListWrapper.getOffset(),
                    paginatedListWrapper.getResultsPerPage(),
                    paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper.getSortDirection().value(), activityTypeId, filterYear, isOpen);
        }

        for (Activity activity : activities) { // force the collection to be loaded to avoid lazy loading exceptions
            activity.setCorrectiveActions(activityDAO.findActivityCorrectiveActions(activity.getId()));
            activity.setDocuments(activityDAO.findActivityDocuments(activity.getId()));
            if (activity.getClosed()) {
                //this is for the validation of the chat log pdf
                activity.setHasChatMessages(activityDAO.countActivityChatMessages(activity.getId()) != 0);
            }
        }

        paginatedListWrapper.setList(activities);

        return paginatedListWrapper;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Activity> findActivitiesByContract(long contractId, String filterYear, String filterSemester, Boolean isOpen) {

        List<Activity> activities = activityDAO.findAll(contractId, filterYear, filterSemester, isOpen);

        for (Activity activity : activities) { // force the collection to be loaded to avoid lazy loading exceptions
            activity.setCorrectiveActions(activityDAO.findActivityCorrectiveActions(activity.getId()));
            activity.setDocuments(activityDAO.findActivityDocuments(activity.getId()));
            if (activity.getClosed()) {
                activity.setChatMessages(activityDAO.findActivityChatMessages(activity.getId()));
                activity.setHasChatMessages(activity.getChatMessages().size() != 0);
            }
        }
        return activities;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<String> findActivityYears(long contractId) {
        return activityDAO.findActivityYears(contractId);
    }

    /**
     * Aux method to set the recurrence fields.
     *
     * @param recurrence     The recurrence.
     * @param recurrenceType The recurrence type.
     * @param warningDays    The number of warning days.
     * @param dateScheduled  The activity scheduled date.
     */
    private void setRecurrenceFields(Recurrence recurrence, RecurrenceType recurrenceType, Integer warningDays, Date dateScheduled) {
        recurrence.setRecurrenceType(recurrenceType);
        if (warningDays != null) {
            recurrence.setWarningDays(warningDays);
        } else {
            recurrence.setWarningDays(recurrenceType.getWarningDays());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateScheduled);
        calendar.add(Calendar.DAY_OF_MONTH, recurrenceType.getIntervalDays() - recurrence.getWarningDays());
        while (calendar.getTime().before(new Date())) {
            calendar.add(Calendar.DAY_OF_MONTH, recurrenceType.getIntervalDays());
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        recurrence.setNextScheduledDate(calendar.getTime());
        recurrence.setActive(true);
        recurrence.setEntityType(RecurrenceEntityType.ACTIVITY);
    }

    private String generateSequenceCode(Long contractId) throws BusinessException {
        try {
            return sequenceGenerator.generateCode(SequenceCode.ACTIVITY, contractId);
        } catch (InvalidSequenceException e) {
            throw new BusinessException("Error generating sequence code for activity.");
        }
    }

    private String getUrlForMail(Activity activity) {
        return Configuration.getInstance().getApplicationDomain() + "/sm/Security.action?"
                + "activityPlanningEdit=" + "&contractId=" + activity.getContract().getId() + "&activityId="
                + activity.getId().toString();
    }

    private List<User> getUsersForMail(Long contractId, User loggedUser) {
        //get expert and intermediate users for this contract
        List<User> users = userDAO.findExpertAndIntermediateUsersByContract(contractId);
        Iterator<User> it = users.iterator();
        while (it.hasNext()) {
            if (it.next().getEmail().equals(loggedUser.getEmail())) {
                it.remove();
            }
        }
        return users;
    }

    private void sendEmailsForCreation(final Activity activity, final User loggedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventCreatedEmails(EmailEventType.ACTIVITY, activity.getCode() + " - " + activity.getName(),
                        getUrlForMail(activity),
                        activity.getContract().getCompany().getName(), activity.getContract().getContractDesignation(),
                        getUsersForMail(activity.getContract().getId(), loggedUser));
            }
        });
    }

    private void sendEmailsForClosing(final Activity activity, final User loggedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventClosedEmails(EmailEventType.ACTIVITY, activity.getCode() + " - " + activity.getName(),
                        getUrlForMail(activity),
                        activity.getContract().getCompany().getName(), activity.getContract().getContractDesignation(),
                        getUsersForMail(activity.getContract().getId(), loggedUser));
            }
        });
    }

    private void sendRecurrenceEmailsNowIfNeeded(Activity activity, List<User> notificationUsers) {
        Calendar today = Calendar.getInstance();
        Calendar notificationDate = Calendar.getInstance();
        notificationDate.setTime(activity.getDateScheduled());
        notificationDate.add(Calendar.DAY_OF_MONTH, -activity.getRecurrence().getWarningDays());

        if (notificationDate.before(today)) {
            int numberDays = (int) Math.ceil(
                    (float) (activity.getDateScheduled().getTime() - today.getTimeInMillis()) / (1000 * 60 * 60 * 24));

            if (numberDays >= 0) {
                String eventName = activity.getCode() + " - " + activity.getName();
                Long contractId = activity.getContract().getId();
                String contract = activity.getContract().getContractDesignation();
                String company = activity.getContract().getCompany().getName();

                try {
                    sendRecurrenceNotificationEmails(eventName, numberDays, getUrlForMail(activity), company, contract,
                            notificationUsers);
                } catch (Exception ex) {
                    LOGGER.error("Error while sending notification emails " + ex);
                }
            }
        }
    }

    private void sendRecurrenceNotificationEmails(final String eventName, final Integer numberDays, final String url,
                                                  final String company, final String contract, final List<User> users) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmRecurrenceNotificationEmails(eventName, numberDays, url, company, contract, users);
            }
        });
    }
}
