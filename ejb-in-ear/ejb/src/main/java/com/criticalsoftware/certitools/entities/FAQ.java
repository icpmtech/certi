/*
 * $Id: FAQ.java,v 1.5 2009/03/12 02:50:41 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/03/12 02:50:41 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;

/**
 * <description>
 *
 * @author pjfsilva
 */
@Entity
public class FAQ implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4096, nullable = false)
    private String question;

    @Column(length = 4096, nullable = false)
    private String answer;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date changedDate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private FAQCategory faqCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(Date changedDate) {
        this.changedDate = changedDate;
    }

    public FAQCategory getFaqCategory() {
        return faqCategory;
    }

    public void setFaqCategory(FAQCategory faqCategory) {
        this.faqCategory = faqCategory;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FAQ faq = (FAQ) o;

        if (id != faq.id) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
