<%@ include file="../../../includes/taglibs.jsp" %>

<head>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery.tooltip.css"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery.tooltip.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery.dimensions.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/effects.core.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/effects.blind.js"></script>

</head>

<title><fmt:message key="legislation.category.title"/></title>


<div id="main-content">
<div class="leftColumn" style="width:660px">

<h1 style="width:660px"><fmt:message key="legislation.category.title"/></h1>

<p>
    <c:forEach items="${requestScope.actionBean.legalDocumentCategoryNavegation}" var="navigation"
               varStatus="index">

        <c:if test="${index.index == 1}">
            <label style="margin-left:-3px;margin-right:5px;">:</label>
        </c:if>

        <c:if test="${index.index >= 2}">
            >
        </c:if>
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                event="viewLegislationCategory">

            <c:out value="${navigation.name}"/>
            <s:param name="categoryId"
                     value="${navigation.id}"/>
            <s:param name="depth"
                     value="${navigation.depth+1}"/>
        </s:link>


        <c:if test="${index.index == (fn:length(requestScope.actionBean.legalDocumentCategoryNavegation)-1)}">
            <c:set var="selectedCategory" value="${navigation}"/>
        </c:if>
    </c:forEach>

</p>

<p class="selectedCategoryLine">

                <span class="left">
                    <c:out value="${pageScope.selectedCategory.name}"/>
                    <c:if test="${pageScope.selectedCategory.allAssociatedDocumentsCounter != null}">
                        <ss:secure roles="legislationmanager">
                            (${pageScope.selectedCategory.allAssociatedDocumentsCounter})
                            <c:set var="isLegislationManager" value="true"/>
                        </ss:secure>
                        <c:if test="${!pageScope.isLegislationManager}">
                            (${pageScope.selectedCategory.activeAssociatedDocumentsCounter})
                        </c:if>
                        <c:set var="isLegislationManager" value="false"/>
                    </c:if>
               </span>
    <c:choose>
        <c:when test="${requestScope.actionBean.subscribed != null && requestScope.actionBean.subscribed}">
                        <span class="right subscribed">
                                <fmt:message
                                        key="legislation.category.alreadysubscribed"/> ${pageScope.selectedCategory.name}: <s:link
                                beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                                event="unSubscribeCategory">
                            <fmt:message key="common.cancel"/>
                            <s:param name="categoryId" value="${pageScope.selectedCategory.id}"/>
                            <s:param name="depth" value="${pageScope.selectedCategory.depth+1}"/>
                        </s:link>
                        </span>
        </c:when>
        <c:when test="${requestScope.actionBean.subscribed != null && !requestScope.actionBean.subscribed && fn:length(requestScope.actionBean.legalDocumentCategoryNavegation) != 1}">
                         <span class="right not-subscribed">
                                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                                        event="subscribeCategory">
                                    <fmt:message
                                            key="legislation.category.newsubscribe"/>: ${pageScope.selectedCategory.name}
                                    <s:param name="categoryId" value="${pageScope.selectedCategory.id}"/>
                                    <s:param name="depth" value="${pageScope.selectedCategory.depth+1}"/>
                                </s:link>
                        </span>
        </c:when>
    </c:choose>
</p>

<p class="separatorLine cleaner" style="margin-bottom:0;">&nbsp;</p>

<p class="alignRight" style="margin-top:0">
    <a href="#" id="subscribedCategoriesLink" class="font-11px closed-section" style="color:#405C6C;">
        <fmt:message key="legislation.category.subscribed.list"/>
    </a>
</p>

