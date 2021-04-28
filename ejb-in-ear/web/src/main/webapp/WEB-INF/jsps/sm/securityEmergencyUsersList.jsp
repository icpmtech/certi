<%@ include file="../../../includes/taglibs.jsp" %>

<s:messages/>

<head>
    <title><fmt:message key="companies.contracts"/> &gt;${pageScope.title}</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery-ui-1.7.custom.css"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/datepicker-${pageContext.request.locale}.js"></script>

    <style>
        input[type=text] {
            padding-left: 5px;
        }
    </style>
</head>

<h2 class="form cleaner"><span><fmt:message key="common.add"/></span></h2>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean"
        class="form leftSideContent" focus="" style="width: 100%;">
    <s:errors/>
    <p>
        <s:label for="name"><fmt:message key="user.name"/> (*):</s:label>
        <s:text id="name" name="emergencyUser.name" class="mediumInput"/>
    </p>

    <p>
        <s:label for="email"><fmt:message key="user.email"/> (*):</s:label>
        <s:text id="email" name="emergencyUser.email" class="mediumInput"/>
    </p>

    <div class="formButtons">
        <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
        <s:submit name="insertEmergencyUser" class="button"><fmt:message key="common.submit"/></s:submit>
        <s:submit name="emergencyActionGrid" class="button"><fmt:message key="common.cancel"/></s:submit>
    </div>
</s:form>

<h2 class="form cleaner"><span><fmt:message key="security.emergency.users.title"/></span></h2>

<c:choose>
    <c:when test="${fn:length(requestScope.actionBean.emergencyUsers) > 0}">
        <display:table list="${requestScope.actionBean.emergencyUsers}" export="false" id="displaytable"
                       class="displaytag" uid="user" requestURI="/sm/SecurityEmergency.action">

            <display:column property="name" titleKey="user.name" escapeXml="true"/>

            <display:column property="email" titleKey="user.email" escapeXml="true"/>

            <display:column class="oneButtonColumnWidth" media="html">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean"
                        event="deleteEmergencyUser" class="confirmDelete">
                    <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                    <s:param name="emergencyUserId" value="${pageScope.user.id}"/>

                    <img src="${pageContext.request.contextPath}/images/Eliminar.png"
                         title="<fmt:message key="common.delete"/>"
                         alt="<fmt:message key="common.delete"/>"/></s:link>
            </display:column>

        </display:table>
    </c:when>
    <c:otherwise>
        <fmt:message key="security.emergency.users.empty"/>
    </c:otherwise>
</c:choose>
