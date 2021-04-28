/*
 * $Id: LegalDocumentHistoryDAO.java,v 1.5 2009/04/17 03:49:11 jp-gomes Exp $
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
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.List;

/**
 * <description>
 *
 * @author jp-gomes
 */
public interface LegalDocumentHistoryDAO extends GenericDAO<LegalDocumentHistory, Long> {

    /**
     * Find All (five) last consulted legal document by a specific user.
     *
     * @param userId    - user id
     * @param published - get list with documents published or not
     * @return - List with legal documents
     */
    List<LegalDocumentHistory> findAllHistoryActiveByUser(Long userId, Boolean published);

    /**
     * Find All Legal Document History records, that contains one legal document. This method is used to remove legal
     * document histories, when a legal document is deleted
     *
     * @param documentId                - legal document
     * @param loadWithInactiveDocuments - load or not, invisible(not active) legal documents
     * @return - List with legal documents history .
     */
    List<LegalDocumentHistory> findAllHistoryByDocument(Long documentId, Boolean loadWithInactiveDocuments);

    /**
     * Find All Legal Document History records, that contains one legal document.
     *
     * @param documentId - legal document Id
     * @return - List with legal documents history .
     */
    List<LegalDocumentHistory> findAllHistoryByDocument(Long documentId);
}
