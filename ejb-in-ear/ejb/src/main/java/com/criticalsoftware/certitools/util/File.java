/*
 * $Id: File.java,v 1.6 2009/04/20 18:36:40 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/20 18:36:40 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.util;

import java.io.InputStream;
import java.util.Date;

/**
 * File object
 *
 * @author : lt-rico
 */
public class File {

    private Long id;

    private String contentType;

    private Date lastModified;

    private InputStream data;

    private String fullDrTitle;

    private String customTitle;

    private String customAbstract;

    private String keywords;

    private String summary;

    private Boolean publish;

    private String fileName;

    public File() {
    }

    public File(Long id, String contentType, InputStream data) {
        this.id = id;
        this.contentType = contentType;
        this.data = data;
    }

    public File(Long id, String contentType, InputStream data, String fileName) {
        this.id = id;
        this.contentType = contentType;
        this.data = data;
        this.fileName = fileName;
    }

    public File(Long id, String contentType, InputStream data, String fullDrTitle, String customTitle,
                String customAbstract,
                String keywords, String summary, Boolean publish) {
        this.id = id;
        this.contentType = contentType;
        this.data = data;
        this.fullDrTitle = fullDrTitle;
        this.customTitle = customTitle;
        this.customAbstract = customAbstract;
        this.keywords = keywords;
        this.summary = summary;
        this.publish = publish;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public String getContentType() {
        return contentType;
    }


    public Date getLastModified() {
        return lastModified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getCustomAbstract() {
        return customAbstract;
    }

    public void setCustomAbstract(String customAbstract) {
        this.customAbstract = customAbstract;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public String getFullDrTitle() {
        return fullDrTitle;
    }

    public void setFullDrTitle(String fullDrTitle) {
        this.fullDrTitle = fullDrTitle;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        File file = (File) o;

        if (!contentType.equals(file.contentType)) {
            return false;
        }
        if (!data.equals(file.data)) {
            return false;
        }
        if (!id.equals(file.id)) {
            return false;
        }
        if (!lastModified.equals(file.lastModified)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + contentType.hashCode();
        result = 31 * result + lastModified.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }
}
