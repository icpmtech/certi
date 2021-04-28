/*
 * $Id: LicenseDownloadActionBean.java,v 1.9 2009/03/31 15:38:10 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/03/31 15:38:10 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.action.license;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.license.LicenseService;
import com.criticalsoftware.certitools.entities.License;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.DownloadFileResolution;
import com.criticalsoftware.certitools.util.Configuration;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.Validate;

import java.io.ByteArrayOutputStream;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class LicenseDownloadActionBean extends AbstractActionBean {

    @EJBBean(value = "certitools/LicenseService")
    private LicenseService licenseService;

    @Validate(required = true)
    private Long licenseId;

    @DefaultHandler
    @DontValidate
    @Secure(roles = "administrator")
    public Resolution downloadLicense() throws BusinessException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        License license = licenseService.find(licenseId);

        if (license == null) {
            throw new BusinessException("Cannot find license to Download");
        }

        StringBuilder writeStream = new StringBuilder();
        writeStream.append("license=").append(license.getLicenseKey());

        try {
            baos.write(writeStream.toString().getBytes());
            baos.close();
        } catch (Exception e) {
            System.err.println("Error writing license to file: " + e.getMessage());
        }
        return new DownloadFileResolution("text/plain", baos)
                .setFilename(Configuration.getInstance().getLicenseFileName());
    }


    public Long getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(Long licenseId) {
        this.licenseId = licenseId;
    }

    public LicenseService getLicenseService() {
        return licenseService;
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @Override
    public void fillLookupFields() {
    }
}
