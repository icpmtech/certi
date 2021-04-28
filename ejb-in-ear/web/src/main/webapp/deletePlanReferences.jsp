<%@ include file="/includes/taglibs.jsp" %>

<c:if test="${requestScope.firstVisit==null}">
    <c:redirect url="/plan/PlanCMDeletePlanReferences.action"/>
</c:if>

<h1>Delete Plan Folders References</h1>

<s:messages/>
<s:errors/>

<ss:secure roles="administrator">
    <s:form beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMDeletePlanReferencesActionBean"
            class="form"
            style="padding-top:20px">
        <p>
            Contract:
            <s:select name="contractId" style="width:200px">
                <c:forEach items="${requestScope.actionBean.contracts}" var="contract">
                    <c:if test="${!pageScope.contract.deleted && pageScope.contract.module.moduleType != 'LEGISLATION'}">
                        <s:option
                                value="${contract.id}">${contract.contractDesignation} (${pageScope.contract.module.moduleType})</s:option>
                    </c:if>
                </c:forEach>
            </s:select>
        </p>

        <p>
            Folders:
            <s:select name="deleteType">
                <s:option value="BOTH">BOTH</s:option>
                <s:option value="OFFLINE">OFFLINE</s:option>
                <s:option value="ONLINE">ONLINE</s:option>
            </s:select>
        </p>

        <p>
            <s:submit name="deletePlanReferences" class="searchButton">Delete</s:submit>
        </p>
    </s:form>
</ss:secure>
