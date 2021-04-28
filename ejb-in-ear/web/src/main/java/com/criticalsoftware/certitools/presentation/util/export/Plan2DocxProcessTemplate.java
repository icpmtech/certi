/*
 * $Id: Plan2DocxProcessTemplate.java,v 1.24 2010/12/30 19:33:23 pjfsilva Exp $
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
import com.aspose.words.Shape;
import com.criticalsoftware.certitools.business.exception.AsposeException;
import com.criticalsoftware.certitools.entities.jcr.*;
import com.criticalsoftware.certitools.presentation.util.ValidationUtils;
import com.criticalsoftware.certitools.presentation.util.export.mailmergedatasource.Template12MeansResourcesDataSource;
import com.criticalsoftware.certitools.presentation.util.export.mailmergedatasource.Template5ContactsDataSource;
import com.criticalsoftware.certitools.presentation.util.export.mailmergedatasource.Template8RiskAnalysisDataSource;
import com.criticalsoftware.certitools.util.*;
import com.lowagie.text.Image;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Does the exportation of templates.
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.24 $
 */
@SuppressWarnings({"unchecked"})
public class Plan2DocxProcessTemplate {
    private static final Logger LOGGER = Logger.getInstance(Plan2DocxProcessTemplate.class);
    private static final String SECTION_SEPARATOR = " - ";

    /**
     * Writes the areas of a clickable areas template
     *
     * @param documentBuilder builder to output
     * @param imageMap        html from the template
     * @throws IOException error inserting content
     */
    protected static void processTemplate1DiagramAreas(DocumentBuilder documentBuilder, String imageMap)
            throws Exception {
        int headingStyle = StyleIdentifier.HEADING_1;

        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode allNodes = cleaner.clean(imageMap);

        // find previous heading and set headingStyle to the correct heading for the areas
        Paragraph currentParagraph = documentBuilder.getCurrentParagraph();
        Node tempNode = currentParagraph.getPreviousSibling();

        boolean keepGoing = true;
        try {
            while (tempNode != null && keepGoing) {
                if (tempNode instanceof Paragraph) {
                    if (((Paragraph) tempNode).getParagraphFormat().isHeading()) {
                        headingStyle = ((Paragraph) tempNode).getParagraphFormat().getStyleIdentifier() + 1;
                        keepGoing = false;
                    }
                }
                tempNode = tempNode.getPreviousSibling();
            }
        } catch (Exception e) {
            LOGGER.error("[processDiagramAreas] Error finding previous heading style");
            throw e;
        }

        // get all area tags and process them
        TagNode[] areaNodes = allNodes.getElementsByName("area", true);
        String title;
        String text;

        for (TagNode areaNode : areaNodes) {
            title = areaNode.getAttributeByName("title");
            if (!StringUtils.isBlank(title)) {
                // try to find the first \n or \r
                int i = title.indexOf('\n');
                if (i == -1) {
                    i = title.indexOf('\r');
                }
                if (i != -1) {
                    // break in title and text
                    text = StringEscapeUtils.unescapeHtml(StringUtils.trim(title.substring(i)));
                    title = StringEscapeUtils.unescapeHtml(StringUtils.trim(title.substring(0, i)));

                    try {
                        if (!StringUtils.isBlank(title)) {
                            documentBuilder.getParagraphFormat().setStyleIdentifier(headingStyle);
                            documentBuilder.writeln(title);
                        }
                        documentBuilder.getParagraphFormat().setStyleIdentifier(StyleIdentifier.NORMAL);
                        documentBuilder.writeln(text);
                    } catch (Exception e) {
                        LOGGER.error("[processDiagramAreas] Error inserting areas text.");
                        throw e;
                    }
                }
            }
        }
    }

