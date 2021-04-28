<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ include file="../includes/taglibs.jsp" %>
<c:set var="email" scope="page" value="${applicationScope.configuration.emailInfo}"/>

<%
    // check if it's a mobile phone accessing the login page
    String userAgent = request.getHeader("user-agent");

    if (!StringUtils.isBlank(userAgent) && (userAgent.startsWith("BlackBerry")
            || StringUtils.contains(userAgent, "Windows CE") || StringUtils.contains(userAgent, "SymbianOS"))) {
        pageContext.setAttribute("mobile", true);
    } else {
        pageContext.setAttribute("mobile", false);
    }
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Window-target" content="_top"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>

    <title>
        <fmt:message key="login.page.title"/>
    </title>

    <!-- Application Icon-->
    <link rel="Shortcut Icon" href="${pageContext.request.contextPath}/favicon.ico"/>

    <!-- CSS Imports-->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/default-style.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/keyboard/keyboard.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/homeMenu.css"/>

    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>
    <c:if test="${!mobile}">
        <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/keyboard/keyboard.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/keyboard/login.js"></script>
    </c:if>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/hoverIntent.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/superfish.js"></script>

    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/openPagesScripts.js"></script>
</head>
<body>

<div id="frame">

    <div id="header">
        <% request.setAttribute("loginOption", "-on");%>
        <%@ include file="../includes/homeHeader.jsp" %>
    </div>


    <div id="security-banner">
        <div class="content">
            <div class="floatLeft alignLeft">
                <div class="title"><fmt:message key="login.page.title"/></div>
                <div class="subtitle"><fmt:message key="login.page.subtitle"/></div>
            </div>
            <div class="security-img"><img src="${pageContext.request.contextPath}/images/Login-Image.png" alt=""></div>
        </div>
    </div>

    <div id="home-content">
        <div class="sep"><!--Do not remove this empty div--></div>

        <form action="j_security_check" method="post">
            <div id="form">
                <div class="homeContentLeft">
                    <c:if test="${param.error != null}">
                        <div id="errorMsg">
                            <fmt:message key="login.page.error"/>
                        </div>
                    </c:if>

                    <c:if test="${param.errorUniquesession != null}">
                        <div id="errorMsg">
                            <fmt:message key="login.page.errorUniquesession"/>
                        </div>
                    </c:if>

                    <c:if test="${param.activationSucess != null}">
                        <fmt:message key="stripes.messages.header"/>
                        <fmt:message key="stripes.messages.beforeMessage"/>
                        <fmt:message key="user.activation.sucess"/>
                        <fmt:message key="stripes.messages.afterMessage"/>
                        <fmt:message key="stripes.messages.footer"/>
                    </c:if>

                    <div id="infoMsg">
                        <fmt:message key="login.page.description"/>
                    </div>
                    <div class="cleaner"><!--do not remove this empty div--></div>
                    <div>
                        <div class="row">
                            <div class="title"><fmt:message key="login.page.email"/>:</div>
                            <div class="input">
                                <input type="text" name="j_username" id="j_username" class="loginInput"/>
                            </div>
                            <div class="cleaner"><!--do not remove this empty div--></div>
                        </div>
                        <div class="row">
                            <div class="title"><fmt:message key="login.page.password"/>:</div>
                            <div class="input" style="height: 20px">
                                <input type="password" name="j_password" id="j_password" class="loginInput"
                                       <c:if test="${!mobile}">readonly="true"</c:if> />
                            </div>
                            <div class="cleaner"><!--do not remove this empty div--></div>
                        </div>

                        <div class="cleaner"><!--do not remove this empty div--></div>
                        <div class="forgotPass">
                            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.UserActivationActionBean"
                                    event="resetPasswordForm"
                                    class="loginLink"><fmt:message key="login.page.forgotten.password"/></s:link>

                        </div>
                        <div class="buttons">
                            <input type="submit" value="<fmt:message key="common.login"/>" class="button"/>
                        </div>
                    </div>
                </div>
                <div class="homeContentRight">
                    <div class="loginLeftBox">
                        <div class="header"><fmt:message key="login.page.fieldset.box.first.title"/></div>
                        <div class="text"><fmt:message key="login.page.fieldset.box.first.text"/></div>
                        <div class="header"><fmt:message key="login.page.fieldset.box.second.title"/></div>
                        <div class="text"><fmt:message key="login.page.fieldset.box.second.text"/></div>
                        <div class="contacts">
                            <div class="email">
                                <a href="mailto:${pageScope.email}">
                                    <fmt:message key="contacts.email.general"/>
                                </a>
                            </div>
                            <div class="email">
                                <a href="mailto:certitools.legislacao@certitecna.pt">
                                    <fmt:message key="contacts.email.legislation"/>
                                </a>
                            </div>
                            <div class="email">
                                <a href="mailto:certitools.PEI@certitecna.pt">
                                    <fmt:message key="contacts.email.pei"/>
                                </a>
                            </div>
                            <div class="phone" style="margin-top:20px"><fmt:message
                                    key="login.page.fieldset.box.contact.phone"/></div>
                        </div>
                    </div>
                </div>
                <div class="cleaner"><!--do not remove this empty div--></div>
            </div>
        </form>
    </div>


</div>

<div id="footer">
    <div class="content">
        <div class="alignLeft floatLeft">
            <ul id="footer-menu">
                <li class="option${requestScope.home}">
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.HomeActionBean">
                        <fmt:message key="home.page.link.home"/>
                    </s:link>
                </li>
                <li class="option${requestScope.what}">
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean">
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
                <li class="option-on">
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
                    <img style="vertical-align: middle;"
                         src="${pageContext.request.contextPath}/images/icon-facebook.png" alt="Facebook"/> Facebook
                </a>
            </div>
        </div>
        <div class="floatRight alignRight">
            <fmt:message key="footer.copyright.message"/>
            <br/>
            <fmt:message key="footer.developed.message"/>
            <a class="img" href="http://www.criticalsoftware.com"
               onclick="window.open('http://www.criticalsoftware.com'); return false">
                <img alt="<fmt:message key="footer.critical" />"
                     src="${pageContext.request.contextPath}/images/critical-logo.png"
                     style="padding-top: 3px; vertical-align: text-bottom;"/></a>
        </div>
        <div class="cleaner"><!--Do not remove this empty div--></div>
    </div>
</div>
<script type="text/javascript">
    if (self != top) {
        top.location.reload();
    }
</script>

<%@include file="../includes/analyticsFooter.jsp" %>

</body>
</html>