<%@ include file="../../../includes/taglibs.jsp" %>
<div class="peiTemplate5Contacts">

<s:form beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean"
        style="margin-top:0" id="searchForm" focus="" class="searchBox" method="GET">
    <s:hidden name="planModuleType">${requestScope.actionBean.planModuleType}</s:hidden>

    <fieldset>
        <legend>
            <fmt:message key="common.search"/>
        </legend>
        <table>
            <tr>
                <td class="first">
                    <s:select name="template5ContactType" id="template5ContactType" style="width: 250px;"
                              value="EXTERNAL_ENTITY">
                        <s:option value="all"> </s:option>

                        <s:option value="EXTERNAL_ENTITY"><fmt:message key="ContactType.EXTERNAL_ENTITY"/></s:option>
                        <c:if test="${requestScope.actionBean.template5InternalContactsEmpty == false}">
                            <s:option value="INTERNAL_PERSON"><fmt:message
                                    key="ContactType.INTERNAL_PERSON"/></s:option>
                        </c:if>
                        <c:if test="${requestScope.actionBean.template5EmergencyContactsEmpty == false}">
                            <s:option value="EMERGENCY_STRUCTURE_PERSON"><fmt:message
                                    key="ContactType.EMERGENCY_STRUCTURE_PERSON"/></s:option>
                        </c:if>
                    </s:select>
                    <s:text name="template5SearchPhrase" class="smallSearchInput"
                            style="width: 530px; margin-left: 10px; vertical-align: middle;"/>
                </td>
                <td class="last">
                    <s:submit name="viewResource" id="searchButton" class="searchButton"><fmt:message
                            key="common.search"/></s:submit>
                </td>
            </tr>
        </table>
    </fieldset>

    <s:hidden name="path" value="${requestScope.actionBean.folder.path}"/>
    <s:hidden name="peiId" value="${requestScope.actionBean.peiId}"/>
    <s:hidden name="_eventName" value="viewResource"/>
</s:form>

<div class="cleaner"><!-- --></div>

<c:if test="${fn:length(requestScope.actionBean.template5ExternalContacts) <= 0
        && fn:length(requestScope.actionBean.template5InternalContacts) <= 0
        && fn:length(requestScope.actionBean.template5EmergencyContacts) <= 0}">
    <fmt:message key="pei.template.5ContactsElement.noContacts"/>
</c:if>
<c:if test="${requestScope.actionBean.emailsList != null}">
    <div class="viewTemplateSendEmails">
        <a href="mailto:${requestScope.actionBean.emailsList}">
            <fmt:message key="pei.template.5ContactsElement.sendEmail"/>
        </a>
    </div>
</c:if>

<c:if test="${fn:length(requestScope.actionBean.template5ExternalContacts) > 0}">
    <h2 class="peiTemplate5ContactsH2"><span><fmt:message
            key="pei.template.5ContactsElement.externalEntities"/></span></h2>

    <display:table list="${requestScope.actionBean.template5ExternalContacts}" export="true" id="externalEntities"
                   excludedParams="viewTemplate5Contacts __fp _sourcePage"
                   sort="list" class="displaytag" requestURI="/plan/Plan.action" uid="contact1">
        <display:column property="template.entityType" titleKey="pei.template.5ContactsElement.entityType"
                        escapeXml="true" sortable="true"/>
        <display:column property="template.entityName" titleKey="pei.template.5ContactsElement.name"
                        escapeXml="true" sortable="true"/>
        <display:column property="template.email" titleKey="pei.template.5ContactsElement.email"
                        media="csv excel xml pdf"/>
        <display:column titleKey="pei.template.5ContactsElement.email" sortable="true" media="html">
            <a href="mailto:${pageScope.contact1.template.email}">
                <c:out value="${pageScope.contact1.template.email}"/>
            </a>
        </display:column>
        <display:column property="template.phone" titleKey="pei.template.5ContactsElement.phone" escapeXml="true"
                        sortable="true"/>
        <display:column property="template.mobile" titleKey="pei.template.5ContactsElement.mobile" escapeXml="true"
                        sortable="true"/>
        <display:column class="photoColumn" media="html">
            <c:choose>
                <c:when test="${pageScope.contact1.template.photo == null}">
                    <img src="${pageContext.request.contextPath}/images/photo-unavailable.png"
                         alt="" title="" width="40"/>
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/plan/Plan.action?viewResource&path=${pageScope.contact1.path}&planModuleType=${requestScope.actionBean.planModuleType}&peiId=${requestScope.actionBean.peiId}&order=1&onlineOffline=${requestScope.actionBean.onlineOffline}"
                         alt="${pageScope.contact1.template.entityName}"
                         title="${pageScope.contact1.template.entityName}" width="40"
                         id="photoTooltip${contact1_rowNum}"/>


                    <script type="text/javascript">
                        $(document).ready(function () {
                            $('#photoTooltip${contact1_rowNum}').tooltip({
                                delay: 0,
                                showURL: false,
                                left: -140,
                                extraClass: "peiTemplate5ContactsTooltip",
                                bodyHandler: function () {
                                    return $("<img/>").attr("src", this.src);
                                }
                            });
                        });
                    </script>
                </c:otherwise>
            </c:choose>
        </display:column>
        <display:column titleKey="pei.template.5ContactsElement.photo" media="pdf">
            <c:if test="${pageScope.contact1.template.photo != null}">
                <img src="${pageContext.request.contextPath}/plan/Plan.action?viewResource&path=${pageScope.contact1.path}&planModuleType=${requestScope.actionBean.planModuleType}&peiId=${requestScope.actionBean.peiId}&order=1&onlineOffline=${requestScope.actionBean.onlineOffline}"/>
            </c:if>
        </display:column>

        <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
        <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
        <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
        <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
    </display:table>
