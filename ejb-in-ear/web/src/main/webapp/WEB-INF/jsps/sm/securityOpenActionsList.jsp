<%@ include file="../../../includes/taglibs.jsp" %>

<div class="securityActionsList">
    <div class="securityActionsListHeader"><fmt:message key="security.open.actions"/></div>
    <ul id="securityActions">
        <c:forEach items="${requestScope.actionBean.correctiveActionsList}" var="action">
            <li>
                <span class="securityActionDate">${action.startDateFormatted}</span>
                <span class="securityActionResponsible">${action.executionResponsible}</span>
                    <span class="securityActionsListCode">
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean"
                            event="actionsPlanningEdit" class="">
                        <s:param name="contractId" value="${requestScope.actionBean.contract.id}"/>
                        <s:param name="correctiveActionId" value="${action.id}"/>
                        <c:out value="${action.code}"/>
                    </s:link>
                    </span>
            </li>
        </c:forEach>
        <c:if test="${empty requestScope.actionBean.correctiveActionsList}">
            <li><fmt:message key="security.open.actions.empty"/></li>
        </c:if>
    </ul>
</div>
