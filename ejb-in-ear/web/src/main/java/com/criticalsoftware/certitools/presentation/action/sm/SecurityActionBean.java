package com.criticalsoftware.certitools.presentation.action.sm;

import com.criticalsoftware.certitools.business.certitools.CompanyService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CSVException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ExcelException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.PDFException;
import com.criticalsoftware.certitools.business.sm.ActivityService;
import com.criticalsoftware.certitools.business.sm.AnomalyService;
import com.criticalsoftware.certitools.business.sm.CorrectiveActionService;
import com.criticalsoftware.certitools.business.sm.EmergencyActionService;
import com.criticalsoftware.certitools.business.sm.MaintenanceService;
import com.criticalsoftware.certitools.business.sm.RecurrenceService;
import com.criticalsoftware.certitools.business.sm.SecurityManagementService;
import com.criticalsoftware.certitools.business.sm.WorkService;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.entities.sm.Activity;
import com.criticalsoftware.certitools.entities.sm.ActivityType;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.EmergencyUser;
import com.criticalsoftware.certitools.entities.sm.Equipment;
import com.criticalsoftware.certitools.entities.sm.RecurrenceType;
import com.criticalsoftware.certitools.entities.sm.Risk;
import com.criticalsoftware.certitools.entities.sm.SecurityImpact;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.persistence.sm.dto.DocumentDTO;
import com.criticalsoftware.certitools.persistence.sm.dto.UpcomingEvent;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.Menu;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PTDateTypeConverter;
import com.criticalsoftware.certitools.presentation.util.PaginatedListAdapter;
import com.criticalsoftware.certitools.presentation.util.PlanUtils;
import com.criticalsoftware.certitools.presentation.util.SecurityMenu;
import com.criticalsoftware.certitools.presentation.util.export.sm.ReportPdf;
import com.criticalsoftware.certitools.presentation.util.export.sm.SmExporter;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.criticalsoftware.certitools.util.Utils;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.ajax.JavaScriptResolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class SecurityActionBean extends DisplayTagSupportActionBean implements ActionBean, ValidationErrorHandler {
    @EJBBean(value = "certitools/SecurityManagementService")
    private SecurityManagementService securityManagementService;

    @EJBBean(value = "certitools/ActivityService")
    private ActivityService activityService;

    @EJBBean(value = "certitools/RecurrenceService")
    private RecurrenceService recurrenceService;

    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    @EJBBean(value = "certitools/AnomalyService")
    private AnomalyService anomalyService;

    @EJBBean(value = "certitools/CorrectiveActionService")
    private CorrectiveActionService correctiveActionService;

    @EJBBean(value = "certitools/MaintenanceService")
    private MaintenanceService maintenanceService;

    @EJBBean(value = "certitools/WorkService")
    private WorkService workService;

    @EJBBean(value = "certitools/EmergencyActionService")
    private EmergencyActionService emergencyActionService;

    private ActionBeanContext actionBeanContext;
    private SecurityMenu securityMenu;
    private String topEvent;
    private String contentTemplate;

    private List<Company> companies;
    private List<Contract> contracts;
    private List<CorrectiveAction> correctiveActionsList;

    // form
    private Contract contract;
    private RecurrenceType recurrenceType;
    private ActivityType activityType;
    private String otherType;
    private Integer warningDays;
    private String otherId;
    private String usersToNotify;
    private Long companyId, contractId, activityId;
    private String activityCode;
    private String planModuleType;
    private List<RecurrenceType> recurrenceTypes;
    private List<User> users;
    private List<FileBean> newAttachments;
    private List<String> attachmentName;

    // front office
    private FileBean coverPicture, logoPicture;
    private List<UpcomingEvent> upcomingEvents;
    private long usedSize;

    // User Type
    private boolean isUserExpert;
    private boolean isUserIntermediate;
    private boolean isUserBasic;

    // chat
    private String chatTitle, chatPopoutUrl, chatGetUrl, chatPostUrl, chatMessage;

    // grid
    public PaginatedListAdapter activitiesAdapter;
    private List<String> years, activityStatus;
    private List<ActivityType> activityTypes;
    private String filterYear;
    private String filterSemester;
    private Boolean isOpen;
    private Long activityTypeId;
    private Long documentId;

    @ValidateNestedProperties({
            @Validate(field = "activityType.name", required = true, maxlength = 255, on = {"insertActivity", "editActivity"}),
            @Validate(field = "name", required = true, maxlength = 255, on = {"insertActivity", "editActivity"}),
            @Validate(field = "duration", required = true, maxlength = 255, on = {"insertActivity", "editActivity"}),
            @Validate(field = "internalResponsible", maxlength = 255, required = true, on = {"insertActivity", "editActivity"}),
            @Validate(field = "externalEntity", maxlength = 255, required = false, on = {"insertActivity", "editActivity"}),
            @Validate(field = "dateScheduled", required = true, converter = PTDateTypeConverter.class, on = {"insertActivity", "editActivity"}),
            @Validate(field = "closedDate", required = false, converter = PTDateTypeConverter.class, on = "editActivity")
    })
    private Activity activity;

    private boolean closed;
    private boolean edit;
    private boolean correctiveActions;
    private Date closedDate;
    private String error = null;
    private String success = null;

    private SubModuleType subModuleType;

    private static final Logger LOGGER = Logger.getInstance(SecurityActionBean.class);

    @DefaultHandler
    @Secure(roles = "user")
    public Resolution selectContract() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        companyId = getUserInSession().getCompany().getId();

        companies = securityManagementService.getUserCompanies(getUserInSession().getId());

        // select the contracts for the user default user company
        contracts = securityManagementService.getUserCompanyContracts(getUserInSession().getId(), companyId);

        if (contracts.size() == 0) {
            // find if the user has any valid contract
            for (Company company : companies) {
                contracts = securityManagementService.getUserCompanyContracts(getUserInSession().getId(), company.getId());
                if (contracts.size() > 0) {
                    companyId = company.getId();
                    break;
                }
            }
        }

        if (companies.size() == 1 && contracts.size() == 1) {
            return new RedirectResolution(SecurityActionBean.class, "frontOffice")
                    .addParameter("companyId", companyId)
                    .addParameter("contractId", contracts.get(0).getId());
        } else if (companies.size() > 1 || contracts.size() > 1) {
            return new ForwardResolution("/WEB-INF/jsps/sm/securitySelectContract.jsp");
        } else {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
    }

    @Secure(roles = "user")
    public Resolution loadCompanyContracts() throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (companyId != null) {
            contracts = securityManagementService.getUserCompanyContracts(getUserInSession().getId(), companyId);

            contracts = new ArrayList<Contract>(PlanUtils.cleanContractsForJavascriptResolution(contracts));

            for (Contract contract : contracts) {
                contract.setUserContract(null); // avoid lazy load exception when it converts the entire object to JSON
                contract.setSmCoverPicture(null);
                contract.setSmLogoPicture(null);
            }
        }

        return new JavaScriptResolution(contracts, Company.class, Date.class, UserContract.class, Module.class);
    }

    @Secure(roles = "user")
    public Resolution frontOffice() throws ObjectNotFoundException, IOException, CertitoolsAuthorizationException {
        final boolean isAdminOrCertitecna =
                securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId());

        if (!isAdminOrCertitecna
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        if (coverPicture != null) {
            if (isAdminOrCertitecna ||
                    securityManagementService.isUserExpert(getUserInSession().getId(), contractId)) {

                LOGGER.info("New contract cover picture set: " + coverPicture.getFileName());
                securityManagementService.saveContractCoverPicture(getUserInSession().getId(), contractId,
                        coverPicture.getInputStream(), coverPicture.getContentType(), coverPicture.getFileName());
                coverPicture.delete();
            }
        }

        if (logoPicture != null) {
            if (isAdminOrCertitecna ||
                    securityManagementService.isUserExpert(getUserInSession().getId(), contractId)) {

                LOGGER.info("New company logo picture set: " + logoPicture.getFileName());
                securityManagementService.saveContractLogoPicture(getUserInSession().getId(), contractId,
                        logoPicture.getInputStream(), logoPicture.getContentType(), logoPicture.getFileName());
                logoPicture.delete();
            }
        }

        setContract(securityManagementService.getContract(contractId));

        Long contractSpaceUsed = securityManagementService.getContractSpaceUsed(contractId);
        if (contractSpaceUsed != null) {
            setUsedSize(securityManagementService.getContractSpaceUsed(contractId) / 1024 / 1024); // the size is stored
            // in Bytes but we show it in MB
        } else {
            setUsedSize(0);
        }

        setTopEvent(null);
        setIsUserExpert(isAdminOrCertitecna ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setUpcomingEvents(securityManagementService.getUpcommingEvents(contractId));
        setContentTemplate("securityFrontOffice.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp");
    }

    @Secure(roles = "user")
    public Resolution getContractLogoPicture() {
        if (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        Document document = securityManagementService.getContractLogoPicture(contractId);
        final byte[] content = document.getContent();
        return new StreamingResolution(document.getContentType()) {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content);
            }
        };
    }

    @Secure(roles = "user")
    public Resolution getContractCoverPicture() {
        if (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        Document document = securityManagementService.getContractCoverPicture(contractId);
        final byte[] content = document.getContent();
        return new StreamingResolution(document.getContentType()) {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content);
            }
        };
    }

    @Secure(roles = "user")
    public Resolution activityPlanningAdd() throws ObjectNotFoundException {
        final boolean isAdminOrCertitecna =
                securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId());

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!isAdminOrCertitecna
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        setContract(securityManagementService.getContract(contractId));
        setEdit(false);
        setClosed(false);
        loadActivityTypes();
        loadRecurrenceTypes();
        users = securityManagementService.findBasicUsersByContract(contractId);

        setIsUserExpert(isAdminOrCertitecna ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setContentTemplate("securityActivityPlanningAdd.jsp");
        setSubModuleType(SubModuleType.ACTV);

        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contract.getId());
    }

    /**
     * Get the selected activity type.
     * If the other is selected, create a new type and set it to the activity.
     *
     * @throws CertitoolsAuthorizationException
     */
    private void getSelectedActivityType() throws CertitoolsAuthorizationException {
        // Get the Selected Activity Type
        Long activityTypeId = new Long(this.getActivity().getActivityType().getName());
        if (activityTypeId.compareTo(new Long("-1")) == 0) {
            LOGGER.info("Activity Type Other - Creating a new activity Type '" + otherType + "' to contract " + contractId);
            activityType = activityService.createActivityType(contractId, otherType, getUserInSession());
            this.activity.setActivityType(activityType);
        } else {
            for (ActivityType at : activityTypes) {
                if (at.getId().compareTo(activityTypeId) == 0) {
                    this.activity.setActivityType(at);
                }
            }
        }
    }

    /**
     * Get the selected users list to be notified.
     *
     * @return the list of users to be notified.
     */
    private List<User> getUsersToBeNotified() {
        if (usersToNotify == null)
            return new ArrayList<User>();
        // Get the user list to notify
        List<User> usersSelected = new ArrayList<User>();
        final String[] usersId = this.usersToNotify.split(",");
        for (String s : usersId) {
            if (s.trim().equals("")) continue;
            for (User u : users) {
                if (u.getId().equals(new Long(s.trim()))) {
                    usersSelected.add(u);
                    break;
                }
            }
        }
        return usersSelected;
    }

    public Resolution insertActivity() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        LOGGER.info("Insert New Activity - User Id: " + getUserInSession().getId());
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        users = securityManagementService.findBasicUsersByContract(contractId);
        List<User> usersSelected = getUsersToBeNotified();

        loadActivityTypes();
        loadRecurrenceTypes();
        getSelectedActivityType();

        // Get the Selected Recurrence Type
        Long recurrenceTypeId = activity.getRecurrence().getRecurrenceType().getId();
        if (recurrenceTypeId.compareTo(new Long("-1")) == 0) {
            recurrenceType = new RecurrenceType();
            recurrenceType.setId((long) -1);
        } else {
            for (RecurrenceType rt : recurrenceTypes) {
                if (rt.getId().compareTo(recurrenceTypeId) == 0) {
                    this.setRecurrenceType(rt);
                }
            }
        }
        activityId = this.activityService.createActivity(contractId, activity, recurrenceType.getId(),
                activity.getRecurrence().getWarningDays(), usersSelected, getUserInSession());

        getContext().getMessages().add(new LocalizableMessage("security.activity.add.success"));
        return new RedirectResolution(SecurityActionBean.class, "activityPlanningEdit")
                .addParameter("contractId", contractId)
                .addParameter("activityId", activityId);
    }

    @ValidationMethod(on = "insertActivity")
    public void validateInsertActivity(ValidationErrors errors) throws Exception {
        // check if date is OK
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        if (activity.getDateScheduled() == null || (activity.getDateScheduled().before(today.getTime()))) {
            errors.add("activity.dateScheduled", new LocalizableError("security.activity.scheduleDate.invalid"));
        }
        // Check if the type is other
        if (activity.getActivityType().getName().equals("-1")) {
            if (this.getOtherType() == null) {
                errors.add("otherType", new LocalizableError("security.activity.type.other.invalid"));
            }
        }
    }

    @Secure(roles = "user")
    public Resolution activityPlanningEdit() throws ObjectNotFoundException, BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        setContract(securityManagementService.getContract(contractId));
        activity = activityService.findActivity(activityId);
        activity.setCorrectiveActions(activityService.findOpenCorrectiveActions(activityId));

        users = securityManagementService.findBasicUsersByContract(contractId);
        setEdit(true);
        setClosed(activity.getClosed());

        if (activity.getRecurrence() != null) {
            recurrenceType = activity.getRecurrence().getRecurrenceType();
        } else {
            recurrenceType = new RecurrenceType();
            recurrenceType.setId((long) -1);
        }

        correctiveActionsList = activity.getCorrectiveActions();

        loadActivityTypes();
        loadRecurrenceTypes();

        setChatTitle(getActivity().getCode());
        setChatPopoutUrl("/sm/Security.action?activityPlanningChat=&contractId=" + contractId +
                "&activityId=" + activityId);
        setChatGetUrl("/sm/Security.action?getChatMessages=&contractId=" + contractId + "&activityId=" + activityId);
        setChatPostUrl("/sm/Security.action?addChatMessage=&contractId=" + contractId + "&activityId=" + activityId);

        final boolean isAdminOrCertitecna = securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId());

        setIsUserExpert(isAdminOrCertitecna ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setIsUserIntermediate(isAdminOrCertitecna ||
                securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId));
        setIsUserBasic(isAdminOrCertitecna ||
                securityManagementService.isUserBasic(getUserInSession().getId(), contractId));
        setSubModuleType(SubModuleType.ACTV);
        setContentTemplate("securityActivityPlanningAdd.jsp");
        getContext().getResponse().setHeader("Stripes-Success", "OK");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contract.getId())
                .addParameter("activityId", activityId);
    }

    public Resolution editActivity() throws ObjectNotFoundException, CertitoolsAuthorizationException, IOException, BusinessException {
        LOGGER.info("Editing activity - " + activityId + " -  contract " + contractId + " - User: " + getUserInSession().getId());

        final boolean isAdminOrCertitecna =
                securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId());

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!isAdminOrCertitecna
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        List<DocumentDTO> documents = new ArrayList<DocumentDTO>();
        if (newAttachments != null) {
            int pos = 0;
            for (FileBean fb : newAttachments) {
                if (fb != null) {
                    String name = fb.getFileName();
                    if (attachmentName != null && pos < attachmentName.size() && attachmentName.get(pos) != null) {
                        name = attachmentName.get(pos);
                    }
                    documents.add(new DocumentDTO(fb.getFileName(), name, fb.getContentType(), fb.getInputStream()));
                }
                pos++;
            }
        }

        if (isAdminOrCertitecna ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId)) {
            users = securityManagementService.findBasicUsersByContract(contractId);
            loadActivityTypes();
            getSelectedActivityType();

            List<User> usersSelected = getUsersToBeNotified();
            Long recurrenceTypeId = null;
            activity.setId(activityId);
            activity.getRecurrence().getRecurrenceType().setWarningDays(activity.getRecurrence().getWarningDays());
            if (activity.getRecurrence().getRecurrenceType().getId().compareTo((long) -1) != 0) {
                recurrenceTypeId = activity.getRecurrence().getRecurrenceType().getId();
            }
            activityService.updateActivityMainFields(contractId, activity,
                    recurrenceTypeId,
                    activity.getRecurrence().getWarningDays(),
                    usersSelected, getUserInSession());
        }

        if (activity == null) {
            activityService.editActivity(contractId, activityId, documents, null, getUserInSession());
        } else {
            activityService.editActivity(contractId, activityId, documents, activity.getClosedDate(), getUserInSession());
        }

        if (correctiveActions) {
            activity = activityService.findActivity(activityId);
            return new RedirectResolution(SecurityActionsPlanningActionBean.class, "actionsPlanningAdd")
                    .addParameter("contractId", contractId)
                    .addParameter("activityId", activityId)
                    .addParameter("activityCode", activity.getCode());
        }

        getContext().getMessages().add(new LocalizableMessage("security.activity.edit.success"));
        return new RedirectResolution(SecurityActionBean.class, "activityPlanningEdit")
                .addParameter("contractId", contractId)
                .addParameter("activityId", activityId)
                .addParameter("success", "security.activity.edit.success");
    }


    @ValidationMethod(on = "editActivity")
    public void validateEditActivity(ValidationErrors errors) throws Exception {
        if (activity.getDateScheduled() == null) {
            errors.add("activity.dateScheduled", new LocalizableError("security.activity.scheduleDate.invalid"));
        } else if (!activity.getDateScheduled().equals(activityService.findActivity(activityId).getDateScheduled())) {

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            if (activity.getDateScheduled().before(today.getTime())) {
                errors.add("activity.dateScheduled", new LocalizableError("security.activity.scheduleDate.invalid"));
            }
        }

        correctiveActionsList = activityService.findOpenCorrectiveActions(activityId);
        if ((correctiveActionsList != null && correctiveActionsList.size() > 0) && activity.getClosedDate() != null) {
            errors.add("closeDate", new LocalizableError("security.activity.edit.open.actions"));
        }
    }

    @Secure(roles = "user")
    public Resolution activityPlanningChat() throws ObjectNotFoundException, BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        activity = activityService.findActivity(activityId);
        setClosed(activity.getClosed());
        setChatTitle(activity.getCode());
        setChatGetUrl("/sm/Security.action?getChatMessages=&contractId=" + contractId + "&activityId=" + activityId);
        setChatPostUrl("/sm/Security.action?addChatMessage=&contractId=" + contractId + "&activityId=" + activityId);
        setIsUserBasic(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserBasic(getUserInSession().getId(), contractId));
        return new ForwardResolution("/WEB-INF/jsps/sm/securityViewChat.jsp")
                .addParameter("contractId", contractId)
                .addParameter("activityId", activityId);
    }

    @Secure(roles = "user")
    public Resolution addChatMessage() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        activityService.addChatMessage(contractId, activityId, chatMessage, getUserInSession());
        return getChatMessages();
    }

    @Secure(roles = "user")
    public Resolution getChatMessages() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        List<Chat> chats = activityService.findChatMessages(activityId);
        SimpleDateFormat dateFormat = new SimpleDateFormat(Configuration.getInstance().getDateHourPattern());
        String json = "[";
        for (Chat chat : chats) {
            json += "{ time: '" + dateFormat.format(chat.getDatetime()) + "', user: '" + chat.getUser().getName()
                    + "', message: '" + chat.getMessage().replace("\n", "<br>") + "'}, ";
        }
        if (chats.size() > 0) {
            json = json.substring(0, json.length() - 2);
        }
        json += "]";
        return new StreamingResolution("text", json);
    }

    @Secure(roles = "user")
    public Resolution activityPlanningGrid() throws BusinessException {
        final boolean isAdminOrCertitecna =
                securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId());

        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!isAdminOrCertitecna
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        PaginatedListWrapper<Activity> wrapper =
                new PaginatedListWrapper<Activity>(getPage(),
                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(), getDirOrder(),
                        isExportRequest());

        activitiesAdapter = new PaginatedListAdapter<Activity>(activityService.findActivitiesByContract(contractId,
                wrapper, activityTypeId, filterYear, isOpen));

        activityTypes = new ArrayList<ActivityType>();
        activityTypes.add(new ActivityType());
        activityTypes.addAll(activityService.findActivityTypes(contractId));

        years = new ArrayList<String>();
        years.add("");
        years.addAll(activityService.findActivityYears(contractId));

        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility.getLocalizedFieldName("security.activity.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility.getLocalizedFieldName("security.activity.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility.getLocalizedFieldName("security.activity.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility.getLocalizedFieldName("security.activity.filename.pdf", null, null, locale));

        setSubModuleType(SubModuleType.ACTV);
        setContract(securityManagementService.getContract(contractId));
        setIsUserExpert(isAdminOrCertitecna ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setIsUserIntermediate(isAdminOrCertitecna ||
                securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId));
        setContentTemplate("securityActivityPlanningGrid.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp");
    }

    @Secure(roles = "user")
    public Resolution getDocument() {
        if (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        Document document = securityManagementService.getDocument(documentId);
        final byte[] content = document.getContent();
        return new StreamingResolution(document.getContentType()) {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content);
            }
        }.setFilename(document.getName());
    }

    @Secure(roles = "user")
    public Resolution deleteDocument() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        activityService.deleteActivity(contractId, activityId, user);
        getContext().getMessages().add(new LocalizableMessage("security.activity.delete.sucess"));
        return new RedirectResolution(SecurityActionBean.class, "activityPlanningGrid")
                .addParameter("contractId", contractId)
                .addParameters(getDisplayTagParameters());
    }

    @Secure(roles = "user")
    public Resolution getChatPdf() throws ObjectNotFoundException, PDFException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        ReportPdf reportPdf = new ReportPdf(getContext());
        Activity activity = activityService.findActivityForChatPdf(activityId);
        final ByteArrayOutputStream content = reportPdf.generateChatPDF(activity);
        return new StreamingResolution("application/pdf") {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content.toByteArray());
            }
        }.setFilename(getMessage(getContext().getLocale(), "security.pdf.filename"));
    }

    @Secure(roles = "user")
    public Resolution reopenActivity() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        activityService.reopenActivity(contractId, activityId, getUserInSession());
        getContext().getMessages().add(new LocalizableMessage("security.activity.reopen.success"));
        return new RedirectResolution(SecurityActionBean.class, "activityPlanningEdit")
                .addParameter("contractId", contractId)
                .addParameter("activityId", activityId);
    }

    @Secure(roles = "user")
    public Resolution deleteFile() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ACTV)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        activityService.deleteDocument(contractId, activityId, documentId, user);
        getContext().getMessages().add(new LocalizableMessage("security.document.delete.success"));
        return new RedirectResolution(SecurityActionBean.class, "activityPlanningEdit")
                .addParameter("contractId", contractId)
                .addParameter("activityId", activityId)
                .addParameters(getDisplayTagParameters());
    }

    @Secure(roles = "user")
    public Resolution smExportForm() throws BusinessException, ObjectNotFoundException,
            CertitoolsAuthorizationException {
        if (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        years = securityManagementService.findSecurityManagementYears(contractId);
        closed = true;

        return new ForwardResolution("/WEB-INF/jsps/sm/securityExport.jsp");
    }

    @Secure(roles = "user")
    public Resolution export() throws ObjectNotFoundException, IOException, CertitoolsAuthorizationException,
            PDFException, ExcelException, CSVException {
        final User user = getUserInSession();
        if (!securityManagementService.isUserAdministratorOrCertitecna(user.getId())
                && !securityManagementService.isUserExpert(user.getId(), contractId)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        //checking filters
        if (filterYear.equals("-1")) {
            filterYear = null;
            filterSemester = null;
        } else if (filterSemester.equals("-1")){
            filterSemester = null;
        }
        if (closed) {
            isOpen = false;
        }
        LOGGER.info("Exporting Security Management files: user = " + user.getEmail() + ", contract = " + contractId +
                ", year = " + filterYear + ", semester = " + filterSemester +  ", open = " + isOpen);
        long startZip = System.currentTimeMillis();

        //Load the information and create the export files
        contract = securityManagementService.getContractWithCompany(contractId);
        Map<String, InputStream> exportFiles = new LinkedHashMap<String, InputStream>();
        Map<String, Long> relatedDocuments = new LinkedHashMap<String, Long>();
        createExportFiles(exportFiles, relatedDocuments);

        String filename = contract.getContractDesignation() +
                (filterYear != null ? ("_" + filterYear) : "") +
                (filterSemester != null ? ("_" + filterSemester) : "") + ".zip";
        filename = Utils.removeAccentedChars(filename.replaceAll("/", "_").replaceAll("\"", "\\\""));

        HttpServletResponse response = getContext().getResponse();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentType("application/zip");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setCharacterEncoding("UTF-8");

        ServletOutputStream sos = response.getOutputStream();
        // Create the ZIP file
        ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(sos);
        zaos.setFallbackToUTF8(true);
        zaos.setUseLanguageEncodingFlag(true);
        zaos.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS);
        zaos.setUseZip64(Zip64Mode.AsNeeded);

        //Add all the system generated files to the zip
        for (Map.Entry<String, InputStream> entry : exportFiles.entrySet()) {
            //Create a zipEntry and add it to the ZipOutputStream
            ZipArchiveEntry zipEntry = new ZipArchiveEntry(entry.getKey());
            zaos.putArchiveEntry(zipEntry);

            //Use Apache Commons to transfer the InputStream to the OutputStream
            //at this moment the file is already being downloaded and growing
            IOUtils.copy(entry.getValue(), zaos);

            zaos.flush();
            zaos.closeArchiveEntry();
            entry.getValue().close();
        }
        //Add all the user uploaded documents to the zip
        for (Map.Entry<String, Long> entry : relatedDocuments.entrySet()) {
            ZipArchiveEntry zipEntry = new ZipArchiveEntry(entry.getKey());
            zaos.putArchiveEntry(zipEntry);

            //Get the document input stream from DB
            InputStream inputStream = securityManagementService.getDocumentContentInputStream(entry.getValue());
            IOUtils.copy(inputStream, zaos);

            zaos.flush();
            zaos.closeArchiveEntry();
            inputStream.close();
        }

        zaos.close();
        sos.close();
        LOGGER.info("Downloaded zip export file: duration = " + (System.currentTimeMillis() - startZip) + " ms");

        return null;
    }

    @Secure(roles = "user")
    public Resolution smDeleteForm() throws BusinessException, ObjectNotFoundException,
            CertitoolsAuthorizationException {
        if (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        years = securityManagementService.findSecurityManagementYears(contractId);
        closed = true;

        return new ForwardResolution("/WEB-INF/jsps/sm/securityDelete.jsp");
    }

    @Secure(roles = "user")
    public Resolution delete() throws ObjectNotFoundException, IOException, CertitoolsAuthorizationException,
            PDFException, ExcelException, CSVException {
        User user = getUserInSession();
        if (!securityManagementService.isUserAdministratorOrCertitecna(user.getId())
                && !securityManagementService.isUserExpert(user.getId(), contractId)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        //checking filters
        if (filterYear.equals("-1")) {
            filterYear = null;
        }
        if (closed) {
            isOpen = false;
        }
        LOGGER.info("Deleting Security Management files: user = " + user.getEmail() + ", contract = " + contractId +
                ", year = " + filterYear + ", open = " + isOpen);

        securityManagementService.deleteRecordsByContract(contractId, filterYear, isOpen, user);

        return new RedirectResolution(SecurityActionBean.class, "frontOffice")
                .addParameter("contractId", contractId);
    }

    @After(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        if (contractId != null) {
            securityMenu = new SecurityMenu(
                    securityManagementService.countOpenItems(contractId),
                    securityManagementService.findContractSubModules(contractId),
                    securityManagementService.isUserBasic(getUserInSession().getId(), contractId),
                    securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId),
                    securityManagementService.isUserExpert(getUserInSession().getId(), contractId),
                    securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()));
        }
        getMenu().select(MenuItem.Item.MENU_SECURITY, null);
        setTopEvent("activityPlanningGrid");
    }

    private static String getMessage(Locale locale, String key, Object... arguments) {
        return new MessageFormat(ResourceBundle.getBundle("StripesResources", locale).getString(key), locale)
                .format(arguments);
    }

    public User getUserInSession() {
        return (User) getContext().getRequest().getSession().getAttribute("user");
    }

    private void loadActivityTypes() {
        activityTypes = activityService.findActivityTypes(contractId);
        setOtherId("-1");
    }

    private void loadRecurrenceTypes() {
        recurrenceTypes = recurrenceService.findRecurrenceTypes();
    }

    public Menu getMenu() {
        return (Menu) getContext().getRequest().getSession().getAttribute("menu");
    }

    public SecurityMenu getSecurityMenu() {
        return securityMenu;
    }

    public void setSecurityMenu(SecurityMenu securityMenu) {
        this.securityMenu = securityMenu;
    }

    public String getContentTemplate() {
        return contentTemplate;
    }

    public void setContentTemplate(String contentTemplate) {
        this.contentTemplate = contentTemplate;
    }

    public void setContext(ActionBeanContext actionBeanContext) {
        this.actionBeanContext = actionBeanContext;
    }

    public ActionBeanContext getContext() {
        return actionBeanContext;
    }

    public String getPlanModuleType() {
        return planModuleType;
    }

    public void setPlanModuleType(String planModuleType) {
        this.planModuleType = planModuleType;
    }

    public String getTopEvent() {
        return topEvent;
    }

    public void setTopEvent(String topEvent) {
        this.topEvent = topEvent;
    }

    public long getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(long usedSize) {
        this.usedSize = usedSize;
    }

    public List<UpcomingEvent> getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(List<UpcomingEvent> upcomingEvent) {
        this.upcomingEvents = upcomingEvent;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public List<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(List<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public List<RecurrenceType> getRecurrenceTypes() {
        return recurrenceTypes;
    }

    public void setRecurrenceTypes(List<RecurrenceType> recurrenceTypes) {
        this.recurrenceTypes = recurrenceTypes;
    }

    public boolean getClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public FileBean getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(FileBean coverPicture) {
        this.coverPicture = coverPicture;
    }

    public FileBean getLogoPicture() {
        return logoPicture;
    }

    public void setLogoPicture(FileBean logoPicture) {
        this.logoPicture = logoPicture;
    }

    public boolean getIsUserBasic() {
        return isUserBasic;
    }

    public void setIsUserBasic(boolean isUserBasic) {
        this.isUserBasic = isUserBasic;
    }

    public boolean getIsUserIntermediate() {
        return isUserIntermediate;
    }

    public void setIsUserIntermediate(boolean isUserIntermediate) {
        this.isUserIntermediate = isUserIntermediate;
    }

    public boolean getIsUserExpert() {
        return isUserExpert;
    }

    public void setIsUserExpert(boolean isUserExpert) {
        this.isUserExpert = isUserExpert;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getOtherType() {
        return otherType;
    }

    public void setOtherType(String otherType) {
        this.otherType = otherType;
    }

    public String getUsersToNotify() {
        return usersToNotify;
    }

    public void setUsersToNotify(String usersToNotify) {
        this.usersToNotify = usersToNotify;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public Integer getWarningDays() {
        return warningDays;
    }

    public void setWarningDays(Integer warningDays) {
        this.warningDays = warningDays;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public PaginatedListAdapter getActivitiesAdapter() {
        return activitiesAdapter;
    }

    public void setActivitiesAdapter(PaginatedListAdapter activitiesAdapter) {
        this.activitiesAdapter = activitiesAdapter;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public List<FileBean> getNewAttachments() {
        return newAttachments;
    }

    public void setNewAttachments(List<FileBean> newAttachments) {
        this.newAttachments = newAttachments;
    }

    public List<String> getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(List<String> attachmentName) {
        this.attachmentName = attachmentName;
    }

    public boolean isCorrectiveActions() {
        return correctiveActions;
    }

    public void setCorrectiveActions(boolean correctiveActions) {
        this.correctiveActions = correctiveActions;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public List<String> getYears() {
        return years;
    }

    public void setYears(List<String> years) {
        this.years = years;
    }

    public List<String> getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(List<String> activityStatus) {
        this.activityStatus = activityStatus;
    }

    public String getFilterYear() {
        return filterYear;
    }

    public void setFilterYear(String filterYear) {
        this.filterYear = filterYear;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Long getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(Long activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    public String getChatPopoutUrl() {
        return chatPopoutUrl;
    }

    public void setChatPopoutUrl(String chatPopoutUrl) {
        this.chatPopoutUrl = chatPopoutUrl;
    }

    public String getChatGetUrl() {
        return chatGetUrl;
    }

    public void setChatGetUrl(String chatGetUrl) {
        this.chatGetUrl = chatGetUrl;
    }

    public String getChatPostUrl() {
        return chatPostUrl;
    }

    public void setChatPostUrl(String chatPostUrl) {
        this.chatPostUrl = chatPostUrl;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<CorrectiveAction> getCorrectiveActionsList() {
        return correctiveActionsList;
    }

    public void setCorrectiveActionsList(List<CorrectiveAction> correctiveActionsList) {
        this.correctiveActionsList = correctiveActionsList;
    }

    public SubModuleType getSubModuleType() {
        return subModuleType;
    }

    public void setSubModuleType(SubModuleType subModuleType) {
        this.subModuleType = subModuleType;
    }

    public String getFilterSemester() {
        return filterSemester;
    }

    public void setFilterSemester(String filterSemester) {
        this.filterSemester = filterSemester;
    }

    //this is for the chat because the emergency action module uses a token to give temporary access to the users
    public boolean getIsValidToken() {
        return false;
    }

    @Override
    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertActivity")) {
            return activityPlanningAdd();
        }
        if (getContext().getEventName().equals("editActivity")) {
            return activityPlanningEdit();

        }
        return null;
    }

    private void createExportFiles(Map<String, InputStream> files, Map<String, Long> relatedDocuments)
            throws CSVException, ExcelException, PDFException {

        List<SubModuleType> subModules = securityManagementService.findContractSubModules(contractId);
        Map<SubModuleType, List> smRecords = new HashMap<SubModuleType, List>();
        List<EmergencyUser> emergencyUsers = null;
        List<User> users = null;
        List<SecurityImpact> securityImpacts = null;
        List<Risk> risks = null;
        List<Equipment> equipments = null;

        long startLoad = System.currentTimeMillis();
        for (SubModuleType subModule : subModules) {
            switch (subModule) {
                case ACTV:
                    smRecords.put(subModule, activityService.findActivitiesByContract(contractId, filterYear,
                            filterSemester, isOpen));
                    break;
                case ANOM:
                    smRecords.put(subModule, anomalyService.findAnomaliesByContract(contractId, filterYear,
                            filterSemester, isOpen));
                    if (securityImpacts == null) {
                        securityImpacts = securityManagementService.findSecurityImpacts();
                    }
                    break;
                case SIW:
                    smRecords.put(subModule, workService.findSecurityImpactWorksByContract(contractId,
                            filterYear, filterSemester, isOpen));
                    risks = workService.findRisks();
                    if (securityImpacts == null) {
                        securityImpacts = securityManagementService.findSecurityImpacts();
                    }
                    break;
                case APC:
                    smRecords.put(subModule, correctiveActionService.findCorrectiveActionsByContract(contractId,
                            filterYear, filterSemester, isOpen));
                    break;
                case MNT:
                    smRecords.put(subModule, maintenanceService.findMaintenancesByContract(contractId, filterYear,
                            filterSemester, isOpen));
                    equipments = maintenanceService.findEquipments(contractId);
                    break;
                case EMRG:
                    smRecords.put(subModule, emergencyActionService.findEmergencyActionsByContract(contractId,
                            filterYear, filterSemester, isOpen));
                    emergencyUsers = emergencyActionService.findEmergencyUsers(contractId);
                    users = securityManagementService.findUsersByContractAndSecurityPermissions(contractId);
                    break;
            }
        }
        long endLoad = System.currentTimeMillis();
        LOGGER.debug("Loaded all the necessary information for export: duration = " + (endLoad - startLoad) + " ms");

        //creating the export files
        SmExporter smExporter = new SmExporter(contract, smRecords, securityImpacts, risks, equipments,
                users, emergencyUsers, getContext(), files, relatedDocuments);
        smExporter.createExportFiles();
        long endCreateFiles = System.currentTimeMillis();
        LOGGER.debug("Created export files: duration = " + (endCreateFiles - endLoad) + " ms");
    }
}
