<%@ include file="../../../includes/taglibs.jsp" %>

<div class="template-cm-padding">
    <div class="form" style="width:100%" id="planCMTemplate11Mirror">
        <img src="${pageContext.request.contextPath}/images/Editar.png" width="16" height="16"
             alt="<fmt:message key="common.edit"/>"
             title="<fmt:message key="common.edit"/>">
        <a href="#" id="linkToPlan">
            ${requestScope.pathToShow}
        </a>
        <br/>
        <br/>
        <!-- This option will remove the link copying the content! -->
        <a href="javascript:convertLink('<fmt:message key="pei.content.copy.message"/>', '${pageContext.request.contextPath}', '${requestScope.actionBean.planModuleType}');"
           class="operationDelete"
           id="deleteFolder">
            &nbsp;<fmt:message key="pei.content.copy.link"/></a>
    </div>
</div>

<script type="text/javascript">
    attachEventToTemplate11MirrorLink("${pageContext.request.contextPath}", "linkToPlan",
            "${requestScope.actionBean.folder.template.sourceContractId}",
            "${requestScope.actionBean.folder.template.sourcePath}",
            '${requestScope.actionBean.folder.template.moduleType}');

    function convertLink(text, url, moduleType) {
        var result = window.confirm(text);
        if (result) {
            window.location = url + "/plan/PlanCMOperations.action?planModuleType=" + moduleType
                    + "&convertLink=&folderId="
                    + getSelectedTreeNodePath(true);
        }
    }

</script>