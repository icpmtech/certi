/*
 * $Id: TemplateDocx.java,v 1.3 2010/07/09 11:09:17 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/07/09 11:09:17 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Represents a plan template
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.3 $
 */
@Entity
public class TemplateDocx implements Serializable, Comparable<TemplateDocx> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    private Collection<Contract> contracts;

    @Column(nullable = true, length = 2048)
    private String observations;

    @Column(nullable = false, length = 255)
    private String title;

    @ManyToOne(optional = false)
    private java.lang.Module module;

    @Column(nullable = false)
    private String fileName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(Collection<Contract> contracts) {
        this.contracts = contracts;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public java.lang.Module getModule() {
        return module;
    }

    public void setModule(java.lang.Module module) {
        this.module = module;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemplateDocx that = (TemplateDocx) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public int compareTo(TemplateDocx o) {
        return title.compareTo(o.getTitle());
    }
}