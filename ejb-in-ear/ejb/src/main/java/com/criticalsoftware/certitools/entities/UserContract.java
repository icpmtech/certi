/*
 * $Id: UserContract.java,v 1.10 2010/02/04 19:40:41 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/02/04 19:40:41 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities;

import com.criticalsoftware.certitools.util.ModuleType;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * Link table between User and Contract
 *
 * @author pjfsilva
 */
@Entity
@Table(name = "user_contract")
public class UserContract {
    @EmbeddedId
    private UserContractPK userContractPK;

    @Column(nullable = true)
    private Date validityStartDate;

    @Column(nullable = true)
    private Date validityEndDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contract_id", updatable = false, insertable = false)
    private Contract contract;

    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<Permission> permissions;

    // transient needed in the new user form
    @Transient
    private boolean associatedWithUser;

    @Transient
    private boolean validityChanged = false;

    public UserContract() {
    }

    public UserContract(UserContractPK userContractPK, User user, Contract contract, Collection<Permission> permissions) {
        this.userContractPK = userContractPK;
        this.user = user;
        this.contract = contract;
        this.permissions = permissions;
    }

    public UserContract(UserContractPK userContractPK, Date validityStartDate, Date validityEndDate, User user,
                        Contract contract, boolean associatedWithUser, boolean validityChanged) {
        this.userContractPK = userContractPK;
        this.validityStartDate = validityStartDate;
        this.validityEndDate = validityEndDate;
        this.user = user;
        this.contract = contract;
        this.associatedWithUser = associatedWithUser;
        this.validityChanged = validityChanged;
    }

    public UserContractPK getUserContractPK() {
        return userContractPK;
    }

    public void setUserContractPK(UserContractPK userContractPK) {
        this.userContractPK = userContractPK;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserContract that = (UserContract) o;

        if (userContractPK != null ? !userContractPK.equals(that.userContractPK) : that.userContractPK != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (userContractPK != null ? userContractPK.hashCode() : 0);
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public boolean isAssociatedWithUser() {
        return associatedWithUser;
    }

    public void setAssociatedWithUser(boolean associatedWithUser) {
        this.associatedWithUser = associatedWithUser;
    }

    public boolean isValidityChanged() {
        return validityChanged;
    }

    public void setValidityChanged(boolean validityChanged) {
        this.validityChanged = validityChanged;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * Checks if the user contract is valid
     * <p/>
     * Rules to check if this user contract is active User is in a Contract that: 0) Type of contract is of the
     * specified moduletype 1) is active 2) Contract validity is OK 3) if UserContract validity is set, it is OK
     *
     * @param moduleType module type of the contract we want to check. If null doesn't check for this
     * @return true if contract is valid
     */
    public boolean isUserContractValid(ModuleType moduleType) {
        boolean canAccess = false;

        if (moduleType != null) {
            java.lang.Module module = new java.lang.Module(moduleType);
            if (!this.getContract().getModule().equals(module)) {
                return false;
            }
        }

        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());

        Calendar userContractStartDate = Calendar.getInstance();
        Calendar userContractEndDate = Calendar.getInstance();
        Calendar contractStartDate = Calendar.getInstance();
        Calendar contractEndDate = Calendar.getInstance();

        // userContract must be active
        if (this.getContract().isActive()) {
            contractStartDate.setTime(this.getContract().getValidityStartDate());
            contractEndDate.setTime(this.getContract().getValidityEndDate());
            contractEndDate.add(Calendar.DATE, 1);

            // userContract validity is set, check this first
            if (this.getValidityStartDate() != null) {
                userContractStartDate.setTime(this.getValidityStartDate());
                userContractEndDate.setTime(this.getValidityEndDate());
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
}