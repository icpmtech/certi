/*
 * $Id: HomeActionBean.java,v 1.21 2012/06/05 11:06:19 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/06/05 11:06:19 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.presentation.action;

import com.criticalsoftware.certitools.business.certitools.NewsService;
import com.criticalsoftware.certitools.business.certitools.UserService;
import com.criticalsoftware.certitools.entities.NewsCategory;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.validation.Validate;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/**
 * Home Action Bean
 *
 * @author lt-rico
 */
public class HomeActionBean extends AbstractActionBean {
    @EJBBean(value = "certitools/NewsService")
    private NewsService newsService;

    @EJBBean(value = "certitools/UserService")
    private UserService userService;

    private NewsCategory first;
    private NewsCategory second;

    @Validate(on = "changeLanguage", required = true)
    private String language;

    @DontValidate
    @DefaultHandler
    public Resolution main() {
        setAttribute("home", "-on");

        Collection<NewsCategory> newsCategories = newsService.findLastNewsPublished(3);
        Iterator<NewsCategory> newsCategoriesIterator = newsCategories.iterator();
        if (newsCategoriesIterator.hasNext()) {
            first = newsCategoriesIterator.next();
        }
        if (newsCategoriesIterator.hasNext()) {
            second = newsCategoriesIterator.next();
        }

        return new ForwardResolution("/WEB-INF/jsps/certitools/home.jsp");
    }

    public Resolution changeLanguage() throws Exception {
        Locale locale = new Locale(language);
        getContext().getRequest().getSession().setAttribute("locale", locale);

        //Invalidate menu
        getContext().getRequest().getSession().setAttribute("menu", null);
        String referer = getContext().getRequest().getHeader("referer");
        if (StringUtils.isEmpty(referer)){
            return new RedirectResolution(HomeActionBean.class);
        }
        return new RedirectResolution(referer, false); 
    }

    public Resolution changeLanguageAndSave() throws Exception {
        if (getUserInSession() != null){
            userService.updateUserLanguage(getUserInSession().getId(), language);
        }

        return changeLanguage();
    }

    public Resolution viewClients(){
        return new ForwardResolution("/WEB-INF/jsps/certitools/clients.jsp");
    }

    public NewsCategory getFirst() {
        return first;
    }

    public void setFirst(NewsCategory first) {
        this.first = first;
    }

    public NewsCategory getSecond() {
        return second;
    }

    public void setSecond(NewsCategory second) {
        this.second = second;
    }

    public void setNewsService(NewsService newsService) {
        this.newsService = newsService;
    }

    public int getPictureId() {
        double pic = Math.random();
        if (pic <= 0.3) {
            return 1;
        } else if (pic <= 0.7) {
            return 2;
        } else {
            return 3;
        }
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void fillLookupFields() {
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
