/*
 * $Id: LegalDocumentCategory.java,v 1.9 2009/04/06 11:33:52 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/04/06 11:33:52 $
 * Last changed by $Author: lt-rico $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <description>
 *
 * @author pjfsilva
 */
@Entity
public class LegalDocumentCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    private String name;

    @Basic(optional = false)
    private Long depth;

    @Basic(optional = false)
    private Long allAssociatedDocumentsCounter;

    @Basic(optional = false)
    private Long activeAssociatedDocumentsCounter;

    @ManyToMany(mappedBy = "legalDocumentCategories")
    private List<LegalDocument> legalDocuments;

    @OneToOne
    private LegalDocumentCategory parentCategory;

    @ManyToMany(mappedBy = "subscriptionsLegalDocuments", fetch = FetchType.LAZY)
    private Collection<User> users;

    @Transient
    private Boolean hasChildren;

    @Transient
    private Boolean toBeSubscrived;

    public LegalDocumentCategory() {}

    public LegalDocumentCategory(Long id) {
        this.id = id;
    }

    public LegalDocumentCategory(String name) {
        this.name = name;
        this.depth = null;
        this.id = null;
    }

    public Long getAllAssociatedDocumentsCounter() {
        return allAssociatedDocumentsCounter;
    }

    public void setAllAssociatedDocumentsCounter(Long allAssociatedDocumentsCounter) {
        this.allAssociatedDocumentsCounter = allAssociatedDocumentsCounter;
    }

    public Long getActiveAssociatedDocumentsCounter() {
        return activeAssociatedDocumentsCounter;
    }

    public void setActiveAssociatedDocumentsCounter(Long activeAssociatedDocumentsCounter) {
        this.activeAssociatedDocumentsCounter = activeAssociatedDocumentsCounter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDepth() {
        return depth;
    }

    public void setDepth(Long depth) {
        this.depth = depth;
    }

    public List<LegalDocument> getLegalDocuments() {
        return legalDocuments;
    }

    public void setLegalDocuments(List<LegalDocument> legalDocuments) {
        this.legalDocuments = legalDocuments;
    }

    public LegalDocumentCategory getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(LegalDocumentCategory parentCategory) {
        this.parentCategory = parentCategory;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Boolean getToBeSubscrived() {
        return toBeSubscrived;
    }

    public void setToBeSubscrived(Boolean toBeSubscrived) {
        this.toBeSubscrived = toBeSubscrived;
    }

    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LegalDocumentCategory that = (LegalDocumentCategory) o;

        if (!id.equals(that.id)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }
}
