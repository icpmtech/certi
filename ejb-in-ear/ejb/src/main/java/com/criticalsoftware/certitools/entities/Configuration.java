/*
 * $Id: Configuration.java,v 1.4 2009/03/11 18:51:44 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/11 18:51:44 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Configuration Entity
 *
 * @author : jp-gomes
 */
@Entity
public class Configuration implements Serializable {

    @Id
    private String key;

    @Column(nullable = false, length = 2048)
    private String value;

    @Column(nullable = false, length = 100)
    private String className;

    @Column(nullable = false)
    private boolean editable;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Configuration() {
    }

    public Configuration(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Configuration(String key, String value, String className, boolean editable) {
        this.key = key;
        this.value = value;
        this.className = className;
        this.editable = editable;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Configuration that = (Configuration) o;

        if (key != null ? !key.equals(that.key) : that.key != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (key != null ? key.hashCode() : 0);
    }
}
