<%@ include file="includes/taglibs.jsp"%>

<html>
    <head>
        <title><fmt:message key="error.title" /></title>
        <c:choose>
                <c:when test="${param.httpError == 400 || param.httpError == 401 ||
                                param.httpError == 403 || param.httpError == 404 ||
                                param.httpError == 408 || param.httpError == 500 ||
                                param.httpError == 503}">
                    <meta name="description" content="<fmt:message key="error.http.subtitle.${param.httpError}" />"/>
                </c:when>
                <c:when test="${!empty requestScope.error}">
                    <meta name="description" content="${requestScope.subtitle}"/>
                </c:when>
                <c:otherwise>
                    <meta name="description" content="<fmt:message key="error.internal.subtitle" />"/>
                </c:otherwise>
            </c:choose>
    </head>
    <body>
        <div class="errors">
            <c:choose>
                <c:when test="${param.fileUploadException}">
                     <h1><fmt:message key="error.FileUploadLimitExceededException.title"/></h1>
                    <fmt:message key="error.FileUploadLimitExceededException.subtitle"/> (${param.maxFileSize}MB).
                </c:when>
                <c:when test="${param.httpError == 400 || param.httpError == 401 ||
                                param.httpError == 403 || param.httpError == 404 ||
                                param.httpError == 408 || param.httpError == 500 ||
                                param.httpError == 503}">
                    <h1><fmt:message key="error.http.subtitle.${param.httpError}" /></h1>
                    <fmt:message key="error.http.${param.httpError}" />
                </c:when>
                <c:when test="${!empty requestScope.error}">
                    <h1>${requestScope.subtitle}</h1>
                    ${requestScope.error}
                </c:when>
                <c:otherwise>
                    <h1><fmt:message key="error.internal.subtitle" /></h1>
                    <fmt:message key="error.internal" />
                </c:otherwise>
            </c:choose>
        </div>
        <script type="text/javascript">
            <c:choose>
                <c:when test="${requestScope.fileUploadException}">
                    if (self != top) {
                        top.location = '${pageContext.request.contextPath}' + '/error.jsp?fileUploadException=true&maxFileSize='+${requestScope.maxFileSize};
                    }    
            </c:when>
                <c:otherwise>
                    if (self != top) {
                       top.location = '${pageContext.request.contextPath}' + '/error.jsp';
                    }
                </c:otherwise>
            </c:choose>
        </script>
    </body>
</html>