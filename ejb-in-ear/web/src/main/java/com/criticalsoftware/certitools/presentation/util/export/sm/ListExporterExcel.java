package com.criticalsoftware.certitools.presentation.util.export.sm;

import com.criticalsoftware.certitools.business.exception.ExcelException;
import com.criticalsoftware.certitools.entities.sm.*;
import com.criticalsoftware.certitools.entities.sm.enums.AnomalyType;
import com.criticalsoftware.certitools.entities.sm.enums.WorkType;
import com.criticalsoftware.certitools.util.Configuration;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.*;

/**
 * Class for creating excel files for security records.
 *
 * @author miseabra
 * @version $Revision: $
 */
@SuppressWarnings("UnusedDeclaration")
public class ListExporterExcel {

    private ResourceBundle resources;

    public ListExporterExcel(Locale l) {
        this.resources = ResourceBundle.getBundle("StripesResources", l);
    }

    public ByteArrayOutputStream generateActivitiesList(List<Activity> activities) throws ExcelException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            createCustomColours(workbook);
            List<HSSFCellStyle> linkStyles = createHyperlinkStyles(workbook);
            List<HSSFCellStyle> stringStyles = createStringStyles(workbook);
            List<HSSFCellStyle> dateStyles = createDateStyles(workbook);
            org.apache.poi.hssf.usermodel.HSSFSheet sheet = workbook.createSheet(getMessage("security.submodule.activity"));

            //creating header row
            List<String> headerValues = new ArrayList<String>();
            headerValues.add(getMessage("security.activity.view.activitytype"));
            headerValues.add(getMessage("security.activity.view.name"));
            headerValues.add(getMessage("security.activity.view.internalresponsible"));
            headerValues.add(getMessage("security.activity.view.datescheduled"));
            headerValues.add(getMessage("security.activity.view.closeddate"));
            headerValues.add(getMessage("security.activity.view.state"));
            headerValues.add(getMessage("security.activity.view.correctiveactions"));
            headerValues.add(getMessage("security.activity.view.documents"));
            createHeaderRow(workbook, sheet, headerValues);

            int firstRow = 1, lastRow;
            for (int i = 0; i < activities.size(); i++) {
                //for each record we may need to use more than one row
                //we can only add one link to each cell, so the documents column will occupy several cells for the same record
                lastRow = firstRow + getNumberOfRowsForDocuments(activities.get(i).getDocuments(),
                        activities.get(i).getCorrectiveActions(), activities.get(i).getClosed(),
                        activities.get(i).getHasChatMessages(), false) - 1;
                createRows(sheet, firstRow, lastRow);

                int styleIndex = i % 2;

                createMergedCell(sheet, firstRow, lastRow, 0, stringStyles.get(styleIndex),
                        activities.get(i).getActivityType().getName());
                createMergedCell(sheet, firstRow, lastRow, 1, stringStyles.get(styleIndex),
                        activities.get(i).getName());
                createMergedCell(sheet, firstRow, lastRow, 2, stringStyles.get(styleIndex),
                        activities.get(i).getInternalResponsible());
                createMergedCell(sheet, firstRow, lastRow, 3, dateStyles.get(styleIndex),
                        activities.get(i).getDateScheduled());
                createMergedCell(sheet, firstRow, lastRow, 4, dateStyles.get(styleIndex),
                        activities.get(i).getClosedDate());
                String status = activities.get(i).getClosed() ? getMessage("security.state.closed") :
                        getMessage("security.state.open");
                createMergedCell(sheet, firstRow, lastRow, 5, stringStyles.get(styleIndex), status);

                createCorrectiveActionsCell(sheet, firstRow, lastRow, 6, stringStyles.get(styleIndex),
                        activities.get(i).getCorrectiveActions());
                createDocumentsCells(sheet, firstRow, 7, linkStyles.get(styleIndex), activities.get(i).getDocuments(),
                        activities.get(i).getCorrectiveActions(), activities.get(i).getCode(),
                        activities.get(i).getClosed(), false, activities.get(i).getHasChatMessages());

                firstRow = lastRow + 1;
            }

