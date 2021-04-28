/*
 * $Id: Plan2Docx.java,v 1.31 2010/12/30 19:33:23 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/12/30 19:33:23 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util.export;

import com.aspose.words.*;
import com.criticalsoftware.certitools.business.exception.AsposeException;
import com.criticalsoftware.certitools.entities.TemplateDocx;
import com.criticalsoftware.certitools.entities.jcr.*;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.File;
import com.criticalsoftware.certitools.util.Logger;
import com.lowagie.text.Image;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Exports a plan to docx format
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.31 $
 */
@SuppressWarnings({"SimplifiableIfStatement"})
public class Plan2Docx {
    private static final Logger LOGGER = Logger.getInstance(Plan2Docx.class);

    private boolean exportOnline;
    private Plan plan;
    private String planName;
    private TemplateDocx templateDocx;
    private File templateDocxFile;
    private List<Folder> foldersToExport; // all folders from the pei (online or offline folders)
    private Map<String, InputStream> filesToExport;
    private Map<String, PlanExportAnnex> documentAnnexes;

    private static SimpleDateFormat dateFormat;

    // configurations static
    private final static String ANNEX = "annex:";
    public final static String ANNEX_FOLDER = "Anexos/";
    private final static String COVER = "cover:";
    private int saveFormat;
    private String saveFormatExtension;

    public enum FolderOptions {
        DIAGRAM_AREAS("areas"),
        DIAGRAM_AREAS2("�reas"), // common shortcut
        START_HEADING1("h1"),
        START_HEADING2("h2"),
        START_HEADING3("h3"),
        START_HEADING4("h4"),
        START_HEADING5("h5"),
        START_HEADING6("h6"),
        DOCUMENT_IMAGE("image"),
        ANNEX_LIST("list"),
        ANNEXES("files"),
        DELETE("delete"); // section annex delete

        private String name;

