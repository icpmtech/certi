/*
 * $Id: GenericDAOEJB.java,v 1.7 2010/03/29 17:14:09 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/03/29 17:14:09 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * GenericDAO, useful for common tasks such as insert/delete/find. Note: User fundAll with caution because of
 * performance issues
 *
 * @author pjfsilva
 */
public abstract class GenericDAOEJB<T, PK extends Serializable> implements GenericDAO<T, PK> {

    private Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];

    @PersistenceContext(unitName = "certitoolsEntityManager")
    protected EntityManager manager;

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public T findById(PK id) {
        return manager.find(persistentClass, id);
    }

    @SuppressWarnings({"unchecked"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Collection<T> findAll() {
        return manager.createQuery("FROM " + persistentClass.getName()).getResultList();
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public T insert(T o) {
        manager.persist(o);
        return o;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void delete(T o) {
        manager.remove(o);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void merge(T o) {
        manager.merge(o);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public void flush() {
        manager.flush();
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void refresh(T o) {
        manager.refresh(o);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public void clear() {
        manager.clear();
    }
}