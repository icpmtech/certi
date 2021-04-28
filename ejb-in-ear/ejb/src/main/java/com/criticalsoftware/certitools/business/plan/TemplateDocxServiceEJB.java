/*
 * $Id: TemplateDocxServiceEJB.java,v 1.5 2012/06/12 14:31:28 d-marques Exp $
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
import com.criticalsoftware.certitools.persistence.certitools.ContractDAO;
import com.criticalsoftware.certitools.persistence.certitools.ModuleDAO;
import com.criticalsoftware.certitools.persistence.certitools.RepositoryDAO;
import com.criticalsoftware.certitools.persistence.plan.TemplateDocxDAO;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.TemplateDocx;
import com.criticalsoftware.certitools.util.File;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Template docx service
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.5 $
 */
@Stateless
@Local(TemplateDocxService.class)
@LocalBinding(jndiBinding = "certitools/TemplateDocxService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class TemplateDocxServiceEJB implements TemplateDocxService {

    @EJB
    private ModuleDAO moduleDAO;

    @EJB
    private TemplateDocxDAO templateDocxDAO;

    @EJB
    private ContractDAO contractDAO;

    @EJB
    private RepositoryDAO repositoryDAO;

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager"})
    public void insertTemplateDocx(TemplateDocx template, InputStream data, String filename, String contentType)
            throws ObjectNotFoundException, BusinessException {
        Module module = moduleDAO.findModuleByModuleType(template.getModule().getModuleType());
        if (module == null) {
            throw new ObjectNotFoundException("Module not found: ", ObjectNotFoundException.Type.MODULE);
        }

        template.setModule(module);
        template.setFileName(filename);
        templateDocxDAO.insert(template);

        // insert file
        try {
            repositoryDAO.insertFileOnFolder(RepositoryDAO.Folder.TEMPLATE_DOCX_FOLDER,
                    new File(template.getId(), contentType, data));
        } catch (JackrabbitException e) {
            throw new BusinessException("Error while saving template docx file (template id:"
                    + template.getId() + ") in repository", e);
        }
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager"})
    public void updateTemplateDocx(TemplateDocx template, InputStream data, String filename, String contentType)
            throws ObjectNotFoundException, BusinessException, JackrabbitException {
        TemplateDocx templateInDb = templateDocxDAO.findById(template.getId());

        if (templateInDb == null) {
            throw new ObjectNotFoundException("Template not found.", ObjectNotFoundException.Type.TEMPLATE);
        }

        Module module = moduleDAO.findModuleByModuleType(template.getModule().getModuleType());
        if (module == null) {
            throw new ObjectNotFoundException("Module not found: ", ObjectNotFoundException.Type.MODULE);
        }

        template.setFileName(templateInDb.getFileName());
        template.setModule(module);
        template.setContracts(templateInDb.getContracts());

        if (data != null) {
            // delete file
            repositoryDAO.removeFileOnFolder(RepositoryDAO.Folder.TEMPLATE_DOCX_FOLDER, template.getId());

            // insert file
            try {
                repositoryDAO.updateFileOnFolder(RepositoryDAO.Folder.TEMPLATE_DOCX_FOLDER,
                        new File(template.getId(), contentType, data));

                template.setFileName(filename);
            } catch (JackrabbitException e) {
                throw new BusinessException("Error while saving template docx file (template id:"
                        + template.getId() + ") in repository", e);
            }
        }

        templateDocxDAO.merge(template);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager"})
    public Collection<TemplateDocx> findAllTemplateDocx() {
        return templateDocxDAO.findAll();
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager"})
    public Collection<TemplateDocx> findAllTemplateDocxByStartLetter(String letter){

        // if letter empty, return empty collection
        if (letter == null || letter.equals("")) {
            return new ArrayList<TemplateDocx>();
        }

        if (letter.equals("ALL")) {
            return templateDocxDAO.findAll();
        } else {
            return templateDocxDAO.findAllByStartLetter(letter);
        }

    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager"})
    public TemplateDocx findTemplateDocx(long templateId) {
        TemplateDocx templateDocx = templateDocxDAO.findById(templateId);
        templateDocx.getContracts().size();
        return templateDocx;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager"})
    public void updateTemplateDocxContracts(long templateId, Collection<Long> contractsId)
            throws ObjectNotFoundException {
        TemplateDocx template = templateDocxDAO.findById(templateId);

        if (template == null) {
            throw new ObjectNotFoundException("Template not found.", ObjectNotFoundException.Type.TEMPLATE);
        }

        Collection<Contract> contracts = new ArrayList<Contract>();

        Contract contractTemp;
        if (contractsId != null) {
            for (Long contractId : contractsId) {
                contractTemp = contractDAO.findById(contractId);
                if (contractTemp == null) {
                    throw new ObjectNotFoundException("Contract not found.", ObjectNotFoundException.Type.CONTRACT);
                }
                contracts.add(contractTemp);
            }
        }
        template.setContracts(contracts);
        templateDocxDAO.merge(template);
    }

    public void deleteTemplateDocx(long templateId)
            throws ObjectNotFoundException, JackrabbitException, IsReferencedException {
        TemplateDocx template = templateDocxDAO.findById(templateId);

        if (template == null) {
            throw new ObjectNotFoundException("Template not found.", ObjectNotFoundException.Type.TEMPLATE);
        }

        if (template.getContracts().size() > 0) {
            throw new IsReferencedException("The template is being used in contracts, so it can't be deleted",
                    IsReferencedException.Type.CONTRACT);
        }

        // delete file
        repositoryDAO.removeFileOnFolder(RepositoryDAO.Folder.TEMPLATE_DOCX_FOLDER, templateId);
        templateDocxDAO.delete(template);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager"})
    public File findTemplateDocxFile(long templateId) throws ObjectNotFoundException, JackrabbitException {
        File f;

        TemplateDocx template = templateDocxDAO.findById(templateId);

        if (template == null) {
            throw new ObjectNotFoundException("Template not found.", ObjectNotFoundException.Type.TEMPLATE);
        }

        f = repositoryDAO.findFileOnFolder(RepositoryDAO.Folder.TEMPLATE_DOCX_FOLDER, templateId);
        f.setFileName(template.getFileName());
        return f;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"peimanager", "clientpeimanager"})
    public Collection<TemplateDocx> findContractTemplatesDocx(long contractId) {
        return templateDocxDAO.findContractTemplatesDocx(contractId);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"peimanager"})
    public Collection<TemplateDocx> findTemplatesDocx(String searchPhrase){
        return templateDocxDAO.findTemplatesDocxByTitle(searchPhrase);
    }
}