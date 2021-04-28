<%@ page import="com.criticalsoftware.certitools.entities.jcr.Plan" %>
<%@ include file="../../../includes/taglibs.jsp" %>
<%@ include file="../../../includes/planSetTitle.jsp" %>

<head>
    <title><c:out value="${requestScope.actionBean.moduleTitle}"/></title>

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/hoverIntent.js"></script>

    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/hslides/accordion.css"/>

    <link rel="stylesheet" type="text/css"
                 href="${pageContext.request.contextPath}/styles/plan/${fn:toLowerCase(requestScope.actionBean.planModuleType)}.css" media="screen"/>
    
    <script src="${pageContext.request.contextPath}/scripts/hslides/jquery.hslides.js"
            type="text/javascript"></script>

</head>

<s:errors/>

<table class="peiTableTitle" cellpadding="0" cellspacing="0">
    <tr>
        <td><h1 class="peiH1"><c:out value="${requestScope.actionBean.moduleTitle}"/></h1></td>
    </tr>
</table>

<c:if test="${sessionScope.userPEIPreview}">
    <div class="alertPreview">
        <fmt:message key="pei.viewingPreview"/>

        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
                event="cancelPEIPreview">
            <fmt:message key="pei.viewingPreviewLink"/>
            <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
        </s:link>
    </div>
</c:if>

<c:choose>
<c:when test="${fn:length(requestScope.actionBean.planList) <= requestScope.actionBean.maxPlansInList && fn:length(requestScope.actionBean.planList) > 0}">
    <div class="containerAccordion">
        <div class="sections">
            <ul class="accordion peiSelectionList">

                <c:forEach items="${requestScope.actionBean.planList}" var="pei" varStatus="index">
                    <li>
                        <c:choose>
                            <c:when test="${pei.userCanAccess}">
                                <div class="section sectionActive">
                            </c:when>
                            <c:otherwise>
                                <div class="section sectionInactive">
                            </c:otherwise>
                        </c:choose>
                                <div class="content">
                                    <span class="hidden">${pageContext.request.contextPath}/plan/Plan.action?planModuleType=${requestScope.actionBean.planModuleType}&viewPEI=&amp;peiId=${pei.name}</span>
                                    <table cellspacing="0" cellpadding="0" width="100%">
                                        <tr>
                                            <td rowspan="2" class="label">&nbsp;</td>
                                            <td class="title"><h2>${pei.planNameOnline}</h2></td>
                                            <td rowspan="2" class="imageColumn">
                                                <c:if test="${pei.installationPhotoOnline != null || (sessionScope.userPEIPreview && pei.installationPhoto != null)}">
                                                    <img src="${pageContext.request.contextPath}/plan/Plan.action?planModuleType=${requestScope.actionBean.planModuleType}&viewInstallationPhoto&amp;peiId=${pei.name}"
                                                         alt="${pei.planNameOnline}" title="${pei.planNameOnline}"
                                                         width="300"/>
                                                </c:if>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="coverData">
                                                <p>
                                                    <c:if test="${pei.companyLogoOnline != null || (sessionScope.userPEIPreview && pei.companyLogo != null)}">
                                                        <img src="${pageContext.request.contextPath}/plan/Plan.action?planModuleType=${requestScope.actionBean.planModuleType}&viewCompanyLogo&amp;peiId=${pei.name}"
                                                             alt="" title=""/>
                                                    </c:if>
                                                </p>

                                                <p><span class="color-label"><fmt:message
                                                        key="pei.version"/>:</span> ${pei.versionOnline}</p>

                                                <p><span class="color-label"><fmt:message
                                                        key="pei.versionDate"/>:</span>
                                                    <fmt:formatDate value="${pei.versionDateOnline}"
                                                                    pattern="${applicationScope.configuration.datePattern}"/>
                                                </p>


                                                <c:if test="${pei.simulationDateOnline != null}">
                                                    <p><span class="color-label"><fmt:message
                                                            key="pei.simulationDate"/>:</span>
                                                        <fmt:formatDate value="${pei.simulationDateOnline}"
                                                                        pattern="${applicationScope.configuration.datePattern}"/>
                                                    </p>
                                                </c:if>

                                                <p><span class="color-label"><fmt:message
                                                        key="pei.authorName"/>:</span> ${pei.authorNameOnline}</p>

                                                <c:if test="${!pei.userCanAccess}">
                                                    <p class="peiAccessDisallowed"><fmt:message
                                                            key="pei.disallowedAccess"/></p>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </div>

                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</c:when>
<c:when test="${fn:length(requestScope.actionBean.planList) > 0}">

    <c:choose>
        <c:when test="${fn:length(requestScope.actionBean.contracts) <= 0}">
            <fmt:message key="pei.noContracts"/>
        </c:when>
        <c:otherwise>

            <s:form action="${pageContext.request.contextPath}/plan/Plan.action"
                  id="contractForm" method="get" class="form-pei-admin-select-pei">
                <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>                
                <p>
                    <label for="companies" class="floatNone"><fmt:message key="pei.entity"/>:</label>
                    <select id="companies" name="companyId" class="mediumInput floatNone" style="width: 300px;">
                        <c:forEach items="${requestScope.actionBean.companies}" var="company">
                            <c:choose>
                                <c:when test="${company.id == requestScope.actionBean.companyId}">
                                    <option value="${company.id}" selected="selected">${company.name}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${company.id}">${company.name}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>

                    <label for="contractsLabel" style="margin-left: 10px" class="floatNone">
                        <fmt:message key="pei.contract"/>:</label>
                    <select id="contractsLabel" name="peiId" class="mediumInput floatNone" style="width: 300px;">
                        <c:forEach items="${requestScope.actionBean.contracts}" var="contract">
                            <c:choose>
                                <c:when test="${requestScope.actionBean.peiId == contract.id}">
                                    <option value="${contract.id}"
                                            selected="selected">${contract.contractDesignation}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${contract.id}">${contract.contractDesignation}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                    <input type="submit" id="viewPEI" name="viewPEI" class="button floatNone buttonPeiView"
                           value="<fmt:message key="common.view"/>"/>
                </p>
                <input type="hidden" name="_eventName" value="viewPEI"/>
            </s:form>

            <script type="text/javascript">
                attachOnChangePeiMainCompanies('${pageContext.request.contextPath}'
                        + '/plan/Plan.action?planModuleType=${requestScope.actionBean.planModuleType}&loadCompanyContracts=');
            </script>
        </c:otherwise>
    </c:choose>
    <p>&nbsp;</p>

    <p>&nbsp;</p>

</c:when>
</c:choose>


<script type="text/javascript">
    $(document).ready(function() {
        loadPEIMenuAccordion();
    });
</script>