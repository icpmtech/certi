/*
 * $Id: LegalDocument.java,v 1.21 2010/01/12 17:51:22 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/01/12 17:51:22 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * <description>
 *
 * @author pjfsilva
 */
@Entity
public class LegalDocument implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customTitle;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false, length = 4)
    private String year;

    @Temporal(value = TemporalType.DATE)
    @Column(nullable = false)
    private Date publicationDate;

    @Column(length = 512)
    private String keywords;

    @Column(length = 4096, nullable = false)
    private String summary;

    @Column(length = 10000)
    @Basic(fetch = FetchType.LAZY)
    private String customAbstract;

    @Column(length = 5000, nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private String legalComplianceValidation;

    @Column(length = 5000, nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private String referenceArticles;

    @Column(length = 4096)
    private String transitoryProvisions;

    @Column(nullable = false)
    private Date creationDate;

    private Date stateChangedDate;

    @Column(nullable = false)
    private boolean sendNotificationNew;

    @Column(nullable = false)
    private boolean sendNotificationChange;

    @Column(nullable = false)
    private boolean published;

    @ManyToOne(optional = false)
    private LegalDocumentType documentType;

    @ManyToOne(optional = false)
    private LegalDocumentState documentState;

    @ManyToOne(fetch = FetchType.LAZY)
    private User changedBy;

    @ManyToMany
    private List<LegalDocument> associatedLegalDocuments;

    @ManyToMany
    private List<LegalDocumentCategory> legalDocumentCategories;

    @Transient
    private String drTitle;

    @Transient
    private String fullDrTitle;

    @Transient
    private String reducedField;

    @Transient
    private List<LegalDocumentCategory[]> categoryNavegation;

    public LegalDocument() {
    }

    public LegalDocument(Long id) {
        this.id = id;
    }

    public LegalDocument(LegalDocument legalDocument) {
        this.id = legalDocument.getId();
        this.fullDrTitle = legalDocument.getFullDrTitle();
        this.documentType = legalDocument.getDocumentType();
        this.number = legalDocument.getNumber();
        this.publicationDate = legalDocument.getPublicationDate();
        this.keywords = legalDocument.getKeywords();
        this.summary = legalDocument.getSummary();
        if (legalDocument.getCustomAbstract() != null) {
            this.customAbstract = legalDocument.getCustomAbstract();
        }
        if (legalDocument.getAssociatedLegalDocuments() != null) {
            this.associatedLegalDocuments = legalDocument.getAssociatedLegalDocuments();
        }
        this.transitoryProvisions = legalDocument.getTransitoryProvisions();
        this.documentState = legalDocument.getDocumentState();
        this.published = legalDocument.isPublished();
        this.customTitle = legalDocument.getCustomTitle();
        this.legalComplianceValidation = legalDocument.getLegalComplianceValidation();
        this.referenceArticles = legalDocument.getReferenceArticles();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCustomAbstract() {
        return customAbstract;
    }

    public void setCustomAbstract(String customAbstract) {
        this.customAbstract = customAbstract;
    }

    public String getTransitoryProvisions() {
        return transitoryProvisions;
    }

    public void setTransitoryProvisions(String transitoryProvisions) {
        this.transitoryProvisions = transitoryProvisions;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getStateChangedDate() {
        return stateChangedDate;
    }

    public void setStateChangedDate(Date stateChangedDate) {
        this.stateChangedDate = stateChangedDate;
    }

    public boolean isSendNotificationNew() {
        return sendNotificationNew;
    }

    public void setSendNotificationNew(boolean sendNotificationNew) {
        this.sendNotificationNew = sendNotificationNew;
    }

    public boolean isSendNotificationChange() {
        return sendNotificationChange;
    }

    public void setSendNotificationChange(boolean sendNotificationChange) {
        this.sendNotificationChange = sendNotificationChange;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LegalDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(LegalDocumentType documentType) {
        this.documentType = documentType;
    }

    public LegalDocumentState getDocumentState() {
        return documentState;
    }

    public void setDocumentState(LegalDocumentState documentState) {
        this.documentState = documentState;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public List<LegalDocument> getAssociatedLegalDocuments() {
        return associatedLegalDocuments;
    }

    public void setAssociatedLegalDocuments(List<LegalDocument> associatedLegalDocuments) {
        this.associatedLegalDocuments = associatedLegalDocuments;
    }

    public List<LegalDocumentCategory> getLegalDocumentCategories() {
        return legalDocumentCategories;
    }

    public void setLegalDocumentCategories(List<LegalDocumentCategory> legalDocumentCategories) {
        this.legalDocumentCategories = legalDocumentCategories;
    }

    public void setDrTitle(String drTitle) {
        this.drTitle = drTitle;
    }

    public String getReducedField() {
        return reducedField;
    }

    public void setReducedField(String reducedField) {
        this.reducedField = reducedField;
    }

    public String getFullDrTitle() {
        if (documentType != null) {
            return documentType.getName() + " n.� " + getDrTitle();
        }
        return fullDrTitle;
    }

    public void setFullDrTitle(String fullDrTitle) {
        this.fullDrTitle = fullDrTitle;
    }

    public List<LegalDocumentCategory[]> getCategoryNavegation() {
        return categoryNavegation;
    }

    public void setCategoryNavegation(List<LegalDocumentCategory[]> categoryNavegation) {
        this.categoryNavegation = categoryNavegation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLegalComplianceValidation() {
        return legalComplianceValidation;
    }

    public void setLegalComplianceValidation(String legalComplianceValidation) {
        this.legalComplianceValidation = legalComplianceValidation;
    }

    public String getReferenceArticles() {
        return referenceArticles;
    }

    public void setReferenceArticles(String referenceArticles) {
        this.referenceArticles = referenceArticles;
    }

    public String getDrTitle() {

        StringBuilder sb = new StringBuilder();
        if (publicationDate == null || number == null) {
            return drTitle;
        }

        Locale locale = new Locale("pt");

        SimpleDateFormat dayFormat = new SimpleDateFormat("d", locale);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMMM", locale);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", locale);

        return sb.append(number).append("/").append(yearFormat.format(publicationDate)).append(", ")
                .append(dayFormat.format(publicationDate))
                .append(" de ").append(monthFormat.format(publicationDate)).toString();

    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LegalDocument that = (LegalDocument) o;

        return id.equals(that.id);

    }

    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
