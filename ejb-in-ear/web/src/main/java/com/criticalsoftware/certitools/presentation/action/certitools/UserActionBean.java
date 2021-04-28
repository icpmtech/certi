/*
 * $Id: UserActionBean.java,v 1.1 2013/12/18 05:28:14 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2013/12/18 05:28:14 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.certitools;

import com.criticalsoftware.certitools.business.certitools.CompanyService;
import com.criticalsoftware.certitools.business.certitools.ContractService;
import com.criticalsoftware.certitools.business.certitools.UserService;
import com.criticalsoftware.certitools.business.exception.AdminDeleteException;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.*;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.DownloadFileResolution;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PTDateTypeConverter;
import com.criticalsoftware.certitools.presentation.util.ValidationUtils;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.ConfigurationProperties;
import com.criticalsoftware.certitools.util.ModuleType;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * User Action Bean
 *
 * @author pjfsilva
 */
public class UserActionBean extends DisplayTagSupportActionBean implements ValidationErrorHandler {

    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    @EJBBean(value = "certitools/UserService")
    private UserService userService;

    private Company company;
    private ArrayList<Contract> contracts;

    @ValidateNestedProperties({
            @Validate(field = "name", required = true, maxlength = 255, on = "insertUser"),
            @Validate(field = "email", required = true,  maxlength = 255, on = "insertUser"),
            @Validate(field = "fiscalNumber", required = true, maxvalue = 999999999999L, on = "insertUser"),
            @Validate(field = "phone", maxlength = 32, on = "insertUser"),
            @Validate(field = "company.id", required = true, on = "insertUser"),
            @Validate(field = "externalUser", maxlength = 255, on = "insertUser")
    })
    private User user;

    @ValidateNestedProperties({
            @Validate(field = "validityStartDate", converter = PTDateTypeConverter.class),
            @Validate(field = "validityEndDate", converter = PTDateTypeConverter.class)
    })
    private ArrayList<UserContract> userContractsForm;
    private List<Role> roles;
    private Map<Long, List<Long>> userContractPermissions;

    private boolean edit;
    private String letter;

    private String letterUser = StringUtils
            .upperCase(com.criticalsoftware.certitools.util.Configuration.getInstance().getUsersDefaultView());

    private ArrayList<User> users;

    // if it has the value "users" it means that when we insert a new user, we should go to the users page, not the
    // companies page
    private String source = null;

