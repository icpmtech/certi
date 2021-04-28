/*
 * $Id: PlanActionBean.java,v 1.25 2012/10/22 12:10:44 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/10/22 12:10:44 $
 * Last changed by : $Author: pjfsilva $
 */

package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.certitools.CompanyService;
import com.criticalsoftware.certitools.business.certitools.ContractService;
import com.criticalsoftware.certitools.business.certitools.UserService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.Company;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.Module;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Plan;
import com.criticalsoftware.certitools.entities.jcr.Resource;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.presentation.action.AbstractActionBean;
import com.criticalsoftware.certitools.presentation.util.HTMLEscapeAndNL2BR;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PlanUtils;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.TreeNode;
import com.criticalsoftware.certitools.util.Utils;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.ajax.JavaScriptResolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.localization.LocalizationUtility;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.security.exception.StripesAuthorizationException;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Plan Action Bean TODO Parametrization for module type done
 *
 * @author : lt-rico
 */
public class PlanActionBean extends AbstractActionBean {
    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @EJBBean(value = "certitools/ContractService")
    private ContractService contractService;

    @EJBBean(value = "certitools/CompanyService")
    private CompanyService companyService;

    @EJBBean(value = "certitools/UserService")
    private UserService userService;

    private int maxPlansInList = 6;

    private Long peiId;

    private Plan pei;
    private ArrayList<TreeNode> peiTreeNodes;
    private List<Plan> planList;

    private Folder section;
    private String sectionFolder;
    private String path;
    private ArrayList<TreeNode> breadcrumbs;

    private List<Company> companies;
    private Collection<Contract> contracts;
    private Long companyId;
    private Integer order;

    private boolean selfImage;
    private Boolean peiViewOffline;

    // needed for PEI CM link
    private String pathCM;
    private boolean planManager;

    private String moduleTitle;

    private static final Logger LOGGER = Logger.getInstance(PlanActionBean.class);

