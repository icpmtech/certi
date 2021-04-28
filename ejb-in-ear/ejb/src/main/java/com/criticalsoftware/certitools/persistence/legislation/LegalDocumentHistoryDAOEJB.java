/*
 * $Id: LegalDocumentHistoryDAOEJB.java,v 1.4 2009/04/17 03:49:11 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/17 03:49:11 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.legislation;

import com.criticalsoftware.certitools.entities.LegalDocumentHistory;
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
 * <description>
 *
 * @author jp-gomes
 */

@Stateless
@Local(LegalDocumentHistoryDAO.class)
@LocalBinding(jndiBinding = "certitools/LegalDocumentHistoryDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LegalDocumentHistoryDAOEJB extends GenericDAOEJB<LegalDocumentHistory, Long>
        implements LegalDocumentHistoryDAO {

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentHistory> findAllHistoryActiveByUser(Long userId, Boolean published) {

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ldh FROM LegalDocumentHistory ldh WHERE ldh.user.id = ?1 ");

        if (published != null) {
            sb.append(" AND ldh.legalDocument.published = ?2 ");
        }
        sb.append(" ORDER BY ldh.timestamp DESC ");

        Query query = manager.createQuery(sb.toString());

        query.setParameter(1, userId);

        if (published != null) {
            query.setParameter(2, published);
        }

        return (List<LegalDocumentHistory>) query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentHistory> findAllHistoryByDocument(Long documentId, Boolean published) {
        Query query = manager.createQuery(
                new StringBuilder().append("SELECT ldh FROM LegalDocumentHistory ldh WHERE ldh.legalDocument.id = ?1 ")
                        .append("AND ldh.legalDocument.published = ?2").toString());

        query.setParameter(1, documentId);
        query.setParameter(2, published);
        return (List<LegalDocumentHistory>) query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentHistory> findAllHistoryByDocument(Long documentId) {
        Query query = manager.createQuery("SELECT ldh FROM LegalDocumentHistory ldh WHERE ldh.legalDocument.id = ?1 ");

        query.setParameter(1, documentId);
        return (List<LegalDocumentHistory>) query.getResultList();
    }

}
