package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.InvalidSequenceException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.EmergencyAction;
import com.criticalsoftware.certitools.entities.sm.EmergencyToken;
import com.criticalsoftware.certitools.entities.sm.EmergencyUser;
import com.criticalsoftware.certitools.persistence.certitools.ContractDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.sm.ChatDAO;
import com.criticalsoftware.certitools.persistence.sm.EmergencyActionDAO;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.MailSender;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.criticalsoftware.certitools.util.enums.EmailEventType;
import com.criticalsoftware.certitools.util.enums.SequenceCode;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Emergency Action Service EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(EmergencyActionService.class)
@LocalBinding(jndiBinding = "certitools/EmergencyActionService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class EmergencyActionServiceEJB implements EmergencyActionService {

    private static final Logger LOGGER = Logger.getInstance(EmergencyActionServiceEJB.class);

    @EJB
    private ContractDAO contractDAO;
    @EJB
    private EmergencyActionDAO emergencyActionDAO;
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
    public List<EmergencyUser> findEmergencyUsers(Long contractId) {
        return emergencyActionDAO.findEmergencyUsers(contractId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public EmergencyUser createEmergencyUser(Long contractId, EmergencyUser emergencyUser, User loggedUser)
            throws CertitoolsAuthorizationException, com.criticalsoftware.certitools.business.exception.ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get current contract
        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            throw new com.criticalsoftware.certitools.business.exception.ObjectNotFoundException("Can't find the contract with the specified id.",
                    com.criticalsoftware.certitools.business.exception.ObjectNotFoundException.Type.CONTRACT);
        }

        EmergencyUser newEmergencyUser = new EmergencyUser();
        newEmergencyUser.setContract(contract);
        newEmergencyUser.setName(emergencyUser.getName());
        newEmergencyUser.setEmail(emergencyUser.getEmail());
        newEmergencyUser.setDeleted(false);
        emergencyActionDAO.insertEmergencyUser(newEmergencyUser);

        //check if there are open emergency actions
        //if so, create access tokens for this emergency user
        List<EmergencyAction> emergencyActions = emergencyActionDAO.findOpenEmergencyActions(contractId);
        if (emergencyActions != null && !emergencyActions.isEmpty()) {
            for (EmergencyAction emergencyAction : emergencyActions) {
                EmergencyToken emergencyToken = new EmergencyToken();
                emergencyToken.setEmergencyAction(emergencyAction);
                emergencyToken.setEmergencyUser(newEmergencyUser);
                String token = UUID.randomUUID().toString();
                emergencyToken.setAccessToken(token);
                emergencyActionDAO.insertEmergencyToken(emergencyToken);
                //send email for the emergency user
                try {
                    sendEmailsForCreation(emergencyAction, contract, emergencyToken);
                } catch (Exception ex) {
                    LOGGER.error("Error while sending notification emails " + ex);
                }
            }
        }
        return newEmergencyUser;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteEmergencyUser(Long contractId, Long emergencyUserId, User loggedUser)
            throws CertitoolsAuthorizationException, com.criticalsoftware.certitools.business.exception.ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //delete user tokens
        emergencyActionDAO.deleteEmergencyUserTokens(emergencyUserId);
        //delete emergency user
        if (emergencyActionDAO.deleteEmergencyUser(emergencyUserId) == 0) {
            throw new com.criticalsoftware.certitools.business.exception.ObjectNotFoundException("Can't find the emergency user with the specified id.",
                    com.criticalsoftware.certitools.business.exception.ObjectNotFoundException.Type.SM_EMERGENCY_USER);
        }
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public boolean existsEmergencyUserByEmailAndContract(Long contractId, String email) {
        return emergencyActionDAO.existsEmergencyUserByEmailAndContract(contractId, email);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public Long createEmergencyAction(Long contractId, EmergencyAction emergencyAction, User loggedUser)
            throws CertitoolsAuthorizationException, com.criticalsoftware.certitools.business.exception.ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserBasic(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have basic permission");
        }
        //get current contract
        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            throw new com.criticalsoftware.certitools.business.exception.ObjectNotFoundException("Can't find the contract with the specified id.",
                    com.criticalsoftware.certitools.business.exception.ObjectNotFoundException.Type.CONTRACT);
        }

        EmergencyAction newEmergencyAction = new EmergencyAction();
        newEmergencyAction.setContract(contract);
        newEmergencyAction.setCode(generateSequenceCode(contractId));
        newEmergencyAction.setStartDate(emergencyAction.getStartDate());
        newEmergencyAction.setOrigin(emergencyAction.getOrigin());
        newEmergencyAction.setDescription(emergencyAction.getDescription());
        Calendar calendar = Calendar.getInstance();
        newEmergencyAction.setCreationDate(calendar.getTime());
        newEmergencyAction.setChangedDate(calendar.getTime());
        newEmergencyAction.setCreatedBy(loggedUser);
        newEmergencyAction.setChangedBy(loggedUser);
        newEmergencyAction.setDeleted(false);
        emergencyActionDAO.insert(newEmergencyAction);

        //create access tokens for the emergency users
        List<EmergencyToken> emergencyTokens = new ArrayList<EmergencyToken>();
        List<EmergencyUser> emergencyUsers = emergencyActionDAO.findEmergencyUsers(contractId);
        for (EmergencyUser emergencyUser : emergencyUsers) {
            EmergencyToken emergencyToken = new EmergencyToken();
            emergencyToken.setEmergencyAction(newEmergencyAction);
            emergencyToken.setEmergencyUser(emergencyUser);
            String token = UUID.randomUUID().toString();
            emergencyToken.setAccessToken(token);
            emergencyActionDAO.insertEmergencyToken(emergencyToken);
            emergencyTokens.add(emergencyToken);
        }

        //create access tokens for the expert and intermediate users of this contract
        List<User> users = userDAO.findExpertAndIntermediateUsersByContract(contractId);
        for (User user : users) {
            //exclude the logged user
            if (!user.getEmail().equals(loggedUser.getEmail())) {
                EmergencyToken emergencyToken = new EmergencyToken();
                emergencyToken.setEmergencyAction(newEmergencyAction);
                emergencyToken.setUser(user);
                String token = UUID.randomUUID().toString();
                emergencyToken.setAccessToken(token);
                emergencyActionDAO.insertEmergencyToken(emergencyToken);
                emergencyTokens.add(emergencyToken);
            }
        }

        //send emails
        try {
            sendEmailsForCreation(newEmergencyAction, emergencyTokens);
        } catch (Exception ex) {
            LOGGER.error("Error while sending notification emails " + ex);
        }

        return newEmergencyAction.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateEmergencyAction(Long contractId, EmergencyAction emergencyAction, User loggedUser, String token)
            throws CertitoolsAuthorizationException, com.criticalsoftware.certitools.business.exception.ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (loggedUser == null && token == null) {
            throw new CertitoolsAuthorizationException("User must be logged in or have valid access token.");
        }
        if (loggedUser != null && !securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have permission.");
        }
        EmergencyToken emergencyToken;
        if (loggedUser == null) {
            emergencyToken = emergencyActionDAO.findEmergencyToken(emergencyAction.getId(), token);
            if (emergencyToken == null || emergencyToken.getUser() == null ||
                    !securityManagementService.isUserExpert(emergencyToken.getUser().getId(), contractId)) {
                throw new CertitoolsAuthorizationException("User doesn't have valid access token or it doesn't have permission.");
            }
            loggedUser = emergencyToken.getUser();
        }

        //get current contract
        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            throw new com.criticalsoftware.certitools.business.exception.ObjectNotFoundException("Can't find the contract with the specified id.",
                    com.criticalsoftware.certitools.business.exception.ObjectNotFoundException.Type.CONTRACT);
        }
        //get emergency action from DB
        EmergencyAction emergencyActionDB = emergencyActionDAO.findEmergencyAction(emergencyAction.getId());
        if (emergencyActionDB == null) {
            throw new com.criticalsoftware.certitools.business.exception.ObjectNotFoundException("Can't find the emergency action with the specified id.",
                    com.criticalsoftware.certitools.business.exception.ObjectNotFoundException.Type.SM_EMERGENCY_ACTION);
        }
        if (emergencyActionDB.getClosed()) {
            throw new BusinessException("Can't update closed emergency action.");
        }

        emergencyActionDB.setStartDate(emergencyAction.getStartDate());
        emergencyActionDB.setOrigin(emergencyAction.getOrigin());
        emergencyActionDB.setDescription(emergencyAction.getDescription());
        Calendar calendar = Calendar.getInstance();
        emergencyActionDB.setChangedDate(calendar.getTime());
        emergencyActionDB.setChangedBy(loggedUser);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void closeEmergencyAction(Long contractId, Long emergencyId, User loggedUser, String token)
            throws CertitoolsAuthorizationException, com.criticalsoftware.certitools.business.exception.ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (loggedUser == null && token == null) {
            throw new CertitoolsAuthorizationException("User must be logged in or have valid access token.");
        }
        if (loggedUser != null && !securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserBasic(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have permission.");
        }
        EmergencyToken emergencyToken;
        if (loggedUser == null) {
            emergencyToken = emergencyActionDAO.findEmergencyToken(emergencyId, token);
            if (emergencyToken == null || emergencyToken.getUser() == null) {
                throw new CertitoolsAuthorizationException("User doesn't have valid access token.");
            }
            loggedUser = emergencyToken.getUser();
        }
        //get emergency action from DB
        EmergencyAction emergencyAction = emergencyActionDAO.findEmergencyActionWithContract(emergencyId);
        if (emergencyAction == null || emergencyAction.getContract().getId() != contractId) {
            throw new com.criticalsoftware.certitools.business.exception.ObjectNotFoundException("Can't find the emergency action with the specified id.",
                    com.criticalsoftware.certitools.business.exception.ObjectNotFoundException.Type.SM_EMERGENCY_ACTION);
        }
        //check if the user has permission to close this emergency action
        if (!hasPermissionToClose(contractId, emergencyId, loggedUser)) {
            throw new CertitoolsAuthorizationException("User does not have permission to close this emergency action.");
        }

        //close emergency action
        Calendar calendar = Calendar.getInstance();
        emergencyAction.setClosedDate(calendar.getTime());
        emergencyAction.setChangedDate(calendar.getTime());
        emergencyAction.setChangedBy(loggedUser);
        //delete tokens
        emergencyActionDAO.deleteEmergencyActionTokens(emergencyId);

        //send emails
        try {
            sendEmailsForClosing(emergencyAction, loggedUser);
        } catch (Exception ex) {
            LOGGER.error("Error while sending notification emails " + ex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteEmergencyAction(Long contractId, Long emergencyId, User loggedUser)
            throws CertitoolsAuthorizationException, com.criticalsoftware.certitools.business.exception.ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get emergency action from DB
        EmergencyAction emergencyAction = emergencyActionDAO.findEmergencyAction(emergencyId);
        if (emergencyAction == null) {
            throw new com.criticalsoftware.certitools.business.exception.ObjectNotFoundException("Can't find the emergency action with the specified id.",
                    com.criticalsoftware.certitools.business.exception.ObjectNotFoundException.Type.SM_EMERGENCY_ACTION);
        }
        //set emergency action as deleted
        emergencyAction.setDeleted(true);
        Calendar calendar = Calendar.getInstance();
        emergencyAction.setChangedDate(calendar.getTime());
        emergencyAction.setChangedBy(loggedUser);

        //delete tokens
        emergencyActionDAO.deleteEmergencyActionTokens(emergencyId);
    }

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addChatMessage(Long contractId, Long emergencyId, String message, User loggedUser, String token)
            throws com.criticalsoftware.certitools.business.exception.ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (loggedUser == null && token == null) {
            throw new CertitoolsAuthorizationException("User must be logged in or have valid access token.");
        }
        if (loggedUser != null && !securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserBasic(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have permission.");
        }
        EmergencyToken emergencyToken = null;
        if (loggedUser == null) {
            emergencyToken = emergencyActionDAO.findEmergencyToken(emergencyId, token);
            if (emergencyToken == null) {
                throw new CertitoolsAuthorizationException("User doesn't have valid access token.");
            }
        }

        //get emergency action from DB
        EmergencyAction emergencyAction = emergencyActionDAO.findEmergencyAction(emergencyId);
        if (emergencyAction == null) {
            throw new com.criticalsoftware.certitools.business.exception.ObjectNotFoundException("Can't find the emergency action with the specified id.",
                    com.criticalsoftware.certitools.business.exception.ObjectNotFoundException.Type.SM_EMERGENCY_ACTION);
        }
        if (emergencyAction.getClosed()) {
            throw new BusinessException("Can't update closed emergency action.");
        }

        Chat chat = new Chat();
        chat.setEmergencyAction(emergencyAction);
        chat.setMessage(message);
        if (loggedUser != null) {
            chat.setUser(loggedUser);
        } else if (emergencyToken.getUser() != null) {
            chat.setUser(emergencyToken.getUser());
        } else {
            chat.setEmergencyUser(emergencyToken.getEmergencyUser());
        }
        chat.setDatetime(new Date());
        chatDAO.insert(chat);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @PermitAll
    public List<Chat> findChatMessages(Long contractId, Long emergencyId, User loggedUser, String token)
            throws CertitoolsAuthorizationException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (loggedUser == null && token == null) {
            throw new CertitoolsAuthorizationException("User must be logged in or have valid access token.");
        }
        if (loggedUser != null && !securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserBasic(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have permission.");
        }
        EmergencyToken emergencyToken;
        if (loggedUser == null) {
            emergencyToken = emergencyActionDAO.findEmergencyToken(emergencyId, token);
            if (emergencyToken == null) {
                throw new CertitoolsAuthorizationException("User doesn't have valid access token.");
            }
        }
        return emergencyActionDAO.findChatMessages(emergencyId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @PermitAll
    public EmergencyAction findEmergencyAction(Long contractId, Long emergencyId, User loggedUser, String token)
            throws com.criticalsoftware.certitools.business.exception.ObjectNotFoundException, CertitoolsAuthorizationException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (loggedUser == null && token == null) {
            throw new CertitoolsAuthorizationException("User must be logged in or have valid access token.");
        }
        if (loggedUser != null && !securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserBasic(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have permission.");
        }
        if (loggedUser == null) {
            EmergencyToken emergencyToken = emergencyActionDAO.findEmergencyToken(emergencyId, token);
            if (emergencyToken == null) {
                throw new CertitoolsAuthorizationException("User doesn't have valid access token.");
            }
        }

        EmergencyAction emergencyAction = emergencyActionDAO.findEmergencyAction(emergencyId);
        if (emergencyAction == null) {
            throw new com.criticalsoftware.certitools.business.exception.ObjectNotFoundException("Can't find the emergency action with the specified id.",
                    com.criticalsoftware.certitools.business.exception.ObjectNotFoundException.Type.SM_EMERGENCY_ACTION);
        }
        return emergencyAction;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public EmergencyAction findEmergencyActionForReportPdf(Long emergencyId) throws com.criticalsoftware.certitools.business.exception.ObjectNotFoundException {
        EmergencyAction emergencyAction = emergencyActionDAO.findEmergencyActionWithContract(emergencyId);
        if (emergencyAction == null) {
            throw new com.criticalsoftware.certitools.business.exception.ObjectNotFoundException("Can't find the emergency action with the specified id.",
                    ObjectNotFoundException.Type.SM_EMERGENCY_ACTION);
        }
        //set chat messages
        emergencyAction.setChatMessages(emergencyActionDAO.findChatMessages(emergencyId));
        return emergencyAction;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public PaginatedListWrapper<EmergencyAction> findEmergencyActionsByContract(long contractId,
                                                                                PaginatedListWrapper<EmergencyAction> paginatedListWrapper,
                                                                                String filterYear, Boolean isOpen)
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

        int count = emergencyActionDAO.countAll(contractId, filterYear);
        paginatedListWrapper.setFullListSize(count);

        List<EmergencyAction> emergencyActions;
        if (paginatedListWrapper.getExport()) {
            emergencyActions = emergencyActionDAO.findAll(contractId, 0, count,
                    paginatedListWrapper.getSortCriterion(), paginatedListWrapper.getSortDirection().value(),
                    filterYear, isOpen);
        } else {
            emergencyActions = emergencyActionDAO.findAll(contractId, paginatedListWrapper.getOffset(),
                    paginatedListWrapper.getResultsPerPage(),
                    paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper.getSortDirection().value(), filterYear, isOpen);
        }

        for (EmergencyAction emergencyAction : emergencyActions) {
            if (emergencyAction.getClosed()) {
                //this is for the validation of the chat log csv
                emergencyAction.setHasChatMessages(emergencyActionDAO.countChatMessages(emergencyAction.getId()) != 0);
            }
        }

        paginatedListWrapper.setList(emergencyActions);

        return paginatedListWrapper;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<EmergencyAction> findEmergencyActionsByContract(long contractId, String filterYear,
                                                                String filterSemester, Boolean isOpen) {

        List<EmergencyAction> emergencyActions = emergencyActionDAO.findAll(contractId, filterYear,
                filterSemester, isOpen);

        for (EmergencyAction emergencyAction : emergencyActions) {
            if (emergencyAction.getClosed()) {
                emergencyAction.setChatMessages(emergencyActionDAO.findChatMessages(emergencyAction.getId()));
                emergencyAction.setHasChatMessages(emergencyAction.getChatMessages().size() != 0);
            }
        }
        return emergencyActions;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<String> findEmergencyActionYears(long contractId) {
        return emergencyActionDAO.findEmergencyActionYears(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @PermitAll
    public boolean isValidToken(Long emergencyId, String token) {
        return emergencyActionDAO.findEmergencyToken(emergencyId, token) != null;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public boolean hasPermissionToClose(Long contractId, Long emergencyId, User user, String token) {
        EmergencyToken emergencyToken;
        if (user == null && token == null) {
            return false;
        } else if (user == null) {
            emergencyToken = emergencyActionDAO.findEmergencyToken(emergencyId, token);
            if (emergencyToken == null || emergencyToken.getUser() == null) {
                return false;
            }
            user = emergencyToken.getUser();
        }

        return hasPermissionToClose(contractId, emergencyId, user);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public boolean hasPermissionToEdit(Long contractId, Long emergencyId, String token) {
        EmergencyToken emergencyToken = emergencyActionDAO.findEmergencyToken(emergencyId, token);
        return emergencyToken != null && emergencyToken.getUser() != null
                && securityManagementService.isUserExpert(emergencyToken.getUser().getId(), contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    private boolean hasPermissionToClose(Long contractId, Long emergencyId, User user) {
        if (securityManagementService.isUserAdministratorOrCertitecna(user.getId())) {
            return true;
        }
        if (!securityManagementService.isUserBasic(user.getId(), contractId)) {
            return false;
        }
        //check if the user has permission to close this emergency action
        //only a user with permission equal or superior to the user that created the record can close it
        User createdBy = emergencyActionDAO.findEmergencyActionCreatedByUser(emergencyId);
        if (securityManagementService.isUserExpert(createdBy.getId(), contractId) &&
                !securityManagementService.isUserExpert(user.getId(), contractId)) {
            return false;
        } else if (securityManagementService.isUserIntermediate(createdBy.getId(), contractId) &&
                !securityManagementService.isUserIntermediate(user.getId(), contractId)) {
            return false;
        }
        return true;
    }

    private String generateSequenceCode(Long contractId) throws BusinessException {
        try {
            return sequenceGenerator.generateCode(SequenceCode.EMERGENCY_ACTION, contractId);
        } catch (InvalidSequenceException e) {
            throw new BusinessException("Error generating sequence code for emergency action.");
        }
    }

    private void sendEmailsForCreation(final EmergencyAction emergencyAction, final List<EmergencyToken> emergencyTokens) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEmergencyActionCreatedEmails(emergencyAction.getCode(), getUrlForMail(emergencyAction),
                        emergencyAction.getContract().getCompany().getName(),
                        emergencyAction.getContract().getContractDesignation(),
                        emergencyTokens);
            }
        });
    }

    private void sendEmailsForCreation(final EmergencyAction emergencyAction, final  Contract contract,
                                       final EmergencyToken emergencyToken) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEmergencyActionCreatedEmails(emergencyAction.getCode(), getUrlForMail(emergencyAction),
                        contract.getCompany().getName(), contract.getContractDesignation(),
                        Collections.singletonList(emergencyToken));
            }
        });
    }

    private String getUrlForMail(EmergencyAction emergencyAction) {
        return Configuration.getInstance().getApplicationDomain() + "/sm/SecurityEmergency.action?"
                + "emergencyActionEdit=" + "&contractId=" + emergencyAction.getContract().getId()
                + "&emergencyId=" + emergencyAction.getId().toString();
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

    private void sendEmailsForClosing(final EmergencyAction emergencyAction, final User loggedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventClosedEmails(EmailEventType.EMERGENCY_ACTION, emergencyAction.getCode(),
                        getUrlForMail(emergencyAction),
                        emergencyAction.getContract().getCompany().getName(),
                        emergencyAction.getContract().getContractDesignation(),
                        getUsersForMail(emergencyAction.getContract().getId(), loggedUser));
            }
        });
    }
}
