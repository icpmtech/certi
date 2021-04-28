/*
 * $Id: CountryServiceEJB.java,v 1.1 2009/03/16 18:44:09 pjfsilva Exp $
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
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.persistence.certitools.CountryDAO;
import com.criticalsoftware.certitools.entities.Country;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.util.Collection;

/**
 * Country Service to return list of available countries
 *
 * @author pjfsilva
 */
@Stateless
@Local(CountryService.class)
@LocalBinding(jndiBinding = "certitools/CountryService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class CountryServiceEJB implements CountryService {
    @EJB
    private CountryDAO countryDAO;

    @PermitAll
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Collection<Country> findAll() {
        return countryDAO.findAll();
    }

    public Country findById(String id) {
        return countryDAO.findById(id);
    }
}