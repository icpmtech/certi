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
        <c:if test="${!requestScope.actionBean.closed}">
            <jsp:include page="securityOpenActionsList.jsp"/>
        </c:if>
    </div>
</c:if>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
        class="form leftSideContent" focus="" style="width: 100%;">
    <s:errors/>
    <s:messages/>
    <c:if test="${requestScope.actionBean.edit}">
        <p id="code">
            <s:label for="code" class="addPaddingBottom"><fmt:message key="security.actions.planning.code"/>:</s:label>
            <span id="code" name="activity.code" class="mediumSpan">${requestScope.actionBean.activity.code}</span>
        </p>
    </c:if>
    <p>
        <s:label for="type"><fmt:message key="security.type"/> (*):</s:label>
        <s:select class="selectType isExpert" id="type" name="activity.activityType.name"
                  disabled="${pageScope.isExpert}">
            <c:forEach items="${requestScope.actionBean.activityTypes}" var="type">
                <s:option
                        id="type-${type.id}"
                        value="${type.id}">
                    ${type.name}
                </s:option>
            </c:forEach>
            <s:option id="type--1" value="-1"><fmt:message key="activity.activityType.other"/></s:option>
        </s:select>
    </p>

    <p>
        <s:text id="newTypeText" name="otherType" class="smallInput"
                style="display:none; margin-left:200px; width: 190px;"
                disabled="${pageScope.isExpert}"/>
    </p>

    <p>
        <s:label for="name"><fmt:message key="security.name"/> (*):</s:label>
        <s:text id="name" style="width: 190px;" name="activity.name" class="smallInput isExpert"
                disabled="${pageScope.isExpert}"/>
    </p>

    <p>
        <s:label for="expected-duration"><fmt:message key="security.expected.duration"/> (*):</s:label>
        <s:text id="expected-duration" style="width: 190px;" name="activity.duration" class="smallInput isExpert"
                disabled="${pageScope.isExpert}"/>
    </p>

    <p>
        <s:label for="responsible"><fmt:message key="security.internal.activity.responsible"/> (*):</s:label>
        <s:text id="responsible" name="activity.internalResponsible" class="mediumInput isExpert"
                disabled="${pageScope.isExpert}"/>
    </p>

    <p>
        <s:label for="external-entity"><fmt:message key="security.external.entity"/>:</s:label>
        <s:text id="external-entity" name="activity.externalEntity" class="mediumInput isExpert"
                disabled="${pageScope.isExpert}"/>
    </p>

    <p>
        <s:label for="expected-date"><fmt:message key="security.expected.date"/> (*):</s:label>
        <s:text id="expected-date" name="activity.dateScheduled" class="dateInput isExpert"
                disabled="${pageScope.isExpert}"/>
    </p>

    <p>
        <s:label for="recurrence"><fmt:message key="security.recurrence"/> (*):</s:label>
        <s:select class="selectRecurrence isExpert" style="width: 190px; height: 23px;"
                  id="recurrence" name="activity.recurrence.recurrenceType.id"
                  disabled="${pageScope.isExpert}">
            <s:option id="0" value="-1"><fmt:message key="activity.recurrence.once"/></s:option>
            <c:forEach items="${requestScope.actionBean.recurrenceTypes}" var="rec">
                <s:option
                        id="${rec.warningDays}"
                        value="${rec.id}">
                    ${rec.name}
                </s:option>
            </c:forEach>
        </s:select>
    </p>

    <p class="isRecurrenceOnce">
        <s:label for="warningDays">
            <fmt:message key="security.notification.days.advance"/>:
        </s:label>
        <s:text id="warningDays" name="activity.recurrence.warningDays"
                class="dateInput isExpert" value="30"
                disabled="${pageScope.isExpert}"/>
    </p>

    <p class="isRecurrenceOnce">
        <input type="hidden" name="usersToNotify" id="usersToNotify"/>
        <s:label for="warning"><fmt:message key="security.warning"/>:</s:label>

    <div class="multiselect isRecurrenceOnce">
        <div id="selectBox">
            <select class="isExpert"
                    disabled="${pageScope.isExpert}">
                <option id="selectedUsers"><fmt:message key="security.select.users"/></option>
            </select>

            <div class="overSelect"></div>
        </div>
        <div id="checkboxes">
            <c:forEach items="${requestScope.actionBean.users}" var="u">
                <s:label class="checkList" for="${u.id}">
                    <input type="checkbox" id="${u.id}" value="${u.name}" email="${u.email}"/>
                    ${u.name}
                </s:label>
            </c:forEach>
        </div>
    </div>
    </p>
    <c:choose>
        <c:when test="${requestScope.actionBean.edit}">
            <h2 class="form cleaner"><span>
                    <%--<fmt:message key="security.activity.edit"/>--%>
            </span></h2>

            <p class="spaceField">
            <s:label for="file" class="documentsLabel"><fmt:message key="security.docs"/> :</s:label>
            <c:choose>
                <c:when test="${(requestScope.actionBean.closed || !requestScope.actionBean.isUserIntermediate) &&
                empty requestScope.actionBean.activity.documents}">
                    <span id="emptyDocs" class="mediumSpan"><fmt:message key="security.common.no.documents"/></span>
                </c:when>
                <c:otherwise>
                    <c:if test="${not empty requestScope.actionBean.activity.documents}">
                        <ul class="fileList">
                            <c:forEach items="${requestScope.actionBean.activity.documents}" var="document">
                                <li>
                                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                                            event="getDocument" class="download-file">
                                        <s:param name="contractId" value="${requestScope.actionBean.contract.id}"/>
                                        <s:param name="documentId" value="${document.id}"/>
                                        <c:out value="${document.displayName}"/>
                                    </s:link>
                                    <c:if test="${
                                    (requestScope.actionBean.isUserExpert || requestScope.actionBean.isUserIntermediate)
                                    && !requestScope.actionBean.closed}">
                                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                                                event="deleteFile" class="confirmDelete deleteIcon">
                                            <s:param name="contractId" value="${requestScope.actionBean.contract.id}"/>
                                            <s:param name="activityId" value="${requestScope.actionBean.activity.id}"/>
                                            <s:param name="documentId" value="${document.id}"/>
                                            <img src="${pageContext.request.contextPath}/images/Eliminar.png"
                                                 title="<fmt:message key="common.delete"/>"
                                                 alt="<fmt:message key="common.delete"/>"/></s:link>
                                    </c:if>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </c:otherwise>
            </c:choose>
            <c:if test="${!requestScope.actionBean.closed &&
                    (requestScope.actionBean.isUserIntermediate || requestScope.actionBean.isUserExpert)
                    && (fn:length(requestScope.actionBean.activity.documents) < 6)}">
                <s:file name="newAttachments[0]" id="file-0" class="isBasic workAttachments"
                        style="margin-bottom:0px;"/>
                <s:label for="file-0" class="workAttachments"><fmt:message key="security.docs.name"/> :</s:label>
                <s:text id="file-name-0" name="attachmentName[0]" class="workAttachments"/>
                <c:forEach
                        begin="${(fn:length(requestScope.actionBean.activity.documents) == 0) ? 1 : fn:length(requestScope.actionBean.activity.documents)}"
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
            <p class="alignTop" style="clear: both;">
                <s:label for="correctiveActions"><fmt:message key="security.corrective.actions"/>:</s:label>
                <s:radio id="yesCorrectiveActions" name="correctiveActions" value="true" class="isBasic"
                         disabled="${(requestScope.actionBean.activity.closed ||
                          (!requestScope.actionBean.isUserIntermediate && !requestScope.actionBean.isUserExpert)) ? true : false}"
                /><fmt:message key="common.yes"/>
                <s:radio id="noCorrectiveActions" name="correctiveActions" value="false" class="isBasic"
                         disabled="${(requestScope.actionBean.activity.closed ||
                          (!requestScope.actionBean.isUserIntermediate && !requestScope.actionBean.isUserExpert)) ? true : false}"
                /><fmt:message key="common.no"/>
            </p>

            <p>
                <s:label for="closed-date"><fmt:message key="security.end.date"/>:</s:label>
                <s:text id="closed-date" name="activity.closedDate" class="dateInput isBasic"
                        disabled="${(requestScope.actionBean.activity.closed ||
                          (!requestScope.actionBean.isUserIntermediate && !requestScope.actionBean.isUserExpert)) ? true : false}"/>
            </p>

            <p>
                <s:label for="activityState"><fmt:message key="security.state"/>:</s:label>
                <c:choose>
                    <c:when test="${requestScope.actionBean.closed == false}">
                        <fmt:message key="security.state.open"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="security.state.closed"/>
                    </c:otherwise>
                </c:choose>
            </p>

            <div class="formButtons">
                <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                <s:param name="contractId" value="${requestScope.actionBean.contract.id}"></s:param>
                <s:param name="activityId" value="${requestScope.actionBean.activity.id}"></s:param>
                <s:submit name="editActivity" class="button isBasic"
                          disabled="${(requestScope.actionBean.activity.closed ||
                          (!requestScope.actionBean.isUserIntermediate && !requestScope.actionBean.isUserExpert)) ? true : false}"
                          style="${(requestScope.actionBean.activity.closed ||
                          (!requestScope.actionBean.isUserIntermediate && !requestScope.actionBean.isUserExpert)) ? 'background-color: #6992B8;':''}"
                ><fmt:message key="common.submit"/></s:submit>
                <c:if test="${requestScope.actionBean.isUserExpert && requestScope.actionBean.activity.closed}">
                    <s:submit name="reopenActivity" class="button confirmReopen"><fmt:message
                            key="security.common.reopen"/></s:submit>
                </c:if>
                <s:submit name="activityPlanningGrid" class="button"
                          disabled="${(requestScope.actionBean.activity.closed ||
                          (!requestScope.actionBean.isUserIntermediate && !requestScope.actionBean.isUserExpert)) ? true : false}"
                          style="${(requestScope.actionBean.activity.closed ||
                          (!requestScope.actionBean.isUserIntermediate && !requestScope.actionBean.isUserExpert)) ? 'background-color: #6992B8;':''}"
                ><fmt:message key="common.cancel"/></s:submit>
            </div>
        </c:when>
        <c:otherwise>
            <div class="formButtons">
                <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                <s:param name="contractId" value="${requestScope.actionBean.contract.id}"></s:param>
                <s:submit name="insertActivity" class="button"><fmt:message key="common.submit"/></s:submit>
                <s:submit name="activityPlanningGrid" class="button"><fmt:message key="common.cancel"/></s:submit>
            </div>
        </c:otherwise>
    </c:choose>
