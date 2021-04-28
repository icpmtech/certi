<%@ include file="../includes/taglibs.jsp" %>

<ss:secure roles="administrator">
    <c:set var="isLoggedIn" value="true" scope="page"/>
</ss:secure>
<div class="content">
    <div class="upper">
        <div class="floatLeft alignLeft">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.license.LicenseActionBean">
                <img src="${pageContext.request.contextPath}/images/Logotype-Aplicacao.gif" width="220" height="59" alt="logo" 
                     title="<fmt:message key="application.client.name"/>"/>
            </s:link>


        </div>
        <div class="authenticated">
            <span class="welcome"><fmt:message key="application.welcome.message"/></span>
                <span class="name">
                        <c:out value="${sessionScope.user.name}"/>
                </span>
                <span class="logout">
                    <span class="separator">(</span>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.license.LicenseLogoutActionBean">
                        <fmt:message key="main.logout"/>
                    </s:link>
                    <span class="separator">)</span>
                </span>
        </div>
    </div>
    <div class="cleaner"><!--Do not remove this empty div--></div>
</div>