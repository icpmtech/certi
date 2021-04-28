<%@ page import="com.criticalsoftware.certitools.presentation.util.HTMLEscapeAndNL2BR" %>
<%@ page import="com.criticalsoftware.certitools.entities.FAQ" %>
<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="faq.view.page.title"/> <c:out value="${requestScope.actionBean.moduleTitle}"/></title>

<h1><fmt:message key="faq.view.page.title"/> <c:out value="${requestScope.actionBean.moduleTitle}"/> </h1>

<ss:secure roles="legislationmanager,peimanager">
    <div class="links">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.FAQActionBean"
                event="viewFAQs" class="operationManagement-arrow">
            <s:param name="moduleType" value="${requestScope.actionBean.moduleType}"/>
            <fmt:message key="faq.view.management"/>
        </s:link>
    </div>
</ss:secure>

<div class="justify">

    <div id="questions">
        <c:forEach items="${requestScope.actionBean.viewModuleFAQ.faqCategories}" var="faqCategory">
            <!--- When FAQs exists-->
            <c:if test="${faqCategory.faqs != null && fn:length(faqCategory.faqs) > 0}">
                <c:set var="exists" value="true"/>
                <!-- print category-->
                <h2><span><c:out value="${faqCategory.name}"/></span></h2>
                <!-- print all questions-->
                <c:forEach items="${faqCategory.faqs}" var="faq">
                    <div class="questionlink">
                        <img src="${pageContext.request.contextPath}/images/faqs.png"
                             alt="<fmt:message key="faq.view.question"/> "/>
                        <a href="#${faq.id}answer"><% out.print(HTMLEscapeAndNL2BR.replaceAndEscape(((FAQ) pageContext.getAttribute("faq")).getQuestion())); %></a>
                    </div>

                </c:forEach>
            </c:if>
        </c:forEach>
    </div>
    <div class="questionsAnswers">
        <c:forEach items="${requestScope.actionBean.viewModuleFAQ.faqCategories}" var="faqCategory">

            <c:if test="${faqCategory.faqs != null && fn:length(faqCategory.faqs) > 0}">
                <c:forEach items="${faqCategory.faqs}" var="faq">
                    <hr id="${faq.id}answer">

                    <div class="backtotop">
                        <a href="#questions">
                            <fmt:message key="faq.view.backtotop"/>
                        </a>
                    </div>

                    <div class="answer">

                        <div class="questionExpand">
                            <li>
                                <% out.print(HTMLEscapeAndNL2BR.replaceAndEscape(((FAQ) pageContext.getAttribute("faq")).getQuestion())); %>
                            </li>
                        </div>

                        <div class="answerExpand">
                            <% out.print(HTMLEscapeAndNL2BR.replaceAndEscape(((FAQ) pageContext.getAttribute("faq")).getAnswer())); %>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
        </c:forEach>
    </div>
</div>

<c:if test="${pageScope.exists == null}">
    <fmt:message key="faq.view.nofaqs"/>
</c:if>


