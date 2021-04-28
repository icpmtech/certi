package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.entities.sm.EmergencyAction;
import com.criticalsoftware.certitools.entities.sm.EmergencyToken;
import com.criticalsoftware.certitools.entities.sm.EmergencyUser;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.Date;
import java.util.List;

/**
 * Emergency Action DAO
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("unused")
public interface EmergencyActionDAO extends GenericDAO<EmergencyAction, Long> {

    int countOpenEmergencyActions(long contractId);

    EmergencyAction findEmergencyAction(Long emergencyId);

    EmergencyAction findEmergencyActionWithContract(Long emergencyId);

    List<Chat> findChatMessages(Long emergencyId);

    Long countChatMessages(Long emergencyId);

    int countAll(long contractId, String filterYear);

    List<EmergencyAction> findAll(long contractId, int currentPage, int resultPerPage, String sortCriteria,
                                  String sortDirection, String filterYear, Boolean isOpen);

    List<EmergencyAction> findAll(long contractId, String filterYear, String filterSemester, Boolean isOpen);

    List<EmergencyAction> findOpenEmergencyActions(Long contractId);

    List<String> findEmergencyActionYears(Long contractId);

    List<EmergencyUser> findEmergencyUsers(Long contractId);

    boolean hasEmergencyUsers(Long contractId);

    EmergencyUser insertEmergencyUser(EmergencyUser emergencyUser);

    int deleteEmergencyUser(Long emergencyUserId);

    EmergencyUser findEmergencyUser(Long emergencyUserId);

    EmergencyToken findEmergencyToken(Long emergencyId, String token);

    EmergencyToken insertEmergencyToken(EmergencyToken emergencyToken);

    int deleteEmergencyActionTokens(Long emergencyId);

    User findEmergencyActionCreatedByUser(Long emergencyId);

    int deleteEmergencyUserTokens(Long emergencyUserId);

    boolean existsEmergencyUserByEmailAndContract(Long contractId, String email);

    List<EmergencyAction> findEmergencyActionsWithChatsAfterDate(Date date);

    List<Long> findEmergencyActionsIds(long contractId, String filterYear, Boolean isOpen);

    int deleteEmergencyActions(List<Long> ids, User loggedUser);

    int deleteEmergencyActionTokens(List<Long> ids);
}
