/*
 * $Id: Permission.java,v 1.6 2010/02/04 13:16:38 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/02/04 13:16:38 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.*;
import java.util.Collection;

/**
 * Permission
 *
 * @author pjfsilva
 */
@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    private String name;

    @Column(nullable = false)
    private boolean userRegisterBasePermission = false;

    @ManyToOne(optional = false)
    private Contract contract;

    @ManyToMany(mappedBy = "permissions")
    private Collection<UserContract> userContracts;

    public Permission() {
    }

    public Permission(long id) {
        this.id = id;
    }

    public Permission(String name) {
        this.name = name;
    }

    public Permission(String name, Contract contract) {
        this.name = name;
        this.contract = contract;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<UserContract> getUserContracts() {
        return userContracts;
    }

    public void setUserContracts(
            Collection<UserContract> userContracts) {
        this.userContracts = userContracts;
    }

    public boolean isUserRegisterBasePermission() {
        return userRegisterBasePermission;
    }

    public void setUserRegisterBasePermission(boolean userRegisterBasePermission) {
        this.userRegisterBasePermission = userRegisterBasePermission;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Permission that = (Permission) o;

        return id == that.id;
    }

    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}