<div class="hidden" id="subscribedCategories">
    <c:choose>
        <c:when test="${fn:length(requestScope.actionBean.userSubscriptions) > 0}">

            <ul class="alignRight" style="list-style:none">
                <c:forEach items="${requestScope.actionBean.userSubscriptions}" var="userSubscriptions">
                    <li>
                        <p style="margin-bottom:0;">

                            <label class="font-13px" style="background-color:#EDEDED;">
                                <c:forEach items="${userSubscriptions}" var="category" varStatus="index">

                                    <c:if test="${index.index >=2}">
                                        >
                                    </c:if>

                                    <c:if test="${index.index != 0}">
                                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                                                event="viewLegislationCategory">

                                            <c:out value="${category.name}"/>
                                            <s:param name="categoryId"
                                                     value="${category.id}"/>
                                            <s:param name="depth"
                                                     value="${category.depth+1}"/>
                                        </s:link>
                                    </c:if>

                                    <c:if test="${index.index == (fn:length(userSubscriptions)-1)}">
                                        <c:set var="selectedCategoryTable" value="${category}"/>
                                    </c:if>

                                </c:forEach>
                            </label>
                        </p>

                        <p style="margin-top:0">
                            <label class="font-11px">
                                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                                        event="unSubscribeCategory">
                                    <fmt:message key="common.cancel"/>
                                    <s:param name="categoryId"
                                             value="${pageScope.selectedCategoryTable.id}"/>
                                    <s:param name="depth"
                                             value="${pageScope.selectedCategoryTable.depth+1}"/>
                                </s:link>
                            </label>

                        </p>
                    </li>
                </c:forEach>
            </ul>
        </c:when>
        <c:otherwise>
            <p class="alignRight" id=""><fmt:message
                    key="legislation.category.nosubscriptions"/></p>
        </c:otherwise>
    </c:choose>

    <p class="separatorLine" style="margin-bottom:0;margin-top:5px;">&nbsp;</p>

</div>
<table class="categoriesTable cleaner" style="width:100%">
    <tr>

        <c:set var="counterArray" value="0"/>
        <c:set var="counter" value="1"/>
        <c:forEach items="${requestScope.actionBean.documentCategoryList}" var="documentCategory">

            <c:if test="${counter == 1}">
                <td>
            </c:if>

            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                    event="viewLegislationCategory">
                ${documentCategory.name}
                <s:param name="categoryId" value="${documentCategory.id}"/>
                <s:param name="depth" value="${documentCategory.depth+1}"/>
            </s:link>

            <ss:secure roles="legislationmanager">
                (${documentCategory.allAssociatedDocumentsCounter})
                <c:set var="isLegislationManager2" value="true"/>
            </ss:secure>
            <c:if test="${!pageScope.isLegislationManager2}">
                (${documentCategory.activeAssociatedDocumentsCounter})
            </c:if>
            <c:set var="isLegislationManager2" value="false"/>
            <br/>

            <c:choose>
                <c:when test="${counter == requestScope.actionBean.categoriesPerColumn[counterArray]}">
                    <c:set var="counterArray" value="${counterArray+1}"/>
                    <c:set var="counter" value="1"/>
                    </td>
                </c:when>
                <c:otherwise>
                    <c:set var="counter" value="${counter+1}"/>
                </c:otherwise>
            </c:choose>

        </c:forEach>
        <c:if test="${fn:length(requestScope.actionBean.documentCategoryList) <=2}">
            <td>
                &nbsp;
            </td>
        </c:if>
    </tr>
</table>

<p class="separatorLine" style="margin-bottom:0;">&nbsp;</p>

<c:if test="${fn:length(requestScope.actionBean.legalDocumentCategoryNavegation) != 1}">
    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
            event="viewLegislations" class="operationManagement cleaner">
        <fmt:message key="legislation.category.seeAll"/>
        <s:param name="categoryId" value="${pageScope.selectedCategory.id}"/>
    </s:link>
</c:if>

</div>
<%@ include file="../../../includes/legislationRightContent.jsp" %>
</div>

<script type="text/javascript">

    $("#subscribedCategoriesLink").click(function() {

        if ($('#subscribedCategories').is(':visible')) {
            $("#subscribedCategoriesLink").attr("class", "closed-section font-11px")
            $("#subscribedCategories").hide('blind');


        } else {
            $("#subscribedCategoriesLink").attr("class", "open-section font-11px")
            $("#subscribedCategories").show('blind');
        }

        return false;
    });

</script>