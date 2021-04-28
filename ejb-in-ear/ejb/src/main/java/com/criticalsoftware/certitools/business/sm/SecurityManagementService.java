package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.SecurityImpact;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;
import com.criticalsoftware.certitools.persistence.sm.dto.UpcomingEvent;

import java.io.InputStream;
import java.util.List;

public interface SecurityManagementService {

    List<Company> getUserCompanies(long userId);

    List<Contract> getUserCompanyContracts(long userId, long companyId);

    /**
     * Find all UpcomingEvent
     *
     * @return - UpcomingEvent list
     */
    List<UpcomingEvent> getUpcommingEvents(long contractId);


    /**
     * Get the list of allowed users.
     *
     * @return the list of users
     */
    List<User> getAllowedUsers(final Long contractId);

    /**
     * Returns the spaced used for a given contract.
     *
     * @param contractId The contract id.
     * @return The space used.
     */
    Long getContractSpaceUsed(long contractId);

    /**
     * Returns the logo picture for the security module of the given contract.
     *
     * @param contractId The contract id.
     * @return The logo picture.
     */
    Document getContractLogoPicture(long contractId);

    /**
     * Returns the cover picture for the security module of the given contract.
     *
     * @param contractId The contract id.
     * @return The cover picture.
     */
    Document getContractCoverPicture(long contractId);

    /**
     * Returns a document of the given contract.
     *
     * @param documentId The document id.
     * @return The document.
     */
    Document getDocument(long documentId);

    /**
     * Save contract logo picture for the  security management front page.
     *
     * @param userId      The user id.
     * @param contractId  The contract id.
     * @param inputStream The file inputStream.
     * @param contentType The file contentType.
     * @param fileName    The file name.
     */
    void saveContractLogoPicture(long userId, long contractId, InputStream inputStream, String contentType, String fileName) throws CertitoolsAuthorizationException;

    /**
     * Save contract cover picture for the  security management front page.
     *
     * @param userId      The user id.
     * @param contractId  The contract id.
     * @param inputStream The file inputStream.
     * @param contentType The file contentType.
     * @param fileName    The file name.
     */
    void saveContractCoverPicture(long userId, long contractId, InputStream inputStream, String contentType, String fileName) throws CertitoolsAuthorizationException;

    /**
     * Count the number o open entities in the security module.
     *
     * @param contractId The contract id.
     * @return An array with the count of each entity in the same order as the menu.
     */
    int[] countOpenItems(long contractId);

    /**
     * Check if the current logged user has administrator or certitecna roles.
     *
     * @param userId the id of the user
     * @return true if is administrator or certitecna otherwise return false
     */
    boolean isUserAdministratorOrCertitecna(Long userId);

    /**
     * Check if the user contract is valid and it has basic permissions.
     *
     * @param userId     The user id.
     * @param contractId The contract id.
     */
    boolean isUserBasic(long userId, long contractId);

    /**
     * Check if the user contract is valid and it has intermediate permissions.
     *
     * @param userId     The user id.
     * @param contractId The contract id.
     */
    boolean isUserIntermediate(long userId, long contractId);

    /**
     * Check if the user contract is valid and it has expert permissions.
     *
     * @param userId     The user id.
     * @param contractId The contract id.
     */
    boolean isUserExpert(long userId, long contractId);

    /**
     * Returns the contract with the given id.
     *
     * @param contractId The contract id.
     * @return The contract with the given id.
     */
    Contract getContract(long contractId);

    /**
     * Returns the contract with the given id including it's company.
     *
     * @param contractId The contract id.
     * @return The contract with the given id.
     */
    Contract getContractWithCompany(long contractId);

    /**
     * Returns the list of security impacts.
     *
     * @return The list of security impacts.
     */
    List<SecurityImpact> findSecurityImpacts();

    /**
     * Returns the security impact with the given id.
     *
     * @param impactId The security impact id.
     * @return The security impact found.
     * @throws ObjectNotFoundException If the security impact doesn't exist.
     */
    SecurityImpact findSecurityImpact(Long impactId) throws ObjectNotFoundException;

    /**
     * Returns the list of the users of the given contract that have security permissions.
     *
     * @param contractId The contract id.
     * @return The list of the users found.
     */
    List<User> findUsersByContractAndSecurityPermissions(long contractId);

    /**
     * Returns the list of the users of the given contract that have expert or intermediate permissions.
     *
     * @param contractId The contract id.
     * @return The list of the users found.
     */
    List<User> findExpertAndIntermediateUsersByContract(long contractId);

    /**
     * Returns the list of the users of the given contract that have basic permission.
     *
     * @param contractId The contract id.
     * @return The list of the users found.
     */
    List<User> findBasicUsersByContract(long contractId);

    /**
     * Checks if the given sub module type is allowed for the given contract.
     *
     * @param contractId    The contract id.
     * @param subModuleType The sub module type.
     * @return true if the sub module is allowed, false otherwise.
     */
    boolean isSubModuleAllowed(Long contractId, SubModuleType subModuleType);

    /**
     * Returns the list of sub module types for the given contract.
     *
     * @param contractId The contract id.
     * @return The list of sub module types.
     */
    List<SubModuleType> findContractSubModules(Long contractId);

    /**
     * Returns the list of years of the security management records of the given contract.
     *
     * @param contractId The contract id.
     * @return The list of years found.
     */
    List<String> findSecurityManagementYears(long contractId);

    /**
     * Checks if the given contract has emergency users.
     *
     * @param contractId The contract id.
     * @return true id the contract has emergency users, false otherwise.
     */
    boolean hasEmergencyUsers(Long contractId);

    /**
     * Send notifications emails for chats that have new messages.
     */
    void sendChatNotifications();

    /**
     * Deletes all events/records of the given contract for the selected year and status.
     *
     * @param contractId The contract id.
     * @param year       The year of the records to delete. If null, all years will be considered.
     * @param isOpen     Indicates the status of the records. If null, all statuses will be considered.
     * @param loggedUser The logged user info.
     */
    void deleteRecordsByContract(long contractId, String year, Boolean isOpen, User loggedUser) throws CertitoolsAuthorizationException;

    /**
     * Returns the given document content as a byte array.
     *
     * @param documentId The document id.
     * @return The document content byte array.
     */
    byte[] getDocumentContent(long documentId);

    /**
     * Returns the given document content as an input stream.
     *
     * @param documentId The document id.
     * @return The document content input stream.
     */
    InputStream getDocumentContentInputStream(long documentId);
}
