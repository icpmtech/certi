/*
 * $Id: LegalDocumentStatisticsService.java,v 1.2 2009/03/30 15:48:59 jp-gomes Exp $
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
package com.criticalsoftware.certitools.business.legislation;

import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.LegalDocumentStatistics;

/**
 * <description>
 *
 * @author jp-gomes
 */
public interface LegalDocumentStatisticsService {

    /**
     * Add Entry on Legal Document Statistics, when user visualize, downloads or search for a legal document
     *
     * @param reportType      - type of action
     * @param text            - text to search
     * @param legalDocumentId - Legal Document to visualize/download
     * @throws ObjectNotFoundException - when user or legal document does not exists
     */
    void addEntry(LegalDocumentStatistics.ReportType reportType, String text, Long legalDocumentId)
            throws ObjectNotFoundException;

}
