/*
 * $Id: PreActionFilter.java,v 1.28 2013/12/18 03:18:01 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2013/12/18 03:18:01 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.business.certitools.UserService;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.license.LicenseService;
import com.criticalsoftware.certitools.entities.*;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.ModuleType;
import org.apache.commons.lang.StringUtils;
import org.jboss.security.SecurityAssociation;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A Pre Action Filter
 *
 * @author : lt-rico
 * @version : $version $
 */
public class PreActionFilter implements Filter {

    private static final Logger LOGGER = Logger.getInstance(PreActionFilter.class);

    /**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     */
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /** Take this filter out of service. */
    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        LOGGER.info("PreActionFilter running " + ((HttpServletRequest) request).getUserPrincipal());

        //Get the menu from request and check it
        Menu menu = (Menu) ((HttpServletRequest) request).getSession().getAttribute("menu");

        try {
            InitialContext ctx = new InitialContext();
            Principal principal = ((HttpServletRequest) request).getUserPrincipal();

            LOGGER.debug("Principal = " + SecurityAssociation.getPrincipal());
            LOGGER.debug("Caller Principal = " + SecurityAssociation.getCallerPrincipal());

            User user = (User) ((HttpServletRequest) request).getSession().getAttribute("user");
            boolean firstLogin = false;
            // user login
            if (user == null && principal != null) {
                UserService userService = (UserService) ctx.lookup("certitools/UserService");

                if (Configuration.getInstance().getLocalInstallation()) {
                    try {
                        userService.validateLoginUserByEmail(principal.getName());
                    } catch (ObjectNotFoundException e) {
                        if (e.getType().equals(ObjectNotFoundException.Type.USER)) {
                            createNewUser(principal.getName(), ctx);
                            firstLogin = true;
                        } else {
                            throw e;
                        }
                    }
                }
                if (!firstLogin) {

                    // check user unique session
                    if (!userService.validateLoginUserByEmail(principal.getName())) {
                        ((HttpServletRequest) request).getSession().invalidate();
                        LOGGER.info("[PreActionFilter] user session already in use");

                        // redirect to the same page he tried to login
                        String urlString = ((HttpServletRequest) request).getRequestURL().toString();
                        urlString += "?errorUniquesession=true";
                        String queryString = ((HttpServletRequest) request).getQueryString();

                        if (queryString != null) {
                            urlString += queryString;
                        }

                        ((HttpServletResponse) response).sendRedirect(urlString);

                        return;
                    }

                    user = userService.findByEmailWithRoles(principal.getName());
                    ((HttpServletRequest) request).getSession().setAttribute("user", user);

                    /* CERTOOL-496
                    // set Locale to Company Locale
                    ((HttpServletRequest) request).getSession()
                            .setAttribute("locale", new Locale(user.getCompany().getLanguage()));
                    */
                    // set Locale to User Locale
                    ((HttpServletRequest) request).getSession()
                            .setAttribute("locale", new Locale(user.getLanguage()));

                    //Set the menu
                    buildMenu(request, user, user.getCompany());

                    //Set user session to active
                    userService.updateUserSessionActive(user.getId(), true);

                    // application licence is ok? set the session variable
                    LicenseService licenseService = (LicenseService) ctx.lookup("certitools/LicenseService");
                    if (!licenseService.validateLicense()) {
                        ((HttpServletRequest) request).getSession()
                                .setAttribute("licenceApplicationValid", false);
                        LOGGER.info("[PreActionFilter] applicationLicense is invalid");
                        request.getRequestDispatcher("/WEB-INF/applicationLicenseError.jsp").forward(request, response);
                        return;
                    } else {
                        LOGGER.info("[PreActionFilter] applicationLicense is OK");
                        ((HttpServletRequest) request).getSession()
                                .setAttribute("licenceApplicationValid", true);
                    }

                    // update user last login date
                    userService.updateUserLoginStatistics(user);

                    // update session inactive time
                    ((HttpServletRequest) request).getSession()
                            .setMaxInactiveInterval(
                                    60 * Integer.valueOf(Configuration.getInstance().getSessionTimeout()));

                    // update user seen help pei
                    if (!user.isSeenPEI()) {
                        user.setShowPEIHelp(true);
                        userService.updateUserSeenPEI(user.getId(), true);
                    }
                }

            }

            if (menu == null) {
                buildMenu(request, user, user == null ? null : user.getCompany());
            }

            // check if license application is ok
            Boolean licenceApplicationValid =
                    (Boolean) ((HttpServletRequest) request).getSession().getAttribute("licenceApplicationValid");

            if (licenceApplicationValid != null && !licenceApplicationValid) {
                LOGGER.info("[PreActionFilter] applicationLicense is invalid");
                request.getRequestDispatcher("/WEB-INF/applicationLicenseError.jsp").forward(request, response);
                return;
            }

            // TODO pjfsilva do a special case to not give the error page when trying to access the contacs page


        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            request.getRequestDispatcher("/WEB-INF/errorMain.jsp").forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    private void buildMenu(ServletRequest request, User user, Company company) {
        // CERTOOL-530 - check menu label for plans
        HttpServletRequest reqTemp = (HttpServletRequest) request;
        Locale locale = (Locale) reqTemp.getSession().getAttribute("locale");
        String peiLabel = null;
        String pprevLabel = null;
        String psiLabel = null;

        if (!reqTemp.isUserInRole("peimanager") &&
                !reqTemp.isUserInRole("administrator") && !reqTemp.isUserInRole("contractmanager") &&
                !reqTemp.isUserInRole("legislationmanager")) {
            if (user != null && user.getUserContract() != null) {
                // counts how may contracts are per plan
                int peiCount = 0;
                int pprevCount = 0;
                int psiCount = 0;

                // these variables are null at the end if the user has 0, 2 or more contracts of that type
                Contract peiContract = null;
                Contract pprevContract = null;
                Contract psiContract = null;

                for (UserContract userContract : user.getUserContract()) {
                    ModuleType key = userContract.getContract().getModule().getModuleType();

                    // TODO-MODULE
                    if (key.equals(ModuleType.PEI)) {
                        peiCount++;
                        peiContract = userContract.getContract();
                    } else if (key.equals(ModuleType.PRV)) {
                        pprevCount++;
                        pprevContract = userContract.getContract();
                    } else if (key.equals(ModuleType.PSI)) {
                        psiCount++;
                        psiContract = userContract.getContract();
                    }
                }

                if (locale.getLanguage().equalsIgnoreCase("pt")) {

                    if (peiContract != null) {
                        if (peiCount == 1 && peiContract.getMenuLabel() != null && !StringUtils
                                .isEmpty(peiContract.getMenuLabel())) {
                            peiLabel = peiContract.getMenuLabel();
                        } else if (peiCount >= 1 && company.getPeiLabelPT() != null && !StringUtils
                                .isEmpty(company.getPeiLabelPT())) {
                            peiLabel = company.getPeiLabelPT();
                        }
                    }

                    if (pprevContract != null) {
                        if (pprevCount == 1 && pprevContract.getMenuLabel() != null && !StringUtils
                                .isEmpty(pprevContract.getMenuLabel())) {
                            pprevLabel = pprevContract.getMenuLabel();
                        } else if (pprevCount >= 1 && company.getPrvLabelPT() != null && !StringUtils
                                .isEmpty(company.getPrvLabelPT())) {
                            pprevLabel = company.getPrvLabelPT();
                        }
                    }

                    if (psiContract != null) {
                        if (psiCount == 1 && psiContract.getMenuLabel() != null && !StringUtils
                                .isEmpty(psiContract.getMenuLabel())) {
                            psiLabel = psiContract.getMenuLabel();
                        } else if (psiCount >= 1 && company.getPsiLabelPT() != null && !StringUtils
                                .isEmpty(company.getPsiLabelPT())) {
                            psiLabel = company.getPsiLabelPT();
                        }
                    }

                } else {
                    if (peiContract != null) {
                        if (peiCount == 1 && peiContract.getMenuLabel() != null && !StringUtils
                                .isEmpty(peiContract.getMenuLabel())) {
                            peiLabel = peiContract.getMenuLabel();
                        } else if (peiCount >= 1 && company.getPeiLabelEN() != null && !StringUtils
                                .isEmpty(company.getPeiLabelEN())) {
                            peiLabel = company.getPeiLabelEN();
                        }
                    }

                    if (pprevContract != null) {
                        if (pprevCount == 1 && pprevContract.getMenuLabel() != null && !StringUtils
                                .isEmpty(pprevContract.getMenuLabel())) {
                            pprevLabel = pprevContract.getMenuLabel();
                        } else if (pprevCount >= 1 && company.getPrvLabelEN() != null && !StringUtils
                                .isEmpty(company.getPrvLabelEN())) {
                            pprevLabel = company.getPrvLabelEN();
                        }
                    }

                    if (psiContract != null) {
                        if (psiCount == 1 && psiContract.getMenuLabel() != null && !StringUtils
                                .isEmpty(psiContract.getMenuLabel())) {
                            psiLabel = psiContract.getMenuLabel();
                        } else if (psiCount >= 1 && company.getPsiLabelEN() != null && !StringUtils
                                .isEmpty(company.getPsiLabelEN())) {
                            psiLabel = company.getPsiLabelEN();
                        }
                    }
                }
            }
        }

        //Set the menu
        ((HttpServletRequest) request).getSession().setAttribute("menu",
                new Menu((HttpServletRequest) request, peiLabel, pprevLabel, psiLabel));
    }

    private void createNewUser(String username, InitialContext ctx) throws ObjectNotFoundException, NamingException {
        ArrayList<Role> rolesSelected = new ArrayList<Role>();
        rolesSelected.add(new Role(Configuration.getInstance().getUserRole()));
        User user = new User();
        user.setRoles(rolesSelected);
        user.setEmail(username);
        user.setName(username);
        user.setFiscalNumber(1L);

        UserService userService = (UserService) ctx.lookup("certitools/UserService");
        userService.insertByLocalInstallation(user, Configuration.getInstance().getBaseCompanyId());

    }
}
