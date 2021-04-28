package com.criticalsoftware.certitools.presentation.action.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CSVException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.PDFException;
import com.criticalsoftware.certitools.business.sm.EmergencyActionService;
import com.criticalsoftware.certitools.business.sm.SecurityManagementService;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.EmergencyAction;
import com.criticalsoftware.certitools.entities.sm.EmergencyUser;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.action.certitools.LoginRedirectActionBean;
import com.criticalsoftware.certitools.presentation.util.*;
import com.criticalsoftware.certitools.presentation.util.export.sm.ReportCsv;
import com.criticalsoftware.certitools.presentation.util.export.sm.ReportPdf;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Security Emergency Action Bean
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("unused")
public class SecurityEmergencyActionBean extends DisplayTagSupportActionBean implements ActionBean, ValidationErrorHandler {

    @EJBBean(value = "certitools/SecurityManagementService")
    private SecurityManagementService securityManagementService;

    @EJBBean(value = "certitools/EmergencyActionService")
    private EmergencyActionService emergencyActionService;

    private SecurityMenu securityMenu;

    private Contract contract;
    private Long contractId;
    private boolean isUserExpert, isUserIntermediate, isUserBasic, isValidToken, hasPermissionToClose, hasPermissionToEdit;
    private String topEvent, event;
    private String contentTemplate;

    @ValidateNestedProperties({
            @Validate(field = "origin", required = true, maxlength = 255, on = {"insertEmergencyAction", "editEmergencyAction"}),
            @Validate(field = "description", required = true, maxlength = 5000, on = {"insertEmergencyAction", "editEmergencyAction"}),
            @Validate(field = "startDate", required = true, converter = PTDateTypeConverter.class, on = {"insertEmergencyAction", "editEmergencyAction"})
    })
    private EmergencyAction emergencyAction;
    private Long emergencyId;

    private boolean edit;
    private boolean closed;

    private String token;

    // chat
    private String chatTitle, chatPopoutUrl, chatGetUrl, chatPostUrl, chatMessage;

    // grid
    public PaginatedListAdapter emergencyActionsAdapter;
    private List<String> emergencyYears;
    private String filterYear;

    @ValidateNestedProperties({
            @Validate(field = "name", required = true, maxlength = 255, on = {"insertEmergencyUser"}),
            @Validate(field = "email", required = true, converter = EmailTypeConverter.class, maxlength = 255,
                    on = {"insertEmergencyUser"})
    })
    private EmergencyUser emergencyUser;
    private Long emergencyUserId;
    private List<EmergencyUser> emergencyUsers;


    private static final Logger LOGGER = Logger.getInstance(SecurityEmergencyActionBean.class);

    @DefaultHandler
    @Secure(roles = "user")
    public Resolution emergencyActionGrid() throws BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        PaginatedListWrapper<EmergencyAction> wrapper =
                new PaginatedListWrapper<EmergencyAction>(getPage(),
                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(), getDirOrder(),
                        isExportRequest());

        emergencyActionsAdapter = new PaginatedListAdapter<EmergencyAction>(
                emergencyActionService.findEmergencyActionsByContract(contractId, wrapper, filterYear, null));

        emergencyYears = new ArrayList<String>();
        emergencyYears.add("");
        emergencyYears.addAll(emergencyActionService.findEmergencyActionYears(contractId));

        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility.getLocalizedFieldName("security.emergency.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility.getLocalizedFieldName("security.emergency.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility.getLocalizedFieldName("security.emergency.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility.getLocalizedFieldName("security.emergency.filename.pdf", null, null, locale));

        setContract(securityManagementService.getContract(contractId));
        setIsUserExpert(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setContentTemplate("securityEmergencyActionGrid.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contractId);
    }

    @Secure(roles = "user")
    public Resolution getEmergencyActionReportPdf() throws ObjectNotFoundException, PDFException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        ReportPdf reportPdf = new ReportPdf(getContext());
        EmergencyAction emergencyAction = emergencyActionService.findEmergencyActionForReportPdf(emergencyId);
        List<User> users = securityManagementService.findUsersByContractAndSecurityPermissions(contractId);
        List<EmergencyUser> emergencyUsers = emergencyActionService.findEmergencyUsers(contractId);
        final ByteArrayOutputStream content = reportPdf.generateEmergencyActionPDF(emergencyAction, users, emergencyUsers);
        return new StreamingResolution("application/pdf") {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content.toByteArray());
            }
        }.setFilename(emergencyAction.getCode() + ".pdf");
    }

