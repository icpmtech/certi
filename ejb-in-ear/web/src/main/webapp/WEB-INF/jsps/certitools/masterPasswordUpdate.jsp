<%@ include file="../../../includes/taglibs.jsp" %>

<html>
<head>
    <title><fmt:message key="masterpassword.edit"/></title>
</head>
<body>
<h1><fmt:message key="configuration.title"/></h1>
<h2 class="formBig"><span><fmt:message key="masterpassword.edit"/></span></h2>

<div class="form">
    <s:errors/>
</div>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.MasterPasswordActionBean"
        method="post" class="form formBig" focus="">
        <s:label for="masterPassword"><fmt:message key="masterpassword.password"/> (*):</s:label>
        <s:password id="masterPassword" name="masterPassword" class="largeInput"/>

    <div class="formButtons">
        <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
        <s:submit name="update" class="button"><fmt:message key="common.submit"/></s:submit>
        <s:submit name="cancel" class="button"><fmt:message key="common.cancel"/></s:submit>
    </div>
</s:form>
</body>
</html>