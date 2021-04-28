<%@ page import="com.criticalsoftware.certitools.presentation.util.export.Plan2Docx" %>
<%@ include file="../../../includes/taglibs.jsp" %>

<div id="pei-admin-main">

<c:choose>
    <c:when test="${requestScope.actionBean.insertFolderLink}">
        <h1 class="title"><fmt:message key="common.add"/> <fmt:message key="folder.linked"/></h1>
    </c:when>
    <c:when test="${requestScope.actionBean.insertFolderFlag}">
        <h1 class="title"><fmt:message key="common.add"/> <fmt:message key="folder"/></h1>
    </c:when>
    <c:otherwise>
        <h1 class="title"><c:out value="${requestScope.actionBean.folder.name}"/></h1>
    </c:otherwise>
</c:choose>

<div class="form" style="width:546px">
<p>
    <s:label for="peiFolderName"><fmt:message key="folder.label.name"/> (*):</s:label>
    <s:text id="peiFolderName" name="folder.name" class="mediumInput" maxlength="255"/>
</p>

<p>
    <s:label for="template"><fmt:message key="folder.name.content"/> (*):</s:label>
    <c:choose>
        <c:when test="${requestScope.actionBean.doNotShowTemplatesSelect == null || !requestScope.actionBean.doNotShowTemplatesSelect}">
            <select id="template" name="template.name" class="mediumInput">
                <option value="Template2Index"
                        <c:if test="${requestScope.actionBean.template.name == 'Template2Index'}">selected="selected"</c:if>>
                    <fmt:message key="pei.template.TEMPLATE_INDEX"/></option>
                <option value="TemplateResource"
                        <c:if test="${requestScope.actionBean.template.name == 'TemplateResource'}">selected="selected"</c:if>>
                    <fmt:message key="pei.template.TEMPLATE_RESOURCE"/></option>
                <optgroup label="<fmt:message key="pei.template.group.diagrams"/>">
                    <option value="Template1Diagram"
                            <c:if test="${requestScope.actionBean.template.name == 'Template1Diagram'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_DIAGRAM"/></option>
                    <option value="Template4PlanClickable"
                            <c:if test="${requestScope.actionBean.template.name == 'Template4PlanClickable'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_PLAN_CLICKABLE"/></option>
                </optgroup>
                <optgroup label="<fmt:message key="pei.template.group.rtf"/>">
                    <option value="Template3RichText"
                            <c:if test="${requestScope.actionBean.template.name == 'Template3RichText'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_RICH_TEXT"/></option>
                    <option value="Template9RichTextWithAttach"
                            <c:if test="${requestScope.actionBean.template.name == 'Template9RichTextWithAttach'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_RICH_TEXT_WITH_ATTACH"/></option>
                </optgroup>
                <optgroup label="<fmt:message key="pei.template.group.contacts"/>">
                    <option value="Template5Contacts"
                            <c:if test="${requestScope.actionBean.template.name == 'Template5Contacts'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_CONTACTS"/></option>
                    <option value="Template5ContactsElement"
                            <c:if test="${requestScope.actionBean.template.name == 'Template5ContactsElement'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_CONTACTS_ELEMENT"/></option>
                </optgroup>
                <optgroup label="<fmt:message key="pei.template.group.meansResources"/>">
                    <option value="Template12MeansResources"
                            <c:if test="${requestScope.actionBean.template.name == 'Template12MeansResources'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_MEANS_RESOURCES"/></option>
                    <option value="Template12MeansResourcesElement"
                            <c:if test="${requestScope.actionBean.template.name == 'Template12MeansResourcesElement'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_MEANS_RESOURCES_ELEMENT"/></option>
                </optgroup>
                <optgroup label="<fmt:message key="pei.template.group.doc"/>">
                    <option value="Template6Documents"
                            <c:if test="${requestScope.actionBean.template.name == 'Template6Documents'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_DOCUMENTS"/></option>
                    <option value="Template6DocumentsElement"
                            <c:if test="${requestScope.actionBean.template.name == 'Template6DocumentsElement'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_DOCUMENTS_ELEMENT"/></option>
                </optgroup>
                <optgroup label="<fmt:message key="pei.template.group.faq"/>">
                    <option value="Template7FAQ"
                            <c:if test="${requestScope.actionBean.template.name == 'Template7FAQ'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_FAQ"/></option>
                    <option value="Template7FAQElement"
                            <c:if test="${requestScope.actionBean.template.name == 'Template7FAQElement'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_FAQ_ELEMENT"/></option>
                </optgroup>
                <optgroup label="<fmt:message key="pei.template.group.procedures"/>">
                    <option value="Template10Procedure"
                            <c:if test="${requestScope.actionBean.template.name == 'Template10Procedure'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_PROCEDURE"/></option>
                    <option value="Template3RichText"
                            <c:if test="${requestScope.actionBean.template.name == 'Template3RichText'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_RICH_TEXT"/></option>
                    <option value="Template9RichTextWithAttach"
                            <c:if test="${requestScope.actionBean.template.name == 'Template9RichTextWithAttach'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_RICH_TEXT_WITH_ATTACH"/></option>
                    <option value="Template1Diagram"
                            <c:if test="${requestScope.actionBean.template.name == 'Template1Diagram'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_DIAGRAM"/></option>
                </optgroup>
                <optgroup label="<fmt:message key="pei.template.group.risks"/>">
                    <option value="Template8RiskAnalysis"
                            <c:if test="${requestScope.actionBean.template.name == 'Template8RiskAnalysis'}">selected="selected"</c:if>>
                        <fmt:message key="pei.template.TEMPLATE_RISK_ANALYSIS"/></option>
                </optgroup>
            </select>

            <label class="warning formSubtitle justify">
                <fmt:message key="folder.warning"/>
            </label>
        </c:when>
        <c:otherwise>
            <span class="floatLeft" style="margin-bottom:10px;">
                <fmt:message key="pei.template.${requestScope.templateName}"/>
            </span>
            <s:hidden name="template.name"/>
        </c:otherwise>
    </c:choose>
