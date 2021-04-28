package com.criticalsoftware.certitools.presentation.action.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.PDFException;
import com.criticalsoftware.certitools.business.sm.AnomalyService;
import com.criticalsoftware.certitools.business.sm.CorrectiveActionService;
import com.criticalsoftware.certitools.business.sm.SecurityManagementService;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.*;
import com.criticalsoftware.certitools.entities.sm.enums.AnomalyType;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
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

@SuppressWarnings("unused")
public class SecurityAnomaliesRecordActionBean extends DisplayTagSupportActionBean implements ActionBean, ValidationErrorHandler {

    @EJBBean(value = "certitools/SecurityManagementService")
    private SecurityManagementService securityManagementService;

    @EJBBean(value = "certitools/AnomalyService")
    private AnomalyService anomalyService;

    @EJBBean(value = "certitools/CorrectiveActionService")
    private CorrectiveActionService correctiveActionService;

    private String topEvent;
    private String contentTemplate;
    private SecurityMenu securityMenu;

    private Contract contract;
    @ValidateNestedProperties({
            @Validate(field = "whoDetected", required = true, maxlength = 255, on = {"insertAnomaly", "editAnomaly"}),
            @Validate(field = "description", required = true, maxlength = 5000, on = {"insertAnomaly", "editAnomaly", "insertOccurrence", "editOccurrence"}),
            @Validate(field = "internalActors", required = true, maxlength = 255, on = {"insertOccurrence", "editOccurrence"}),
            @Validate(field = "externalActors", required = true, maxlength = 255, on = {"insertOccurrence", "editOccurrence"}),
            @Validate(field = "datetime", required = true, converter = PTDateTypeConverter.class, on = {"insertAnomaly", "editAnomaly", "insertOccurrence", "editOccurrence"}),
            @Validate(field = "qualifiedEntity", required = true, maxlength = 255, on = "editOccurrence")
    })
    private Anomaly anomaly;

    private List<SecurityImpact> securityImpacts;

    private Long contractId, anomalyId, documentId;
    private boolean isUserExpert, isUserIntermediate, isUserBasic;
    private boolean edit;
    private boolean closed;
    private boolean correctiveActions;
    private boolean isAnomaly;
    private List<FileBean> newAttachments;
    private List<String> attachmentName;
    private List<CorrectiveAction> correctiveActionsList;

    // chat
    private String chatTitle, chatPopoutUrl, chatGetUrl, chatPostUrl, chatMessage;

    // grid
    public PaginatedListAdapter anomalyAdapter;
    private List<String> anomalyYears, anomalyStatus;
    private String filterYear;
    private Boolean isOpen;
    private Long correctiveActionId;
    private AnomalyType anomalyType;

    private static final Logger LOGGER = Logger.getInstance(SecurityAnomaliesRecordActionBean.class);

    @Secure(roles = "user")
    public Resolution anomaliesRecordAdd() throws BusinessException {
        setIsAnomaly(true);
        return insertPageCommon();
    }

    @Secure(roles = "user")
    public Resolution occurrencesRecordAdd() throws BusinessException {
        setIsAnomaly(false);
        return insertPageCommon();
    }

    public Resolution insertPageCommon() {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        setContract(securityManagementService.getContract(contractId));
        setEdit(false);
        setClosed(false);
        setIsUserExpert(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setContentTemplate("securityAnomaliesRecordAdd.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp");
    }

    public Resolution insertAnomaly() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {
        LOGGER.info("Insert New Anomaly - User Id: " + getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("security.anomaly.add.success"));

        return insertActionCommon();

    }

    public Resolution insertOccurrence() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {
        LOGGER.info("Insert New Occurrence - User Id: " + getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("security.occurrence.add.success"));

        return insertActionCommon();
    }

    public Resolution insertActionCommon() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        String event;
        if (isAnomaly) {
            anomalyId = anomalyService.createAnomaly(contractId, anomaly, AnomalyType.ANOMALY, getUserInSession());
            event = "anomaliesRecordEdit";
        } else {
            anomalyId = anomalyService.createAnomaly(contractId, anomaly, AnomalyType.OCCURRENCE, getUserInSession());
            event = "occurrencesRecordEdit";
        }
        return new RedirectResolution(SecurityAnomaliesRecordActionBean.class, event)
                .addParameter("contractId", contractId)
                .addParameter("anomalyId", anomalyId);
    }

