<%@ include file="../../../includes/taglibs.jsp" %>
<%@ include file="../../../includes/planSetTitle.jsp" %>

<head>
    <title>${pageScope.planTitle}</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/sm/security.css"
          media="screen"/>
</head>

<s:errors/>

<table class="securityHeader peiTableTitle" cellpadding="0" cellspacing="0">
    <tr>
        <td><h1 class="peiH1"><fmt:message key="menu.security"/></h1></td>
    </tr>
</table>

<c:choose>
    <c:when test="${fn:length(requestScope.actionBean.contracts) <= 0}">
        <fmt:message key="security.noContracts"/>
    </c:when>
    <c:otherwise>
        <s:form action="${pageContext.request.contextPath}/sm/Security.action"
                id="contractForm" method="get" class="form-pei-admin-select-pei">
            <p>
                <c:if test="${fn:length(requestScope.actionBean.companies) > 1}">
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
                </c:if>

                <label for="contractsLabel" style="margin-left: 10px" class="floatNone">
                    <fmt:message key="pei.contract"/>:</label>
                <select id="contractsLabel" name="contractId" class="mediumInput floatNone" style="width: 300px;">
                    <c:forEach items="${requestScope.actionBean.contracts}" var="contract">
                        <option value="${contract.id}">${contract.contractDesignation}</option>
                    </c:forEach>
                </select>
                <input type="submit" id="frontOffice" name="frontOffice" class="button floatNone buttonPeiView"
                       value="<fmt:message key="common.view"/>"/>
            </p>
            <input type="hidden" name="_eventName" value="frontOffice"/>
        </s:form>

        <script type="text/javascript">
            attachOnChangePeiMainCompanies('${pageContext.request.contextPath}'
                    + '/sm/Security.action?loadCompanyContracts=}');
        </script>
    </c:otherwise>
</c:choose>

<div class="cleaner">
    <p>&nbsp;</p>
    <p>&nbsp;</p>
</div>
