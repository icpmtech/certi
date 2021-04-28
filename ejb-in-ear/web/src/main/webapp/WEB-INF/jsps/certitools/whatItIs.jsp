<%@ include file="../../../includes/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="what.it.is.simpletitle"/></title>
</head>
<body>
<div id="home-content">
    <div class="categories" style="text-align: justify;">
        <fmt:message key="home.what.it.is.paragraph.1"/>
        <div style="text-align: center">
            <img src="${pageContext.request.contextPath}/images/what-it-is-1_${requestScope.actionBean.context.locale}.png"
                 alt="" title=""/>
        </div>
        <p>&nbsp;</p>
        <fmt:message key="home.what.it.is.paragraph.2"/>
        <div style="text-align: center">
            <img src="${pageContext.request.contextPath}/images/what-it-is-2_${requestScope.actionBean.context.locale}.png"
                 alt="" title=""/>
        </div>
        <ul>
            <li class="arrow-list"><fmt:message key="home.what.it.is.list.1"/></li>
            <li class="arrow-list"><fmt:message key="home.what.it.is.list.2"/></li>
            <li class="arrow-list"><fmt:message key="home.what.it.is.list.3"/></li>
            <li class="arrow-list"><fmt:message key="home.what.it.is.list.4"/></li>
            <li class="arrow-list"><fmt:message key="home.what.it.is.list.5"/></li>
        </ul>

        <p>&nbsp;</p>
        <fmt:message key="home.what.it.is.paragraph.3"/>
        <div style="text-align: center">
            <img src="${pageContext.request.contextPath}/images/what-it-is-3.png" alt="" title=""/>
        </div>
        <fmt:message key="home.what.it.is.paragraph.4"/>
        <div class="contacts">
            <div class="email">
                <a href="mailto:${applicationScope.configuration.emailInfo}">
                    <fmt:message key="contacts.email.general"/>
                </a>
            </div>
            <div class="email">

                <script type="text/javascript">
                document.write('<a href="mailto:' + 'certitools.legisl' + 'acao@cert' + 'itecna.pt' + '">' + '<fmt:message key="contacts.email.legislation"/>' + '</a>');
                </script>

            </div>
            <div class="email">
                <script type="text/javascript">
                document.write('<a href="mailto:' + 'certitools' + '.PEI' + '@certi' + 'tecna.pt' + '">' + '<fmt:message key="contacts.email.pei"/>' + '</a>');
                </script>
               
            </div>
            <div class="phone" style="margin-top: 20px;"><fmt:message
                    key="login.page.fieldset.box.contact.phone"/></div>
        </div>
    </div>
</div>
</body>
</html>