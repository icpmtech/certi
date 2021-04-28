<%@ page import="com.criticalsoftware.certitools.business.legislation.LegislationService" %>
<%@ page import="javax.naming.InitialContext" %>
<%
    InitialContext context = new InitialContext();
    LegislationService legislationService = (LegislationService) context.lookup("certitools/LegislationService");

    legislationService.fixCategoriesCounter();
%>