    // used to build the links
    private String[] alphabet =
            {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
             "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    // resource name of the global error we want to show
    private String error;

    private String searchPhrase;

    // to see the list of users per contract
    private Contract contract;

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(MenuItem.Item.MENU_ADMIN, MenuItem.Item.SUB_MENU_ADMIN_COMPANY);
    }

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertUser")) {
            return insertUserForm();
        }
        return null;
    }

    @DontValidate
    @DefaultHandler
    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution viewCompanies() {
        Long companyId = (company.getId() != null) ? company.getId() : null;

        return new RedirectResolution(CompanyActionBean.class).addParameter("company.id", companyId)
                .addParameter("letter", letter);
    }

    @DontValidate
    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution cancelInsertUser() {
        Long companyId = (company.getId() != null) ? company.getId() : null;
        Long contractId = (contract != null) ? contract.getId() : null;

        if (source != null && source.equalsIgnoreCase("users")) {
            return new RedirectResolution(UserActionBean.class, "viewUsers").addParameter("company.id", companyId)
                    .addParameter("letterUser", letterUser)
                    .addParameter("contract.id", contractId)
                    .addParameter("user.id", user.getId());
        } else {
            return new RedirectResolution(CompanyActionBean.class).addParameter("company.id", companyId)
                    .addParameter("letter", letter);
        }
    }

    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution viewUsers() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        // set errors if they exist
        // input sanitization: check if errors is in a pre-defined whitelist
        if (error != null) {
            String[] whitelistErrors = {"user.del.adminError"};
            if (ArrayUtils.contains(whitelistErrors, error)) {
                getContext().getValidationErrors().addGlobalError(new LocalizableError(error));
            }
        }

        User userInSession = getUserInSession();

        // user selected so we must get it
        if (user != null && user.getId() != null) {
            user = userService.findByIdAllowed(user.getId(), userInSession);
        }

        // export options
        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility.getLocalizedFieldName("users.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility.getLocalizedFieldName("users.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility.getLocalizedFieldName("users.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility.getLocalizedFieldName("users.filename.pdf", null, null, locale));

        // get contract name if we came from the "users of a contract" list
        if (contract != null) {
            contract = contractService.findById(contract.getId());
        }

        if (isExportRequest()) {
            if (contract != null) {
                users = (ArrayList<User>) userService
                        .findAllWithRolesByContractIdAllowed(contract.getId(), userInSession);
            } else {
                users = (ArrayList<User>) userService
                        .findAllWithRolesByCompanyIdAllowed(company.getId(), userInSession);
            }
        } else {

            if (!ValidationUtils.validateNavigationLetter(letterUser)) {
                letterUser = "";
            }

            if (user != null && user.getId() != null && (letterUser == null || letterUser.equals(""))) {
                letterUser = "";
                List<User> userListTemp = new ArrayList<User>();
                userListTemp.add(user);
                users = (ArrayList<User>) userListTemp;
            } else {

                if (contract != null) {
                    users = (ArrayList<User>) userService
                            .findUsersByStartLetterAndContractIdAllowed(letterUser, contract.getId(), userInSession);

                } else {
                    users = (ArrayList<User>) userService
                            .findUsersByStartLetterAllowed(letterUser, company.getId(), userInSession);
                }
            }
        }

        company = companyService.findAllowed(company.getId(), userInSession);

        setHelpId("#user-management");
        return new ForwardResolution("/WEB-INF/jsps/certitools/users.jsp");
    }

    // ajax called
    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution viewUserFragment() throws ObjectNotFoundException, CertitoolsAuthorizationException {
        user = userService.findByIdWithContractsRolesAllowed(user.getId(), getUserInSession());

        Contract contract;
        for (UserContract userContract : user.getUserContract()) {
            contract = userContract.getContract();
            contract.setModule(loadModule(contract.getModule().getModuleType()));
            contract.setContractPermissions(contractService.findContractPermissions(contract.getId()));
        }

        setAttribute("specialPermission", ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey());

        getContext().getResponse().setHeader("Stripes-Success", "OK");
        return new ForwardResolution("/WEB-INF/jsps/certitools/userFragment.jsp");
    }

    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution insertUserForm() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        company = companyService.findAllowed(company.getId(), getUserInSession());
        contracts = (ArrayList<Contract>)
                contractService.findAllWithUserContractAllowed(company.getId(), getUserInSession());
        for (Contract contract : contracts) {
            contract.setModule(loadModule(contract.getModule().getModuleType()));

            // TODO-MODULE
            if (contract.getModule().getModuleType().equals(ModuleType.PEI) || contract.getModule().getModuleType()
                    .equals(ModuleType.PRV) || contract.getModule().getModuleType().equals(ModuleType.PSI)
                    || contract.getModule().getModuleType().equals(ModuleType.GSC)) {
                contract.setContractPermissions(contractService.findContractPermissions(contract.getId()));
            }
        }

        boolean isCertitecna = company.getId().toString()
                .equalsIgnoreCase(Configuration.getInstance().getCertitecnaId());
        roles = (List<Role>) userService.findAllRolesAllowed(isCertitecna, getUserInSession());

        setAttribute("specialPermission", ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey());
        //setAttribute("specialPermission", ConfigurationProperties.PERMISSION_GSC_MANAGER.getKey());

        setHelpId("#add-user");
        return new ForwardResolution("/WEB-INF/jsps/certitools/usersInsert.jsp");
    }

    @SuppressWarnings({"unchecked"})
    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution insertUser() throws ObjectNotFoundException, CertitoolsAuthorizationException {
        ArrayList<UserContract> userContracts = new ArrayList<UserContract>();

        // check user contracts
        if (userContractsForm != null) {
            for (UserContract userContract : userContractsForm) {
                if (userContract != null && userContract.isAssociatedWithUser()) {

                    if (!userContract.isValidityChanged()) {
                        userContract.setValidityStartDate(null);
                        userContract.setValidityEndDate(null);
                    }

                    userContracts.add(userContract);
                }
            }
        }

        // check roles
        ArrayList<Role> rolesSelected = new ArrayList<Role>();
        if (roles != null) {
            for (Role role : roles) {
                if (role.isAssociatedWithUser()) {
                    rolesSelected.add(role);
                }
            }
        }
        // add user role
        rolesSelected.add(new Role(Configuration.getInstance().getUserRole()));

        user.setUserContract(new HashSet(userContracts));
        user.setRoles(rolesSelected);
        //add user contract permissions
        prepareAndSetUserPermissionsForService();

        if (edit) {
            userService.update(user, getUserInSession());
        } else {
            userService.insert(user, getUserInSession());
        }

        getContext().getMessages().add(new LocalizableMessage("user.add.sucess"));

        // if it has the value "users" it means that when we insert a new user, we should go to the users page, not the
        // companies page
        // in case of edit we always came from the users pages
        if (edit || (source != null && source.equalsIgnoreCase("users"))) {

            if (edit && contract != null) {
                return new RedirectResolution(UserActionBean.class, "viewUsers")
                        .addParameter("company.id", company.getId())
                        .addParameter("letterUser", "")
                        .addParameter("contract.id", contract.getId())
                        .addParameter("user.id", user.getId());
            } else {
                return new RedirectResolution(UserActionBean.class, "viewUsers")
                        .addParameter("company.id", company.getId())
                        .addParameter("letterUser", "")
                        .addParameter("user.id", user.getId());
            }

        }

        return new RedirectResolution(CompanyActionBean.class).addParameter("company.id", company.getId())
                .addParameter("letter", letter);
    }

    @ValidationMethod(on = "insertUser", when = ValidationState.NO_ERRORS)
    public void validateInsertUser(ValidationErrors errors) throws Exception {
        User originalUser = null;

        if (edit) {
            originalUser = userService.findByIdWithContractsRolesAllowed(user.getId(), getUserInSession());

        }
        // if editing and changed the email, check if it's in use
        // if inserting always check
        if ((edit && user.getEmail().compareToIgnoreCase(originalUser.getEmail()) != 0 &&
                userService.findByEmailWithDeletedAllowed(user.getEmail()).size() > 0)
                ||
                (!edit && userService.findByEmailWithDeletedAllowed(user.getEmail()).size() > 0)) {
            errors.add("user.email", new LocalizableError("user.error.duplicateEmail"));
        }

        UserContract userContract;
        Contract contractTemp;
        if (userContractsForm != null) {
            for (int i = 0; i < userContractsForm.size(); i++) {
                userContract = userContractsForm.get(i);
                if (userContract != null && userContract.isAssociatedWithUser() &&
                        userContract.isValidityChanged() &&
                        userContract.getValidityStartDate().after(userContract.getValidityEndDate())) {
                    errors.add("userContractsForm[" + i + "].validityStartDate",
                            new LocalizableError("user.startDate.afterEndDate"));
                }


                if (userContract.isAssociatedWithUser()) {
                    if (!edit || (edit && !originalUser.getUserContract().contains(userContract))) {
                        // licenses available?
                        contractTemp =
                                contractService
                                        .findByIdWithUserContract(userContract.getUserContractPK().getIdContract(),
                                                getUserInSession());

                        if (contractTemp.getLicenses() - contractTemp.getUserContract().size() <= 0) {
                            errors.add("userContractsForm[" + i + "].associatedWithUser",
                                    new LocalizableError("user.error.noLicensesAvailable", contractTemp.getNumber()));
                        }
                    }
                }
            }
        }
    }

    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution deleteUser() throws ObjectNotFoundException, CertitoolsAuthorizationException {

        Long contractId = (contract != null) ? contract.getId() : null;

        try {
            userService.delete(user, getUserInSession());
            getContext().getMessages().add(new LocalizableMessage("user.del.sucess"));
        } catch (AdminDeleteException e) {

            return new RedirectResolution(UserActionBean.class, "viewUsers").addParameter("company.id", company.getId())
                    .addParameter("letterUser", letterUser)
                    .addParameter("contract.id", contractId)
                    .addParameter("error", "user.del.adminError");
        }

        // in the special that the user was seeing only 1 user and deleted it, set the letterUser to A
        //if (letterUser == null || letterUser.equals("")) {
        //    letterUser = "A";
        //}

        return new RedirectResolution(UserActionBean.class, "viewUsers").addParameter("company.id", company.getId())
                .addParameter("contract.id", contractId)
                .addParameter("letterUser", letterUser);
    }

    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution searchUser() throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException {
        if (searchPhrase == null) {
            searchPhrase = "";
        }

        if (contract != null) {
            contract = contractService.findById(contract.getId());
            users = (ArrayList<User>)
                    userService.findUsersByContractIdAndNameAllowed(contract.getId(), searchPhrase, getUserInSession());
        } else {
            users = (ArrayList<User>)
                    userService.findUsersByCompanyIdAndNameAllowed(company.getId(), searchPhrase, getUserInSession());
        }
        company = companyService.findAllowed(company.getId(), getUserInSession());
        letterUser = "";

        return new ForwardResolution("/WEB-INF/jsps/certitools/users.jsp");
    }

    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution updateUserForm() throws ObjectNotFoundException, CertitoolsAuthorizationException {
        company = companyService.findAllowed(company.getId(), getUserInSession());
        contracts = (ArrayList<Contract>) contractService.findAllWithUserContractAndPermissionAllowed(company.getId(),
                getUserInSession());                     // roles

        userContractPermissions = new HashMap<Long, List<Long>>();
        for (Contract contract : contracts) {
            contract.setModule(loadModule(contract.getModule().getModuleType()));

            if (contract.getModule().getModuleType().equals(ModuleType.PEI) || contract.getModule().getModuleType()
                    .equals(ModuleType.PRV)|| contract.getModule().getModuleType().equals(ModuleType.PSI)
                    || contract.getModule().getModuleType().equals(ModuleType.GSC)) {
                contract.setContractPermissions(contractService.findContractPermissions(contract.getId()));
                prepareAndSetUserPermissionsForWeb(contract);
            }
        }

        boolean isCertitecna = company.getId().toString()
                .equalsIgnoreCase(Configuration.getInstance().getCertitecnaId());
        roles = (List<Role>) userService.findAllRolesAllowed(isCertitecna, getUserInSession());

        edit = true;
        user = userService.findByIdWithContractsRolesAllowed(user.getId(), getUserInSession());

        // populate roles
        Collection<Role> userRoles = user.getRoles();
        for (Role role : roles) {
            if (userRoles.contains(role)) {
                role.setAssociatedWithUser(true);
            }
        }

        // populate user contracts
        loadContractsAndUserContracts(user.getUserContract());

        setHelpId("#edit-user");
        setAttribute("specialPermission", ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey());

        return new ForwardResolution("/WEB-INF/jsps/certitools/usersInsert.jsp");
    }

    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution resetUserPassword() throws ObjectNotFoundException, CertitoolsAuthorizationException {

        userService.resetUserPasswordAllowed(user, getUserInSession());
        getContext().getMessages().add(new LocalizableMessage("user.resetPassword.sucess"));

        if (contract != null) {
            return new RedirectResolution(UserActionBean.class, "viewUsers").addParameter("company.id", company.getId())
                    .addParameter("letterUser", letterUser)
                    .addParameter("contract.id", contract.getId())
                    .addParameter("user.id", user.getId());
        }

        return new RedirectResolution(UserActionBean.class, "viewUsers").addParameter("company.id", company.getId())
                .addParameter("letterUser", letterUser)
                .addParameter("user.id", user.getId());

    }

    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution exportUsers() throws IOException {
        ByteArrayOutputStream os = userService
                .exportUsers(company == null ? null : company.getId(), contract == null ? null : contract.getId());
        return new DownloadFileResolution("application/vnd.ms-excel", os).setFilename(
                LocalizationUtility.getLocalizedFieldName("users.filename.csv.export", null, null,
                        getContext().getLocale()));
    }

    /**
     * This correctly sets the userContractsForm field, needed when editing the user
     *
     * @param userContracts Set from the user
     */
    private void loadContractsAndUserContracts(Set<UserContract> userContracts) {
        UserContract userContractTemp;
        userContractsForm = new ArrayList<UserContract>();

        for (Contract contractTemp : contracts) {
            userContractTemp = new UserContract();

            for (UserContract userContract : userContracts) {
                if (userContract.getContract().equals(contractTemp)) {
                    userContract.setAssociatedWithUser(true);

                    if (userContract.getValidityStartDate() != null) {
                        userContract.setValidityChanged(true);
                    }
                    userContractTemp = userContract;
                    break;
                }
            }
            userContractsForm.add(userContractTemp);
        }
    }

    private void prepareAndSetUserPermissionsForService() {

        if (user.getUserContract() == null || userContractPermissions == null) {
            return;
        }

        for (UserContract userContract : user.getUserContract()) {
            //Contains key so load permissions
            if (userContractPermissions.containsKey(userContract.getUserContractPK().getIdContract())) {
                List<Permission> permissionList = new ArrayList<Permission>();
                for (Long l : userContractPermissions.get(userContract.getUserContractPK().getIdContract())) {
                    permissionList.add(new Permission(l));

                }
                userContract.setPermissions(permissionList);
            }
        }
    }

    private void prepareAndSetUserPermissionsForWeb(Contract c) {
        if (c.getUserContract() != null) {
            for (UserContract userContract : c.getUserContract()) {
                if (userContract.getUserContractPK().getIdUser() == getUser().getId()) {
                    if (userContract.getPermissions() != null && !userContract.getPermissions().isEmpty()) {
                        List<Long> permissionsIds = new ArrayList<Long>();
                        for (Permission permission : userContract.getPermissions()) {
                            permissionsIds.add(permission.getId());
                        }
                        userContractPermissions.put(c.getId(), permissionsIds);
                    }
                }
            }
        }
    }

    private Module loadModule(ModuleType moduleType) {
        return new Module(moduleType,
                LocalizationUtility.getLocalizedFieldName(moduleType.getKey(), null, null, getContext().getLocale()));
    }

    // getters and setters
    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public ContractService getContractService() {
        return contractService;
    }

    public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public ArrayList<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(ArrayList<Contract> contracts) {
        this.contracts = contracts;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public String getLetterUser() {
        return letterUser;
    }

    public void setLetterUser(String letterUser) {
        this.letterUser = letterUser;
    }

    public String[] getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(String[] alphabet) {
        this.alphabet = alphabet;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ArrayList<UserContract> getUserContractsForm() {
        return userContractsForm;
    }

    public void setUserContractsForm(ArrayList<UserContract> userContractsForm) {
        this.userContractsForm = userContractsForm;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Map<Long, List<Long>> getUserContractPermissions() {
        return userContractPermissions;
    }

    public void setUserContractPermissions(Map<Long, List<Long>> userContractPermissions) {
        this.userContractPermissions = userContractPermissions;
    }
}
