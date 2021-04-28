/*
 * $Id: MonitorServiceEJB.java,v 1.1 2009/09/07 15:04:50 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/09/07 15:04:50 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.legislation.LegislationService;
import com.criticalsoftware.certitools.business.legislation.NewsletterService;
import com.criticalsoftware.certitools.entities.LegalDocumentCategory;
import com.criticalsoftware.certitools.util.File;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Monitor Service
 *
 * @author pjfsilva
 */

@Stateless
@Local(MonitorService.class)
@LocalBinding(jndiBinding = "certitools/MonitorService")
@SecurityDomain("CertiToolsRealm")
@RunAs("legislationmanager")
public class MonitorServiceEJB implements MonitorService {

    @EJB
    private LegislationService legislationService;

    @EJB
    private NewsletterService newsletterService;

    public List<LegalDocumentCategory> findLegalDocumentCategoriesByDepthAndId(Long id, Long depth) {
        return legislationService.findLegalDocumentCategoriesByDepthAndId(id, depth);
    }

    public File findNewsletterLogo() throws ObjectNotFoundException{
        return newsletterService.findNewsletterLogo();         
    }
}
