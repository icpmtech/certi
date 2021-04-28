<%@ include file="../../../includes/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Window-target" content="_top"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>

    <!-- CSS Imports-->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/default-style.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery.tabs.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery-ui-1.7.custom.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/tree/ui.dynatree.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/modal/modal.css"/>

    <!-- JavaScript Imports-->
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery.curvycorners.packed.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/scripts.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/datepicker-${pageContext.request.locale}.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/fckeditor/fckeditor.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/fckeditor-jquery/jquery.FCKEditor.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/tree/jquery.dynatree.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/iframe/iframe.inside.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/modal/jquery.simplemodal-1.2.3.js"></script>

    <script type="text/javascript">

        var alreadyLoadedAdditionalContent = false;
        var alreadyLoadedFolderMirrorReferences = false;

        <c:choose>
        <c:when test="${requestScope.actionBean.treeOperation == 'UPDATE_FOLDER'}">
        //Get active Node parent (new one not exists in tree)
        var tree = parent.$("#tree").dynatree("getTree");
        var parentsList = parent.$("#tree").dynatree("getActiveNode")._parentList(false, false);
        var parentNode = parentsList[parentsList.length - 1];

        if (parentNode.childList != null) {

            var childrenKeys = new Array();

            for (var i = 0; i < parentNode.childList.length; i++) {
                childrenKeys[i] = parentNode.childList[i].data.key;
            }

            /* Remove All parent Node children */
            for (var j = 0; j < childrenKeys.length; j++) {
                var node = tree.getNodeByKey(childrenKeys[j]);
                node.remove();

            }
            /* Update with new nodes*/
            parentNode.append(${requestScope.actionBean.changesInTree});
            parentNode.render(false, false);

            /* remove expand from new selected node*/
            var removeExpand = tree.getNodeByKey('${requestScope.actionBean.nodeUpdated.path}');
            removeExpand.expand(false);
        }

        </c:when>
        <c:when test="${requestScope.actionBean.treeOperation == 'PEI_UPDATE'}">
        //Get Active Tree Node
        var rootNode = parent.$("#tree").dynatree("getActiveNode");

        //Change node title
        rootNode.data.title = '${requestScope.actionBean.pei.planName}';

        //Apply changes
        rootNode.render(false, false);
        </c:when>
        <c:when test="${requestScope.actionBean.treeOperation == 'INSERT_FOLDER'}">

        //Parent Key
        var parentKey = '${requestScope.parentId}';

        //Get Parent Node
        parentNode = parent.$("#tree").dynatree("getTree").getNodeByKey(parentKey);

        //Apend new child Node
        parentNode.append(${requestScope.actionBean.changesInTree});

        //Activate parent
        parent.$("#tree").dynatree("getTree").activateKey('${requestScope.actionBean.folderId}');

        //Show folder management links visible
        parent.$('#addFolder').show();
        parent.$('#deleteFolder').show();
        parent.$('#addFolderWithTemplate11Mirror').show();


        <c:if test="${requestScope.actionBean.folderReferencesToUpdate != null && !empty requestScope.actionBean.folderReferencesToUpdate}">
        <c:forEach items="${requestScope.actionBean.folderReferencesToUpdate}" var="referenceToUpdate">
        var node = parent.$("#tree").dynatree("getTree").getNodeByKey("${pageScope.referenceToUpdate}");
        if (node != null) {
            node.data.addClass = node.data.addClass + " mirrorReferences";
            node.render(false);
        }

        </c:forEach>
        </c:if>
        </c:when>
        <c:when test="${requestScope.actionBean.treeOperation == 'DELETE_FOLDER'}">
        //Get to remove node
        var toRemoveNode = parent.$("#tree").dynatree("getTree").getNodeByKey('${requestScope.deletedFolder}');

        //Remove node
        toRemoveNode.remove();

        <c:if test="${requestScope.actionBean.folderReferencesToUpdate != null && !empty requestScope.actionBean.folderReferencesToUpdate}">
        <c:forEach items="${requestScope.actionBean.folderReferencesToUpdate}" var="referenceToUpdate">
        var node = parent.$("#tree").dynatree("getTree").getNodeByKey("${pageScope.referenceToUpdate}");
        if (node != null) {
            var cssToApply = node.data.addClass;
            cssToApply = cssToApply.replace(/mirrorReferences/g, "");
            cssToApply = cssToApply.replace(/template11Mirror-same-contract/g, "");
            cssToApply = cssToApply.replace(/template11Mirror-different-contract/g, "");
            cssToApply = cssToApply.replace(/no-image/g, "");
            node.data.addClass = cssToApply + " no-image";
            node.render(false);
        }

        </c:forEach>
        </c:if>

        //Activate parent
        parent.$("#tree").dynatree("getTree").activateKey('${requestScope.parentFolder}');
        </c:when>
        </c:choose>
    </script>
