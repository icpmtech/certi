<%@ include file="../../../includes/taglibs.jsp" %>

<head>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/tree/jquery.dynatree.js"></script>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/tree/ui.dynatree.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/modal/modal.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/plan/${fn:toLowerCase(requestScope.actionBean.planModuleType)}.css"
          media="screen"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/iframe/iframe.outside.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/modal/jquery.simplemodal-1.2.3.js"></script>
</head>

<title><fmt:message key="menu.emergency"/> &gt; <fmt:message key="menu.pei.administration"/></title>


<h1><fmt:message key="menu.pei.administration"/></h1>

<s:errors/>
<s:messages/>

<div id="modalBoxCopyPEI" class="modalBox">
    <div class="header"><fmt:message key="pei.publish.waitPanel.header"/></div>
    <p><img src="${pageContext.request.contextPath}/images/ajax-loader.gif" alt="<fmt:message key="common.loading"/>"/>
    </p>

    <p><fmt:message key="pei.publish.waitPanel.footer"/></p>
</div>

<div id="modalBoxExportPEI" class="modalBox">
    <div class="header"><fmt:message key="pei.export.waitPanel.header"/></div>
    <p><img src="${pageContext.request.contextPath}/images/ajax-loader.gif" alt="<fmt:message key="common.loading"/>"/>
    </p>

    <p><fmt:message key="pei.export.waitPanel.footer"/></p>
</div>

<!-- export plan modal box-->
<div id="exportModalBox" class="modalBox"><!-- --></div>

<s:form action="/plan/PlanCM.action" class="form-pei-admin-select-pei" method="get" id="contractForm">
    <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>

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
    <input type="hidden" name="_eventName" value="viewPeiCM"/>
    <br/><br/>
</s:form>

<c:if test="${requestScope.actionBean.pei != null}">
    <div class="main-form-content">
        <div id="operationsPlanCMResize">
            <p>
                <img id="plus" title="<fmt:message key="common.expandTree"/>"
                     alt="<fmt:message key="common.expandTree"/>"
                     src="${pageContext.request.contextPath}/images/plus.gif"/>
            </p>

            <p>
                <img id="minus" title="<fmt:message key="common.collapseTree"/>"
                     alt="<fmt:message key="common.collapseTree"/>"
                     src="${pageContext.request.contextPath}/images/minus.gif" style="margin-left:0.1px"/>
            </p>
        </div>
        <div id="peiTreeContent" class="tree-small">
            <div class="links" id="operations" style="height:20px;padding-top:10px">
                <a href="javascript:insertFolderForm('${pageContext.request.contextPath}', '${requestScope.actionBean.planModuleType}');"
                   class="operationAdd"
                   id="addFolder">
                    <fmt:message key="common.folder"/></a>
                <ss:secure roles="peimanager">
                    <a href="javascript:insertFolderWithTemplate11MirrorForm('${pageContext.request.contextPath}', '${requestScope.actionBean.planModuleType}');"
                       class="operationAdd"
                       id="addFolderWithTemplate11Mirror">
                        <fmt:message key="common.link"/></a>
                </ss:secure>
                <a href="javascript:deleteFolder('<fmt:message key="folder.confirmDelete"/>', '${pageContext.request.contextPath}', '${requestScope.actionBean.planModuleType}');"
                   class="operationDelete"
                   id="deleteFolder">
                    <fmt:message key="common.delete"/></a>
            </div>

            <div id="tree">
                <c:choose>
                    <c:when test="${!requestScope.actionBean.openTreeDirectFolder}">
                        <ul>
                            <li data="addClass: 'strong'" id="${requestScope.actionBean.pei.path}"
                                class="folder expanded active">
                                    <c:out value="${requestScope.actionBean.pei.planName}"/>
                                <ul>
                                    <c:forEach items="${requestScope.actionBean.sections}" var="offlineFolder">
                                        <li data="addClass: 'strong ${pageScope.offlineFolder.cssToApply}'"
                                            id="${pageScope.offlineFolder.path}" class="folder lazy">
                                            <c:out value="${pageScope.offlineFolder.name}"/>
                                        </li>
                                    </c:forEach>
                                </ul>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <ul>
                        <c:choose>
                            <c:when test="${requestScope.actionBean.pei.path == requestScope.paths[0]}">
                                <li data="addClass: 'strong'" id="${requestScope.actionBean.pei.path}" class="folder expanded active">
                            </c:when>
                            <c:otherwise>
                                <li data="addClass: 'strong'" id="${requestScope.actionBean.pei.path}" class="folder expanded">
                            </c:otherwise>
                        </c:choose>
                        <c:out value="${requestScope.actionBean.pei.planName}"/>
                        <c:set var="lastDepth" value="1"/>
                        <ul>

                        <c:forEach items="${requestScope.actionBean.sections}" var="treeNode" varStatus="i">

                            <c:choose>
                                <c:when test="${treeNode.depth > lastDepth}">
                                    <ul>
                                </c:when>
                                <c:when test="${treeNode.depth < lastDepth}">
                                    <c:forEach begin="${treeNode.depth}" end="${lastDepth-1}">
                                        </li>
                                        </ul>
                                    </c:forEach>
                                    </li>
                                </c:when>
                                <c:when test="${!i.first}">
                                    </li>
                                </c:when>
                            </c:choose>

                            <c:forEach items="${requestScope.paths}" var="path" varStatus="j">
                                <c:choose>
                                    <c:when test="${path == pageScope.treeNode.path}">
                                        <c:choose>
                                            <c:when test="${j.last}">
                                                <c:choose>
                                                    <c:when test="${treeNode.depth == 1}">
                                                        <li data="addClass: 'strong ${pageScope.treeNode.cssToApply}'" id="${pageScope.treeNode.path}" class="folder expanded active">
                                                        <c:out value="${pageScope.treeNode.name}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <li data="addClass: '${pageScope.treeNode.cssToApply}' "id="${pageScope.treeNode.path}" class="folder expanded active">
                                                        <c:out value="${pageScope.treeNode.name}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <c:choose>
                                                    <c:when test="${treeNode.depth == 1}">
                                                        <li data="addClass: 'strong ${pageScope.treeNode.cssToApply}'" id="${pageScope.treeNode.path}" class="folder expanded">
                                                        <c:out value="${pageScope.treeNode.name}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <li data="addClass: '${pageScope.treeNode.cssToApply}'" id="${pageScope.treeNode.path}" class="folder expanded">
                                                        <c:out value="${pageScope.treeNode.name}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:set var="alreadySelectedPath" value="true"/>
                                    </c:when>
                                </c:choose>
                            </c:forEach>
                            <c:if test="${!pageScope.alreadySelectedPath}">
                                <c:choose>
                                    <c:when test="${treeNode.depth == 1}">
                                        <li data="addClass: 'strong ${pageScope.treeNode.cssToApply}'" id="${pageScope.treeNode.path}" class="folder lazy">
                                        <c:out value="${pageScope.treeNode.name}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <li data="addClass: '${pageScope.treeNode.cssToApply}'" id="${pageScope.treeNode.path}" class="folder lazy">
                                        <c:out value="${pageScope.treeNode.name}"/>
                                    </c:otherwise>
                                </c:choose>

                            </c:if>
                            <c:set var="alreadySelectedPath" value="false"/>


                            <c:set var="lastDepth" value="${treeNode.depth}"/>

                            <c:if test="${i.last}">

                                <c:forEach begin="1" end="${lastDepth-1}">
                                    </li>
                                    <!-- last special-->
                                    </ul>
                                </c:forEach>
                                </li>
                            </c:if>

                        </c:forEach>
                        </ul>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
            <br>
        </div>

        <iframe id="peiPropertyContent" class="autoHeight" scrolling="no" frameborder="0">
            <!-- do not remove--></iframe>
    </div>

    <script type="text/javascript">

        initTreePEIAdmin('${pageContext.request.contextPath}', '${requestScope.actionBean.planModuleType}');

    </script>
