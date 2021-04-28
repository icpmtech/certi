/*
 * $Id: LegalDocumentComparator.java,v 1.2 2009/04/03 09:37:20 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/04/03 09:37:20 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.util;

import com.criticalsoftware.certitools.entities.LegalDocument;

import java.util.Comparator;

/**
 * Comparator for legal documents by publication date
 *
 * @author : lt-rico
 */
public class LegalDocumentComparator implements Comparator<LegalDocument> {

    public int compare(LegalDocument a, LegalDocument b) {

        if (a.getDocumentState().getId() < b.getDocumentState().getId()) {
            return -1;
        } else if (a.getDocumentState().getId() > b.getDocumentState().getId()) {
            return 1;
        } else {
            if (a.getPublicationDate().before(b.getPublicationDate())) {
                return 1;
            } else if (a.getPublicationDate().after(b.getPublicationDate())) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}

