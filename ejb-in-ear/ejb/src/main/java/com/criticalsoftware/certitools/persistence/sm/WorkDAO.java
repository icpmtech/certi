package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.Risk;
import com.criticalsoftware.certitools.entities.sm.SecurityImpactWork;
import com.criticalsoftware.certitools.entities.sm.enums.WorkType;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.Date;
import java.util.List;

/**
 * Work DAO
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("unused")
public interface WorkDAO extends GenericDAO<SecurityImpactWork, Long> {

    <T> T getEntityReference(Class<T> clasz, Long id);

    List<Risk> findRisks();

    Risk findRisk(Long riskId);

    SecurityImpactWork findSecurityImpactWork(Long workId);

    SecurityImpactWork findSecurityImpactWorkWithContract(Long workId);

    List<Document> findDocuments(Long workId);

    List<Document> findDocumentsWithContent(Long workId);

    List<CorrectiveAction> findCorrectiveActions(Long workId);

    List<CorrectiveAction> findOpenCorrectiveActions(Long workId);

    boolean hasOpenCorrectiveActions(Long workId);

    List<Risk> findSecurityImpactWorkRisks(Long workId);

    List<Chat> findChatMessages(Long workId);

    Long countChatMessages(Long workId);

    int countOpenSecurityImpactWorks(long contractId);

    int countAll(long contractId, WorkType workType, String filterYear, Boolean isOpen);

    List<SecurityImpactWork> findAll(long contractId, int currentPage, int resultPerPage, String sortCriteria,
                                     String sortDirection, WorkType workType, String filterYear, Boolean isOpen);

    List<SecurityImpactWork> findAll(long contractId, String filterYear, String filterSemester, Boolean isOpen);

    List<String> findSecurityImpactWorkYears(Long contractId);

    List<SecurityImpactWork> findSecurityImpactWorksWithChatsAfterDate(Date date);

    List<Long> findSecurityImpactWorksIds(long contractId, String filterYear, Boolean isOpen);

    int deleteSecurityImpactWorks(List<Long> ids, User loggedUser);

    int deleteSecurityImpactWorksDocuments(List<Long> ids);

    int deleteSecurityImpactWorkDocuments(Long workId);
}
