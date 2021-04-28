package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.InvalidSequenceException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.persistence.certitools.ContractDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.sm.ChatDAO;
import com.criticalsoftware.certitools.persistence.sm.DocumentDAO;
import com.criticalsoftware.certitools.persistence.sm.MaintenanceDAO;
import com.criticalsoftware.certitools.persistence.sm.RecurrenceDAO;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.Equipment;
import com.criticalsoftware.certitools.entities.sm.Maintenance;
import com.criticalsoftware.certitools.entities.sm.MaintenanceType;
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
 * Maintenance Service EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(MaintenanceService.class)
@LocalBinding(jndiBinding = "certitools/MaintenanceService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class MaintenanceServiceEJB implements MaintenanceService {

    private static final Logger LOGGER = Logger.getInstance(MaintenanceServiceEJB.class);

    @EJB
    private ContractDAO contractDAO;
    @EJB
    private MaintenanceDAO maintenanceDAO;
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

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<MaintenanceType> findMaintenanceTypes(long contractId) {
        return maintenanceDAO.findMaintenanceTypes(contractId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public MaintenanceType createMaintenanceType(Long contractId, String name, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)) {
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

        MaintenanceType maintenanceType = new MaintenanceType();
        maintenanceType.setContract(contract);
        maintenanceType.setName(name);
        return maintenanceDAO.insertMaintenanceType(maintenanceType);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public MaintenanceType findMaintenanceType(Long typeId) throws ObjectNotFoundException {
        MaintenanceType maintenanceType = maintenanceDAO.findMaintenanceType(typeId);
        if (maintenanceType == null) {
            throw new ObjectNotFoundException("Can't find the maintenance type with the specified id.",
                    ObjectNotFoundException.Type.SM_MAINTENANCE_TYPE);
        }
        return maintenanceType;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Equipment> findEquipments(long contractId) {
        return maintenanceDAO.findEquipments(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Equipment> findEquipmentsByContract(long contractId) {
        return maintenanceDAO.findEquipmentsByContract(contractId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public Equipment createEquipment(Long contractId, String name, User loggedUser, DocumentDTO equipmentDocument)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)) {
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

        Equipment equipment = new Equipment();
        equipment.setContract(contract);
        equipment.setName(name);
        maintenanceDAO.insertEquipment(equipment);

        //insert equipment document
        if (equipmentDocument != null) {
            Document document = new Document();
            document.setContract(contract);
            document.setEquipment(equipment);
            document.setName(equipmentDocument.getName());
            document.setDisplayName(equipmentDocument.getDisplayName());
            document.setContentType(equipmentDocument.getContentType());
            byte[] bytes = new byte[0];
            try {
                bytes = IOUtils.toByteArray(equipmentDocument.getInputStream());
            } catch (IOException ignore) {
            }
            if (bytes.length > 0) {
                document.setContent(bytes);
                document.setContentLength(document.getContent().length);
                documentDAO.insert(document);
            }
        }

        return equipment;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteEquipment(Long contractId, Long equipmentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        if (maintenanceDAO.countMaintenanceActionsByEquipment(equipmentId) != 0) {
            throw new BusinessException("Can't delete the equipment because it's associated with maintenances.");
        }
        //delete equipment document, if exists
        maintenanceDAO.deleteEquipmentDocument(equipmentId);
        //delete equipment
        if (maintenanceDAO.deleteEquipment(equipmentId) == 0) {
            throw new ObjectNotFoundException("Can't find the equipment with the specified id.",
                    ObjectNotFoundException.Type.SM_EQUIPMENT);
        }
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Long countMaintenanceActionsByEquipment(Long equipmentId) {
        return maintenanceDAO.countMaintenanceActionsByEquipment(equipmentId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Equipment findEquipment(Long equipmentId) throws ObjectNotFoundException {
        Equipment equipment = maintenanceDAO.findEquipment(equipmentId);
        if (equipment == null) {
            throw new ObjectNotFoundException("Can't find the equipment with the specified id.",
                    ObjectNotFoundException.Type.SM_EQUIPMENT);
        }
        return equipment;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Document findEquipmentDocument(Long equipmentId) {
        return maintenanceDAO.findEquipmentDocument(equipmentId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public Long createMaintenance(Long contractId, Maintenance maintenance, Long recurrenceTypeId,
                                  Integer warningDays, List<User> notificationUsers, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserIntermediate(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get current contract
        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id.",
                    ObjectNotFoundException.Type.CONTRACT);
        }

        //create maintenance
        Maintenance newMaintenance = new Maintenance();
        newMaintenance.setCode(generateSequenceCode(contractId));
        newMaintenance.setMaintenanceType(maintenanceDAO.getEntityReference(MaintenanceType.class, maintenance.getMaintenanceType().getId()));
        newMaintenance.setEquipment(maintenanceDAO.getEntityReference(Equipment.class, maintenance.getEquipment().getId()));
        newMaintenance.setDesignation(maintenance.getDesignation());
        newMaintenance.setDescription(maintenance.getDescription());
        newMaintenance.setInternalResponsible(maintenance.getInternalResponsible());
        newMaintenance.setExternalEntity(maintenance.getExternalEntity());
        newMaintenance.setDateScheduled(maintenance.getDateScheduled());
        newMaintenance.setContract(contract);
        Calendar calendar = Calendar.getInstance();
        newMaintenance.setCreationDate(calendar.getTime());
        newMaintenance.setChangedDate(calendar.getTime());
        newMaintenance.setCreatedBy(loggedUser);
        newMaintenance.setChangedBy(loggedUser);
        newMaintenance.setDeleted(false);
        maintenanceDAO.insert(newMaintenance);

        RecurrenceType recurrenceType = null;
        if (recurrenceTypeId != null) {
            recurrenceType = recurrenceDAO.findRecurrenceType(recurrenceTypeId);
        }
        if (recurrenceType != null) {
            //create recurrence
            Recurrence recurrence = new Recurrence();
            setRecurrenceFields(recurrence, recurrenceType, warningDays, newMaintenance.getDateScheduled());

            recurrenceDAO.insert(recurrence);

            //create notifications
            for (User user : notificationUsers) {
                RecurrenceNotification notification = new RecurrenceNotification();
                notification.setRecurrence(recurrence);
                notification.setUser(user);
                recurrenceDAO.insertRecurrenceNotification(notification);
            }

            newMaintenance.setRecurrence(recurrence);
        }

        //send notifications
        try {
            sendEmailsForCreation(newMaintenance, loggedUser);
        } catch (Exception ex) {
            LOGGER.error("Error while sending notification emails " + ex);
        }
        if (newMaintenance.getRecurrence() != null) {
            // if the chosen scheduled date minus the warning days is a date before today, send the notification emails now
            sendRecurrenceEmailsNowIfNeeded(newMaintenance, notificationUsers);
        }

        return newMaintenance.getId();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void updateMaintenanceMainFields(Long contractId, Maintenance maintenance, Long recurrenceTypeId,
                                            Integer warningDays, List<User> notificationUsers, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserIntermediate(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get current contract
        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id.",
                    ObjectNotFoundException.Type.CONTRACT);
        }
        //get maintenance from DB
        Maintenance maintenanceDB = maintenanceDAO.findMaintenance(maintenance.getId());
        if (maintenanceDB == null) {
            throw new ObjectNotFoundException("Can't find the maintenance with the specified id.",
                    ObjectNotFoundException.Type.SM_MAINTENANCE);
        }
        if (maintenanceDB.getClosed()) {
            throw new BusinessException("Can't update closed maintenance.");
        }

        long oldDateScheduled = maintenanceDB.getDateScheduled().getTime();

        //update maintenance fields
        maintenanceDB.setMaintenanceType(maintenanceDAO.getEntityReference(MaintenanceType.class, maintenance.getMaintenanceType().getId()));
        maintenanceDB.setEquipment(maintenanceDAO.getEntityReference(Equipment.class, maintenance.getEquipment().getId()));
        maintenanceDB.setDesignation(maintenance.getDesignation());
        maintenanceDB.setDescription(maintenance.getDescription());
        maintenanceDB.setInternalResponsible(maintenance.getInternalResponsible());
        maintenanceDB.setExternalEntity(maintenance.getExternalEntity());
        maintenanceDB.setDateScheduled(maintenance.getDateScheduled());

        Calendar calendar = Calendar.getInstance();
        maintenanceDB.setChangedDate(calendar.getTime());
        maintenanceDB.setChangedBy(loggedUser);

        //update recurrence
        Recurrence recurrence = maintenanceDB.getRecurrence();
        RecurrenceType recurrenceType = null;
        if (recurrenceTypeId != null) {
            recurrenceType = recurrenceDAO.findRecurrenceType(recurrenceTypeId);
        }

        Integer oldWarningDays = recurrence != null ? recurrence.getWarningDays() : null;

        if (recurrence != null) {
            if (recurrenceType != null) {
                //update the existing recurrence
                setRecurrenceFields(recurrence, recurrenceType, warningDays, maintenanceDB.getDateScheduled());

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
                maintenanceDB.setRecurrence(null);
            }
        } else if (recurrenceType != null) {
            //create the recurrence
            recurrence = new Recurrence();
            setRecurrenceFields(recurrence, recurrenceType, warningDays, maintenanceDB.getDateScheduled());

            recurrenceDAO.insert(recurrence);

            //create notifications
            for (User user : notificationUsers) {
                RecurrenceNotification notification = new RecurrenceNotification();
                notification.setRecurrence(recurrence);
                notification.setUser(user);
                recurrenceDAO.insertRecurrenceNotification(notification);
            }

            maintenanceDB.setRecurrence(recurrence);
        }

        if (maintenanceDB.getRecurrence() != null &&
                (oldDateScheduled != maintenanceDB.getDateScheduled().getTime()
                        || (oldWarningDays == null && warningDays != null && warningDays > 0)
                        || (oldWarningDays != null && warningDays != null && warningDays > 0 && !oldWarningDays.equals(warningDays)))) {
            // if the chosen scheduled date minus the warning days is a date before today, send the notification email now
            sendRecurrenceEmailsNowIfNeeded(maintenanceDB, notificationUsers);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void editMaintenance(Long contractId, Long maintenanceId, List<DocumentDTO> newDocuments, Date closedDate, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)) {
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
        //get maintenance from DB
        Maintenance maintenanceDB = maintenanceDAO.findMaintenance(maintenanceId);
        if (maintenanceDB == null) {
            throw new ObjectNotFoundException("Can't find the maintenance with the specified id.",
                    ObjectNotFoundException.Type.SM_MAINTENANCE);
        }
        if (maintenanceDB.getClosed()) {
            throw new BusinessException("Can't update closed maintenance.");
        }

        if (closedDate != null) {
            //check if this maintenance has open corrective actions
            //if so, it can not be closed
            if (maintenanceDAO.hasOpenCorrectiveActions(maintenanceId)) {
                throw new BusinessException("Can't close the maintenance because it has corrective actions that are still open.");
            }
            maintenanceDB.setClosedDate(closedDate);

            //send notifications
            try {
                sendEmailsForClosing(maintenanceDB, loggedUser);
            } catch (Exception ex) {
                LOGGER.error("Error while sending notification emails " + ex);
            }
        }

        Calendar calendar = Calendar.getInstance();
        maintenanceDB.setChangedDate(calendar.getTime());
        maintenanceDB.setChangedBy(loggedUser);

        //save documents
        for (DocumentDTO doc : newDocuments) {
            Document document = new Document();
            document.setContract(contract);
            document.setMaintenance(maintenanceDB);
            document.setName(doc.getName());
            document.setDisplayName(doc.getDisplayName());
            document.setContentType(doc.getContentType());
            byte[] bytes = new byte[0];
            try {
                bytes = IOUtils.toByteArray(doc.getInputStream());
            } catch (IOException e) {
                //ignore
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
    public void deleteMaintenance(Long contractId, Long maintenanceId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get maintenance from DB
        Maintenance maintenance = maintenanceDAO.findMaintenance(maintenanceId);
        if (maintenance == null) {
            throw new ObjectNotFoundException("Can't find the maintenance with the specified id.",
                    ObjectNotFoundException.Type.SM_MAINTENANCE);
        }
        if (maintenance.getRecurrence() != null) {
            //check if this is the latest maintenance of it's recurrence and, if so, sets the recurrence as inactive
            if (maintenanceDAO.isLatestMaintenanceByRecurrence(maintenance.getRecurrence().getId(), maintenanceId)) {
                maintenance.getRecurrence().setActive(false);
            }
        }
        //set maintenance as deleted
        maintenance.setDeleted(true);
        Calendar calendar = Calendar.getInstance();
        maintenance.setChangedDate(calendar.getTime());
        maintenance.setChangedBy(loggedUser);
        //delete related documents
        maintenanceDAO.deleteMaintenanceDocuments(maintenanceId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void reopenMaintenance(Long contractId, Long maintenanceId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserIntermediate(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }
        //get maintenance from DB
        Maintenance maintenance = maintenanceDAO.findMaintenance(maintenanceId);
        if (maintenance == null) {
            throw new ObjectNotFoundException("Can't find the maintenance with the specified id.",
                    ObjectNotFoundException.Type.SM_MAINTENANCE);
        }
        maintenance.setClosedDate(null);
        Calendar calendar = Calendar.getInstance();
        maintenance.setChangedDate(calendar.getTime());
        maintenance.setChangedBy(loggedUser);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteDocument(Long contractId, Long maintenanceId, Long documentId, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserIntermediate(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert " +
                    "or intermediate permission");
        }
        Document document = documentDAO.findById(documentId);
        if (document == null || document.getMaintenance() == null || !document.getMaintenance().getId().equals(maintenanceId)) {
            throw new ObjectNotFoundException("Can't find the document with the specified id.",
                    ObjectNotFoundException.Type.SM_DOCUMENT);
        }
        if (document.getMaintenance().getClosed()) {
            throw new BusinessException("Can't update closed maintenance.");
        }
        //delete the document
        documentDAO.delete(document);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void addChatMessage(Long contractId, Long maintenanceId, String message, User loggedUser)
            throws CertitoolsAuthorizationException, ObjectNotFoundException, BusinessException {

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)) {
            throw new CertitoolsAuthorizationException("User contract does not have access to this module.");
        }
        if (!securityManagementService.isUserAdministratorOrCertitecna(loggedUser.getId())
                && !securityManagementService.isUserBasic(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert " +
                    "or intermediate permission");
        }
        //get maintenance from DB
        Maintenance maintenance = maintenanceDAO.findMaintenance(maintenanceId);
        if (maintenance == null) {
            throw new ObjectNotFoundException("Can't find the maintenance with the specified id.",
                    ObjectNotFoundException.Type.SM_MAINTENANCE);
        }
        if (maintenance.getClosed()) {
            throw new BusinessException("Can't update closed maintenance.");
        }
        Chat chat = new Chat();
        chat.setMaintenance(maintenance);
        chat.setMessage(message);
        chat.setUser(loggedUser);
        chat.setDatetime(new Date());
        chatDAO.insert(chat);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Chat> findChatMessages(Long maintenanceId) {
        return maintenanceDAO.findChatMessages(maintenanceId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<CorrectiveAction> findOpenCorrectiveActions(Long maintenanceId) {
        return maintenanceDAO.findOpenCorrectiveActions(maintenanceId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Maintenance findMaintenance(Long maintenanceId) throws ObjectNotFoundException {
        Maintenance maintenance = maintenanceDAO.findMaintenanceWithRecurrence(maintenanceId);
        if (maintenance == null) {
            throw new ObjectNotFoundException("Can't find the maintenance with the specified id.",
                    ObjectNotFoundException.Type.SM_MAINTENANCE);
        }
        //set documents
        maintenance.setDocuments(maintenanceDAO.findDocuments(maintenanceId));
        return maintenance;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Maintenance findMaintenanceForChatPdf(Long maintenanceId) throws ObjectNotFoundException {
        Maintenance maintenance = maintenanceDAO.findMaintenanceWithContract(maintenanceId);
        if (maintenance == null) {
            throw new ObjectNotFoundException("Can't find the maintenance with the specified id.",
                    ObjectNotFoundException.Type.SM_MAINTENANCE);
        }
        //set chat messages
        maintenance.setChatMessages(maintenanceDAO.findChatMessages(maintenanceId));
        return maintenance;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Maintenance findMaintenanceForReportPdf(Long maintenanceId) throws ObjectNotFoundException {
        Maintenance maintenance = maintenanceDAO.findMaintenanceWithContract(maintenanceId);
        if (maintenance == null) {
            throw new ObjectNotFoundException("Can't find the maintenance with the specified id.",
                    ObjectNotFoundException.Type.SM_MAINTENANCE);
        }
        //set corrective actions
        maintenance.setCorrectiveActions(maintenanceDAO.findCorrectiveActions(maintenanceId));
        //set documents
        maintenance.setDocuments(maintenanceDAO.findDocuments(maintenanceId));
        return maintenance;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public PaginatedListWrapper<Maintenance> findMaintenancesByContract(long contractId,
                                                                        PaginatedListWrapper<Maintenance> paginatedListWrapper,
                                                                        Long maintenanceTypeId, String filterYear,
                                                                        Boolean isOpen)
            throws BusinessException {

        if (paginatedListWrapper == null) {
            throw new BusinessException("No PaginatedListWrapper initialized ");
        }

        if (paginatedListWrapper.getSortCriterion() == null) {
            paginatedListWrapper.setSortCriterion("maintenanceType.name");
        }
        if (paginatedListWrapper.getSortDirection() == null) {
            paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.DESC);
        }

        int count = maintenanceDAO.countAll(contractId, maintenanceTypeId, filterYear, isOpen);
        paginatedListWrapper.setFullListSize(count);

        List<Maintenance> maintenances;
        if (paginatedListWrapper.getExport()) {
            maintenances = maintenanceDAO.findAll(contractId, 0, count,
                    paginatedListWrapper.getSortCriterion(), paginatedListWrapper.getSortDirection().value(),
                    maintenanceTypeId, filterYear, isOpen);
        } else {
            maintenances = maintenanceDAO.findAll(contractId, paginatedListWrapper.getOffset(),
                    paginatedListWrapper.getResultsPerPage(),
                    paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper.getSortDirection().value(), maintenanceTypeId, filterYear, isOpen);
        }

        for (Maintenance maintenance : maintenances) { // force the collection to be loaded to avoid lazy loading exceptions
            maintenance.setCorrectiveActions(maintenanceDAO.findCorrectiveActions(maintenance.getId()));
            maintenance.setDocuments(maintenanceDAO.findDocuments(maintenance.getId()));
            if (maintenance.getClosed()) {
                //this is for the validation of the chat log pdf
                maintenance.setHasChatMessages(maintenanceDAO.countChatMessages(maintenance.getId()) != 0);
            }
        }

        paginatedListWrapper.setList(maintenances);

        return paginatedListWrapper;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Maintenance> findMaintenancesByContract(long contractId, String filterYear, String filterSemester,
                                                        Boolean isOpen) {

        List<Maintenance> maintenances = maintenanceDAO.findAll(contractId, filterYear, filterSemester, isOpen);

        for (Maintenance maintenance : maintenances) { // force the collection to be loaded to avoid lazy loading exceptions
            maintenance.setCorrectiveActions(maintenanceDAO.findCorrectiveActions(maintenance.getId()));
            maintenance.setDocuments(maintenanceDAO.findDocuments(maintenance.getId()));
            if (maintenance.getClosed()) {
                maintenance.setChatMessages(maintenanceDAO.findChatMessages(maintenance.getId()));
                maintenance.setHasChatMessages(maintenance.getChatMessages().size() != 0);
            }
        }
        return maintenances;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<String> findMaintenanceYears(long contractId) {
        return maintenanceDAO.findMaintenanceYears(contractId);
    }

    /**
     * Aux method to set the recurrence fields.
     *
     * @param recurrence     The recurrence.
     * @param recurrenceType The recurrence type.
     * @param warningDays    The number of warning days.
     * @param dateScheduled  The maintenance scheduled date.
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
        recurrence.setEntityType(RecurrenceEntityType.MAINTENANCE);
    }

    private String generateSequenceCode(Long contractId) throws BusinessException {
        try {
            return sequenceGenerator.generateCode(SequenceCode.MAINTENANCE, contractId);
        } catch (InvalidSequenceException e) {
            throw new BusinessException("Error generating sequence code for maintenance.");
        }
    }

    private void sendRecurrenceEmailsNowIfNeeded(Maintenance maintenance, List<User> notificationUsers) {
        Calendar today = Calendar.getInstance();
        Calendar notificationDate = Calendar.getInstance();
        notificationDate.setTime(maintenance.getDateScheduled());
        notificationDate.add(Calendar.DAY_OF_MONTH, -maintenance.getRecurrence().getWarningDays());

        if (notificationDate.before(today)) {
            int numberDays = (int) Math.ceil(
                    (float) (maintenance.getDateScheduled().getTime() - today.getTimeInMillis()) / (1000 * 60 * 60 * 24));

            if (numberDays >= 0) {
                String eventName = maintenance.getCode();
                Long contractId = maintenance.getContract().getId();
                String contract = maintenance.getContract().getContractDesignation();
                String company = maintenance.getContract().getCompany().getName();

                try {
                    sendRecurrenceNotificationEmails(eventName, numberDays, getUrlForMail(maintenance), company,
                            contract, notificationUsers);
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

    private String getUrlForMail(Maintenance maintenance) {
        return Configuration.getInstance().getApplicationDomain() + "/sm/SecurityMaintenance.action?"
                + "maintenanceEdit=" + "&contractId=" + maintenance.getContract().getId() + "&maintenanceId="
                + maintenance.getId().toString();
    }

    private List<User> getUsersForMail(Long contractId, User loggedUser) {
        //get expert and intermediate users for this contract
        List<User> users = userDAO.findExpertAndIntermediateUsersByContract(contractId);
        Iterator<User> it = users.iterator();
        while (it.hasNext()) {
            if (it.next().getEmailContact().equals(loggedUser.getEmailContact())) {
                it.remove();
            }
        }
        return users;
    }

    private void sendEmailsForCreation(final Maintenance maintenance, final User loggedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventCreatedEmails(EmailEventType.MAINTENANCE, maintenance.getCode(),
                        getUrlForMail(maintenance),
                        maintenance.getContract().getCompany().getName(), maintenance.getContract().getContractDesignation(),
                        getUsersForMail(maintenance.getContract().getId(), loggedUser));
            }
        });
    }

    private void sendEmailsForClosing(final Maintenance maintenance, final User loggedUser) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmEventClosedEmails(EmailEventType.MAINTENANCE, maintenance.getCode(),
                        getUrlForMail(maintenance),
                        maintenance.getContract().getCompany().getName(), maintenance.getContract().getContractDesignation(),
                        getUsersForMail(maintenance.getContract().getId(), loggedUser));
            }
        });
    }
}
