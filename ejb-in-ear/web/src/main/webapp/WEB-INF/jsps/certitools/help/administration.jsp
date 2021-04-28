<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Administração</title>
</head>

<ss:secure roles="administrator">
    <h2><span>Administração</span></h2>

    <p> A aplicação para gerir as licenças do CertiTools está disponível no endereço:<br/>
        <a href="${pageContext.request.contextPath}/license-management/">${applicationScope.configuration.applicationDomain}/license-management/</a>
    </p>
</ss:secure>
