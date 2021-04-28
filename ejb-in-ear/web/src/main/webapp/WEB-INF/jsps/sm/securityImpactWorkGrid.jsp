<%@ include file="../../../includes/taglibs.jsp" %>
<%@ page import="com.criticalsoftware.certitools.entities.sm.enums.WorkType" %>

<s:messages/>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean" focus=""
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
                    <s:select name="workType" style="width: 220px;" id="filterType">
                        <s:option></s:option>
                        <s:option value="MODIFICATION">
                            <fmt:message key="security.workType.modification"/></s:option>
                        <s:option value="WORK_AUTHORIZATION">
                            <fmt:message key="security.workType.authorization"/></s:option>
                    </s:select>

                    <s:label for="filterYear"><fmt:message key="security.view.filter.year"/>: </s:label>
                    <s:select name="filterYear" style="width: 220px;" id="filterYear">
                        <s:options-collection collection="${requestScope.actionBean.impactWorkYears}"/>
                    </s:select>

                    <s:label for="filterStatus"><fmt:message key="security.view.filter.status"/>: </s:label>
                    <s:select name="isOpen" style="width: 220px;" id="filterStatus">
                        <s:option></s:option>
                        <s:option value="true"><fmt:message key="security.state.open"/></s:option>
                        <s:option value="false"><fmt:message key="security.state.closed"/></s:option>
                    </s:select>
                </td>
                <td class="last">
                    <s:submit name="impactWorkGrid" id="searchButton" class="searchButton"><fmt:message
                            key="common.search"/></s:submit>
                </td>
            </tr>
        </table>
    </fieldset>
    <s:hidden name="contractId" value="${requestScope.actionBean.contractId}"/>
</s:form>

<display:table list="${requestScope.actionBean.impactWorkAdapter}" export="true" id="displaytable" class="displaytag"
               uid="securityImpactWork"
               requestURI="/sm/SecurityImpactWork.action">

    <display:column titleKey="security.impact.work.view.type" sortProperty="workType" escapeXml="true" sortable="true">
        <c:choose>
            <c:when test="${pageScope.securityImpactWork.workType == 'MODIFICATION'}">
                <fmt:message key="security.workType.modification"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="security.workType.authorization"/>
            </c:otherwise>
        </c:choose>
    </display:column>

    <display:column titleKey="security.impact.work.view.code" sortProperty="code" sortable="true" media="html">
        <c:choose>
            <c:when test="${pageScope.securityImpactWork.workType == 'MODIFICATION'}">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
                        event="modificationsChangesEdit">
                    <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                    <s:param name="impactWorkId" value="${pageScope.securityImpactWork.id}"/>
                    <c:out value="${pageScope.securityImpactWork.code}"/>
                </s:link>
            </c:when>
            <c:otherwise>
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
                        event="authorizationEdit">
                    <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                    <s:param name="impactWorkId" value="${pageScope.securityImpactWork.id}"/>
                    <c:out value="${pageScope.securityImpactWork.code}"/>
                </s:link>
            </c:otherwise>
        </c:choose>
    </display:column>

    <display:column property="code" titleKey="security.impact.work.view.code" sortProperty="code" sortable="true"
                    media="csv excel xml pdf"/>

    <display:column titleKey="security.impact.work.view.name" sortable="true" sortProperty="name">
        ${pageScope.securityImpactWork.name}
    </display:column>

    <display:column titleKey="security.impact.work.view.datetime" sortable="true"
                    sortProperty="startDate">
        <fmt:formatDate value="${pageScope.securityImpactWork.startDate}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column titleKey="security.impact.work.view.closeddate" sortable="true"
                    sortProperty="closedDate">
        <fmt:formatDate value="${pageScope.securityImpactWork.closedDate}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column titleKey="security.impact.work.view.state" sortProperty="closed" escapeXml="true" sortable="true">
        <c:choose>
            <c:when test="${pageScope.securityImpactWork.closed}">
                <fmt:message key="security.state.closed"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="security.state.open"/>
            </c:otherwise>
        </c:choose>
    </display:column>

    <display:column titleKey="security.impact.work.view.correctiveactions" media="html">
        <ul>
            <c:forEach items="${pageScope.securityImpactWork.correctiveActions}" var="correctiveAction">
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

    <display:column titleKey="security.impact.work.view.correctiveactions" media="csv excel xml pdf">
        <c:forEach items="${pageScope.securityImpactWork.correctiveActions}" var="correctiveAction"
                   varStatus="pos"><c:if
                test="${pos.index != 0}">, </c:if><c:out value="${correctiveAction.code}"/></c:forEach>
    </display:column>

    <display:column titleKey="security.impact.work.view.documents" media="html">
        <ul>
            <c:forEach items="${pageScope.securityImpactWork.documents}" var="document">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
                            event="getDocument" class="download-file">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <s:param name="documentId" value="${document.id}"/>
                        <c:out value="${document.displayName}"/>
                    </s:link>
                </li>
            </c:forEach>
            <c:if test="${pageScope.securityImpactWork.closed}">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
                            event="getImpactWorkReportPdf" class="pdfDownloadIcon">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <s:param name="impactWorkId" value="${pageScope.securityImpactWork.id}"/>
                        <c:out value="${pageScope.securityImpactWork.code}"/>.pdf
                    </s:link>
                </li>
                <c:if test="${pageScope.securityImpactWork.hasChatMessages}">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
                                event="getChatPdf" class="pdfDownloadIcon">
                            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                            <s:param name="impactWorkId" value="${pageScope.securityImpactWork.id}"/>
                            <fmt:message key="security.pdf.filename"/>
                        </s:link>
                    </li>
                </c:if>
                <c:forEach items="${pageScope.securityImpactWork.correctiveActions}" var="correctiveAction">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
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

    <display:column titleKey="security.impact.work.view.documents" media="csv excel xml pdf">
        <c:forEach items="${pageScope.securityImpactWork.documents}" var="document" varStatus="pos"><c:if
                test="${pos.index != 0}">, </c:if><c:out value="${document.displayName}"/></c:forEach><c:if
            test="${pageScope.securityImpactWork.closed}"><c:if
            test="${fn:length(pageScope.securityImpactWork.documents) > 0}">, </c:if><c:out
            value="${pageScope.securityImpactWork.code}"/>.pdf<c:if
            test="${pageScope.securityImpactWork.hasChatMessages}">, <fmt:message
            key="security.pdf.filename"/></c:if><c:forEach
            items="${pageScope.securityImpactWork.correctiveActions}" var="correctiveAction">, <c:out
            value="${correctiveAction.code}"/>.pdf</c:forEach></c:if>
    </display:column>

    <c:if test="${requestScope.actionBean.isUserExpert}">
        <display:column class="oneButtonColumnWidth" media="html">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
                    event="deleteWork" class="confirmDelete">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="impactWorkId" value="${pageScope.securityImpactWork.id}"/>

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
        attachConfirmDelete("<fmt:message key="security.impact.work.confirmDelete"/>");
    });
</script>