</c:if>

<script type="text/javascript">

    $(function() {
        /* set Focus on first form field*/
        $('#companies').focus();

        attachOnChangeToCompanies('${pageContext.request.contextPath}'
                + '/plan/PlanCM.action?planModuleType=${requestScope.actionBean.planModuleType}&loadCompanyContracts=');
        attachOnChangeToContracts();

        $('#minus').click(function () {
            //Reduce to
            if ($('#peiTreeContent').attr('class') == 'tree-large') {
                $('#peiTreeContent').removeClass('tree-large');
                $('#peiTreeContent').addClass('tree-small');
                $('#peiPropertyContent').width(710);
            } else if ($('#peiTreeContent').attr('class') == 'tree-small') {
                //Reduce to tree-none
                $('#peiTreeContent').removeClass('tree-small');
                $('#peiTreeContent').addClass('tree-none');
                $('#peiPropertyContent').width(960);
            }
        });
        $('#plus').click(function () {
            //Reduce to
            if ($('#peiTreeContent').attr('class') == 'tree-none') {
                $('#peiTreeContent').removeClass('tree-none');
                $('#peiTreeContent').addClass('tree-small');
                $('#peiPropertyContent').width(710);
            } else if ($('#peiTreeContent').attr('class') == 'tree-small') {
                //Reduce to tree-none
                $('#peiTreeContent').removeClass('tree-small');
                $('#peiTreeContent').addClass('tree-large');
                $('#peiPropertyContent').width(960);
            }
        });
    });

    <c:if test="${requestScope.actionBean.contractId != null}">
    var idMenu = '';
        <c:choose>
            <c:when test="${requestScope.actionBean.planModuleType == 'PEI'}">
                idMenu = "#sub_menu_pei_permissions";
            </c:when>
            <c:otherwise>
                idMenu = "#sub_menu_safety_permissions";
            </c:otherwise>
        </c:choose>

        $(idMenu).attr('href', '${pageContext.request.contextPath}/plan/PlanCMPermissions.action?companyId=${requestScope.actionBean.companyId}&contractId=${requestScope.actionBean.contractId}&planModuleType=${requestScope.actionBean.planModuleType}');
    </c:if>


</script>
