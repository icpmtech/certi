<%@ include file="../includes/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Window-target" content="_top"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>

    <title><fmt:message key="application.title"/> &gt; <decorator:title/></title>

    <!-- Application Icon-->
    <link rel="Shortcut Icon" href="${pageContext.request.contextPath}/favicon.ico"/>

    <!-- CSS Imports-->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/default-style.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/scripts/colorbox/colorbox.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/homeMenu.css"/>

    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/colorbox/jquery.colorbox-min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/hoverIntent.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/superfish.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/openPagesScripts.js"></script>

    <style type="text/css">
        
    </style>
</head>
<body>
<div id="frame">

    <div id="header">
        <%@ include file="../includes/homeHeader.jsp" %>
    </div>


    <div id="home-banner">
        <div class="content" style="padding: 30px 0 30px 0">
            <div class="titles">
                <div class="title"><fmt:message key="home.page.title"/></div>
                <div class="subtitle"><fmt:message key="home.page.subtitle"/></div>
            </div>
            <div class="pictures">
                <img width="390px" height="230px"
                     src="${pageContext.request.contextPath}/images/random-${requestScope.actionBean.pictureId}.gif"
                     alt="<fmt:message key="ramdom.picture"/>" title="<fmt:message key="ramdom.picture"/>"/>
            </div>
            <div class="cleaner"><!--Do not remove this empty div--></div>
        </div>
    </div>

    <div id="home-content">
        <div class="sep" style="height: 20px;"><!--Do not remove this empty div--></div>
        <decorator:body/>
    </div>

    <div id="footer">
        <div class="content">
            <%@ include file="../includes/homeFooter.jsp" %>
        </div>
    </div>

</div>
</body>
</html>