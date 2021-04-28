<%@ include file="../../../includes/taglibs.jsp" %>

<c:set var="lastDepth" value="1"/>
<ul class="peiPermissionDetailsList">
<c:forEach items="${requestScope.actionBean.nodes}" var="peiNode" varStatus="i">
    <c:choose>
        <c:when test="${peiNode.depth > lastDepth}">
            <ul style="padding-left:12px;padding-top:2px">
        </c:when>
        <c:when test="${peiNode.depth < lastDepth}">
            <c:forEach begin="${peiNode.depth}" end="${lastDepth-1}">
                </li>
                </ul>
            </c:forEach>
            </li>
        </c:when>
        <c:when test="${!i.first}">
            </li>
        </c:when>
    </c:choose>
    <li style="list-style:none;padding-top:2px">
    <c:choose>
        <c:when test="${peiNode.special}">
            <c:set var="linkColor" value="color:#40CF7C!important;"/>
        </c:when>
        <c:otherwise>
            <c:set var="linkColor" value="color:#E10000!important;"/>
        </c:otherwise>
    </c:choose>

    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMActionBean"
            style="${linkColor}" event="viewPeiCMFromPreview">
        <s:param name="planModuleType" value="${requestScope.actionBean.planModuleType}"/>

        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
        <s:param name="path" value="${peiNode.path}"/>
        ${peiNode.name}
    </s:link>


    <c:set var="lastDepth" value="${peiNode.depth}"/>

    <c:if test="${i.last}">

        <c:forEach begin="1" end="${lastDepth-1}">
            </li>
            <!-- last special-->
            </ul>
        </c:forEach>
        </li>
    </c:if>

</c:forEach>
</ul><!-- last-->


