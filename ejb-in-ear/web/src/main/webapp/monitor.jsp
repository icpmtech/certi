<%@ page import="com.criticalsoftware.certitools.business.certitools.MonitorService" %>
<%@ page import="com.criticalsoftware.certitools.entities.LegalDocumentCategory" %>
<%@ page import="com.criticalsoftware.certitools.util.File" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="java.util.List" %>
<%@ page import="com.criticalsoftware.certitools.util.Logger" %>
<%@ include file="includes/taglibs.jsp" %>

<%
    try {
        InitialContext context = new InitialContext();
        MonitorService monitorService = (MonitorService) context.lookup("certitools/MonitorService");

        List<LegalDocumentCategory> categories = monitorService.findLegalDocumentCategoriesByDepthAndId(null, null);

        File logo = monitorService.findNewsletterLogo();

        if (logo == null) {
            throw new Exception("Newsletter Logo not found");
        }

        if (categories == null || categories.size() <= 0) {
            throw new Exception("Legal Document Categories <= 0");
        }

    } catch (Throwable e) {
        Logger log = Logger.getInstance(this.getClass());
        log.error(e.toString());
        response.sendError(500, "Exception Error");
    }

    out.println("OK");
%>