<%@ page import="com.criticalsoftware.certitools.presentation.util.HTMLEscapeAndNL2BR" %>
<%@ include file="../../../includes/taglibs.jsp" %>

<div class="hidden">
    <ss:secure roles="peimanager">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplatesDocxActionBean"
                event="updateTemplateDocxForm" class="operationEdit copyCompany">
            <s:param name="template.id">${requestScope.actionBean.template.id}</s:param>
            <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
            <fmt:message key="common.edit"/> <fmt:message key="templateDocx"/>
        </s:link>
    </ss:secure>
    
    <ss:secure roles="peimanager">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplatesDocxActionBean"
                event="deleteTemplateDocx" class="operationDelete copyCompany confirmDelete">
            <s:param name="template.id">${requestScope.actionBean.template.id}</s:param>
            <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
            <fmt:message key="common.delete"/> <fmt:message key="templateDocx"/>
        </s:link>
    </ss:secure>
</div>

<c:choose>
    <c:when test="${requestScope.actionBean.template.title == null}">
        <p>&nbsp;</p>
    </c:when>
    <c:otherwise>

        <h1><c:out value="${requestScope.actionBean.template.title}"/></h1>
        <p>
            <span class="label"><fmt:message key="templateDocx.templateFile"/>: </span>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplatesDocxActionBean" event="downloadTemplateDocx">
                <s:param name="template.id">${requestScope.actionBean.template.id}</s:param>
                <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
                <img src="${pageContext.request.contextPath}/images/document-word.png"
                             title="<fmt:message key="templateDocx.templateFile"/>"
                             alt="<fmt:message key="templateDocx.templateFile"/>"/>
            </s:link>
        </p>

        <p>
            <span class="label"><fmt:message key="contract.module"/>: </span>
            <c:out value="${requestScope.actionBean.template.module.name}"/>
        </p>

        <p>
            <c:set var="observationsVar">${requestScope.actionBean.template.observations}</c:set>
            <% out.print(HTMLEscapeAndNL2BR.replaceAndEscape(((String) pageContext.getAttribute("observationsVar")))); %>
        </p>

        <h3><fmt:message key="templateDocx.contractsAssociated"/>:</h3>

        <c:if test="${fn:length(requestScope.actionBean.template.contracts) <= 0}">
            <fmt:message key="contract.noContracts"/>
        </c:if>

        <table class="templatesContractsTable">

            <c:forEach items="${requestScope.actionBean.template.contracts}" var="contract" varStatus="rowCounter">
                <c:choose>
                    <c:when test="${rowCounter.count % 2 == 0}">
                        <c:set var="rowStyle" scope="page" value="odd"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="rowStyle" scope="page" value="even"/>
                    </c:otherwise>
                </c:choose>
                <tr class="${rowStyle}">
                    <td>
                        <span class="label"><fmt:message key="pei.entity"/>:</span>
                        <c:out value="${contract.company.name}"/>
                    </td>
                    <td>
                        <span class="label"><fmt:message key="contract.contractDesignation"/>:</span>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMActionBean">
                            <s:param name="contractId">${contract.id}</s:param>
                            <s:param name="companyId">${contract.company.id}</s:param>
                            <s:param name="planModuleType">${contract.module.moduleType}</s:param>
                        <c:out value="${contract.contractDesignation}"/>
                        </s:link>
                    </td>
                    <td>
                        <span class="label"><fmt:message key="contract.number"/>:</span>
                        <c:out value="${contract.number}"/>
                    </td>
                </tr>
            </c:forEach>
        </table>

    </c:otherwise>
</c:choose>

<p>&nbsp;</p>

<h2 style="width: inherit;"><span><fmt:message key="templateDocx.associateToContrats"/></span></h2>
<s:form beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplatesDocxActionBean">
    <label for="companies" class="label"><fmt:message key="pei.entity"/>:</label>
    <select id="companies" name="companyId" class="mediumInput" style="float: none !important;">
        <c:forEach items="${requestScope.actionBean.companies}" var="company">
            <option value="${company.id}">${company.name}</option>
        </c:forEach>
    </select>

    <c:forEach items="${requestScope.actionBean.companies}" var="company" varStatus="rowCounter">
        <div class="contractsToAssociateDiv" id="contractsToAssociateDiv${company.id}"
             <c:if test="${rowCounter.count != 1}">style="display: none;"</c:if>>
            <table class="templatesContractsTable"
                   style="margin-top: 10px !important;"
                   id="companyTable${company.id}">
                <c:forEach items="${company.contracts}" var="contract">
                    <tr>
                        <td style="width: 32px;">
                            <s:checkbox name="selectedContracts" value="${contract.id}"
                                        id="contractCheckbox${contract.id}"/>
                        </td>
                        <td>
                            <span class="label"><fmt:message key="contract.contractDesignation"/>: </span>
                            <c:out value="${contract.contractDesignation}"/>
                        </td>
                        <td>
                            <span class="label"><fmt:message key="contract.number"/>: </span>
                            <c:out value="${contract.number}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
            <c:if test="${company.contracts == null || fn:length(company.contracts) <= 0}">
                <fmt:message key="contract.noContracts"/>
            </c:if>
        </div>
    </c:forEach>

    <s:hidden name="template.id"/>
    <input type="hidden" name="_eventName" value="insertTemplateDocxContractAssociation"/>
    <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>

    <div class="formButtons">
        <s:submit name="insertTemplateDocxContractAssociation" class="button"><fmt:message
                key="common.submit"/></s:submit>
    </div>
</s:form>

<script type="text/javascript">
    function attachCompanySelectBox() {
        $('#companies').change(function() {
            $('.contractsToAssociateDiv').hide();
            $('#contractsToAssociateDiv' + $('#companies').val()).show();
        });
    }

    $(document).ready(function() {
        attachCompanySelectBox();
    });

</script>
