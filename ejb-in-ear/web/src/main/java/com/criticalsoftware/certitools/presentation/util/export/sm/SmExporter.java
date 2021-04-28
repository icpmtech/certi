package com.criticalsoftware.certitools.presentation.util.export.sm;

import com.criticalsoftware.certitools.business.exception.CSVException;
import com.criticalsoftware.certitools.business.exception.ExcelException;
import com.criticalsoftware.certitools.business.exception.PDFException;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Activity;
import com.criticalsoftware.certitools.entities.sm.Anomaly;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.EmergencyAction;
import com.criticalsoftware.certitools.entities.sm.EmergencyUser;
import com.criticalsoftware.certitools.entities.sm.Equipment;
import com.criticalsoftware.certitools.entities.sm.Maintenance;
import com.criticalsoftware.certitools.entities.sm.Risk;
import com.criticalsoftware.certitools.entities.sm.SecurityImpact;
import com.criticalsoftware.certitools.entities.sm.SecurityImpactWork;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import net.sourceforge.stripes.action.ActionBeanContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Security Management Exporter
 *
 * @author miseabra
 * @version $Revision: $
 */
public class SmExporter {

    private Contract contract;
    private ActionBeanContext context;
    private ResourceBundle resources;
    private Map<SubModuleType, List> smRecords;
    private Map<String, InputStream> exportFiles;
    private Map<String, Long> documents;
    private List<SecurityImpact> securityImpacts;
    private List<Risk> risks;
    private List<Equipment> equipments;
    private List<User> users;
    private List<EmergencyUser> emergencyUsers;

    public SmExporter(Contract contract, Map<SubModuleType, List> smRecords, List<SecurityImpact> securityImpacts,
                      List<Risk> risks, List<Equipment> equipments, List<User> users,
                      List<EmergencyUser> emergencyUsers, ActionBeanContext context,
                      Map<String, InputStream> exportFiles, Map<String, Long> documents) {
        this.contract = contract;
        this.smRecords = smRecords;
        this.context = context;
        this.exportFiles = exportFiles;
        this.documents = documents;
        this.resources = ResourceBundle.getBundle("StripesResources", context.getLocale());
        this.securityImpacts = securityImpacts;
        this.risks = risks;
        this.equipments = equipments;
        this.users = users;
        this.emergencyUsers = emergencyUsers;
    }

    public void createExportFiles() throws PDFException, ExcelException, CSVException {
        for (Map.Entry<SubModuleType, List> entry : smRecords.entrySet()) {
            //create excel workbook with the list of records and add it to the export files
            createRecordsList(entry.getKey());
            //add the related documents to the export files
            addRelatedDocuments(entry.getKey());
        }
    }

    @SuppressWarnings("unchecked")
    private void createRecordsList(SubModuleType subModule) throws ExcelException {
        ListExporterExcel listExporter = new ListExporterExcel(context.getLocale());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String listName = null;
        switch (subModule) {
            case ACTV:
                baos = listExporter.generateActivitiesList((List<Activity>) smRecords.get(subModule));
                listName = getMessage("security.activity.filename.xls");
                break;
            case ANOM:
                baos = listExporter.generateAnomaliesList((List<Anomaly>) smRecords.get(subModule));
                listName = getMessage("security.anomaly.filename.xls");
                break;
            case SIW:
                baos = listExporter.generateSecurityImpactWorksList((List<SecurityImpactWork>) smRecords.get(subModule));
                listName = getMessage("security.work.filename.xls");
                break;
            case APC:
                baos = listExporter.generateCorrectiveActionsList((List<CorrectiveAction>) smRecords.get(subModule));
                listName = getMessage("security.action.filename.xls");
                break;
            case MNT:
                baos = listExporter.generateMaintenancesList((List<Maintenance>) smRecords.get(subModule), equipments);
                listName = getMessage("security.maintenance.filename.xls");
                break;
            case EMRG:
                baos = listExporter.generateEmergencyActionsList((List<EmergencyAction>) smRecords.get(subModule),
                        emergencyUsers);
                listName = getMessage("security.emergency.filename.xls");
                break;
        }
        exportFiles.put(getMessage(subModule.getKey()) + "/" + listName, new ByteArrayInputStream(baos.toByteArray()));
    }

    private void addRelatedDocuments(SubModuleType subModule) throws PDFException, CSVException {
        String folder = getMessage(subModule.getKey()) + "/" + getMessage("security.documents.folder") + "/";
        List recordsList = smRecords.get(subModule);
        ReportPdf reportPdf = new ReportPdf(context);
        ReportCsv reportCsv = new ReportCsv(context.getLocale());

        //add the user uploaded files to the documents list and generate reports if the record is closed
        for (Object o : recordsList) {
            switch (subModule) {
                case ACTV:
                    Activity activity = (Activity) o;
                    activity.setContract(contract);
                    addDocumentsToFiles(folder, activity.getDocuments());
                    addReportsToFiles(reportPdf, folder, activity);
                    break;
                case ANOM:
                    Anomaly anomaly = (Anomaly) o;
                    anomaly.setContract(contract);
                    addDocumentsToFiles(folder, anomaly.getDocuments());
                    addReportsToFiles(reportPdf, folder, anomaly);
                    break;
                case SIW:
                    SecurityImpactWork securityImpactWork = (SecurityImpactWork) o;
                    securityImpactWork.setContract(contract);
                    addDocumentsToFiles(folder, securityImpactWork.getDocuments());
                    addReportsToFiles(reportPdf, folder, securityImpactWork);
                    break;
                case APC:
                    CorrectiveAction correctiveAction = (CorrectiveAction) o;
                    correctiveAction.setContract(contract);
                    addDocumentsToFiles(folder, correctiveAction.getDocuments());
                    addReportsToFiles(reportPdf, folder, correctiveAction);
                    break;
                case MNT:
                    Maintenance maintenance = (Maintenance) o;
                    maintenance.setContract(contract);
                    addDocumentsToFiles(folder, maintenance.getDocuments());
                    addReportsToFiles(reportPdf, folder, maintenance);
                    //add equipments documents
                    for (Equipment equipment : equipments) {
                        if (equipment.getDocument() != null) {
                            addDocumentToFiles(folder, equipment.getDocument());
                        }
                    }
                    break;
                case EMRG:
                    EmergencyAction emergencyAction = (EmergencyAction) o;
                    emergencyAction.setContract(contract);
                    addReportsToFiles(reportPdf, reportCsv, folder, emergencyAction);
                    break;
            }
        }
    }

