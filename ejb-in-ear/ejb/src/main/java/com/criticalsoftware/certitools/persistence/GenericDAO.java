/*
 * $Id: GenericDAO.java,v 1.5 2010/03/29 17:14:08 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/03/29 17:14:08 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence;

import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * Generic DAO
 *
 * @author pjfsilva
 */
public interface GenericDAO<T, PK extends Serializable> {

    public T findById(PK id);

    public Collection<T> findAll();

    public T insert(T o);

    public void delete(T o);

    public void merge(T o);

    public void flush();

    public void refresh(T o);

    public void clear();
}
