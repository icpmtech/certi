<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Listagem de Perguntas Frequentes</title>
</head>

<c:set var="control" value="0"/>

<ss:secure roles="legislationmanager,peimanager">

    <c:set var="control" value="1"/>

    <h2><span>Listagem de Perguntas Frequentes</span></h2>

    <p>Após clicar na funcionalidade que permite aceder à listagem de perguntas frequentes, é devolvida uma página com
        as
        seguintes funcionalidades:
        <em>Gestão de Perguntas Frequentes,</em>
        <em>Detalhes de Perguntas Frequentes.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/perguntas-frequentes.png"
                                alt="Perguntas Frequentes"
                                width="970" height="535"/></p>

    <div class="imageCaption">Perguntas Frequentes</div>
    <p>Após clicar numa pergunta frequente, os seus detalhes são devolvidos.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/30-03-2009 16-23-25.png"
                                alt="Detalhes de Pergunta Frequente" width="933" height="256"/></p>

    <div class="imageCaption">Detalhes de Pergunta Frequente</div>
</ss:secure>

<ss:secure roles="user">
    <c:if test="${pageScope.control != 1}">
        <h2><span>Listagem de Perguntas Frequentes</span></h2>

        <p>Após clicar na funcionalidade que permite aceder à listagem de perguntas frequentes,é devolvida uma página
            com as
            seguintes funcionalidades:
            <em>Detalhes de Perguntas Frequentes.</em>
        </p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/perguntas-frequentes-user.png"
                                    alt="Perguntas Frequentes"
                                    width="933" height="495"/></p>

        <div class="imageCaption">Perguntas Frequentes</div>
        <p>Após clicar numa pergunta frequente, os seus detalhes são devolvidos.</p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/30-03-2009 16-23-25.png"
                                    alt="Detalhes de Pergunta Frequente" width="933" height="256"/></p>

        <div class="imageCaption">Detalhes de Pergunta Frequente</div>
    </c:if>
</ss:secure>