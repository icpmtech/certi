/*
 * $Id: PlanViewTemplateActionBean.java,v 1.23 2010/06/28 10:21:00 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/06/28 10:21:00 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Plan;
import com.criticalsoftware.certitools.entities.jcr.Resource;
import com.criticalsoftware.certitools.entities.jcr.RiskAnalysisElement;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.entities.jcr.Template11Mirror;
import com.criticalsoftware.certitools.entities.jcr.Template1Diagram;
import com.criticalsoftware.certitools.entities.jcr.Template3RichText;
import com.criticalsoftware.certitools.entities.jcr.Template4PlanClickable;
import com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement;
import com.criticalsoftware.certitools.entities.jcr.Template6DocumentsElement;
import com.criticalsoftware.certitools.entities.jcr.Template9RichTextWithAttach;
import com.criticalsoftware.certitools.entities.jcr.TemplateResource;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.PaginatedListAdapter;
import com.criticalsoftware.certitools.presentation.util.PlanUtils;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.criticalsoftware.certitools.util.TreeNode;
import com.criticalsoftware.certitools.util.Utils;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.ajax.JavaScriptResolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ActionBean to show a node/folder based on the template it has
 *
 * @author pjfsilva
 */
@SuppressWarnings("UnusedDeclaration")
public class PlanViewTemplateActionBean extends DisplayTagSupportActionBean {
    // general fields
    private Long peiId;
    private Plan pei;
    private ArrayList<TreeNode> peiTreeNodes;
    private ArrayList<TreeNode> breadcrumbs;
    private Folder folder;
    private Template template;
    private String pathCM;
    private boolean planManager;
    private String sectionFolder;
    private Folder section;

    // needed for some templates to store the information
    private List<Folder> folders;

    private Integer order;

    //Template5Contacts
    private String template5SearchPhrase;
    private String template5ContactType;
    private List<Folder> template5ExternalContacts;
    private List<Folder> template5InternalContacts;
    private List<Folder> template5EmergencyContacts;
    private String onlineOffline; // may be useful for other templates
    private boolean template5InternalContactsEmpty = false;
    private boolean template5EmergencyContactsEmpty = false;
    private String emailsList;

    private boolean selfImage;

    //Tempate8RiskAnalysis
    private RiskAnalysisElement riskAnalysisElementToFilter;
    private RiskAnalysisElement riskAnalysisElementToLoad;
    private PaginatedListAdapter<RiskAnalysisElement> riskAnalysis;
    private List<String> products;
    private List<String> releaseConditions;
    private List<String> weathers;

    //Template6Documents
    private Template6DocumentsElement documentElementFilter;
    private List<String> documentsTypes;
    private List<String> documentsSubTypes;

    //Template10Procedure
    private List<String> procedureFilters;
    private List<Folder> procedureFirstList;
    private List<Folder> procedureSecondList;
    private List<Folder> procedureThirdList;
    private Resolution resolution;
    private Boolean viewTemplateFragment;

    private String referencedPath;

    //Template12MeansResources
    private List<String> resourcesTypes;
    private String template12SearchPhrase;
    private String template12ResourceType;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @SuppressWarnings({"UnusedDeclaration"})
    private static final Logger LOGGER = Logger.getInstance(PlanViewTemplateActionBean.class);

    /* Loads general data needed for all templates */
    @Before(stages = LifecycleStage.BindingAndValidation,
            on = {"!viewTemplate8RiskAnalysisLoadFiltersList", "!viewTemplate6DocumentsLoadFiltersList",
                    "!viewTemplate10ProcedureFragment"})
    public void loadPEI() throws BusinessException, UnsupportedEncodingException {

        if (referencedPath == null) {
            pei = super.getPEI();
            breadcrumbs = super.getBreadcrumbs();
            peiTreeNodes = super.getPEITreeNodes();
            folder = super.getFolder();
            planManager = super.getPlanManagerInRequest();
            pathCM = super.getPathCM();
            sectionFolder = Utils.encodeURI("/" + PlanUtils.getFolderInPathByIndex(folder.getPath(), 3));
            section = super.getSection();
        }
    }

