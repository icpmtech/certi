<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="/includes/taglibs.jsp" %>

<div class="justify helpContent" id="helpContent">
    <c:choose>
        <c:when test="${requestScope.actionBean.helpSearchableContents != null}">

            <title><fmt:message key="common.search"/></title>

            <p style="margin-top:0;padding-top:10px;margin-bottom:0">
              <span style="color:#627686;font-size:13pt;">
                  <fmt:message key="legislation.search.results"/>
              </span>
               <span>
                       (${fn:length(requestScope.actionBean.helpSearchableContents)}
                       <fmt:message key="legislation.search.results.founded"/>)
               </span>
            </p>

            <p class="separatorLine" style="margin-top:3px">&nbsp;</p>
            <c:forEach items="${requestScope.actionBean.helpSearchableContents}" var="helpSearchableContent">
                <p style="padding-left:10px;">
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean">
                        <s:param name="helpId" value="${pageScope.helpSearchableContent.fileName}"/>
                        <c:out value="${pageScope.helpSearchableContent.titleToShow}"/>
                    </s:link>
                </p>
            </c:forEach>

        </c:when>
        <c:otherwise>
            <jsp:include
                    page="${requestScope.actionBean.helpModule}/help/${requestScope.actionBean.helpId}.jsp"/>
        </c:otherwise>
    </c:choose>
</div>
