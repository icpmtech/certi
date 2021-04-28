<%@ include file="../../../includes/taglibs.jsp" %>

<head>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery.autocomplete.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/tree/ui.dynatree-no-folders.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery-ui-1.7.custom.css"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/datepicker-${pageContext.request.locale}.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/tree/jquery.dynatree.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery.autocomplete.js"></script>
</head>

<c:choose>
    <c:when test="${requestScope.actionBean.update}">
        <c:set var="title"><fmt:message key="common.edit"/></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="title"><fmt:message key="common.add"/></c:set>
    </c:otherwise>
</c:choose>

<title><fmt:message key="legislation.mainTitle"/> &gt; ${pageScope.title}</title>

<h1><fmt:message key="legislation.mainTitle"/></h1>

<h2 class="form" style="width:970px"><span>${pageScope.title}</span></h2>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean" focus=""
        class="form">

<s:hidden name="update"/>
<s:hidden name="legalDocument.id"/>
<s:hidden name="number" value="${requestScope.actionBean.legalDocument.number}"/>
<s:hidden name="year" value="${requestScope.actionBean.legalDocument.year}"/>
<s:hidden name="documentTypeId" value="${requestScope.actionBean.legalDocument.documentType.id}"/>

<div class="legislationInsertForm">
<s:errors/>

<div class="leftFields">

<s:label for="title"><fmt:message key="legislation.add.title"/> (*):</s:label>
<s:text id="title" name="legalDocument.customTitle" class="largeInput"/>

<s:label for="documentType"><fmt:message key="legislation.add.type"/> (*):</s:label>
<s:select name="legalDocument.documentType.id" id="documentType" class="largeInput">
    <s:options-collection collection="${requestScope.actionBean.documentTypeList}" label="name" value="id"/>
</s:select>

<s:label for="number"><fmt:message key="legislation.add.number"/> (*):</s:label>
<s:text id="number" name="legalDocument.number" class="largeInput"/>

<s:label for="publishdate"><fmt:message key="legislation.add.publishdate"/> (*):</s:label>
<s:text id="publishdate" name="legalDocument.publicationDate" class="dateInput" readonly="true"/>

<div class="cleaner"><!-- --></div>

<s:label for="keywords"><fmt:message key="legislation.add.keywords"/>:</s:label>
<s:text id="keywords" name="legalDocument.keywords" class="largeInput"/>

<s:label for="summary" style="margin-bottom: 5px;"><fmt:message key="legislation.add.summary"/> (*):</s:label>
<p>
    <s:textarea id="summary" name="legalDocument.summary" class="largeInputTextArea" rows="5"
                style="width: 600px;"/>
</p>


<c:url value="/scripts/fckeditor/editor/filemanager/browser/default/browser.html?showBrowseServerButton=false"
       var="browserUrl"/>

<s:label for="customAbstract" style="margin-bottom: 5px; margin-top: 20px;"><fmt:message
        key="legislation.add.customAbstract"/>:</s:label>
<p>
    <fck:editor instanceName="legalDocument.customAbstract" height="300px" width="605px">
                <jsp:attribute name="value">
                    <c:choose>
                        <c:when test="${requestScope.actionBean.legalDocument.customAbstract != null && requestScope.actionBean.legalDocument.customAbstract != ''}">
                            ${requestScope.actionBean.legalDocument.customAbstract}
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="legislation.add.defaultcustomAbstract"/>
                        </c:otherwise>
                    </c:choose>
                </jsp:attribute>
        <jsp:body>
            <fck:config DefaultLanguage="${requestScope.actionBean.context.locale.language}"
                        LinkBrowserURL="${browserUrl}"/>
        </jsp:body>
    </fck:editor>
</p>

<s:label for="transitoryProvisions" style="margin-bottom: 5px; margin-top: 20px;"><fmt:message
        key="legislation.add.transitoryProvisions"/>:</s:label>
<p>
    <fck:editor instanceName="legalDocument.transitoryProvisions" height="300px" width="605px">
                <jsp:attribute name="value">
                    <c:choose>
                        <c:when test="${requestScope.actionBean.legalDocument.transitoryProvisions!= ''}">
                            ${requestScope.actionBean.legalDocument.transitoryProvisions}
                        </c:when>
                        <c:otherwise>

                        </c:otherwise>
                    </c:choose>
                </jsp:attribute>
        <jsp:body>
            <fck:config DefaultLanguage="${requestScope.actionBean.context.locale.language}"
                        LinkBrowserURL="${browserUrl}"/>
        </jsp:body>
    </fck:editor>
