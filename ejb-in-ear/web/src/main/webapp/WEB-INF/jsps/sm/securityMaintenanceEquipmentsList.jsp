<%@ include file="../../../includes/taglibs.jsp" %>

<s:messages/>

<head>
    <title><fmt:message key="companies.contracts"/> &gt;${pageScope.title}</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery-ui-1.7.custom.css"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/datepicker-${pageContext.request.locale}.js"></script>

    <style>
        input[type=text] {
            padding-left: 5px;
        }
    </style>
</head>

<h2 class="form cleaner"><span><fmt:message key="common.add"/></span></h2>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean"
        class="form leftSideContent" focus="" style="width: 100%;">
    <s:errors/>
    <p>
        <s:label for="name"><fmt:message key="security.maintenances.equipments.view.name"/> (*):</s:label>
        <s:text id="name" name="name" class="mediumInput"/>
    </p>

    <p>
        <s:label for="document"><fmt:message key="security.maintenances.equipments.view.document"/>:</s:label>
        <s:file id="document" name="document" class="mediumInput"/>
    </p>

    <div class="formButtons">
        <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
        <s:submit name="insertEquipment" class="button"><fmt:message key="common.submit"/></s:submit>
        <s:submit name="maintenanceGrid" class="button"><fmt:message key="common.cancel"/></s:submit>
    </div>
</s:form>

<h2 class="form cleaner"><span><fmt:message key="security.maintenances.equipments.title"/></span></h2>

<c:choose>
    <c:when test="${fn:length(requestScope.actionBean.equipments) > 0}">
        <display:table list="${requestScope.actionBean.equipments}" export="false" id="displaytable"
                       class="displaytag" uid="equipment" requestURI="/sm/SecurityMaintenance.action">

            <display:column property="name" titleKey="security.maintenances.equipments.view.name" escapeXml="true"/>

            <display:column titleKey="security.maintenances.equipments.view.document" media="html">
                <c:if test="${pageScope.equipment.document != null}">
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean"
                            event="getEquipmentDocument" class="download-file">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <s:param name="equipmentId" value="${pageScope.equipment.id}"/>
                        <c:out value="${pageScope.equipment.document.name}"/>
                    </s:link>
                </c:if>
            </display:column>

            <display:column class="oneButtonColumnWidth" media="html">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean"
                        event="deleteEquipment" class="confirmDelete">
                    <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                    <s:param name="equipmentId" value="${pageScope.equipment.id}"/>

                    <img src="${pageContext.request.contextPath}/images/Eliminar.png"
                         title="<fmt:message key="common.delete"/>"
                         alt="<fmt:message key="common.delete"/>"/></s:link>
            </display:column>

        </display:table>
    </c:when>
    <c:otherwise>
        <fmt:message key="security.maintenances.equipments.empty"/>
    </c:otherwise>
</c:choose>
