<%@ include file="../includes/taglibs.jsp"%>

<ss:secure roles="user">
    <c:set var="isLoggedIn" value="true" scope="page"/>
</ss:secure>
<div class="alignLeft floatLeft">
    <ul id="footer-menu">
        <li class="option${requestScope.home}">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.HomeActionBean">
                <fmt:message key="home.page.link.home"/>
            </s:link>
        </li>
        <li class="option${requestScope.what}">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.WhatItIsActionBean">
                <fmt:message key="home.page.link.what.it.is"/>
            </s:link>
        </li>
        <li class="option${requestScope.how}">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.HowItWorksActionBean">
                <fmt:message key="home.page.link.how.it.works"/>
            </s:link>
        </li>
        <li class="option${requestScope.legislation}">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.LegislationPublicActionBean">
                <fmt:message key="home.page.link.legislation"/>
            </s:link>
        </li>
        <li class="option${requestScope.plans}">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean">
                <fmt:message key="home.page.link.plans"/>
            </s:link>
        </li>
        <li class="option${requestScope.contacts}">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.ContactsActionBean">
                <fmt:message key="home.page.link.contacts"/>
            </s:link>
        </li>
        <li>
            <span class="separator">|</span>
        </li>
        <li class="option${requestScope.security}">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.LoginRedirectActionBean">
                <c:choose>
                    <c:when test="${pageScope.isLoggedIn}">
                        <fmt:message key="main.continue"/>
                    </c:when>
                    <c:otherwise>
                            <fmt:message key="main.login"/>
                    </c:otherwise>
                </c:choose>
            </s:link>
        </li>
    </ul>
    <div style="padding-top: 5px; clear: both;">
        <a href="https://www.facebook.com/pages/CertiTools/127900053958402" target="_blank">
        <img style="vertical-align: middle;" src="${pageContext.request.contextPath}/images/icon-facebook.png" alt="Facebook"/> Facebook
        </a>
    </div>
</div>
<div class="floatRight alignRight">
    <fmt:message key="footer.copyright.message"/>
    <br/>
    <fmt:message key="footer.developed.message"/>
    <a class="img" href="http://www.criticalsoftware.com" onclick="window.open('http://www.criticalsoftware.com'); return false;">
        <img alt="<fmt:message key="footer.critical" />"
             src="${pageContext.request.contextPath}/images/critical-logo.png"
             style="padding-top: 3px; vertical-align: text-bottom;"/></a>
</div>
<div class="cleaner"><!--Do not remove this empty div--></div>

<%@include file="analyticsFooter.jsp"%>