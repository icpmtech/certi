package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.persistence.certitools.CompanyDAO;
import com.criticalsoftware.certitools.persistence.certitools.ContractDAO;
import com.criticalsoftware.certitools.persistence.certitools.ModuleDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.Permission;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.entities.sm.Activity;
import com.criticalsoftware.certitools.entities.sm.Anomaly;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.EmergencyAction;
import com.criticalsoftware.certitools.entities.sm.Maintenance;
import com.criticalsoftware.certitools.entities.sm.SecurityImpact;
import com.criticalsoftware.certitools.entities.sm.SecurityImpactWork;
import com.criticalsoftware.certitools.entities.sm.enums.AnomalyType;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.entities.sm.enums.WorkType;
import com.criticalsoftware.certitools.persistence.sm.ActivityDAO;
import com.criticalsoftware.certitools.persistence.sm.AnomalyDAO;
import com.criticalsoftware.certitools.persistence.sm.ChatDAO;
import com.criticalsoftware.certitools.persistence.sm.CorrectiveActionDAO;
import com.criticalsoftware.certitools.persistence.sm.DocumentDAO;
import com.criticalsoftware.certitools.persistence.sm.EmergencyActionDAO;
import com.criticalsoftware.certitools.persistence.sm.MaintenanceDAO;
import com.criticalsoftware.certitools.persistence.sm.WorkDAO;
import com.criticalsoftware.certitools.persistence.sm.dto.UpcomingEvent;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.ConfigurationProperties;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.MailSender;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.enums.EmailEventType;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(SecurityManagementService.class)
@LocalBinding(jndiBinding = "certitools/SecurityManagementService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class SecurityManagementServiceEJB implements SecurityManagementService {

    @EJB
    private ActivityDAO activityDAO;
    @EJB
    private CorrectiveActionDAO correctiveActionDAO;
    @EJB
    private MaintenanceDAO maintenanceDAO;
    @EJB
    private UserDAO userDAO;
    @EJB
    private DocumentDAO documentDAO;
    @EJB
    private ContractDAO contractDAO;
    @EJB
    private CompanyDAO companyDAO;
    @EJB
    private ModuleDAO moduleDAO;
    @EJB
    private AnomalyDAO anomalyDAO;
    @EJB
    private WorkDAO workDAO;
    @EJB
    private EmergencyActionDAO emergencyActionDAO;
    @EJB
    private ChatDAO chatDAO;

    @Resource
    private SessionContext sessionContext;

    private static final Logger LOGGER = Logger.getInstance(SecurityManagementServiceEJB.class);

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<Company> getUserCompanies(long userId) {
        List<Company> companies = new ArrayList<Company>();

        // administrator can see all companies
        if (isUserAdministratorOrCertitecna(userId)) {
            companies.addAll(companyDAO.findAllWithPlan(ModuleType.GSC));
            return companies;
        }

        // users can see only his company
        if (sessionContext.isCallerInRole("user")) {
            Company company = userDAO.findById(userId).getCompany();

            if (company != null) {
                companies.add(company);
            }
        }

        return companies;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<Contract> getUserCompanyContracts(long userId, long companyId) {
        List<Contract> contracts = new ArrayList<Contract>();


        // isCertitecna users can see all contracts
        if (isUserAdministratorOrCertitecna(userId)) {
            Module module = moduleDAO.findModuleByModuleType(ModuleType.GSC);
            contracts.addAll(contractDAO.findAllByCompanyAndModule(companyId, module));
            return contracts;
        }

        // users can see only his company
        if (sessionContext.isCallerInRole("user")) {
            for (Contract contract : contractDAO.findAllByCompany(companyId)) {
                UserContract userContract = contractDAO.findUserContract(userId, contract.getId());
                if (userContract != null &&
                        userContract.isUserContractValid(ModuleType.GSC)) {
                    contracts.add(contract);
                }
            }
        }
        return contracts;
    }


    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<UpcomingEvent> getUpcommingEvents(long contractId) {

        List<UpcomingEvent> upcomingEvents = activityDAO.findUpcomingEvents(contractId);
        upcomingEvents.addAll(maintenanceDAO.findUpcomingEvents(contractId));

        Collections.sort(upcomingEvents, new Comparator<UpcomingEvent>() {
            @Override
            public int compare(UpcomingEvent o1, UpcomingEvent o2) {
                return o1.getDateScheduled().compareTo(o2.getDateScheduled());
            }
        });

        if (upcomingEvents.size() > 5) {
            upcomingEvents = upcomingEvents.subList(0, 5);
        }

        return upcomingEvents;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<User> getAllowedUsers(final Long contractId) {
        return userDAO.findAllByContractId(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Long getContractSpaceUsed(long contractId) {
        return documentDAO.getContractSpaceUsed(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Document getContractLogoPicture(long contractId) {
        return documentDAO.getContractLogoPicture(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Document getContractCoverPicture(long contractId) {
        return documentDAO.getContractCoverPicture(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Document getDocument(long documentId) {
        return documentDAO.findDocumentById(documentId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public byte[] getDocumentContent(long documentId) {
        return documentDAO.findDocumentContent(documentId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public InputStream getDocumentContentInputStream(long documentId) {
        return documentDAO.findDocumentContentInputStream(documentId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void saveContractLogoPicture(long userId, long contractId, InputStream inputStream, String contentType,
                                        String fileName) {
        Document document = documentDAO.getContractLogoPicture(contractId);
        if (document != null) {
            updateDocument(document, inputStream, contentType, fileName);
            document.setDisplayName(document.getName());
        } else {
            document = new Document();
            Contract contract = contractDAO.findById(contractId);
            document.setContract(contract);
            updateDocument(document, inputStream, contentType, fileName);
            document.setDisplayName(document.getName());
            documentDAO.insert(document);

            contract.setSmLogoPicture(document);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void saveContractCoverPicture(long userId, long contractId, InputStream inputStream, String contentType,
                                         String fileName) {
        Document document = documentDAO.getContractCoverPicture(contractId);
        if (document != null) {
            updateDocument(document, inputStream, contentType, fileName);
            document.setDisplayName(document.getName());
        } else {
            document = new Document();
            Contract contract = contractDAO.findById(contractId);
            document.setContract(contract);
            updateDocument(document, inputStream, contentType, fileName);
            document.setDisplayName(document.getName());
            documentDAO.insert(document);

            contract.setSmCoverPicture(document);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "user"})
    public void deleteRecordsByContract(long contractId, String year, Boolean isOpen, User loggedUser)
            throws CertitoolsAuthorizationException {
        if (!isUserAdministratorOrCertitecna(loggedUser.getId()) && !isUserExpert(loggedUser.getId(), contractId)) {
            throw new CertitoolsAuthorizationException("User contract is not valid or it does not have expert permission");
        }

        List<SubModuleType> subModules = findContractSubModules(contractId);
        for (SubModuleType subModule : subModules) {
            switch (subModule) {
                case ACTV:
                    List<Long> activities = activityDAO.findActivitiesIds(contractId, year, isOpen);
                    if (!activities.isEmpty()) {
                        activityDAO.deleteActivitiesDocuments(activities);
                        activityDAO.deactivateActivitiesRecurrences(activities);
                        activityDAO.deleteActivities(activities, loggedUser);
                    }
                    break;
                case ANOM:
                    List<Long> anomalies = anomalyDAO.findAnomaliesIds(contractId, year, isOpen);
                    if (!anomalies.isEmpty()) {
                        anomalyDAO.deleteAnomaliesDocuments(anomalies);
                        anomalyDAO.deleteAnomalies(anomalies, loggedUser);
                    }
                    break;
                case SIW:
                    List<Long> actions = correctiveActionDAO.findCorrectiveActionsIds(contractId, year, isOpen);
                    if (!actions.isEmpty()) {
                        correctiveActionDAO.deleteCorrectiveActionsDocuments(actions);
                        correctiveActionDAO.deleteCorrectiveActions(actions, loggedUser);
                    }
                    break;
                case APC:
                    List<Long> maintenances = maintenanceDAO.findMaintenancesIds(contractId, year, isOpen);
                    if (!maintenances.isEmpty()) {
                        maintenanceDAO.deleteMaintenancesDocuments(maintenances);
                        maintenanceDAO.deactivateMaintenancesRecurrences(maintenances);
                        maintenanceDAO.deleteMaintenances(maintenances, loggedUser);
                    }
                    break;
                case MNT:
                    List<Long> securityImpactWorks = workDAO.findSecurityImpactWorksIds(contractId, year, isOpen);
                    if (!securityImpactWorks.isEmpty()) {
                        workDAO.deleteSecurityImpactWorksDocuments(securityImpactWorks);
                        workDAO.deleteSecurityImpactWorks(securityImpactWorks, loggedUser);
                    }
                    break;
                case EMRG:
                    List<Long> emergencyActions = emergencyActionDAO.findEmergencyActionsIds(contractId, year, isOpen);
                    if (!emergencyActions.isEmpty()) {
                        emergencyActionDAO.deleteEmergencyActions(emergencyActions, loggedUser);
                        emergencyActionDAO.deleteEmergencyActionTokens(emergencyActions);
                    }
                    break;
            }
        }
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public int[] countOpenItems(long contractId) {
        int[] count = {0, 0, 0, 0, 0, 0};

        count[0] = activityDAO.countOpenActivity(contractId);
        count[1] = anomalyDAO.countOpenAnomalies(contractId);
        count[2] = workDAO.countOpenSecurityImpactWorks(contractId);
        count[3] = correctiveActionDAO.countOpenCorrectiveAction(contractId);
        count[4] = maintenanceDAO.countOpenMaintenance(contractId);
        count[5] = emergencyActionDAO.countOpenEmergencyActions(contractId);

        return count;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public boolean isUserAdministratorOrCertitecna(Long userId) {
        return userDAO.findById(userId).getCompany().getId().toString()
                .equalsIgnoreCase(Configuration.getInstance().getCertitecnaId());
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public boolean isUserBasic(long userId, long contractId) {
        UserContract userContract = contractDAO.findUserContract(userId, contractId);
        if (userContract == null || !userContract.isUserContractValid(ModuleType.GSC)) {
            return false;
        }
        for (Permission permission : userContract.getPermissions()) {
            if (permission.getName().equals(ConfigurationProperties.PERMISSION_GSC_BASIC.getKey()) ||
                    permission.getName().equals(ConfigurationProperties.PERMISSION_GSC_INTERMEDIATE.getKey()) ||
                    permission.getName().equals(ConfigurationProperties.PERMISSION_GSC_EXPERT.getKey())) {
                return true;
            }
        }
        return false;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public boolean isUserIntermediate(long userId, long contractId) {
        UserContract userContract = contractDAO.findUserContract(userId, contractId);
        if (userContract == null || !userContract.isUserContractValid(ModuleType.GSC)) {
            return false;
        }
        for (Permission permission : userContract.getPermissions()) {
            if (permission.getName().equals(ConfigurationProperties.PERMISSION_GSC_INTERMEDIATE.getKey()) ||
                    permission.getName().equals(ConfigurationProperties.PERMISSION_GSC_EXPERT.getKey())) {
                return true;
            }
        }
        return false;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public boolean isUserExpert(long userId, long contractId) {
        UserContract userContract = contractDAO.findUserContract(userId, contractId);
        if (userContract == null || !userContract.isUserContractValid(ModuleType.GSC)) {
            return false;
        }
        for (Permission permission : userContract.getPermissions()) {
            if (permission.getName().equals(ConfigurationProperties.PERMISSION_GSC_EXPERT.getKey())) {
                return true;
            }
        }
        return false;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Contract getContract(long contractId) {
        return contractDAO.findById(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public Contract getContractWithCompany(long contractId) {
        return contractDAO.findContractWithCompany(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<SecurityImpact> findSecurityImpacts() {
        return anomalyDAO.findSecurityImpacts();
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public SecurityImpact findSecurityImpact(Long impactId) throws ObjectNotFoundException {
        SecurityImpact securityImpact = anomalyDAO.findSecurityImpact(impactId);
        if (securityImpact == null) {
            throw new ObjectNotFoundException("Can't find the maintenance type with the specified id.",
                    ObjectNotFoundException.Type.SM_SECURITY_IMPACT);
        }
        return securityImpact;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<User> findUsersByContractAndSecurityPermissions(long contractId) {
        return userDAO.findUsersByContractAndSecurityPermissions(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<User> findExpertAndIntermediateUsersByContract(long contractId) {
        return userDAO.findExpertAndIntermediateUsersByContract(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<User> findBasicUsersByContract(long contractId) {
        return userDAO.findBasicUsersByContract(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public boolean isSubModuleAllowed(Long contractId, SubModuleType subModuleType) {
        return moduleDAO.existsSubModuleByContractAndType(contractId, subModuleType);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<SubModuleType> findContractSubModules(Long contractId) {
        return moduleDAO.findContractSubModules(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public boolean hasEmergencyUsers(Long contractId) {
        return emergencyActionDAO.hasEmergencyUsers(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "user"})
    public List<String> findSecurityManagementYears(long contractId) {
        Set<String> yearsSet = new HashSet<String>(); // Set is used to remove duplicates
        yearsSet.addAll(activityDAO.findActivityYears(contractId));
        yearsSet.addAll(anomalyDAO.findAnomalyYears(contractId));
        yearsSet.addAll(workDAO.findSecurityImpactWorkYears(contractId));
        yearsSet.addAll(correctiveActionDAO.findCorrectiveActionYears(contractId));
        yearsSet.addAll(maintenanceDAO.findMaintenanceYears(contractId));
        yearsSet.addAll(emergencyActionDAO.findEmergencyActionYears(contractId));

        List<String> years = new ArrayList<String>();
        years.addAll(yearsSet);
        Collections.sort(years, Collections.reverseOrder());
        return years;
    }

    public void sendChatNotifications() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        List<Activity> activities = activityDAO.findActivitiesWithChatsAfterDate(calendar.getTime());
        for (Activity activity : activities) {
            List<User> users = userDAO.findUsersByContractAndSecurityPermissions(activity.getContract().getId());
            String url = Configuration.getInstance().getApplicationDomain() + "/sm/Security.action?"
                    + "activityPlanningEdit="
                    + "&activityId=" + activity.getId()
                    + "&contractId=" + activity.getContract().getId();
            String contract = activity.getContract().getContractDesignation();
            String company = activity.getContract().getCompany().getName();
            sendChatNotificationEmails(EmailEventType.ACTIVITY, activity.getCode() + " - " + activity.getName(),
                    url, company, contract, users);
        }

        List<Anomaly> anomalies = anomalyDAO.findAnomaliesWithChatsAfterDate(calendar.getTime());
        for (Anomaly anomaly : anomalies) {
            List<User> users = userDAO.findUsersByContractAndSecurityPermissions(anomaly.getContract().getId());
            String url = Configuration.getInstance().getApplicationDomain() + "/sm/SecurityAnomaliesRecord.action?"
                    + (anomaly.getAnomalyType() == AnomalyType.ANOMALY ? "anomaliesRecordEdit=" : "occurrencesRecordEdit")
                    + "&anomalyId=" + anomaly.getId()
                    + "&contractId=" + anomaly.getContract().getId();
            String contract = anomaly.getContract().getContractDesignation();
            String company = anomaly.getContract().getCompany().getName();
            sendChatNotificationEmails(EmailEventType.valueOf(anomaly.getAnomalyType().name()), anomaly.getCode(),
                    url, company, contract, users);
        }

        List<SecurityImpactWork> works = workDAO.findSecurityImpactWorksWithChatsAfterDate(calendar.getTime());
        for (SecurityImpactWork work : works) {
            List<User> users = userDAO.findUsersByContractAndSecurityPermissions(work.getContract().getId());
            String url = Configuration.getInstance().getApplicationDomain() + "/sm/SecurityImpactWork.action?"
                    + (work.getWorkType() == WorkType.MODIFICATION ? "modificationsChangesEdit=" : "authorizationEdit=")
                    + "&contractId=" + work.getContract().getId()
                    + "&impactWorkId=" + work.getId().toString();
            String contract = work.getContract().getContractDesignation();
            String company = work.getContract().getCompany().getName();
            sendChatNotificationEmails(EmailEventType.valueOf(work.getWorkType().name()), work.getCode(),
                    url, company, contract, users);
        }

        List<CorrectiveAction> actions = correctiveActionDAO.findCorrectiveActionsWithChatsAfterDate(calendar.getTime());
        for (CorrectiveAction action : actions) {
            List<User> users = userDAO.findUsersByContractAndSecurityPermissions(action.getContract().getId());
            String url = Configuration.getInstance().getApplicationDomain() + "/sm/SecurityActionsPlanning.action?"
                    + "actionsPlanningEdit="
                    + "&correctiveActionId=" + action.getId()
                    + "&contractId=" + action.getContract().getId();
            String contract = action.getContract().getContractDesignation();
            String company = action.getContract().getCompany().getName();
            sendChatNotificationEmails(EmailEventType.CORRECTIVE_ACTION, action.getCode(), url, company, contract, users);
        }

        List<Maintenance> maintenances = maintenanceDAO.findMaintenancesWithChatsAfterDate(calendar.getTime());
        for (Maintenance maintenance : maintenances) {
            List<User> users = userDAO.findUsersByContractAndSecurityPermissions(maintenance.getContract().getId());
            String url = Configuration.getInstance().getApplicationDomain() + "/sm/SecurityMaintenance.action?"
                    + "maintenanceEdit="
                    + "&maintenanceId=" + maintenance.getId()
                    + "&contractId=" + maintenance.getContract().getId();
            String contract = maintenance.getContract().getContractDesignation();
            String company = maintenance.getContract().getCompany().getName();
            sendChatNotificationEmails(EmailEventType.MAINTENANCE, maintenance.getCode(), url, company, contract, users);
        }

        List<EmergencyAction> emergencies = emergencyActionDAO.findEmergencyActionsWithChatsAfterDate(calendar.getTime());
        for (EmergencyAction emergency : emergencies) {
            List<User> users = userDAO.findUsersByContractAndSecurityPermissions(emergency.getContract().getId());
            String url = Configuration.getInstance().getApplicationDomain() + "/sm/SecurityEmergency.action?"
                    + "emergencyActionEdit="
                    + "&emergencyId=" + emergency.getId()
                    + "&contractId=" + emergency.getContract().getId();
            String contract = emergency.getContract().getContractDesignation();
            String company = emergency.getContract().getCompany().getName();
            sendChatNotificationEmails(EmailEventType.EMERGENCY_ACTION, emergency.getCode(), url, company, contract, users);
        }
    }

    private void sendChatNotificationEmails(final EmailEventType eventType, final String eventName, final String url,
                                            final String company, final String contract, final List<User> users) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Entering new thread...");
                MailSender.sendSmChatNotificationEmails(eventType, eventName, url, company, contract, users);
            }
        });
    }

    private void updateDocument(Document document, InputStream inputStream, String contentType, String fileName) {
        byte[] bytes = new byte[0];
        try {
            bytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            //ignore
        }

        document.setContent(bytes);
        document.setContentLength(document.getContent().length);
        document.setContentType(contentType);
        document.setName(fileName);
    }
}
