/*
 * $Id: HelpSearchableContent.java,v 1.3 2009/10/27 11:55:41 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/27 11:55:41 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Help Searchable Content
 *
 * @author jp-gomes
 */
@Entity
public class HelpSearchableContent implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30000, nullable = false)
    private String searchContent;

    @Column(nullable = false)
    private String titleToSearch;

    @Column(nullable = false)
    private String titleToShow;

    @Column(nullable = false)
    private String fileName;

    @Column
    private String permissions;

    public HelpSearchableContent() {

    }

    public HelpSearchableContent(String titleToShow, String fileName, String permissions) {
        this.titleToShow = titleToShow;
        this.fileName = fileName;
        this.permissions = permissions;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HelpSearchableContent that = (HelpSearchableContent) o;
        return fileName.equals(that.fileName);
    }

    public int hashCode() {
        return id.hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getTitleToSearch() {
        return titleToSearch;
    }

    public void setTitleToSearch(String titleToSearch) {
        this.titleToSearch = titleToSearch;
    }

    public String getTitleToShow() {
        return titleToShow;
    }

    public void setTitleToShow(String titleToShow) {
        this.titleToShow = titleToShow;
    }
}