    /**
     * This method parses and inserts the rich text text provided in the DOCX document.
     *
     * @param documentBuilder docx builder
     * @param folder          folder to insert
     * @param richTextHTML    text to insert
     * @param planFolders     all the folders of the plan (online or offline)
     * @throws com.criticalsoftware.certitools.business.exception.AsposeException
     *                             problem inserting content
     * @throws java.io.IOException problem reading images or other files
     */
    @SuppressWarnings({"unchecked"})
    protected static void processRichTextTemplates(DocumentBuilder documentBuilder, Folder folder, String richTextHTML,
                                                   List<Folder> planFolders)
            throws AsposeException, IOException {

        try {
            richTextHTML =
                    cleanHTMLForExport(richTextHTML, Configuration.getInstance().getDocxExportRemoveStyleAttribute());
            richTextHTML = richTextHTML.replaceAll("&apos;", "'");
        } catch (IOException e) {
            LOGGER.error("Error cleaning HTML for export");
            throw e;
        }

        // Needed to center all tables
        ArrayList<Node> oldTables = new ArrayList<Node>();
        if (Configuration.getInstance().getDocxExportCenterTables()) {
            oldTables = new ArrayList<Node>();
            java.util.List oldList =
                    Arrays.asList(documentBuilder.getDocument().getChildNodes(NodeType.TABLE, true).toArray());
            oldTables.addAll(oldList);
        }

        // get all old paragraphs
        /*
        ArrayList<Node> oldParagraphs = new ArrayList<Node>();
        if (Configuration.getInstance().getDocxExportApplyAligmnentParagraphsFix()) {
            oldParagraphs = new ArrayList<Node>();
            java.util.List oldList2 =
                    Arrays.asList(documentBuilder.getDocument().getChildNodes(NodeType.PARAGRAPH, true).toArray());
            oldParagraphs.addAll(oldList2);
        }
        */

        try {
            // find all certitools images to "break" the text in parts
            String regexpIMG =
                    "<img[^>]+ src=\"/(plan|pei)/(Plan|PEI).action(\\?viewResource=|\\?planModuleType=[A-Z]+(&|&amp;)viewResource=)([^\">]+)\"[^>]*>";
            Pattern pattern = Pattern.compile(regexpIMG,
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.COMMENTS);
            String[] textSplited = pattern.split(richTextHTML);

            if (textSplited.length <= 1) {
                // No image found, just insert the text to docx
                documentBuilder.insertHtml(richTextHTML);
            } else {
                // images found, we need to get all the images
                Matcher m = pattern.matcher(richTextHTML);

                // we split the HTML in parts so that we can insert the partial HTML and then the IMAGE
                for (int i = 0; i < textSplited.length - 1; i++) {
                    String partialHTML = textSplited[i];
                    // Insert HTML before image
                    documentBuilder.insertHtml(partialHTML);

                    // Insert image (calculate width/height)
                    m.find();
                    String src = m.group(5);
                    src = URLDecoder.decode(src, "UTF-8");
                    src = src.substring(src.indexOf("path=") + 5);
                    ByteArrayInputStream imageStream = PlanExportUtil.findImage(planFolders, src);
                    insertImageCropped(documentBuilder, imageStream);
                }

                // insert last HTML part
                documentBuilder.insertHtml(textSplited[textSplited.length - 1]);
            }


            // To center all tables
            if (Configuration.getInstance().getDocxExportCenterTables()) {
                // Gather the array of tables after inserting
                ArrayList<Node> currentTables = new ArrayList<Node>();
                java.util.List currentList =
                        Arrays.asList(documentBuilder.getDocument().getChildNodes(NodeType.TABLE, true).toArray());
                currentTables.addAll(currentList);

                // Get the tables we just inserted by  getting the nodes which are only in currentTables
                // and not in oldTables (CT complement OT)
                for (Node oldTable : oldTables) {
                    currentTables.remove(oldTable);
                }

                /*
                Style style = documentBuilder.getDocument().getStyles().add(StyleType.PARAGRAPH, "mystyle");
                style.getParagraphFormat().setLeftIndent(0);
                style.setBaseStyle("Normal");

                Style style1 = documentBuilder.getDocument().getStyles().get("Normal Tables");
                */
                // Center new tables
                if (currentTables.size() > 0) {
                    for (Node currentTable : currentTables) {
                        Table table = (Table) currentTable;

                        /*
                        NodeCollection nodeCollection = table.getChildNodes(NodeType.PARAGRAPH, true, true);
                        for (Object o : nodeCollection) {
                            Paragraph p = (Paragraph) o;
                            //p.getParagraphFormat().setStyle(style1);
                            System.out.println("found p || " + p);
                        }
                        */

                        // insert paragraph after tables
                        // table.getParentNode().insertAfter(new Paragraph(documentBuilder.getDocument()), table);

                        // repeat header across pages
                        if (table.getRows().getCount() >= 1 &&
                                Configuration.getInstance().getDocxExportRepeatHeaderTables()) {
                            Row row = table.getRows().get(0);
                            row.getRowFormat().setHeadingFormat(true);
                        }



                        // center table
                        //old Fix
                        //for (int j = 0; j < table.getRows().getCount(); j++) {
                        //    Row row = table.getRows().get(j);

                         //   row.getRowFormat().setAlignment(RowAlignment.CENTER);
                        //}
                        // center table new fix
                        table.setAlignment(TableAlignment.CENTER);

                    }
                }
            }

            // to force paragraph alignment
            /*
            try{
            if (Configuration.getInstance().getDocxExportApplyAligmnentParagraphsFix()) {
                ArrayList<Node> currentParagraphs = new ArrayList<Node>();
                java.util.List currentList2 =
                        Arrays.asList(documentBuilder.getDocument().getChildNodes(NodeType.PARAGRAPH, true).toArray());
                currentParagraphs.addAll(currentList2);

                // Get only the paragraphs we just inserted by removing old paragraphs
                for (Node oldParagraph : oldParagraphs) {
                    currentParagraphs.remove(oldParagraph);
                }

                if (currentParagraphs.size() > 0) {
                    int normalStyleAlignment =
                            documentBuilder.getDocument().getStyles().get(StyleIdentifier.NORMAL).getParagraphFormat()
                                    .getAlignment();

                    for (Node currentP : currentParagraphs) {
                        Paragraph paragraph = (Paragraph) currentP;
                        if (paragraph.getParagraphFormat().getStyleIdentifier() == StyleIdentifier.NORMAL) {
                            paragraph.getParagraphFormat().setAlignment(normalStyleAlignment);
                        }
                    }
                }
            }
            }catch(Exception e){
                LOGGER.error("Problem applying paragraph alignments");
                LOGGER.error(e);
            }
            */

        } catch (Exception e) {
            throw new AsposeException("Error inserting html on template rich text. Folder: " + folder.getPath());
        }
    }

