/*
 * $Id: User.java,v 1.31 2012/05/28 16:50:38 d-marques Exp $
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
package com.criticalsoftware.certitools.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * User Entity
 *
 * @author : jp-gomes
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Basic(optional = false)
    private String name;

    @Column(length = 32)
    private String phone;

    @Column(nullable = false)
    private Long fiscalNumber;

    @ManyToOne(optional = false)
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<Role> roles;

    @Column
    private String externalUser;

    @Column(nullable = false)
    private boolean uniqueSession = false;

    // application level activation
    @Column(nullable = false)
    private boolean active = true;

    // entity is deleted, if true entity was deleted by the user
    @Column(nullable = false)
    private boolean deleted = false;

    // if password is null, password for this user was reseted or the user is new,
    // and activation key value should be NOT NULL
    @Column(length = 128)
    private String password;

    @Column(length = 36)
    private String activationKey;

    @Temporal(value = TemporalType.DATE)
    private Date lastLoginDate;

    @Column(columnDefinition = "bigint default '0'")
    private long numberLogins;

    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastPlanOrLegislationView;

    @Column(nullable = false, length = 5)
    private String language = "PT";

    @Column(nullable = false)
    private boolean activatePassNotificationSend = false;

    @Column(unique = true)
    private String emailcontact;

    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<LegalDocumentCategory> subscriptionsLegalDocuments;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserContract> userContract;

    // user is logged in or not (session active or not)
    private int sessionsActive = 0;

    @Basic
    private boolean seenPEI = false;

    @Transient
    private Boolean inActiveContracts = null;

    @Transient
    private boolean accessLegislation = false;

    @Transient
    private boolean accessPEI = false;

    //TODO-MODULE
    @Transient
    private boolean accessPRV = false;

    @Transient
    private boolean accessPSI = false;

    @Transient
    private boolean accessGSC = false;

    @Transient
    private Boolean certitecnaSpecialRoles;

    @Transient
    private boolean showPEIHelp = false;

    public User() {

    }

    public User(String email, String name, String emailcontact) {
        this.email = email;
        this.name = name;
        this.emailcontact = emailcontact;
    }

    public User(long id, String email, String name, boolean active ,String emailcontact) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.active = active;
        this.emailcontact = emailcontact;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailContact() {
        return emailcontact;
    }

    public void setEmailContact(String emailcontact) {
        this.emailcontact = emailcontact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFiscalNumber() {
        return fiscalNumber;
    }

    public void setFiscalNumber(Long fiscalNumber) {
        this.fiscalNumber = fiscalNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public String getExternalUser() {
        return externalUser;
    }

    public void setExternalUser(String externalUser) {
        this.externalUser = externalUser;
    }

    public boolean isUniqueSession() {
        return uniqueSession;
    }

    public void setUniqueSession(boolean uniqueSession) {
        this.uniqueSession = uniqueSession;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public long getNumberLogins() {
        return numberLogins;
    }

    public void setNumberLogins(long numberLogins) {
        this.numberLogins = numberLogins;
    }

    public Collection<LegalDocumentCategory> getSubscriptionsLegalDocuments() {
        return subscriptionsLegalDocuments;
    }

    public void setSubscriptionsLegalDocuments(Collection<LegalDocumentCategory> subscriptionsLegalDocuments) {
        this.subscriptionsLegalDocuments = subscriptionsLegalDocuments;
    }

    public Set<UserContract> getUserContract() {
        return userContract;
    }

    public void setUserContract(Set<UserContract> userContract) {
        this.userContract = userContract;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (email != null ? !email.equals(user.email) : user.email != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (email != null ? email.hashCode() : 0);
    }

    @SuppressWarnings({"RedundantIfStatement"})
    public Boolean getCertitecnaSpecialRoles() {
        if (certitecnaSpecialRoles == null) {
            if (roles.contains(new Role("administrator")) || roles.contains(new Role("contractmanager")) || roles
                    .contains(new Role("legislationmanager"))) {
                certitecnaSpecialRoles = true;
            } else {
                certitecnaSpecialRoles = false;
            }
        }

        return certitecnaSpecialRoles;
    }

    public boolean isInActiveContracts() {
        if (inActiveContracts == null) {
            inActiveContracts = false;

            // Rules to check if user is in activeContracts:
            // User is in a Contract that:
            // - is active
            // - Contract validity is OK
            // - if UserContract validity is set, it is OK

            // check if user has contracts
            if (userContract == null || userContract.size() <= 0) {
                inActiveContracts = false;
                return inActiveContracts;
            }

            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTime(new Date());

            Calendar userContractStartDate = Calendar.getInstance();
            Calendar userContractEndDate = Calendar.getInstance();
            Calendar contractStartDate = Calendar.getInstance();
            Calendar contractEndDate = Calendar.getInstance();

            // for all the contracts check if userContract validity is ok, and if contract validity is ok
            for (UserContract userContract : this.userContract) {
                // userContract must be active
                if (userContract.getContract().isActive()) {
                    contractStartDate.setTime(userContract.getContract().getValidityStartDate());
                    contractEndDate.setTime(userContract.getContract().getValidityEndDate());
                    contractEndDate.add(Calendar.DATE, 1);

                    // userContract validity is set, check this first
                    if (userContract.getValidityStartDate() != null) {
                        userContractStartDate.setTime(userContract.getValidityStartDate());
                        userContractEndDate.setTime(userContract.getValidityEndDate());
                        userContractEndDate.add(Calendar.DATE, 1);


                        if (userContractStartDate.before(nowCalendar) && userContractEndDate.after(nowCalendar) &&
                                contractStartDate.before(nowCalendar) && contractEndDate.after(nowCalendar)) {
                            inActiveContracts = true;
                            return inActiveContracts;
                        }
                    } else {

                        // only userContract validity is set
                        if (contractStartDate.before(nowCalendar) && contractEndDate.after(nowCalendar)) {
                            inActiveContracts = true;
                            return inActiveContracts;
                        }
                    }
                }
            }
        }

        return inActiveContracts;
    }

    /**
     * Checks if a user can access the module or not. This requires that user has usercontracts loaded
     *
     * @param moduleType module to check
     * @return true if access is allowed
     */
    public boolean validateModuleAccess(ModuleType moduleType) {
        // check if user is inactive or deleted
        if (!this.isActive() || this.isDeleted()) {
            return false;
        }

        java.lang.Module module = new java.lang.Module(moduleType);

        boolean canAccess = false;

        // Rules to check if user is in activeContracts:
        // User is in a Contract that:
        // - is active
        // - Contract validity is OK
        // - if UserContract validity is set, it is OK

        // check if user has contracts
        if (this.getUserContract() == null || this.getUserContract().size() <= 0) {
            canAccess = false;
            return canAccess;
        }

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());

        Calendar userContractStartDate = Calendar.getInstance();
        Calendar userContractEndDate = Calendar.getInstance();
        Calendar contractStartDate = Calendar.getInstance();
        Calendar contractEndDate = Calendar.getInstance();

        // for all the contracts check if userContract validity is ok, and if contract validity is ok
        for (UserContract userContract : this.getUserContract()) {
            if (userContract.getContract().getModule().equals(module)) {

                // userContract must be active
                if (userContract.getContract().isActive()) {
                    contractStartDate.setTime(userContract.getContract().getValidityStartDate());
                    contractEndDate.setTime(userContract.getContract().getValidityEndDate());
                    contractEndDate.add(Calendar.DATE, 1);

                    // userContract validity is set, check this first
                    if (userContract.getValidityStartDate() != null) {
                        userContractStartDate.setTime(userContract.getValidityStartDate());
                        userContractEndDate.setTime(userContract.getValidityEndDate());
                        userContractEndDate.add(Calendar.DATE, 1);


                        if (userContractStartDate.before(nowCalendar) && userContractEndDate.after(nowCalendar) &&
                                contractStartDate.before(nowCalendar) && contractEndDate.after(nowCalendar)) {
                            canAccess = true;
                            return canAccess;
                        }
                    } else {

                        // only userContract validity is set
                        if (contractStartDate.before(nowCalendar) && contractEndDate.after(nowCalendar)) {
                            canAccess = true;
                            return canAccess;
                        }
                    }
                }
            }
        }

        return canAccess;
    }

    public boolean isContractAccessValid(UserContract userContract) {
        boolean canAccess = false;

        // Rules to check if user is in activeContracts:
        // User is in a Contract that:
        // - is active
        // - Contract validity is OK
        // - if UserContract validity is set, it is OK

        // check if user has contracts
        if (this.getUserContract() == null || this.getUserContract().size() <= 0) {
            canAccess = false;
            return canAccess;
        }

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());

        Calendar userContractStartDate = Calendar.getInstance();
        Calendar userContractEndDate = Calendar.getInstance();
        Calendar contractStartDate = Calendar.getInstance();
        Calendar contractEndDate = Calendar.getInstance();

        // for all the contracts check if userContract validity is ok, and if contract validity is ok

        // userContract must be active
        if (userContract.getContract().isActive()) {
            contractStartDate.setTime(userContract.getContract().getValidityStartDate());
            contractEndDate.setTime(userContract.getContract().getValidityEndDate());
            contractEndDate.add(Calendar.DATE, 1);

            // userContract validity is set, check this first
            if (userContract.getValidityStartDate() != null) {
                userContractStartDate.setTime(userContract.getValidityStartDate());
                userContractEndDate.setTime(userContract.getValidityEndDate());
                userContractEndDate.add(Calendar.DATE, 1);


                if (userContractStartDate.before(nowCalendar) && userContractEndDate.after(nowCalendar) &&
                        contractStartDate.before(nowCalendar) && contractEndDate.after(nowCalendar)) {
                    canAccess = true;
                    return canAccess;
                }
            } else {

                // only userContract validity is set
                if (contractStartDate.before(nowCalendar) && contractEndDate.after(nowCalendar)) {
                    canAccess = true;
                    return canAccess;
                }
            }
        }
        return canAccess;
    }

    public void setInActiveContracts(boolean inActiveContracts) {
        this.inActiveContracts = inActiveContracts;
    }

    public int getSessionsActive() {
        return sessionsActive;
    }

    public void setSessionsActive(int sessionsActive) {
        this.sessionsActive = sessionsActive;
    }

    public boolean isAccessLegislation() {
        return accessLegislation;
    }

    public void setAccessLegislation(boolean accessLegislation) {
        this.accessLegislation = accessLegislation;
    }

    public boolean isAccessPEI() {
        return accessPEI;
    }

    public void setAccessPEI(boolean accessPEI) {
        this.accessPEI = accessPEI;
    }

    public boolean isSeenPEI() {
        return seenPEI;
    }

    public void setSeenPEI(boolean seenPEI) {
        this.seenPEI = seenPEI;
    }

    public Boolean getInActiveContracts() {
        return inActiveContracts;
    }

    public void setInActiveContracts(Boolean inActiveContracts) {
        this.inActiveContracts = inActiveContracts;
    }

    public boolean isShowPEIHelp() {
        return showPEIHelp;
    }

    public void setShowPEIHelp(boolean showPEIHelp) {
        this.showPEIHelp = showPEIHelp;
    }

    public boolean isAccessPRV() {
        return accessPRV;
    }

    public void setAccessPRV(boolean accessPRV) {
        this.accessPRV = accessPRV;
    }

    public boolean isAccessPSI() {
        return accessPSI;
    }

    public void setAccessPSI(boolean accessPSI) {
        this.accessPSI = accessPSI;
    }

    public boolean isAccessGSC() {
        return accessGSC;
    }

    public void setAccessGSC(boolean accessGSC) {
        this.accessGSC = accessGSC;
    }

    public Date getLastPlanOrLegislationView() {
        return lastPlanOrLegislationView;
    }

    public void setLastPlanOrLegislationView(Date lastPlanOrLegislationView) {
        this.lastPlanOrLegislationView = lastPlanOrLegislationView;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isActivatePassNotificationSend() {
        return activatePassNotificationSend;
    }

    public void setActivatePassNotificationSend(boolean activatePassNotificationSend) {
        this.activatePassNotificationSend = activatePassNotificationSend;
    }
}