/*
 * $Id: HelpDAOEJB.java,v 1.3 2009/10/20 17:23:35 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/20 17:23:35 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.entities.HelpSearchableContent;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import org.jboss.annotation.ejb.LocalBinding;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Help DAO Implementation
 *
 * @author jp-gomes
 */
@Stateless
@Local(HelpDAO.class)
@LocalBinding(jndiBinding = "certitools/HelpDAO")
@RolesAllowed("private")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class HelpDAOEJB extends GenericDAOEJB<HelpSearchableContent, Long> implements HelpDAO {
    @PersistenceContext(unitName = "certitoolsEntityManager")
    private EntityManager manager;

    @SuppressWarnings({"unchecked"})
    public List<HelpSearchableContent> search(List<String> searchItems) {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT DISTINCT new HelpSearchableContent(hsc.titleToShow,hsc.fileName,hsc.permissions) FROM HelpSearchableContent hsc ");

        if (searchItems != null) {
            sb.append(" WHERE ");
            for (String searchItem : searchItems) {
                sb.append(" ( ");
                sb.append(" hsc.titleToSearch LIKE ?").append(searchItems.indexOf(searchItem) + 1);
                sb.append(" OR hsc.searchContent LIKE ?").append(searchItems.indexOf(searchItem) + 1);
                sb.append(" ) ");
                if (searchItems.indexOf(searchItem) != searchItems.size() - 1) {
                    sb.append(" AND ");
                }
            }
        }
        sb.append(" ORDER BY hsc.titleToShow ASC ");
        Query query = manager.createQuery(sb.toString());

        if (searchItems != null) {
            for (String searchItem : searchItems) {
                query.setParameter(searchItems.indexOf(searchItem) + 1, "%" + searchItem + "%");
            }
        }
        return (List<HelpSearchableContent>) query.getResultList();
    }
}

