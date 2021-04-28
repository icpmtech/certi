/*
 * $Id: LegalDocumentStatistics.java,v 1.8 2009/04/02 16:21:23 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/02 16:21:23 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Legal document statistics report
 *
 * @author : lt-rico
 */
@Entity
public class LegalDocumentStatistics implements Serializable {

    public enum ReportType {
        VISUALIZATION, DOWNLOAD, SEARCH_TERM
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date date;

    @Column
    private String text;

    @Column
    private Long documentId;

    @Transient
    private Long countVisualizations;

    @Transient
    private Long countDownloads;

    @Transient
    private Long countSearchTerms;

    public LegalDocumentStatistics() {
    }

    public LegalDocumentStatistics(Long id, ReportType reportType, Date date, String text) {
        this.id = id;
        this.reportType = reportType;
        this.date = date;
        this.text = text;
    }

    public LegalDocumentStatistics(ReportType reportType, String text, Long documentId, Long count) {
        this.text = text;
        this.documentId = documentId;
        switch (reportType) {
            case VISUALIZATION:
                this.countVisualizations = count;
                break;
            case DOWNLOAD:
                this.countDownloads = count;
                break;
            case SEARCH_TERM:
                this.countSearchTerms = count;
                break;
        }
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public Long getCountVisualizations() {
        return countVisualizations;
    }

    public void setCountVisualizations(Long countVisualizations) {
        this.countVisualizations = countVisualizations;
    }

    public Long getCountSearchTerms() {
        return countSearchTerms;
    }

    public void setCountSearchTerms(Long countSearchTerms) {
        this.countSearchTerms = countSearchTerms;
    }

    public Long getCountDownloads() {
        return countDownloads;
    }

    public void setCountDownloads(Long countDownloads) {
        this.countDownloads = countDownloads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LegalDocumentStatistics that = (LegalDocumentStatistics) o;

        if (text != null ? !text.equals(that.text) : that.text != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }

    @Override public String toString() {
        return new StringBuilder().append("LegalDocumentStatistics{").append("id=").append(id).append(", reportType=")
                .append(reportType).append(", date=").append(date).append(", text='").append(text).append('\'')
                .append(", legalDocument=").append(", countVisualizations=")
                .append(countVisualizations).append('}')
                .toString();
    }
}
