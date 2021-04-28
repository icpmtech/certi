<%@ include file="../../../includes/taglibs.jsp" %>

<div id="template10procedureFragram">

    <s:form beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanViewTemplateActionBean" focus=""
            class="searchBox" id="searchForm" method="GET">
        <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>

        <table>
            <tr>
                <td colspan="2">
                    <s:select id="procedureFirstList" name="procedureFilters[0]" style="width: 312px;">
                        <s:options-collection collection="${requestScope.actionBean.procedureFirstList}"
                                              value="path"
                                              label="name"/>
                    </s:select>

                    <s:select id="procedureSecondList" name="procedureFilters[1]" style="width: 312px;">
                        <s:options-collection collection="${requestScope.actionBean.procedureSecondList}"
                                              value="path"
                                              label="name"/>
                    </s:select>

                    <s:select id="procedureThirdList" name="procedureFilters[2]" style="width: 312px;">
                        <s:options-collection collection="${requestScope.actionBean.procedureThirdList}"
                                              value="path"
                                              label="name"/>
                    </s:select>
                </td>
            </tr>
        </table>
        <input name="_eventName" value="viewTemplate10Procedure" type="hidden"/>
        <input id="folderPath" value="${requestScope.actionBean.folder.path}" type="hidden"/>
    </s:form>

    <c:if test="${requestScope.firstLoad == null}">
        <c:set var="includeFilename">planView${requestScope.actionBean.folder.template.name}.jsp</c:set>
        <div class="peiContent cleaner" style="margin-top:10px">
            <jsp:include page='<%= (String)pageContext.getAttribute("includeFilename") %>'/>
        </div>
    </c:if>
</div>

<script type="text/javascript">

    <c:if test="${requestScope.firstLoad}">
    var params = "";
    <c:forEach items="${requestScope.actionBean.procedureFilters}" var="procedureFilter" varStatus="i">
    params += "&procedureFilters[${i.index}]=" + '${pageScope.procedureFilter}';
    </c:forEach>
    if (params != '') {
        params += '&folder.path=' + $('#folderPath').val();
        params = encodeURI(params);
        $.get('${pageContext.request.contextPath}'
                + '/plan/PlanViewTemplate.action?planModuleType=${requestScope.actionBean.planModuleType}&viewTemplate10ProcedureFragment=', params, function(data) {
            $('#template10procedureFragram').html(data);
        });
    }
    </c:if>

    checkTemplateProcedureSelects();
    attachEventToDropDownTemplateProcedure('${pageContext.request.contextPath}', 'procedureFirstList', 1, '${requestScope.actionBean.planModuleType}');
    attachEventToDropDownTemplateProcedure('${pageContext.request.contextPath}', 'procedureSecondList', 2, '${requestScope.actionBean.planModuleType}');
    attachEventToDropDownTemplateProcedure('${pageContext.request.contextPath}', 'procedureThirdList', 3, '${requestScope.actionBean.planModuleType}');
</script>