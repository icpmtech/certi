<%@ include file="../../../includes/taglibs.jsp" %>

<head>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery.tooltip.css"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery.tooltip.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery.dimensions.js"></script>
</head>

<s:messages/>
<s:errors/>


<div class="leftColumn" style="width:650px;padding-right:10px">

<ss:secure roles="legislationmanager">

    <p class="alignLeft links" style="margin-bottom:0;margin-top:40px">

        <c:if test="${requestScope.showBack}">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                    event="back" class="operationBack">
                <fmt:message key="common.back"/>
            </s:link>
        </c:if>
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                event="updateLegislationForm" class="operationEdit">
            <fmt:message key="common.edit"/>
            <s:param name="legalDocument.id" value="${requestScope.actionBean.legalDocument.id}"/>
        </s:link>

        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                event="deleteLegislation" class="operationDelete confirmDelete">
            <fmt:message key="common.delete"/>
            <s:param name="legalDocument.id" value="${requestScope.actionBean.legalDocument.id}"/>
        </s:link>

        <span class="strong color-black font-13px">
            <c:choose>
                <c:when test="${requestScope.actionBean.legalDocument.published}">
                    (<fmt:message key="legislation.search.results.active"/>)
                </c:when>
                <c:otherwise>
                    (<fmt:message key="legislation.search.results.notactive"/>)
                </c:otherwise>
            </c:choose>
        </span>
    </p>

</ss:secure>

<title><c:out value="${requestScope.actionBean.legalDocument.fullDrTitle}"/></title>

<h1 style="padding-bottom:0;margin-bottom:4px; width:650px">
    <c:out value="${requestScope.actionBean.legalDocument.fullDrTitle}"/>
</h1>

<p class="justify">
    <c:out value="${requestScope.actionBean.legalDocument.customTitle}"/>
</p>

<p class="separatorLine" style="margin-top:10px">&nbsp;</p>

<p>

                <span class="floatRight alignRight" style="padding-bottom:10px;width:50%">

                    <c:choose>
                        <c:when test="${requestScope.actionBean.canDownload == null}">

                            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                                    event="downloadFile" class="operationDownload-auth">
                                <s:param name="legalDocument.id" value="${requestScope.actionBean.legalDocument.id}"/>
                                <fmt:message
                                        key="legislation.view.consult"/> ${requestScope.actionBean.legalDocument.documentType.name}
                            </s:link>
                        </c:when>
                        <c:otherwise>
                            <label class="operationDownload-non-auth">
                                <fmt:message
                                        key="legislation.view.consult"/> ${requestScope.actionBean.legalDocument.documentType.name}
                            </label>

                        </c:otherwise>
                    </c:choose>
                    <br/>
                    <label class="color-label"><fmt:message key="legislation.view.pdf"/></label>
                </span>

            <span class="alignLeft">
               <label class="color-label"><fmt:message key="legislation.add.state"/>: </label>
               <label class="strong color-black">${requestScope.actionBean.legalDocument.documentState.name}</label>
               <br/>
               <label class="color-label" style="margin-top:3px"><fmt:message key="legislation.add.keywords"/>: </label>
               <label class="viewResultGreyTextColor">
                   <c:choose>
                       <c:when test="${requestScope.actionBean.keywords == null || fn:length(requestScope.actionBean.keywords) == 0}">
                           <fmt:message key="legislation.general.nothing"/>
                       </c:when>
                       <c:otherwise>
                           <c:forEach items="${requestScope.actionBean.keywords}" var="keyword" varStatus="index">

                               <c:if test="${index.index != 0}">, </c:if>
                               <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                                       event="viewLegislations">
                                   <s:param name="searchLegislation" value="${keyword}"/>
                                   <c:out value="${keyword}"/>
                               </s:link>
                           </c:forEach>
                       </c:otherwise>
                   </c:choose>
               </label>
            </span>
</p>

<p class="color-label"><fmt:message key="legislation.add.category"/>:</p>

<ul class="arrow-list" style="margin-top:5px;">
    <c:forEach items="${requestScope.actionBean.legaDocumentCategoryNavigation}" var="rows">
        <li>
            <c:forEach items="${rows}" var="category" varStatus="index">

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
            </c:forEach>
        </li>
    </c:forEach>
</ul>

<p class="color-label fieldsetsLabels">
    <fmt:message key="legislation.add.summary"/>:
</p>

