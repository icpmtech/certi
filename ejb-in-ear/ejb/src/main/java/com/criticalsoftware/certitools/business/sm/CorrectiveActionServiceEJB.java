package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.InvalidSequenceException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.persistence.certitools.ContractDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.sm.ChatDAO;
import com.criticalsoftware.certitools.persistence.sm.CorrectiveActionDAO;
import com.criticalsoftware.certitools.persistence.sm.DocumentDAO;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Activity;
import com.criticalsoftware.certitools.entities.sm.Anomaly;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.Maintenance;
import com.criticalsoftware.certitools.entities.sm.SecurityImpactWork;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.persistence.sm.dto.DocumentDTO;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.MailSender;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.criticalsoftware.certitools.util.enums.EmailEventType;
import com.criticalsoftware.certitools.util.enums.SequenceCode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
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
 * Corrective Action Service EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(CorrectiveActionService.class)
@LocalBinding(jndiBinding = "certitools/CorrectiveActionService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class CorrectiveActionServiceEJB implements CorrectiveActionService {

    private static final Logger LOGGER = Logger.getInstance(CorrectiveActionServiceEJB.class);

    @EJB
    private ContractDAO contractDAO;
    @EJB
    private CorrectiveActionDAO correctiveActionDAO;
    @EJB
    private SecurityManagementService securityManagementService;
    @EJB
    private SequenceGeneratorService sequenceGenerator;
    @EJB
    private ChatDAO chatDAO;
    @EJB
    private DocumentDAO documentDAO;
    @EJB
    private UserDAO userDAO;

    @Resource
    private SessionContext sessionContext;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public Long createCorrectiveAction(Long contractId, CorrectiveAction correctiveAction, Long activityId,
                                       Long anomalyId, Long workId, Long maintenanceId, List<User> notificationUsers, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)) {
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

        CorrectiveAction newCorrectiveAction = new CorrectiveAction();
        newCorrectiveAction.setContract(contract);
        newCorrectiveAction.setCode(generateSequenceCode(contractId));
        newCorrectiveAction.setStartDate(correctiveAction.getStartDate());
        newCorrectiveAction.setName(correctiveAction.getName());
        newCorrectiveAction.setDescription(correctiveAction.getDescription());
        newCorrectiveAction.setExecutionResponsible(correctiveAction.getExecutionResponsible());
        newCorrectiveAction.setDuration(correctiveAction.getDuration());
        if (activityId != null) {
            newCorrectiveAction.setActivity(correctiveActionDAO.getEntityReference(Activity.class, activityId));
        } else if (anomalyId != null) {
            newCorrectiveAction.setAnomaly(correctiveActionDAO.getEntityReference(Anomaly.class, anomalyId));
        } else if (workId != null) {
            newCorrectiveAction.setSecurityImpactWork(correctiveActionDAO.getEntityReference(SecurityImpactWork.class, workId));
        } else if (maintenanceId != null) {
            newCorrectiveAction.setMaintenance(correctiveActionDAO.getEntityReference(Maintenance.class, maintenanceId));
        }
        Calendar calendar = Calendar.getInstance();
        newCorrectiveAction.setCreationDate(calendar.getTime());
        newCorrectiveAction.setChangedDate(calendar.getTime());
        newCorrectiveAction.setCreatedBy(loggedUser);
        newCorrectiveAction.setChangedBy(loggedUser);
        newCorrectiveAction.setDeleted(false);
        correctiveActionDAO.insert(newCorrectiveAction);

        //send notifications
        try {
            sendEmailsForCreation(newCorrectiveAction, notificationUsers, loggedUser);
        } catch (Exception ex) {
            LOGGER.error("Error while sending notification emails " + ex);
        }

        return newCorrectiveAction.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public boolean updateCorrectiveActionMainFields(Long contractId, CorrectiveAction correctiveAction, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)) {
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
        //get corrective action from DB
        CorrectiveAction correctiveActionDB = correctiveActionDAO.findCorrectiveAction(correctiveAction.getId());
        if (correctiveActionDB == null) {
            throw new ObjectNotFoundException("Can't find the corrective action with the specified id.",
                    ObjectNotFoundException.Type.SM_CORRECTIVE_ACTION);
        }
        if (correctiveActionDB.getClosed()) {
            throw new BusinessException("Can't update closed corrective action.");
        }

        boolean areFieldsModified = false;

        if ((correctiveAction.getStartDate() != null && !correctiveAction.getStartDate().equals(correctiveActionDB.getStartDate())) ||
                (correctiveAction.getDescription() != null && !correctiveAction.getDescription().equals(correctiveActionDB.getDescription())) ||
                (correctiveAction.getExecutionResponsible() != null && !correctiveAction.getExecutionResponsible().equals(correctiveActionDB.getExecutionResponsible())) ||
                (correctiveAction.getDuration() != null && !correctiveAction.getDuration().equals(correctiveActionDB.getDuration())) ||
                (correctiveAction.getName() != null && !correctiveAction.getName().equals(correctiveActionDB.getName()))) {
            areFieldsModified = true;
        }

        //update activity fields
        correctiveActionDB.setStartDate(correctiveAction.getStartDate());
        correctiveActionDB.setName(correctiveAction.getName());
        correctiveActionDB.setDescription(correctiveAction.getDescription());
        correctiveActionDB.setExecutionResponsible(correctiveAction.getExecutionResponsible());
        correctiveActionDB.setDuration(correctiveAction.getDuration());

        Calendar calendar = Calendar.getInstance();
        correctiveActionDB.setChangedDate(calendar.getTime());
        correctiveActionDB.setChangedBy(loggedUser);

        return areFieldsModified;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void editCorrectiveAction(Long contractId, CorrectiveAction correctiveAction, List<DocumentDTO> newDocuments,
                                     List<User> notificationUsers, User loggedUser, boolean areFieldsModified) throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)) {
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
        //get corrective action from DB
        CorrectiveAction correctiveActionDB = correctiveActionDAO.findCorrectiveAction(correctiveAction.getId());
        if (correctiveActionDB == null) {
            throw new ObjectNotFoundException("Can't find the corrective action with the specified id.",
                    ObjectNotFoundException.Type.SM_CORRECTIVE_ACTION);
        }
        if (correctiveActionDB.getClosed()) {
            throw new BusinessException("Can't update closed corrective action.");
        }

        if ((correctiveAction.getNotes() != null && !correctiveAction.getNotes().equals(correctiveActionDB.getNotes())) ||
                (newDocuments.size() > 0)) {
            areFieldsModified = true;
        }



        correctiveActionDB.setNotes(correctiveAction.getNotes());
        Calendar calendar = Calendar.getInstance();
        correctiveActionDB.setChangedDate(calendar.getTime());
        correctiveActionDB.setChangedBy(loggedUser);

        //save documents
        for (DocumentDTO doc : newDocuments) {
            Document document = new Document();
            document.setContract(contract);
            document.setCorrectiveAction(correctiveActionDB);
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

        if (correctiveAction.getClosedDate() == null) {
            //send notifications for edit
            if (areFieldsModified) try {
                sendEmailsForEditing(correctiveActionDB, notificationUsers, loggedUser);
            } catch (Exception ex) {
                LOGGER.error("Error while sending notification emails " + ex);
            }
        } else {
            correctiveActionDB.setClosedDate(correctiveAction.getClosedDate());
            //send notifications for closing
            try {
                sendEmailsForClosing(correctiveActionDB, loggedUser);
            } catch (Exception ex) {
                LOGGER.error("Error while sending notification emails " + ex);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteCorrectiveAction(Long contractId, Long actionId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get corrective action from DB
        CorrectiveAction correctiveAction = correctiveActionDAO.findCorrectiveAction(actionId);
        if (correctiveAction == null) {
            throw new ObjectNotFoundException("Can't find the corrective action with the specified id.",
                    ObjectNotFoundException.Type.SM_CORRECTIVE_ACTION);
        }
        //set corrective action as deleted
        correctiveAction.setDeleted(true);
        Calendar calendar = Calendar.getInstance();
        correctiveAction.setChangedDate(calendar.getTime());
        correctiveAction.setChangedBy(loggedUser);
        //delete related documents
        correctiveActionDAO.deleteCorrectiveActionDocuments(actionId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void reopenCorrectiveAction(Long contractId, Long actionId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get corrective action from DB
        CorrectiveAction correctiveAction = correctiveActionDAO.findCorrectiveAction(actionId);
        if (correctiveAction == null) {
            throw new ObjectNotFoundException("Can't find the corrective action with the specified id.",
                    ObjectNotFoundException.Type.SM_CORRECTIVE_ACTION);
        }
        correctiveAction.setClosedDate(null);
        Calendar calendar = Calendar.getInstance();
        correctiveAction.setChangedDate(calendar.getTime());
        correctiveAction.setChangedBy(loggedUser);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteDocument(Long contractId, Long actionId, Long documentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserIntermediate(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert " +
                    "or intermediate permission");
        }
        Document document = documentDAO.findById(documentId);
        if (document == null || document.getCorrectiveAction() == null
                || !document.getCorrectiveAction().getId().equals(actionId)) {
            throw new ObjectNotFoundException("Can't find the document with the specified id.",
                    ObjectNotFoundException.Type.SM_DOCUMENT);
        }
        if (document.getCorrectiveAction().getClosed()) {
            throw new BusinessException("Can't update closed corrective action.");
        }

        //delete the document
        documentDAO.delete(document);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void addChatMessage(Long contractId, Long actionId, String message, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserBasic(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert " +
                    "or intermediate permission");
        }
        //get corrective action from DB
        CorrectiveAction correctiveAction = correctiveActionDAO.findCorrectiveAction(actionId);
        if (correctiveAction == null) {
            throw new ObjectNotFoundException("Can't find the corrective action with the specified id.",
                    ObjectNotFoundException.Type.SM_CORRECTIVE_ACTION);
        }
        if (correctiveAction.getClosed()) {
            throw new BusinessException("Can't update closed corrective action.");
        }

        Chat chat = new Chat();
        chat.setCorrectiveAction(correctiveAction);
        chat.setMessage(message);
        chat.setUser(loggedUser);
        chat.setDatetime(new Date());
        chatDAO.insert(chat);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Chat> findChatMessages(Long actionId) {
        return correctiveActionDAO.findCorrectiveActionChatMessages(actionId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public CorrectiveAction findCorrectiveAction(Long actionId) throws ObjectNotFoundException {
        CorrectiveAction correctiveAction = correctiveActionDAO.findCorrectiveActionComplete(actionId);
        if (correctiveAction == null) {
            throw new ObjectNotFoundException("Can't find the corrective action with the specified id.",
                    ObjectNotFoundException.Type.SM_CORRECTIVE_ACTION);
        }
        correctiveAction.setDocuments(correctiveActionDAO.findDocuments(correctiveAction.getId()));
        return correctiveAction;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public CorrectiveAction findCorrectiveActionForChatPdf(Long actionId) throws ObjectNotFoundException {
        CorrectiveAction correctiveAction = correctiveActionDAO.findCorrectiveActionWithContract(actionId);
        if (correctiveAction == null) {
            throw new ObjectNotFoundException("Can't find the corrective action with the specified id.",
                    ObjectNotFoundException.Type.SM_CORRECTIVE_ACTION);
        }
        //set chat messages
        correctiveAction.setChatMessages(correctiveActionDAO.findCorrectiveActionChatMessages(actionId));
        return correctiveAction;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public PaginatedListWrapper<CorrectiveAction> findCorrectiveActionsByContract(
            long contractId, PaginatedListWrapper<CorrectiveAction> paginatedListWrapper, String filterYear,
            Boolean isOpen) throws BusinessException {

        if (paginatedListWrapper == null) {
            throw new BusinessException("No PaginatedListWrapper initialized ");
        }

        if (paginatedListWrapper.getSortCriterion() == null) {
            paginatedListWrapper.setSortCriterion("startDate");
        }
        if (paginatedListWrapper.getSortDirection() == null) {
            paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.DESC);
        }

        int count = correctiveActionDAO.countAll(contractId, filterYear, isOpen);
        paginatedListWrapper.setFullListSize(count);

        List<CorrectiveAction> correctiveActions;
        if (paginatedListWrapper.getExport()) {
            correctiveActions = correctiveActionDAO.findAll(contractId, 0, count,
                    paginatedListWrapper.getSortCriterion(), paginatedListWrapper.getSortDirection().value(),
                    filterYear, isOpen);
        } else {
            correctiveActions = correctiveActionDAO.findAll(contractId, paginatedListWrapper.getOffset(),
                    paginatedListWrapper.getResultsPerPage(),
                    paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper.getSortDirection().value(), filterYear, isOpen);
        }

        for (CorrectiveAction action : correctiveActions) { // force the collection to be loaded to avoid lazy loading exceptions
            action.setDocuments(correctiveActionDAO.findDocuments(action.getId()));
            if (action.getClosed()) {
                //this is for the validation of the chat log pdf
                action.setHasChatMessages(correctiveActionDAO.countCorrectiveActionChatMessages(action.getId()) != 0);
            }
        }

        paginatedListWrapper.setList(correctiveActions);

        return paginatedListWrapper;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<CorrectiveAction> findCorrectiveActionsByContract(long contractId, String filterYear,
                                                                  String filterSemester, Boolean isOpen) {

        List<CorrectiveAction> correctiveActions = correctiveActionDAO.findAllComplete(contractId, filterYear,
                filterSemester, isOpen);

        for (CorrectiveAction action : correctiveActions) { // force the collection to be loaded to avoid lazy loading exceptions
            action.setDocuments(correctiveActionDAO.findDocuments(action.getId()));
            if (action.getClosed()) {
                action.setChatMessages(correctiveActionDAO.findCorrectiveActionChatMessages(action.getId()));
                action.setHasChatMessages(action.getChatMessages().size() != 0);
            }
        }
        return correctiveActions;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<String> findCorrectiveActionYears(long contractId) {
        return correctiveActionDAO.findCorrectiveActionYears(contractId);
    }

    private String generateSequenceCode(Long contractId) throws BusinessException {
        try {
            return sequenceGenerator.generateCode(SequenceCode.CORRECTIVE_ACTION, contractId);
        } catch (InvalidSequenceException e) {
            throw new BusinessException("Error generating sequence code for corrective action.");
        }
    }

    private String getUrlForMail(CorrectiveAction correctiveAction) {
        return Configuration.getInstance().getApplicationDomain() + "/sm/SecurityActionsPlanning.action?"
                + "actionsPlanningEdit=" + "&contractId=" + correctiveAction.getContract().getId() + "&correctiveActionId="
                + correctiveAction.getId().toString();
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

    /*
    NEW */
    private List<User> getNotificationUsersForMail(Long contractID, List<User> notificationUsers, User loggedUser) {
        List<User> expertAndIntermediateUsers = getUsersForMail(contractID, loggedUser);
        for (Object u : notificationUsers) {
            if (!expertAndIntermediateUsers.contains(u))
                expertAndIntermediateUsers.add((User) u);
        }
        return expertAndIntermediateUsers;
    }

    private void sendEmailsForCreation(final CorrectiveAction correctiveAction, final List<User> notificationUsers, final User loggedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventCreatedEmails(EmailEventType.CORRECTIVE_ACTION,
                        correctiveAction.getCode() + " - " + correctiveAction.getName(),
                        getUrlForMail(correctiveAction),
                        correctiveAction.getContract().getCompany().getName(),
                        correctiveAction.getContract().getContractDesignation(),
                        getNotificationUsersForMail(correctiveAction.getContract().getId(), notificationUsers, loggedUser));
            }
        });
    }

    private void sendEmailsForEditing(final CorrectiveAction correctiveAction, final List<User> notificationUsers, final User loggedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventEditEmails(EmailEventType.CORRECTIVE_ACTION,
                        correctiveAction.getCode() + " - " + correctiveAction.getName(),
                        getUrlForMail(correctiveAction),
                        correctiveAction.getContract().getCompany().getName(), correctiveAction.getContract().getContractDesignation(),
                        getNotificationUsersForMail(correctiveAction.getContract().getId(), notificationUsers, loggedUser));
            }
        });
    }

    private void sendEmailsForClosing(final CorrectiveAction correctiveAction, final User loggedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventClosedEmails(EmailEventType.CORRECTIVE_ACTION,
                        correctiveAction.getCode() + " - " + correctiveAction.getName(),
                        getUrlForMail(correctiveAction),
                        correctiveAction.getContract().getCompany().getName(),
                        correctiveAction.getContract().getContractDesignation(),
                        getUsersForMail(correctiveAction.getContract().getId(), loggedUser));
            }
        });
    }


}
