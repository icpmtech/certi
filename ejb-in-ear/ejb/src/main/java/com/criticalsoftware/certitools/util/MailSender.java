/*
 * $Id: MailSender.java,v 1.28 2012/05/23 15:35:37 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/05/23 15:35:37 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.util;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.entities.LegalDocument;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.UserContract;
import com.criticalsoftware.certitools.entities.sm.EmergencyToken;
import com.criticalsoftware.certitools.util.enums.EmailEventType;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Mail Sender
 *
 * @author : lt-rico
 */
public class MailSender {

    private static final Logger LOGGER = Logger.getInstance(MailSender.class);

    private static final String CID = "logo";
    private static final String LEGAL_DOCUMENT_HREF =
            "/legislation/Legislation.action?viewLegislation=&legalDocument.id=";


    public static void sendCompanyAlertEmail(String from, String to, String subject, String body)
            throws NamingException, MessagingException, BusinessException {

        Date date = new Date();
        try {
            InitialContext initialContext = new InitialContext();
            Session session = (javax.mail.Session) initialContext.lookup("java:/Mail");

            // -- Create a new message --
            MimeMessage msg = new MimeMessage(session);
            msg.setHeader("X-Mailer", "CertiTools.Email");
            msg.setSentDate(date);

            // -- Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, to);

            // -- Set the subject text --
            msg.setSubject(subject, "UTF-8");

            // -- Set the content
            msg.setText(body, "UTF-8");

            msg.setHeader("Content-Type", "text/plain; charset=UTF-8");

            // -- Send the message --
            Transport.send(msg);
            LOGGER.info("Company Alerts Message sent at: " + date + " for [" + to + "] with the subject: " + subject);
        } catch (Exception e) {
            throw new BusinessException("Error while sending message", e);
        }
    }

    public static void sendUserInactivityWarningEmail(String email, String subject, String body) {
        try {

            ResourceBundle resources = ResourceBundle.getBundle("EJBResources");

            InitialContext initialContext = new InitialContext();
            Session session = (javax.mail.Session) initialContext.lookup("java:/Mail");

            // -- Create a new message --
            MimeMessage msg = new MimeMessage(session);
            msg.setHeader("X-Mailer", "CertiTools.Email");

            // -- Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(Configuration.getInstance().getEmailInfo(),
                    resources.getString("newsletter.emailAdress.from.personal")));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
            // -- Set the subject text --
            msg.setSubject(subject, "UTF-8");

            // -- Set the content
            msg.setText(body, "UTF-8");

            msg.setHeader("Content-Type", "text/plain; charset=UTF-8");

