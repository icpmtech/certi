/*
 * $Id: Configuration.java,v 1.35 2013/12/18 03:11:49 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2013/12/18 03:11:49 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.util;

import com.criticalsoftware.certitools.business.certitools.ConfigurationService;

import javax.naming.InitialContext;
import java.util.Map;
import java.util.TreeMap;

/**
 * Base Configuration Class that loads configurations from database. This load its only made one time, by default
 *
 * @version : $version $
 */
public class Configuration {

    private static final Logger log = Logger.getInstance(Configuration.class);
    private static final Map<String, String> configurationParams = new TreeMap<String, String>();
    private static Configuration configuration;

    protected Configuration() {
        log.info("Loading " + Configuration.class.getSimpleName());
        try {
            InitialContext initialContext = new InitialContext();
            ConfigurationService configurationService =
                    (ConfigurationService) initialContext.lookup("certitools/ConfigurationService");

            configurationParams.putAll(configurationService.findAllInMap());
        } catch (Exception e) {
            log.error("Error loading configuration", e);
        }
    }

    public static Configuration getInstance() {
        if (configuration == null) {
            log.info("Started new Instance of " + Configuration.class.getSimpleName());
            configuration = new Configuration();
        }
        return configuration;
    }

    public static void reloadInstance() {
        configuration = new Configuration();
    }

    public static Map<String, String> getConfigurationParams() {
        return configurationParams;
    }

