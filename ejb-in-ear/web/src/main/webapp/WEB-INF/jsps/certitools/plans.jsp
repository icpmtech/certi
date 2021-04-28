<%@ include file="../../../includes/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="home.page.link.plans"/></title>
</head>
<body>
<div id="home-content">
    <!-- TODO-MODULE -->
    <!-- TODO-MODULE create file plans<moduleName>.jsp (eg. plansPEI.jsp) -->
    <!-- TODO-MODULE update the jsp files named plan... with the link to the new module -->
    <p style="font-weight: bold;">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                style="color: #0000AA;">
            <s:param name="module">PSI</s:param>
            <fmt:message key="home.page.link.plans.psi"/>
        </s:link>
        &nbsp; |
        &nbsp;
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                style="color: #C00000;">
            <s:param name="module">PEI</s:param>
            <fmt:message key="home.page.link.plans.pei"/>
        </s:link>
        &nbsp;|&nbsp;
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                style="color: #00B050;">
            <s:param name="module">PPREV</s:param>
            <fmt:message key="home.page.link.plans.pprev"/>
        </s:link>
        &nbsp;|&nbsp;
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.PlansPublicActionBean"
                style="color: #E46C0A;">
            <s:param name="module">SECURITY</s:param>
            <fmt:message key="home.page.link.plans.security"/>
        </s:link>


    </p>
</div>
</body>
</html>