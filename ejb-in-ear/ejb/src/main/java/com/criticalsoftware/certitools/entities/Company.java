/*
 * $Id: Company.java,v 1.14 2012/06/01 13:51:50 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/06/01 13:51:50 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Company Entity
 *
 * @author : pjfsilva
 */
@Entity
public class Company implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    private String name;

    private String address;

    @Column(length = 32)
    private String phone;

    private Long fiscalNumber;

    @Column(nullable = false)
    private boolean active = true;

    @Column(length = 5)
    private String language;

    //CERTOOL-539
    //TODO-MODULE
    @Column(nullable = true, length = 64)
    private String peiLabelPT;

    @Column(nullable = true, length = 64)
    private String prvLabelPT;

    @Column(nullable = true, length = 64)
    private String psiLabelPT;

    @Column(nullable = true, length = 64)
    private String gscLabelPT;

    @Column(nullable = true, length = 64)
    private String peiLabelEN;

    @Column(nullable = true, length = 64)
    private String prvLabelEN;

    @Column(nullable = true, length = 64)
    private String psiLabelEN;

    @Column(nullable = true, length = 64)
    private String gscLabelEN;

    @Basic
    private boolean showFullListPEI = true;

    @ManyToOne
    private Country country;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private Collection<Contract> contracts;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private Collection<User> users;

    // entity is deleted, if true entity was deleted by the user
    @Column(nullable = false)
    private boolean deleted = false;

    public Company() {
    }

    public Company(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Company(Long id, String name, Country country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public Company(Long id, String name, String address, String phone, Long nif, boolean active,
                   String language) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.fiscalNumber = nif;
        this.active = active;
        this.language = language;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getFiscalNumber() {
        return fiscalNumber;
    }

    public void setFiscalNumber(Long fiscalNumber) {
        this.fiscalNumber = fiscalNumber;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPeiLabelPT() {
        return peiLabelPT;
    }

    public void setPeiLabelPT(String peiLabelPT) {
        this.peiLabelPT = peiLabelPT;
    }

    public String getPrvLabelPT() {
        return prvLabelPT;
    }

    public void setPrvLabelPT(String prvLabelPT) {
        this.prvLabelPT = prvLabelPT;
    }

    public String getPsiLabelPT() {
        return psiLabelPT;
    }

    public void setPsiLabelPT(String psiLabelPT) {
        this.psiLabelPT = psiLabelPT;
    }

    public String getGscLabelPT() {
        return gscLabelPT;
    }

    public void setGscLabelPT(String gscLabelPT) {
        this.gscLabelPT = gscLabelPT;
    }

    public String getPeiLabelEN() {
        return peiLabelEN;
    }

    public void setPeiLabelEN(String peiLabelEN) {
        this.peiLabelEN = peiLabelEN;
    }

    public String getPrvLabelEN() {
        return prvLabelEN;
    }

    public void setPrvLabelEN(String prvLabelEN) {
        this.prvLabelEN = prvLabelEN;
    }

    public String getPsiLabelEN() {
        return psiLabelEN;
    }

    public void setPsiLabelEN(String psiLabelEN) {
        this.psiLabelEN = psiLabelEN;
    }

    public String getGscLabelEN() {
        return gscLabelEN;
    }

    public void setGscLabelEN(String gscLabelEN) {
        this.gscLabelEN = gscLabelEN;
    }

    public Collection<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(Collection<Contract> contracts) {
        this.contracts = contracts;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Company company = (Company) o;

        if (id != company.id) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }


    public boolean isShowFullListPEI() {
        return showFullListPEI;
    }

    public void setShowFullListPEI(boolean showFullListPEI) {
        this.showFullListPEI = showFullListPEI;
    }
}
