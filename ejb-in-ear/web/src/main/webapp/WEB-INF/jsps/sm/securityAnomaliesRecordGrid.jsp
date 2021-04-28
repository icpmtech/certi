<%@ include file="../../../includes/taglibs.jsp" %>
<%@ page import="com.criticalsoftware.certitools.entities.sm.enums.AnomalyType" %>

<s:messages/>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean" focus=""
        class="searchBox"
        method="GET">
    <fieldset>
        <legend>
            <fmt:message key="common.search"/>
        </legend>
        <table>
            <tr>
                <td class="first">
                    <s:label for="filterType"><fmt:message key="security.view.filter.type"/>: </s:label>
                    <s:select name="anomalyType" style="width: 220px;" id="filterType">
                        <s:option></s:option>
                        <s:option value="ANOMALY">
                            <fmt:message key="security.anomalyType.anomaly"/></s:option>
                        <s:option value="OCCURRENCE">
                            <fmt:message key="security.anomalyType.occurrence"/></s:option>
                    </s:select>

                    <s:label for="filterYear"><fmt:message key="security.view.filter.year"/>: </s:label>
                    <s:select name="filterYear" style="width: 220px;" id="filterYear">
                        <s:options-collection collection="${requestScope.actionBean.anomalyYears}"/>
                    </s:select>

                    <s:label for="filterStatus"><fmt:message key="security.view.filter.status"/>: </s:label>
                    <s:select name="isOpen" style="width: 220px;" id="filterStatus">
                        <s:option></s:option>
                        <s:option value="true"><fmt:message key="security.state.open"/></s:option>
                        <s:option value="false"><fmt:message key="security.state.closed"/></s:option>
                    </s:select>
                </td>
                <td class="last">
                    <s:submit name="anomaliesRecordGrid" id="searchButton" class="searchButton"><fmt:message
                            key="common.search"/></s:submit>
                </td>
            </tr>
        </table>
    </fieldset>
    <s:hidden name="contractId" value="${requestScope.actionBean.contractId}"/>
</s:form>

