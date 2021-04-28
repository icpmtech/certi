/*
 * $Id: Role.java,v 1.5 2009/03/27 15:16:59 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/03/27 15:16:59 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * <description>
 *
 * @author haraujo
 */
@Entity
public class Role implements Serializable {

    @Id
    @Column(length = 100)
    private String role;

    private String description;

    @Column(nullable = false)
    private boolean isCertitecna = false;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    @Transient
    private boolean associatedWithUser = false;

    public Role() {
    }

    public Role(String role) {
        this.role = role;
    }

    public Role(String role, String description, boolean associatedWithUser) {
        this.role = role;
        this.description = description;
        this.associatedWithUser = associatedWithUser;
    }

    public Role(String role, boolean associatedWithUser) {
        this.role = role;
        this.associatedWithUser = associatedWithUser;
    }

    public Role(String role, String description) {
        this.role = role;
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role1 = (Role) o;

        return !(role != null ? !role.equals(role1.role) : role1.role != null);
    }

    public int hashCode() {
        int result;
        result = (role != null ? role.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public boolean isCertitecna() {
        return isCertitecna;
    }

    public void setCertitecna(boolean certitecna) {
        isCertitecna = certitecna;
    }

    public boolean isAssociatedWithUser() {
        return associatedWithUser;
    }

    public void setAssociatedWithUser(boolean associatedWithUser) {
        this.associatedWithUser = associatedWithUser;
    }
}