</p>


<s:label for="legalComplianceValidation" style="margin-bottom: 5px; margin-top: 20px;"><fmt:message
        key="legislation.add.legalComplianceValidation"/>:</s:label>
<p>
    <fck:editor instanceName="legalDocument.legalComplianceValidation" height="300px" width="605px">
                <jsp:attribute name="value">
                    <c:choose>
                        <c:when test="${requestScope.actionBean.legalDocument.legalComplianceValidation!= ''}">
                            ${requestScope.actionBean.legalDocument.legalComplianceValidation}
                        </c:when>
                        <c:otherwise>

                        </c:otherwise>
                    </c:choose>
                </jsp:attribute>
        <jsp:body>
            <fck:config DefaultLanguage="${requestScope.actionBean.context.locale.language}"
                        LinkBrowserURL="${browserUrl}"/>
        </jsp:body>
    </fck:editor>
</p>

<s:label for="referenceArticles" style="margin-bottom: 5px; margin-top: 20px;"><fmt:message
        key="legislation.add.referenceArticles"/>:</s:label>
<p>
    <fck:editor instanceName="legalDocument.referenceArticles" height="300px" width="605px">
                <jsp:attribute name="value">
                    <c:choose>
                        <c:when test="${requestScope.actionBean.legalDocument.referenceArticles!= ''}">
                            ${requestScope.actionBean.legalDocument.referenceArticles}
                        </c:when>
                        <c:otherwise>

                        </c:otherwise>
                    </c:choose>
                </jsp:attribute>
        <jsp:body>
            <fck:config DefaultLanguage="${requestScope.actionBean.context.locale.language}"
                        LinkBrowserURL="${browserUrl}"/>
        </jsp:body>
    </fck:editor>
</p>

<p>&nbsp;</p>

<s:label for="fileD"><fmt:message key="legislation.add.document"/> (*):</s:label>
<s:file name="file" id="fileD" class="largeInput"/>
<c:if test="${requestScope.actionBean.update}">
    <label class="formSubtitle" style="width:100%;margin-bottom:10px;margin-top:-10px;">(<fmt:message
            key="legislation.add.replaceFile"/>)</label>
</c:if>

<div class="cleaner"><!-- --></div>


<s:label for="documentState" style="margin-top:5px"><fmt:message key="legislation.add.state"/> (*):</s:label>
<s:select name="legalDocument.documentState.id" id="documentState" class="largeInput">
    <s:options-collection collection="${requestScope.actionBean.documentStateList}" label="name"
                          value="id"/>
</s:select>


<div class="cleaner"><!-- --></div>

<s:label for="published"><fmt:message key="legislation.add.active"/>:</s:label>
<s:checkbox name="legalDocument.published" id="published"/>

<div class="cleaner"><!-- --></div>

<c:choose>
    <c:when test="${requestScope.actionBean.update}">

        <s:label for="sendNotificationNew"><fmt:message key="legislation.add.sendnotificationnew"/>:</s:label>
        <s:checkbox name="legalDocument.sendNotificationNew" id="sendNotificationNew"/>

        <div class="cleaner"><!-- --></div>

        <s:label for="sendNotificationChange"><fmt:message key="legislation.add.sendnotificationchange"/>:</s:label>
        <s:checkbox name="legalDocument.sendNotificationChange" id="sendNotificationChange"/>

        <div class="cleaner"><!-- --></div>
    </c:when>
    <c:otherwise>
        <s:label for="sendNotification"><fmt:message key="legislation.add.notification"/>:</s:label>
        <s:checkbox name="legalDocument.sendNotificationNew" id="sendNotification"/>
    </c:otherwise>
</c:choose>


<h2><span><fmt:message key="legislation.add.associatedLegalDocuments"/> </span></h2>

<div id="legalDocumentAssociations"> <!-- --> </div>

<div id="template" style="display:none">
    <div class="legalDocumentAssociation" id="legalDocumentAssociationTemplate">

                                    
        <span id="divTitleTemplate" class="legalDocumentAssociationTitle">
               <fmt:message key="legislation.legalDocument"/>
        </span>


        <span style="width:580px;margin-top:5px">
            <select id="documentTypeTemplate" name="documentTypeTemplateName"
                    style="float:left; width:350px;margin-right:5px">
                <c:forEach items="${requestScope.actionBean.documentTypeList}" var="option">
                    <option value="${option.id}">${option.name}</option>
                </c:forEach>
            </select>

             <span class="legalDocumentAssociationTitle" style="width: 15px; margin-right: 10px;">nº</span>
             <input type="text" name="titleTemplateName" id="titleTemplateId" class="mediumSmallInput"
                    style="width:193px"/>
             <label class="example" style="color:#BFBFBF;"><fmt:message key="common.example"/> : "123/2009"</label>
        </span>
        <span id="removeRow" class="remove">&nbsp;</span>
        <span class="legislationInsertLineSeparator">&nbsp;</span>
    </div>
