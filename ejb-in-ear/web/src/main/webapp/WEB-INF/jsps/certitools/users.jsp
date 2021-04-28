<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="users"/></title>

<h1><fmt:message key="users"/></h1>

<h2><span><c:out value="${requestScope.actionBean.company.name}"/>
    <c:if test="${requestScope.actionBean.contract != null}"> - <fmt:message key="contract"/> <c:out
            value="${requestScope.actionBean.contract.number}"/></c:if></span></h2>

<s:messages/>
<s:errors globalErrorsOnly="true"/>

<div class="links">

    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean"
            event="viewCompanies" class="operationBack">
        <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
        <s:param name="letter">${requestScope.actionBean.letter}</s:param>
        <fmt:message key="common.back"/>
    </s:link>

    <span class="separator" style="margin-right: 15px;">|</span>

    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
            event="insertUserForm" class="operationAdd">
        <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
        <s:param name="letterUser">${requestScope.actionBean.letterUser}</s:param>
        <s:param name="contract.id">${requestScope.actionBean.contract.id}</s:param>
        <s:param name="source">users</s:param>
        <fmt:message key="common.new"/> <fmt:message key="companies.user"/>
    </s:link>
    <span id="aditionalLinks">
        
    </span>
</div>


<div class="links2">
    <c:choose>
        <c:when test="${requestScope.actionBean.letterUser == 'ALL'}">
            <span class="selected" style="margin-right: 11px;"><fmt:message key="common.alphanavbar.all"/></span>
        </c:when>

        <c:otherwise>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
                    event="viewUsers" style="margin-right: 11px;">
                <s:param name="letterUser">ALL</s:param>
                <s:param name="contract.id">${requestScope.actionBean.contract.id}</s:param>
                <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                <fmt:message key="common.alphanavbar.all"/>
            </s:link>
        </c:otherwise>

    </c:choose>

    <span class="separator" style="margin-right: 15px;">|</span>

    <c:forEach items="${requestScope.actionBean.alphabet}" var="letter">
        <c:choose>

            <c:when test="${requestScope.actionBean.letterUser == letter}">
                <span class="selected">${letter}</span>
            </c:when>

            <c:otherwise>
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
                        event="viewUsers">
                    <s:param name="letterUser">${letter}</s:param>
                    <s:param name="contract.id">${requestScope.actionBean.contract.id}</s:param>
                    <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                    ${letter}
                </s:link>
            </c:otherwise>

        </c:choose>
    </c:forEach>
</div>


