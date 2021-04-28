package com.criticalsoftware.certitools.presentation.action.sm;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.PDFException;
import com.criticalsoftware.certitools.business.sm.MaintenanceService;
import com.criticalsoftware.certitools.business.sm.RecurrenceService;
import com.criticalsoftware.certitools.business.sm.SecurityManagementService;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.*;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.persistence.sm.dto.DocumentDTO;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.*;
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
public class SecurityMaintenanceActionBean extends DisplayTagSupportActionBean implements ActionBean, ValidationErrorHandler {
    @EJBBean(value = "certitools/SecurityManagementService")
    private SecurityManagementService securityManagementService;

    @EJBBean(value = "certitools/MaintenanceService")
    private MaintenanceService maintenanceService;

    @EJBBean(value = "certitools/RecurrenceService")
    private RecurrenceService recurrenceService;

    private ActionBeanContext actionBeanContext;

    private SecurityMenu securityMenu;

    private Contract contract;
    private Long contractId, documentId, correctiveActionId;
    private Long recurrenceTypeId;
    private boolean isUserExpert, isUserIntermediate, isUserBasic;
    private String topEvent, event;
    private String contentTemplate;
    private String usersToNotify;
    private Integer warningDays;
    private RecurrenceType recurrenceType;
    private MaintenanceType maintenanceType;
    private String otherType, otherEquipment;
    private FileBean newEquipmentDoc;

    private boolean edit, correctiveActions, closed;

    private List<FileBean> newAttachments;
    private List<String> attachmentName;
    private List<CorrectiveAction> correctiveActionsList;

    @ValidateNestedProperties({
            @Validate(field = "maintenanceType.id", required = true, maxlength = 128, on = {"insertMaintenance", "editMaintenance"}),
            @Validate(field = "equipment.id", required = true, maxlength = 128, on = {"insertMaintenance", "editMaintenance"}),
            @Validate(field = "description", required = true, maxlength = 5000, on = {"insertMaintenance", "editMaintenance"}),
            @Validate(field = "designation", required = true, maxlength = 255, on = {"insertMaintenance", "editMaintenance"}),
            @Validate(field = "internalResponsible", maxlength = 255, required = true, on = {"insertMaintenance", "editMaintenance"}),
            @Validate(field = "dateScheduled", required = true, converter = PTDateTypeConverter.class, on = {"insertMaintenance", "editMaintenance"})
    })
    private Maintenance maintenance;
    private List<RecurrenceType> recurrenceTypes;
    private List<User> users;

    // chat
    private String chatTitle, chatPopoutUrl, chatGetUrl, chatPostUrl, chatMessage;

    // grid
    public PaginatedListAdapter maintenanceAdapter;
    private List<String> maintenanceYears, maintenanceStatus;
    private String filterYear;
    private Boolean isOpen;
    private Long maintenanceId, maintenanceTypeId;
    private List<MaintenanceType> maintenanceTypes;

    // equipments list
    private List<Equipment> equipments;
    private Equipment equipment;
    private Long equipmentId;

    @Validate(required = true, maxlength = 128, on = {"insertEquipment"})
    private String name;
    private FileBean document;

    private static final Logger LOGGER = Logger.getInstance(SecurityMaintenanceActionBean.class);

    @DefaultHandler
    @Secure(roles = "user")
    public Resolution maintenanceGrid() throws BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        PaginatedListWrapper<Maintenance> wrapper =
                new PaginatedListWrapper<Maintenance>(getPage(),
                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(), getDirOrder(),
                        isExportRequest());

        maintenanceAdapter = new PaginatedListAdapter<Maintenance>(
                maintenanceService.findMaintenancesByContract(contractId, wrapper, maintenanceTypeId, filterYear, isOpen));

        maintenanceTypes = new ArrayList<MaintenanceType>();
        maintenanceTypes.add(new MaintenanceType());
        maintenanceTypes.addAll(maintenanceService.findMaintenanceTypes(contractId));

        maintenanceYears = new ArrayList<String>();
        maintenanceYears.add("");
        maintenanceYears.addAll(maintenanceService.findMaintenanceYears(contractId));

        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility.getLocalizedFieldName("security.maintenance.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility.getLocalizedFieldName("security.maintenance.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility.getLocalizedFieldName("security.maintenance.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility.getLocalizedFieldName("security.maintenance.filename.pdf", null, null, locale));

        setContract(securityManagementService.getContract(contractId));
        setIsUserExpert(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
        setContentTemplate("securityMaintenanceGrid.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp");
    }

    @Secure(roles = "user")
    public Resolution getEquipmentDocument() {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        Document document = maintenanceService.findEquipmentDocument(equipmentId);
        final byte[] content = document.getContent();
        return new StreamingResolution(document.getContentType()) {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content);
            }
        }.setFilename(document.getName());
    }

