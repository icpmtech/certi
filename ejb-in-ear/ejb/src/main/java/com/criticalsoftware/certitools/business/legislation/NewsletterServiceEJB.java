/*
 * $Id: NewsletterServiceEJB.java,v 1.26 2010/05/26 18:15:27 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/05/26 18:15:27 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.business.legislation;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.persistence.certitools.ConfigurationDAO;
import com.criticalsoftware.certitools.persistence.certitools.RepositoryDAO;
import com.criticalsoftware.certitools.persistence.certitools.UserDAO;
import com.criticalsoftware.certitools.persistence.legislation.LegislationDAO;
import com.criticalsoftware.certitools.entities.Configuration;
import com.criticalsoftware.certitools.entities.LegalDocument;
import com.criticalsoftware.certitools.entities.LegalDocumentCategory;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.*;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Newsletter service EJB
 *
 * @author : lt-rico
 */
@Stateless
@Local(NewsletterService.class)
@LocalBinding(jndiBinding = "certitools/NewsletterService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class NewsletterServiceEJB implements NewsletterService {

    private static final Logger LOGGER = Logger.getInstance(NewsletterServiceEJB.class);

    @EJB
    private ConfigurationDAO configurationDAO;

    @EJB
    private RepositoryDAO repositoryDAO;

    @EJB
    private LegislationDAO legislationDAO;

    @EJB
    private LegislationService legislationService;

    @EJB
    private UserDAO userDAO;

    @RolesAllowed(value = {"legislationmanager", "administrator"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public Collection<Configuration> findNewsletterConfigurations() {
        Collection<Configuration> newsletterConfigurations = new ArrayList<Configuration>();
        newsletterConfigurations
                .add(configurationDAO.findById(ConfigurationProperties.LEGAL_DOCUMENT_NEWSLETTER_SUBJECT.getKey()));
        newsletterConfigurations
                .add(configurationDAO.findById(ConfigurationProperties.LEGAL_DOCUMENT_NEWSLETTER_HEADER.getKey()));
        newsletterConfigurations
                .add(configurationDAO.findById(ConfigurationProperties.LEGAL_DOCUMENT_NEWSLETTER_FOOTER.getKey()));
        return newsletterConfigurations;
    }

    @RolesAllowed(value = {"legislationmanager", "administrator"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(Collection<Configuration> configurations, String contentType,
                       InputStream inputStream) throws BusinessException, ObjectNotFoundException {
        if (configurations == null || configurations.size() == 0) {
            throw new BusinessException("Invalid collection of CONFIGURATION objects parameters");
        }

        for (Configuration conf : configurations) {
            Configuration toBeUpdated = configurationDAO.findById(conf.getKey());
            toBeUpdated.setValue(conf.getValue());
        }
        com.criticalsoftware.certitools.util.Configuration.reloadInstance();

        if (contentType != null && contentType.length() > 0 && inputStream != null) {
            try {
                repositoryDAO.insertFileOnFolder(RepositoryDAO.Folder.NEWSLETTER_FOLDER,
                        new File(RepositoryDAO.NEWSLETTER_LOGO_ID, contentType, inputStream));
            } catch (JackrabbitException e) {
                throw new BusinessException("Error while saving file in repository", e);
            }
        }
    }

    @RolesAllowed(value = "private")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void sendNewsletters() {
        LOGGER.info("[sendNewsletters] Starting method");
        Collection<User> users = userDAO.findAllForNewsletter();

        List<LegalDocument> newLegalDocuments = new ArrayList<LegalDocument>();
        List<LegalDocument> changedLegalDocuments = new ArrayList<LegalDocument>();
        File logo;
        byte[] logoBytes;

        try {
            logo = findNewsletterLogo();

            int bufferSize = logo.getData().available();
            byte[] buffer = new byte[bufferSize];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int bytesread;
            while (true) {
                bytesread = logo.getData().read(buffer);
                if (bytesread == -1) {
                    break;
                }
                baos.write(buffer, 0, bytesread);
            }
            logoBytes = baos.toByteArray();
        } catch (ObjectNotFoundException e) {
            LOGGER.error("Error reading logo", e);
            return;
        } catch (IOException e) {
            LOGGER.error("Error reading logo", e);
            return;
        }

        LOGGER.info("[sendNewsletters] Start - send email to users");
        for (User u : users) {
            // check if user can access legislation
            User userInDb = userDAO.findByIdWithContractsRoles(u.getId());

            if (userInDb.validateModuleAccess(ModuleType.LEGISLATION)) {
                for (LegalDocumentCategory category : u.getSubscriptionsLegalDocuments()) {
                    for (LegalDocument legalDocument : category.getLegalDocuments()) {
                        if (legalDocument.isSendNotificationNew()) {
                            if (!newLegalDocuments.contains(legalDocument)) {
                                newLegalDocuments.add(legalDocument);
                            }
                        } else if (legalDocument.isSendNotificationChange()) {
                            if (!changedLegalDocuments.contains(legalDocument)) {
                                changedLegalDocuments.add(legalDocument);
                            }
                        }
                    }
                }

                MailSender.send(u.getEmailContact(), sortLegalDocs(newLegalDocuments), sortLegalDocs(changedLegalDocuments),
                        logoBytes, logo.getContentType());
            }
        }
        LOGGER.info("[sendNewsletters] emails sent");
        LOGGER.info("[sendNewsletters] Start - reseting legal documents");

        //Reset the notification system
        for (User u : users) {
            for (LegalDocumentCategory category : u.getSubscriptionsLegalDocuments()) {
                for (LegalDocument legalDocument : category.getLegalDocuments()) {
                    if (legalDocument.isSendNotificationNew()) {
                        legalDocument.setSendNotificationNew(false);
                    } else {
                        legalDocument.setSendNotificationChange(false);
                    }
                }
            }
        }
        LOGGER.info("[sendNewsletters] end method");
    }

    @RolesAllowed(value = "user")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void subscribe(Long userId, Long legalDocumentCategoryId)
            throws BusinessException, ObjectNotFoundException {
        if (userId == null) {
            throw new BusinessException("Invalid parameter");
        }
        User u = userDAO.findById(userId);
        if (u == null) {
            throw new ObjectNotFoundException("Object not found ", ObjectNotFoundException.Type.USER);
        }
        LegalDocumentCategory cat = legislationDAO.findLegalDocumentCategoryById(legalDocumentCategoryId);
        if (cat == null) {
            throw new ObjectNotFoundException("Object not found ",
                    ObjectNotFoundException.Type.LEGAL_DOCUMENT_CATEGORY);
        }
        if (u.getSubscriptionsLegalDocuments() == null) {
            Collection<LegalDocumentCategory> categories = new ArrayList<LegalDocumentCategory>();
            u.setSubscriptionsLegalDocuments(categories);
        } else if (u.getSubscriptionsLegalDocuments().contains(cat)) {
            throw new BusinessException("User allready subscrives the category");
        }

        List<LegalDocumentCategory> currentCategory = new ArrayList<LegalDocumentCategory>();
        currentCategory.add(cat);

        //Get all chidren
        Set<LegalDocumentCategory> children =
                new HashSet<LegalDocumentCategory>(
                        legislationService.findAllLegalDocumentCategoryForTree(currentCategory));


        List<LegalDocumentCategory> currentLegalDocumentCategories =
                new ArrayList<LegalDocumentCategory>(u.getSubscriptionsLegalDocuments());

        //Remove repeated children categories  
        for (LegalDocumentCategory ldc : children) {

            if (currentLegalDocumentCategories.contains(ldc)) {
                currentLegalDocumentCategories.remove(ldc);
            }
        }
        currentLegalDocumentCategories.add(cat);
        u.setSubscriptionsLegalDocuments(currentLegalDocumentCategories);
    }

    @RolesAllowed(value = "user")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void unsubscribe(Long userId, Long legalDocumentCategoryId)
            throws BusinessException, ObjectNotFoundException {
        if (userId == null) {
            throw new BusinessException("Invalid parameter");
        }
        User u = userDAO.findById(userId);
        if (u == null) {
            throw new ObjectNotFoundException("Object not found ", ObjectNotFoundException.Type.USER);
        }
        LegalDocumentCategory cat = legislationDAO.findLegalDocumentCategoryById(legalDocumentCategoryId);
        if (cat == null) {
            throw new ObjectNotFoundException("Object not found ",
                    ObjectNotFoundException.Type.LEGAL_DOCUMENT_CATEGORY);
        }
        if (u.getSubscriptionsLegalDocuments() == null || !u.getSubscriptionsLegalDocuments().contains(cat)) {
            throw new BusinessException("Cannot unsubscribe user from legal document category newsletter");
        }
        u.getSubscriptionsLegalDocuments().remove(cat);
    }

    @RolesAllowed(value = {"private", "legislationmanager", "administrator"})
    @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
    public File findNewsletterLogo() throws ObjectNotFoundException {
        try {
            return repositoryDAO
                    .findFileOnFolder(RepositoryDAO.Folder.NEWSLETTER_FOLDER, RepositoryDAO.NEWSLETTER_LOGO_ID);
        } catch (JackrabbitException e) {
            throw new ObjectNotFoundException("Object was not found on repository", e,
                    ObjectNotFoundException.Type.REPOSITORY_FILE);
        }
    }

    private static List<LegalDocument> sortLegalDocs(Collection<LegalDocument> legalDocuments) {
        List<LegalDocument> docs = null;
        if (legalDocuments != null && legalDocuments.size() > 0) {
            docs = new ArrayList<LegalDocument>();
            for (LegalDocument d : legalDocuments) {
                docs.add(d);
            }
            Collections.sort(docs, new LegalDocumentComparator());
        }
        return docs;
    }
}
