/*
 * $Id: TreeNodeComparatorByOrder.java,v 1.1 2009/05/12 15:53:44 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/05/12 15:53:44 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.util;

import java.util.Comparator;

/**
 * TreeNodeComparator - compares 2 treenodes according to their order property and after that, compares their name
 *
 * @author pjfsilva
 */
public class TreeNodeComparatorByOrder implements Comparator<TreeNode> {
    public int compare(TreeNode o1, TreeNode o2) {
        if (o1.getOrder() == o2.getOrder()) {
            return o1.getName().compareTo(o2.getName());
        } else if (o1.getOrder() > o2.getOrder()) {
            return 1;
        } else {
            return -1;
        }
    }
}