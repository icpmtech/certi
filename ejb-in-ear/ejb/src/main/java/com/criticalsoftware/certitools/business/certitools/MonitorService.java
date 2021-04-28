/*
 * $Id: MonitorService.java,v 1.1 2009/09/07 15:04:50 pjfsilva Exp $
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

import com.criticalsoftware.certitools.entities.LegalDocumentCategory;
import com.criticalsoftware.certitools.util.File;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;

import java.util.List;

/**
 * This service provides example services used to monitor the state of the application
 *
 * @author pjfsilva
 */

public interface MonitorService {

    public List<LegalDocumentCategory> findLegalDocumentCategoriesByDepthAndId(Long id, Long depth);

     File findNewsletterLogo() throws ObjectNotFoundException;
}