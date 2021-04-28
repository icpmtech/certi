<%@ include file="../../../includes/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="user.register.main.title"/></title>
</head>
<body>
<div id="home-content">
    <div class="categories">

        <c:if test="${requestScope.actionBean.showForm == null || requestScope.actionBean.showForm == true}">
            <h2 class="form"><span><fmt:message key="user.register.main.title"/></span></h2>
        </c:if>

        <s:messages/>
        <s:errors/>

        <c:choose>
            <c:when test="${requestScope.actionBean.showForm == null || requestScope.actionBean.showForm == true}">
                <s:form beanclass="com.criticalsoftware.certitools.presentation.action.UserRegisterActionBean"
                        focus="" class="form">
                    <p>
                        <label><fmt:message key="companies.company"/> (*):</label>
                        <span class="fixedInput">${requestScope.actionBean.company.name}</span>
                        <s:hidden name="user.company.id" value="${requestScope.actionBean.company.id}"/>
                    </p>

                    <p>
                        <s:label for="name"><fmt:message key="user.name"/> (*):</s:label>
                        <s:text id="name" name="user.name" class="largeInput"/>
                    </p>

                    <s:label for="email"><fmt:message key="user.email"/> (*):</s:label>
                    <s:text id="email" name="user.email" class="largeInput"/>

                    <p>
                        <s:label for="nif"><fmt:message key="user.fiscalNumber"/> (*):</s:label>
                        <s:text id="nif" name="user.fiscalNumber" class="smallInput noMarginBottom"/>
                    </p>
                    <p style="margin-bottom: 10px;" class="formSubtitle">
                        <fmt:message key="user.register.fiscalNumber.subLabel"/>
                    </p>

                    <p>
                        <s:label for="phone"><fmt:message key="user.phone"/>:</s:label>
                        <s:text id="phone" name="user.phone" class="smallInput"/>
                    </p>

                    <s:hidden name="contractId"/>
                    <s:hidden name="code"/>

                    <div class="formButtons">
                        <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                        <s:submit name="insertUser" class="button"><fmt:message key="common.submit"/></s:submit>
                    </div>
                </s:form>
            </c:when>
            <c:when test="${requestScope.actionBean.success != null && requestScope.actionBean.success}">
                <fmt:message key="user.register.sucess.title"/>
            </c:when>
        </c:choose>


    </div>
</div>

</body>
</html>