    @Secure(roles = "user")
    public Resolution getChatCsv() throws ObjectNotFoundException, CSVException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        ReportCsv reportCsv = new ReportCsv(getContext().getLocale());
        EmergencyAction emergencyAction = emergencyActionService.findEmergencyActionForReportPdf(emergencyId);
        final ByteArrayOutputStream content = reportCsv.generateChatCSV(emergencyAction.getChatMessages());
        return new StreamingResolution("text/csv") {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content.toByteArray());
            }
        }.setFilename(getMessage(getContext().getLocale(), "security.csv.filename"));
    }

    @Secure(roles = "user")
    public Resolution deleteEmergencyAction() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        emergencyActionService.deleteEmergencyAction(contractId, emergencyId, user);
        getContext().getMessages().add(new LocalizableMessage("security.emergency.delete.success"));
        return new RedirectResolution(SecurityEmergencyActionBean.class, "emergencyActionGrid")
                .addParameter("contractId", contractId)
                .addParameters(getDisplayTagParameters());
    }

    @Secure(roles = "user")
    public Resolution emergencyActionAdd() throws ObjectNotFoundException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        setContract(securityManagementService.getContract(contractId));
        setEdit(false);
        setClosed(false);

        setIsUserExpert(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserExpert(user.getId(), contractId));
        setContentTemplate("securityEmergencyActionAdd.jsp");

        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contractId);
    }

    public Resolution insertEmergencyAction() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        LOGGER.info("Create Emergency Action - User Id: " + getUserInSession().getId());

        emergencyId = emergencyActionService.createEmergencyAction(contractId, emergencyAction, getUserInSession());

        getContext().getMessages().add(new LocalizableMessage("security.emergency.add.success"));
        return new RedirectResolution(SecurityEmergencyActionBean.class, "emergencyActionEdit")
                .addParameter("contractId", contractId)
                .addParameter("emergencyId", emergencyId);
    }

    public Resolution emergencyActionEdit() throws ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        if (getUserInSession() == null && token == null) {
            return new RedirectResolution(LoginRedirectActionBean.class)
                    .addParameter("securityEmergencyEdit", true)
                    .addParameter("contractId", contractId)
                    .addParameter("emergencyId", emergencyId);
        }
        if (getUserInSession() != null && !securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        if (getUserInSession() == null && !emergencyActionService.isValidToken(emergencyId, token)) {
            return new RedirectResolution(LoginRedirectActionBean.class)
                    .addParameter("securityEmergencyEdit", true)
                    .addParameter("contractId", contractId)
                    .addParameter("emergencyId", emergencyId);
        }

        User user = getUserInSession();
        setContract(securityManagementService.getContract(contractId));
        emergencyAction = emergencyActionService.findEmergencyAction(contractId, emergencyId, user, token);

        setEdit(true);
        setClosed(emergencyAction.getClosed());

        setChatTitle(emergencyAction.getCode());
        setChatPopoutUrl("/sm/SecurityEmergency.action?emergencyActionChat=&contractId=" + contractId +
                "&emergencyId=" + emergencyId + (token != null ? "&token=" + token : ""));
        setChatGetUrl("/sm/SecurityEmergency.action?getChatMessages=&contractId=" + contractId +
                "&emergencyId=" + emergencyId + (token != null ? "&token=" + token : ""));
        setChatPostUrl("/sm/SecurityEmergency.action?addChatMessage=&contractId=" + contractId +
                "&emergencyId=" + emergencyId + (token != null ? "&token=" + token : ""));

        if (user != null) {
            setIsUserExpert(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                    securityManagementService.isUserExpert(user.getId(), contractId));
            setIsUserIntermediate(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                    securityManagementService.isUserIntermediate(user.getId(), contractId));
            setIsUserBasic(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                    securityManagementService.isUserBasic(user.getId(), contractId));
        } else if (token != null) {
            setIsValidToken(emergencyActionService.isValidToken(emergencyId, token));
            setHasPermissionToEdit(emergencyActionService.hasPermissionToEdit(contractId, emergencyId, token));

            // disable menu items
            for (MenuItem menuItem : getMenu().getMenuItems()) {
                menuItem.setDisabled(true);
            }
        }
        setHasPermissionToClose(emergencyActionService.hasPermissionToClose(contractId, emergencyId, user, token));

        setContentTemplate("securityEmergencyActionAdd.jsp");
        getContext().getResponse().setHeader("Stripes-Success", "OK");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contractId)
                .addParameter("emergencyId", emergencyId)
                .addParameter("token", token);
    }

    public Resolution editEmergencyAction() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        if (getUserInSession() == null && token == null) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        if ((getUserInSession() != null && (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) ||
                (getUserInSession() == null && !emergencyActionService.hasPermissionToEdit(contractId, emergencyId, token))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        LOGGER.info("Edit Emergency Action");

        emergencyAction.setId(emergencyId);
        emergencyActionService.updateEmergencyAction(contractId, emergencyAction, getUserInSession(), token);

        getContext().getMessages().add(new LocalizableMessage("security.emergency.edit.success"));
        return new RedirectResolution(SecurityEmergencyActionBean.class, "emergencyActionEdit")
                .addParameter("contractId", contractId)
                .addParameter("emergencyId", emergencyId)
                .addParameter("token", token);
    }

    public Resolution resetEmergencyAction() {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        if (getUserInSession() == null && token == null) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        if ((getUserInSession() != null && (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) ||
                (getUserInSession() == null && !emergencyActionService.hasPermissionToEdit(contractId, emergencyId, token))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        return new RedirectResolution(SecurityEmergencyActionBean.class, "emergencyActionEdit")
                .addParameter("contractId", contractId)
                .addParameter("emergencyId", emergencyId)
                .addParameter("token", token);
    }

    public Resolution closeEmergencyAction() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)
                || !emergencyActionService.hasPermissionToClose(contractId, emergencyId, getUserInSession(), token)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        LOGGER.info("Close Emergency Action");

        emergencyActionService.closeEmergencyAction(contractId, emergencyId, getUserInSession(), token);

        getContext().getMessages().add(new LocalizableMessage("security.emergency.close.success"));
        return new RedirectResolution(SecurityEmergencyActionBean.class, "emergencyActionEdit")
                .addParameter("contractId", contractId)
                .addParameter("emergencyId", emergencyId)
                .addParameter("token", token);
    }

    public Resolution emergencyActionChat() throws ObjectNotFoundException, BusinessException, CertitoolsAuthorizationException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        if (getUserInSession() == null && token == null) {
            return new RedirectResolution(LoginRedirectActionBean.class)
                    .addParameter("securityEmergencyEdit", true)
                    .addParameter("contractId", contractId)
                    .addParameter("emergencyId", emergencyId);
        }
        if (getUserInSession() != null && !securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId)) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        if (getUserInSession() == null && !emergencyActionService.isValidToken(emergencyId, token)) {
            return new RedirectResolution(LoginRedirectActionBean.class)
                    .addParameter("securityEmergencyEdit", true)
                    .addParameter("contractId", contractId)
                    .addParameter("emergencyId", emergencyId);
        }

        User user = getUserInSession();
        emergencyAction = emergencyActionService.findEmergencyAction(contractId, emergencyId, user, token);
        setClosed(emergencyAction.getClosed());
        setChatTitle(emergencyAction.getCode());
        setChatGetUrl("/sm/SecurityEmergency.action?getChatMessages=&contractId=" + contractId +
                "&emergencyId=" + emergencyId + (token != null ? "&token=" + token : ""));
        setChatPostUrl("/sm/SecurityEmergency.action?addChatMessage=&contractId=" + contractId +
                "&emergencyId=" + emergencyId + (token != null ? "&token=" + token : ""));

        if (user != null) {
            setIsUserBasic(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                    securityManagementService.isUserBasic(user.getId(), contractId));
        } else if (token != null) {
            setIsValidToken(emergencyActionService.isValidToken(emergencyId, token));
        }
        return new ForwardResolution("/WEB-INF/jsps/sm/securityViewChat.jsp")
                .addParameter("contractId", contractId)
                .addParameter("emergencyId", emergencyId)
                .addParameter("token", token);
    }

    public Resolution getChatMessages() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        // access is validated in emergencyActionService
        List<Chat> chats = emergencyActionService.findChatMessages(contractId, emergencyId, getUserInSession(), token);
        SimpleDateFormat dateFormat = new SimpleDateFormat(Configuration.getInstance().getDateHourPattern());
        String json = "[";
        for (Chat chat : chats) {
            json += "{ time: '" + dateFormat.format(chat.getDatetime()) + "', user: '"
                    + (chat.getUser() != null ? chat.getUser().getName() : chat.getEmergencyUser().getName())
                    + "', message: '" + chat.getMessage().replace("\n", "<br>") + "'}, ";
        }
        if (chats.size() > 0) {
            json = json.substring(0, json.length() - 2);
        }
        json += "]";
        return new StreamingResolution("text", json);
    }

    public Resolution addChatMessage() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        // access is validated in emergencyActionService
        emergencyActionService.addChatMessage(contractId, emergencyId, chatMessage, getUserInSession(), token);
        return getChatMessages();
    }

    @Secure(roles = "user")
    public Resolution emergencyUsersDefine() {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        setContract(securityManagementService.getContract(contractId));
        setEmergencyUsers(emergencyActionService.findEmergencyUsers(contractId));

        setContentTemplate("securityEmergencyUsersList.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contractId);
    }

    public Resolution insertEmergencyUser() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        LOGGER.info("Create Emergency User - User Id: " + getUserInSession().getId());

        emergencyActionService.createEmergencyUser(contractId, emergencyUser, getUserInSession());

        getContext().getMessages().add(new LocalizableMessage("security.emergency.users.success"));
        return new RedirectResolution(SecurityEmergencyActionBean.class, "emergencyUsersDefine")
                .addParameter("contractId", contractId);
    }

    @Secure(roles = "user")
    public Resolution deleteEmergencyUser() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.EMRG)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        emergencyActionService.deleteEmergencyUser(contractId, emergencyUserId, user);
        getContext().getMessages().add(new LocalizableMessage("security.emergency.users.delete.success"));
        return new RedirectResolution(SecurityEmergencyActionBean.class, "emergencyUsersDefine")
                .addParameter("contractId", contractId);
    }

    @ValidationMethod(on = "insertEmergencyUser", when = ValidationState.NO_ERRORS)
    public void validateInsertEmergencyUser(ValidationErrors errors) throws Exception {
        if (emergencyActionService.existsEmergencyUserByEmailAndContract(contractId, emergencyUser.getEmail())) {
            errors.add("user.email", new LocalizableError("security.emergency.users.duplicateEmail"));
        }
    }

    public Resolution getContractLogoPicture() {
        if (!emergencyActionService.isValidToken(emergencyId, token)) {
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

    @After(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        if (contractId != null && getUserInSession() != null) {
            securityMenu = new SecurityMenu(
                    securityManagementService.countOpenItems(contractId),
                    securityManagementService.findContractSubModules(contractId),
                    securityManagementService.isUserBasic(getUserInSession().getId(), contractId),
                    securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId),
                    securityManagementService.isUserExpert(getUserInSession().getId(), contractId),
                    securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()));
        } else if (contractId != null) {
            securityMenu = new SecurityMenu(new int[]{0, 0, 0, 0, 0, 0}, new ArrayList<SubModuleType>(),
                    false, false, false, false);
        }
        getMenu().select(MenuItem.Item.MENU_SECURITY, null);
        setTopEvent("emergencyActionGrid");
    }

    @Override
    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertEmergencyAction")) {
            return emergencyActionAdd();
        }
        if (getContext().getEventName().equals("insertEmergencyUser")) {
            return emergencyUsersDefine();
        }
        if (getContext().getEventName().equals("editEmergencyAction")) {
            return emergencyActionEdit();
        }
        return null;
    }

    public User getUserInSession() {
        return (User) getContext().getRequest().getSession().getAttribute("user");
    }

    public Menu getMenu() {
        return (Menu) getContext().getRequest().getSession().getAttribute("menu");
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
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

    public EmergencyAction getEmergencyAction() {
        return emergencyAction;
    }

    public void setEmergencyAction(EmergencyAction emergencyAction) {
        this.emergencyAction = emergencyAction;
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

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public PaginatedListAdapter getEmergencyActionsAdapter() {
        return emergencyActionsAdapter;
    }

    public void setEmergencyActionsAdapter(PaginatedListAdapter emergencyActionsAdapter) {
        this.emergencyActionsAdapter = emergencyActionsAdapter;
    }

    public List<String> getEmergencyYears() {
        return emergencyYears;
    }

    public void setEmergencyYears(List<String> emergencyYears) {
        this.emergencyYears = emergencyYears;
    }

    public String getFilterYear() {
        return filterYear;
    }

    public void setFilterYear(String filterYear) {
        this.filterYear = filterYear;
    }

    public Long getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(Long emergencyId) {
        this.emergencyId = emergencyId;
    }

    public SecurityMenu getSecurityMenu() {
        return securityMenu;
    }

    public void setSecurityMenu(SecurityMenu securityMenu) {
        this.securityMenu = securityMenu;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean getIsValidToken() {
        return isValidToken;
    }

    public void setIsValidToken(boolean isValidToken) {
        this.isValidToken = isValidToken;
    }

    public boolean getHasPermissionToClose() {
        return hasPermissionToClose;
    }

    public void setHasPermissionToClose(boolean hasPermissionToClose) {
        this.hasPermissionToClose = hasPermissionToClose;
    }

    public boolean getHasPermissionToEdit() {
        return hasPermissionToEdit;
    }

    public void setHasPermissionToEdit(boolean hasPermissionToEdit) {
        this.hasPermissionToEdit = hasPermissionToEdit;
    }

    public EmergencyUser getEmergencyUser() {
        return emergencyUser;
    }

    public void setEmergencyUser(EmergencyUser emergencyUser) {
        this.emergencyUser = emergencyUser;
    }

    public List<EmergencyUser> getEmergencyUsers() {
        return emergencyUsers;
    }

    public void setEmergencyUsers(List<EmergencyUser> emergencyUsers) {
        this.emergencyUsers = emergencyUsers;
    }

    public Long getEmergencyUserId() {
        return emergencyUserId;
    }

    public void setEmergencyUserId(Long emergencyUserId) {
        this.emergencyUserId = emergencyUserId;
    }

    public SubModuleType getSubModuleType() {
        return SubModuleType.EMRG;
    }

    private static String getMessage(Locale locale, String key, Object... arguments) {
        return new MessageFormat(ResourceBundle.getBundle("StripesResources", locale).getString(key), locale)
                .format(arguments);
    }
}
