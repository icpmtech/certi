package com.criticalsoftware.certitools.presentation.util.export.sm;

import com.criticalsoftware.certitools.business.exception.PDFException;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Activity;
import com.criticalsoftware.certitools.entities.sm.Anomaly;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.EmergencyAction;
import com.criticalsoftware.certitools.entities.sm.EmergencyUser;
import com.criticalsoftware.certitools.entities.sm.Maintenance;
import com.criticalsoftware.certitools.entities.sm.Risk;
import com.criticalsoftware.certitools.entities.sm.SecurityImpact;
import com.criticalsoftware.certitools.entities.sm.SecurityImpactWork;
import com.criticalsoftware.certitools.entities.sm.enums.AnomalyType;
import com.criticalsoftware.certitools.entities.sm.enums.WorkType;
import com.criticalsoftware.certitools.util.Configuration;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import net.sourceforge.stripes.action.ActionBeanContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * ReportPdf
 * Class that creates pdf documents for security records.
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
public class ReportPdf {

    private ResourceBundle resources;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dateHourFormat;
    private String logoPath;
    private String checkedPath;
    private String uncheckedPath;

    public ReportPdf(ActionBeanContext context) {
        this.resources = ResourceBundle.getBundle("StripesResources", context.getLocale());
        this.dateFormat = new SimpleDateFormat(Configuration.getInstance().getDatePattern(), context.getLocale());
        this.dateHourFormat = new SimpleDateFormat(Configuration.getInstance().getDateHourPattern(), context.getLocale());
        String imagesDir = context.getServletContext().getRealPath("/images/") + System.getProperty("file.separator");
        this.logoPath = imagesDir + "logopdf.png";
        this.checkedPath = imagesDir + "ic_check_box_black_24dp.png";
        this.uncheckedPath = imagesDir + "ic_check_box_outline_blank_black_24dp.png";
    }

    public ByteArrayOutputStream generateCorrectiveActionPDF(CorrectiveAction correctiveAction)
            throws PDFException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            //Set the document properties
            setProperties(document, correctiveAction.getCode(), correctiveAction.getCode());
            //Set the print writer
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPdfVersion(PdfWriter.VERSION_1_7);
            Image img;
            try {
                img = Image.getInstance(logoPath);
            } catch (BadElementException e) {
                throw new PDFException("Error creating header images", e);
            } catch (IOException e) {
                throw new PDFException("Error creating header images", e);
            }
            writer.setPageEvent(new HeaderFooterPageEvent(img));

            document.open();

            //build document
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSplitLate(false);

            //add header, entity (company), installation (contract) and reference (code) rows
            addTopRows(table, getMessage("security.pdf.correctiveAction.title"),
                    correctiveAction.getContract().getCompany().getName(),
                    correctiveAction.getContract().getContractDesignation(), correctiveAction.getCode());

            //related records row
            String relatedEntity = null;
            if (correctiveAction.getActivity() != null) {
                relatedEntity = correctiveAction.getActivity().getCode();
            } else if (correctiveAction.getAnomaly() != null) {
                relatedEntity = correctiveAction.getAnomaly().getCode();
            } else if (correctiveAction.getSecurityImpactWork() != null) {
                relatedEntity = correctiveAction.getSecurityImpactWork().getCode();
            } else if (correctiveAction.getMaintenance() != null) {
                relatedEntity = correctiveAction.getMaintenance().getCode();
            }
            table.addCell(PdfUtils.getInstance().buildTableCell(2, 0.9f, 25, false,
                    new PdfCellValue(getMessage("security.pdf.relatedEntities"), relatedEntity)));

            //execution responsible row
            table.addCell(PdfUtils.getInstance().buildTableCell(1, 1f, 25, false,
                    new PdfCellValue(getMessage("security.pdf.executionResponsible"),
                            correctiveAction.getExecutionResponsible(), true)));
            table.addCell(PdfUtils.getInstance().buildTableCell(1, 1f, 25, false,
                    new PdfCellValue[]{
                            new PdfCellValue(getMessage("security.pdf.startDate"),
                                    dateFormat.format(correctiveAction.getStartDate())),
                            new PdfCellValue(getMessage("security.pdf.duration"), correctiveAction.getDuration())
                    }));

