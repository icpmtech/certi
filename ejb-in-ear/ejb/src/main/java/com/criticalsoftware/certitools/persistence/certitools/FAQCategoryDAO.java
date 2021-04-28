/*
 * $Id: FAQCategoryDAO.java,v 1.3 2009/03/19 22:46:33 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/19 22:46:33 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.FAQCategory;
import com.criticalsoftware.certitools.persistence.GenericDAO;
import com.criticalsoftware.certitools.util.ModuleType;

import java.util.List;

/**
 * FAQ Category DAO
 *
 * @author jp-gomes
 */
public interface FAQCategoryDAO extends GenericDAO<FAQCategory, Long> {

    /**
     * Return FAQ Categories for auto-complete.
     *
     * @param name        - name no search
     * @param moduleType- module to search
     * @return - FAQ CAtegory list
     */
    List<FAQCategory> findByNameToAutoComplete(String name, ModuleType moduleType);
}
