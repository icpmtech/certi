<%@ include file="../../../includes/taglibs.jsp" %>

<p class="headerExport">
    <fmt:message key="security.delete.form.header"/>
</p>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
        class="planExportForm" method="POST" id="smDeleteForm">
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
    </p>

    <div style="font-size:10px;">
        <fmt:message key="security.export.form.info"/>
    </div>
    <p class="warning" style="color:#E10000;margin-right:5px">
        <fmt:message key="security.delete.warning"/>
    </p>

    <div class="formButtons" style="padding-bottom:5px">
        <s:button name="delete" class="button buttonOrange" id="delete"><fmt:message
                key="common.delete"/></s:button>
        <s:button name="" class="button" id="closeDelete"><fmt:message key="common.cancel"/></s:button>
    </div>
    <input type="hidden" name="_eventName" value="delete"/>
    <s:hidden name="contractId">${requestScope.actionBean.contractId}</s:hidden>

</s:form>

<script type="text/javascript">

    /* Close modal on cancel click*/
    $('#closeDelete').click(function () {
        $.modal.close();
    });

    $('#delete').click(function () {
        if (confirm('<fmt:message key="security.delete.confirm"/>')) {
            $('#smDeleteForm').submit();
            $.modal.close();
        }
    });

</script>
