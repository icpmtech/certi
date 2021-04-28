/*
 * $Id: LegislationServiceEJB.java,v 1.61 2010/02/05 17:54:27 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/02/05 17:54:27 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.legislation;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.DocumentNotExistsException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.persistence.certitools.RepositoryDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.legislation.LegalDocumentCategoryDAO;
import com.criticalsoftware.certitools.persistence.legislation.LegalDocumentHistoryDAO;
import com.criticalsoftware.certitools.persistence.legislation.LegalDocumentStatisticsDAO;
import com.criticalsoftware.certitools.persistence.legislation.LegislationDAO;
import com.criticalsoftware.certitools.entities.LegalDocument;
import com.criticalsoftware.certitools.entities.LegalDocumentCategory;
import com.criticalsoftware.certitools.entities.LegalDocumentHistory;
import com.criticalsoftware.certitools.entities.LegalDocumentState;
import com.criticalsoftware.certitools.entities.LegalDocumentStatistics;
import com.criticalsoftware.certitools.entities.LegalDocumentType;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.File;
import com.criticalsoftware.certitools.util.LegalDocumentCategoryComparator;
import com.criticalsoftware.certitools.util.LegalDocumentComparator;
import com.criticalsoftware.certitools.util.LegislationUtils;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.criticalsoftware.certitools.util.Utils;

import org.apache.commons.lang.StringUtils;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Legal Document Service implementation
 *
 * @author : lt-rico
 */