<display:table list="${requestScope.actionBean.anomalyAdapter}" export="true" id="displaytable" class="displaytag"
               uid="anomalies"
               requestURI="/sm/SecurityAnomaliesRecord.action">

    <display:column titleKey="security.anomaly.view.type" sortProperty="anomalyType" escapeXml="true" sortable="true">
        <c:choose>
            <c:when test="${pageScope.anomalies.anomalyType == 'ANOMALY'}">
                <fmt:message key="security.anomalyType.anomaly"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="security.anomalyType.occurrence"/>
            </c:otherwise>
        </c:choose>
    </display:column>

    <display:column titleKey="security.anomaly.view.code" sortProperty="code" sortable="true" media="html">
        <c:choose>
            <c:when test="${pageScope.anomalies.anomalyType == 'ANOMALY'}">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
                        event="anomaliesRecordEdit">
                    <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                    <s:param name="anomalyId" value="${pageScope.anomalies.id}"/>
                    <c:out value="${pageScope.anomalies.code}"/>
                </s:link>
            </c:when>
            <c:otherwise>
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
                        event="occurrencesRecordEdit">
                    <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                    <s:param name="anomalyId" value="${pageScope.anomalies.id}"/>
                    <c:out value="${pageScope.anomalies.code}"/>
                </s:link>
            </c:otherwise>
        </c:choose>
    </display:column>

    <display:column property="code" titleKey="security.anomaly.view.code" sortProperty="code" sortable="true"
                    media="csv excel xml pdf"/>

    <display:column titleKey="security.anomaly.view.name" sortable="true" sortProperty="name">
                    ${pageScope.anomalies.name}
    </display:column>

    <display:column titleKey="security.anomaly.view.datetime" sortable="true"
                    sortProperty="datetime">
        <fmt:formatDate value="${pageScope.anomalies.datetime}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column titleKey="security.anomaly.view.closeddate" sortable="true"
                    sortProperty="closedDate">
        <fmt:formatDate value="${pageScope.anomalies.closedDate}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column titleKey="security.anomaly.view.state" sortProperty="closed" escapeXml="true" sortable="true">
        <c:choose>
            <c:when test="${pageScope.anomalies.closed}">
                <fmt:message key="security.state.closed"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="security.state.open"/>
            </c:otherwise>
        </c:choose>
    </display:column>

    <display:column titleKey="security.anomaly.view.correctiveactions" media="html">
        <ul>
            <c:forEach items="${pageScope.anomalies.correctiveActions}" var="correctiveAction">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean"
                            event="actionsPlanningEdit">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <s:param name="correctiveActionId" value="${correctiveAction.id}"/>
                        <c:out value="${correctiveAction.code}"/>
                    </s:link>
                </li>
            </c:forEach>
        </ul>
    </display:column>

    <display:column titleKey="security.anomaly.view.correctiveactions" media="csv excel xml pdf">
        <c:forEach items="${pageScope.anomalies.correctiveActions}" var="correctiveAction" varStatus="pos"><c:if
                test="${pos.index != 0}">, </c:if><c:out value="${correctiveAction.code}"/></c:forEach>
    </display:column>

    <display:column titleKey="security.anomaly.view.documents" media="html">
        <ul>
            <c:forEach items="${pageScope.anomalies.documents}" var="document">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
                            event="getDocument" class="download-file">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <s:param name="documentId" value="${document.id}"/>
                        <c:out value="${document.displayName}"/>
                    </s:link>
                </li>
            </c:forEach>
            <c:if test="${pageScope.anomalies.closed}">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
                            event="getAnomalyReportPdf" class="pdfDownloadIcon">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <s:param name="anomalyId" value="${pageScope.anomalies.id}"/>
                        <c:out value="${pageScope.anomalies.code}"/>.pdf
                    </s:link>
                </li>
                <c:if test="${pageScope.anomalies.hasChatMessages}">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
                                event="getChatPdf" class="pdfDownloadIcon">
                            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                            <s:param name="anomalyId" value="${pageScope.anomalies.id}"/>
                            <fmt:message key="security.pdf.filename"/>
                        </s:link>
                    </li>
                </c:if>
                <c:forEach items="${pageScope.anomalies.correctiveActions}" var="correctiveAction">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
                                event="getCorrectiveActionReportPdf" class="pdfDownloadIcon">
                            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                            <s:param name="correctiveActionId" value="${correctiveAction.id}"/>
                            <c:out value="${correctiveAction.code}"/>.pdf
                        </s:link>
                    </li>
                </c:forEach>
            </c:if>
        </ul>
    </display:column>

    <display:column titleKey="security.anomaly.view.documents" media="csv excel xml pdf">
        <c:forEach items="${pageScope.anomalies.documents}" var="document" varStatus="pos"><c:if
                test="${pos.index != 0}">, </c:if><c:out value="${document.displayName}"/></c:forEach><c:if
            test="${pageScope.anomalies.closed}"><c:if
            test="${fn:length(pageScope.anomalies.documents) > 0}">, </c:if><c:out
            value="${pageScope.anomalies.code}"/>.pdf<c:if test="${pageScope.anomalies.hasChatMessages}">, <fmt:message
            key="security.pdf.filename"/></c:if><c:forEach
            items="${pageScope.anomalies.correctiveActions}" var="correctiveAction">, <c:out
            value="${correctiveAction.code}"/>.pdf</c:forEach></c:if>
    </display:column>

    <c:if test="${requestScope.actionBean.isUserExpert}">
        <display:column class="oneButtonColumnWidth" media="html">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
                    event="deleteAnomaly" class="confirmDelete">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="anomalyId" value="${pageScope.anomalies.id}"/>

                <img src="${pageContext.request.contextPath}/images/Eliminar.png"
                     title="<fmt:message key="common.delete"/>"
                     alt="<fmt:message key="common.delete"/>"/></s:link>
        </display:column>
    </c:if>

    <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
    <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
    <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
    <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
</display:table>

<script type="text/javascript">
    $(document).ready(function () {
        attachConfirmDelete("<fmt:message key="security.anomaly.confirmDelete"/>");
    });
</script>