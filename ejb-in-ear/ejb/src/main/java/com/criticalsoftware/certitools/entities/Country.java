/*
 * $Id: Country.java,v 1.1 2009/03/16 18:44:09 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/03/16 18:44:09 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import java.io.Serializable;

/**
 * Country list
 *
 * @author pjfsilva
 */
@Entity
public class Country implements Serializable {

    @Id
    @Column(length = 2)
    private String iso;

    @Column(length = 255, nullable = false)
    private String name;

    public Country() {
    }

    public Country(String iso, String name) {
        this.iso = iso;
        this.name = name;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Country country = (Country) o;

        return !(iso != null ? !iso.equals(country.iso) : country.iso != null);

    }

    public int hashCode() {
        return (iso != null ? iso.hashCode() : 0);
    }
}