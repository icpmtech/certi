<%@ include file="../../../includes/taglibs.jsp" %>

<s:messages/>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean" focus=""
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
                    <s:select name="maintenanceTypeId" style="width: 220px;" id="filterType">
                        <s:options-collection collection="${requestScope.actionBean.maintenanceTypes}" label="name"
                                              value="id"/>
                    </s:select>

                    <s:label for="filterYear"><fmt:message key="security.view.filter.year"/>: </s:label>
                    <s:select name="filterYear" style="width: 220px;" id="filterYear">
                        <s:options-collection collection="${requestScope.actionBean.maintenanceYears}"/>
                    </s:select>

                    <s:label for="filterStatus"><fmt:message key="security.view.filter.status"/>: </s:label>
                    <s:select name="isOpen" style="width: 220px;" id="filterStatus">
                        <s:option></s:option>
                        <s:option value="true"><fmt:message key="security.state.open"/></s:option>
                        <s:option value="false"><fmt:message key="security.state.closed"/></s:option>
                    </s:select>
                </td>
                <td class="last">
                    <s:submit name="maintenanceGrid" id="searchButton" class="searchButton"><fmt:message
                            key="common.search"/></s:submit>
                </td>
            </tr>
        </table>
    </fieldset>
    <s:hidden name="contractId" value="${requestScope.actionBean.contractId}"/>
</s:form>

<display:table list="${requestScope.actionBean.maintenanceAdapter}" export="true" id="displaytable"
               class="displaytag securityMaintenancesList" uid="maintenances"
               requestURI="/sm/SecurityMaintenance.action">

    <display:column property="maintenanceType.name" titleKey="security.maintenances.view.type" escapeXml="true"
                    sortable="true"/>

    <display:column titleKey="security.maintenances.view.code" sortProperty="code" sortable="true" media="html">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean"
                event="maintenanceEdit">
            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
            <s:param name="maintenanceId" value="${pageScope.maintenances.id}"/>
            <c:out value="${pageScope.maintenances.code}"/>
        </s:link>
    </display:column>

    <display:column property="code" titleKey="security.maintenances.view.code" sortProperty="code" sortable="true"
                    media="csv excel xml pdf"/>

    <display:column titleKey="security.maintenances.view.equipment" media="html" sortProperty="equipment.name"
                    sortable="true">
        <c:choose>
            <c:when test="${pageScope.maintenances.equipment.document != null
                            && pageScope.maintenances.maintenanceType.id == 2}">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean"
                        event="getEquipmentDocument" class="download-equipment">
                    <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                    <s:param name="equipmentId" value="${pageScope.maintenances.equipment.id}"/>
                    <c:out value="${pageScope.maintenances.equipment.name}"/>
                </s:link>
            </c:when>
            <c:otherwise>
                <c:out value="${pageScope.maintenances.equipment.name}"/>
            </c:otherwise>
        </c:choose>
    </display:column>

    <display:column property="equipment.name" titleKey="security.maintenances.view.equipment" escapeXml="true"
                    sortable="true" media="csv excel xml pdf"/>

    <display:column property="internalResponsible" titleKey="security.maintenances.view.internalresponsible"
                    escapeXml="true" sortable="true"/>

    <display:column titleKey="security.maintenances.view.datescheduled" sortable="true"
                    sortProperty="dateScheduled">
        <fmt:formatDate value="${pageScope.maintenances.dateScheduled}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column titleKey="security.maintenances.view.closeddate" sortable="true"
                    sortProperty="closedDate">
        <fmt:formatDate value="${pageScope.maintenances.closedDate}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column titleKey="security.maintenances.view.state" sortProperty="closed" escapeXml="true" sortable="true">
        <c:choose>
            <c:when test="${pageScope.maintenances.closed}">
                <fmt:message key="security.state.closed"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="security.state.open"/>
            </c:otherwise>
        </c:choose>
    </display:column>

    <display:column titleKey="security.maintenances.view.correctiveactions" media="html">
        <ul>
            <c:forEach items="${pageScope.maintenances.correctiveActions}" var="correctiveAction">
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

    <display:column titleKey="security.maintenances.view.correctiveactions" media="csv excel xml pdf">
        <c:forEach items="${pageScope.maintenances.correctiveActions}" var="correctiveAction" varStatus="pos"><c:if
                test="${pos.index != 0}">, </c:if><c:out value="${correctiveAction.code}"/></c:forEach>
    </display:column>

    <display:column titleKey="security.maintenances.view.documents" media="html">
        <ul>
            <c:forEach items="${pageScope.maintenances.documents}" var="document">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean"
                            event="getMaintenanceDocument" class="download-file">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <s:param name="documentId" value="${document.id}"/>
                        <c:out value="${document.displayName}"/>
                    </s:link>
                </li>
            </c:forEach>
            <c:if test="${pageScope.maintenances.closed}">
                <c:if test="${pageScope.maintenances.hasChatMessages}">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean"
                                event="getChatPdf" class="pdfDownloadIcon">
                            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                            <s:param name="maintenanceId" value="${pageScope.maintenances.id}"/>
                            <fmt:message key="security.pdf.filename"/>
                        </s:link>
                    </li>
                </c:if>
                <c:forEach items="${pageScope.maintenances.correctiveActions}" var="correctiveAction">
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

    <display:column titleKey="security.maintenances.view.documents" media="csv excel xml pdf">
        <c:forEach items="${pageScope.maintenances.documents}" var="document" varStatus="pos"><c:if
                test="${pos.index != 0}">, </c:if><c:out value="${document.displayName}"/></c:forEach><c:if
            test="${pageScope.maintenances.closed}"><c:if test="${pageScope.maintenances.hasChatMessages}"><c:if
            test="${fn:length(pageScope.maintenances.documents) > 0}">, </c:if><fmt:message
            key="security.pdf.filename"/></c:if><c:forEach
            items="${pageScope.maintenances.correctiveActions}" var="correctiveAction">, <c:out
            value="${correctiveAction.code}"/>.pdf</c:forEach></c:if>
    </display:column>

    <c:if test="${requestScope.actionBean.isUserExpert}">
        <display:column class="oneButtonColumnWidth" media="html">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean"
                    event="deleteMaintenance" class="confirmDelete">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="maintenanceId" value="${pageScope.maintenances.id}"/>

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
        attachConfirmDelete("<fmt:message key="security.maintenances.confirmDelete"/>");
    });

    $(".download-equipment").mouseover(function () {
        $(this).attr('title', '<fmt:message key="security.maintenances.equipments.download"/>');
    });

</script>