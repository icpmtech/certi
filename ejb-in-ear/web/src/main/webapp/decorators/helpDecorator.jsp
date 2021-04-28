<%@ include file="../includes/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Window-target" content="_top"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>

    <title><fmt:message key="application.title"/> &gt; <fmt:message key="application.help"/> &gt;
        <decorator:title/></title>

    <!-- Application Icon-->
    <link rel="Shortcut Icon" href="${pageContext.request.contextPath}/favicon.ico"/>

    <!-- CSS Imports-->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/default-style.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/displaytag/displaytag.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/help/help.css" media="screen"/>

    <!-- JavaScript Imports-->
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/scripts.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/hoverIntent.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/supersubs.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/superfish.js"></script>
</head>
<body>
<div id="frame">
    <div id="main-header" style="height: auto;">
        <div class="content">
            <div class="upper">
                <div class="alignLeft floatLeft">
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.LoginRedirectActionBean"
                            class="img">
                        <img src="${pageContext.request.contextPath}/images/Logotype-Aplicacao.gif"
                             width="220" height="59" alt="logo" title="<fmt:message key="application.client.name"/>"/>
                    </s:link>
                </div>
                <div class="authenticated" style="width: 215px;">
                    <span style="color: #B3D3E0; padding-right: 40px;"><fmt:message key="help.title"/></span>
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
            <table id="helpHeaderContent" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="helpMenu">
                        <%@ include file="../includes/helpMenu.jsp" %>
                    </td>
                    <td class="alignRight">
                        <form action="/certitools/Help.action" method="GET"
                              style="margin: 0; padding: 0; display: inline;">
                            <input type="hidden" name="_eventName" value="searchHelp"/>
                            <input type="text" name="searchPhrase" value="${requestScope.actionBean.searchPhrase}">
                            <input value="<fmt:message key="common.search"/>" class="searchButton" type="submit"/>
                        </form>
                    </td>
                </tr>
            </table>
        </div>
    </div>
    <div class="cleaner"><!--Do not remove this empty div--></div>
    <div id="main" style="margin-top:0;background:none">
        <div id="main-content">
            <decorator:body/>
        </div>
    </div>
    <div id="main-footer">
        <%@ include file="../includes/mainFooter.jsp" %>
    </div>
</div>
<script type="text/javascript">
    jQuery(function() {
        jQuery('ul.sf-menu').supersubs({
            minWidth: 12,
            maxWidth: 12,
            /*maxWidth:    25,   // maximum width of sub-menus in em units*/
            extraWidth:  1     // extra width can ensure lines don't sometimes turn over
            // due to slight rounding differences and font-family
        }).superfish({
            animation:   { show:"show", speed: 0},
            delay: 1000

        });
    });
    $(document).click(function() {
        $('ul.sf-menu').hideSuperfishUl();
    });
</script>
</body>
</html>