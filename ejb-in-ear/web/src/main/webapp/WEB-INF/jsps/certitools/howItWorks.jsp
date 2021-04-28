<%@ include file="../../../includes/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="how.it.works.title"/></title>
</head>
<body>
<div id="home-content">
    <div class="categories" style="text-align: justify;">
        <fmt:message key="home.how.it.works.paragraph.1"/>
        <div style="text-align: center">
            <img src="${pageContext.request.contextPath}/images/how-it-works_${requestScope.actionBean.context.locale}.png" alt="" title=""/>
        </div>    
        <fmt:message key="home.how.it.works.paragraph.2"/>
        <ul>
            <li class="arrow-list"><fmt:message key="home.how.it.works.list.1"/></li>
            <li class="arrow-list"><fmt:message key="home.how.it.works.list.2"/></li>
            <li class="arrow-list"><fmt:message key="home.how.it.works.list.3"/></li>
        </ul>
        <fmt:message key="home.how.it.works.paragraph.3"/>
        <div class="contacts">
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
            <div class="phone" style="margin-top:20px"><fmt:message key="login.page.fieldset.box.contact.phone"/></div>
        </div>
    </div>
</div>
</body>
</html>