</s:form>

<script type="text/javascript">

    var selectedUsers = new Array();
    var expanded = false;
    var attachments = ${fn:length(requestScope.actionBean.activity.documents)};
    var lastEnabledAttachmentInput = 0;
    var ATTACHMENT_MAX_SIZE = 5;

    $(function () {

        $('input[name=editActivity]').click(function () {
            $(".isExpert").removeAttr("disabled");
        });

        if ($('select#recurrence').val().indexOf(-1) == 0) {
            $('.isRecurrenceOnce').hide();
        }

        if ($('#emptyDocs').length > 0) {
            $('#emptyDocs').parent().css('width', '600px');
        }

        $('select#recurrence').change(function (e) {
            if ($('select#recurrence').val().indexOf(-1) == 0) {
                $('.isRecurrenceOnce').hide();
            } else {
                $('.isRecurrenceOnce').show();
            }
        });

        <%--<c:if test="${requestScope.actionBean.closed}">--%>
        <%--$(".isExpert").attr("disabled", "true");--%>
        <%--$(".isBasic").attr("disabled", "true");--%>
        <%--</c:if>--%>

        <%--<c:if test="${(requestScope.actionBean.edit && !requestScope.actionBean.isUserExpert) || requestScope.actionBean.closed}">--%>
        <%--$(".isExpert").attr("disabled", "true");--%>
        <%--</c:if>--%>

        <%--<c:if test="${requestScope.actionBean.edit && (requestScope.actionBean.isUserBasic--%>
        <%--&&  !requestScope.actionBean.isUserExpert &&  !requestScope.actionBean.isUserIntermediate)}">--%>
        <%--$(".isBasic").attr("disabled", "true");--%>
        <%--</c:if>--%>

        <c:if test="${(!requestScope.actionBean.edit || requestScope.actionBean.isUserExpert)
            && !requestScope.actionBean.closed  }">
        /**
         * Show hide the list of users.
         */
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
        $('#expected-date').datepicker({
            minDate: 0,
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="security.validityStartDate"/>'
        });
        $('#expected-date').css('float', 'none');

        </c:if>

        attachConfirmDelete("<fmt:message key="security.document.confirmDelete"/>");
        attachConfirmDeleteClass("confirmReopen", "<fmt:message key="security.activity.reopen.confirmReopen"/>");

        <c:if test="${requestScope.actionBean.activity.activityType.id != null}">
        $("#type").attr("value", ${requestScope.actionBean.activity.activityType.id});
        </c:if>

        <c:if test="${!requestScope.actionBean.closed &&
            (requestScope.actionBean.edit &&
                (requestScope.actionBean.isUserIntermediate || requestScope.actionBean.isUserExpert)
            || !requestScope.actionBean.isUserBasic)}">

        /**
         * Multiple file upload
         * */
        $('#file-0, #file-1, #file-2, #file-3, #file-4, #file-5').change(handleFileSelection);

        $('#closed-date').datepicker({
            maxDate: 0,
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="security.validityStartDate"/>'
        });
        $('#closed-Date').css('float', 'none');
        </c:if>
        <c:choose>
        <c:when test="${requestScope.actionBean.activity.recurrence != null}">
        <c:forEach items="${requestScope.actionBean.activity.recurrence.notifications}" var="n">
        selectedUsers.push({id: '${n.user.id}', name: '${n.user.name}', email: '${n.user.email}'});
        var uId =${n.user.id};
        $('input#' + uId)[0].checked = true;
        if (selectedUsers.length == 1) {
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
        </c:forEach>
        </c:when>
        <c:otherwise>
        $("select#recurrence").val(-1);
        $('.isRecurrenceOnce').hide();
        </c:otherwise>
        </c:choose>

        <c:choose>
        <c:when test="${requestScope.actionBean.closed || !requestScope.actionBean.isUserExpert}">
        if (typeof $('.multiselect').multiselect !== 'undefined') {
            $('.multiselect').multiselect('disable');
        }
        $('.multiselect select').attr('disabled', true);
        </c:when>
        <c:otherwise>
        $('.multiselect select').attr('disabled', false);
        </c:otherwise>
        </c:choose>

        /**
         *
         */
        function checkIfTypeIsOther() {
            if ($('#type option:selected').val() === '${requestScope.actionBean.otherId}') {
                $('#newTypeText').show();
            } else {
                $('#newTypeText').hide();
            }
        }

        /**
         *  Activity Type - Other
         *  Show/hide the input
         */
        $('#type').change(function () {
            checkIfTypeIsOther();
        });


        /**
         *  Change the days prior to the notification.
         *  Set this equivalent to the selected recurrence type.
         */
        $('#recurrence').change(function () {
            var opVal = $("#recurrence").val();
            var selectedOpVal = $("select#recurrence option[value=" + opVal + "]").attr("id");
            $('#warningDays').val(selectedOpVal);
        });

        /**
         * Display the list of select users.
         */
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
         * Hide the users list when the page is clicked.
         */
        $('html').click(function (e) {
            if (expanded
                    && $(e.target).parent().attr('id') !== 'checkboxes'
                    && $(e.target).parent().parent().attr('id') !== 'checkboxes') {
                $('#checkboxes').hide();
                expanded = false;
            }
        });
        checkIfTypeIsOther();

        /**
         * We cannot allow to add corrective actions if the user is going to close this entity
         */
        $('#closed-date').change(function () {
            if ($('#closed-date').val() !== '') {
                $("#noCorrectiveActions").attr('checked', true);
            }
        });
        $('#yesCorrectiveActions').change(function () {
            if ($('#yesCorrectiveActions').is(":checked")) {
                $('#closed-date').val('')
            }
        });
    });


</script>
