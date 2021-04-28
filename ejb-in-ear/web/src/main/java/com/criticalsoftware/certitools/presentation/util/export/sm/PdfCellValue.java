package com.criticalsoftware.certitools.presentation.util.export.sm;

/**
 * Utility class used to add values to pdf table cells
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
public class PdfCellValue {

    private String label;
    private String value;
    private boolean breakLine;

    public PdfCellValue(String label, String value) {
        this.label = label;
        this.value = value;
        this.breakLine = false;
    }

    public PdfCellValue(String label, String value, boolean breakLine) {
        this.label = label;
        this.value = value;
        this.breakLine = breakLine;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isBreakLine() {
        return breakLine;
    }

    public void setBreakLine(boolean breakLine) {
        this.breakLine = breakLine;
    }
}
