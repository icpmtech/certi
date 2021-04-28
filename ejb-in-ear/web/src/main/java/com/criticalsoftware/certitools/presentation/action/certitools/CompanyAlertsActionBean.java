/*
 * $Id: CompanyAlertsActionBean.java,v 1.3 2010/05/27 15:23:34 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/05/27 15:23:34 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.action.certitools;

import com.criticalsoftware.certitools.business.certitools.CompanyService;
import com.criticalsoftware.certitools.business.certitools.ContractService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.ValidationState;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.NamingException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Description.
 *
 * @author :    jp-gomes
 * @version :   $Revision: 1.3 $
 */

@Secure(roles = "administrator,contractmanager,clientcontractmanager")
public class CompanyAlertsActionBean extends AbstractActionBean implements ValidationErrorHandler {

    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    private Collection<Contract> contracts;
    private Long companyId;
    private String letter;
    private String companyName;

    private Map<Long, Set<Long>> contractListMap;

    @Validate(on = "sendAlerts", required = true, converter = EmailTypeConverter.class)
    private String from;
    @Validate(on = "sendAlerts", required = true, maxlength = 250)
    private String subject;
    @Validate(on = "sendAlerts", required = true, maxlength = 4000)
    private String body;

    @DefaultHandler
    public Resolution viewSendAlertsForm() throws CertitoolsAuthorizationException, ObjectNotFoundException,
            AddressException {

        from = getUserInSession().getEmail();
        loadFormData();

        return new ForwardResolution("/WEB-INF/jsps/certitools/companyAlerts.jsp");
    }

    public Resolution sendAlerts()
            throws BusinessException, NamingException, MessagingException, CertitoolsAuthorizationException {
        int numberOfEmailsSent =
                contractService.sendCompanyAlerts(contractListMap, from, subject, body, getUserInSession(), companyId);
        getContext().getMessages().add(new LocalizableMessage("companies.alerts.success", numberOfEmailsSent));
        return redirectToCompaniesList();
    }

    public Resolution redirectToCompaniesList() {
        return new RedirectResolution(CompanyActionBean.class).addParameter("company.id", companyId)
                .addParameter("letter", letter);
    }

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("sendAlerts")) {
            loadFormData();
            return new ForwardResolution("/WEB-INF/jsps/certitools/companyAlerts.jsp");
        }
        return null;
    }

    @ValidationMethod(on = "sendAlerts", when = ValidationState.ALWAYS)
    public void validateInsertUser() {
        if (contractListMap == null || contractListMap.isEmpty()) {
            getContext().getValidationErrors()
                    .addGlobalError(new LocalizableError("companies.alerts.errors.noPermissionSelected"));
        }
    }

    private void loadFormData() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        contracts = contractService.findAllCompanyContactsWithPermissionAllowed(companyId, getUserInSession());
        companyName = companyService.findAllowed(companyId, getUserInSession()).getName();
    }

    @Override
    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        setHelpId("#alert-entity");
        getMenu().select(MenuItem.Item.MENU_ADMIN, MenuItem.Item.SUB_MENU_ADMIN_COMPANY);
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public ContractService getContractService() {
        return contractService;
    }

    public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public Collection<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(Collection<Contract> contracts) {
        this.contracts = contracts;
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public Map<Long, Set<Long>> getContractListMap() {
        return contractListMap;
    }

    public void setContractListMap(Map<Long, Set<Long>> contractListMap) {
        this.contractListMap = contractListMap;
    }
}
