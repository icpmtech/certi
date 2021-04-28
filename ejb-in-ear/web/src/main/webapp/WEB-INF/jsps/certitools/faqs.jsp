<%@ page import="com.criticalsoftware.certitools.entities.FAQ" %>
<%@ page import="com.criticalsoftware.certitools.presentation.util.HTMLEscapeAndNL2BR" %>
<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="menu.legislation.faq"/></title>

<h1><fmt:message key="menu.legislation.faq"/></h1>

<s:messages/>
<s:errors/>

<div class="links">
    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.FAQActionBean"
            event="insertOrUpdateFAQForm" class="operationAdd">
        <s:param name="moduleType" value="${requestScope.actionBean.moduleType}"/>
        <fmt:message key="common.add"/>
    </s:link>
</div>

<display:table list="${requestScope.actionBean.faqs}" export="true" id="displaytag" class="displaytag"
               uid="faqs" requestURI="/certitools/FAQ.action">

    <display:column property="faqCategory.module.moduleType" titleKey="faq.list.module" escapeXml="true"
                    sortable="true"
                    decorator="com.criticalsoftware.certitools.presentation.util.FAQTableModuleTypeColumnDecorator"/>
    <display:column property="faqCategory.name" titleKey="faq.list.category" escapeXml="true" sortable="true"/>
    <display:column titleKey="faq.list.question">
        <% out.print(HTMLEscapeAndNL2BR.replaceAndEscape(((FAQ) pageContext.getAttribute("faqs")).getQuestion())); %>
    </display:column>
    <display:column titleKey="faq.list.answer">
        <% out.print(HTMLEscapeAndNL2BR.replaceAndEscape(((FAQ) pageContext.getAttribute("faqs")).getAnswer())); %>
    </display:column>
    <display:column sortProperty="changedDate" titleKey="faq.list.changeDate" sortable="true">
        <fmt:formatDate value="${pageScope.faqs.changedDate}"
                        pattern="${applicationScope.configuration.dateHourPattern}"/>
    </display:column>
    <display:column class="twoButtonColumnWidth" media="html">

        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.FAQActionBean"
                event="updateFAQForm">
            <s:param name="faq.id" value="${pageScope.faqs.id}"/>
            <s:param name="sort" value="${requestScope.actionBean.sort}"/>
            <s:param name="page" value="${requestScope.actionBean.page}"/>
            <s:param name="dir" value="${requestScope.actionBean.dir}"/>
            <s:param name="moduleType" value="${requestScope.actionBean.moduleType}"/>

            <img src="${pageContext.request.contextPath}/images/Editar.png"
                 title="<fmt:message key="common.edit"/>"
                 alt="<fmt:message key="common.edit"/>">

        </s:link>

        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.FAQActionBean"
                event="deleteFAQ" class="confirmDelete">
            <s:param name="faq.id" value="${pageScope.faqs.id}"/>
            <s:param name="sort" value="${requestScope.actionBean.sort}"/>
            <s:param name="page" value="${requestScope.actionBean.page}"/>
            <s:param name="dir" value="${requestScope.actionBean.dir}"/>
            <s:param name="moduleType" value="${requestScope.actionBean.moduleType}"/>

            <img src="${pageContext.request.contextPath}/images/Eliminar.png"
                 title="<fmt:message key="common.delete"/>"
                 alt="<fmt:message key="common.delete"/>">

        </s:link>
    </display:column>
    <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
    <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
    <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
    <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
</display:table>

<script type="text/javascript">
    $(document).ready(function() {
        attachConfirmDelete("<fmt:message key="faq.confirmDelete"/>");
    });
</script>