<%@ include file="../../../includes/taglibs.jsp" %>

<c:choose>
<c:when test="${requestScope.actionBean.user.name == null}">
    <p>&nbsp;</p>
</c:when>
<c:otherwise>

<div class="hidden">

    <c:choose>
        <c:when test="${applicationScope.configuration.adminId == requestScope.actionBean.user.id}">
            <span id="linkEdit">&nbsp;</span>
            <span id="linkDelete">&nbsp;</span>
        </c:when>
        <c:otherwise>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
                    event="updateUserForm" class="operationEdit" id="linkEdit">
                <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                <s:param name="user.id">${requestScope.actionBean.user.id}</s:param>
                <s:param name="letterUser">${requestScope.actionBean.letterUser}</s:param>
                <s:param name="source">users</s:param>
                <s:param name="contract.id">${requestScope.actionBean.contract.id}</s:param>
                <fmt:message key="common.edit"/> <fmt:message key="companies.user"/>
            </s:link>

            <c:if test="${!applicationScope.configuration.localInstallation}">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
                        event="deleteUser" class="operationDelete confirmDelete" id="linkDelete">
                    <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                    <s:param name="user.id">${requestScope.actionBean.user.id}</s:param>
                    <s:param name="letterUser">${requestScope.actionBean.letterUser}</s:param>
                    <s:param name="source">users</s:param>
                    <s:param name="contract.id">${requestScope.actionBean.contract.id}</s:param>
                    <fmt:message key="common.delete"/> <fmt:message key="companies.user"/>
                </s:link>
            </c:if>

            <c:if test="${!applicationScope.configuration.localInstallation}">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
                        event="resetUserPassword" class="operationReset" id="linkReset">
                    <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                    <s:param name="user.id">${requestScope.actionBean.user.id}</s:param>
                    <s:param name="letterUser">${requestScope.actionBean.letterUser}</s:param>
                    <s:param name="source">users</s:param>
                    <s:param name="contract.id">${requestScope.actionBean.contract.id}</s:param>
                    <fmt:message key="user.resetPassword"/>
                </s:link>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>


<h1><c:out value="${requestScope.actionBean.user.name}"/></h1>

<table class="cleanTable">
    <tr>
        <td class="cleanTableTD" style="white-space: nowrap;">
            <c:out value="${requestScope.actionBean.user.email}"/>
            &nbsp;
        </td>
        <td class="cleanTableTD">
            <span class="label"><fmt:message key="user.fiscalNumber"/>: </span>
            <c:out value="${requestScope.actionBean.user.fiscalNumber}"/>
        </td>
        <td class="cleanTableTD">
            <c:if test="${requestScope.actionBean.user.phone != null}">
                    <span class="phone">
                        <c:out value="${requestScope.actionBean.user.phone}"/>
                    </span>
            </c:if>
        </td>
    </tr>
    <tr>
        <td class="cleanTableTD paddingTop">
            <span class="label"><fmt:message key="user.active"/>: </span>
            <c:choose>
                <c:when test="${requestScope.actionBean.user.active == true}">
                    <img src="${pageContext.request.contextPath}/images/button-ok.png"
                         title="<fmt:message key="user.active"/>"
                         alt="<fmt:message key="user.active"/>"/>
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/images/button-disabled.png"
                         title="<fmt:message key="common.not"/>"
                         alt="<fmt:message key="common.not"/>"/>
                </c:otherwise>
            </c:choose>
        </td>
        <td class="cleanTableTD paddingTop">
            <span class="label"><fmt:message key="user.uniqueSession"/>: </span>

            <c:choose>
                <c:when test="${requestScope.actionBean.user.uniqueSession == true}">
                    <img src="${pageContext.request.contextPath}/images/button-ok.png"
                         title="<fmt:message key="user.uniqueSession"/>"
                         alt="<fmt:message key="user.uniqueSession"/>"/>
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/images/button-disabled.png"
                         title="<fmt:message key="common.not"/>"
                         alt="<fmt:message key="common.not"/>"/>
                </c:otherwise>
            </c:choose>
        </td>
        <td class="cleanTableTD paddingTop">
            <span class="label"><fmt:message key="user.passwordDefined"/>: </span>

            <c:choose>
                <c:when test="${!empty requestScope.actionBean.user.password}">
                    <img src="${pageContext.request.contextPath}/images/button-ok.png"
                         title="<fmt:message key="user.passwordDefined"/>"
                         alt="<fmt:message key="user.passwordDefined"/>"/>
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/images/button-disabled.png"
                         title="<fmt:message key="common.not"/>"
                         alt="<fmt:message key="common.not"/>"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <td colspan="3" class="cleanTableTD paddingTop">
            <span class="label"><fmt:message key="user.externalUser"/>: </span>
                ${requestScope.actionBean.user.externalUser}
        </td>
    </tr>
