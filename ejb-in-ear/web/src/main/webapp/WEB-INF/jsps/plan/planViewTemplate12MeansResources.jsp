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
                    <s:label for="contentList"><fmt:message key="pei.template.12MeansResourcesElement.resourceType"/>: </s:label>
                    <s:select name="template12ResourceType" style="width: 250px;" id="contentList">
                        <s:options-collection collection="${requestScope.actionBean.resourcesTypes}"/>
                    </s:select>
                    <s:text name="template12SearchPhrase" class="viewTemplateSearchInput"/>
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
               excludedParams="viewTemplate12MeansResources __fp _sourcePage" uid="folder"
               class="displaytag peiViewTemplate12MeansResources" requestURI="/plan/Plan.action" defaultsort="1">
    <display:column property="template.resourceName" titleKey="pei.template.12MeansResourcesElement.resourceName"
                    escapeXml="true" sortable="true"/>
    <display:column property="template.resourceType" titleKey="pei.template.12MeansResourcesElement.resourceType"
                    escapeXml="true" sortable="true"/>
    <display:column property="template.entityName" titleKey="pei.template.12MeansResourcesElement.entityName"
                    escapeXml="true" sortable="true"/>
    <display:column property="template.quantity" titleKey="pei.template.12MeansResourcesElement.quantity"
                    escapeXml="true" sortable="true"/>
    <display:column property="template.characteristics" titleKey="pei.template.12MeansResourcesElement.characteristics"
                    escapeXml="true" sortable="true"/>

    <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
    <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
    <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
    <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
</display:table>

