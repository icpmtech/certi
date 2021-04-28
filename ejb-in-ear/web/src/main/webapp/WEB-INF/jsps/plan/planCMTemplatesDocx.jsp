<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="menu.pei.docx"/></title>

<h1><fmt:message key="menu.pei.docx"/></h1>

<s:messages/>
<s:errors globalErrorsOnly="true"/>

<div class="links">
    <ss:secure roles="peimanager">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplatesDocxActionBean"
                event="insertTemplateDocxForm" class="operationAdd">
            <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
            <fmt:message key="common.new"/> <fmt:message key="templateDocx"/>
        </s:link>
    </ss:secure>

    &nbsp;

    <span id="aditionalLinks">

    </span>

</div>

<div class="links2">
    <c:choose>
        <c:when test="${requestScope.actionBean.letterTemplate == 'ALL'}">
            <span class="selected" style="margin-right: 11px;"><fmt:message key="common.alphanavbar.all"/></span>
        </c:when>
        <c:otherwise>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplatesDocxActionBean"
                    event="viewPlanCMTemplatesDocx" style="margin-right: 11px;">
                <s:param name="letterTemplate">ALL</s:param>
                <fmt:message key="common.alphanavbar.all"/>
            </s:link>
        </c:otherwise>
    </c:choose>

    <span class="separator" style="margin-right: 15px;">|</span>

    <c:forEach items="${requestScope.actionBean.alphabet}" var="letter">
        <c:choose>

            <c:when test="${requestScope.actionBean.letterTemplate == letter}">
                <span class="selected">${letter}</span>
            </c:when>

            <c:otherwise>
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplatesDocxActionBean"
                        event="viewPlanCMTemplatesDocx">
                    <s:param name="letterTemplate">${letter}</s:param>
                    ${letter}
                </s:link>
            </c:otherwise>

        </c:choose>
    </c:forEach>
</div>

<table id="companiesTableWrapper" class="displaytag">
    <tr>
        <td class="leftColumn">

            <s:form beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplatesDocxActionBean" focus=""
                    method="GET">
                <s:text name="searchPhrase" class="searchInput"></s:text>
                <s:submit name="searchTemplateDocx" class="searchButton"><fmt:message key="common.search"/></s:submit>
                <input type="hidden" name="_eventName" value="searchTemplateDocx"/>
                <s:hidden name="letterTemplate"/>
            </s:form>

            <display:table htmlId="companiesTable" list="${requestScope.actionBean.templateList}" export="false"
                           uid="row"
                           requestURI="/plan/PlanCMTemplatesDocx.action">
                <display:column media="html">

                    <c:choose>
                        <c:when test="${pageScope.row.id == requestScope.actionBean.template.id}">
                            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplatesDocxActionBean"
                                    event="viewTemplateDocxFragment" class="selected companySelectionLink"
                                    onclick="$(window).scrollTop(0);"
                                    id="company${pageScope.row.id}">
                                <s:param name="template.id">${pageScope.row.id}</s:param>
                                <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
                                <c:out value="${pageScope.row.title}"/>
                                <br/>
                                <span class="country"><c:out value="${pageScope.row.module.name}"/></span>
                            </s:link>
                        </c:when>

                        <c:otherwise>
                            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplatesDocxActionBean"
                                    event="viewTemplateDocxFragment" class="companySelectionLink"
                                    onclick="$(window).scrollTop(0);"
                                    id="company${pageScope.row.id}">
                                <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
                                <s:param name="template.id">${pageScope.row.id}</s:param>
                                <c:out value="${pageScope.row.title}"/>
                                <br/>
                                <span class="country"><c:out value="${pageScope.row.module.name}"/></span>
                            </s:link>
                        </c:otherwise>
                    </c:choose>

                </display:column>
                <display:setProperty name="basic.show.header" value="false"/>
                <display:setProperty name="basic.show.header" value="false"/>

            </display:table>
        </td>
        <td class="rightColumn" id="companyId">

        </td>
    </tr>
</table>
<div class="cleaner">&nbsp;</div>

<script type="text/javascript">
    $(document).ready(function() {
        attachViewTemplateDocxLink('<fmt:message key="templateDocx.confirmDelete" />');

    <c:if test="${requestScope.actionBean.template.id != null}">
        loadTemplateDocx(${requestScope.actionBean.template.id}, '<fmt:message key="templateDocx.confirmDelete" />','${requestScope.actionBean.planModuleType}');
    </c:if>
    });

</script>
