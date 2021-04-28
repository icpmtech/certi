/*
 * $Id: LegalDocumentCategoryComparator.java,v 1.2 2009/09/23 10:53:14 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/09/23 10:53:14 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.util;

import com.criticalsoftware.certitools.entities.LegalDocument;
import com.criticalsoftware.certitools.entities.LegalDocumentCategory;

import java.util.Comparator;
import java.util.List;

/**
 * Comparator to sort LegalDocuments by Category
 *
 * @author jp-gomes
 */
public class LegalDocumentCategoryComparator implements Comparator<LegalDocument> {

    public int compare(LegalDocument o1, LegalDocument o2) {

        int maxLength = o1.getLegalDocumentCategories().size();
        if (o2.getLegalDocumentCategories().size() > maxLength) {
            maxLength = o2.getLegalDocumentCategories().size();
        }
        for (int i = 1; i <= maxLength; i++) {
            LegalDocumentCategory legalDocumentCategoryO1 = findCategoryByDepth(i, o1.getLegalDocumentCategories());
            LegalDocumentCategory legalDocumentCategoryO2 = findCategoryByDepth(i, o2.getLegalDocumentCategories());

            if (legalDocumentCategoryO1 == null) {
                return -1;
            }
            if (legalDocumentCategoryO2 == null) {
                return 1;
            }
            String legalDocumentCategoryO1Name =
                    Utils.removeAccentedChars(legalDocumentCategoryO1.getName().toLowerCase());
            String legalDocumentCategoryO2Name =
                    Utils.removeAccentedChars(legalDocumentCategoryO2.getName().toLowerCase());
            if (!legalDocumentCategoryO1Name.equals(legalDocumentCategoryO2Name)) {
                return legalDocumentCategoryO1Name.compareTo(legalDocumentCategoryO2Name);
            }
        }
        return 0;
    }

    private LegalDocumentCategory findCategoryByDepth(int depth, List<LegalDocumentCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        for (LegalDocumentCategory category : categories) {
            if (category.getDepth() == depth) {
                return category;
            }
        }
        return null;
    }
}
