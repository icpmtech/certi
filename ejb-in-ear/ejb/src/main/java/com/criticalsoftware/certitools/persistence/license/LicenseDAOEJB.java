/*
 * $Id: LicenseDAOEJB.java,v 1.3 2009/03/13 17:37:53 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/13 17:37:53 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.license;

import com.criticalsoftware.certitools.entities.License;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.util.List;

/**
 * License DAO Implementation
 *
 * @author jp-gomes
 */
@Stateless
@Local(LicenseDAO.class)
@LocalBinding(jndiBinding = "certitools/LicenseDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LicenseDAOEJB extends GenericDAOEJB<License, Long> implements LicenseDAO {

    @SuppressWarnings({"unchecked"})
    public List<License> findAll(int currentPage, int resultPerPage, String sortCriteria, String sortDirection) {

        Query query = manager.createQuery(
                new StringBuilder()
                        .append("SELECT l FROM License l ORDER BY l.")
                        .append(sortCriteria).append(" ").append(sortDirection).toString());
        query.setFirstResult(currentPage);
        query.setMaxResults(resultPerPage);

        return query.getResultList();
    }

    public int countAll() {
        Query query = manager.createQuery("SELECT count(l) FROM License l");
        return ((Long) (query.getSingleResult())).intValue();
    }
}
