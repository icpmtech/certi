<%@ include file="../../../includes/taglibs.jsp" %>

<html>
    <head>
        <title><fmt:message key="statistics.title" /> &gt; <fmt:message key="statistics.filter.title" /></title>
        <link rel="stylesheet" type="text/css"
            href="${pageContext.request.contextPath}/styles/jquery/jquery-ui-1.7.custom.css"/>
        <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
        <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/datepicker-${pageContext.request.locale}.js"></script>
    </head>
    <body>
        <h1><fmt:message key="statistics.filter.title" /></h1>
        <h2 class="form"><span><fmt:message key="statistics.filter.title" /></span></h2>

        <div class="form">
            <s:errors/>
        </div>

        <s:form beanclass="com.criticalsoftware.certitools.presentation.action.legislation.StatisticsActionBean"
                    method="get" class="form" focus="list">
            <s:label for="initDate"><fmt:message key="statistics.filter.initDate"/> (*):</s:label>
            <s:text id="initDate" name="initDate" class="dateInput" readonly="true" style="margin-right:10px;"/>
            <div class="cleaner"><!----></div>
            <s:label for="endDate"><fmt:message key="statistics.filter.endDate"/> (*):</s:label>
            <s:text id="endDate" name="endDate" class="dateInput" readonly="true" style="margin-right:10px;"/>

            <div class="formButtons">
                <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                <s:submit name="list" class="button"><fmt:message key="common.login"/></s:submit>
            </div>
        </s:form>
    <script type="text/javascript">

        $(function() {
            $('#initDate').datepicker({
                buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
                dateFormat: '<c:out value="${applicationScope.configuration.datePatternCalendar}"/>',
                buttonText: '<fmt:message key="statistics.filter.initDate"/>'});

            $('#endDate').datepicker({
                buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
                dateFormat: '<c:out value="${applicationScope.configuration.datePatternCalendar}"/>',
                buttonText: '<fmt:message key="statistics.filter.endDate"/>'});
        });
    </script>
    </body>
</html>

