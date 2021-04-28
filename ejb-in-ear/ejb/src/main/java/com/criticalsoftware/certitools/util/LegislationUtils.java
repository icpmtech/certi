/*
 * $Id: LegislationUtils.java,v 1.2 2009/09/23 10:53:14 jp-gomes Exp $
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

import com.criticalsoftware.certitools.entities.LegalDocumentCategory;

import java.util.List;
import java.util.ArrayList;

/**
 * Legislation Utils Class
 *
 * @author jp-gomes
 */
public class LegislationUtils {

    public static LegalDocumentCategory[] buildCategoryHierarchy(LegalDocumentCategory category) {
        LegalDocumentCategory[] resultList = new LegalDocumentCategory[category.getDepth().intValue()];
        resultList[category.getDepth().intValue() - 1] = category;
        while (category.getParentCategory() != null) {
            resultList[category.getParentCategory().getDepth().intValue() - 1] =
                    category.getParentCategory();
            category = category.getParentCategory();
        }
        return resultList;
    }

    public static List<LegalDocumentCategory> showLongestCategory(List<LegalDocumentCategory> ldCategories) {
        List<LegalDocumentCategory> all = new ArrayList<LegalDocumentCategory>();
        // Add all Paths to List
        for (LegalDocumentCategory ldc : ldCategories) {
            all.addAll(buildCategoryPath(ldc));
        }
        List<LegalDocumentCategory> finalList = all;
        for (LegalDocumentCategory ldc : all) {
            if (containMoreThenOne(finalList, ldc)) {
                finalList = removeAllElements(finalList, ldc);
            }
        }
        return finalList;
    }

    private static List<LegalDocumentCategory> buildCategoryPath(LegalDocumentCategory ldc) {
        List<LegalDocumentCategory> ldcList = new ArrayList<LegalDocumentCategory>();
        ldcList.add(ldc);
        while (ldc.getParentCategory() != null) {
            ldc = ldc.getParentCategory();
            ldcList.add(ldc);
        }
        return ldcList;
    }

    private static boolean containMoreThenOne(List<LegalDocumentCategory> list, LegalDocumentCategory ldc) {

        int counter = 0;
        for (LegalDocumentCategory ldcFromList : list) {
            if (ldcFromList.equals(ldc)) {
                counter++;
            }
            if (counter >= 2) {
                return true;
            }
        }
        return false;
    }

    private static List<LegalDocumentCategory> removeAllElements(List<LegalDocumentCategory> list,
                                                                 LegalDocumentCategory toRemove) {
        List<LegalDocumentCategory> finalList = new ArrayList<LegalDocumentCategory>();
        for (LegalDocumentCategory ldc : list) {
            if (!ldc.equals(toRemove)) {
                finalList.add(ldc);
            }
        }
        return finalList;
    }
}
