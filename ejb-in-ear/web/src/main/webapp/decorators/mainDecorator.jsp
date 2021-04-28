<%@ include file="../includes/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Window-target" content="_top"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />

    <title><fmt:message key="application.title"/> &gt; <decorator:title/></title>

    <!-- Application Icon-->
    <link rel="Shortcut Icon" href="${pageContext.request.contextPath}/favicon.ico"/>

    <!-- CSS Imports-->

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/default-style.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/displaytag/displaytag.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/jquery/pagination.css"/>

    <!-- JavaScript Imports-->
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery.curvycorners.packed.js"></script>
    
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/scripts.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/submenu.js"></script>

    <decorator:head/>

</head>
<body>
<div id="frame">

    <div id="main-header">
        <%@ include file="../includes/mainHeader.jsp" %>
    </div>
    <div id="sub-menu">
        <%@ include file="../includes/submenu.jsp" %>
    </div>

    <div id="main">
        <div id="main-content">
            <decorator:body/>
        </div>
    </div>

    <div id="main-footer">
        <%@ include file="../includes/mainFooter.jsp" %>
    </div>

</div>
</body>
</html>