        FolderOptions(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum CoverBookmarks {
        DESIGNATION("designation"),
        AUTHOR("author"),
        VERSION("version"),
        VERSION_DATE("version_date"),
        VERSION_DATE2("version date"),
        SIMULATION_DATE("last_simulation"),
        SIMULATION_DATE2("last simulation"),
        PHOTO("photo"),
        LOGO("logo");

        private String name;

        CoverBookmarks(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public Plan2Docx(boolean exportOnline, Plan plan, TemplateDocx templateDocx, File templateDocxFile, Locale locale,
                     int saveFormat) {
        this.exportOnline = exportOnline;
        this.plan = plan;
        this.templateDocxFile = templateDocxFile;
        this.foldersToExport = exportOnline ? plan.getOnline() : plan.getOffline();
        this.planName = exportOnline ? plan.getPlanNameOnline() : plan.getPlanName();
        this.filesToExport = new HashMap<String, InputStream>();
        this.documentAnnexes = new HashMap<String, PlanExportAnnex>();
        this.saveFormat = saveFormat;
        this.templateDocx = templateDocx;

        switch (saveFormat) {
            case SaveFormat.DOCX:
                saveFormatExtension = ".docx";
                break;
            case SaveFormat.DOC:
                saveFormatExtension = ".doc";
                break;
            default:
                saveFormatExtension = ".docx";
        }

        dateFormat = new SimpleDateFormat(Configuration.getInstance().getDatePattern(), locale);
    }

    public PlanExport generateFile() throws AsposeException {
        Document templateDoc;

        // read template
        startupAspose();
        try {
            templateDoc = new Document(templateDocxFile.getData());
        } catch (Exception e) {
            throw new AsposeException("Error when reading template document");
        }

        // correct bookmarks in other paragraphs
        try {
            ArrayList<Bookmark> bookmarksToMove = findFolderBookmarks(templateDoc.getRange());
            bookmarksToMove.addAll(findCoverBookmarks(templateDoc.getRange()));

            for (Bookmark bookmark : bookmarksToMove) {
                moveOtherBookmarksFromParagraph(bookmark.getBookmarkStart().getParentNode(), bookmark);
            }
        } catch (Exception e) {
            LOGGER.error("Error organizing bookmarks");
            throw new AsposeException(e.getMessage(), e.getCause());
        }

        // process sections
        try {
            // to keep only sections that aren't annexes in the templatedoc
            ArrayList<Section> templateDocSections = new ArrayList<Section>();

            int i = 0;
            for (Section section : templateDoc.getSections()) {
                String sectionTitle;
                sectionTitle = findSectionTitle(section);

                if (sectionTitle != null) {
                    // get Annex document
                    PlanExportAnnex annexDocument;
                    annexDocument = documentAnnexes.get(sectionTitle);

                    // if empty, create it
                    if (annexDocument == null) {
                        annexDocument = new PlanExportAnnex(sectionTitle, templateDoc, i);
                        documentAnnexes.put(sectionTitle, annexDocument);
                    }
                } else {
                    templateDocSections.add(section);
                }
                i++;
            }

            // remove all sections and re-add only the ones that aren't annexes
            templateDoc.getSections().clear();
            for (Section templateDocSection : templateDocSections) {
                templateDoc.getSections().add(templateDocSection);
            }
        } catch (Exception e) {
            LOGGER.error("Error processing sections to individual docs");
            LOGGER.error(e);
        }

        // process main document
        ByteArrayOutputStream mainDocBAOS = processSection(templateDoc, null, null, saveFormat);
        filesToExport.put(PlanExportUtil.sanitizeFilenameToExport(templateDocx.getTitle()) + saveFormatExtension,
                new ByteArrayInputStream(mainDocBAOS.toByteArray()));

        // process annex sections
        for (PlanExportAnnex planExportAnnex : documentAnnexes.values()) {
            ByteArrayOutputStream annexBAOS =
                    processSection(planExportAnnex.getTemplateDoc(), planExportAnnex.getTitle(),
                            planExportAnnex.getSectionOptions(), saveFormat);
            if (annexBAOS != null) {
                filesToExport
                        .put(ANNEX_FOLDER + PlanExportUtil.sanitizeFilenameToExport(planExportAnnex.getTitle()) +
                                saveFormatExtension, new ByteArrayInputStream(annexBAOS.toByteArray()));
            }
        }

        if (filesToExport.size() > 1) {
            // Zip it
            try {
                return new PlanExport(PlanExportUtil.sanitizeFilenameToExport(templateDocx.getTitle()) + ".zip",
                        "application/zip", PlanExportUtil.doZip(filesToExport));
            } catch (IOException e) {
                LOGGER.error(e);
                LOGGER.error("Error zipping the files. PlanName:" + planName + " Template docx name: " +
                        templateDocx.getTitle());
            }
        }

        return new PlanExport(PlanExportUtil.sanitizeFilenameToExport(templateDocx.getTitle()) + saveFormatExtension,
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                mainDocBAOS);
    }

    private ByteArrayOutputStream processSection(Document templateDoc, String sectionTitle,
                                                 ArrayList<String> sectionOptions, int saveFormat)
            throws AsposeException {
        DocumentBuilder documentBuilder;
        ByteArrayOutputStream exportFileStream;

        // remove section title bookmark
        try {
            ArrayList<Bookmark> bookmarks = findSectionTitleBookmarks(templateDoc.getRange());
            if (bookmarks != null && bookmarks.size() > 0) {
                for (Bookmark bookmark : bookmarks) {
                    removeBookmark(bookmark);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error when trying to remove chapter titles");
            throw new AsposeException(e.getMessage(), e.getCause());
        }

        // process cover bookmarks
        try {
            documentBuilder = new DocumentBuilder(templateDoc);

            documentBuilder.moveToDocumentStart();
            ArrayList<Bookmark> coverBookmarks = findCoverBookmarks(templateDoc.getRange());
            for (Bookmark coverBookmark : coverBookmarks) {
                processCoverBookmark(coverBookmark, documentBuilder);
            }
            documentBuilder.moveToDocumentStart();
        } catch (Exception e) {
            throw new AsposeException(e.getMessage(), e.getCause());
        }

        try {
            // process template
            ArrayList<Bookmark> replacementBookmarks = findFolderBookmarks(templateDoc.getRange());
            for (Bookmark bookmark : replacementBookmarks) {
                processBookmark(bookmark, documentBuilder, sectionTitle);
            }
        } catch (Exception e) {
            throw new AsposeException(e.getMessage(), e.getCause());
        }

        // process merge fields
        processMergeFields(templateDoc, documentBuilder, sectionTitle);

        // if section delete don't continue
        if (sectionTitle != null && !StringUtils.isBlank(sectionTitle) && sectionOptions != null &&
                sectionOptions.size() > 0 && sectionOptions.contains(FolderOptions.DELETE.getName())) {
            return null;
        }

        // remove empty merge field tables
        try {
            String[] fields = templateDoc.getMailMerge().getFieldNames();
            for (String field : fields) {
                documentBuilder.moveToMergeField(field);
                Node table = documentBuilder.getCurrentParagraph().getAncestor(NodeType.TABLE);
                if (table != null) {
                    try {
                        table.remove();
                    } catch (java.lang.IllegalStateException e) {
                        // ignoring this exception
                        LOGGER.info("Deleting empty merge fields " + e.getMessage());
                    }
                }
            }

            // remove remaining merge fields
            templateDoc.getMailMerge().deleteFields();
        } catch (Exception e) {
            LOGGER.error("Error when trying to remove empty merge regions");
            throw new AsposeException(e.getMessage(), e.getCause());
        }

        // update toc
        /*
        try {
            templateDoc.updateFields();
            templateDoc.updatePageLayout();
        } catch (Exception e) {
            LOGGER.error("Error updating fields in section: " + sectionTitle);
            LOGGER.error(e);
        }
        */

        // save template file
        try {
            exportFileStream = new ByteArrayOutputStream();
            templateDoc.save(exportFileStream, saveFormat);
        } catch (Exception e) {
            throw new AsposeException("Error when saving the exported Docx file");
        }

        return exportFileStream;
    }

    private void processCoverBookmark(Bookmark bookmark, DocumentBuilder documentBuilder) throws Exception {
        // Place cursor before bookmark.
        documentBuilder.moveToBookmark(bookmark.getName(), true, false);

        // Process bookmark
        StringTokenizer st = new StringTokenizer(bookmark.getText(), ":");
        String coverField;
        ArrayList<String> coverFieldOptions = new ArrayList<String>();

        if (st.countTokens() <= 2) {
            coverField = bookmark.getText().substring(COVER.length()).trim();
        } else {
            st.nextToken();
            coverField = st.nextToken();
            while (st.hasMoreTokens()) {
                coverFieldOptions.add(st.nextToken().trim());
            }
        }


        if (CoverBookmarks.DESIGNATION.getName().equals(coverField)) {
            String text = exportOnline ? plan.getPlanNameOnline() : plan.getPlanName();
            documentBuilder.write(text != null ? text : "");

        } else if (CoverBookmarks.AUTHOR.getName().equals(coverField)) {
            String text = exportOnline ? plan.getAuthorNameOnline() : plan.getAuthorName();
            documentBuilder.write(text != null ? text : "");

        } else if (CoverBookmarks.VERSION.getName().equals(coverField)) {
            String text = exportOnline ? plan.getVersionOnline() : plan.getVersion();
            documentBuilder.write(text != null ? text : "");

        } else if (CoverBookmarks.VERSION_DATE.getName().equals(coverField) ||
                CoverBookmarks.VERSION_DATE2.getName().equals(coverField)) {
            Date date = exportOnline ? plan.getVersionDateOnline() : plan.getVersionDate();
            if (date != null) {
                documentBuilder.write(dateFormat.format(date));
            }

        } else if (CoverBookmarks.SIMULATION_DATE.getName().equals(coverField) ||
                CoverBookmarks.SIMULATION_DATE2.getName().equals(coverField)) {
            Date date = exportOnline ? plan.getSimulationDateOnline() : plan.getSimulationDate();
            if (date != null) {
                documentBuilder.write(dateFormat.format(date));
            }

        } else if (CoverBookmarks.PHOTO.getName().equals(coverField)) {
            Resource photo = exportOnline ? plan.getInstallationPhotoOnline() : plan.getInstallationPhoto();
            if (photo != null) {
                insertCoverImageCropped(photo.getData(), coverFieldOptions, documentBuilder);
            }

        } else if (CoverBookmarks.LOGO.getName().equals(coverField)) {
            Resource photo = exportOnline ? plan.getCompanyLogoOnline() : plan.getCompanyLogo();
            if (photo != null) {
                insertCoverImageCropped(photo.getData(), coverFieldOptions, documentBuilder);
            }
        } else {
            LOGGER.info("Invalid cover link specified: " + coverField);
            documentBuilder.insertHtml("<p>Invalid cover link specified: <strong>\"" + coverField + "\"</strong></p>");
        }

        // remove bookmark
        removeBookmark(bookmark);
    }

    private void insertCoverImageCropped(InputStream imageStream, ArrayList<String> coverFieldOptions,
                                         DocumentBuilder db)
            throws Exception {
        if (coverFieldOptions.size() >= 1) {
            // at least width defined
            double maxWidth = Double.parseDouble(coverFieldOptions.get(0));

            Double maxHeight = null;
            if (coverFieldOptions.size() > 1) {
                maxHeight = Double.parseDouble(coverFieldOptions.get(1));
            }

            Image image = PlanExportUtil.getImage(imageStream);
            double width = image.getWidth();
            double height = image.getHeight();
            double ratio;

            if (width > maxWidth) {
                ratio = maxWidth / width;
                width = maxWidth;
                height = height * ratio;
            }

            // if max height is defined
            if (maxHeight != null && height > maxHeight) {
                ratio = maxHeight / height;
                height = maxHeight;
                width = width * ratio;
            }

            PlanExportUtil.resetInputStream(imageStream);
            db.insertImage(imageStream, ConvertUtil.pixelToPoint(width),
                    ConvertUtil.pixelToPoint(height));
            PlanExportUtil.resetInputStream(imageStream);
        } else {
            // no width/height defined
            db.insertImage(imageStream);
            PlanExportUtil.resetInputStream(imageStream);
        }
    }

    private void processBookmark(Bookmark bookmark, DocumentBuilder documentBuilder, String sectionTitle)
            throws Exception {
        // Place cursor before bookmark.
        documentBuilder.moveToBookmark(bookmark.getName(), true, false);

        // Process and replace bookmark with folder content
        String templateLink = bookmark.getText().trim();
        String[] templateLinkSplited = templateLink.split(":");
        String[] folderOptions = null;

        if (templateLinkSplited.length > 1) {
            folderOptions = new String[templateLinkSplited.length - 1];
            for (int i = 1; i < templateLinkSplited.length; i++) {
                String folderOption = templateLinkSplited[i];
                folderOptions[i - 1] = folderOption.toLowerCase();
            }
        }

        processFolder(templateLinkSplited[0], folderOptions, documentBuilder, sectionTitle);
        removeBookmark(bookmark);
    }

    private void removeBookmark(Bookmark bookmark) throws Exception {
        // Remove paragraph with bookmark if BookmarkStart is the first node of the paragraph.
        CompositeNode bkParent = bookmark.getBookmarkStart().getParentNode();
        if (bkParent.getFirstChild().equals(bookmark.getBookmarkStart())) {
            //moveOtherBookmarksFromParagraph(bkParent, bookmark);
            bkParent.remove();
        } else {
            bookmark.setText("");
            bookmark.remove();
        }
    }

    private void processMergeFields(Document doc, DocumentBuilder documentBuilder, String sectionTitle) {
        ArrayList<String> tablesSources = new ArrayList<String>();

        try {
            String[] fieldNames = doc.getMailMerge().getFieldNames();
            tablesSources = new ArrayList<String>();

            for (String fieldName : fieldNames) {
                if (fieldName.startsWith("TableStart")) {
                    tablesSources.add(fieldName.substring(fieldName.indexOf("TableStart:") + "TableStart:".length()));
                }
            }
        } catch (Exception e) {
            LOGGER.error("[processMergeFields] Error getting fieldnames");
            LOGGER.error(e);
        }

        for (String folderName : tablesSources) {
            Folder folder = PlanExportUtil.findFolderFromPlanList(folderName, foldersToExport);
            if (folder == null) {
                LOGGER.info("Folder not found" + folderName);
                return;
            }

            Template t = folder.getTemplate();
            try {
                if (t == null) {
                    LOGGER.info("Template from folder not found" + folderName);
                    return;
                } else if ((t.getName().equals(Template.Type.TEMPLATE_CONTACTS.getName()))) {
                    Plan2DocxProcessTemplate.processTemplate5Contacts(folder, folderName, doc);
                } else if ((t.getName().equals(Template.Type.TEMPLATE_RISK_ANALYSIS.getName()))) {
                    Plan2DocxProcessTemplate
                            .processTemplate8RiskAnalysis(documentBuilder, folder, folderName, foldersToExport,
                                    filesToExport, sectionTitle);
                } else if ((t.getName().equals(Template.Type.TEMPLATE_MEANS_RESOURCES.getName()))) {
                    Plan2DocxProcessTemplate.processTemplate12MeansResources(folder, folderName, doc);
                }
            } catch (Exception e) {
                LOGGER.error(
                        "[processMergeFields] - Error processing merge fields. Folder: " + folder.getName() + " | " +
                                folder.getPath());
                LOGGER.error(e);
            }
        }

        /*
        if (Configuration.getInstance().getDocxExportApplyAligmnentParagraphsFix()) {
            // traverse all paragraphs and set aligment :: bug fix for CERTOOL-525 1)
            try {
                Style normalStyle = doc.getStyles().get(StyleIdentifier.NORMAL);
                int normalStyleAlignment = normalStyle.getParagraphFormat().getAlignment();

                NodeCollection paragraphs = doc.getChildNodes(NodeType.PARAGRAPH, true);
                for (Object node : paragraphs) {
                    Paragraph paragraph = (Paragraph) node;
                    if (paragraph.getParagraphFormat().getStyleIdentifier() == StyleIdentifier.NORMAL) {
                        paragraph.getParagraphFormat().setAlignment(normalStyleAlignment);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("[processMergeFields] - Error fixing alignments in paragraphs");
            }
        }
        */
    }

    private void processFolder(String folderName, String[] folderOptions, DocumentBuilder documentBuilder,
                               String sectionTitle)
            throws AsposeException, IOException {
        Folder folder = PlanExportUtil.findFolderFromPlanList(folderName, foldersToExport);
        if (folder == null) {
            LOGGER.info("Folder not found" + folderName);
            return;
        }
        Template t = folder.getTemplate();
        if (t == null) {
            LOGGER.info("Template from folder not found" + folderName);
            return;
        }

        try {
            if ((t.getName().equals(Template.Type.TEMPLATE_DIAGRAM.getName()))) {
                if (folderOptions != null &&
                        (Arrays.asList(folderOptions).contains(FolderOptions.DIAGRAM_AREAS.getName()) ||
                                (Arrays.asList(folderOptions).contains(FolderOptions.DIAGRAM_AREAS2.getName())))) {
                    Plan2DocxProcessTemplate
                            .processTemplate1DiagramAreas(documentBuilder,
                                    ((Template1Diagram) folder.getTemplate()).getImageMap());
                } else {
                    Plan2DocxProcessTemplate.processRichTextTemplates(documentBuilder, folder,
                            ((Template1Diagram) folder.getTemplate()).getImageMap(), foldersToExport);
                }

            } else if ((t.getName().equals(Template.Type.TEMPLATE_RICH_TEXT.getName()))) {
                Plan2DocxProcessTemplate.processRichTextTemplates(documentBuilder, folder,
                        ((Template3RichText) folder.getTemplate()).getText(), foldersToExport);

            } else if ((t.getName().equals(Template.Type.TEMPLATE_RICH_TEXT_WITH_ATTACH.getName()))) {
                Plan2DocxProcessTemplate.processRichTextTemplates(documentBuilder, folder,
                        ((Template9RichTextWithAttach) folder.getTemplate()).getText(), foldersToExport);

            } else if ((t.getName().equals(Template.Type.TEMPLATE_PLAN_CLICKABLE.getName()))) {
                if (folderOptionPresent(folderOptions, FolderOptions.ANNEXES)) {
                    Plan2DocxProcessTemplate.processTemplate4PlanClickableAnnexes(folder, filesToExport, sectionTitle,
                            documentBuilder, folderOptionPresent(folderOptions, FolderOptions.ANNEX_LIST));
                } else {
                    Plan2DocxProcessTemplate.processRichTextTemplates(documentBuilder, folder,
                            ((Template4PlanClickable) folder.getTemplate()).getImageMap(), foldersToExport);
                }


            } else if ((t.getName().equals(Template.Type.TEMPLATE_CONTACTS.getName()))) {
                documentBuilder.insertHtml("<p>Template contacts incorrect export usage. Please check help file</p>");

            } else if ((t.getName().equals(Template.Type.TEMPLATE_MEANS_RESOURCES.getName()))) {
                documentBuilder.insertHtml("<p>Template means/resources incorrect export usage. Please check help file</p>");

            } else if ((t.getName().equals(Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName()))) {
                if (folderOptionPresent(folderOptions, FolderOptions.DOCUMENT_IMAGE)) {
                    Plan2DocxProcessTemplate.processTemplate6DocumentsElementImage(documentBuilder, folder);
                } else {
                    Plan2DocxProcessTemplate
                            .processTemplate6DocumentsElement(folder, filesToExport, sectionTitle, documentBuilder,
                                    folderOptionPresent(folderOptions, FolderOptions.ANNEX_LIST));
                }

            } else if ((t.getName().equals(Template.Type.TEMPLATE_DOCUMENTS.getName()))) {
                Plan2DocxProcessTemplate.processTemplate6Documents(folder, filesToExport, sectionTitle, documentBuilder,
                        folderOptionPresent(folderOptions, FolderOptions.ANNEX_LIST));

            } else if ((t.getName().equals(Template.Type.TEMPLATE_FAQ.getName()))) {
                Plan2DocxProcessTemplate.processTemplate7FAQ(folder, documentBuilder, foldersToExport);

            } else if ((t.getName().equals(Template.Type.TEMPLATE_FAQ_ELEMENT.getName()))) {
                Plan2DocxProcessTemplate.processTemplate7FAQElement(folder, documentBuilder, foldersToExport);

            } else if ((t.getName().equals(Template.Type.TEMPLATE_RISK_ANALYSIS.getName()))) {
                documentBuilder
                        .insertHtml("<p>Template risk analysis incorrect export usage. Please check help file</p>");

            } else if ((t.getName().equals(Template.Type.TEMPLATE_PROCEDURE.getName()))) {

                int heading = 1;
                if (folderOptionPresent(folderOptions, FolderOptions.START_HEADING1)) {
                    heading = 1;
                } else if (folderOptionPresent(folderOptions, FolderOptions.START_HEADING2)) {
                    heading = 2;
                } else if (folderOptionPresent(folderOptions, FolderOptions.START_HEADING3)) {
                    heading = 3;
                } else if (folderOptionPresent(folderOptions, FolderOptions.START_HEADING4)) {
                    heading = 4;
                } else if (folderOptionPresent(folderOptions, FolderOptions.START_HEADING5)) {
                    heading = 5;
                } else if (folderOptionPresent(folderOptions, FolderOptions.START_HEADING6)) {
                    heading = 6;
                }

                Plan2DocxProcessTemplate
                        .processTemplate10Procedure(documentBuilder, folder, heading, foldersToExport, filesToExport,
                                sectionTitle);

            } else if ((t.getName().equals(Template.Type.TEMPLATE_RESOURCE.getName()))) {
                Plan2DocxProcessTemplate.processTemplateResource(folder, filesToExport, sectionTitle);

            } else {
                LOGGER.info("Invalid template/link: Folder name: " + folderName + ". " + "Folder options:" +
                        folderOptions.toString());
                documentBuilder
                        .insertHtml("<p>Invalid link or template is not supported. Folder name: " + folderName + ". " +
                                "Folder options:" + folderOptions.toString() + " </p>");
            }
        } catch (Exception e) {
            LOGGER.error(
                    "[processFolder] Error processing folder. Folder: " + folder.getName() + " folder options: " +
                            folderOptions.toString() + " | " + folder.getPath());
            LOGGER.error(e);
        }
    }

    private boolean folderOptionPresent(String[] folderOptions, FolderOptions option) {
        if (folderOptions == null || option == null) {
            return false;
        }
        return Arrays.asList(folderOptions).contains(option.getName());
    }

    private void startupAspose() throws AsposeException {
        License license = new License();
        try {
            license.setLicense(Configuration.getInstance().getAsposeLicenseFile());
        } catch (Exception e) {
            LOGGER.error(e);
            throw new AsposeException("Error when reading license file");
        }
    }

    private ArrayList<Bookmark> findCoverBookmarks(Range range) throws Exception {
        BookmarkCollection bookmarks = range.getBookmarks();
        ArrayList<Bookmark> bookmarksResult = new ArrayList<Bookmark>();
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.getText().toLowerCase().startsWith(COVER)) {
                bookmarksResult.add(bookmark);
            }
        }
        return bookmarksResult;
    }

    private ArrayList<Bookmark> findFolderBookmarks(Range range) throws Exception {
        BookmarkCollection bookmarks = range.getBookmarks();
        ArrayList<Bookmark> bookmarksResult = new ArrayList<Bookmark>();
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.getText().startsWith("/")) {
                bookmarksResult.add(bookmark);
            }
        }
        return bookmarksResult;
    }

    private static String findSectionTitle(Section section) throws Exception {
        BookmarkCollection bookmarks = section.getRange().getBookmarks();
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.getText().toLowerCase().startsWith(ANNEX)) {
                return bookmark.getText().substring(ANNEX.length()).trim();
            }
        }
        return null;
    }