    public Resolution viewTemplateResource() throws ObjectNotFoundException, JackrabbitException {
        TemplateResource templateResource;

        //Redirected from Template4ClickablePlan or Template1Diagram
        if (selfImage) {
            if (folder.getTemplate() instanceof Template1Diagram) {
                Template1Diagram template = (Template1Diagram) folder.getTemplate();
                return viewResource(template.getResource());

            } else if (folder.getTemplate() instanceof Template4PlanClickable) {
                Template4PlanClickable template = (Template4PlanClickable) folder.getTemplate();
                return viewResource(template.getResource());
            }
        } else {
            templateResource = (TemplateResource) folder.getTemplate();
            return viewResource(templateResource.getResource());
        }
        return null;
    }

    public Resolution viewTemplate1Diagram() throws ObjectNotFoundException, JackrabbitException, BusinessException,
            CertitoolsAuthorizationException, UnsupportedEncodingException {
        if (selfImage) {
            return viewTemplateResource();
        } else {
            if (referencedPath == null) {
                ((Template1Diagram) folder.getTemplate())
                        .setImageMap(parseHTMLImages(((Template1Diagram) folder.getTemplate()).getImageMap()));
            } else {
                ((Template1Diagram) folder.getTemplate())
                        .setImageMap(planService.parseHTMLTemplate11Mirror(folder.getPath(), referencedPath,
                                parseHTMLImages(((Template1Diagram) folder.getTemplate()).getImageMap())));
            }
        }
        resolution = new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp");
        return resolution;
    }

    public Resolution viewTemplate2Index()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {

        folders =
                planService.findIndexTemplateFolders(folder.getPath(), getUserInSession(), getModuleTypeFromEnum());
        //Simplify folder path for frontoffice
        folders = PlanUtils.simplifyFolderPath(folders);
        resolution = new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp");
        return resolution;
    }

    public Resolution viewTemplate3RichText() throws BusinessException, ObjectNotFoundException,
            CertitoolsAuthorizationException, JackrabbitException, UnsupportedEncodingException {
        if (referencedPath == null) {
            ((Template3RichText) folder.getTemplate())
                    .setText(parseHTMLImages(((Template3RichText) folder.getTemplate()).getText()));
        } else {
            ((Template3RichText) folder.getTemplate())
                    .setText(planService.parseHTMLTemplate11Mirror(folder.getPath(), referencedPath,
                            parseHTMLImages(((Template3RichText) folder.getTemplate()).getText())));
        }
        resolution = new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp");
        return resolution;
    }

    public Resolution viewTemplate4PlanClickable() throws ObjectNotFoundException, JackrabbitException,
            BusinessException, CertitoolsAuthorizationException, UnsupportedEncodingException {
        if (selfImage) {
            return viewTemplateResource();
        } else {
            if (referencedPath == null) {
                ((Template4PlanClickable) folder.getTemplate())
                        .setImageMap(parseHTMLImages(((Template4PlanClickable) folder.getTemplate()).getImageMap()));
            } else {
                ((Template4PlanClickable) folder.getTemplate())
                        .setImageMap(planService.parseHTMLTemplate11Mirror(folder.getPath(), referencedPath,
                                parseHTMLImages(((Template4PlanClickable) folder.getTemplate()).getImageMap())));
            }
        }
        resolution = new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp");
        return resolution;
    }

    public Resolution viewTemplate5Contacts()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        if (StringUtils.isEmpty(template5ContactType)) {
            template5ContactType = Template5ContactsElement.ContactType.EXTERNAL_ENTITY.toString();
        }

        folders = planService.findContactsTemplateFolders(folder.getPath(), getUserInSession(), template5SearchPhrase,
                template5ContactType, getModuleTypeFromEnum());

