/*
 * $Id: DownloadFileResolution.java,v 1.2 2009/05/14 17:11:07 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/05/14 17:11:07 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.presentation.util;

import net.sourceforge.stripes.action.StreamingResolution;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class DownloadFileResolution extends StreamingResolution {

    private ByteArrayOutputStream outputStream;

    public DownloadFileResolution(String contentType, ByteArrayOutputStream outputStream) {
        super(contentType);
        this.outputStream = outputStream;
    }

    protected void stream(HttpServletResponse response) throws Exception {
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(outputStream.size());
        response.getOutputStream().write(outputStream.toByteArray());
        response.getOutputStream().flush();
        outputStream.close();
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(ByteArrayOutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
