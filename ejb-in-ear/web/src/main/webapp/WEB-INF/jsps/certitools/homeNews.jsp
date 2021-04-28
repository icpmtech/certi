<%@ page import="com.criticalsoftware.certitools.presentation.util.HTMLEscapeAndNL2BR" %>
<%@ page import="com.criticalsoftware.certitools.entities.News" %>
<%@ include file="../../../includes/taglibs.jsp"%>
<html>
    <head>
        <title><fmt:message key="home.page.news.title"/></title>
    </head>
    <body>
        <div class="categories">
            <div>
            <c:choose>
                <c:when test="${!empty requestScope.actionBean.newsList}">
                    <h1><fmt:message key="home.page.news.title"/></h1>
                    <c:forEach items="${requestScope.actionBean.newsList}" var="news">
                        <h3 id="${pageScope.news.title}" class="news"><c:out value="${pageScope.news.title}"/></h3>
                        <p style="font-weight: bold;"><fmt:formatDate value="${pageScope.news.creationDate}" pattern="${applicationScope.configuration.datePatternNews}" /></p>
                        <p class="newsContent">
                            <% out.print(HTMLEscapeAndNL2BR.replace(((News) pageContext.getAttribute("news")).getContent())); %>
                        </p>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                        <h1><fmt:message key="home.page.news.title"/>&nbsp;-&nbsp;<c:out value="${requestScope.actionBean.news.category.name}"/></h1>
                        <h3 id="${requestScope.actionBean.news.title}"><c:out value="${requestScope.actionBean.news.title}"/></h3>
                        <p style="font-weight: bold;"><fmt:formatDate value="${requestScope.actionBean.news.creationDate}" pattern="${applicationScope.configuration.datePatternNews}" /></p>
                        <p class="newsContent">
                            <c:set var="newsContent">${requestScope.actionBean.news.content}</c:set>
                            <% out.print(HTMLEscapeAndNL2BR.replace(((String) pageContext.getAttribute("newsContent")))); %>
                        </p>
                </c:otherwise>
            </c:choose>
            </div>
        </div>
    </body>
</html>
