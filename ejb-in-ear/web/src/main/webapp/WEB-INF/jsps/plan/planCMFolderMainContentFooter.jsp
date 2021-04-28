<%@ include file="../../../includes/taglibs.jsp" %>

<table class="pei-info">
    <tr>
        <td class="first">
            ${requestScope.peiPublicationInfo}
        </td>
        <td class="second">
            <c:choose>
                <c:when test="${requestScope.folderLastSavedWarning}">
                    <span class="warning-red">${requestScope.folderLastSavedInfo}</span>
                </c:when>
                <c:otherwise>
                    ${requestScope.folderLastSavedInfo}
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <c:if test="${requestScope.peiPublicationParcialInfo != null}">
        <tr>
            <td class="first warning-red">
                    ${requestScope.peiPublicationParcialInfo}
            </td>
        </tr>
    </c:if>
</table>