    private void addDocumentToFiles(String folder, Document document) {
        documents.put(folder + "Document" + document.getId() + "-" + document.getName(), document.getId());
    }

    private void addDocumentsToFiles(String folder, List<Document> documents) {
        for (Document d : documents) {
            addDocumentToFiles(folder, d);
        }
    }

    private void addReportsToFiles(ReportPdf reportPdf, String folder, Activity activity) throws PDFException {
        if (activity.getClosed() && activity.getHasChatMessages()) {
            exportFiles.put(folder + activity.getCode() + "-" + getMessage("security.pdf.filename"),
                    new ByteArrayInputStream(reportPdf.generateChatPDF(activity).toByteArray()));
        }
    }

    private void addReportsToFiles(ReportPdf reportPdf, String folder, Anomaly anomaly) throws PDFException {
        if (anomaly.getClosed()) {
            if (anomaly.getHasChatMessages()) {
                exportFiles.put(folder + anomaly.getCode() + "-" + getMessage("security.pdf.filename"),
                        new ByteArrayInputStream(reportPdf.generateChatPDF(anomaly).toByteArray()));
            }
            exportFiles.put(folder + anomaly.getCode() + ".pdf",
                    new ByteArrayInputStream(reportPdf.generateAnomalyPDF(anomaly, securityImpacts).toByteArray()));
        }
    }

    private void addReportsToFiles(ReportPdf reportPdf, String folder, SecurityImpactWork securityImpactWork)
            throws PDFException {
        if (securityImpactWork.getClosed()) {
            if (securityImpactWork.getHasChatMessages()) {
                exportFiles.put(folder + securityImpactWork.getCode() + "-" + getMessage("security.pdf.filename"),
                        new ByteArrayInputStream(reportPdf.generateChatPDF(securityImpactWork).toByteArray()));
            }
            exportFiles.put(folder + securityImpactWork.getCode() + ".pdf",
                    new ByteArrayInputStream(reportPdf.generateSecurityImpactWorkPDF(
                            securityImpactWork, securityImpacts, risks).toByteArray()));
        }
    }

    private void addReportsToFiles(ReportPdf reportPdf, String folder, CorrectiveAction correctiveAction)
            throws PDFException {
        if (correctiveAction.getClosed()) {
            if (correctiveAction.getHasChatMessages()) {
                exportFiles.put(folder + correctiveAction.getCode() + "-" + getMessage("security.pdf.filename"),
                        new ByteArrayInputStream(reportPdf.generateChatPDF(correctiveAction).toByteArray()));
            }
            exportFiles.put(folder + correctiveAction.getCode() + ".pdf",
                    new ByteArrayInputStream(reportPdf.generateCorrectiveActionPDF(correctiveAction).toByteArray()));
        }
    }

    private void addReportsToFiles(ReportPdf reportPdf, String folder, Maintenance maintenance) throws PDFException {
        if (maintenance.getClosed() && maintenance.getHasChatMessages()) {
            exportFiles.put(folder + maintenance.getCode() + "-" + getMessage("security.pdf.filename"),
                    new ByteArrayInputStream(reportPdf.generateChatPDF(maintenance).toByteArray()));
        }
    }

    private void addReportsToFiles(ReportPdf reportPdf, ReportCsv reportCsv, String folder,
                                   EmergencyAction emergencyAction) throws PDFException, CSVException {
        if (emergencyAction.getClosed()) {
            if (emergencyAction.getHasChatMessages()) {
                exportFiles.put(folder + emergencyAction.getCode() + "-" + getMessage("security.csv.filename"),
                        new ByteArrayInputStream(reportCsv.generateChatCSV(emergencyAction.getChatMessages()).toByteArray()));
            }
            exportFiles.put(folder + emergencyAction.getCode() + ".pdf",
                    new ByteArrayInputStream(reportPdf.generateEmergencyActionPDF(
                            emergencyAction, users, emergencyUsers).toByteArray()));
        }
    }

    /**
     * Get a resource from the resource bundle.
     *
     * @param messageKey The message key.
     * @param arguments  Var args parameters to be included on the message.
     * @return The resource string.
     */
    private String getMessage(String messageKey, String... arguments) {
        return new MessageFormat(resources.getString(messageKey)).format(arguments);
    }
}
