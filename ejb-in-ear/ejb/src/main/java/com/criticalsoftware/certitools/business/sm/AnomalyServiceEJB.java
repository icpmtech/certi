package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.InvalidSequenceException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.persistence.certitools.ContractDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.sm.AnomalyDAO;
import com.criticalsoftware.certitools.persistence.sm.ChatDAO;
import com.criticalsoftware.certitools.persistence.sm.DocumentDAO;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Anomaly;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.SecurityImpact;
import com.criticalsoftware.certitools.entities.sm.enums.AnomalyType;
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
 * Anomaly Service EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(AnomalyService.class)
@LocalBinding(jndiBinding = "certitools/AnomalyService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class AnomalyServiceEJB implements AnomalyService {

    private static final Logger LOGGER = Logger.getInstance(AnomalyServiceEJB.class);

    @EJB
    private ContractDAO contractDAO;
    @EJB
    private AnomalyDAO anomalyDAO;
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public Long createAnomaly(Long contractId, Anomaly anomaly, AnomalyType anomalyType, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserBasic(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have basic permission");
        }
        //get current contract
        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id.",
                    ObjectNotFoundException.Type.CONTRACT);
        }

        Anomaly newAnomaly = new Anomaly();
        newAnomaly.setContract(contract);
        newAnomaly.setCode(generateSequenceCode(contractId, anomalyType));
        newAnomaly.setDatetime(anomaly.getDatetime());
        newAnomaly.setDescription(anomaly.getDescription());
        newAnomaly.setName(anomaly.getName());
        Calendar calendar = Calendar.getInstance();
        newAnomaly.setCreationDate(calendar.getTime());
        newAnomaly.setChangedDate(calendar.getTime());
        newAnomaly.setCreatedBy(loggedUser);
        newAnomaly.setChangedBy(loggedUser);
        newAnomaly.setDeleted(false);
        //set default security impact
        newAnomaly.setSecurityImpact(anomalyDAO.getEntityReference(SecurityImpact.class, 1L));

        newAnomaly.setAnomalyType(anomalyType);
        if (anomalyType == AnomalyType.ANOMALY) {
            newAnomaly.setWhoDetected(anomaly.getWhoDetected());
        } else {
            newAnomaly.setInternalActors(anomaly.getInternalActors());
            newAnomaly.setExternalActors(anomaly.getExternalActors());
        }

        anomalyDAO.insert(newAnomaly);

        //send notifications
        try {
            sendEmailsForCreation(newAnomaly, loggedUser);
        } catch (Exception ex) {
            LOGGER.error("Error while sending notification emails " + ex);
        }

        return newAnomaly.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void updateAnomalyMainFields(Long contractId, Anomaly anomaly, AnomalyType anomalyType, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)) {
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
        //get anomaly from DB
        Anomaly anomalyDB = anomalyDAO.findAnomaly(anomaly.getId());
        if (anomalyDB == null) {
            throw new ObjectNotFoundException("Can't find the anomaly with the specified id.",
                    ObjectNotFoundException.Type.SM_ANOMALY);
        }
        if (anomalyDB.getClosed()) {
            throw new BusinessException("Can't update closed anomaly.");
        }

        anomalyDB.setDatetime(anomaly.getDatetime());
        anomalyDB.setDescription(anomaly.getDescription());
        anomalyDB.setName(anomaly.getName());
        Calendar calendar = Calendar.getInstance();
        // anomalyDB.setDatetime(calendar.getTime());
        anomalyDB.setChangedDate(calendar.getTime());
        anomalyDB.setChangedBy(loggedUser);

        if (anomalyType == AnomalyType.ANOMALY) {
            anomalyDB.setWhoDetected(anomaly.getWhoDetected());
        } else {
            anomalyDB.setInternalActors(anomaly.getInternalActors());
            anomalyDB.setExternalActors(anomaly.getExternalActors());
        }

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void editAnomaly(Long contractId, Anomaly anomaly, AnomalyType anomalyType, List<DocumentDTO> newDocuments, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)) {
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
        //get anomaly from DB
        Anomaly anomalyDB = anomalyDAO.findAnomaly(anomaly.getId());
        if (anomalyDB == null) {
            throw new ObjectNotFoundException("Can't find the anomaly with the specified id.",
                    ObjectNotFoundException.Type.SM_ANOMALY);
        }
        if (anomalyDB.getClosed()) {
            throw new BusinessException("Can't update closed anomaly.");
        }

        anomalyDB.setSecurityImpact(anomaly.getSecurityImpact());

        if (anomalyType == AnomalyType.OCCURRENCE) {
            anomalyDB.setQualifiedEntity(anomaly.getQualifiedEntity());
        }

        if (anomaly.getClosedDate() != null) {
            //check if this anomaly has open corrective actions
            //if so, it can not be closed
            if (anomalyDAO.hasOpenCorrectiveActions(anomaly.getId())) {
                throw new BusinessException("Can't close the anomaly because it has corrective actions that are still open.");
            }
            anomalyDB.setClosedDate(anomaly.getClosedDate());

            //send notifications
            try {
                sendEmailsForClosing(anomalyDB, loggedUser);
            } catch (Exception ex) {
                LOGGER.error("Error while sending notification emails " + ex);
            }
        }

        Calendar calendar = Calendar.getInstance();
        anomaly.setChangedDate(calendar.getTime());
        anomaly.setChangedBy(loggedUser);

        //save documents
        for (DocumentDTO doc : newDocuments) {
            Document document = new Document();
            document.setContract(contract);
            document.setAnomaly(anomalyDB);
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
    public void reopenAnomaly(Long contractId, Long anomalyId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get anomaly from DB
        Anomaly anomaly = anomalyDAO.findAnomaly(anomalyId);
        if (anomaly == null) {
            throw new ObjectNotFoundException("Can't find the anomaly with the specified id.",
                    ObjectNotFoundException.Type.SM_ANOMALY);
        }
        anomaly.setClosedDate(null);
        Calendar calendar = Calendar.getInstance();
        anomaly.setChangedDate(calendar.getTime());
        anomaly.setChangedBy(loggedUser);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteAnomaly(Long contractId, Long anomalyId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get anomaly from DB
        Anomaly anomaly = anomalyDAO.findAnomaly(anomalyId);
        if (anomaly == null) {
            throw new ObjectNotFoundException("Can't find the anomaly with the specified id.",
                    ObjectNotFoundException.Type.SM_ANOMALY);
        }
        //set anomaly as deleted
        anomaly.setDeleted(true);
        Calendar calendar = Calendar.getInstance();
        anomaly.setChangedDate(calendar.getTime());
        anomaly.setChangedBy(loggedUser);
        //delete related documents
        anomalyDAO.deleteAnomalyDocuments(anomalyId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteDocument(Long contractId, Long anomalyId, Long documentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserIntermediate(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert " +
                    "or intermediate permission");
        }
        Document document = documentDAO.findById(documentId);
        if (document == null || document.getAnomaly() == null || !document.getAnomaly().getId().equals(anomalyId)) {
            throw new ObjectNotFoundException("Can't find the document with the specified id.",
                    ObjectNotFoundException.Type.SM_DOCUMENT);
        }
        if (document.getAnomaly().getClosed()) {
            throw new BusinessException("Can't update closed anomaly.");
        }
        //delete the document
        documentDAO.delete(document);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void addChatMessage(Long contractId, Long anomalyId, String message, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserBasic(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert " +
                    "or intermediate permission");
        }
        //get anomaly from DB
        Anomaly anomaly = anomalyDAO.findAnomaly(anomalyId);
        if (anomaly == null) {
            throw new ObjectNotFoundException("Can't find the anomaly with the specified id.",
                    ObjectNotFoundException.Type.SM_ANOMALY);
        }
        if (anomaly.getClosed()) {
            throw new BusinessException("Can't update closed anomaly.");
        }

        Chat chat = new Chat();
        chat.setAnomaly(anomaly);
        chat.setMessage(message);
        chat.setUser(loggedUser);
        chat.setDatetime(new Date());
        chatDAO.insert(chat);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Chat> findChatMessages(Long anomalyId) {
        return anomalyDAO.findChatMessages(anomalyId);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<CorrectiveAction> findOpenCorrectiveActions(Long anomalyId) {
        return anomalyDAO.findAnomalyOpenCorrectiveActions(anomalyId);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Anomaly findAnomaly(Long anomalyId) throws ObjectNotFoundException {
        Anomaly anomaly = anomalyDAO.findAnomaly(anomalyId);
        if (anomaly == null) {
            throw new ObjectNotFoundException("Can't find the anomaly with the specified id.",
                    ObjectNotFoundException.Type.SM_ANOMALY);
        }
        //set documents
        anomaly.setDocuments(anomalyDAO.findAnomalyDocuments(anomalyId));
        return anomaly;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Anomaly findAnomalyForChatPdf(Long anomalyId) throws ObjectNotFoundException {
        Anomaly anomaly = anomalyDAO.findAnomalyWithContract(anomalyId);
        if (anomaly == null) {
            throw new ObjectNotFoundException("Can't find the anomaly with the specified id.",
                    ObjectNotFoundException.Type.SM_ANOMALY);
        }
        //set chat messages
        anomaly.setChatMessages(anomalyDAO.findChatMessages(anomalyId));
        return anomaly;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Anomaly findAnomalyForReportPdf(Long anomalyId) throws ObjectNotFoundException {
        Anomaly anomaly = anomalyDAO.findAnomalyWithContract(anomalyId);
        if (anomaly == null) {
            throw new ObjectNotFoundException("Can't find the anomaly with the specified id.",
                    ObjectNotFoundException.Type.SM_ANOMALY);
        }
        //set corrective actions
        anomaly.setCorrectiveActions(anomalyDAO.findAnomalyCorrectiveActions(anomalyId));
        //set documents
        anomaly.setDocuments(anomalyDAO.findAnomalyDocuments(anomalyId));
        return anomaly;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public PaginatedListWrapper<Anomaly> findAnomaliesByContract(long contractId,
                                                                 PaginatedListWrapper<Anomaly> paginatedListWrapper,
                                                                 AnomalyType anomalyType, String filterYear,
                                                                 Boolean isOpen)
            throws BusinessException {

        if (paginatedListWrapper == null) {
            throw new BusinessException("No PaginatedListWrapper initialized ");
        }

        if (paginatedListWrapper.getSortCriterion() == null) {
            paginatedListWrapper.setSortCriterion("datetime");
        }
        if (paginatedListWrapper.getSortDirection() == null) {
            paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.DESC);
        }

        int count = anomalyDAO.countAll(contractId, anomalyType, filterYear, isOpen);
        paginatedListWrapper.setFullListSize(count);

        List<Anomaly> anomalies;
        if (paginatedListWrapper.getExport()) {
            anomalies = anomalyDAO.findAll(contractId, 0, count,
                    paginatedListWrapper.getSortCriterion(), paginatedListWrapper.getSortDirection().value(),
                    anomalyType, filterYear, isOpen);
        } else {
            anomalies = anomalyDAO.findAll(contractId, paginatedListWrapper.getOffset(),
                    paginatedListWrapper.getResultsPerPage(),
                    paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper.getSortDirection().value(), anomalyType, filterYear, isOpen);
        }

        for (Anomaly anomaly : anomalies) { // force the collection to be loaded to avoid lazy loading exceptions
            anomaly.setCorrectiveActions(anomalyDAO.findAnomalyCorrectiveActions(anomaly.getId()));
            anomaly.setDocuments(anomalyDAO.findAnomalyDocuments(anomaly.getId()));
            if (anomaly.getClosed()) {
                //this is for the validation of the chat log pdf
                anomaly.setHasChatMessages(anomalyDAO.countChatMessages(anomaly.getId()) != 0);
            }
        }

        paginatedListWrapper.setList(anomalies);

        return paginatedListWrapper;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Anomaly> findAnomaliesByContract(long contractId, String filterYear, String filterSemester, Boolean isOpen) {

        List<Anomaly> anomalies = anomalyDAO.findAll(contractId, filterYear, filterSemester, isOpen);

        for (Anomaly anomaly : anomalies) { // force the collection to be loaded to avoid lazy loading exceptions
            anomaly.setCorrectiveActions(anomalyDAO.findAnomalyCorrectiveActions(anomaly.getId()));
            anomaly.setDocuments(anomalyDAO.findAnomalyDocuments(anomaly.getId()));
            if (anomaly.getClosed()) {
                anomaly.setChatMessages(anomalyDAO.findChatMessages(anomaly.getId()));
                anomaly.setHasChatMessages(anomaly.getChatMessages().size() != 0);
            }
        }
        return anomalies;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<String> findAnomalyYears(long contractId) {
        return anomalyDAO.findAnomalyYears(contractId);
    }

    private String generateSequenceCode(Long contractId, AnomalyType anomalyType) throws BusinessException {
        try {
            return sequenceGenerator.generateCode(SequenceCode.valueOf(anomalyType.name()), contractId);
        } catch (InvalidSequenceException e) {
            throw new BusinessException("Error generating sequence code for anomaly.");
        }
    }

    private String getUrlForMail(Anomaly anomaly) {
        return Configuration.getInstance().getApplicationDomain() + "/sm/SecurityAnomaliesRecord.action?"
                + (anomaly.getAnomalyType() == AnomalyType.ANOMALY ? "anomaliesRecordEdit=" : "occurrencesRecordEdit")
                + "&contractId=" + anomaly.getContract().getId()
                + "&anomalyId=" + anomaly.getId().toString();
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

    private void sendEmailsForCreation(final Anomaly anomaly, final User loggedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventCreatedEmails(EmailEventType.valueOf(anomaly.getAnomalyType().name()),
                        anomaly.getCode() + " - " + anomaly.getName(),
                        getUrlForMail(anomaly),
                        anomaly.getContract().getCompany().getName(),
                        anomaly.getContract().getContractDesignation(),
                        getUsersForMail(anomaly.getContract().getId(), loggedUser));
            }
        });
    }

    private void sendEmailsForClosing(final Anomaly anomaly, final User loggedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventClosedEmails(EmailEventType.valueOf(anomaly.getAnomalyType().name()),
                        anomaly.getCode() + " - " + anomaly.getName(),
                        getUrlForMail(anomaly),
                        anomaly.getContract().getCompany().getName(),
                        anomaly.getContract().getContractDesignation(),
                        getUsersForMail(anomaly.getContract().getId(), loggedUser));
            }
        });
    }

}
