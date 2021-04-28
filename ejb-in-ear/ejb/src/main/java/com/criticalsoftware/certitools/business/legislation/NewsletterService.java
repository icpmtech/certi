/*
 * $Id: NewsletterService.java,v 1.10 2009/04/08 16:30:54 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/08 16:30:54 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.business.legislation;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.Configuration;
import com.criticalsoftware.certitools.util.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Newletter service interface
 *
 * @author : lt-rico
 */
public interface NewsletterService {
    /**
     * Finds all newsletter configurations
     *
     * @return a list of configurations
     */
    Collection<Configuration> findNewsletterConfigurations();

    /**
     * Updates configuration
     *
     * @param configurations configuration objects
     * @param contentType    logo file content type
     * @param inputStream    logo file input stream
     * @throws com.criticalsoftware.certitools.business.exception.BusinessException
     *          when parameters are invalid
     * @throws com.criticalsoftware.certitools.business.exception.ObjectNotFoundException
     *          when un object is not found
     * @throws IOException when a problem with reading the file
     */
    void update(Collection<Configuration> configurations, String contentType, InputStream inputStream)
            throws BusinessException, ObjectNotFoundException, IOException;

    /**
     * Sends newsletters
     */
    void sendNewsletters();

    /**
     * Subscribes a user in a certain document category newsletter
     *
     * @param userId the user id
     *@param legalDocumentCategoryId the legal document category id  @throws BusinessException       when the category does allready belong to the user
     * @throws ObjectNotFoundException when some of the objects cannot be found
     * @throws BusinessException when somethis is missing
     */
    void subscribe(Long userId, Long legalDocumentCategoryId) throws BusinessException, ObjectNotFoundException;

    /**
     * Unsubscribes a user from a ceratiain document category newsletter
     *
     * @param userId the user id
     *@param legalDocumentCategoryId a legal document category id  @throws BusinessException       when the category does not belong to the user
     * @throws ObjectNotFoundException when some of the objects are not found
     * @throws BusinessException when somethis is missing
     */
    void unsubscribe(Long userId, Long legalDocumentCategoryId) throws BusinessException, ObjectNotFoundException;


    /**
     * Finds the newsletter logo
     * @return a Map with content type as key and inputstream as value;
     * @throws com.criticalsoftware.certitools.business.exception.ObjectNotFoundException when object is not found
     */
    File findNewsletterLogo() throws ObjectNotFoundException;
}
