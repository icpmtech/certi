<%@ include file="../../../includes/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Window-target" content="_top"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />

    <title>
        <fmt:message key="application.title"/> &gt; <fmt:message key="license.page.subtitle"/> &gt; <fmt:message
            key="common.add"/>
    </title>

    <!-- Application Icon-->
    <link rel="Shortcut Icon" href="${pageContext.request.contextPath}/favicon.ico"/>

    <!-- CSS Imports-->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/default-style.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery-ui-1.7.custom.css"/>

    <!-- JavaScript Imports-->
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/scripts.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery.corner.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/datepicker-${pageContext.request.locale}.js"></script>

</head>
<body>
<div id="frame">

    <div id="main-header">
        <%@ include file="../../../includes/licenseHeader.jsp" %>
    </div>

    <div id="main">
        <div id="main-content">

            <h1><fmt:message key="license.page.subtitle"/></h1>

            <h2 class="form"><span><fmt:message key="license.page.add"/></span></h2>

            <s:form beanclass="com.criticalsoftware.certitools.presentation.action.license.LicenseActionBean"
                    class="form" focus="">

                <s:errors/>

                <s:label for="companyId"><fmt:message key="license.company.name"/> (*):</s:label>
                <s:select name="license.company.id" id="companyId" class="selectInput">
                    <s:options-collection collection="${requestScope.actionBean.companyList}" label="name" value="id"/>
                </s:select>

                <div class="cleaner"><!-- --></div>

                <s:label for="startDate"><fmt:message key="license.startDate"/> (*):</s:label>
                <s:text id="startDate" name="license.startDate" class="dateInput" readonly="true"/>

                <div class="cleaner"><!-- --></div>

                <s:label for="endDate"><fmt:message key="license.endDate"/> (*):</s:label>
                <s:text id="endDate" name="license.endDate" class="dateInput" readonly="true"/>

                <div class="cleaner"><!-- --></div>

                <div class="formButtons">
                    <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                    <s:submit name="insertLicense" class="button"><fmt:message key="common.submit"/></s:submit>
                    <s:submit name="cancel" class="button"><fmt:message key="common.cancel"/></s:submit>
                </div>

            </s:form>
        </div>
    </div>

    <div id="main-footer">
        <%@ include file="../../../includes/mainFooter.jsp" %>
    </div>

</div>
</body>
<script type="text/javascript">
    $(function() {
        $('#startDate').datepicker({
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="license.startDate"/>'});
    });

    $(function() {
        $('#endDate').datepicker({
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="license.endDate"/>'});
    });

</script>
</html>


