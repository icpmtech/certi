/*
 * $Id: NewsActionBean.java,v 1.4 2009/04/22 15:15:49 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/04/22 15:15:49 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action;

import com.criticalsoftware.certitools.business.certitools.NewsService;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.News;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import java.util.Collection;

/**
 * Lists all published news
 *
 * @author haraujo
 */
public class NewsActionBean extends AbstractActionBean {

    @EJBBean(value = "certitools/NewsService")
    private NewsService newsService;

    private Collection<News> newsList;

    @ValidateNestedProperties({
            @Validate(field = "id", on = "showOne", required = true),
            @Validate(field = "category.id", on = "showAll", required = true)
    })
    private News news;

    @Validate
    public Resolution showAll() {
        setAttribute("home", "-on");

        setNewsList(newsService.findAllNewsPublished(news.getCategory().getId()));
        return new ForwardResolution("/WEB-INF/jsps/certitools/homeNews.jsp");
    }

    @Validate
    public Resolution showOne() throws ObjectNotFoundException {
        setAttribute("home", "-on");
        News news = newsService.findNews(this.news.getId());

        setNews(news);
        return new ForwardResolution("/WEB-INF/jsps/certitools/homeNews.jsp");
    }

    public NewsService getNewsService() {
        return newsService;
    }

    public void setNewsService(NewsService newsService) {
        this.newsService = newsService;
    }

    public Collection<News> getNewsList() {
        return newsList;
    }

    public void setNewsList(Collection<News> newsList) {
        this.newsList = newsList;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public void fillLookupFields() {}
}
