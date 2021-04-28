<%@ include file="../../../includes/taglibs.jsp" %>

<br>

<div class="justify ">

    <div id="questions">
        <c:forEach items="${requestScope.actionBean.folders}" var="folder" varStatus="index">
            <c:set var="indent" value="${folder.depth * 10}px"/>
            <c:choose>
                <c:when test="${folder.special}">
                    <h2 style="margin-left:${indent};"><span>${folder.name}</span></h2>
                </c:when>
                <c:otherwise>
                    <div class="questionlink" style="margin-left:${indent};">
                        <a href="#${index.index}answer">${folder.template.question}</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </div>
    <div class="questionsAnswers">
        <c:forEach items="${requestScope.actionBean.folders}" var="folder" varStatus="index">

            <c:if test="${!folder.special}">
                <hr id="${index.index}answer" class="cleaner">

                <div class="backtotop">
                    <a href="#questions">
                        <fmt:message key="faq.view.backtotop"/>
                    </a>
                </div>

                <div class="answer">

                    <div class="questionExpand">
                        <li>
                                ${folder.template.question}
                        </li>
                    </div>

                    <div class="answerExpand">
                            ${folder.template.answer}
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>
</div>