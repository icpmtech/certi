<%@ include file="../../../includes/taglibs.jsp" %>

<c:choose>
    <c:when test="${requestScope.actionBean.edit}">
        <c:set var="title"><fmt:message key="common.edit"/></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="title"><fmt:message key="common.add"/></c:set>
    </c:otherwise>
</c:choose>
<c:set var="isExpert"
       value="${(requestScope.actionBean.edit && !requestScope.actionBean.isUserExpert)
       || requestScope.actionBean.closed}" scope="page"></c:set>

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
<h2 class="form cleaner"><span>${pageScope.title}</span></h2>

<c:if test="${requestScope.actionBean.edit}">
    <div class="rightSideContent">
        <div class="chatContainer">
            <jsp:include page="securityChat.jsp"/>
        </div>
    </div>
</c:if>
<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean"
        class="form leftSideContent" focus="" style="width: 100%;">
    <s:errors/>
    <s:messages/>
    <c:if test="${requestScope.actionBean.edit}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message key="security.actions.planning.code"/>:</s:label>
            <span id="code" name="correctiveAction.id"
                  class="mediumSpan">${requestScope.actionBean.correctiveAction.code}</span>
        </p>
    </c:if>
    <%-- Begin Activity --%>
    <c:if test="${requestScope.actionBean.correctiveAction.activity != null}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message
                    key="security.actions.planning.origin.code"/>:</s:label>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                    event="activityPlanningEdit" class="">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="activityId" value="${requestScope.actionBean.correctiveAction.activity.id}"/>
                <span id="code" name="correctiveAction.activity.code"
                      class="mediumSpan">${requestScope.actionBean.correctiveAction.activity.code}</span>
            </s:link>
        </p>
    </c:if>
    <c:if test="${requestScope.actionBean.activityId != null && requestScope.actionBean.activityCode != null}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message
                    key="security.actions.planning.origin.code"/>:</s:label>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                    event="activityPlanningEdit" class="">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="activityId" value="${requestScope.actionBean.activityId}"/>
                <span id="code" name="correctiveAction.activity.code"
                      class="mediumSpan">${requestScope.actionBean.activityCode}</span>
            </s:link>
        </p>
    </c:if>
    <%-- End Activity --%>
    <%-- Begin Anomlay --%>
    <c:if test="${requestScope.actionBean.correctiveAction.anomaly != null}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message
                    key="security.actions.planning.origin.code"/>:</s:label>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
                    event="${requestScope.actionBean.event}" class="">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="anomalyId" value="${requestScope.actionBean.correctiveAction.anomaly.id}"/>
                <span id="code" name="correctiveAction.anomaly.code"
                      class="mediumSpan">${requestScope.actionBean.correctiveAction.anomaly.code}</span>
            </s:link>
        </p>
    </c:if>
    <c:if test="${requestScope.actionBean.anomalyId != null && requestScope.actionBean.anomalyCode != null}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message
                    key="security.actions.planning.origin.code"/>:</s:label>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
                    event="${requestScope.actionBean.event}" class="">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="anomalyId" value="${requestScope.actionBean.anomalyId}"/>
                <span id="code" name="correctiveAction.anomaly.code"
                      class="mediumSpan">${requestScope.actionBean.anomalyCode}</span>
            </s:link>
        </p>
    </c:if>
    <%-- End Activity --%>
    <%-- Begin Work Impact --%>
    <c:if test="${requestScope.actionBean.correctiveAction.securityImpactWork != null}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message
                    key="security.actions.planning.origin.code"/>:</s:label>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
                    event="${requestScope.actionBean.event}" class="">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="impactWorkId" value="${requestScope.actionBean.correctiveAction.securityImpactWork.id}"/>
                <span id="code" name="correctiveAction.securityImpactWork.code"
                      class="mediumSpan">${requestScope.actionBean.correctiveAction.securityImpactWork.code}</span>
            </s:link>
        </p>
    </c:if>
    <c:if test="${requestScope.actionBean.impactWorkId != null && requestScope.actionBean.impactWorkCode != null}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message
                    key="security.actions.planning.origin.code"/>:</s:label>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
                    event="${requestScope.actionBean.event}" class="">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="impactWorkId" value="${requestScope.actionBean.impactWorkId}"/>
                <span id="code" name="correctiveAction.securityImpactWork.code"
                      class="mediumSpan">${requestScope.actionBean.impactWorkCode}</span>
            </s:link>
        </p>
    </c:if>
    <%-- End Work Impact --%>
    <%-- Begin Maintenance --%>
    <c:if test="${requestScope.actionBean.correctiveAction.maintenance != null}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message
                    key="security.actions.planning.origin.code"/>:</s:label>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean"
                    event="maintenanceEdit" class="">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="maintenanceId" value="${requestScope.actionBean.correctiveAction.maintenance.id}"/>
                <span id="code" name="correctiveAction.activity.code"
                      class="mediumSpan">${requestScope.actionBean.correctiveAction.maintenance.code}</span>
            </s:link>
        </p>
    </c:if>
    <c:if test="${requestScope.actionBean.maintenanceId != null && requestScope.actionBean.maintenanceCode != null}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message
                    key="security.actions.planning.origin.code"/>:</s:label>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean"
                    event="maintenanceEdit" class="">
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                <s:param name="maintenanceId" value="${requestScope.actionBean.maintenanceId}"/>
                <span id="code" name="correctiveAction.activity.code"
                      class="mediumSpan">${requestScope.actionBean.maintenanceCode}</span>
            </s:link>
        </p>
    </c:if>
    <%-- End Maintenance --%>
    <p>
        <s:label for="begin-date"><fmt:message key="security.actions.planning.initial.date"/> (*):</s:label>
        <s:text id="begin-date" name="correctiveAction.startDate" class="dateInput isExpert"
                disabled="${pageScope.isExpert}"/>
    </p>

    <p>
        <s:label for="name"><fmt:message key="security.actions.planning.name"/>:</s:label>
        <s:text id="name" name="correctiveAction.name" class="madiumInput isExpert" disabled="${pageScope.isExpert}"/>
    </p>

    <p>
        <s:label for="description"><fmt:message key="security.actions.planning.description"/> (*):</s:label>
        <s:textarea id="description" name="correctiveAction.description" class="mediumInput isExpertSpecial" rows="10"
                    readonly="${pageScope.isExpert}"/>
    </p>

    <p>
        <s:label for="responsible"><fmt:message key="security.actions.planning.execution.responsible"/> (*):</s:label>
        <s:text id="responsible" name="correctiveAction.executionResponsible" class="mediumInput isExpert"
                disabled="${pageScope.isExpert}"/>
    </p>

    <p>
        <s:label for="expected-duration"><fmt:message key="security.expected.duration"/> (*):</s:label>
        <s:text id="expected-duration" style="width: 190px;" name="correctiveAction.duration"
                class="smallInput isExpert" disabled="${pageScope.isExpert}"/>
    </p>

    <%-- CERTOOL-683 Nova combobox para notificacoes--%>
    <p class="isRecurrenceOnce">
        <input type="hidden" name="usersToNotify" id="usersToNotify"/>
        <s:label for="warning"><fmt:message key="security.notification.list"/>:</s:label>

    <div class="multiselect isRecurrenceOnce">
        <div id="selectBox">
            <select class="isExpert"
                    disabled="${pageScope.isExpert}">
                <option id="selectedUsers"><fmt:message key="security.select.users"/></option>
            </select>

            <div class="overSelect"></div>
        </div>
        <div id="checkboxes">
            <c:set var="preselectedUsers" value="${requestScope.actionBean.usersToNotify}"/>
            <c:forEach items="${requestScope.actionBean.users}" var="u">
                <s:label class="checkList" for="${u.id}">
                    <c:choose>
                        <c:when test="${fn:contains(preselectedUsers, u.id)}">
                            <input type="checkbox" id="${u.id}" value="${u.name}" email="${u.email}" checked="true"/>
                        </c:when>
                        <c:otherwise>
                            <input type="checkbox" id="${u.id}" value="${u.name}" email="${u.email}"/>
                        </c:otherwise>
                    </c:choose>
                    ${u.name}
                </s:label>
            </c:forEach>
        </div>
    </div>
    </p>

    <c:choose>
        <c:when test="${requestScope.actionBean.edit}">
            <h2 class="form cleaner"><span></span></h2>
            <p class="spaceField">
            <s:label for="file" class="documentsLabel"><fmt:message key="security.docs"/> :</s:label>
            <c:choose>
                <c:when test="${(requestScope.actionBean.closed || !requestScope.actionBean.isUserIntermediate) &&
               empty requestScope.actionBean.correctiveAction.documents}">
                    <span id="emptyDocs" class="mediumSpan"><fmt:message key="security.common.no.documents"/></span>
                </c:when>
                <c:otherwise>
                    <c:if test="${not empty requestScope.actionBean.correctiveAction.documents}">
                        <ul class="fileList">
                            <c:forEach items="${requestScope.actionBean.correctiveAction.documents}" var="document">
                                <li>
                                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean"
                                            event="getDocument" class="download-file">
                                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                                        <s:param name="correctiveActionId"
                                                 value="${requestScope.actionBean.correctiveAction.id}"/>
                                        <s:param name="documentId" value="${document.id}"/>
                                        <c:out value="${document.displayName}"/>
                                    </s:link>
                                    <c:if test="${(requestScope.actionBean.isUserIntermediate || requestScope.actionBean.isUserExpert)
                                           && !requestScope.actionBean.correctiveAction.closed}">
                                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionsPlanningActionBean"
                                                event="deleteDocument" class="confirmDelete deleteIcon">
                                            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                                            <s:param name="correctiveActionId"
                                                     value="${requestScope.actionBean.correctiveAction.id}"/>
                                            <s:param name="documentId" value="${document.id}"/>
                                            <img src="${pageContext.request.contextPath}/images/Eliminar.png"
                                                 title="<fmt:message key="common.delete"/>"
                                                 alt="<fmt:message key="common.delete"/>"/>
                                        </s:link>
                                    </c:if>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </c:otherwise>
            </c:choose>
            <c:if test="${!requestScope.actionBean.closed &&
                   (requestScope.actionBean.isUserIntermediate || requestScope.actionBean.isUserExpert)
                   && (fn:length(requestScope.actionBean.correctiveAction.documents) < 6)}">
                <s:file name="newAttachments[0]" id="file-0" class="isBasic workAttachments"
                        style="margin-bottom:0px;"/>
                <s:label for="file-0" class="workAttachments"><fmt:message key="security.docs.name"/> :</s:label>
                <s:text id="file-name-0" name="attachmentName[0]" class="workAttachments"/>
                <c:forEach
                        begin="${(fn:length(requestScope.actionBean.correctiveAction.documents) == 0) ? 1 : fn:length(requestScope.actionBean.correctiveAction.documents)}"
                        end="5" step="1" varStatus="loop">
                    <s:file name="newAttachments[${loop.index}]" id="file-${loop.index}"
                            class="hidden workAttachments"/>
                    <s:label id="file-label-${loop.index}" for="file-name-${loop.index}" class="hidden workAttachments"><fmt:message
                            key="security.docs.name"/> :</s:label>
                    <s:text id="file-name-${loop.index}" name="attachmentName[${loop.index}]"
                            class="hidden workAttachments"/>
                </c:forEach>
            </c:if>
            </p>
            <p>
                <s:label for="notes"><fmt:message key="security.actions.planning.notes"/>:</s:label>
                <s:textarea id="notes" name="correctiveAction.notes" class="mediumInput isBasic" rows="10"/>
            </p>

            <p>
                <s:label for="closed-date"><fmt:message key="security.end.date"/>:</s:label>
                <s:text id="closed-date" name="correctiveAction.closedDate" class="dateInput isBasic"/>
            </p>

            <p>
                <s:label for="activityState"><fmt:message key="security.state"/>:</s:label>
                <c:choose>
                    <c:when test="${requestScope.actionBean.correctiveAction.closed == false}">
                        <fmt:message key="security.state.open"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="security.state.closed"/>
                    </c:otherwise>
                </c:choose>
            </p>

            <div class="formButtons">
                <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"></s:param>
                <s:param name="correctiveActionId" value="${requestScope.actionBean.correctiveActionId}"></s:param>
                <s:submit name="editAction" class="button isBasic"
                          style="${(requestScope.actionBean.isUserBasic
                         && !requestScope.actionBean.isUserIntermediate
                         && !requestScope.actionBean.isUserExpert || (requestScope.actionBean.correctiveAction.closed)) ? 'background-color: #6992B8;':''}"
                ><fmt:message key="common.submit"/></s:submit>
                <c:if test="${requestScope.actionBean.isUserExpert && requestScope.actionBean.correctiveAction.closed &&
               ((requestScope.actionBean.correctiveAction.activity != null && requestScope.actionBean.correctiveAction.activity.closed == false)
               || (requestScope.actionBean.correctiveAction.maintenance != null && requestScope.actionBean.correctiveAction.maintenance.closed == false)
               || (requestScope.actionBean.correctiveAction.anomaly != null && requestScope.actionBean.correctiveAction.anomaly.closed == false)
               || (requestScope.actionBean.correctiveAction.securityImpactWork != null && requestScope.actionBean.correctiveAction.securityImpactWork.closed == false)
               )}">
                    <s:submit name="reopenAction" class="button confirmReopen"><fmt:message
                            key="security.common.reopen"/></s:submit>
                </c:if>
                <s:submit name="viewActions" class="button isBasic"
                          style="${(requestScope.actionBean.isUserBasic
                         && !requestScope.actionBean.isUserIntermediate
                         && !requestScope.actionBean.isUserExpert || (requestScope.actionBean.correctiveAction.closed)) ? 'background-color: #6992B8;':''}"
                ><fmt:message key="common.cancel"/></s:submit>
            </div>
        </c:when>
        <c:otherwise>
            <div class="formButtons">
                <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"></s:param>
                <c:if test="${requestScope.actionBean.activityId != null}">
                    <s:param name="activityId" value="${requestScope.actionBean.activityId}"></s:param>
                    <s:param name="activityCode" value="${requestScope.actionBean.activityCode}"></s:param>
                </c:if>
                <c:if test="${requestScope.actionBean.anomalyId != null}">
                    <s:param name="anomalyId" value="${requestScope.actionBean.anomalyId}"></s:param>
                    <s:param name="anomalyCode" value="${requestScope.actionBean.anomalyCode}"></s:param>
                    <s:param name="event" value="${requestScope.actionBean.event}"></s:param>
                </c:if>
                <c:if test="${requestScope.actionBean.impactWorkId != null}">
                    <s:param name="impactWorkId" value="${requestScope.actionBean.impactWorkId}"/>
                    <s:param name="impactWorkCode" value="${requestScope.actionBean.impactWorkCode}"/>
                    <s:param name="event" value="${requestScope.actionBean.event}"/>
                </c:if>
                <c:if test="${requestScope.actionBean.maintenanceId != null}">
                    <s:param name="maintenanceId" value="${requestScope.actionBean.maintenanceId}"/>
                    <s:param name="maintenanceCode" value="${requestScope.actionBean.maintenanceCode}"/>
                </c:if>
                <c:if test="${requestScope.actionBean.activityId != null || requestScope.actionBean.anomalyId != null ||
                       requestScope.actionBean.impactWorkId != null || requestScope.actionBean.maintenanceId != null}">
                    <s:submit name="insertAnotherAction" class="button" style="width: 140px;"><fmt:message
                            key="security.common.insert.other"/></s:submit>
                </c:if>
                <s:submit name="insertAction" class="button"><fmt:message key="common.submit"/></s:submit>
                <s:submit name="viewActions" class="button"><fmt:message key="common.cancel"/></s:submit>
            </div>
        </c:otherwise>
    </c:choose>
