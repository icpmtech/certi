<%@ include file="../../../includes/taglibs.jsp" %>

<s:form partial="true" beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMOperationsActionBean">
    <div class="template-cm-padding">
        <div class="form" style="width:100%">

            <p>
                <s:label for="fileTemplate" class="labelFileUploadTemplate">
                    <fmt:message key="pei.cm.template.file"/> (*):
                </s:label>
                <s:file id="fileTemplate" name="file" class="fileInput"/>
            </p>

            <c:if test="${requestScope.actionBean.folder.template.resource != null}">
                <p class="warningReplaceFile-pei">
                    (<fmt:message key="legislation.add.replaceFile"/>)
                </p>
            </c:if>

            <p class="mandatoryFields-pei">
                <fmt:message key="common.mandatoryfields"/>
            </p>

            <c:if test="${requestScope.actionBean.folder.template.resource != null}">
                <p class="imageContent">
                    <c:choose>
                        <c:when test="${requestScope.actionBean.folder.template.resource.photo}">
                            <img src="${pageContext.request.contextPath}/plan/Plan.action?viewResource=&peiViewOffline=true&path=${requestScope.actionBean.folder.path}&peiId=${requestScope.actionBean.peiId}&planModuleType=${requestScope.actionBean.planModuleType}"
                                 alt="${requestScope.actionBean.folder.template.resource.name}"/>
                        </c:when>
                        <c:otherwise>
                            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
                                    event="viewResource" class="download-file">
                                <s:param name="path" value="${requestScope.actionBean.folder.path}"/>
                                <s:param name="peiId" value="${requestScope.actionBean.peiId}"/>
                                <s:param name="peiViewOffline" value="true"/>
                                <s:param name="planModuleType" value="${requestScope.actionBean.planModuleType}"/>
                                <c:out value="${requestScope.actionBean.folder.template.resource.name}"/>
                            </s:link>
                        </c:otherwise>
                    </c:choose>
                </p>
            </c:if>
        </div>
    </div>
</s:form>