            // -- Send the message --
            Transport.send(msg);
            LOGGER.info("[sendUserInactivityWarningEmail] Message sent at: " + new Date() + " for [" + email + "]");
            LOGGER.info("[sendUserInactivityWarningEmail] Message body: " + body);
        } catch (Exception ex) {
            LOGGER.error("[sendUserInactivityWarningEmail] Error while sending message ", ex);
        }
    }

    /**
     * "send" method to send the message.
     *
     * @param email             the recipt email
     * @param newDocsSorted     the new documents sorted
     * @param changedDocsSorted the changed documents sorted
     * @param logo              byte array with the logo file
     * @param contentType       contentType
     */
    public static void send(String email, List<LegalDocument> newDocsSorted, List<LegalDocument> changedDocsSorted,
                            byte[] logo, String contentType) {

        Date d = new Date();
        try {

            LOGGER.debug("[send] Going to send email to [" + email + "]");
            ResourceBundle resources = ResourceBundle.getBundle("EJBResources");

            InitialContext initialContext = new InitialContext();
            Session session = (javax.mail.Session) initialContext.lookup("java:/Mail");

            // -- Create a new message --
            MimeMessage msg = new MimeMessage(session);
            msg.setHeader("X-Mailer", "CertiTools.Newsletter");
            msg.setSentDate(d);

            // -- Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(Configuration.getInstance().getEmailInfo(),
                    resources.getString("newsletter.emailAdress.from.personal")));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
            // -- Set the subject text --
            msg.setSubject(Configuration.getInstance().getNewsletterSubject(), "UTF-8");

            // -- Set the content
            msg.setContent(createContent(createTextContent(newDocsSorted, changedDocsSorted),
                    createHtmlContent(newDocsSorted, changedDocsSorted), logo, contentType));

            // -- Send the message --
            Transport.send(msg);
            LOGGER.info("Message sent at: " + d + " for [" + email + "]");
        } catch (Exception ex) {
            LOGGER.error("Error while sending message ", ex);
        }
    }

    /**
     * Sends the Activation Email for a User
     *
     * @param user user to send the activation email
     */
    public static void sendActivationEmail(User user) {
        try {

            LOGGER.info("[sendActivationEmail] Going to send email to [" + user.getEmailContact() + "]");
            ResourceBundle resources = ResourceBundle.getBundle("EJBResources");

            InitialContext initialContext = new InitialContext();
            Session session = (javax.mail.Session) initialContext.lookup("java:/Mail");

            // -- Create a new message --
            MimeMessage msg = new MimeMessage(session);
            msg.setHeader("X-Mailer", "CertiTools.Email");

            // -- Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(Configuration.getInstance().getEmailInfo(),
                    resources.getString("newsletter.emailAdress.from.personal")));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmailContact(), false));
            // -- Set the subject text --
            msg.setSubject(Configuration.getInstance().getActivationEmailSubject(), "UTF-8");

            // -- Set the content
            msg.setText(createActivationEmailMessage(user), "UTF-8");

            msg.setHeader("Content-Type", "text/plain; charset=UTF-8");

            // -- Send the message --
            Transport.send(msg);
            LOGGER.info("[sendActivationEmail] Message sent at: " + new Date() + " for [" + user.getEmailContact() + "]");
            LOGGER.debug("[sendActivationEmail] Message body: " + createActivationEmailMessage(user));
        } catch (Exception ex) {
            LOGGER.error("[sendActivationEmail] Error while sending message ", ex);
        }
    }

    private static String createActivationEmailMessage(User user) {
        String url = Configuration.getInstance().getApplicationDomain() + "/UserActivation.action?"
                + "user.id=" + user.getId() + "&uid=" + user.getActivationKey();

        LOGGER.info("[sendActivationEmail] URL activation: " + url);

        StringBuffer text = new StringBuffer();
        text.append(Configuration.getInstance().getActivationEmailMessageHeader());
        text.append("\n").append(url).append("\n\n");
        text.append(Configuration.getInstance().getActivationEmailMessageFooter());
        text.append("\n\nEntidade: ").append(user.getCompany().getName());
        if (user.getUserContract() != null) {
            text.append("\nContrato(s): ");
            for (UserContract userContract : user.getUserContract()) {
                text.append(userContract.getContract().getContractDesignation()).append("; ");
            }
        }
        //Fiscal Number - codigo de seguranca
        text.append("\n").append("C\u00f3digo de Seguran\u00e7a: ");
        text.append(user.getFiscalNumber());
        return text.toString();
    }


    private static Multipart createContent(String bodyText, String bodyHtml, byte[] logo, String contentType)
            throws MessagingException, UnsupportedEncodingException {
        // Create a "htmlPart" Multipart message
        Multipart theAllPart = new MimeMultipart("alternative");

        // Set text version email for old clients
        BodyPart plainTextPart = new MimeBodyPart();
        plainTextPart.setDataHandler(new DataHandler(new ByteArrayDataSource(bodyText.getBytes(), "text/plain")));
        theAllPart.addBodyPart(plainTextPart);

        Multipart htmlPart = new MimeMultipart("related");

        // Set the HTML one
        BodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(bodyHtml.getBytes(), "text/html")));
        // and add it to the message.
        htmlPart.addBodyPart(htmlBodyPart);

        if (logo != null) {
            // Get the image file
            MimeBodyPart image = new MimeBodyPart();

            // Initialize and add the image file to the html body part
            image.setFileName("logo");
            image.setText(CID);
            image.setDataHandler(new DataHandler(new ByteArrayDataSource(logo, contentType)));
            image.setHeader("Content-ID", "<" + CID + ">");
            image.setDisposition("inline");
            // and add it to the message.
            htmlPart.addBodyPart(image);
        }

        //Make magic and put it all together
        BodyPart alternativeBodypart2 = new MimeBodyPart();
        alternativeBodypart2.setContent(htmlPart);
        theAllPart.addBodyPart(alternativeBodypart2);

        return theAllPart;
    }

    private static String createTextContent(List<LegalDocument> newDocs, List<LegalDocument> changedDocs) {
        StringBuilder builder = new StringBuilder();

        if (newDocs != null && newDocs.size() > 0) {
            builder.append(createMessage("newsletter.legal.document.new")).append("\n\n");
            for (LegalDocument ld : newDocs) {
                createStringInText(builder, ld);
            }
        }
        if (changedDocs != null && changedDocs.size() > 0) {
            builder.append(createMessage("newsletter.legal.document.changed")).append("\n\n");
            for (LegalDocument ld : changedDocs) {
                createStringInText(builder, ld);
            }
        }
        builder.append("\n");

        return builder.toString();
    }

    private static void createStringInText(StringBuilder builder, LegalDocument ld) {
        builder.append(ld.getCustomTitle()).append("\n");

        builder.append(ld.getFullDrTitle()).append("\n");
        builder.append(Configuration.getInstance().getApplicationDomain()).append(LEGAL_DOCUMENT_HREF)
                .append(ld.getId()).append("\n");
        try {
            builder.append(
                    ld.getSummary().substring(0,
                            Configuration.getInstance().getLegalDocumentSummaryTruncate()))
                    .append(" (...)").append("\n");
        } catch (IndexOutOfBoundsException e) {
            builder.append(ld.getSummary()).append("\n");
        }
        builder.append("\n\n");
    }

    private static String createHtmlContent(List<LegalDocument> newDocs, List<LegalDocument> changedDocs) {
        StringBuilder builder = new StringBuilder();

        if (newDocs != null && newDocs.size() > 0) {
            builder.append("<h2>").append(createMessage("newsletter.legal.document.new"))
                    .append("</h2>");
            for (LegalDocument ld : newDocs) {
                createStringInHtml(builder, ld);
            }
        }
        builder.append("<br/>");

        if (changedDocs != null && changedDocs.size() > 0) {
            builder.append("<h2>").append(createMessage("newsletter.legal.document.changed"))
                    .append("</h2>");
            for (LegalDocument ld : changedDocs) {
                createStringInHtml(builder, ld);
            }
        }
        builder.append("<br/>");

        return createMessage("newsletter.html.formt",
                Configuration.getInstance().getNewsletterHeader(),
                CID,
                builder.toString(),
                Configuration.getInstance().getNewsletterFooter());
    }

    private static void createStringInHtml(StringBuilder builder, LegalDocument ld) {
        builder.append("<strong style=\"color:#38586C;font-size: 14px;\">").append(ld.getCustomTitle())
                .append("</strong><br/>");

        builder.append(createMessage("newsletter.link.src", Configuration.getInstance().getApplicationDomain() +
                LEGAL_DOCUMENT_HREF + ld.getId(), ld.getFullDrTitle()));
        builder.append("<br/>");
        try {
            builder.append(
                    ld.getSummary().substring(0, Configuration.getInstance().getLegalDocumentSummaryTruncate()))
                    .append(" (...)").append("<br/>");
        } catch (IndexOutOfBoundsException e) {
            builder.append(ld.getSummary()).append("<br/>");
        }
        builder.append("<br/><br/>");
    }

    private static String createMessage(String messageKey, String... parameter) {
        ResourceBundle resources = ResourceBundle.getBundle("EJBResources");
        String message = resources.getString(messageKey);
        int i = 0;
        for (String o : parameter) {
            message = message.replaceAll("\\{" + i + "\\}", o);
            i += 1;
        }
        return message;
    }

    public static void sendSmRecurrenceNotificationEmails(String eventName, Integer numberDays, String url,
                                                          String company, String contract, List<User> users) {
        String subject = Configuration.getInstance().getSmRecurrenceNotificationEmailSubject();
        String body = MessageFormat.format(Configuration.getInstance().getSmRecurrenceNotificationEmailBody(),
                numberDays.toString(), eventName, company, contract, url);
        for (User user : users) {
            sendEmail(user.getEmailContact(), subject, body, "Recurrence_Email");
        }
    }

    public static void sendSmEventCreatedEmails(EmailEventType eventType, String eventName, String url,
                                                String company, String contract, List<User> users) {
        ResourceBundle resources = ResourceBundle.getBundle("EJBResources");

        String subject = MessageFormat.format(Configuration.getInstance().getSmEventCreatedEmailSubject(),
                resources.getString(eventType.getResourceKey()));
        String body = MessageFormat.format(Configuration.getInstance().getSmEventCreatedEmailBody(),
                resources.getString(eventType.getResourceKey()) + " - " + eventName, company, contract, url);
        for (User user : users) {
            sendEmail(user.getEmailContact(), subject, body, eventName.replaceAll("\\s+", ""));
        }
    }

    public static void sendSmEventEditEmails(EmailEventType eventType, String eventName, String url,
                                                String company, String contract, List<User> users) {
        ResourceBundle resources = ResourceBundle.getBundle("EJBResources");

        String subject = MessageFormat.format(Configuration.getInstance().getSmEventEditEmailSubject(),
                resources.getString(eventType.getResourceKey()));
        String body = MessageFormat.format(Configuration.getInstance().getSmEventEditEmailBody(),
                resources.getString(eventType.getResourceKey()) + " - " + eventName, company, contract, url);
        for (User user : users) {
            sendEmail(user.getEmailContact(), subject, body, eventName.replaceAll("\\s+", ""));
        }
    }

    public static void sendSmEventClosedEmails(EmailEventType eventType, String eventName, String url,
                                               String company, String contract, List<User> users) {
        ResourceBundle resources = ResourceBundle.getBundle("EJBResources");

        String subject = MessageFormat.format(Configuration.getInstance().getSmEventClosedEmailSubject(),
                resources.getString(eventType.getResourceKey()));
        String body = MessageFormat.format(Configuration.getInstance().getSmEventClosedEmailBody(),
                resources.getString(eventType.getResourceKey()) + " - " + eventName, company, contract, url);
        for (User user : users) {
            sendEmail(user.getEmailContact(), subject, body, eventName.replaceAll("\\s+", ""));
        }
    }

    public static void sendSmEmergencyActionCreatedEmails(String emergencyCode, String url,
                                                          String company, String contract,
                                                          List<EmergencyToken> emergencyTokens) {
        ResourceBundle resources = ResourceBundle.getBundle("EJBResources");

        url = url + "&token=";
        String subject = MessageFormat.format(Configuration.getInstance().getSmEventCreatedEmailSubject(),
                resources.getString(EmailEventType.EMERGENCY_ACTION.getResourceKey()));

        for (EmergencyToken et : emergencyTokens) {
            String email = et.getUser() != null ? et.getUser().getEmailContact() : et.getEmergencyUser().getEmail();
            //TODO

            String body = MessageFormat.format(Configuration.getInstance().getSmEventCreatedEmailBody(),
                    resources.getString(EmailEventType.EMERGENCY_ACTION.getResourceKey()) + " - " + emergencyCode,
                    company, contract, url + et.getAccessToken());

            sendEmail(email, subject, body, emergencyCode.replaceAll("\\s+", ""));
        }
    }

    public static void sendSmChatNotificationEmails(EmailEventType eventType, String eventName, String url,
                                                    String company, String contract, List<User> users) {
        ResourceBundle resources = ResourceBundle.getBundle("EJBResources");

        String subject = MessageFormat.format(Configuration.getInstance().getSmChatNotificationEmailSubject(),
                resources.getString(eventType.getResourceKey()));
        String body = MessageFormat.format(Configuration.getInstance().getSmChatNotificationEmailBody(),
                resources.getString(eventType.getResourceKey()) + " - " + eventName, company, contract, url);
        for (User user : users) {
            sendEmail(user.getEmailContact(), subject, body, eventName.replaceAll("\\s+", ""));
        }
    }

    private static void sendEmail(String email, String subject, String body, String eventType) {
        try {

            LOGGER.debug("[sendEmail] Going to send email to [" + email + "]");
            ResourceBundle resources = ResourceBundle.getBundle("EJBResources");

            InitialContext initialContext = new InitialContext();
            Session session = (javax.mail.Session) initialContext.lookup("java:/Mail");

            // -- Create a new message --
            MimeMessage msg = new MimeMessage(session);
            msg.setHeader("X-Mailer", "CertiTools.Email");

            // -- Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(Configuration.getInstance().getEmailInfo(),
                    resources.getString("newsletter.emailAdress.from.personal")));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
            // -- Set the subject text --
            msg.setSubject(subject, "UTF-8");

            // -- Set the content
            msg.setText(body, "UTF-8");

            msg.setHeader("Content-Type", "text/plain; charset=UTF-8");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
            Date date = new Date();

            //Save the email as a file to the /tmp folder
            //String emailFileName = "/tmp/" + eventType + "-" + formatter.format(date) + ".eml";
            //OutputStream os = new FileOutputStream(emailFileName);
            //msg.writeTo(os);

            // -- Send the message --
            Transport.send(msg);
            LOGGER.info("[sendSmNotificationEmail] Message sent at: " + new Date() + " for [" + email + "]");
            LOGGER.info("[sendSmNotificationEmail] Message subject: " + subject);
        } catch (Exception ex) {
            LOGGER.error("[sendSmNotificationEmail] Error while sending message ", ex);
        }
    }
}
