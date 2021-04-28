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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/homeMenu.css"/>

    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/hoverIntent.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/superfish.js"></script>

    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/openPagesScripts.js"></script>

    <decorator:head/>
</head>
<body>
<div id="frame">

    <div id="header">
        <%@ include file="../includes/homeHeader.jsp" %>
    </div>


    <div id="security-banner">
        <div class="content">
            <div class="floatLeft alignLeft">
                <div class="title"><decorator:title default="${pageScope.title}"/></div>
            </div>
        </div>
    </div>

    <div id="home-content">
        <div class="sep"><!--Do not remove this empty div--></div>
        <decorator:body/>
    </div>

    <div id="footer">
        <div class="content">
            <%@ include file="../includes/homeFooter.jsp" %>
        </div>
    </div>

</div>

<script type="text/javascript">
    startHomeMenuPlans();
</script>

</body>
</html>