    @Secure(roles = "user")
    public Resolution anomaliesRecordEdit() throws BusinessException, ObjectNotFoundException {
        LOGGER.info("Editing Anomaly: " + anomalyId + " - User Id: " + getUserInSession().getId());
        setIsAnomaly(true);
        return editPageCommon();
    }

    @Secure(roles = "user")
    public Resolution occurrencesRecordEdit() throws BusinessException, ObjectNotFoundException {
        LOGGER.info("Editing Occurrence: " + anomalyId + " - User Id: " + getUserInSession().getId());
        setIsAnomaly(false);
        return editPageCommon();
    }

    public Resolution editPageCommon() throws ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        setContract(securityManagementService.getContract(contractId));
        anomaly = anomalyService.findAnomaly(anomalyId);
        securityImpacts = securityManagementService.findSecurityImpacts();
        anomaly.setCorrectiveActions(anomalyService.findOpenCorrectiveActions(anomalyId));
        correctiveActionsList = anomaly.getCorrectiveActions();

        if (anomaly.getAnomalyType() == AnomalyType.ANOMALY) {
            setIsAnomaly(true);
        } else {
            setIsAnomaly(false);
        }

        setChatTitle(getAnomaly().getCode());
        setChatPopoutUrl("/sm/SecurityAnomaliesRecord.action?anomaliesRecordChat=&contractId=" + contractId +
                "&anomalyId=" + anomalyId);
        setChatGetUrl("/sm/SecurityAnomaliesRecord.action?getChatMessages=&contractId=" + contractId +
                "&anomalyId=" + anomalyId);
        setChatPostUrl("/sm/SecurityAnomaliesRecord.action?addChatMessage=&contractId=" + contractId +
                "&anomalyId=" + anomalyId);

        setEdit(true);
        setClosed(anomaly.getClosed());
        setCorrectiveActions(false);
        setIsUserExpert(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setIsUserIntermediate(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId));
        setIsUserBasic(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserBasic(getUserInSession().getId(), contractId));
        setContentTemplate("securityAnomaliesRecordAdd.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contractId)
                .addParameter("anomalyId", anomalyId);
    }


    public Resolution editAnomaly() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException, IOException {
        LOGGER.info("Editing Anomaly: " + anomalyId + " - User Id: " + getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("security.anomaly.edit.success"));

        return editActionsCommon();
    }

    public Resolution editOccurrence() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException, IOException {
        LOGGER.info("Editing Occurrence: " + anomalyId + " - User Id: " + getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("security.occurrence.edit.success"));

        return editActionsCommon();
    }

    @ValidationMethod(on = {"editAnomaly", "editOccurrence"})
    public void validateEditAnomaly(ValidationErrors errors) throws Exception {
        if ((anomalyService.findOpenCorrectiveActions(anomalyId).size() > 0) && anomaly.getClosed()) {
            errors.add("closeDate", new LocalizableError("security.anomaly.edit.open.actions"));
        }
    }

