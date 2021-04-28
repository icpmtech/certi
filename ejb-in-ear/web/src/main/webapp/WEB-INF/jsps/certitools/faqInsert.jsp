<%@ include file="../../../includes/taglibs.jsp" %>

<head>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery.autocomplete.js"></script>

    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery.autocomplete.css"/>
</head>

<c:choose>
    <c:when test="${requestScope.actionBean.edit}">
        <c:set var="title"><fmt:message key="common.edit"/></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="title"><fmt:message key="common.add"/></c:set>
    </c:otherwise>
</c:choose>

<title><fmt:message key="menu.legislation.faq"/> &gt; ${pageScope.title}</title>

<h1><fmt:message key="menu.legislation.faq"/></h1>

<h2 class="form"><span>${pageScope.title}</span></h2>
<s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.FAQActionBean" class="form"
        focus="">

    <s:hidden name="moduleType"/>
    <s:hidden name="edit"/>
    <s:hidden name="faq.id"/>

    <s:errors/>

    <s:label for="question"><fmt:message key="faq.add.question"/> (*):</s:label>
    <s:textarea id="question" name="faq.question" class="largeInputTextArea" rows="5"/>

    <s:label for="answer"><fmt:message key="faq.add.answer"/> (*):</s:label>
    <s:textarea id="answer" name="faq.answer" class="largeInputTextArea" rows="5"/>

    <s:label for="module"><fmt:message key="faq.add.module"/> (*):</s:label>
    <s:select id="module" name="faq.faqCategory.module.moduleType" class="largeInput">
        <s:options-collection collection="${requestScope.actionBean.modulesAllowed}" label="name" value="moduleType"/>
    </s:select>

    <s:label for="category"><fmt:message key="faq.add.category"/> (*):</s:label>
    <s:text id="category" name="faq.faqCategory.name" class="largeInput"/>

    <s:text name="" style="display: none;"/>    

    <div class="formButtons">
        <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>

        <c:choose>
            <c:when test="${requestScope.actionBean.edit}">
                <s:submit name="updateFAQ" class="button"><fmt:message key="common.submit"/></s:submit>
            </c:when>
            <c:otherwise>
                <s:submit name="insertFAQ" class="button"><fmt:message key="common.submit"/></s:submit>
            </c:otherwise>
        </c:choose>
        <s:submit name="cancel" class="button"><fmt:message key="common.cancel"/></s:submit>
    </div>

</s:form>

<script type="text/javascript">

    $(document).ready(function() {

        //Initialize Auto complete
        $("#category").autocomplete("${pageContext.request.contextPath}/certitools/FAQ.action?autoCompleteFAQCategory=", {
            minChars: 0,
            scroll:true,
            maxItemsToShow:30,
            extraParams:{
                "moduleType":function() {
                    return $("#module").val();
                },
                "searchAutoCompleteField":function() {
                    return $("#category").val();
                }
            }
        });

        cleanFieldOnChangeEvent('module', 'category');
    });


</script>