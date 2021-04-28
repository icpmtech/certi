<%@ include file="taglibs.jsp" %>

<div class="rightColumn" style="text-align:left;margin-top:16px;">

<div class="legislationRightContent cleaner" style="border-color:#E10000; border-width:1px">

    <div class="title">
        <div class="newLegislation">
            <fmt:message key="legislation.newLegislationBox.new"/>
        </div>
    </div>

    <c:forEach items="${requestScope.actionBean.lastInsertedLegalDocuments}" var="lastLegalDocument"
               varStatus="index">
        <div class="content">

            <h4>
                <c:set var="text">
                    <c:out value="${lastLegalDocument.fullDrTitle}" escapeXml="true"/> - <c:out
                        value="${lastLegalDocument.summary}" escapeXml="true"/>
                </c:set>

                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                        event="viewLegislation"
                        title="${text}"
                        id="lastInserted${lastLegalDocument.id}">

                    <c:out value="${lastLegalDocument.fullDrTitle}" escapeXml="true"/>
                    <s:param name="legalDocument.id" value="${lastLegalDocument.id}"/>
                </s:link>
            </h4>

            <p class="summary <c:if test="${index.index != fn:length(requestScope.actionBean.lastInsertedLegalDocuments)-1}"> borderBottom</c:if>">
                <c:out value="${lastLegalDocument.reducedField}" escapeXml="true"/>
            </p>
            <script type="text/javascript">
                $(function() {
                    $("#lastInserted${lastLegalDocument.id}").tooltip({
                        track: true,
                        delay: 0,
                        showURL: false,
                        opacity: 0,
                        fixPNG: true,
                        showBody: " - ",
                        fade: 250
                    });
                });
            </script>

        </div>
    </c:forEach>
    <c:if test="${requestScope.actionBean.lastInsertedLegalDocuments == null || fn:length(requestScope.actionBean.lastInsertedLegalDocuments) == 0}">
        <div class="content">
            <fmt:message key="legislation.newLegislationBox.doesNotExist"/>
        </div>
    </c:if>
</div>

<div class="legislationRightContent" style="margin-top:30px">

    <div class="title">
        <div class="history">
            <fmt:message key="legislation.visualizedLegislationBox.history"/>
        </div>
    </div>
    <div class="message"><fmt:message key="legislation.visualizedLegislationBox.information"/></div>

    <c:forEach items="${requestScope.actionBean.lastVisualizedLegalDocuments}" var="legalDocument"
               varStatus="index">
        <div class="content">

            <c:set var="text2">
                <c:out value="${legalDocument.fullDrTitle}" escapeXml="true"/> - <c:out
                    value="${legalDocument.summary}" escapeXml="true"/>
            </c:set>

            <h4>
                <s:link beanclass="com.criticalsoftware.certitools.presentation.action.legislation.LegislationActionBean"
                        event="viewLegislation" title="${text2}"
                        id="history${legalDocument.id}">

                    <c:out value="${legalDocument.fullDrTitle}" escapeXml="true"/>
                    <s:param name="legalDocument.id" value="${legalDocument.id}"/>
                </s:link>
            </h4>

            <script type="text/javascript">
                $(function() {
                    $('#history${legalDocument.id}').tooltip({
                        track: true,
                        delay: 0,
                        showURL: false,
                        opacity: 0,
                        fixPNG: true,
                        showBody: " - ",
                        fade: 250                      
                    });
                });
            </script>
            <p class="summary <c:if test="${index.index != fn:length(requestScope.actionBean.lastVisualizedLegalDocuments)-1}"> borderBottom</c:if>">
                <c:out value="${legalDocument.reducedField}" escapeXml="true"/>
            </p>
        </div>
    </c:forEach>
    <c:if test="${requestScope.actionBean.lastVisualizedLegalDocuments == null || fn:length(requestScope.actionBean.lastVisualizedLegalDocuments) == 0}">
        <div class="content">
            <fmt:message key="legislation.visualizedLegislationBox.doesNotExist"/>
        </div>
    </c:if>

</div>
</div>
<div class="cleaner"><!--Do not remove this empty div--></div>