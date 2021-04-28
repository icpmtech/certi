package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Anomaly;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.CorrectiveAction;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.SecurityImpact;
import com.criticalsoftware.certitools.entities.sm.enums.AnomalyType;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.Date;
import java.util.List;

/**
 * Anomaly DAO
 *
 * @author miseabra
 * @version $Revision$
 */
public interface AnomalyDAO extends GenericDAO<Anomaly, Long> {

    <T> T getEntityReference(Class<T> clasz, Long id);

    List<SecurityImpact> findSecurityImpacts();

    SecurityImpact findSecurityImpact(Long impactId);

    Anomaly findAnomaly(Long anomalyId);

    Anomaly findAnomalyWithContract(Long anomalyId);

    List<Document> findAnomalyDocuments(Long anomalyId);

    List<Document> findDocumentsWithContent(Long anomalyId);

    List<CorrectiveAction> findAnomalyCorrectiveActions(Long anomalyId);

    List<CorrectiveAction> findAnomalyOpenCorrectiveActions(Long anomalyId);

    List<Chat> findChatMessages(Long anomalyId);

    int countOpenAnomalies(long contractId);

    int countAll(long contractId, AnomalyType anomalyType, String filterYear, Boolean isOpen);

    List<Anomaly> findAll(long contractId, int currentPage, int resultPerPage, String sortCriteria,
                          String sortDirection, AnomalyType anomalyType, String filterYear, Boolean isOpen);

    List<Anomaly> findAll(long contractId, String filterYear, String filterSemester, Boolean isOpen);

    boolean hasOpenCorrectiveActions(Long anomalyId);

    List<String> findAnomalyYears(Long contractId);

    Long countChatMessages(Long anomalyId);

    List<Anomaly> findAnomaliesWithChatsAfterDate(Date date);

    List<Long> findAnomaliesIds(long contractId, String filterYear, Boolean isOpen);

    int deleteAnomalies(List<Long> ids, User loggedUser);

    int deleteAnomaliesDocuments(List<Long> ids);

    int deleteAnomalyDocuments(Long anomalyId);
}
