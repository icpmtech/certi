<%@ include file="../../../includes/taglibs.jsp" %>

<s:form partial="true" beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMOperationsActionBean">
    <div class="template-cm-padding">
        <div class="form" style="width:100%">
            <p>
                <s:label for="contentName"><fmt:message key="pei.template.6DocumentsElement.name"/> (*):</s:label>
                <s:text id="contentName" name="template.contentName" class="mediumInput"/>
            </p>

            <p>
                <s:label for="contentType"><fmt:message key="pei.template.6DocumentsElement.type"/> (*):</s:label>
                <s:text id="contentType" name="template.contentType" class="mediumInput"/>
            </p>

            <p>
                <s:label for="contentSubType"><fmt:message key="pei.template.6DocumentsElement.subtype"/>:</s:label>
                <s:text id="contentSubType" name="template.contentSubType" class="mediumInput"/>
            </p>

            <p>
                <s:label for="contentDate"><fmt:message key="pei.template.6DocumentsElement.date"/> (*):</s:label>
                <s:text id="contentDate" name="template.contentDate" class="dateInput" readonly="true"/>
            </p>

            <h2 style="margin-bottom:0"><span><fmt:message
                    key="pei.template.6DocumentsElement.addFiles"/></span>
            </h2>

            <div id="fileUploadDiv">
                &nbsp;
            </div>
            <p style="margin-top:10px">
                <a href="javascript:addFileRowToTemplate6DocumentElement(true);" class="operationAddMore"
                   style="font-size:8pt">
                    <fmt:message key="pei.template.6DocumentsElement.files"/>
                </a>
            </p>

            <h2 style="margin-bottom:0"><span><fmt:message
                    key="pei.template.6DocumentsElement.links"/></span>
            </h2>

            <div id="linksDiv">
                &nbsp;
            </div>
            <p style="margin-top:10px">
                <a href="javascript:addLinkRowToTemplate6DocumentElement(true);" class="operationAddMore"
                   style="font-size:8pt">
                    <fmt:message key="pei.template.6DocumentsElement.addlinks"/>
                </a>
            </p>

            <p class="mandatoryFields-pei" style="margin-top: 25px;">
                <fmt:message key="common.mandatoryfields"/>
            </p>

            <div class="cleaner"><!-- do not remove--></div>
        </div>
    </div>
</s:form>

<div id="fileUploadTemplate" class="hidden">
    <table class="peiCMTemplate6Documents">
        <tr>
            <td class="firstColumn">
                <label for="fileUploadAliasId"><fmt:message key="pei.template.6DocumentsElement.title"/>:</label>
            </td>
            <td class="secondColumn">
                <input type="text" name="fileUploadAliasName" id="fileUploadAliasId" class="mediumInput"/>
            </td>
            <td class="thirdColumn" rowspan="2">
                <img src="${pageContext.request.contextPath}/images/Eliminar.png"
                     alt="<fmt:message key="common.delete"/> " id="removeFileRow"/>
            </td>
        </tr>
        <tr>
            <td class="firstColumn">
                <label for="fileUploadId"><fmt:message key="pei.cm.template.file"/>:</label>
            </td>
            <td class="secondColumn">
                <input type="file" name="fileUploadName" id="fileUploadId" class="mediumInput"/>
            </td>
        </tr>
    </table>
</div>

<div id="linksDivTemplate" class="hidden">
    <table class="peiCMTemplate6Documents">
        <tr>
            <td class="firstColumn">
                <label for="linkAliasId"><fmt:message key="pei.template.6DocumentsElement.title"/> (*):</label>
            </td>
            <td class="secondColumn">
                <input type="text" name="linkAliasName" id="linkAliasId" class="mediumInput"/>
            </td>
            <td class="thirdColumn" rowspan="2">
                <img src="${pageContext.request.contextPath}/images/Eliminar.png"
                     alt="<fmt:message key="common.delete"/> " id="removeLinkRow"/>
            </td>
        </tr>
        <tr>
            <td class="firstColumn">
                <label for="linkHrefId"><fmt:message key="pei.template.6DocumentsElement.address"/>:</label>
            </td>
            <td class="secondColumn">
                <input type="text" name="linkHrefName" id="linkHrefId" class="mediumInput"/>
            </td>
        </tr>

    </table>
</div>

<script type="text/javascript">
    var fileCounter = 0;
    var linksCounter = 0;

    <c:forEach items="${requestScope.actionBean.folder.template.resources}" var="dbFile" varStatus="i">
    addFileRowToTemplate6DocumentElement(false, "${pageContext.request.contextPath}", "${requestScope.actionBean.folder.path}", "${dbFile.name}", "${requestScope.actionBean.peiId}", "${i.index}", "<certitools:escape type="js" value="${dbFile.alias}"/>", "${requestScope.actionBean.planModuleType}");
    </c:forEach>
    <c:forEach items="${requestScope.actionBean.folder.template.links}" var="link">
    addLinkRowToTemplate6DocumentElement(false, "<certitools:escape type="js" value="${(pageScope.link.href)}"/>", "<certitools:escape type="js" value="${(pageScope.link.alias)}"/>");
    </c:forEach>

    $(function() {
        $('#contentDate').datepicker({
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="pei.template.6DocumentsElement.date"/>'});
    });
</script>