</table>


<c:if test="${!requestScope.actionBean.user.inActiveContracts && !requestScope.actionBean.user.certitecnaSpecialRoles}">
    <p class="notActiveInContracts"><fmt:message key="user.notActiveInContracts"/></p>
</c:if>

<h3 class="border"><fmt:message key="user.roles"/>:</h3>

<ul>
    <c:forEach items="${requestScope.actionBean.user.roles}" var="role">
        <li><fmt:message key="${role.description}"/></li>
    </c:forEach>
</ul>

<h3><fmt:message key="companies.contracts"/>:</h3>

<c:if test="${fn:length(requestScope.actionBean.user.userContract) <= 0}">
    <fmt:message key="contract.noContracts"/>
</c:if>

<table class="contractsTable">

    <c:forEach items="${requestScope.actionBean.user.userContract}" var="userContract">
        <tr class="first">
            <td style="width: 130px; white-space: nowrap;">
                <c:out value="${userContract.contract.number}"/></td>
            <td>${userContract.contract.module.name}</td>
            <td style="text-align:right; white-space:nowrap;"><c:out value="${userContract.contract.licenses}"/>
                <c:choose>
                    <c:when test="${userContract.contract.licenses == 1}">
                        <fmt:message key="contract.license"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="contract.licenses"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td><fmt:message key="contract.contractDesignation"/>:</td>
            <td colspan="2">
                <c:out value="${userContract.contract.contractDesignation}"/>
            </td>
        </tr>
        <tr>
            <td><fmt:message key="contract.validity"/>:</td>
            <td colspan="2"><fmt:formatDate value="${userContract.contract.validityStartDate}"
                                            pattern="${applicationScope.configuration.datePattern}"/>
                / <fmt:formatDate value="${userContract.contract.validityEndDate}"
                                  pattern="${applicationScope.configuration.datePattern}"/>
            </td>
        </tr>
        <tr>
            <td><fmt:message key="user.userContract.validity"/>:</td>
            <td colspan="2">
                <c:choose>
                <c:when test="${userContract.validityStartDate == null}">
                    <fmt:formatDate value="${userContract.contract.validityStartDate}"
                                    pattern="${applicationScope.configuration.datePattern}"/>
                /
                    <fmt:formatDate value="${userContract.contract.validityEndDate}"
                                    pattern="${applicationScope.configuration.datePattern}"/>
                </c:when>
                <c:otherwise>
                    <fmt:formatDate value="${userContract.validityStartDate}"
                                    pattern="${applicationScope.configuration.datePattern}"/>
                /
                    <fmt:formatDate value="${userContract.validityEndDate}"
                                    pattern="${applicationScope.configuration.datePattern}"/>
                </c:otherwise>
                </c:choose>
        </tr>
        <tr class="last">
            <td colspan="3">
                <c:if test="${userContract.contract.contractPermissions != null && fn:length(userContract.contract.contractPermissions) > 0}">
                    <c:if test="${userContract.permissions != null}">
                        <p><span style="font-size:10pt;color:#618293;font-weight:bold;">
                                <fmt:message key="contract.permissions"/></span>
                        </p>


                        <c:if test="${fn:length(userContract.permissions) == 0}">
                            <p><fmt:message key="user.permissions.empty"/></p>
                        </c:if>

                        <ul style="margin-top: 2px; padding-top: 0;">

                            <c:forEach items="${userContract.permissions}" var="permission">
                                <li>

                                    <c:choose>
                                        <c:when test="${permission.name == requestScope.specialPermission}">
                                            <label class="italic">${permission.name}</label>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${permission.name == 'security.permission.basic' || permission.name == 'security.permission.intermediate' || permission.name == 'security.permission.expert'}">
                                                    <fmt:message key="${permission.name}"></fmt:message>
                                                </c:when>
                                                <c:otherwise>
                                                    <label>${permission.name}</label>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>

                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </c:if>

            </td>
        </tr>
    </c:forEach>
</table>

</c:otherwise>
</c:choose>