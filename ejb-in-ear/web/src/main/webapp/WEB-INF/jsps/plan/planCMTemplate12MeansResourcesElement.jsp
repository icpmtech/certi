<%@ include file="../../../includes/taglibs.jsp" %>

<s:form partial="true"
        beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplate12MeansResourcesElementActionBean">

    <div class="template-cm-padding">
        <div class="form" style="width:100%">
            <p>
                <s:label for="name"><fmt:message key="pei.template.12MeansResourcesElement.resourceName"/> (*):</s:label>
                <s:text id="name" name="template.resourceName" class="mediumInput"/>
            </p>

            <p>
                <s:label for="type"><fmt:message key="pei.template.12MeansResourcesElement.resourceType"/> (*):</s:label>
                <s:text id="type" name="template.resourceType" class="mediumInput"/>
            </p>

            <p>
                <s:label for="entityName"><fmt:message key="pei.template.12MeansResourcesElement.entityName"/>:</s:label>
                <s:text id="entityName" name="template.entityName" class="mediumInput"/>
            </p>

            <p>
                <s:label for="quantity"><fmt:message key="pei.template.12MeansResourcesElement.quantity"/>:</s:label>
                <s:text id="quantity" name="template.quantity" class="mediumInput"/>
            </p>

            <p>
                <s:label for="characteristics"><fmt:message key="pei.template.12MeansResourcesElement.characteristics"/>:</s:label>
                <s:textarea id="characteristics" name="template.characteristics" rows="4" class="mediumInput" style="padding: 1px 0px; height:"/>
            </p>

            <p class="mandatoryFields-pei floatLeft" style="margin-top:20px">
                <fmt:message key="common.mandatoryfields"/>
            </p>

            <div class="cleaner"><!-- do not remove--></div>
        </div>
    </div>
</s:form>

