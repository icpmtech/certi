<%@ include file="../../../includes/taglibs.jsp" %>

<c:choose>
    <c:when test="${requestScope.actionBean.folders != null && fn:length(requestScope.actionBean.folders) > 0}">
        <div class="template9RichTextAttach">
            <div class="left">
                    ${requestScope.actionBean.folder.template.text}
            </div>
            <div class="right">
                <div class="mainContent">
                    <div class="title">
                        <p>
                            <span class="attachFile">&nbsp;</span><fmt:message
                                key="pei.template.9RichTextWithAttach.attach"/>
                        </p>
                    </div>
                    <div class="attachBoard">
                        <ul>
                            <c:forEach items="${requestScope.actionBean.folders}" var="folder">
                                <li>
                                    <img src="${pageContext.request.contextPath}/images/Seccao-Fechada.png" alt=""/>
                                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
                                            event="viewResource">
                                        <s:param name="path" value="${pageScope.folder.path}"/>
                                        <s:param name="peiId" value="${requestScope.actionBean.peiId}"/>
                                        <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
                                        ${folder.name}
                                    </s:link>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        ${requestScope.actionBean.folder.template.text}
    </c:otherwise>
</c:choose>




