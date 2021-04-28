/*
 * $Id: DisplayTagSupportActionBean.java,v 1.14 2009/07/08 11:27:13 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/07/08 11:27:13 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.action;

import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.controller.StripesConstants;
import org.displaytag.properties.SortOrderEnum;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <description>
 *
 * @author jp-gomes
 */
public abstract class DisplayTagSupportActionBean extends AbstractActionBean {

    private static final String DIRECTION_ASC = "asc";
    private static final String DIRECTION_DESC = "desc";

    private List<Integer> columnsNoLinks;

    private Integer page;
    private String sort;
    private String dir;

    private String exportXLS;
    private String exportCSV;
    private String exportXML;
    private String exportPDF;

    public Integer getPage() {
        if (page != null) {
            return page;
        } else {
            return 1;
        }
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public List<Integer> getColumnsNoLinks() {
        return columnsNoLinks;
    }

    public void setColumnsNoLinks(List<Integer> columnsNoLinks) {
        this.columnsNoLinks = columnsNoLinks;
    }

    public String getExportXLS() {
        return exportXLS;
    }

    public void setExportXLS(String exportXLS) {
        this.exportXLS = exportXLS;
    }

    public String getExportCSV() {
        return exportCSV;
    }

    public void setExportCSV(String exportCSV) {
        this.exportCSV = exportCSV;
    }

    public String getExportXML() {
        return exportXML;
    }

    public void setExportXML(String exportXML) {
        this.exportXML = exportXML;
    }

    public String getExportPDF() {
        return exportPDF;
    }

    public void setExportPDF(String exportPDF) {
        this.exportPDF = exportPDF;
    }

    public PaginatedListWrapper.Direction getDirOrder() {
        if (dir != null) {
            if (dir.equalsIgnoreCase(DIRECTION_ASC)) {
                return PaginatedListWrapper.Direction.ASC;
            } else if (dir.equalsIgnoreCase(DIRECTION_DESC)) {
                return PaginatedListWrapper.Direction.DESC;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setDirOrder(SortOrderEnum order) {
        if (order.equals(SortOrderEnum.ASCENDING)) {
            dir = DIRECTION_ASC;
        } else if (order.equals(SortOrderEnum.DESCENDING)) {
            dir = DIRECTION_DESC;
        } else {
            dir = null;
        }
    }

    public Boolean isExportRequest() {
        String tokens[];
        try {
            tokens = getContext().getRequest().getQueryString().split("d-\\b(.*?)-e(.*?)(=)");
            if (tokens.length > 1) {
                return true;
            } else {
                for (Object mapEntry : getContext().getRequest().getParameterMap().keySet()) {
                    if (mapEntry.toString().matches("d-\\b(.*?)-e")) {
                        return true;
                    }
                }
            }
            return false;
        } catch (NullPointerException e) {
            return Boolean.FALSE;
        }
    }

    /**
     * Returns a new parameter map to be add to Redirect Resolution This is done so the action cannot loose the display
     * tag export values
     *
     * @return Map<String, Object> an HashMap representation
     */
    public Map<String, Object> getDisplayTagParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (getPage() != null) {
            parameters.put("page", getPage());
        }
        if (getSort() != null && getSort().length() > 0) {
            parameters.put("sort", getSort());
        }
        if (getDir() != null && getDir().length() > 0) {
            parameters.put("dir", getDir());
        }
        return parameters;
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public Resolution check() throws Exception {
        if (getContext().getRequest().getParameter("page") != null) {
            try {
                Integer.parseInt(getContext().getRequest().getParameter("page"));
            } catch (NumberFormatException e) {
                Map<String, String> newParam = new HashMap<String, String>();
                Iterator it = getContext().getRequest().getParameterMap().keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String[] value = (String[]) getContext().getRequest().getParameterMap().get(key);
                    newParam.put(key, key.equals("page") ? "1" : value[0]);
                }

                ActionBean bean =
                        (ActionBean) getContext().getRequest().getAttribute(StripesConstants.REQ_ATTR_ACTION_BEAN);
                return new RedirectResolution(bean.getClass()).addParameters(newParam);
            }
        }
        return null;
    }
}

