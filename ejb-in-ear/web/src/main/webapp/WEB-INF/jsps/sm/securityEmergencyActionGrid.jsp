<%@ include file="../../../includes/taglibs.jsp" %>

<s:messages/>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean" focus=""
        class="searchBox"
        method="GET">
    <fieldset>
        <legend>
            <fmt:message key="common.search"/>
        </legend>
        <table>
            <tr>
                <td class="first">
                    <s:label for="filterYear"><fmt:message key="security.view.filter.year"/>: </s:label>
                    <s:select name="filterYear" style="width: 220px;" id="filterYear">
                        <s:options-collection collection="${requestScope.actionBean.emergencyYears}"/>
                    </s:select>

                </td>
                <td class="last">
                    <s:submit name="emergencyActionGrid" id="searchButton" class="searchButton"><fmt:message
                            key="common.search"/></s:submit>
                </td>
            </tr>
        </table>
    </fieldset>
    <s:hidden name="contractId" value="${requestScope.actionBean.contractId}"/>
</s:form>

<display:table list="${requestScope.actionBean.emergencyActionsAdapter}" export="true" id="displaytable"
               class="displaytag"
               uid="emergency"
               requestURI="/sm/SecurityEmergency.action">

    <display:column titleKey="security.emergency.code" sortProperty="code" sortable="true" media="html">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean"
                event="emergencyActionEdit">
            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
            <s:param name="emergencyId" value="${pageScope.emergency.id}"/>
            <c:out value="${pageScope.emergency.code}"/>
        </s:link>
    </display:column>

    <display:column property="code" titleKey="security.emergency.code" sortProperty="code" sortable="true"
                    media="csv excel xml pdf"/>

    <display:column property="origin" titleKey="security.emergency.origin" escapeXml="true"
                    sortable="true"/>

    <display:column titleKey="security.emergency.startdate" sortable="true"
                    sortProperty="startDate">
        <fmt:formatDate value="${pageScope.emergency.startDate}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column titleKey="security.emergency.documents" media="html">
        <ul>
            <c:if test="${pageScope.emergency.closed}">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean"
                            event="getEmergencyActionReportPdf" class="pdfDownloadIcon">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <s:param name="emergencyId" value="${pageScope.emergency.id}"/>
                        <c:out value="${pageScope.emergency.code}"/>.pdf
                    </s:link>
                </li>
                <c:if test="${pageScope.emergency.hasChatMessages}">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean"
                                event="getChatCsv" class="pdfDownloadIcon">
                            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                            <s:param name="emergencyId" value="${pageScope.emergency.id}"/>
                            <fmt:message key="security.csv.filename"/>
                        </s:link>
                    </li>
                </c:if>
            </c:if>
        </ul>
    </display:column>

    <display:column titleKey="security.emergency.documents" media="csv excel xml pdf">
        <c:if test="${pageScope.emergency.closed}">
            <c:out value="${pageScope.emergency.code}"/>.pdf
        </c:if>
    </display:column>

    <c:if test="${requestScope.actionBean.isUserExpert}">
        <display:column class="oneButtonColumnWidth" media="html">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean"
                    event="deleteEmergencyAction" class="confirmDelete">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="emergencyId" value="${pageScope.emergency.id}"/>

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
        attachConfirmDelete("<fmt:message key="security.emergency.confirmDelete"/>");
    });
</script>
