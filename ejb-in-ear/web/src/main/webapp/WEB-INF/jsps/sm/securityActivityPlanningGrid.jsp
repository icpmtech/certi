<%@ include file="../../../includes/taglibs.jsp" %>

<s:messages/>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean" focus="" class="searchBox"
        method="GET">
    <fieldset>
        <legend>
            <fmt:message key="common.search"/>
        </legend>
        <table>
            <tr>
                <td class="first">
                    <s:label for="filterType"><fmt:message key="security.view.filter.type"/>: </s:label>
                    <s:select name="activityTypeId" style="width: 220px;" id="filterType">
                        <s:options-collection collection="${requestScope.actionBean.activityTypes}" label="name"
                                              value="id"/>
                    </s:select>

                    <s:label for="filterYear"><fmt:message key="security.view.filter.year"/>: </s:label>
                    <s:select name="filterYear" style="width: 220px;" id="filterYear">
                        <s:options-collection collection="${requestScope.actionBean.years}"/>
                    </s:select>

                    <s:label for="filterStatus"><fmt:message key="security.view.filter.status"/>: </s:label>
                    <s:select name="isOpen" style="width: 220px;" id="filterStatus">
                        <s:option></s:option>
                        <s:option value="true"><fmt:message key="security.state.open"/></s:option>
                        <s:option value="false"><fmt:message key="security.state.closed"/></s:option>
                    </s:select>
                </td>
                <td class="last">
                    <s:submit name="activityPlanningGrid" id="searchButton" class="searchButton"><fmt:message
                            key="common.search"/></s:submit>
                </td>
            </tr>
        </table>
    </fieldset>
    <s:hidden name="contractId" value="${requestScope.actionBean.contractId}"/>
</s:form>

<display:table list="${requestScope.actionBean.activitiesAdapter}" export="true" id="displaytable" class="displaytag"
               uid="activities"
               requestURI="/sm/Security.action">

    <display:column property="activityType.name" titleKey="security.activity.view.activitytype" escapeXml="true"
                    sortable="true"/>

    <display:column titleKey="security.activity.view.name" sortProperty="name" sortable="true" media="html">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                event="activityPlanningEdit">
            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
            <s:param name="activityId" value="${pageScope.activities.id}"/>
            <c:out value="${pageScope.activities.name}"/>
        </s:link>
    </display:column>

    <display:column property="name" titleKey="security.activity.view.name" sortProperty="name" sortable="true"
                    media="csv excel xml pdf"/>

    <display:column property="internalResponsible" titleKey="security.activity.view.internalresponsible"
                    escapeXml="true" sortable="true"/>

    <display:column titleKey="security.activity.view.datescheduled" escapeXml="true" sortable="true"
                    sortProperty="dateScheduled">
        <fmt:formatDate value="${pageScope.activities.dateScheduled}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column titleKey="security.activity.view.closeddate" escapeXml="true" sortable="true"
                    sortProperty="closedDate">
        <fmt:formatDate value="${pageScope.activities.closedDate}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column titleKey="security.activity.view.state" sortProperty="closed" escapeXml="true" sortable="true">
        <c:choose>
            <c:when test="${pageScope.activities.closed}">
                <fmt:message key="security.state.closed"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="security.state.open"/>
            </c:otherwise>
        </c:choose>
    </display:column>

    <display:column titleKey="security.activity.view.correctiveactions" media="html">
        <ul>
            <c:forEach items="${pageScope.activities.correctiveActions}" var="correctiveAction">
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

    <display:column titleKey="security.activity.view.correctiveactions" media="csv excel xml pdf">
        <c:forEach items="${pageScope.activities.correctiveActions}" var="correctiveAction" varStatus="pos"><c:if
                test="${pos.index != 0}">, </c:if><c:out value="${correctiveAction.code}"/></c:forEach>
    </display:column>

    <display:column titleKey="security.activity.view.documents" media="html">
        <ul>
            <c:forEach items="${pageScope.activities.documents}" var="document">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                            event="getDocument" class="download-file">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <s:param name="documentId" value="${document.id}"/>
                        <c:out value="${document.displayName}"/>
                    </s:link>
                </li>
            </c:forEach>
            <c:if test="${pageScope.activities.closed}">
                <c:if test="${pageScope.activities.hasChatMessages}">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                                event="getChatPdf" class="pdfDownloadIcon">
                            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                            <s:param name="activityId" value="${pageScope.activities.id}"/>
                            <fmt:message key="security.pdf.filename"/>
                        </s:link>
                    </li>
                </c:if>
                <c:forEach items="${pageScope.activities.correctiveActions}" var="correctiveAction">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean"
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

    <display:column titleKey="security.activity.view.documents" media="csv excel xml pdf">
        <c:forEach items="${pageScope.activities.documents}" var="document" varStatus="pos"><c:if
                test="${pos.index != 0}">, </c:if><c:out value="${document.displayName}"/></c:forEach><c:if
            test="${pageScope.activities.closed}"><c:if test="${pageScope.activities.hasChatMessages}"><c:if
            test="${fn:length(pageScope.activities.documents) > 0}">, </c:if><fmt:message
            key="security.pdf.filename"/></c:if><c:forEach
            items="${pageScope.activities.correctiveActions}" var="correctiveAction">, <c:out
            value="${correctiveAction.code}"/>.pdf</c:forEach></c:if>
    </display:column>

    <c:if test="${requestScope.actionBean.isUserExpert}">
        <display:column class="oneButtonColumnWidth" media="html">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                    event="deleteDocument" class="confirmDelete">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="activityId" value="${pageScope.activities.id}"/>

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
        attachConfirmDelete("<fmt:message key="security.activity.confirmDelete"/>");
    });
</script>