/*
 * $Id: Utils.java,v 1.4 2012/06/13 15:57:11 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/06/13 15:57:11 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.util.ModuleType;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Web utils generic stuff
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.4 $
 */
public class Utils {
    /**
     * CERTOOL-530 - This returns the label for the menu of plans if the user is associated with 1 and only 1 contract
     * for that type of plans. Returns null if we should use the default label or the string with the correct label
     *
     * @param moduleType module type
     * @param user       user
     * @param request    request
     *
     * @return string with the correct label or null if it's to use the default label
     */
    public static String getMenuLabelForPlans(ModuleType moduleType, User user, HttpServletRequest request) {
        String label = null;

        if (!request.isUserInRole("peimanager") &&
                !request.isUserInRole("administrator") && !request.isUserInRole("contractmanager") &&
                !request.isUserInRole("legislationmanager")) {
            if (user != null && user.getUserContract() != null) {
                // counts how may contracts are per plan
                int count = 0;
                int pprevCount = 0;

                // these variables are null at the end if the user has 0, 2 or more contracts of that type
                Contract contract = null;

                for (UserContract userContract : user.getUserContract()) {
                    ModuleType key = userContract.getContract().getModule().getModuleType();

                    if (key.equals(moduleType)) {
                        count++;
                        contract = userContract.getContract();
                    }
                }

                if (count == 1 && contract != null && !StringUtils.isEmpty(contract.getMenuLabel())) {
                    label = contract.getMenuLabel();
                } else if (contract != null && contract.getCompany() != null) {
                    Locale locale = (Locale) request.getSession().getAttribute("locale");
                    if (locale.getLanguage().equalsIgnoreCase("pt")) {
                        if (moduleType.equals(ModuleType.PEI) && contract.getCompany().getPeiLabelPT() != null) {
                            label = contract.getCompany().getPeiLabelPT();
                        } else if (moduleType.equals(ModuleType.PRV) && contract.getCompany().getPrvLabelPT() != null) {
                            label = contract.getCompany().getPrvLabelPT();
                        } else if (moduleType.equals(ModuleType.PSI) && contract.getCompany().getPsiLabelPT() != null) {
                            label = contract.getCompany().getPsiLabelPT();
                        }
                    } else {
                        if (moduleType.equals(ModuleType.PEI) && contract.getCompany().getPeiLabelEN() != null) {
                            label = contract.getCompany().getPeiLabelEN();
                        } else if (moduleType.equals(ModuleType.PRV) && contract.getCompany().getPrvLabelEN() != null) {
                            label = contract.getCompany().getPrvLabelEN();
                        } else if (moduleType.equals(ModuleType.PSI) && contract.getCompany().getPsiLabelEN() != null) {
                            label = contract.getCompany().getPsiLabelEN();
                        }
                    }
                }
            }
        }
        return label;
    }
}