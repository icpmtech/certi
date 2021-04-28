<%@ include file="../../../includes/taglibs.jsp" %>

<s:form partial="true" beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMOperationsActionBean">
    <div class="form" style="width:100%">

        <c:url value="/scripts/fckeditor/editor/filemanager/browser/default/browser.html" var="browserUrl"/>
        <c:url value="/scripts/fckeditor/editor/filemanager/connectors" var="connectorUrl"/>
        <!--To send more then one parameter URL must be encoded-->
        <c:url value="%3Ffolder%3D${requestScope.encodedFolderId}%26contextPath%3D${pageScope.request.contextPath}%26insertFolderFlag%3D${requestScope.actionBean.insertFolderFlag}%26planModuleType%3D${requestScope.actionBean.planModuleType}"
               var="params"/>

        <fck:editor instanceName="template.text" height="500px">
                <jsp:attribute name="value">
                    <c:choose>
                        <c:when test="${requestScope.actionBean.folder.template.text != null}">
                            ${requestScope.actionBean.folder.template.text}
                        </c:when>
                        <c:otherwise>

                        </c:otherwise>
                    </c:choose>
                </jsp:attribute>
            <jsp:body>
                <fck:config DefaultLanguage="${requestScope.actionBean.context.locale.language}"
                            LinkBrowserURL="${browserUrl}?Type=File&Connector=${connectorUrl}${params}"
                            ImageBrowserURL="${browserUrl}?Type=Image&Connector=${connectorUrl}${params}"/>
            </jsp:body>
        </fck:editor>
    </div>
</s:form>