@Stateless
@Local(LegislationService.class)
@LocalBinding(jndiBinding = "certitools/LegislationService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class LegislationServiceEJB implements LegislationService {

    @EJB
    private UserDAO userDAO;

    @EJB
    private LegislationDAO legislationDAO;

    @EJB
    private LegislationService legislationService;

    @EJB
    private LegalDocumentStatisticsDAO legalDocumentDAO;

    @EJB
    private LegalDocumentCategoryDAO legalDocumentCategoryDAO;

    @EJB
    private LegalDocumentHistoryDAO legalDocumentHistoryDAO;

    @EJB
    private RepositoryDAO repositoryDAO;

    @Resource
    private SessionContext sessionContext;

    @RolesAllowed(value = {"legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public List<LegalDocument> findAllLegalDocumentSortByCategory() {
        List<LegalDocument> legalDocuments = legislationDAO.findAllLegalDocumentWithCategoriesLoaded();
        List<LegalDocument> finalDocuments = new ArrayList<LegalDocument>();

        for (LegalDocument legalDocument : legalDocuments) {
            legalDocument.getCustomAbstract();
            legalDocument.getLegalComplianceValidation();
            legalDocument.getReferenceArticles();
            if (legalDocument.getAssociatedLegalDocuments() != null) {
                legalDocument.getAssociatedLegalDocuments().size();
            }
            List<LegalDocumentCategory> legalDocumentCategoriesToShow =
                    LegislationUtils.showLongestCategory(legalDocument.getLegalDocumentCategories());
            List<LegalDocumentCategory[]> legalDocumentCategories = new ArrayList<LegalDocumentCategory[]>();

            for (LegalDocumentCategory legalDocumentCategory : legalDocumentCategoriesToShow) {
                legalDocumentCategories.add(LegislationUtils.buildCategoryHierarchy(legalDocumentCategory));
            }
            for (LegalDocumentCategory[] categoriesArray : legalDocumentCategories) {
                LegalDocument document = new LegalDocument(legalDocument);
                document.setLegalDocumentCategories(Arrays.asList(categoriesArray));
                finalDocuments.add(document);
            }
        }
        Collections.sort(finalDocuments, new LegalDocumentCategoryComparator());
        return finalDocuments;
    }

    @RolesAllowed(value = {"legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public LegalDocument findLegalDocumentByTypeNumberAndYear(Long documentTypeId, String number, String year) {
        return legislationDAO.findLegalDocumentByTypeNumberAndYear(documentTypeId, number, year);

    }

    @RolesAllowed(value = {"user", "legislationmanager", "private"})
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public LegalDocument findLegalDocumentFullyLoadedById(Long id, Boolean sortAssociatedLegalDocuments)
            throws DocumentNotExistsException {

        LegalDocument legalDocument = legislationDAO.findById(id);

        if (legalDocument == null) {
            throw new DocumentNotExistsException("Cannot find Legal Document by Id");
        }

        legalDocument.getCustomAbstract();
        legalDocument.getLegalComplianceValidation();
        legalDocument.getReferenceArticles();
        legalDocument.getLegalDocumentCategories().size();
        legalDocument.getAssociatedLegalDocuments().size();

        if (sortAssociatedLegalDocuments != null && sortAssociatedLegalDocuments) {
            Collections.sort(legalDocument.getAssociatedLegalDocuments(), new LegalDocumentComparator());
        }
        return legalDocument;
    }

    @RolesAllowed(value = {"legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<LegalDocument> findLegalDocumentsByTypeAutoComplete(Long documentTypeId, String number, String year) {
        return legislationDAO.findLegalDocumentsByTypeAutoComplete(documentTypeId, number, year);
    }

    @RolesAllowed(value = {"user", "legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public PaginatedListWrapper<LegalDocument> findLegalDocuments(
            PaginatedListWrapper<LegalDocument> wrapper, String searchLegislation)
            throws BusinessException {
        try {
            List<Long> legalDocumentIds = repositoryDAO
                    .searchLegalDocument(wrapper.getResultsPerPage(), wrapper.getOffset(), searchLegislation,
                            !sessionContext.isCallerInRole("legislationmanager"));

            wrapper.setFullListSize(repositoryDAO.countLegalDocument(searchLegislation,
                    !sessionContext.isCallerInRole("legislationmanager")));

            List<LegalDocument> sorted = new ArrayList<LegalDocument>();

            if (legalDocumentIds != null) {
                List<LegalDocument> legalDocuments = legislationDAO.findLegalDocumentsIn(legalDocumentIds);
                for (Long id : legalDocumentIds) {
                    for (LegalDocument legalDocument : legalDocuments) {
                        if (id.equals(legalDocument.getId())) {
                            sorted.add(legalDocument);
                        }
                    }
                }
            }
            wrapper.setList(sorted);

        } catch (JackrabbitException je) {
            throw new BusinessException("Problem with jackrabbit repository seraching for legal documents", je);
        }
        return wrapper;
    }

    @RolesAllowed(value = {"user", "legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public PaginatedListWrapper<LegalDocument> findLegalDocumentsByCategory(PaginatedListWrapper<LegalDocument> wrapper,
                                                                            Long categoryId, Boolean isAdmin)
            throws BusinessException {

        List<LegalDocument> ldList = legislationDAO.findLegalDocumentsByCategory(wrapper.getOffset(),
                wrapper.getResultsPerPage(), categoryId, isAdmin);

        for (LegalDocument ld : ldList) {
            ld.setLegalDocumentCategories(legislationDAO.findAllLegalDocumentCategories(ld.getId()));
        }
        wrapper.setList(ldList);
        wrapper.setFullListSize(legislationDAO.countDocumentsByCategory(categoryId, isAdmin));

        return wrapper;
    }

    @RolesAllowed(value = {"user", "legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<LegalDocument> findLegalDocumentPublishedHistory(Long history) {
        return legislationDAO.findLegalDocumentPublishedHistory(history);
    }

    @RolesAllowed(value = {"legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Integer findLegalDocumentAssociationsCounter(Long legalDocumentId) {
        return legislationDAO.findLegalDocumentAssociationsCounter(legalDocumentId);
    }

    @RolesAllowed(value = {"user", "legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public LegalDocumentCategory findLegalDocumentCategoryById(Long legalDocumentCategoryId)
            throws ObjectNotFoundException {

        LegalDocumentCategory ldc = legislationDAO.findLegalDocumentCategoryById(legalDocumentCategoryId);

        if (ldc == null) {
            throw new ObjectNotFoundException("Trying to find LegalDocumentCategorty by id, but not exists",
                    ObjectNotFoundException.Type.LEGAL_DOCUMENT_CATEGORY);
        }
        return ldc;
    }

    @RolesAllowed(value = {"user", "legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<LegalDocumentCategory> findLegalDocumentCategoriesByDepthAndId(Long id, Long depth) {
        return legislationDAO.findLegalDocumentCategoriesByDepthAndId(id, depth);
    }

    @RolesAllowed(value = {"user", "legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<LegalDocumentCategory> findTopParentDocumentCategory() {
        return legislationDAO.findTopParentDocumentCategory();
    }

    @RolesAllowed(value = {"legislationmanager", "user", "private"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<LegalDocumentCategory> findAllLegalDocumentCategoryForTree(List<LegalDocumentCategory> categoryList) {
        return buildList(categoryList, 0);
        //return buildList(legislationDAO.findTopParentDocumentCategory(), 0);
    }

    @RolesAllowed(value = {"legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<LegalDocumentState> findAllLegalDocumentState() {
        return legislationDAO.findAllLegalDocumentState();
    }

    @RolesAllowed(value = {"legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<LegalDocumentType> findAllLegalDocumentTypes() {
        return legislationDAO.findAllLegalDocumentTypes();
    }

    @RolesAllowed(value = {"legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void insertLegislation(LegalDocument legalDocument, Set<Long> legalDocumentCategories,
                                  String contentType, InputStream inputStream, String fileName)
            throws ObjectNotFoundException, BusinessException {

        //Document Type check
        LegalDocumentType legalDocumentType =
                legislationDAO.findLegalDocumentTypeById(legalDocument.getDocumentType().getId());

        if (legalDocumentType == null) {
            throw new ObjectNotFoundException("Trying to select Legal Document Type by Id and there were no results",
                    ObjectNotFoundException.Type.LEGISLATION);
        }
        legalDocument.setDocumentType(legalDocumentType);

        //Document State check
        LegalDocumentState legalDocumentState =
                legislationDAO.findLegalDocumentStateById(legalDocument.getDocumentState().getId());

        if (legalDocumentState == null) {
            throw new ObjectNotFoundException("Trying to select Legal Document State by Id and there were no results",
                    ObjectNotFoundException.Type.LEGISLATION);
        }
        legalDocument.setDocumentState(legalDocumentState);

        //Year
        Calendar date = Calendar.getInstance();
        date.setTime(legalDocument.getPublicationDate());
        Integer temp = date.get(Calendar.YEAR);
        legalDocument.setYear(temp.toString());

        //Check if document already exists
        if (legislationDAO.findLegalDocumentByTypeNumberAndYear(legalDocument.getDocumentType().getId(),
                legalDocument.getNumber(), legalDocument.getYear())
                != null) {
            throw new BusinessException("Trying to insert a duplicated legal document");
        }

        //creation Date
        legalDocument.setCreationDate(new Date());

        //legalDocumentCategories

        List<LegalDocumentCategory> selectedLDCategories =
                legislationDAO.findLegalDocumentCategoriesForSelect(legalDocumentCategories);

        Set<LegalDocumentCategory> parentsList = new HashSet<LegalDocumentCategory>();

        // Build parents Category List
        for (LegalDocumentCategory legalDocumentCategory : selectedLDCategories) {
            while (legalDocumentCategory.getParentCategory() != null) {
                legalDocumentCategory = legalDocumentCategory.getParentCategory();
                parentsList.add(legalDocumentCategory);
            }
        }

        //Remove repeated entry from selected list (this list only contains child nodes)
        selectedLDCategories.removeAll(parentsList);

        //Increments parents document category counter
        for (LegalDocumentCategory ldc : parentsList) {

            ldc.setAllAssociatedDocumentsCounter(ldc.getAllAssociatedDocumentsCounter() + 1);

            if (legalDocument.isPublished()) {
                ldc.setActiveAssociatedDocumentsCounter(ldc.getActiveAssociatedDocumentsCounter() + 1);
            }
        }

        //Increments child legal document category counter
        for (LegalDocumentCategory legalDocumentCategory : selectedLDCategories) {

            legalDocumentCategory
                    .setAllAssociatedDocumentsCounter(legalDocumentCategory.getAllAssociatedDocumentsCounter() + 1);

            if (legalDocument.isPublished()) {
                legalDocumentCategory
                        .setActiveAssociatedDocumentsCounter(
                                legalDocumentCategory.getActiveAssociatedDocumentsCounter() + 1);
            }
        }

        Set<LegalDocumentCategory> finalList = new HashSet<LegalDocumentCategory>();
        finalList.addAll(parentsList);
        finalList.addAll(selectedLDCategories);

        legalDocument.setLegalDocumentCategories(new ArrayList<LegalDocumentCategory>(finalList));

        //associatedLegalDocuments
        Set<LegalDocument> associatedLegalDocumentList = new HashSet<LegalDocument>();

        if (legalDocument.getAssociatedLegalDocuments() != null
                && !legalDocument.getAssociatedLegalDocuments().isEmpty()) {


            for (LegalDocument associatedLegalDocument : legalDocument.getAssociatedLegalDocuments()) {
                LegalDocument ldocument = legislationDAO.findLegalDocumentByTypeNumberAndYear(
                        associatedLegalDocument.getDocumentType().getId(),
                        getDocumentNumber(associatedLegalDocument.getDrTitle()),
                        getDocumentYear(associatedLegalDocument.getDrTitle()));

                if (ldocument == null) {
                    throw new BusinessException("Trying to find associated legal document but it not exists");
                }
                associatedLegalDocumentList.add(ldocument);
            }
            legalDocument.setAssociatedLegalDocuments(new ArrayList<LegalDocument>(associatedLegalDocumentList));
        }

        legalDocument.setSendNotificationChange(false);
        legalDocument.setFileName(fileName);

        legislationDAO.insert(legalDocument);

        //For each associated legal document, associate this one as well
        for (LegalDocument toAssociate : associatedLegalDocumentList) {
            if (!toAssociate.getAssociatedLegalDocuments().contains(legalDocument)) {
                toAssociate.getAssociatedLegalDocuments().add(legalDocument);
                legislationDAO.merge(toAssociate);
            }
        }

        //Jackrabbit insert
        File file = new File(legalDocument.getId(), contentType, inputStream, legalDocument.getFullDrTitle(),
                legalDocument.getCustomTitle(), legalDocument.getCustomAbstract(), legalDocument.getKeywords(),
                legalDocument.getSummary(), legalDocument.isPublished());

        //Put full dr title searchable by parts (number / year)
        //file.setFullDrTitle(legalDocument.getFullDrTitle() + " " + legalDocument.getNumber() + " " +
        //        legalDocument.getFullDrTitle().substring(legalDocument.getFullDrTitle().indexOf("/")+1,
        //                legalDocument.getFullDrTitle().indexOf(",")));

        try {
            repositoryDAO.insertFileOnFolder(RepositoryDAO.Folder.LEGISLATION_FOLDER, file);
        } catch (JackrabbitException e) {
            throw new BusinessException("Cannot insert document : problem with Jackrabbit repository", e);
        }
    }

    @RolesAllowed(value = {"legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void updateLegislation(LegalDocument legalDocument, Set<Long> legalDocumentCategories,
                                  String contentType, InputStream inputStream, String fileName, User user)
            throws ObjectNotFoundException, BusinessException, DocumentNotExistsException {


        LegalDocument legalDocumentDB =
                legislationService.findLegalDocumentFullyLoadedById(legalDocument.getId(), false);

        if (legalDocumentDB == null) {
            throw new ObjectNotFoundException("Trying to select Legal Document by Id and there were no results",
                    ObjectNotFoundException.Type.LEGAL_DOCUMENT);
        }

        //Year
        Calendar date = Calendar.getInstance();
        date.setTime(legalDocument.getPublicationDate());
        Integer temp = date.get(Calendar.YEAR);

        if (!legalDocumentDB.getNumber().equals(legalDocument.getNumber())
                || !legalDocumentDB.getYear().equals(temp.toString()) || !legalDocumentDB.getDocumentType()
                .getId().equals(legalDocument.getDocumentType().getId())) {

            //Check if document already exists
            if (legislationDAO.findLegalDocumentByTypeNumberAndYear(legalDocument.getDocumentType().getId(),
                    legalDocument.getNumber(), legalDocument.getYear()) != null) {
                throw new BusinessException("Trying to insert a duplicated legal document");
            }
        }

        legalDocumentDB.setYear(temp.toString());
        legalDocumentDB.setCustomAbstract(legalDocument.getCustomAbstract());
        legalDocumentDB.setCustomTitle(legalDocument.getCustomTitle());
        legalDocumentDB.setKeywords(legalDocument.getKeywords());
        legalDocumentDB.setNumber(legalDocument.getNumber());
        legalDocumentDB.setSummary(legalDocument.getSummary());
        legalDocumentDB.setTransitoryProvisions(legalDocument.getTransitoryProvisions());
        legalDocumentDB.setLegalComplianceValidation(legalDocument.getLegalComplianceValidation());
        legalDocumentDB.setReferenceArticles(legalDocument.getReferenceArticles());

        legalDocumentDB.setSendNotificationNew(legalDocument.isSendNotificationNew());
        legalDocumentDB.setSendNotificationChange(legalDocument.isSendNotificationChange());
        legalDocumentDB.setPublicationDate(legalDocument.getPublicationDate());

        //Document Type check
        LegalDocumentType legalDocumentType =
                legislationDAO.findLegalDocumentTypeById(legalDocument.getDocumentType().getId());

        if (legalDocumentType == null) {
            throw new ObjectNotFoundException("Trying to select Legal Document Type by Id and there were no results",
                    ObjectNotFoundException.Type.LEGISLATION);
        }
        legalDocumentDB.setDocumentType(legalDocumentType);

        //Document State check
        LegalDocumentState legalDocumentState =
                legislationDAO.findLegalDocumentStateById(legalDocument.getDocumentState().getId());

        if (legalDocumentState == null) {
            throw new ObjectNotFoundException("Trying to select Legal Document State by Id and there were no results",
                    ObjectNotFoundException.Type.LEGISLATION);
        }

        if (!legalDocumentDB.getDocumentState().getId().equals(legalDocument.getDocumentState().getId())) {
            legalDocumentDB.setStateChangedDate(new Date());
        }
        legalDocumentDB.setChangedBy(user);
        legalDocumentDB.setDocumentState(legalDocumentState);

        //legalDocumentCategories
        Set<LegalDocumentCategory> toInsertCategories =
                new HashSet<LegalDocumentCategory>(
                        legislationDAO.findLegalDocumentCategoriesForSelect(legalDocumentCategories));

        Set<LegalDocumentCategory> parentList = new HashSet<LegalDocumentCategory>();

        // Build parents Category List
        for (LegalDocumentCategory legalDocumentCategory : toInsertCategories) {
            while (legalDocumentCategory.getParentCategory() != null) {
                legalDocumentCategory = legalDocumentCategory.getParentCategory();
                parentList.add(legalDocumentCategory);
            }
        }

        //All categories to Insert
        toInsertCategories.addAll(parentList);

        Set<LegalDocumentCategory> toRemove = new HashSet<LegalDocumentCategory>();
        Set<LegalDocumentCategory> newCategories = new HashSet<LegalDocumentCategory>();
        Set<LegalDocumentCategory> notChangedCategories = new HashSet<LegalDocumentCategory>();

        //Set All not present categories
        for (LegalDocumentCategory ldc : legalDocumentDB.getLegalDocumentCategories()) {
            if (!toInsertCategories.contains(ldc)) {
                toRemove.add(ldc);
            } else {
                notChangedCategories.add(ldc);
            }
        }

        //Set All new categories
        for (LegalDocumentCategory ldc : toInsertCategories) {
            if (!legalDocumentDB.getLegalDocumentCategories().contains(ldc)) {
                newCategories.add(ldc);

            } else {
                notChangedCategories.add(ldc);
            }
        }

        //Remove old categories
        for (LegalDocumentCategory remove : toRemove) {

            if (remove.getAllAssociatedDocumentsCounter() == 0) {
                remove.setAllAssociatedDocumentsCounter(1L);
            }
            if (remove.getActiveAssociatedDocumentsCounter() == 0) {
                remove.setActiveAssociatedDocumentsCounter(1L);
            }
            if (legalDocument.isPublished()) {
                remove.setActiveAssociatedDocumentsCounter(remove.getActiveAssociatedDocumentsCounter() - 1);
            }
            remove.setAllAssociatedDocumentsCounter(remove.getAllAssociatedDocumentsCounter() - 1);

            legalDocumentCategoryDAO.merge(remove);
        }

        //New categories, then increment counters
        for (LegalDocumentCategory newC : newCategories) {

            if (legalDocument.isPublished()) {
                newC.setActiveAssociatedDocumentsCounter(newC.getActiveAssociatedDocumentsCounter() + 1);
            }
            newC.setAllAssociatedDocumentsCounter(newC.getAllAssociatedDocumentsCounter() + 1);
        }

        //New categories, then increment counters
        for (LegalDocumentCategory notChangedC : notChangedCategories) {

            if (!legalDocument.isPublished() && legalDocumentDB.isPublished()) {

                if (notChangedC.getActiveAssociatedDocumentsCounter() == 0) {
                    notChangedC.setActiveAssociatedDocumentsCounter(0L);

                } else {
                    notChangedC
                            .setActiveAssociatedDocumentsCounter(notChangedC.getActiveAssociatedDocumentsCounter() - 1);
                }
            } else if (legalDocument.isPublished() && !legalDocumentDB.isPublished()) {
                notChangedC
                        .setActiveAssociatedDocumentsCounter(notChangedC.getActiveAssociatedDocumentsCounter() + 1);
            }
        }

        List<LegalDocumentCategory> finalList = new ArrayList<LegalDocumentCategory>(notChangedCategories);
        finalList.addAll(newCategories);

        legalDocumentDB.setLegalDocumentCategories(finalList);
        legalDocumentDB.setPublished(legalDocument.isPublished());

        Set<LegalDocument> associatedLegalDocumentList = new HashSet<LegalDocument>();
        Set<LegalDocument> removedAssociatedLegalDocumentList = new HashSet<LegalDocument>();

        if (legalDocument.getAssociatedLegalDocuments() == null || legalDocument.getAssociatedLegalDocuments()
                .isEmpty()) {

            //all associated Legal Documents removed
            removedAssociatedLegalDocumentList =
                    new HashSet<LegalDocument>(legalDocumentDB.getAssociatedLegalDocuments());

            legalDocumentDB.setAssociatedLegalDocuments(new ArrayList<LegalDocument>());

        } else {

            for (LegalDocument associatedLegalDocument : legalDocument.getAssociatedLegalDocuments()) {
                LegalDocument ldocument = legislationDAO.findLegalDocumentByTypeNumberAndYear(
                        associatedLegalDocument.getDocumentType().getId(),
                        getDocumentNumber(associatedLegalDocument.getDrTitle()),
                        getDocumentYear(associatedLegalDocument.getDrTitle()));

                if (ldocument == null) {
                    throw new BusinessException("Trying to find associated legal document but it not exists");
                }
                associatedLegalDocumentList.add(ldocument);
            }

            //Removed associated legal documents
            for (LegalDocument ld : legalDocumentDB.getAssociatedLegalDocuments()) {
                if (!associatedLegalDocumentList.contains(ld)) {
                    removedAssociatedLegalDocumentList.add(ld);
                }
            }

            legalDocumentDB.setAssociatedLegalDocuments(new ArrayList<LegalDocument>(associatedLegalDocumentList));
        }

        if (fileName != null) {
            legalDocumentDB.setFileName(fileName);
        }

        legislationDAO.merge(legalDocumentDB);

        //For each associated legal document, associate this one as well
        for (LegalDocument toAssociate : associatedLegalDocumentList) {
            if (!toAssociate.getAssociatedLegalDocuments().contains(legalDocumentDB)) {
                toAssociate.getAssociatedLegalDocuments().add(legalDocument);
                legislationDAO.merge(toAssociate);
            }
        }

        //removed this legal document from removed associated legal documents
        for (LegalDocument toRem : removedAssociatedLegalDocumentList) {
            toRem.getAssociatedLegalDocuments().remove(legalDocumentDB);
            legislationDAO.merge(toRem);
        }

        //Add new File into Repository
        //Jackrabbit insert
        File file = new File(legalDocumentDB.getId(), contentType, inputStream, legalDocumentDB.getFullDrTitle(),
                legalDocumentDB.getCustomTitle(), legalDocumentDB.getCustomAbstract(),
                legalDocumentDB.getKeywords(),
                legalDocumentDB.getSummary(), legalDocumentDB.isPublished());

        //Put full dr title searchable by parts (number / year)
        file.setFullDrTitle(legalDocumentDB.getFullDrTitle() + " " + legalDocumentDB.getNumber() + " " +
                legalDocumentDB.getFullDrTitle().substring(legalDocumentDB.getFullDrTitle().indexOf("/") + 1,
                        legalDocumentDB.getFullDrTitle().indexOf(",")));

        try {
            repositoryDAO.updateFileOnFolder(RepositoryDAO.Folder.LEGISLATION_FOLDER, file);
        } catch (JackrabbitException e) {
            throw new BusinessException("Cannot updateFileOnFolder document: problem with Jackrabbit repository", e);
        }
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    @RolesAllowed(value = {"legislationmanager"})
    public void deleteLegalDocument(Long documentId) throws ObjectNotFoundException, BusinessException {

        LegalDocument ld = legislationDAO.findById(documentId);

        if (ld == null) {
            throw new ObjectNotFoundException("Trying to find legal document by id to delete, but does not exists",
                    ObjectNotFoundException.Type.LEGAL_DOCUMENT);
        }

        //Remove from User history
        List<LegalDocumentHistory> legalDocumentHistoryList =
                legalDocumentHistoryDAO.findAllHistoryByDocument(documentId);

        for (LegalDocumentHistory ldh : legalDocumentHistoryList) {
            if (ldh.getLegalDocument().getId().equals(ld.getId())) {
                legalDocumentHistoryDAO.delete(ldh);
            }
        }
        legalDocumentHistoryDAO.flush();

        //Remove document categories counters

        for (LegalDocumentCategory ldc : ld.getLegalDocumentCategories()) {
            if (ldc.getAllAssociatedDocumentsCounter() == 0) {
                ldc.setAllAssociatedDocumentsCounter(0L);

            } else {
                ldc.setAllAssociatedDocumentsCounter(ldc.getAllAssociatedDocumentsCounter() - 1);
            }

            if (ld.isPublished()) {

                if (ldc.getActiveAssociatedDocumentsCounter() == 0) {
                    ldc.setActiveAssociatedDocumentsCounter(0L);
                } else {
                    ldc.setActiveAssociatedDocumentsCounter(ldc.getActiveAssociatedDocumentsCounter() - 1);
                }
            }
            legalDocumentCategoryDAO.merge(ldc);
        }

        legislationDAO.delete(legislationDAO.findById(documentId));

        //Remove file from repository
        try {
            repositoryDAO.removeFileOnFolder(RepositoryDAO.Folder.LEGISLATION_FOLDER, documentId);
        } catch (JackrabbitException e) {
            throw new BusinessException("cannot delete document: problem with Jackrabbit repository");
        }
    }

    @RolesAllowed(value = {"user", "legislationmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public File findLegalDocumentFileToDownload(Long legalDocumentId)
            throws BusinessException, DocumentNotExistsException, CertitoolsAuthorizationException {

        if (sessionContext.isCallerInRole("userguest")) {
            throw new CertitoolsAuthorizationException(
                    "User with role 'userguest' is trying to download legal document");
        }

        LegalDocument ld = legislationDAO.findById(legalDocumentId);

        if (ld == null) {
            throw new DocumentNotExistsException("Cannot find file to Download");
        }

        try {
            File f = repositoryDAO.findFileOnFolder(RepositoryDAO.Folder.LEGISLATION_FOLDER, ld.getId());
            f.setFileName(ld.getFileName());
            return f;
        } catch (JackrabbitException e) {
            throw new BusinessException("Cannot get document: problem with Jackrabbit repository", e);
        }
    }

    @RolesAllowed(value = {"legislationmanager", "administrator", "contractmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<LegalDocumentStatistics> findVisualizationAndDownloadStatistcs(Date initDate, Date endDate) {

        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        List<LegalDocumentStatistics> all =
                legalDocumentDAO
                        .findAllBetween(LegalDocumentStatistics.ReportType.VISUALIZATION, initDate, end.getTime());

        int index = 0;
        for (LegalDocumentStatistics download : legalDocumentDAO
                .findAllBetween(LegalDocumentStatistics.ReportType.DOWNLOAD, initDate, end.getTime())) {
            if (all.contains(download)) {
                all.get(index).setCountDownloads(download.getCountDownloads());
            } else {
                all.add(all.size() - 1, download);
            }
            index++;
        }

        return all;
    }

    @RolesAllowed(value = {"legislationmanager", "administrator", "contractmanager"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public List<LegalDocumentStatistics> findSearchTermStatistcs(Date initDate, Date endDate) {
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        return legalDocumentDAO.findAllBetween(LegalDocumentStatistics.ReportType.SEARCH_TERM, initDate, end.getTime());
    }

    @PermitAll
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Collection<LegalDocument> findAll() {
        return legislationDAO.findAll();
    }

    @PermitAll
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public LegalDocumentCategory findLegalDocumentCategoryByName(String category, Long depth, String parent) {
        return legislationDAO.findLegalDocumentCategoryByName(category, depth, parent);
    }

    @PermitAll
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public LegalDocumentType findLegalDocumentTypeByName(String type) {
        return legislationDAO.findLegalDocumentTypeByName(type);
    }

    @PermitAll
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public LegalDocumentState findLegalDocumentStateByName(String state) {
        return legislationDAO.findLegalDocumentStateByName(state);
    }

    private String getDocumentNumber(String drTitle) throws BusinessException {
        String[] split = drTitle.split("/");

        if (split.length < 2) {
            throw new BusinessException("drTitle invalid when trying to get number, Inserting LegalDocument");
        }

        return StringUtils.substring(drTitle, 0, StringUtils.lastIndexOf(drTitle, "/"));
    }


    private String getDocumentYear(String drTitle) throws BusinessException {
        String[] splitAll = drTitle.split(",");

        if (splitAll.length != 2) {
            throw new BusinessException("drTitle invalid when trying to get year, Inserting LegalDocument");
        }

        return StringUtils.substring(splitAll[0], StringUtils.lastIndexOf(splitAll[0], "/") + 1);
    }

    private List<LegalDocumentCategory> buildList(List<LegalDocumentCategory> list, int index) {
        if (list.isEmpty()) {
            return new ArrayList<LegalDocumentCategory>();
        }

        LegalDocumentCategory ldCategrory = list.get(index);

        List<LegalDocumentCategory> children = legislationDAO.findAllDocumentCategoryChildren(ldCategrory.getId());
        if (children.isEmpty()) {
            ldCategrory.setHasChildren(false);
            if (index == (list.size() - 1)) {
                return list;
            }
            return buildList(list, index + 1);
        } else {
            ldCategrory.setHasChildren(true);
            list.addAll(index + 1, children);
            return buildList(list, index + 1);
        }
    }

    @RolesAllowed(value = {"legislationmanager"})
    public void updateRepository() throws BusinessException {
        try {
            repositoryDAO.updateFullDrTitleField();
        } catch (JackrabbitException e) {
            throw new BusinessException("Cannot updateFileOnFolder document: problem with Jackrabbit repository", e);
        }
    }

    @RolesAllowed(value = {"administrator"})
    public void updateFilenames() throws BusinessException {

        Collection<LegalDocument> legalDocuments = legislationDAO.findAll();

        for (LegalDocument legalDocument : legalDocuments) {
            String filename =
                    Utils.removeAccentedChars(legalDocument.getDocumentType().getName()).trim() + "_"
                            + legalDocument.getNumber().trim()
                            + "_" + legalDocument.getYear().trim() + ".pdf";
            filename = filename.replaceAll(" ", "_");
            System.out.println("Filename set to: " + filename);
            legalDocument.setFileName(filename);
        }
    }

    @RolesAllowed(value = {"legislationmanager"})
    public void fixCategoriesCounter() {
        List<LegalDocumentCategory> allCategories = legislationDAO.findAllLegalDocumentCategoryWithDocuments();

        Long allDocumentCounter = 0L;
        Long activeCounter = 0L;

        for (LegalDocumentCategory ldcategory : allCategories) {
            System.out.println("*********Category : " + ldcategory.getName() + "*************");
            for (LegalDocument ld : ldcategory.getLegalDocuments()) {

                allDocumentCounter++;
                if (ld.isPublished()) {
                    activeCounter++;
                }
            }
            ldcategory.setActiveAssociatedDocumentsCounter(activeCounter);
            ldcategory.setAllAssociatedDocumentsCounter(allDocumentCounter);
            System.out.println("***ALL: " + allDocumentCounter);
            System.out.println("***Active: " + activeCounter);

            legalDocumentCategoryDAO.merge(ldcategory);

            allDocumentCounter = 0L;
            activeCounter = 0L;
        }
    }
}
