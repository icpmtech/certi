/*
 * $Id: LegislationActionBean.java,v 1.70 2010/02/05 17:54:27 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/02/05 17:54:27 $
 * Last changed by : $Author: pjfsilva $
 */

package com.criticalsoftware.certitools.presentation.action.legislation;

import com.criticalsoftware.certitools.business.certitools.UserService;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.DocumentNotExistsException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.legislation.LegalDocumentHistoryService;
import com.criticalsoftware.certitools.business.legislation.LegalDocumentStatisticsService;
import com.criticalsoftware.certitools.business.legislation.LegislationService;
import com.criticalsoftware.certitools.business.legislation.NewsletterService;
import com.criticalsoftware.certitools.entities.*;
import com.criticalsoftware.certitools.presentation.action.DisplayTagSupportActionBean;
import com.criticalsoftware.certitools.presentation.util.HTMLEscapeAndNL2BR;
import com.criticalsoftware.certitools.presentation.util.MenuItem;
import com.criticalsoftware.certitools.presentation.util.PTDateTypeConverter;
import com.criticalsoftware.certitools.presentation.util.PaginatedListAdapter;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.File;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.PaginatedListWrapper;
import com.criticalsoftware.certitools.util.LegislationUtils;
import com.samaxes.stripejb3.EJBBean;

import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.*;
import net.sourceforge.stripes.localization.LocalizationUtility;

import java.io.IOException;
import java.util.*;

import org.apache.commons.lang.StringUtils;

/**
 * Legislation Action Bean
 *
 * @author : lt-rico
 * @version : $version $
 */
@Secure(roles = "legislationAccess")
public class LegislationActionBean extends DisplayTagSupportActionBean implements ValidationErrorHandler {

    @EJBBean(value = "certitools/LegislationService")
    private LegislationService legislationService;

    @EJBBean(value = "certitools/UserService")
    private UserService userService;

    @EJBBean(value = "certitools/NewsletterService")
    private NewsletterService newsLetterService;

    @EJBBean(value = "certitools/LegalDocumentStatisticsService")
    private LegalDocumentStatisticsService legalDocumentStatisticsService;

    @EJBBean(value = "certitools/LegalDocumentHistoryService")
    private LegalDocumentHistoryService legalDocumentHistoryService;

    /* Insert form lists*/
    private List<LegalDocumentType> documentTypeList;
    private List<LegalDocumentCategory> documentCategoryList;
    private List<LegalDocumentState> documentStateList;

    private Set<Long> selectedLegalDocumentCategories;
    private StringBuilder legalDocumentsSearch;

    private Long legalDocumentTypeId;
    private String searchField;
    private Boolean update = false;

    private String drTitle;

    // Last inserted Legal Documents
    private List<LegalDocument> lastInsertedLegalDocuments;
    // Last Visualized Legal Documents
    private Set<LegalDocument> lastVisualizedLegalDocuments;

    //Legislation Category
    private LegalDocumentCategory[] legalDocumentCategoryNavegation;
    private Long depth;
    private Long categoryId;
    private List<LegalDocumentCategory[]> userSubscriptions;
    private Boolean subscribed;
    private Boolean isCategorySearch;
    private LegalDocumentCategory category;
    private long[] categoriesPerColumn;

    //Legislation View
    private List<LegalDocumentCategory[]> legaDocumentCategoryNavigation;
    private List<String> keywords;
    private Boolean canDownload;

    //Legislation Search
    private String searchLegislation;

    private Long documentTypeId;
    private String year;
    private String number;

    private List<LegalDocument> legalDocumentsToExport;

    private PaginatedListAdapter<LegalDocument> legalDocuments;

