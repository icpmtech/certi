package com.criticalsoftware.certitools.presentation.util.export.sm;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;

import java.awt.*;

/**
 * Utility class to add cells to pdf tables
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
public class PdfUtils {

    private Font fontWhiteBoldBig;
    private Font fontWhiteNormalBig;
    private Font fontBold;
    private Font fontNormal;
    private Font fontNormalSmall;
    private Font fontGrayNormalSmall;
    private static PdfUtils pdfUtils;


    protected PdfUtils() {
        fontWhiteBoldBig = new Font(Font.HELVETICA, 14, Font.BOLD, Color.WHITE);
        fontWhiteNormalBig = new Font(Font.HELVETICA, 14, Font.NORMAL, Color.WHITE);
        fontBold = new Font(Font.HELVETICA, 10, Font.BOLD);
        fontNormal = new Font(Font.HELVETICA, 10, Font.NORMAL);
        fontNormalSmall = new Font(Font.HELVETICA, 9, Font.NORMAL);
        fontGrayNormalSmall = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY);
    }

    public static PdfUtils getInstance() {
        if (pdfUtils == null) {
            pdfUtils = new PdfUtils();
        }
        return pdfUtils;
    }

    public PdfPCell buildTableHeaderRow(String title1, String title2) {
        PdfPCell cell = new PdfPCell();
        Paragraph paragraph = new Paragraph(title1, fontWhiteNormalBig);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        paragraph = new Paragraph(title2, fontWhiteBoldBig);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        cell.setColspan(2);
        cell.setGrayFill(0.7f);
        cell.setFixedHeight(50);
        return cell;
    }

    public PdfPCell buildTableSignatureCell(int colspan, float grayFill, float height, String name, String label) {
        PdfPCell cell = new PdfPCell();
        setCellProperties(cell, colspan, grayFill, height);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        Paragraph paragraph = new Paragraph(name, fontNormal);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        paragraph = new Paragraph(label, fontNormalSmall);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);
        return cell;
    }

    public PdfPCell buildTableCell(int colspan, float grayFill, float height, boolean alignCenter, boolean bold, String value) {
        PdfPCell cell = new PdfPCell();
        setCellProperties(cell, colspan, grayFill, height);

        Paragraph paragraph = new Paragraph();
        if (value != null) {
            Chunk chunk = new Chunk(value);
            if (bold) {
                chunk.setFont(fontBold);
            } else {
                chunk.setFont(fontNormal);
            }
            paragraph.add(chunk);
            if (alignCenter) {
                paragraph.setAlignment(Element.ALIGN_CENTER);
            } else {
                paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
            }
        }
        paragraph.setSpacingBefore(0);
        cell.addElement(paragraph);
        return cell;
    }

    public PdfPCell buildTableCell(int colspan, float grayFill, float height, boolean alignCenter, PdfCellValue... values) {
        PdfPCell cell = new PdfPCell();
        setCellProperties(cell, colspan, grayFill, height);

        for (PdfCellValue value : values) {
            Paragraph paragraph = new Paragraph();
            Chunk chunk;
            if (value.getLabel() != null) {
                chunk = new Chunk(value.getLabel() + ": ", fontBold);
                paragraph.add(chunk);
            }
            if (value.isBreakLine()) {
                if (alignCenter) {
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                } else {
                    paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
                }
                cell.addElement(paragraph);
                paragraph = new Paragraph();
            }
            if (value.getValue() != null) {
                chunk = new Chunk(value.getValue(), fontNormal);
                paragraph.add(chunk);
            }
            if (alignCenter) {
                paragraph.setAlignment(Element.ALIGN_CENTER);
            } else {
                paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
            }
            cell.addElement(paragraph);
        }

        return cell;
    }

    public PdfPCell buildTableCell(int colspan, float grayFill, float height, boolean alignCenter, String label, String... values) {
        PdfPCell cell = new PdfPCell();
        setCellProperties(cell, colspan, grayFill, height);

        Paragraph paragraph = new Paragraph();
        Chunk chunk = new Chunk(label + ": ", fontBold);
        paragraph.add(chunk);
        if (alignCenter) {
            paragraph.setAlignment(Element.ALIGN_CENTER);
        } else {
            paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        }
        cell.addElement(paragraph);

        for (String value : values) {
            paragraph = new Paragraph();
            chunk = new Chunk(value, fontNormal);
            paragraph.add(chunk);
            if (alignCenter) {
                paragraph.setAlignment(Element.ALIGN_CENTER);
            } else {
                paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
            }
            cell.addElement(paragraph);
        }

        return cell;
    }


    public PdfPCell buildTableCell(int colspan, float grayFill, float height, boolean alignCenter, Image checked,
                                   Image unchecked, String label, PdfCheckListValue... values) {
        PdfPCell cell = new PdfPCell();
        setCellProperties(cell, colspan, grayFill, height);

        Paragraph paragraph = new Paragraph();
        Chunk chunk = new Chunk(label + ": ", fontBold);
        paragraph.add(chunk);
        if (alignCenter) {
            paragraph.setAlignment(Element.ALIGN_CENTER);
        } else {
            paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        }
        cell.addElement(paragraph);

        //scale checkbox images
        float imgHeight = checked.getScaledHeight();
        //we want 8 px of height
        if (imgHeight > 8) {
            checked.scalePercent(8 / imgHeight * 100);
            unchecked.scalePercent(8 / imgHeight * 100);
        }

        for (PdfCheckListValue value : values) {
            paragraph = new Paragraph();

            if (value.isSelected()) {
                chunk = new Chunk(checked, 0, 0);
            } else {
                chunk = new Chunk(unchecked, 0, 0);
            }
            paragraph.add(chunk);
            paragraph.add(new Chunk("\t\t"));

            chunk = new Chunk(value.getValue(), fontNormal);
            paragraph.add(chunk);
            if (alignCenter) {
                paragraph.setAlignment(Element.ALIGN_CENTER);
            } else {
                paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
            }
            cell.addElement(paragraph);
        }

        return cell;
    }

    public Font getFontWhiteBoldBig() {
        return fontWhiteBoldBig;
    }

    public Font getFontWhiteNormalBig() {
        return fontWhiteNormalBig;
    }

    public Font getFontBold() {
        return fontBold;
    }

    public Font getFontNormal() {
        return fontNormal;
    }

    public Font getFontNormalSmall() {
        return fontNormalSmall;
    }

    public Font getFontGrayNormalSmall() {
        return fontGrayNormalSmall;
    }

    private void setCellProperties(PdfPCell cell, int colspan, float grayFill, float height) {
        cell.setPaddingLeft(6);
        cell.setPaddingRight(6);
        cell.setPaddingTop(0);
        cell.setPaddingBottom(6);
        cell.setGrayFill(grayFill);
        cell.setMinimumHeight(height);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setColspan(colspan);
    }
}
