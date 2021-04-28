/*
 * $Id: CompanyActionBean.java,v 1.37 2012/06/01 13:51:51 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2012/06/01 13:51:51 $
 * Last changed by $Author: d-marques $
 */
package com.criticalsoftware.certitools.presentation.action.certitools;

import com.criticalsoftware.certitools.business.certitools.*;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ImportException;
import com.criticalsoftware.certitools.business.exception.IsReferencedException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.entities.*;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.ValidationUtils;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.Utils;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * Company Management Action Bean
 *
 * @author pjfsilva
 */
public class CompanyActionBean extends DisplayTagSupportActionBean implements ValidationErrorHandler {
    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    @EJBBean(value = "certitools/CountryService")
    private CountryService countryService;

    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    @EJBBean(value = "certitools/ModuleService")
    private ModuleService moduleService;

    @EJBBean(value = "certitools/UserService")
    private UserService userService;

    // Company related fields
    // this is used to store the company details (right column) or the data in inserts/edits
    @ValidateNestedProperties({
        @Validate(field = "id", required = true, on = "import"),
        @Validate(field = "name", required = true, maxlength = 255, on = "insertCompany"),
        @Validate(field = "address", maxlength = 255, on = "insertCompany"),
        @Validate(field = "country.id", maxlength = 5, on = "insertCompany"),
        @Validate(field = "phone", maxlength = 32, on = "insertCompany"),
        @Validate(field = "fiscalNumber", maxvalue = 999999999999L, on = "insertCompany"),
        @Validate(field = "language", maxlength = 5, on = "insertCompany"),
        //TODO-MODULE
        //CERTOOL-539
        @Validate(field = "peiLabelPT", maxlength = 64),
        @Validate(field = "prvLabelPT", maxlength = 64),
        @Validate(field = "psiLabelPT", maxlength = 64),
        @Validate(field = "gscLabelPT", maxlength = 64),
        @Validate(field = "peiLabelEN", maxlength = 64),
        @Validate(field = "prvLabelEN", maxlength = 64),
        @Validate(field = "psiLabelEN", maxlength = 64),
        @Validate(field = "gscLabelEN", maxlength = 64)
            })
    private Company company;

    // this is used to show the left column list
    private List<Company> companies;

    private boolean edit = false;

    private Collection<Country> countries;

    // selected letter
    private String letter = StringUtils
            .upperCase(com.criticalsoftware.certitools.util.Configuration.getInstance().getCompaniesDefaultView());