    @ValidateNestedProperties(value = {
            @Validate(field = "customTitle", maxlength = 255, required = true,
                    on = {"insertLegislation", "updateLegislation"}),
            @Validate(field = "documentType.id", required = true, on = {"insertLegislation", "updateLegislation"}),
            @Validate(field = "number", required = true, on = {"insertLegislation", "updateLegislation"}),
            @Validate(field = "publicationDate", required = true, on = {"insertLegislation", "updateLegislation"},
                    converter = PTDateTypeConverter.class),
            @Validate(field = "summary", maxlength = 4096, required = true,
                    on = {"insertLegislation", "updateLegislation"}),
            @Validate(field = "documentState.id", required = true, on = {"insertLegislation", "updateLegislation"}),
            @Validate(field = "keywords", maxlength = 512, required = false,
                    on = {"insertLegislation", "updateLegislation"}),
            @Validate(field = "customAbstract", maxlength = 10000, required = false,
                    on = {"insertLegislation", "updateLegislation"}),
            @Validate(field = "transitoryProvisions", maxlength = 4096, required = false,
                    on = {"insertLegislation", "updateLegislation"}),
            @Validate(field = "legalComplianceValidation", maxlength = 5000, required = false,
                    on = {"insertLegislation", "updateLegislation"}),
            @Validate(field = "referenceArticles", maxlength = 5000, required = false,
                    on = {"insertLegislation", "updateLegislation"})})
    private LegalDocument legalDocument;

    @Validate(required = true, on = {"insertLegislation"})
    private FileBean file;

    @DontValidate
    @DefaultHandler
    @Secure(roles = "user")
    public Resolution viewLegislations() throws ObjectNotFoundException, BusinessException {
        //Legislation Category Search
        if (categoryId != null) {
            category = legislationService.findLegalDocumentCategoryById(categoryId);

            if (isUserInRole("legislationmanager")) {

                legalDocuments = new PaginatedListAdapter<LegalDocument>(
                        legislationService.findLegalDocumentsByCategory(
                                new PaginatedListWrapper<LegalDocument>(getPage(),
                                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(),
                                        getDirOrder(),
                                        isExportRequest()), categoryId, true));
            } else {
                legalDocuments = new PaginatedListAdapter<LegalDocument>(
                        legislationService.findLegalDocumentsByCategory(
                                new PaginatedListWrapper<LegalDocument>(getPage(),
                                        Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(),
                                        getDirOrder(),
                                        isExportRequest()), categoryId, false));
            }

        } else if (searchLegislation != null) {
            legalDocuments = new PaginatedListAdapter<LegalDocument>(
                    legislationService.findLegalDocuments(new PaginatedListWrapper<LegalDocument>(getPage(),
                            Integer.parseInt(Configuration.getInstance().getPageListSize()), getSort(), getDirOrder(),
                            isExportRequest()), searchLegislation));

            //Add entry to statistics
            legalDocumentStatisticsService
                    .addEntry(LegalDocumentStatistics.ReportType.SEARCH_TERM, searchLegislation, null);
        }
        /* Build Category Navegation for each LegalDocument*/
        if (legalDocuments != null && legalDocuments.getList() != null) {
            for (LegalDocument ld : legalDocuments.getList()) {
                List<LegalDocumentCategory[]> ldcList = new ArrayList<LegalDocumentCategory[]>();

                List<LegalDocumentCategory> categoriesToShow =
                        LegislationUtils.showLongestCategory(ld.getLegalDocumentCategories());

                for (LegalDocumentCategory ldcToShow : categoriesToShow) {
                    ldcList.add(buildCategoryHierarchy(ldcToShow.getId()));
                }
                ld.setCategoryNavegation(ldcList);
                ld.setSummary(reduceField(ld.getSummary()));
            }
        }

        //No search made
        if (legalDocuments == null) {
            //Load base category list
            documentCategoryList = legislationService.findLegalDocumentCategoriesByDepthAndId(null, null);
            if (documentCategoryList != null) {
                categoriesPerColumn = calcCategoriesPerColumn(documentCategoryList.size());
            }
            //User Subscriptions Category List
            User user = userService.findUserWithSubscriptions(getUserInSession().getId());
            buildUserSubscriptionsList(user);
        }

        /* List must have at least one element for export operations appear in DisplayTag */
        if (isUserInRole("legislationmanager")) {
            legalDocumentsToExport = new ArrayList<LegalDocument>();
            legalDocumentsToExport.add(new LegalDocument(1L));
        }

        //Update last legislation search date
        userService.updateUserLastPlanOrLegislationView(getUserInSession().getId(), ModuleType.LEGISLATION, null);

        setLastInsertedAndVisualizedLegalDocumentsList();
        setHelpId("#legislation-search");
        return new ForwardResolution("/WEB-INF/jsps/legislation/legislations.jsp");
    }

