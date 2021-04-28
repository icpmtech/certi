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
        <c:if test="${!requestScope.actionBean.securityImpactWork.closed}">
            <jsp:include page="securityOpenActionsList.jsp"/>
        </c:if>
    </div>
</c:if>
<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
        class="form leftSideContent" focus="" style="width: 100%;">
    <input type="hidden" name="securityImpactWork.id" value="${requestScope.actionBean.impactWorkId}">
    <s:errors/>
    <s:messages/>

    <c:if test="${requestScope.actionBean.edit}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message key="security.actions.planning.code"/>:</s:label>
            <span id="code" name="securityImpactWork.id"
                  class="mediumSpan">${requestScope.actionBean.securityImpactWork.code}</span>
        </p>
    </c:if>
    <p>
        <s:label for="start-date"><fmt:message key="security.actions.view.startdate"/> (*):</s:label>
        <s:text id="start-date" name="securityImpactWork.startDate" class="dateInput isExpert"
                disabled="${pageScope.isExpert}"/>
    </p>

    <p>
        <s:label for="responsible"><fmt:message key="security.impact.work.responsible"/> (*):</s:label>
        <s:text id="responsible" name="securityImpactWork.responsible" class="mediumInput isExpert"
                disabled="${pageScope.isExpert}"/>
    </p>
    <c:choose>
        <c:when test="${requestScope.actionBean.isModification}">
            <p>
                <s:label for="name"><fmt:message key="security.impact.work.name"/>:</s:label>
                <s:text id="name" name="securityImpactWork.name" class="madiumInput isExpert" disabled="${pageScope.isExpert}"/>
            </p>
            <p>
                <s:label for="description"><fmt:message key="security.actions.planning.description"/>(*):</s:label>
                <s:textarea id="description" name="securityImpactWork.description" class="mediumInput isExpert" rows="6"
                            disabled="${pageScope.isExpert}"/>
            </p>
        </c:when>
        <c:otherwise>
            <p>
                <s:label for="description"><fmt:message key="security.impact.work.local.description"/>(*):</s:label>
                <s:text id="description" name="securityImpactWork.description" class="mediumInput isExpert"
                        disabled="${pageScope.isExpert}"/>
            </p>

            <p>
                <s:label for="duration"><fmt:message key="security.impact.work.estimated.duration"/> (*):</s:label>
                <s:text id="duration" name="securityImpactWork.duration" class="mediumInput isExpert"
                        disabled="${pageScope.isExpert}"/>
            </p>

            <p>
                <input type="hidden" name="risksId" id="workRisks"/>
                <s:label for="warning"><fmt:message key="security.impact.work.risks"/>:</s:label>

            <div class="multiselect">
                <div id="selectBox">
                    <select class="isExpert"/>
                    <option id="selectedRisks"><fmt:message key="security.impact.work.select.risks"/></option>
                    </select>
                    <div class="overSelect"></div>
                </div>
                <div id="checkboxes">
                    <c:forEach items="${requestScope.actionBean.risks}" var="r">
                        <s:label class="checkList" for="${r.id}">
                            <input type="checkbox" id="${r.id}" value="${r.id}" name="${r.name}"/>
                            ${r.name}
                        </s:label>
                    </c:forEach>
                </div>
            </div>
            </p>
        </c:otherwise>
    </c:choose>
    <c:if test="${!requestScope.actionBean.isModification && requestScope.actionBean.edit}">
        <p class="workAthorizationPrint">
            <img class="alignBottom" src="${pageContext.request.contextPath}/images/PDF.png" width="16" height="16">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
                    event="getImpactWorkReportPdf" class="" style="">
                <s:param name="contractId" value="${requestScope.actionBean.contract.id}"/>
                <s:param name="impactWorkId" value="${requestScope.actionBean.securityImpactWork.id}"/>
                <fmt:message key="security.common.print.work.auth"/>
            </s:link>
        </p>
    </c:if>
    <c:choose>
        <c:when test="${requestScope.actionBean.edit}">
            <h2 class="form cleaner"><span>
                    <%--<fmt:message key="security.activity.edit"/>--%>
            </span></h2>
            <c:if test="${requestScope.actionBean.isModification}">
                <p>
                    <s:label for="securityImpact"><fmt:message key="security.anomaly.securityImpact"/> (*):</s:label>
                    <s:select class="selectSecurityImpact isBasic" style="width: 190px; height: 23px;"
                              id="securityImpact" name="securityImpactWork.securityImpact.id">
                        <c:forEach items="${requestScope.actionBean.securityImpacts}" var="sec">
                            <s:option
                                    id="securityImpact-${sec.id}"
                                    value="${sec.id}">
                                ${sec.name}
                            </s:option>
                        </c:forEach>
                    </s:select>
                </p>

                <p>
                    <s:label for="qualifiedEntity" class="spaceField"><fmt:message
                            key="security.impact.work.qualifiedEntity"/> (*):</s:label>
                    <s:text id="qualifiedEntity" name="securityImpactWork.qualifiedEntity" class="mediumInput isBasic"/>
                </p>
            </c:if>
            <p class="spaceField">
            <s:label for="file" class="documentsLabel"><fmt:message key="security.docs"/> :</s:label>
            <c:choose>
                <c:when test="${(requestScope.actionBean.closed || !requestScope.actionBean.isUserIntermediate) &&
                empty requestScope.actionBean.securityImpactWork.documents}">
                    <span id="emptyDocs" class="mediumSpan"><fmt:message key="security.common.no.documents"/></span>
                </c:when>
                <c:otherwise>
                    <c:if test="${not empty requestScope.actionBean.securityImpactWork.documents}">
                        <ul class="fileList">
                            <c:forEach items="${requestScope.actionBean.securityImpactWork.documents}" var="document">
                                <li>
                                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
                                            event="getDocument" class="download-file">
                                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                                        <s:param name="impactWorkId"
                                                 value="${requestScope.actionBean.securityImpactWork.id}"/>
                                        <s:param name="documentId" value="${document.id}"/>
                                        <c:out value="${document.displayName}"/>
                                    </s:link>
                                    <c:if test="${(requestScope.actionBean.isUserIntermediate || requestScope.actionBean.isUserExpert)
                                            && !requestScope.actionBean.securityImpactWork.closed}">
                                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityImpactWorkActionBean"
                                                event="deleteDocument" class="confirmDelete deleteIcon">
                                            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                                            <s:param name="isModification"
                                                     value="${requestScope.actionBean.isModification}"/>
                                            <s:param name="impactWorkId"
                                                     value="${requestScope.actionBean.securityImpactWork.id}"/>
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
                    && (fn:length(requestScope.actionBean.securityImpactWork.documents) < 6)}">
                <s:file name="newAttachments[0]" id="file-0" class="isBasic workAttachments"
                        style="margin-bottom:0px;"/>
                <s:label for="file-0" class="workAttachments"><fmt:message key="security.docs.name"/> :</s:label>
                <s:text id="file-name-0" name="attachmentName[0]" class="workAttachments"/>
                <c:forEach
                        begin="${(fn:length(requestScope.actionBean.securityImpactWork.documents) == 0) ? 1 : fn:length(requestScope.actionBean.securityImpactWork.documents)}"
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
                <s:radio id="yesCorrectiveActions" name="correctiveActions" value="true" class="isBasic"/><fmt:message
                    key="common.yes"/>
                <s:radio id="noCorrectiveActions" name="correctiveActions" value="false" class="isBasic"/><fmt:message
                    key="common.no"/>
            </p>

            <p>
                <s:label for="closed-date"><fmt:message key="security.end.date"/>:</s:label>
                <s:text id="closed-date" name="securityImpactWork.closedDate" class="dateInput isBasic"/>
            </p>

            <p>
                <s:label for="activityState"><fmt:message key="security.state"/>:</s:label>
                <c:choose>
                    <c:when test="${requestScope.actionBean.securityImpactWork.closed == false}">
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
                <s:param name="impactWorkId" value="${requestScope.actionBean.impactWorkId}"></s:param>
                <s:submit name="${requestScope.actionBean.isModification ? 'editModification' : 'editAuthorization'}"
                          class="button isBasic"
                          style="${((requestScope.actionBean.isUserBasic && !requestScope.actionBean.isUserIntermediate
                    && !requestScope.actionBean.isUserExpert) || requestScope.actionBean.securityImpactWork.closed ) ? 'background-color: #6992B8;':''}"
                ><fmt:message key="common.submit"/></s:submit>
                <c:if test="${requestScope.actionBean.isUserExpert && requestScope.actionBean.securityImpactWork.closed}">
                    <s:submit name="reopenWork" class="button confirmReopen"><fmt:message key="security.common.reopen"/></s:submit>
                </c:if>
                <s:submit name="viewImpactWorks" class="button isBasic"
                          style="${((requestScope.actionBean.isUserBasic && !requestScope.actionBean.isUserIntermediate
                    && !requestScope.actionBean.isUserExpert) || requestScope.actionBean.securityImpactWork.closed ) ? 'background-color: #6992B8;':''}"
                ><fmt:message key="common.cancel"/></s:submit>
            </div>
        </c:when>
        <c:otherwise>
            <div class="formButtons">
                <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"></s:param>
                <s:submit
                        name="${requestScope.actionBean.isModification ? 'insertModification' : 'insertAuthorization'}"
                        class="button"><fmt:message key="common.submit"/></s:submit>
                <s:submit name="viewImpactWorks" class="button"><fmt:message key="common.cancel"/></s:submit>
            </div>
        </c:otherwise>
    </c:choose>
