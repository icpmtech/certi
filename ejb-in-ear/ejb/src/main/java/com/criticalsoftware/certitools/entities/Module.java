/*
 * $Id: Module.java,v 1.8 2010/05/26 16:55:29 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/05/26 16:55:29 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities;

import com.criticalsoftware.certitools.util.ModuleType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Module Entity
 *
 * @author : pjfsilva
 */
@Entity
public class Module implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private ModuleType moduleType;

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    private Collection<FAQCategory> faqCategories;

    @OneToMany(mappedBy = "module")
    private Collection<Contract> contracts;

    @Transient
    private String name;

    public Module() {
    }

    public Module(ModuleType moduleType, String name) {
        this.moduleType = moduleType;
        this.name = name;
    }

    public Module(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    public Collection<FAQCategory> getFaqCategories() {
        return faqCategories;
    }

    public void setFaqCategories(Collection<FAQCategory> faqCategories) {
        this.faqCategories = faqCategories;
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

        Module module = (Module) o;

        if (moduleType != module.moduleType) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return moduleType.hashCode();
    }

    public Collection<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(Collection<Contract> contracts) {
        this.contracts = contracts;
    }

}
