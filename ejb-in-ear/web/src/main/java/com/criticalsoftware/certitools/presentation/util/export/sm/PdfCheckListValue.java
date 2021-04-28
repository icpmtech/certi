package com.criticalsoftware.certitools.presentation.util.export.sm;

/**
 * Utility class used to add checked/unchecked values to pdf table cells
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
public class PdfCheckListValue {

    private String value;
    private boolean selected;

    public PdfCheckListValue(String value, boolean selected) {
        this.value = value;
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
