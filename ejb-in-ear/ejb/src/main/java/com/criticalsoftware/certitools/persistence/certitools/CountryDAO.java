/*
 * $Id: CountryDAO.java,v 1.1 2009/03/16 18:44:09 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/03/16 18:44:09 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.Country;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.Collection;

/**
 * Country DAO
 *
 * @author pjfsilva
 */
public interface CountryDAO extends GenericDAO<Country, String> {

    public Collection<Country> findAll();
}