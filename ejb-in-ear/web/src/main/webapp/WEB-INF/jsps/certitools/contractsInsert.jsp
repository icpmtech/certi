<%@ include file="../../../includes/taglibs.jsp" %>

<c:choose>
    <c:when test="${requestScope.actionBean.edit}">
        <c:set var="title"><fmt:message key="common.edit"/></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="title"><fmt:message key="common.add"/></c:set>
    </c:otherwise>
</c:choose>

<head>
    <title><fmt:message key="companies.contracts"/> &gt; ${pageScope.title}</title>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery-ui-1.7.custom.css"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/datepicker-${pageContext.request.locale}.js"></script>
</head>


<h1><fmt:message key="companies.contracts"/></h1>

<h2 class="form"><span>${pageScope.title}</span></h2>
<s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.ContractActionBean" class="form"
        focus="">

    <s:errors/>

    <p>
        <s:label for=""><fmt:message key="companies.company"/> (*):</s:label>
        <span class="fixedInput">${requestScope.actionBean.company.name}</span>
        <s:hidden name="contract.company.id" value="${requestScope.actionBean.company.id}"/>
    </p>

    <s:label for="module"><fmt:message key="contract.module"/> (*):</s:label>

    <c:choose>
        <c:when test="${requestScope.actionBean.edit}">
            <span class="fixedInput">${requestScope.actionBean.contract.module.name}</span>
            <s:hidden id="module" name="contract.module.moduleType"/>
        </c:when>
        <c:otherwise>
            <s:select name="contract.module.moduleType" id="module" class="selectInput largeInput">
                <s:options-collection collection="${requestScope.actionBean.modules}" label="name" value="moduleType"/>
            </s:select>
        </c:otherwise>
    </c:choose>

    <div id="securitySubModules" style="display: none;">
        <c:forEach items="${requestScope.actionBean.subModules}" var="submodule">
            <p class="securitySubModule">
                <s:label for="submodule${submodule.subModuleType}">${submodule.name}</s:label>
                <s:checkbox id="submodule${submodule.subModuleType}" value="${submodule.subModuleType}"
                            name="selectedSubModules"/>
            </p>
        </c:forEach>
    </div>

    <p>
        <s:label for="number"><fmt:message key="contract.number"/> (*):</s:label>
        <s:text id="number" name="contract.number" class="largeInput"/>
    </p>

    <s:label for="designation"><fmt:message key="contract.contractDesignation"/> (*):</s:label>
    <s:text id="designation" name="contract.contractDesignation" class="largeInput"/>

    <p>
        <s:label for="licenses"><fmt:message key="contract.licensesNumber"/> (*):</s:label>
        <s:text id="licenses" name="contract.licenses" class="smallInput"/>
    </p>

    <p>
        <s:label for="startDate" style="margin-bottom: 15px;"><fmt:message
                key="contract.validity.startEndDate"/> (*):</s:label>
        <s:text id="startDate" name="contract.validityStartDate" style="margin-bottom: 0;" class="dateInput"
                readonly="readonly"/>
        &nbsp; &nbsp; / &nbsp; &nbsp;
        <s:text style="float: none; margin-bottom: 0;" id="endDate" name="contract.validityEndDate" class="dateInput"
                readonly="readonly"/>
    </p>

    <p>
        <s:label for="value"><fmt:message key="contract.value"/>:</s:label>
        <s:text id="value" name="contract.value" class="smallInput"/>
    </p>

    <p <c:if test="${requestScope.actionBean.contract.contractFile != null}">style="margin-bottom: 10px;"</c:if>>
        <s:label for="file"><fmt:message key="contract.document"/> :</s:label>

        <c:choose>
            <c:when test="${requestScope.actionBean.contract.contractFile != null}">
                <s:file name="file" id="file" style="margin-bottom: 0;"/>
                <span class="formSubtitle">(<fmt:message key="contract.replaceFile"/>)</span>
            </c:when>
            <c:otherwise>
                <s:file name="file" id="file"/>
            </c:otherwise>
        </c:choose>

    </p>

    <p style="clear: both;">
        <s:label for="active"><fmt:message key="contract.active"/>:</s:label>
        <s:checkbox checked="true" id="active" name="contract.active"/>
    </p>

    <p>
        <s:label for="menuLabel"><fmt:message key="contract.menuLabel"/>:</s:label>
        <s:text id="menuLabel" name="contract.menuLabel" class="largeInput"/>
    </p>

    <h2 class="form cleaner"><span><fmt:message key="contract.maintenance"/></span></h2>

    <s:label for="designationMaint"><fmt:message key="contract.contractDesignation"/>:</s:label>
    <s:text id="designationMaint" name="contract.contractDesignationMaintenance" class="largeInput"/>

    <s:label for="valueMaint"><fmt:message key="contract.value"/>:</s:label>
    <s:text id="valueMaint" name="contract.valueMaintenance" class="smallInput"/>


    <h2 class="form cleaner"><span><fmt:message key="contract.contact"/></span></h2>
    <c:if test="${!empty requestScope.actionBean.contracts}">

        <s:label for="contractsContacts"><fmt:message key="contract.contactChoose"/>:</s:label>
        <s:select name="contractsContacts" id="contractsContacts" class="selectInput largeInput">
            <s:option value="" selected="true">&nbsp;</s:option>
            <s:options-collection collection="${requestScope.actionBean.contracts}" label="contactName" value="id"
                                  sort="label"/>
        </s:select>
    </c:if>

    <s:label for="contactName"><fmt:message key="contract.contactName"/> (*):</s:label>
    <s:text id="contactName" name="contract.contactName" class="largeInput"/>

    <s:label for="contactPosition"><fmt:message key="contract.contactPosition"/>:</s:label>
    <s:text id="contactPosition" name="contract.contactPosition" class="largeInput"/>

    <s:label for="contactEmail"><fmt:message key="contract.contactEmail"/>:</s:label>
    <s:text id="contactEmail" name="contract.contactEmail" class="largeInput"/>

    <s:label for="contactPhone"><fmt:message key="contract.contactPhone"/>:</s:label>
    <s:text id="contactPhone" name="contract.contactPhone" class="smallInput"/>


    <h2 class="form cleaner"><span><fmt:message key="contract.userRegister"/></span></h2>

    <p>
        <s:label for="userRegisterCode"><fmt:message key="contract.userRegisterCode"/>:</s:label>
        <s:text id="userRegisterCode" name="contract.userRegisterCode" style="width:190px;float:left;"/>
        <input type="button" style="width: 200px;" class="button"
               value="<fmt:message key="contract.userRegisterGenerateRandomCode" />"
               onclick="$('#userRegisterCode').val(generateRandomString(16));"/>
    </p>

    <p>
        <s:label for="userRegisterDomain"><fmt:message key="contract.userRegisterDomains"/>:</s:label>
        <s:text id="userRegisterDomain" name="contract.userRegisterDomains" class="largeInput"/>
    </p>
    <c:if test="${requestScope.actionBean.edit && requestScope.actionBean.contract.contractPermissions != null
                && fn:length(requestScope.actionBean.contract.contractPermissions) > 0}">
        <h2 class="form" style="clear: both; margin-bottom:0;margin-top:0;width:auto;"><span
                style="font-size:10pt;color:#618293;font-weight:bold;" class="permissionsSpan">
        <fmt:message key="contract.userRegisterBasePermissions"/></span></h2>

        <ul class="permissionsList" style="margin-top: 10px;">
            <c:forEach items="${requestScope.actionBean.contract.contractPermissions}" var="permission">
                <li>
                    <c:if test="${permission.name != requestScope.specialPermission}">
                        <c:choose>
                            <c:when test="${permission.name == 'security.permission.basic' || permission.name == 'security.permission.intermediate' || permission.name == 'security.permission.expert'}">
                                <s:label
                                        for="permission${permission.id}${requestScope.actionBean.contract.id}"><fmt:message
                                        key="${permission.name}"/></s:label>
                                <s:checkbox
                                        id="permission${permission.id}${requestScope.actionBean.contract.id}"
                                        value="${permission.id}"
                                        name="userRegisterPermissions"/>
                            </c:when>
                            <c:otherwise>
                                <s:label
                                        for="permission${permission.id}${requestScope.actionBean.contract.id}">${permission.name}</s:label>
                                <s:checkbox
                                        id="permission${permission.id}${requestScope.actionBean.contract.id}"
                                        value="${permission.id}"
                                        name="userRegisterPermissions"/>
                            </c:otherwise>
                        </c:choose>

                    </c:if>
                </li>
            </c:forEach>
        </ul>

    </c:if>


    <s:hidden name="contract.contractFile"/>

    <s:hidden name="edit"/>
    <s:hidden name="company.id"/>
    <s:hidden name="contract.id"/>
    <s:hidden name="letter"/>

    <div class="formButtons">
        <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
        <s:submit name="insertContract" class="button"><fmt:message key="common.submit"/></s:submit>
        <s:submit name="viewCompanies" class="button"><fmt:message key="common.cancel"/></s:submit>
    </div>