    @Secure(roles = "user")
    public Resolution maintenanceAdd() throws ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        setContract(securityManagementService.getContract(contractId));
        users = securityManagementService.findBasicUsersByContract(contractId);
        loadMaitenanceTypes();
        loadRecurrenceTypes();
        loadEquipments();

        setEdit(false);
        setUserPermissionType();
        setContentTemplate("securityMaintenanceAdd.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contract.getId());
    }

    @ValidationMethod(on = "insertMaintenance")
    public void validateInsertMaintenance(ValidationErrors errors) throws Exception {
        if (maintenance.getMaintenanceType().getId() == -1 && otherType == null) {
            errors.add("otherType", new LocalizableError("security.maintenance.type.other.name"));
        }
    }

    public Resolution insertMaintenance() throws BusinessException, CertitoolsAuthorizationException,
            ObjectNotFoundException, IOException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        LOGGER.info("Insert New Maintenance - User Id: " + getUserInSession().getId());
        getContext().getMessages().add(new LocalizableMessage("security.maintenance.add.success"));

        loadMaitenanceTypes();
        loadRecurrenceTypes();
        loadEquipments();

        // Get the Selected Recurrence Type
        Long recurrenceTypeId = maintenance.getRecurrence().getRecurrenceType().getId();
        if (recurrenceTypeId.compareTo(new Long("-1")) == 0) {
            recurrenceTypeId = null;
        }

        getSelectedMaintenanceType();
        getSelectedEquipmentType();

        maintenanceId = maintenanceService.createMaintenance(contractId, maintenance, recurrenceTypeId,
                maintenance.getRecurrence().getWarningDays(), getUsersToBeNotified(), getUserInSession());

