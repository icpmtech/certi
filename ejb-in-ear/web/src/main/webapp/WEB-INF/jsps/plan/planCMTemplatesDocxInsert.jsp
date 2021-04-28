<%@ include file="../../../includes/taglibs.jsp" %>

<c:choose>
    <c:when test="${requestScope.actionBean.edit}">
        <c:set var="title"><fmt:message key="common.edit"/></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="title"><fmt:message key="common.add"/></c:set>
    </c:otherwise>
</c:choose>

<title><fmt:message key="menu.pei.docx"/> &gt; ${pageScope.title}</title>

<h1><fmt:message key="menu.pei.docx"/></h1>

<h2 class="form"><span>${pageScope.title}</span></h2>
<s:form beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplatesDocxActionBean" class="form"
        focus="">

    <s:errors/>

    <s:label for="name"><fmt:message key="templateDocx.title"/> (*):</s:label>
    <s:text id="name" name="template.title" class="largeInput"/>

    <c:choose>
        <c:when test="${requestScope.actionBean.edit}">
            <p>
                <s:label for="module"><fmt:message key="contract.module"/> (*):</s:label>
                <span class="fixedInput">${requestScope.actionBean.template.module.name}</span>
                <s:hidden name="template.module.moduleType"
                          value="${requestScope.actionBean.template.module.moduleType}"/>
            </p>
        </c:when>
        <c:otherwise>
            <p>
            <s:label for="module"><fmt:message key="contract.module"/> (*):</s:label>
            <s:select name="template.module.moduleType" id="module" class="selectInput largeInput">
                <s:options-collection collection="${requestScope.actionBean.modules}" label="name" value="moduleType"/>
            </s:select>
            </p>
        </c:otherwise>
    </c:choose>

    <p style="clear: both;">
    <s:label for="file"><fmt:message key="templateDocx.templateFile"/> (*):</s:label>
    <c:choose>
        <c:when test="${requestScope.actionBean.template.fileName != null}">
            <s:file name="templateFile" id="file" style="margin-bottom: 0;"/>
            <span class="formSubtitle">(<fmt:message key="contract.replaceFile"/>)</span>
        </c:when>
        <c:otherwise>
            <s:file name="templateFile" id="file"/>
        </c:otherwise>
    </c:choose>
    </p>
    <s:label for="observations"><fmt:message key="templateDocx.observations"/>:</s:label>
    <s:textarea id="observations" name="template.observations" class="largeInput" style="margin-bottom: 10px;"
                rows="3"/>

    <s:hidden name="edit"/>
    <s:hidden name="template.id"/>

    <div class="formButtons">
        <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
        <c:choose>
            <c:when test="${requestScope.actionBean.edit}">
                <s:submit name="updateTemplateDocx" class="button"><fmt:message key="common.submit"/></s:submit>
            </c:when>
            <c:otherwise>
                <s:submit name="insertTemplateDocx" class="button"><fmt:message key="common.submit"/></s:submit>
            </c:otherwise>
        </c:choose>


        <s:submit name="cancelTemplateDocxForm" class="button"><fmt:message key="common.cancel" /></s:submit>
    </div>

    <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>
</s:form>
