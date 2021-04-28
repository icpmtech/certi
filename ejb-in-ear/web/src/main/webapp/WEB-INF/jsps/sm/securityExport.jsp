<%@ include file="../../../includes/taglibs.jsp" %>

<p class="headerExport">
    <fmt:message key="security.export.form.header"/>
</p>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
        class="planExportForm" method="POST" id="smExportForm">
    <p>
        <label class="fieldLabel"><fmt:message key="security.view.filter.status"/>:</label>
        <label><fmt:message key="security.export.form.closed"/></label>
        <s:checkbox checked="true" id="closed" name="closed"/>
    </p>
    <p>
        <label class="fieldLabel"><fmt:message key="security.view.filter.year"/> (*):</label>
        <s:select name="filterYear" style="width: 150px;" id="filterYear">
            <s:option id="allYears" value="-1"><fmt:message key="security.export.form.allYears"/></s:option>
            <c:forEach items="${requestScope.actionBean.years}" var="year">
                <s:option id="year-${year}" value="${year}">
                    ${year}
                </s:option>
            </c:forEach>
        </s:select>

        <s:select name="filterSemester" style="width: 150px; margin-left: 70px; margin-top: 5px; display: none;" id="filterSemester">
            <s:option id="allSemesters" value="-1"><fmt:message key="security.export.form.allSemesters"/></s:option>
            <s:option id="firstSemester" value="1"><fmt:message key="security.export.form.firstSemester"/></s:option>
            <s:option id="secondSemester" value="2"><fmt:message key="security.export.form.secondSemester"/></s:option>
        </s:select>
    </p>

    <div style="font-size:10px;">
        <fmt:message key="security.export.form.info"/>
    </div>
    <p class="warning">
        <fmt:message key="security.export.warning"/>
    </p>
    <div class="formButtons" style="padding-bottom:5px">
        <s:button name="export" class="button" id="export"><fmt:message key="common.export"/></s:button>
        <s:button name="" class="button" id="close"><fmt:message key="common.cancel"/></s:button>
    </div>
    <input type="hidden" name="_eventName" value="export"/>
    <s:hidden name="contractId">${requestScope.actionBean.contractId}</s:hidden>

</s:form>

<script type="text/javascript">

    $('#filterYear').change(function () {
        if ($('#filterYear option:selected').val() === '-1') {
            $('#filterSemester').hide();
        } else {
            $('#filterSemester').show();
        }
    });

    /* Close modal on cancel click*/
    $('#close').click(function () {
        $.modal.close();
    });

    $('#export').click(function () {
        $('#smExportForm').submit();
        $.modal.close();
    });

</script>
