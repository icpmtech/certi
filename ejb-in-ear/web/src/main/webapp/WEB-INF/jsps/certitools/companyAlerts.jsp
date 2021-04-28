<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="companies.alerts.page.title"/></title>

<h1><fmt:message key="companies.alerts.page.title"/></h1>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyAlertsActionBean" class="form"
        method="post">

    <s:messages/>
    <s:errors/>

    <h2><span>${requestScope.actionBean.companyName}</span></h2>

    <c:choose>
        <c:when test="${requestScope.actionBean.contracts != null && fn:length(requestScope.actionBean.contracts) > 0}">
            <c:forEach items="${requestScope.actionBean.contracts}" var="contract">
                <h6 style="margin-bottom:5px;margin-top:5px;">
                    <span style="padding-right:0"><fmt:message
                            key="contract"/> ${contract.number} - ${contract.contractDesignation} - <fmt:message
                            key="${contract.module.moduleType.key}"/>
                    </span>
                </h6>

                <ul class="permissionsList" style="margin-left:20px;margin-top:10px">
                    <li>
                        <s:label for="all${contract.id}" class="strong"><fmt:message key="common.all"/></s:label>
                        <s:checkbox id="all${contract.id}" value="-1" name="contractListMap[${contract.id}]"/>
                    </li>
                    <c:forEach items="${contract.contractPermissions}" var="permission">
                        <li>
                            <s:label for="permission${permission.id}">${permission.name}</s:label>
                            <s:checkbox id="permission${permission.id}"
                                        name="contractListMap[${contract.id}]" value="${permission.id}"/>
                        </li>
                    </c:forEach>
                </ul>
                <div class="cleaner"><!-- --></div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <fmt:message key="companies.noContracts"/>
        </c:otherwise>
    </c:choose>
    <div class="cleaner"><!-- --></div>

    <c:if test="${requestScope.actionBean.contracts != null && fn:length(requestScope.actionBean.contracts) > 0}">
        <h2><span><fmt:message key="companies.alerts.alertDefinition"/></span></h2>

        <p>
            <s:label for="fromField">
                <fmt:message key="companies.alerts.from"/> (*):
            </s:label>
            <s:text name="from" id="fromField" value="${requestScope.actionBean.from}" class="largeInput"/>
        </p>

        <p>
            <s:label for="subjectField">
                <fmt:message key="companies.alerts.subject"/> (*):
            </s:label>
            <s:text name="subject" id="subjectField" class="largeInput" maxlength="250"/>
        </p>

        <p>
            <s:label for="bodyField">
                <fmt:message key="companies.alerts.body"/> (*):
            </s:label>
            <s:textarea name="body" id="bodyField" class="largeInput" rows="5"/>
        </p>
    </c:if>

    <div class="formButtons">
        <c:if test="${requestScope.actionBean.contracts != null && fn:length(requestScope.actionBean.contracts) > 0}">
            <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
            <s:submit name="sendAlerts" class="button" id="sendAlert"><fmt:message
                    key="common.send"/></s:submit>
        </c:if>
        <s:submit name="redirectToCompaniesList" class="button"><fmt:message key="common.cancel"/></s:submit>
    </div>

    <s:hidden name="companyId"/>
    <s:hidden name="letter"/>
</s:form>

<script type="text/javascript">
    function attachEventToSendNotification(text) {
        $('#sendAlert').click(function () {
            return confirm(text);
        });
    }
    attachEventToSendNotification("<fmt:message key="companies.alerts.warning"/>")
</script>


