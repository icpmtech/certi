<%@ include file="../../../includes/taglibs.jsp" %>

<div class="peiTemplate2IndexView">
    <c:if test="${requestScope.actionBean.folders != null && fn:length(requestScope.actionBean.folders) >= 1}">
        <h2><span>${requestScope.actionBean.folders[0].name}</span></h2>

        <p>&nbsp;</p>
    </c:if>
    <c:if test="${requestScope.actionBean.folders != null && fn:length(requestScope.actionBean.folders) >= 2}">
        <div class="indexContainer">
            <c:forEach items="${requestScope.actionBean.folders}" var="folder" varStatus="index">

                <c:if test="${index.index != 0}">
                    <c:set var="indent" value="${folder.depth * 25}px"/>

                    <c:choose>
                        <c:when test="${folder.depth == 1}">
                            <p style="margin-left:${indent}; <c:if test="${index.index != 1}">margin-top: 30px;</c:if>" class="title"> 
                        </c:when>
                        <c:otherwise>
                            <p style="margin-left:${indent}; ">
                        </c:otherwise>
                    </c:choose>

                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.plan.PlanActionBean" event="viewResource">
                        <s:param name="path">${folder.path}</s:param>
                        <s:param name="peiId">${requestScope.actionBean.peiId}</s:param>
                        <s:param name="planModuleType">${requestScope.actionBean.planModuleType}</s:param>
                        ${folder.name}
                    </s:link>
                    </p>
                </c:if>
            </c:forEach>
        </div>
    </c:if>
</div>


<script type="text/javascript">
    $(document).ready(function() {
        /*
         $('div.peiTemplate2IndexView div.indexContainer').corner({
         tl: { radius: 16 },
         tr: { radius: 16 },
         bl: { radius: 16 },
         br: { radius: 16 },
         antiAlias: true
         });
         */

        
         $('div.peiTemplate2IndexView div.indexContainer p.title').corner({
         tl: { radius: 7 },
         bl: { radius: 7 },
         tr: { radius: 7 },
         br: { radius: 7 },
         antiAlias: true,
         autoPad: true
         });




        $('div.peiTemplate2IndexView div.indexContainer p').corner({
         tl: false,
         bl: false,
         tr: { radius: 7 },
         br: { radius: 7 },
         antiAlias: true,
         autoPad: true
         });


        $('div.peiTemplate2IndexView div.indexContainer').corner({
            tr: { radius: 12 },
            br: { radius: 12 },
            tl: { radius: 12 },
            bl: { radius: 12 },
            antiAlias: true,
            autoPad: true
        });

    });
</script>