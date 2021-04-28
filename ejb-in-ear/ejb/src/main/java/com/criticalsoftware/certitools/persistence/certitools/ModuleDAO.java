/*
 * $Id: ModuleDAO.java,v 1.4 2009/03/20 17:26:53 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/20 17:26:53 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.sm.SubModule;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.persistence.GenericDAO;
import com.criticalsoftware.certitools.util.ModuleType;

import java.util.List;

/**
 * Module DAO
 *
 * @author jp-gomes
 */
public interface ModuleDAO extends GenericDAO<Module, Long> {

    /**
     * Find module with the specified moduletype
     *
     * @param moduleType - module Type to find
     * @return - module
     */
    Module findModuleByModuleType(ModuleType moduleType);


    /**
     * Find Module with FAQ Category loaded
     *
     * @param moduleType - module Type to find
     * @return - module
     */
    Module findWithFAQCategory(ModuleType moduleType);

    SubModule insertSubModule(SubModule subModule);

    void deleteContractSubModules(Long contractId);

    boolean existsSubModuleByContractAndType(Long contractId, SubModuleType subModuleType);

    List<SubModuleType> findContractSubModules(Long contractId);
}
