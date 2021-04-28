/*
 * $Id: FAQService.java,v 1.9 2009/04/08 16:37:46 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/08 16:37:46 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.entities.FAQ;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.FAQCategory;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;

import java.util.List;

/**
 * FAQ Service
 *
 * @author jp-gomes
 */
public interface FAQService {

    /**
     * Find all FAQCategory, for autocomplete
     *
     * @param name       -FAQCategory name to search
     * @param userId     -user id
     * @param moduleType -module to search
     * @return -FAQ Category list
     *
     * @throws CertitoolsAuthorizationException
     *                                 - when trying acess to unauthorized modules
     * @throws ObjectNotFoundException -when user not found
     */
    List<FAQCategory> findFaqCategoryByNameToAutoComplete(String name, Long userId, ModuleType moduleType)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Find All with display table
     *
     * @param paginatedListWrapper - wrapper
     * @param userId               - user id
     * @return paginatedListWrapper with display table configurations
     *
     * @throws BusinessException       - when error
     * @throws ObjectNotFoundException - when user not found
     */
    PaginatedListWrapper<FAQ> findAllFAQ(PaginatedListWrapper<FAQ> paginatedListWrapper, Long userId)
            throws BusinessException, ObjectNotFoundException;

    /**
     * Insert FAQ
     *
     * @param userId - user id
     * @param faq    - FAQ to insert
     * @throws ObjectNotFoundException - when user not found
     * @throws CertitoolsAuthorizationException
     *                                 - when trying acess to unauthorized modules
     */
    void insertFAQ(Long userId, FAQ faq) throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Find user allowed Modules
     *
     * @param userId - userId
     * @return - List with all allowed modules
     *
     * @throws ObjectNotFoundException - when user not found
     */
    List<ModuleType> findUserModulesAllowed(Long userId) throws ObjectNotFoundException;

    /**
     * Find Module with FAQCategories loaded (FAQs is optional)
     *
     * @param user        - user
     * @param moduleType- module to load
     * @param loadFAQ     - when is necessary to load FAQ
     * @return - Module loaded
     *
     * @throws CertitoolsAuthorizationException
     *                                 -  when trying acess to unauthorized modules
     * @throws ObjectNotFoundException - when user not found
     */
    Module findModuleFAQCategories(User user, ModuleType moduleType, Boolean loadFAQ)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Delete FAQ
     *
     * @param id     - FAQ id to delete
     * @param userId - userId
     * @throws ObjectNotFoundException - when user not found
     * @throws CertitoolsAuthorizationException
     *                                 -  when trying acess to unauthorized modules
     */
    void deleteFAQ(Long id, Long userId) throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Find FAQ with FAQCategory and Module Loaded
     *
     * @param id      - FAQ id
     * @param userId- userId
     * @return - FAQ loaded
     *
     * @throws ObjectNotFoundException - when user not found
     * @throws CertitoolsAuthorizationException
     *                                 -  when trying acess to unauthorized modules
     */
    FAQ findFAQWithCategoryAndModule(Long id, Long userId)
            throws ObjectNotFoundException, CertitoolsAuthorizationException;

    /**
     * Update FAQ
     *
     * @param faq    - FAQ to update
     * @param userId - userId
     * @throws CertitoolsAuthorizationException
     *                                 -  when trying acess to unauthorized modules
     * @throws ObjectNotFoundException - when user not found
     */
    void updateFAQ(FAQ faq, Long userId) throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Find all FAQs that user is authorized to see
     *
     * @param userId - user userId
     * @return - FAQs list
     *
     * @throws ObjectNotFoundException - when user not found
     */
    List<FAQ> findAllFAQ(Long userId) throws ObjectNotFoundException;

    /**
     * Count all FAQs that user is authorized to see
     *
     * @param userId -userId
     * @return - FAQs count
     *
     * @throws ObjectNotFoundException -  when user not found
     */
    Integer countAll(Long userId) throws ObjectNotFoundException;
}
