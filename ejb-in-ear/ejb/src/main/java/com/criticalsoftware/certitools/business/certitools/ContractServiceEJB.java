/*
 * $Id: ContractServiceEJB.java,v 1.55 2012/06/05 11:06:19 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2012/06/05 11:06:19 $
 * Last changed by $Author: d-marques $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.business.exception.*;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.*;
import com.criticalsoftware.certitools.entities.jcr.Plan;
import com.criticalsoftware.certitools.entities.sm.SubModule;
import com.criticalsoftware.certitools.persistence.plan.PermissionDAO;
import com.criticalsoftware.certitools.persistence.plan.PlanDAO;
import com.criticalsoftware.certitools.persistence.certitools.*;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.*;
import org.apache.commons.lang.StringUtils;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import javax.mail.MessagingException;
import javax.naming.NamingException;
import java.util.*;

/**
 * Contract Service
 *
 * @author pjfsilva
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(ContractService.class)
@LocalBinding(jndiBinding = "certitools/ContractService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class ContractServiceEJB implements ContractService {

    @EJB
    private ContractDAO contractDAO;

    @EJB
    private UserDAO userDAO;

    @EJB
    private ModuleDAO moduleDAO;

    @EJB
    private RepositoryDAO repositoryDAO;

    @EJB
    private PermissionDAO permissionDAO;

    @EJB
    private PlanDAO planDAO;

    @EJB
    private UserService userService;

    @EJB
    private ConfigurationDAO configurationDAO;

    @Resource
    private SessionContext sessionContext;

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = Logger.getInstance(ContractServiceEJB.class);

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = "contractmanager")
    public void insertContract(Contract contract, File file) throws ObjectNotFoundException, JackrabbitException {

        // check if module is ok
        Module module = moduleDAO.findModuleByModuleType(contract.getModule().getModuleType());
        if (module == null) {
            throw new ObjectNotFoundException("Can't find the module with the specified module type.",
                    ObjectNotFoundException.Type.MODULE);
        }

        contract.setModule(module);

        if (file != null) {
            contract.setFileName(file.getFileName());
        }

        contractDAO.insert(contract);

        if (file != null) {
            file.setId(contract.getId());
            repositoryDAO.insertFileOnFolder(RepositoryDAO.Folder.CONTRACT_FOLDER, file);
            contract.setContractFile(String.valueOf(contract.getId()));
        }

        hookInsertContract(contract);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = "contractmanager")
    public void deleteContract(Contract contract)
            throws ObjectNotFoundException, IsReferencedException, JackrabbitException,
            CertitoolsAuthorizationException, BusinessException {

        // check if contract exists
        contract = contractDAO.findById(contract.getId());

        if (contract == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id.",
                    ObjectNotFoundException.Type.CONTRACT);
        }
        // check if there are users in this contract
        if (contractDAO.countUsersInContract(contract.getId()) > 0) {
            throw new IsReferencedException("The contract has users so can't be deleted",
                    IsReferencedException.Type.USER);
        }
        // check if there are folder with references to other folders in this contract plan
        //TODO-MODULE
        if (contract.getModule().getModuleType().equals(ModuleType.PEI)
                || contract.getModule().getModuleType().equals(ModuleType.PRV)
                || contract.getModule().getModuleType().equals(ModuleType.PSI)
                || contract.getModule().getModuleType().equals(ModuleType.GSC)) {
            if (planDAO.isPlanWithFolderReferences(contract.getId(), contract.getModule().getModuleType())) {
                throw new IsReferencedException("The contract has folder references",
                        IsReferencedException.Type.PLAN);
            }
        }
        contract.setDeleted(true);
        hookDeleteContract(contract);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"contractmanager", "clientcontractmanager"})
    public void updateContractInactivitySettings(Contract contract) throws ObjectNotFoundException {
        Contract contractInDb = contractDAO.findByIdWithUserContract(contract.getId());

        if (contractInDb == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id.",
                    ObjectNotFoundException.Type.CONTRACT);
        }

        //Reset User contracts last Plan Or Legislation View date to today date
        if (contractInDb.getUserContract() != null) {
            for (UserContract userContract : contractInDb.getUserContract()) {
                User user = userContract.getUser();
                user.setLastPlanOrLegislationView(new Date());
                userDAO.merge(user);
            }
        }

        contractInDb.setFirstInactivityMessageTemplateBody(contract.getFirstInactivityMessageTemplateBody());
        contractInDb.setFirstInactivityMessageTemplateSubject(contract.getFirstInactivityMessageTemplateSubject());
        contractInDb.setFirstInactivityMessageTerm(contract.getFirstInactivityMessageTerm());

        contractInDb.setSecondInactivityMessageTemplateBody(contract.getSecondInactivityMessageTemplateBody());
        contractInDb.setSecondInactivityMessageTemplateSubject(contract.getSecondInactivityMessageTemplateSubject());
        contractInDb.setSecondInactivityMessageTerm(contract.getSecondInactivityMessageTerm());

        contractInDb.setDeleteUserTerm(contract.getDeleteUserTerm());

        contractDAO.merge(contractInDb);
    }

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public boolean validateUserRegisterCode(long contractId, String code)
            throws ObjectNotFoundException, BusinessException {
        Contract contractInDb = contractDAO.findById(contractId);

        if (contractInDb == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id.",
                    ObjectNotFoundException.Type.CONTRACT);
        }

        //noinspection SimplifiableIfStatement
        if (StringUtils.isBlank(code) || StringUtils.isBlank(contractInDb.getUserRegisterCode())) {
            return false;
        }

        return StringUtils.equals(code, Utils.encryptMD5(contractInDb.getUserRegisterCode()));
    }

    @RolesAllowed(value = "contractmanager")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateContractBasePermissions(long contractId, List<Long> userRegisterPermissions)
            throws ObjectNotFoundException {
        Contract contractInDb = contractDAO.findById(contractId);

        if (contractInDb == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id.",
                    ObjectNotFoundException.Type.CONTRACT);
        }

        Collection<Permission> contractPermissions = permissionDAO.find(contractId);

        for (Permission contractPermission : contractPermissions) {
            if (userRegisterPermissions != null && userRegisterPermissions.contains(contractPermission.getId())) {
                contractPermission.setUserRegisterBasePermission(true);
            } else {
                contractPermission.setUserRegisterBasePermission(false);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = "contractmanager")
    public void updateContract(Contract contract, File file) throws ObjectNotFoundException, JackrabbitException {
        Contract contractInDb = contractDAO.findById(contract.getId());

        if (contractInDb == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id.",
                    ObjectNotFoundException.Type.CONTRACT);
        }

        // check if module is ok
        Module module = moduleDAO.findModuleByModuleType(contract.getModule().getModuleType());
        if (module == null) {
            throw new ObjectNotFoundException("Can't find the module with the specified module type.",
                    ObjectNotFoundException.Type.MODULE);
        }

        contract.setModule(module);

        if (contract.getModule().getModuleType() == ModuleType.GSC) {
            //update security management sub modules allowed
            moduleDAO.deleteContractSubModules(contract.getId());
            for (SubModule subModule : contract.getSubModules()) {
                subModule.setContract(contract);
                moduleDAO.insertSubModule(subModule);
            }
            //keep same logo and cover pictures
            contract.setSmLogoPicture(contractInDb.getSmLogoPicture());
            contract.setSmCoverPicture(contractInDb.getSmCoverPicture());
        }

        // the contract had a file?
        if (file != null && contractInDb.getContractFile() != null) {
            // remove the old file
            repositoryDAO
                    .removeFileOnFolder(RepositoryDAO.Folder.CONTRACT_FOLDER,
                            Long.valueOf(contractInDb.getContractFile()));

            // upload the new one
            file.setId(contractInDb.getId());
            repositoryDAO.insertFileOnFolder(RepositoryDAO.Folder.CONTRACT_FOLDER, file);
            contract.setFileName(file.getFileName());
            contract.setContractFile(String.valueOf(contract.getId()));

        } else if (file != null) {
            // contract didn't have a file, just upload the new one
            file.setId(contractInDb.getId());
            repositoryDAO.insertFileOnFolder(RepositoryDAO.Folder.CONTRACT_FOLDER, file);
            contract.setFileName(file.getFileName());
            contract.setContractFile(String.valueOf(contract.getId()));
        }
        //Must update all contract designation folder references to new name
        if (!contractInDb.getContractDesignation().equals(contract.getContractDesignation())) {
            Collection<Contract> contracts = new ArrayList<Contract>();
            contracts.add(contract);
            planDAO.updatePlansFolderReferencePath(contracts, contract.getContractDesignation(), null);
        }
        contractDAO.merge(contract);

        //For each user in Contract check /send notification email
        contractDAO.flush();
        contractDAO.clear();
        // TODO filtrar o que vem do getUserContract() com outro método findByIDInactivatedUsersInContract
        /*Contract loadedContract = contractDAO.findByIdWithUserContract(contract.getId());
        if (loadedContract.getUserContract() != null) {
            for (UserContract userContract : loadedContract.getUserContract()) {
                userService.checkAndSendNotificationEmail(userContract.getUserContractPK().getIdUser(), false);
            }
        }*/
        Collection<UserContract> userContracts = contractDAO.findByIDInactivatedUsersInContract(contract.getId());
        if (userContracts != null) {
            for (UserContract userContract : userContracts) {
                userService.checkAndSendNotificationEmail(userContract.getUserContractPK().getIdUser(), false);
            }
        }
    }

    // contract manager can get all users
    // client contract manager and administrator can only see their company users

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<Contract> findAllWithUserContractAllowed(long companyId, User userInSession)
            throws CertitoolsAuthorizationException {
        //noinspection StatementWithEmptyBody
        if (sessionContext.isCallerInRole("contractmanager")) {
            // ok
        } else if (sessionContext.isCallerInRole("administrator") || sessionContext
                .isCallerInRole("clientcontractmanager")) {
            if (userInSession.getCompany().getId() != companyId) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see users outside their company");
            }
        }

        return contractDAO.findAllWithUserContract(companyId, " c.number ASC");
    }

    public Collection<Contract> findAllPlanWithUserContractAllowed(long companyId, User userInSession,
                                                                   ModuleType moduleType)
            throws ObjectNotFoundException, CertitoolsAuthorizationException {
        return findAllPlansWithUserContractAllowed(companyId, userInSession, false, moduleType);
    }

    // contract manager can get all users
    // client contract manager and administrator can only see their company users
    // user can only access contracts valid
    // if pei manager, return all contracts
    // if client pei manager doesn't check the dates for the contracts he is clientpeimanager
    // if user, check contract validity

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"peimanager", "clientpeimanager", "administrator", "contractmanager", "user"})
    public Collection<Contract> findAllPlansWithUserContractAllowed(long companyId, User userInSession,
                                                                    boolean frontoffice, ModuleType moduleType)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        if (!(frontoffice && sessionContext.isCallerInRole("administrator"))) {
            validateUserAcess(companyId, userInSession);
        }

        if (sessionContext.isCallerInRole("peimanager") || (frontoffice && (
                sessionContext.isCallerInRole("contractmanager") || sessionContext.isCallerInRole("administrator")))) {
            return filterContractsByModuleType(
                    contractDAO.findAllWithUserContract(companyId, " c.contractDesignation ASC "), moduleType);
        }
        // check what contracts user can access
        User userInDb = userDAO.findByIdWithContractsPermissionsRoles(userInSession.getId());

        if (userInDb == null) {
            throw new ObjectNotFoundException("Object not found: User", ObjectNotFoundException.Type.USER);
        }

        Set<UserContract> userContractSet = userInDb.getUserContract();
        Collection<Contract> contractList = new ArrayList<Contract>();

        for (UserContract userContract : userContractSet) {
            // check if usercontract is valid
            if ((userContract.isUserContractValid(null) && frontoffice) || isUserClientPEIManager(userContract)) {
                contractList.add(userContract.getContract());
            }
        }

        return filterContractsByModuleType(contractList, moduleType);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RolesAllowed(value =
            {"administrator", "contractmanager", "clientcontractmanager", "peimanager", "clientpeimanager"})
    public Collection<Contract> findAllWithUserContractAndPermissionAllowed(long companyId, User userInSession)
            throws CertitoolsAuthorizationException {

        validateUserAcess(companyId, userInSession);
        Collection<Contract> contracts = contractDAO.findAllWithUserContract(companyId, " c.number ASC ");

        if (contracts != null) {
            for (Contract contract : contracts) {
                if (contract.getUserContract() != null) {
                    for (UserContract userContract : contract.getUserContract()) {
                        if (userContract.getPermissions() != null) {
                            userContract.getPermissions().size();
                        }
                    }
                }
            }
        }
        return contracts;
    }

    // contract manager can get all contracts
    // client contract manager and administrator can only see their company contrats

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Contract findByIdWithUserContract(long contractId, User userInSession)
            throws CertitoolsAuthorizationException {
        Contract contractInDb = contractDAO.findByIdWithUserContract(contractId);

        if (sessionContext.isCallerInRole("contractmanager")) {
            // ok
            return contractInDb;
        } else if (sessionContext.isCallerInRole("administrator") || sessionContext
                .isCallerInRole("clientcontractmanager")) {

            if (contractInDb.getCompany().getId().longValue() != userInSession.getCompany().getId().longValue()) {
                throw new CertitoolsAuthorizationException(
                        "clientcontractmanager or administrator role can't see contracts outside their company");
            }
        }

        return contractInDb;
    }

    public Contract findByIdWithUserContract(long contractId) throws ObjectNotFoundException {
        Contract contractInDb = contractDAO.findByIdWithUserContract(contractId);

        if (contractInDb == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id: " + contractId,
                    ObjectNotFoundException.Type.CONTRACT);
        }

        return contractInDb;
    }

    @RolesAllowed(value = "contractmanager")
    public long countLicensesInUse(long contractId) {
        return contractDAO.countLicensesInUse(contractId);
    }

    // administrator and clientcontractmanager need this to run findContractFileAllowed.
    // no check is needed here for that roles, because the check is done in findContractFileAllowed()

    @RolesAllowed(
            value = {"administrator", "contractmanager", "clientcontractmanager", "peimanager", "clientpeimanager"})
    public Contract findById(long contractId) throws ObjectNotFoundException {
        Contract contract = contractDAO.findById(contractId);

        if (contract == null) {
            throw new ObjectNotFoundException("Can't find the contract with the specified id: " + contractId,
                    ObjectNotFoundException.Type.CONTRACT);
        }

        return contract;
    }

    // client contract manager can only see their company
    // administrator can only see certitecna
    // contract manager can see it all
    // TESTED ALL BRANCHES OK

    @RolesAllowed(value = {"administrator", "contractmanager"})
    public File findContractFileAllowed(long contractId, User userInSession)
            throws JackrabbitException, CertitoolsAuthorizationException, ObjectNotFoundException {

        File f;

        Contract contractInDb = findById(contractId);

        if (sessionContext.isCallerInRole("contractmanager")) {
            // ok - user can see it all
            f = repositoryDAO.findFileOnFolder(RepositoryDAO.Folder.CONTRACT_FOLDER, contractId);
            f.setFileName(contractInDb.getFileName());
            return f;
        }


        long companyId = contractInDb.getCompany().getId();

        if (sessionContext.isCallerInRole("administrator") &&
                companyId != Long.valueOf(Configuration.getInstance().getCertitecnaId())) {
            throw new CertitoolsAuthorizationException("Administrator role can't see other companies");

        }

        f = repositoryDAO.findFileOnFolder(RepositoryDAO.Folder.CONTRACT_FOLDER, contractId);
        f.setFileName(contractInDb.getFileName());
        return f;
    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<Permission> findContractPermissions(long contractId) {
        return permissionDAO.find(contractId);
    }

    @RolesAllowed(value = {"contractmanager"})
    public Collection<Contract> findByCompanyId(Long companyId, User userInSession) {
        return contractDAO.findAll(companyId);
    }

    @RolesAllowed(value = {"administrator"})
    public Collection<Contract> findAll() {
        return contractDAO.findAll();
    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    public Collection<Contract> findAllCompanyContactsWithPermissionAllowed(long companyId, User userInSession)
            throws CertitoolsAuthorizationException {
        return findAllCompanyContactsWithPermissionAllowedPrivate(companyId, userInSession);

    }

    @RolesAllowed(value = {"administrator", "contractmanager", "clientcontractmanager"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int sendCompanyAlerts(Map<Long, Set<Long>> contractListMap, String from, String subject, String body,
                                 User userInSession, long companyId)
            throws MessagingException, BusinessException, NamingException, CertitoolsAuthorizationException {

        //First Validate user Access
        validateUserAcess(companyId, userInSession);

        if (!isSendCompanyAlertsSecure(companyId, userInSession, contractListMap)) {
            throw new CertitoolsAuthorizationException(
                    "user cannot access a specific contract or permission for send company alerts");
        }
        Set<User> usersToSendAlert = new HashSet<User>();

        for (Map.Entry<Long, Set<Long>> contractListMapEntry : contractListMap.entrySet()) {
            /* Send to all contract users*/
            if (contractListMapEntry.getValue().contains(-1L)) {
                Contract contract = contractDAO.findByIdWithUserContract(contractListMapEntry.getKey());
                if (contract != null) {
                    checkAndAddUserToReceiveCompanyAlerts(contract.getUserContract(), usersToSendAlert);
                }
            } else {
                /*For each selected permission user contract*/
                for (Long permissionId : contractListMapEntry.getValue()) {
                    Permission permission = permissionDAO.findByIdWithUserContract(permissionId);
                    if (permission != null && permission.getUserContracts() != null) {
                        checkAndAddUserToReceiveCompanyAlerts(permission.getUserContracts(), usersToSendAlert);
                    }
                }
            }
        }
        //Send emails...
        for (User user : usersToSendAlert) {
            MailSender.sendCompanyAlertEmail(from, user.getEmailContact(), subject, body);
        }
        return usersToSendAlert.size();
    }

    private void checkAndAddUserToReceiveCompanyAlerts(Collection<UserContract> userContracts,
                                                       Set<User> usersToSendAlert) {
        if (userContracts != null) {
            for (UserContract userContract : userContracts) {
                User user = userContract.getUser();
                if (user.isActive() && !user.isDeleted()) {
                    if (user.getCompany().getId()
                            .equals(Long.valueOf(Configuration.getInstance().getCertitecnaId()).longValue())) {
                        //Certitecna so receive alert
                        usersToSendAlert.add(user);
                    } else if (user.getRoles().contains(new Role("clientcontractmanager")) && Utils
                            .isUserClientPlanManager(userContract)) {
                        //clientcontractmanager also receive alerts
                        usersToSendAlert.add(user);
                    } else if (user.isContractAccessValid(userContract)) {
                        //Check user contract 
                        usersToSendAlert.add(user);
                    }
                }
            }
        }
    }

    private boolean isSendCompanyAlertsSecure(long companyId, User userInSession,
                                              Map<Long, Set<Long>> contractListMap)
            throws CertitoolsAuthorizationException {

        //Security checks
        Collection<Contract> contractsDB = findAllCompanyContactsWithPermissionAllowedPrivate(companyId, userInSession);

        if (contractsDB == null || contractsDB.isEmpty()) {
            return true;
        }
        if (contractListMap == null || contractListMap.isEmpty()) {
            return true;
        }

        //Validate contracts ids
        for (Map.Entry<Long, Set<Long>> contractListMapEntry : contractListMap.entrySet()) {
            if (!contractsDB.contains(new Contract(contractListMapEntry.getKey()))) {
                return false;
            } else {
                Set<Long> permissionsDBIds = getPermissionsFromContract(contractListMapEntry.getKey(), contractsDB);

                if (!contractListMapEntry.getValue().contains(-1L)) {
                    for (Long permissionId : contractListMapEntry.getValue()) {
                        if (!permissionsDBIds.contains(permissionId)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private Set<Long> getPermissionsFromContract(long contractId, Collection<Contract> contracts) {
        Set<Long> permissionsIds = new HashSet<Long>();

        if (contracts != null) {
            for (Contract contract : contracts) {
                //Find the contract
                if (contract.getId() == contractId) {
                    Collection<Permission> permissions = contract.getContractPermissions();

                    if (permissions != null) {
                        for (Permission permission : permissions) {
                            permissionsIds.add(permission.getId());
                        }
                    }
                }
            }
        }
        return permissionsIds;
    }

    // TODO-MODULE - when adding a new module, change this accordingly

    private void hookInsertContract(Contract contract) throws JackrabbitException {
        if (contract.getModule().getModuleType().equals(ModuleType.PEI)
                || contract.getModule().getModuleType().equals(ModuleType.PRV)
                || contract.getModule().getModuleType().equals(ModuleType.PSI)
                || contract.getModule().getModuleType().equals(ModuleType.GSC)) {
            Company company = contract.getCompany();

            String planName = null;
            if (contract.getModule().getModuleType().equals(ModuleType.PEI))
                planName = company.getPeiLabelPT();
            else if (contract.getModule().getModuleType().equals(ModuleType.PRV))
                planName = company.getPrvLabelPT();
            else if (contract.getModule().getModuleType().equals(ModuleType.PSI))
                planName = company.getPsiLabelPT();
            else if (contract.getModule().getModuleType().equals(ModuleType.GSC)) {

                LOGGER.info("Creating new contract permissions");
                //Insert new Permission in contract
                permissionDAO.insert(new Permission(ConfigurationProperties.PERMISSION_GSC_BASIC.getKey(), contract));
                permissionDAO.insert(new Permission(ConfigurationProperties.PERMISSION_GSC_INTERMEDIATE.getKey(), contract));
                permissionDAO.insert(new Permission(ConfigurationProperties.PERMISSION_GSC_EXPERT.getKey(), contract));

                //insert security management sub modules allowed
                for(SubModule subModule: contract.getSubModules()) {
                    subModule.setContract(contract);
                    moduleDAO.insertSubModule(subModule);
                }
                return;
            }


            // if contract is PEI, create the PEI structure
            Plan pei = new Plan(contract.getId(), contract.getContractDesignation(), null, null, null, null,
                    contract.getModule().getModuleType(), planName);

            //Insert new Permission in contract
            permissionDAO.insert(new Permission(ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey(), contract));
            planDAO.insert(pei);

        }
    }

    // TODO-MODULE - when adding a new module, change this accordingly

    private void hookDeleteContract(Contract contract)
            throws JackrabbitException, CertitoolsAuthorizationException, BusinessException {
        if (contract.getModule().getModuleType().equals(ModuleType.PEI)
                || contract.getModule().getModuleType().equals(ModuleType.PRV)
                || contract.getModule().getModuleType().equals(ModuleType.PSI)
                || contract.getModule().getModuleType().equals(ModuleType.GSC)) {
            // if contract is a plan, delete the plan structure
            if (planDAO.findPlan(contract.getId(), contract.getModule().getModuleType()) != null) {
                planDAO.deleteSourceFoldersReferences(contract.getId(), contract.getModule().getModuleType());
                planDAO.delete("/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + contract.getModule().getModuleType() + contract
                        .getId());
            }
        }
    }

    private void validateUserAcess(long companyId, User userInSession) throws CertitoolsAuthorizationException {
        //noinspection StatementWithEmptyBody
        if (sessionContext.isCallerInRole("contractmanager") || sessionContext.isCallerInRole("peimanager")) {
            // ok
        } else if (sessionContext.isCallerInRole("administrator") ||
                sessionContext.isCallerInRole("clientcontractmanager") ||
                sessionContext.isCallerInRole("clientpeimanager")) {
            if (userInSession.getCompany().getId() != companyId) {
                throw new CertitoolsAuthorizationException("clientcontractmanager, clientpeimanager or administrator " +
                        "role can't see users outside their company");
            }
        }
    }

    /**
     * Checks if a user has the role clientpeimanager and that he is pei manager of the specified pei
     *
     * @param userContract to check
     * @return true if user has the role clientpeimanager and that he is pei manager of the specified pei
     */
    private boolean isUserClientPEIManager(UserContract userContract) {
        if (!sessionContext.isCallerInRole("clientpeimanager")) {
            return false;
        }

        if (userContract == null) {
            return false;
        }

        // check if user is client pei manager
        for (Permission permission : userContract.getPermissions()) {
            if (permission.getName().equals(ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Filters a arraylist of contracts according to the specified moduletype
     *
     * @param contracts  contracts to filter
     * @param moduleType module type of the contract to return
     * @return rraylist of user contracts according with the specified moduletype
     */
    private Collection<Contract> filterContractsByModuleType(Collection<Contract> contracts, ModuleType moduleType) {
        Module module = new Module(moduleType);
        ArrayList<Contract> contractsResult = new ArrayList<Contract>();

        for (Contract contract : contracts) {
            if (contract.getModule().equals(module)) {
                contractsResult.add(contract);
            }
        }
        return contractsResult;
    }

    private Collection<Contract> findAllCompanyContactsWithPermissionAllowedPrivate(long companyId, User userInSession)
            throws CertitoolsAuthorizationException {
        if (sessionContext.isCallerInRole("clientcontractmanager") && userInSession.getCompany().getId() != companyId) {
            throw new CertitoolsAuthorizationException(
                    "clientcontractmanager role can't see users outside their company");
        }
        Collection<Contract> contracts = contractDAO.findAllByCompany(companyId);
        if (contracts != null) {
            for (Contract contract : contracts) {
                contract.setContractPermissions(permissionDAO.find(contract.getId()));
            }
        }
        return contracts;
    }
}