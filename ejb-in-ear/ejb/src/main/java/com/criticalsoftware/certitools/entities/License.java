/*
 * $Id: License.java,v 1.1 2009/03/09 18:33:43 haraujo Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/09 18:33:43 $
 * Last changed by : $Author: haraujo $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * <description>
 *
 * @author jp-gomes
 */
@Entity
public class License implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String licenseKey;

    @Temporal(value = TemporalType.DATE)
    @Column(nullable = false)
    private Date creationDate;

    @Temporal(value = TemporalType.DATE)
    @Column(nullable = false)
    private Date startDate;

    @Temporal(value = TemporalType.DATE)
    @Column(nullable = false)
    private Date endDate;

    @OneToOne
    @JoinColumn(name = "companyId")
    private Company company;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        License license = (License) o;

        if (!id.equals(license.id)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }
}
