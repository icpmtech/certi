<%@ include file="../../../includes/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="user.resetPassword.main.title"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/keyboard/keyboard.css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>

    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/keyboard/keyboard.js"></script>
</head>
<body>
<div id="home-content">
    <div class="categories">

        <c:if test="${requestScope.actionBean.showForm == null || requestScope.actionBean.showForm == true}">
            <h2 class="form"><span><fmt:message key="user.resetPassword.subtitle"/></span></h2>
        </c:if>

        <s:messages/>
        <s:errors/>

        <c:choose>
            <c:when test="${requestScope.actionBean.showForm == null || requestScope.actionBean.showForm == true}">
                <s:form beanclass="com.criticalsoftware.certitools.presentation.action.UserActivationActionBean"
                        focus="" class="form">
                    <p>
                        <s:label for="email1"><fmt:message key="user.email"/> (*):</s:label>
                        <s:text name="user.email" class="largeInput" id="email1"/>
                    </p>

                    <p>
                        <s:label for="fiscalNumber"><fmt:message key="user.fiscalNumber"/> (*):</s:label>
                        <s:text name="user.fiscalNumber" class="largeInput" id="fiscalNumber"/>
                    </p>

                    <div class="formButtons">
                        <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                        <s:submit name="resetPassword" class="button"><fmt:message key="common.login"/></s:submit>
                    </div>
                </s:form>
            </c:when>
            <c:otherwise>
                <fmt:message key="user.resetPassword.success2"/>
            </c:otherwise>
        </c:choose>


    </div>
</div>

</body>
</html>
