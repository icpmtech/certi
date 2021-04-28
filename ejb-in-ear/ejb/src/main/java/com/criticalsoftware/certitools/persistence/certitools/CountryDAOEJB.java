/*
 * $Id: CountryDAOEJB.java,v 1.2 2009/05/22 16:28:41 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/05/22 16:28:41 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.Country;
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
import java.util.Collection;

/**
 * Country DAO
 *
 * @author pjfsilva
 */
@Stateless
@Local(CountryDAO.class)
@LocalBinding(jndiBinding = "certitools/CountryDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CountryDAOEJB extends GenericDAOEJB<Country, String> implements CountryDAO {
    @PersistenceContext(unitName = "certitoolsEntityManager")
    private EntityManager manager;

    @SuppressWarnings({"unchecked"})    
    public Collection<Country> findAll() {
        return manager.createQuery("SELECT c FROM Country c order by c.name ASC").getResultList();
    }
}