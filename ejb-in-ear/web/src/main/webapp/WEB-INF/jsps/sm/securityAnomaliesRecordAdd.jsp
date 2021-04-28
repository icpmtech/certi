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
        <c:if test="${!requestScope.actionBean.anomaly.closed}">
            <jsp:include page="securityOpenActionsList.jsp"/>
        </c:if>
    </div>
</c:if>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
        class="form leftSideContent" focus="" style="width: 100%;">
    <input type="hidden" name="anomaly.id" value="${requestScope.actionBean.anomalyId}">
    <input type="hidden" name="isAnomaly" value="${requestScope.actionBean.isAnomaly}">
    <s:errors/>
    <s:messages/>
    <c:if test="${requestScope.actionBean.edit}">
        <p>
            <s:label for="code" class="addPaddingBottom"><fmt:message key="security.actions.planning.code"/>:</s:label>
            <span id="code" name="anomaly.id" class="mediumSpan">${requestScope.actionBean.anomaly.code}</span>
        </p>
    </c:if>
    <p>
        <s:label for="detected-date"><fmt:message key="security.anomaly.date"/> (*):</s:label>
        <s:text id="detected-date" name="anomaly.datetime" class="dateInput isExpert" disabled="${pageScope.isExpert}"/>
    </p>
    <p>
        <s:label for="name"><fmt:message key="security.anomaly.name"/>:</s:label>
        <s:text id="name" name="anomaly.name" class="madiumInput isExpert" disabled="${pageScope.isExpert}"/>
    </p>
    <c:choose>
        <c:when test="${requestScope.actionBean.isAnomaly}">
            <p>
                <s:label for="whoDetected"><fmt:message key="security.anomaly.who.detected"/> (*):</s:label>
                <s:text id="whoDetected" name="anomaly.whoDetected" class="mediumInput isExpert"
                        disabled="${pageScope.isExpert}"/>
            </p>
        </c:when>
        <c:otherwise>
            <p>
                <s:label for="internalActors"><fmt:message key="security.occurrence.internal.actors"/> (*):</s:label>
                <s:text id="internalActors" name="anomaly.internalActors" class="mediumInput isExpert"
                        disabled="${pageScope.isExpert}"/>
            </p>

            <p>
                <s:label for="externalActors"><fmt:message key="security.occurrence.external.actors"/> (*):</s:label>
                <s:text id="externalActors" name="anomaly.externalActors" class="mediumInput isExpert"
                        disabled="${pageScope.isExpert}"/>
            </p>
        </c:otherwise>
    </c:choose>
    <p>
        <s:label for="description"><fmt:message key="security.actions.planning.description"/> (*):</s:label>
        <s:textarea id="description" name="anomaly.description" class="mediumInput isExpert"
                    disabled="${pageScope.isExpert}" rows="6"/>
    </p>
    <c:choose>
        <c:when test="${requestScope.actionBean.edit}">
            <h2 class="form cleaner"><span></span></h2>

            <p>
                <s:label for="securityImpact"><fmt:message key="security.anomaly.securityImpact"/> (*):</s:label>
                <s:select class="selectSecurityImpact isBasic" style="width: 190px; height: 23px;"
                          id="securityImpact" name="anomaly.securityImpact.id">
                    <c:forEach items="${requestScope.actionBean.securityImpacts}" var="sec">
                        <s:option
                                id="securityImpact-${sec.id}"
                                value="${sec.id}">
                            ${sec.name}
                        </s:option>
                    </c:forEach>
                </s:select>
            </p>
            <c:if test="${!requestScope.actionBean.isAnomaly}">
                <p>
                    <s:label for="qualifiedEntity"><fmt:message
                            key="security.occurrence.qualifiedEntity"/> (*):</s:label>
                    <s:text id="qualifiedEntity" name="anomaly.qualifiedEntity" class="mediumInput isBasic"/>
                </p>
            </c:if>
            <p class="spaceField">
            <s:label for="file" class="documentsLabel"><fmt:message key="security.docs"/> :</s:label>
            <c:choose>
                <c:when test="${(requestScope.actionBean.closed || !requestScope.actionBean.isUserIntermediate) &&
                empty requestScope.actionBean.anomaly.documents}">
                    <span id="emptyDocs" class="mediumSpan"><fmt:message key="security.common.no.documents"/></span>
                </c:when>
                <c:otherwise>
                    <c:if test="${not empty requestScope.actionBean.anomaly.documents}">
                        <ul class="fileList">
                            <c:forEach items="${requestScope.actionBean.anomaly.documents}" var="document">
                                <li>
                                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
                                            event="getDocument" class="download-file">
                                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                                        <s:param name="anomalyId" value="${requestScope.actionBean.anomaly.id}"/>
                                        <s:param name="documentId" value="${document.id}"/>
                                        <c:out value="${document.displayName}"/>
                                    </s:link>
                                    <c:if test="${(requestScope.actionBean.isUserIntermediate || requestScope.actionBean.isUserExpert)
                                            && !requestScope.actionBean.anomaly.closed}">
                                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"
                                                event="deleteDocument" class="confirmDelete deleteIcon">
                                            <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                                            <s:param name="anomalyId" value="${requestScope.actionBean.anomaly.id}"/>
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
                    && (fn:length(requestScope.actionBean.anomaly.documents) < 6)}">
                <s:file name="newAttachments[0]" id="file-0" class="isBasic workAttachments"
                        style="margin-bottom:0px;"/>
                <s:label for="file-0" class="workAttachments"><fmt:message key="security.docs.name"/> :</s:label>
                <s:text id="file-name-0" name="attachmentName[0]" class="workAttachments"/>
                <c:forEach
                        begin="${(fn:length(requestScope.actionBean.anomaly.documents) == 0) ? 1 : fn:length(requestScope.actionBean.anomaly.documents)}"
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
                <s:text id="closed-date" name="anomaly.closedDate" class="dateInput isBasic"/>
            </p>

            <p>
                <s:label for="activityState"><fmt:message key="security.state"/>:</s:label>
                <c:choose>
                    <c:when test="${requestScope.actionBean.anomaly.closed == false}">
                        <fmt:message key="security.state.open"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="security.state.closed"/>
                        <%--<c:if test="${requestScope.actionBean.isUserExpert}">--%>
                        <%--<s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityAnomaliesRecordActionBean"--%>
                        <%--event="reopenAnomaly" class="confirmReopen deleteIcon" style="float:none;">--%>
                        <%--<s:param name="contractId" value="${requestScope.actionBean.contract.id}"/>--%>
                        <%--<s:param name="anomalyId" value="${requestScope.actionBean.anomaly.id}"/>--%>

                        <%--<img src="${pageContext.request.contextPath}/images/Eliminar.png"--%>
                        <%--title="<fmt:message key="common.delete"/>"--%>
                        <%--alt="<fmt:message key="common.delete"/>"/></s:link>--%>
                        <%--</c:if>--%>
                    </c:otherwise>
                </c:choose>
            </p>

            <div class="formButtons">
                <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"></s:param>
                <s:param name="anomalyId" value="${requestScope.actionBean.anomalyId}"></s:param>
                <s:submit name="${requestScope.actionBean.isAnomaly ? 'editAnomaly' : 'editOccurrence'}"
                          class="button isBasic"
                          style="${(!requestScope.actionBean.isUserIntermediate || requestScope.actionBean.anomaly.closed) ? 'background-color: #6992B8;':''}"
                ><fmt:message key="common.submit"/></s:submit>
                <c:if test="${requestScope.actionBean.isUserExpert && requestScope.actionBean.anomaly.closed}">
                    <s:submit name="reopenAnomaly" class="button confirmReopen"><fmt:message
                            key="security.common.reopen"/></s:submit>
                </c:if>
                <s:submit name="viewAnomalies" class="button isBasic"
                          style="${(!requestScope.actionBean.isUserIntermediate || requestScope.actionBean.anomaly.closed) ? 'background-color: #6992B8;':''}"
                ><fmt:message key="common.cancel"/></s:submit>
            </div>
        </c:when>
        <c:otherwise>
            <div class="formButtons">
                <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                <s:param name="contractId" value="${requestScope.actionBean.contractId}"></s:param>
                <s:submit name="${requestScope.actionBean.isAnomaly ? 'insertAnomaly' : 'insertOccurrence'}"
                          class="button"><fmt:message key="common.submit"/></s:submit>
                <s:submit name="viewAnomalies" class="button"><fmt:message key="common.cancel"/></s:submit>
            </div>
        </c:otherwise>
    </c:choose>
