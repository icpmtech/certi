<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Detalhe de Legislação</title>
</head>

<c:set var="control" value="0"/>

<ss:secure roles="legislationmanager">
    <c:set var="control" value="1"/>
    <h2><span>Detalhe de Legislação</span></h2>

    <p>Após clicar na funcionalidade que permite aceder ao detalhe de uma legislação, é devolvida uma página com a
        informação relativa à legislação seleccionada.</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/detalhe-legislacao-gestor-legislacao.png"
            alt="Detalhe de Legislação"
            width="933" height="808"/></p>

    <div class="imageCaption">Detalhe de Legislação</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>Apenas se poderá remover um documento legal, se a este não estiverem associados outros documentos
            legais.
        </li>
    </ul>
</ss:secure>

<ss:secure roles="userguest">
    <c:if test="${pageScope.control != 1}">
        <c:set var="control" value="1"/>
        <h2><span>Detalhe de Legislação</span></h2>

        <p>Após clicar na funcionalidade que permite aceder ao detalhe de uma legislação, é devolvida uma página com a
            informação relativa à legislação seleccionada.</p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/detalhe-legislacao-invited.png"
                                    alt="Detalhe de Legislação"
                                    width="933" height="624"/></p>

        <div class="imageCaption">Detalhe de Legislação</div>
    </c:if>
</ss:secure>

<ss:secure roles="user">
    <c:if test="${pageScope.control != 1}">
        <h2><span>Detalhe de Legislação</span></h2>

        <p>Após clicar na funcionalidade que permite aceder ao detalhe de uma legislação, é devolvida uma página com a
            informação relativa à legislação seleccionada.</p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/detalhe-legislacao-user.png"
                                    alt="Detalhe de Legislação"
                                    width="933" height="757"/></p>

        <div class="imageCaption">Detalhe de Legislação</div>
    </c:if>
</ss:secure>