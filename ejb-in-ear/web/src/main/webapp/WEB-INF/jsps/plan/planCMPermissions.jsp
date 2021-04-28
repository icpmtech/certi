<%@ include file="../../../includes/taglibs.jsp" %>

<head>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
</head>

<title><fmt:message key="menu.emergency"/> &gt; <fmt:message key="menu.pei.permissions"/></title>

<h1><fmt:message key="menu.pei.permissions"/></h1>

<s:errors/>
<s:messages/>

<s:form action="/plan/PlanCMPermissions.action" class="form-pei-admin-select-pei" method="get" id="contractForm">
    <p>
        <label for="companies"><fmt:message key="pei.entity"/>:</label>
        <select id="companies" name="companyId" class="mediumInput">
            <c:forEach items="${requestScope.actionBean.companies}" var="company">
                <c:choose>
                    <c:when test="${company.id == requestScope.actionBean.companyId}">
                        <option value="${company.id}" selected="selected">${company.name}</option>
                    </c:when>
                    <c:otherwise>
                        <option value="${company.id}">${company.name}</option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </select>

        <label for="contractsLabel" style="margin-left: 40px"><fmt:message key="pei.contract"/>:</label>
        <select id="contractsLabel" name="contractId" class="mediumInput">
            <c:forEach items="${requestScope.actionBean.contracts}" var="contract">
                <c:choose>
                    <c:when test="${requestScope.actionBean.contractId == contract.id}">
                        <option value="${contract.id}" selected="selected">${contract.contractDesignation}</option>
                    </c:when>
                    <c:otherwise>
                        <option value="${contract.id}">${contract.contractDesignation}</option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </select>
    </p>
    <input type="hidden" name="_eventName" value="view"/>
    <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>
</s:form>

<br/><br/>

<c:if test="${requestScope.actionBean.contractId != null}">
    <s:form action="/plan/PlanCMPermissions.action" class="form-pei-admin-select-pei" method="post">
        <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>
        <h2><span><fmt:message key="pei.permission.add"/></span></h2>
        <input type="text" name="permission" id="permission" class="mediumInput" maxlength="255"/>
        <input type="submit" value="<fmt:message key="common.add"/>" name="addPermission" class="button"/>
        <input type="hidden" name="_eventName" value="addPermission"/>
        <input type="hidden" name="contractId" value="${requestScope.actionBean.contractId}"/>
        <input type="hidden" name="companyId" value="${requestScope.actionBean.companyId}"/>
    </s:form>
</c:if>

<c:if test="${requestScope.actionBean.permissions != null}">
    <display:table list="${requestScope.actionBean.permissions}" export="false" id="displaytable"
                   class="displaytag" uid="permission" htmlId="peiPermissionList"
                   decorator="com.criticalsoftware.certitools.presentation.util.TableCMPermissionsDecorator">
        <display:column titleKey="pei.permission.name" property="name" escapeXml="true" style="width:98%"/>
        <display:column class="oneButtonColumnWidth" media="html">
            <c:if test="${pageScope.permission.name != applicationScope.configuration.PEIPermissionPEIManager}">
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMPermissionsActionBean"
                        event="deletePermission" class="confirmDelete">
                    <s:param name="companyId" value="${requestScope.actionBean.companyId}"/>
                    <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                    <s:param name="permission" value="${pageScope.permission.name}"/>
                    <s:param name="planModuleType" value="${requestScope.actionBean.planModuleType}"/>
                    <img src="${pageContext.request.contextPath}/images/Eliminar.png"
                         title="<fmt:message key="common.delete"/>"
                         alt="<fmt:message key="common.delete"/>"/></s:link>
            </c:if>
        </display:column>
    </display:table>
</c:if>

<c:if test="${requestScope.actionBean.permissions != null && fn:length(requestScope.actionBean.permissions) >0}">

    <h2><span><fmt:message key="pei.permission.fullSchema.title"/></span></h2>

    <p class="alignRight" style="margin-top:0;width: 100px; float: right; margin-bottom: 0;">
        <a href="#" id="togglePeiFolderPermissions" class="font-11px open-section" style="color:#405C6C;">
            <fmt:message key="pei.cm.hide"/>
        </a>
    </p>

    <div style="font-size:0.9em;!important;" id="peiFolderPermissions">
        <c:set var="lastDepth" value="1"/>
        <ul class="peiPermissionDetailsList">
            <c:forEach items="${requestScope.actionBean.permissionsFullSchemaUsage}" var="peiNode" varStatus="i">
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
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMActionBean"
                        event="viewPeiCMFromPreview">
                    <s:param name="planModuleType" value="${requestScope.actionBean.planModuleType}"/>

                    <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                    <s:param name="path" value="${peiNode.path}"/>
                    ${peiNode.name}
                </s:link>

                <c:if test="${peiNode.permissions != null && fn:length(peiNode.permissions) > 0}">
                    ( <c:forEach items="${peiNode.permissions}" var="permission" varStatus="statusPermission">
                    ${permission.name}
                    <c:if test="${!statusPermission.last}">
                        +
                    </c:if>
                </c:forEach> )
                </c:if>

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
    </div>
    <p class="mandatoryFields">
        <fmt:message key="pei.permission.fullSchema.note"/>
    </p>

</c:if>

<script type="text/javascript">
    $(function() {
        $('#permission').focus();

        attachOnChangeToCompanies('${pageContext.request.contextPath}' +
                '/plan/PlanCMPermissions.action?planModuleType=${requestScope.actionBean.planModuleType}&loadCompanyContracts=');

        attachOnChangeToContracts();

        attachConfirmDelete("<fmt:message key="pei.permission.confirmDelete"/>");

        $('#togglePeiFolderPermissions').toggle(
                function (){
                    $('#peiFolderPermissions').hide();
                    $(this).html('<fmt:message key="pei.cm.show" />');
                    $(this).removeClass("open-section");
                    $(this).addClass("closed-section");

                },
                function (){
                    $('#peiFolderPermissions').show();
                    $(this).html('<fmt:message key="pei.cm.hide" />');
                    $(this).removeClass("closed-section");
                    $(this).addClass("open-section");
                }
                );
    });
</script>
