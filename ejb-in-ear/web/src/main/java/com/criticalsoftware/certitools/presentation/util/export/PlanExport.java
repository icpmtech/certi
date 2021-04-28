/*
 * $Id: PlanExport.java,v 1.1 2010/07/01 19:12:04 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/07/01 19:12:04 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util.export;

import java.io.ByteArrayOutputStream;

/**
 * Plan object to export
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.1 $
 */
public class PlanExport {
    private String filename;
    private String mimetype;
    private ByteArrayOutputStream data;

    public PlanExport(String filename, String mimetype, ByteArrayOutputStream data) {
        this.filename = filename;
        this.mimetype = mimetype;
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public String getMimetype() {
        return mimetype;
    }

    public ByteArrayOutputStream getData() {
        return data;
    }
}