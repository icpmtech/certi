/*
 * $Id: LicenseActionBean.java,v 1.28 2009/04/14 16:19:40 lt-rico Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/14 16:19:40 $
 * Last changed by : $Author: lt-rico $
 */
package com.criticalsoftware.certitools.presentation.action.license;

import com.criticalsoftware.certitools.business.certitools.CompanyService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.license.LicenseService;
import com.criticalsoftware.certitools.entities.License;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.PTDateTypeConverter;
import com.criticalsoftware.certitools.presentation.util.PaginatedListAdapter;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.*;

import java.util.Collection;
import java.util.Locale;

/**
 * License Management Action Bean
 *
 * @author jp-gomes
 */

public class LicenseActionBean extends DisplayTagSupportActionBean implements ValidationErrorHandler {

    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    @EJBBean(value = "certitools/LicenseService")
    private LicenseService licenseService;

    private PaginatedListAdapter<License> licenses;
    private Collection companyList;

    @ValidateNestedProperties({
        @Validate(field = "startDate", required = true, converter = PTDateTypeConverter.class),
        @Validate(field = "endDate", required = true, converter = PTDateTypeConverter.class),
        @Validate(field = "company.id", required = true)})
    private License license;

    @DefaultHandler
    @DontValidate
    @Secure(roles = "administrator")
    public Resolution viewLicenses() throws BusinessException {

        PaginatedListWrapper<License> wrapper =
                new PaginatedListWrapper<License>(getPage(),
                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(), getDirOrder(),
                        isExportRequest());

        licenses = new PaginatedListAdapter<License>(licenseService.findAll(wrapper));

        Locale locale = getContext().getLocale();

        setExportXLS(LocalizationUtility
                .getLocalizedFieldName("table.license.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility
                .getLocalizedFieldName("table.license.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility
                .getLocalizedFieldName("table.license.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility
                .getLocalizedFieldName("table.license.filename.pdf", null, null, locale));

        return new ForwardResolution("/WEB-INF/jsps/license/licenses.jsp");
    }

    @Secure(roles = "administrator")
    @DontValidate
    public Resolution insertLicenseForm() throws BusinessException {
        companyList = companyService.findAll();
        return new ForwardResolution("/WEB-INF/jsps/license/licenseInsert.jsp");
    }

    @Secure(roles = "administrator")
    public Resolution insertLicense() throws BusinessException {
        licenseService.insert(license);
        getContext().getMessages().add(new LocalizableMessage("license.add.sucess"));
        return new RedirectResolution(LicenseActionBean.class).flash(this);
    }

    @Secure(roles = "administrator")
    @DontValidate
    public Resolution cancel() throws BusinessException {
        return new RedirectResolution(LicenseActionBean.class);
    }

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertLicense")) {
            return insertLicenseForm();
        }
        return null;
    }

    @ValidationMethod(on = "insertLicense")
    public void validateInsertLicense(ValidationErrors errors) throws Exception {

        if (license.getStartDate().after(license.getEndDate())) {
            errors.add("license.startDate", new LocalizableError("license.startDate.afterEndDate"));
        }
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public LicenseService getLicenseService() {
        return licenseService;
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public PaginatedListAdapter<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(PaginatedListAdapter<License> licenses) {
        this.licenses = licenses;
    }

    public Collection getCompanyList() {
        return companyList;
    }

    public void setCompanyList(Collection companyList) {
        this.companyList = companyList;
    }
}
