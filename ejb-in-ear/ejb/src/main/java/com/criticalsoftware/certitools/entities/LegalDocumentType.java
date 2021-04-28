/*
 * $Id: LegalDocumentType.java,v 1.5 2009/04/06 13:46:29 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/06 13:46:29 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Legal Document Types
 *
 * @author jp-gomes
 */
@Entity
public class LegalDocumentType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    private String name;

    public Long getId() {
        return id;
    }

    public LegalDocumentType() {
    }

    public LegalDocumentType(Long id) {
        this.id = id;
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

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LegalDocumentType that = (LegalDocumentType) o;

        return id.equals(that.id);

    }

    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
