/*
 * $Id: NewsDAOEJB.java,v 1.6 2009/09/24 10:54:28 pjfsilva Exp $
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
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

/**
 * <description>
 *
 * @author pjfsilva
 */
@Stateless
@Local(NewsDAO.class)
@LocalBinding(jndiBinding = "certitools/NewsDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class NewsDAOEJB extends GenericDAOEJB<News, Long> implements NewsDAO {
    @PersistenceContext(unitName = "certitoolsEntityManager")
    private EntityManager manager;

    /** @return all news in the db ordered by creation date */
    @SuppressWarnings({"unchecked"})
    public Collection<News> findAll() {
        return manager.createQuery("SELECT n FROM News n order by n.creationDate DESC").getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<News> findAll(int currentPage, int resultPerPage, String sortCriteria, String sortDirection) {

        Query query = manager.createQuery("SELECT n FROM News n ORDER BY n." + sortCriteria + " " + sortDirection);
        query.setFirstResult(currentPage);
        query.setMaxResults(resultPerPage);

        return query.getResultList();
    }

    public int countAll() {
        Query query = manager.createQuery("SELECT count(n) FROM News n");
        return ((Long) (query.getSingleResult())).intValue();
    }

    public int countAllPublish() {
        Query query = manager.createQuery("SELECT count(n) FROM News n where n.published = true ");
        return ((Long) (query.getSingleResult())).intValue();
    }

    /** @return all published news ordered by creation date */
    @SuppressWarnings({"unchecked"})
    public Collection<News> findAllPublished() {
        return manager.createQuery("SELECT n FROM News n where n.published = true order by n.creationDate DESC")
                .getResultList();
    }

    /**
     * Finds the X published news of the specified news category where X is given by the specified limit
     * 
     * @param categoryId id of the news category
     * @param limit number of results to return
     * @return X published news of the specified news category where X is given by the specified limit
     */
    @SuppressWarnings({"unchecked"})
    public Collection<News> findNewsPublishedByCategoryId(long categoryId, int limit) {
        Query query = manager.createQuery(
                "SELECT n FROM News n where n.published = true and n.category.id = :category order by n.creationDate DESC");
        query.setMaxResults(limit);
        query.setParameter("category", categoryId);

        return query.getResultList();
    }

    /**
     * Returns all the news categories
     *
     * @return all the news categories
     */
    @SuppressWarnings({"unchecked"})
    public Collection<NewsCategory> findAllNewsCategories() {
        return manager.createQuery("SELECT n FROM NewsCategory n order by n.id ASC")
                .getResultList();
    }

    /**
     * Returns the news category with the specified id
     *
     * @param id news category id
     * @return news category with the specified id
     */
    public NewsCategory findNewsCategory(long id) {
        Query query = manager.createQuery("SELECT nc FROM NewsCategory nc where nc.id = :id");
        query.setParameter("id", id);
        return (NewsCategory) query.getSingleResult();
    }
}