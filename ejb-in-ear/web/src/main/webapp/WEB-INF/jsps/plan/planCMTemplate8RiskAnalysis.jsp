<%@ include file="../../../includes/taglibs.jsp" %>

<s:form partial="true" beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMOperationsActionBean">
    <div class="template-cm-padding">
        <div class="form" style="width:100%">

            <p>
                <s:label for="fileTemplate" class="labelFileUploadTemplate">
                    <fmt:message key="pei.cm.template.file"/> (*):
                </s:label>
                <s:file name="fileTemplate8RiskAnalysis" id="fileTemplate" class="fileInput"/>
                <span class="recomended-style">(<fmt:message key="common.recommended.fileType"/>: .csv)
                </span>
            </p>

            <c:if test="${requestScope.actionBean.folder.template.riskAnalysis != null && fn:length(requestScope.actionBean.folder.template.riskAnalysis) > 0}">
                <p class="warningReplaceFile-pei">
                    (<fmt:message key="legislation.add.replaceFile"/>)
                </p>
            </c:if>

            <p class="mandatoryFields-pei">
                <fmt:message key="common.mandatoryfields"/>
            </p>
        </div>
    </div>
</s:form>