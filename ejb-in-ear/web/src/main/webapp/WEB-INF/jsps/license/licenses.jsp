<%@ include file="../../../includes/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Window-target" content="_top"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />

    <title>
        <fmt:message key="application.title"/> &gt; <fmt:message key="license.page.subtitle"/>
    </title>

    <!-- Application Icon-->
    <link rel="Shortcut Icon" href="${pageContext.request.contextPath}/favicon.ico"/>

    <!-- CSS Imports-->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/default-style.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/displaytag/displaytag.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery-ui-1.7.custom.css"/>

    <!-- JavaScript Imports-->
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/scripts.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery.corner.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/meu.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>

</head>
<body>
<div id="frame">

    <div id="main-header">
        <%@ include file="../../../includes/licenseHeader.jsp" %>
    </div>

    <div id="main">
        <div id="main-content">

            <h1><fmt:message key="license.page.subtitle"/></h1>

            <s:messages/>

            <div class="links">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.license.LicenseActionBean"
                        event="insertLicenseForm" class="operationAdd">
                    <fmt:message key="common.add"/>
                </s:link>
            </div>

            <display:table list="${requestScope.actionBean.licenses}" export="true" id="displaytag"
                           class="displaytag"
                           requestURI="/license/License.action" uid="license">

                <display:column titleKey="license.creationDate" property="creationDate" sortable="true"/>
                <display:column titleKey="license.startDate" property="startDate" sortable="true"/>
                <display:column titleKey="license.endDate" property="endDate" sortable="true"/>
                <display:column titleKey="license.company.name" property="company.name" sortable="true"/>
                <display:column titleKey="license.download" media="html" style="text-align:center;">
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.license.LicenseDownloadActionBean"
                            event="downloadFile">
                        <s:param name="licenseId" value="${pageScope.license.id}"/>
                        <img src="${pageContext.request.contextPath}/images/Artigo-Ajuda.png"
                             title="<fmt:message key="license.download"/>"
                             alt="<fmt:message key="license.download"/> "
                    </s:link>
                </display:column>
                <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
                <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
                <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
                <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
            </display:table>
        </div>
    </div>

    <div id="main-footer">
        <%@ include file="../../../includes/mainFooter.jsp" %>
    </div>

</div>
</body>
</html>


