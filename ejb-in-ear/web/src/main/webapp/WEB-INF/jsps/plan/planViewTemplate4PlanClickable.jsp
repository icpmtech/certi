<%@ include file="../../../includes/taglibs.jsp" %>

<div id="imageMap">
    ${requestScope.actionBean.folder.template.imageMap}
</div>


<script type="text/javascript">
    $(function() {
        $('#imageMap').find("img").maphilight();
    });
</script>

