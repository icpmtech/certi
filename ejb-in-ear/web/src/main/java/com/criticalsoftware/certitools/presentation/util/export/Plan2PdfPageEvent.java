/*
 * $Id: Plan2PdfPageEvent.java,v 1.1 2009/09/24 16:48:07 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/09/24 16:48:07 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util.export;

import com.criticalsoftware.certitools.util.Logger;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;

import java.util.ResourceBundle;

/**
 * <insert description here>
 *
 * @author : lt-rico
 */
public class Plan2PdfPageEvent extends PdfPageEventHelper {

    private static final Logger LOGGER = Logger.getInstance(Plan2PdfPageEvent.class);
    private ResourceBundle resources;

    private Image headerLeft;
    private Image headerRight;
    private PdfTemplate total;
    private BaseFont helv;
    private PdfGState gstate;
    private boolean isToc;


    public Plan2PdfPageEvent(ResourceBundle resources, Image headerLeft, Image headerRight, boolean isToc) {
        this.resources = resources;
        this.headerLeft = headerLeft;
        this.headerRight = headerRight;
        this.isToc = isToc;
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        if (!isToc) {
            total = writer.getDirectContent().createTemplate(100, 100);
            total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
        }
        try {
            helv = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            LOGGER.error("Error while opening the pdf document", e);
        }
        gstate = new PdfGState();
        gstate.setFillOpacity(0.3f);
        gstate.setStrokeOpacity(0.3f);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        String text = resources.getString("pei.export.pdf.footer.page.number") + " " + writer.getPageNumber() + " " +
                resources.getString("pei.export.pdf.footer.page.of") + " ";
        float headerBase = document.top() + 10;
        float footerBase = document.bottom() - 30;
        float textSize = helv.getWidthPoint(text, 6);
        float adjust = helv.getWidthPoint("0", 6);

        PdfContentByte cb = writer.getDirectContent();

        //writting header
        cb.setGState(gstate);
        try {
            if (headerRight != null) {
                headerRight.scalePercent(72);
                headerRight.setAbsolutePosition(document.right() - headerRight.getScaledWidth() + 20, headerBase);
                cb.addImage(headerRight);
            }
            if (headerLeft != null) {
                cb.addImage(headerLeft, headerLeft.getWidth() / 6, 0, 0, headerLeft.getHeight() / 6, document.left(),
                        headerBase);
            }
        } catch (DocumentException e) {
            LOGGER.error("Error while adding logo to PDF", e);
        }

        //writting footer
        cb.beginText();
        cb.setFontAndSize(helv, 6);
        cb.setTextMatrix(document.left(), footerBase);
        cb.showText(resources.getString("pei.export.pdf.footer.copyright"));
        cb.endText();

        if (isToc) {
            if (cb.getPdfDocument().getPageNumber() > 1) {
                String page = "";
                for (int i = 0; i < cb.getPdfDocument().getPageNumber(); i++) {
                    page = page + "i";
                }
                cb.beginText();
                cb.setFontAndSize(helv, 6);
                cb.setTextMatrix(document.right() - textSize - adjust, footerBase);
                cb.showText(page);
                cb.endText();
            }
        } else {
            cb.beginText();
            cb.setFontAndSize(helv, 6);
            cb.setTextMatrix(document.right() - textSize - adjust, footerBase);
            cb.showText(text);
            cb.endText();
            cb.addTemplate(total, document.right() - adjust, footerBase);
        }

        cb.sanityCheck();
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        if (!isToc) {
            total.beginText();
            total.setFontAndSize(helv, 6);
            total.setTextMatrix(0, 0);
            total.showText(String.valueOf(writer.getPageNumber() - 1));
            total.endText();
        }
    }
}
