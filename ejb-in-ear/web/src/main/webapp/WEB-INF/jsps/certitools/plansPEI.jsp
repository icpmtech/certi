<%@ include file="../../../includes/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="home.page.link.plans.modulePEI"/></title>
</head>
<body>
<div id="home-content">
    <div class="categories" style="text-align: justify;">
        <fmt:message key="plans.text.top.1"/>

        <p>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                    style="color: #0000AA;font-weight: bold;">
                <s:param name="module">PSI</s:param>
                <fmt:message key="home.page.link.plans.psi"/>
            </s:link>
            &nbsp; |
            &nbsp;
            <span style="color: #C00000; font-weight: bold;">
                <fmt:message key="home.page.link.plans.pei"/>
            </span>
            &nbsp; |
            &nbsp;
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                    style="color: #00B050;font-weight: bold;">
                <s:param name="module">PPREV</s:param>
                <fmt:message key="home.page.link.plans.pprev"/>
            </s:link>
            &nbsp; |
            &nbsp;
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                    style="color: #E46C0A;font-weight: bold;">
                <s:param name="module">SECURITY</s:param>
                <fmt:message key="home.page.link.plans.security"/>
            </s:link>
            &nbsp; |
            &nbsp;
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                    style="color: #0000AA;font-weight: bold;">
                <s:param name="module">SM</s:param>
                <fmt:message key="home.page.link.plans.moduleSM"/>
            </s:link>

        </p>

        <fmt:message key="plans.text.top.2"/>

        <table border="0">
            <tr>
                <td>
                    <img src="${pageContext.request.contextPath}/images/plans-background-pei.jpg" alt=" " width="350"
                         height="1111"/>
                </td>
                <td style="padding-left: 20px; vertical-align: top; line-height: 125%;">

                    <fmt:message key="plans.pei.text.main"/>
                </td>
            </tr>
        </table>
    </div>
</div>
</body>
</html>