/*
 * $Id: LegalDocumentStatisticsServiceEJB.java,v 1.3 2009/03/31 15:38:10 jp-gomes Exp $
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
package com.criticalsoftware.certitools.business.legislation;

import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.legislation.LegalDocumentStatisticsDAO;
import com.criticalsoftware.certitools.persistence.legislation.LegislationDAO;
import com.criticalsoftware.certitools.entities.LegalDocument;
import com.criticalsoftware.certitools.entities.LegalDocumentStatistics;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Date;

/**
 * <description>
 *
 * @author jp-gomes
 */

@Stateless
@Local(LegalDocumentStatisticsService.class)
@LocalBinding(jndiBinding = "certitools/LegalDocumentStatisticsService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class LegalDocumentStatisticsServiceEJB implements LegalDocumentStatisticsService {

    @EJB
    private LegislationDAO legislationDAO;

    @EJB
    private LegalDocumentStatisticsDAO legalDocumentStatisticsDAO;

    @EJB
    private UserDAO userDAO;

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"user"})
    public void addEntry(LegalDocumentStatistics.ReportType reportType, String text, Long legalDocumentId)
            throws ObjectNotFoundException {

        LegalDocumentStatistics statistics = new LegalDocumentStatistics();
        statistics.setDate(new Date());
        statistics.setReportType(reportType);

        if (reportType.equals(LegalDocumentStatistics.ReportType.VISUALIZATION) || reportType.equals(
                LegalDocumentStatistics.ReportType.DOWNLOAD)) {

            LegalDocument legalDocument = legislationDAO.findById(legalDocumentId);

            if (legalDocument == null) {
                throw new ObjectNotFoundException("Cannot find legal document to insert statistics",
                        ObjectNotFoundException.Type.LEGAL_DOCUMENT_STATISTICS);
            }
            statistics.setText(legalDocument.getFullDrTitle());
            statistics.setDocumentId(legalDocument.getId());

        } else {
            statistics.setText(text);
        }

        legalDocumentStatisticsDAO.insert(statistics);
    }
}

