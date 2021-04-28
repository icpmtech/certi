<%@ include file="../../../includes/taglibs.jsp" %>

<s:messages/>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean" focus=""
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
                        <s:options-collection collection="${requestScope.actionBean.actionYears}"/>
                    </s:select>

                    <s:label for="filterStatus"><fmt:message key="security.view.filter.status"/>: </s:label>
                    <s:select name="isOpen" style="width: 220px;" id="filterStatus">
                        <s:option></s:option>
                        <s:option value="true"><fmt:message key="security.state.open"/></s:option>
                        <s:option value="false"><fmt:message key="security.state.closed"/></s:option>
                    </s:select>
                </td>
                <td class="last">
                    <s:submit name="actionsPlanningGrid" id="searchButton" class="searchButton"><fmt:message
                            key="common.search"/></s:submit>
                </td>
            </tr>
        </table>
    </fieldset>
    <s:hidden name="contractId" value="${requestScope.actionBean.contractId}"/>
</s:form>

<display:table list="${requestScope.actionBean.actionsAdapter}" export="true" id="displaytable" class="displaytag"
               uid="actions"
               requestURI="/sm/SecurityActionsPlanning.action">

    <display:column titleKey="security.actions.view.code" sortProperty="code" sortable="true" media="html">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean"
                event="actionsPlanningEdit">
            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
            <s:param name="correctiveActionId" value="${pageScope.actions.id}"/>
            <c:out value="${pageScope.actions.code}"/>
        </s:link>
    </display:column>

    <display:column property="code" titleKey="security.actions.view.code" sortProperty="code" sortable="true"
                    media="csv excel xml pdf"/>

    <display:column titleKey="security.actions.view.name" sortable="true" sortProperty="name">
        ${pageScope.actions.name}
    </display:column>

    <display:column titleKey="security.actions.view.startdate" sortable="true"
                    sortProperty="startDate">
        <fmt:formatDate value="${pageScope.actions.startDate}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column titleKey="security.actions.view.closeddate" sortable="true"
                    sortProperty="closedDate">
        <fmt:formatDate value="${pageScope.actions.closedDate}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column titleKey="security.actions.view.state" sortProperty="closed" escapeXml="true" sortable="true">
        <c:choose>
            <c:when test="${pageScope.actions.closed}">
                <fmt:message key="security.state.closed"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="security.state.open"/>
            </c:otherwise>
        </c:choose>
    </display:column>

    <display:column property="duration" titleKey="security.actions.view.duration"
                    escapeXml="true" sortable="true"/>

    <display:column property="executionResponsible" titleKey="security.actions.view.executionresponsible"
                    escapeXml="true" sortable="true"/>

    <display:column titleKey="security.actions.view.documents" media="html">
        <ul>
            <c:forEach items="${pageScope.actions.documents}" var="document">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                            event="getDocument" class="download-file">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <s:param name="documentId" value="${document.id}"/>
                        <c:out value="${document.displayName}"/>
                    </s:link>
                </li>
            </c:forEach>
            <c:if test="${pageScope.actions.closed}">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean"
                            event="getCorrectiveActionReportPdf" class="pdfDownloadIcon">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <s:param name="correctiveActionId" value="${pageScope.actions.id}"/>
                        <c:out value="${pageScope.actions.code}"/>.pdf
                    </s:link>
                </li>
                <c:if test="${pageScope.actions.hasChatMessages}">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean"
                                event="getChatPdf" class="pdfDownloadIcon">
                            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                            <s:param name="correctiveActionId" value="${pageScope.actions.id}"/>
                            <fmt:message key="security.pdf.filename"/>
                        </s:link>
                    </li>
                </c:if>
            </c:if>
        </ul>
    </display:column>

    <display:column titleKey="security.actions.view.documents" media="csv excel xml pdf">
        <c:forEach items="${pageScope.actions.documents}" var="document" varStatus="pos"><c:if
                test="${pos.index != 0}">, </c:if><c:out value="${document.displayName}"/></c:forEach><c:if
            test="${pageScope.actions.closed}"><c:if
            test="${fn:length(pageScope.actions.documents) > 0}">, </c:if><c:out
            value="${pageScope.actions.code}"/>.pdf<c:if
            test="${pageScope.actions.hasChatMessages}">, <fmt:message
            key="security.pdf.filename"/></c:if></c:if>
    </display:column>

    <c:if test="${requestScope.actionBean.isUserExpert}">
        <display:column class="oneButtonColumnWidth" media="html">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean"
                    event="deleteAction" class="confirmDelete">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="correctiveActionId" value="${pageScope.actions.id}"/>

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
        attachConfirmDelete("<fmt:message key="security.actions.confirmDelete"/>");
    });
</script>