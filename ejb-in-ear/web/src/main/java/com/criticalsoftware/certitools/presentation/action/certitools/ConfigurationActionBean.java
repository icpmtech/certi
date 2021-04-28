/*
 * $Id: ConfigurationActionBean.java,v 1.16 2013/12/17 19:31:15 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2013/12/17 19:31:15 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.certitools;

import com.criticalsoftware.certitools.business.certitools.ConfigurationService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.Configuration;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.ValidationState;
import org.apache.jackrabbit.ocm.nodemanagement.impl.RepositoryUtil;

import javax.jcr.*;
import javax.mail.internet.InternetAddress;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * List Configuration
 *
 * @author lt-rico
 */
public class ConfigurationActionBean extends DisplayTagSupportActionBean {

    @EJBBean(value = "certitools/ConfigurationService")
    private ConfigurationService configurationService;

    private ArrayList<Configuration> configurations;

    private String masterPasswordAuthor;
    private boolean masterPasswordActive;
    private Date masterPasswordExpiry;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    // TEST
    public FileBean templateFile;

    @Secure(roles = "administrator")
    @DontValidate
    @DefaultHandler
    public Resolution list() {
        setConfigurations((ArrayList<Configuration>) configurationService.findAll());

        // master password
        Long masterPasswordExpiryDate =
                com.criticalsoftware.certitools.util.Configuration.getInstance().getMasterPasswordExpiryDate();

        if (masterPasswordExpiryDate != null && System.currentTimeMillis() / 1000 < masterPasswordExpiryDate) {
            masterPasswordActive = true;
            masterPasswordExpiry = new Date(masterPasswordExpiryDate * 1000);
        } else {
            masterPasswordActive = false;
        }

        masterPasswordAuthor =
                com.criticalsoftware.certitools.util.Configuration.getInstance().getMasterPasswordAuthor();

        setHelpId("#configuration");
        return new ForwardResolution("/WEB-INF/jsps/certitools/configurationList.jsp");
    }

    @Secure(roles = "administrator")
    @DontValidate
    public Resolution updateForm() {
        setConfigurations((ArrayList<Configuration>) configurationService.findAll());

        setHelpId("#edit-configuration");
        return new ForwardResolution("/WEB-INF/jsps/certitools/configurationUpdate.jsp");
    }

    @Secure(roles = "administrator")
    @Validate
    public Resolution update() throws BusinessException, ObjectNotFoundException {
        configurationService.update(configurations);
        getContext().getMessages().add(new LocalizableMessage("message.configuration.success"));
        return new RedirectResolution(this.getClass());
    }

    @Secure(roles = "administrator")
    @DontValidate
    public Resolution cancel() {
        return new RedirectResolution(this.getClass());
    }

    @ValidationMethod(on = "update", when = ValidationState.NO_ERRORS)
    public void validate() {
        ResourceBundle resources = ResourceBundle.getBundle("StripesResources", getContext().getLocale());
        if (configurations != null && configurations.size() > 0) {
            int index = 0;
            for (Configuration conf : configurations) {
                if (conf.getValue() == null || conf.getValue().length() == 0) {
                    getContext().getValidationErrors().add("configurations[" + index + "].value",
                            new LocalizableError("custom.validation.required.valueNotPresent",
                                    resources.getString("configuration." + conf.getKey())));
                } else {
                    try {
                        if (conf.getClassName().equals(Integer.class.getCanonicalName())) {
                            Integer.parseInt(conf.getValue());
                        }
                        if (conf.getClassName().equals(InternetAddress.class.getCanonicalName())) {
                            InternetAddress address = new InternetAddress(conf.getValue());
                            String result = address.getAddress();
                            if (!result.contains("@")) {
                                throw new Exception();
                            }
                        }
                    } catch (Exception e) {

                        getContext().getValidationErrors().add("configurations[" + index + "].value",
                                new LocalizableError("error.configuration.invalid.value",
                                        resources.getString("configuration." + conf.getKey())));
                    }

                    if (conf.getValue().length() > 2048) {
                        getContext().getValidationErrors().add("configurations[" + index + "].value",
                                new LocalizableError("error.configuration.maxlenght.exceed",
                                        resources.getString("configuration." + conf.getKey())));
                    }
                }
                index++;
            }
        }
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(MenuItem.Item.MENU_ADMIN, MenuItem.Item.SUB_MENU_ADMIN_CONFIG);
    }

    @Secure(roles = "administrator")
    @DontValidate
    public Resolution hiddenAdmin() {
        return new ForwardResolution("/WEB-INF/jsps/certitools/hiddenAdmin.jsp");
    }

    @Secure(roles = "administrator")
    @DontValidate
    public Resolution export() {
        String filepath = "D:\\lebre\\export1\\export.xml";
        File f = new File(filepath);

        try {
            Repository repository = (Repository) new InitialContext().lookup("java:jcr/local");
            Session session = RepositoryUtil.login(repository, "superuser", "");
            FileOutputStream os = new FileOutputStream(f);
            //export all including binary, recursive
            session.exportSystemView("/certitools_plan_root/PSI1", os, false, false);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NamingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (PathNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return new ForwardResolution("/WEB-INF/jsps/certitools/hiddenAdmin.jsp");
    }

    @Secure(roles = "administrator")
    @DontValidate
    public Resolution importForm() {
        return new ForwardResolution("/WEB-INF/jsps/plan/planCMMigrationImport.jsp");
    }

    @Secure(roles = "administrator")
    @DontValidate
    public Resolution importInsert() {
        try {
            Repository repository = (Repository) new InitialContext().lookup("java:jcr/local");
            Session session = RepositoryUtil.login(repository, "superuser", "");

            session.importXML("/certitools_plan_root/PSI2", templateFile.getInputStream(), ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
            session.save();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NamingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        getContext().getMessages().add(new LocalizableMessage("message.configuration.success"));
        return new RedirectResolution(this.getClass(), "importForm");
    }

    public Resolution forceDeleteFolderLinks() {
        String folderPath = masterPasswordAuthor;
        try {
            planService.deletePlanFolderReference(folderPath);
            getContext().getMessages().add(new LocalizableMessage("message.configuration.success"));

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return new RedirectResolution(this.getClass(), "hiddenAdmin");
    }

    @Secure(roles = "administrator")
    @DontValidate
    public Resolution reloadConfiguration(){
        com.criticalsoftware.certitools.util.Configuration.reloadInstance();
        getContext().getMessages().add(new LocalizableMessage("message.configuration.success"));
        System.out.println("Configuration reloaded");
        return new RedirectResolution(this.getClass());
    }

    public ArrayList<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(ArrayList<Configuration> configurations) {
        this.configurations = configurations;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public String getMasterPasswordAuthor() {
        return masterPasswordAuthor;
    }

    public void setMasterPasswordAuthor(String masterPasswordAuthor) {
        this.masterPasswordAuthor = masterPasswordAuthor;
    }

    public boolean isMasterPasswordActive() {
        return masterPasswordActive;
    }

    public void setMasterPasswordActive(boolean masterPasswordActive) {
        this.masterPasswordActive = masterPasswordActive;
    }

    public Date getMasterPasswordExpiry() {
        return masterPasswordExpiry;
    }

    public void setMasterPasswordExpiry(Date masterPasswordExpiry) {
        this.masterPasswordExpiry = masterPasswordExpiry;
    }

    public PlanService getPlanService() {
        return planService;
    }

    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }
}
