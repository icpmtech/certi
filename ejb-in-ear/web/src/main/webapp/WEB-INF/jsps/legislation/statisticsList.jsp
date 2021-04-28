<%@ include file="../../../includes/taglibs.jsp" %>

<html>
<head>
    <title><fmt:message key="statistics.title"/> &gt; <fmt:message key="statistics.subtitle"/></title>
</head>
<body>
<h1>
    <fmt:message key="statistics.compose.subtitle">
        <fmt:param>
            <fmt:formatDate value="${requestScope.actionBean.initDate}"
                            pattern="${applicationScope.configuration.datePattern}"/>
        </fmt:param>
        <fmt:param>
            <fmt:formatDate value="${requestScope.actionBean.endDate}"
                            pattern="${applicationScope.configuration.datePattern}"/>
        </fmt:param>
    </fmt:message>
</h1>

<display:table list="${requestScope.actionBean.visualizations}" export="true" id="vizuAndDown"
               class="displaytag"
               uid="statisticsA"
               requestURI="/legislation/Statistics.action">

    <display:column titleKey="statistics.legal.document"
                    property="text"
                    sortable="true"
                    paramProperty="documentId"
                    paramId="legalDocument.id"
                    url="/legislation/Legislation.action?viewLegislation="/>
    <display:column titleKey="statistics.report.type.visualization"
                    property="countVisualizations"
                    sortable="true"
                    class="number"/>
    <display:column titleKey="statistics.report.type.download"
                    property="countDownloads"
                    sortable="true"
                    class="number"/>

    <display:setProperty name="export.excel.filename" value="${requestScope.exportXLSLegalDocument}"/>
    <display:setProperty name="export.xml.filename" value="${requestScope.exportXMLLegalDocument}"/>
    <display:setProperty name="export.pdf.filename" value="${requestScope.exportPDFLegalDocument}"/>
    <display:setProperty name="export.csv.filename" value="${requestScope.exportCSVLegalDocument}"/>
</display:table>


<display:table list="${requestScope.actionBean.searchTerms}" export="true" id="searchTerm"
               class="displaytag"
               uid="statisticsB"
               requestURI="/legislation/Statistics.action">
    <display:column titleKey="statistics.search.term"
                    property="text"
                    sortable="true"/>
    <display:column titleKey="statistics.report.type.search.term"
                    property="countSearchTerms"
                    sortable="true"
                    class="number"/>

    <display:setProperty name="export.excel.filename" value="${requestScope.exportXLSSearch}"/>
    <display:setProperty name="export.xml.filename" value="${requestScope.exportXMLSearch}"/>
    <display:setProperty name="export.pdf.filename" value="${requestScope.exportPDFSearch}"/>
    <display:setProperty name="export.csv.filename" value="${requestScope.exportCSVSearch}"/>
</display:table>

</body>
</html>