<div class="fieldsets" style="background-color:#EDEDED;">
    <c:choose>
        <c:when test="${requestScope.actionBean.legalDocument.summary == '' || requestScope.actionBean.legalDocument.summary == null}">
            <fmt:message key="legislation.general.nothing"/>
        </c:when>
        <c:otherwise>
            ${requestScope.actionBean.legalDocument.summary}
        </c:otherwise>
    </c:choose>
</div>


<p class="color-label fieldsetsLabels">
    <fmt:message key="legislation.add.customAbstract"/>:
</p>

<div class="fieldsets" style="padding-top: 0; padding-bottom: 0;">
    <c:choose>
        <c:when test="${requestScope.actionBean.legalDocument.customAbstract == '' || requestScope.actionBean.legalDocument.customAbstract == null}">
            <p><fmt:message key="legislation.general.nothing"/></p>
        </c:when>
        <c:otherwise>
            ${requestScope.actionBean.legalDocument.customAbstract}
        </c:otherwise></c:choose>
</div>

<p class="color-label fieldsetsLabels">
    <fmt:message key="legislation.add.transitoryProvisions"/>:
</p>

<div class="fieldsets" style="padding-top: 0; padding-bottom: 0;">
    <c:choose>
        <c:when test="${requestScope.actionBean.legalDocument.transitoryProvisions == '' || requestScope.actionBean.legalDocument.transitoryProvisions == null}">
            <p><fmt:message key="legislation.general.nothing"/></p>
        </c:when>
        <c:otherwise>
            ${requestScope.actionBean.legalDocument.transitoryProvisions}
        </c:otherwise>
    </c:choose>
</div>

<ss:secure roles="legislationmanager">
    <c:if test="${requestScope.actionBean.legalDocument.legalComplianceValidation != null && requestScope.actionBean.legalDocument.legalComplianceValidation != ''}">
        <p class="color-label fieldsetsLabels">
            <fmt:message key="legislation.add.legalComplianceValidation"/>:
        </p>

        <div class="fieldsets" style="padding-top: 0; padding-bottom: 0;">
                ${requestScope.actionBean.legalDocument.legalComplianceValidation}
        </div>
    </c:if>

    <c:if test="${requestScope.actionBean.legalDocument.referenceArticles != null && requestScope.actionBean.legalDocument.referenceArticles != ''}">
        <p class="color-label fieldsetsLabels">
            <fmt:message key="legislation.add.referenceArticles"/>:
        </p>

        <div class="fieldsets" style="padding-top: 0; padding-bottom: 0;">
                ${requestScope.actionBean.legalDocument.referenceArticles}
        </div>
    </c:if>
</ss:secure>

<p class="color-label">
    <fmt:message key="legislation.view.associatedLegalDocuments"/>:
</p>

<ul class="arrow-list" style="margin-top:5px;">
    <c:forEach items="${requestScope.actionBean.legalDocument.associatedLegalDocuments}"
               var="associatedLegalDocument">

        <c:set var="text">
            <c:out value="${associatedLegalDocument.fullDrTitle}" escapeXml="true"/> - <c:out
                value="${associatedLegalDocument.summary}" escapeXml="true"/>
        </c:set>

        <li class="justify" style="margin-bottom:10px;">
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                    event="viewLegislation" id="associatedLegalDocument${associatedLegalDocument.id}" title="${text}">
                <s:param name="legalDocument.id" value="${associatedLegalDocument.id}"/>
                <c:out value="${associatedLegalDocument.fullDrTitle}"/>
            </s:link>
            <br/>
            <label class="viewResultGreyTextColor">
                <c:out value="${associatedLegalDocument.customTitle}"/>
            </label>
        </li>

        <script type="text/javascript">
            $(function() {
                $("#associatedLegalDocument${associatedLegalDocument.id}").tooltip({
                    track: true,
                    delay: 0,
                    showURL: false,
                    opacity: 0,
                    fixPNG: true,
                    showBody: " - ",
                    fade: 250,
                    extraClass:'tooltip-width'
                });
            });
        </script>

    </c:forEach>
    <c:if test="${requestScope.actionBean.legalDocument.associatedLegalDocuments == null || fn:length(requestScope.actionBean.legalDocument.associatedLegalDocuments) == 0}">
        <li class="viewResultGreyTextColor justify" style="margin-bottom:10px;">
            <fmt:message key="legislation.general.nothing"/>
        </li>
    </c:if>
</ul>

</div>
<%@ include file="../../../includes/legislationRightContent.jsp" %>

<script type="text/javascript">
    $(document).ready(function() {
        attachConfirmDelete("<fmt:message key="legislation.delete.confirmDelete"/>");
    });
</script>




