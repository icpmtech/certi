<%@ include file="../includes/taglibs.jsp" %>

<ss:secure roles="user">
    <c:set var="isLoggedIn" value="true" scope="page"/>
</ss:secure>
<div class="content">
    <div class="upper">
        <%@ include file="../includes/mainHeaderLogo.jsp" %>
        <div class="authenticated">
            <c:choose>
                <c:when test="${requestScope.actionBean.context.locale.language == 'en'}">
                    <c:set var="enKlass" value="color: #ff0000"/>
                </c:when>
                <c:otherwise>
                    <c:set var="ptKlass" value="color: #ff0000"/>
                </c:otherwise>
            </c:choose>

            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.HomeActionBean"
                    event="changeLanguageAndSave">
                <s:param name="language" value="PT"/>
                <span style="${pageScope.ptKlass}"><fmt:message key="locale.pt"/></span>
            </s:link>
            &nbsp;<span class="separator">|</span>&nbsp;
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.HomeActionBean"
                    event="changeLanguageAndSave" style="padding-right: 25px;">
                <s:param name="language" value="EN"/>
                <span style="${pageScope.enKlass}"><fmt:message key="locale.en"/></span>
            </s:link>

            <span class="welcome"><fmt:message key="application.welcome.message"/></span>
                <span class="name">
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserProfileActionBean">
                        <c:out value="${sessionScope.user.name}"/>
                    </s:link>&nbsp;
                </span>
                <span class="logout">
                    <span class="separator">(</span>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.LogoutActionBean">
                        <fmt:message key="main.logout"/>
                    </s:link>
                    <span class="separator">)</span>
                </span>
        </div>
    </div>
    <div class="cleaner"><!--Do not remove this empty div--></div>
    <div class="menu">
        <%@ include file="../includes/menu.jsp" %>
    </div>
</div>