</s:form>

<script type="text/javascript">
    $(document).ready(function () {
        $('#contractsContacts').change(function () {

            if ($(this).val() == '') {
                $('#contactName').val('');
                $('#contactPosition').val('');
                $('#contactEmail').val('');
                $('#contactPhone').val('');
            }
            else {
                var c = contracts[$(this).val()];

                $('#contactName').val(c[0]);
                $('#contactPosition').val(c[1]);
                $('#contactEmail').val(c[2]);
                $('#contactPhone').val(c[3]);
            }
        });

        $('#module').change(function () {
            if ($(this).val() === 'GSC') {
                $('#securitySubModules').show();
            } else {
                $('#securitySubModules').hide();
            }
        });

        $('#securitySubModules :checkbox').change(function () {
            var type = $(this).val();
            if ($(this).is(':checked') && (type === 'ACTV' || type === 'ANOM' || type === 'SIW' || type === 'MNT')) {
                $('#submoduleAPC').attr('checked', true);
            }
        });

        if ($('#module').val() === 'GSC') {
            $('#securitySubModules').show();
        } else {
            $('#securitySubModules').hide();
        }
    });

    var contracts = [];

    <c:forEach var="c" items="${requestScope.actionBean.contracts}">
    var c = ['<certitools:escape value="${c.contactName}" type="js" />', '<certitools:escape value="${c.contactPosition}" type="js" />', '<certitools:escape value="${c.contactEmail}" type="js" />', '<certitools:escape value="${c.contactPhone}" type="js" />'];
    contracts[${c.id}] = c;
    </c:forEach>

    $(function () {
        $('#startDate').datepicker({
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="contract.validityStartDate"/>'
        });
    });

    $(function () {
        $('#endDate').datepicker({
            buttonImage: '${pageContext.request.contextPath}/images/calendar/calendar.png',
            dateFormat: '${applicationScope.configuration.datePatternCalendar}',
            buttonText: '<fmt:message key="contract.validityEndDate"/>'
        });
    });

</script>