    private static ArrayList<Bookmark> findSectionTitleBookmarks(Range range) throws Exception {
        BookmarkCollection bookmarks = range.getBookmarks();
        ArrayList<Bookmark> bookmarksResult = new ArrayList<Bookmark>();
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.getText().toLowerCase().startsWith(ANNEX)) {
                bookmarksResult.add(bookmark);
            }
        }
        return bookmarksResult;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private static void printDoc(Document doc) {

        for (Object n : doc.getChildNodes(NodeType.ANY, true)) {
            Node node = (Node) n;

            System.out.println("" + node.getClass() + " ||||  " + node.getParentNode().getClass());
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private static void printFolderBookmarks(Document doc) throws Exception {
        System.out.println("****************************************************\nBOOKMARKS");
        BookmarkCollection bookmarks = doc.getRange().getBookmarks();
        ArrayList<Bookmark> bookmarksResult = new ArrayList<Bookmark>();
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.getText().startsWith("/")) {
                System.out.println("" + bookmark.getName() + " | " + bookmark.getText());
            }
        }
    }


    /**
     * This moves the bookmark end / start so that 1 bookmark is only in 1 paragraph. Used to solve the problem that
     * bookmark start is in paragraph 1 and bookmark end is in paragraph 2 which, when removed, can remove other bookmarks
     * messing up the document.
     * <p/>
     * From Aspose bug report forum:
     * http://www.aspose.com/community/forums/showthread.aspx?PostID=247470
     *
     * @param para     paragraph of the bookmark
     * @param bookmark bookmark to be moved
     * @throws Exception aspose exception
     */
    @SuppressWarnings({"ForLoopReplaceableByForEach", "unchecked"})
    private static void moveOtherBookmarksFromParagraph(CompositeNode para, Bookmark bookmark) throws Exception {
        List nodeList = Arrays.asList(para.getChildNodes().toArray());
        ArrayList nodes = new ArrayList(nodeList);

        for (int i = 0; i < nodes.size(); i++) {
            Node node = (Node) nodes.get(i);

            if (node.getNodeType() == NodeType.BOOKMARK_START || node.getNodeType() == NodeType.BOOKMARK_END) {
                Bookmark currentBookmark = findBookMarkFromMarker(node);

                // If this bookmark is not the one we are going to remove then continue on to move it to a different paragraph
                if (currentBookmark != null && !currentBookmark.getName().equals(bookmark.getName())) {

                    // If this node is BOOKMARK_START then assume the end bookmark is in one of the paragraphs after this, move this one downward
                    // else this node is BOOKMARK_END so assume bookmark start is in one of the paragraphs before this, move this one upward
                    boolean direction = (node.getNodeType() == NodeType.BOOKMARK_START);
                    moveNodeToNextParagraph(node, direction);
                }
            }
        }
    }

    private static Paragraph findNextParagraph(Paragraph para, boolean isDown) {

        Node nextnode;
        if (isDown)
            nextnode = para.getNextSibling();
        else
            nextnode = para.getPreviousSibling();

        while (nextnode != null) {

            if (nextnode.getNodeType() == NodeType.PARAGRAPH)
                return (Paragraph) nextnode;

            if (isDown)
                nextnode = nextnode.getNextSibling();
            else
                nextnode = nextnode.getPreviousSibling();
        }

        return null;
    }

    private static Bookmark findBookMarkFromMarker(Node node) {
        if (node.getNodeType() == NodeType.BOOKMARK_START) {
            BookmarkStart start = (BookmarkStart) node;
            return start.getBookmark();
        } else {
            // Takes a bit more work to find it from the bookmark end
            BookmarkEnd end = (BookmarkEnd) node;
            NodeCollection bookmarkStarts = node.getDocument().getChildNodes(NodeType.BOOKMARK_START, true);

            for (int i = 0; i < bookmarkStarts.getCount(); i++) {
                BookmarkStart start = (BookmarkStart) bookmarkStarts.get(i);
                if (start.getName().equals(end.getName())) {
                    return start.getBookmark();
                }

            }
        }

        return null;
    }

    private static void moveNodeToNextParagraph(Node node, boolean moveDown) throws Exception {
        Bookmark bookmark = findBookMarkFromMarker(node);

        // If the extra bookmark found in the paragraph starts and finishes within the same paragraph then
        // do nothing, they should be removed when the paragraph is removed with no problems.
        Node startNode = bookmark.getBookmarkStart().getParentNode();
        Node endNode = bookmark.getBookmarkEnd().getParentNode();
        if (startNode.equals(endNode))
            return;

        Paragraph para = findNextParagraph((Paragraph) node.getParentNode(), moveDown);

        // If para is null then most likely there are no other paragraphs to move the bookmark to, this is an unlikely situation since the other
        // bookmark part should be in the direction we are going and the child of a paragraph but maybe consider adding code to remove the entire
        // bookmark instead so an exception isn't thrown by removing only half of it
        if (para != null) {
            //If we are moving the bookmark down insert it at the beginning of the next paragraph
            if (moveDown)
                para.prependChild(node);
            else
                //If we are moving the bookmark up insert it at the end of the previous paragraph
                para.appendChild(node);
        }
    }
}