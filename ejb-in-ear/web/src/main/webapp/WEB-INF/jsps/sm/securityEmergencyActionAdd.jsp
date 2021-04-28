<%@ include file="../../../includes/taglibs.jsp" %>

<c:choose>
    <c:when test="${requestScope.actionBean.edit}">
        <c:set var="title"><fmt:message key="common.edit"/></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="title"><fmt:message key="common.add"/></c:set>
    </c:otherwise>
</c:choose>

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

        #description {
            margin-bottom: 10px;
        }
    </style>
</head>
<h2 class="form cleaner"><span>${pageScope.title}</span></h2>

<c:if test="${requestScope.actionBean.edit}">
    <div class="rightSideContent">
        <div class="chatContainer">
            <jsp:include page="securityChat.jsp"/>
        </div>
    </div>
</c:if>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityEmergencyActionBean"
        class="form leftSideContent" focus="" style="width: 100%;">
    <input type="hidden" name="emergencyId" value="${requestScope.actionBean.emergencyId}">
    <s:errors/>
    <s:messages/>
    <c:if test="${requestScope.actionBean.edit}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message key="security.emergency.code"/>:</s:label>
            <span id="code" name="emergencyAction.id"
                  class="mediumSpan">${requestScope.actionBean.emergencyAction.code}</span>
        </p>
    </c:if>
    <p>
        <s:label for="origin"><fmt:message key="security.emergency.origin"/> (*):</s:label>
        <s:text id="origin" name="emergencyAction.origin" class="mediumInput isBasic"/>
    </p>

    <p>
        <s:label for="description"><fmt:message key="security.emergency.description"/>(*):</s:label>
        <s:textarea id="description" name="emergencyAction.description" class="mediumInput isBasic" rows="6"/>
    </p>

    <p>
        <s:label for="start-date"><fmt:message key="security.emergency.startdate"/> (*):</s:label>
        <s:text id="start-date" name="emergencyAction.startDate" class="dateInput isBasic"/>
    </p>

    <c:choose>
        <c:when test="${requestScope.actionBean.edit}">
            <div style="margin-top: 50px" class="formButtons">
                <span class="mandatoryFields" style="margin-right: 50px"><fmt:message
                        key="common.mandatoryfields"/></span>
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="emergencyId" value="${requestScope.actionBean.emergencyId}"/>
                <s:submit name="editEmergencyAction" class="button isBasic"
                          style="${(!requestScope.actionBean.isUserExpert
                          && !requestScope.actionBean.hasPermissionToEdit ||
                          requestScope.actionBean.emergencyAction.closed) ? 'background-color: #6992B8;':''}"
                ><fmt:message key="common.submit"/></s:submit>
                <s:param name="token" value="${requestScope.actionBean.token}"/>
                <s:submit name="closeEmergencyAction" class="button canClose"
                          style="${(requestScope.actionBean.emergencyAction.closed ||
                          !requestScope.actionBean.hasPermissionToClose) ? 'background-color: #6992B8;':''}"
                ><fmt:message key="security.emergency.close"/></s:submit>
                <s:submit name="resetEmergencyAction" class="button isBasic"
                          style="${(!requestScope.actionBean.isUserExpert
                          && !requestScope.actionBean.hasPermissionToEdit ||
                          requestScope.actionBean.emergencyAction.closed) ? 'background-color: #6992B8;':''}"
                ><fmt:message key="common.cancel"/></s:submit>
            </div>
        </c:when>
        <c:otherwise>
            <div class="formButtons">
                <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:submit name="insertEmergencyAction" class="button"><fmt:message key="common.submit"/></s:submit>
                <s:submit name="emergencyActionGrid" class="button"><fmt:message key="common.cancel"/></s:submit>
            </div>
        </c:otherwise>
    </c:choose>
</s:form>

<script type="text/javascript">
    attachConfirmDeleteClass("canClose", "<fmt:message key="security.emergency.confirmClose"/>");

    $(function () {

        <c:if test="${requestScope.actionBean.emergencyAction.closed}">
        $(".canClose").attr("disabled", "true");
        $(".isBasic").attr("disabled", "true");
        </c:if>

        <c:if test="${requestScope.actionBean.edit && !requestScope.actionBean.hasPermissionToClose}">
        $(".canClose").attr("disabled", "true");
        </c:if>

        <c:if test="${requestScope.actionBean.edit && !requestScope.actionBean.isUserExpert && !requestScope.actionBean.hasPermissionToEdit}">
        $(".isBasic").attr("disabled", "true");
        </c:if>

        /**
         *  Date Picker
         */
        <c:if test="${(!requestScope.actionBean.edit || requestScope.actionBean.isUserExpert || requestScope.actionBean.hasPermissionToEdit)
           && !requestScope.actionBean.emergencyAction.closed  }">
        $('#start-date').datepicker({
            maxDate: 0,
            showOn: "button",
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="security.validityStartDate"/>'
        });
        $('#start-date').css('float', 'none');
        </c:if>
    });
</script>