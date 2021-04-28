/*
 * $Id: LegalDocumentState.java,v 1.3 2009/04/06 13:46:29 lt-rico Exp $
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

/**
 * Legal Document State
 *
 * @author jp-gomes
 */
@Entity
public class LegalDocumentState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    private String name;

    public LegalDocumentState() {
    }

    public LegalDocumentState(Long id) {
        this.id = id;
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

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LegalDocumentState that = (LegalDocumentState) o;

        if (!id.equals(that.id)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }
}
