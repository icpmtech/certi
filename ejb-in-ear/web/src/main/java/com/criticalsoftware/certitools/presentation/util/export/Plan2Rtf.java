/*
 * $Id: Plan2Rtf.java,v 1.4 2010/06/23 17:09:54 pjfsilva Exp $
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
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.field.RtfPageNumber;
import com.lowagie.text.rtf.field.RtfTableOfContents;
import com.lowagie.text.rtf.field.RtfTotalPageNumber;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooter;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooterGroup;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * PEI Export to RTF
 *
 * @author : lt-rico
 */
public class Plan2Rtf {

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


    public Plan2Rtf(Locale l, String logoPath, String peiName, String authorName, String version, Date versionDate,
                   Date simulationDate, InputStream companyLogo, InputStream installationPhoto,
                   List<Folder> folderToExport) {
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
    public ByteArrayOutputStream generateRTF() throws PDFException {
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

        return convertPEI2Rtf();
    }


    /**
     * Generates a byte array output stream with an pdf document
     *
     * @return ByteArrayOutputStream the stream
     *
     * @throws PDFException when something goes wrong
     */
    private ByteArrayOutputStream convertPEI2Rtf() throws PDFException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            //Set the document properties
            setProperties(document);
            //Set the print writer
            RtfWriter2 writer = RtfWriter2.getInstance(document, baos);

            writer.setAutogenerateTOCEntries(true);

            setHeaderFooter(headerLeft, headerRight, writer);

            document.open();//Open the document
            Image img = installationPhoto != null ? PlanExportUtil.getImage(installationPhoto) : null;
            for (int i = 0; i < 5; i++) {
                document.add(new Paragraph(Chunk.NEWLINE));
            }
            createCoverTable(document, img);
            document.newPage();

            // Create a Paragraph and add the table of contents to it
            Paragraph par = new Paragraph(new Chunk(Chunk.NEWLINE));
            par.add(new RtfTableOfContents(getMessage("pei.export.table.of.contents.update")));
            par.setFont(getFontNormal());
            document.add(par);

            buildDocument(null, document, folderToExport, resources.getLocale(), false);//Let's build it
            document.close();//Close it
            writer.close();
        } catch (DocumentException e) {
            throw new PDFException("Error while creating RTF " + e.getMessage(), e);
        } catch (IOException e) {
            throw new PDFException("Error while creating RTF " + e.getMessage(), e);
        }
        return baos;
    }

    private void setHeaderFooter(Image iLeft, Image iRight, RtfWriter2 writer) throws BadElementException {
        Table header = new Table(2);
        header.setBorder(Table.NO_BORDER);
        header.setWidth(100);
        Cell cell;
        if (iLeft != null) {
            iLeft.scaleAbsoluteWidth(iLeft.getWidth() / 5);
            iLeft.scaleAbsoluteHeight(iLeft.getHeight() / 5);
            iLeft.setAlignment(Image.ALIGN_LEFT);
            cell = new Cell(iLeft);
        } else {
            cell = new Cell(" ");
        }
        cell.setHorizontalAlignment(Cell.ALIGN_LEFT);
        cell.setBorder(Cell.NO_BORDER);
        header.addCell(cell);
        Paragraph p = new Paragraph(" ");
        p.setAlignment(Paragraph.ALIGN_RIGHT);
        cell = new Cell(p);
        if (iRight != null) {

            if (iRight.getWidth() > 150) {
                iRight.scaleAbsoluteWidth(150);
                iRight.scaleAbsoluteHeight(iRight.getHeight() / iRight.getWidth() * 150);
                
            }
            if (iRight.getScaledHeight() > 150) {
                iRight.scaleAbsoluteHeight(150);
                iRight.scaleAbsoluteWidth(iRight.getWidth() / iRight.getHeight() * 150);
            }
            iRight.setAlignment(Image.ALIGN_RIGHT);
            cell.addElement(iRight);
        }
        cell.setHorizontalAlignment(Cell.ALIGN_RIGHT);
        cell.setBorder(Cell.NO_BORDER);
        header.addCell(cell);

        RtfHeaderFooter headerFooter = new RtfHeaderFooter(header);
        writer.setHeader(headerFooter);

        //First Page
        Paragraph firstPageFooter =
                new Paragraph(resources.getString("pei.export.pdf.footer.copyright"), getFontSmaller());
        firstPageFooter.setAlignment(Paragraph.ALIGN_LEFT);

        //Other pages
        RtfHeaderFooterGroup footers = new RtfHeaderFooterGroup();
        Table footer = buildFooter(resources);

        footers.setHeaderFooter(new RtfHeaderFooter(firstPageFooter), RtfHeaderFooter.DISPLAY_FIRST_PAGE);
        footers.setHeaderFooter(new RtfHeaderFooter(footer), RtfHeaderFooter.DISPLAY_LEFT_PAGES);
        footers.setHeaderFooter(new RtfHeaderFooter(footer), RtfHeaderFooter.DISPLAY_RIGHT_PAGES);

        writer.setFooter(footers);
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


    /**
     * Creates the first page
     *
     * @param document the document
     * @param img      image logo
     * @throws IOException       when something goes wrong reading data
     * @throws DocumentException when something goes wrong writing to document
     */
    private void createCoverTable(Document document, Image img) throws IOException, DocumentException {

        //PEI NAME IN BLUE
        Table table = new Table(1);
        table.setBorder(Table.NO_BORDER);
        table.setWidth(100);
        Font font = getFontBig();
        font.setColor(Color.BLUE);
        font.setStyle(Font.BOLD);
        font.setSize(34);
        Cell cell = new Cell(new Paragraph(peiName, font));
        cell.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell.setVerticalAlignment(Cell.ALIGN_TOP);
        cell.setBorder(Cell.NO_BORDER);
        table.addCell(cell);
        document.add(table);

        //PEI ATTRS
        table = new Table(2);
        table.setCellsFitPage(true);
        table.setBorder(Table.NO_BORDER);
        table.setAlignment(Table.ALIGN_LEFT);
        table.setWidth(100);
        table.setWidths(new float[]{150, 300});

        font = getFontSmall();
        font.setStyle(Font.BOLD);
        Paragraph p1 = new Paragraph(getMessage("pei.version") + ":", font);
        p1.setAlignment(Paragraph.ALIGN_LEFT);
        cell = new Cell(p1);
        Paragraph p2 = new Paragraph(version != null ? version : "", getFontSmall());
        p2.setAlignment(Paragraph.ALIGN_LEFT);
        cell.addElement(p2);
        Paragraph p3 = new Paragraph(getMessage("pei.versionDate") + ":", font);
        p3.setAlignment(Paragraph.ALIGN_LEFT);
        cell.addElement(p3);
        Paragraph p4 = new Paragraph(versionDate != null ? dateFormat.format(versionDate) : "", getFontSmall());
        p4.setAlignment(Paragraph.ALIGN_LEFT);
        cell.addElement(p4);
        Paragraph p5 = new Paragraph(simulationDate != null ? getMessage("pei.simulationDate") + ":" : "", font);
        p5.setAlignment(Paragraph.ALIGN_LEFT);
        cell.addElement(p5);
        Paragraph p6 = new Paragraph(simulationDate != null ? dateFormat.format(simulationDate) : "", getFontSmall());
        p6.setAlignment(Paragraph.ALIGN_LEFT);
        cell.addElement(p6);
        Paragraph p7 = new Paragraph(getMessage("pei.authorName") + ":", font);
        p7.setAlignment(Paragraph.ALIGN_LEFT);
        cell.addElement(p7);
        Paragraph p8 = new Paragraph(authorName != null ? authorName : "", getFontSmall());
        p8.setAlignment(Paragraph.ALIGN_LEFT);
        cell.addElement(p8);

        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(cell);

        if (img != null) {
            if (img.getWidth() > 295) {
                img.scaleAbsoluteWidth(295);
                img.scaleAbsoluteHeight(img.getHeight() / img.getWidth() * 295);
            }
            img.setAlignment(Image.ALIGN_RIGHT);
            Paragraph p = new Paragraph(" ");
            p.setAlignment(Paragraph.ALIGN_RIGHT);
            cell = new Cell(p);
            cell.setHorizontalAlignment(Cell.ALIGN_RIGHT);
            cell.addElement(img);
        } else {
            cell = new Cell(" ");
        }
        cell.setBorder(Cell.NO_BORDER);

        table.addCell(cell);

        document.add(table);
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

    public static Table buildFooter(ResourceBundle resources) throws BadElementException {
        //First Page
        Paragraph firstPageFooter =
                new Paragraph(resources.getString("pei.export.pdf.footer.copyright"), getFontSmaller());
        firstPageFooter.setAlignment(Paragraph.ALIGN_LEFT);

        //Other pages
        Table footer = new Table(2);
        footer.setBorder(Table.NO_BORDER);
        footer.setWidth(100);
        /* Left Footer Cell*/
        Paragraph leftPhrase =
                new Paragraph(resources.getString("pei.export.pdf.footer.copyright"), getFontSmaller());
        Cell leftCell = new Cell(leftPhrase);
        leftCell.setBorder(Cell.NO_BORDER);
        leftCell.setHorizontalAlignment(Cell.ALIGN_LEFT);
        footer.addCell(leftCell);

        /* Right Footer Cell*/
        Paragraph rigthPhrase =
                new Paragraph(resources.getString("pei.export.pdf.footer.page.number") + " ",
                        getFontSmaller());
        rigthPhrase.add(new RtfPageNumber());
        rigthPhrase.add(" " + resources.getString("pei.export.pdf.footer.page.of") + " ");
        rigthPhrase.add(new RtfTotalPageNumber());
        Cell rightCell = new Cell(rigthPhrase);
        rightCell.setHorizontalAlignment(Cell.ALIGN_RIGHT);
        rightCell.setBorder(Cell.NO_BORDER);
        footer.addCell(rightCell);

        return footer;
    }

}