    @Secure(roles = "user")
    public Resolution downloadFile() throws BusinessException, ObjectNotFoundException, DocumentNotExistsException,
            CertitoolsAuthorizationException {

        File file = legislationService.findLegalDocumentFileToDownload(legalDocument.getId());

        if (file == null) {
            throw new BusinessException("the file to Download is null");
        }
        //Add entry to statistics
        legalDocumentStatisticsService
                .addEntry(LegalDocumentStatistics.ReportType.DOWNLOAD, null, legalDocument.getId());

        getContext().getResponse().setHeader("Cache-control", "");
        getContext().getResponse().setHeader("Pragma", "");

        return new StreamingResolution(file.getContentType(), file.getData()).setFilename(file.getFileName());
    }

    @Secure(roles = "user")
    public Resolution subscribeCategory() throws ObjectNotFoundException {
        try {
            newsLetterService.subscribe(getUserInSession().getId(), categoryId);
        } catch (BusinessException e) {
            //To nothing
        }
        return viewLegislationCategory();
    }

    @Secure(roles = "user")
    public Resolution unSubscribeCategory() throws ObjectNotFoundException {
        try {
            newsLetterService.unsubscribe(getUserInSession().getId(), categoryId);
        } catch (BusinessException e) {
            //To nothing
        }
        return viewLegislationCategory();
    }

    @Secure(roles = "user")
    public Resolution viewLegislationCategory() throws ObjectNotFoundException {

        documentCategoryList = legislationService.findLegalDocumentCategoriesByDepthAndId(categoryId, depth);

        if (documentCategoryList != null) {
            categoriesPerColumn = calcCategoriesPerColumn(documentCategoryList.size());
        }

        //Legal Document Category Hierarchy
        legalDocumentCategoryNavegation = buildCategoryHierarchy(categoryId);

        //User Subscriptions Category List
        User user = userService.findUserWithSubscriptions(getUserInSession().getId());
        buildUserSubscriptionsList(user);

        //is Subscribed
        if (!userSubscriptions.isEmpty() && categoryId != null) {

            LegalDocumentCategory selectedCategory = legislationService.findLegalDocumentCategoryById(categoryId);

            if (selectedCategory.getParentCategory() == null) {
                subscribed = user.getSubscriptionsLegalDocuments()
                        .contains(selectedCategory);

            } else {
                if (user.getSubscriptionsLegalDocuments().contains(selectedCategory.getParentCategory())) {
                    subscribed = null;

                } else {
                    subscribed = user.getSubscriptionsLegalDocuments().contains(selectedCategory);
                }
            }
        } else {
            subscribed = false;
        }

        setLastInsertedAndVisualizedLegalDocumentsList();

        setHelpId("#legislation-search-category");

        return new ForwardResolution("/WEB-INF/jsps/legislation/legislationsCategory.jsp");
    }

    @Secure(roles = "user")
    public Resolution viewLegislation() throws ObjectNotFoundException, DocumentNotExistsException {

        //Find Legal Document
        legalDocument = legislationService.findLegalDocumentFullyLoadedById(legalDocument.getId(), true);

        if (legalDocument != null && legalDocument.getAssociatedLegalDocuments() != null
                && legalDocument.getAssociatedLegalDocuments().size() > 0) {

            /* Filter Legal Documents, only show published*/

            List<LegalDocument> documentsPublish = new ArrayList<LegalDocument>();

            for (LegalDocument ld : legalDocument.getAssociatedLegalDocuments()) {
                if (ld.isPublished()) {
                    documentsPublish.add(ld);
                }
            }
            legalDocument.setAssociatedLegalDocuments(documentsPublish);
        }

        //Build Category Navegation
        legaDocumentCategoryNavigation = new ArrayList<LegalDocumentCategory[]>();

        List<LegalDocumentCategory> categoriesToShow =
                LegislationUtils.showLongestCategory(legalDocument.getLegalDocumentCategories());

        for (LegalDocumentCategory ldc : categoriesToShow) {
            legaDocumentCategoryNavigation.add(buildCategoryHierarchy(ldc.getId()));
        }

        //Add entry to statistics
        legalDocumentStatisticsService
                .addEntry(LegalDocumentStatistics.ReportType.VISUALIZATION, null, legalDocument.getId());

        //Add entry to visualized documents history
        legalDocumentHistoryService.addEntry(legalDocument.getId(), getUserInSession().getId());

        //set Multiple link to keywords
        if (legalDocument.getKeywords() != null) {
            keywords = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(legalDocument.getKeywords(), ", ;");

            while (st.hasMoreElements()) {
                keywords.add(st.nextToken());
            }
        }

        // Do not show download link
        if (isUserInRole("userguest")) {
            canDownload = false;
        }

        setLastInsertedAndVisualizedLegalDocumentsList();

        legalDocument.setSummary(HTMLEscapeAndNL2BR.replaceAndEscape(legalDocument.getSummary()));

        setHelpId("#legislation-detail");

        if (isUserInRole("legislationmanager")) {
            String referer = getContext().getRequest().getHeader("referer");
            if (referer == null) {
                setSessionAttribute("legislationBackPage", null);
            } else {
                if (referer.contains("searchLegislation") || referer.contains("viewLegislations")) {
                    setSessionAttribute("legislationBackPage", referer);
                } else if (!(referer.contains("updateLegislationForm") || referer.contains("insertLegislationForm"))) {
                    setSessionAttribute("legislationBackPage", null);
                }
            }
            String refererInSession =
                    (String) getContext().getRequest().getSession().getAttribute("legislationBackPage");
            if (refererInSession != null && !refererInSession.isEmpty()) {
                setAttribute("showBack", true);
            }
        }
        return new ForwardResolution("/WEB-INF/jsps/legislation/legislationView.jsp");
    }

