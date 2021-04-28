<%@ include file="../../../includes/taglibs.jsp" %>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean" id="searchForm" focus=""
        class="searchBox" method="GET">
    <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>

    <fieldset style="margin-bottom:10px">
        <legend>
            <fmt:message key="common.search"/>
        </legend>
        <table>
            <tr>
               <td class="first">
                    <s:label for="productSort"><fmt:message key="pei.template.8RiskAnalysis.product"/>: </s:label>
                    <s:select id="productSort" name="riskAnalysisElementToFilter.product" style="width: 100px;">
                        <s:options-collection collection="${requestScope.actionBean.products}"/>
                    </s:select>

                    <s:label for="releaseConditionsSort"><fmt:message
                            key="pei.template.8RiskAnalysis.releaseConditions"/>: </s:label>
                    <s:select id="releaseConditionsSort" name="riskAnalysisElementToFilter.releaseConditions"
                              style="width: 324px;">
                        <s:options-collection collection="${requestScope.actionBean.releaseConditions}"/>
                    </s:select>

                    <s:label for="weatherSort"><fmt:message
                            key="pei.template.8RiskAnalysis.weather"/>: </s:label>
                    <s:select id="weatherSort" name="riskAnalysisElementToFilter.weather" style="width: 100px;">
                        <s:options-collection collection="${requestScope.actionBean.weathers}"/>
                    </s:select>
                </td>
               <td class="last">
                    <s:submit name="viewResource" id="searchButton" class="searchButton"><fmt:message
                            key="common.search"/></s:submit>
                </td>
            </tr>
        </table>
    </fieldset>

    <display:table list="${requestScope.actionBean.riskAnalysis}" export="true" id="displaytag"
                   excludedParams="viewTemplate8RiskAnalysis __fp _sourcePage" uid="risk"
                   class="displaytag" requestURI="/plan/Plan.action">
        <display:column property="product" titleKey="pei.template.8RiskAnalysis.product" escapeXml="true"
                        sortable="true"/>
        <display:column property="releaseConditions" titleKey="pei.template.8RiskAnalysis.releaseConditions"
                        escapeXml="true" sortable="true"/>
        <display:column property="weather" titleKey="pei.template.8RiskAnalysis.weather" escapeXml="true"
                        sortable="true"/>
        <display:column property="ignitionPoint" titleKey="pei.template.8RiskAnalysis.ignitionPoint"
                        escapeXml="true" sortable="true"/>
        <display:column property="radiation" titleKey="pei.template.8RiskAnalysis.radiation" escapeXml="true"
                        sortable="true"/>
        <display:column property="pressurized" titleKey="pei.template.8RiskAnalysis.pressurized"
                        escapeXml="true" sortable="true"/>
        <display:column property="toxicity" titleKey="pei.template.8RiskAnalysis.toxicity" escapeXml="true"
                        sortable="true"/>
        <display:column titleKey="pei.template.6DocumentsElement.files.attach" media="html"
                        style="text-align:center;">
            <c:if test="${pageScope.risk.fileFolderLinksLists != null && !empty pageScope.risk.fileFolderLinksLists}">
                <c:forEach items="${pageScope.risk.fileFolderLinksLists}" var="link">
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
                            event="viewResource" style="">
                        <s:param name="path" value="${pageScope.link}"/>
                        <s:param name="peiId" value="${requestScope.actionBean.peiId}"/>
                        <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
                        <img src="${pageContext.request.contextPath}/images/Artigo-Ajuda.png"
                             alt="<fmt:message key="pei.template.6DocumentsElement.files.attach"/>"/>
                    </s:link>
                    <br>
                </c:forEach>
            </c:if>
        </display:column>
        <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
        <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
        <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
        <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
    </display:table>

    <s:hidden name="path" value="${requestScope.actionBean.folder.path}"/>
    <s:hidden name="peiId" value="${requestScope.actionBean.peiId}"/>
</s:form>

<script type="text/javascript">

    function attachEventToRiskAnalysisProduct(contextPath) {
        $('#productSort').change(function() {
            //Only update when is not branck
            if ($(this).val() != '') {
                $.get(contextPath
                        + '/plan/PlanViewTemplate.action?planModuleType=${requestScope.actionBean.planModuleType}&viewTemplate8RiskAnalysisLoadFiltersList=', {'folder.path': '${requestScope.actionBean.folder.path}','riskAnalysisElementToLoad.product': $(this).val()}, function(
                        j) {
                    var result = eval(j);
                    var options = '';

                    for (var i = 0; i < result.length; i++) {
                        options += '<option value="' + result[i] + '">' + result[i] + '</option>';
                    }
                    $('#releaseConditionsSort').removeAttr("disabled");
                    $('#releaseConditionsSort').html(options);
                });
            } else {
                $('#releaseConditionsSort').html('<option value=""/>');
                $('#releaseConditionsSort').attr("disabled", "disabled");
            }
            $('#weatherSort').html('<option value=""/>');
            $('#weatherSort').attr("disabled", "disabled");
        });
    }

    function attachEventToRiskAnalysisReleaseConditions(contextPath) {
        $('#releaseConditionsSort').change(function() {
            if ($(this).val() != '') {
                $.get(contextPath
                        + '/plan/PlanViewTemplate.action?planModuleType=${requestScope.actionBean.planModuleType}&viewTemplate8RiskAnalysisLoadFiltersList=', {'folder.path': '${requestScope.actionBean.folder.path}','riskAnalysisElementToLoad.product': $('#productSort').val(), 'riskAnalysisElementToLoad.releaseConditions': $(this).val()}, function(
                        j) {
                    var result = eval(j);
                    var options = '';

                    for (var i = 0; i < result.length; i++) {
                        options += '<option value="' + result[i] + '">' + result[i] + '</option>';
                    }
                    $('#weatherSort').removeAttr("disabled");
                    $('#weatherSort').html(options);
                });
            } else {
                $('#weatherSort').html('<option value=""/>');
                $('#weatherSort').attr("disabled", "disabled");
            }
        });
    }


    function checkRiskAnalysisSelects() {
        if ($("#releaseConditionsSort option").length == 1 && $('#releaseConditionsSort').val() == '') {
            $('#releaseConditionsSort').attr("disabled", "disabled");
        }
        //alert($("#weatherSort option").length)
        if ($("#weatherSort option").length == 1 && $('#weatherSort').val() == '') {
            $('#weatherSort').attr("disabled", "disabled");
        }
    }

    attachEventToRiskAnalysisProduct('${pageContext.request.contextPath}');
    attachEventToRiskAnalysisReleaseConditions('${pageContext.request.contextPath}');
    checkRiskAnalysisSelects();
</script>