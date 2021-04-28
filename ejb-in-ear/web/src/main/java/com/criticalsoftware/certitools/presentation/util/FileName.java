/*
 * $Id: FileName.java,v 1.2 2009/10/16 10:27:27 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/16 10:27:27 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.util;

import net.sourceforge.stripes.action.FileBean;

/**
 * FileBean with alias web tier representation
 *
 * @author jp-gomes
 */
public class FileName {
    private FileBean file;
    private String alias;

    public FileName() {
    }

    public FileName(FileBean file, String alias) {
        this.file = file;
        this.alias = alias;
    }

    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }

    public int hashCode() {
        int result;
        result = (file != null ? file.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }

    public FileBean getFile() {
        return file;
    }

    public void setFile(FileBean file) {
        this.file = file;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