</head>
<body>
<div class="hidden" id="PEICMContent">
    <!-- set Focus in correct field-->
    <c:choose>
        <c:when test="${requestScope.actionBean.insertFolderFlag || requestScope.actionBean.loadTemplate}">
            <c:set var="focusField" value="folder.name"/>
        </c:when>
        <c:otherwise>
            <c:set var="focusField" value="pei.planName"/>
        </c:otherwise>
    </c:choose>

    <s:form beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMOperationsActionBean"
            id="peiFolderForm"
            focus="${pageScope.focusField}" onsubmit="setInRequestSelectedTab();" enctype="multipart/form-data"
            method="POST">

    <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>

    <s:hidden name="peiId">${requestScope.actionBean.peiId}</s:hidden>
    <s:hidden name="folder.path">${requestScope.folder.path}</s:hidden>
    <s:hidden name="tabToOpen" id="tabToOpen"/>
    <s:hidden name="exportId" id="exportid"/>
    <s:hidden name="online" id="online"/>
    <s:hidden name="exportOnline" id="exportOnline"/>

    <input type="hidden" name="_eventName" id="eventName"/>

    <div id="tabs" class="alignLeft">

        <ul>
            <li><a accesskey="p" href="#folderProperty"><fmt:message key="pei.tabs.properties"/></a></li>

            <c:if test="${requestScope.actionBean.loadTemplate}">
                <li><a href="#templateProperty" accesskey="c"><fmt:message key="pei.tabs.content"/></a></li>
                <li>
                    <a accesskey="t" id="additionalTemplateTextTab" href="#additionalTemplateText"><fmt:message
                            key="pei.tabs.additionalContent"/></a>
                </li>
            </c:if>

            <li class="pei-admin-operation-bar-top">
                <c:choose>
                    <c:when test="${requestScope.actionBean.loadTemplate}">
                        <s:submit name="validateInsertFolder" id="saveForm"
                                  class="button pei-admin-operation-button buttonGreen"><fmt:message
                                key="common.submit"/></s:submit>
                    </c:when>
                    <c:otherwise>
                        <s:submit name="updatePEIFields" id="saveForm"
                                  class="button pei-admin-operation-button buttonGreen"><fmt:message
                                key="common.submit"/></s:submit>
                    </c:otherwise>
                </c:choose>
            </li>
        </ul>

        <div class="errors-custom">
            <s:errors/>
            <s:messages/>
        </div>

        <div id="folderProperty">
            <c:choose>
                <c:when test="${requestScope.actionBean.loadTemplate}">
                    <%@ include file="planCMFolderProperties.jsp" %>
                </c:when>
                <c:otherwise>
                    <%@ include file="planCMRootProperties.jsp" %>
                </c:otherwise>
            </c:choose>
        </div>

        <c:if test="${requestScope.actionBean.loadTemplate}">
            <div id="templateProperty">
                <c:choose>
                    <c:when test="${requestScope.actionBean.insertFolderLink}">
                        <%@ include file="planCMFolderWithTemplate11Mirror.jsp" %>
                    </c:when>
                    <c:otherwise>
                        <%@ include file="planCMFolderTemplate.jsp" %>
                    </c:otherwise>
                </c:choose>
            </div>


            <div id="additionalTemplateText">
                <c:if test="${requestScope.actionBean.loadAdditionalExportInfo}">
                    <%@ include file="planCMFolderAdditionalContent.jsp" %>
                </c:if>
            </div>
        </c:if>

        <div class="pei-admin-operation-bar-bottom">
            <c:choose>
                <c:when test="${requestScope.actionBean.loadTemplate}">
                    <s:submit name="validateInsertFolder"
                              class="button floatRight pei-admin-operation-button buttonGreen"
                              style="margin-left:9px"><fmt:message
                            key="common.submit"/></s:submit>
                </c:when>
                <c:otherwise>
                    <s:submit name="updatePEIFields" class="button floatRight pei-admin-operation-button buttonGreen"
                              style="margin-left:9px"><fmt:message
                            key="common.submit"/></s:submit>
                </c:otherwise>
            </c:choose>

            <s:button name="copyOfflineToOnline" id="copyOfflineToOnlineBottom"
                      class="button floatLeft pei-admin-operation-button buttonOrange"><fmt:message
                    key="common.publish"/></s:button>

            <s:button name="export" id="export"
                      class="button floatLeft pei-admin-operation-button"><fmt:message
                    key="common.export"/></s:button>

            <s:button name="preview" id="preview" class="button floatLeft pei-admin-operation-button"
                      style="width:100px;"><fmt:message
                    key="pei.preview"/></s:button>
            <s:button name="view" id="view" class="button floatLeft pei-admin-operation-button"
                      style="width:80px;"><fmt:message key="pei.view"/></s:button>

        </div>
        </s:form>
    </div>
    <div id="pei-info">
        <%@ include file="planCMFolderMainContentFooter.jsp" %>
    </div>