</s:form>
<script type="text/javascript">
    var selectedRisks = new Array();
    var expanded = false;
    var attachments = ${fn:length(requestScope.actionBean.securityImpactWork.documents)};
    var lastEnabledAttachmentInput = 0;
    var ATTACHMENT_MAX_SIZE = 5;
    $(function () {

        <c:if test="${requestScope.actionBean.securityImpactWork.risks != null}">
        <c:forEach items="${requestScope.actionBean.securityImpactWork.risks}" var="r">
        selectedRisks.push({id: '${r.id}', name: '${r.name}', value: '${r.id}'});
        </c:forEach>

        if (selectedRisks.length == 1) {
            $('#selectedRisks').html(selectedRisks[0].name);
            $('#workRisks').val(selectedRisks[0].id);
            $('input#' + selectedRisks[0].id)[0].checked = true;
        } else {
            $('#selectedRisks').html("");
            $('#workRisks').val("");
            for (var i = 0; i < selectedRisks.length; i++) {
                $('input#' + selectedRisks[i].id)[0].checked = true;
                $('#selectedRisks').append(selectedRisks[i].name);
                $('#workRisks').val($('#workRisks').val() + ", " + selectedRisks[i].id);
                if (i + 1 < selectedRisks.length) {
                    $('#selectedRisks').append(", ");
                }
            }
        }

        </c:if>
        $('input[name=editModification]').click(function () {
            $(".isExpert").removeAttr("disabled");
        });
        $('input[name=editAuthorization]').click(function () {
            $(".isExpert").removeAttr("disabled");
        });


        <c:if test="${requestScope.actionBean.securityImpactWork.closed}">
        $(".isExpert").attr("disabled", "true");
        $(".isBasic").attr("disabled", "true");
        </c:if>

        <%--<c:if test="${requestScope.actionBean.edit && !requestScope.actionBean.isUserExpert}">--%>
        <%--$(".isExpert").attr("disabled", "true");--%>
        <%--</c:if>--%>

        <c:if test="${requestScope.actionBean.edit && (requestScope.actionBean.isUserBasic
            &&  !requestScope.actionBean.isUserExpert &&  !requestScope.actionBean.isUserIntermediate)}">
        $(".isBasic").attr("disabled", "true");
        </c:if>

        <c:if test="${requestScope.actionBean.securityImpactWork.securityImpact.id != null}">
        $("#type").attr("value", ${requestScope.actionBean.securityImpactWork.securityImpact.id});
        </c:if>
        /**
         *  Date Picker
         */
        <c:if test="${(!requestScope.actionBean.edit || requestScope.actionBean.isUserExpert)
           && !requestScope.actionBean.securityImpactWork.closed  }">
        $('#start-date').datepicker({
            showOn: "button",
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="security.validityStartDate"/>'
        });
        $('#start-date').css('float', 'none');
        </c:if>
        <c:if test="${requestScope.actionBean.edit && !requestScope.actionBean.securityImpactWork.closed &&
        requestScope.actionBean.isUserIntermediate}">
        $('#closed-date').datepicker({
            maxDate: 0,
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="security.validityStartDate"/>'
        });
        $('#closed-date').css('float', 'none');

        /**
         * Multiple file upload
         * */
        $('#file-0, #file-1, #file-2, #file-3, #file-4, #file-5').change(handleFileSelection);
        </c:if>
        attachConfirmDelete("<fmt:message key="security.document.confirmDelete"/>");
        attachConfirmDeleteClass("confirmReopen", "<fmt:message key="security.impact.work.reopen.confirmReopen"/>");
        <c:if test="${!requestScope.actionBean.securityImpactWork.closed &&
        (!requestScope.actionBean.edit || requestScope.actionBean.isUserExpert)}">
        /**
         * Show hide the list of risks.
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
         * Display the list of select risks.
         */
        $('#checkboxes input').change(function (e) {
            e.stopPropagation();
            if (e.currentTarget.checked) {
                selectedRisks.push({id: e.target.id, name: e.target.name, value: e.target.value});
            } else {
                findAndRemove(selectedRisks, 'id', e.target.id);
            }

            if (selectedRisks.length == 0) {
                $('#selectedRisks').html('<fmt:message key="security.impact.work.select.risks"/>');
                $('#workRisks').val("");
            } else if (selectedRisks.length == 1) {
                $('#selectedRisks').html(selectedRisks[0].name);
                $('#workRisks').val(selectedRisks[0].id);
            } else {
                $('#selectedRisks').html("");
                $('#workRisks').val("");
                for (var i = 0; i < selectedRisks.length; i++) {
                    $('#selectedRisks').append(selectedRisks[i].name);
                    $('#workRisks').val($('#workRisks').val() + ", " + selectedRisks[i].id);
                    if (i + 1 < selectedRisks.length) {
                        $('#selectedRisks').append(", ");
                    }
                }
            }
        });
        </c:if>
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
    });

    /**
     * Remove risks from the list
     *
     * @param array     The list of risks
     * @param property  The property to compare
     * @param value     The value to remove
     **/
    function findAndRemove(array, property, value) {
        $.each(array, function (index, result) {
            if (result[property] == value) {
                array.splice(index, 1);
                return false;
            }
        });
    }

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
</script>