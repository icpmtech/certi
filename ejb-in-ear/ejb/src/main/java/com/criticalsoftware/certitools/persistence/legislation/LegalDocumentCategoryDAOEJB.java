/*
 * $Id: LegalDocumentCategoryDAOEJB.java,v 1.2 2009/04/01 23:07:23 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/01 23:07:23 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.legislation;

import com.criticalsoftware.certitools.entities.LegalDocumentCategory;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * Legal Document Category Implementation
 *
 * @author jp-gomes
 */

@Stateless
@Local(LegalDocumentCategoryDAO.class)
@LocalBinding(jndiBinding = "certitools/LegalDocumentCategoryDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LegalDocumentCategoryDAOEJB extends GenericDAOEJB<LegalDocumentCategory, Long>
        implements LegalDocumentCategoryDAO {
}
