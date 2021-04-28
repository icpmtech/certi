<%@ include file="../../../includes/taglibs.jsp" %>

<html>
<head>
    <title><fmt:message key="configuration.title"/></title>
</head>
<body>
<h1><fmt:message key="configuration.title"/></h1>

<s:messages/>

<div class="links">
    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.ConfigurationActionBean"
            event="updateForm" class="operationEdit">
        <fmt:message key="common.edit"/>
    </s:link>
    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.MasterPasswordActionBean"
            event="updateForm" class="operationEdit">
        <fmt:message key="masterpassword.edit"/>
    </s:link>
</div>

<display:table list="${requestScope.actionBean.configurations}" export="false" id="displaytable"
               class="displaytag"
               uid="conf"
               requestURI="/license/License.action">
    <display:column titleKey="configuration.key">
        <fmt:message key="configuration.${pageScope.conf.key}"/>
    </display:column>
    <display:column titleKey="configuration.value" property="value"/>
</display:table>

<p>&nbsp;</p>

<h2 class="formBig"><span><fmt:message key="masterpassword.password"/></span></h2>
<c:choose>
    <c:when test="${requestScope.actionBean.masterPasswordActive}">
        <p>
            <fmt:message key="masterpassword.active" >
                <fmt:param><strong><fmt:formatDate value="${requestScope.actionBean.masterPasswordExpiry}" pattern="${applicationScope.configuration.dateHourPattern}"/></strong></fmt:param>
                <fmt:param><strong>${requestScope.actionBean.masterPasswordAuthor}</strong></fmt:param>
            </fmt:message>
        </p>
    </c:when>
    <c:otherwise>
        <p><fmt:message key="masterpassword.inactive"/></p>
    </c:otherwise>
</c:choose>


</body>
</html>