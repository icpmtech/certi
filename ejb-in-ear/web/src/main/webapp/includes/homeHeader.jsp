<%@ include file="../includes/taglibs.jsp" %>

<ss:secure roles="user">
    <c:set var="isLoggedIn" value="true" scope="page"/>
</ss:secure>

<div class="content">
    <div class="floatLeft alignLeft" style="width: 280px">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.HomeActionBean" class="img">
            <img width="280" height="64" alt="<fmt:message key="application.title" />"
                 src="${pageContext.request.contextPath}/images/logo-home_${requestScope.actionBean.context.locale}.gif"
                 style="margin-top: 5px;"/>
        </s:link>
    </div>
    <div class="floatRight alignRight" style="width: 690px">
        <div class="locale">
            <c:choose>
                <c:when test="${requestScope.actionBean.context.locale.language == 'en'}">
                    <c:set var="enKlass" value="color: #ff0000"/>
                </c:when>
                <c:otherwise>
                    <c:set var="ptKlass" value="color: #ff0000"/>
                </c:otherwise>
            </c:choose>

            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.HomeActionBean"
                    event="changeLanguage">
                <s:param name="language" value="PT"/>
                <span style="${pageScope.ptKlass}"><fmt:message key="locale.pt"/></span>
            </s:link>
            &nbsp;<span class="separator">|</span>&nbsp;
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.HomeActionBean"
                    event="changeLanguage">
                <s:param name="language" value="EN"/>
                <span style="${pageScope.enKlass}"><fmt:message key="locale.en"/></span>
            </s:link>
        </div>
        <div class="alignRight">
            <ul id="home-menu" class="floatRight">
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
                    <ul class="sf-menu" id="menuPlans">
                        <li>
                            <a href="#"><fmt:message key="home.page.link.plans"/></a>
                            <ul>
                                <!-- TODO-MODULE -->
                                <li>
                                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                                            style="color: #0000AA;">
                                        <s:param name="module">PSI</s:param>
                                        <fmt:message key="home.page.link.plans.psi"/>
                                    </s:link>
                                </li>
                                <li class="borderTop">
                                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                                            style="color: #C00000;">
                                        <s:param name="module">PEI</s:param>
                                        <fmt:message key="home.page.link.plans.pei"/>
                                    </s:link>
                                </li>
                                <li class="borderTop">
                                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                                            style="color: #00B050;">
                                        <s:param name="module">PPREV</s:param>
                                        <fmt:message key="home.page.link.plans.pprev"/>
                                    </s:link>
                                    </li>
                                <li class="borderTop">
                                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                                            style="color: #E46C0A;">
                                        <s:param name="module">SECURITY</s:param>
                                        <fmt:message key="home.page.link.plans.security"/>
                                    </s:link>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </li>
                <li class="option${requestScope.contacts}">
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.ContactsActionBean">
                        <fmt:message key="home.page.link.contacts"/>
                    </s:link>
                </li>
                <li class="separator">|</li>
                <li class="option${requestScope.loginOption}">
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
            <div class="cleaner"><!--Do not remove this empty div--></div>
        </div>
    </div>
    <div class="cleaner"><!--Do not remove this empty div--></div>
</div>

<script type="text/javascript">
    startHomeMenuPlans();
</script>