<%@ include file="../../../includes/taglibs.jsp" %>

<p class="headerExport">
    <fmt:message key="pei.export.form.header"/>
</p>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMOperationsActionBean"
        class="planExportForm" method="post" id="planExportForm">
    <p>
        <label class="fieldLabel"><fmt:message key="pei.export.form.version"/>:</label>
        <s:radio value="true" name="exportOnline" id="planOnline"/>
        <s:label for="planOnline" id="planOnlineLabel"><fmt:message key="common.published"/></s:label>

        <s:radio value="false" name="exportOnline" id="planOffline"/>
        <s:label for="planOffline"><fmt:message key="common.notpublished"/></s:label>
    </p>

    <p>
        <label class="fieldLabel"><fmt:message key="pei.export.form.format"/>:</label>

        <c:if test="${requestScope.actionBean.templateDocxes != null && fn:length(requestScope.actionBean.templateDocxes) >0}">
            <c:if test="${applicationScope.configuration.docxExportEnabled}">
                <s:radio value="1" name="exportDocx" class="docx" id="docx"/>
                <s:label for="docx"><fmt:message key="common.docx"/></s:label>
            </c:if>

            <s:radio value="2" name="exportDocx" class="docx" id="doc"/>
            <s:label for="doc"><fmt:message key="common.doc"/></s:label>
        </c:if>

        <s:radio value="0" name="exportDocx" id="pdfRft"/>
        <s:label for="pdfRft"><fmt:message key="common.pdfRft"/></s:label>
    </p>

    <c:if test="${requestScope.actionBean.templateDocxes != null && fn:length(requestScope.actionBean.templateDocxes) >0}">
        <p id="templateSelect">
            <s:label class="fieldLabel" for="templateDocxField"><fmt:message key="pei.export.form.template"/>:</s:label>
            <s:select name="templateDocx.id" id="templateDocxField" style="width: 280px;">
                <s:options-collection collection="${requestScope.actionBean.templateDocxes}" label="title" value="id"/>
            </s:select>
        </p>
    </c:if>

    <c:if test="${requestScope.actionBean.templateDocxes == null || fn:length(requestScope.actionBean.templateDocxes) == 0}">
        <p class="mandatoryFields">
            <fmt:message key="pei.export.form.exportDocxDisable"/>
        </p>
    </c:if>

    <div class="formButtons" style="padding-bottom:5px">
        <s:button name="export" class="button" id="export"><fmt:message key="common.export"/></s:button>
        <s:button name="" class="button" id="close"><fmt:message key="common.cancel"/></s:button>
    </div>
    <input type="hidden" name="_eventName" value="export"/>
    <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>
    <s:hidden name="exportId"/>
</s:form>

<script type="text/javascript">
    <c:if test="${!requestScope.actionBean.exportOnline}">
    /* Hide export online option*/
    $('#planOnline').hide();
    $('#planOnlineLabel').hide();
    /* Select offline checkbox*/
    $('#planOffline').attr("checked", "checked");
    </c:if>

    /* Close modal on cancel click*/
    $('#close').click(function () {
        $.modal.close();
    });

    <c:if test="${requestScope.actionBean.templateDocxes != null && fn:length(requestScope.actionBean.templateDocxes) >0}">
    $('#pdfRft').click(function () {
        $('#templateSelect').hide();
    });

    $('.docx').click(function () {
        $('#templateSelect').show();
    });

    </c:if>

    $('#close').click(function () {
        $.modal.close();
    });

    $('#export').click(function () {
        $('#planExportForm').submit();
        $.modal.close();
    });

</script>
