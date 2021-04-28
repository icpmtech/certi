<%@ include file="../../../includes/taglibs.jsp" %>
<c:set var="email" scope="page" value="${applicationScope.configuration.emailInfo}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Window-target" content="_top"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />

    <title>
        <fmt:message key="application.title"/> &gt; <fmt:message key="license.page.subtitle"/> &gt; <fmt:message
            key="license.page.title"/>
    </title>

    <!-- Application Icon-->
    <link rel="Shortcut Icon" href="${pageContext.request.contextPath}/favicon.ico"/>

    <!-- CSS Imports-->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/default-style.css"/>

</head>
<body>
<div id="frame">

    <div id="header">
        <div class="content">
            <div class="floatLeft alignLeft">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.HomeActionBean" class="img">
                    <img width="280" height="64" alt="<fmt:message key="application.title" />" 
                         src="${pageContext.request.contextPath}/images/logo-home_${requestScope.actionBean.context.locale}.gif" style="margin-top: 5px;"/>
                </s:link>
            </div>
            <div class="cleaner"><!--Do not remove this empty div--></div>
        </div>
    </div>


    <div id="security-banner">
        <div class="content">
            <div class="floatLeft alignLeft">
                <div class="title"><fmt:message key="license.page.title"/></div>
                <div class="subtitle"><fmt:message key="license.page.subtitle"/></div>
            </div>
            <div class="security-img"><img src="${pageContext.request.contextPath}/images/Login-Image.png" alt=""></div>
        </div>
    </div>

    <div id="home-content">
        <div class="sep"><!--Do not remove this empty div--></div>

        <form action="j_security_check" method="post">
            <div id="form">
                <div class="contentLeft">
                    <c:if test="${param.error != null}">
                        <div id="errorMsg">
                            <fmt:message key="login.page.error"/>
                        </div>
                    </c:if>
                    <div id="infoMsg">
                        <fmt:message key="login.page.description"/>
                    </div>
                    <div class="cleaner"><!--do not remove this empty div--></div>
                    <div>
                        <div class="row">
                            <div class="title" style="width:160px;"><fmt:message key="login.page.email"/>:</div>
                            <div style="float:left;width:360px;">
                                <input type="text" name="j_username" id="j_username" class="loginInput"/>
                            </div>
                            <div class="cleaner"><!--do not remove this empty div--></div>
                        </div>
                        <div class="row">
                            <div class="title" style="width:160px;"><fmt:message key="login.page.password"/>:</div>
                            <div style="float:left;width:360px;">
                                <input type="password" name="j_password" id="j_password"
                                       class="loginInput"/>
                            </div>
                            <div class="cleaner"><!--do not remove this empty div--></div>
                        </div>
                        <div class="forgotPass" style="width:51%;margin-top:10px;">
                            <a href="mailto:${pageScope.email}?subject=<fmt:message key="login.page.forgotten.password"/>"><fmt:message
                                    key="login.page.forgotten.password"/></a>
                        </div>
                        <div class="buttons" style="width: 492px;">
                            <input type="submit" value="<fmt:message key="common.login"/>" class="button"/>
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
        <%@ include file="../../../includes/licenseFooter.jsp" %>
    </div>
</div>
</body>

<script type="text/javascript">
    document.getElementById("j_username").focus();
</script>
</html>