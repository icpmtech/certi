<%@ page import="javax.naming.InitialContext" %>
<%@ page import="com.criticalsoftware.certitools.business.plan.PlanService" %>
<%
    InitialContext context = new InitialContext();
    PlanService planService = (PlanService) context.lookup("certitools/PlanService");

    planService.updateToVersion3();
%>