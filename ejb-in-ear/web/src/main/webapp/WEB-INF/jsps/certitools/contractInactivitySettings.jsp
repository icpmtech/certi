<%@ include file="../../../includes/taglibs.jsp" %>
<c:set var="title"><fmt:message key="contract.inactivitySetting"/></c:set>

<head>
    <title><fmt:message key="companies.contracts"/> &gt; ${pageScope.title}</title>
</head>

<h1><fmt:message key="contract.inactivitySetting"/></h1>

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.ContractActionBean" class="form"
        focus="" style="width:660px">

    <s:errors/>

    <s:hidden name="company.id"/>
    <s:hidden name="contract.id"/>
    <s:hidden name="letter"/>

    <!-- First Message-->
    <s:label for="firstInactivityMessageTermId" style="width:250px"><fmt:message
            key="contract.firstInactivityMessageTerm"/> (*):</s:label>
    <s:text id="firstInactivityMessageTermId" name="contract.firstInactivityMessageTerm" style="width:40px"
            class="floatLeft"/>
    <span style="margin-left:5px;padding-top:4px" class="floatLeft"><fmt:message key="common.days"/></span>

    <s:label for="firstInactivityMessageHeaderId" style="width:250px;margin-top:10px"><fmt:message
            key="contract.firstInactivityMessageTemplateSubject"/>:</s:label>
    <s:text id="firstInactivityMessageHeaderId" name="contract.firstInactivityMessageTemplateSubject"
            class="largeInput" maxlength="200" style="margin-top:10px"/>

    <s:label for="firstInactivityMessageTemplateBodyId" style="width:250px"><fmt:message
            key="contract.firstInactivityMessageTemplateBody"/>:</s:label>
    <s:textarea id="firstInactivityMessageTemplateBodyId" name="contract.firstInactivityMessageTemplateBody"
                class="largeInput" style="margin-bottom:10px" rows="3"/>

    <!-- Second Message -->
    <s:label for="secondInactivityMessageTermId" style="width:250px"><fmt:message
            key="contract.secondInactivityMessageTerm"/> (*):</s:label>
    <s:text id="secondInactivityMessageTermId" name="contract.secondInactivityMessageTerm" style="width:40px"
            class="floatLeft"/>
    <span style="margin-left:5px;padding-top:4px" class="floatLeft"><fmt:message key="common.days"/></span>

    <s:label for="secondInactivityMessageHeaderId" style="width:250px;margin-top:10px"><fmt:message
            key="contract.secondInactivityMessageTemplateSubject"/>:</s:label>
    <s:text id="secondInactivityMessageHeaderId" name="contract.secondInactivityMessageTemplateSubject"
            class="largeInput" style="margin-top:10px"/>

    <s:label for="secondInactivityMessageTemplateBodyId" style="width:250px"><fmt:message
            key="contract.secondInactivityMessageTemplateBody"/>:</s:label>
    <s:textarea id="secondInactivityMessageTemplateBodyId" name="contract.secondInactivityMessageTemplateBody"
                class="largeInput" style="margin-bottom:10px" rows="3"/>

    <s:label for="deleteUserTermId" style="width:250px;margin-bottom:15px"><fmt:message
            key="contract.deleteUserTerm"/> (*):</s:label>
    <s:text id="deleteUserTermId" name="contract.deleteUserTerm" style="width:40px"
            class="floatLeft"/>

    <span style="margin-left:5px;padding-top:4px;" class="floatLeft"><fmt:message key="common.days"/></span>

    <p style="font-size: 80%">
        <fmt:message key="common.mandatoryfields"/>. <fmt:message key="contract.inactivitySetting.warning"/>
    </p>

    <div class="formButtons">
        <s:submit name="insertContractInactivitySettings" class="button"><fmt:message key="common.submit"/></s:submit>
        <s:submit name="cancel" class="button"><fmt:message key="common.cancel"/></s:submit>
    </div>

</s:form>


