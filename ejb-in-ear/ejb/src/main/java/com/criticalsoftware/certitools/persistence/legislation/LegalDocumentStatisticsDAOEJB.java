/*
 * $Id: LegalDocumentStatisticsDAOEJB.java,v 1.5 2009/03/31 15:38:10 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/31 15:38:10 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.legislation;

import com.criticalsoftware.certitools.entities.LegalDocumentStatistics;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Legal Document statistics Data access object bean
 *
 * @author : lt-rico
 */
@Stateless
@Local(LegalDocumentStatisticsDAO.class)
@LocalBinding(jndiBinding = "certitools/LegalDocumentStatisticsDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LegalDocumentStatisticsDAOEJB extends GenericDAOEJB<LegalDocumentStatistics, Long>
        implements LegalDocumentStatisticsDAO {

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentStatistics> findAllBetween(LegalDocumentStatistics.ReportType reportType,
                                                        Date initDate, Date endDate) {
        Query query = manager.createQuery(
                new StringBuilder()
                        .append("SELECT new LegalDocumentStatistics(lds.reportType, lds.text, lds.documentId, count(lds.text)) ")
                        .append("FROM LegalDocumentStatistics lds ").append("WHERE lds.date BETWEEN ?1 AND ?2 ")
                        .append("AND lds.reportType = ?3 ")
                        .append("GROUP BY lds.reportType, lds.text, lds.documentId ")
                        .append("ORDER BY count(lds.text) DESC").toString());

        query.setParameter(1, initDate);
        query.setParameter(2, endDate);
        query.setParameter(3, reportType);

        return query.getResultList();
    }
}

