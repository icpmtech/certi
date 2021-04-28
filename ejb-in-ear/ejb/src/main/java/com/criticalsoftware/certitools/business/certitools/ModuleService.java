/*
 * $Id: ModuleService.java,v 1.2 2009/03/13 17:37:53 jp-gomes Exp $
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
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.entities.Module;

import java.util.Collection;

/**
 * Module Service
 *
 * @author jp-gomes
 */
public interface ModuleService {

    /**
     * Find All Modules in Application
     *
     * @return - All Modules
     */
    Collection<Module> findAll();
}
