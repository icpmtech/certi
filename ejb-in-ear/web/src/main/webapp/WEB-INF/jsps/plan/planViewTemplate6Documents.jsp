<%@ include file="../../../includes/taglibs.jsp" %>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean" focus="" class="searchBox"
        method="GET">
    <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>

    <fieldset>
        <legend>
            <fmt:message key="common.search"/>
        </legend>
        <table>
            <tr>
                <td class="first">
                    <s:label for="contentList"><fmt:message key="pei.template.6DocumentsElement.type"/>: </s:label>
                    <s:select name="documentElementFilter.contentType" style="width: 360px;" id="contentList">
                        <s:options-collection collection="${requestScope.actionBean.documentsTypes}"/>
                    </s:select>

                    <s:label for="subContentList"><fmt:message
                            key="pei.template.6DocumentsElement.subtype"/>: </s:label>
                    <s:select name="documentElementFilter.contentSubType" style="width:360px;" id="subContentList">
                        <s:options-collection collection="${requestScope.actionBean.documentsSubTypes}"/>
                    </s:select>
                </td>
                <td class="last">
                    <s:submit name="viewResource" id="searchButton" class="searchButton"><fmt:message
                            key="common.search"/></s:submit>
                </td>
            </tr>
        </table>
    </fieldset>

    <s:hidden name="path" value="${requestScope.actionBean.folder.path}"/>
    <s:hidden name="peiId" value="${requestScope.actionBean.peiId}"/>
</s:form>

<display:table list="${requestScope.actionBean.folders}" export="true" id="displaytag"
               excludedParams="viewTemplate6Documents __fp _sourcePage" uid="folder"
               class="displaytag peiViewTemplate6Documents" requestURI="/plan/Plan.action" defaultsort="1">
    <display:column property="template.contentName" titleKey="pei.template.6DocumentsElement.name"
                    escapeXml="true"
                    sortable="true"/>
    <display:column property="template.contentType" titleKey="pei.template.6DocumentsElement.type"
                    escapeXml="true"
                    sortable="true"/>
    <display:column property="template.contentSubType" titleKey="pei.template.6DocumentsElement.subtype"
                    escapeXml="true" sortable="true"/>
    <display:column titleKey="pei.template.6DocumentsElement.date"
                    sortable="true" sortProperty="template.contentDate">
        <fmt:formatDate value="${pageScope.folder.template.contentDate}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>
    <display:column titleKey="pei.template.6DocumentsElement.files.attach" media="html"
                    style="text-align:left;">
        <c:if test="${pageScope.folder.template.resources != null}">
            <c:forEach items="${pageScope.folder.template.resources}" var="resource" varStatus="j">
                <p>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
                            event="viewResource" class="download-file" style="height:10px">
                        <s:param name="path" value="${pageScope.folder.path}"/>
                        <s:param name="order" value="${j.index}"/>
                        <s:param name="peiId" value="${requestScope.actionBean.peiId}"/>
                        <s:param name="planModuleType" value="${requestScope.actionBean.planModuleType}"/>
                        <c:choose>
                            <c:when test="${resource.alias != null && !empty resource.alias}">
                                <c:out value="${pageScope.resource.alias}"/>
                            </c:when>
                            <c:otherwise>
                                <c:out value="${pageScope.resource.name}"/>
                            </c:otherwise>
                        </c:choose>
                    </s:link>
                </p>
            </c:forEach>
        </c:if>
        <c:if test="${pageScope.folder.template.links != null}">
            <c:forEach items="${pageScope.folder.template.links}" var="link">
                <p>
                    <s:link href="${pageScope.link.href}" class="link" target="_blank">
                        <c:out value="${pageScope.link.alias}"/>
                    </s:link>
                </p>
            </c:forEach>
        </c:if>
    </display:column>
    <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
    <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
    <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
    <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
</display:table>

<script type="text/javascript">

    function attachEventToDocumentElementType(contextPath) {
        $('#contentList').change(function() {
            //Only update when is not branck
            if ($(this).val() != '') {
                $.get(contextPath
                        + '/plan/PlanViewTemplate.action?planModuleType=${requestScope.actionBean.planModuleType}&viewTemplate6DocumentsLoadFiltersList=', {'folder.path': '${requestScope.actionBean.folder.path}','documentElementFilter.contentType': $(this).val()}, function(
                        j) {
                    var result = eval(j);
                    var options = '';

                    for (var i = 0; i < result.length; i++) {
                        options += '<option value="' + result[i] + '">' + result[i] + '</option>';
                    }
                    $('#subContentList').removeAttr("disabled");
                    $('#subContentList').html(options);
                });
            } else {
                $('#subContentList').html('<option value=""/>');
                $('#subContentList').attr("disabled", "disabled");
            }
        });

        if ($("#subContentList option").length == 1 && $('#subContentList').val() == '') {
            $('#subContentList').attr("disabled", "disabled");
        }
    }
    attachEventToDocumentElementType('${pageContext.request.contextPath}');
</script>