/*
 * $Id: TemplateDocxDAOEJB.java,v 1.3 2012/06/12 14:31:28 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2012/06/12 14:31:28 $
 * Last changed by $Author: d-marques $
 */
package com.criticalsoftware.certitools.persistence.plan;

import com.criticalsoftware.certitools.entities.TemplateDocx;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Template docx DAO
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.3 $
 */
@Stateless
@Local(TemplateDocxDAO.class)
@LocalBinding(jndiBinding = "certitools/TemplateDocxDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TemplateDocxDAOEJB extends GenericDAOEJB<TemplateDocx, Long> implements TemplateDocxDAO {

    @SuppressWarnings({"unchecked"})
    public Collection<TemplateDocx> findContractTemplatesDocx(long contractId) {
        Query query = manager.createQuery(
                "SELECT t FROM TemplateDocx t INNER JOIN t.contracts c WHERE c.id = ?1 ORDER BY t.title ASC");
        query.setParameter(1, contractId);
        return query.getResultList();
    }

    public Collection<TemplateDocx> findAllByStartLetter(String letter) {
        Query query;

        if (letter.equals("#")) {
            query = manager.createNativeQuery(
                    "SELECT * FROM TemplateDocx t WHERE UPPER(to_ascii(convert_to(t.title, 'latin1'), 'latin1')) ~ '^[^a-zA-Z]' ",
                    TemplateDocx.class);
        } else {
            query = manager.createQuery(
                    "SELECT t FROM TemplateDocx t WHERE UPPER(to_ascii(convert_to(t.title, 'latin1'), 'latin1')) LIKE :letter ");
            query.setParameter("letter", letter + "%");
        }

        return query.getResultList();
    }

    @Override
    public Collection<TemplateDocx> findTemplatesDocxByTitle(String searchPhrase) {

        searchPhrase = searchPhrase.trim();
        searchPhrase = searchPhrase.replaceAll(" ", "%");
        searchPhrase = "%" + searchPhrase + "%";

        Query query = manager.createQuery("SELECT t FROM TemplateDocx t LEFT JOIN FETCH t.module WHERE UPPER(to_ascii(convert_to(t.title, 'latin1'), 'latin1')) "
                + " LIKE UPPER(to_ascii(convert_to(:searchPhrase, 'latin1'), 'latin1')) ORDER BY t.title" );
        query.setParameter("searchPhrase",searchPhrase);
        return query.getResultList();
    }
}