/*
 * $Id: ConfigurationServiceEJB.java,v 1.11 2010/02/04 19:40:41 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/02/04 19:40:41 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.persistence.certitools.ConfigurationDAO;
import com.criticalsoftware.certitools.entities.Configuration;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.ConfigurationProperties;
import com.criticalsoftware.certitools.util.Utils;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

/**
 * Configuration Service EJB
 *
 * @author : jp-gomes
 */
@Stateless
@Local(ConfigurationService.class)
@LocalBinding(jndiBinding = "certitools/ConfigurationService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class ConfigurationServiceEJB implements ConfigurationService {
    @EJB
    private ConfigurationDAO configurationDAO;

    @PermitAll
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Map<String, String> findAllInMap() {
        return configurationDAO.findAllInMap();
    }

    @RolesAllowed(value = "administrator")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Collection<Configuration> findAll() {
        return configurationDAO.findAllEditable();
    }

    @RolesAllowed(value = "administrator")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateMasterPassword(String password, User userInSession) throws BusinessException {
        Configuration toBeUpdated = configurationDAO.findById(ConfigurationProperties.MASTERPASSWORD.getKey());
        toBeUpdated.setValue(Utils.encryptMD5(password));

        // set expiry date to + 1
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        Configuration toBeUpdated2 =
                configurationDAO.findById(ConfigurationProperties.MASTERPASSWORD_EXPIRYDATE.getKey());
        toBeUpdated2.setValue("" + calendar.getTimeInMillis() / 1000);

        // set author
        Configuration toBeUpdated3 = configurationDAO.findById(ConfigurationProperties.MASTERPASSWORD_AUTHOR.getKey());
        toBeUpdated3.setValue(userInSession.getName());

        com.criticalsoftware.certitools.util.Configuration.reloadInstance();        
    }

    @RolesAllowed(value = "administrator")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(Collection<Configuration> configurations) throws BusinessException, ObjectNotFoundException {
        if (configurations == null || configurations.size() == 0) {
            throw new BusinessException("Invalid collection of CONFIGURATION objects paramerts");
        }

        for (Configuration conf : configurations) {
            Configuration toBeUpdated = configurationDAO.findById(conf.getKey());
            toBeUpdated.setValue(conf.getValue());
        }
        com.criticalsoftware.certitools.util.Configuration.reloadInstance();
    }
}
