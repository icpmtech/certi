<%@ page import="com.criticalsoftware.certitools.util.Configuration" %>
<%@ include file="includes/taglibs.jsp"%>

<% if (!Configuration.getInstance().getLocalInstallation()){ %>
<c:redirect url="/Home.action" />
<% } else{ %>
<c:redirect url="/FirstLogin.action" />
<% }%>