<table id="companiesTableWrapper" class="displaytag">
    <tr>
        <td class="leftColumn">
            <s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean" focus=""
                    method="GET">
                <s:text name="searchPhrase" class="searchInput"></s:text>
                <s:submit name="searchUser" class="searchButton"><fmt:message key="common.search"/></s:submit>
                <input type="hidden" name="_eventName" value="searchUser"/>
                <s:hidden name="company.id"/>
                <s:hidden name="letterUser"/>
                <s:hidden name="contract.id">${requestScope.actionBean.contract.id}</s:hidden>
            </s:form>

            <c:if test="${fn:length(requestScope.actionBean.users) > 0 ||
            fn:length(requestScope.actionBean.letterUser) > 0 || fn:length(requestScope.actionBean.searchPhrase) > 0 }">
                <display:table htmlId="companiesTable" list="${requestScope.actionBean.users}" export="true" uid="row"
                               decorator="com.criticalsoftware.certitools.presentation.util.UserTableDecorator"
                               requestURI="/certitools/User.action">
                    <display:column media="html">

                        <c:choose>
                            <c:when test="${pageScope.row.id == requestScope.actionBean.user.email}">
                                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
                                        event="viewUserFragment" class="selected userSelectionLink"
                                        onclick="$(window).scrollTop(0);"
                                        id="user${pageScope.row.id}">
                                    <s:param name="user.id">${pageScope.row.id}</s:param>
                                    <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                                    <s:param name="letterUser">${requestScope.actionBean.letterUser}</s:param>
                                    <s:param name="contract.id">${requestScope.actionBean.contract.id}</s:param>
                                    <c:out value="${pageScope.row.name}"/>
                                    <br/>
                                    <span class="country"><c:out value="${pageScope.row.email}"/></span>
                                </s:link>
                            </c:when>

                            <c:otherwise>
                                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
                                        event="viewUserFragment" class="userSelectionLink"
                                        onclick="$(window).scrollTop(0);" id="user${pageScope.row.id}">
                                    <s:param name="user.id">${pageScope.row.id}</s:param>
                                    <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                                    <s:param name="letterUser">${requestScope.actionBean.letterUser}</s:param>
                                    <s:param name="contract.id">${requestScope.actionBean.contract.id}</s:param>
                                    <c:out value="${pageScope.row.name}"/>
                                    <br/>
                                    <span class="country"><c:out value="${pageScope.row.email}"/></span>
                                </s:link>
                            </c:otherwise>
                        </c:choose>

                    </display:column>
                    <display:column property="id" media="csv excel xml pdf"/>
                    <display:column property="name" media="csv excel xml pdf" titleKey="user.name"/>
                    <display:column property="email" media="csv excel xml pdf" titleKey="user.email"/>
                    <display:column property="phone" media="csv excel xml pdf" titleKey="user.phone"/>
                    <display:column property="fiscalNumber" media="csv excel xml pdf" titleKey="user.fiscalNumber"/>
                    <display:column property="company.id" media="csv excel xml pdf" titleKey="companies.id"/>
                    <display:column property="company.name" media="csv excel xml pdf" titleKey="companies.company"/>
                    <display:column media="csv excel xml pdf" titleKey="user.roles">
                        <c:forEach items="${pageScope.row.roles}" var="role">${role.role};</c:forEach>
                    </display:column>
                    <display:column property="externalUser" media="csv excel xml pdf" titleKey="user.externalUser"/>
                    <display:column property="uniqueSession" media="csv excel xml pdf" titleKey="user.uniqueSession"/>
                    <display:column property="active" media="csv excel xml pdf" titleKey="user.active"/>
                    <display:column property="numberLogins" media="csv excel xml pdf" titleKey="user.numberLogins"/>
                    <display:column media="csv excel xml pdf" titleKey="user.lastLoginDate">
                        <fmt:formatDate value="${pageScope.row.lastLoginDate}"
                                        pattern="${applicationScope.configuration.datePattern}"/>
                    </display:column>
                    <display:column media="csv excel xml pdf" titleKey="user.lastPlanOrLegislationView">
                        <fmt:formatDate value="${pageScope.row.lastPlanOrLegislationView}"
                                        pattern="${applicationScope.configuration.datePattern}"/>
                    </display:column>

                    <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
                    <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
                    <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
                    <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
                    <display:setProperty name="basic.show.header" value="false"/>
                    <display:setProperty name="export.banner">
                        <p style="margin-top: 0; padding-left: 10px; margin-bottom: 10px;">{0}
                            <ss:secure roles="administrator,contractmanager,clientcontractmanager">
                                | <s:link id="export"
                                          beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserActionBean"
                                          event="exportUsers">
                                <s:param name="company.id">${requestScope.actionBean.company.id}</s:param>
                                <s:param name="contract.id">${requestScope.actionBean.contract.id}</s:param>
                                <span class="export csv2"><fmt:message key="user.export"/></span>
                            </s:link>
                            </ss:secure>
                        </p>

                        <p class="companiesNumber" style="text-align: right; margin-top: 0; padding-right: 10px;">

                <span style="padding-right: 5px;">
                <c:choose>
                    <c:when test="${fn:length(requestScope.actionBean.users) == 1}">
                        <c:set var="u"><fmt:message key="user"/></c:set>
                        ${fn:length(requestScope.actionBean.users)} ${fn:toLowerCase(u)}
                    </c:when>
                    <c:when test="${fn:length(requestScope.actionBean.users) > 1}">
                        <c:set var="u"><fmt:message key="users"/></c:set>
                        ${fn:length(requestScope.actionBean.users)} ${fn:toLowerCase(u)}
                    </c:when>
                </c:choose>
                </span>
                        </p>
                    </display:setProperty>
                </display:table>
            </c:if>
        </td>
        <td class="rightColumn" id="userId">

        </td>
    </tr>
</table>
<div class="cleaner">&nbsp;</div>


<script type="text/javascript" charset="utf-8">
    $(document).ready(function () {
        attachViewUserLink("<fmt:message key="user.confirmDelete"/>");

        <c:if test="${requestScope.actionBean.user.id != null}">
        loadUser(${requestScope.actionBean.user.id}, "<fmt:message key="user.confirmDelete"/>");
        </c:if>
    });
</script>
