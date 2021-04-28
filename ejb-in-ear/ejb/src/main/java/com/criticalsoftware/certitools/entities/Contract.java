/*
 * $Id: Contract.java,v 1.18 2011/06/21 16:14:46 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2011/06/21 16:14:46 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities;

import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.entities.sm.SubModule;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * <description>
 *
 * @author : pjfsilva
 */
@Entity
public class Contract implements Serializable, Comparable<Contract> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 128, nullable = false)
    private String number;

    @Column
    private String fileName;

    @Column(nullable = false)
    private String contractDesignation;

    @ManyToOne(optional = false)
    private Company company;

    @Column(nullable = false)
    private int licenses;

    @Column(nullable = false)
    private Date validityStartDate;

    @Column(nullable = false)
    private Date validityEndDate;

    private Double value;

    private String contractDesignationMaintenance;

    private Double valueMaintenance;

    // contract file id in the Repository
    private String contractFile;

    // contact information
    @Column(length = 128)
    private String contactName;
    @Column(length = 128)
    private String contactPosition;
    private String contactEmail;
    @Column(length = 32)
    private String contactPhone;

    // application level activation (inactive are still visible in the application)
    @Column(nullable = false)
    private boolean active = true;

    // entity is deleted, if true entity was deleted by the user
    @Column(nullable = false)
    private boolean deleted = false;

    //Inactivity Templates and Deadline terms
    @Column(nullable = true, length = 200)
    private String firstInactivityMessageTemplateSubject;

    @Column(nullable = true, length = 500)
    private String firstInactivityMessageTemplateBody;

    @Column(nullable = false)
    private int firstInactivityMessageTerm;

    @Column(nullable = true, length = 200)
    private String secondInactivityMessageTemplateSubject;

    @Column(nullable = true, length = 500)
    private String secondInactivityMessageTemplateBody;

    @Column(nullable = false)
    private int secondInactivityMessageTerm;

    @Column(nullable = false)
    private int deleteUserTerm;

    @Column(nullable = true, length = 128)
    private String userRegisterCode;

    @Column(nullable = true, length = 256)
    private String userRegisterDomains;

    @Column(nullable = true, length = 64)
    private String menuLabel;

    @ManyToOne(optional = false)
    private java.lang.Module module;

    @OneToMany(mappedBy = "contract", fetch = FetchType.LAZY)
    private Collection<UserContract> userContract;

    @ManyToMany(mappedBy = "contracts")
    private Collection<TemplateDocx> templatesDocx;

    //security management attributes (logo and cover pictures)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sm_logopicture_id")
    private Document smLogoPicture;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sm_coverpicture_id")
    private Document smCoverPicture;

    //sub modules
    @OneToMany(mappedBy = "contract", fetch = FetchType.EAGER)
    private List<SubModule> subModules;

    // validity is ok
    @Transient
    private Boolean dateActive = null;

    @Transient
    public Collection<Permission> contractPermissions;

    @Transient
    public boolean contractInactivityOn;

    public Contract() {
    }

    public Contract(long id, String number, String contractDesignation, java.lang.Module module) {
        this.id = id;
        this.number = number;
        this.contractDesignation = contractDesignation;
        this.module = module;
    }

    public Contract(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getContractDesignation() {
        return contractDesignation;
    }

    public void setContractDesignation(String contractDesignation) {
        this.contractDesignation = contractDesignation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public int getLicenses() {
        return licenses;
    }

    public void setLicenses(int licenses) {
        this.licenses = licenses;
    }

    public Date getValidityStartDate() {
        return validityStartDate;
    }

    public void setValidityStartDate(Date validityStartDate) {
        this.validityStartDate = validityStartDate;
    }

    public Date getValidityEndDate() {
        return validityEndDate;
    }

    public void setValidityEndDate(Date validityEndDate) {
        this.validityEndDate = validityEndDate;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getContractDesignationMaintenance() {
        return contractDesignationMaintenance;
    }

    public void setContractDesignationMaintenance(String contractDesignationMaintenance) {
        this.contractDesignationMaintenance = contractDesignationMaintenance;
    }

    public Double getValueMaintenance() {
        return valueMaintenance;
    }

    public void setValueMaintenance(Double valueMaintenance) {
        this.valueMaintenance = valueMaintenance;
    }

    public String getContractFile() {
        return contractFile;
    }

    public void setContractFile(String contractFile) {
        this.contractFile = contractFile;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPosition() {
        return contactPosition;
    }

    public void setContactPosition(String contactPosition) {
        this.contactPosition = contactPosition;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getFirstInactivityMessageTerm() {
        return firstInactivityMessageTerm;
    }

    public void setFirstInactivityMessageTerm(int firstInactivityMessageTerm) {
        this.firstInactivityMessageTerm = firstInactivityMessageTerm;
    }

    public String getFirstInactivityMessageTemplateSubject() {
        return firstInactivityMessageTemplateSubject;
    }

    public void setFirstInactivityMessageTemplateSubject(String firstInactivityMessageTemplateSubject) {
        this.firstInactivityMessageTemplateSubject = firstInactivityMessageTemplateSubject;
    }

    public String getFirstInactivityMessageTemplateBody() {
        return firstInactivityMessageTemplateBody;
    }

    public void setFirstInactivityMessageTemplateBody(String firstInactivityMessageTemplateBody) {
        this.firstInactivityMessageTemplateBody = firstInactivityMessageTemplateBody;
    }

    public String getSecondInactivityMessageTemplateSubject() {
        return secondInactivityMessageTemplateSubject;
    }

    public void setSecondInactivityMessageTemplateSubject(String secondInactivityMessageTemplateSubject) {
        this.secondInactivityMessageTemplateSubject = secondInactivityMessageTemplateSubject;
    }

    public String getSecondInactivityMessageTemplateBody() {
        return secondInactivityMessageTemplateBody;
    }

    public void setSecondInactivityMessageTemplateBody(String secondInactivityMessageTemplateBody) {
        this.secondInactivityMessageTemplateBody = secondInactivityMessageTemplateBody;
    }

    public int getSecondInactivityMessageTerm() {
        return secondInactivityMessageTerm;
    }

    public void setSecondInactivityMessageTerm(int secondInactivityMessageTerm) {
        this.secondInactivityMessageTerm = secondInactivityMessageTerm;
    }

    public int getDeleteUserTerm() {
        return deleteUserTerm;
    }

    public void setDeleteUserTerm(int deleteUserTerm) {
        this.deleteUserTerm = deleteUserTerm;
    }

    public boolean isContractInactivityOn() {
        return firstInactivityMessageTerm != 0 && secondInactivityMessageTerm != 0 && deleteUserTerm != 0;
    }

    public String getUserRegisterCode() {
        return userRegisterCode;
    }

    public void setUserRegisterCode(String userRegisterCode) {
        this.userRegisterCode = userRegisterCode;
    }

    public String getUserRegisterDomains() {
        return userRegisterDomains;
    }

    public void setUserRegisterDomains(String userRegisterDomains) {
        this.userRegisterDomains = userRegisterDomains;
    }

    public java.lang.Module getModule() {
        return module;
    }

    public void setModule(java.lang.Module module) {
        this.module = module;
    }

    public Collection<UserContract> getUserContract() {
        return userContract;
    }

    public void setUserContract(
            Collection<UserContract> userContract) {
        this.userContract = userContract;
    }

    public Collection<Permission> getContractPermissions() {
        return contractPermissions;
    }

    public void setContractPermissions(Collection<Permission> contractPermissions) {
        this.contractPermissions = contractPermissions;
    }

    public Boolean getDateActive() {
        if (dateActive == null) {
            dateActive = false;

            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTime(new Date());

            Calendar contractStartDate = Calendar.getInstance();
            Calendar contractEndDate = Calendar.getInstance();
            contractStartDate.setTime(getValidityStartDate());
            contractEndDate.setTime(getValidityEndDate());
            contractEndDate.add(Calendar.DATE, 1);

            if (contractStartDate.before(nowCalendar) && contractEndDate.after(nowCalendar)) {
                dateActive = true;
            }
        }

        return dateActive;
    }

    public void setDateActive(Boolean dateActive) {
        this.dateActive = dateActive;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Contract contract = (Contract) o;

        return id == contract.id;

    }

    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public int compareTo(Contract contract) {
        boolean contractActive = false;
        boolean thisContractActive = false;

        if (contract.isActive() && contract.getDateActive()) {
            contractActive = true;
        }
        if (this.isActive() && this.getDateActive()) {
            thisContractActive = true;
        }

        if (contractActive == thisContractActive) {
            int numberComparison = this.getNumber().compareTo(contract.getNumber());
            if (numberComparison != 0) {
                return numberComparison;
            }
            return this.getContractDesignation().compareTo(contract.getContractDesignation());
        } else if (contractActive && !thisContractActive) {
            return 1;
        } else {
            return -1;
        }
    }

    public Collection<TemplateDocx> getTemplatesDocx() {
        return templatesDocx;
    }

    public void setTemplatesDocx(Collection<TemplateDocx> templatesDocx) {
        this.templatesDocx = templatesDocx;
    }

    public String getMenuLabel() {
        return menuLabel;
    }

    public void setMenuLabel(String menuLabel) {
        this.menuLabel = menuLabel;
    }

    public Document getSmLogoPicture() {
        return smLogoPicture;
    }

    public void setSmLogoPicture(Document smLogoPicture) {
        this.smLogoPicture = smLogoPicture;
    }

    public Document getSmCoverPicture() {
        return smCoverPicture;
    }

    public void setSmCoverPicture(Document smCoverPicture) {
        this.smCoverPicture = smCoverPicture;
    }

    public List<SubModule> getSubModules() {
        return subModules;
    }

    public void setSubModules(List<SubModule> subModules) {
        this.subModules = subModules;
    }
}
