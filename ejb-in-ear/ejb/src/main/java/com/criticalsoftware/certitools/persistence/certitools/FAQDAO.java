/*
 * $Id: FAQDAO.java,v 1.5 2009/03/13 17:37:53 jp-gomes Exp $
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
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.FAQ;
import com.criticalsoftware.certitools.persistence.GenericDAO;
import com.criticalsoftware.certitools.util.ModuleType;

import java.util.List;

/**
 * FAQ DAO
 *
 * @author jp-gomes
 */
public interface FAQDAO extends GenericDAO<FAQ, Long> {

    /**
     * Find All FAQs for displayTable by module
     *
     * @param currentPage   - displaytable current page
     * @param resultPerPage - displaytable result per page
     * @param sortCriteria  - displaytable sort
     * @param sortDirection - displaytable sort direction
     * @param moduleTypes   - FAQs modules to search
     * @return - FAQ List
     */
    List<FAQ> findAllFAQs(int currentPage, int resultPerPage, String sortCriteria,
                          String sortDirection, List<ModuleType> moduleTypes);

    /**
     * Find All FAQs by module
     *
     * @param moduleTypes - FAQs modules to search
     * @return - FAQ List
     */
    List<FAQ> findAllFAQs(List<ModuleType> moduleTypes);

    /**
     * Count All FAQs by module
     *
     * @param moduleTypes - - FAQs modules to count
     * @return - counter
     */
    int countAll(List<ModuleType> moduleTypes);

    /**
     * Find FAQ with FAQ category and Module Loaded
     *
     * @param id - FAQ ID
     * @return - FAQ
     */
    FAQ findWithCategoryAndModule(Long id);

    /**
     * Find FAQs by FAQ Category
     *
     * @param faqCategoryId - FAQ CAtegory ID
     * @return - FAQ List
     */
    List<FAQ> find(Long faqCategoryId);
}
