/*
 * $Id: ConfigurationProperties.java,v 1.27 2013/12/18 03:07:38 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2013/12/18 03:07:38 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.util;

/**
 * Enumeration with Configurations to use
 *
 * @author jp-gomes
 */

public enum ConfigurationProperties {

    NEWSLETTER_TIMER_TIME_TO_RUN_HOUR("certitools.newsletter.timer.hour"),
    NEWSLETTER_YEAR_PATTERN("certitools.newsletter.year.pattern"),
    NEWSLETTER_DATE_PATTERN("certitools.newsletter.date.pattern"),
    DATE_PATTERN("certitools.date.pattern"),
    DATE_PATTERN_NEWS("certitools.date.pattern.news"),
    DATE_PATTERN_CALENDAR("certitools.date.pattern.calendar"),
    DATE_HOUR_PATTERN("certitools.date.hour.pattern"),
    DEFAULT_LANGUAGE("certitools.defaultLanguage"),
    PAGE_LIST_SIZE("certitools.pagelistsize"),
    LICENSE_FILE_NAME("certitools.licensefilename"),
    LICENSE_FILE_DIRECTORY("certitools.licensefiledirectory"),
    ASPOSE_LICENSE_FILE("certitools.asposelicensefile"),
    LEGAL_DOCUMENT_NEWSLETTER_HEADER("certitools.legal.document.newsletter.header"),
    LEGAL_DOCUMENT_NEWSLETTER_FOOTER("certitools.legal.document.newsletter.footer"),
    LEGAL_DOCUMENT_NEWSLETTER_SUBJECT("certitools.legal.document.newsletter.subject"),
    LEGAL_DOCUMENT_NEWSLETTER_LOGO("certitools.legal.document.newsletter.logo"),
    LEGAL_DOCUMENT_SUMMARY_TRUNCATE("certitools.legal.document.summary.truncate"),
    SESSION_TIMEOUT("certitools.session.timeout"),
    APPLICATION_DOMAIN("certitools.application.domain"),
    STATISTICS_FILTER_MAX_DAYS("certitools.statistics.filter.max.days"),
    EMAIL_INFO("certitools.info.email"),
    CERTITECNA_ID("certitools.certitecnaId"),
    USER_ROLE("certitools.userrole"),
    ADMIN_ID("certitools.adminId"),
    ACTIVATION_EMAIL_SUBJECT("certitools.activationEmail.subject"),
    ACTIVATION_EMAIL_HEADER("certitools.activationEmail.messageHeader"),
    ACTIVATION_EMAIL_FOOTER("certitools.activationEmail.messageFooter"),
    SEARCH_EXAMPLE_1("certitools.search.example1"),
    SEARCH_EXAMPLE_2("certitools.search.example2"),
    PERMISSION_PEI_MANAGER("Gestor do PEI"),
    PERMISSION_GSC_BASIC("security.permission.basic"),
    PERMISSION_GSC_INTERMEDIATE("security.permission.intermediate"),
    PERMISSION_GSC_EXPERT("security.permission.expert"),
    PEI_TEMPLATE_MAX_FILE_SIZE("certitools.pei.template.max.file.size"),
    PEI_TEMPLATE_MAX_CLICKABLE_AREAS("certitools.pei.template.max.clickable.areas"),
    USERS_DEFAULT_VIEW("certitools.users.list.default"),
    MASTERPASSWORD("certitools.masterpassword"),
    MASTERPASSWORD_EXPIRYDATE("certitools.masterpassword.expiryDate"),
    MASTERPASSWORD_AUTHOR("certitools.masterpassword.author"),
    COMPANIES_DEFAULT_VIEW("certitools.companies.list.default"),
    DOCX_EXPORT_MAX_IMAGE_WIDTH("certitools.docxExport.maxImageWidth"),
    DOCX_EXPORT_CENTER_TABLES("certitools.docxExport.centerTables"),
    DOCX_EXPORT_REMOVE_STYLES_ATTR("certitools.docxExport.removeStyles"),
    DOCX_EXPORT_REPEAT_HEADER_TABLES("certitools.docxExport.repeatHeaderTables"),
    DOCX_EXPORT_ENABLED("certitools.docxExport.docxExportEnabled"),
    DOCX_EXPORT_APPLY_ALIGMNENTALLPARAGRAPHS_FIX("certitools.docxExport.applyAligmnentAllParagraphsFix"),
    HOME_CUSTOMERS_LINK("home.customers.link"),
    HOME_CUSTOMERS_WIDTH("home.customers.width"),
    HOME_CUSTOMERS_HEIGHT("home.customers.height"),
    LOCAL_INSTALLATION("certitools.localInstallation"),
    BASE_COMPANY("certitools.baseCompanyId"),
    SECURITY_UPCOMING_EVENT_DATE_HOUR_PATTERN("certitools.security.upcomingEvent.date.hour.pattern"),
    SM_RECURRENCE_TIMER_TIME_TO_RUN_HOUR("certitools.sm.recurrence.timer.hour"),
    SM_RECURRENCE_NOTIFICATION_EMAIL_SUBJECT("certitools.sm.recurrenceNotificationEmail.subject"),
    SM_RECURRENCE_NOTIFICATION_EMAIL_BODY("certitools.sm.recurrenceNotificationEmail.body"),
    SM_EVENT_CREATED_EMAIL_SUBJECT("certitools.sm.eventCreatedEmail.subject"),
    SM_EVENT_CREATED_EMAIL_BODY("certitools.sm.eventCreatedEmail.body"),
    SM_EVENT_EDIT_EMAIL_SUBJECT("certitools.sm.eventEditEmail.subject"),
    SM_EVENT_EDIT_EMAIL_BODY("certitools.sm.eventEditEmail.body"),
    SM_EVENT_CLOSED_EMAIL_SUBJECT("certitools.sm.eventClosedEmail.subject"),
    SM_EVENT_CLOSED_EMAIL_BODY("certitools.sm.eventClosedEmail.body"),
    SM_CHAT_NOTIFICATION_EMAIL_SUBJECT("certitools.sm.chatMessagesNotification.subject"),
    SM_CHAT_NOTIFICATION_EMAIL_BODY("certitools.sm.chatMessagesNotification.body"),
    SM_ACTIVITY_CODE_SEQUENCE_PATTERN("certitools.sm.activityCodeSequencePattern"),
    SM_ANOMALY_CODE_SEQUENCE_PATTERN("certitools.sm.anomalyCodeSequencePattern"),
    SM_OCCURRENCE_CODE_SEQUENCE_PATTERN("certitools.sm.occurrenceCodeSequencePattern"),
    SM_MODIFICATION_CODE_SEQUENCE_PATTERN("certitools.sm.modificationCodeSequencePattern"),
    SM_WORK_AUTHORIZATION_CODE_SEQUENCE_PATTERN("certitools.sm.workAuthorizationCodeSequencePattern"),
    SM_CORRECTIVE_ACTION_CODE_SEQUENCE_PATTERN("certitools.sm.correctiveActionCodeSequencePattern"),
    SM_MAINTENANCE_CODE_SEQUENCE_PATTERN("certitools.sm.maintenanceCodeSequencePattern"),
    SM_EMERGENCY_ACTION_CODE_SEQUENCE_PATTERN("certitools.sm.emergencyActionCodeSequencePattern");

    private String key;

    ConfigurationProperties(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
