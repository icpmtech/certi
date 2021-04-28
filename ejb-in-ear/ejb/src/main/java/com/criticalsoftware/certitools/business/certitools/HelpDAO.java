/*
 * $Id: HelpDAO.java,v 1.2 2009/10/20 17:04:38 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/20 17:04:38 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.entities.HelpSearchableContent;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.List;

/**
 * Help DAO 
 *
 * @author jp-gomes
 */
public interface HelpDAO extends GenericDAO<HelpSearchableContent, Long> {
    List<HelpSearchableContent> search(List<String> searchItems);
}
