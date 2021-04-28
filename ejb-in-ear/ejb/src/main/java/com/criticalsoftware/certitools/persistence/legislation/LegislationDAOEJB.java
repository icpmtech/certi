/*
 * $Id: LegislationDAOEJB.java,v 1.26 2009/09/23 10:53:14 jp-gomes Exp $
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
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import com.criticalsoftware.certitools.util.Logger;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * <description>
 *
 * @author jp-gomes
 */
@Stateless
@Local(LegislationDAO.class)
@LocalBinding(jndiBinding = "certitools/LegislationDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LegislationDAOEJB extends GenericDAOEJB<LegalDocument, Long> implements LegislationDAO {

    private static final Logger LOGGER = Logger.getInstance(LegislationDAOEJB.class);

    @SuppressWarnings({"unchecked"})
    public List<LegalDocument> findAllLegalDocumentWithCategoriesLoaded() {
        Query query = manager.createQuery(
                "SELECT DISTINCT doc FROM LegalDocument doc INNER JOIN FETCH doc.legalDocumentCategories");
        return (List<LegalDocument>) query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentCategory> findAllLegalDocumentCategoryWithDocuments() {

        Query query = manager.createQuery(
                "SELECT DISTINCT cat from LegalDocumentCategory  cat INNER JOIN cat.legalDocuments AS ld");

        return (List<LegalDocumentCategory>) query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentCategory> findAllLegalDocumentCategories(Long legalDocumentId) {

        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT DISTINCT ldc from LegalDocumentCategory ldc ")
                .append("INNER JOIN FETCH ldc.legalDocuments docs WHERE docs.id=?1 ");

        Query query = manager.createQuery(sb.toString());

        query.setParameter(1, legalDocumentId);
        return (List<LegalDocumentCategory>) query.getResultList();
    }

    public Integer findLegalDocumentAssociationsCounter(Long legalDocumentId) {

        Query query = manager.createNativeQuery(
                "SELECT count(*) FROM legaldocument_legaldocument where associatedlegaldocuments_id =?1");
        query.setParameter(1, legalDocumentId);
        BigInteger o = (BigInteger) query.getSingleResult();
        return o.intValue();

    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocument> findLegalDocumentPublishedHistory(Long history) {
        Query query = manager.createQuery(
                "SELECT l from LegalDocument l WHERE l.published =?1 ORDER BY l.publicationDate DESC");

        query.setMaxResults(history.intValue());
        query.setParameter(1, true);
        return (List<LegalDocument>) query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocument> findLegalDocumentsByCategory(int currentPage, int resultPerPage, Long categoryId,
                                                            Boolean isAdmin) {

        StringBuilder sb = new StringBuilder()
                .append("SELECT DISTINCT l FROM LegalDocument l INNER JOIN FETCH l.legalDocumentCategories categories ")
                .append("WHERE categories.id = ?1 ");

        if (!isAdmin) {
            sb.append(" AND l.published = ?2 ");
        }

        sb.append(" ORDER BY l.documentState ASC, ").append(" l.publicationDate DESC ");

        Query query = manager.createQuery(sb.toString());
        query.setParameter(1, categoryId);

        query.setFirstResult(currentPage);
        query.setMaxResults(resultPerPage);

        if (!isAdmin) {
            query.setParameter(2, true);
        }

        return (List<LegalDocument>) query.getResultList();
    }

    public int countDocumentsByCategory(Long categoryId, Boolean isAdmin) {

        StringBuilder sb = new StringBuilder()
                .append("SELECT DISTINCT l FROM LegalDocument l INNER JOIN FETCH l.legalDocumentCategories categories ")
                .append("WHERE categories.id = ?1 ");

        if (!isAdmin) {
            sb.append(" AND l.published = ?2 ");
        }

        Query query = manager.createQuery(sb.toString());
        query.setParameter(1, categoryId);

        if (!isAdmin) {
            query.setParameter(2, true);
        }
        return query.getResultList().size();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentCategory> findLegalDocumentCategoriesByDepthAndId(Long id, Long depth) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT l from LegalDocumentCategory l where l.depth =?1 ");

        if (id != null) {
            sb.append(" AND l.parentCategory.id = ?2 ");
        }
        sb.append(" ORDER BY l.name");

        Query query = manager.createQuery(sb.toString());

        if (depth == null) {
            query.setParameter(1, 1L);

        } else {
            query.setParameter(1, depth);
        }

        if (id != null) {
            query.setParameter(2, id);
        }

        return (List<LegalDocumentCategory>) query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentType> findAllLegalDocumentTypes() {
        Query query = manager.createQuery("SELECT l FROM LegalDocumentType l ORDER BY l.name ASC");
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentCategory> findTopParentDocumentCategory() {
        Query query = manager.createQuery(
                "SELECT l FROM LegalDocumentCategory l WHERE l.parentCategory IS NULL ORDER BY l.name");
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentCategory> findAllDocumentCategoryChildren(Long parentId) {
        Query query = manager.createQuery(
                "SELECT l FROM LegalDocumentCategory l WHERE l.parentCategory.id = ?1 ORDER BY l.name ");
        query.setParameter(1, parentId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentState> findAllLegalDocumentState() {
        Query query = manager.createQuery("SELECT l FROM LegalDocumentState l ORDER BY l.name ASC");
        return query.getResultList();
    }

    @Override
    public LegalDocumentCategory findLegalDocumentCategoryById(Long legalDocumentCategoryId) {
        Query query = manager.createQuery("SELECT cat FROM LegalDocumentCategory cat WHERE cat.id = ?1");
        query.setParameter(1, legalDocumentCategoryId);

        try {
            return (LegalDocumentCategory) query.getSingleResult();
        } catch (NoResultException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No entity found");
            }
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocument> findLegalDocumentsIn(List<Long> legalDocumentsIds) {
        Query query = manager.createQuery(
                "SELECT DISTINCT l FROM LegalDocument l INNER JOIN FETCH l.legalDocumentCategories WHERE l.id IN (?1)");
        query.setParameter(1, legalDocumentsIds);

        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocument> findLegalDocumentsByType(Long documentTypeId) {
        Query query = manager.createQuery("SELECT l FROM LegalDocument l WHERE l.documentType.id = ?1");
        query.setParameter(1, documentTypeId);
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocument> findLegalDocumentsByTypeAutoComplete(Long documentTypeId, String number, String year) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT l from LegalDocument l WHERE l.documentType.id = ?1 ");

        if (number != null && number.trim().length() != 0) {
            sb.append(" AND l.number like ?2");
        }

        if (year != null && year.trim().length() != 0) {
            sb.append(" AND l.year like ?3");
        }
        Query query = manager.createQuery(sb.toString());
        query.setParameter(1, documentTypeId);

        if (number != null && number.trim().length() != 0) {
            query.setParameter(2, number + '%');
        }

        if (year != null && year.trim().length() != 0) {
            query.setParameter(3, year + '%');
        }

        return (List<LegalDocument>) query.getResultList();
    }


    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentCategory> findAllDocumentCategory() {
        Query query = manager.createQuery("SELECT new(ldc.id, ldc.name) FROM LegalDocumentCategory ldc ORDER by ");
        return query.getResultList();
    }

    public LegalDocumentType findLegalDocumentTypeById(Long id) {
        Query query = manager.createQuery("SELECT l FROM LegalDocumentType l WHERE l.id = ?1");
        query.setParameter(1, id);

        try {
            return (LegalDocumentType) query.getSingleResult();
        } catch (NoResultException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No entity found");
            }
            return null;
        }
    }

    public LegalDocumentState findLegalDocumentStateById(Long id) {
        Query query = manager.createQuery("SELECT l FROM LegalDocumentState l WHERE l.id = ?1");
        query.setParameter(1, id);

        try {
            return (LegalDocumentState) query.getSingleResult();
        } catch (NoResultException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No entity found");
            }
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public List<LegalDocumentCategory> findLegalDocumentCategoriesForSelect(Set<Long> ids) {

        StringBuilder sb = new StringBuilder();
        Integer counter = 1;
        sb.append("SELECT l from LegalDocumentCategory l WHERE ");

        for (Long id : ids) {
            sb.append(" l.id = ?").append(counter);

            if (counter - 1 != ids.size() - 1) {
                sb.append(" OR");
            }
            counter++;
        }
        Query query = manager.createQuery(sb.toString());

        counter = 1;
        for (Long id : ids) {
            query.setParameter(counter, id);
            counter++;
        }
        return query.getResultList();
    }

    public LegalDocument findLegalDocumentByTypeNumberAndYear(Long documentTypeId, String number, String year) {
        Query query = manager.createQuery(
                "SELECT l from LegalDocument l where l.number = ?1 AND l.year = ?2 AND l.documentType.id = ?3");
        query.setParameter(1, number);
        query.setParameter(2, year);
        query.setParameter(3, documentTypeId);

        try {
            return (LegalDocument) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public LegalDocumentCategory findLegalDocumentCategoryByName(String category, Long depth, String parent) {
        Query query;
        if (parent != null) {
            query = manager.createQuery("SELECT c FROM LegalDocumentCategory c WHERE c.name = ?1 AND c.depth = ?2 " +
                    "AND c.parentCategory.name = ?3");
            query.setParameter(1, category);
            query.setParameter(2, depth);
            query.setParameter(3, parent);
        } else {
            query = manager.createQuery("SELECT c FROM LegalDocumentCategory c WHERE c.name = ?1 AND c.depth = ?2");
            query.setParameter(1, category);
            query.setParameter(2, depth);
        }

        try {
            return (LegalDocumentCategory) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public LegalDocumentType findLegalDocumentTypeByName(String type) {
        Query query = manager.createQuery(
                "SELECT t from LegalDocumentType t where t.name = ?1");
        query.setParameter(1, type);

        try {
            return (LegalDocumentType) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public LegalDocumentState findLegalDocumentStateByName(String state) {
        Query query = manager.createQuery(
                "SELECT s from LegalDocumentState s where s.name = ?1");
        query.setParameter(1, state);

        try {
            return (LegalDocumentState) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