    /**
     * Cleans the html styles and prunes striked text and tags for exportation (docx)
     * <p/> TODO pjfsilva: insert this in help file
     * Rules to follow:
     * 1) Remove all tags: hr, style and innerHTML
     * 2) Remove all tags but keep content: span, a
     * 3) Remove all striked text
     * 4) Remove all style attributes
     *
     * @param html                 html to clean
     * @param removeStyleAttribute if we remove all style attrs or not
     * @return clean html
     * @throws IOException error with HTML Cleaner
     */
    private static String cleanHTMLForExport(String html, boolean removeStyleAttribute) throws IOException {
        html = "<div>" + html + "</div>";
        boolean removeParent = false;
        HtmlCleaner htmlCleaner = new HtmlCleaner();

        // Remove all span tags but keep inner content
        CleanerTransformations transformations = new CleanerTransformations();
        TagTransformation spanTransformation = new TagTransformation("span");
        TagTransformation aTransformation = new TagTransformation("a");
        TagTransformation fontTransformation = new TagTransformation("font");
        TagTransformation headingsTransformations;
        for (int i = 1; i <= 6; i++) {
            headingsTransformations = new TagTransformation("h" + i, "p", false);
            headingsTransformations.addAttributeTransformation("styleBold", "boldme");
            transformations.addTransformation(headingsTransformations);
        }

        if (removeStyleAttribute) {
            transformations.addTransformation(spanTransformation);
        }
        transformations.addTransformation(aTransformation);
        transformations.addTransformation(fontTransformation);
        htmlCleaner.setTransformations(transformations);

        // Remove hr and style tags AND inner content
        CleanerProperties properties = htmlCleaner.getProperties();
        properties.setOmitXmlDeclaration(true);
        properties.setOmitHtmlEnvelope(true);
        properties.setPruneTags("hr,style");

        TagNode allNodes = htmlCleaner.clean(html);

        // Remove empty paragraphs caused by removing of strike text
        TagNode[] strikeNodes = allNodes.getElementsByName("strike", true);

        if (strikeNodes != null) {
            for (TagNode strikeNode : strikeNodes) {
                //Parent only have a <strike> tag has a children, so remove...
                if (strikeNode.getParent().getChildren().size() == 1) {
                    removeParent = true;
                }
                strikeNode.removeFromTree();

                if (removeParent) {
                    strikeNode.getParent().removeFromTree();
                }
                removeParent = false;
            }
        }

        // remove all style attributes
        if (removeStyleAttribute) {
            TagNode[] nodesWithStyle = allNodes.getElementsHavingAttribute("style", true);
            for (TagNode tagNode : nodesWithStyle) {
                tagNode.removeAttribute("style");
            }
        }

        /*
        All this code is just to remove the <p> inside <li> and keep all the inside content of the paragraphs. This is
        needed because of a bug in aspose.
        find li with paragraphs inside and remove the paragraphs, keeping all the inner content
        not nested with paragraphs nested inside paragraphs
        fix for bug in aspose (4626) http://www.aspose.com/community/forums/thread/244269.aspx
         */
        TagNode[] liList = allNodes.getElementsByName("li", true);
        TagNode newLiNode;
        TagNode currentParagraphNode;
        List<TagNode> newLiNodeContentList;

        for (TagNode currentLiNode : liList) {
            newLiNode = new TagNode("li");
            newLiNodeContentList = new ArrayList<TagNode>(3);

            // traverses all the first level child tokens, if it's a paragraph, remove it but keep its
            // content. If it's another type of tag, just copy it
            // if we get a ContentToken (which is just text) also add it. This is needed to suport <li>Text</li> instead
            // of <li><p>Text</p></li>
            List allChildrens = currentLiNode.getChildren();
            for (int i = 0; i < allChildrens.size(); i++) {
                Object currentChildObject = allChildrens.get(i);
                if (currentChildObject instanceof TagNode) {
                    TagNode currentChildElement;
                    currentChildElement = (TagNode) currentChildObject;
                    if (currentChildElement.getName().equals("p")) {
                        // add all the children of the paragraph and 2 br's
                        newLiNodeContentList.addAll(currentChildElement.getChildren());

                        if (i != allChildrens.size() - 1) {
                            // no need to add BR has Aspose already adds some margin-bottom
                            //newLiNodeContentList.add(new TagNode("br"));
                            //newLiNodeContentList.add(new TagNode("br"));
                        }

                    } else {
                        newLiNodeContentList.add(currentChildElement);
                    }
                }
                // if child is just text, add it
                if (currentChildObject instanceof ContentToken) {
                    newLiNode.addChild(currentChildObject);
                }

            }
            newLiNode.addChildren(newLiNodeContentList);
            currentLiNode.getParent().addChild(newLiNode);
            currentLiNode.removeFromTree();
        }

        // find bold paragraphs and replace with strong
        TagNode[] boldParagraphs = allNodes.getElementsHavingAttribute("styleBold", true);
        TagNode strongNode;
        for (TagNode currentParagraph : boldParagraphs) {
            strongNode = new TagNode("strong");
            strongNode.addChildren(currentParagraph.getChildren());
            while (currentParagraph.getChildren().size() > 0) {
                currentParagraph.removeChild(currentParagraph.getChildren().get(0));
            }
            currentParagraph.removeAttribute("styleBold");
            currentParagraph.addChild(strongNode);
        }

        XmlSerializer xmlSerializer = new SimpleXmlSerializer(properties);
        StringWriter writer = new StringWriter();
        xmlSerializer.writeXml(allNodes, writer, "UTF-8");

        if (writer.toString().equals("<div />")) {
            return "";
        }
        return writer.toString();
    }

