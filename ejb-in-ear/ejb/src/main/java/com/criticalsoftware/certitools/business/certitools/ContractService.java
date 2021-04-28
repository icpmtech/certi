/*
 * $Id: ContractService.java,v 1.23 2010/05/26 15:33:24 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/05/26 15:33:24 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.IsReferencedException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.Permission;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.File;
import com.criticalsoftware.certitools.util.ModuleType;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contract Service
 *
 * @author pjfsilva
 */
public interface ContractService {

    public void insertContract(Contract contract, File file) throws ObjectNotFoundException, JackrabbitException;

    public void deleteContract(Contract contract)
            throws ObjectNotFoundException, IsReferencedException, JackrabbitException,
            CertitoolsAuthorizationException, BusinessException;

    public void updateContract(Contract contract, File file) throws ObjectNotFoundException, JackrabbitException;

    public Collection<Contract> findAllWithUserContractAllowed(long companyId, User userInSession)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    public Collection<Contract> findAllPlanWithUserContractAllowed(long companyId, User userInSession,
                                                                   ModuleType moduleType)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    /**
     * Returns contracts that user is allowed to see
     *
     * @param companyId     id of company
     * @param userInSession user that called
     * @param frontoffice   true if called from frontoffice (permissions check is different) if frontoffice returns
     *                      contracts that user can see, if false returns contracts that user can *MANAGE*
     * @param moduleType    - moduleType
     *
     * @return contracts that user is allowed to see
     *
     * @throws CertitoolsAuthorizationException
     *                                 user not authorized
     * @throws ObjectNotFoundException user not found
     */
    public Collection<Contract> findAllPlansWithUserContractAllowed(long companyId, User userInSession,
                                                                    boolean frontoffice, ModuleType moduleType)
            throws CertitoolsAuthorizationException, ObjectNotFoundException;

    public Collection<Contract> findAllWithUserContractAndPermissionAllowed(long companyId, User userInSession)
            throws CertitoolsAuthorizationException;

    public Contract findById(long contractId) throws ObjectNotFoundException;

    public Contract findByIdWithUserContract(long contractId, User userInSession)
            throws CertitoolsAuthorizationException;

    public Contract findByIdWithUserContract(long contractId) throws ObjectNotFoundException;

    public long countLicensesInUse(long contractId);

    public File findContractFileAllowed(long contractId, User userInSession)
            throws JackrabbitException, CertitoolsAuthorizationException, ObjectNotFoundException;

    public Collection<Permission> findContractPermissions(long contractId);

    public Collection<Contract> findByCompanyId(Long companyId, User userInSession);

    Collection<Contract> findAll();

    void updateContractInactivitySettings(Contract contract) throws ObjectNotFoundException;

    /**
     * Validates the user register code specified with the code in the contract
     *
     * @param contractId id of the contract to compare
     * @param code       code to validate (in md5)
     *
     * @return true if code is equal to code in contract
     *
     * @throws ObjectNotFoundException contract not found
     * @throws com.criticalsoftware.certitools.business.exception.BusinessException
     *                                 error encrypting to md5
     */
    boolean validateUserRegisterCode(long contractId, String code) throws ObjectNotFoundException, BusinessException;

    /**
     * Updates the contract base permissions.
     *
     * @param contractId              id of the contract
     * @param userRegisterPermissions list of ids of permissions that are part of the base permissions
     *
     * @throws com.criticalsoftware.certitools.business.exception.ObjectNotFoundException
     *          contract not found
     */
    void updateContractBasePermissions(long contractId, List<Long> userRegisterPermissions)
            throws ObjectNotFoundException;

    Collection<Contract> findAllCompanyContactsWithPermissionAllowed(long companyId, User userInSession)
            throws CertitoolsAuthorizationException;

    int sendCompanyAlerts(Map<Long, Set<Long>> contractListMap, String from, String subject, String body,
                           User userInSession, long companyId)
            throws MessagingException, BusinessException, NamingException, CertitoolsAuthorizationException;
}