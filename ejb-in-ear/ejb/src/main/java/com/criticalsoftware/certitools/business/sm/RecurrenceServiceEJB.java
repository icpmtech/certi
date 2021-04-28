package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.InvalidSequenceException;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.sm.ActivityDAO;
import com.criticalsoftware.certitools.persistence.sm.MaintenanceDAO;
import com.criticalsoftware.certitools.persistence.sm.RecurrenceDAO;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.*;
import com.criticalsoftware.certitools.entities.sm.enums.RecurrenceEntityType;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.MailSender;
import com.criticalsoftware.certitools.util.enums.SequenceCode;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Recurrence Service EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(RecurrenceService.class)
@LocalBinding(jndiBinding = "certitools/RecurrenceService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class RecurrenceServiceEJB implements RecurrenceService {

    private static final Logger LOGGER = Logger.getInstance(RecurrenceServiceEJB.class);

    @EJB
    private RecurrenceDAO recurrenceDAO;
    @EJB
    private ActivityDAO activityDAO;
    @EJB
    private MaintenanceDAO maintenanceDAO;
    @EJB
    private SequenceGeneratorService sequenceGenerator;
    @EJB
    private UserDAO userDAO;

    @RolesAllowed(value = {"administrator", "user"})
    public List<RecurrenceType> findRecurrenceTypes() {
        return recurrenceDAO.findRecurrenceTypes();
    }

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createScheduledEvents() throws BusinessException {
        LOGGER.info("createScheduledEvents");

        //get scheduled recurrences
        List<Recurrence> recurrences = recurrenceDAO.findScheduledRecurrences();

        //create activity or maintenance for each recurrence
        for (Recurrence recurrence : recurrences) {
            String eventName = null;
            String url = null;
            Long contractId = null;
            Integer numberDays = null;
            String contract = null;
            String company = null;

            if (recurrence.getEntityType() == RecurrenceEntityType.ACTIVITY) {
                //get latest activity for this recurrence
                Activity latestActivity = activityDAO.getLatestActivityByRecurrence(recurrence.getId());

                if (latestActivity != null) {
                    Calendar calendar = Calendar.getInstance();
                    Activity newActivity = new Activity();
                    newActivity.setContract(latestActivity.getContract());
                    newActivity.setActivityType(latestActivity.getActivityType());
                    newActivity.setCode(generateSequenceCode(SequenceCode.ACTIVITY, latestActivity.getContract().getId()));
                    newActivity.setName(latestActivity.getName());
                    newActivity.setDuration(latestActivity.getDuration());
                    newActivity.setInternalResponsible(latestActivity.getInternalResponsible());
                    newActivity.setExternalEntity(latestActivity.getExternalEntity());
                    newActivity.setRecurrence(recurrence);
                    newActivity.setDeleted(false);
                    newActivity.setCreationDate(calendar.getTime());
                    newActivity.setChangedDate(calendar.getTime());
                    newActivity.setCreatedBy(latestActivity.getCreatedBy());
                    newActivity.setChangedBy(latestActivity.getCreatedBy());
                    //calculate date scheduled for the new activity
                    calendar.setTime(latestActivity.getDateScheduled());
                    calendar.add(Calendar.DAY_OF_MONTH, recurrence.getRecurrenceType().getIntervalDays());
                    while (calendar.getTime().before(new Date())) {
                        //this is to assure that the activity isn't scheduled for a date in the past
                        //will only happen in case the timer fails for some reason and the activity is not created when it's supposed to
                        calendar.add(Calendar.DAY_OF_MONTH, recurrence.getRecurrenceType().getIntervalDays());
                    }
                    newActivity.setDateScheduled(calendar.getTime());
                    activityDAO.insert(newActivity);

                    eventName = newActivity.getCode() + " - " + newActivity.getName();
                    contractId = newActivity.getContract().getId();
                    url = Configuration.getInstance().getApplicationDomain() + "/sm/Security.action?"
                            + "activityPlanningEdit=" + "&contractId=" + contractId.toString()
                            + "&activityId=" + newActivity.getId().toString();
                    numberDays = (int) Math.ceil(
                            (float) (newActivity.getDateScheduled().getTime() - new Date().getTime()) / (24 * 60 * 60 * 1000));
                    contract = newActivity.getContract().getContractDesignation();
                    company = newActivity.getContract().getCompany().getName();

                }
            } else {
                //get latest maintenance for this recurrence
                Maintenance latestMaintenance = maintenanceDAO.getLatestMaintenanceByRecurrence(recurrence.getId());

                if (latestMaintenance != null) {
                    Calendar calendar = Calendar.getInstance();
                    Maintenance newMaintenance = new Maintenance();
                    newMaintenance.setContract(latestMaintenance.getContract());
                    newMaintenance.setCode(generateSequenceCode(SequenceCode.MAINTENANCE, latestMaintenance.getContract().getId()));
                    newMaintenance.setMaintenanceType(latestMaintenance.getMaintenanceType());
                    newMaintenance.setEquipment(latestMaintenance.getEquipment());
                    newMaintenance.setDesignation(latestMaintenance.getDesignation());
                    newMaintenance.setDescription(latestMaintenance.getDescription());
                    newMaintenance.setInternalResponsible(latestMaintenance.getInternalResponsible());
                    newMaintenance.setExternalEntity(latestMaintenance.getExternalEntity());
                    newMaintenance.setCreationDate(calendar.getTime());
                    newMaintenance.setChangedDate(calendar.getTime());
                    newMaintenance.setCreatedBy(latestMaintenance.getCreatedBy());
                    newMaintenance.setChangedBy(latestMaintenance.getCreatedBy());
                    newMaintenance.setDeleted(false);
                    newMaintenance.setRecurrence(recurrence);
                    //calculate date scheduled for the new maintenance
                    calendar.setTime(latestMaintenance.getDateScheduled());
                    calendar.add(Calendar.DAY_OF_MONTH, recurrence.getRecurrenceType().getIntervalDays());
                    while (calendar.getTime().before(new Date())) {
                        //this is to assure that the maintenance isn't scheduled for a date in the past
                        //will only happen in case the timer fails for some reason and the maintenance is not created when it's supposed to
                        calendar.add(Calendar.DAY_OF_MONTH, recurrence.getRecurrenceType().getIntervalDays());
                    }
                    newMaintenance.setDateScheduled(calendar.getTime());
                    maintenanceDAO.insert(newMaintenance);

                    eventName = newMaintenance.getCode();
                    contractId = newMaintenance.getContract().getId();
                    url = Configuration.getInstance().getApplicationDomain() + "/sm/SecurityMaintenance.action?"
                            + "maintenanceEdit=" + "&contractId=" + contractId.toString()
                            + "&maintenanceId=" + newMaintenance.getId().toString();
                    numberDays = (int) Math.ceil(
                            (float) (newMaintenance.getDateScheduled().getTime() - new Date().getTime()) / (24 * 60 * 60 * 1000));
                    contract = newMaintenance.getContract().getContractDesignation();
                    company = newMaintenance.getContract().getCompany().getName();

                }
            }

            //update recurrence next scheduled date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(recurrence.getNextScheduledDate());
            calendar.add(Calendar.DAY_OF_MONTH, recurrence.getRecurrenceType().getIntervalDays());
            while (calendar.getTime().before(new Date())) {
                //this is to assure that the recurrence isn't scheduled for a date in the past
                calendar.add(Calendar.DAY_OF_MONTH, recurrence.getRecurrenceType().getIntervalDays());
            }
            recurrence.setNextScheduledDate(calendar.getTime());

            //send notification emails
            List<User> users = new ArrayList<User>();
            Set<User> distinctUsers = new HashSet<User>();
            for (RecurrenceNotification notification : recurrence.getNotifications()) {
                distinctUsers.add(notification.getUser());
            }
            //get expert and intermediate users for this contract
            distinctUsers.addAll(userDAO.findExpertAndIntermediateUsersByContract(contractId));
            users.addAll(distinctUsers);
            try {
                sendRecurrenceNotificationEmails(eventName, numberDays, url, company, contract, users);
            } catch (Exception ex) {
                LOGGER.error("Error while sending notification emails " + ex);
            }

        }
    }

    private String generateSequenceCode(SequenceCode sequenceCode, Long contractId) throws BusinessException {
        try {
            return sequenceGenerator.generateCode(sequenceCode, contractId);
        } catch (InvalidSequenceException e) {
            throw new BusinessException("Error generating sequence code for activity.");
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
