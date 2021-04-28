package com.criticalsoftware.certitools.presentation.action.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.PDFException;
import com.criticalsoftware.certitools.business.sm.CorrectiveActionService;
import com.criticalsoftware.certitools.business.sm.SecurityManagementService;
import com.criticalsoftware.certitools.business.sm.WorkService;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.*;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.entities.sm.enums.WorkType;
import com.criticalsoftware.certitools.persistence.sm.dto.DocumentDTO;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PTDateTypeConverter;
import com.criticalsoftware.certitools.presentation.util.PaginatedListAdapter;
import com.criticalsoftware.certitools.presentation.util.SecurityMenu;
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
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * SecurityImpactWorkActionBean
 *
 * @author embarros
 * @version $Revision: $
 */@SuppressWarnings("unused")
public class SecurityImpactWorkActionBean extends DisplayTagSupportActionBean implements ActionBean, ValidationErrorHandler {

    @EJBBean(value = "certitools/SecurityManagementService")
    private SecurityManagementService securityManagementService;

    @EJBBean(value = "certitools/WorkService")
    private WorkService workService;

    @EJBBean(value = "certitools/CorrectiveActionService")
    private CorrectiveActionService correctiveActionService;

    private String topEvent;
    private String contentTemplate;
    private SecurityMenu securityMenu;

    private Contract contract;
    private List<Risk> risks;
    private String risksId;

    private List<CorrectiveAction> correctiveActionsList;
    private List<SecurityImpact> securityImpacts;

    @ValidateNestedProperties({
            @Validate(field = "startDate", required = true, converter = PTDateTypeConverter.class, on = {"insertModification", "editModification", "insertAuthorization"}),
            @Validate(field = "responsible", required = true, maxlength = 255, on = {"insertModification", "editModification", "insertAuthorization"}),
            @Validate(field = "description", required = true, maxlength = 5000, on = {"insertModification", "editModification", "insertAuthorization"}),
            @Validate(field = "duration", required = true, maxlength = 255, on = {"insertAuthorization", "editAuthorization"}),
            @Validate(field = "qualifiedEntity", required = true, maxlength = 255, on = "editModification")
    })
    private SecurityImpactWork securityImpactWork;

    private Long contractId, impactWorkId, documentId, correctiveActionId;
    private boolean isUserExpert, isUserIntermediate, isUserBasic;
    private boolean isModification, edit, correctiveActions;
    private List<FileBean> newAttachments;
    private List<String> attachmentName;
    private boolean closed;

    // chat
    private String chatTitle, chatPopoutUrl, chatGetUrl, chatPostUrl, chatMessage;

    // grid
    public PaginatedListAdapter impactWorkAdapter;
    private List<String> impactWorkYears, impactWorkStatus;
    private String filterYear;
    private Boolean isOpen;
    private WorkType workType;


    private static final Logger LOGGER = Logger.getInstance(SecurityImpactWorkActionBean.class);

    /**
     * ******
     * Insert new
     */
    @Secure(roles = "user")
    public Resolution modificationsChangesAdd() throws BusinessException {
        setIsModification(true);
        return insertPageCommon();
    }

    @Secure(roles = "user")
    public Resolution authorizationAdd() throws BusinessException {
        setIsModification(false);
        return insertPageCommon();
    }

