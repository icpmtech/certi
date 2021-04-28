<%@ include file="../../../includes/taglibs.jsp" %>

<c:choose>
    <c:when test="${requestScope.actionBean.edit}">
        <c:set var="title"><fmt:message key="common.edit"/></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="title"><fmt:message key="common.add"/></c:set>
    </c:otherwise>
</c:choose>

<title><fmt:message key="companies.companies"/> &gt; ${pageScope.title}</title>

<h1><fmt:message key="companies.companies"/></h1>

<h2 class="form"><span>${pageScope.title}</span></h2>
<s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.CompanyActionBean" class="form"
        focus="">

    <s:errors/>

    <s:label for="name"><fmt:message key="company.name"/> (*):</s:label>
    <s:text id="name" name="company.name" class="largeInput"/>

    <s:label for="address"><fmt:message key="company.address"/>:</s:label>
    <s:textarea id="address" name="company.address" class="largeInput" style="margin-bottom: 10px;" rows="3"/>

    <s:label for="countryId"><fmt:message key="company.country.iso"/>:</s:label>
    <s:select name="company.country.iso" id="countryId" class="largeInput">
        <s:option label="" value=""/>
        <s:options-collection collection="${requestScope.actionBean.countries}" label="name" value="iso"/>
    </s:select>

    <p>
        <s:label for="phone"><fmt:message key="company.phone"/>:</s:label>
        <s:text id="phone" name="company.phone" class="smallInput"/>
    </p>

    <p>
        <s:label for="nif"><fmt:message key="company.fiscalNumber"/>:</s:label>
        <s:text id="nif" name="company.fiscalNumber" class="smallInput"/>
    </p>

    <!-- TODO-MODULE -->
    <p>
        <s:label for="peiLabelPT"><fmt:message key="company.peiLabelPT"/>:</s:label>
        <s:text id="peiLabelPT" name="company.peiLabelPT" class="largeInput"/>
    </p>

    <p>
        <s:label for="prvLabelPT"><fmt:message key="company.prvLabelPT"/>:</s:label>
        <s:text id="prvLabelPT" name="company.prvLabelPT" class="largeInput"/>
    </p>

    <p>
        <s:label for="psiLabelPT"><fmt:message key="company.psiLabelPT"/>:</s:label>
        <s:text id="psiLabelPT" name="company.psiLabelPT" class="largeInput"/>
    </p>
    <p>
        <s:label for="gscLabelPT"><fmt:message key="company.gscLabelPT"/>:</s:label>
        <s:text id="gscLabelPT" name="company.gscLabelPT" class="largeInput"/>
    </p>

    <!-- TODO-MODULE -->
    <p>
        <s:label for="peiLabelEN"><fmt:message key="company.peiLabelEN"/>:</s:label>
        <s:text id="peiLabelEN" name="company.peiLabelEN" class="largeInput"/>
    </p>

    <p>
        <s:label for="prvLabelEN"><fmt:message key="company.prvLabelEN"/>:</s:label>
        <s:text id="prvLabelEN" name="company.prvLabelEN" class="largeInput"/>
    </p>

    <p>
        <s:label for="psiLabelEN"><fmt:message key="company.psiLabelEN"/>:</s:label>
        <s:text id="psiLabelEN" name="company.psiLabelEN" class="largeInput"/>
    </p>

    <p>
        <s:label for="gscLabelEN"><fmt:message key="company.gscLabelEN"/>:</s:label>
        <s:text id="gscLabelEN" name="company.gscLabelEN" class="largeInput"/>
    </p>

    <p>
        <s:label for="language"><fmt:message key="company.language"/>:</s:label>
        <s:select name="company.language" id="language" class="smallInput">
            <s:options-enumeration enum="com.criticalsoftware.certitools.util.Language" label="name"/>
        </s:select>
    </p>

    <p>
        <s:label for="showFullListPEI"><fmt:message key="company.showFullListPEI"/>:</s:label>
        <s:checkbox name="company.showFullListPEI" id="showFullListPEI" />
    </p>

    <s:hidden name="edit"/>
    <s:hidden name="company.id"/>
    <s:hidden name="letter"/>

    <div class="formButtons">
        <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
        <s:submit name="insertCompany" class="button"><fmt:message key="common.submit"/></s:submit>
        <s:submit name="viewCompanies" class="button"><fmt:message key="common.cancel"/></s:submit>
    </div>

</s:form>
