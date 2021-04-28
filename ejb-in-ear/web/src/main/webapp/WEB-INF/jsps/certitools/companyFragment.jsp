<%@ page import="com.criticalsoftware.certitools.presentation.util.HTMLEscapeAndNL2BR" %>
<%@ include file="../../../includes/taglibs.jsp" %>

<c:choose>
<c:when test="${requestScope.actionBean.company.name == null}">
    <p>&nbsp;</p>
</c:when>
<c:otherwise>

<div class="hidden">
    <ss:secure roles="contractmanager">

        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean"
                event="updateCompanyForm" class="operationEdit copyCompany">
            <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
            <s:param name="letter">${requestScope.actionBean.letter}</s:param>
            <fmt:message key="common.edit"/>
        </s:link>

        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean"
                event="deleteCompany" class="operationDelete copyCompany confirmDelete">
            <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
            <s:param name="letter">${requestScope.actionBean.letter}</s:param>
            <fmt:message key="common.delete"/>
        </s:link>

        <span class="separator copyCompany" style="margin-right: 10px;">|</span>

    </ss:secure>

    <c:if test="${requestScope.actionBean.company.id != null}">
        <ss:secure roles="contractmanager">

            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.ContractActionBean"
                    event="insertContractForm" class="operationAdd copyCompany">
                <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                <s:param name="letter">${requestScope.actionBean.letter}</s:param>
                <fmt:message key="common.new"/> <fmt:message key="companies.contract"/>
            </s:link>

            <span class="separator copyCompany" style="margin-right: 10px;">|</span>
        </ss:secure>


        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
                event="insertUserForm" class="operationAdd copyCompany">
            <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
            <s:param name="letter">${requestScope.actionBean.letter}</s:param>
            <fmt:message key="common.new"/> <fmt:message key="companies.user"/>
        </s:link>

        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
                event="viewUsers" class="operationList copyCompany">
            <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
            <s:param name="letter">${requestScope.actionBean.letter}</s:param>
            <fmt:message key="user.list"/>
        </s:link>

        <ss:secure roles="administrator,contractmanager,clientcontractmanager">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean"
                    event="importUsersForm" class="operationImport copyCompany">
                <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                <s:param name="letter">${requestScope.actionBean.letter}</s:param>
                <fmt:message key="user.import"/>
            </s:link>
            <span class="separator copyCompany" style="margin-right: 10px;">|</span>
        </ss:secure>

        <c:if test="${!applicationScope.configuration.localInstallation}">
            <ss:secure roles="administrator,contractmanager,clientcontractmanager">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyAlertsActionBean"
                        event="viewSendAlertsForm" class="operationSendAlerts copyCompany">
                    <s:param name="companyId">${requestScope.actionBean.company.id}</s:param>
                    <s:param name="letter">${requestScope.actionBean.letter}</s:param>
                    <fmt:message key="common.sendAlert"/>
                </s:link>
            </ss:secure>
        </c:if>
    </c:if>

</div>


<h1><c:out value="${requestScope.actionBean.company.name}"/></h1>

<p>
    <c:set var="addressVar">${requestScope.actionBean.company.address}</c:set>
    <% out.print(HTMLEscapeAndNL2BR.replaceAndEscape(((String) pageContext.getAttribute("addressVar")))); %>

    <br/>
    <c:out value="${requestScope.actionBean.company.country.name}"/>
</p>

<p class="phone" style="padding-left: 27px;">
    <c:out value="${requestScope.actionBean.company.phone}"/>
</p>

<p style="margin-top: 0; width:49%; float: left;">
    <span class="label"><fmt:message key="company.fiscalNumber"/>: </span>
    <c:out value="${requestScope.actionBean.company.fiscalNumber}"/>
</p>

<p style="margin-top: 0; width:49%; float: left;">
    <span class="label"><fmt:message key="company.language"/>: </span>
    <c:out value="${requestScope.actionBean.company.language}"/>
</p>

<p>&nbsp;</p>

<h3><fmt:message key="companies.contracts"/>:</h3>

<c:if test="${fn:length(requestScope.actionBean.company.contracts) <= 0}">
    <fmt:message key="contract.noContracts"/>
</c:if>

