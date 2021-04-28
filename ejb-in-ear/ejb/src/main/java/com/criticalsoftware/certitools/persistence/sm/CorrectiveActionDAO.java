package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.Date;
import java.util.List;

/**
 * Corrective Action DAO
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("unused")
public interface CorrectiveActionDAO extends GenericDAO<CorrectiveAction, Long> {

    <T> T getEntityReference(Class<T> clasz, Long id);

    CorrectiveAction findCorrectiveAction(Long actionId);

    CorrectiveAction findCorrectiveActionComplete(Long actionId);

    CorrectiveAction findCorrectiveActionWithContract(Long actionId);

    List<Chat> findCorrectiveActionChatMessages(Long actionId);

    int countOpenCorrectiveAction(long contractId);

    int countAll(Long contractId, String filterYear, Boolean isOpen);

    List<CorrectiveAction> findAll(Long contractId, int currentPage, int resultPerPage, String sortCriteria,
                                   String sortDirection, String filterYear, Boolean isOpen);

    List<CorrectiveAction> findAllComplete(Long contractId, String filterYear, String filterSemester, Boolean isOpen);

    List<String> findCorrectiveActionYears(Long contractId);

    Long countCorrectiveActionChatMessages(Long actionId);

    List<CorrectiveAction> findCorrectiveActionsWithChatsAfterDate(Date date);

    List<Document> findDocuments(Long actionId);

    List<Document> findDocumentsWithContent(Long actionId);

    List<Long> findCorrectiveActionsIds(long contractId, String filterYear, Boolean isOpen);

    int deleteCorrectiveActions(List<Long> ids, User loggedUser);

    int deleteCorrectiveActionsDocuments(List<Long> ids);

    int deleteCorrectiveActionDocuments(Long actionId);
}