    public String getActivationEmailMessageFooter() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.ACTIVATION_EMAIL_FOOTER.getKey());
    }

    public String getActivationEmailMessageHeader() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.ACTIVATION_EMAIL_HEADER.getKey());
    }

    public String getActivationEmailSubject() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.ACTIVATION_EMAIL_SUBJECT.getKey());
    }

    public Long getAdminId() {
        return Long.valueOf(Configuration.getConfigurationParams().get(ConfigurationProperties.ADMIN_ID.getKey()));
    }

    public String getUserRole() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.USER_ROLE.getKey());
    }

    public String getCertitecnaId() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.CERTITECNA_ID.getKey());
    }

    public String getDefaultLanguage() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.DEFAULT_LANGUAGE.getKey());
    }

    public String getNewsletterDatePattern() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.NEWSLETTER_DATE_PATTERN.getKey());
    }

    public String getNewsletterYearPattern() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.NEWSLETTER_YEAR_PATTERN.getKey());
    }

    public String getDatePattern() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.DATE_PATTERN.getKey());
    }

    public String getDatePatternCalendar() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.DATE_PATTERN_CALENDAR.getKey());
    }

    public String getDateHourPattern() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.DATE_HOUR_PATTERN.getKey());
    }

    public String getDatePatternNews() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.DATE_PATTERN_NEWS.getKey());
    }

    public String getPageListSize() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.PAGE_LIST_SIZE.getKey());
    }

    public String getLicenseFileName() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.LICENSE_FILE_NAME.getKey());
    }

    public String getLicenseFileDirectory() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.LICENSE_FILE_DIRECTORY.getKey());
    }

    public String getEmailInfo() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.EMAIL_INFO.getKey());
    }

    public String getNewsletterSubject() {
        return Configuration.getConfigurationParams()
                .get(ConfigurationProperties.LEGAL_DOCUMENT_NEWSLETTER_SUBJECT.getKey());
    }

    public String getNewsletterHeader() {
        return Configuration.getConfigurationParams()
                .get(ConfigurationProperties.LEGAL_DOCUMENT_NEWSLETTER_HEADER.getKey());
    }

    public String getNewsletterFooter() {
        return Configuration.getConfigurationParams()
                .get(ConfigurationProperties.LEGAL_DOCUMENT_NEWSLETTER_FOOTER.getKey());
    }

    public int getLegalDocumentSummaryTruncate() {
        return new Integer(Configuration.getConfigurationParams().get(
                ConfigurationProperties.LEGAL_DOCUMENT_SUMMARY_TRUNCATE.getKey())).intValue();
    }

    public String getSessionTimeout() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.SESSION_TIMEOUT.getKey());
    }

    public int getNewsletterTimerHourToRun() {
        return new Integer(Configuration.getConfigurationParams().get(
                ConfigurationProperties.NEWSLETTER_TIMER_TIME_TO_RUN_HOUR.getKey())).intValue();
    }

    public String getApplicationDomain() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.APPLICATION_DOMAIN.getKey());
    }

    public int getStatisticsFilterMaxDays() {
        return new Integer(
                Configuration.getConfigurationParams().get(ConfigurationProperties.STATISTICS_FILTER_MAX_DAYS.getKey()))
                .intValue();
    }

    public String getSearchExample1() {
        return Configuration.getConfigurationParams()
                .get(ConfigurationProperties.SEARCH_EXAMPLE_1.getKey());
    }

    public String getSearchExample2() {
        return Configuration.getConfigurationParams()
                .get(ConfigurationProperties.SEARCH_EXAMPLE_2.getKey());
    }

    public Long getPEITemplateMaxFileSize() {
        return new Long(Configuration.getConfigurationParams().get(
                ConfigurationProperties.PEI_TEMPLATE_MAX_FILE_SIZE.getKey()));
    }

    public Long getPEITemplateMaxClickableAreas() {
        return new Long(Configuration.getConfigurationParams().get(
                ConfigurationProperties.PEI_TEMPLATE_MAX_CLICKABLE_AREAS.getKey()));
    }

    public String getUsersDefaultView() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.USERS_DEFAULT_VIEW.getKey());
    }

    public String getCompaniesDefaultView() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.COMPANIES_DEFAULT_VIEW.getKey());
    }

    public Long getPEITemplateMaxFileSizeInMB() {
        return (getPEITemplateMaxFileSize() / (1024L * 1024L));
    }

    public String getPEIPermissionPEIManager() {
        return ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey();
    }

    public String getMasterPassword() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.MASTERPASSWORD.getKey());
    }

    public Long getMasterPasswordExpiryDate() {
        return new Long(
                Configuration.getConfigurationParams().get(ConfigurationProperties.MASTERPASSWORD_EXPIRYDATE.getKey()));
    }

    public String getMasterPasswordAuthor() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.MASTERPASSWORD_AUTHOR.getKey());
    }

    public String getAsposeLicenseFile() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.ASPOSE_LICENSE_FILE.getKey());
    }

    public Long getDocxExportImageMaxWidth() {
        return Long.valueOf(
                Configuration.getConfigurationParams().get(
                        ConfigurationProperties.DOCX_EXPORT_MAX_IMAGE_WIDTH.getKey()));
    }

    public boolean getDocxExportRemoveStyleAttribute() {
        Long value = Long.valueOf(Configuration.getConfigurationParams().get(
                ConfigurationProperties.DOCX_EXPORT_REMOVE_STYLES_ATTR.getKey()));
        return value == 1;
    }

    public boolean getDocxExportCenterTables() {
        Long value = Long.valueOf(Configuration.getConfigurationParams().get(
                ConfigurationProperties.DOCX_EXPORT_CENTER_TABLES.getKey()));
        return value == 1;
    }

    public boolean getDocxExportRepeatHeaderTables() {
        Long value = Long.valueOf(Configuration.getConfigurationParams().get(
                ConfigurationProperties.DOCX_EXPORT_REPEAT_HEADER_TABLES.getKey()));
        return value == 1;
    }

    public boolean getDocxExportEnabled() {
        Long value = Long.valueOf(Configuration.getConfigurationParams().get(
                ConfigurationProperties.DOCX_EXPORT_ENABLED.getKey()));
        return value == 1;
    }

    public boolean getDocxExportApplyAligmnentParagraphsFix() {
        Long value = Long.valueOf(Configuration.getConfigurationParams().get(
                ConfigurationProperties.DOCX_EXPORT_APPLY_ALIGMNENTALLPARAGRAPHS_FIX.getKey()));
        return value == 1;
    }

    public String getHomeCustomersLink() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.HOME_CUSTOMERS_LINK.getKey());
    }

    public Long getHomeCustomersWidth() {
        return new Long(Configuration.getConfigurationParams().get(ConfigurationProperties.HOME_CUSTOMERS_WIDTH.getKey()));
    }

    public Long getHomeCustomersHeight() {
        return new Long(Configuration.getConfigurationParams().get(ConfigurationProperties.HOME_CUSTOMERS_HEIGHT.getKey()));
    }

    public boolean getLocalInstallation() {
        // if true it's BPI installation with SSO / SPENEGO
        Long value = Long.valueOf(Configuration.getConfigurationParams().get(
                ConfigurationProperties.LOCAL_INSTALLATION.getKey()));
        return value == 1;
    }

    public Long getBaseCompanyId() {
        return new Long(Configuration.getConfigurationParams().get(ConfigurationProperties.BASE_COMPANY.getKey()));
    }

    public String getSecurityUpcomingDateHourPattern() {
        return Configuration.getConfigurationParams().get(ConfigurationProperties.SECURITY_UPCOMING_EVENT_DATE_HOUR_PATTERN.getKey());
    }

    public Integer getRecurrenceTimerHourToRun() {
        return new Integer(Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_RECURRENCE_TIMER_TIME_TO_RUN_HOUR.getKey()));
    }

    public String getSmRecurrenceNotificationEmailSubject() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_RECURRENCE_NOTIFICATION_EMAIL_SUBJECT.getKey());
    }

    public String getSmRecurrenceNotificationEmailBody() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_RECURRENCE_NOTIFICATION_EMAIL_BODY.getKey());
    }

    public String getSmEventCreatedEmailSubject() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_EVENT_CREATED_EMAIL_SUBJECT.getKey());
    }

    public String getSmEventCreatedEmailBody() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_EVENT_CREATED_EMAIL_BODY.getKey());
    }

    public String getSmEventEditEmailSubject() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_EVENT_EDIT_EMAIL_SUBJECT.getKey());
    }

    public String getSmEventEditEmailBody() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_EVENT_EDIT_EMAIL_BODY.getKey());
    }

    public String getSmEventClosedEmailSubject() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_EVENT_CLOSED_EMAIL_SUBJECT.getKey());
    }

    public String getSmEventClosedEmailBody() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_EVENT_CLOSED_EMAIL_BODY.getKey());
    }

    public String getSmChatNotificationEmailSubject() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_CHAT_NOTIFICATION_EMAIL_SUBJECT.getKey());
    }

    public String getSmChatNotificationEmailBody() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_CHAT_NOTIFICATION_EMAIL_BODY.getKey());
    }

    public String getSmActivityCodeSequencePattern() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_ACTIVITY_CODE_SEQUENCE_PATTERN.getKey());
    }

    public String getSmAnomalyCodeSequencePattern() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_ANOMALY_CODE_SEQUENCE_PATTERN.getKey());
    }

    public String getSmOccurrenceCodeSequencePattern() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_OCCURRENCE_CODE_SEQUENCE_PATTERN.getKey());
    }

    public String getSmModificationCodeSequencePattern() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_MODIFICATION_CODE_SEQUENCE_PATTERN.getKey());
    }

    public String getSmWorkAuthorizationCodeSequencePattern() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_WORK_AUTHORIZATION_CODE_SEQUENCE_PATTERN.getKey());
    }

    public String getSmCorrectiveActionCodeSequencePattern() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_CORRECTIVE_ACTION_CODE_SEQUENCE_PATTERN.getKey());
    }

    public String getSmMaintenanceCodeSequencePattern() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_MAINTENANCE_CODE_SEQUENCE_PATTERN.getKey());
    }

    public String getSmEmergencyActionCodeSequencePattern() {
        return Configuration.getConfigurationParams().get(
                ConfigurationProperties.SM_EMERGENCY_ACTION_CODE_SEQUENCE_PATTERN.getKey());
    }
}
