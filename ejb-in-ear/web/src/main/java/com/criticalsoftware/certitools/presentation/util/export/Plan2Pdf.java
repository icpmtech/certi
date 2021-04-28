/*
 * $Id: Plan2Pdf.java,v 1.4 2010/06/23 17:09:54 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/06/23 17:09:54 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util.export;

import com.criticalsoftware.certitools.business.exception.PDFException;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import static com.criticalsoftware.certitools.presentation.util.export.Plan2PdfUtil.*;
import com.criticalsoftware.certitools.util.Configuration;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * <insert description here>
 *
 * @author : lt-rico
 */
public class Plan2Pdf {

    private ResourceBundle resources;
    private SimpleDateFormat dateFormat;

    private String logoPath;
    private Image headerLeft, headerRight;

    //attributes of PEI
    private String peiName, authorName, version;
    private Date versionDate;
    private Date simulationDate;
    private List<Folder> folderToExport;
    private InputStream companyLogo, installationPhoto;


    public Plan2Pdf(Locale l, String logoPath, String peiName, String authorName, String version, Date versionDate,
                    Date simulationDate, InputStream companyLogo, InputStream installationPhoto,
                    java.util.List<Folder> folderToExport) {
        this.logoPath = logoPath;
        this.peiName = peiName;
        this.authorName = authorName;
        this.version = version;
        this.versionDate = versionDate;
        this.simulationDate = simulationDate;
        this.companyLogo = companyLogo;
        this.installationPhoto = installationPhoto;
        this.folderToExport = folderToExport;
        resources = ResourceBundle.getBundle("StripesResources", l);
        dateFormat = new SimpleDateFormat(Configuration.getInstance().getDatePattern(), l);


    }

    @SuppressWarnings({"unchecked"})
    public ByteArrayOutputStream generatePDF() throws PDFException {
        try {
            headerLeft = Image.getInstance(logoPath);
            if (companyLogo != null) {
                headerRight = PlanExportUtil.getImage(companyLogo);
            }
        } catch (BadElementException e) {
            throw new PDFException("Error creating header footer images", e);
        } catch (IOException e) {
            throw new PDFException("Error reading header footer images", e);
        }

        return addCoverAndToc(convertPEI2Pdf());
    }