            finalAdjustments(sheet, headerValues.size());
            workbook.write(baos);

        } catch (Exception e) {
            throw new ExcelException("Error while creating Excel", e);
        }
        return baos;
    }

    public ByteArrayOutputStream generateAnomaliesList(List<Anomaly> anomalies) throws ExcelException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            createCustomColours(workbook);
            List<HSSFCellStyle> linkStyles = createHyperlinkStyles(workbook);
            List<HSSFCellStyle> stringStyles = createStringStyles(workbook);
            List<HSSFCellStyle> dateStyles = createDateStyles(workbook);
            HSSFSheet sheet = workbook.createSheet(getMessage("security.submodule.anomaly"));

            //creating header row
            List<String> headerValues = new ArrayList<String>();
            headerValues.add(getMessage("security.anomaly.view.type"));
            headerValues.add(getMessage("security.anomaly.view.code"));
            headerValues.add(getMessage("security.anomaly.view.datetime"));
            headerValues.add(getMessage("security.anomaly.view.closeddate"));
            headerValues.add(getMessage("security.anomaly.view.state"));
            headerValues.add(getMessage("security.anomaly.view.correctiveactions"));
            headerValues.add(getMessage("security.anomaly.view.documents"));
            createHeaderRow(workbook, sheet, headerValues);

            int firstRow = 1, lastRow;
            for (int i = 0; i < anomalies.size(); i++) {
                //for each record we may need to use more than one row
                //we can only add one link to each cell, so the documents column will occupy several cells for the same record
                lastRow = firstRow + getNumberOfRowsForDocuments(anomalies.get(i).getDocuments(),
                        anomalies.get(i).getCorrectiveActions(), anomalies.get(i).getClosed(),
                        anomalies.get(i).getHasChatMessages(), true) - 1;
                createRows(sheet, firstRow, lastRow);

                int styleIndex = i % 2;

                String type = anomalies.get(i).getAnomalyType() == AnomalyType.ANOMALY ?
                        getMessage("security.anomalyType.anomaly") : getMessage("security.anomalyType.occurrence");
                createMergedCell(sheet, firstRow, lastRow, 0, stringStyles.get(styleIndex), type);
                createMergedCell(sheet, firstRow, lastRow, 1, stringStyles.get(styleIndex),
                        anomalies.get(i).getCode());
                createMergedCell(sheet, firstRow, lastRow, 2, dateStyles.get(styleIndex),
                        anomalies.get(i).getDatetime());
                createMergedCell(sheet, firstRow, lastRow, 3, dateStyles.get(styleIndex),
                        anomalies.get(i).getClosedDate());
                String status = anomalies.get(i).getClosed() ? getMessage("security.state.closed") :
                        getMessage("security.state.open");
                createMergedCell(sheet, firstRow, lastRow, 4, stringStyles.get(styleIndex), status);

                createCorrectiveActionsCell(sheet, firstRow, lastRow, 5, stringStyles.get(styleIndex),
                        anomalies.get(i).getCorrectiveActions());
                createDocumentsCells(sheet, firstRow, 6, linkStyles.get(styleIndex), anomalies.get(i).getDocuments(),
                        anomalies.get(i).getCorrectiveActions(), anomalies.get(i).getCode(),
                        anomalies.get(i).getClosed(), true, anomalies.get(i).getHasChatMessages());

                firstRow = lastRow + 1;
            }

            finalAdjustments(sheet, headerValues.size());
            workbook.write(baos);

        } catch (Exception e) {
            throw new ExcelException("Error while creating Excel", e);
        }
        return baos;
    }

    public ByteArrayOutputStream generateSecurityImpactWorksList(List<SecurityImpactWork> securityImpactWorks)
            throws ExcelException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            createCustomColours(workbook);
            List<HSSFCellStyle> linkStyles = createHyperlinkStyles(workbook);
            List<HSSFCellStyle> stringStyles = createStringStyles(workbook);
            List<HSSFCellStyle> dateStyles = createDateStyles(workbook);
            HSSFSheet sheet = workbook.createSheet(getMessage("security.submodule.securityimpactwork"));

            //creating header row
            List<String> headerValues = new ArrayList<String>();
            headerValues.add(getMessage("security.impact.work.view.type"));
            headerValues.add(getMessage("security.impact.work.view.code"));
            headerValues.add(getMessage("security.impact.work.view.datetime"));
            headerValues.add(getMessage("security.impact.work.view.closeddate"));
            headerValues.add(getMessage("security.impact.work.view.state"));
            headerValues.add(getMessage("security.impact.work.view.correctiveactions"));
            headerValues.add(getMessage("security.impact.work.view.documents"));
            createHeaderRow(workbook, sheet, headerValues);

            int firstRow = 1, lastRow;
            for (int i = 0; i < securityImpactWorks.size(); i++) {
                //for each record we may need to use more than one row
                //we can only add one link to each cell, so the documents column will occupy several cells for the same record
                lastRow = firstRow + getNumberOfRowsForDocuments(securityImpactWorks.get(i).getDocuments(),
                        securityImpactWorks.get(i).getCorrectiveActions(), securityImpactWorks.get(i).getClosed(),
                        securityImpactWorks.get(i).getHasChatMessages(), true) - 1;
                createRows(sheet, firstRow, lastRow);

                int styleIndex = i % 2;

                String type = securityImpactWorks.get(i).getWorkType() == WorkType.MODIFICATION ?
                        getMessage("security.workType.modification") : getMessage("security.workType.authorization");
                createMergedCell(sheet, firstRow, lastRow, 0, stringStyles.get(styleIndex), type);
                createMergedCell(sheet, firstRow, lastRow, 1, stringStyles.get(styleIndex),
                        securityImpactWorks.get(i).getCode());
                createMergedCell(sheet, firstRow, lastRow, 2, dateStyles.get(styleIndex),
                        securityImpactWorks.get(i).getStartDate());
                createMergedCell(sheet, firstRow, lastRow, 3, dateStyles.get(styleIndex),
                        securityImpactWorks.get(i).getClosedDate());
                String status = securityImpactWorks.get(i).getClosed() ? getMessage("security.state.closed") :
                        getMessage("security.state.open");
                createMergedCell(sheet, firstRow, lastRow, 4, stringStyles.get(styleIndex), status);

                createCorrectiveActionsCell(sheet, firstRow, lastRow, 5, stringStyles.get(styleIndex),
                        securityImpactWorks.get(i).getCorrectiveActions());
                createDocumentsCells(sheet, firstRow, 6, linkStyles.get(styleIndex),
                        securityImpactWorks.get(i).getDocuments(), securityImpactWorks.get(i).getCorrectiveActions(),
                        securityImpactWorks.get(i).getCode(), securityImpactWorks.get(i).getClosed(), true,
                        securityImpactWorks.get(i).getHasChatMessages());

                firstRow = lastRow + 1;
            }

            finalAdjustments(sheet, headerValues.size());
            workbook.write(baos);

        } catch (Exception e) {
            throw new ExcelException("Error while creating Excel", e);
        }
        return baos;
    }

    public ByteArrayOutputStream generateCorrectiveActionsList(List<CorrectiveAction> correctiveActions)
            throws ExcelException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            createCustomColours(workbook);
            List<HSSFCellStyle> linkStyles = createHyperlinkStyles(workbook);
            List<HSSFCellStyle> stringStyles = createStringStyles(workbook);
            List<HSSFCellStyle> dateStyles = createDateStyles(workbook);
            HSSFSheet sheet = workbook.createSheet(getMessage("security.submodule.correctiveaction"));

            //creating header row
            List<String> headerValues = new ArrayList<String>();
            headerValues.add(getMessage("security.actions.view.code"));
            headerValues.add(getMessage("security.actions.view.startdate"));
            headerValues.add(getMessage("security.actions.view.closeddate"));
            headerValues.add(getMessage("security.actions.view.state"));
            headerValues.add(getMessage("security.actions.view.duration"));
            headerValues.add(getMessage("security.actions.view.executionresponsible"));
            headerValues.add(getMessage("security.actions.view.documents"));
            createHeaderRow(workbook, sheet, headerValues);

            int firstRow = 1, lastRow;
            for (int i = 0; i < correctiveActions.size(); i++) {
                //for each record we may need to use more than one row
                //we can only add one link to each cell, so the documents column will occupy several cells for the same record
                lastRow = firstRow + getNumberOfRowsForDocuments(correctiveActions.get(i).getDocuments(), null,
                        correctiveActions.get(i).getClosed(), correctiveActions.get(i).getHasChatMessages(), true) - 1;
                createRows(sheet, firstRow, lastRow);

                int styleIndex = i % 2;

                createMergedCell(sheet, firstRow, lastRow, 0, stringStyles.get(styleIndex),
                        correctiveActions.get(i).getCode());
                createMergedCell(sheet, firstRow, lastRow, 1, dateStyles.get(styleIndex),
                        correctiveActions.get(i).getCreationDate());
                createMergedCell(sheet, firstRow, lastRow, 2, dateStyles.get(styleIndex),
                        correctiveActions.get(i).getClosedDate());
                String status = correctiveActions.get(i).getClosed() ? getMessage("security.state.closed") :
                        getMessage("security.state.open");
                createMergedCell(sheet, firstRow, lastRow, 3, stringStyles.get(styleIndex), status);
                createMergedCell(sheet, firstRow, lastRow, 4, stringStyles.get(styleIndex),
                        correctiveActions.get(i).getDuration());
                createMergedCell(sheet, firstRow, lastRow, 5, stringStyles.get(styleIndex),
                        correctiveActions.get(i).getExecutionResponsible());
                createDocumentsCells(sheet, firstRow, 6, linkStyles.get(styleIndex),
                        correctiveActions.get(i).getDocuments(), null, correctiveActions.get(i).getCode(),
                        correctiveActions.get(i).getClosed(), true, correctiveActions.get(i).getHasChatMessages());

                firstRow = lastRow + 1;
            }

            finalAdjustments(sheet, headerValues.size());
            workbook.write(baos);

        } catch (Exception e) {
            throw new ExcelException("Error while creating Excel", e);
        }
        return baos;
    }

    public ByteArrayOutputStream generateMaintenancesList(List<Maintenance> maintenances, List<Equipment> equipments)
            throws ExcelException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            createCustomColours(workbook);
            List<HSSFCellStyle> linkStyles = createHyperlinkStyles(workbook);
            List<HSSFCellStyle> stringStyles = createStringStyles(workbook);
            List<HSSFCellStyle> dateStyles = createDateStyles(workbook);
            createMaintenancesSheet(workbook, linkStyles, stringStyles, dateStyles, maintenances);
            createEquipmentsSheet(workbook, linkStyles, stringStyles, equipments);

            workbook.write(baos);

        } catch (Exception e) {
            throw new ExcelException("Error while creating Excel", e);
        }
        return baos;
    }

    public ByteArrayOutputStream generateEmergencyActionsList(List<EmergencyAction> emergencyActions,
                                                              List<EmergencyUser> emergencyUsers)
            throws ExcelException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            createCustomColours(workbook);
            List<HSSFCellStyle> linkStyles = createHyperlinkStyles(workbook);
            List<HSSFCellStyle> stringStyles = createStringStyles(workbook);
            List<HSSFCellStyle> dateStyles = createDateStyles(workbook);
            createEmergencyActionsSheet(workbook, linkStyles, stringStyles, dateStyles, emergencyActions);
            createEmergencyUsersSheet(workbook, stringStyles, emergencyUsers);

            workbook.write(baos);

        } catch (Exception e) {
            throw new ExcelException("Error while creating Excel", e);
        }
        return baos;
    }

    private void createMaintenancesSheet(HSSFWorkbook workbook, List<HSSFCellStyle> linkStyles,
                                         List<HSSFCellStyle> stringStyles, List<HSSFCellStyle> dateStyles,
                                         List<Maintenance> maintenances) {

        HSSFSheet sheet = workbook.createSheet(getMessage("security.submodule.maintenance"));

        //creating header row
        List<String> headerValues = new ArrayList<String>();
        headerValues.add(getMessage("security.maintenances.view.type"));
        headerValues.add(getMessage("security.maintenances.view.code"));
        headerValues.add(getMessage("security.maintenances.view.equipment"));
        headerValues.add(getMessage("security.maintenances.view.internalresponsible"));
        headerValues.add(getMessage("security.maintenances.view.datescheduled"));
        headerValues.add(getMessage("security.maintenances.view.closeddate"));
        headerValues.add(getMessage("security.maintenances.view.state"));
        headerValues.add(getMessage("security.maintenances.view.correctiveactions"));
        headerValues.add(getMessage("security.maintenances.view.documents"));
        createHeaderRow(workbook, sheet, headerValues);

        int firstRow = 1, lastRow;
        for (int i = 0; i < maintenances.size(); i++) {
            //for each record we may need to use more than one row
            //we can only add one link to each cell, so the documents column will occupy several cells for the same record
            lastRow = firstRow + getNumberOfRowsForDocuments(maintenances.get(i).getDocuments(),
                    maintenances.get(i).getCorrectiveActions(), maintenances.get(i).getClosed(),
                    maintenances.get(i).getHasChatMessages(), false) - 1;
            createRows(sheet, firstRow, lastRow);

            int styleIndex = i % 2;

            createMergedCell(sheet, firstRow, lastRow, 0, stringStyles.get(styleIndex),
                    maintenances.get(i).getMaintenanceType().getName());
            createMergedCell(sheet, firstRow, lastRow, 1, stringStyles.get(styleIndex), maintenances.get(i).getCode());

            //equipment cell - allows the download of the equipment document when the maintenance is a verification (type id = 2)
            if (maintenances.get(i).getEquipment().getDocument() != null &&
                    maintenances.get(i).getMaintenanceType().getId() == 2) {
                createHyperlinkCell(sheet, firstRow, 2, linkStyles.get(styleIndex),
                        maintenances.get(i).getEquipment().getName(),
                        getMessage("security.documents.folder") + "\\Document" +
                                maintenances.get(i).getEquipment().getDocument().getId() + "-" +
                                maintenances.get(i).getEquipment().getDocument().getName());
                sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, 2, 2));
            } else {
                createMergedCell(sheet, firstRow, lastRow, 2, stringStyles.get(styleIndex),
                        maintenances.get(i).getEquipment().getName());
            }

            createMergedCell(sheet, firstRow, lastRow, 3, stringStyles.get(styleIndex),
                    maintenances.get(i).getInternalResponsible());
            createMergedCell(sheet, firstRow, lastRow, 4, dateStyles.get(styleIndex),
                    maintenances.get(i).getDateScheduled());
            createMergedCell(sheet, firstRow, lastRow, 5, dateStyles.get(styleIndex),
                    maintenances.get(i).getClosedDate());
            String status = maintenances.get(i).getClosed() ? getMessage("security.state.closed") :
                    getMessage("security.state.open");
            createMergedCell(sheet, firstRow, lastRow, 6, stringStyles.get(styleIndex), status);

            createCorrectiveActionsCell(sheet, firstRow, lastRow, 7, stringStyles.get(styleIndex),
                    maintenances.get(i).getCorrectiveActions());
            createDocumentsCells(sheet, firstRow, 8, linkStyles.get(styleIndex), maintenances.get(i).getDocuments(),
                    maintenances.get(i).getCorrectiveActions(), maintenances.get(i).getCode(),
                    maintenances.get(i).getClosed(), false, maintenances.get(i).getHasChatMessages());

            firstRow = lastRow + 1;
        }
        finalAdjustments(sheet, headerValues.size());
    }

    private void createEquipmentsSheet(HSSFWorkbook workbook, List<HSSFCellStyle> linkStyles, List<HSSFCellStyle> stringStyles,
                                       List<Equipment> equipments) {

        HSSFSheet sheet = workbook.createSheet(getMessage("security.maintenances.equipments.title"));

        //creating header row
        List<String> headerValues = new ArrayList<String>();
        headerValues.add(getMessage("security.maintenances.equipments.view.name"));
        headerValues.add(getMessage("security.maintenances.equipments.view.document"));
        createHeaderRow(workbook, sheet, headerValues);

        int rowNumber = 1;
        createRows(sheet, rowNumber, rowNumber + equipments.size());
        for (int i = 0; i < equipments.size(); i++) {
            int styleIndex = i % 2;

            createMergedCell(sheet, rowNumber, rowNumber, 0, stringStyles.get(styleIndex), equipments.get(i).getName());
            if (equipments.get(i).getDocument() != null) {
                createHyperlinkCell(sheet, rowNumber, 1, linkStyles.get(styleIndex),
                        equipments.get(i).getDocument().getDisplayName(), getMessage("security.documents.folder") +
                                "\\Document" + equipments.get(i).getDocument().getId() + "-" +
                                equipments.get(i).getDocument().getName());
            }
            rowNumber++;
        }
        finalAdjustments(sheet, headerValues.size());
    }

    private void createEmergencyActionsSheet(HSSFWorkbook workbook, List<HSSFCellStyle> linkStyles,
                                             List<HSSFCellStyle> stringStyles, List<HSSFCellStyle> dateStyles,
                                             List<EmergencyAction> emergencyActions) {

        HSSFSheet sheet = workbook.createSheet(getMessage("security.submodule.emergencyaction"));

        //creating header row
        List<String> headerValues = new ArrayList<String>();
        headerValues.add(getMessage("security.emergency.code"));
        headerValues.add(getMessage("security.emergency.origin"));
        headerValues.add(getMessage("security.emergency.startdate"));
        headerValues.add(getMessage("security.emergency.documents"));
        createHeaderRow(workbook, sheet, headerValues);

        int firstRow = 1, lastRow;
        for (int i = 0; i < emergencyActions.size(); i++) {
            //for each record we may need to use more than one row
            //we can only add one link to each cell, so the documents column will occupy several cells for the same record
            lastRow = firstRow + getNumberOfRowsForDocuments(null, null, emergencyActions.get(i).getClosed(),
                    emergencyActions.get(i).getHasChatMessages(), true) - 1;
            createRows(sheet, firstRow, lastRow);

            int styleIndex = i % 2;

            createMergedCell(sheet, firstRow, lastRow, 0, stringStyles.get(styleIndex),
                    emergencyActions.get(i).getCode());
            createMergedCell(sheet, firstRow, lastRow, 1, stringStyles.get(styleIndex),
                    emergencyActions.get(i).getOrigin());
            createMergedCell(sheet, firstRow, lastRow, 2, dateStyles.get(styleIndex),
                    emergencyActions.get(i).getStartDate());

            //documents cells
            if (emergencyActions.get(i).getClosed()) {
                createHyperlinkCell(sheet, firstRow, 3, linkStyles.get(styleIndex),
                        emergencyActions.get(i).getCode() + ".pdf",
                        getMessage("security.documents.folder") + "\\" + emergencyActions.get(i).getCode() + ".pdf");
                //chat csv report
                if (emergencyActions.get(i).getHasChatMessages()) {
                    createHyperlinkCell(sheet, firstRow + 1, 3, linkStyles.get(styleIndex),
                            getMessage("security.csv.filename"), getMessage("security.documents.folder") + "\\" +
                                    emergencyActions.get(i).getCode() + "-" + getMessage("security.csv.filename"));
                }
            } else {
                //create empty cell if there are no documents to add
                createEmptyCell(sheet, firstRow, lastRow, 3, linkStyles.get(styleIndex));
            }

            firstRow = lastRow + 1;
        }
        finalAdjustments(sheet, headerValues.size());
    }

    private void createEmergencyUsersSheet(HSSFWorkbook workbook, List<HSSFCellStyle> stringStyles,
                                           List<EmergencyUser> emergencyUsers) {

        HSSFSheet sheet = workbook.createSheet(getMessage("security.emergency.users.title"));

        //creating header row
        List<String> headerValues = new ArrayList<String>();
        headerValues.add(getMessage("user.name"));
        headerValues.add(getMessage("user.email"));
        createHeaderRow(workbook, sheet, headerValues);

        int rowNumber = 1;
        createRows(sheet, rowNumber, rowNumber + emergencyUsers.size());
        for (int i = 0; i < emergencyUsers.size(); i++) {
            int styleIndex = i % 2;

            createMergedCell(sheet, rowNumber, rowNumber, 0, stringStyles.get(styleIndex),
                    emergencyUsers.get(i).getName());
            createMergedCell(sheet, rowNumber, rowNumber, 1, stringStyles.get(styleIndex),
                    emergencyUsers.get(i).getEmail());
            rowNumber++;
        }
        finalAdjustments(sheet, headerValues.size());
    }

    private void createHeaderRow(HSSFWorkbook workbook, HSSFSheet sheet, List<String> values) {
        //creating bold, white font
        HSSFFont font = workbook.createFont();
        //old fix  set color
        //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //new fix
        font.setBold(true);
        //old fix
        //font.setColor(HSSFColor.WHITE.index);
        //new fix
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        font.setFontHeightInPoints((short) 12);
        //creating cell style
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        //old fix
        //style.setFillForegroundColor(HSSFColor.DARK_BLUE.index);
        //new fix
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        //old fix
        //style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //new fix
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //old fix
        //style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //new fix
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        HSSFRow row = sheet.createRow(0);
        row.setHeightInPoints((short) 30);
        for (int i = 0; i < values.size(); i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(new HSSFRichTextString(values.get(i)));
            cell.setCellStyle(style);
        }
    }

    /**
     * Creates a custom color palette for this workbook with the same colors as the application.
     *
     * @param workbook The workbook.
     */
    private void createCustomColours(HSSFWorkbook workbook) {
        HSSFPalette palette = workbook.getCustomPalette();
        //old fix
        //palette.setColorAtIndex(HSSFColor.BLUE.index, (byte) 5, (byte) 99, (byte) 193);
        //new fix
        palette.setColorAtIndex(HSSFColor.HSSFColorPredefined.BLUE.getIndex(), (byte) 5, (byte) 99, (byte) 193);
        //old fix
        //palette.setColorAtIndex(HSSFColor.DARK_BLUE.index, (byte) 55, (byte) 87, (byte) 108);
        //new fix
        palette.setColorAtIndex(HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex(), (byte) 55, (byte) 87, (byte) 108);
        //old fix
        //palette.setColorAtIndex(HSSFColor.LIGHT_BLUE.index, (byte) 223, (byte) 234, (byte) 255);
        //new fix
        palette.setColorAtIndex(HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex(), (byte) 223, (byte) 234, (byte) 255);
    }

    /**
     * Creates the styles for the hyperlink cells of this workbook. Several styles are created, differing only on the foreground color.
     *
     * @param workbook The workbook.
     * @return The list os styles created.
     */
    private List<HSSFCellStyle> createHyperlinkStyles(HSSFWorkbook workbook) {
        List<HSSFCellStyle> styles = new ArrayList<HSSFCellStyle>();
        //old fix
        //styles.add(createHyperlinkCellStyle(workbook, HSSFColor.WHITE.index));
        //new fix
        styles.add(createHyperlinkCellStyle(workbook, HSSFColor.HSSFColorPredefined.WHITE.getIndex()));
        //old fix
        //styles.add(createHyperlinkCellStyle(workbook, HSSFColor.LIGHT_BLUE.index));
        //new fix
        styles.add(createHyperlinkCellStyle(workbook, HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex()));
        return styles;
    }

    /**
     * Creates the styles for the string cells of this workbook. Several styles are created, differing only on the foreground color.
     *
     * @param workbook The workbook.
     * @return The list os styles created.
     */
    private List<HSSFCellStyle> createStringStyles(HSSFWorkbook workbook) {
        List<HSSFCellStyle> styles = new ArrayList<HSSFCellStyle>();
        //old fix
        //styles.add(createStringCellStyle(workbook, HSSFColor.WHITE.index));
        //new fix
        styles.add(createStringCellStyle(workbook, HSSFColor.HSSFColorPredefined.WHITE.getIndex()));
        //old fix
        //styles.add(createStringCellStyle(workbook, HSSFColor.LIGHT_BLUE.index));
        //new fix
        styles.add(createStringCellStyle(workbook, HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex()));
        return styles;
    }

    /**
     * Creates the styles for the date cells of this workbook. Several styles are created, differing only on the foreground color.
     *
     * @param workbook The workbook.
     * @return The list os styles created.
     */
    private List<HSSFCellStyle> createDateStyles(HSSFWorkbook workbook) {
        List<HSSFCellStyle> styles = new ArrayList<HSSFCellStyle>();
        //old fix
        //styles.add(createDateCellStyle(workbook, HSSFColor.WHITE.index));
        //new fix
        styles.add(createDateCellStyle(workbook, HSSFColor.HSSFColorPredefined.WHITE.getIndex()));
        //old fix
        //styles.add(createDateCellStyle(workbook, HSSFColor.LIGHT_BLUE.index));
        //new fix
        styles.add(createDateCellStyle(workbook, HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex()));
        return styles;
    }

    /**
     * Creates a style for the hyperlink cells of this workbook with the given color.
     *
     * @param workbook The workbook.
     * @return The style created.
     */
    private HSSFCellStyle createHyperlinkCellStyle(HSSFWorkbook workbook, short color) {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setUnderline(HSSFFont.U_SINGLE);
        //old fix
        //font.setColor(HSSFColor.BLUE.index);
        //new fix
        font.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        //old fix
        //style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //new fix
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setFillForegroundColor(color);
        //old fix
        //style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //new fix
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * Creates a style for the string cells of this workbook with the given color.
     *
     * @param workbook The workbook.
     * @return The style created.
     */
    private HSSFCellStyle createStringCellStyle(HSSFWorkbook workbook, short color) {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        //old fix
        //style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //new fix
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setWrapText(false);
        style.setFillForegroundColor(color);
        //old fix
        //style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //new fix
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * Creates a style for the date cells of this workbook with the given color.
     *
     * @param workbook The workbook.
     * @return The style created.
     */
    private HSSFCellStyle createDateCellStyle(HSSFWorkbook workbook, short color) {
        HSSFDataFormat format = workbook.createDataFormat();
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        //old fix
        //style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        //new fix
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setDataFormat(format.getFormat(Configuration.getInstance().getDatePattern()));
        style.setFillForegroundColor(color);
        //old fix
        //style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //new fix
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * Calculates the number of rows needed for the documents column.
     *
     * @param documents         The list of documents to insert.
     * @param correctiveActions The list of corrective actions.
     * @param isClosed          The record status.
     * @param hasChatMessages   Indicates if this record has chat messages.
     * @param hasReport         Indicates if this record has a main report.
     * @return The number of rows needed. The minimum number returned is 1.
     */
    private int getNumberOfRowsForDocuments(List<Document> documents, List<CorrectiveAction> correctiveActions,
                                            boolean isClosed, boolean hasChatMessages, boolean hasReport) {
        int rowsToFill = 0;
        if (documents != null) {
            rowsToFill += documents.size();
        }
        //when the record is closed, the system generated reports will be added to the documents
        //this includes the chat pdf report and the related corrective actions reports
        if (isClosed) {
            int numberReports = 0;
            if (hasChatMessages) {
                numberReports++;
            }
            if (hasReport) {
                numberReports++;
            }
            rowsToFill += numberReports;
            if (correctiveActions != null) {
                rowsToFill += correctiveActions.size();
            }
        }
        return rowsToFill > 0 ? rowsToFill : 1;
    }

    /**
     * Creates rows in this sheet.
     *
     * @param sheet The sheet.
     * @param first The number of the first row to create.
     * @param last  The number of the last row to create.
     */
    private void createRows(HSSFSheet sheet, int first, int last) {
        for (int i = first; i <= last; i++) {
            sheet.createRow(i).setHeightInPoints((short) 20);
        }
    }

    /**
     * Creates a merged region of cells with the given style and value.
     *
     * @param sheet        The sheet.
     * @param firstRow     Index of the first row of the region.
     * @param lastRow      Index of the last row of the region.
     * @param columnNumber Index of the column.
     * @param style        The style to apply.
     * @param value        The value to insert in this merged cell.
     */
    private void createMergedCell(HSSFSheet sheet, int firstRow, int lastRow, int columnNumber,
                                  HSSFCellStyle style, String value) {
        if (value != null) {
            HSSFRow row = sheet.getRow(firstRow);
            HSSFCell cell = row.createCell(columnNumber);
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, columnNumber, columnNumber));
            cell.setCellStyle(style);
            //old fix
            //cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            //new fix
            cell.setCellType(CellType.STRING);
            cell.setCellValue(new HSSFRichTextString(value));
        } else {
            createEmptyCell(sheet, firstRow, lastRow, columnNumber, style);
        }
    }

    /**
     * Creates a merged region of cells with the given style and value.
     *
     * @param sheet        The sheet.
     * @param firstRow     Index of the first row of the region.
     * @param lastRow      Index of the last row of the region.
     * @param columnNumber Index of the column.
     * @param style        The style to apply.
     * @param value        The value to insert in this merged cell.
     */
    private void createMergedCell(HSSFSheet sheet, int firstRow, int lastRow, int columnNumber,
                                  HSSFCellStyle style, Date value) {
        if (value != null) {
            HSSFRow row = sheet.getRow(firstRow);
            HSSFCell cell = row.createCell(columnNumber);
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, columnNumber, columnNumber));
            cell.setCellStyle(style);
            //old fix
            //cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            //new fix
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(value);
        } else {
            createEmptyCell(sheet, firstRow, lastRow, columnNumber, style);
        }
    }

    /**
     * Creates a cell with a hyperlink formula.
     *
     * @param sheet        The sheet.
     * @param rowNumber    Index of the row.
     * @param columnNumber Index of the column.
     * @param style        The style to apply.
     * @param displayName  The value to display in the cell.
     * @param documentPath The link location.
     */
    private void createHyperlinkCell(HSSFSheet sheet, int rowNumber, int columnNumber, HSSFCellStyle style,
                                     String displayName, String documentPath) {
        HSSFRow row = sheet.getRow(rowNumber);
        HSSFCell cell = row.createCell(columnNumber);
        //old fix
        //cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
        //new fix
        cell.setCellType(CellType.FORMULA);
        cell.setCellFormula("HYPERLINK(\"" + documentPath + "\",\"" + displayName + "\")");
        cell.setCellStyle(style);
    }

    /**
     * Creates a merged region of empty cells with the given style.
     *
     * @param sheet        The sheet.
     * @param firstRow     Index of the first row of the region.
     * @param lastRow      Index of the last row of the region.
     * @param columnNumber Index of the column.
     * @param style        The style to apply.
     */
    private void createEmptyCell(HSSFSheet sheet, int firstRow, int lastRow, int columnNumber, HSSFCellStyle style) {
        HSSFRow row = sheet.getRow(firstRow);
        HSSFCell cell = row.createCell(columnNumber);
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, columnNumber, columnNumber));
        cell.setCellStyle(style);
        //old fix
        //cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
        //new fix
        cell.setCellType(CellType.BLANK);
    }

    /**
     * Creates a merged region of cells with the references (codes) of the given corrective actions.
     * The several references will be separated by line breaks.
     *
     * @param sheet             The sheet.
     * @param firstRow          Index of the first row of the region.
     * @param lastRow           Index of the last row of the region.
     * @param columnNumber      Index of the column.
     * @param style             The style to apply.
     * @param correctiveActions The list of corrective actions.
     */
    private void createCorrectiveActionsCell(HSSFSheet sheet, int firstRow, int lastRow, int columnNumber,
                                             HSSFCellStyle style, List<CorrectiveAction> correctiveActions) {
        String value = "";
        for (CorrectiveAction correctiveAction : correctiveActions) {
            if (!value.isEmpty()) {
                value += "\n";
            }
            value += correctiveAction.getCode();
        }
        createMergedCell(sheet, firstRow, lastRow, columnNumber, style, value);
    }

    /**
     * Creates the documents cells. Creates one cell for each document and one cell for each report if the record is closed.
     *
     * @param sheet             The sheet.
     * @param rowNumber         Index of the row.
     * @param columnNumber      Index of the column.
     * @param style             The style to apply.
     * @param documents         The list of documents to insert.
     * @param correctiveActions The list of corrective actions related to this record.
     * @param code              The code of the record. This is used for the report name.
     * @param isClosed          The record status.
     * @param hasReport         Indicates if this record has a main report.
     * @param hasChatMessages   Indicates if this record has chat messages.
     */
    private void createDocumentsCells(HSSFSheet sheet, int rowNumber, int columnNumber, HSSFCellStyle style,
                                      List<Document> documents, List<CorrectiveAction> correctiveActions,
                                      String code, boolean isClosed, boolean hasReport, boolean hasChatMessages) {
        //documents cells
        String folder = getMessage("security.documents.folder") + "\\";
        int i = 0;
        if (documents != null) {
            while (i < documents.size()) {
                createHyperlinkCell(sheet, rowNumber + i, columnNumber, style, documents.get(i).getDisplayName(),
                        folder + "Document" + documents.get(i).getId() + "-" + documents.get(i).getName());
                i++;
            }
        }
        //the reports are included with the documents when the record is closed
        if (isClosed) {
            //main report
            if (hasReport) {
                createHyperlinkCell(sheet, rowNumber + i, columnNumber, style, code + ".pdf",
                        folder + code + ".pdf");
                i++;
            }
            //chat pdf report
            if (hasChatMessages) {
                createHyperlinkCell(sheet, rowNumber + i, columnNumber, style, getMessage("security.pdf.filename"),
                        folder + code + "-" + getMessage("security.pdf.filename"));
                i++;
            }
            //related corrective actions reports
            if (correctiveActions != null) {
                for (CorrectiveAction correctiveAction : correctiveActions) {
                    if (correctiveAction.getClosed()) {
                        createHyperlinkCell(sheet, rowNumber + i, columnNumber, style,
                                correctiveAction.getCode() + ".pdf",
                                "..\\" + getMessage("security.submodule.correctiveaction") + "\\" +
                                        folder + correctiveAction.getCode() + ".pdf");
                        i++;
                    }
                }
            }
        }
        //create empty cell if no cell was created
        if (i == 0) {
            createEmptyCell(sheet, rowNumber, rowNumber, columnNumber, style);
        }
    }

    /**
     * Creates the final adjustments for this sheet.
     *
     * @param sheet         The sheet.
     * @param numberColumns The total number of columns of this sheet.
     */
    private void finalAdjustments(HSSFSheet sheet, int numberColumns) {
        //adjusting the column width to fit the contents
        for (int i = 0; i < numberColumns; i++) {
            sheet.autoSizeColumn((short) i, true);
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