    @Secure(roles = "legislationmanager")
    public Resolution back() {
        return new RedirectResolution(
                (String) getContext().getRequest().getSession().getAttribute("legislationBackPage"), false);
    }

    @Secure(roles = "legislationmanager")
    public Resolution exportLegislation() {
        Locale locale = getContext().getLocale();
        setExportXLS(LocalizationUtility
                .getLocalizedFieldName("table.legislation.filename.xls", null, null, locale));
        legalDocumentsToExport = legislationService.findAllLegalDocumentSortByCategory();
        return new ForwardResolution("/WEB-INF/jsps/legislation/legislations.jsp");
    }

    @Secure(roles = "legislationmanager")
    public Resolution deleteLegislation() throws ObjectNotFoundException, BusinessException {
        legislationService.deleteLegalDocument(legalDocument.getId());
        getContext().getMessages().add(new LocalizableMessage("legislation.delete.sucess"));
        return new RedirectResolution(LegislationActionBean.class);
    }

    @Secure(roles = "legislationmanager")
    public Resolution insertLegislationForm() {

        /* Set checkbox published and notification*/
        if (legalDocument == null) {
            legalDocument = new LegalDocument();
            legalDocument.setPublished(true);
            legalDocument.setSendNotificationNew(true);
            legalDocument.setDocumentState(legislationService.findLegalDocumentStateByName("Em Vigor"));
        }

        setHelpId("#legislation-add-legislation");

        return loadValuesAndRedirectToForm();
    }

    @Secure(roles = "legislationmanager")
    public Resolution updateLegislationForm() throws ObjectNotFoundException, DocumentNotExistsException {

        update = true;
        legalDocument = legislationService.findLegalDocumentFullyLoadedById(legalDocument.getId(), true);
        drTitle = legalDocument.getDrTitle();

        selectedLegalDocumentCategories = new HashSet<Long>();

        if (legalDocument.getLegalDocumentCategories() != null) {
            for (LegalDocumentCategory legalDocumentCategory : legalDocument.getLegalDocumentCategories()) {
                selectedLegalDocumentCategories.add(legalDocumentCategory.getId());
            }
        }

        setHelpId("#legislation-edit-legislation");
        return loadValuesAndRedirectToForm();
    }

    @Secure(roles = "legislationmanager")
    private Resolution loadValuesAndRedirectToForm() {
        documentTypeList = legislationService.findAllLegalDocumentTypes();
        documentCategoryList = legislationService
                .findAllLegalDocumentCategoryForTree(legislationService.findTopParentDocumentCategory());
        documentStateList = legislationService.findAllLegalDocumentState();

        return new ForwardResolution("/WEB-INF/jsps/legislation/legislationInsert.jsp");
    }

    @Secure(roles = "legislationmanager")
    public Resolution autoCompleteInsertLegislation() {

        legalDocumentsSearch = new StringBuilder();

        List<String> searchParsed = parseSearchField(searchField);
        List<LegalDocument> legalDocumentList =
                legislationService.findLegalDocumentsByTypeAutoComplete(legalDocumentTypeId, searchParsed.get(0),
                        searchParsed.get(1));

        if (legalDocumentList == null || legalDocumentList.size() == 0) {
            return new StreamingResolution("text/plain", "\n");
        }

        for (LegalDocument legalDocument : legalDocumentList) {
            legalDocumentsSearch.append(legalDocument.getDrTitle()).append("\n");
        }

        return new StreamingResolution("text/plain", legalDocumentsSearch.toString());
    }

