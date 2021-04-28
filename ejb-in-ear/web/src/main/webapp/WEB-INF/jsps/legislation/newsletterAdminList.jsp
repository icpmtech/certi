<%@ include file="../../../includes/taglibs.jsp" %>

<html>
    <head>
        <title><fmt:message key="newsletter.admin.title" /></title>
    </head>
    <body>
        <h1><fmt:message key="newsletter.admin.title"/></h1>

        <s:messages/>

        <div class="links">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.NewsletterActionBean" event="updateForm" class="operationEdit">
                <fmt:message key="common.edit"/>
            </s:link>
        </div>

        <display:table list="${requestScope.actionBean.configurations}" export="false" id="displaytable"
                       class="displaytag"
                       uid="conf"
                       requestURI="/legislation/Newsletter.action">
            <display:column titleKey="configuration.key">
                <fmt:message key="configuration.${pageScope.conf.key}"/>
            </display:column>
            <display:column titleKey="configuration.value">
                <c:choose>
                    <c:when test="${pageScope.conf.key == 'certitools.legal.document.newsletter.logo'}">
                        <img src="${pageContext.request.contextPath}/legislation/Newsletter.action?getLogo=" alt="" />
                    </c:when>
                    <c:otherwise>
                        <c:out value="${pageScope.conf.value}"/>
                    </c:otherwise>
                </c:choose>
            </display:column>
        </display:table>
    </body>
</html>
