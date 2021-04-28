/*
 * $Id: NewsDAO.java,v 1.6 2009/09/24 10:54:28 pjfsilva Exp $
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
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.News;
import com.criticalsoftware.certitools.entities.NewsCategory;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.Collection;
import java.util.List;

/**
 * <description>
 *
 * @author pjfsilva
 */
public interface NewsDAO extends GenericDAO<News, Long> {

    public Collection<News> findAll();

    public List<News> findAll(int currentPage, int resultPerPage, String sortCriteria, String sortDirection);

    public int countAll();

    /**
     * Count all publish news
     * @return number of news
     */
    int countAllPublish();

    public Collection<News> findAllPublished();

    public Collection<News> findNewsPublishedByCategoryId(long categoryId, int limit);

    public Collection<NewsCategory> findAllNewsCategories();

    public NewsCategory findNewsCategory(long id);

}