<%@ include file="../includes/taglibs.jsp" %>


<div class="floatRight alignRight">
    <fmt:message key="footer.copyright.message"/>
    <br/>
    <fmt:message key="footer.developed.message"/>
    <a class="img" href="http://www.criticalsoftware.com"
       onclick="window.open('http://www.criticalsoftware.com'); return false;">
        <img alt="<fmt:message key="footer.critical" />"
             src="${pageContext.request.contextPath}/images/critical-logo.png" style="padding-top:3px"/>
    </a>
</div>
<div class="cleaner"><!--Do not remove this empty div--></div>

<%@include file="analyticsFooter.jsp"%>