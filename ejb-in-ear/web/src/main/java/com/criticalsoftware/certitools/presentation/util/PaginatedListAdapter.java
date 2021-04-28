/*
 * $Id: PaginatedListAdapter.java,v 1.3 2009/03/09 15:26:05 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/09 15:26:05 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.util.List;

import com.criticalsoftware.certitools.util.PaginatedListWrapper;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class PaginatedListAdapter<T> implements PaginatedList {

    private PaginatedListWrapper<T> paginatedListWrapper;

    /**
     * Paginator List Adapter.
     *
     * @param paginatedListWrapper the Paginated List Wrapper
     */
    public PaginatedListAdapter(PaginatedListWrapper<T> paginatedListWrapper) {
        this.paginatedListWrapper = paginatedListWrapper;
    }

    /**
     * Gets the list.
     *
     * @return the list
     */
    public List<T> getList() {
        return paginatedListWrapper.getList();
    }

    /**
     * Gets the PageNumber.
     *
     * @return the currentPage
     */
    public int getPageNumber() {
        return paginatedListWrapper.getCurrentPage();
    }

    /**
     * Gets the objectsPerPage.
     *
     * @return the resultsPerPage
     */
    public int getObjectsPerPage() {
        if (!paginatedListWrapper.getExport()) {
            return paginatedListWrapper.getResultsPerPage();
        } else {
            return getFullListSize();
        }
    }

    /**
     * Gets the fullListSize.
     *
     * @return the fullListSize
     */
    public int getFullListSize() {
        return paginatedListWrapper.getFullListSize();
    }

    /**
     * Gets the sortCriterion.
     *
     * @return the sortCriterion
     */
    public String getSortCriterion() {
        return paginatedListWrapper.getSortCriterion();
    }

    /**
     * Gets the sortDirection.
     *
     * @return the sortDirection
     */
    public SortOrderEnum getSortDirection() {
        return (paginatedListWrapper.getSortDirection() == PaginatedListWrapper.Direction.ASC) ? SortOrderEnum.ASCENDING
                : SortOrderEnum.DESCENDING;
    }

    /**
     * Gets the searchId.
     *
     * @return null
     */
    public String getSearchId() {
        return null;
    }
}
