/*
 * $Id: FAQCategory.java,v 1.2 2009/03/12 02:50:41 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/12 02:50:41 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Collection;

/**
 * <description>
 *
 * @author jp-gomes
 */
@Entity
public class FAQCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private java.lang.Module module;

    @OneToMany(mappedBy = "faqCategory", fetch = FetchType.LAZY)
    private Collection<FAQ> faqs;

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

    public java.lang.Module getModule() {
        return module;
    }

    public void setModule(java.lang.Module module) {
        this.module = module;
    }

    public Collection<FAQ> getFaqs() {
        return faqs;
    }

    public void setFaqs(Collection<FAQ> faqs) {
        this.faqs = faqs;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FAQCategory that = (FAQCategory) o;

        if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }
}