            //description row
            table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 100, false,
                    new PdfCellValue(getMessage("security.pdf.description"), correctiveAction.getDescription(), true)));

            //observations row
            table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 100, false,
                    new PdfCellValue(getMessage("security.pdf.notes"), correctiveAction.getNotes(), true)));

            //documents row
            String[] docs = new String[correctiveAction.getDocuments().size()];
            for (int i = 0; i < docs.length; i++) {
                docs[i] = correctiveAction.getDocuments().get(i).getDisplayName();
            }

            table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 25, false,
                    getMessage("security.pdf.documents"), docs));

            //closed date row
            addClosedRow(table, correctiveAction.getClosed(), correctiveAction.getClosedDate(),
                    correctiveAction.getChangedBy().getName());

            document.add(table);
            document.close();
            writer.close();

        } catch (DocumentException e) {
            throw new PDFException("Error while creating PDF " + e.getMessage(), e);
        }
        return baos;
    }

    public ByteArrayOutputStream generateAnomalyPDF(Anomaly anomaly, java.util.List<SecurityImpact> securityImpacts)
            throws PDFException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            //Set the document properties
            setProperties(document, anomaly.getCode(), anomaly.getCode());
            //Set the print writer
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPdfVersion(PdfWriter.VERSION_1_7);
            Image img;
            Image checked;
            Image unchecked;
            try {
                img = Image.getInstance(logoPath);
                checked = Image.getInstance(checkedPath);
                unchecked = Image.getInstance(uncheckedPath);
            } catch (BadElementException e) {
                throw new PDFException("Error creating pdf images", e);
            } catch (IOException e) {
                throw new PDFException("Error creating pdf images", e);
            }
            writer.setPageEvent(new HeaderFooterPageEvent(img));

            document.open();

            //build document
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSplitLate(false);

            //add header, entity (company), installation (contract) and reference (code) rows
            addTopRows(table, (anomaly.getAnomalyType() == AnomalyType.ANOMALY ?
                            getMessage("security.pdf.anomaly.title") : getMessage("security.pdf.occurrence.title")),
                    anomaly.getContract().getCompany().getName(),
                    anomaly.getContract().getContractDesignation(), anomaly.getCode());

            //start date row
            if (anomaly.getAnomalyType() == AnomalyType.ANOMALY) {
                table.addCell(PdfUtils.getInstance().buildTableCell(1, 0.95f, 25, false,
                        new PdfCellValue(getMessage("security.pdf.whoDetected"), anomaly.getWhoDetected())));
                table.addCell(PdfUtils.getInstance().buildTableCell(1, 0.95f, 25, false,
                        new PdfCellValue(getMessage("security.pdf.startDate"),
                                dateFormat.format(anomaly.getDatetime()))));
            } else {
                table.addCell(PdfUtils.getInstance().buildTableCell(2, 0.95f, 25, false,
                        new PdfCellValue(getMessage("security.pdf.startDate"),
                                dateFormat.format(anomaly.getDatetime()))));
            }

            //description row
            table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 100, false,
                    new PdfCellValue(getMessage("security.pdf.description"), anomaly.getDescription(), true)));

            //internal and external actors row
            if (anomaly.getAnomalyType() == AnomalyType.OCCURRENCE) {
                table.addCell(PdfUtils.getInstance().buildTableCell(1, 1f, 25, false,
                        new PdfCellValue(getMessage("security.pdf.internalActors"),
                                anomaly.getInternalActors(), true)));
                table.addCell(PdfUtils.getInstance().buildTableCell(1, 1f, 25, false,
                        new PdfCellValue(getMessage("security.pdf.externalActors"),
                                anomaly.getExternalActors(), true)));
            }

            //security impact row
            PdfCheckListValue[] impacts = new PdfCheckListValue[securityImpacts.size()];
            for (int i = 0; i < impacts.length; i++) {
                impacts[i] = new PdfCheckListValue(securityImpacts.get(i).getName(),
                        securityImpacts.get(i).getName().equals(anomaly.getSecurityImpact().getName()));
            }
            table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 25, false, checked, unchecked,
                    getMessage("security.pdf.securityImpact"), impacts));

            //qualified entity row
            if (anomaly.getAnomalyType() == AnomalyType.OCCURRENCE) {
                table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 25, false,
                        new PdfCellValue(getMessage("security.pdf.qualifiedEntity"),
                                anomaly.getQualifiedEntity(), true)));
            }

            //documents row
            String[] docs = new String[anomaly.getDocuments().size()];
            for (int i = 0; i < docs.length; i++) {
                docs[i] = anomaly.getDocuments().get(i).getDisplayName();
            }
            table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 25, false,
                    getMessage("security.pdf.documents"), docs));

            //corrective actions row
            String[] actions = new String[anomaly.getCorrectiveActions().size()];
            for (int i = 0; i < actions.length; i++) {
                actions[i] = anomaly.getCorrectiveActions().get(i).getCode();
            }
            table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 25, false,
                    getMessage("security.pdf.correctiveActions"), actions));


            //closed date row
            addClosedRow(table, anomaly.getClosed(), anomaly.getClosedDate(),
                    anomaly.getChangedBy().getName());

            document.add(table);
            document.close();
            writer.close();

        } catch (DocumentException e) {
            throw new PDFException("Error while creating PDF " + e.getMessage(), e);
        }
        return baos;
    }

    public ByteArrayOutputStream generateSecurityImpactWorkPDF(SecurityImpactWork securityImpactWork,
                                                               java.util.List<SecurityImpact> securityImpacts,
                                                               java.util.List<Risk> risks) throws PDFException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            //Set the document properties
            setProperties(document, securityImpactWork.getCode(), securityImpactWork.getCode());
            //Set the print writer
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPdfVersion(PdfWriter.VERSION_1_7);
            Image img;
            Image checked;
            Image unchecked;
            try {
                img = Image.getInstance(logoPath);
                checked = Image.getInstance(checkedPath);
                unchecked = Image.getInstance(uncheckedPath);
            } catch (BadElementException e) {
                throw new PDFException("Error creating pdf images", e);
            } catch (IOException e) {
                throw new PDFException("Error creating pdf images", e);
            }
            writer.setPageEvent(new HeaderFooterPageEvent(img));

            document.open();

            //build document
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSplitLate(false);

            //add header, entity (company), installation (contract) and reference (code) rows
            addTopRows(table, (securityImpactWork.getWorkType() == WorkType.MODIFICATION ?
                            getMessage("security.pdf.modification.title") :
                            getMessage("security.pdf.workAuthorization.title")),
                    securityImpactWork.getContract().getCompany().getName(),
                    securityImpactWork.getContract().getContractDesignation(), securityImpactWork.getCode());

            //start date row
            table.addCell(PdfUtils.getInstance().buildTableCell(1, 1f, 25, false,
                    new PdfCellValue(getMessage("security.pdf.responsible"), securityImpactWork.getResponsible())));
            if (securityImpactWork.getWorkType() == WorkType.MODIFICATION) {
                table.addCell(PdfUtils.getInstance().buildTableCell(1, 1f, 25, false,
                        new PdfCellValue(getMessage("security.pdf.startDate"),
                                dateFormat.format(securityImpactWork.getStartDate()))));

                //description row
                table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 100, false,
                        new PdfCellValue(getMessage("security.pdf.description"),
                                securityImpactWork.getDescription(), true)));

                //security impact row
                PdfCheckListValue[] impacts = new PdfCheckListValue[securityImpacts.size()];
                for (int i = 0; i < impacts.length; i++) {
                    impacts[i] = new PdfCheckListValue(securityImpacts.get(i).getName(),
                            securityImpacts.get(i).getName().equals(securityImpactWork.getSecurityImpact().getName()));
                }
                table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 25, false, checked, unchecked,
                        getMessage("security.pdf.securityImpact"), impacts));

                //qualified entity row
                table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 25, false,
                        new PdfCellValue(getMessage("security.pdf.qualifiedEntity"),
                                securityImpactWork.getQualifiedEntity(), true)));

                //documents row
                String[] docs = new String[securityImpactWork.getDocuments().size()];
                for (int i = 0; i < docs.length; i++) {
                    docs[i] = securityImpactWork.getDocuments().get(i).getDisplayName();
                }
                table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 25, false,
                        getMessage("security.pdf.documents"), docs));

                //corrective actions row
                String[] actions = new String[securityImpactWork.getCorrectiveActions().size()];
                for (int i = 0; i < actions.length; i++) {
                    actions[i] = securityImpactWork.getCorrectiveActions().get(i).getCode();
                }
                table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 25, false,
                        getMessage("security.pdf.correctiveActions"), actions));

                //closed date row
                addClosedRow(table, securityImpactWork.getClosed(), securityImpactWork.getClosedDate(),
                        securityImpactWork.getChangedBy().getName());

                document.add(table);

            } else {
                table.addCell(PdfUtils.getInstance().buildTableCell(1, 1f, 25, false,
                        new PdfCellValue[]{
                                new PdfCellValue(getMessage("security.pdf.startDate"),
                                        dateFormat.format(securityImpactWork.getStartDate())),
                                new PdfCellValue(getMessage("security.pdf.duration"), securityImpactWork.getDuration())
                        }));

                //description row
                table.addCell(PdfUtils.getInstance().buildTableCell(2, 0.9f, 20, false, true,
                        getMessage("security.pdf.workAuthorizationDescription") + ":"));
                table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 50, false,
                        new PdfCellValue(null, securityImpactWork.getDescription())));

                //risks row
                table.addCell(PdfUtils.getInstance().buildTableCell(2, 0.9f, 20, false, true,
                        getMessage("security.pdf.workAuthorizationRisks") + ":"));
                PdfCheckListValue[] selectedRisks = new PdfCheckListValue[risks.size()];
                for (int i = 0; i < selectedRisks.length; i++) {
                    selectedRisks[i] = new PdfCheckListValue(risks.get(i).getName(),
                            securityImpactWork.getRisks().contains(risks.get(i)));
                }
                table.addCell(PdfUtils.getInstance().buildTableCell(1, 1f, 25, false, checked, unchecked,
                        getMessage("security.pdf.risks"), selectedRisks));
                String[] measures = new String[securityImpactWork.getRisks().size()];
                for (int i = 0; i < measures.length; i++) {
                    measures[i] = securityImpactWork.getRisks().get(i).getPreventiveMeasures();
                }
                table.addCell(PdfUtils.getInstance().buildTableCell(1, 1f, 25, false,
                        getMessage("security.pdf.riskPreventiveMeasures"), measures));
                document.add(table);

                //other rows (authorization validity)
                document.add(buildWorkAuthorizationValidityTable());
            }

            document.close();
            writer.close();

        } catch (DocumentException e) {
            throw new PDFException("Error while creating PDF " + e.getMessage(), e);
        }
        return baos;
    }

    public ByteArrayOutputStream generateEmergencyActionPDF(EmergencyAction emergencyAction,
                                                            java.util.List<User> users,
                                                            java.util.List<EmergencyUser> emergencyUsers)
            throws PDFException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            //set the document properties
            setProperties(document, emergencyAction.getCode(), emergencyAction.getCode());
            //set the print writer
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPdfVersion(PdfWriter.VERSION_1_7);
            Image img;
            try {
                img = Image.getInstance(logoPath);
            } catch (BadElementException e) {
                throw new PDFException("Error creating header images", e);
            } catch (IOException e) {
                throw new PDFException("Error creating header images", e);
            }
            writer.setPageEvent(new HeaderFooterPageEvent(img));

            document.open();

            //build document
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSplitLate(false);

            //add header, entity (company), installation (contract) and reference (code) rows
            addTopRows(table, getMessage("security.pdf.emergencyAction.title"),
                    emergencyAction.getContract().getCompany().getName(),
                    emergencyAction.getContract().getContractDesignation(), emergencyAction.getCode());

            //participants row
            StringBuilder participants = new StringBuilder();
            for (int i = 0; i < users.size(); i++) {
                participants.append(users.get(i).getName());
                if (i != users.size() - 1) {
                    participants.append(", ");
                }
            }
            for (EmergencyUser emergencyUser : emergencyUsers) {
                participants.append(", ");
                participants.append(emergencyUser.getName());
            }
            table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 25, false,
                    new PdfCellValue(getMessage("security.pdf.participantsList"), participants.toString(), true)));

            //start date row
            table.addCell(PdfUtils.getInstance().buildTableCell(1, 0.9f, 25, false,
                    new PdfCellValue(getMessage("security.pdf.startDate"),
                            dateFormat.format(emergencyAction.getStartDate()))));
            table.addCell(PdfUtils.getInstance().buildTableCell(1, 0.9f, 25, false,
                    new PdfCellValue(getMessage("security.pdf.origin"), emergencyAction.getOrigin())));

            //description row
            table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 100, false,
                    new PdfCellValue(getMessage("security.pdf.description"), emergencyAction.getDescription(), true)));

            //chat messages row
            table.addCell(PdfUtils.getInstance().buildTableCell(2, 0.9f, 20, false, true,
                    getMessage("security.pdf.actionsLog") + ":"));
            table.addCell(buildChatMessagesCell(emergencyAction.getChatMessages()));

            //closed date row
            table.addCell(PdfUtils.getInstance().buildTableCell(1, 0.95f, 25, false,
                    new PdfCellValue(getMessage("security.pdf.closedDate"),
                            dateFormat.format(emergencyAction.getClosedDate()), true)));
            table.addCell(PdfUtils.getInstance().buildTableSignatureCell(1, 0.95f, 25,
                    emergencyAction.getChangedBy().getName(), "(" + getMessage("security.pdf.closedBy") + ")"));

            document.add(table);

            document.close();
            writer.close();

        } catch (DocumentException e) {
            throw new PDFException("Error while creating PDF " + e.getMessage(), e);
        }
        return baos;
    }

    public ByteArrayOutputStream generateChatPDF(Activity activity) throws PDFException {
        return generateChatPDF(activity.getContract().getCompany().getName(),
                activity.getContract().getContractDesignation(), activity.getCode(), activity.getName(),
                activity.getClosedDate(), activity.getChatMessages());
    }

    public ByteArrayOutputStream generateChatPDF(CorrectiveAction correctiveAction) throws PDFException {
        return generateChatPDF(correctiveAction.getContract().getCompany().getName(),
                correctiveAction.getContract().getContractDesignation(), correctiveAction.getCode(), null,
                correctiveAction.getClosedDate(), correctiveAction.getChatMessages());
    }

    public ByteArrayOutputStream generateChatPDF(Anomaly anomaly) throws PDFException {
        return generateChatPDF(anomaly.getContract().getCompany().getName(),
                anomaly.getContract().getContractDesignation(), anomaly.getCode(), null,
                anomaly.getClosedDate(), anomaly.getChatMessages());
    }

    public ByteArrayOutputStream generateChatPDF(Maintenance maintenance) throws PDFException {
        return generateChatPDF(maintenance.getContract().getCompany().getName(),
                maintenance.getContract().getContractDesignation(), maintenance.getCode(), null,
                maintenance.getClosedDate(), maintenance.getChatMessages());
    }

    public ByteArrayOutputStream generateChatPDF(SecurityImpactWork securityImpactWork) throws PDFException {
        return generateChatPDF(securityImpactWork.getContract().getCompany().getName(),
                securityImpactWork.getContract().getContractDesignation(), securityImpactWork.getCode(), null,
                securityImpactWork.getClosedDate(), securityImpactWork.getChatMessages());
    }

    private ByteArrayOutputStream generateChatPDF(String company, String contract, String code,
                                                  String name, Date closedDate, java.util.List<Chat> chatMessages)
            throws PDFException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            //set the document properties
            setProperties(document, code, code);
            //set the print writer
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPdfVersion(PdfWriter.VERSION_1_7);
            Image img;
            try {
                img = Image.getInstance(logoPath);
            } catch (BadElementException e) {
                throw new PDFException("Error creating header images", e);
            } catch (IOException e) {
                throw new PDFException("Error creating header images", e);
            }
            writer.setPageEvent(new HeaderFooterPageEvent(img));

            document.open();

            //build document
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSplitLate(false);

            //add header, entity (company), installation (contract) and reference (code) rows
            addTopRows(table, getMessage("security.pdf.chat.title"), company, contract, code +
                    (name != null ? " - " + name : ""));

            //chat messages row
            table.addCell(buildChatMessagesCell(chatMessages));

            //closed date row
            table.addCell(PdfUtils.getInstance().buildTableCell(2, 0.95f, 25, false,
                    new PdfCellValue(getMessage("security.pdf.closedDate"), dateFormat.format(closedDate), true)));

            document.add(table);

            document.close();
            writer.close();

        } catch (DocumentException e) {
            throw new PDFException("Error while creating PDF " + e.getMessage(), e);
        }
        return baos;
    }

    /**
     * Builds a table cell with the given chat messages.
     *
     * @param chatMessages The list of chat messages.
     * @return The table cell.
     */
    private PdfPCell buildChatMessagesCell(java.util.List<Chat> chatMessages) {
        PdfPCell cell;
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        for (Chat chat : chatMessages) {
            Paragraph paragraph = new Paragraph();
            Chunk chunk = new Chunk((chat.getUser() != null ? chat.getUser().getName() : chat.getEmergencyUser().getName()),
                    PdfUtils.getInstance().getFontNormalSmall());
            paragraph.add(chunk);
            chunk = new Chunk(" (" + dateHourFormat.format(chat.getDatetime()) + ")", PdfUtils.getInstance().getFontGrayNormalSmall());
            paragraph.add(chunk);
            paragraph.setSpacingAfter(6);
            cell = new PdfPCell();
            cell.addElement(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            paragraph = new Paragraph();
            chunk = new Chunk(chat.getMessage(), PdfUtils.getInstance().getFontNormalSmall());
            paragraph.add(chunk);
            paragraph.setSpacingAfter(6);
            cell = new PdfPCell();
            cell.addElement(paragraph);
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
        }

        cell = new PdfPCell();
        cell.setColspan(2);
        cell.setMinimumHeight(25);
        cell.addElement(table);
        return cell;
    }

    private PdfPTable buildWorkAuthorizationValidityTable() {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSplitLate(true);
        table.addCell(buildWorkAuthorizationValidityCell(1));
        table.addCell(buildWorkAuthorizationValidityCell(2));
        return table;
    }

    private PdfPCell buildWorkAuthorizationValidityCell(int shiftNumber) {
        PdfPCell cell;
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        if (shiftNumber == 1) {
            //authorization rows
            table.addCell(PdfUtils.getInstance().buildTableCell(6, 0.9f, 20, true, true,
                    getMessage("security.pdf.authorizationValidity") + ":"));
        }

        table.addCell(PdfUtils.getInstance().buildTableCell(3, 1f, 20, true, true,
                shiftNumber + getMessage("security.pdf.shift")));
        table.addCell(PdfUtils.getInstance().buildTableCell(3, 1f, 20, true, false,
                getMessage("security.pdf.from") + " ____:____ " + getMessage("security.pdf.to") + " ____:____ "));

        table.addCell(PdfUtils.getInstance().buildTableCell(2, 0.9f, 20, true, false,
                getMessage("security.pdf.theExecutor") + ":"));
        table.addCell(PdfUtils.getInstance().buildTableCell(2, 0.9f, 20, true, false,
                getMessage("security.pdf.whoMonitorsWork") + ":"));
        table.addCell(PdfUtils.getInstance().buildTableCell(2, 0.9f, 20, true, false,
                getMessage("security.pdf.theResponsible") + ":"));

        table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 20, true, false, null));
        table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 20, true, false, null));
        table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 20, true, false, null));

        table.addCell(PdfUtils.getInstance().buildTableCell(4, 1f, 60, false, true,
                getMessage("security.pdf.detectedAnomalies") + ":"));
        table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 60, true, false,
                getMessage("security.pdf.whoMonitorsWork") + ":"));

        table.addCell(PdfUtils.getInstance().buildTableCell(4, 1f, 60, false, true,
                getMessage("security.pdf.inspectionAfterCompletion") + ":"));
        table.addCell(PdfUtils.getInstance().buildTableCell(2, 1f, 60, true, false,
                getMessage("security.pdf.theResponsible") + ":"));

        cell = new PdfPCell();
        cell.setPadding(0);
        cell.setColspan(2);
        cell.setMinimumHeight(100);
        cell.addElement(table);
        return cell;
    }


    /**
     * Utility method to add top rows common to all pdf reports.
     *
     * @param table    The table to add the rows to.
     * @param title    The table title.
     * @param company  The company name.
     * @param contract The contract designation.
     * @param code     The record reference code.
     */
    private void addTopRows(PdfPTable table, String title, String company, String contract, String code) {
        //header row
        table.addCell(PdfUtils.getInstance().buildTableHeaderRow(getMessage("security.pdf.title"), title));

        //company and contract row
        table.addCell(PdfUtils.getInstance().buildTableCell(1, 0.8f, 25, false,
                new PdfCellValue(getMessage("companies.company"), company)));
        table.addCell(PdfUtils.getInstance().buildTableCell(1, 0.8f, 25, false,
                new PdfCellValue(getMessage("security.pdf.installation"), contract)));

        //record reference row
        table.addCell(PdfUtils.getInstance().buildTableCell(2, 0.9f, 25, true,
                new PdfCellValue(getMessage("security.pdf.reference"), code)));
    }

    /**
     * Utility method to add bottom row common to most pdf reports.
     *
     * @param table      The table to add the row to.
     * @param closed     True if the record is closed (always!).
     * @param closedDate The closed date.
     * @param closedBy   The name of the user who closed the record.
     */
    private void addClosedRow(PdfPTable table, boolean closed, Date closedDate, String closedBy) {
        table.addCell(PdfUtils.getInstance().buildTableCell(1, 0.95f, 25, false,
                new PdfCellValue[]{
                        new PdfCellValue(getMessage("security.pdf.status"),
                                (closed ? getMessage("security.pdf.closed") : getMessage("security.pdf.open"))),
                        new PdfCellValue(getMessage("security.pdf.closedDate"), dateFormat.format(closedDate))
                }));
        table.addCell(PdfUtils.getInstance().buildTableSignatureCell(1, 0.95f, 25, closedBy,
                "(" + getMessage("security.pdf.closedBy") + ")"));
    }

    /**
     * Set the document properties.
     *
     * @param document The document.
     * @param title    The title to add to the document.
     * @param subject  The subject to add to the document.
     */
    private void setProperties(Document document, String title, String subject) {
        //Set the document properties
        document.setPageSize(PageSize.A4);
        document.setMargins(50, 50, 70, 50);

        //Set the document metadata
        document.addTitle(title);
        document.addSubject(subject);
        document.addKeywords(subject);
        document.addCreator(getMessage("application.title"));
        document.addAuthor(getMessage("application.title"));
        document.addHeader("Expires", "0");
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
