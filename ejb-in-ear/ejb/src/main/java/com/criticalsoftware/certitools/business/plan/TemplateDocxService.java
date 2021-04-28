/*
 * $Id: TemplateDocxService.java,v 1.4 2012/06/12 14:31:28 d-marques Exp $
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
package com.criticalsoftware.certitools.business.plan;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.IsReferencedException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.TemplateDocx;
import com.criticalsoftware.certitools.util.File;

import java.io.InputStream;
import java.util.Collection;

/**
 * Template docx service
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.4 $
 */
public interface TemplateDocxService {
    void insertTemplateDocx(TemplateDocx template, InputStream data, String filename, String contentType) throws ObjectNotFoundException, BusinessException;

    void updateTemplateDocx(TemplateDocx template, InputStream data, String filename, String contentType)
            throws ObjectNotFoundException, BusinessException, JackrabbitException;

    Collection<TemplateDocx> findAllTemplateDocx();

    Collection<TemplateDocx> findAllTemplateDocxByStartLetter(String letter);

    TemplateDocx findTemplateDocx(long templateId);

    void updateTemplateDocxContracts(long templateId, Collection<Long> contractsId) throws ObjectNotFoundException;

    void deleteTemplateDocx(long templateId) throws ObjectNotFoundException, JackrabbitException, IsReferencedException;

    File findTemplateDocxFile(long templateId) throws ObjectNotFoundException, JackrabbitException;

    Collection<TemplateDocx> findContractTemplatesDocx(long contractId);

    Collection<TemplateDocx> findTemplatesDocx(String searchPhrase);
}