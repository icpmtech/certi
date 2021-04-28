<%@ include file="../../../includes/taglibs.jsp" %>

<head>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery.pagination.js"></script>
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


<s:messages/>
<s:errors/>


<div class="leftColumn">

<c:choose>
    <c:when test="${requestScope.actionBean.category != null}">
        <title><fmt:message key="legislation.search.category.title"/>: ${requestScope.actionBean.category.name}</title>

        <h1 id="title" style="width:670px;"><fmt:message
                key="legislation.search.category.title"/>: ${requestScope.actionBean.category.name}</h1>
    </c:when>
    <c:otherwise>
        <title><fmt:message key="legislation.title"/></title>

        <h1 id="title" style="width:670px;"><fmt:message key="legislation.title"/></h1>
    </c:otherwise>
</c:choose>

<ss:secure roles="legislationmanager">
    <div class="links" style="width:640px;">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                event="insertLegislationForm" class="operationAdd">
            <fmt:message key="common.add"/>
        </s:link>

        <display:table list="${requestScope.actionBean.legalDocumentsToExport}" export="true" id="row"
                       requestURI="/legislation/Legislation.action?exportLegislation" excludedParams="viewLegislations"
                       class="hidden"
                       decorator="com.criticalsoftware.certitools.presentation.util.LegalDocumentExportDecorator">
            <display:column property="id" media="html"/>
            <display:column titleKey="table.legislation.firstCategory" property="firstCategory" media="excel"/>
            <display:column titleKey="table.legislation.secondCategory" property="secondCategory" media="excel"/>
            <display:column titleKey="table.legislation.thirdCategory" property="thirdCategory" media="excel"/>
            <display:column titleKey="table.legislation.fullDrTitle" property="fullDrTitle" media="excel"/>
            <display:column titleKey="legislation.add.title" property="customTitle" media="excel"/>
            <display:column titleKey="legislation.add.type" property="documentType.name" media="excel"/>
            <display:column titleKey="legislation.add.number" property="number" media="excel"/>
            <display:column titleKey="legislation.add.publishdate" property="publicationDate" media="excel"/>
            <display:column titleKey="legislation.add.keywords" property="keywords" media="excel"/>
            <display:column titleKey="legislation.add.summary" property="summary" media="excel"/>
            <display:column titleKey="legislation.add.customAbstract" property="customAbstract" media="excel"/>
            <display:column titleKey="legislation.add.transitoryProvisions" property="transitoryProvisions"
                            media="excel"/>
            <display:column titleKey="legislation.add.state" property="documentState.name" media="excel"/>
            <display:column titleKey="legislation.add.active" property="published" media="excel"/>
            <display:column titleKey="legislation.add.associatedLegalDocuments" property="associatedLegalDocuments"
                            media="excel"/>
            <display:column titleKey="legislation.add.legalComplianceValidation" property="legalComplianceValidation"
                            media="excel"/>
            <display:column titleKey="legislation.add.referenceArticles" property="referenceArticles"
                            media="excel"/>
            <display:setProperty name="export.xml" value="false"/>
            <display:setProperty name="export.csv" value="false"/>
            <display:setProperty name="export.pdf" value="false"/>
            <display:setProperty name="export.banner.sepchar" value=" "/>
            <display:setProperty name="export.banner">
                <span id="legislationExport">{0}</span>
            </display:setProperty>
            <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
        </display:table>
    </div>
</ss:secure>

<div id="searchLegislation">
    <c:if test="${requestScope.actionBean.category == null}">

        <form method="get" action="/legislation/Legislation.action" style="margin-bottom:0">
            <input class="largeInput" type="text" name="searchLegislation"
                   value="<c:out value="${requestScope.actionBean.searchLegislation}"/>" id="searchField"/>
            <input value="<fmt:message key="common.search"/>" class="button" type="submit"/>
        </form>

        <script type="text/javascript">
            setFocus('searchField');
        </script>

    <span class="examples" style="margin-top:0">
           <fmt:message key="legislation.search.examples"/> :
           <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                   event="viewLegislations">
               ${applicationScope.configuration.searchExample1}
               <s:param name="searchLegislation" value="${applicationScope.configuration.searchExample1}"/>
           </s:link>
                ,
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                        event="viewLegislations">
                    ${applicationScope.configuration.searchExample2}
                    <s:param name="searchLegislation" value="${applicationScope.configuration.searchExample2}"/>
                </s:link>
    </span>
    </c:if>

            <span class="searchByCategory">
                 <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                         event="viewLegislationCategory">
                     <fmt:message key="legislation.search.category"/>
                 </s:link>
            </span>

</div>
<hr class="floatLeft cleaner line-red" style="width:650px;"/>

<c:if test="${requestScope.actionBean.legalDocuments.list == null}">

    <p class="alignRight" style="margin-top:0;width: 650px;">
        <a href="#" id="subscribedCategoriesLink" class="font-11px closed-section" style="color:#405C6C;">
            <fmt:message key="legislation.category.subscribed.list"/>
        </a>
    </p>

    <div class="hidden" id="subscribedCategories" style="width: 650px;">
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

    <table class="categoriesTable" style="width:100%">
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
    <p class="separatorLine cleaner" style="margin-bottom:0;margin-top:5px;width: 650px;">&nbsp;</p>