    public Resolution insertPageCommon() {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        if (!getIsModification()) {
            risks = workService.findRisks();
        }

        setContract(securityManagementService.getContract(contractId));
        setEdit(false);
        setClosed(false);
        setIsUserExpert(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setContentTemplate("securityImpactWorkAdd.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp");
    }

    public Resolution insertModification() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {
        LOGGER.info("Insert New  - User Id: " + getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("security.impact.work.modification.add.success"));
        setIsModification(true);
        return insertActionCommon();

    }

    public Resolution insertAuthorization() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {
        LOGGER.info("Insert New Authorization - User Id: " + getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("security.impact.work.authorization.add.success"));
        setIsModification(false);
        return insertActionCommon();
    }

    public Resolution insertActionCommon() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        String event;
        if (getIsModification()) {
            impactWorkId = workService.createSecurityImpactWork(contractId, securityImpactWork, WorkType.MODIFICATION, getUserInSession());
            event = "modificationsChangesEdit";
        } else {
            securityImpactWork.setRisks(getSelectedRisks());
            impactWorkId = workService.createSecurityImpactWork(contractId, securityImpactWork, WorkType.WORK_AUTHORIZATION, getUserInSession());
            event = "authorizationEdit";
        }
        return new RedirectResolution(SecurityImpactWorkActionBean.class, event)
                .addParameter("contractId", contractId)
                .addParameter("impactWorkId", impactWorkId);
    }

    /**
     * Get the selected risks.
     *
     * @return the list of risks.
     */
    private List<Risk> getSelectedRisks() {
        if (risksId == null)
            return new ArrayList<Risk>();
        if (risks == null)
            risks = workService.findRisks();
        // Get the risk list to notify
        List<Risk> risksSelected = new ArrayList<Risk>();
        final String[] ids = this.risksId.split(",");
        for (String s : ids) {
            if (s.trim().equals("")) continue;
            for (Risk r : risks) {
                if (r.getId().equals(new Long(s.trim()))) {
                    risksSelected.add(r);
                    break;
                }
            }
        }
        return risksSelected;
    }


    /**
     * ******
     * Edit
     */
    @Secure(roles = "user")
    public Resolution modificationsChangesEdit() throws BusinessException, ObjectNotFoundException {
        LOGGER.info("Editing Modification: " + impactWorkId + " - User Id: " + getUserInSession().getId());
        setIsModification(true);
        return editPageCommon();
    }

    @Secure(roles = "user")
    public Resolution authorizationEdit() throws BusinessException, ObjectNotFoundException {
        LOGGER.info("Editing Authorization: " + impactWorkId + " - User Id: " + getUserInSession().getId());
        setIsModification(false);
        return editPageCommon();
    }

