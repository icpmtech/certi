<%@ include file="../../../includes/taglibs.jsp" %>
<%@ include file="../../../includes/planSetTitle.jsp" %>

<html>
<head>
    <title>${pageScope.planTitle}</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/plan/base.css" media="screen"/>
    <link rel="stylesheet" type="text/css"
             href="${pageContext.request.contextPath}/styles/plan/${fn:toLowerCase(requestScope.actionBean.planModuleType)}.css" media="screen"/>
    
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/hoverIntent.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/supersubs.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/superfish.js"></script>

    <script type="text/javascript">
        jQuery(function() {
            jQuery('ul.sf-menu').supersubs({

            maxWidth:    25,   // maximum width of sub-menus in em units
            extraWidth:  1     // extra width can ensure lines don't sometimes turn over
                               // due to slight rounding differences and font-family
        }).superfish({
                animation:   { show:"show", speed: 0},
                delay: 1000

            });
        });
        $(document).click(function() {
            $('ul.sf-menu').hideSuperfishUl();
        });
    </script>

</head>
<body>

<table class="peiTableTitle cleaner" cellpadding="0" cellspacing="0">
    <tr>
        <td>
            <h1 class="peiH1"><c:out value="${requestScope.actionBean.pei.planNameOnline}"/></h1>
        </td>
        <td rowspan="2" class="peiCompanyLogo">
            <c:if test="${requestScope.actionBean.pei.companyLogoOnline != null || (sessionScope.userPEIPreview && requestScope.actionBean.pei.companyLogo != null)}">
                <img src="${pageContext.request.contextPath}/plan/Plan.action?planModuleType=${requestScope.actionBean.planModuleType}&viewCompanyLogo&amp;peiId=${requestScope.actionBean.pei.name}"
                     alt="" title=""/>
            </c:if>
        </td>
    </tr>
    <tr>
        <td class="peiBreadcrumbs">
            <c:choose>
                <c:when test="${requestScope.actionBean.breadcrumbs == null}">
                    <!--<fmt:message key="pei.cover"/>-->
                    &nbsp;
                </c:when>
                <c:otherwise>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
                            event="viewPEI">
                        <s:param name="peiId">${requestScope.actionBean.peiId}</s:param>
                        <fmt:message key="pei.cover"/>
                        <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
                    </s:link>
                    &gt;
                </c:otherwise>
            </c:choose>

            <c:forEach items="${requestScope.actionBean.breadcrumbs}" var="breadcrumb"
                       varStatus="statusBreadcrumb">
                <c:choose>
                    <c:when test="${!statusBreadcrumb.last}">
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
                                event="viewResource">
                            <s:param name="path">${breadcrumb.path}</s:param>
                            <s:param name="peiId">${requestScope.actionBean.peiId}</s:param>
                            <c:out value="${breadcrumb.name}"/>
                            <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
                        </s:link>
                        &gt;
                    </c:when>
                    <c:otherwise>
                        <c:out value="${breadcrumb.name}"/>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            &nbsp;
        </td>
    </tr>
</table>

<c:if test="${sessionScope.userPEIPreview}">
    <div class="alertPreview cleaner">
        <fmt:message key="pei.viewingPreview"/>

        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
                event="cancelPEIPreview">
            <s:param name="peiId" value="${requestScope.actionBean.peiId}"/>
            <s:param name="path" value="${requestScope.actionBean.folder.path}"/>
            <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
            <fmt:message key="pei.viewingPreviewLink"/>
        </s:link>
    </div>
</c:if>

