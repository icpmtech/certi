<%@ include file="../../../includes/taglibs.jsp" %>


<title><fmt:message key="companies.companies"/> &gt; <fmt:message key="user.import"/></title>

<h1><fmt:message key="user.import"/></h1>

<h2><span><c:out value="${requestScope.actionBean.company.name}"/></span></h2>
<s:errors/>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean" class="form"
    focus="importFile">


    <s:label for="importFile"><fmt:message key="importFile.name"/> (*):</s:label>
    <s:file id="importFile" name="importFile"/>

    <s:hidden name="edit"/>
    <s:hidden name="company.id"/>
    <s:hidden name="company.name"/>
    <s:hidden name="letter"/>

    <div class="formButtons">
        <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
        <s:submit name="importUsers" class="button"><fmt:message key="common.submit"/></s:submit>
        <s:submit name="viewCompanies" class="button"><fmt:message key="common.cancel"/></s:submit>
    </div>

</s:form>
