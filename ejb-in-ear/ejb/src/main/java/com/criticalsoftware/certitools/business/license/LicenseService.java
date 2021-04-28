/*
 * $Id: LicenseService.java,v 1.3 2009/03/09 18:33:43 haraujo Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/03/09 18:33:43 $
 * Last changed by $Author: haraujo $
 */
package com.criticalsoftware.certitools.business.license;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.entities.License;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;

/**
 * License Service Interface
 *
 * @author haraujo
 */
public interface LicenseService {

    /**
     * Get a List of all licenses
     *
     * @param paginatedListWrapper to data transfer
     * @return paginatedListWrapper with data transfer
     *
     * @throws BusinessException when error
     */
    PaginatedListWrapper<License> findAll(PaginatedListWrapper<License> paginatedListWrapper)
            throws BusinessException;

    /**
     * Insert new License in Database
     *
     * @param license - license to insert
     * @throws BusinessException - when error
     */
    void insert(License license) throws BusinessException;

    /**
     * Find License by Id
     *
     * @param id - license Id
     * @return - License found
     */
    License find(Long id);

    /**
     * Validate Application License
     *
     * @return - true if it is valid, false if is isn t
     *
     * @throws BusinessException - when error
     */
    Boolean validateLicense() throws BusinessException;

}
