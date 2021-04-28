/*
 * $Id: DisplayTagPDFExporterImage.java,v 1.3 2009/10/20 22:21:00 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/20 22:21:00 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Template11Mirror;
import com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement;
import com.criticalsoftware.certitools.util.*;
import com.lowagie.text.*;
import com.lowagie.text.Cell;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.displaytag.Messages;
import org.displaytag.exception.BaseNestableJspTagException;
import org.displaytag.exception.SeverityEnum;
import org.displaytag.export.BinaryExportView;
import org.displaytag.export.PdfView;
import org.displaytag.model.*;
import org.displaytag.model.Row;
import org.displaytag.util.TagConstants;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.jsp.JspException;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The code from this is taken from displaytag PdfView and only changed to allow the insertion of images in PDF
 *
 * @author pjfsilva
 */
public class DisplayTagPDFExporterImage implements BinaryExportView {

    /** TableModel to render. */
    private TableModel model;

    /** export full list? */
    private boolean exportFull;

    /** include header in export? */
    private boolean header;

    /** decorate export? */
    private boolean decorated;

    /**
     * This is the table, added as an Element to the PDF document. It contains all the data, needed to represent the
     * visible table into the PDF
     */
    private Table tablePDF;

    /** The default font used in the document. */
    private Font smallFont;

    private static final Logger LOGGER = Logger.getInstance(DisplayTagPDFExporterImage.class);


    /** @see org.displaytag.export.ExportView#setParameters(TableModel, boolean, boolean, boolean) */
    public void setParameters(TableModel tableModel, boolean exportFullList, boolean includeHeader,
                              boolean decorateValues) {
        this.model = tableModel;
        this.exportFull = exportFullList;
        this.header = includeHeader;
        this.decorated = decorateValues;
    }