</p>
<p>
    <s:label for="peiFolderOrder"><fmt:message key="folder.label.order"/> (*):</s:label>
    <s:text id="peiFolderOrder" name="folder.order" style="width:248px;"/>
</p>

<p>
    <s:label for="peiFolderMenu"><fmt:message key="folder.includeInMenu"/>:</s:label>
    <c:choose>
        <c:when test="${requestScope.actionBean.folder.template != null && requestScope.actionBean.folder.navigable == false}">
            <s:checkbox id="peiFolderMenu" name="folder.includeInMenu" disabled="true"/>
        </c:when>
        <c:otherwise>
            <s:checkbox id="peiFolderMenu" name="folder.includeInMenu"/>
        </c:otherwise>
    </c:choose>
</p>

<p>
    <s:label for="peiFolderActive"><fmt:message key="folder.active"/>:</s:label>
    <s:checkbox id="peiFolderActive" name="folder.active"/>
</p>

<h2><span><fmt:message key="folder.permissions"/></span></h2>

<c:if test="${requestScope.actionBean.permissionsList == null || fn:length(requestScope.actionBean.permissionsList) == 0}">
    <label class="information"><fmt:message key="folder.permissions.empty"/></label>
</c:if>

<ul class="rolesList-smaller floatLeft">

    <c:forEach items="${requestScope.actionBean.permissionsList}" var="permission">
        <li>
            <s:label for="${pageScope.permission.id}">${pageScope.permission.name}</s:label>
            <s:checkbox id="${pageScope.permission.id}" name="permissions"
                        value="${pageScope.permission.id}"/>
        </li>
    </c:forEach>
</ul>

<div class="cleaner"><!-- --></div>

<h2><span><fmt:message key="folder.mirrorReferences"/></span></h2>
<c:choose>
    <c:when test="${requestScope.actionBean.folder.folderMirrorReferences != null && !empty requestScope.actionBean.folder.folderMirrorReferences}">
        <c:forEach items="${requestScope.actionBean.folder.folderMirrorReferences}" var="reference"
                   varStatus="i">
            <p class="folderReferences">
                <img src="${pageContext.request.contextPath}/images/Editar.png" width="16" height="16"
                     alt="<fmt:message key="common.edit"/>"
                     title="<fmt:message key="common.edit"/>">
                <a href="#" id="referenceLink${pageScope.i.index}">
                        ${pageScope.reference.pathToShow}
                </a>
            </p>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <label class="information"><fmt:message key="folder.nomirrorReferences"/></label>
    </c:otherwise>
</c:choose>


<div class="cleaner"><!-- --></div>

<c:if test="${requestScope.actionBean.showHelpSection}">
    <h2><span><fmt:message key="folder.help"/></span></h2>

    <p style="margin-top:4px">
        <s:textarea name="folder.help" style="width:100%" rows="3"/>
    </p>
</c:if>

