<%@ include file="../../../includes/taglibs.jsp" %>

<div class="template-cm-padding" style="float:none">
    <h2 style="margin-top:0;margin-bottom:0"><span><fmt:message key="folder.headerText"/></span></h2>
</div>
<s:form partial="true" beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMOperationsActionBean">
    <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>
    
    <c:url value="/scripts/fckeditor/editor/filemanager/browser/default/browser.html?showBrowseServerButton=false" var="browserUrl"/>
    <c:url value="/scripts/fckeditor/editor/filemanager/connectors" var="connectorUrl"/>
    <!--To send more then one parameter URL must be encoded-->
    <c:url value="%3Ffolder%3D${requestScope.encodedFolderId}%26contextPath%3D${pageScope.request.contextPath}%26planModuleType%3D${requestScope.actionBean.planModuleType}"
           var="params"/>

    <fck:editor instanceName="folder.folderHeader" height="300px">
                <jsp:attribute name="value">
                    <c:choose>
                        <c:when test="${requestScope.additionalFolderInfoHeader != null && fn:trim(requestScope.additionalFolderInfoHeader) != ''}">
                            ${requestScope.additionalFolderInfoHeader}
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

    <div class="template-cm-padding" style="float:none">
        <h2 style="margin-top:0;margin-bottom:0"><span><fmt:message key="folder.footerText"/></span></h2>
    </div>
    <fck:editor instanceName="folder.folderFooter" height="300px">
                <jsp:attribute name="value">
                    <c:choose>
                        <c:when test="${requestScope.additionalFolderInfoFooter != null && fn:trim(requestScope.additionalFolderInfoFooter) != ''}">
                            ${requestScope.additionalFolderInfoFooter}
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
</s:form>
