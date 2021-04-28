/*
 * $Id: NewsService.java,v 1.19 2009/09/24 10:54:28 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/09/24 10:54:28 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.entities.News;
import com.criticalsoftware.certitools.entities.NewsCategory;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;

import java.util.Collection;

/**
 * News Service Interface
 *
 * @author pjfsilva
 */
public interface NewsService {

    /**
     * Returns the news with the specified id
     *
     * @param id id of the news to find
     * @return news object with the specified id
     */
    public News findNews(long id) throws ObjectNotFoundException;

    /**
     * Returns all news
     *
     * @return all news
     */
    public Collection<News> findAllNews();

    /**
     * Returns all news according to the params in paginatedListWrapper
     *
     * @param paginatedListWrapper with the parameters for the search
     * @return news according to the params in paginatedListWrapper
     * @throws BusinessException if the paginatedListWrapper is null
     */
    public PaginatedListWrapper<News> findAllNews(PaginatedListWrapper<News> paginatedListWrapper)
            throws BusinessException;

    /**
     * Finds all published news from a certain category
     * @param newsCategoryId the news category id
     * @return a collection of news
     */
    public Collection<News> findAllNewsPublished(long newsCategoryId);

    /**
     * Returns all the news categories
     *
     * @return all the news categories
     */
    public Collection<NewsCategory> findAllNewsCategories();

    /**
     * Returns a collection of the newscategories with the latest news for each category.
     *
     * @param limit number of news to retrieve, per category
     * @return collection of the newscategories with the latest news for each category
    */
    public Collection<NewsCategory> findLastNewsPublished(int limit);

    /**
     * Insert a new News
     *
     * @param news object to insert
     * @return the objected that was inserted
     * @throws ObjectNotFoundException if the news category is invalid
     */
    public News insertNews(News news) throws ObjectNotFoundException;

    /**
     * Deletes the news with the specified id
     *
     * @param id id of the news to delete
     * @throws ObjectNotFoundException news not found
     */
    public void deleteNews(long id) throws ObjectNotFoundException;

    /**
     * Updates news
     * @param news The news object to update
     * @throws ObjectNotFoundException if the news category or the news is invalid
     */
    public void updateNews(News news) throws ObjectNotFoundException;

    /**
     * Updates the news Categories
     *
     * @param newsCategory1 name of the first news category
     * @param newsCategory2 name of the second news category
     */
    public void updateNewsCategory(String newsCategory1, String newsCategory2);

    /**
     * Toggles the published status of a news. If the news is published it is unpublished and the opposite
     *
     * @param id id of the news to update
     * @throws ObjectNotFoundException if the news id is invalid
     * @return the published status of the news
     */
    public boolean toggleNewsPublishedStatus(long id) throws ObjectNotFoundException;
}