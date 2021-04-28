/*
 * $Id: UserRegisterActionBean.java,v 1.2 2010/02/05 15:17:50 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/02/05 15:17:50 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action;

import com.criticalsoftware.certitools.business.certitools.ContractService;
import com.criticalsoftware.certitools.business.certitools.UserService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.Role;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.Configuration;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.*;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Description.
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.2 $
 */
public class UserRegisterActionBean extends AbstractActionBean implements ValidationErrorHandler {
    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    @EJBBean(value = "certitools/UserService")
    private UserService userService;

    private long contractId;
    private String code;

    private boolean showForm = false;
    private boolean success = false;

    @ValidateNestedProperties({
            @Validate(field = "name", required = true, maxlength = 255, on = "insertUser"),
            @Validate(field = "email", required = true, converter = EmailTypeConverter.class, maxlength = 255,
                    on = "insertUser"),
            @Validate(field = "fiscalNumber", required = true, maxvalue = 999999999999L, on = "insertUser"),
            @Validate(field = "phone", maxlength = 32, on = "insertUser")
    })
    private User user;

    private Company company;

    @Override
    public void fillLookupFields() {
    }

    @DefaultHandler
    public Resolution insertUserForm() throws ObjectNotFoundException, BusinessException {
        Contract contractInDb = contractService.findByIdWithUserContract(contractId);

        if (!checkUserRegistrationaValid(contractInDb)) {
            return new ForwardResolution("/WEB-INF/jsps/certitools/userRegister.jsp");
        }

        company = contractInDb.getCompany();

        showForm = true;
        return new ForwardResolution("/WEB-INF/jsps/certitools/userRegister.jsp");
    }

    public Resolution insertUser() throws ObjectNotFoundException, BusinessException {
        Contract contractInDb = contractService.findByIdWithUserContract(contractId);

        if (!checkUserRegistrationaValid(contractInDb)) {
            return new ForwardResolution("/WEB-INF/jsps/certitools/userRegister.jsp");
        }

        ArrayList<Role> rolesSelected = new ArrayList<Role>();
        rolesSelected.add(new Role(Configuration.getInstance().getUserRole()));
        user.setRoles(rolesSelected);

        userService.insertByUserRegistration(user, contractId);
        success = true;

        return new ForwardResolution("/WEB-INF/jsps/certitools/userRegister.jsp");
    }

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertUser")) {
            return insertUserForm();
        }
        return null;
    }

    @ValidationMethod(on = "insertUser", when = ValidationState.NO_ERRORS)
    public void validateInsertUser(ValidationErrors errors) throws Exception {
        // check if email domain is valid
        Contract contractInDb = contractService.findByIdWithUserContract(contractId);

        if (userService.findByEmailWithDeletedAllowed(user.getEmail()).size() > 0) {
            errors.add("user.email", new LocalizableError("user.error.duplicateEmail"));
        }

        if (!StringUtils.isBlank(contractInDb.getUserRegisterDomains())) {
            String domainUser = user.getEmail().substring(user.getEmail().lastIndexOf("@") + 1);

            StringTokenizer st = new StringTokenizer(contractInDb.getUserRegisterDomains(), ",");
            String domainTemp;
            boolean validationOk = false;

            while (st.hasMoreTokens()) {
                domainTemp = StringUtils.trim(st.nextToken());
                if (StringUtils.equalsIgnoreCase(domainTemp, domainUser)) {
                    validationOk = true;
                }
            }

            if (!validationOk){
                 errors.add("user.email", new LocalizableError("user.register.error.domainInvalid"));
            }
        }
    }

    private boolean checkUserRegistrationaValid(Contract contract) throws BusinessException, ObjectNotFoundException {
        // check link is ok
        if (!contractService.validateUserRegisterCode(contractId, code)) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("user.register.error.invalidLink"));
            return false;
        }

        // contract has licenses?
        if (contract.getLicenses() - contract.getUserContract().size() <= 0) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("user.register.error.missingLicenses"));
            return false;
        }

        return true;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ContractService getContractService() {
        return contractService;
    }

    public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}