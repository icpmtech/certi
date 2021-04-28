<%@ include file="../../../includes/taglibs.jsp" %>

<h2><span style="padding-left:1em"><fmt:message key="pei.cm.template.question"/></span></h2>

<s:form partial="true" beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMOperationsActionBean">
    <div class="form" style="width:100%">

        <c:url value="/scripts/fckeditor/editor/filemanager/browser/default/browser.html" var="browserUrl"/>
        <c:url value="/scripts/fckeditor/editor/filemanager/connectors" var="connectorUrl"/>
        <!--To send more then one parameter URL must be encoded-->
        <c:url value="%3Ffolder%3D${requestScope.encodedFolderId}%26contextPath%3D${pageScope.request.contextPath}%26insertFolderFlag%3D${requestScope.actionBean.insertFolderFlag}%26planModuleType%3D${requestScope.actionBean.planModuleType}"
               var="params"/>

        <fck:editor instanceName="template.question" height="300px">
                <jsp:attribute name="value">
                    <c:choose>
                        <c:when test="${requestScope.actionBean.folder.template.question != null}">
                            ${requestScope.actionBean.folder.template.question}
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

        <br>

        <h2><span style="padding-left:1em"><fmt:message key="pei.cm.template.answer"/></span></h2>

        <fck:editor instanceName="template.answer" height="300px">
                <jsp:attribute name="value">
                    <c:choose>
                        <c:when test="${requestScope.actionBean.folder.template.answer != null}">
                            ${requestScope.actionBean.folder.template.answer}
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