    /**
     * Initialize the main info holder table.
     *
     * @throws BadElementException for errors during table initialization
     */
    protected void initTable() throws BadElementException {
        tablePDF = new Table(this.model.getNumberOfColumns());
        tablePDF.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
        tablePDF.setCellsFitPage(true);
        tablePDF.setWidth(100);

        tablePDF.setPadding(2);
        tablePDF.setSpacing(0);

        smallFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL, new Color(0, 0, 0));

    }

    /**
     * @return "application/pdf"
     *
     * @see org.displaytag.export.BaseExportView#getMimeType()
     */
    public String getMimeType() {
        return "application/pdf"; //$NON-NLS-1$
    }

    /**
     * The overall PDF table generator.
     *
     * @throws JspException        for errors during value retrieving from the table model
     * @throws BadElementException IText exception
     */
    protected void generatePDFTable() throws JspException, BadElementException {
        if (this.header) {
            generateHeaders();
        }
        tablePDF.endHeaders();
        generateRows();
    }

    /** @see org.displaytag.export.BinaryExportView#doExport(java.io.OutputStream) */
    public void doExport(OutputStream out) throws JspException {
        try {
            // Initialize the table with the appropriate number of columns
            initTable();

            // Initialize the Document and register it with PdfWriter listener and the OutputStream
            Document document = new Document(PageSize.A4.rotate(), 60, 60, 40, 40);
            document.addCreationDate();
            HeaderFooter footer = new HeaderFooter(new Phrase(TagConstants.EMPTY_STRING, smallFont), true);
            footer.setBorder(Rectangle.NO_BORDER);
            footer.setAlignment(Element.ALIGN_CENTER);

            PdfWriter.getInstance(document, out);

            // Fill the virtual PDF table with the necessary data
            generatePDFTable();
            document.open();
            document.setFooter(footer);
            document.add(this.tablePDF);
            document.close();

        }
        catch (Exception e) {
            throw new PdfGenerationException(e);
        }
    }

    /**
     * Generates the header cells, which persist on every page of the PDF document.
     *
     * @throws BadElementException IText exception
     */
    protected void generateHeaders() throws BadElementException {
        Iterator iterator = this.model.getHeaderCellList().iterator();

        while (iterator.hasNext()) {
            HeaderCell headerCell = (HeaderCell) iterator.next();

            String columnHeader = headerCell.getTitle();

            if (columnHeader == null) {
                columnHeader = StringUtils.capitalize(headerCell.getBeanPropertyName());
            }

            Cell hdrCell = getCell(columnHeader);
            hdrCell.setGrayFill(0.9f);
            hdrCell.setHeader(true);
            tablePDF.addCell(hdrCell);

        }
    }

    /**
     * Generates all the row cells.
     *
     * @throws JspException        for errors during value retrieving from the table model
     * @throws BadElementException errors while generating content
     */
    protected void generateRows() throws JspException, BadElementException {
        // get the correct iterator (full or partial list according to the exportFull field)
        RowIterator rowIterator = this.model.getRowIterator(this.exportFull);
        // iterator on rows
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // iterator on columns
            ColumnIterator columnIterator = row.getColumnIterator(this.model.getHeaderCellList());

            while (columnIterator.hasNext()) {
                Column column = columnIterator.nextColumn();

                // Get the value to be displayed for the column
                Object value = column.getValue(this.decorated);

                Cell cell = getCell(ObjectUtils.toString(value));
                tablePDF.addCell(cell);
            }
        }
    }

    /**
     * Returns a formatted cell for the given value. CERTITOOLS-CHANGED
     *
     * @param value cell value
     * @return Cell
     *
     * @throws BadElementException errors while generating content
     */
    private Cell getCell(String value) throws BadElementException {
        // include image in PDF

        // first check if value starts with img tag
        if (value != null && value.toString().trim().startsWith("<img src=")) {
            String urlImage = parseUrlImage(value);

            // then check if img src is to plan
            if (!urlImage.contains("plan/Plan.action?viewResource")) {
                getCellDefault(value);
            }

            // now let's parse the contact photo and get it
            try {
                byte[] imageByteArray = getTemplate5ContactsElementPhoto(urlImage);

                if (imageByteArray == null) {
                    getCell("");
                }

                Cell cell = new Cell();
                Image image = Image.getInstance(imageByteArray);
                image.scaleToFit(50, 50);
                cell.add(image);
                cell.setHorizontalAlignment(Cell.ALIGN_CENTER);
                return cell;

            } catch (IOException e) {
                LOGGER.info("[getCell] - Invalid URL passed. Cell value:" + value);
                getCell("");
            }
        }

        return getCellDefault(value);
    }

    /**
     * This is the default getCell from display tag CERTITOOLS-CHANGED
     *
     * @param value cell value
     * @return cell
     *
     * @throws BadElementException when value is valid
     */
    private Cell getCellDefault(String value) throws BadElementException {
        // normal behaviour
        Cell cell = new Cell(new Chunk(StringUtils.trimToEmpty(value), smallFont));
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setLeading(8);
        return cell;
    }

    /**
     * Analyses the urlFolder and returns the byte array of the contact photo CERTITOOLS-CHANGED
     *
     * @param urlFolder img src url (eg.  /plan/Plan.action?viewResource&path=/Contactos/folders/c3&planModuleType=PRV&peiId=5&order=1)
     * @return byte array of the photo
     */
    private byte[] getTemplate5ContactsElementPhoto(String urlFolder) {
        // parse url and get path, moduleType, peiId and order
        urlFolder = StringUtils.substring(urlFolder, urlFolder.indexOf("/plan"));
        urlFolder = StringUtils.substring(urlFolder, urlFolder.indexOf("viewResource&") + "viewResource&".length());

        // Example urlFolder:
        //          /plan/Plan.action?viewResource&path=/Contactos/folders/c3&planModuleType=PRV&peiId=5&order=1&onlineOffline=offline
        //          path=/Contactos/folders/c3&planModuleType=PRV&peiId=5&order=1&onlineOffline=offline

        StringTokenizer st = new StringTokenizer(urlFolder, "&");
        String token;

        // path
        token = st.nextToken();
        String path = StringUtils.substring(token, "path=".length());

        // planModuleType
        token = st.nextToken();
        String planModuleType = StringUtils.substring(token, "planModuleType=".length());

        // peiId
        token = st.nextToken();
        String peiId = StringUtils.substring(token, "peiId=".length());

        // order (ignore)
        token = st.nextToken();
        String order = StringUtils.substring(token, "order=".length());

        // online or offline
        token = st.nextToken();
        String onlineOffline = StringUtils.substring(token, "onlineOffline=".length());

        // now get the folder according to the parameters parsed
        try {
            InitialContext initialContext = new InitialContext();
            PlanService planService = (PlanService) initialContext.lookup("certitools/PlanService");

            Folder folder = planService.findFolderAllAllowed(
                    "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + planModuleType + peiId + "/" + onlineOffline + path, false);

            if (folder.getTemplate() instanceof Template11Mirror) {
                Template11Mirror template11Mirror = (Template11Mirror) folder.getTemplate();
                String pathToLoad = template11Mirror.getSourcePath();
                if (com.criticalsoftware.certitools.util.PlanUtils.getOnlineOrOfflineFromPath(folder.getPath())
                        .equals("online")) {
                    pathToLoad = pathToLoad.replaceFirst("offline", "online");
                }
                Folder sourceFolder = planService.findFolderAllAllowed(pathToLoad, false);
                if (sourceFolder != null && (sourceFolder.getTemplate() instanceof Template5ContactsElement)) {
                    folder.setTemplate(sourceFolder.getTemplate());
                } else {
                    return null;
                }
            }

            if (!(folder.getTemplate() instanceof Template5ContactsElement)) {
                return null;
            }

            Template5ContactsElement template = (Template5ContactsElement) folder.getTemplate();
            InputStream inputStream = template.getPhoto().getData();

            if (inputStream == null) {
                return null;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[32768];
            int n;

            while ((n = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, n);
            }

            return outputStream.toByteArray();

        } catch (NamingException e) {
            LOGGER.info("[getTemplate5ContactsElementPhoto] - unable to get planService");
        } catch (Exception e) {
            LOGGER.error("[getTemplate5ContactsElementPhoto] - Exception: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Parses html string containing a img and returns only the src string CERTITOOLS-CHANGED
     *
     * @param html string with a img tag
     * @return src from img tag
     */
    private String parseUrlImage(String html) {
        String regexpIMG = "src=\"(.*)\"";
        Pattern patt = Pattern.compile(regexpIMG, Pattern.MULTILINE);
        Matcher m = patt.matcher(html);

        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    /**
     * Wraps IText-generated exceptions.
     *
     * @author Fabrizio Giustina
     * @version $Revision: 1.3 $ ($Author: pjfsilva $)
     */
    static class PdfGenerationException extends BaseNestableJspTagException {

        /** D1597A17A6. */
        private static final long serialVersionUID = 899149338534L;

        /**
         * Instantiate a new PdfGenerationException with a fixed message and the given cause.
         *
         * @param cause Previous exception
         */
        public PdfGenerationException(Throwable cause) {
            super(PdfView.class, Messages.getString("PdfView.errorexporting"), cause); //$NON-NLS-1$
        }

        /** @see org.displaytag.exception.BaseNestableJspTagException#getSeverity() */
        public SeverityEnum getSeverity() {
            return SeverityEnum.ERROR;
        }
    }
}