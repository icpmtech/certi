<%@ include file="../../../includes/taglibs.jsp" %>

<head>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/modal/jquery.simplemodal-1.2.3.js"></script>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/modal/modal.css"/>
</head>

<title><fmt:message key="menu.emergency"/> &gt; <fmt:message key="menu.pei.copy"/></title>

<h1><fmt:message key="menu.pei.copy"/></h1>

<s:errors/>
<s:messages/>

<div id="modalBoxCopyPEI" class="modalBox">
    <div class="header"><fmt:message key="pei.copy.waitPanel.header"/></div>
    <p><img src="${pageContext.request.contextPath}/images/ajax-loader.gif" alt="<fmt:message key="common.loading"/>"/>
    </p>

    <p><fmt:message key="pei.publish.waitPanel.footer"/></p>
</div>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMCopyActionBean"
        class="form-pei-admin-select-pei" style="width: 755px;">
    <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>

    <h2 style="width:inherit;"><span><fmt:message key="pei.copy.source"/></span></h2>

    <p class="floatLeft">
        <s:label for="companiesS"><fmt:message key="pei.entity"/>:</s:label>
        <s:select id="companiesS" name="companySourceId" class="floatLeft" style="width:250px">
            <s:options-collection collection="${requestScope.actionBean.companiesSource}" label="name" value="id"/>
        </s:select>

        <s:label for="contractsS" style="margin-left: 40px"><fmt:message key="pei.contract"/>:</s:label>
        <s:select id="contractsS" name="contractSourceId" class="floatLeft" style="width:250px">
            <s:options-collection collection="${requestScope.actionBean.contractsSource}" label="contractDesignation"
                                  value="id"/>
        </s:select>
    </p>

    <div class="cleaner"><!-- do not remove div--></div>

    <h2 style="width:inherit"><span><fmt:message key="pei.copy.target"/></span></h2>

    <p class="floatLeft">
        <s:label for="companiesT"><fmt:message key="pei.entity"/>:</s:label>
        <s:select id="companiesT" name="companyTargetId" class="floatLeft" style="width:250px">
            <s:options-collection collection="${requestScope.actionBean.companiesTarget}" label="name" value="id"/>
        </s:select>

        <s:label for="contractsT" style="margin-left: 40px"><fmt:message key="pei.contract"/>:</s:label>
        <s:select id="contractsT" name="contractTargetId" class="floatLeft" style="width:250px">
            <s:options-collection collection="${requestScope.actionBean.contractsTarget}" label="contractDesignation"
                                  value="id"/>
        </s:select>
    </p>

    <div class="cleaner"><!-- do not remove div--></div>

    <p class="warning" style="color:#E10000;">
        <fmt:message key="pei.copy.warning2"/>
    </p>

    <p class="formButtons">
        <s:submit name="copyPEI" id="copyPEI" class="button confirmCopy buttonOrange"><fmt:message
                key="common.copy"/></s:submit>
    </p>

</s:form>

<script type="text/javascript">
    function attachOnChangeToCompanies(url, isSource) {
        var sourceTarget = "";
        if (isSource) {
            sourceTarget = "S";
        } else {
            sourceTarget = "T";
        }

        $('#companies' + sourceTarget).change(function() {
            $.get(url, {companyId: $(this).val()}, function(j) {
                var result = eval(j);
                var options = '';

                for (var i = 0; i < result.length; i++) {
                    options += '<option value="' + result[i].id + '">' + result[i].contractDesignation + '</option>';
                }

                $('#contracts' + sourceTarget).html(options);
            });
        });
    }

    $(document).ready(function() {
        attachOnChangeToCompanies('${pageContext.request.contextPath}'
                + '/plan/PlanCM.action?planModuleType=${requestScope.actionBean.planModuleType}&loadCompanyContracts=', true);

        attachOnChangeToCompanies('${pageContext.request.contextPath}'
                + '/plan/PlanCM.action?planModuleType=${requestScope.actionBean.planModuleType}&loadCompanyContracts=', false);

        $("#copyPEI").click(function () {
            var value = confirm('<fmt:message key="pei.copy.warning"/>');
            if (value) {
                $('#modalBoxCopyPEI').modal();
            }
            return value;
        });

    });
</script>
