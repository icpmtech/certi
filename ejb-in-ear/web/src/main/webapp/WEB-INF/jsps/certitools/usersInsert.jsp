<%@ include file="../../../includes/taglibs.jsp" %>

<c:choose>
    <c:when test="${requestScope.actionBean.edit}">
        <c:set var="title"><fmt:message key="common.edit"/></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="title"><fmt:message key="common.add"/></c:set>
    </c:otherwise>
</c:choose>

<head>
    <title><fmt:message key="companies.contracts"/> &gt; ${pageScope.title}</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery-ui-1.7.custom.css"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/datepicker-${pageContext.request.locale}.js"></script>
</head>


<h1><fmt:message key="users"/></h1>

<h2 class="form"><span>${pageScope.title}</span></h2>
<s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean" class="form"
        focus="">

<s:errors/>

<p>
    <label><fmt:message key="companies.company"/> (*):</label>
    <span class="fixedInput">${requestScope.actionBean.company.name}</span>
    <s:hidden name="user.company.id" value="${requestScope.actionBean.company.id}"/>
</p>

<p>
    <s:label for="name"><fmt:message key="user.name"/> (*):</s:label>
    <s:text id="name" name="user.name" class="largeInput"/>
</p>

<s:label for="email"><fmt:message key="user.email"/> (*):</s:label>
<s:text id="email" name="user.email" class="largeInput"/>

<p>
    <s:label for="nif"><fmt:message key="user.fiscalNumber"/> (*):</s:label>
    <s:text id="nif" name="user.fiscalNumber" class="smallInput"/>
</p>

<p>
    <s:label for="phone"><fmt:message key="user.phone"/>:</s:label>
    <s:text id="phone" name="user.phone" class="smallInput"/>
</p>

<p>
    <s:label for="externalUser"><fmt:message key="user.externalUser"/>:</s:label>
    <s:text id="externalUser" name="user.externalUser" class="smallInput"/>
</p>

<p style="clear: both;">
    <s:label for="uniquesession"><fmt:message key="user.uniqueSession"/>:</s:label>
    <s:checkbox id="uniquesession" name="user.uniqueSession"/>
</p>

<p style="clear: both;">
    <s:label for="active"><fmt:message key="user.active"/>:</s:label>
    <s:checkbox checked="true" id="active" name="user.active"/>
</p>


<c:choose>
    <c:when test="<%=request.isUserInRole("clientcontractmanager")%>"></c:when>
    <c:otherwise>
        <h2 class="form cleaner"><span><fmt:message key="user.roles"/></span></h2>

        <ul class="rolesList">
            <c:forEach items="${requestScope.actionBean.roles}" var="role" varStatus="i">
                <li>
                    <s:label for="role${i.index}"><fmt:message key="${role.description}"/>:</s:label>
                    <c:choose>
                        <c:when test="${role.role == 'user'}">
                            <s:checkbox id="role${i.index}" name="roles[${i.index}].associatedWithUser"
                                        disabled="true"/>
                        </c:when>
                        <c:when test="${role.role == 'clientpeimanager'}">
                            <s:checkbox id="role${i.index}" name="roles[${i.index}].associatedWithUser"
                                        class="clientpeimanagerCheck"/>
                        </c:when>
                        <c:otherwise>
                            <s:checkbox id="role${i.index}" name="roles[${i.index}].associatedWithUser"/>
                        </c:otherwise>
                    </c:choose>
                    <s:hidden name="roles[${i.index}].role"/>
                </li>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>

<h2 class="form cleaner"><span><fmt:message key="contracts"/></span></h2>

<c:if test="${fn:length(requestScope.actionBean.contracts) <= 0}">
    <fmt:message key="contract.noContracts"/>
</c:if>