    public static void processTemplate4PlanClickableAnnexes(Folder folder, Map<String, InputStream> filesToExport,
                                                            String sectionTitle, DocumentBuilder documentBuilder,
                                                            boolean printListFiles) throws Exception {
        ArrayList<Folder> resourceChildren = new ArrayList<Folder>();
        PlanExportUtil.getChildrenByType(folder, resourceChildren, Template.Type.TEMPLATE_RESOURCE.getName());


        if (resourceChildren.size() > 0) {
            for (Folder resourceChild : resourceChildren) {
                String filename = "";
                TemplateResource resourceTemplate = ((TemplateResource) resourceChild.getTemplate());
                if (sectionTitle != null && !StringUtils.isBlank(sectionTitle)) {
                    filename = PlanExportUtil.sanitizeFilenameToExport(sectionTitle) + "/" +
                            PlanExportUtil.sanitizeFilenameToExport(resourceTemplate.getResource().getName());
                } else {
                    filename += PlanExportUtil.sanitizeFilenameToExport(folder.getName()) + "/" +
                            PlanExportUtil.sanitizeFilenameToExport(resourceTemplate.getResource().getName());
                }

                filesToExport.put(Plan2Docx.ANNEX_FOLDER + filename, resourceTemplate.getResource().getData());

                if (printListFiles) {
                    documentBuilder.writeln(resourceChild.getName() + " ( " + filename + " )");
                }
            }
        }
    }