</div>

<div class="cleaner"><!-- --></div>

<div style="margin-top:10px">
    <a href="javascript:addRow('${pageContext.request.contextPath}');" class="operationAddMore">
        <fmt:message key="legislation.add.addAssociatedLegalDocuments"/>
    </a>
</div>

<div class="formButtons">
    <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
    <c:choose>
        <c:when test="${requestScope.actionBean.update}">
            <s:submit name="updateLegislation" class="button"><fmt:message key="common.submit"/></s:submit>
        </c:when>
        <c:otherwise>
            <s:submit name="insertLegislation" class="button"><fmt:message key="common.submit"/></s:submit>
        </c:otherwise>
    </c:choose>
    <s:submit name="cancel" class="button"><fmt:message key="common.cancel"/></s:submit>
</div>

</div>

<div class="rightFields">

    <label style="margin-bottom:10px"><fmt:message key="legislation.add.category"/> (*):</label>

    <div class="cleaner"><!-- --></div>

    <div id="tree1"><!-- --></div>

    <c:set var="lastDepth" value="1"/>
    <ul id="treeData" style="display: none;">

        <c:forEach items="${requestScope.actionBean.documentCategoryList}" var="documentCategory">

        <c:choose>
            <c:when test="${documentCategory.depth > lastDepth}">
                <ul>
            </c:when>
            <c:when test="${documentCategory.depth < lastDepth}">
                <c:forEach begin="${documentCategory.depth}" end="${lastDepth-1}">
                    </ul>
                </c:forEach>
            </c:when>
        </c:choose>

        <c:set var="liClass" value=""/>
        <c:if test="${documentCategory.hasChildren}">
            <c:set var="liClass" value="${liClass} folder"/>
        </c:if>
        <c:if test="${documentCategory.depth == 1}">
            <c:set var="liClass" value="${liClass} expanded"/>
        </c:if>

        <c:set var="selectedAlready" value="false"/>
        <c:forEach items="${requestScope.actionBean.selectedLegalDocumentCategories}" var="selectedCategory">
            <c:if test="${selectedCategory == documentCategory.id && !selectedAlready}">
                <c:set var="liClass" value="${liClass} selected"/>
                <c:set var="selectedAlready" value="true"/>
            </c:if>
        </c:forEach>

        <li class="${liClass}" id="${documentCategory.id}">${documentCategory.name}
                <c:set var="lastDepth" value="${documentCategory.depth}"/>
            <input type="hidden" name="selectedLegalDocumentCategories" value=""
                   id="hidden${documentCategory.id}"/>

            </c:forEach>
    </ul>
</div>
</div>

</s:form>

<script type="text/javascript">

    selectCheckbox(${requestScope.actionBean.update});
    addUpdateLegalDocumentChangeEvent(${requestScope.actionBean.update});

    <c:forEach items="${requestScope.actionBean.legalDocument.associatedLegalDocuments}" var="associatedLegalDocument">
    addRow('${pageContext.request.contextPath}', '${associatedLegalDocument.drTitle}', '${associatedLegalDocument.documentType.id}');
    </c:forEach>

    $(function() {
        $('#publishdate').datepicker({
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="legislation.add.publishdate"/>'});
    });

    $("#tree1").dynatree({
        checkbox: true,
        selectMode: 3,
        onDblClick: function(dtnode) {
            dtnode.toggleSelect();
        },
        fx: { height: "toggle", duration: 200 },

        onSelect: function(flag, dtnode) {

            if (! flag) {
                //Deselect current node
                $("#hidden" + dtnode.data.key).val('')

                //Deselect child nodes
                dtnode.visit(function(dtnode2) {
                    $("#hidden" + dtnode2.data.key).val('')
                });
            } else {

                var selectedNodes = dtnode.tree.getSelectedNodes();
                $.map(selectedNodes, function(node) {
                    $("#hidden" + node.data.key).val(node.data.key);
                })
            }
        },

        initId: "treeData"
    });
</script>