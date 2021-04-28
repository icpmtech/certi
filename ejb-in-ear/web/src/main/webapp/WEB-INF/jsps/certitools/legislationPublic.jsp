<%@ include file="../../../includes/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="legislation.public.title"/></title>
</head>
<body>
<div id="home-content">
    <div class="categories" style="text-align: justify;">


        <fmt:message key="legislation.public.paragraph.1"/>

        <div style="text-align: center"><img src="${pageContext.request.contextPath}/images/legislation_module_${requestScope.actionBean.context.locale}.png"
                                             alt=""/></div>

        <fmt:message key="legislation.public.paragraph.2"/>

        <div class="email">
            <a href="mailto:${applicationScope.configuration.emailInfo}">
                <fmt:message key="contacts.email.general"/>
            </a>
        </div>
        <div class="email">
            <a href="mailto:certitools.legislacao@certitecna.pt">
                <fmt:message key="contacts.email.legislation"/>
            </a>
        </div>
        <div class="email">
            <a href="mailto:certitools.PEI@certitecna.pt">
                <fmt:message key="contacts.email.pei"/>
            </a>
        </div>
        <div class="phone" style="margin-top: 20px;"><fmt:message key="login.page.fieldset.box.contact.phone"/></div>

    </div>
</div>
</body>
</html>