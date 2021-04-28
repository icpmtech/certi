<%@ include file="../../../includes/taglibs.jsp" %>
<%@ page import="com.criticalsoftware.certitools.entities.jcr.Template" %>

<s:form partial="true" beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMOperationsActionBean">
    <div class="template-cm-padding">
        <div class="form" style="width:100%">

            <c:url value="/scripts/fckeditor/editor/filemanager/browser/default/browser.html" var="browserUrl"/>
            <c:url value="/scripts/fckeditor/editor/filemanager/connectors" var="connectorUrl"/>
            <!--To send more then one parameter URL must be encoded-->
            <c:url value="%3Ffolder%3D${requestScope.encodedFolderId}%26contextPath%3D${pageScope.request.contextPath}%26insertFolderFlag%3D${requestScope.actionBean.insertFolderFlag}%26planModuleType%3D${requestScope.actionBean.planModuleType}"
                   var="params"/>

            <p>
                <s:label for="fileTemplate" class="labelFileUploadTemplate">
                    <fmt:message key="pei.cm.template.file"/> (*):
                </s:label>
                <s:file id="fileTemplate" name="fileTemplate1" class="fileInput"/>
                <span class="recomended-style">(<fmt:message key="common.recommended.dimensions"/>: <fmt:message
                        key="pei.template.recommended.width"/> <fmt:message key="common.by"/> <fmt:message
                        key="pei.template.recommended.height"/> <fmt:message
                        key="common.pixels"/>)
                </span>
                <br>
                <span class="recomended-style">(<fmt:message key="common.recommended.fileType"/>: .jpg, .bmp, .gif, .png)
                </span>
            </p>

            <c:if test="${requestScope.actionBean.folder.template.resource != null || requestScope.actionBean.folder.template.imageMap != null}">
                <p class="warningReplaceFile-pei">
                    (<fmt:message key="legislation.add.replaceFile"/>)
                </p>
                <p>
                    <s:label style="font-size: 10px; float:left; width:auto; margin-top:3px;" for="replaceImageMap"><fmt:message key="pei.template.clear.map"/></s:label>
                    <input type="checkbox" name="replaceImageMap" style="" id="replaceImageMap" />
                </p>
            </c:if>

            <p class="mandatoryFields-pei">
                <fmt:message key="common.mandatoryfields"/>
            </p>

        </div>
    </div>
    <c:if test="${requestScope.actionBean.folder.template.resource != null || requestScope.actionBean.folder.template.imageMap != null}">

        <fck:editor instanceName="template.imageMap" height="500px" toolbarSet="imageMap">
                <jsp:attribute name="value">
                     ${requestScope.actionBean.folder.template.imageMap}
                </jsp:attribute>
            <jsp:body>
                <fck:config DefaultLanguage="${requestScope.actionBean.context.locale.language}"
                            LinkBrowserURL="${browserUrl}?Type=File&Connector=${connectorUrl}${params}"
                            ImageBrowserURL="${browserUrl}?Type=Image&Connector=${connectorUrl}${params}"/>
            </jsp:body>
        </fck:editor>
    </c:if>
</s:form>