/*
 * $Id: LicenseServiceEJB.java,v 1.8 2009/06/24 22:43:57 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/06/24 22:43:57 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.license;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.persistence.certitools.CompanyDAO;
import com.criticalsoftware.certitools.persistence.license.LicenseDAO;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.License;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;



import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.ejb.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * License Service Implementation
 *
 * @author haraujo
 */
@Stateless
@Local(LicenseService.class)
@LocalBinding(jndiBinding = "certitools/LicenseService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class LicenseServiceEJB implements LicenseService {

    @EJB
    private LicenseDAO licenseDAO;

    @EJB
    private CompanyDAO companyDAO;

    private static final Logger LOGGER = Logger.getInstance(LicenseServiceEJB.class);

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @PermitAll
    public Boolean validateLicense() throws BusinessException {

        /* Get License File*/
        Properties properties = loadLicenseFile();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        /* Problem open file or file does not exists or is empty*/
        if (properties == null || properties.isEmpty()) {
            return false;

        } else {
            if (properties.containsKey("license")) {
                Object encryptKey = properties.get("license");

                String decryptKey = decryptKey(encryptKey.toString(), getSecretEncryptKey());

                try {

                    StringTokenizer st = new StringTokenizer(decryptKey, "|");

                    //Start Date
                    String tempStartDate = st.nextToken();
                    StringTokenizer stemp = new StringTokenizer(tempStartDate, ":");
                    startDate.set(Integer.parseInt(stemp.nextToken()), Integer.parseInt(stemp.nextToken()),
                            Integer.parseInt(stemp.nextToken()));

                    //End Date
                    String tempEndDate = st.nextToken();
                    stemp = new StringTokenizer(tempEndDate, ":");
                    endDate.set(Integer.parseInt(stemp.nextToken()), Integer.parseInt(stemp.nextToken()),
                            Integer.parseInt(stemp.nextToken()));

                } catch (Exception e) {
                    throw new BusinessException("Error parsing licenseKey from File", e);
                }

                return (today.equals(startDate) || today.after(startDate)) && (today.equals(endDate) || today
                        .before(endDate));

            } else {
                return false;
            }
        }
    }

    private Properties loadLicenseFile() {

        StringBuilder sb = new StringBuilder();
        Properties properties = null;
        try {
            sb.append(Configuration.getInstance().getLicenseFileDirectory())
                    .append(Configuration.getInstance().getLicenseFileName());
            FileInputStream fis = new FileInputStream(sb.toString());
            properties = new Properties();
            properties.load(fis);

        } catch (IOException e) {
            LOGGER.error("[LicenseServiceEJB] loadLicenseFile");
            LOGGER.error(e);
        }

        return properties;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = "administrator")
    public License find(Long id) {
        return licenseDAO.findById(id);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = "administrator")
    public void insert(License license) throws BusinessException {

        /* See if the company exists*/
        Company company = companyDAO.findById(license.getCompany().getId());
        if (company == null) {
            throw new BusinessException("The company that users choose to give a license does not exist");
        } else {
            license.setCompany(company);
            license.setLicenseKey(
                    encryptKey(license.getCompany().getId(), license.getStartDate(), license.getEndDate()));
            license.setCreationDate(new Date());
            licenseDAO.insert(license);
        }
    }


    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = "administrator")
    public PaginatedListWrapper<License> findAll(PaginatedListWrapper<License> paginatedListWrapper)
            throws BusinessException {

        if (paginatedListWrapper == null) {
            throw new BusinessException("No PaginatedListWrapper initialized ");
        }

        if (paginatedListWrapper.getSortCriterion() == null) {
            paginatedListWrapper.setSortCriterion("creationDate");
        }
        if (paginatedListWrapper.getSortDirection() == null) {
            paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.DESC);
        }

        int count = licenseDAO.countAll();
        paginatedListWrapper.setFullListSize(count);

        if (paginatedListWrapper.getExport()) {
            paginatedListWrapper.setList(licenseDAO.findAll(0,
                    count, paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper
                            .getSortDirection().value()));

        } else {
            paginatedListWrapper.setList(licenseDAO.findAll(paginatedListWrapper.getOffset(),
                    paginatedListWrapper.getResultsPerPage(), paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper
                            .getSortDirection().value()));
        }


        return paginatedListWrapper;

    }

    private SecretKey getSecretEncryptKey() throws BusinessException {

        byte[] secret = "aEF$%tgHWPOE12aVcx6#!]�3#4�{�!ee%".getBytes();

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            DESKeySpec desSpec = new DESKeySpec(secret);
            return skf.generateSecret(desSpec);
        } catch (Exception e) {
            throw new BusinessException("Error Getting encrypt key");
        }
    }

    private String encryptKey(Long companyId, Date startDate, Date endDate) throws BusinessException {

        StringBuilder keyToEncript = new StringBuilder();
        String key;
        SecretKey secretKey;

        try {
            /* Prepare String to encrypt*/
            Calendar startDateCal = Calendar.getInstance();
            Calendar endDateCal = Calendar.getInstance();
            startDateCal.setTime(startDate);
            endDateCal.setTime(endDate);
            /* Start Date*/
            keyToEncript.append(startDateCal.get(Calendar.YEAR));
            keyToEncript.append(":");
            keyToEncript.append(startDateCal.get(Calendar.MONTH));
            keyToEncript.append(":");
            keyToEncript.append(startDateCal.get(Calendar.DAY_OF_MONTH));
            /* Separtor*/
            keyToEncript.append("|");
            /* End Date */
            keyToEncript.append(endDateCal.get(Calendar.YEAR));
            keyToEncript.append(":");
            keyToEncript.append(endDateCal.get(Calendar.MONTH));
            keyToEncript.append(":");
            keyToEncript.append(endDateCal.get(Calendar.DAY_OF_MONTH));
            /* Separtor*/
            keyToEncript.append("|");
            /* Company Id*/
            keyToEncript.append(companyId);

            secretKey = getSecretEncryptKey();

            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Gets the raw bytes to encrypt, UTF8 is needed for
            // having a standard character set
            byte[] stringBytes = keyToEncript.toString().getBytes("UTF8");

            // encrypt using the cypher
            byte[] raw = cipher.doFinal(stringBytes);

            key = Base64.getEncoder().encode(raw).toString();
        }
        catch (Exception e) {
            throw new BusinessException("Error generating license.", e);
        }

        return key;
    }

    private String decryptKey(String key, SecretKey secretKey) throws BusinessException {

        String dKey;
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            //decode the BASE64 coded message
            byte[] raw = Base64.getDecoder().decode(key);

            //decode the message
            byte[] stringBytes = cipher.doFinal(raw);

            //converts the decoded message to a String
            dKey = new String(stringBytes, "UTF8");

        } catch (Exception e) {
            throw new BusinessException("Error decrypting key from file", e);
        }

        return dKey;
    }
}

