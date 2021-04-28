<%@ include file="../../../includes/taglibs.jsp" %>

<!-- export modal box-->
<div id="exportModalBox" class="modalBox"><!-- --></div>

<!-- delete modal box-->
<div id="deleteModalBox" class="modalBox"><!-- --></div>

<div class="securityFrontOffice">
    <div class="infoPanel">

        <div id="frontOfficeDatepicker"></div>

        <c:forEach items="${requestScope.actionBean.upcomingEvents}" var="event" varStatus="i">
            <c:if test="${event.upcomingEventType == 'ACTIVITY'}">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                        event="activityPlanningEdit">
                    <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                    <s:param name="activityId" value="${event.id}"/>
                    <span class="eventContainer">
                        <p>
                            <c:out value="${event.name}"/>
                        </p>
                        <fmt:formatDate value="${event.dateScheduled}"
                                        pattern="${applicationScope.configuration.securityUpcomingDateHourPattern}"/>
                    </span>
                </s:link>
            </c:if>
            <c:if test="${event.upcomingEventType == 'MAINTENANCE'}">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityMaintenanceActionBean"
                        event="maintenanceEdit">
                    <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                    <s:param name="maintenanceId" value="${event.id}"/>
                    <span class="eventContainer">
                        <p>
                            <c:out value="${event.name}"/>
                        </p>
                        <fmt:formatDate value="${event.dateScheduled}"
                                        pattern="${applicationScope.configuration.securityUpcomingDateHourPattern}"/>
                    </span>
                </s:link>
            </c:if>
        </c:forEach>
        <p>
            <span class="color-label"><fmt:message
                    key="security.frontOffice.usedSize"/>:</span> ${requestScope.actionBean.usedSize}MB
        </p>

        <c:if test="${requestScope.actionBean.isUserExpert}">
            <div align="center">
                <input type="button" class="button" id="exportSM" name="exportSM" style="margin-left:0"
                       value="<fmt:message key="common.export"/>"/>
                <input type="button" class="button buttonOrange" id="deleteSM" name="deleteSM"
                       value="<fmt:message key="common.delete"/>"/>
            </div>
        </c:if>
    </div>

    <div class="imagePanel">
        <span class="imageContainer">
            <c:if test="${requestScope.actionBean.contract.smCoverPicture != null}">
                <img title="<fmt:message key="security.frontOffice.coverPicture"/>"
                     alt="<fmt:message key="security.frontOffice.coverPicture"/>"
                     src="${pageContext.request.contextPath}/sm/Security.action?getContractCoverPicture=&contractId=${requestScope.actionBean.contract.id}"/>
            </c:if>
            <c:if test="${requestScope.actionBean.isUserExpert}">
                <s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                        id="changeCoverPictureForm">
                    <s:file id="changeCoverPictureFileInput" name="coverPicture"/>
                    <s:param name="contractId" value="${requestScope.actionBean.contract.id}"/>
                    <s:submit name="frontOffice" id="changeCoverPictureFormInput" class="button"
                            ><fmt:message key="security.changeImage"/></s:submit>
                    <input type="button" name="changeCoverPictureFormButton" id="changeCoverPictureFormButton"
                           value="<fmt:message key="security.changeImage"/>" class="button"/>
                </s:form>
            </c:if>
        </span>
    </div>
</div>
<script type="text/javascript">
    $(function () {
        $('#frontOfficeDatepicker').datepicker({
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '<c:out value="${applicationScope.configuration.datePatternCalendar}"/>',
            buttonText: '<fmt:message key="statistics.filter.initDate"/>'
        });
    });

    $('#exportSM').click(function () {
        var params = "contractId=" + ${requestScope.actionBean.contractId};

        $.get("${pageContext.request.contextPath}/sm/Security.action?smExportForm=", params, function (data) {
            window.$('#exportModalBox').html(data);
            window.$('#exportModalBox').modal({
                close: true,
                closeHTML: '<a class="modalCloseImg" title="Close"></a>',
                containerCss: {height: 'auto'}
            });
        });
    });

    $('#deleteSM').click(function () {
        var params = "contractId=" + ${requestScope.actionBean.contractId};

        $.get("${pageContext.request.contextPath}/sm/Security.action?smDeleteForm=", params, function (data) {
            window.$('#deleteModalBox').html(data);
            window.$('#deleteModalBox').modal({
                close: true,
                closeHTML: '<a class="modalCloseImg" title="Close"></a>',
                containerCss: {height: 'auto'}
            });
        });
    });

    if (!$.browser.msie) { // for decent browser hide the file input because we can submit it by javascript
        $('#changeCoverPictureFileInput').hide();
        $('#changeCoverPictureFormInput').hide();
        $('#changeCoverPictureFormButton').click(function () {
            $('#changeCoverPictureFileInput').trigger('click');
        });
        $('#changeCoverPictureFileInput').change(function () {
            $('#changeCoverPictureFormInput').trigger('click');
        });
    } else {
        $('#changeCoverPictureFormButton').hide();
    }
</script>
