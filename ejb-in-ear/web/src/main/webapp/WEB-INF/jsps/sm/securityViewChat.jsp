<%@ include file="../../../includes/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" style="height: 100%;">
<head>
    <title><c:out value="${requestScope.actionBean.chatTitle}"/></title>

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/sm/security.css"
          media="screen"/>

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/default-style.css"/>

    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>
</head>
<body class="popout-chat">
<jsp:include page="securityChat.jsp"/>
</body>
</html>