<ss:secure roles="peimanager">
    <c:if test="${requestScope.actionBean.template.name != 'Template2Index'}">
        <h2><span><fmt:message key="templateDocx.templateLinkTitle"/></span></h2>

        <p class="peiFormTemplateDocxLink">
            <label><fmt:message key="templateDocx.templateLink"/>:</label>
            <input type="text" class="mediumInput" readonly="readonly"
                   value="<c:out value="${requestScope.actionBean.folder.path}"/>" onclick="$(this).select();"/>
        </p>

        <% pageContext.setAttribute("ANNEX_LIST", Plan2Docx.FolderOptions.ANNEX_LIST.getName()); %>

        <c:if test="${requestScope.actionBean.template.name == 'Template1Diagram'}">
            <% pageContext.setAttribute("DIAGRAM_AREAS", Plan2Docx.FolderOptions.DIAGRAM_AREAS.getName()); %>
            <p class="peiFormTemplateDocxLink">
                <label><fmt:message key="templateDocx.templateLink.diagramAreas"/>:</label>
                <input type="text" class="mediumInput" readonly="readonly"
                       value="<c:out value="${requestScope.actionBean.folder.path}"/>:${DIAGRAM_AREAS}"
                       onclick="$(this).select();"/>
            </p>
        </c:if>
        <c:if test="${requestScope.actionBean.template.name == 'Template6DocumentsElement' || requestScope.actionBean.template.name == 'Template6Documents'}">

            <p class="peiFormTemplateDocxLink">
                <label><fmt:message key="templateDocx.templateLink.documentList"/>:</label>
                <input type="text" class="mediumInput" readonly="readonly"
                       value="<c:out value="${requestScope.actionBean.folder.path}"/>:${ANNEX_LIST}"
                       onclick="$(this).select();"/>
            </p>
        </c:if>

        <c:if test="${requestScope.actionBean.template.name == 'Template6DocumentsElement'}">
            <% pageContext.setAttribute("DOCUMENT_IMAGE", Plan2Docx.FolderOptions.DOCUMENT_IMAGE.getName()); %>

            <p class="peiFormTemplateDocxLink">
                <label><fmt:message key="templateDocx.templateLink.documentImage"/>:</label>
                <input type="text" class="mediumInput" readonly="readonly"
                       value="<c:out value="${requestScope.actionBean.folder.path}"/>:${DOCUMENT_IMAGE}"
                       onclick="$(this).select();"/>
            </p>
        </c:if>

        <c:if test="${requestScope.actionBean.template.name == 'Template4PlanClickable'}">
            <% pageContext.setAttribute("ANNEXES", Plan2Docx.FolderOptions.ANNEXES.getName()); %>

            <p class="peiFormTemplateDocxLink">
                <label><fmt:message key="templateDocx.templateLink.annexes"/>:</label>
                <input type="text" class="mediumInput" readonly="readonly"
                       value="<c:out value="${requestScope.actionBean.folder.path}"/>:${ANNEXES}"
                       onclick="$(this).select();"/>
            </p>
            <p class="peiFormTemplateDocxLink">
                <label><fmt:message key="templateDocx.templateLink.annexesList"/>:</label>
                <input type="text" class="mediumInput" readonly="readonly"
                       value="<c:out value="${requestScope.actionBean.folder.path}"/>:${ANNEXES}:${ANNEX_LIST}"
                       onclick="$(this).select();"/>
            </p>
        </c:if>
    </c:if>
</ss:secure>


<p class="mandatoryFields-pei" style="margin-top: 25px;">
    <fmt:message key="common.mandatoryfields"/>
</p>

<input type="hidden" name="insertFolderFlag" id="insertFolderFlag"
       value="${requestScope.actionBean.insertFolderFlag}"/>
<input type="hidden" name="folderId" value="${requestScope.actionBean.folderId}"/>

<div class="cleaner"><!-- do not remove--></div>
</div>
</div>

<script type="text/javascript">

    function disableIncludeInMenuCheckbox(firstLoad, selectedTemplate) {
        // TODO-TEMPLATE
        // templates that are not navigable by definition
        var notNavigableTemplates = ["TemplateResource" , "Template7FAQElement", "Template5ContactsElement",
            "Template6DocumentsElement", "Template12MeansResourcesElement"];

        if (jQuery.inArray(selectedTemplate, notNavigableTemplates) != -1) {
            $('#peiFolderMenu').attr("disabled", true);
            $('#peiFolderMenu').attr('checked', false);
        }
        else {
            $('#peiFolderMenu').attr("disabled", false);
            if (!firstLoad) {
                $('#peiFolderMenu').attr('checked', true);
            }
        }
    }

    $(document).ready(function() {
    <c:choose>
    <c:when test="${requestScope.actionBean.doNotShowTemplatesSelect == null || !requestScope.actionBean.doNotShowTemplatesSelect}">
        disableIncludeInMenuCheckbox(true, $('#template').val());
        $('#template').change(function() {
            disableIncludeInMenuCheckbox(false, $('#template').val());
        });
    </c:when>
    <c:otherwise>
        disableIncludeInMenuCheckbox(true, '${requestScope.templateNameSourceFolder}');
    </c:otherwise>
    </c:choose>
    });

    //Disable and check all parent folder permissions
    <c:if test="${requestScope.actionBean.parentsFolderPermissions != null}">
    <c:forEach items="${requestScope.actionBean.parentsFolderPermissions}" var="parentPermission">
    $('#' +${parentPermission}).attr('checked', 'true');
    $('#' +${parentPermission}).attr('disabled', 'true');
    </c:forEach>
    </c:if>

    <c:forEach items="${requestScope.actionBean.folder.folderMirrorReferences}" var="reference" varStatus="i">
    attachEventToTemplate11MirrorLink("${pageContext.request.contextPath}", "referenceLink${pageScope.i.index}", "${pageScope.reference.referenceContractId}", "${pageScope.reference.referencePath}", '${pageScope.reference.moduleType}');
    </c:forEach>
</script>