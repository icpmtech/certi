/*
 * $Id: ConfigurationDAOEJB.java,v 1.6 2009/04/14 12:45:06 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/14 12:45:06 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.certitools;

import com.criticalsoftware.certitools.entities.Configuration;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import org.jboss.annotation.ejb.LocalBinding;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Configuration DAO EJB
 *
 * @author : jp-gomes
 */
@Stateless
@Local(ConfigurationDAO.class)
@LocalBinding(jndiBinding = "certitools/ConfigurationDAO")
//@SecurityDomain("CertiToolsRealm")
//@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ConfigurationDAOEJB extends GenericDAOEJB<Configuration, String> implements ConfigurationDAO {

    @SuppressWarnings({"unchecked"})
    public Map<String, String> findAllInMap() {
        Query query = manager.createQuery("SELECT cp FROM Configuration cp ");
        List<Configuration> configurationParameters = query.getResultList();

        Map<String, String> configuration = new TreeMap<String, String>();

        for (Configuration configurationParameter : configurationParameters) {
            configuration.put(configurationParameter.getKey(), configurationParameter.getValue());
        }
        return configuration;
    }

    @SuppressWarnings({"unchecked"})
    public List<Configuration> findAllEditable() {
        return manager.createQuery("SELECT cp FROM Configuration cp WHERE cp.editable = true ORDER BY cp.key ASC")
                .getResultList();
    }
}