    public Resolution editPageCommon() throws ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }


        setEdit(true);
        setContract(securityManagementService.getContract(contractId));
        securityImpactWork = workService.findSecurityImpactWork(impactWorkId);
        securityImpactWork.setCorrectiveActions(workService.findOpenCorrectiveActions(impactWorkId));
        correctiveActionsList = securityImpactWork.getCorrectiveActions();
        securityImpacts = securityManagementService.findSecurityImpacts();


        if (securityImpactWork.getWorkType() == WorkType.MODIFICATION) {
            securityImpactWork.setRisks(null);
            setIsModification(true);
        } else {
            risks = workService.findRisks();
            if (securityImpactWork.getRisks().isEmpty()) {
                securityImpactWork.setRisks(null);
            }
            setIsModification(false);
        }

        setChatTitle(securityImpactWork.getCode());
        setChatPopoutUrl("/sm/SecurityImpactWork.action?modificationsChangesChat=&contractId=" + contractId +
                "&impactWorkId=" + impactWorkId);
        setChatGetUrl("/sm/SecurityImpactWork.action?getChatMessages=&contractId=" + contractId +
                "&impactWorkId=" + impactWorkId);
        setChatPostUrl("/sm/SecurityImpactWork.action?addChatMessage=&contractId=" + contractId +
                "&impactWorkId=" + impactWorkId);

        setEdit(true);
        setClosed(securityImpactWork.getClosed());
        setCorrectiveActions(false);
        setIsUserExpert(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setIsUserIntermediate(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId));
        setIsUserBasic(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserBasic(getUserInSession().getId(), contractId));
        setContentTemplate("securityImpactWorkAdd.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contractId)
                .addParameter("impactWorkId", impactWorkId);
    }


    public Resolution editModification() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException, IOException {
        LOGGER.info("Editing Modification: " + impactWorkId + "  - User Id: " + getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("security.impact.work.modification.edit.success"));
        setIsModification(true);
        return editWorkCommon();

    }

    public Resolution editAuthorization() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException, IOException {
        LOGGER.info("Editing Authorization: " + impactWorkId + " - User Id: " + getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("security.impact.work.authorization.edit.success"));
        setIsModification(false);
        return editWorkCommon();
    }

    public Resolution editWorkCommon() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException, IOException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        if (securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                || securityManagementService.isUserExpert(getUserInSession().getId(), contractId)) {
            if (isModification) {
                workService.updateSecurityImpactWorkMainFields(contractId, securityImpactWork, WorkType.MODIFICATION, getUserInSession());
            } else {
                securityImpactWork.setRisks(getSelectedRisks());
                workService.updateSecurityImpactWorkMainFields(contractId, securityImpactWork, WorkType.WORK_AUTHORIZATION, getUserInSession());
            }
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

        String event;
        if (isModification) {
            workService.editSecurityImpactWork(contractId, securityImpactWork, WorkType.MODIFICATION, documents, getUserInSession());
            event = "modificationsChangesEdit";
        } else {
            workService.editSecurityImpactWork(contractId, securityImpactWork, WorkType.WORK_AUTHORIZATION, documents, getUserInSession());
            event = "authorizationEdit";
        }

        if (correctiveActions) {
            securityImpactWork = workService.findSecurityImpactWork(impactWorkId);
            return new RedirectResolution(SecurityActionsPlanningActionBean.class, "actionsPlanningAdd")
                    .addParameter("contractId", contractId)
                    .addParameter("impactWorkId", impactWorkId)
                    .addParameter("event", event)
                    .addParameter("impactWorkCode", securityImpactWork.getCode());
        }

        return new RedirectResolution(SecurityImpactWorkActionBean.class, event)
                .addParameter("contractId", contractId)
                .addParameter("impactWorkId", impactWorkId);
    }


    @Secure(roles = "user")
    public Resolution deleteDocument() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        workService.deleteDocument(contractId, impactWorkId, documentId, user);
        String event;
        if (isModification) {
            event = "modificationsChangesEdit";
        } else {
            event = "authorizationEdit";
        }
        getContext().getMessages().add(new LocalizableMessage("security.document.delete.success"));
        return new RedirectResolution(SecurityImpactWorkActionBean.class, event)
                .addParameter("contractId", contractId)
                .addParameter("impactWorkId", impactWorkId);
    }

    @Secure(roles = "user")
    public Resolution getDocument() {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
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
    public Resolution reopenWork() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        String event;
        if (isModification) {
            event = "modificationsChangesEdit";
        } else {
            event = "authorizationEdit";
        }
        workService.reopenSecurityImpactWork(contractId, impactWorkId, getUserInSession());
        getContext().getMessages().add(new LocalizableMessage("security.impact.work.reopen.success"));
        return new RedirectResolution(SecurityImpactWorkActionBean.class, event)
                .addParameter("contractId", contractId)
                .addParameter("impactWorkId", impactWorkId);
    }

    @Secure(roles = "user")
    public Resolution deleteWork() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        workService.deleteSecurityImpactWork(contractId, impactWorkId, user);
        getContext().getMessages().add(new LocalizableMessage("security.impact.work.delete.success"));
        return new RedirectResolution(SecurityImpactWorkActionBean.class, "impactWorkGrid")
                .addParameter("contractId", contractId)
                .addParameters(getDisplayTagParameters());
    }

    @ValidationMethod(on = {"editModification", "editAuthorization"})
    public void validateEditAnomaly(ValidationErrors errors) throws Exception {
        if ((workService.findOpenCorrectiveActions(impactWorkId).size() > 0) && securityImpactWork.getClosedDate() != null) {
            errors.add("closeDate", new LocalizableError("security.impact.work.edit.open.actions"));
        }
    }

    /**
     * *****
     * Grid *
     * ******
     */

    @DefaultHandler
    @Secure(roles = "user")
    public Resolution impactWorkGrid() throws BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        PaginatedListWrapper<SecurityImpactWork> wrapper =
                new PaginatedListWrapper<SecurityImpactWork>(getPage(),
                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(), getDirOrder(),
                        isExportRequest());

        impactWorkAdapter = new PaginatedListAdapter<SecurityImpactWork>(
                workService.findSecurityImpactWorksByContract(contractId, wrapper, workType, filterYear, isOpen));

        impactWorkYears = new ArrayList<String>();
        impactWorkYears.add("");
        impactWorkYears.addAll(workService.findSecurityImpactWorkYears(contractId));

        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility.getLocalizedFieldName("security.work.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility.getLocalizedFieldName("security.work.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility.getLocalizedFieldName("security.work.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility.getLocalizedFieldName("security.work.filename.pdf", null, null, locale));

        setContract(securityManagementService.getContract(contractId));
        setIsUserExpert(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setContentTemplate("securityImpactWorkGrid.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp");
    }

    @Secure(roles = "user")
    public Resolution modificationsChangesChat() throws ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        securityImpactWork = workService.findSecurityImpactWork(impactWorkId);

        setChatTitle(securityImpactWork.getCode());
        setChatGetUrl("/sm/SecurityImpactWork.action?getChatMessages=&contractId=" + contractId +
                "&impactWorkId=" + impactWorkId);
        setChatPostUrl("/sm/SecurityImpactWork.action?addChatMessage=&contractId=" + contractId +
                "&impactWorkId=" + impactWorkId);

        setEdit(true);
        setClosed(securityImpactWork.getClosed());
        setIsUserBasic(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserBasic(getUserInSession().getId(), contractId));
        return new ForwardResolution("/WEB-INF/jsps/sm/securityViewChat.jsp")
                .addParameter("contractId", contractId)
                .addParameter("impactWorkId", impactWorkId);
    }

    @Secure(roles = "user")
    public Resolution addChatMessage() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        workService.addChatMessage(contractId, impactWorkId, chatMessage, getUserInSession());
        return getChatMessages();
    }

    @Secure(roles = "user")
    public Resolution getChatMessages() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        List<Chat> chats = workService.findChatMessages(impactWorkId);
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
    public Resolution getChatPdf() throws ObjectNotFoundException, PDFException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        ReportPdf reportPdf = new ReportPdf(getContext());
        SecurityImpactWork impactWorkForChatPdf = workService.findSecurityImpactWorkForChatPdf(impactWorkId);
        final ByteArrayOutputStream content = reportPdf.generateChatPDF(impactWorkForChatPdf);
        return new StreamingResolution("application/pdf") {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content.toByteArray());
            }
        }.setFilename(getMessage(getContext().getLocale(), "security.pdf.filename"));
    }

    @Secure(roles = "user")
    public Resolution getImpactWorkReportPdf() throws ObjectNotFoundException, PDFException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        ReportPdf reportPdf = new ReportPdf(getContext());
        SecurityImpactWork impactWork = workService.findSecurityImpactWorkForReportPdf(impactWorkId);
        final ByteArrayOutputStream content = reportPdf.generateSecurityImpactWorkPDF(impactWork,
                securityManagementService.findSecurityImpacts(), workService.findRisks());
        return new StreamingResolution("application/pdf") {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content.toByteArray());
            }
        }.setFilename(impactWork.getCode() + ".pdf");
    }


    @Secure(roles = "user")
    public Resolution getCorrectiveActionReportPdf() throws ObjectNotFoundException, PDFException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.SIW)
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

    public String getTopEvent() {
        return topEvent;
    }

    public void setTopEvent(String topEvent) {
        this.topEvent = topEvent;
    }

    public String getContentTemplate() {
        return contentTemplate;
    }

    public void setContentTemplate(String contentTemplate) {
        this.contentTemplate = contentTemplate;
    }

    public SecurityMenu getSecurityMenu() {
        return securityMenu;
    }

    public void setSecurityMenu(SecurityMenu securityMenu) {
        this.securityMenu = securityMenu;
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

    public boolean getIsUserIntermediate() {
        return isUserIntermediate;
    }

    public void setIsUserIntermediate(boolean isUserIntermediate) {
        this.isUserIntermediate = isUserIntermediate;
    }

    public boolean getIsUserBasic() {
        return isUserBasic;
    }

    public void setIsUserBasic(boolean isUserBasic) {
        this.isUserBasic = isUserBasic;
    }

    public Long getImpactWorkId() {
        return impactWorkId;
    }

    public void setImpactWorkId(Long impactWorkId) {
        this.impactWorkId = impactWorkId;
    }

    public boolean getIsModification() {
        return isModification;
    }

    public void setIsModification(boolean isModification) {
        this.isModification = isModification;
    }

    public boolean getEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public SecurityImpactWork getSecurityImpactWork() {
        return securityImpactWork;
    }

    public void setSecurityImpactWork(SecurityImpactWork securityImpactWork) {
        this.securityImpactWork = securityImpactWork;
    }

    public List<Risk> getRisks() {
        return risks;
    }

    public void setRisks(List<Risk> risks) {
        this.risks = risks;
    }

    public String getRisksId() {
        return risksId;
    }

    public void setRisksId(String risksId) {
        this.risksId = risksId;
    }

    public boolean getCorrectiveActions() {
        return correctiveActions;
    }

    public void setCorrectiveActions(boolean correctiveActions) {
        this.correctiveActions = correctiveActions;
    }

    public List<CorrectiveAction> getCorrectiveActionsList() {
        return correctiveActionsList;
    }

    public void setCorrectiveActionsList(List<CorrectiveAction> correctiveActionsList) {
        this.correctiveActionsList = correctiveActionsList;
    }

    public List<SecurityImpact> getSecurityImpacts() {
        return securityImpacts;
    }

    public void setSecurityImpacts(List<SecurityImpact> securityImpacts) {
        this.securityImpacts = securityImpacts;
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

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
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

    public PaginatedListAdapter getImpactWorkAdapter() {
        return impactWorkAdapter;
    }

    public void setImpactWorkAdapter(PaginatedListAdapter impactWorkAdapter) {
        this.impactWorkAdapter = impactWorkAdapter;
    }

    public List<String> getImpactWorkYears() {
        return impactWorkYears;
    }

    public void setImpactWorkYears(List<String> impactWorkYears) {
        this.impactWorkYears = impactWorkYears;
    }

    public List<String> getImpactWorkStatus() {
        return impactWorkStatus;
    }

    public void setImpactWorkStatus(List<String> impactWorkStatus) {
        this.impactWorkStatus = impactWorkStatus;
    }

    public String getFilterYear() {
        return filterYear;
    }

    public void setFilterYear(String filterYear) {
        this.filterYear = filterYear;
    }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Long getCorrectiveActionId() {
        return correctiveActionId;
    }

    public void setCorrectiveActionId(Long correctiveActionId) {
        this.correctiveActionId = correctiveActionId;
    }

    public boolean getClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    //this is for the chat because the emergency action module uses a token to give temporary access to the users
    public boolean getIsValidToken() {
        return false;
    }

    public SubModuleType getSubModuleType() {
        return SubModuleType.SIW;
    }

    private static String getMessage(Locale locale, String key, Object... arguments) {
        return new MessageFormat(ResourceBundle.getBundle("StripesResources", locale).getString(key), locale)
                .format(arguments);
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
        setTopEvent("impactWorkGrid");
    }

    @Override
    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertModification")) {
            return modificationsChangesAdd();
        }
        if (getContext().getEventName().equals("insertAuthorization")) {
            return authorizationAdd();
        }
        if (getContext().getEventName().equals("editModification")) {
            return modificationsChangesEdit();
        }
        if (getContext().getEventName().equals("editAuthorization")) {
            return authorizationEdit();
        }
        return null;
    }

}