    @Secure(roles = "legislationmanager")
    public Resolution insertLegislation() throws IOException, ObjectNotFoundException, BusinessException {

        legislationService.insertLegislation(legalDocument, selectedLegalDocumentCategories,
                file.getContentType(), file.getInputStream(), file.getFileName());

        getContext().getMessages().add(new LocalizableMessage("legislation.add.sucess"));
        return new RedirectResolution(LegislationActionBean.class, "viewLegislation")
                .addParameter("legalDocument.id", legalDocument.getId());
    }

    @Secure(roles = "legislationmanager")
    public Resolution updateLegislation()
            throws IOException, ObjectNotFoundException, BusinessException, DocumentNotExistsException {

        if (file == null) {
            legislationService.updateLegislation(legalDocument, selectedLegalDocumentCategories,
                    null, null, null, getUserInSession());
        } else {
            legislationService.updateLegislation(legalDocument, selectedLegalDocumentCategories,
                    file.getContentType(), file.getInputStream(), file.getFileName(), getUserInSession());
        }

        getContext().getMessages().add(new LocalizableMessage("legislation.add.sucess"));
        return new RedirectResolution(LegislationActionBean.class, "viewLegislation")
                .addParameter("legalDocument.id", legalDocument.getId());
    }

    @Secure(roles = "legislationmanager")
    public Resolution cancel() {
        if (update != null && !update) {
            return new RedirectResolution(LegislationActionBean.class);
        }
        return new RedirectResolution(LegislationActionBean.class, "viewLegislation")
                .addParameter("legalDocument.id", legalDocument.getId());
    }

    public Resolution handleValidationErrors(ValidationErrors validationErrors) throws Exception {
        if (getContext().getEventName().equals("insertLegislation") || getContext().getEventName()
                .equals("updateLegislation")) {
            return loadValuesAndRedirectToForm();
        }

        if (getContext().getEventName().equals("deleteLegislation")) {
            return viewLegislation();
        }

        return null;
    }

    @ValidationMethod(on = {"insertLegislation", "updateLegislation"}, when = ValidationState.ALWAYS)
    public void validateInsertLegislation(ValidationErrors errors) throws Exception {

        if (legalDocument.getAssociatedLegalDocuments() != null) {
            /* Validate if associatedLegalDocument have title and its format*/
            Integer counter = 1;
            StringBuilder sbWrongPatter = new StringBuilder();
            StringBuilder sbDocumentNotExists = new StringBuilder();

            boolean countDone;
            for (LegalDocument associatedLegalDocument : legalDocument.getAssociatedLegalDocuments()) {
                countDone = false;

                if (associatedLegalDocument.getDrTitle() == null || associatedLegalDocument.getDrTitle().trim()
                        .equals("")) {

                    sbWrongPatter.append(" ").append(counter).append(",");
                    counter++;
                    countDone = true;
                    continue;
                }

                List<String> parseResult = parseSearchField(associatedLegalDocument.getDrTitle());

                if (parseResult == null || parseResult.get(0) == null || parseResult.get(1) == null) {
                    sbWrongPatter.append(" ").append(counter).append(",");
                    counter++;
                    countDone = true;
                    continue;
                }

                /*  CERTOOL-492
                Pattern patternDate = Pattern.compile("\\d{4}");
                Matcher matcherDate = patternDate.matcher(parseResult.get(1));

                if (!matcherDate.matches()) {
                    sbWrongPatter.append(" ").append(counter).append(",");
                    counter++;
                    continue;
                }
                */

                if (legislationService.findLegalDocumentByTypeNumberAndYear(
                        associatedLegalDocument.getDocumentType().getId(), parseResult.get(0), parseResult.get(1))
                        == null) {
                    sbDocumentNotExists.append(" ").append(counter).append(",");
                    counter++;
                    countDone = true;
                }

                if (!countDone){
                    counter++;
                }
            }


            if (sbWrongPatter.length() != 0) {

                errors.addGlobalError(
                        new LocalizableError("legislation.associatedLegalDocuments.drTitle.wrongPattern",
                                sbWrongPatter.substring(0, sbWrongPatter.length() - 1)));
            }


            if (sbDocumentNotExists.length() != 0) {

                errors.addGlobalError(
                        new LocalizableError("legislation.associatedLegalDocuments.drTitle.doesNotExists",
                                sbDocumentNotExists.substring(0, sbDocumentNotExists.length() - 1)));
            }
        }

        if (legalDocument.getPublicationDate() != null) {
            /* Check if legal document already exists in the database*/
            Calendar date = Calendar.getInstance();
            date.setTime(legalDocument.getPublicationDate());
            Integer temp = date.get(Calendar.YEAR);

            if (!update || update && !number.equals(legalDocument.getNumber())
                    || update && !year.equals(temp.toString())
                    || update && !documentTypeId.equals(legalDocument.getDocumentType().getId())) {
                if (legislationService.findLegalDocumentByTypeNumberAndYear(legalDocument.getDocumentType().getId(),
                        legalDocument.getNumber(), temp.toString()) != null) {
                    errors.addGlobalError(
                            new LocalizableError("legislation.associatedLegalDocuments.alreadyExists"));
                }
            }
        }

        if (selectedLegalDocumentCategories == null || selectedLegalDocumentCategories.isEmpty()) {
            errors.add("selectedLegalDocumentCategories", new LocalizableError("legislation.category.empty"));

        }

        if (file != null) {
            if (!file.getContentType().equals("application/pdf") && !file.getContentType()
                    .equals("application/x-pdf")) {
                errors.add("file", new LocalizableError("legislation.invalidDocument"));
            }
        }

    }