        return new RedirectResolution(SecurityMaintenanceActionBean.class, "maintenanceEdit")
                .addParameter("contractId", contractId)
                .addParameter("maintenanceId", maintenanceId);
    }

    /**
     * Get the selected users list to be notified.
     *
     * @return the list of users to be notified.
     */
    private List<User> getUsersToBeNotified() {
        if (usersToNotify == null)
            return new ArrayList<User>();
        users = securityManagementService.findBasicUsersByContract(contractId);
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

    /**
     * Get the selected maintenance type.
     * If the other is selected, create a new type and set it to the maintenance.
     *
     * @throws CertitoolsAuthorizationException
     */
    private void getSelectedMaintenanceType() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        // Get the Selected maintenance Type
        Long maintenanceTypeId = maintenance.getMaintenanceType().getId();
        if (maintenanceTypeId.compareTo(new Long("-1")) == 0) {
            LOGGER.info("Maintenance Type Other - Creating a new maintenance Type '" + otherType + "' to contract " + contractId);
            maintenanceType = maintenanceService.createMaintenanceType(contractId, otherType, getUserInSession());
            maintenance.setMaintenanceType(maintenanceType);
        } else {
            for (MaintenanceType mt : maintenanceTypes) {
                if (mt.getId().compareTo(maintenanceTypeId) == 0) {
                    maintenance.setMaintenanceType(mt);
                }
            }
        }
    }

    /**
     * Get the selected equipment.
     * If the other is selected, create a new equipment and set it to the maintenance.
     *
     * @throws CertitoolsAuthorizationException
     */
    private void getSelectedEquipmentType() throws CertitoolsAuthorizationException, ObjectNotFoundException, IOException {
        // Get the Selected Maintenance Type
        Long equipmentId = maintenance.getEquipment().getId();
        if (equipmentId.compareTo(new Long("-1")) == 0) {
            LOGGER.info("Equipment Other - Creating a new equipment Type '" + otherEquipment + "' to contract " + contractId);
            DocumentDTO doc = new DocumentDTO(newEquipmentDoc.getFileName(), otherEquipment, newEquipmentDoc.getContentType(), newEquipmentDoc.getInputStream());
            equipment = maintenanceService.createEquipment(contractId, otherEquipment, getUserInSession(), doc);
            maintenance.setEquipment(equipment);
        } else {
            for (Equipment eq : equipments) {
                if (eq.getId().compareTo(equipmentId) == 0) {
                    maintenance.setEquipment(eq);
                }
            }
        }
    }

    @Secure(roles = "user")
    public Resolution maintenanceEdit() throws ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        setContract(securityManagementService.getContract(contractId));
        users = securityManagementService.findBasicUsersByContract(contractId);
        loadMaitenanceTypes();
        loadRecurrenceTypes();
        loadEquipments();

        maintenance = maintenanceService.findMaintenance(maintenanceId);
        correctiveActionsList = maintenanceService.findOpenCorrectiveActions(maintenanceId);

        setChatTitle(maintenance.getCode());
        setChatPopoutUrl("/sm/SecurityMaintenance.action?maintenanceChat=&contractId=" + contractId +
                "&maintenanceId=" + maintenanceId);
        setChatGetUrl("/sm/SecurityMaintenance.action?getChatMessages=&contractId=" + contractId +
                "&maintenanceId=" + maintenanceId);
        setChatPostUrl("/sm/SecurityMaintenance.action?addChatMessage=&contractId=" + contractId +
                "&maintenanceId=" + maintenanceId);

        setClosed(maintenance.getClosed());
        setEdit(true);
        setUserPermissionType();
        setContentTemplate("securityMaintenanceAdd.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contract.getId());
    }


    public Resolution editMaintenance() throws BusinessException, CertitoolsAuthorizationException, ObjectNotFoundException, IOException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        LOGGER.info("Editing Maintenance " + maintenanceId + " -  contract " + contractId + " - User: " + getUserInSession().getId());
        maintenance.setId(maintenanceId);

        if (securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId)) {
            // Get the Selected Recurrence Type
            Long recurrenceTypeId = maintenance.getRecurrence().getRecurrenceType().getId();
            if (recurrenceTypeId.compareTo(new Long("-1")) == 0) {
                recurrenceTypeId = null;
            }

            loadRecurrenceTypes();
            loadMaitenanceTypes();
            loadEquipments();

            getSelectedMaintenanceType();
            getSelectedEquipmentType();

            maintenanceService.updateMaintenanceMainFields(contractId, maintenance, recurrenceTypeId,
                    maintenance.getRecurrence().getWarningDays(), getUsersToBeNotified(), getUserInSession());
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
        getContext().getMessages().add(new LocalizableMessage("security.maintenance.edit.success"));
        maintenanceService.editMaintenance(contractId, maintenanceId, documents, maintenance.getClosedDate(), getUserInSession());

        if (correctiveActions) {
            maintenance = maintenanceService.findMaintenance(maintenanceId);
            return new RedirectResolution(SecurityActionsPlanningActionBean.class, "actionsPlanningAdd")
                    .addParameter("contractId", contractId)
                    .addParameter("maintenanceId", maintenanceId)
                    .addParameter("event", event)
                    .addParameter("maintenanceCode", maintenance.getCode());
        }
        return new RedirectResolution(SecurityMaintenanceActionBean.class, "maintenanceEdit")
                .addParameter("contractId", contractId)
                .addParameter("maintenanceId", maintenanceId);
    }

    @ValidationMethod(on = "editMaintenance")
    public void validateEditAnomaly(ValidationErrors errors) throws Exception {
        if ((maintenanceService.findOpenCorrectiveActions(maintenanceId).size() > 0) && maintenance.getClosedDate() != null) {
            errors.add("closeDate", new LocalizableError("security.impact.work.edit.open.actions"));
        }
        if (maintenance.getMaintenanceType().getId() == -1 && otherType == null) {
            errors.add("otherType", new LocalizableError("security.maintenance.type.other.name"));
        }
    }

    @Secure(roles = "user")
    public Resolution maintenanceEquipmentsDefine() {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        setContract(securityManagementService.getContract(contractId));
        setEquipments(maintenanceService.findEquipmentsByContract(contractId));

        setContentTemplate("securityMaintenanceEquipmentsList.jsp");
        return new ForwardResolution("/WEB-INF/jsps/sm/securityView.jsp")
                .addParameter("contractId", contractId);
    }

    public Resolution insertEquipment() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException, IOException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        DocumentDTO documentDTO = null;
        if (document != null) {
            documentDTO = new DocumentDTO(document.getFileName(), document.getFileName(), document.getContentType(),
                    document.getInputStream());
        }
        maintenanceService.createEquipment(contractId, name, user, documentDTO);

        getContext().getMessages().add(new LocalizableMessage("security.maintenances.equipments.success"));
        return new RedirectResolution(SecurityMaintenanceActionBean.class, "maintenanceEquipmentsDefine")
                .addParameter("contractId", contractId);
    }

    @Secure(roles = "user")
    public Resolution deleteEquipment() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        if (maintenanceService.countMaintenanceActionsByEquipment(equipmentId) == 0) {
            maintenanceService.deleteEquipment(contractId, equipmentId, user);
            getContext().getMessages().add(new LocalizableMessage("security.maintenances.equipments.delete.success"));
            return new RedirectResolution(SecurityMaintenanceActionBean.class, "maintenanceEquipmentsDefine")
                    .addParameter("contractId", contractId);
        } else {
            getContext().getValidationErrors().addGlobalError(new LocalizableError("security.maintenances.equipments.delete.error"));
            return maintenanceEquipmentsDefine();
        }
    }

    @Secure(roles = "user")
    public Resolution reopenMaintenance() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        maintenanceService.reopenMaintenance(contractId, maintenanceId, user);
        getContext().getMessages().add(new LocalizableMessage("security.maintenance.reopen.success"));
        return new RedirectResolution(SecurityMaintenanceActionBean.class, "maintenanceEdit")
                .addParameter("contractId", contractId)
                .addParameter("maintenanceId", maintenanceId);
    }

    @Secure(roles = "user")
    public Resolution getMaintenanceDocument() {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
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
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        maintenanceService.deleteDocument(contractId, maintenanceId, documentId, user);
        getContext().getMessages().add(new LocalizableMessage("security.document.delete.success"));
        return new RedirectResolution(SecurityMaintenanceActionBean.class, "maintenanceEdit")
                .addParameter("contractId", contractId)
                .addParameter("maintenanceId", maintenanceId);
    }

    @Secure(roles = "user")
    public Resolution getChatPdf() throws ObjectNotFoundException, PDFException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        ReportPdf reportPdf = new ReportPdf(getContext());
        Maintenance maintenance = maintenanceService.findMaintenanceForChatPdf(maintenanceId);
        final ByteArrayOutputStream content = reportPdf.generateChatPDF(maintenance);
        return new StreamingResolution("application/pdf") {
            @Override
            protected void stream(HttpServletResponse response) throws Exception {
                response.getOutputStream().write(content.toByteArray());
            }
        }.setFilename(getMessage(getContext().getLocale(), "security.pdf.filename"));
    }

    @Secure(roles = "user")
    public Resolution deleteMaintenance() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        User user = getUserInSession();
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserExpert(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        maintenanceService.deleteMaintenance(contractId, maintenanceId, user);
        getContext().getMessages().add(new LocalizableMessage("security.maintenances.delete.success"));
        return new RedirectResolution(SecurityMaintenanceActionBean.class, "maintenancesGrid")
                .addParameter("contractId", contractId)
                .addParameters(getDisplayTagParameters());
    }

    @Secure(roles = "user")
    public Resolution maintenanceChat() throws ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }

        maintenance = maintenanceService.findMaintenance(maintenanceId);
        setClosed(maintenance.getClosed());
        setChatTitle(maintenance.getCode());
        setChatGetUrl("/sm/SecurityMaintenance.action?getChatMessages=&contractId=" + contractId +
                "&maintenanceId=" + maintenanceId);
        setChatPostUrl("/sm/SecurityMaintenance.action?addChatMessage=&contractId=" + contractId +
                "&maintenanceId=" + maintenanceId);

        setIsUserBasic(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserBasic(getUserInSession().getId(), contractId));
        return new ForwardResolution("/WEB-INF/jsps/sm/securityViewChat.jsp")
                .addParameter("contractId", contractId)
                .addParameter("maintenanceId", maintenanceId);
    }

    @Secure(roles = "user")
    public Resolution addChatMessage() throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        maintenanceService.addChatMessage(contractId, maintenanceId, chatMessage, getUserInSession());
        return getChatMessages();
    }

    @Secure(roles = "user")
    public Resolution getChatMessages() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        if (!securityManagementService.isSubModuleAllowed(contractId, SubModuleType.MNT)
                || (!securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId())
                && !securityManagementService.isUserBasic(getUserInSession().getId(), contractId))) {
            return new ForwardResolution("/WEB-INF/jsps/sm/unauthorized.jsp");
        }
        List<Chat> chats = maintenanceService.findChatMessages(maintenanceId);
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

    public void setUserPermissionType() {
        setIsUserBasic(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserBasic(getUserInSession().getId(), contractId));
        setIsUserIntermediate(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserIntermediate(getUserInSession().getId(), contractId));
        setIsUserExpert(securityManagementService.isUserAdministratorOrCertitecna(getUserInSession().getId()) ||
                securityManagementService.isUserExpert(getUserInSession().getId(), contractId));
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
        setTopEvent("maintenanceGrid");
    }

    private static String getMessage(Locale locale, String key, Object... arguments) {
        return new MessageFormat(ResourceBundle.getBundle("StripesResources", locale).getString(key), locale)
                .format(arguments);
    }

    private void loadMaitenanceTypes() {
        maintenanceTypes = maintenanceService.findMaintenanceTypes(contractId);
    }

    public MaintenanceType getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(MaintenanceType maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getOtherType() {
        return otherType;
    }

    public void setOtherType(String otherType) {
        this.otherType = otherType;
    }

    public String getOtherEquipment() {
        return otherEquipment;
    }

    public void setOtherEquipment(String otherEquipment) {
        this.otherEquipment = otherEquipment;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public FileBean getNewEquipmentDoc() {
        return newEquipmentDoc;
    }

    public void setNewEquipmentDoc(FileBean newEquipmentDoc) {
        this.newEquipmentDoc = newEquipmentDoc;
    }

    private void loadRecurrenceTypes() {
        recurrenceTypes = recurrenceService.findRecurrenceTypes();
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    private void loadEquipments() {
        equipments = maintenanceService.findEquipments(contractId);
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

    public PaginatedListAdapter getMaintenanceAdapter() {
        return maintenanceAdapter;
    }

    public void setMaintenanceAdapter(PaginatedListAdapter maintenanceAdapter) {
        this.maintenanceAdapter = maintenanceAdapter;
    }

    public List<String> getMaintenanceYears() {
        return maintenanceYears;
    }

    public void setMaintenanceYears(List<String> maintenanceYears) {
        this.maintenanceYears = maintenanceYears;
    }

    public List<String> getMaintenanceStatus() {
        return maintenanceStatus;
    }

    public void setMaintenanceStatus(List<String> maintenanceStatus) {
        this.maintenanceStatus = maintenanceStatus;
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

    public Long getMaintenanceTypeId() {
        return maintenanceTypeId;
    }

    public void setMaintenanceTypeId(Long maintenanceTypeId) {
        this.maintenanceTypeId = maintenanceTypeId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
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

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean getClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
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

    public Maintenance getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(Maintenance maintenance) {
        this.maintenance = maintenance;
    }

    public List<RecurrenceType> getRecurrenceTypes() {
        return recurrenceTypes;
    }

    public void setRecurrenceTypes(List<RecurrenceType> recurrenceTypes) {
        this.recurrenceTypes = recurrenceTypes;
    }

    public List<MaintenanceType> getMaintenanceTypes() {
        return maintenanceTypes;
    }

    public void setMaintenanceTypes(List<MaintenanceType> maintenanceTypes) {
        this.maintenanceTypes = maintenanceTypes;
    }

    public List<Equipment> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<Equipment> equipments) {
        this.equipments = equipments;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(Long maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public FileBean getDocument() {
        return document;
    }

    public void setDocument(FileBean document) {
        this.document = document;
    }

    public Long getRecurrenceTypeId() {
        return recurrenceTypeId;
    }

    public void setRecurrenceTypeId(Long recurrenceTypeId) {
        this.recurrenceTypeId = recurrenceTypeId;
    }

    public String getUsersToNotify() {
        return usersToNotify;
    }

    public void setUsersToNotify(String usersToNotify) {
        this.usersToNotify = usersToNotify;
    }

    public Integer getWarningDays() {
        return warningDays;
    }

    public void setWarningDays(Integer warningDays) {
        this.warningDays = warningDays;
    }

    @Override
    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertEquipment")) {
            return maintenanceEquipmentsDefine();
        }
        if (getContext().getEventName().equals("insertMaintenance")) {
            return maintenanceAdd();
        }
        if (getContext().getEventName().equals("editMaintenance")) {
            return maintenanceEdit();
        }
        return null;
    }

    public boolean getIsValidToken() {
        return false;
    }

    public SubModuleType getSubModuleType() {
        return SubModuleType.MNT;
    }

}
