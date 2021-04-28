<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="news.page.title"/> &gt; <fmt:message key="news.category.edit"/></title>

<h1><fmt:message key="news.page.title"/></h1>

<h2 class="form"><span><fmt:message key="news.category.edit"/></span></h2>
<s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.NewsActionBean" class="form" focus="">

    <s:errors/>

    <s:label for="newsCategory1" name="labelNewsCategory1"><fmt:message key="/certitools/News.action.newsCategory1"/> (*):</s:label>
    <s:text id="newsCategory1" name="newsCategory1" class="largeInput" maxlength="128" />

    <s:label for="newsCategory2" name="labelNewsCategory2"><fmt:message key="/certitools/News.action.newsCategory2"/> (*):</s:label>
    <s:text id="newsCategory2" name="newsCategory2" class="largeInput" maxlength="128" />

    <div class="formButtons">
        <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
        <s:submit name="updateNewsCategory" class="button"><fmt:message key="common.submit"/></s:submit>
        <s:submit name="" class="button"><fmt:message key="common.cancel"/></s:submit>
    </div>

</s:form>