    protected static void processTemplate5Contacts(Folder folder, String folderName, Document doc) throws Exception {
        LinkedList<Template5ContactsElement> external = new LinkedList<Template5ContactsElement>();
        LinkedList<Template5ContactsElement> internal = new LinkedList<Template5ContactsElement>();
        LinkedList<Template5ContactsElement> emergency = new LinkedList<Template5ContactsElement>();
        PlanExportUtil.getContacts(folder, external, internal, emergency);

        try {
            Template5ContactsDataSource contactsDataSource =
                    new Template5ContactsDataSource(external, internal, emergency, folderName);

            doc.getMailMerge().executeWithRegions(contactsDataSource);

        } catch (Exception e) {
            LOGGER.error("[processContacts] Error replacing mergeField");
            throw e;
        }
    }

    public static void processTemplate6Documents(Folder folder, Map<String, InputStream> filesToExport,
                                                 String sectionTitle,
                                                 DocumentBuilder documentBuilder, boolean printListFiles)
            throws Exception {
        ArrayList<Folder> children = new ArrayList<Folder>();
        PlanExportUtil.getChildrenByType(folder, children, Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName());

        if (children.size() > 0) {
            for (Folder child : children) {
                processTemplate6DocumentsElement(child, filesToExport, sectionTitle, documentBuilder, printListFiles);
            }
        }
    }

    public static void processTemplate6DocumentsElement(Folder folder, Map<String, InputStream> filesToExport,
                                                        String sectionTitle, DocumentBuilder documentBuilder,
                                                        boolean printListFiles) throws Exception {
        Template6DocumentsElement document = (Template6DocumentsElement) folder.getTemplate();
        List<Resource> resources = document.getResources();
        if (resources == null || resources.size() <= 0) {
            LOGGER.info("[processTemplate6DocumentsElement] No files found. Folder:" + folder.getName());
            return;
        }

        for (Resource resource : resources) {
            String filename = "";
            if (sectionTitle != null && !StringUtils.isBlank(sectionTitle)) {
                filename = PlanExportUtil.sanitizeFilenameToExport(sectionTitle) + SECTION_SEPARATOR;
            }
            filename += PlanExportUtil.sanitizeFilenameToExport(resource.getName());

            filesToExport.put(Plan2Docx.ANNEX_FOLDER + filename, resource.getData());

            if (printListFiles) {
                documentBuilder.writeln(resource.getAlias() + " (" + filename + " )");
            }
        }
    }

    public static void processTemplate6DocumentsElementImage(DocumentBuilder documentBuilder, Folder folder)
            throws Exception {
        Template6DocumentsElement document = (Template6DocumentsElement) folder.getTemplate();
        List<Resource> resources = document.getResources();
        if (resources == null || resources.size() <= 0) {
            LOGGER.info("[processTemplate6DocumentsElement] No files found. Folder:" + folder.getName());
            return;
        }

        for (Resource resource : resources) {
            if (ValidationUtils.validateImageContentType(resource.getMimeType())) {
                // insert image
                try {
                    insertImageCropped(documentBuilder, resource.getData());
                } catch (Exception e) {
                    LOGGER.error("[processTemplate6DocumentsElement] Problem inserting image into document. Folder: " +
                            folder.getName());
                    throw e;
                }
                return;
            }
        }
        LOGGER.info("[processTemplate6DocumentsElement] No image found. Folder:" + folder.getName());
    }

    public static void processTemplate7FAQ(Folder folder, DocumentBuilder documentBuilder,
                                           List<Folder> foldersToExport) throws AsposeException, IOException {
        ArrayList<Folder> children = new ArrayList<Folder>();
        PlanExportUtil.getChildrenByType(folder, children, Template.Type.TEMPLATE_FAQ_ELEMENT.getName());

        if (children.size() <= 0) {
            LOGGER.info("[processTemplate7FAQ] No FAQ Element children found");
            return;
        }

        for (Folder child : children) {
            processTemplate7FAQElement(child, documentBuilder, foldersToExport);
        }
    }

