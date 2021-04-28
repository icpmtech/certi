/*
 * $Id: ConfigurationService.java,v 1.3 2009/11/11 05:26:33 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/11/11 05:26:33 $
 * Last changed by : $Author: pjfsilva $
 */

package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.entities.Configuration;
import com.criticalsoftware.certitools.entities.User;

import java.util.Map;
import java.util.Collection;

/**
 * Configuration Service Interface
 *
 * @author jp-gomes
 */

public interface ConfigurationService {

    /**
     * Finds all Configurations in Database
     *
     * @return - Map with configurations: configuration key and respective value
     */
    Map<String, String> findAllInMap();

    /**
     * Get all configurations
     *
     * @return a collection
     */
    Collection<Configuration> findAll();

    /**
     * Sets a new master password
     * @param password password to set
     * @param userInSession user in session
     */
    void updateMasterPassword(String password, User userInSession) throws BusinessException;

    /**
     * Updates configuration
     *
     * @param configurations configuration objects
     * @throws BusinessException       when parameters are invalid
     * @throws ObjectNotFoundException when un object is not found
     */
    void update(Collection<Configuration> configurations) throws BusinessException, ObjectNotFoundException;
}
