package com.criticalsoftware.certitools.presentation.util.export.sm;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

/**
 * Class to set header and footer of a pdf document
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
public class HeaderFooterPageEvent extends PdfPageEventHelper {

    private PdfTemplate total;
    private Image img;
    private BaseFont helv;

    public HeaderFooterPageEvent(Image img) {
        this.img = img;
    }

    public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(100, 100);
        total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
        try {
            helv = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();

        //add header image
        try {
            float width = img.getScaledWidth();
            //we want 115 px of max width
            if (width > 115) {
                img.scalePercent(115 / width * 100);
            }
            img.setAbsolutePosition(document.right() - img.getScaledWidth(), document.top() + 10);
            cb.addImage(img);
        } catch (DocumentException e) {
            //ignore
        }

        //add page number in the footer
        String text = document.getPageNumber() + "/";
        float textBase = document.bottom() - 20;
        float textSize = helv.getWidthPoint(text, 6);
        cb.beginText();
        cb.setFontAndSize(helv, 6);
        float adjust = helv.getWidthPoint("0", 6);
        cb.setTextMatrix(document.right() - textSize - adjust, textBase);
        cb.showText(text);
        cb.endText();
        cb.addTemplate(total, document.right() - adjust, textBase);
        cb.restoreState();
    }

    public void onCloseDocument(PdfWriter writer, Document document) {
        //add number of pages in the footer
        total.beginText();
        total.setFontAndSize(helv, 6);
        total.setTextMatrix(0, 0);
        total.showText(String.valueOf(writer.getPageNumber() - 1));
        total.endText();
    }
}
