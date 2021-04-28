/*
 * $Id: LegalDocumentStatisticsDAO.java,v 1.4 2009/03/30 15:48:59 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/30 15:48:59 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.legislation;

import com.criticalsoftware.certitools.entities.LegalDocumentStatistics;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.Date;
import java.util.List;

/**
 * Legal Document statistics Data access object
 *
 * @author : lt-rico
 */
public interface LegalDocumentStatisticsDAO extends GenericDAO<LegalDocumentStatistics, Long> {

    /**
     * Finds all legal document statistics between
     *
     * @param reportType the report type
     * @param initDate   the report init date
     * @param endDate    the report end date
     * @return a collection of legal document statistics
     */
    List<LegalDocumentStatistics> findAllBetween(LegalDocumentStatistics.ReportType reportType, Date initDate,
                                                 Date endDate);
}
