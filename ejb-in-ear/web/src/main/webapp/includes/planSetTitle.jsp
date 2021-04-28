<%@ include file="taglibs.jsp" %>

<%-- TODO-MODULE --%>
<c:choose>
    <c:when test="${requestScope.actionBean.planModuleType == 'PRV'}">
        <c:set var="planTitle"><fmt:message key="prv.view.title"/></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="planTitle"><fmt:message key="pei.view.title"/></c:set>
    </c:otherwise>
</c:choose>