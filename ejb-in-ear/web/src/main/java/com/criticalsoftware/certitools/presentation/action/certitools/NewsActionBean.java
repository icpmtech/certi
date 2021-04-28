/*
 * $Id: NewsActionBean.java,v 1.35 2010/03/30 17:27:15 jp-gomes Exp $
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
package com.criticalsoftware.certitools.presentation.action.certitools;

import com.criticalsoftware.certitools.business.certitools.NewsService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.News;
import com.criticalsoftware.certitools.entities.NewsCategory;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PTDateTypeConverter;
import com.criticalsoftware.certitools.presentation.util.PaginatedListAdapter;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * News Administration Action Bean
 *
 * @author pjfsilva
 */
@Secure(roles = "administrator")
public class NewsActionBean extends DisplayTagSupportActionBean implements ValidationErrorHandler {
    @EJBBean(value = "certitools/NewsService")
    private NewsService newsService;

    // news related
    @ValidateNestedProperties({
            @Validate(field = "title", required = true, maxlength = 255, on = "insertNews"),
            @Validate(field = "content", required = true, maxlength = 4096, on = "insertNews"),
            @Validate(field = "creationDate", required = true, on = "insertNews",
                    converter = PTDateTypeConverter.class),
            @Validate(field = "category.id", required = true, on = "insertNews")
    })
    private News news;

    // News category related
    @Validate(required = true, maxlength = 128, on = "updateNewsCategory")
    public String newsCategory1;
    @Validate(required = true, maxlength = 128, on = "updateNewsCategory")
    public String newsCategory2;

    private boolean edit = false;
    public PaginatedListAdapter newsAdapter;
    public Collection<NewsCategory> newsCategories;

    @DefaultHandler
    public Resolution viewNews() throws BusinessException {
        PaginatedListWrapper<News> wrapper =
                new PaginatedListWrapper<News>(getPage(),
                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(), getDirOrder(),
                        isExportRequest());

        newsAdapter = new PaginatedListAdapter<News>(newsService.findAllNews(wrapper));

        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility.getLocalizedFieldName("news.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility.getLocalizedFieldName("news.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility.getLocalizedFieldName("news.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility.getLocalizedFieldName("news.filename.pdf", null, null, locale));


        setHelpId("#news-administration");
        return new ForwardResolution("/WEB-INF/jsps/certitools/news.jsp");
    }

    public Resolution insertNewsForm() {
        newsCategories = newsService.findAllNewsCategories();

        if (news == null) {
            news = new News();
            news.setContent("");
        }

        setHelpId("#add-news");
        return new ForwardResolution("/WEB-INF/jsps/certitools/newsInsert.jsp");
    }

    public Resolution insertNews() throws ObjectNotFoundException {
        if (edit) {
            newsService.updateNews(news);
        } else {
            newsService.insertNews(news);
        }
        getContext().getMessages().add(new LocalizableMessage("news.add.sucess"));
        return new RedirectResolution(NewsActionBean.class);
    }

    public Resolution updateNews() throws BusinessException, ObjectNotFoundException {
        newsCategories = newsService.findAllNewsCategories();
        news = newsService.findNews(news.getId());
        edit = true;

        setHelpId("#edit-news");
        return new ForwardResolution("/WEB-INF/jsps/certitools/newsInsert.jsp");
    }

    public Resolution updateNewsCategoryForm() {
        List<NewsCategory> newsCategories = (List<NewsCategory>) newsService.findAllNewsCategories();

        newsCategory1 = (newsCategories.get(0).getName());
        newsCategory2 = (newsCategories.get(1).getName());

        setHelpId("#edit-news-category");
        return new ForwardResolution("/WEB-INF/jsps/certitools/newsCategoriesUpdate.jsp");
    }

    public Resolution updateNewsCategory() {
        newsService.updateNewsCategory(newsCategory1, newsCategory2);
        getContext().getMessages().add(new LocalizableMessage("news.category.edit.sucess"));
        return new RedirectResolution(NewsActionBean.class);
    }

    public Resolution deleteNews() throws BusinessException, ObjectNotFoundException {
        newsService.deleteNews(news.getId());
        getContext().getMessages().add(new LocalizableMessage("news.delete.sucess"));
        return new RedirectResolution(NewsActionBean.class).addParameters(getDisplayTagParameters());
    }

    public Resolution togglePublished() throws ObjectNotFoundException {
        Boolean newsPublished = newsService.toggleNewsPublishedStatus(news.getId());
        //getContext().getMessages().add(new LocalizableMessage("news.publishedStatus.sucess"));
        return new StreamingResolution("text/html", newsPublished.toString());
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(MenuItem.Item.MENU_ADMIN, MenuItem.Item.SUB_MENU_ADMIN_NEWS);
    }

    public NewsService getNewsService() {
        return newsService;
    }

    public void setNewsService(NewsService newsService) {
        this.newsService = newsService;
    }

    public PaginatedListAdapter getNewsAdapter() {
        return newsAdapter;
    }

    public Collection<NewsCategory> getNewsCategories() {
        return newsCategories;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertNews")) {
            return insertNewsForm();
        }
        return null;
    }
}
