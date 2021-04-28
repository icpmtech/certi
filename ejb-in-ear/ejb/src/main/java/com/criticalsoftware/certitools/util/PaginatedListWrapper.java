/*
 * $Id: PaginatedListWrapper.java,v 1.4 2009/06/05 02:09:12 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/06/05 02:09:12 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.util;

import java.util.List;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class PaginatedListWrapper<T> {
    /** Sort direction. */
    public enum Direction {
        /** Ascending order. */
        ASC("ASC"),
        /** Descending order. */
        DESC("DESC"),
        /** Xpath Ascending order. */
        ASCENDING("ascending"),
        /** Xpath Descending order. */
        DESCENDING("descending");

        private final String value;

        Direction(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    private int currentPage;
    private int resultsPerPage;
    private int fullListSize;
    private String sortCriterion;
    private Direction sortDirection;

    private Boolean export;

    private List<T> list = null;

    /**
     * Paginator List Wrapper.
     *
     * @param currentPage    the currentPage selected
     * @param resultsPerPage the number of resultsPerPage
     * @param sortCriterion  the sortCriterion applied
     * @param sortDirection  the sortDirection applied
     */
    public PaginatedListWrapper(int currentPage, int resultsPerPage, String sortCriterion, Direction sortDirection) {
        this.currentPage = currentPage;
        this.resultsPerPage = resultsPerPage;
        this.sortCriterion = sortCriterion;
        this.sortDirection = sortDirection;
        this.export = Boolean.FALSE;
    }

    public PaginatedListWrapper(int currentPage, int resultsPerPage, String sortCriterion, Direction sortDirection,
                                Boolean export) {
        this.currentPage = currentPage;
        this.resultsPerPage = resultsPerPage;
        this.sortCriterion = sortCriterion;
        this.sortDirection = sortDirection;
        this.export = export;
    }

    /**
     * Gets the currentPage.
     *
     * @return the currentPage
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Sets the currentPage.
     *
     * @param currentPage the currentPage to be set
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = (currentPage <= 0) ? 1 : currentPage;
    }

    /**
     * Gets the resultsPerPage.
     *
     * @return the resultsPerPage
     */
    public int getResultsPerPage() {
        return resultsPerPage;
    }

    /**
     * Sets the resultsPerPage.
     *
     * @param resultsPerPage the resultsPerPage to be set
     */
    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    /**
     * Gets the fullListSize.
     *
     * @return the fullListSize
     */
    public int getFullListSize() {
        return fullListSize;
    }

    /**
     * Sets the fullListSize.
     *
     * @param fullListSize the fullListSize to be set
     */
    public void setFullListSize(int fullListSize) {
        this.fullListSize = fullListSize;

        if (currentPage != 0 && resultsPerPage != 0) {
            if (fullListSize / (currentPage * resultsPerPage) == 0) {

                int counter = 1;
                while (fullListSize / (counter * resultsPerPage) > 0 && fullListSize != (counter * resultsPerPage)) {
                    counter++;
                }
                currentPage = counter;
            }
        }
    }

    /**
     * Gets the sortCriterion.
     *
     * @return the sortCriterion
     */
    public String getSortCriterion() {
        return sortCriterion;
    }

    /**
     * Sets the sortCriterion.
     *
     * @param sortCriterion the sortCriterion to be set
     */
    public void setSortCriterion(String sortCriterion) {
        this.sortCriterion = sortCriterion;
    }

    /**
     * Gets the sortDirection.
     *
     * @return the sortDirection
     */
    public Direction getSortDirection() {
        return sortDirection;
    }

    /**
     * Sets the sortDirection.
     *
     * @param sortDirection the sortDirection to be set
     */
    public void setSortDirection(Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    /** Gets the export. */
    public Boolean getExport() {
        return export;
    }

    /**
     * Sets the export.
     *
     * @param export the export to be set
     */
    public void setExport(Boolean export) {
        this.export = export;
    }

    /**
     * Gets the list.
     *
     * @return the list
     */
    public List<T> getList() {
        return list;
    }

    /**
     * Sets the list.
     *
     * @param list the list to be set
     */
    public void setList(List<T> list) {
        this.list = list;
    }

    /**
     * Gets the offset.
     *
     * @return the offset
     */
    public int getOffset() {
        return (currentPage - 1) * resultsPerPage;
    }

    /**
     * Gets the limit.
     *
     * @return the limit
     */
    public int getLimit() {
        return getOffset() + resultsPerPage;
    }
}

