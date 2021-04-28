<%@ include file="../../../includes/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="contacts.title"/></title>
</head>
<body>
<div id="home-content">
    <div class="categories" style="text-align: justify;">
        <p><fmt:message key="home.contacs.paragraph.1"/></p>

        <div class="email">
            <a href="mailto:${applicationScope.configuration.emailInfo}">
                <fmt:message key="home.contacs.email.1"/>
            </a>
        </div>
        <div class="email">
            <a href="mailto:certitools.legislacao@certitecna.pt">
                <fmt:message key="home.contacs.email.2"/>
            </a>
        </div>
        <div class="email">
            <a href="mailto:certitools.PEI@certitecna.pt">
                <fmt:message key="home.contacs.email.3"/>
            </a>
        </div>
        <p><fmt:message key="home.contacs.paragraph.2"/></p>
        <div class="email">
            <a href="mailto:${applicationScope.configuration.emailInfo}">
                <fmt:message key="home.contacs.paragraph.3"/>
            </a>
        </div>
        <div class="phone" style="margin-top:20px"><fmt:message key="login.page.fieldset.box.contact.phone"/></div>
    </div>
</div>
</body>
</html>