<div class="peiMenuContainer">
<div class="peiMenu">
    <c:set var="lastDepth" value="1"/>
    <c:set var="numberSections" value="0"/>
    <ul class="sf-menu">

        <c:forEach items="${requestScope.actionBean.peiTreeNodes}" var="peiNode" varStatus="i">

        <c:if test="${peiNode.depth == 1}">
            <c:set var="numberSections" value="${numberSections+1}"/>
        </c:if>

        <c:choose>
            <c:when test="${peiNode.depth > lastDepth}">
                <ul>
            </c:when>
            <c:when test="${peiNode.depth < lastDepth}">
                <c:forEach begin="${peiNode.depth}" end="${lastDepth-1}">
                    </li>
                    </ul>
                </c:forEach>
                </li>
            </c:when>
            <c:when test="${!i.first}">
                </li>
            </c:when>
        </c:choose>

        <li <c:if test="${numberSections == 9}">class="peiMenuLast"</c:if>>
            <c:choose>
                <c:when test="${!peiNode.accessAllowed}">
                    <a href="" onclick="return false;" class="peiMenuAccessDenied"><c:out value="${peiNode.name}"/></a>
                </c:when>
                <c:when test="${requestScope.actionBean.sectionFolder != null && peiNode.pathURL != null && peiNode.pathURL == requestScope.actionBean.sectionFolder}">
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
                            event="viewResource" class="selectedSection">
                        <s:param name="path">${peiNode.pathURL}</s:param>
                        <s:param name="peiId">${requestScope.actionBean.peiId}</s:param>
                        <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
                        <c:out value="${peiNode.name}"/>
                    </s:link>
                </c:when>
                <c:otherwise>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
                            event="viewResource">
                        <s:param name="path">${peiNode.pathURL}</s:param>
                        <s:param name="peiId">${requestScope.actionBean.peiId}</s:param>
                        <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
                        <c:out value="${peiNode.name}"/>
                    </s:link>
                </c:otherwise>
            </c:choose>

            <c:set var="lastDepth" value="${peiNode.depth}"/>

            <c:if test="${i.last}">

            <c:forEach begin="1" end="${lastDepth-1}">
        </li>
        <!-- last special-->
    </ul>
    </c:forEach>
    </li>
    </c:if>

    </c:forEach>
    </ul><!-- last-->

</div>
</div>

<div class="peiContent cleaner">
    <table class="peiCoverTable" cellspacing="0" cellpadding="0">
        <tr>
            <td class="title"><h2>${requestScope.actionBean.pei.planNameOnline}</h2></td>
            <td class="image" rowspan="2">
                <c:if test="${requestScope.actionBean.pei.installationPhotoOnline != null ||
                (sessionScope.userPEIPreview && requestScope.actionBean.pei.installationPhoto != null)}">
                    <p>
                        <img src="${pageContext.request.contextPath}/plan/Plan.action?planModuleType=${requestScope.actionBean.planModuleType}&viewInstallationPhoto&amp;peiId=${requestScope.actionBean.pei.name}"
                             alt="${requestScope.actionBean.pei.planNameOnline}"
                             title="${requestScope.actionBean.pei.planNameOnline}"/>
                    </p>
                </c:if>
            </td>
        </tr>
        <tr>
            <td class="coverData">
                <p><span class="color-label"><fmt:message
                        key="pei.version"/>:</span> ${requestScope.actionBean.pei.versionOnline}</p>

                <p><span class="color-label"><fmt:message key="pei.versionDate"/>:</span>
                    <fmt:formatDate value="${requestScope.actionBean.pei.versionDateOnline}"
                                    pattern="${applicationScope.configuration.datePattern}"/>
                </p>


                <c:if test="${requestScope.actionBean.pei.simulationDateOnline != null}">
                    <p><span class="color-label"><fmt:message key="pei.simulationDate"/>:</span>
                        <fmt:formatDate value="${requestScope.actionBean.pei.simulationDateOnline}"
                                        pattern="${applicationScope.configuration.datePattern}"/>
                    </p>
                </c:if>

                <p><span class="color-label"><fmt:message
                        key="pei.authorName"/>:</span> ${requestScope.actionBean.pei.authorNameOnline}</p>
            </td>
        </tr>
    </table>
</div>

<div class="cleaner">
    <p>&nbsp;</p>

    <p>&nbsp;</p>
</div>


<c:if test="${requestScope.actionBean.planManager && !sessionScope.userPEIPreview}">
    <div class="peiCMLink">
        <img src="${pageContext.request.contextPath}/images/Editar.png" width="16" height="16"
             alt="<fmt:message key="common.edit"/>"
             title="<fmt:message key="common.edit"/>">

        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMActionBean"
                event="viewPeiCMFromPreview">
            <s:param name="contractId" value="${requestScope.actionBean.peiId}"/>
            <s:param name="path" value=""/>
            <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
            <fmt:message key="common.edit"/>
        </s:link>
    </div>
</c:if>
</body>
</html>