</c:if>

<c:if test="${fn:length(requestScope.actionBean.template5InternalContacts) > 0}">
    <h2 class="peiTemplate5ContactsH2"><span><fmt:message
            key="pei.template.5ContactsElement.internalEntities"/></span>
    </h2>

    <display:table list="${requestScope.actionBean.template5InternalContacts}" export="true" id="internalEntities"
                   excludedParams="viewTemplate5Contacts __fp _sourcePage"
                   sort="list" class="displaytag" requestURI="/plan/Plan.action" uid="contact2">
        <display:column property="template.entityName" titleKey="pei.template.5ContactsElement.entityName"
                        escapeXml="true" sortable="true"/>
        <display:column property="template.personName" titleKey="pei.template.5ContactsElement.personName"
                        escapeXml="true" sortable="true"/>
        <display:column property="template.email" titleKey="pei.template.5ContactsElement.email"
                        media="csv excel xml pdf"/>
        <display:column titleKey="pei.template.5ContactsElement.email" sortable="true" media="html">
            <a href="mailto:${pageScope.contact2.template.email}">
                <c:out value="${pageScope.contact2.template.email}"/>
            </a>
        </display:column>
        <display:column property="template.phone" titleKey="pei.template.5ContactsElement.phone" escapeXml="true"
                        sortable="true"/>
        <display:column property="template.mobile" titleKey="pei.template.5ContactsElement.mobile" escapeXml="true"
                        sortable="true"/>
        <display:column class="photoColumn" media="html">
            <c:choose>
                <c:when test="${pageScope.contact2.template.photo == null}">
                    <img src="${pageContext.request.contextPath}/images/photo-unavailable.png"
                         alt="" title="" width="40"/>
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/plan/Plan.action?viewResource&path=${pageScope.contact2.path}&planModuleType=${requestScope.actionBean.planModuleType}&peiId=${requestScope.actionBean.peiId}&order=1&onlineOffline=${requestScope.actionBean.onlineOffline}"
                         alt="${pageScope.contact2.template.entityName}"
                         title="${pageScope.contact2.template.entityName}" width="40"
                         id="2photoTooltip${contact2_rowNum}"/>


                    <script type="text/javascript">
                        $(document).ready(function () {
                            $('#2photoTooltip${contact2_rowNum}').tooltip({
                                delay: 0,
                                showURL: false,
                                left: -140,
                                extraClass: "peiTemplate5ContactsTooltip",
                                bodyHandler: function () {
                                    return $("<img/>").attr("src", this.src);
                                }
                            });
                        });
                    </script>
                </c:otherwise>
            </c:choose>
        </display:column>
        <display:column titleKey="pei.template.5ContactsElement.photo" media="pdf">
            <c:if test="${pageScope.contact2.template.photo != null}">
                <img src="${pageContext.request.contextPath}/plan/Plan.action?viewResource&path=${pageScope.contact2.path}&planModuleType=${requestScope.actionBean.planModuleType}&peiId=${requestScope.actionBean.peiId}&order=1&onlineOffline=${requestScope.actionBean.onlineOffline}"/>
            </c:if>
        </display:column>

        <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
        <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
        <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
        <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
    </display:table>
