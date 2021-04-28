/*
 * $Id: LegislationDAO.java,v 1.16 2009/09/23 10:53:14 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/09/23 10:53:14 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.persistence.legislation;

import com.criticalsoftware.certitools.entities.LegalDocument;
import com.criticalsoftware.certitools.entities.LegalDocumentCategory;
import com.criticalsoftware.certitools.entities.LegalDocumentState;
import com.criticalsoftware.certitools.entities.LegalDocumentType;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.util.List;
import java.util.Set;

/**
 * Legislation DAO
 *
 * @author jp-gomes
 */
public interface LegislationDAO extends GenericDAO<LegalDocument, Long> {

    /**
     * Find All LegalDocuments with categories loaded
     *
     * @return legal document list
     */
    List<LegalDocument> findAllLegalDocumentWithCategoriesLoaded();

    List<LegalDocumentCategory> findAllLegalDocumentCategoryWithDocuments();

    /**
     * Count number of associated legal documents that a document has
     *
     * @param legalDocumentId - legal document to count
     * @return - counter
     */
    Integer findLegalDocumentAssociationsCounter(Long legalDocumentId);

    /**
     * Finds all Legal Documents States
     *
     * @return - List with Documents States
     */
    List<LegalDocumentState> findAllLegalDocumentState();

    /**
     * Finds all Legal Documents Types
     *
     * @return - List with Documents Types
     */
    List<LegalDocumentType> findAllLegalDocumentTypes();

    /**
     * Find Legal Document Category Top parents
     *
     * @return - List
     */
    List<LegalDocumentCategory> findTopParentDocumentCategory();

    List<LegalDocumentCategory> findAllDocumentCategoryChildren(Long parentId);

    /**
     * Find legal document categories
     *
     * @param legalDocumentId - legal document id
     * @return - legal document categories list
     */
    List<LegalDocumentCategory> findAllLegalDocumentCategories(Long legalDocumentId);

    /**
     * Find legal document type by id
     *
     * @param id - document type id
     * @return - document type
     */
    LegalDocumentType findLegalDocumentTypeById(Long id);

    /**
     * Find All Legal Document Category children, by depth and parent Id
     *
     * @param id    - parent Id
     * @param depth - depth
     * @return - List of Legal Document Categories
     */
    List<LegalDocumentCategory> findLegalDocumentCategoriesByDepthAndId(Long id, Long depth);

    /**
     * Find a legal document category by  id
     *
     * @param legalDocumentCategoryId the id
     * @return a legal document category
     */
    LegalDocumentCategory findLegalDocumentCategoryById(Long legalDocumentCategoryId);

    List<LegalDocument> findLegalDocumentsIn(List<Long> legalDocumentsIds);


    /**
     * Find legal documents by type, number and year for auto complete
     *
     * @param documentTypeId - document type Id
     * @param number         - legal document number
     * @param year           - legal document year
     * @return - legal document list
     */
    List<LegalDocument> findLegalDocumentsByTypeAutoComplete(Long documentTypeId, String number, String year);

    /**
     * Find Legal Document, by type, number and year
     *
     * @param documentTypeId - legal document type id
     * @param number         - legal document number
     * @param year-          legal document year
     * @return - legal document
     */
    LegalDocument findLegalDocumentByTypeNumberAndYear(Long documentTypeId, String number, String year);

    List<LegalDocumentCategory> findAllDocumentCategory();

    /**
     * Find legal document state by id
     *
     * @param id - document state id
     * @return - document state
     */
    LegalDocumentState findLegalDocumentStateById(Long id);

    /**
     * Finds a list of legal document cateogories, for select
     *
     * @param ids - set of legal document categories to select
     * @return - list of selected Legal Document Categories
     */
    List<LegalDocumentCategory> findLegalDocumentCategoriesForSelect(Set<Long> ids);

    /**
     * Finds legal documents by cateogry, with pagination. if it is admin, returns also not published legal documents
     *
     * @param firstResult - first result
     * @param maxResult   - max resukt
     * @param categoryId  - category Id
     * @param isAdmin     - has privileges role
     * @return - List of select legal documents
     */
    List<LegalDocument> findLegalDocumentsByCategory(int firstResult, int maxResult, Long categoryId,
                                                     Boolean isAdmin);

    /**
     * Count all legal document category documents. if it is admin, counts also not published legal documents
     *
     * @param categoryId - lega category category id
     * @param isAdmin    - has privileges role
     * @return - counter
     */
    int countDocumentsByCategory(Long categoryId, Boolean isAdmin);

    /**
     * Find last published legal documents
     *
     * @param history - number of documents (per user there aren t more then 5)
     * @return - List
     */
    List<LegalDocument> findLegalDocumentPublishedHistory(Long history);

    LegalDocumentCategory findLegalDocumentCategoryByName(String category, Long depth, String parent);

    LegalDocumentType findLegalDocumentTypeByName(String type);

    LegalDocumentState findLegalDocumentStateByName(String state);

}
