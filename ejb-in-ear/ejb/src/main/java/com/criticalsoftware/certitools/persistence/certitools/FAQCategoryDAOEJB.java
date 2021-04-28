/*
 * $Id: FAQCategoryDAOEJB.java,v 1.5 2009/04/02 15:44:15 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/02 15:44:15 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.FAQCategory;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import com.criticalsoftware.certitools.util.ModuleType;
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
 * FAQ Category DAO Implementation
 *
 * @author jp-gomes
 */
@Stateless
@Local(FAQCategoryDAO.class)
@LocalBinding(jndiBinding = "certitools/FAQCategoryDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class FAQCategoryDAOEJB extends GenericDAOEJB<FAQCategory, Long> implements FAQCategoryDAO {

    @SuppressWarnings({"unchecked"})
    public List<FAQCategory> findByNameToAutoComplete(String name, ModuleType moduleType) {
        Query query =
                manager.createQuery(
                        "SELECT distinct faqC FROM FAQCategory faqC INNER JOIN FETCH faqC.faqs WHERE LOWER(faqC.name) "
                                + "LIKE LOWER(?1) AND faqC.module.moduleType=?2");
        query.setParameter(1, name + "%");
        query.setParameter(2, moduleType);
        return query.getResultList();
    }
}
