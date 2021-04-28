<%@ include file="../../../includes/taglibs.jsp" %>

<html>
    <head>
        <title><fmt:message key="configuration.title" /> &gt; <fmt:message key="configuration.update.subtitle"/></title>
    </head>
    <body>
        <h1><fmt:message key="configuration.title"/></h1>
        <h2 class="formBig"><span><fmt:message key="configuration.update.subtitle"/></span></h2>

        <div class="form">
            <s:errors/>
        </div>

        <s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.ConfigurationActionBean"
                    method="post" class="form formBig" focus="">
            <c:forEach items="${requestScope.actionBean.configurations}" var="conf" varStatus="status">
                <c:set var="index" value="${status.count - 1}"/>
                <s:hidden name="configurations[${pageScope.index}].key"/>
                <s:hidden name="configurations[${pageScope.index}].className"/>
                <s:label for="configurations[${pageScope.index}].value"><fmt:message key="configuration.${pageScope.conf.key}"/> (*):</s:label>
                <s:text id="configurations[${pageScope.index}].value" name="configurations[${pageScope.index}].value" class="largeInput"/>
            </c:forEach>

            <div class="formButtons">
                <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                <s:submit name="update" class="button"><fmt:message key="common.submit"/></s:submit>
                <s:submit name="cancel" class="button"><fmt:message key="common.cancel"/></s:submit>
            </div>
        </s:form>
    </body>
</html>

