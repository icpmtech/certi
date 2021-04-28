<%@ include file="../../../includes/taglibs.jsp" %>

<head>
    <title><fmt:message key="application.title"/> - <fmt:message key="user.profile.title"/></title>

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/keyboard/keyboard.css"/>

    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/keyboard/keyboard.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/keyboard/userProfile.js"></script>

</head>
<body>
<h1><fmt:message key="user.profile.title"/></h1>

<div class="form">
    <s:messages/>
    <s:errors/>
</div>

<h2 class="form"><span><fmt:message key="user.profile.data"/></span></h2>

<table class="profileTable">

    <tr>
        <td class="labelBlue">
            <fmt:message key="companies.company"/>:
        </td>
        <td>
            <c:out value="${sessionScope.user.company.name}"/>
        </td>
    </tr>
    <tr>
        <td class="labelBlue">
            <fmt:message key="user.name"/>:
        </td>
        <td>
            <c:out value="${sessionScope.user.name}"/>
        </td>
    </tr>
    <tr>
        <td class="labelBlue">
            <fmt:message key="user.email"/>:
        </td>
        <td>
            <c:out value="${sessionScope.user.email}"/>
        </td>
    </tr>
    <tr>
        <td class="labelBlue">
            <fmt:message key="user.fiscalNumber"/>:
        </td>
        <td>
            <c:out value="${sessionScope.user.fiscalNumber}"/>
        </td>
    </tr>
    <tr>
        <td class="labelBlue">
            <fmt:message key="user.phone"/>:
        </td>
        <td>
            <c:out value="${sessionScope.user.phone}"/>
        </td>
    </tr>
</table>

<c:if test="${!applicationScope.configuration.localInstallation}">
    <h2 class="form"><span><fmt:message key="user.profile.subtitle"/></span></h2>
    <s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.UserProfileActionBean"
            class="form"
            style="width: 700px;">
        <input type="hidden" name="_eventName" value="update"/>
        <s:label for="oldPassword"><fmt:message key="oldPassword.label"/> (*):</s:label>
        <s:password id="oldPassword" name="oldPassword" class="largeInput"
                    style="margin-right: 10px;" readonly="true"/>

        <div class="cleaner"><!--Do not remove--></div>
        <s:label for="newPassword"><fmt:message key="newPassword.label"/> (*):</s:label>
        <s:password id="newPassword" name="newPassword" class="largeInput"
                    style="margin-right: 10px;" readonly="true"/>
        <div class="cleaner"><!--Do not remove--></div>
        <s:label for="newPasswordConfirm"><fmt:message key="newPasswordConfirm.label"/> (*):</s:label>
        <s:password id="newPasswordConfirm" name="newPasswordConfirm" class="largeInput"
                    style="margin-right: 10px;" readonly="true"/>
        <div class="cleaner"><!--Do not remove--></div>
        <div class="formButtons" style="padding-right:100px;">
            <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
            <s:submit name="update" class="button"><fmt:message key="common.submit"/></s:submit>
        </div>
    </s:form>
</c:if>
</body>

</html>