        if (template5ContactType.equals("all")) {
            // to show the contacts in different tables we need to split the folders arraylist
            template5InternalContacts = new ArrayList<Folder>();
            template5ExternalContacts = new ArrayList<Folder>();
            template5EmergencyContacts = new ArrayList<Folder>();

            // split folders
            String contactType;
            for (Folder folderTemp : folders) {
                contactType = ((Template5ContactsElement) folderTemp.getTemplate()).getContactType();
                if (contactType.equals(Template5ContactsElement.ContactType.INTERNAL_PERSON.toString())) {
                    template5InternalContacts.add(folderTemp);
                } else if (contactType.equals(Template5ContactsElement.ContactType.EXTERNAL_ENTITY.toString())) {
                    template5ExternalContacts.add(folderTemp);
                } else if (contactType
                        .equals(Template5ContactsElement.ContactType.EMERGENCY_STRUCTURE_PERSON.toString())) {
                    template5EmergencyContacts.add(folderTemp);
                }
            }
        } else {
            if (template5ContactType.equals(Template5ContactsElement.ContactType.INTERNAL_PERSON.toString())) {
                template5InternalContacts = folders;
            } else if (template5ContactType.equals(Template5ContactsElement.ContactType.EXTERNAL_ENTITY.toString())) {
                template5ExternalContacts = folders;
            } else if (template5ContactType
                    .equals(Template5ContactsElement.ContactType.EMERGENCY_STRUCTURE_PERSON.toString())) {
                template5EmergencyContacts = folders;
            }
        }

        emailsList = createEmailsList();

        folders = PlanUtils.simplifyFolderPath(folders);

        // set offline/online var
        onlineOffline = PlanUtils.getOnlineOfflineFromPath(folder.getPath());

        // check if internal and emergency contacts are empty, so that we don't show that option in the filters
        List<Folder> foldersEmergency = planService
                .findContactsTemplateFolders(folder.getPath(), getUserInSession(), null,
                        Template5ContactsElement.ContactType.EMERGENCY_STRUCTURE_PERSON.toString(),
                        getModuleTypeFromEnum());
        List<Folder> foldersInternal = planService
                .findContactsTemplateFolders(folder.getPath(), getUserInSession(), null,
                        Template5ContactsElement.ContactType.INTERNAL_PERSON.toString(),
                        getModuleTypeFromEnum());