</c:if>

<c:if test="${fn:length(requestScope.actionBean.template5EmergencyContacts) > 0}">
    <h2 class="peiTemplate5ContactsH2"><span><fmt:message
            key="pei.template.5ContactsElement.emergencyStructurePerson"/></span></h2>

    <display:table list="${requestScope.actionBean.template5EmergencyContacts}" export="true"
                   id="emergencyStructurePerson"
                   excludedParams="viewTemplate5Contacts __fp _sourcePage"
                   sort="list" class="displaytag" requestURI="/plan/Plan.action" uid="contact3">
        <display:column property="template.personPosition" titleKey="pei.template.5ContactsElement.personPosition"
                        escapeXml="true" sortable="true"/>
        <display:column property="template.personArea" titleKey="pei.template.5ContactsElement.personArea"
                        escapeXml="true" sortable="true"/>
        <display:column property="template.personName" titleKey="pei.template.5ContactsElement.personName"
                        escapeXml="true" sortable="true"/>
        <display:column property="template.email" titleKey="pei.template.5ContactsElement.email"
                        media="csv excel xml pdf"/>
        <display:column titleKey="pei.template.5ContactsElement.email" sortable="true" media="html">
            <a href="mailto:${pageScope.contact3.template.email}">
                <c:out value="${pageScope.contact3.template.email}"/>
            </a>
        </display:column>
        <display:column property="template.phone" titleKey="pei.template.5ContactsElement.phone" escapeXml="true"
                        sortable="true"/>
        <display:column property="template.mobile" titleKey="pei.template.5ContactsElement.mobile" escapeXml="true"
                        sortable="true"/>
        <display:column class="photoColumn" media="html">
            <c:choose>
                <c:when test="${pageScope.contact3.template.photo == null}">
                    <img src="${pageContext.request.contextPath}/images/photo-unavailable.png"
                         alt="" title="" width="40"/>
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/plan/Plan.action?viewResource&path=${pageScope.contact3.path}&planModuleType=${requestScope.actionBean.planModuleType}&peiId=${requestScope.actionBean.peiId}&order=1&onlineOffline=${requestScope.actionBean.onlineOffline}"
                         alt="${pageScope.contact3.template.entityName}"
                         title="${pageScope.contact3.template.entityName}" width="40"
                         id="2photoTooltip${contact3_rowNum}"/>


                    <script type="text/javascript">
                        $(document).ready(function () {
                            $('#2photoTooltip${contact3_rowNum}').tooltip({
                                delay: 0,
                                showURL: false,
                                left: -140,
                                extraClass: "peiTemplate5ContactsTooltip",
                                bodyHandler: function () {
                                    return $("<img/>").attr("src", this.src);
                                }
                            });
                        });
                    </script>
                </c:otherwise>
            </c:choose>
        </display:column>

        <display:column titleKey="pei.template.5ContactsElement.photo" media="pdf">
            <c:if test="${pageScope.contact3.template.photo != null}">
                <img src="${pageContext.request.contextPath}/plan/Plan.action?viewResource&path=${pageScope.contact3.path}&planModuleType=${requestScope.actionBean.planModuleType}&peiId=${requestScope.actionBean.peiId}&order=1&onlineOffline=${requestScope.actionBean.onlineOffline}"/>
            </c:if>
        </display:column>

        <display:setProperty name="export.excel.filename" value="${requestScope.actionBean.exportXLS}"/>
        <display:setProperty name="export.xml.filename" value="${requestScope.actionBean.exportXML}"/>
        <display:setProperty name="export.pdf.filename" value="${requestScope.actionBean.exportPDF}"/>
        <display:setProperty name="export.csv.filename" value="${requestScope.actionBean.exportCSV}"/>
    </display:table>
</c:if>
</div>