</c:if>
<!-- Search Results-->
<c:if test="${requestScope.actionBean.legalDocuments.list != null}">
    <div class="searchResults">

        <div class="header floatLeft cleaner">

        <span class="floatRight alignRight">

          ${requestScope.actionBean.legalDocuments.fullListSize} <fmt:message key="legislation.search.results.founded"/>
          <c:if test="${requestScope.actionBean.legalDocuments.list != null && fn:length(requestScope.actionBean.legalDocuments.list) > 0}">
              <p class="alignRight font-13px color-black" style="margin:0">
                      ${((requestScope.actionBean.page-1)*applicationScope.configuration.pageListSize)+1}
                  - ${((requestScope.actionBean.page-1)*applicationScope.configuration.pageListSize)+1 + fn:length(requestScope.actionBean.legalDocuments.list) - 1}
                  <fmt:message
                          key="legislation.search.presented"/>
              </p>
          </c:if>
        </span>
        <span>
            <fmt:message key="legislation.search.results"/> 
        </span>
        </div>
        <span class="separator" style="margin-bottom:0">&nbsp;</span>

        <div class="result">
            <c:forEach items="${requestScope.actionBean.legalDocuments.list}"
                       var="legalDocument">

                <p style="margin-bottom:0">
                    <span class="floatRight alignRight" style="margin-left:30px">
                            <label class="viewResultGreyColor font-13px">
                                <fmt:message key="legislation.search.results.state"/>:
                            </label>
                            <label class="strong font-13px"><c:out
                                    value="${pageScope.legalDocument.documentState.name}"/>
                            </label>

                            <ss:secure roles="legislationmanager">
                                <label class="viewResultGreyTextColor font-11px">
                                    (
                                    <c:choose>

                                        <c:when test="${pageScope.legalDocument.published}">
                                            <fmt:message key="legislation.search.results.active"/>
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:message key="legislation.search.results.notactive"/>
                                        </c:otherwise>
                                    </c:choose>
                                    )
                                </label>
                            </ss:secure>
                        </span>

                    <label class="font-15px">
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                                event="viewLegislation">
                            <s:param name="legalDocument.id" value="${pageScope.legalDocument.id}"/>
                            <c:out value="${pageScope.legalDocument.fullDrTitle}"/>
                        </s:link>
                    </label>
                </p>

                <p class="cleaner justify" style="margin-top:5px;margin-bottom:0">
                    <c:out value="${pageScope.legalDocument.customTitle}"/>
                </p>

                <p class="justify" style="color:#AFAFAF;margin-top:5px;margin-bottom:0">
                    <c:out value="${pageScope.legalDocument.summary}"/>

                </p>

                <p class="alignRight" style="margin-bottom:0;margin-top:5px">
                    <c:forEach items="${pageScope.legalDocument.categoryNavegation}" var="categoryNavegationList">

                        <c:forEach items="${pageScope.categoryNavegationList}" var="categoryNavegation"
                                   varStatus="index">
                            <c:if test="${index.index >=2}">
                                >
                            </c:if>
                            <c:if test="${index.index != 0}">
                                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                                        event="viewLegislationCategory">

                                    <c:out value="${categoryNavegation.name}"/>
                                    <s:param name="categoryId"
                                             value="${categoryNavegation.id}"/>
                                    <s:param name="depth"
                                             value="${categoryNavegation.depth+1}"/>
                                </s:link>
                            </c:if>
                        </c:forEach>
                        <br/>
                    </c:forEach>
                </p>

                <p class="separator">&nbsp;</p>
            </c:forEach>
        </div>
    </div>
</c:if>

<div id="Pagination" class="pagination">&nbsp;</div>


</div>

<%@ include file="../../../includes/legislationRightContent.jsp" %>


<c:choose>
    <c:when test="${requestScope.actionBean.categoryId != null}">
        <c:set var="link"
               value="${pageContext.request.contextPath}/legislation/Legislation.action?viewLegislations=&categoryId=${requestScope.actionBean.categoryId}"/>
    </c:when>
    <c:otherwise>
        <c:set var="link"
               value="${pageContext.request.contextPath}/legislation/Legislation.action?viewLegislations=&searchLegislation=${requestScope.actionBean.searchLegislation}"/>
    </c:otherwise>
</c:choose>

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

    function handlePaginationClick(new_page_index, pagination_container) {
    }

    <c:if test="${requestScope.actionBean.legalDocuments.list != null && fn:length(requestScope.actionBean.legalDocuments.list) > 0}">
    $("#Pagination").pagination(${requestScope.actionBean.legalDocuments.fullListSize}, {
        next_text:'<fmt:message key="pagination.next"/>',
        prev_text:'<fmt:message key="pagination.previous"/>',
        num_edge_entries:1,
        num_display_entries:10,
        current_page:${requestScope.actionBean.page-1},
        items_per_page:${applicationScope.configuration.pageListSize},
        callback:handlePaginationClick,
        link_to:'${pageScope.link}&page=__id__&#title'

    });
    </c:if>
</script>