    public static void processTemplate7FAQElement(Folder folder, DocumentBuilder documentBuilder,
                                                  List<Folder> foldersToExport) throws AsposeException, IOException {

        Template7FAQElement template = (Template7FAQElement) folder.getTemplate();

        processRichTextTemplates(documentBuilder, folder, template.getQuestion(), foldersToExport);
        processRichTextTemplates(documentBuilder, folder, template.getAnswer(), foldersToExport);
    }

    protected static void processTemplate8RiskAnalysis(DocumentBuilder documentBuilder, Folder folder,
                                                       String folderName, List<Folder> foldersToExport,
                                                       Map<String, InputStream> filesToExport,
                                                       String sectionTitle) throws Exception {
        List<RiskAnalysisElement> riskAnalysis = ((Template8RiskAnalysis) folder.getTemplate()).getRiskAnalysis();
        String parentFolderPath = PlanUtils.simplifyPath(folder.getPath());

        // get all attached files referenced in the risk table
        for (RiskAnalysisElement riskAnalysisElement : riskAnalysis) {
            if (!StringUtils.isBlank(riskAnalysisElement.getFileFolderLinks())) {
                // convert folder links to folder list links
                List<String> foldersLinks = new ArrayList<String>();
                String[] split = riskAnalysisElement.getFileFolderLinks().split(";");
                for (String string : split) {
                    foldersLinks.add(parentFolderPath + "/folders" + string);
                }
                riskAnalysisElement.setFileFolderLinksLists(foldersLinks);

                // for all the links, attach the file
                for (String file : riskAnalysisElement.getFileFolderLinksLists()) {
                    Folder attachment = PlanExportUtil.findFolderFromPlanList(file, foldersToExport);

                    String filename = "";
                    if (sectionTitle != null && !StringUtils.isBlank(sectionTitle)) {
                        filename = sectionTitle + "/" +
                                PlanExportUtil.sanitizeFilenameToExport(
                                        ((TemplateResource) attachment.getTemplate()).getResource().getName());
                    } else {
                        filename = PlanExportUtil.sanitizeFilenameToExport(folder.getName()) + "/" +
                                PlanExportUtil.sanitizeFilenameToExport(
                                        ((TemplateResource) attachment.getTemplate()).getResource().getName());
                    }
                    filesToExport.put(Plan2Docx.ANNEX_FOLDER + filename,
                            ((TemplateResource) attachment.getTemplate()).getResource().getData());
                }
            }
        }

        // insert risk analysys table
        try {
            Template8RiskAnalysisDataSource riskDataSource =
                    new Template8RiskAnalysisDataSource(folderName, riskAnalysis);
            documentBuilder.getDocument().getMailMerge().executeWithRegions(riskDataSource);
        } catch (Exception e) {
            LOGGER.error("[processTemplateRiskAnalysis] Error replacing mergeField");
            throw e;
        }
    }

    protected static void processTemplate10Procedure(DocumentBuilder documentBuilder, Folder folder,
                                                     int heading, List<Folder> foldersToExport,
                                                     Map<String, InputStream> filesToExport,
                                                     String sectionTitle) {
        ArrayList<Folder> firstLevelFolders = new ArrayList<Folder>();
        ArrayList<Folder> proceduresFolders = new ArrayList<Folder>();
        ArrayList<Folder> filesAttached = new ArrayList<Folder>();
        PlanExportUtil.getProceduresContent(folder, proceduresFolders, filesAttached, -1);
        int headingStyle = StyleIdentifier.HEADING_1 + heading - 1;


        for (Folder proceduresFolder : proceduresFolders) {
            if (proceduresFolder.getDepth() == 0) {
                firstLevelFolders.add(proceduresFolder);
            }
            try {
                documentBuilder.getParagraphFormat()
                        .setStyleIdentifier(headingStyle + (int) proceduresFolder.getDepth());
                documentBuilder.writeln(proceduresFolder.getName());
                documentBuilder.getParagraphFormat().setStyleIdentifier(StyleIdentifier.NORMAL);

                String text = "";
                if (proceduresFolder.getTemplate().getName().equals(Template.Type.TEMPLATE_RICH_TEXT.getName())) {
                    text = ((Template3RichText) proceduresFolder.getTemplate()).getText();
                } else if (proceduresFolder.getTemplate().getName()
                        .equals(Template.Type.TEMPLATE_RICH_TEXT_WITH_ATTACH.getName())) {
                    text = ((Template9RichTextWithAttach) proceduresFolder.getTemplate()).getText();
                }

                if (!StringUtils.isBlank(text)) {
                    processRichTextTemplates(documentBuilder, folder, text, foldersToExport);
                }

            } catch (Exception e) {
                LOGGER.error(e);
                LOGGER.error("[processTemplateProcedure] Error writing template procedure");
            }
        }

        // process attached files
        // find first depth folders
        for (Folder fileAttached : filesAttached) {
            filesToExport.put(Plan2Docx.ANNEX_FOLDER +
                    findFilenameForProcedureAttachment(sectionTitle, fileAttached.getPath(),
                            ((TemplateResource) fileAttached.getTemplate()).getResource().getName(),
                            firstLevelFolders),
                    ((TemplateResource) fileAttached.getTemplate()).getResource().getData());
        }
    }

