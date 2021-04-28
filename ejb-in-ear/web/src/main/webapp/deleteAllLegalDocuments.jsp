<%@ include file="includes/taglibs.jsp" %>
<%@ page import="com.criticalsoftware.certitools.business.legislation.LegislationService" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.criticalsoftware.certitools.entities.LegalDocument" %>

<%

    if (request.isUserInRole("legislationmanager")) {
        System.out.println("Deleting all legal documents");

        InitialContext context = new InitialContext();
        LegislationService legislationService = (LegislationService) context.lookup("certitools/LegislationService");

        java.util.Collection<com.criticalsoftware.certitools.entities.LegalDocument> legalDocuments = legislationService.findAll();
        for (com.criticalsoftware.certitools.entities.LegalDocument legalDocument : legalDocuments) {
            legislationService.deleteLegalDocument(legalDocument.getId());
        }
        System.out.println("all deleted");
    }
%>