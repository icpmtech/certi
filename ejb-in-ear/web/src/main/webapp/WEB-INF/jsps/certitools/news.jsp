<%@ page import="com.criticalsoftware.certitools.presentation.util.HTMLEscapeAndNL2BR" %>
<%@ page import="com.criticalsoftware.certitools.entities.News" %>
<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="news.page.title"/></title>

<h1><fmt:message key="news.page.title"/></h1>

<s:messages/>

<div class="links">
    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.NewsActionBean"
            event="insertNewsForm" class="operationAdd">
        <fmt:message key="common.add"/>
    </s:link>

    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.NewsActionBean"
            event="updateNewsCategoryForm" class="operationEdit">
        <fmt:message key="news.category.edit"/>
    </s:link>
</div>

<display:table list="${requestScope.actionBean.newsAdapter}" export="true" id="displaytable" class="displaytag"
               uid="news"
               requestURI="/certitools/News.action">

    <display:column property="category.name" titleKey="news.category" escapeXml="true" sortable="true"/>

    <display:column property="title" titleKey="news.title" escapeXml="true" sortable="true"/>

    <display:column titleKey="news.content" media="html">
        <% out.print(HTMLEscapeAndNL2BR.replace(((News) pageContext.getAttribute("news")).getContent())); %>
    </display:column>

    <display:column titleKey="news.content" property="content" media="csv excel xml pdf"
                    decorator="com.criticalsoftware.certitools.presentation.util.NewsExportContentDecorator"/>

    <display:column titleKey="news.creationDate" sortable="true" sortProperty="creationDate"
                    style="white-space: nowrap;">
        <fmt:formatDate value="${pageScope.news.creationDate}"
                        pattern="${applicationScope.configuration.datePattern}"/>
    </display:column>

    <display:column sortProperty="published" titleKey="news.published" sortable="true" class="center"
                    style="width: 70px;" media="html">

        <c:choose>
            <c:when test="${pageScope.news.published}">

                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.NewsActionBean"
                        event="togglePublished" class="publishLinkAjax">
                    <s:param name="news.id" value="${pageScope.news.id}"/>
                    <s:param name="sort" value="${requestScope.actionBean.sort}"/>
                    <s:param name="page" value="${requestScope.actionBean.page}"/>
                    <s:param name="dir" value="${requestScope.actionBean.dir}"/>

                    <img src="${pageContext.request.contextPath}/images/button-ok.png"
                         title="<fmt:message key="news.unpublishAlt"/>"
                         alt="<fmt:message key="news.unpublishAlt"/>"/></s:link>

            </c:when>
            <c:otherwise>

                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.NewsActionBean"
                        event="togglePublished" class="publishLinkAjax">
                    <s:param name="news.id" value="${pageScope.news.id}"/>
                    <s:param name="sort" value="${requestScope.actionBean.sort}"/>
                    <s:param name="page" value="${requestScope.actionBean.page}"/>
                    <s:param name="dir" value="${requestScope.actionBean.dir}"/>

                    <img src="${pageContext.request.contextPath}/images/button-disabled.png"
                         title="<fmt:message key="news.publishAlt"/>"
                         alt="<fmt:message key="news.publishAlt"/>"/></s:link>

            </c:otherwise>
        </c:choose>

    </display:column>

    <display:column titleKey="news.published" sortProperty="published" media="csv excel xml pdf">
        <c:choose>
            <c:when test="${pageScope.news.published}">
                <fmt:message key="common.published"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="common.notpublished"/>
            </c:otherwise>
        </c:choose>
    </display:column>

    <display:column class="twoButtonColumnWidth" media="html">

        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.NewsActionBean"
                event="updateNews">
            <s:param name="news.id" value="${pageScope.news.id}"/>
            <s:param name="sort" value="${requestScope.actionBean.sort}"/>
            <s:param name="page" value="${requestScope.actionBean.page}"/>
            <s:param name="dir" value="${requestScope.actionBean.dir}"/>

            <img src="${pageContext.request.contextPath}/images/Editar.png"
                 title="<fmt:message key="common.edit"/>"
                 alt="<fmt:message key="common.edit"/>"/></s:link>

        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.NewsActionBean"
                event="deleteNews" class="confirmDelete">
            <s:param name="news.id" value="${pageScope.news.id}"/>
            <s:param name="sort" value="${requestScope.actionBean.sort}"/>
            <s:param name="page" value="${requestScope.actionBean.page}"/>
            <s:param name="dir" value="${requestScope.actionBean.dir}"/>

            <img src="${pageContext.request.contextPath}/images/Eliminar.png"
                 title="<fmt:message key="common.delete"/>"
                 alt="<fmt:message key="common.delete"/>"/></s:link>

    </display:column>

    <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
    <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
    <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
    <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
</display:table>


<script type="text/javascript">
    $(document).ready(function() {
        attachAjaxTogglePublished('<fmt:message key="news.publishAlt"/>', '<fmt:message key="news.unpublishAlt"/>'
                , '${pageContext.request.contextPath}');

        attachConfirmDelete("<fmt:message key="news.confirmDelete"/>");

    });
</script>