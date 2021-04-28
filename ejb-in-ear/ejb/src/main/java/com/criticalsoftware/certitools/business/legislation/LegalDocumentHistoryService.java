/*
 * $Id: LegalDocumentHistoryService.java,v 1.4 2009/04/08 16:55:05 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/08 16:55:05 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.legislation;

import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.LegalDocument;
import com.criticalsoftware.certitools.entities.LegalDocumentHistory;

import java.util.Set;
import java.util.List;

/**
 * Legal Document History Service
 *
 * @author jp-gomes
 */
public interface LegalDocumentHistoryService {

    /**
     * Add entry when user consults one legal document. In order to have fast a search on this table, for each user, the
     * are only five records in this table
     *
     * @param documentId - legal document consulted
     * @param userId      - user id
     * @throws ObjectNotFoundException - when document does not exists
     */
    void addEntry(Long documentId, Long userId) throws ObjectNotFoundException;

    /**
     * Find All (five) last consulted legal document by a specific user. In this list only contains published(active),
     * not deleted and not repeted  legal documents
     *
     * @param userId - user id
     * @return - List with legal documents
     */
    Set<LegalDocument> findAllHistoryActiveByUser(Long userId);

    /**
     * Find All Legal Document History records, that contains one legal document. This method is used to remove legal
     * document histories, when a legal document is deleted
     *
     * @param documentId                - legal document
     * @param loadWithInactiveDocuments - load or not, published legal documents
     * @return - List with legal documents history .
     */
    List<LegalDocumentHistory> findAllHistoryByDocument(Long documentId, Boolean loadWithInactiveDocuments);
}
