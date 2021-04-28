<%@ include file="../../../includes/taglibs.jsp" %>

<html>
<head>
    <title><fmt:message key="gsc.view.title"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/default-style.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/plan/base.css" media="screen"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/sm/security.css"
          media="screen"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/modal/modal.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/styles/jquery/jquery-ui-1.7.custom.css"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.7.custom.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/jquery/datepicker-${pageContext.request.locale}.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/hoverIntent.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/supersubs.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/superfish-1.4.8/superfish.js"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/scripts/modal/jquery.simplemodal-1.2.3.js"></script>

    <script type="text/javascript">
        jQuery(function () {
            jQuery('ul.sf-menu').supersubs({

                maxWidth: 25,   // maximum width of sub-menus in em units
                extraWidth: 1     // extra width can ensure lines don't sometimes turn over
                                  // due to slight rounding differences and font-family
            }).superfish({
                animation: {show: "show", speed: 0},
                delay: 1000

            });
        });
        $(document).click(function () {
            $('ul.sf-menu').hideSuperfishUl();
        });
    </script>

</head>
<body>

<table class="securityHeader peiTableTitle cleaner" cellpadding="0" cellspacing="0">
    <tr>
        <td>
            <h1 class="peiH1">${requestScope.actionBean.contract.contractDesignation}</h1>
        </td>
        <td rowspan="2" class="companyLogo">
            <span class="imageContainer">
                <c:if test="${requestScope.actionBean.contract.smLogoPicture != null}">
                    <c:choose>
                        <c:when test="${requestScope.actionBean.isValidToken}">
                            <img title="<fmt:message key="security.logoPicture"/>"
                                 alt="<fmt:message key="security.logoPicture"/>"
                                 src="${pageContext.request.contextPath}/sm/SecurityEmergency.action?getContractLogoPicture=&contractId=${requestScope.actionBean.contract.id}&emergencyId=${requestScope.actionBean.emergencyId}&token=${requestScope.actionBean.token}"/>
                        </c:when>
                        <c:otherwise>
                            <img title="<fmt:message key="security.logoPicture"/>"
                                 alt="<fmt:message key="security.logoPicture"/>"
                                 src="${pageContext.request.contextPath}/sm/Security.action?getContractLogoPicture=&contractId=${requestScope.actionBean.contract.id}"/>
                        </c:otherwise>
                    </c:choose>
                </c:if>
                <c:if test="${requestScope.actionBean.isUserExpert && requestScope.actionBean.contentTemplate == 'securityFrontOffice.jsp'}">
                    <s:form beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                            id="changeLogoPictureForm">
                        <s:file id="changeLogoPictureFileInput" name="logoPicture"/>
                        <s:param name="contractId" value="${requestScope.actionBean.contract.id}"/>
                        <s:submit name="frontOffice" id="changeLogoPictureFormInput" class="button"
                        ><fmt:message key="security.changeImage"/></s:submit>
                        <input type="button" name="changeLogoPictureFormButton" id="changeLogoPictureFormButton"
                               value="<fmt:message key="security.changeImage"/>" class="button"/>
                    </s:form>
                </c:if>
            </span>
        </td>
    </tr>
    <tr>
        <td class="peiBreadcrumbs">
            <c:choose>
                <c:when test="${requestScope.actionBean.subModuleType == null}">
                    &nbsp;
                </c:when>
                <c:otherwise>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.sm.SecurityActionBean"
                            event="frontOffice">
                        <s:param name="contractId" value="${requestScope.actionBean.contractId}"/>
                        <fmt:message key="security.cover"/>
                    </s:link>
                    &gt;
                    <fmt:message key="${requestScope.actionBean.subModuleType.menuName}"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
</table>

<div class="securityMenu" ${isValidToken ? 'disabled="disabled"' : ''}>
    <c:set var="lastDepth" value="1"/>
    <c:set var="numberSections" value="0"/>
    <ul class="sf-menu">

        <c:forEach items="${requestScope.actionBean.securityMenu.menuItems}" var="menuItem" varStatus="i">

        <c:if test="${menuItem.depth == 1}">
            <c:set var="numberSections" value="${numberSections+1}"/>
        </c:if>

        <c:if test="${menuItem.enabled || menuItem.depth == 1}">

        <c:choose>
            <c:when test="${menuItem.depth > lastDepth}">
                <ul>
            </c:when>
            <c:when test="${menuItem.depth < lastDepth}">
                <c:forEach begin="${menuItem.depth}" end="${lastDepth-1}">
                    </li>
                    </ul>
                </c:forEach>
                </li>
            </c:when>
            <c:when test="${!i.first}">
                </li>
            </c:when>
        </c:choose>

        <li>

            <c:choose>
                <c:when test="${menuItem.depth == 1}">
                    <c:choose>
                        <c:when test="${menuItem.enabled}">
                            <s:link beanclass="${menuItem.action}"
                                    event="${menuItem.event}"
                                    class="${menuItem.event == requestScope.actionBean.topEvent ? 'selectedSection' : ''} firstLevelMenuItem">
                                <s:param name="contractId" value="${requestScope.actionBean.contract.id}"/>
                                <fmt:message key="${menuItem.label}"/>
                                <c:if test="${menuItem.openItems > 0}">
                                    <span class="openItemsIndicator">${menuItem.openItems}</span>
                                </c:if>
                            </s:link>
                        </c:when>
                        <c:otherwise>
                            <span class="${menuItem.event == requestScope.actionBean.topEvent ? 'selectedSection' : ''} disabledFirstLevelMenuItem">
                                <fmt:message key="${menuItem.label}"/>
                                <c:if test="${menuItem.enabled && menuItem.openItems > 0}">
                                    <span class="openItemsIndicator">${menuItem.openItems}</span>
                                </c:if>
                            </span>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <s:link beanclass="${menuItem.action}"
                            event="${menuItem.event}">
                        <s:param name="contractId" value="${requestScope.actionBean.contract.id}"/>
                        <fmt:message key="${menuItem.label}"/>
                    </s:link>
                </c:otherwise>
            </c:choose>


            <c:set var="lastDepth" value="${menuItem.depth}"/>

            <c:if test="${i.last}">

            <c:forEach begin="1" end="${lastDepth-1}">
        </li>
        <!-- last special-->
    </ul>
    </c:forEach>
    </li>
    </c:if>
    </c:if>
    </c:forEach>
    </ul><!-- last-->
</div>

<div class="securityContent cleaner">
    <jsp:include page="${requestScope.actionBean.contentTemplate}"/>
</div>

<div class="cleaner">
    <p>&nbsp;</p>

    <p>&nbsp;</p>
</div>

<script type="text/javascript">
    if (!$.browser.msie) { // for decent browser hide the file input because we can submit it by javascript
        $('#changeLogoPictureFileInput').hide();
        $('#changeLogoPictureFormInput').hide();
        $('#changeLogoPictureFormButton').click(function () {
            $('#changeLogoPictureFileInput').trigger('click');
        });
        $('#changeLogoPictureFileInput').change(function () {
            $('#changeLogoPictureFormInput').trigger('click');
        });
    } else {
        $('#changeLogoPictureFormButton').hide();
    }

    if ($.browser.msie) { // ie does not support border-radius
        $('.openItemsIndicator').corner().css('position', 'absolute').css('height', '0px').css('bottom', '11px');
    }
</script>

</body>
</html>