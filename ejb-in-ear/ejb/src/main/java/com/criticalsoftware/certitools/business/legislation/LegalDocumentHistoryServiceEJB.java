/*
 * $Id: LegalDocumentHistoryServiceEJB.java,v 1.4 2009/04/08 16:55:05 jp-gomes Exp $
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
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.legislation.LegalDocumentHistoryDAO;
import com.criticalsoftware.certitools.persistence.legislation.LegislationDAO;
import com.criticalsoftware.certitools.entities.LegalDocument;
import com.criticalsoftware.certitools.entities.LegalDocumentHistory;
import com.criticalsoftware.certitools.entities.User;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <description>
 *
 * @author jp-gomes
 */

@Stateless
@Local(LegalDocumentHistoryService.class)
@LocalBinding(jndiBinding = "certitools/LegalDocumentHistoryService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class LegalDocumentHistoryServiceEJB implements LegalDocumentHistoryService {

    @EJB
    private LegislationDAO legislationDAO;

    @EJB
    private UserDAO userDAO;

    @EJB
    private LegalDocumentHistoryDAO legalDocumentHistoryDAO;


    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"user"})
    public void addEntry(Long documentId, Long userId) throws ObjectNotFoundException {
        LegalDocumentHistory ldh = new LegalDocumentHistory();

        LegalDocument ld = legislationDAO.findById(documentId);

        if (ld == null) {
            throw new ObjectNotFoundException("Trying to find legaldocument by id, but is does not exists",
                    ObjectNotFoundException.Type.LEGAL_DOCUMENT_HISTORY);
        }

        User user = userDAO.findById(userId);

        if (user == null) {
            throw new ObjectNotFoundException("Trying to find user by email, but is does not exists",
                    ObjectNotFoundException.Type.LEGAL_DOCUMENT_HISTORY);
        }

        List<LegalDocumentHistory> all = legalDocumentHistoryDAO.findAllHistoryActiveByUser(userId, null);


        if (all != null && !all.isEmpty()) {
            for (LegalDocumentHistory currentldh : all) {
                if (currentldh.getLegalDocument().getId().equals(ld.getId())) {
                    currentldh.setTimestamp(new Date());
                    return;
                }
            }
            //Must remove oldest
            if (all.size() > 5) {
                legalDocumentHistoryDAO.delete(all.get(all.size() - 1));
            }
        }

        ldh.setUser(user);
        ldh.setTimestamp(new Date());
        ldh.setLegalDocument(ld);
        legalDocumentHistoryDAO.insert(ldh);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"legislationmanager"})
    public List<LegalDocumentHistory> findAllHistoryByDocument(Long documentId, Boolean loadWithInactiveDocuments) {
        return legalDocumentHistoryDAO.findAllHistoryByDocument(documentId, loadWithInactiveDocuments);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"user"})
    public Set<LegalDocument> findAllHistoryActiveByUser(Long userId) {
        List<LegalDocumentHistory> historyList = legalDocumentHistoryDAO.findAllHistoryActiveByUser(userId, true);

        Set<LegalDocument> set = new LinkedHashSet<LegalDocument>();

        for (LegalDocumentHistory history : historyList) {
            set.add(history.getLegalDocument());
        }

        return set;
    }
}
