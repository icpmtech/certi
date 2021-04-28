/*
 * $Id: LicenseDAO.java,v 1.4 2009/03/13 17:37:53 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/13 17:37:53 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.license;

import com.criticalsoftware.certitools.entities.License;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.List;

/**
 * License DAO
 *
 * @author jp-gomes
 */
public interface LicenseDAO extends GenericDAO<License, Long> {

    /**
     * Get All Licenses
     *
     * @param currentPage   - current page displayed
     * @param resultPerPage - results to show, per page
     * @param sortCriteria  - sort criteria
     * @param sortDirection - sort direction
     * @return - Founded lincenses
     */
    List<License> findAll(int currentPage, int resultPerPage, String sortCriteria,
                          String sortDirection);

    /**
     * Counts all licenses
     *
     * @return number of results
     */
    int countAll();
}