    // used to build the links
    private String[] alphabet =
            {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
                    "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private String error = null;

    private String searchPhrase;

    @Validate(required = true, on = "importUsers")
    private FileBean importFile;

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(MenuItem.Item.MENU_ADMIN, MenuItem.Item.SUB_MENU_ADMIN_COMPANY);
    }

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertCompany")) {
            return insertCompanyForm();
        }
        return null;
    }

    /**
     * How can this event be called (parameters):
     * <p/>
     * if company.id is defined and letter is null - a new company was inserted, show only the company page if
     * company.id and letter are defined - user canceled an edit - show the page where he was if letter is set - show
     * companies for that letter if company.id and letter are NOT defined - show the default letter companies
     *
     * @return Resolution
     *
     * @throws CertitoolsAuthorizationException
     *          in case of access to an unauthorized company
     * @throws ObjectNotFoundException
     *          in case some information can not be found
     */
    @DefaultHandler
    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution viewCompanies() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        User userInSession = getUserInSession();

        // set errors if they exist
        // input sanitization: check if errors is in a pre-defined whitelist
        if (error != null) {
            String[] whitelistErrors = {"companies.del.isReferenced.contract", "companies.del.isReferenced.user",
                    "contract.del.isReferenced.user", "contract.del.isReferenced.plan"};
            if (ArrayUtils.contains(whitelistErrors, error)) {
                getContext().getValidationErrors().addGlobalError(new LocalizableError(error));
            }
        }

        // export options
        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility.getLocalizedFieldName("companies.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility.getLocalizedFieldName("companies.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility.getLocalizedFieldName("companies.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility.getLocalizedFieldName("companies.filename.pdf", null, null, locale));

        // user selected a company, so we must get it
        if (company != null && company.getId() != null) {
            company = companyService
                    .findByIdWithContractsAllowed(company.getId(), "contracts.active DESC", userInSession);
            for (Contract contract : company.getContracts()) {
                contract.setModule(loadModule(contract.getModule().getModuleType()));
            }
        }

        // get list of companies
        if (isExportRequest()) {
            companies = (List<Company>) companyService.findAllAuthorized(userInSession);
        } else {

            // user searched? if yes set the page to "ALL" and show the search results
            if (searchPhrase != null) {
                companies = (List<Company>) companyService.findByName(searchPhrase, userInSession);
                letter = "";
            } else {

                if (!ValidationUtils.validateNavigationLetter(letter)) {
                    letter = "";
                }

                // if we selected 1 company without a letter, show only the company
                if (company != null && company.getId() != null && (letter == null || letter.equals(""))) {
                    // no need to check the roles as the company is already roles allowed filtered
                    letter = "";
                    List<Company> companiesListTemp = new ArrayList<Company>();
                    companiesListTemp.add(company);
                    companies = companiesListTemp;
                } else {
                    companies = (List<Company>) companyService.findAllAuthorizedByStartLetter(letter, userInSession);
                }
            }
        }
        setHelpId("#entities-management");
        return new ForwardResolution("/WEB-INF/jsps/certitools/companies.jsp");
    }

    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution searchCompany() throws ObjectNotFoundException, CertitoolsAuthorizationException {
        if (searchPhrase == null) {
            searchPhrase = "";
        }

        return viewCompanies();
    }

    // ajax called
    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution viewCompanyFragment()
            throws ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException {
        company = companyService
                .findByIdWithContractsAllowed(company.getId(), "contracts.active DESC", getUserInSession());

        for (Contract contract : company.getContracts()) {
            contract.setModule(loadModule(contract.getModule().getModuleType()));
            if (contract.getUserRegisterCode() != null){
                contract.setUserRegisterCode(Utils.encryptMD5(contract.getUserRegisterCode()));
            }
        }

        getContext().getResponse().setHeader("Stripes-Success", "OK");
        return new ForwardResolution("/WEB-INF/jsps/certitools/companyFragment.jsp");
    }

    @Secure(roles = "contractmanager")
    public Resolution updateCompanyForm() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        edit = true;
        company = companyService.findAllowed(company.getId(), getUserInSession());
        countries = countryService.findAll();
        setHelpId("#edit-entity");

        return new ForwardResolution("/WEB-INF/jsps/certitools/companiesInsert.jsp");
    }

    @Secure(roles = "contractmanager")
    public Resolution insertCompanyForm() {
        company = new Company();
        company.setShowFullListPEI(true);
        countries = countryService.findAll();

        setHelpId("#add-entity");

        return new ForwardResolution("/WEB-INF/jsps/certitools/companiesInsert.jsp");
    }

    @Secure(roles = "contractmanager")
    public Resolution insertCompany() throws ObjectNotFoundException, JackrabbitException {
        if (edit) {
            companyService.updateCompany(company);
        } else {
            companyService.insertCompany(company);
            letter = "";
        }
        getContext().getMessages().add(new LocalizableMessage("companies.add.sucess"));
        return new RedirectResolution(CompanyActionBean.class)
                .addParameter("letter", letter)
                .addParameter("company.id", company.getId());
    }

    @Secure(roles = "contractmanager")
    public Resolution deleteCompany() throws ObjectNotFoundException {

        try {
            companyService.deleteCompany(company);
            getContext().getMessages().add(new LocalizableMessage("companies.del.sucess"));

        } catch (IsReferencedException e) {

            if (e.getType() == IsReferencedException.Type.CONTRACT) {

                return new RedirectResolution(CompanyActionBean.class, "viewCompanies")
                        .addParameter("company.id", company.getId()).addParameter("letter", letter)
                        .addParameter("error", "companies.del.isReferenced.contract");

            } else if (e.getType() == IsReferencedException.Type.USER) {

                return new RedirectResolution(CompanyActionBean.class, "viewCompanies")
                        .addParameter("company.id", company.getId()).addParameter("letter", letter)
                        .addParameter("error", "companies.del.isReferenced.user");
            }
        }

        // in the special that the user was seeing only 1 user and deleted it, set the letterUser to A
        //if (letter == null || letter.equals("")) {
        //    letter = "A";
        //}
        return new RedirectResolution(CompanyActionBean.class).addParameter("letter", letter);
    }

    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution importUsersForm() throws ObjectNotFoundException, CertitoolsAuthorizationException {
        setHelpId("#import-users");
        setCompany(companyService.findByIdWithContractsAllowed(company.getId(), null, getUserInSession()));
        setLetter(letter);
        return new ForwardResolution("/WEB-INF/jsps/certitools/usersImport.jsp");
    }

    @Secure(roles = "administrator,contractmanager,clientcontractmanager")
    public Resolution importUsers() throws IOException, ObjectNotFoundException {
        setHelpId("#import-users");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("company.id", company.getId());
        params.put("letter", letter);
        try {
            int i = userService.importUsers(company.getId(), importFile.getInputStream());
            importFile.delete();
            getContext().getMessages().add(new LocalizableMessage("users.import.success", i));
            return new RedirectResolution(CompanyActionBean.class).addParameters(params);
        } catch (ImportException e) {
            importFile.delete();
            getContext().getValidationErrors()
                    .add("importFile", new LocalizableError(e.getMessageResource(), e.getLineNumberError(),
                            e.getEmail(), e.getAux()));
            return new ForwardResolution("/WEB-INF/jsps/certitools/usersImport.jsp").addParameters(params);
        }
    }

    @ValidationMethod(on = "importUsers", when = ValidationState.NO_ERRORS)
    public void validateImport() {
        if (!importFile.getFileName().endsWith(".csv")) {
            getContext().getValidationErrors().add("importFile", new LocalizableError("error.users.import.file.csv"));
        }
    }


    private Module loadModule(ModuleType moduleType) {
        return new Module(moduleType,
                LocalizationUtility.getLocalizedFieldName(moduleType.getKey(), null, null, getContext().getLocale()));
    }

    // Getters and Setters
    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public Company getCompany() {
        return company;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public boolean isEdit() {
        return edit;
    }

    public String[] getAlphabet() {
        return alphabet;
    }

    public Collection<Country> getCountries() {
        return countries;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public ModuleService getModuleService() {
        return moduleService;
    }

    public void setModuleService(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    public ContractService getContractService() {
        return contractService;
    }

    public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }

    public FileBean getImportFile() {
        return importFile;
    }

    public void setImportFile(FileBean importFile) {
        this.importFile = importFile;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}