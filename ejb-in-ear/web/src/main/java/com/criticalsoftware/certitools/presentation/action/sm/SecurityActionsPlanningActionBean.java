package com.criticalsoftware.certitools.presentation.action.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.PDFException;
import com.criticalsoftware.certitools.business.sm.CorrectiveActionService;
import com.criticalsoftware.certitools.business.sm.SecurityManagementService;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.enums.AnomalyType;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.entities.sm.enums.WorkType;
import com.criticalsoftware.certitools.persistence.sm.dto.DocumentDTO;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.Menu;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PTDateTypeConverter;
import com.criticalsoftware.certitools.presentation.util.PaginatedListAdapter;
import com.criticalsoftware.certitools.presentation.util.SecurityMenu;
import com.criticalsoftware.certitools.presentation.util.export.sm.ReportPdf;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
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
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class SecurityActionsPlanningActionBean extends DisplayTagSupportActionBean implements ActionBean, ValidationErrorHandler {
    @EJBBean(value = "certitools/SecurityManagementService")
    private SecurityManagementService securityManagementService;

    @EJBBean(value = "certitools/CorrectiveActionService")
    private CorrectiveActionService correctiveActionService;

    private ActionBeanContext actionBeanContext;

    private SecurityMenu securityMenu;

    private Contract contract;
    private Long contractId, activityId, documentId, anomalyId, impactWorkId, maintenanceId;
    private String activityCode, anomalyCode, impactWorkCode, maintenanceCode;
    private boolean isUserExpert, isUserIntermediate, isUserBasic;
    private String topEvent, event;
    private String contentTemplate;

    private boolean edit, isAnother;
    private boolean closed;

    private List<FileBean> newAttachments;
    private List<String> attachmentName;

    private List<User> users;
    private String usersToNotify;

    @ValidateNestedProperties({
            @Validate(field = "duration", required = true, maxlength = 255, on = {"insertAction", "editAction"}),
            @Validate(field = "description", required = true, maxlength = 5000, on = {"insertAction", "editAction"}),
            @Validate(field = "executionResponsible", maxlength = 255, required = true, on = {"insertAction", "editAction"}),
            @Validate(field = "startDate", required = true, converter = PTDateTypeConverter.class, on = {"insertAction", "editAction"}),
            @Validate(field = "closeDate", required = false, converter = PTDateTypeConverter.class, on = "editAction"),
            @Validate(field = "notes", required = false, maxlength = 5000, on = {"editAction"})
    })
    private CorrectiveAction correctiveAction;

    // chat
    private String chatTitle, chatPopoutUrl, chatGetUrl, chatPostUrl, chatMessage;

    // grid
    public PaginatedListAdapter actionsAdapter;
    private List<String> actionYears, actionStatus;
    private String filterYear;
    private Boolean isOpen;
    private Long correctiveActionId;

    private static final Logger LOGGER = Logger.getInstance(SecurityActionsPlanningActionBean.class);

    @DefaultHandler
    @Secure(roles = "user")
    public Resolution actionsPlanningGrid() throws BusinessException {

        final boolean isUserAdminOrCertitecna = securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId());
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!isUserAdminOrCertitecna
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        PaginatedListWrapper<CorrectiveAction> wrapper =
                new PaginatedListWrapper<CorrectiveAction>(getPage(),
                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(), getDirOrder(),
                        isExportRequest());

        actionsAdapter = new PaginatedListAdapter<CorrectiveAction>(
                correctiveActionService.findCorrectiveActionsByContract(contractId, wrapper, filterYear, isOpen));

        actionYears = new ArrayList<String>();
        actionYears.add("");
        actionYears.addAll(correctiveActionService.findCorrectiveActionYears(contractId));

        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility.getLocalizedFieldName("security.action.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility.getLocalizedFieldName("security.action.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility.getLocalizedFieldName("security.action.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility.getLocalizedFieldName("security.action.filename.pdf", null, null, locale));

        setContract(securityManagementService.getContract(contractId));
        setIsUserExpert(isUserAdminOrCertitecna ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setContentTemplate("securityActionsPlanningGrid.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp");
    }

    @Secure(roles = "user")
    public Resolution actionsPlanningAdd() throws ObjectNotFoundException {
        final boolean isUserAdminOrCertitecna = securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId());
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!isUserAdminOrCertitecna
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        setContract(securityManagementService.getContract(contractId));
        setEdit(false);
        setClosed(false);
        setUserPermissionType();
        users = securityManagementService.findBasicUsersByContract(contractId);

        setContentTemplate("securityActionsPlanningAdd.jsp");


        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contract.getId());
    }

    public Resolution insertAnotherAction() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        LOGGER.info("Insert New Action - User Id: " + getUserInSession().getId());

        /*
        NEW */
        users = securityManagementService.findBasicUsersByContract(contractId);
        List<User> usersSelected = getUsersToBeNotified();

        getContext().getMessages().add(new LocalizableMessage("security.actions.add.success"));
        correctiveActionId = correctiveActionService.createCorrectiveAction(contractId, correctiveAction, activityId, anomalyId, impactWorkId, maintenanceId, usersSelected, getUserInSession());

        return new RedirectResolution(SecurityActionsPlanningActionBean.class, "actionsPlanningAdd")
                .addParameter("contractId", contractId)
                .addParameter("event", event)
                .addParameter("activityId", activityId)
                .addParameter("activityCode", activityCode)
                .addParameter("anomalyId", anomalyId)
                .addParameter("anomalyCode", anomalyCode)
                .addParameter("impactWorkId", impactWorkId)
                .addParameter("impactWorkCode", impactWorkCode)
                .addParameter("maintenanceId", maintenanceId)
                .addParameter("maintenanceCode", maintenanceCode);
    }

    /*
    NEW */

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

    public Resolution insertAction() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        LOGGER.info("Insert New Action - User Id: " + getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("security.actions.add.success"));

        /*
        NEW */
        users = securityManagementService.findBasicUsersByContract(contractId);
        LOGGER.info("SecurityActionsPlanningActionBean.insertAction Users: " + users.toString());
        List<User> usersSelected = getUsersToBeNotified();


        correctiveActionId = correctiveActionService.createCorrectiveAction(contractId, correctiveAction, activityId, anomalyId, impactWorkId, maintenanceId, usersSelected, getUserInSession());

        if (activityId != null) {
            return new RedirectResolution(SecurityActionBean.class, "activityPlanningEdit")
                    .addParameter("contractId", contractId)
                    .addParameter("activityId", activityId);
        }
        if (anomalyId != null) {
            return new RedirectResolution(SecurityAnomaliesRecordActionBean.class, "editPageCommon")
                    .addParameter("contractId", contractId)
                    .addParameter("anomalyId", anomalyId);
        }
        if (impactWorkId != null) {
            return new RedirectResolution(SecurityImpactWorkActionBean.class, "editPageCommon")
                    .addParameter("contractId", contractId)
                    .addParameter("impactWorkId", impactWorkId);
        }
        if (maintenanceId != null) {
            return new RedirectResolution(SecurityMaintenanceActionBean.class, "maintenanceEdit")
                    .addParameter("contractId", contractId)
                    .addParameter("maintenanceId", maintenanceId);
        }
        return new RedirectResolution(SecurityActionsPlanningActionBean.class, "actionsPlanningEdit")
                .addParameter("contractId", contractId)
                .addParameter("correctiveActionId", correctiveActionId);
    }

    public Resolution viewActions() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        if (activityId != null) {
            return new RedirectResolution(SecurityActionBean.class, "activityPlanningEdit")
                    .addParameter("contractId", contractId)
                    .addParameter("activityId", activityId);
        }
        if (anomalyId != null) {
            return new RedirectResolution(SecurityAnomaliesRecordActionBean.class, "editPageCommon")
                    .addParameter("contractId", contractId)
                    .addParameter("anomalyId", anomalyId);
        }
        if (impactWorkId != null) {
            return new RedirectResolution(SecurityImpactWorkActionBean.class, "editPageCommon")
                    .addParameter("contractId", contractId)
                    .addParameter("impactWorkId", impactWorkId);
        }
        if (maintenanceId != null) {
            return new RedirectResolution(SecurityMaintenanceActionBean.class, "maintenanceEdit")
                    .addParameter("contractId", contractId)
                    .addParameter("maintenanceId", maintenanceId);
        }
        return new RedirectResolution(SecurityActionsPlanningActionBean.class, "actionsPlanningEdit")
                .addParameter("contractId", contractId)
                .addParameter("correctiveActionId", correctiveActionId);
    }


    @Secure(roles = "user")
    public Resolution actionsPlanningEdit() throws ObjectNotFoundException {
        final boolean isUserAdminOrCertitecna = securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId());
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!isUserAdminOrCertitecna
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        setContract(securityManagementService.getContract(contractId));
        correctiveAction = correctiveActionService.findCorrectiveAction(correctiveActionId);
        users = securityManagementService.findBasicUsersByContract(contractId);

        if (correctiveAction.getAnomaly() != null) {
            if (correctiveAction.getAnomaly().getAnomalyType() == AnomalyType.ANOMALY) {
                event = "anomaliesRecordEdit";
            } else {
                event = "occurrencesRecordEdit";
            }
        } else if (correctiveAction.getSecurityImpactWork() != null) {
            if (correctiveAction.getSecurityImpactWork().getWorkType() == WorkType.MODIFICATION) {
                event = "modificationsChangesEdit";
            } else {
                event = "authorizationEdit";
            }
        }
        setClosed(correctiveAction.getClosed());

        setChatTitle(getCorrectiveAction().getCode());
        setChatPopoutUrl("/sm/SecurityActionsPlanning.action?actionsPlanningChat=&contractId=" + contractId +
                "&correctiveActionId=" + correctiveActionId);
        setChatGetUrl("/sm/SecurityActionsPlanning.action?getChatMessages=&contractId=" + contractId +
                "&correctiveActionId=" + correctiveActionId);
        setChatPostUrl("/sm/SecurityActionsPlanning.action?addChatMessage=&contractId=" + contractId +
                "&correctiveActionId=" + correctiveActionId);

        setEdit(true);
        setUserPermissionType();
        setContentTemplate("securityActionsPlanningAdd.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contractId)
                .addParameter("correctiveActionId", correctiveActionId);
    }

    public void setUserPermissionType() {
        final boolean isUserAdminOrCertitecna = securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId());
        setIsUserBasic(isUserAdminOrCertitecna ||
                securityManagementService.isUserBasic(getUserInSession().getId(), contractId));
        setIsUserIntermediate(isUserAdminOrCertitecna ||
                securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId));
        setIsUserExpert(isUserAdminOrCertitecna ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
    }

    public Resolution editAction() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException, IOException {
        final boolean isUserAdminOrCertitecna = securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId());
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!isUserAdminOrCertitecna
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        LOGGER.info("Editing Action " + correctiveActionId + " -  contract " + contractId + " - User: " + getUserInSession().getId());
        correctiveAction.setId(correctiveActionId);

        users = securityManagementService.findBasicUsersByContract(contractId);

        boolean areFieldsModified = false;
        if (isUserAdminOrCertitecna ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId)) {
            areFieldsModified = correctiveActionService.updateCorrectiveActionMainFields(contractId, correctiveAction, getUserInSession());
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

        users = securityManagementService.findBasicUsersByContract(contractId);
        List<User> usersSelected = getUsersToBeNotified();

        correctiveActionService.editCorrectiveAction(contractId, correctiveAction, documents, usersSelected, getUserInSession(), areFieldsModified);
        getContext().getMessages().add(new LocalizableMessage("security.action.edit.success"));
        getContext().getResponse().setHeader("Stripes-Success", "OK");

        return new RedirectResolution(SecurityActionsPlanningActionBean.class, "actionsPlanningEdit")
                .addParameter("contractId", contractId)
                .addParameter("correctiveActionId", correctiveActionId);
    }

    @Secure(roles = "user")
    public Resolution reopenAction() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!securityManagementService.isUserAdministratorOrCertitecna(user.getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        correctiveActionService.reopenCorrectiveAction(contractId, correctiveActionId, user);
        getContext().getMessages().add(new LocalizableMessage("security.actions.reopen.success"));
        return new RedirectResolution(SecurityActionsPlanningActionBean.class, "actionsPlanningEdit")
                .addParameter("contractId", contractId)
                .addParameter("correctiveActionId", correctiveActionId);
    }

    @Secure(roles = "user")
    public Resolution getDocument() {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
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
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        correctiveActionService.deleteDocument(contractId, correctiveActionId, documentId, user);
        getContext().getMessages().add(new LocalizableMessage("security.document.delete.success"));
        return new RedirectResolution(SecurityActionsPlanningActionBean.class, "actionsPlanningEdit")
                .addParameter("contractId", contractId)
                .addParameter("correctiveActionId", correctiveActionId);
    }

    @Secure(roles = "user")
    public Resolution getChatPdf() throws ObjectNotFoundException, PDFException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        ReportPdf reportPdf = new ReportPdf(getContext());
        CorrectiveAction correctiveAction = correctiveActionService.findCorrectiveActionForChatPdf(correctiveActionId);
        final ByteArrayOutputStream content = reportPdf.generateChatPDF(correctiveAction);
        return new StreamingResolution("application/pdf") {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content.toByteArray());
            }
        }.setFilename(getMessage(getContext().getLocale(), "security.pdf.filename"));
    }

    @Secure(roles = "user")
    public Resolution getCorrectiveActionReportPdf() throws ObjectNotFoundException, PDFException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        ReportPdf reportPdf = new ReportPdf(getContext());
        CorrectiveAction correctiveAction = correctiveActionService.findCorrectiveAction(correctiveActionId);
        final ByteArrayOutputStream content = reportPdf.generateCorrectiveActionPDF(correctiveAction);
        return new StreamingResolution("application/pdf") {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content.toByteArray());
            }
        }.setFilename(correctiveAction.getCode() + ".pdf");
    }

    @Secure(roles = "user")
    public Resolution deleteAction() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        correctiveActionService.deleteCorrectiveAction(contractId, correctiveActionId, user);
        getContext().getMessages().add(new LocalizableMessage("security.actions.delete.sucess"));
        return new RedirectResolution(SecurityActionsPlanningActionBean.class, "actionsPlanningGrid")
                .addParameter("contractId", contractId)
                .addParameters(getDisplayTagParameters());
    }

    @Secure(roles = "user")
    public Resolution actionsPlanningChat() throws ObjectNotFoundException {
        final boolean isUserAdminOrCertitecna = securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId());
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!isUserAdminOrCertitecna
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        correctiveAction = correctiveActionService.findCorrectiveAction(correctiveActionId);
        setClosed(correctiveAction.getClosed());
        setChatTitle(correctiveAction.getCode());
        setChatGetUrl("/sm/SecurityActionsPlanning.action?getChatMessages=&contractId=" + contractId +
                "&correctiveActionId=" + correctiveActionId);
        setChatPostUrl("/sm/SecurityActionsPlanning.action?addChatMessage=&contractId=" + contractId +
                "&correctiveActionId=" + correctiveActionId);
        setIsUserBasic(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserBasic(getUserInSession().getId(), contractId));
        return new ForwardResolution("/WEB-INF/jsps/sm/securityViewChat.jsp")
                .addParameter("contractId", contractId)
                .addParameter("correctiveActionId", correctiveActionId);
    }

    @Secure(roles = "user")
    public Resolution addChatMessage() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        correctiveActionService.addChatMessage(contractId, correctiveActionId, chatMessage, getUserInSession());
        return getChatMessages();
    }

    @Secure(roles = "user")
    public Resolution getChatMessages() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.APC)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        List<Chat> chats = correctiveActionService.findChatMessages(correctiveActionId);
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
        setTopEvent("actionsPlanningGrid");
    }

    private static String getMessage(Locale locale, String key, Object... arguments) {
        return new MessageFormat(ResourceBundle.getBundle("StripesResources", locale).getString(key), locale)
                .format(arguments);
    }

    public User getUserInSession() {
        return (User) getContext().getRequest().getSession().getAttribute("user");
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

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getCorrectiveActionId() {
        return correctiveActionId;
    }

    public void setCorrectiveActionId(Long correctiveActionId) {
        this.correctiveActionId = correctiveActionId;
    }

    public String getTopEvent() {
        return topEvent;
    }

    public void setTopEvent(String topEvent) {
        this.topEvent = topEvent;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getContentTemplate() {
        return contentTemplate;
    }

    public void setContentTemplate(String contentTemplate) {
        this.contentTemplate = contentTemplate;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public boolean getIsUserExpert() {
        return isUserExpert;
    }

    public void setIsUserExpert(boolean isUserExpert) {
        this.isUserExpert = isUserExpert;
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

    public PaginatedListAdapter getActionsAdapter() {
        return actionsAdapter;
    }

    public void setActionsAdapter(PaginatedListAdapter actionsAdapter) {
        this.actionsAdapter = actionsAdapter;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public CorrectiveAction getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(CorrectiveAction correctiveAction) {
        this.correctiveAction = correctiveAction;
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

    public List<String> getActionYears() {
        return actionYears;
    }

    public void setActionYears(List<String> actionYears) {
        this.actionYears = actionYears;
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

    public Long getAnomalyId() {
        return anomalyId;
    }

    public void setAnomalyId(Long anomalyId) {
        this.anomalyId = anomalyId;
    }

    public String getAnomalyCode() {
        return anomalyCode;
    }

    public void setAnomalyCode(String anomalyCode) {
        this.anomalyCode = anomalyCode;
    }

    public Long getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(Long maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public String getMaintenanceCode() {
        return maintenanceCode;
    }

    public void setMaintenanceCode(String maintenanceCode) {
        this.maintenanceCode = maintenanceCode;
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

    public Long getImpactWorkId() {
        return impactWorkId;
    }

    public void setImpactWorkId(Long impactWorkId) {
        this.impactWorkId = impactWorkId;
    }

    public String getImpactWorkCode() {
        return impactWorkCode;
    }

    public void setImpactWorkCode(String impactWorkCode) {
        this.impactWorkCode = impactWorkCode;
    }

    public boolean getClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean getIsAnother() {
        return isAnother;
    }

    public void setIsAnother(boolean isAnother) {
        this.isAnother = isAnother;
    }

    public SubModuleType getSubModuleType() {
        return SubModuleType.APC;
    }

    public List<User> getUsers() { return users; }

    public void setUsers(List<User> users) { this.users = users; }

    public String getUsersToNotify() {
        return usersToNotify;
    }

    public void setUsersToNotify(String usersToNotify) {
        this.usersToNotify = usersToNotify;
    }

    //this is for the chat because the emergency action module uses a token to give temporary access to the users
    public boolean getIsValidToken() {
        return false;
    }

    @Override
    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertAction")) {
            return actionsPlanningAdd();
        }
        if (getContext().getEventName().equals("editAction")) {
            return actionsPlanningEdit();
        }
        return null;
    }

}
