/*
 * $Id: SearchStatistic.java,v 1.2 2009/03/03 13:03:30 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/03/03 13:03:30 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.*;

/**
 * <description>
 *
 * @author pjfsilva
 */
@Entity
public class SearchStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 512)
    private String searchPhrase;

    @Column(nullable = false)
    private long count = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SearchStatistic that = (SearchStatistic) o;

        if (id != that.id) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}