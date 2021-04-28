/*
 * $Id: FAQServiceEJB.java,v 1.17 2012/05/28 16:50:38 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/05/28 16:50:38 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.business.certitools;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.*;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.persistence.certitools.FAQCategoryDAO;
import com.criticalsoftware.certitools.persistence.certitools.FAQDAO;
import com.criticalsoftware.certitools.persistence.certitools.ModuleDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FAQ Service Implementation
 *
 * @author jp-gomes
 */

@Stateless
@Local(FAQService.class)
@LocalBinding(jndiBinding = "certitools/FAQService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class FAQServiceEJB implements FAQService {

    @EJB
    private UserDAO userDAO;

    @EJB
    private FAQService faqService;

    @EJB
    private ModuleDAO moduleDAO;

    @EJB
    private FAQDAO faqDAO;

    @EJB
    private FAQCategoryDAO faqCategoryDAO;

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "legislationmanager", "peimanager"})
    public FAQ findFAQWithCategoryAndModule(Long id, Long userId)
            throws ObjectNotFoundException, CertitoolsAuthorizationException {

        List<ModuleType> moduleAllowed = faqService.findUserModulesAllowed(userId);
        FAQ faq = faqDAO.findWithCategoryAndModule(id);

        if (faq == null) {
            throw new ObjectNotFoundException("Can�t find faq", ObjectNotFoundException.Type.FAQ);
        }

        if (!moduleAllowed.contains(faq.getFaqCategory().getModule().getModuleType())) {
            throw new CertitoolsAuthorizationException("User is trying to acess FAQ in a unauthorized module");
        }
        return faq;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "legislationmanager","peimanager"})
    public Integer countAll(Long userId) throws ObjectNotFoundException {

        List<ModuleType> moduleAllowed = faqService.findUserModulesAllowed(userId);
        return faqDAO.countAll(moduleAllowed);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "legislationmanager", "peimanager"})
    public void deleteFAQ(Long id, Long userId) throws ObjectNotFoundException, CertitoolsAuthorizationException {

        List<ModuleType> moduleAllowed = faqService.findUserModulesAllowed(userId);

        FAQ faq = faqDAO.findWithCategoryAndModule(id);

        if (faq == null) {
            throw new ObjectNotFoundException("Can�t find faq", ObjectNotFoundException.Type.FAQ);
        }

        if (!moduleAllowed.contains(faq.getFaqCategory().getModule().getModuleType())) {
            throw new CertitoolsAuthorizationException("User is trying to delete FAQ in a unauthorized module");
        }

        faqDAO.delete(faq);
    }


    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "legislationmanager","peimanager"})
    public List<FAQ> findAllFAQ(Long userId) throws ObjectNotFoundException {

        List<ModuleType> moduleAllowed = faqService.findUserModulesAllowed(userId);
        return faqDAO.findAllFAQs(moduleAllowed);
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "legislationmanager", "peimanager"})
    public PaginatedListWrapper<FAQ> findAllFAQ(PaginatedListWrapper<FAQ> paginatedListWrapper, Long userId)
            throws BusinessException, ObjectNotFoundException {


        List<ModuleType> moduleAllowed = faqService.findUserModulesAllowed(userId);

        if (paginatedListWrapper == null) {
            throw new BusinessException("No PaginatedListWrapper initialized ");
        }

        if (paginatedListWrapper.getSortCriterion() == null) {
            paginatedListWrapper.setSortCriterion("faqCategory.module.moduleType, f.faqCategory.name ");
        }
        if (paginatedListWrapper.getSortDirection() == null) {
            paginatedListWrapper.setSortDirection(PaginatedListWrapper.Direction.ASC);
        }

        int count = faqDAO.countAll(moduleAllowed);

        paginatedListWrapper.setFullListSize(count);

        if (paginatedListWrapper.getExport()) {
            paginatedListWrapper.setList(faqDAO.findAllFAQs(0,
                    count, paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper
                            .getSortDirection().value(), moduleAllowed));

        } else {
            paginatedListWrapper.setList(faqDAO.findAllFAQs(paginatedListWrapper.getOffset(),
                    paginatedListWrapper.getResultsPerPage(), paginatedListWrapper.getSortCriterion(),
                    paginatedListWrapper.getSortDirection().value(), moduleAllowed));
        }

        return paginatedListWrapper;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "legislationmanager", "peimanager"})
    public void updateFAQ(FAQ newFaq, Long userId) throws CertitoolsAuthorizationException, ObjectNotFoundException {

        FAQ oldFaq = faqDAO.findWithCategoryAndModule(newFaq.getId());

        if (oldFaq == null) {
            throw new ObjectNotFoundException("Can�t find faq", ObjectNotFoundException.Type.FAQ);
        }

        List<ModuleType> modulesAllow = faqService.findUserModulesAllowed(userId);

        if (!modulesAllow.contains(newFaq.getFaqCategory().getModule().getModuleType())) {
            throw new CertitoolsAuthorizationException("User is trying to update FAQ in a unauthorized module");
        }

        Module newModule = moduleDAO.findWithFAQCategory(newFaq.getFaqCategory().getModule().getModuleType());

        if (newModule.getFaqCategories().contains(newFaq.getFaqCategory())) {
            //Already existing FAQ category
            for (FAQCategory faqCategory : newModule.getFaqCategories()) {

                if (faqCategory.getName().equals(newFaq.getFaqCategory().getName())) {
                    //Update FAQ
                    oldFaq.setChangedDate(new Date());
                    oldFaq.setFaqCategory(faqCategory);
                    oldFaq.setAnswer(newFaq.getAnswer());
                    oldFaq.setQuestion(newFaq.getQuestion());
                    return;
                }
            }
        }
        //Create FAQ Category
        FAQCategory faqCategory = new FAQCategory();
        faqCategory.setModule(newModule);
        faqCategory.setName(newFaq.getFaqCategory().getName());
        faqCategoryDAO.insert(faqCategory);

        oldFaq.setAnswer(newFaq.getAnswer());
        oldFaq.setQuestion(newFaq.getQuestion());
        oldFaq.setChangedDate(new Date());
        oldFaq.setFaqCategory(faqCategory);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"administrator", "legislationmanager", "peimanager"})
    public void insertFAQ(Long userId, FAQ faq) throws ObjectNotFoundException, CertitoolsAuthorizationException {

        List<ModuleType> modulesAllow = faqService.findUserModulesAllowed(userId);

        if (!modulesAllow.contains(faq.getFaqCategory().getModule().getModuleType())) {
            throw new CertitoolsAuthorizationException("User is trying to insert FAQ in a unauthorized module");
        }

        Module module = moduleDAO.findWithFAQCategory(faq.getFaqCategory().getModule().getModuleType());

        if (module.getFaqCategories().contains(faq.getFaqCategory())) {

            //Already existing FAQ category
            for (FAQCategory faqCategory : module.getFaqCategories()) {

                if (faqCategory.getName().equals(faq.getFaqCategory().getName())) {
                    //Create FAQ
                    faq.setChangedDate(new Date());
                    faq.setFaqCategory(faqCategory);
                    faqDAO.insert(faq);
                    return;
                }
            }
        }
        //Create FAQ Category
        FAQCategory faqCategory = new FAQCategory();
        faqCategory.setModule(module);
        faqCategory.setName(faq.getFaqCategory().getName());
        faqCategoryDAO.insert(faqCategory);

        //Create FAQ
        FAQ newFaq = new FAQ();
        newFaq.setAnswer(faq.getAnswer());
        newFaq.setQuestion(faq.getQuestion());
        newFaq.setChangedDate(new Date());
        newFaq.setFaqCategory(faqCategory);
        faqDAO.insert(newFaq);

    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    @RolesAllowed(value = {"administrator", "legislationmanager", "peimanager"})
    public List<FAQCategory> findFaqCategoryByNameToAutoComplete(String name, Long userId, ModuleType moduleType)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        List<ModuleType> moduleAllowed = faqService.findUserModulesAllowed(userId);

        if (!moduleAllowed.contains(moduleType)) {
            throw new CertitoolsAuthorizationException("User is trying to acess FAQCategory in a unauthorized module");
        }

        return faqCategoryDAO.findByNameToAutoComplete(name, moduleType);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"user"})
    public Module findModuleFAQCategories(User user, ModuleType moduleType, Boolean loadFAQ)
            throws CertitoolsAuthorizationException, ObjectNotFoundException {

        // TODO-MODULE
        if (moduleType.equals(ModuleType.LEGISLATION) && !user.isAccessLegislation()) {
            throw new CertitoolsAuthorizationException(
                    "User is trying to acess FAQCategory in a unauthorized module [LEGISLATION]");
        } else if (moduleType.equals(ModuleType.PEI) && !user.isAccessPEI()) {
            throw new CertitoolsAuthorizationException(
                    "User is trying to acess FAQCategory in a unauthorized module [PEI]");
        } else if (moduleType.equals(ModuleType.PRV) && !user.isAccessPRV()) {
            throw new CertitoolsAuthorizationException(
                    "User is trying to acess FAQCategory in a unauthorized module [PRV]");
        } else if (moduleType.equals(ModuleType.PSI) && !user.isAccessPSI()) {
            throw new CertitoolsAuthorizationException(
                    "User is trying to acess FAQCategory in a unauthorized module [PSI]");
        }

        Module module = moduleDAO.findWithFAQCategory(moduleType);

        if (loadFAQ) {
            if (module != null) {
                for (FAQCategory fCategory : module.getFaqCategories()) {
                    fCategory.setFaqs(faqDAO.find(fCategory.getId()));
                }
            }
        }
        return module;
    }

    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<ModuleType> findUserModulesAllowed(Long userId) throws ObjectNotFoundException {

        List<ModuleType> moduleAllowed = new ArrayList<ModuleType>();

        User u = userDAO.findUserWithRoles(userId);

        if (u == null) {
            throw new ObjectNotFoundException("User does not exists or does not have roles",
                    ObjectNotFoundException.Type.FAQ);
        }

        for (Role r : u.getRoles()) {
            if (r.getRole().equals("administrator")) {
                //TODO-MODULE                
                moduleAllowed = new ArrayList<ModuleType>();
                moduleAllowed.add(ModuleType.LEGISLATION);
                moduleAllowed.add(ModuleType.PEI);
                moduleAllowed.add(ModuleType.PRV);
                moduleAllowed.add(ModuleType.PSI);
                return moduleAllowed;
            }

            if (r.getRole().equals("peimanager")) {
                //TODO-MODULE
                moduleAllowed.add(ModuleType.PEI);
                moduleAllowed.add(ModuleType.PRV);
                moduleAllowed.add(ModuleType.PSI);
                continue;
            }

            if (r.getRole().equals("legislationmanager")) {
                moduleAllowed.add(ModuleType.LEGISLATION);
            }
        }
        return moduleAllowed;
    }
}