    @ValidationMethod(on = "deleteLegislation")
    public void validateDeleteLegislation(ValidationErrors errors) throws Exception {

        Integer associations = legislationService.findLegalDocumentAssociationsCounter(legalDocument.getId());

        if (associations > 0) {
            errors.addGlobalError(new LocalizableError("legislation.associatedLegalDocuments.hasdependence"));
        }
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void fillLookupFields() {
        getMenu().select(MenuItem.Item.MENU_LEGISLATION, MenuItem.Item.SUB_MENU_LEGISLATION_SEARCH);
    }

    @After(stages = LifecycleStage.BindingAndValidation)
    public void cleanAssociatedDocuments() {

        if (getContext().getEventName().equals("insertLegislation") || getContext().getEventName()
                .equals("updateLegislation")) {

            if (legalDocument != null && legalDocument.getAssociatedLegalDocuments() != null) {

                List<LegalDocument> associatedLegalDocumentTemp = new ArrayList<LegalDocument>();

                for (LegalDocument legalDocumentTemp : legalDocument.getAssociatedLegalDocuments()) {
                    if (legalDocumentTemp != null) {
                        associatedLegalDocumentTemp.add(legalDocumentTemp);
                    }
                }
                legalDocument.setAssociatedLegalDocuments(associatedLegalDocumentTemp);
            }
        }
    }

    private long[] calcCategoriesPerColumn(int size) {

        long[] numberPerColumn = new long[3];
        numberPerColumn[0] = 0;
        numberPerColumn[1] = 0;
        numberPerColumn[2] = 0;
        Boolean firstC = true;
        Boolean secondC = false;

        for (int i = 0; i < size; i++) {

            if (firstC) {
                numberPerColumn[0]++;
                firstC = false;
                secondC = true;

            } else if (secondC) {
                numberPerColumn[1]++;
                firstC = false;
                secondC = false;

            } else {
                numberPerColumn[2]++;
                firstC = true;
                secondC = false;

            }
        }
        return numberPerColumn;
    }

    private void buildUserSubscriptionsList(User user) throws ObjectNotFoundException {
        //User Subscriptions Category List

        if (user.getSubscriptionsLegalDocuments() != null) {
            userSubscriptions = new ArrayList<LegalDocumentCategory[]>();

            for (LegalDocumentCategory legalDocumentCategory : user.getSubscriptionsLegalDocuments()) {
                userSubscriptions.add(buildCategoryHierarchy(legalDocumentCategory.getId()));
            }
        }
    }

    //Set Last inserted Legal Documents List

    private void setLastInsertedAndVisualizedLegalDocumentsList() {

        lastInsertedLegalDocuments = legislationService.findLegalDocumentPublishedHistory(5L);

        for (LegalDocument lDocument : lastInsertedLegalDocuments) {
            lDocument.setReducedField(reduceField(lDocument.getSummary()));
        }

        lastVisualizedLegalDocuments =
                legalDocumentHistoryService.findAllHistoryActiveByUser(getUserInSession().getId());

        for (LegalDocument ld : lastVisualizedLegalDocuments) {
            ld.setReducedField(reduceField(ld.getSummary()));
        }
    }

    private LegalDocumentCategory[] buildCategoryHierarchy(Long categoryIdSelected) throws ObjectNotFoundException {

        LegalDocumentCategory[] resultList;

        Locale locale = getContext().getLocale();

        if (categoryIdSelected == null) {
            resultList = new LegalDocumentCategory[1];
            resultList[0] = new LegalDocumentCategory(
                    LocalizationUtility.getLocalizedFieldName("legislation.add.category", null, null, locale));
            return resultList;
        }

        LegalDocumentCategory selectedCategory = legislationService.findLegalDocumentCategoryById(categoryIdSelected);
        resultList = new LegalDocumentCategory[selectedCategory.getDepth().intValue() + 1];
        resultList[0] = new LegalDocumentCategory(
                LocalizationUtility.getLocalizedFieldName("legislation.add.category", null, null, locale));
        resultList[selectedCategory.getDepth().intValue()] = selectedCategory;

        while (selectedCategory.getParentCategory() != null) {
            resultList[selectedCategory.getParentCategory().getDepth().intValue()] =
                    selectedCategory.getParentCategory();
            selectedCategory = selectedCategory.getParentCategory();
        }
        return resultList;
    }

    private List<String> parseSearchField(String search) {
        //Pattern: 2/2008, 7 de Maio
        // 2/A/2008, 7 de Maio

        List<String> result = new ArrayList<String>();

        if (search == null || search.trim().length() == 0) {
            result.add(null);
            result.add(null);
            return result;
        }

        String[] splitAll = search.split(",");
        int lastSlash = StringUtils.lastIndexOf(splitAll[0], "/");

        if (lastSlash <= 0) {
            result.add(null);
            result.add(null);
            return result;
        }

        result.add(StringUtils.substring(splitAll[0], 0, lastSlash));
        result.add(StringUtils.substring(splitAll[0], lastSlash + 1));
        
        return result;
    }

    private String reduceField(String field) {

        int maxAllowed = Configuration.getInstance().getLegalDocumentSummaryTruncate();
        if (field.length() >= maxAllowed) {

            if (maxAllowed <= 0) {
                maxAllowed = 0;
            }

            field = field.substring(0, maxAllowed) + " (...)";
        }
        return field;
    }

    public LegalDocument getLegalDocument() {
        return legalDocument;
    }

    public void setLegalDocument(LegalDocument legalDocument) {
        this.legalDocument = legalDocument;
    }

    public FileBean getFile() {
        return file;
    }

    public void setFile(FileBean file) {
        this.file = file;
    }

    public List<LegalDocumentType> getDocumentTypeList() {
        return documentTypeList;
    }

    public void setDocumentTypeList(List<LegalDocumentType> documentTypeList) {
        this.documentTypeList = documentTypeList;
    }

    public LegislationService getLegislationService() {
        return legislationService;
    }

    public String getDrTitle() {
        return drTitle;
    }

    public void setDrTitle(String drTitle) {
        this.drTitle = drTitle;
    }

    public void setLegislationService(LegislationService legislationService) {
        this.legislationService = legislationService;
    }

    public List<LegalDocumentCategory> getDocumentCategoryList() {
        return documentCategoryList;
    }

    public void setDocumentCategoryList(List<LegalDocumentCategory> documentCategoryList) {
        this.documentCategoryList = documentCategoryList;
    }

    public List<LegalDocumentState> getDocumentStateList() {
        return documentStateList;
    }

    public void setDocumentStateList(List<LegalDocumentState> documentStateList) {
        this.documentStateList = documentStateList;
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public Set<Long> getSelectedLegalDocumentCategories() {
        return selectedLegalDocumentCategories;
    }

    public void setSelectedLegalDocumentCategories(Set<Long> selectedLegalDocumentCategories) {
        this.selectedLegalDocumentCategories = selectedLegalDocumentCategories;
    }

    public StringBuilder getLegalDocumentsSearch() {
        return legalDocumentsSearch;
    }

    public void setLegalDocumentsSearch(StringBuilder legalDocumentsSearch) {
        this.legalDocumentsSearch = legalDocumentsSearch;
    }

    public Long getLegalDocumentTypeId() {
        return legalDocumentTypeId;
    }

    public void setLegalDocumentTypeId(Long legalDocumentTypeId) {
        this.legalDocumentTypeId = legalDocumentTypeId;
    }

    public Long getDepth() {
        return depth;
    }

    public void setDepth(Long depth) {
        this.depth = depth;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public LegalDocumentCategory[] getLegalDocumentCategoryNavegation() {
        return legalDocumentCategoryNavegation;
    }

    public void setLegalDocumentCategoryNavegation(LegalDocumentCategory[] legalDocumentCategoryNavegation) {
        this.legalDocumentCategoryNavegation = legalDocumentCategoryNavegation;
    }

    public List<LegalDocumentCategory[]> getUserSubscriptions() {
        return userSubscriptions;
    }

    public void setUserSubscriptions(List<LegalDocumentCategory[]> userSubscriptions) {
        this.userSubscriptions = userSubscriptions;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    public NewsletterService getNewsLetterService() {
        return newsLetterService;
    }

    public void setNewsLetterService(NewsletterService newsLetterService) {
        this.newsLetterService = newsLetterService;
    }

    public String getSearchLegislation() {
        return searchLegislation;
    }

    public void setSearchLegislation(String searchLegislation) {
        this.searchLegislation = searchLegislation;
    }

    public List<LegalDocument> getLastInsertedLegalDocuments() {
        return lastInsertedLegalDocuments;
    }

    public void setLastInsertedLegalDocuments(List<LegalDocument> lastInsertedLegalDocuments) {
        this.lastInsertedLegalDocuments = lastInsertedLegalDocuments;
    }

    public LegalDocumentStatisticsService getLegalDocumentStatisticsService() {
        return legalDocumentStatisticsService;
    }

    public void setLegalDocumentStatisticsService(LegalDocumentStatisticsService legalDocumentStatisticsService) {
        this.legalDocumentStatisticsService = legalDocumentStatisticsService;
    }

    public Boolean getUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    public Set<LegalDocument> getLastVisualizedLegalDocuments() {
        return lastVisualizedLegalDocuments;
    }

    public void setLastVisualizedLegalDocuments(Set<LegalDocument> lastVisualizedLegalDocuments) {
        this.lastVisualizedLegalDocuments = lastVisualizedLegalDocuments;
    }

    public LegalDocumentHistoryService getLegalDocumentHistoryService() {
        return legalDocumentHistoryService;
    }

    public void setLegalDocumentHistoryService(LegalDocumentHistoryService legalDocumentHistoryService) {
        this.legalDocumentHistoryService = legalDocumentHistoryService;
    }

    public Boolean getCategorySearch() {
        return isCategorySearch;
    }

    public void setCategorySearch(Boolean categorySearch) {
        isCategorySearch = categorySearch;
    }

    public List<LegalDocumentCategory[]> getLegaDocumentCategoryNavigation() {
        return legaDocumentCategoryNavigation;
    }

    public void setLegaDocumentCategoryNavigation(List<LegalDocumentCategory[]> legaDocumentCategoryNavigation) {
        this.legaDocumentCategoryNavigation = legaDocumentCategoryNavigation;
    }

    public PaginatedListAdapter<LegalDocument> getLegalDocuments() {
        return legalDocuments;
    }

    public void setLegalDocuments(PaginatedListAdapter<LegalDocument> legalDocuments) {
        this.legalDocuments = legalDocuments;
    }

    public LegalDocumentCategory getCategory() {
        return category;
    }

    public void setCategory(LegalDocumentCategory category) {
        this.category = category;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Boolean getCanDownload() {
        return canDownload;
    }

    public void setCanDownload(Boolean canDownload) {
        this.canDownload = canDownload;
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long[] getCategoriesPerColumn() {
        return categoriesPerColumn;
    }

    public void setCategoriesPerColumn(long[] categoriesPerColumn) {
        this.categoriesPerColumn = categoriesPerColumn;
    }

    public List<LegalDocument> getLegalDocumentsToExport() {
        return legalDocumentsToExport;
    }

    public void setLegalDocumentsToExport(List<LegalDocument> legalDocumentsToExport) {
        this.legalDocumentsToExport = legalDocumentsToExport;
    }
}