    /**
     * Generates a byte array output stream with an pdf document
     *
     * @return ByteArrayOutputStream the stream
     *
     * @throws PDFException when something goes wrong
     */
    private ByteArrayOutputStream convertPEI2Pdf() throws PDFException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            //Set the document properties
            setProperties(document);
            //Set the print writer
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPdfVersion(PdfWriter.VERSION_1_7);
            writer.setViewerPreferences(PdfWriter.PageModeUseOutlines | PdfWriter.FitWindow);
            writer.setStrictImageSequence(true);
            writer.setPageEvent(new Plan2PdfPageEvent(resources, headerLeft, headerRight, false));
            document.open();//Open the document
            buildDocument(writer, document, folderToExport, resources.getLocale(), true);//Let's build it
            document.close();//Close it
            writer.close();
        } catch (DocumentException e) {
            throw new PDFException("Error while creating PDF " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PDFException("Error while creating PDF " + e.getMessage(), e);
        }
        return baos;
    }

    // changes page numbers and shift bookmarks
    @SuppressWarnings({"unchecked"})
    private ByteArrayOutputStream addCoverAndToc(ByteArrayOutputStream baos) throws PDFException {
        ByteArrayOutputStream fBaos = new ByteArrayOutputStream();
        try {
            PdfReader pei = new PdfReader(baos.toByteArray());
            ArrayList bookmarks = new ArrayList(SimpleBookmark.getBookmark(pei));

            Image img = installationPhoto != null ? PlanExportUtil.getImage(installationPhoto) : null;
            PdfReader toc = new PdfReader(createTOC(img, bookmarks).toByteArray());

            Document document = new Document(toc.getPageSizeWithRotation(1));
            setProperties(document);
            PdfSmartCopy copy = new PdfSmartCopy(document, fBaos);

            document.open();

            for (int i = 1; i <= toc.getNumberOfPages(); i++) {
                copy.addPage(copy.getImportedPage(toc, i));
            }
            for (int i = 1; i <= pei.getNumberOfPages(); i++) {
                copy.addPage(copy.getImportedPage(pei, i));
            }

            SimpleBookmark.shiftPageNumbers(bookmarks, toc.getNumberOfPages(), null);

            copy.setOutlines(bookmarks);
            document.close();
            toc.close();
            pei.close();
            copy.close();
        } catch (IOException e) {
            throw new PDFException("Error while reading the PEI PDF " + e.getMessage(), e);
        } catch (DocumentException e) {
            throw new PDFException("Error while adding content to PEI PDF " + e.getMessage(), e);
        }


        return fBaos;
    }

    private ByteArrayOutputStream createTOC(Image img, List bookmarks) throws PDFException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            setProperties(document);

            //Set the print writer
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPdfVersion(PdfWriter.VERSION_1_7);
            writer.setViewerPreferences(PdfWriter.PageModeUseOutlines | PdfWriter.FitWindow);
            writer.setPageEvent(new Plan2PdfPageEvent(resources, headerLeft, headerRight, true));

            document.open();//Open the document
            setProperties(document);

            for (int i = 0; i < 5; i++) {
                document.add(new Paragraph(Chunk.NEWLINE));
            }
            document.add(createCoverTable(img));
            document.newPage();
            PdfPTable tableToc = createTable(new float[]{10, 1});
            document.add(createTableToc(tableToc, bookmarks, 0));

            document.close();//Close it
        } catch (DocumentException e) {
            throw new PDFException("Error while creating PDF " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PDFException("Error while creating PDF " + e.getMessage(), e);
        }

        return baos;
    }

    // CONFIGURATION PDF    
    private void setProperties(Document document) {
        //Set the document properties
        document.setPageSize(PageSize.A4);
        document.setMargins(70, 50, 70, 70);

        //Set the document metadata
        document.addTitle(peiName);
        document.addSubject(peiName);
        document.addKeywords(peiName);
        document.addCreator(getMessage("application.title"));
        document.addAuthor(getMessage("application.title"));
        document.addHeader("Expires", "0");
    }

    private PdfPTable createTableToc(PdfPTable toc, java.util.List list, int depth) {
        Font font = getFontNormal();
        font.setSize(font.getSize() - depth);
        if (font.getSize() < getFontSmaller().getSize()) {
            font = getFontSmaller();
        }
        Chunk link;
        String info;
        int p;
        //float y;
        //PdfAction action;
        for (Object o : list) {
            Map bookmark = (Map) o;
            String spacing = "";
            for (int i = 0; i < depth; i++) {
                spacing = spacing + "\t\t";
            }
            link = new Chunk(spacing + bookmark.get("Title"));
            info = (String) bookmark.get("Page");
            p = Integer.parseInt(info.substring(0, info.indexOf(' ')));
            //y = Float.parseFloat(info.substring(info.lastIndexOf(' ') + 1) + "f");
            //action = PdfAction.gotoLocalPage(p, new PdfDestination(PdfDestination.FITH, y), writer);
            //link.setAction(action);
            link.setFont(font);

            toc.addCell(createPhraseCell(new Phrase(link), 2, 0));
            toc.addCell(createPhraseCell(new Phrase("" + p, font), 2, 0));

            java.util.List kids = (java.util.List) bookmark.get("Kids");
            if (kids != null) {
                int k = depth + 1;
                createTableToc(toc, kids, k);
            }
        }
        return toc;
    }


    /**
     * Creates the first page
     *
     * @param img image logo
     * @return PdfPTable a table
     *
     * @throws IOException       when something goes wrong reading data
     * @throws DocumentException when something goes wrong writing to document
     */
    private PdfPTable createCoverTable(Image img) throws IOException, DocumentException {
        Font font = getFontBig();
        font.setColor(Color.BLUE);
        font.setStyle(Font.BOLD);
        font.setSize(42);

        PdfPTable table = createTable(new float[]{5f, 0.1f, 10f});

        PdfPCell cell = createPhraseCell(new Paragraph(peiName, font), 15, 5);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
        table.addCell(cell);

        PdfPTable inner = createTable(1);
        font = getFontSmall();
        font.setStyle(Font.BOLD);
        inner.addCell(createPhraseCell(new Paragraph(getMessage("pei.version") + ":", font), 3, 0));
        inner.addCell(
                createPhraseCell(new Paragraph(version != null ? version : "", getFontSmall()), 3,
                        0));
        inner.addCell(createPhraseCell(new Paragraph(getMessage("pei.versionDate") + ":", font), 3, 0));
        inner.addCell(createPhraseCell(
                new Paragraph(versionDate != null ? dateFormat.format(versionDate) : "", getFontSmall()), 3, 0));

        inner.addCell(createPhraseCell(
                new Paragraph(simulationDate != null ? getMessage("pei.simulationDate") + ":" : "", font), 3, 0));
        inner.addCell(createPhraseCell(
                new Paragraph(simulationDate != null ? dateFormat.format(simulationDate) : "", getFontSmall()), 3, 0));
        inner.addCell(createPhraseCell(new Paragraph(getMessage("pei.authorName") + ":", font), 3, 0));
        inner.addCell(
                createPhraseCell(new Paragraph(authorName != null ? authorName : "", getFontSmall()), 3,
                        0));

        cell = new PdfPCell(inner);
        cell.setPadding(5);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(cell);

        table.addCell(createPhraseCell(new Phrase(), 0, 0));

        table.addCell(
                img != null ? createImageCell(img, true, 0, 0) : createPhraseCell(new Phrase(""), 0, 0));

        return table;
    }


    /**
     * Get a resource from the resource bundle
     *
     * @param messageKey the message key
     * @param arguments  var args parameters to be included on th emessage
     * @return the resource string
     */
    private String getMessage(String messageKey, String... arguments) {
        return new MessageFormat(resources.getString(messageKey)).format(arguments);
    }

}