        if (foldersEmergency == null || foldersEmergency.size() <= 0) {
            template5EmergencyContactsEmpty = true;
        }
        if (foldersInternal == null || foldersInternal.size() <= 0) {
            template5InternalContactsEmpty = true;
        }


        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility
                .getLocalizedFieldName("table.template.5ContactsElement.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility
                .getLocalizedFieldName("table.template.5ContactsElement.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility
                .getLocalizedFieldName("table.template.5ContactsElement.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility
                .getLocalizedFieldName("table.template.5ContactsElement.filename.pdf", null, null, locale));
        resolution = new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp");
        return resolution;
    }

    public Resolution viewTemplate5ContactsElement() throws ObjectNotFoundException {
        // TODO jp-gomes needs to analyze this and make it "mirror safe"
        Template5ContactsElement template = (Template5ContactsElement) folder.getTemplate();
        Resource photo = template.getPhoto();

        if (photo == null) {
            throw new ObjectNotFoundException("[viewTemplate5ContactsPhoto] Photo not found",
                    ObjectNotFoundException.Type.FOLDER);
        }

        return viewResource(photo);
    }

    public Resolution viewTemplate6Documents()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException,
            UnsupportedEncodingException {

        /* See backoffice link to uploaded file, so redirect to viewTemplate6DocumentsElement*/
        if (order != null) {
            return viewTemplate6DocumentsElement();
        }

        //Load type list
        documentsTypes = planService
                .findDocumentsTemplateFiltersList(folder.getPath(), getUserInSession(), null, getModuleTypeFromEnum());

        if (documentElementFilter == null) {
            //Set empty documents SubTypes
            documentsSubTypes = new ArrayList<String>();
            documentsSubTypes.add("");
        } else {
            //Load documents SubTypes
            documentsSubTypes = planService
                    .findDocumentsTemplateFiltersList(folder.getPath(), getUserInSession(), documentElementFilter,
                            getModuleTypeFromEnum());
        }

        folders = planService.findDocumentsTemplateFolders(folder.getPath(), getUserInSession(), documentElementFilter,
                isExportRequest(), getModuleTypeFromEnum());
        //Simplify folder path for frontoffice
        folders = PlanUtils.simplifyFolderPath(folders);

        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility
                .getLocalizedFieldName("table.template.6DocumentsElement.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility
                .getLocalizedFieldName("table.template.6DocumentsElement.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility
                .getLocalizedFieldName("table.template.6DocumentsElement.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility
                .getLocalizedFieldName("table.template.6DocumentsElement.filename.pdf", null, null, locale));
        resolution = new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp");
        return resolution;
    }

    public Resolution viewTemplate6DocumentsLoadFiltersList()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        resolution = new JavaScriptResolution(planService.findDocumentsTemplateFiltersList(folder.getPath(),
                getUserInSession(), documentElementFilter, getModuleTypeFromEnum()));
        return resolution;
    }

    public Resolution viewTemplate6DocumentsElement()
            throws UnsupportedEncodingException, BusinessException, ObjectNotFoundException, JackrabbitException {

        Template6DocumentsElement template6DocumentsElement = (Template6DocumentsElement) folder.getTemplate();
        if (order == null || template6DocumentsElement.getResources() == null
                || template6DocumentsElement.getResources().size() < order) {
            throw new BusinessException(
                    "Error when trying to view Template6DocumentsElement. Possible cause: order is missing, resources are null or the resource does not exists");
        }
        return viewResource(template6DocumentsElement.getResources().get(order));
    }

    public Resolution viewTemplate7FAQ()
            throws JackrabbitException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException,
            UnsupportedEncodingException {
        folders = planService.findFAQTemplateFolders(folder.getPath(), getUserInSession(), getModuleTypeFromEnum());
        resolution = new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp");
        return resolution;
    }

    public Resolution viewTemplate8RiskAnalysis() throws JackrabbitException, BusinessException {
        PaginatedListWrapper<RiskAnalysisElement> wrapper =
                new PaginatedListWrapper<RiskAnalysisElement>(getPage(),
                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(), getDirOrder(),
                        isExportRequest());

        String pathToLoad;
        if (referencedPath != null) {
            pathToLoad = referencedPath;
        } else {
            pathToLoad = folder.getPath();
        }
        riskAnalysis =
                new PaginatedListAdapter<RiskAnalysisElement>(
                        planService.findRiskAnalysis(wrapper, folder.getPath(), pathToLoad,
                                riskAnalysisElementToFilter));

        products = planService.findRiskAnalysisList(pathToLoad, null, false);
        //Only show products loaded
        if (riskAnalysisElementToFilter == null) {
            releaseConditions = new ArrayList<String>();
            releaseConditions.add("");
            weathers = new ArrayList<String>();
            weathers.add("");
        } else {
            if (riskAnalysisElementToFilter.getProduct() != null) {
                RiskAnalysisElement temp = new RiskAnalysisElement();
                temp.setProduct(riskAnalysisElementToFilter.getProduct());
                releaseConditions = planService.findRiskAnalysisList(pathToLoad, temp, false);
            } else {
                releaseConditions = new ArrayList<String>();
                releaseConditions.add("");
            }
            if (riskAnalysisElementToFilter.getReleaseConditions() != null) {
                RiskAnalysisElement temp = new RiskAnalysisElement();
                temp.setProduct(riskAnalysisElementToFilter.getProduct());
                temp.setReleaseConditions(riskAnalysisElementToFilter.getReleaseConditions());
                weathers = planService.findRiskAnalysisList(pathToLoad, temp, false);
            } else {
                weathers = new ArrayList<String>();
                weathers.add("");
            }
        }
        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility
                .getLocalizedFieldName("table.template.8RiskAnalysis.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility
                .getLocalizedFieldName("table.template.8RiskAnalysis.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility
                .getLocalizedFieldName("table.template.8RiskAnalysis.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility
                .getLocalizedFieldName("table.template.8RiskAnalysis.filename.pdf", null, null, locale));

        resolution = new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp");
        return resolution;
    }

    public Resolution viewTemplate8RiskAnalysisLoadFiltersList()
            throws JackrabbitException, UnsupportedEncodingException {
        String pathToLoad;
        if (referencedPath != null) {
            pathToLoad = referencedPath;
        } else {
            pathToLoad = folder.getPath();
        }
        resolution = new JavaScriptResolution(
                planService.findRiskAnalysisList(Utils.decodeURI(pathToLoad), riskAnalysisElementToLoad, true));
        return resolution;
    }

    public Resolution viewTemplate9RichTextWithAttach()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException,
            UnsupportedEncodingException {
        if (referencedPath == null) {
            ((Template9RichTextWithAttach) folder.getTemplate())
                    .setText(parseHTMLImages(((Template9RichTextWithAttach) folder.getTemplate()).getText()));
        } else {
            ((Template9RichTextWithAttach) folder.getTemplate())
                    .setText(planService.parseHTMLTemplate11Mirror(folder.getPath(), referencedPath,
                            parseHTMLImages(((Template9RichTextWithAttach) folder.getTemplate()).getText())));
        }
        folders = planService
                .findRichTextWithAttachTemplateFolders(folder.getPath(), getUserInSession(), getModuleTypeFromEnum());
        folders = PlanUtils.simplifyFolderPath(folders);
        resolution = new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp");
        return resolution;
    }

    public Resolution viewTemplate10Procedure()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        loadTemplate10ProcedureLists();
        setAttribute("firstLoad", true);
        resolution = new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp");
        return resolution;
    }

    public Resolution viewTemplate10ProcedureFragment()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        loadTemplate10ProcedureLists();
        return new RedirectResolution(PlanActionBean.class, "viewResource")
                .addParameter("viewTemplateFragment", true)
                .addParameter("peiId",
                        PlanUtils.getContractNumberByPath(procedureFilters.get(procedureFilters.size() - 1)))
                .addParameter("path", PlanUtils.simplifyPath(procedureFilters.get(procedureFilters.size() - 1)))
                .addParameter("procedureFilters", procedureFilters)
                .addParameter("planModuleType", getPlanModuleType());
    }

    public Resolution viewTemplate11Mirror()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException,
            UnsupportedEncodingException {
        Template11Mirror template11Mirror = (Template11Mirror) folder.getTemplate();
        String pathToLoad = template11Mirror.getSourcePath();

        if (PlanUtils.getOnlineOfflineFromPath(folder.getPath()).equals("online")) {
            pathToLoad = pathToLoad.replaceFirst("offline", "online");
        }
        Folder referencedFolder =
                planService.findFolderAllAllowed(pathToLoad, false);
        referencedPath = referencedFolder.getPath();

        folder.setTemplate(referencedFolder.getTemplate());
        return new ForwardResolution(PlanViewTemplateActionBean.class,
                "view" + referencedFolder.getTemplate().getName()).addParameter("referencedPath", referencedPath);
    }

    public Resolution viewTemplate12MeansResources()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException,
            UnsupportedEncodingException {

        //Load type list
        resourcesTypes = planService.findMeansResourcesTemplateFiltersList(folder.getPath(),
                getUserInSession(), getModuleTypeFromEnum());

        folders = planService.findMeansResourcesTemplateFolders(folder.getPath(), getUserInSession(),
                template12SearchPhrase, template12ResourceType, getModuleTypeFromEnum());
        //Simplify folder path for frontoffice
        folders = PlanUtils.simplifyFolderPath(folders);

        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility
                .getLocalizedFieldName("table.template.12MeansResourcesElement.filename.xls", null, null, locale));
        setExportCSV(LocalizationUtility
                .getLocalizedFieldName("table.template.12MeansResourcesElement.filename.csv", null, null, locale));
        setExportXML(LocalizationUtility
                .getLocalizedFieldName("table.template.12MeansResourcesElement.filename.xml", null, null, locale));
        setExportPDF(LocalizationUtility
                .getLocalizedFieldName("table.template.12MeansResourcesElement.filename.pdf", null, null, locale));
        resolution = new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp");
        return resolution;
    }

    public Resolution viewTemplate12MeansResourcesElement() {
        folder.setTemplate(new Template("TemplateNoNavigable"));
        return new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @After(stages = LifecycleStage.EventHandling, on = {"!viewTemplate10ProcedureFragment", "!viewTemplate11Mirror"})
    public Resolution redirect()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        if (viewTemplateFragment != null && viewTemplateFragment) {
            loadTemplate10ProcedureLists();
            return new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate10ProcedureFragment.jsp");
        } else {
            return resolution;
        }
    }

    private void loadTemplate10ProcedureLists()
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {

        //Load first List
        if (procedureFilters == null) {
            //First load
            procedureFilters = new ArrayList<String>();
            procedureFirstList =
                    planService.findProcedureTemplateFiltersList(folder.getPath(), getUserInSession(),
                            getModuleTypeFromEnum());

            if (!procedureFirstList.isEmpty()) {
                procedureFilters.add(0, procedureFirstList.get(0).getPath());
            } else {
                procedureSecondList = new ArrayList<Folder>();
                procedureThirdList = new ArrayList<Folder>();
                return;
            }

        } else {
            procedureFirstList =
                    planService.findProcedureTemplateFiltersList(PlanUtils.getParentPath(procedureFilters.get(0)),
                            getUserInSession(), getModuleTypeFromEnum());
        }
        //Load second List
        procedureSecondList =
                planService.findProcedureTemplateFiltersList(procedureFilters.get(0), getUserInSession(),
                        getModuleTypeFromEnum());

        if (procedureFilters.size() < 2) {
            if (!procedureSecondList.isEmpty()) {
                procedureFilters.add(1, procedureSecondList.get(0).getPath());
            } else {
                procedureThirdList = new ArrayList<Folder>();
                return;
            }
        }
        procedureThirdList =
                planService.findProcedureTemplateFiltersList(procedureFilters.get(1), getUserInSession(),
                        getModuleTypeFromEnum());

        if (procedureFilters.size() != 3) {
            if (!procedureThirdList.isEmpty()) {
                procedureFilters.add(2, procedureThirdList.get(0).getPath());
            }
        }
    }

    private StreamingResolution viewResource(Resource resource) {
        getContext().getResponse().setHeader("Expires", "0");
        getContext().getResponse().setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        getContext().getResponse().setHeader("Pragma", "public");
        getContext().getResponse().setCharacterEncoding("UTF-8");
        resolution =
                new StreamingResolution(resource.getMimeType(), resource.getData()).setFilename(resource.getName());
        return (StreamingResolution) resolution;
    }

    public void fillLookupFields() {
        // empty on purpose, not useful
    }

    private String createEmailsList() {
        List<String> emails = new ArrayList<String>();
        addEmailsToList(emails, template5ExternalContacts);
        addEmailsToList(emails, template5InternalContacts);
        addEmailsToList(emails, template5EmergencyContacts);
        if (emails.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (String email : emails) {
            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(email);
        }
        return sb.toString();
    }

    private void addEmailsToList(List<String> emails, List<Folder> folders) {
        if (folders != null) {
            for (Folder f : folders) {
                Template5ContactsElement template = (Template5ContactsElement) f.getTemplate();
                if (template.getEmail() != null && !template.getEmail().isEmpty()) {
                    String email = template.getEmail().trim();
                    if (!email.isEmpty() && !emails.contains(email)) {
                        emails.add(email);
                    }
                }
            }
        }
    }

    private String parseHTMLImages(String text) {
        if (!getUserPEIPreview() && text != null) {

            String regexpIMG = "<img[^>]+ src=\"/plan/Plan.action(\\?viewResource=|\\?planModuleType=[A-Z]+(&|&amp;)viewResource=)([^\">]+)\"";
            Pattern patt = Pattern.compile(regexpIMG,
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.COMMENTS);
            Matcher m = patt.matcher(text);
            String imgSrc, imgTag;
            StringBuffer sb = new StringBuffer();

            while (m.find()) {
                imgTag = deleteNewlines(m.group(0));
                imgSrc = deleteNewlines(m.group(3));
                if (StringUtils.contains(imgSrc, "peiViewOffline=true")) {
                    // in case it's well formed HTML or not
                    imgTag = imgTag.replace("&amp;peiViewOffline=true", "");
                    imgTag = imgTag.replace("&peiViewOffline=true", "");
                    m.appendReplacement(sb, imgTag);
                }
            }
            m.appendTail(sb);

            return sb.toString();
        }
        return text;
    }

    private String deleteNewlines(String text) {
        text = text.replaceAll("\\n", "");
        text = text.replaceAll("\\t", "");
        text = text.replaceAll("\\r", "");

        return text;
    }

    public Long getPeiId() {
        return peiId;
    }

    public void setPeiId(Long peiId) {
        this.peiId = peiId;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Plan getPei() {
        return pei;
    }

    public ArrayList<TreeNode> getPeiTreeNodes() {
        return peiTreeNodes;
    }

    public ArrayList<TreeNode> getBreadcrumbs() {
        return breadcrumbs;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public PlanService getPeiService() {
        return planService;
    }

    public void setPeiService(PlanService planService) {
        this.planService = planService;
    }

    public PaginatedListAdapter<RiskAnalysisElement> getRiskAnalysis() {
        return riskAnalysis;
    }

    public void setRiskAnalysis(PaginatedListAdapter<RiskAnalysisElement> riskAnalysis) {
        this.riskAnalysis = riskAnalysis;
    }

    public RiskAnalysisElement getRiskAnalysisElementToFilter() {
        return riskAnalysisElementToFilter;
    }

    public void setRiskAnalysisElementToFilter(RiskAnalysisElement riskAnalysisElementToFilter) {
        this.riskAnalysisElementToFilter = riskAnalysisElementToFilter;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getTemplate5SearchPhrase() {
        return template5SearchPhrase;
    }

    public void setTemplate5SearchPhrase(String template5SearchPhrase) {
        this.template5SearchPhrase = template5SearchPhrase;
    }

    public String getTemplate5ContactType() {
        return template5ContactType;
    }

    public void setTemplate5ContactType(String template5ContactType) {
        this.template5ContactType = template5ContactType;
    }

    public List<Folder> getTemplate5ExternalContacts() {
        return template5ExternalContacts;
    }

    public void setTemplate5ExternalContacts(List<Folder> template5ExternalContacts) {
        this.template5ExternalContacts = template5ExternalContacts;
    }

    public List<Folder> getTemplate5InternalContacts() {
        return template5InternalContacts;
    }

    public void setTemplate5InternalContacts(List<Folder> template5InternalContacts) {
        this.template5InternalContacts = template5InternalContacts;
    }

    public List<Folder> getTemplate5EmergencyContacts() {
        return template5EmergencyContacts;
    }

    public void setTemplate5EmergencyContacts(List<Folder> template5EmergencyContacts) {
        this.template5EmergencyContacts = template5EmergencyContacts;
    }

    public boolean isSelfImage() {
        return selfImage;
    }

    public void setSelfImage(boolean selfImage) {
        this.selfImage = selfImage;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public List<String> getReleaseConditions() {
        return releaseConditions;
    }

    public void setReleaseConditions(List<String> releaseConditions) {
        this.releaseConditions = releaseConditions;
    }

    public List<String> getWeathers() {
        return weathers;
    }

    public void setWeathers(List<String> weathers) {
        this.weathers = weathers;
    }

    public boolean isPlanManager() {
        return planManager;
    }

    public void setPlanManager(boolean planManager) {
        this.planManager = planManager;
    }

    public String getPathCM() {
        return pathCM;
    }

    public void setPathCM(String pathCM) {
        this.pathCM = pathCM;
    }

    public String getSectionFolder() {
        return sectionFolder;
    }

    public void setSectionFolder(String sectionFolder) {
        this.sectionFolder = sectionFolder;
    }

    public Folder getSection() {
        return section;
    }

    public void setSection(Folder section) {
        this.section = section;
    }

    public RiskAnalysisElement getRiskAnalysisElementToLoad() {
        return riskAnalysisElementToLoad;
    }

    public void setRiskAnalysisElementToLoad(RiskAnalysisElement riskAnalysisElementToLoad) {
        this.riskAnalysisElementToLoad = riskAnalysisElementToLoad;
    }

    public Template6DocumentsElement getDocumentElementFilter() {
        return documentElementFilter;
    }

    public void setDocumentElementFilter(Template6DocumentsElement documentElementFilter) {
        this.documentElementFilter = documentElementFilter;
    }

    public List<String> getDocumentsTypes() {
        return documentsTypes;
    }

    public void setDocumentsTypes(List<String> documentsTypes) {
        this.documentsTypes = documentsTypes;
    }

    public List<String> getDocumentsSubTypes() {
        return documentsSubTypes;
    }

    public void setDocumentsSubTypes(List<String> documentsSubTypes) {
        this.documentsSubTypes = documentsSubTypes;
    }

    public List<String> getProcedureFilters() {
        return procedureFilters;
    }

    public void setProcedureFilters(List<String> procedureFilters) {
        this.procedureFilters = procedureFilters;
    }

    public List<Folder> getProcedureFirstList() {
        return procedureFirstList;
    }

    public void setProcedureFirstList(List<Folder> procedureFirstList) {
        this.procedureFirstList = procedureFirstList;
    }

    public List<Folder> getProcedureSecondList() {
        return procedureSecondList;
    }

    public void setProcedureSecondList(List<Folder> procedureSecondList) {
        this.procedureSecondList = procedureSecondList;
    }

    public List<Folder> getProcedureThirdList() {
        return procedureThirdList;
    }

    public void setProcedureThirdList(List<Folder> procedureThirdList) {
        this.procedureThirdList = procedureThirdList;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
    }

    public Boolean getViewTemplateFragment() {
        return viewTemplateFragment;
    }

    public void setViewTemplateFragment(Boolean viewTemplateFragment) {
        this.viewTemplateFragment = viewTemplateFragment;
    }

    public PlanService getPlanService() {
        return planService;
    }

    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }

    public String getReferencedPath() {
        return referencedPath;
    }

    public void setReferencedPath(String referencedPath) {
        this.referencedPath = referencedPath;
    }

    public String getOnlineOffline() {
        return onlineOffline;
    }

    public void setOnlineOffline(String onlineOffline) {
        this.onlineOffline = onlineOffline;
    }

    public boolean isTemplate5InternalContactsEmpty() {
        return template5InternalContactsEmpty;
    }

    public void setTemplate5InternalContactsEmpty(boolean template5InternalContactsEmpty) {
        this.template5InternalContactsEmpty = template5InternalContactsEmpty;
    }

    public boolean isTemplate5EmergencyContactsEmpty() {
        return template5EmergencyContactsEmpty;
    }

    public void setTemplate5EmergencyContactsEmpty(boolean template5EmergencyContactsEmpty) {
        this.template5EmergencyContactsEmpty = template5EmergencyContactsEmpty;
    }

    public List<String> getResourcesTypes() {
        return resourcesTypes;
    }

    public void setResourcesTypes(List<String> resourcesTypes) {
        this.resourcesTypes = resourcesTypes;
    }

    public String getTemplate12SearchPhrase() {
        return template12SearchPhrase;
    }

    public void setTemplate12SearchPhrase(String template12SearchPhrase) {
        this.template12SearchPhrase = template12SearchPhrase;
    }

    public String getTemplate12ResourceType() {
        return template12ResourceType;
    }

    public void setTemplate12ResourceType(String template12ResourceType) {
        this.template12ResourceType = template12ResourceType;
    }

    public String getEmailsList() {
        return emailsList;
    }

    public void setEmailsList(String emailsList) {
        this.emailsList = emailsList;
    }
}
