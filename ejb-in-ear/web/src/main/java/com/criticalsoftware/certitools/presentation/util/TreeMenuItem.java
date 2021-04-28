package com.criticalsoftware.certitools.presentation.util;

/**
 * Created by aurelio-santos on 6/18/15.
 */
public class TreeMenuItem {
    private String action, event;
    private int depth, openItems;
    private String label;
    private boolean enabled;

    public TreeMenuItem(String action, String event, int depth, String label, boolean enabled) {
        this.action = action;
        this.event = event;
        this.depth = depth;
        this.label = label;
        this.enabled = enabled;
    }

    public TreeMenuItem(String action, String event, int depth, String label, int openItems, boolean enabled) {
        this.action = action;
        this.event = event;
        this.depth = depth;
        this.label = label;
        this.openItems = openItems;
        this.enabled = enabled;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getOpenItems() {
        return openItems;
    }

    public void setOpenItems(int openItems) {
        this.openItems = openItems;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
