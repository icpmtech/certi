/*
 * $Id: NewsServiceEJB.java,v 1.22 2010/03/30 17:27:15 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/03/30 17:27:15 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.persistence.certitools.NewsDAO;
import com.criticalsoftware.certitools.entities.News;
import com.criticalsoftware.certitools.entities.NewsCategory;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.util.Collection;
import java.util.List;

/**
 * News Service
 *
 * @author pjfsilva
 */
@Stateless
@Local(NewsService.class)
@LocalBinding(jndiBinding = "certitools/NewsService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class NewsServiceEJB implements NewsService {

    @EJB
    private NewsDAO newsDAO;

    @PermitAll
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public News findNews(long id) throws ObjectNotFoundException {
        News news = newsDAO.findById(id);

        if (news == null) {
            throw new ObjectNotFoundException("Can't find the news with the specified id: " + id,
                    ObjectNotFoundException.Type.NEWS);
        }
        return news;
    }

    @RolesAllowed(value = "administrator")
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Collection<News> findAllNews() {
        return newsDAO.findAll();
    }

    @RolesAllowed(value = "administrator")
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public PaginatedListWrapper<News> findAllNews(PaginatedListWrapper<News> paginatedListWrapper)
            throws BusinessException {

        if (paginatedListWrapper == null) {
            throw new BusinessException("No PaginatedListWrapper initialized ");
        }

        if (paginatedListWrapper.getSortCriterion() == null) {
            paginatedListWrapper.setSortCriterion("creationDate");
        }
        if (paginatedListWrapper.getSortDirection() == null) {
            paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.DESC);
        }

        int count = newsDAO.countAll();
        paginatedListWrapper.setFullListSize(count);

        if (paginatedListWrapper.getExport()) {
            paginatedListWrapper.setList(newsDAO.findAll(0, count, paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper.getSortDirection().value()));
        } else {
            paginatedListWrapper.setList(newsDAO.findAll(paginatedListWrapper.getOffset(),
                    paginatedListWrapper.getResultsPerPage(), paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper.getSortDirection().value()));
        }

        return paginatedListWrapper;
    }

    @RolesAllowed(value = "administrator")
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Collection<NewsCategory> findAllNewsCategories() {
        return newsDAO.findAllNewsCategories();
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Collection<NewsCategory> findLastNewsPublished(int limit) {
        Collection<NewsCategory> newsCategories = newsDAO.findAllNewsCategories();
        for (NewsCategory newsCategory : newsCategories) {
            newsCategory.setNews(newsDAO.findNewsPublishedByCategoryId(newsCategory.getId(), limit));
        }
        return newsCategories;
    }

    @RolesAllowed(value = "administrator")
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public News insertNews(News news) throws ObjectNotFoundException {
        NewsCategory newsCategory = newsDAO.findNewsCategory(news.getCategory().getId());

        if (newsCategory == null) {
            throw new ObjectNotFoundException("Can't find the news category with the specified id.",
                    ObjectNotFoundException.Type.NEWS_CATEGORIES);
        } else {
            return newsDAO.insert(news);
        }
    }

    @RolesAllowed(value = "administrator")
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void deleteNews(long id) throws ObjectNotFoundException {
        News newsInDb = newsDAO.findById(id);

        if (newsInDb == null){
            throw new ObjectNotFoundException("Can't find the news with the specified id: " + id,
                    ObjectNotFoundException.Type.NEWS);
        }
        newsDAO.delete(newsInDb);
    }

    @RolesAllowed(value = "administrator")
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void updateNews(News news) throws ObjectNotFoundException {
        NewsCategory newsCategory = newsDAO.findNewsCategory(news.getCategory().getId());
        News newsInDb = newsDAO.findById(news.getId());

        if (newsCategory == null) {
            throw new ObjectNotFoundException("Can't find the news category with the specified id.",
                    ObjectNotFoundException.Type.NEWS_CATEGORIES);
        } else if (newsInDb == null) {
            throw new ObjectNotFoundException("Can't find the news with the specified id.",
                    ObjectNotFoundException.Type.NEWS);
        } else {
            newsInDb.setTitle(news.getTitle());
            newsInDb.setContent(news.getContent());
            newsInDb.setCategory(news.getCategory());
            newsInDb.setPublished(news.isPublished());
            newsInDb.setCreationDate(news.getCreationDate());
        }
    }

    @RolesAllowed(value = "administrator")
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void updateNewsCategory(String newsCategory1, String newsCategory2) {
        List<NewsCategory> allNewsCategories = (List<NewsCategory>) newsDAO.findAllNewsCategories();

        allNewsCategories.get(0).setName(newsCategory1);
        allNewsCategories.get(1).setName(newsCategory2);
    }

    @RolesAllowed(value = "administrator")
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public boolean toggleNewsPublishedStatus(long id) throws ObjectNotFoundException {
        News news = newsDAO.findById(id);

        if (news == null) {
            throw new ObjectNotFoundException("Can't find the News to toggle the published status.",
                    ObjectNotFoundException.Type.NEWS);
        }

        news.setPublished((news.isPublished()) ? (false) : (true));

        return news.isPublished();
    }

    @PermitAll
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public Collection<News> findAllNewsPublished(long newsCategoryId) {
        return newsDAO.findNewsPublishedByCategoryId(newsCategoryId, newsDAO.countAll());
    }
}