    public Resolution editActionsCommon() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException, IOException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        if (securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId)) {
            if (isAnomaly) {
                anomalyService.updateAnomalyMainFields(contractId, anomaly, AnomalyType.ANOMALY, getUserInSession());
            } else {
                anomalyService.updateAnomalyMainFields(contractId, anomaly, AnomalyType.OCCURRENCE, getUserInSession());
            }
        }

        String event;

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
        if (isAnomaly) {
            anomalyService.editAnomaly(contractId, anomaly, AnomalyType.ANOMALY, documents, getUserInSession());
            event = "anomaliesRecordEdit";
        } else {
            anomalyService.editAnomaly(contractId, anomaly, AnomalyType.OCCURRENCE, documents, getUserInSession());
            event = "occurrencesRecordEdit";
        }

        if (correctiveActions) {
            anomaly = anomalyService.findAnomaly(anomalyId);
            return new RedirectResolution(SecurityActionsPlanningActionBean.class, "actionsPlanningAdd")
                    .addParameter("contractId", contractId)
                    .addParameter("anomalyId", anomalyId)
                    .addParameter("event", event)
                    .addParameter("anomalyCode", anomaly.getCode());
        }

        return new RedirectResolution(SecurityAnomaliesRecordActionBean.class, event)
                .addParameter("contractId", contractId)
                .addParameter("anomalyId", anomalyId);
    }

    @Secure(roles = "user")
    public Resolution deleteDocument() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        anomalyService.deleteDocument(contractId, anomalyId, documentId, user);
        String event;
        if (isAnomaly) {
            event = "anomaliesRecordEdit";
        } else {
            event = "occurrencesRecordEdit";
        }
        getContext().getMessages().add(new LocalizableMessage("security.document.delete.success"));
        return new RedirectResolution(SecurityAnomaliesRecordActionBean.class, event)
                .addParameter("contractId", contractId)
                .addParameter("anomalyId", anomalyId);
    }

    @Secure(roles = "user")
    public Resolution reopenAnomaly() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        anomalyService.reopenAnomaly(contractId, anomalyId, getUserInSession());
        String event;
        if (isAnomaly) {
            event = "anomaliesRecordEdit";
        } else {
            event = "occurrencesRecordEdit";
        }
        getContext().getMessages().add(new LocalizableMessage("security.anomaly.reopen.success"));
        return new RedirectResolution(SecurityAnomaliesRecordActionBean.class, event)
                .addParameter("contractId", contractId)
                .addParameter("anomalyId", anomalyId);
    }

    /**
     * *****
     * Grid *
     * ******
     */

    @DefaultHandler
    @Secure(roles = "user")
    public Resolution anomaliesRecordGrid() throws BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        PaginatedListWrapper<Anomaly> wrapper =
                new PaginatedListWrapper<Anomaly>(getPage(),
                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(), getDirOrder(),
                        isExportRequest());

        anomalyAdapter = new PaginatedListAdapter<Anomaly>(
                anomalyService.findAnomaliesByContract(contractId, wrapper, anomalyType, filterYear, isOpen));

        anomalyYears = new ArrayList<String>();
        anomalyYears.add("");
        anomalyYears.addAll(anomalyService.findAnomalyYears(contractId));

        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility.getLocalizedFieldName("security.anomaly.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility.getLocalizedFieldName("security.anomaly.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility.getLocalizedFieldName("security.anomaly.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility.getLocalizedFieldName("security.anomaly.filename.pdf", null, null, locale));

        setContract(securityManagementService.getContract(contractId));
        setIsUserExpert(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setContentTemplate("securityAnomaliesRecordGrid.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp");
    }

    @Secure(roles = "user")
    public Resolution getDocument() {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
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
    public Resolution getChatPdf() throws ObjectNotFoundException, PDFException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        ReportPdf reportPdf = new ReportPdf(getContext());
        Anomaly anomaly = anomalyService.findAnomalyForChatPdf(anomalyId);
        final ByteArrayOutputStream content = reportPdf.generateChatPDF(anomaly);
        return new StreamingResolution("application/pdf") {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content.toByteArray());
            }
        }.setFilename(getMessage(getContext().getLocale(), "security.pdf.filename"));
    }

    @Secure(roles = "user")
    public Resolution getAnomalyReportPdf() throws ObjectNotFoundException, PDFException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        ReportPdf reportPdf = new ReportPdf(getContext());
        Anomaly anomaly = anomalyService.findAnomalyForReportPdf(anomalyId);

        String imagesDir = getContext().getServletContext().getRealPath("/images/") +
                System.getProperty("file.separator"),
                logoPath = imagesDir + "logopdf.png",
                checkedPath = imagesDir + "ic_check_box_black_24dp.png",
                uncheckedPath = imagesDir + "ic_check_box_outline_blank_black_24dp.png";

        List<SecurityImpact> securityImpacts = securityManagementService.findSecurityImpacts();

        final ByteArrayOutputStream content = reportPdf.generateAnomalyPDF(anomaly, securityImpacts);

        return new StreamingResolution("application/pdf") {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content.toByteArray());
            }
        }.setFilename(anomaly.getCode() + ".pdf");
    }

    @Secure(roles = "user")
    public Resolution getCorrectiveActionReportPdf() throws ObjectNotFoundException, PDFException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
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
    public Resolution deleteAnomaly() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        anomalyService.deleteAnomaly(contractId, anomalyId, user);
        getContext().getMessages().add(new LocalizableMessage("security.anomaly.delete.success"));
        return new RedirectResolution(SecurityAnomaliesRecordActionBean.class, "anomaliesRecordGrid")
                .addParameter("contractId", contractId)
                .addParameters(getDisplayTagParameters());
    }

    @Secure(roles = "user")
    public Resolution anomaliesRecordChat() throws ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        anomaly = anomalyService.findAnomaly(anomalyId);
        setChatTitle(anomaly.getCode());
        setChatGetUrl("/sm/SecurityAnomaliesRecord.action?getChatMessages=&contractId=" + contractId +
                "&anomalyId=" + anomalyId);
        setChatPostUrl("/sm/SecurityAnomaliesRecord.action?addChatMessage=&contractId=" + contractId +
                "&anomalyId=" + anomalyId);
        setClosed(anomaly.getClosed());
        setIsUserBasic(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserBasic(getUserInSession().getId(), contractId));
        return new ForwardResolution("/WEB-INF/jsps/sm/securityViewChat.jsp")
                .addParameter("contractId", contractId)
                .addParameter("anomalyId", anomalyId);
    }

    @Secure(roles = "user")
    public Resolution addChatMessage() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        anomalyService.addChatMessage(contractId, anomalyId, chatMessage, getUserInSession());
        return getChatMessages();
    }

    @Secure(roles = "user")
    public Resolution getChatMessages() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.ANOM)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        List<Chat> chats = anomalyService.findChatMessages(anomalyId);
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

    /**
     * ********************
     * Getters And Setters *
     * *********************
     */

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

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getAnomalyId() {
        return anomalyId;
    }

    public void setAnomalyId(Long anomalyId) {
        this.anomalyId = anomalyId;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Anomaly getAnomaly() {
        return anomaly;
    }

    public void setAnomaly(Anomaly anomaly) {
        this.anomaly = anomaly;
    }

    public List<SecurityImpact> getSecurityImpacts() {
        return securityImpacts;
    }

    public void setSecurityImpacts(List<SecurityImpact> securityImpacts) {
        this.securityImpacts = securityImpacts;
    }

    public SecurityMenu getSecurityMenu() {
        return securityMenu;
    }

    public void setSecurityMenu(SecurityMenu securityMenu) {
        this.securityMenu = securityMenu;
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

    public boolean getCorrectiveActions() {
        return correctiveActions;
    }

    public void setCorrectiveActions(boolean correctiveActions) {
        this.correctiveActions = correctiveActions;
    }

    public boolean getEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    private static String getMessage(Locale locale, String key, Object... arguments) {
        return new MessageFormat(ResourceBundle.getBundle("StripesResources", locale).getString(key), locale)
                .format(arguments);
    }

    public PaginatedListAdapter getAnomalyAdapter() {
        return anomalyAdapter;
    }

    public void setAnomalyAdapter(PaginatedListAdapter anomalyAdapter) {
        this.anomalyAdapter = anomalyAdapter;
    }

    public List<String> getAnomalyYears() {
        return anomalyYears;
    }

    public void setAnomalyYears(List<String> anomalyYears) {
        this.anomalyYears = anomalyYears;
    }

    public List<String> getAnomalyStatus() {
        return anomalyStatus;
    }

    public void setAnomalyStatus(List<String> anomalyStatus) {
        this.anomalyStatus = anomalyStatus;
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

    public Long getCorrectiveActionId() {
        return correctiveActionId;
    }

    public void setCorrectiveActionId(Long correctiveActionId) {
        this.correctiveActionId = correctiveActionId;
    }

    public boolean getIsAnomaly() {
        return isAnomaly;
    }

    public void setIsAnomaly(boolean isAnomaly) {
        this.isAnomaly = isAnomaly;
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

    public AnomalyType getAnomalyType() {
        return anomalyType;
    }

    public void setAnomalyType(AnomalyType anomalyType) {
        this.anomalyType = anomalyType;
    }

    public List<CorrectiveAction> getCorrectiveActionsList() {
        return correctiveActionsList;
    }

    public void setCorrectiveActionsList(List<CorrectiveAction> correctiveActionsList) {
        this.correctiveActionsList = correctiveActionsList;
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

    public boolean getClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public SubModuleType getSubModuleType() {
        return SubModuleType.ANOM;
    }

    //this is for the chat because the emergency action module uses a token to give temporary access to the users
    public boolean getIsValidToken() {
        return false;
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
        setTopEvent("anomaliesRecordGrid");
    }

    @Override
    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertAnomaly")) {
            return anomaliesRecordAdd();
        }
        if (getContext().getEventName().equals("insertOccurrence")) {
            return occurrencesRecordAdd();
        }
        if (getContext().getEventName().equals("editAnomaly")) {
            return anomaliesRecordEdit();
        }
        if (getContext().getEventName().equals("editOccurrence")) {
            return occurrencesRecordEdit();
        }
        return null;
    }
}
