/*
 * $Id: Plan2PdfUtil.java,v 1.5 2010/06/30 17:28:35 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/06/30 17:28:35 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util.export;

import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Resource;
import com.criticalsoftware.certitools.entities.jcr.RiskAnalysisElement;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.entities.jcr.Template12MeansResourcesElement;
import com.criticalsoftware.certitools.entities.jcr.Template1Diagram;
import com.criticalsoftware.certitools.entities.jcr.Template3RichText;
import com.criticalsoftware.certitools.entities.jcr.Template4PlanClickable;
import com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement;
import com.criticalsoftware.certitools.entities.jcr.Template6DocumentsElement;
import com.criticalsoftware.certitools.entities.jcr.Template7FAQElement;
import com.criticalsoftware.certitools.entities.jcr.Template8RiskAnalysis;
import com.criticalsoftware.certitools.entities.jcr.Template9RichTextWithAttach;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Section;
import com.lowagie.text.Table;
import com.lowagie.text.html.Markup;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooter;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooterGroup;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utitlity class that contains shared methods
 *
 * @author : lt-rico
 */
@SuppressWarnings("UnusedDeclaration")
public class Plan2PdfUtil {

    private static boolean isPDF;
    private static List<Folder> allPEIFolders;

    private static final Logger LOGGER = Logger.getInstance(Plan2PdfUtil.class);

    public static boolean isPDF() {
        return isPDF;
    }

    public static void setPDF(boolean PDF) {
        isPDF = PDF;
    }

    protected static ByteArrayInputStream findImage(String folderName)
            throws IOException, BadElementException {
        return PlanExportUtil.findImage(allPEIFolders, folderName);
    }

