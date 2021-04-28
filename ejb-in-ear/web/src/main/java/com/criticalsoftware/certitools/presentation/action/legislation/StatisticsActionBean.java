/*
 * $Id: StatisticsActionBean.java,v 1.11 2009/09/04 16:23:28 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/09/04 16:23:28 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.legislation;

import com.criticalsoftware.certitools.business.legislation.LegislationService;
import com.criticalsoftware.certitools.entities.LegalDocumentStatistics;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PTDateTypeConverter;
import com.criticalsoftware.certitools.util.Configuration;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.ValidationState;

import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * Legal Document Statistics action bean
 *
 * @author : lt-rico
 */
public class StatisticsActionBean extends AbstractActionBean {

    @Validate(on = "list", required = true, converter = PTDateTypeConverter.class)
    private Date initDate;
    @Validate(on = "list", required = true, converter = PTDateTypeConverter.class)
    private Date endDate;

    @EJBBean(value = "certitools/LegislationService")
    private LegislationService legislationService;

    private Collection<LegalDocumentStatistics> visualizations;
    private Collection<LegalDocumentStatistics> searchTerms;

    @DefaultHandler
    @DontValidate
    @Secure(roles = "legislationmanager,administrator,contractmanager")
    public Resolution main() {
        setHelpId("#legislation-stats");
        return new ForwardResolution("/WEB-INF/jsps/legislation/statisticsFilter.jsp");
    }

    @Validate
    @Secure(roles = "legislationmanager,administrator,contractmanager")
    public Resolution list() {
        setHelpId("#legislation-stats-result");
        setVisualizations(legislationService.findVisualizationAndDownloadStatistcs(initDate, endDate));
        setSearchTerms(legislationService.findSearchTermStatistcs(initDate, endDate));

        Locale locale = getContext().getLocale();

        setAttribute("exportXLSLegalDocument", LocalizationUtility
                .getLocalizedFieldName("table.statistics.filename.xls", null, null, locale));
        setAttribute("exportCSVLegalDocument", LocalizationUtility
                .getLocalizedFieldName("table.statistics.filename.csv", null, null, locale));
        setAttribute("exportXMLLegalDocument", LocalizationUtility
                .getLocalizedFieldName("table.statistics.filename.xml", null, null, locale));
        setAttribute("exportPDFLegalDocument", LocalizationUtility
                .getLocalizedFieldName("table.statistics.filename.pdf", null, null, locale));

        setAttribute("exportXLSSearch", LocalizationUtility
                .getLocalizedFieldName("table.statistics.search.filename.xls", null, null, locale));
        setAttribute("exportCSVSearch", LocalizationUtility
                .getLocalizedFieldName("table.statistics.search.filename.csv", null, null, locale));
        setAttribute("exportXMLSearch", LocalizationUtility
                .getLocalizedFieldName("table.statistics.search.filename.xml", null, null, locale));
        setAttribute("exportPDFSearch", LocalizationUtility
                .getLocalizedFieldName("table.statistics.search.filename.pdf", null, null, locale));

        return new ForwardResolution("/WEB-INF/jsps/legislation/statisticsList.jsp");
    }

    @ValidationMethod(on = "list", when = ValidationState.NO_ERRORS)
    public void validate() {
        if (endDate.before(initDate)) {
            getContext().getValidationErrors()
                    .add("initDate", new LocalizableError("error.statistics.invalid.init.date"));
        }

        if ((endDate.getTime() - initDate.getTime()) >
                Configuration.getInstance().getStatisticsFilterMaxDays() * 86400000L) {
            getContext().getValidationErrors().add("endDate", new LocalizableError("error.statistics.invalid.end.date",
                    Configuration.getInstance().getStatisticsFilterMaxDays()));
        }
    }


    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(MenuItem.Item.MENU_LEGISLATION, MenuItem.Item.SUB_MENU_LEGISLATION_STATISTICS);
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public LegislationService getLegislationService() {
        return legislationService;
    }

    public void setLegislationService(LegislationService legislationService) {
        this.legislationService = legislationService;
    }

    public Collection<LegalDocumentStatistics> getVisualizations() {
        return visualizations;
    }

    public void setVisualizations(Collection<LegalDocumentStatistics> visualizations) {
        this.visualizations = visualizations;
    }

    public Collection<LegalDocumentStatistics> getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(Collection<LegalDocumentStatistics> searchTerms) {
        this.searchTerms = searchTerms;
    }
}
