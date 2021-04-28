<%@ include file="../includes/taglibs.jsp" %>
<div class="submenu" id="submenuContent">
    <c:if test="${!empty sessionScope.menu.menuItems}">

        <c:forEach items="${requestScope.actionBean.menu.menuItems}" var="item1">
            <c:choose>
                <c:when test="${item1.selected == true && item1.menuItems != null}">
                    <c:forEach items="${item1.menuItems}" var="item2">
                        <c:choose>
                            <c:when test="${item2.resourceKey == true}">
                                <c:set var="item2Key"><fmt:message key="${item2.key}"/></c:set>
                            </c:when>
                            <c:otherwise>
                                <c:set var="item2Key"><c:out value="${item2.key}"/></c:set>
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when test="${item2.selected == true}">
                                <div class="option-on">
                            </c:when>
                            <c:otherwise>
                                <div class="option-off">
                            </c:otherwise>
                        </c:choose>
                        
                                <img src="${pageContext.request.contextPath}/images/${item2.image}" alt="${item2Key}"
                                     title="${item2Key}" style="vertical-align: bottom;"/>

                        <c:choose>
                            <c:when test="${item2.key != null && !empty item2.key}">
                                <a href="${pageContext.request.contextPath}${item2.link}" id="${item2.id}">
                                    ${item2Key}
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}${item2.link}">
                                    ${item2Key}
                                </a>
                            </c:otherwise>
                        </c:choose>

                        </div>
                    </c:forEach>

                <div class="cleaner"><!--Do not remove this empty div--></div>
                </c:when>
            </c:choose>
        </c:forEach>
    </c:if>
</div>

    <c:choose>
        <c:when test="${requestScope.helper}">
            <div id="helper-on" class="helper">
        </c:when>
        <c:otherwise>
            <div id="helper-off" class="helper">
        </c:otherwise>
    </c:choose>

            <img src="${pageContext.request.contextPath}/images/help.png" alt="<fmt:message key="application.help"/>"
                 title="<fmt:message key="application.help"/>" style="vertical-align: bottom;"/>
            <s:link href="${pageContext.request.contextPath}/certitools/Help.action" target="_blank">
                <s:param name="helpId">${fn:substring(requestScope.actionBean.helpId,1,-1)}</s:param>
                <fmt:message key="application.help"/>
            </s:link>
        </div>

        <div class="cleaner"><!--Do not remove this empty div--></div>