</s:form>

<script type="text/javascript">

    var selectedUsers = new Array();
    var expanded = false;
    var attachments = ${fn:length(requestScope.actionBean.correctiveAction.documents)};
    var lastEnabledAttachmentInput = 0;
    var ATTACHMENT_MAX_SIZE = 5;
    $(function () {

        $('input[name=editAction]').click(function () {
            $(".isExpert").removeAttr("disabled");
        });

        <c:if test="${!requestScope.actionBean.correctiveAction.closed
            && ((requestScope.actionBean.isUserIntermediate && !requestScope.actionBean.edit) || requestScope.actionBean.isUserExpert)}">

        /**
         * Show hide the list of users.
         NEW */
        $('#selectBox').click(function (e) {
            e.stopPropagation();
            var checkboxes = $('#checkboxes');
            if (!expanded) {
                checkboxes.show();
                expanded = true;
            } else {
                checkboxes.hide();
                expanded = false;
            }
        });

        /**
         *  Date Picker
         */
        $('#begin-date').datepicker({
            showOn: "button",
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="security.validityStartDate"/>'
        });
        $('#begin-date').css('float', 'none');
        <c:if test="${requestScope.actionBean.edit}">
        $('#closed-date').datepicker({
            maxDate: 0,
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="security.validityStartDate"/>'
        });
        $('#closed-date').css('float', 'none');
        </c:if>
        </c:if>

        <c:if test="${requestScope.actionBean.correctiveAction.closed}">
        $(".isExpert").attr("disabled", "true");
        $(".isBasic").attr("disabled", "true");
        </c:if>

        <c:choose>
        <%--<c:when test="${requestScope.actionBean.closed || requestScope.actionBean.isUserBasic || (!requestScope.actionBean.isUserExpert && (requestScope.actionBean.isUserIntermediate && requestScope.actionBean.edit))}">
        --%><c:when test="${requestScope.actionBean.closed || !requestScope.actionBean.isUserExpert}">
        if (typeof $('.multiselect').multiselect !== 'undefined') {
            $('.multiselect').multiselect('disable');
        }
        $('.multiselect select').attr('disabled', true);
        </c:when>
        <c:otherwise>
        $('.multiselect select').attr('disabled', false);
        </c:otherwise>
        </c:choose>

        <%--<c:if test="${requestScope.actionBean.edit && !requestScope.actionBean.isUserExpert}">--%>
        <%--$(".isExpert").attr("disabled", "true");--%>
        <%--</c:if>--%>

        <c:if test="${requestScope.actionBean.edit
            && (requestScope.actionBean.isUserBasic &&  !requestScope.actionBean.isUserExpert
            &&  !requestScope.actionBean.isUserIntermediate)}">
        $(".isBasic").attr("disabled", "true");
        </c:if>


        /**
         * Display the list of select users.
         NEW */
        $('#checkboxes input').change(function (e) {
            e.stopPropagation();
            if (e.currentTarget.checked) {
                selectedUsers.push({id: e.target.id, name: e.target.value, email: $("#" + e.target.id).attr("email")});
            } else {
                findAndRemove(selectedUsers, 'id', e.target.id);
            }

            if (selectedUsers.length == 0) {
                $('#selectedUsers').html('<fmt:message key="security.select.users"/>');
                $('#usersToNotify').val("");
            } else if (selectedUsers.length == 1) {
                $('#selectedUsers').html(selectedUsers[0].name);
                $('#usersToNotify').val(selectedUsers[0].id);
            } else {
                $('#selectedUsers').html("");
                $('#usersToNotify').val("");
                for (var i = 0; i < selectedUsers.length; i++) {
                    $('#selectedUsers').append(selectedUsers[i].name);
                    $('#usersToNotify').val($('#usersToNotify').val() + ", " + selectedUsers[i].id);
                    if (i + 1 < selectedUsers.length) {
                        $('#selectedUsers').append(", ");
                    }
                }
            }
        });

        /**
         * Refresh list.
         NEW */
        $('#checkboxes input').trigger('change');

        /**
         * Hide the users list when the page is clicked.
         NEW */
        $('html').click(function (e) {
            if (expanded
                && $(e.target).parent().attr('id') !== 'checkboxes'
                && $(e.target).parent().parent().attr('id') !== 'checkboxes') {
                $('#checkboxes').hide();
                expanded = false;
            }
        });

        /**
         * Multiple file upload
         * */
        $('#file-0, #file-1, #file-2, #file-3, #file-4, #file-5').change(handleFileSelection);

        attachConfirmDelete("<fmt:message key="security.document.confirmDelete"/>");
        attachConfirmDeleteClass("confirmReopen", "<fmt:message key="security.actions.reopen.confirmReopen"/>");

    });
</script>