<table class="contractsTable">

    <c:forEach items="${requestScope.actionBean.company.contracts}" var="contract">
        <c:set var="classInactive"></c:set>
        <c:if test="${!contract.active || !contract.dateActive}">
            <c:set var="classInactive">inactive</c:set>
        </c:if>

        <tr class="first ${pageScope.classInactive}">
            <td style="width: 80px; white-space: nowrap;">
                <c:out value="${contract.number}"/></td>
            <td><c:out value="${contract.contractDesignation}"/></td>
            <td style="text-align:right; white-space:nowrap;width:21%">
                <c:out value="${fn:length(contract.userContract)}"/>/<c:out value="${contract.licenses}"/>
                <c:choose>
                    <c:when test="${contract.licenses == 1}">
                        <fmt:message key="contract.license"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="contract.licenses"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>

        <tr class="${pageScope.classInactive}">
            <td><fmt:message key="contract.validity"/>:</td>
            <td colspan="2"><fmt:formatDate value="${contract.validityStartDate}"
                                            pattern="${applicationScope.configuration.datePattern}"/>
                / <fmt:formatDate value="${contract.validityEndDate}"
                                  pattern="${applicationScope.configuration.datePattern}"/>
            </td>
        </tr>
        <tr class="${pageScope.classInactive}">
            <td><fmt:message key="contract.module"/>:</td>
            <td colspan="2"><c:out value="${contract.module.name}"/></td>
        </tr>
        <tr class="${pageScope.classInactive}">
            <td><fmt:message key="contract.id"/>:</td>
            <td colspan="2"><c:out value="${contract.id}"/></td>
        </tr>

        <c:if test="${contract.userRegisterCode != null}">
            <tr class="${pageScope.classInactive}">
                <td><fmt:message key="contract.userRegisterLink"/>:</td>
                <td colspan="2">
                    <input type="text"
                           value="${applicationScope.configuration.applicationDomain}/UserRegister.action?contractId=<c:out
                        value="${contract.id}"/>&code=<c:out value="${contract.userRegisterCode}"/>" readonly="readonly"
                           onclick="$(this).select();"
                           style="font-size: 80%; width: 100%; color: #004080;">
                </td>
            </tr>
        </c:if>

        <tr class="last-1 ${pageScope.classInactive}">
            <td><fmt:message key="contract.contact"/>:</td>
            <td colspan="2">
                <p><c:out value="${contract.contactName}"/></p>
                <c:if test="${contract.contactPosition != null}"><p><c:out
                        value="${contract.contactPosition}"/></p></c:if>
                <c:if test="${contract.contactEmail != null}"><p class="email"><c:out
                        value="${contract.contactEmail}"/></p></c:if>
                <c:if test="${contract.contactPhone != null}"><p class="phone"><c:out
                        value="${contract.contactPhone}"/></p></c:if>
            </td>
        </tr>

        <tr class="last-contracts ${pageScope.classInactive}">
            <!-- Contract inactivity setting-->
            <td colspan="2">
                <fmt:message key="contract.inactivitySetting.title"/>:
                <c:choose>
                    <c:when test="${contract.contractInactivityOn}">
                        <img src="${pageContext.request.contextPath}/images/button-ok.png"
                             title="<fmt:message key="common.active"/>"
                             alt="<fmt:message key="common.active"/>"/>
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/images/button-disabled.png"
                             title="<fmt:message key="common.inactive"/>"
                             alt="<fmt:message key="common.inactive"/>"/>
                    </c:otherwise>
                </c:choose>
            </td>
            <td style="text-align: right; vertical-align: bottom;">
                <ss:secure roles="contractmanager,administrator">
                    <c:if test="${contract.contractFile != null}">
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.ContractActionBean"
                                event="downloadContractFile">
                            <s:param name="contract.id">${contract.id}</s:param>

                            <img src="${pageContext.request.contextPath}/images/displaytag/ico_file_pdf.png"
                                 alt="<fmt:message key="contract.document"/>"
                                 title="<fmt:message key="contract.document"/>"/>
                        </s:link>
                    </c:if>
                </ss:secure>
                &nbsp;

                <!-- Inactivity Settings-->
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.ContractActionBean"
                        event="insertContractInactivitySettingsForm">
                    <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                    <s:param name="letter">${requestScope.actionBean.letter}</s:param>
                    <s:param name="contract.id">${contract.id}</s:param>

                    <img src="${pageContext.request.contextPath}/images/user-inactivity-system.png"
                         title="<fmt:message key="contract.inactivitySetting"/>"
                         alt="<fmt:message key="contract.inactivitySetting"/>">
                </s:link>

                &nbsp;

                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
                        event="viewUsers">
                    <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                    <s:param name="letter">${requestScope.actionBean.letter}</s:param>
                    <s:param name="contract.id">${contract.id}</s:param>

                    <img src="${pageContext.request.contextPath}/images/kuser.png"
                         title="<fmt:message key="user.list"/>"
                         alt="<fmt:message key="user.list"/>">
                </s:link>

                &nbsp;

                <ss:secure roles="contractmanager">

                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.ContractActionBean"
                            event="updateContractForm">
                        <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                        <s:param name="letter">${requestScope.actionBean.letter}</s:param>
                        <s:param name="contract.id">${contract.id}</s:param>

                        <img src="${pageContext.request.contextPath}/images/Editar.png"
                             title="<fmt:message key="common.edit"/>"
                             alt="<fmt:message key="common.edit"/>">
                    </s:link>

                    &nbsp;

                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.ContractActionBean"
                            event="deleteContract" class="confirmDeleteContract">
                        <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                        <s:param name="letter">${requestScope.actionBean.letter}</s:param>
                        <s:param name="contract.id">${contract.id}</s:param>

                        <img src="${pageContext.request.contextPath}/images/Eliminar.png"
                             title="<fmt:message key="common.delete"/>"
                             alt="<fmt:message key="common.delete"/>">
                    </s:link>

                </ss:secure>
            </td>
        </tr>
    </c:forEach>
</table>

</c:otherwise>
</c:choose>