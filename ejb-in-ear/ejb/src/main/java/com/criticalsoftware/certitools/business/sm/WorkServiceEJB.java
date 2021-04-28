package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.InvalidSequenceException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.persistence.certitools.ContractDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.sm.ChatDAO;
import com.criticalsoftware.certitools.persistence.sm.DocumentDAO;
import com.criticalsoftware.certitools.persistence.sm.WorkDAO;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.Risk;
import com.criticalsoftware.certitools.entities.sm.SecurityImpact;
import com.criticalsoftware.certitools.entities.sm.SecurityImpactWork;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.entities.sm.enums.WorkType;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Work Service EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(WorkService.class)
@LocalBinding(jndiBinding = "certitools/WorkService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class WorkServiceEJB implements WorkService {

    private static final Logger LOGGER = Logger.getInstance(WorkServiceEJB.class);

    @EJB
    private ContractDAO contractDAO;
    @EJB
    private WorkDAO workDAO;
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

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Risk> findRisks() {
        return workDAO.findRisks();
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Risk findRisk(Long riskId) throws ObjectNotFoundException {
        Risk risk = workDAO.findRisk(riskId);
        if (risk == null) {
            throw new ObjectNotFoundException("Can't find the risk with the specified id.",
                    ObjectNotFoundException.Type.SM_RISK);
        }
        return risk;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public Long createSecurityImpactWork(Long contractId, SecurityImpactWork securityImpactWork, WorkType workType, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)) {
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

        SecurityImpactWork newSecurityImpactWork = new SecurityImpactWork();
        newSecurityImpactWork.setContract(contract);
        newSecurityImpactWork.setCode(generateSequenceCode(contractId, workType));
        newSecurityImpactWork.setStartDate(securityImpactWork.getStartDate());
        newSecurityImpactWork.setName(securityImpactWork.getName());
        newSecurityImpactWork.setDescription(securityImpactWork.getDescription());
        newSecurityImpactWork.setResponsible(securityImpactWork.getResponsible());
        Calendar calendar = Calendar.getInstance();
        newSecurityImpactWork.setCreationDate(calendar.getTime());
        newSecurityImpactWork.setChangedDate(calendar.getTime());
        newSecurityImpactWork.setCreatedBy(loggedUser);
        newSecurityImpactWork.setChangedBy(loggedUser);
        newSecurityImpactWork.setDeleted(false);

        newSecurityImpactWork.setWorkType(workType);
        if (workType == WorkType.WORK_AUTHORIZATION) {
            newSecurityImpactWork.setDuration(securityImpactWork.getDuration());
            List<Risk> risks = new ArrayList<Risk>();
            for (Risk r : securityImpactWork.getRisks()) {
                risks.add(workDAO.getEntityReference(Risk.class, r.getId()));
            }
            newSecurityImpactWork.setRisks(risks);
        } else {
            //set default security impact
            newSecurityImpactWork.setSecurityImpact(workDAO.getEntityReference(SecurityImpact.class, 1L));
        }

        workDAO.insert(newSecurityImpactWork);

        //send notifications
        String modificationName = "";
        if (workType == WorkType.MODIFICATION) {
            modificationName = " - " + securityImpactWork.getName();
        }
        try {
            sendEmailsForCreation(newSecurityImpactWork, loggedUser, modificationName);
        } catch (Exception ex) {
            LOGGER.error("Error while sending notification emails " + ex);
        }

        return newSecurityImpactWork.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void updateSecurityImpactWorkMainFields(Long contractId, SecurityImpactWork securityImpactWork, WorkType workType, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)) {
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
        //get security impact work from DB
        SecurityImpactWork securityImpactWorkDB = workDAO.findSecurityImpactWork(securityImpactWork.getId());
        if (securityImpactWorkDB == null) {
            throw new ObjectNotFoundException("Can't find the security impact work with the specified id.",
                    ObjectNotFoundException.Type.SM_SECURITY_IMPACT_WORK);
        }
        if (securityImpactWorkDB.getClosed()) {
            throw new BusinessException("Can't update closed security impact work.");
        }

        securityImpactWorkDB.setStartDate(securityImpactWork.getStartDate());
        securityImpactWorkDB.setName(securityImpactWork.getName());
        securityImpactWorkDB.setDescription(securityImpactWork.getDescription());
        securityImpactWorkDB.setResponsible(securityImpactWork.getResponsible());
        Calendar calendar = Calendar.getInstance();
        securityImpactWorkDB.setChangedDate(calendar.getTime());
        securityImpactWorkDB.setChangedBy(loggedUser);

        if (workType == WorkType.WORK_AUTHORIZATION) {
            securityImpactWorkDB.setDuration(securityImpactWork.getDuration());
            List<Risk> risks = new ArrayList<Risk>();
            for (Risk r : securityImpactWork.getRisks()) {
                risks.add(workDAO.getEntityReference(Risk.class, r.getId()));
            }
            securityImpactWorkDB.setRisks(risks);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void editSecurityImpactWork(Long contractId, SecurityImpactWork securityImpactWork, WorkType workType,
                                       List<DocumentDTO> newDocuments, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)) {
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
        //get security impact work from DB
        SecurityImpactWork securityImpactWorkDB = workDAO.findSecurityImpactWork(securityImpactWork.getId());
        if (securityImpactWorkDB == null) {
            throw new ObjectNotFoundException("Can't find the security impact work with the specified id.",
                    ObjectNotFoundException.Type.SM_SECURITY_IMPACT_WORK);
        }
        if (securityImpactWorkDB.getClosed()) {
            throw new BusinessException("Can't update closed security impact work.");
        }

        if (workType == WorkType.MODIFICATION) {
            securityImpactWorkDB.setSecurityImpact(workDAO.getEntityReference(SecurityImpact.class, securityImpactWork.getSecurityImpact().getId()));
            securityImpactWorkDB.setQualifiedEntity(securityImpactWork.getQualifiedEntity());
        }

        if (securityImpactWork.getClosedDate() != null) {
            //check if this security impact work has open corrective actions
            //if so, it can not be closed
            if (workDAO.hasOpenCorrectiveActions(securityImpactWork.getId())) {
                throw new BusinessException("Can't close the security impact work because it has corrective actions that are still open.");
            }
            securityImpactWorkDB.setClosedDate(securityImpactWork.getClosedDate());

            //send notifications
            String modificationName = "";
            if (workType == WorkType.MODIFICATION) {
                modificationName = " - " + securityImpactWork.getName();
            }
            try {
                sendEmailsForClosing(securityImpactWorkDB, loggedUser, modificationName);
            } catch (Exception ex) {
                LOGGER.error("Error while sending notification emails " + ex);
            }
        }

        Calendar calendar = Calendar.getInstance();
        securityImpactWorkDB.setChangedDate(calendar.getTime());
        securityImpactWorkDB.setChangedBy(loggedUser);

        //save documents
        for (DocumentDTO doc : newDocuments) {
            Document document = new Document();
            document.setContract(contract);
            document.setSecurityImpactWork(securityImpactWorkDB);
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
    public void reopenSecurityImpactWork(Long contractId, Long workId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get security impact work from DB
        SecurityImpactWork securityImpactWork = workDAO.findSecurityImpactWork(workId);
        if (securityImpactWork == null) {
            throw new ObjectNotFoundException("Can't find the security impact work with the specified id.",
                    ObjectNotFoundException.Type.SM_SECURITY_IMPACT_WORK);
        }
        securityImpactWork.setClosedDate(null);
        Calendar calendar = Calendar.getInstance();
        securityImpactWork.setChangedDate(calendar.getTime());
        securityImpactWork.setChangedBy(loggedUser);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteSecurityImpactWork(Long contractId, Long workId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get security impact work from DB
        SecurityImpactWork securityImpactWork = workDAO.findSecurityImpactWork(workId);
        if (securityImpactWork == null) {
            throw new ObjectNotFoundException("Can't find the security impact work with the specified id.",
                    ObjectNotFoundException.Type.SM_SECURITY_IMPACT_WORK);
        }
        //set security impact work as deleted
        securityImpactWork.setDeleted(true);
        Calendar calendar = Calendar.getInstance();
        securityImpactWork.setChangedDate(calendar.getTime());
        securityImpactWork.setChangedBy(loggedUser);
        //delete related documents
        workDAO.deleteSecurityImpactWorkDocuments(workId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteDocument(Long contractId, Long workId, Long documentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserIntermediate(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert " +
                    "or intermediate permission");
        }
        Document document = documentDAO.findById(documentId);
        if (document == null || document.getSecurityImpactWork() == null || !document.getSecurityImpactWork().getId().equals(workId)) {
            throw new ObjectNotFoundException("Can't find the document with the specified id.",
                    ObjectNotFoundException.Type.SM_DOCUMENT);
        }
        if (document.getSecurityImpactWork().getClosed()) {
            throw new BusinessException("Can't update closed security impact work.");
        }
        //delete the document
        documentDAO.delete(document);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void addChatMessage(Long contractId, Long workId, String message, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserBasic(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert " +
                    "or intermediate permission");
        }
        //get security impact work from DB
        SecurityImpactWork securityImpactWork = workDAO.findSecurityImpactWork(workId);
        if (securityImpactWork == null) {
            throw new ObjectNotFoundException("Can't find the security impact work with the specified id.",
                    ObjectNotFoundException.Type.SM_SECURITY_IMPACT_WORK);
        }
        if (securityImpactWork.getClosed()) {
            throw new BusinessException("Can't update closed security impact work.");
        }
        Chat chat = new Chat();
        chat.setSecurityImpactWork(securityImpactWork);
        chat.setMessage(message);
        chat.setUser(loggedUser);
        chat.setDatetime(new Date());
        chatDAO.insert(chat);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Chat> findChatMessages(Long workId) {
        return workDAO.findChatMessages(workId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<CorrectiveAction> findOpenCorrectiveActions(Long workId) {
        return workDAO.findOpenCorrectiveActions(workId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public SecurityImpactWork findSecurityImpactWork(Long workId) throws ObjectNotFoundException {
        SecurityImpactWork securityImpactWork = workDAO.findSecurityImpactWork(workId);
        if (securityImpactWork == null) {
            throw new ObjectNotFoundException("Can't find the security impact work with the specified id.",
                    ObjectNotFoundException.Type.SM_SECURITY_IMPACT_WORK);
        }
        //set risks
        securityImpactWork.setRisks(workDAO.findSecurityImpactWorkRisks(workId));
        //set documents
        securityImpactWork.setDocuments(workDAO.findDocuments(workId));
        return securityImpactWork;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public SecurityImpactWork findSecurityImpactWorkForChatPdf(Long workId) throws ObjectNotFoundException {
        SecurityImpactWork securityImpactWork = workDAO.findSecurityImpactWorkWithContract(workId);
        if (securityImpactWork == null) {
            throw new ObjectNotFoundException("Can't find the security impact work with the specified id.",
                    ObjectNotFoundException.Type.SM_SECURITY_IMPACT_WORK);
        }
        //set chat messages
        securityImpactWork.setChatMessages(workDAO.findChatMessages(workId));
        return securityImpactWork;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public SecurityImpactWork findSecurityImpactWorkForReportPdf(Long workId) throws ObjectNotFoundException {
        SecurityImpactWork securityImpactWork = workDAO.findSecurityImpactWorkWithContract(workId);
        if (securityImpactWork == null) {
            throw new ObjectNotFoundException("Can't find the security impact work with the specified id.",
                    ObjectNotFoundException.Type.SM_SECURITY_IMPACT_WORK);
        }
        //set risks
        securityImpactWork.setRisks(workDAO.findSecurityImpactWorkRisks(workId));
        //set corrective actions
        securityImpactWork.setCorrectiveActions(workDAO.findCorrectiveActions(workId));
        //set documents
        securityImpactWork.setDocuments(workDAO.findDocuments(workId));
        return securityImpactWork;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public PaginatedListWrapper<SecurityImpactWork> findSecurityImpactWorksByContract(long contractId,
                                                                                      PaginatedListWrapper<SecurityImpactWork> paginatedListWrapper,
                                                                                      WorkType workType, String filterYear,
                                                                                      Boolean isOpen)
            throws BusinessException {

        if (paginatedListWrapper == null) {
            throw new BusinessException("No PaginatedListWrapper initialized ");
        }

        if (paginatedListWrapper.getSortCriterion() == null) {
            paginatedListWrapper.setSortCriterion("startDate");
        }
        if (paginatedListWrapper.getSortDirection() == null) {
            paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.DESC);
        }

        int count = workDAO.countAll(contractId, workType, filterYear, isOpen);
        paginatedListWrapper.setFullListSize(count);

        List<SecurityImpactWork> securityImpactWorks;
        if (paginatedListWrapper.getExport()) {
            securityImpactWorks = workDAO.findAll(contractId, 0, count,
                    paginatedListWrapper.getSortCriterion(), paginatedListWrapper.getSortDirection().value(),
                    workType, filterYear, isOpen);
        } else {
            securityImpactWorks = workDAO.findAll(contractId, paginatedListWrapper.getOffset(),
                    paginatedListWrapper.getResultsPerPage(),
                    paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper.getSortDirection().value(), workType, filterYear, isOpen);
        }

        for (SecurityImpactWork securityImpactWork : securityImpactWorks) { // force the collection to be loaded to avoid lazy loading exceptions
            securityImpactWork.setCorrectiveActions(workDAO.findCorrectiveActions(securityImpactWork.getId()));
            securityImpactWork.setDocuments(workDAO.findDocuments(securityImpactWork.getId()));
            if (securityImpactWork.getClosed()) {
                //this is for the validation of the chat log pdf
                securityImpactWork.setHasChatMessages(workDAO.countChatMessages(securityImpactWork.getId()) != 0);
            }
        }

        paginatedListWrapper.setList(securityImpactWorks);

        return paginatedListWrapper;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<SecurityImpactWork> findSecurityImpactWorksByContract(long contractId, String filterYear,
                                                                      String filterSemester, Boolean isOpen) {

        List<SecurityImpactWork> securityImpactWorks = workDAO.findAll(contractId, filterYear, filterSemester, isOpen);

        for (SecurityImpactWork securityImpactWork : securityImpactWorks) { // force the collection to be loaded to avoid lazy loading exceptions
            securityImpactWork.setCorrectiveActions(workDAO.findCorrectiveActions(securityImpactWork.getId()));
            securityImpactWork.setDocuments(workDAO.findDocuments(securityImpactWork.getId()));
            if (securityImpactWork.getClosed()) {
                securityImpactWork.setChatMessages(workDAO.findChatMessages(securityImpactWork.getId()));
                securityImpactWork.setHasChatMessages(securityImpactWork.getChatMessages().size() != 0);
            }
            securityImpactWork.setRisks(workDAO.findSecurityImpactWorkRisks(securityImpactWork.getId()));
        }
        return securityImpactWorks;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<String> findSecurityImpactWorkYears(long contractId) {
        return workDAO.findSecurityImpactWorkYears(contractId);
    }

    private String generateSequenceCode(Long contractId, WorkType workType) throws BusinessException {
        try {
            return sequenceGenerator.generateCode(SequenceCode.valueOf(workType.name()), contractId);
        } catch (InvalidSequenceException e) {
            throw new BusinessException("Error generating sequence code for security impact work.");
        }
    }

    private String getUrlForMail(SecurityImpactWork securityImpactWork) {
        return Configuration.getInstance().getApplicationDomain() + "/sm/SecurityImpactWork.action?"
                + (securityImpactWork.getWorkType() == WorkType.MODIFICATION ? "modificationsChangesEdit=" : "authorizationEdit=")
                + "&contractId=" + securityImpactWork.getContract().getId() + "&impactWorkId="
                + securityImpactWork.getId().toString();
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

    private void sendEmailsForCreation(final SecurityImpactWork securityImpactWork, final User loggedUser, final String modificationName) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventCreatedEmails(EmailEventType.valueOf(securityImpactWork.getWorkType().name()),
                        securityImpactWork.getCode() + modificationName,
                        getUrlForMail(securityImpactWork),
                        securityImpactWork.getContract().getCompany().getName(),
                        securityImpactWork.getContract().getContractDesignation(),
                        getUsersForMail(securityImpactWork.getContract().getId(), loggedUser));
            }
        });
    }

    private void sendEmailsForClosing(final SecurityImpactWork securityImpactWork, final User loggedUser, final String modificationName) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventClosedEmails(EmailEventType.valueOf(securityImpactWork.getWorkType().name()),
                        securityImpactWork.getCode() + modificationName,
                        getUrlForMail(securityImpactWork),
                        securityImpactWork.getContract().getCompany().getName(),
                        securityImpactWork.getContract().getContractDesignation(),
                        getUsersForMail(securityImpactWork.getContract().getId(), loggedUser));
            }
        });
    }
}
