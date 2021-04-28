/*
 * $Id: News.java,v 1.5 2010/03/30 17:27:15 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/03/30 17:27:15 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.*;
import java.util.Date;
import java.io.Serializable;

/**
 * <description>
 *
 * @author pjfsilva
 */

@Entity
public class News implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic(optional = false)
    private String title;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationDate;

    @Column(length = 4096, nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean published = false;

    @ManyToOne(optional = false)
    private NewsCategory category;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public NewsCategory getCategory() {
        return category;
    }

    public void setCategory(NewsCategory category) {
        this.category = category;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        News news = (News) o;

        return id == news.id;

    }

    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
