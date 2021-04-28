<%@ include file="../../../includes/taglibs.jsp" %>

<s:form partial="true" beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMOperationsActionBean">
    <s:hidden name="planModuleType" id="planModuleTypeId">${requestScope.actionBean.planModuleType}</s:hidden>
    <div id="pei-admin-main">
        <div class="form" style="width:546px">
            <p>
                <s:label for="companiesFolderLink"><fmt:message key="pei.entity"/>:</s:label>
                <s:select name="companyId" id="companiesFolderLink" class="floatLeft" style="width:344px">
                    <s:options-collection collection="${requestScope.actionBean.companies}" label="name" value="id"/>
                </s:select>
            </p>

            <p>
                <s:label for="contractsFolderLink"><fmt:message key="pei.contract"/>:</s:label>
                <s:select name="contractId" id="contractsFolderLink" class="floatLeft" style="width:344px">
                    <s:options-collection collection="${requestScope.actionBean.contracts}" label="contractDesignation"
                                          value="id"/>
                </s:select>
            </p>

            <fieldset id="addFolderLinkTree" class="cleaner" style="padding: 10px 0 0 0">
                <legend>
                    <c:out value="${requestScope.actionBean.peiName}"/>
                </legend>
                <ul>
                    <li data="addClass: 'strong hidden'" id="${requestScope.actionBean.pei.path}"
                        class="folder expanded">
                        <c:out value="${requestScope.actionBean.pei.planName}"/>
                        <c:set var="lastDepth" value="1"/>
                        <ul>
                            <c:forEach items="${requestScope.actionBean.addFolderLinkTreeNodes}" var="treeNode"
                                       varStatus="i">

                            <c:choose>
                            <c:when test="${treeNode.depth > lastDepth}">
                            <ul>
                                </c:when>
                                <c:when test="${treeNode.depth < lastDepth}">
                                <c:forEach begin="${treeNode.depth}" end="${lastDepth-1}">
                    </li>
                </ul>
                </c:forEach>
                </li>
                </c:when>
                <c:when test="${!i.first}">
                    </li>
                </c:when>
                </c:choose>
                <c:choose>
                <c:when test="${treeNode.depth == 1}">
                <li data="addClass: 'strong'" id="${pageScope.treeNode.path}" class="folder expanded">
                        <c:out value="${pageScope.treeNode.name}"/>
                    </c:when>
                    <c:otherwise>
                <li id="${pageScope.treeNode.path}" class="folder">
                    <c:out value="${pageScope.treeNode.name}"/>
                    </c:otherwise>
                    </c:choose>
                    <c:set var="lastDepth" value="${treeNode.depth}"/>
                    <c:if test="${i.last}">

                    <c:forEach begin="1" end="${lastDepth-1}">
                </li>
                <!-- last special-->
                </ul>
                </c:forEach>
                </li>
                </c:if>

                </c:forEach>
                </ul>
                </ul>
            </fieldset>
            <div class="cleaner"><!-- do not remove--></div>
        </div>
    </div>
    <input type="hidden" name="template.sourcePath" id="folderIdSeletedFolderLinkTree"/>
    <s:hidden name="template.name"/>
    <s:hidden name="folder.name" value="temporaryFolderName"/>
    <s:hidden name="folder.order" value="1"/>
    <s:hidden name="insertFolderLink"/>
    <s:hidden name="insertFolderFlag" value="false"/>
</s:form>

<script type="text/javascript">
    $("#addFolderLinkTree").dynatree({
        minExpandLevel:1,
        onActivate: function(dtnode) {
            $('#folderIdSeletedFolderLinkTree').val(dtnode.data.key);
            /* Resize iframe*/
            iResizeInsideIFrame();
        },
        onExpand: function() {
            iResizeInsideIFrame();
        }
    });
    function attachEventToSelectBox(selectId, contextPath) {
        $('#' + selectId).change(function() {
            var params = "&companyId=" + $('#companiesFolderLink').val();
            if(selectId == "contractsFolderLink") {
               params += "&contractId=" + $('#contractsFolderLink').val();
            }
            params += "&folderId=" + getSelectedTreeNodePath(true);
            params += "&planModuleType=" + $('#planModuleTypeId').val();
            $.get(contextPath + '/plan/PlanCMOperations.action?updateFolderLinkFormInsert=' + params, function(data) {
                $('#templateProperty').html(data);
                iResizeInsideIFrame();
            });
        });
    }
    attachEventToSelectBox("companiesFolderLink", "${pageContext.request.contextPath}");
    attachEventToSelectBox("contractsFolderLink", "${pageContext.request.contextPath}");
</script>