    protected static void processTemplate12MeansResources(Folder folder, String folderName, Document doc) throws Exception {

        LinkedList<Template12MeansResourcesElement> meansResourcesElements = new LinkedList<Template12MeansResourcesElement>();
        PlanExportUtil.getMeansResources(folder, meansResourcesElements);

        try {
            Template12MeansResourcesDataSource meansResourcesDataSource =
                    new Template12MeansResourcesDataSource(folderName, meansResourcesElements);
            doc.getMailMerge().executeWithRegions(meansResourcesDataSource);
        } catch (Exception e) {
            LOGGER.error("[processTemplate12MeansResources] Error replacing mergeField");
            throw e;
        }
    }

    private static String findFilenameForProcedureAttachment(String sectionTitle, String attachPath, String attachName,
                                                             ArrayList<Folder> firstLevelFolders) {
        if (sectionTitle != null && !StringUtils.isBlank(sectionTitle)) {
            // attach section name and first level folder
            for (Folder firstLevelFolder : firstLevelFolders) {
                if (attachPath.startsWith(firstLevelFolder.getPath())) {
                    return PlanExportUtil.sanitizeFilenameToExport(
                            sectionTitle + SECTION_SEPARATOR + firstLevelFolder.getName()) + "/" +
                            PlanExportUtil.sanitizeFilenameToExport(attachName);
                }
            }
        } else {
            // attach first level folder
            for (Folder firstLevelFolder : firstLevelFolders) {
                if (attachPath.startsWith(firstLevelFolder.getPath())) {
                    return PlanExportUtil.sanitizeFilenameToExport(firstLevelFolder.getName()) + "/" +
                            PlanExportUtil.sanitizeFilenameToExport(attachName);
                }
            }
        }
        // no first section found, just use filename
        return PlanExportUtil.sanitizeFilenameToExport(attachName);
    }

    protected static void processTemplateResource(Folder folder,
                                                  Map<String, InputStream> filesToExport, String sectionTitle) {

        String filename = "";
        if (sectionTitle != null && !StringUtils.isBlank(sectionTitle)) {
            filename = PlanExportUtil.sanitizeFilenameToExport(sectionTitle) + SECTION_SEPARATOR;
        }
        filename += PlanExportUtil
                .sanitizeFilenameToExport(((TemplateResource) folder.getTemplate()).getResource().getName());

        filesToExport.put(Plan2Docx.ANNEX_FOLDER + filename,
                ((TemplateResource) folder.getTemplate()).getResource().getData());
    }

    /**
     * Inserts an image into the document checking if it's width doesn't exceed the max. In case it exceeds, resizes
     * accordingly
     *
     * @param documentBuilder builder for the current doc
     * @param imageStream     image stream
     * @throws Exception aspose problem or IO problem
     */
    private static void insertImageCropped(DocumentBuilder documentBuilder, InputStream imageStream)
            throws Exception {
        Image image = PlanExportUtil.getImage(imageStream);
        double width = image.getWidth();
        double height = image.getHeight();
        double ratio;

        if (width > Configuration.getInstance().getDocxExportImageMaxWidth()) {
            ratio = Configuration.getInstance().getDocxExportImageMaxWidth() / width;
            width = Configuration.getInstance().getDocxExportImageMaxWidth();
            height = height * ratio;
        }

        PlanExportUtil.resetInputStream(imageStream);
        Shape shape = documentBuilder.insertImage(imageStream, ConvertUtil.pixelToPoint(width),
                ConvertUtil.pixelToPoint(height));
        shape.getParentParagraph().getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        documentBuilder.insertParagraph();
        PlanExportUtil.resetInputStream(imageStream);
    }
}