<table class="contractsTable">

    <c:forEach var="contract" items="${requestScope.actionBean.contracts}" varStatus="rowCounter">
        <tr class="first ${contract.id}">

            <c:choose>
            <c:when test="${contract.contractPermissions == null}">
            <td rowspan="4" style="border-bottom: 1px solid #ccc;">
                </c:when>
                <c:otherwise>
            <td rowspan="6" style="border-bottom: 1px solid #ccc;vertical-align:top;">
                </c:otherwise>
                </c:choose>
                <s:checkbox name="userContractsForm[${rowCounter.count - 1}].associatedWithUser"
                            class="checkboxContract" id="${contract.id}"/>
            </td>
            <td width="180"><fmt:message key="contract.number"/> <c:out value="${contract.number}"/></td>
            <td>${contract.module.name}</td>
            <td style="text-align:right; white-space:nowrap;">
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
        <tr class="${contract.id}">
            <td><fmt:message key="contract.contractDesignation"/>:</td>
            <td colspan="2"><c:out value="${contract.contractDesignation}"/></td>
        </tr>
        <tr class="${contract.id}">
            <td><fmt:message key="contract.validity"/>:</td>
            <td colspan="2">
                <span id="contractStartDate${rowCounter.count - 1}"><fmt:formatDate
                        value="${contract.validityStartDate}"
                        pattern="${applicationScope.configuration.datePattern}"/></span>
                /
                <span id="contractEndDate${rowCounter.count - 1}"><fmt:formatDate value="${contract.validityEndDate}"
                                                                                  pattern="${applicationScope.configuration.datePattern}"/></span>
            </td>
        </tr>

        <c:choose>
            <c:when test="${contract.contractPermissions == null}">
                <tr class="last ${contract.id}">
            </c:when>
            <c:otherwise>
                <tr class="${contract.id}">
            </c:otherwise>
        </c:choose>

        <td class="alignMiddle">
            <s:label for="startDate${rowCounter.count - 1}" style="margin-bottom: 15px;"><fmt:message
                    key="user.userContract.validity"/>:</s:label>
        </td>
        <td class="alignCenter">

            <p>
                <s:checkbox id="${rowCounter.count - 1}"
                            name="userContractsForm[${rowCounter.count - 1}].validityChanged"
                            class="checkboxValidity" style="margin: 0;"/>
                <s:label for="${rowCounter.count - 1}"><fmt:message key="user.changeValidityUser"/> ?</s:label>
            </p>
        </td>

        <td colspan="2">
            <p>
                <s:text id="startDate${rowCounter.count - 1}"
                        name="userContractsForm[${rowCounter.count - 1}].validityStartDate"
                        style="margin-bottom: 0;"
                        readonly="true" class="dateInput startDate"
                        />
                /
                <s:text style="float: none; margin-bottom: 0;" id="endDate${rowCounter.count - 1}"
                        name="userContractsForm[${rowCounter.count - 1}].validityEndDate"
                        readonly="true"
                        class="dateInput endDate"
                        />
                <s:hidden name="userContractsForm[${rowCounter.count - 1}].userContractPK.idContract"
                          value="${contract.id}"/>

                <s:hidden name="userContractsForm[${rowCounter.count - 1}].userContractPK.idUser"
                          value="${user.id}"/>
            </p>
        </td>
        </tr>

        <c:if test="${contract.contractPermissions != null}">
            <tr class="${contract.id}">
                <td colspan="3">
                    <h2 class="form" style="margin-bottom:0;margin-top:0;width:auto;"><span
                            style="font-size:10pt;color:#618293;font-weight:bold;"
                            class="permissionsSpan ${contract.id}"><fmt:message
                            key="contract.permissions"/></span></h2>
                </td>
            </tr>

            <tr class="last ${contract.id}">
                <td colspan="3">
                    <c:if test="${fn:length(contract.contractPermissions) == 0}">
                        <fmt:message key="contract.permissions.empty"/>
                    </c:if>

                    <ul class="permissionsList">

                        <c:forEach items="${contract.contractPermissions}" var="permission">
                            <li>

                                <c:choose>
                                    <c:when test="${permission.name == requestScope.specialPermission}">
                                        <s:label class="italic"
                                                 for="permission${permission.id}${contract.id}">${permission.name}</s:label>
                                        <s:checkbox id="permission${permission.id}${contract.id}"
                                                    value="${permission.id}"
                                                    name="userContractPermissions[${contract.id}]"
                                                    class="specialPermission"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:choose>
                                            <c:when test="${permission.name == 'security.permission.basic' || permission.name == 'security.permission.intermediate' || permission.name == 'security.permission.expert'}">
                                                <s:label
                                                        for="permission${permission.id}${contract.id}"><fmt:message key="${permission.name}"/></s:label>
                                                <s:checkbox id="permission${permission.id}${contract.id}"
                                                            name="userContractPermissions[${contract.id}]"
                                                            value="${permission.id}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <s:label
                                                        for="permission${permission.id}${contract.id}">${permission.name}</s:label>
                                                <s:checkbox id="permission${permission.id}${contract.id}"
                                                            name="userContractPermissions[${contract.id}]"
                                                            value="${permission.id}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>

                            </li>
                        </c:forEach>
                    </ul>
                </td>
            </tr>
        </c:if>

    </c:forEach>

</table>

<s:hidden name="edit"/>
<s:hidden name="user.id"/>
<s:hidden name="company.id"/>
<s:hidden name="letter"/>
<s:hidden name="letterUser"/>
<s:hidden name="source"/>

<s:hidden name="contract.id"/>

<div class="formButtons">
    <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
    <s:submit name="insertUser" class="button"><fmt:message key="common.submit"/></s:submit>
    <s:submit name="cancelInsertUser" class="button"><fmt:message key="common.cancel"/></s:submit>
</div>

</s:form>

<script type="text/javascript">

    $(function () {
        $('.startDate').datepicker({
            buttonImage:'${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat:'${applicationScope.configuration.datePatternCalendar}',
            buttonText:'<fmt:message key="contract.validityStartDate"/>'});
    });

    $(function () {
        $('.endDate').datepicker({
            buttonImage:'${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat:'${applicationScope.configuration.datePatternCalendar}',
            buttonText:'<fmt:message key="contract.validityEndDate"/>'});
    });

    $(document).ready(function () {
        attachContractCheckbox();
        attachUserValidityCheckbox();
        attachOnChangeToProfile();
    });

</script>


