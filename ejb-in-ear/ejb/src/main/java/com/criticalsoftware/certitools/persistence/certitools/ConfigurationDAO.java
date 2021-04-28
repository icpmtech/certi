/*
 * $Id: ConfigurationDAO.java,v 1.3 2009/03/17 18:11:40 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/17 18:11:40 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.Configuration;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.List;
import java.util.Map;

/**
 * Configuration DAO Interface
 *
 * @author jp-gomes
 */

public interface ConfigurationDAO extends GenericDAO<Configuration, String> {
    /**
     * Finds all configurations
     *
     * @return a map containing all keys and conf
     */
    Map<String, String> findAllInMap();

    /**
     * Find all editable
     *
     * @return
     */
    List<Configuration> findAllEditable();
}
