<%@ include file="../../../includes/taglibs.jsp" %>

<head>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery-ui-1.7.custom.css"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/datepicker-${pageContext.request.locale}.js"></script>
</head>

<c:choose>
    <c:when test="${requestScope.actionBean.edit}">
        <c:set var="title"><fmt:message key="common.edit"/></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="title"><fmt:message key="common.add"/></c:set>
    </c:otherwise>
</c:choose>

<title><fmt:message key="news.page.title"/> &gt; ${pageScope.title}</title>

<h1><fmt:message key="news.page.title"/></h1>

<h2 class="formBig"><span>${pageScope.title}</span></h2>
<s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.NewsActionBean" focus="">

    <div class="form">
        <s:errors/>

        <s:label for="categoryId"><fmt:message key="news.category"/> (*):</s:label>
        <s:select name="news.category.id" id="categoryId" class="largeInput">
            <s:options-collection collection="${requestScope.actionBean.newsCategories}" label="name" value="id"/>
        </s:select>

        <s:label for="title"><fmt:message key="news.title"/> (*):</s:label>
        <s:text id="title" name="news.title" class="largeInput"/>

        <s:label for="creationDate"><fmt:message key="news.creationDate"/> (*):</s:label>
        <s:text id="creationDate" name="news.creationDate" class="dateInput" readonly="true"/>

        <div class="cleaner"><!-- --></div>

        <s:label for="published"><fmt:message key="news.published"/>:</s:label>
        <s:checkbox name="news.published" id="published" style="float: left;"/>

        <c:url value="/scripts/fckeditor/editor/filemanager/browser/default/browser.html?showBrowseServerButton=false"
               var="browserUrl"/>

        <!--To send more then one parameter URL must be encoded-->

        <label><fmt:message key="news.content"/> (*):</label>

        <p>&nbsp;</p>
    </div>
    <div class="form formBig">
        <fck:editor instanceName="news.content" height="300px">
                <jsp:attribute name="value">
                    <c:choose>
                        <c:when test="${requestScope.actionBean.news.content != ''}">
                            ${requestScope.actionBean.news.content}
                        </c:when>
                        <c:otherwise>

                        </c:otherwise>
                    </c:choose>
                </jsp:attribute>
            <jsp:body>
                <fck:config DefaultLanguage="${requestScope.actionBean.context.locale.language}"
                            LinkBrowserURL="${browserUrl}"/>
            </jsp:body>
        </fck:editor>

        <s:text name="" style="display: none;"/>

        <s:hidden name="edit"/>
        <s:hidden name="news.id"/>

        <div class="formButtons" style="padding-right:0">
            <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
            <s:submit name="insertNews" class="button"><fmt:message key="common.submit"/></s:submit>
            <s:submit name="" class="button"><fmt:message key="common.cancel"/></s:submit>
        </div>
    </div>
</s:form>

<script type="text/javascript">
    $(function() {
        $('#creationDate').datepicker({
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="news.creationDate"/>'});

    <c:if test="${!requestScope.actionBean.edit}">
        $("#creationDate").datepicker('setDate', new Date());
    </c:if>
    });
</script>
