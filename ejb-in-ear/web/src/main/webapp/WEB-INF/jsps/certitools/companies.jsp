<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="companies.companies"/></title>

<h1><fmt:message key="companies.companies"/></h1>

<s:messages/>
<s:errors globalErrorsOnly="true"/>

<div class="links">

    <ss:secure roles="contractmanager">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean"
                event="insertCompanyForm" class="operationAdd">
            <s:param name="letter">${requestScope.actionBean.letter}</s:param>
            <fmt:message key="common.newF"/> <fmt:message key="companies.company"/>
        </s:link>
    </ss:secure>

    &nbsp;

    <span id="aditionalLinks">

    </span>

</div>

<div class="links2">

    <c:choose>
        <c:when test="${requestScope.actionBean.letter == 'ALL'}">
            <span class="selected" style="margin-right: 11px;"><fmt:message key="common.alphanavbar.all"/></span>
        </c:when>

        <c:otherwise>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean"
                    event="viewCompanies" style="margin-right: 11px;">
                <s:param name="letter">ALL</s:param>
                <fmt:message key="common.alphanavbar.all"/>
            </s:link>
        </c:otherwise>

    </c:choose>

    <span class="separator" style="margin-right: 15px;">|</span>

    <c:forEach items="${requestScope.actionBean.alphabet}" var="letter">
        <c:choose>

            <c:when test="${requestScope.actionBean.letter == letter}">
                <span class="selected">${letter}</span>
            </c:when>

            <c:otherwise>
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean"
                        event="viewCompanies">
                    <s:param name="letter">${letter}</s:param>
                    ${letter}
                </s:link>
            </c:otherwise>

        </c:choose>
    </c:forEach>
</div>


<table id="companiesTableWrapper" class="displaytag">
    <tr>
        <td class="leftColumn">

            <s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean"
                    focus="" method="GET">
                <s:text name="searchPhrase" class="searchInput"></s:text>
                <s:submit name="searchCompany" class="searchButton"><fmt:message key="common.search"/></s:submit>
                <s:hidden name="letter"/>
                <input type="hidden" name="_eventName" value="searchCompany"/>
            </s:form>


            <c:if test="${fn:length(requestScope.actionBean.companies) > 0 || fn:length(requestScope.actionBean.letter) > 0
            || fn:length(requestScope.actionBean.searchPhrase) > 0}">
            <display:table htmlId="companiesTable" list="${requestScope.actionBean.companies}" export="true" uid="row"
                           decorator="com.criticalsoftware.certitools.presentation.util.CompanyTableDecorator"
                           requestURI="/certitools/Company.action">
                <display:column media="html">

                    <c:choose>
                        <c:when test="${pageScope.row.id == requestScope.actionBean.company.id}">
                            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean"
                                    event="viewCompanyFragment" class="selected companySelectionLink"
                                    onclick="$(window).scrollTop(0);"
                                    id="company${pageScope.row.id}">
                                <s:param name="company.id">${pageScope.row.id}</s:param>
                                <s:param name="letter">${requestScope.actionBean.letter}</s:param>
                                <c:out value="${pageScope.row.name}"/>
                                <br/>
                                <span class="country"><c:out value="${pageScope.row.country.name}"/></span>
                            </s:link>
                        </c:when>

                        <c:otherwise>
                            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean"
                                    event="viewCompanyFragment" class="companySelectionLink" target="#companyId"
                                    onclick="$(window).scrollTop(0);"
                                    id="company${pageScope.row.id}">
                                <s:param name="company.id">${pageScope.row.id}</s:param>
                                <s:param name="letter">${requestScope.actionBean.letter}</s:param>
                                <c:out value="${pageScope.row.name}"/>
                                <br/>
                                <span class="country"><c:out value="${pageScope.row.country.name}"/></span>
                            </s:link>
                        </c:otherwise>
                    </c:choose>

                </display:column>

                <display:column property="id" media="csv excel xml pdf"/>
                <display:column property="name" media="csv excel xml pdf" titleKey="company.name"/>
                <display:column property="address" media="csv excel xml pdf" titleKey="company.address"/>
                <display:column property="country.name" media="csv excel xml pdf" titleKey="company.country.iso"/>
                <display:column property="phone" media="csv excel xml pdf" titleKey="company.phone"/>
                <display:column property="fiscalNumber" media="csv excel xml pdf" titleKey="company.fiscalNumber"/>
                <display:column property="language" media="csv excel xml pdf" titleKey="company.language"/>

                <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
                <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
                <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
                <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
                <display:setProperty name="basic.show.header" value="false"/>
                <display:setProperty name="export.banner">
                    <p style="width: 200px; float: left; margin-top: 0; padding-left: 10px;">{0}</p>

                    <p class="companiesNumber" style="float: right; width:100px; margin-top: 0; padding-right: 10px;">

                        <c:choose>
                            <c:when test="${fn:length(requestScope.actionBean.companies) == 1}">
                                <c:set var="u"><fmt:message key="companies.company"/></c:set>
                                ${fn:length(requestScope.actionBean.companies)} ${fn:toLowerCase(u)}
                            </c:when>
                            <c:when test="${fn:length(requestScope.actionBean.companies) > 1}">
                                <c:set var="u"><fmt:message key="companies.companies"/></c:set>
                                ${fn:length(requestScope.actionBean.companies)} ${fn:toLowerCase(u)}
                            </c:when>
                        </c:choose>
                    </p>
                </display:setProperty>
            </display:table>
            </c:if>
        </td>
        <td class="rightColumn" id="companyId">

        </td>
    </tr>
</table>
<div class="cleaner">&nbsp;</div>

<script type="text/javascript">
    $(document).ready(function() {
        attachViewCompanyLink("<fmt:message key="companies.confirmDelete"/>", "<fmt:message key="contract.confirmDelete"/>");

    <c:if test="${requestScope.actionBean.company.id != null}">
        loadCompany(${requestScope.actionBean.company.id}, "<fmt:message key="companies.confirmDelete"/>",
                "<fmt:message key="contract.confirmDelete"/>");
    </c:if>
    });

</script>