</s:form>
<script type="text/javascript">
    var attachments = ${fn:length(requestScope.actionBean.anomaly.documents)};
    var lastEnabledAttachmentInput = 0;
    var ATTACHMENT_MAX_SIZE = 5;
    $(function () {

        $('input[name=editAnomaly]').click(function () {
            $(".isExpert").removeAttr("disabled");
        });
        $('input[name=editOccurrence]').click(function () {
            $(".isExpert").removeAttr("disabled");
        });


        <c:if test="${requestScope.actionBean.edit && !requestScope.actionBean.isUserExpert}">
        $(".isExpert").attr("disabled", "true");
        </c:if>

        <c:if test="${requestScope.actionBean.anomaly.closed ||
        (requestScope.actionBean.edit && !requestScope.actionBean.isUserIntermediate)}">
        $(".isBasic").attr("disabled", "true");
        </c:if>

        <c:if test="${requestScope.actionBean.anomaly.securityImpact.id != null}">
        $("#type").attr("value", ${requestScope.actionBean.anomaly.securityImpact.id});
        </c:if>
        /**
         *  Date Picker
         */
        <c:if test="${(!requestScope.actionBean.edit || requestScope.actionBean.isUserExpert)
           && !requestScope.actionBean.anomaly.closed  }">
        $('#detected-date').datepicker({
            maxDate: 0,
            showOn: "button",
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="security.validityStartDate"/>'
        });
        $('#detected-date').css('float', 'none');
        </c:if>
        <c:if test="${requestScope.actionBean.edit && !requestScope.actionBean.anomaly.closed &&
        requestScope.actionBean.isUserIntermediate}">
        $('#closed-date').datepicker({
            maxDate: 0,
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="security.validityStartDate"/>'
        });
        $('#closed-date').css('float', 'none');
        </c:if>
        /**
         * Multiple file upload
         * */
        $('#file-0, #file-1, #file-2, #file-3, #file-4, #file-5').change(handleFileSelection);

        attachConfirmDelete("<fmt:message key="security.document.confirmDelete"/>");
        attachConfirmDeleteClass("confirmReopen", "<fmt:message key="security.anomaly.reopen.confirmReopen"/>");

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