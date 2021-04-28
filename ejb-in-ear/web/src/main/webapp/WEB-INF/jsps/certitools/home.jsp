<%@ include file="../../../includes/taglibs.jsp"%>
<html>
    <head>
        <title><fmt:message key="home.page.title" /></title>
    </head>
    <body>
    <table class="otherHomeLinks">
        <tr>
            <td>
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.WhatItIsActionBean" >
                        <img src="${pageContext.request.contextPath}/images/u52_${requestScope.actionBean.context.locale}.png" alt="" />
                </s:link>
            </td>
            <td>
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.HowItWorksActionBean" >
                        <img src="${pageContext.request.contextPath}/images/u53_${requestScope.actionBean.context.locale}.png" alt="" />
                </s:link>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/Home.action?viewClients=" class="clients" title="<fmt:message key="home.page.clients"/>">
                    <img src="${pageContext.request.contextPath}/images/u54_${requestScope.actionBean.context.locale}.png" alt="" />
                </a>
            </td>
            <!--<td>
                <a href="https://www.certitools.pt/UserRegister.action?contractId=92&code=7f244eb7d73664720b67b9d59dee828f" class="legislation" title="<fmt:message key="menu.legislation"/>">
                    <img src="${pageContext.request.contextPath}/images/u55_${requestScope.actionBean.context.locale}.png" alt="" />
                </a>
            </td>-->
        </tr>
    </table>
        <div class="cleaner"><!----></div>
        <div class="categories">
            <div class="categoryLeft">
            <div class="catTitle">
                <c:out value="${requestScope.actionBean.first.name}"/>
            </div>
            <div class="catNews">
                <ul>
                    <c:forEach items="${requestScope.actionBean.first.news}" var="newA">
                    <li>
                        <fmt:formatDate value="${pageScope.newA.creationDate}" pattern="${applicationScope.configuration.datePatternNews}" />&nbsp;
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.NewsActionBean" event="showOne">
                            <s:param name="news.id" value="${pageScope.newA.id}"/>
                            <c:out value="${pageScope.newA.title}"/>
                        </s:link>
                    </li>
                    </c:forEach>
                </ul>
            </div>
            <div class="catOthers">
                >>
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.NewsActionBean" event="showAll">
                    <s:param name="news.category.id" value="${requestScope.actionBean.first.id}"/>
                    <fmt:message key="home.page.news.other.link2"/>
                </s:link>
            </div>
        </div>
        <div class="categoryRight">
            <div class="catTitle">
                <c:out value="${requestScope.actionBean.second.name}"/>
            </div>
            <div class="catNews">
                <ul>
                    <c:forEach items="${requestScope.actionBean.second.news}" var="newB">
                    <li>
                        <fmt:formatDate value="${pageScope.newB.creationDate}" pattern="${applicationScope.configuration.datePatternNews}" />
                        &nbsp;
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.NewsActionBean" event="showOne">
                            <s:param name="news.id" value="${pageScope.newB.id}"/>

                            <c:out value="${pageScope.newB.title}"/>
                        </s:link>
                    </li>
                    </c:forEach>
                </ul>
            </div>
            <div class="catOthers">
                >>
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.NewsActionBean" event="showAll">
                    <s:param name="news.category.id" value="${requestScope.actionBean.second.id}"/>
                    <fmt:message key="home.page.news.other.link2"/>
                </s:link>
            </div>
        </div>
        <div class="cleaner"><!--Do not remove this empty div--></div>
        </div>



    <script type="text/javascript">
    $(document).ready(function() {
        $(".clients").colorbox({width:"${applicationScope.configuration.homeCustomersWidth}", height:"${applicationScope.configuration.homeCustomersHeight}", scrolling: true});
        /* $(".legislation").colorbox({width:"680", height:"560", iframe: true, scrolling: false}); */
        /*$(".pei").colorbox({width:"680", height:"560", iframe: true, scrolling: false});*/
    });
    </script>

    </body>
</html>