</div>
<script type="text/javascript">

    var firstLoad = true;
    $(document).ready(function() {

        //Add event to load additional info when is a folder
    <c:if test="${requestScope.actionBean.loadTemplate && requestScope.actionBean.loadAdditionalExportInfo==null}">
        $('#additionalTemplateTextTab').click(function() {
            loadAdditionalFolderText('${pageContext.request.contextPath}', ${requestScope.actionBean.insertFolderFlag}, '${requestScope.actionBean.planModuleType}');
        });
    </c:if>
        var $tabs = $("#tabs").tabs({
            show: function(event, ui) {
                if (!firstLoad) {
                    //Refresh iFrame
                    iResizeInsideIFrame();
                }
            }});
        firstLoad = false;

    <c:if test="${requestScope.actionBean.tabToOpen != null}">
    <c:choose>
    <c:when test="${requestScope.actionBean.loadAdditionalExportInfo}">
        changeSelectedTab($tabs, ${requestScope.actionBean.tabToOpen}, '${pageContext.request.contextPath}', ${requestScope.actionBean.insertFolderFlag}, false, '${requestScope.actionBean.planModuleType}');
    </c:when>
    <c:otherwise>
        changeSelectedTab($tabs, ${requestScope.actionBean.tabToOpen}, '${pageContext.request.contextPath}', ${requestScope.actionBean.insertFolderFlag}, true, '${requestScope.actionBean.planModuleType}');
    </c:otherwise>
    </c:choose>
    </c:if>

    <c:choose>
    <c:when test="${requestScope.doNotPublish}">
        disableOrEnablePEIButtons('copyOfflineToOnlineBottom', false);
    </c:when>
    <c:otherwise>
        disableOrEnablePEIButtons('copyOfflineToOnlineBottom', true);
    </c:otherwise>
    </c:choose>

    <c:choose>
    <c:when test="${(requestScope.actionBean.insertFolderFlag != null && requestScope.actionBean.insertFolderFlag) || (requestScope.actionBean.insertFolderLink != null && requestScope.actionBean.insertFolderLink)}">
        disableOrEnablePEIButtons('preview', false);
        disableOrEnablePEIButtons('view', false);
    </c:when>
    <c:otherwise>
        disableOrEnablePEIButtons('preview', true);
        disableOrEnablePEIButtons('view', true);
    </c:otherwise>
    </c:choose>
    <c:if test="${requestScope.actionBean.insertFolderLink}">
        $tabs.tabs('select', 1);
        $('#tabs').tabs('disable', 0);
        $('#tabs').tabs('disable', 2);
    </c:if>
        changeTemplateTabStatus('<%=Template.Type.TEMPLATE_INDEX.getName()%>', '<%=Template.Type.TEMPLATE_FAQ.getName()%>', '<%=Template.Type.TEMPLATE_DOCUMENTS.getName()%>', '<%=Template.Type.TEMPLATE_CONTACTS.getName()%>', '<%=Template.Type.TEMPLATE_PROCEDURE.getName()%>', '<%=Template.Type.TEMPLATE_MEANS_RESOURCES.getName()%>');
        attachOnChangeToTemplate('<%=Template.Type.TEMPLATE_INDEX.getName()%>', '<%=Template.Type.TEMPLATE_FAQ.getName()%>', '<%=Template.Type.TEMPLATE_DOCUMENTS.getName()%>', '<%=Template.Type.TEMPLATE_CONTACTS.getName()%>', '<%=Template.Type.TEMPLATE_PROCEDURE.getName()%>', '<%=Template.Type.TEMPLATE_MEANS_RESOURCES.getName()%>');
        attachEventToCopyPEI('copyOfflineToOnlineBottom', '${pageContext.request.contextPath}', '<fmt:message key="folder.copy"/>', '${requestScope.actionBean.planModuleType}');
        attachEventToExportPlan('export', '${pageContext.request.contextPath}', '${requestScope.actionBean.planModuleType}')
        attachEventToPEIPreview('${pageContext.request.contextPath}', '${requestScope.actionBean.peiId}', '${requestScope.actionBean.folder.path}', '${requestScope.actionBean.planModuleType}');
        attachEventToPEIView('${pageContext.request.contextPath}', '${requestScope.actionBean.peiId}', '${requestScope.actionBean.folder.path}', '${requestScope.actionBean.planModuleType}');
        $('#PEICMContent').show();

        attachOnChangeToTemplates('${pageContext.request.contextPath}', '${requestScope.actionBean.planModuleType}');
        attachOnChangeToTemplates('${pageContext.request.contextPath}', '${requestScope.actionBean.planModuleType}');

    <c:if test="${requestScope.actionBean.blockContentTab != null && requestScope.actionBean.blockContentTab}">
        $('#tabs').tabs('disable', 1);
    </c:if>
    });
</script>
</body>
</html>