    @DefaultHandler
    @Secure(roles = "user")
    public Resolution viewPEIList()
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException,
            StripesAuthorizationException {
        planList = planService.findAllPlansAllowed(this.getUserInSession(), getModuleTypeFromEnum());

        // remove peis that don't have online content if user is not in preview mode
        if (!getUserPEIPreview()) {
            List<Plan> peiListFiltered = new ArrayList<Plan>();
            for (Plan peiTemp : planList) {
                if (peiTemp.getOnline() != null && peiTemp.getOnline().size() > 0) {
                    peiListFiltered.add(peiTemp);
                }
            }
            planList = peiListFiltered;
        }

        // check if user has some PEI active
        boolean hasSomePlanActive = false;
        for (Plan peiTemp : planList) {
            if (peiTemp.isUserCanAccess()) {
                hasSomePlanActive = true;
            }
        }

        // if user only has 1 plan, select it automagically
        if (planList.size() == 1 && hasSomePlanActive) {
            return new RedirectResolution(PlanActionBean.class, "viewPEI")
                    .addParameter("peiId", planList.get(0).getName())
                    .addParameter("planModuleType", getPlanModuleType());
        }

        // if user doesn't have PEIs, show unauthorized default page
        if (planList.size() == 0) {
            if (isUserInRole("peimanager") || isUserInRole("clientpeimanager")) {
                return new ForwardResolution(
                        "/WEB-INF/jsps/" + getPlanModuleType().toLowerCase() + "/unauthorized.jsp");
            }

            throw new StripesAuthorizationException(getPlanModuleType().toLowerCase() + "Access");
        }

        if (getUserPEIPreview()) {
            for (Plan peiTemp : planList) {
                peiTemp.setAuthorNameOnline(peiTemp.getAuthorName());
                peiTemp.setPlanNameOnline(peiTemp.getPlanName());
                peiTemp.setSimulationDateOnline(peiTemp.getSimulationDate());
                peiTemp.setVersionDateOnline(peiTemp.getVersionDate());
                peiTemp.setVersionOnline(peiTemp.getVersion());
            }
        }

        if (planList.size() > maxPlansInList) {
            loadLists();
            if (contracts.size() <= 0) {
                if (isUserInRole("peimanager") || isUserInRole("clientpeimanager")) {
                    return new ForwardResolution(
                            "/WEB-INF/jsps/" + getPlanModuleType().toLowerCase() + "/unauthorized.jsp");
                }
                throw new StripesAuthorizationException(getPlanModuleType().toLowerCase() + "Access");
            }
        }

        pei = planList.get(0);

        // if normal user, check if user can only access PEI because of showFullListPEI = true
        // if user doesn't have a contract active, don't show submenu.
        if (!isUserInRole("peimanager") && !isUserInRole("contractmanager") && !isUserInRole("administrator")) {
            boolean canAccessSomePEI = false;
            for (Plan peiTemp : planList) {
                if (peiTemp.isUserCanAccess()) {
                    canAccessSomePEI = true;
                    break;
                }
            }
            if (!canAccessSomePEI) {
                getMenu().removeSubMenu(MenuItem.Item.MENU_PEI);
            }
        }

        // TODO-MODULE
        String moduleType = getPlanModuleType();
        if (moduleType.equals(ModuleType.PEI.toString())) {
            moduleTitle = com.criticalsoftware.certitools.presentation.util.Utils.getMenuLabelForPlans(
                    ModuleType.PEI, getUserInSession(), getContext().getRequest());
            if (moduleTitle == null) {
                moduleTitle = LocalizationUtility.getLocalizedFieldName("menu.emergency", null, null,
                        getContext().getLocale());
            }
        } else if (moduleType.equals(ModuleType.PRV.toString())) {
            moduleTitle = com.criticalsoftware.certitools.presentation.util.Utils.getMenuLabelForPlans(
                    ModuleType.PRV, getUserInSession(), getContext().getRequest());
            if (moduleTitle == null) {
                moduleTitle = LocalizationUtility.getLocalizedFieldName("menu.safety", null, null,
                        getContext().getLocale());
            }
        } else if (moduleType.equals(ModuleType.PSI.toString())) {
            moduleTitle = com.criticalsoftware.certitools.presentation.util.Utils.getMenuLabelForPlans(
                    ModuleType.PSI, getUserInSession(), getContext().getRequest());
            if (moduleTitle == null) {
                moduleTitle = LocalizationUtility.getLocalizedFieldName("menu.psi", null, null,
                        getContext().getLocale());
            }
        } else if (moduleType.equals(ModuleType.GSC.toString())) {
            moduleTitle = com.criticalsoftware.certitools.presentation.util.Utils.getMenuLabelForPlans(
                    ModuleType.GSC, getUserInSession(), getContext().getRequest());
            if (moduleTitle == null) {
                moduleTitle = LocalizationUtility.getLocalizedFieldName("menu.gsc", null, null,
                        getContext().getLocale());
            }
        }

        setHelpId("#pei-select");
        return new ForwardResolution("/WEB-INF/jsps/plan/planMain.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "user")
    public Resolution viewPEI()
            throws JackrabbitException, ObjectNotFoundException, IOException, CertitoolsAuthorizationException,
            BusinessException {

        if (peiId != null) {
            pei = planService.find(getUserInSession(), peiId, true, getModuleTypeFromEnum());
            peiTreeNodes = planService.findFoldersTreeAllowed(peiId, getUserInSession(), !getUserPEIPreview(),
                    getModuleTypeFromEnum());

            if (pei != null && getUserPEIPreview()) {
                pei.setAuthorNameOnline(pei.getAuthorName());
                pei.setPlanNameOnline(pei.getPlanName());
                pei.setSimulationDateOnline(pei.getSimulationDate());
                pei.setVersionDateOnline(pei.getVersionDate());
                pei.setVersionOnline(pei.getVersion());
            }

            if (pei == null || (!getUserPEIPreview() && (pei.getOnline() == null || pei.getOnline().size() <= 0))) {
                throw new ObjectNotFoundException("Plan not found or without online content. PlanID: " + peiId,
                        ObjectNotFoundException.Type.PLAN);
            }
        } else {
            throw new ObjectNotFoundException("No Plan was selected", ObjectNotFoundException.Type.PLAN);
        }

        // load pei manager boolean (if user can manage the pei or not)
        loadPathCMAndPeiManager(null, peiId);

        setHelpId("#pei-cover");

        //Update last Plan Cover View
        userService.updateUserLastPlanOrLegislationView(getUserInSession().getId(), getModuleTypeFromEnum(), peiId);

        return new ForwardResolution("/WEB-INF/jsps/plan/planView.jsp")
                .addParameter("planModuleType", getPlanModuleType());
    }

    /**
     * Shows a resource from the PEI. If path is null or empty or / redirects to viewPEI, cover page Needs these fields
     * set: planId, path
     *
     * @return Resolution
     *
     * @throws JackrabbitException     error in jackrabbit
     * @throws ObjectNotFoundException pei not found
     * @throws CertitoolsAuthorizationException
     *                                 user can't access some data
     * @throws BusinessException
     *                                 Template name invalid / not supported
     * @throws java.io.UnsupportedEncodingException
     *                                 URIEncode error
     */
    @Secure(roles = "user")
    public Resolution viewResource()
            throws JackrabbitException, ObjectNotFoundException, CertitoolsAuthorizationException, BusinessException,
            UnsupportedEncodingException {
        setHelpId("#pei-details");

        if (path == null || StringUtils.isEmpty(path) || path.equals("/")) {
            return new RedirectResolution(PlanActionBean.class, "viewPEI").addParameter("peiId", peiId)
                    .addParameter("planModuleType", getPlanModuleType());
        }

        if (peiId != null) {
            pei = planService.find(getUserInSession(), peiId, getModuleTypeFromEnum());
            peiTreeNodes = planService.findFoldersTreeAllowed(peiId, getUserInSession(), !getUserPEIPreview(),
                    getModuleTypeFromEnum());
        } else {
            throw new ObjectNotFoundException("No PEI was selected", ObjectNotFoundException.Type.PLAN);
        }

        String onlineOffline;

        onlineOffline = (getUserPEIPreview()) ? "offline" : "online";

        peiViewOffline = peiViewOffline == null ? false : peiViewOffline;
        if (peiViewOffline != null && peiViewOffline) {
            onlineOffline = "offline";
        }

        Folder folder =
                planService.findFolderAllowed(
                        "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + getPlanModuleType() + peiId + "/" + onlineOffline
                                + path,
                        false, getUserInSession());

        String templateName = folder.getTemplate().getName();
        // nl2br the folder help
        folder.setHelp(HTMLEscapeAndNL2BR.replaceAndEscape(folder.getHelp()));

        breadcrumbs = findBreadcrumbs(onlineOffline);

        super.setFolder(folder);
        super.setBreadcrumbs(breadcrumbs);
        super.setPEI(pei);
        super.setPEITreeNodes(peiTreeNodes);

        // if folder is inactive and not in preview mode, send error
        if ((!folder.getActive() && !peiViewOffline) && !getUserPEIPreview()) {
            throw new ObjectNotFoundException("[viewResource] Folder not found or inactive. Path: " + path,
                    ObjectNotFoundException.Type.FOLDER);
        }

        // check if user is peimanager
        loadPathCMAndPeiManager(folder, Long.parseLong(pei.getName()));

        sectionFolder = Utils.encodeURI("/" + PlanUtils.getFolderInPathByIndex(folder.getPath(), 3));

        // get Parent folder to get section help
        String sectionPath = PlanUtils.getSectionPath(folder.getPath());
        Folder sectionFolder = planService.findFolderAllowed(sectionPath, false, getUserInSession());
        sectionFolder.setHelp(HTMLEscapeAndNL2BR.replace(sectionFolder.getHelp()));
        super.setSection(sectionFolder);
        section = sectionFolder;

        if (!folder.hasFrontOffice(order)) {
            folder.setTemplate(new Template("TemplateNoNavigable"));
            return new ForwardResolution("/WEB-INF/jsps/plan/planViewTemplate.jsp")
                    .addParameter("planModuleType", getPlanModuleType());
        }
        return redirectToTemplateActionBean(templateName);
    }

    @Secure(roles = "user")
    public Resolution viewPEIPreview() {
        setUserPEIPreview(true);

        return new RedirectResolution(PlanActionBean.class, "viewResource").addParameter("peiId", peiId)
                .addParameter("path", path).addParameter("planModuleType", getPlanModuleType());
    }

    @Secure(roles = "user")
    public Resolution cancelPEIPreview() {
        setUserPEIPreview(false);

        if (peiId == null) {
            return new RedirectResolution(PlanCMActionBean.class).addParameter("planModuleType", getPlanModuleType());
        }

        return new RedirectResolution(PlanCMActionBean.class, "viewPeiCMFromPreview").addParameter("contractId", peiId)
                .addParameter("path", path).addParameter("planModuleType", getPlanModuleType());
    }

    /**
     * Returns the Installation Photo
     * <p/>
     * Needs the planId param set. Uses the online boolean variable
     *
     * @return Resolution with the installation photo bytes
     *
     * @throws ObjectNotFoundException PEI not found
     * @throws JackrabbitException     Jackrrabit error
     * @throws CertitoolsAuthorizationException
     *                                 user cannot acess PEI
     */
    @Secure(roles = "user")
    public Resolution viewInstallationPhoto()
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException {
        pei = planService.findPEIWithShowFullListPEI(getUserInSession(), peiId, getModuleTypeFromEnum());

        getContext().getResponse().setHeader("Cache-control", "");
        getContext().getResponse().setHeader("Pragma", "");

        Resource resource;
        if (!getUserPEIPreview()) {
            resource = pei.getInstallationPhotoOnline();
        } else {
            resource = pei.getInstallationPhoto();
        }

        if (resource == null) {
            return null;
        }
        return new StreamingResolution(resource.getMimeType(), resource.getData());
    }

    /**
     * Returns the company logo
     * <p/>
     * Needs the planId param set. Uses the online boolean variable
     *
     * @return Resolution with the company logo bytes
     *
     * @throws ObjectNotFoundException PEI not found
     * @throws JackrabbitException     Jackrrabit error
     * @throws CertitoolsAuthorizationException
     *                                 user cannot acess PEI
     */
    @Secure(roles = "user")
    public Resolution viewCompanyLogo()
            throws ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException {
        pei = planService.findPEIWithShowFullListPEI(getUserInSession(), peiId, getModuleTypeFromEnum());

        getContext().getResponse().setHeader("Cache-control", "");
        getContext().getResponse().setHeader("Pragma", "");

        Resource resource;
        if (!getUserPEIPreview()) {
            resource = pei.getCompanyLogoOnline();
        } else {
            resource = pei.getCompanyLogo();
        }

        if (resource == null) {
            return null;
        }
        return new StreamingResolution(resource.getMimeType(), resource.getData()).setFilename(resource.getName());
    }

    @After(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        setPlanMenu(MenuItem.Item.SUB_MENU_PEI_VIEW, MenuItem.Item.SUB_MENU_SAFETY_VIEW,
                MenuItem.Item.SUB_MENU_PSI_VIEW, MenuItem.Item.SUB_MENU_GSC_VIEW);
    }

    @After(stages = LifecycleStage.EventHandling)
    public void doEncoding() throws UnsupportedEncodingException {
        if (peiTreeNodes != null) {
            for (TreeNode tn : peiTreeNodes) {
                tn.setPath(URLEncoder.encode(tn.getPath(), "UTF-8"));
            }
        }
        if (breadcrumbs != null) {
            for (TreeNode tn : breadcrumbs) {
                tn.setPath(URLEncoder.encode(tn.getPath(), "UTF-8"));
            }
        }
    }

    @Secure(roles = "user")
    public Resolution loadCompanyContracts() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        contracts = new ArrayList<Contract>();

        if (companyId != null) {
            contracts = contractService.findAllPlansWithUserContractAllowed(companyId, getUserInSession(), true,
                    getModuleTypeFromEnum());

            contracts = PlanUtils.cleanContractsForJavascriptResolution(contracts);
        }

        return new JavaScriptResolution(contracts, Company.class, Date.class, UserContract.class, Module.class);
    }

    /**
     * Loads and sets the peiManager variable (if user is the pei manager or not) and the path to CM (path to
     * administration area of that folder)
     *
     * @param folder folder to analyse, or null if no folder
     * @param planId id of the pei to check
     */
    private void loadPathCMAndPeiManager(Folder folder, long planId) {
        planManager = false;
        try {
            planManager = planService.isUserPlanManager(planId, getUserInSession());
        } catch (ObjectNotFoundException e) {
            LOGGER.error("[loadPEI] - user not found. ID: " + getUserInSession().getId());
        }

        if (folder != null) {
            String pathCM =
                    folder.getPath().substring(0, StringUtils.ordinalIndexOf(folder.getPath(), "/", 3)) + "/offline"
                            + folder.getPath().substring(StringUtils.ordinalIndexOf(folder.getPath(), "/", 4));
            super.setPathCM(pathCM);
        }
        super.setPlanManagerInRequest(planManager);
    }

    /**
     * Redirects to the View Template action bean, to the correct event according to the template name (that sets the
     * type of template)
     *
     * @param templateName name of the template
     *
     * @return ForwardResolution to the correct event in the view template action bean
     *
     * @throws BusinessException Template name invalid
     */
    private Resolution redirectToTemplateActionBean(String templateName) throws BusinessException {
        boolean templateValid = false;

        for (Template.Type type : EnumSet.allOf(Template.Type.class)) {
            if (type.getName().equals(templateName)) {
                templateValid = true;
                break;
            }
        }

        if (!templateValid) {
            LOGGER.error("[redirectToTemplateActionBean] Template not supported");
            throw new BusinessException("Template not supported");
        }

        // TODO improvement: this chould check if the correct method exists in the peiviewtemplateactionbean, otherwise
        // give an error message

        String eventName = "view" + templateName;

        return new ForwardResolution(PlanViewTemplateActionBean.class, eventName);
    }

    // update according to peicm

    private void loadLists() throws CertitoolsAuthorizationException, ObjectNotFoundException {
        Collection<Contract> allContractAllowed = new ArrayList<Contract>();
        contracts = new ArrayList<Contract>();
        companies = new ArrayList<Company>(companyService.findAllWithPlan(getUserInSession(), getModuleTypeFromEnum(),
                true));


        if (companyId == null) {
            if (companies.size() > 0) {
                allContractAllowed =
                        contractService
                                .findAllPlansWithUserContractAllowed(companies.get(0).getId(), getUserInSession(), true,
                                        getModuleTypeFromEnum());
            }
        } else {
            allContractAllowed =
                    contractService.findAllPlansWithUserContractAllowed(companyId, getUserInSession(), true,
                            getModuleTypeFromEnum());
        }

        boolean first = true;
        for (Contract c : allContractAllowed) {
            if (c.getModule().getModuleType().equals(getModuleTypeFromEnum())) {
                if (peiId == null) {
                    if (first) {
                        peiId = c.getId();
                        first = false;
                    }
                }
                contracts.add(c);
            }
        }
    }

    private ArrayList<TreeNode> findBreadcrumbs(String onlineOffline)
            throws BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException, JackrabbitException {
        ArrayList<TreeNode> breadcrumbsTemp = new ArrayList<TreeNode>();

        int depth = calculateDepth(path);

        StringTokenizer st = new StringTokenizer(path, "/");
        String pathCurrent = "";
        String node;

        for (int i = 0; i < depth; i++) {
            node = st.nextToken();
            pathCurrent += "/" + node;

            // check if folder is navigable and to be included in menu
            Folder currentFolder = planService.findFolderAllAllowed(
                    "/" + PlanUtils.ROOT_PLAN_FOLDER + "/" + getPlanModuleType() + peiId + "/" + onlineOffline
                            + pathCurrent,
                    false);
            if (currentFolder.isNavigable() && currentFolder.getIncludeInMenuOrIsNavigable()) {
                breadcrumbsTemp.add(new TreeNode(node, pathCurrent));
            }

            if (i != depth - 1) {
                pathCurrent += "/" + st.nextToken();
            }
        }

        return breadcrumbsTemp;
    }

    /**
     * Calculates the depth of a Folder based on the path (counts '/') /certitools_pei_root/PEI16/offline/Sec��o 1
     * returns 1 /certitools_pei_root/PEI16/offline/Sec��o 1/folders/sub returns 2 /certitools_pei_root/PEI16/offline/Sec��o
     * 1/folders/sub/folders/sub sub  returns 3
     *
     * @param path path to analyse
     *
     * @return depth of the path
     */
    private int calculateDepth(String path) {

        int matches = StringUtils.countMatches(path, "/");
        matches = (matches - 1);
        return matches / 2 + 1;

    }

    public ArrayList<TreeNode> getPeiTreeNodes() {
        return peiTreeNodes;
    }

    public void setPeiTreeNodes(ArrayList<TreeNode> peiTreeNodes) {
        this.peiTreeNodes = peiTreeNodes;
    }

    public Long getPeiId() {
        return peiId;
    }

    public void setPeiId(Long peiId) {
        this.peiId = peiId;
    }

    public void setPEIService(PlanService PlanService) {
        this.planService = PlanService;
    }

    public List<Plan> getPlanList() {
        return planList;
    }

    public void setPlanList(List<Plan> planList) {
        this.planList = planList;
    }

    public String getPath() {
        try {
            return URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return path;
        }
    }

    public void setPath(String path) {
        try {
            this.path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            this.path = path;
        }
    }

    public ArrayList<TreeNode> getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(ArrayList<TreeNode> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public Plan getPei() {
        return pei;
    }

    public void setPei(Plan pei) {
        this.pei = pei;
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    public CompanyService getCompanyService() {
        return companyService;
    }

    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public boolean isSelfImage() {
        return selfImage;
    }

    public void setSelfImage(boolean selfImage) {
        this.selfImage = selfImage;
    }

    public Boolean isPeiViewOffline() {
        return peiViewOffline;
    }

    public void setPeiViewOffline(Boolean peiViewOffline) {
        this.peiViewOffline = peiViewOffline;
    }

    public String getPathCM() {
        return pathCM;
    }

    public void setPathCM(String pathCM) {
        this.pathCM = pathCM;
    }

    public boolean isPlanManager() {
        return planManager;
    }

    public void setPlanManager(boolean planManager) {
        this.planManager = planManager;
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

    public int getMaxPlansInList() {
        return maxPlansInList;
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
    }
}
