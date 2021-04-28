/*
 * $Id: TemplateDocxDAO.java,v 1.3 2012/06/12 14:31:28 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2012/06/12 14:31:28 $
 * Last changed by $Author: d-marques $
 */
package com.criticalsoftware.certitools.persistence.plan;

import com.criticalsoftware.certitools.entities.TemplateDocx;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.Collection;

/**
 * Description.
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.3 $
 */
public interface TemplateDocxDAO extends GenericDAO<TemplateDocx, Long> {
    Collection<TemplateDocx> findContractTemplatesDocx(long contractId);
    Collection<TemplateDocx> findAllByStartLetter(String letter);
    Collection<TemplateDocx> findTemplatesDocxByTitle(String searchPhrase);
}