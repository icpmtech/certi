<%@ include file="../includes/taglibs.jsp" %>

<ul>
    <c:forEach items="${sessionScope.menu.menuItems}" var="item">
        <c:choose>
            <c:when test="${item.selected == true && item.resourceKey == true}">
                <li class="option-on"><a
                        href="${pageContext.request.contextPath}${item.link}"><fmt:message key="${item.key}"/></a></li>
            </c:when>
            <c:when test="${item.selected == true}">
                <li class="option-on"><a
                        href="${pageContext.request.contextPath}${item.link}"><c:out value="${item.key}"/></a></li>
            </c:when>

            <c:when test="${item.resourceKey == true}">
                <li class="option-off"><a
                        href="${pageContext.request.contextPath}${item.link}"><fmt:message key="${item.key}"/></a></li>
            </c:when>
            <c:otherwise>
                <li class="option-off"><a
                        href="${pageContext.request.contextPath}${item.link}"><c:out value="${item.key}"/></a></li>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</ul>