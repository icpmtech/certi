<%@ include file="../../../includes/taglibs.jsp" %>

<s:form partial="true"
        beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanCMTemplate5ContactsElementActionBean">

    <div class="template-cm-padding">
        <div class="form" style="width:100%">
            <p>
                <label for="template5ContactType"><fmt:message key="pei.template.5ContactsElement.type"/> (*):</label>
                <s:select name="template.contactType" id="template5ContactType" class="mediumInput">
                    <s:options-enumeration
                            enum="com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement.ContactType"/>
                </s:select>
            </p>

            <p class="template5Fields <%=com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement.ContactType.INTERNAL_PERSON%> <%=com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement.ContactType.EMERGENCY_STRUCTURE_PERSON%> ">
                <s:label for="personName"><fmt:message key="pei.template.5ContactsElement.personName"/>:</s:label>
                <s:text id="personName" name="template.personName" class="mediumInput"/>
            </p>

            <p class="template5Fields <%=com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement.ContactType.EXTERNAL_ENTITY%>">
                <s:label for="entityType"><fmt:message key="pei.template.5ContactsElement.entityType"/>:</s:label>
                <s:text id="entityType" name="template.entityType" class="mediumInput"/>
            </p>

            <p class="template5Fields <%=com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement.ContactType.EXTERNAL_ENTITY%> ">
                <s:label for="entityName"><fmt:message key="pei.template.5ContactsElement.name"/>:</s:label>
                <s:text id="entityName" name="template.entityName" class="mediumInput"/>
            </p>

            <p class="template5Fields <%=com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement.ContactType.INTERNAL_PERSON%> ">
                <s:label for="entityName"><fmt:message key="pei.template.5ContactsElement.entityName"/>:</s:label>
                <s:text id="entityName" name="template.entityName" class="mediumInput"/>
            </p>

            <div class="template5Fields <%=com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement.ContactType.EMERGENCY_STRUCTURE_PERSON%>">
                <p>
                    <s:label for="personPosition"><fmt:message
                            key="pei.template.5ContactsElement.personPosition"/>:</s:label>
                    <s:text id="personPosition" name="template.personPosition" class="mediumInput"/>
                </p>

                <p>
                    <s:label for="personArea"><fmt:message key="pei.template.5ContactsElement.personArea"/>:</s:label>
                    <s:text id="personArea" name="template.personArea" class="mediumInput"/>
                </p>
            </div>

            <div class="template5Fields template5FieldsAlwaysShow">
                <p>
                    <s:label for="email"><fmt:message key="pei.template.5ContactsElement.email"/>:</s:label>
                    <s:text id="email" name="template.email" class="mediumInput"/>
                </p>

                <p>
                    <s:label for="phone"><fmt:message key="pei.template.5ContactsElement.phone"/>:</s:label>
                    <s:text id="phone" name="template.phone" class="mediumInput"/>
                </p>

                <p>
                    <s:label for="mobile"><fmt:message key="pei.template.5ContactsElement.mobile"/>:</s:label>
                    <s:text id="mobile" name="template.mobile" class="mediumInput"/>
                </p>

                <p>
                    <s:label for="photo"><fmt:message key="pei.template.5ContactsElement.photo"/>:</s:label>
                    <s:file id="photo" name="filePhoto" class="fileInput" style="margin-bottom:0"/>
                    <label class="recomended-style">(<fmt:message key="common.recommended.dimensions"/>: <fmt:message
                            key="pei.template.5ContactsElement.photo.recommendedDimensions.width"/> <fmt:message
                            key="common.by"/> <fmt:message
                            key="pei.template.5ContactsElement.photo.recommendedDimensions.height"/> <fmt:message
                            key="common.pixels"/>)</label>
                </p>
                <c:if test="${requestScope.actionBean.folder.template.photo != null}">
                    <p style="margin:0;padding:0" class="floatLeft">
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
                                event="viewResource" class="file-download">
                            <s:param name="planModuleType" value="${requestScope.actionBean.planModuleType}"/>
                            <s:param name="peiViewOffline" value="true"/>
                            <s:param name="path" value="${requestScope.actionBean.folder.path}"/>
                            <s:param name="peiId" value="${requestScope.actionBean.peiId}"/>
                            <s:param name="order" value="0"/>
                            <c:out value="${requestScope.actionBean.folder.template.photo.name}"/>
                        </s:link>
                    </p>
                </c:if>
            </div>

            <p class="mandatoryFields-pei floatLeft" style="margin-top:20px">
                <fmt:message key="common.mandatoryfields"/>
            </p>

            <div class="cleaner"><!-- do not remove--></div>
        </div>
    </div>
</s:form>

<script type="text/javascript">
    $(document).ready(function() {

        $('#template5ContactType').change(function () {
            changeContactType();
        });

        changeContactType();
    });

    function changeContactType() {
        var val = "." + $('#template5ContactType').val();
        $(".template5Fields").hide();
        $(".template5FieldsAlwaysShow").show();
        $(val).show();
        iResizeInsideIFrame();
    }
</script>