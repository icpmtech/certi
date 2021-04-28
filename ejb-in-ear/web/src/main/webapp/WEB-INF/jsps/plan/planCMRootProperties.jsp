<%@ include file="../../../includes/taglibs.jsp" %>

<div id="pei-admin-main">

    <h1><c:out value="${requestScope.actionBean.pei.planName}"/></h1>

    <div class="form" style="width:100%">
        <p>
            <s:label for="peiRootName"><fmt:message key="pei.planName"/> (*):</s:label>
            <s:text id="peiRootName" name="pei.planName" class="mediumInput"/>
        </p>

        <p>
            <s:label for="peiRootAutor"><fmt:message key="pei.authorName"/> (*):</s:label>
            <s:text id="peiRootAutor" name="pei.authorName" class="mediumInput"/>
        </p>

        <p>
            <s:label for="peiRootVersion"><fmt:message key="pei.version"/> (*):</s:label>
            <s:text id="peiRootVersion" name="pei.version" class="dateInput"/>
        </p>

        <p>
            <s:label for="peiRootVersionDate"><fmt:message key="pei.versionDate"/> (*):</s:label>
            <s:text id="peiRootVersionDate" name="pei.versionDate" class="dateInput" readonly="true"/>
        </p>

        <p>
            <s:label for="peiRootLastExercise"><fmt:message key="pei.simulationDate"/>:</s:label>
            <s:text id="peiRootLastExercise" name="pei.simulationDate" class="dateInput"/>
        </p>

        <p>
            <s:label for="peiRootInstalationPhoto"><fmt:message
                    key="peiInstallationPhoto"/>:</s:label>
            <s:file name="installationPhoto" id="peiRootInstalationPhoto" class="mediumInput"
                    style="clear:none;width:220px;"/>
            <label class="recomended-style floatLeft">(<fmt:message key="common.recommended.dimensions"/>: <fmt:message
                    key="pei.installationPhoto.recommended.width"/> <fmt:message key="common.by"/> <fmt:message
                    key="pei.installationPhoto.recommended.height"/> <fmt:message
                    key="common.pixels"/>)</label>
            <c:if test="${requestScope.actionBean.pei.installationPhoto != null}">
                <label class="formSubtitle" style="margin-bottom:10px;margin-top:-10px;">(<fmt:message
                        key="legislation.add.replaceFile"/>)</label>
            </c:if>
        </p>

        <p>
            <s:label for="peiCompanyLogoi"><fmt:message key="peiCompanyLogo"/>:</s:label>
            <s:file name="companyLogo" id="peiCompanyLogoi" class="mediumInput" style="clear:none;width:220px"/>
            <label class="recomended-style">(<fmt:message key="common.recommended.dimensions"/>: <fmt:message
                    key="pei.peiCompanyLogo.recommended.width"/> <fmt:message key="common.by"/> <fmt:message
                    key="pei.peiCompanyLogo.recommended.height"/> <fmt:message
                    key="common.pixels"/>)</label>
            <c:if test="${requestScope.actionBean.pei.companyLogo != null}">
                <label class="formSubtitle" style="margin-bottom:10px;margin-top:-10px;">(<fmt:message
                        key="legislation.add.replaceFile"/>)</label>
            </c:if>
        </p>

        <s:hidden name="folderId"/>

         <p class="mandatoryFields-pei">
            <fmt:message key="common.mandatoryfields"/>
         </p>

        <div class="cleaner"><!-- do not remove--></div>
    </div>
</div>

<script type="text/javascript">
    $(function() {
        $('#peiRootVersionDate').datepicker({
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="pei.versionDate"/>'});

        $('#peiRootLastExercise').datepicker({
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="pei.simulationDate"/>'});
    });
</script>