    protected static byte[] getImageData(InputStream data) throws IOException, BadElementException {
        if (data != null) {
            byte[] buf = new byte[data.available()];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            while ((len = data.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            byte imagedata[] = out.toByteArray();
            out.close();

            return imagedata;
        }
        return null;
    }

    private static PdfPCell buildCell(PdfPCell cell, int padding, int columnSpan) {
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(columnSpan);
        cell.setPadding(padding);
        return cell;
    }

    /**
     * Get font smaller
     *
     * @return font element
     */
    // CONFIGURATION PDF
    protected static Font getFontSmaller() {
        Font font = new Font(Font.HELVETICA);
        font.setStyle(Font.NORMAL);
        font.setColor(Color.BLACK);
        font.setSize(8);
        return font;
    }

    /**
     * Get font small
     *
     * @return font element
     */
    protected static Font getFontSmall() {
        Font font = getFontSmaller();
        font.setSize(10);
        return font;
    }

    /**
     * Get font normal
     *
     * @return font element
     */
    protected static Font getFontNormal() {
        Font font = getFontSmaller();
        font.setSize(12);
        return font;
    }

    protected static Font getFontBold() {
        Font font = getFontSmaller();
        font.setStyle(Font.BOLD);
        return font;
    }

    /**
     * Get font big
     *
     * @return font element
     */
    protected static Font getFontBig() {
        Font font = getFontSmaller();
        font.setSize(16);
        return font;
    }

    /**
     * Get font for chapters
     *
     * @return font element
     */
    protected static Font getFontChapter() {
        Font font = getFontSmaller();
        font.setStyle(Font.BOLDITALIC);
        font.setSize(22);
        return font;
    }

    /**
     * Get font for sections
     *
     * @return font element
     */
    protected static Font getFontSection() {
        Font font = getFontSmaller();
        font.setStyle(Font.BOLDITALIC);
        font.setSize(18);
        return font;
    }

    protected static String escapeAndRemoveHMTLTags(String text) {
        //Remove <br> from String
        text = text.replaceAll("<line_break>", "\n");
        //Remove tags from String
        text = text.replaceAll("\\<.*?\\>", "");
        return text;
    }

    /**
     * Creates a cell with a phrase inside that cell
     *
     * @param phrase  the phrase
     * @param padding the cell padding
     * @param colspan the cell colspan
     * @return pdf cell element
     */
    protected static PdfPCell createPhraseCell(Phrase phrase, int padding, int colspan) {
        return buildCell(new PdfPCell(phrase), padding, colspan);
    }

    /**
     * Creates a cell with a phrase inside that cell with a border
     *
     * @param phrase  the phrase
     * @param padding the cell padding
     * @param colspan the cell colspan
     * @return pdf cell element
     */
    protected static PdfPCell createPhraseCellBorder(Phrase phrase, int padding, int colspan) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(colspan);
        cell.setPadding(padding);
        return cell;
    }

    protected static PdfPCell setCellBorder(PdfPCell cell, int padding, int colspan) {
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(colspan);
        cell.setPadding(padding);
        return cell;
    }

    /**
     * Creates a cell with a Image element
     *
     * @param image        the image
     * @param adjustToCell adust image to cell
     * @param padding      the padding
     * @param columnSpan   the colspan
     * @return pdf cell element
     */
    protected static PdfPCell createImageCell(Image image, boolean adjustToCell, int padding, int columnSpan) {
        return buildCell(new PdfPCell(image, adjustToCell), padding, columnSpan);
    }

    protected static PdfPCell createImageCellBorder(Image image, boolean adjustToCell, int padding, int columnSpan) {
        PdfPCell cell = new PdfPCell(image, adjustToCell);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(columnSpan);
        cell.setPadding(padding);
        return cell;
    }

    /**
     * Creates a table
     *
     * @param columnsWidth the columns widths
     * @return pdf table element
     */
    protected static PdfPTable createTable(float[] columnsWidth) {
        return setTableProps(new PdfPTable(columnsWidth));
    }

    /**
     * Creates a table with a certain number of columsn
     *
     * @param numberOfColumns number
     * @return pdf table element
     */
    protected static PdfPTable createTable(int numberOfColumns) {
        return setTableProps(new PdfPTable(numberOfColumns));
    }

    private static PdfPTable setTableProps(PdfPTable table) {
        table.setWidthPercentage(100);
        return table;
    }


    /**
     * Build the document
     *
     * @param writer          pdf writer
     * @param document        the document
     * @param foldersToExport the specific folders to export
     * @param locale          the locale
     * @param isPDF           is PDF export or RTF
     * @throws IOException       when something happens adding content to PDF
     * @throws DocumentException when something happens adding content to PDF
     */
    protected static void buildDocument(PdfWriter writer, Document document, java.util.List<Folder> foldersToExport,
                                        Locale locale, boolean isPDF) throws IOException, DocumentException {
        Chapter chap;
        int i = 0;
        Collections.sort(foldersToExport);

        allPEIFolders = foldersToExport;
        setPDF(isPDF);
        for (Folder folder : foldersToExport) {
            if (folder.getActive().equals(Boolean.TRUE)) {

                if (!isPDF && i > 1) {
                    RtfHeaderFooterGroup footers = new RtfHeaderFooterGroup();
                    Table footer = Plan2Rtf.buildFooter(ResourceBundle.getBundle("StripesResources", locale));
                    footers.setHeaderFooter(new RtfHeaderFooter(footer), RtfHeaderFooter.DISPLAY_ALL_PAGES);
                    document.setFooter(footers);
                }
                chap = new Chapter(new Paragraph(folder.getName(), getFontChapter()), ++i);
                chap.setBookmarkOpen(false);
                chap.add(new Paragraph(Chunk.NEWLINE));

                parseTemplate(writer, chap, folder, locale, isPDF);

                if (folder.getTemplate().getName().equals(Template.Type.TEMPLATE_PLAN_CLICKABLE.getName())) {
                    createSubContent(writer, chap, folder, 2, locale, false, isPDF);
                } else {
                    createSubContent(writer, chap, folder, 2, locale, true, isPDF);
                }
                document.add(chap);
            }
        }
    }

    private static void createSubContent(PdfWriter writer, Object o, Folder folder, int depth, Locale locale,
                                         boolean toProcess, boolean isPDF)
            throws DocumentException, IOException {
        Collections.sort(folder.getFolders());
        for (Folder f : folder.getFolders()) {
            if (toProcess && f.getActive().equals(Boolean.TRUE) &&
                    !f.getTemplate().getName().equals(Template.Type.TEMPLATE_RESOURCE.getName()) &&
                    !f.getTemplate().getName().equals(Template.Type.TEMPLATE_CONTACTS_ELEMENT.getName()) &&
                    !f.getTemplate().getName().equals(Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName()) &&
                    !f.getTemplate().getName().equals(Template.Type.TEMPLATE_MEANS_RESOURCES_ELEMENT.getName())) {
                Section s;
                Font font = getFontSection();
                font.setSize(font.getSize() - depth);
                if (font.getSize() < getFontSmaller().getSize()) {
                    font = getFontSmaller();
                }
                if (o instanceof Chapter) {
                    if (f.getTemplate() instanceof Template7FAQElement) {
                        s = ((Chapter) o);
                    } else {
                        s = ((Chapter) o).addSection(new Paragraph(f.getName(), font), depth);
                    }
                } else {
                    if (f.getTemplate() instanceof Template7FAQElement) {
                        s = ((Section) o);
                    } else {
                        s = ((Section) o).addSection(new Paragraph(f.getName(), font), depth);
                    }

                }
                s.setBookmarkOpen(false);
                s.add(new Paragraph(Chunk.NEWLINE));
                parseTemplate(writer, s, f, locale, isPDF);

                if (f.getTemplate().getName().equals(Template.Type.TEMPLATE_PLAN_CLICKABLE.getName())) {
                    createSubContent(writer, s, f, depth + 1, locale, false, isPDF);
                } else {
                    createSubContent(writer, s, f, depth + 1, locale, toProcess, isPDF);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "StatementWithEmptyBody"})
    private static void parseTemplate(PdfWriter writer, Section section, Folder folder, Locale l, boolean isPDF)
            throws DocumentException, IOException {
        LinkedList<Element> elements = new LinkedList<Element>();
        Template t = folder.getTemplate();

        //Add folder header
        if (folder.getFolderHeader() != null && folder.getFolderHeader().length() > 0) {
            InputStream s = new ByteArrayInputStream(folder.getFolderHeader().getBytes());
            java.util.List<Element> header =
                    Plan2PdfHTMLWorker
                            .parseToList(writer, new InputStreamReader(s), getStyle(), folder.getFolders(), isPDF);
            if (header.size() > 0) {
                if (t.getName().equals(Template.Type.TEMPLATE_CONTACTS.getName()) ||
                        t.getName().equals(Template.Type.TEMPLATE_DOCUMENTS.getName()) ||
                        t.getName().equals(Template.Type.TEMPLATE_MEANS_RESOURCES.getName())) {
                    section.addAll(header);
                    section.add(new Paragraph(Chunk.NEWLINE));
                } else {
                    elements.addAll(header);
                    elements.add(new Paragraph(Chunk.NEWLINE));
                }
            }
        }

        //parse template RESOURCE
        if (t.getName().equals(Template.Type.TEMPLATE_RESOURCE.getName())) {
            //Do nothing

            /*java.util.List<String> s = Arrays.asList(PlanService.IMAGE_MEDIA_TYPES);
           if (!s.contains(((TemplateResource) t).getResource().getMimeType())) {
               String file = folder.getPath();
               if (file.contains("online")) {
                   file = file.substring(file.indexOf("/online/") + 8, file.length())
                           .replaceAll("/folders", "")
                           .replaceAll("/", "_");
               } else {
                   file = file.substring(file.indexOf("/offline/") + 9, file.length())
                           .replaceAll("/folders", "")
                           .replaceAll("/", "_");
               }
               Chunk c = new Chunk(folder.getName(), Plan2PdfUtil.getFontSmaller());
               if (writer != null) {
                   PdfAction action = new PdfAction();
                   action.put(PdfName.S, PdfName.LAUNCH);
                   PdfFileSpecification pfs =
                           PdfFileSpecification.fileExtern(writer, ".\\resources\\" + file);
                   action.put(PdfName.F, pfs.getReference());
                   c.setAction(action);
               }
               elements.add(c);
           } else {

               InputStream data = ((TemplateResource) t).getResource().getData();
               byte[] buf = new byte[data.available()];
               ByteArrayOutputStream out = new ByteArrayOutputStream();
               int len;
               while ((len = data.read(buf)) > 0) {
                   out.write(buf, 0, len);
               }
               byte imagedata[] = out.toByteArray();
               out.close();

               ((TemplateResource) t).getResource().setData(new ByteArrayInputStream(imagedata));

               Image i = Plan2PdfUtil.getImage(new ByteArrayInputStream(imagedata));
               if (i != null) {
                   i.setAlignment(Image.ALIGN_MIDDLE);
                   resizeImage(i, getFontSmaller().getSize(), 0, 0);
                   i.setWidthPercentage(0);
                   elements.add(i);
               }
           }
           elements.add(new Paragraph(Chunk.NEWLINE));*/
        }
        //parse template 1 DIAGRAM
        else if (t.getName().equals(Template.Type.TEMPLATE_DIAGRAM.getName())) {
            String map = ((Template1Diagram) t).getImageMap();
            if (map != null) {
                java.util.List<Folder> folders = new ArrayList<Folder>();
                folders.add(folder);
                HashMap interfaceprops = new HashMap();
                //interfaceprops.put("message", getMessage(l, "template.diagram.message"));
                map = map.replaceAll("\n", "<line_break>");
                elements.addAll(Plan2PdfHTMLWorker
                        .parseToList(writer, new InputStreamReader(new ByteArrayInputStream(map.getBytes())),
                                getStyle(),
                                interfaceprops, folders, isPDF));
                elements.add(new Paragraph(Chunk.NEWLINE));
            }
        }
        //parse template 2
        else if (t.getName().equals(Template.Type.TEMPLATE_INDEX.getName())) {
            //do nothing
        }
        //parse template 3 RICH TEXT
        else if (t.getName().equals(Template.Type.TEMPLATE_RICH_TEXT.getName())) {
            String text = ((Template3RichText) t).getText();
            if (text != null) {
                elements.addAll(Plan2PdfHTMLWorker
                        .parseToList(writer, new InputStreamReader(new ByteArrayInputStream(text.getBytes())),
                                getStyle(),
                                folder.getFolders(), isPDF));
                elements.add(new Paragraph(Chunk.NEWLINE));
            }
        }
        //parse template 4 PLAN CLICKABLE
        else if (t.getName().equals(Template.Type.TEMPLATE_PLAN_CLICKABLE.getName())) {
            String map = ((Template4PlanClickable) t).getImageMap();
            if (map != null) {
                java.util.List<Folder> folders = new ArrayList<Folder>();
                folders.add(folder);
                HashMap interfaceprops = new HashMap();
                interfaceprops.put("message", getMessage(l, "template.plan.clickable.area.message"));
                elements.addAll(Plan2PdfHTMLWorker
                        .parseToList(writer, new InputStreamReader(new ByteArrayInputStream(map.getBytes())),
                                getStyle(),
                                interfaceprops, folders, isPDF));
                elements.add(new Paragraph(Chunk.NEWLINE));
            }
        }
        //parse template 5 CONTACTS AND CONTACTS ELEMENTS
        else if (t.getName().equals(Template.Type.TEMPLATE_CONTACTS.getName())) {
            LinkedList<Template5ContactsElement> external = new LinkedList<Template5ContactsElement>();
            LinkedList<Template5ContactsElement> internal = new LinkedList<Template5ContactsElement>();
            LinkedList<Template5ContactsElement> emergency = new LinkedList<Template5ContactsElement>();

            Collections.sort(folder.getFolders());

            getContacts(folder, external, internal, emergency);

            Font subFont = new Font(section.getTitle().getFont());
            subFont.setSize(subFont.getSize() - (2 * section.getDepth()));
            Section s;
            PdfPTable table;
            PdfPCell cell;
            if (external.size() > 0) {
                s = section.addSection(
                        new Paragraph(getMessage(l, "pei.template.5ContactsElement.externalEntities"), subFont),
                        section.getDepth() + 1);
                s.setBookmarkOpen(false);
                s.add(new Paragraph(Chunk.NEWLINE));

                Font f = getFontNormal();
                f.setStyle(Font.BOLD);
                table = createTable(6);
                if (isPDF) {
                    table.setHeaderRows(1);
                } else {
                    table.setHeaderRows(0);
                }
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.entityType"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.name"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.email"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.phone"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.mobile"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.photo"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);

                for (Template5ContactsElement temp : external) {
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getEntityType() == null ? "" : temp.getEntityType(), getFontSmaller()), 3,
                            0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getEntityName() == null ? "" : temp.getEntityName(), getFontSmaller()), 3,
                            0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getEmail() == null ? "" : temp.getEmail(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getPhone() == null ? "" : temp.getPhone(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getMobile() == null ? "" : temp.getMobile(), getFontSmaller()), 3, 0);
                    table.addCell(cell);

                    // contact photo
                    Resource photo = temp.getPhoto();

                    if (photo != null) {
                        InputStream photoInputStream = photo.getData();
                        byte[] photoData = getImageData(photoInputStream);
                        Image image = Image.getInstance(photoData);
                        // "reset" the inputreader
                        photo.setData(new ByteArrayInputStream(photoData));

                        image.scaleToFit(40, 40);
                        cell = createImageCellBorder(image, false, 3, 0);
                        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);

                    } else {
                        cell = createPhraseCellBorder(new Phrase("", getFontSmaller()), 3, 0);
                    }

                    table.addCell(cell);
                }
                s.add(table);
                s.add(new Paragraph(Chunk.NEWLINE));
            }

            if (internal.size() > 0) {

                s = section.addSection(
                        new Paragraph(getMessage(l, "pei.template.5ContactsElement.internalEntities"), subFont),
                        section.getDepth() + 1);
                s.setBookmarkOpen(false);
                s.add(new Paragraph(Chunk.NEWLINE));

                table = createTable(6);
                if (isPDF) {
                    table.setHeaderRows(1);
                } else {
                    table.setHeaderRows(0);
                }
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.entityName"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.personName"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.email"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.phone"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.mobile"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.photo"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);

                for (Template5ContactsElement temp : internal) {
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getEntityName() == null ? "" : temp.getEntityName(), getFontSmaller()), 3,
                            0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getName() == null ? "" : temp.getPersonName(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getEmail() == null ? "" : temp.getEmail(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getPhone() == null ? "" : temp.getPhone(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getMobile() == null ? "" : temp.getMobile(), getFontSmaller()), 3, 0);
                    table.addCell(cell);

                    // contact photo
                    Resource photo = temp.getPhoto();

                    if (photo != null) {
                        InputStream photoInputStream = photo.getData();
                        byte[] photoData = getImageData(photoInputStream);
                        Image image = Image.getInstance(photoData);
                        // "reset" the inputreader
                        photo.setData(new ByteArrayInputStream(photoData));

                        image.scaleToFit(40, 40);
                        cell = createImageCellBorder(image, false, 3, 0);
                        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);

                    } else {
                        cell = createPhraseCellBorder(new Phrase("", getFontSmaller()), 3, 0);
                    }

                    table.addCell(cell);

                }
                s.add(table);
                s.add(new Paragraph(Chunk.NEWLINE));
            }

            if (emergency.size() > 0) {
                s = section.addSection(
                        new Paragraph(getMessage(l, "pei.template.5ContactsElement.emergencyStructurePerson"), subFont),
                        section.getDepth() + 1);
                s.setBookmarkOpen(false);
                s.add(new Paragraph(Chunk.NEWLINE));

                table = createTable(7);
                if (isPDF) {
                    table.setHeaderRows(1);
                } else {
                    table.setHeaderRows(0);
                }
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.personName"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.personPosition"), getFontSmall()), 2,
                        0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.personArea"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.email"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.phone"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.mobile"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.5ContactsElement.photo"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);

                for (Template5ContactsElement temp : emergency) {
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getPersonName() == null ? "" : temp.getPersonName(), getFontSmaller()), 3,
                            0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getPersonPosition() == null ? "" : temp.getPersonPosition(),
                                    getFontSmaller()),
                            3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getPersonArea() == null ? "" : temp.getPersonArea(), getFontSmaller()), 3,
                            0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getEmail() == null ? "" : temp.getEmail(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getPhone() == null ? "" : temp.getPhone(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getMobile() == null ? "" : temp.getMobile(), getFontSmaller()), 3, 0);
                    table.addCell(cell);

                    // contact photo
                    Resource photo = temp.getPhoto();

                    if (photo != null) {
                        InputStream photoInputStream = photo.getData();
                        byte[] photoData = getImageData(photoInputStream);
                        Image image = Image.getInstance(photoData);
                        // "reset" the inputreader
                        photo.setData(new ByteArrayInputStream(photoData));

                        image.scaleToFit(40, 40);
                        cell = createImageCellBorder(image, false, 3, 0);
                        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);

                    } else {
                        cell = createPhraseCellBorder(new Phrase("", getFontSmaller()), 3, 0);
                    }

                    table.addCell(cell);

                }
                s.add(table);
                s.add(new Paragraph(Chunk.NEWLINE));
            }
        }
        //parse template 5 CONTACTS ELEMENTS
        else if (t.getName().equals(Template.Type.TEMPLATE_CONTACTS_ELEMENT.getName())) {
            //do nothing
        }
        //parse template 6 DOCUMENTS
        else if (t.getName().equals(Template.Type.TEMPLATE_DOCUMENTS.getName())) {
            LinkedList<Template6DocumentsElement> docs = new LinkedList<Template6DocumentsElement>();
            getDocs(folder, docs);
            Collections.sort(docs);

            if (docs.size() > 0) {

                PdfPTable table = createTable(new float[]{1, 2, 2, 2, 3});

                if (isPDF) {
                    table.setHeaderRows(1);
                } else {
                    table.setHeaderRows(0);
                }

                PdfPCell cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.6DocumentsElement.name"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);

                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.6DocumentsElement.type"), getFontSmall()), 2,
                        0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.6DocumentsElement.subtype"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.6DocumentsElement.date"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.6DocumentsElement.files.attach"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);

                for (Template6DocumentsElement temp : docs) {
                    //Name
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getContentName() == null ? "" : temp.getContentName(),
                                    getFontSmaller()),
                            3, 0);
                    table.addCell(cell);
                    //Type
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getContentType() == null ? "" : temp.getContentType(), getFontSmaller()), 3,
                            0);
                    table.addCell(cell);
                    //SubType
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getContentSubType() == null ? "" : temp.getContentSubType(),
                                    getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    //Date
                    cell = createPhraseCellBorder(
                            new Phrase(temp.getContentDate() == null ? "" :
                                    new SimpleDateFormat(Configuration.getInstance().getDatePattern(), l)
                                            .format(temp.getContentDate()), getFontSmaller()), 3,
                            0);
                    table.addCell(cell);

                    if (temp.getResources() != null && temp.getResources().size() > 0) {
                        Phrase p = new Phrase();
                        for (Resource r : temp.getResources()) {
                            String path = r.getPath();
                            if (path.contains("online")) {
                                path = path.substring(path.indexOf("/online/") + 8, path.indexOf("/template"))
                                        .replaceAll("/folders", "")
                                        .replaceAll("/", "_");
                            } else {
                                path = path.substring(path.indexOf("/offline/") + 9, path.indexOf("/template"))
                                        .replaceAll("/folders", "")
                                        .replaceAll("/", "_");
                            }
                            path += "_" + r.getPath()
                                    .substring(r.getPath().indexOf("/resources/") + 11, r.getPath().length());
                            Chunk c = new Chunk(path, getFontSmaller());
                            if (writer != null) {
                                PdfAction action = new PdfAction();
                                action.put(PdfName.S, PdfName.LAUNCH);
                                PdfFileSpecification pfs =
                                        PdfFileSpecification.fileExtern(writer, ".\\resources\\" + path);
                                action.put(PdfName.F, pfs.getReference());
                                c.setAction(action);
                            }
                            p.add(c);
                            p.add(new Paragraph(Chunk.NEWLINE));
                            p.add(new Paragraph(Chunk.NEWLINE));
                        }
                        cell = createPhraseCellBorder(p, 3, 0);
                    } else {
                        cell = createPhraseCellBorder(new Phrase("", getFontSmaller()), 3, 0);
                    }

                    table.addCell(cell);
                }
                elements.add(new Paragraph(Chunk.NEWLINE));
                elements.add(table);
                elements.add(new Paragraph(Chunk.NEWLINE));
            }

        }
        //parse template 6 DOCUMENTS ELEMENT
        else if (t.getName().equals(Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName())) {
            //do nothing
        }
        //parse template 7 FAQ INDEX
        else if (t.getName().equals(Template.Type.TEMPLATE_FAQ.getName())) {
            //do nothing
        }
        // parse template 7 FAQ ELEMENT
        else if (t.getName().equals(Template.Type.TEMPLATE_FAQ_ELEMENT.getName())) {
            String text = ((Template7FAQElement) t).getQuestion();
            if (text != null) {
                text = "<strong>" + text + "</strong>";
                elements.addAll(Plan2PdfHTMLWorker
                        .parseToList(writer, new InputStreamReader(new ByteArrayInputStream(text.getBytes())),
                                getStyle(), folder.getFolders(), isPDF));
            }
            elements.add(new Paragraph(Chunk.NEWLINE));
            text = ((Template7FAQElement) t).getAnswer();
            if (text != null) {
                elements.addAll(Plan2PdfHTMLWorker
                        .parseToList(writer, new InputStreamReader(new ByteArrayInputStream(text.getBytes())),
                                getStyle(),
                                folder.getFolders(), isPDF));
                elements.add(new Paragraph(Chunk.NEWLINE));
            }
        }
        //parse template 8 RISK ANALYSIS
        else if (t.getName().equals(Template.Type.TEMPLATE_RISK_ANALYSIS.getName())) {
            java.util.List<RiskAnalysisElement> list = ((Template8RiskAnalysis) t).getRiskAnalysis();
            if (list.size() > 0) {
                elements.add(new Paragraph(new Chunk(Chunk.NEWLINE)));

                PdfPTable table = createTable(7);
                if (isPDF()) {
                    table.setHeaderRows(1);
                } else {
                    table.setHeaderRows(0);
                }


                PdfPCell cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.8RiskAnalysis.product"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.8RiskAnalysis.releaseConditions"), getFontSmall()), 2,
                        0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.8RiskAnalysis.weather"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.8RiskAnalysis.ignitionPoint"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.8RiskAnalysis.radiation"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.8RiskAnalysis.pressurized"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.8RiskAnalysis.toxicity"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);

                Collections.sort(list);

                for (RiskAnalysisElement risk : list) {
                    cell = createPhraseCellBorder(new Phrase(risk.getProduct(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(new Phrase(risk.getReleaseConditions(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(new Phrase(risk.getWeather(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(new Phrase(risk.getIgnitionPoint(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(new Phrase(risk.getRadiation(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(new Phrase(risk.getPressurized(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    cell = createPhraseCellBorder(new Phrase(risk.getToxicity(), getFontSmaller()), 3, 0);
                    table.addCell(cell);
                }
                elements.add(table);
            }
        }
        //parse template 9 TEMPLATE_RICH_TEXT_WITH_ATTACH
        else if (t.getName().equals(Template.Type.TEMPLATE_RICH_TEXT_WITH_ATTACH.getName())) {
            String text = ((Template9RichTextWithAttach) t).getText();
            if (text != null) {
                elements.addAll(Plan2PdfHTMLWorker
                        .parseToList(writer, new InputStreamReader(new ByteArrayInputStream(text.getBytes())),
                                getStyle(),
                                folder.getFolders(), isPDF));
                elements.add(new Paragraph(Chunk.NEWLINE));
            }
        }

        //parse template 12 MEANS/RESOURCES and it's elements
        else if (t.getName().equals(Template.Type.TEMPLATE_MEANS_RESOURCES.getName())) {
            LinkedList<Template12MeansResourcesElement> list = new LinkedList<Template12MeansResourcesElement>();
            Collections.sort(folder.getFolders());
            PlanExportUtil.getMeansResources(folder, list);

            if (list.size() > 0) {

                PdfPTable table = createTable(new float[]{2, 2, 2, 2, 4});

                if (isPDF) {
                    table.setHeaderRows(1);
                } else {
                    table.setHeaderRows(0);
                }

                PdfPCell cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.12MeansResourcesElement.resourceName"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);

                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.12MeansResourcesElement.resourceType"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.12MeansResourcesElement.entityName"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.12MeansResourcesElement.quantity"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
                cell = createPhraseCellBorder(
                        new Phrase(getMessage(l, "pei.template.12MeansResourcesElement.characteristics"), getFontSmall()), 2, 0);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);

                for (Template12MeansResourcesElement element : list) {
                    //Name
                    cell = createPhraseCellBorder(
                            new Phrase(element.getResourceName() == null ? "" : element.getResourceName(),
                                    getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    //Type
                    cell = createPhraseCellBorder(
                            new Phrase(element.getResourceType() == null ? "" : element.getResourceType(),
                                    getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    //Entity name
                    cell = createPhraseCellBorder(
                            new Phrase(element.getEntityName() == null ? "" : element.getEntityName(),
                                    getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    //Quantity
                    cell = createPhraseCellBorder(
                            new Phrase(element.getQuantity() == null ? "" : element.getQuantity(),
                                    getFontSmaller()), 3, 0);
                    table.addCell(cell);
                    //Characteristics
                    cell = createPhraseCellBorder(
                            new Phrase(element.getCharacteristics() == null ? "" : element.getCharacteristics(),
                                    getFontSmaller()), 3, 0);
                    table.addCell(cell);
                }
                elements.add(new Paragraph(Chunk.NEWLINE));
                elements.add(table);
                elements.add(new Paragraph(Chunk.NEWLINE));
            }
        }
        //parse template 12 MEANS RESOURCES ELEMENTS
        else if (t.getName().equals(Template.Type.TEMPLATE_MEANS_RESOURCES_ELEMENT.getName())) {
            //do nothing
        }

        //Add folder footer
        if (folder.getFolderFooter() != null && folder.getFolderFooter().length() > 0) {
            InputStream s = new ByteArrayInputStream(folder.getFolderFooter().getBytes());
            java.util.List<Element> footer =
                    Plan2PdfHTMLWorker.parseToList(writer, new InputStreamReader(s), getStyle(),
                            folder.getFolders(), isPDF);
            if (footer.size() > 0) {
                if (t.getName().equals(Template.Type.TEMPLATE_CONTACTS.getName()) ||
                        t.getName().equals(Template.Type.TEMPLATE_DOCUMENTS.getName())) {
                    section.addAll(footer);
                    section.add(new Paragraph(Chunk.NEWLINE));
                } else {
                    elements.addAll(footer);
                    elements.add(new Paragraph(Chunk.NEWLINE));
                }
            }
        }

        //Add parsed template
        if (elements.size() > 0) {
            for (Element e : elements) {
                section.add(e);
            }
        }

    }

    private static void getDocs(Folder folder, LinkedList<Template6DocumentsElement> docs) {
        for (Folder f : folder.getFolders()) {
            if (f.getTemplate().getName().equals(Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName()) &&
                    f.getActive().equals(Boolean.TRUE)) {
                docs.add(((Template6DocumentsElement) f.getTemplate()));
            }
            getDocs(f, docs);
        }
    }

    private static void getContacts(Folder folder, LinkedList<Template5ContactsElement> external,
                                    LinkedList<Template5ContactsElement> internal,
                                    LinkedList<Template5ContactsElement> emergency) {

        for (Folder f : folder.getFolders()) {
            if (f.getTemplate().getName().equals(Template.Type.TEMPLATE_CONTACTS_ELEMENT.getName()) &&
                    f.getActive().equals(Boolean.TRUE)) {
                Template5ContactsElement temp = ((Template5ContactsElement) f.getTemplate());
                if (temp.getContactType().equals(Template5ContactsElement.ContactType.EXTERNAL_ENTITY.toString())) {
                    external.add(temp);
                } else if (temp.getContactType()
                        .equals(Template5ContactsElement.ContactType.INTERNAL_PERSON.toString())) {
                    internal.add(temp);
                } else {
                    emergency.add(temp);
                }
            }
            getContacts(f, external, internal, emergency);
        }
    }

    private static String getMessage(Locale locale, String key, Object... arguments) {
        return new MessageFormat(ResourceBundle.getBundle("StripesResources", locale).getString(key), locale)
                .format(arguments);
    }

    // CONFIGURATION PDF
    private static StyleSheet getStyle() {
        StyleSheet ss = new StyleSheet();
        for (Object o : Plan2PdfHTMLWorker.tagsSupported.keySet()) {
            String tag = (String) o;
            if (isPDF) {
                ss.loadTagStyle(tag, "face", FontFactory.HELVETICA);
            } else {
                ss.loadTagStyle(tag, "face", FontFactory.HELVETICA_BOLD);
            }

        }
        return ss;
    }

    /*private static StyleSheet getTempateFAQElementQuestionStyle() {
        StyleSheet ss = new StyleSheet();
        for (Object o : Plan2PdfHTMLWorker.tagsSupported.keySet()) {
            String tag = (String) o;
            ss.loadTagStyle(tag, "face", FontFactory.HELVETICA_BOLD);
        }
        return ss;
    }*/

    // CONFIGURATION PDF

    public static void resizeImage(Image img, float actualFontSize, float widthInPoints, float heightInPoints) {
        if (widthInPoints == 0) {
            widthInPoints = Markup.parseLength("" + img.getWidth(), actualFontSize);
        }
        if (heightInPoints == 0) {
            heightInPoints = Markup.parseLength("" + img.getHeight(), actualFontSize);
        }

        if (widthInPoints > PageSize.A4.getWidth() - 120) {
            widthInPoints = PageSize.A4.getWidth() - 120;
            heightInPoints = img.getHeight() / img.getWidth() * widthInPoints;
        }

        if (heightInPoints > PageSize.A4.getHeight() - 140) {
            heightInPoints = PageSize.A4.getHeight() - 140;
            widthInPoints = img.getWidth() / img.getHeight() * heightInPoints;
        }

        img.scaleAbsolute(widthInPoints, heightInPoints);
    }
}