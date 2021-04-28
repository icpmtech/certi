<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Pesquisar Legislação por Texto Livre</title>
</head>

<c:set var="control" value="0"/>
<ss:secure roles="legislationmanager">
    <h2><span>Pesquisar Legislação por Texto Livre</span></h2>

    <p>Após clicar na funcionalidade que permite aceder à pesquisa de legislação por texto livre,é devolvida uma
        página
        com
        as seguintes funcionalidades:
        <em>Pesquisa Texto Livre,</em>
        <em>Pesquisa Categorias,</em>
        <em>Adicionar Legislação,</em>
        <em>Histórico de Nova Legislação,</em>
        <em>Histórico de Visualização de Legislação.</em>
    </p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/pesquisa-legislacao-gestor-legislacao.png"
            alt="Pesquisar Legislação"
            width="933" height="790"/></p>

    <div class="imageCaption">Pesquisar Legislação por Texto Livre</div>
    <p></p>

    <p>A pesquisa é executada sobre os seguintes campos constantes na ficha do diploma legal, por ordem decrescente
        de
        importância:
    </p>

    <ul>
        <li>Identificador do Diploma;</li>
        <li>Título do Diploma;</li>
        <li>Palavras-chave;</li>
        <li>Sumário do diploma legal;</li>
        <li>Caracterização Técnica;</li>
        <li>Diploma legal em formato PDF(a pesquisa sobre documentos em formato PDF só é possível para documentos
            desprotegidos e conteúdos em texto).
        </li>
    </ul>
    <p>Nota:</p>
    <ul>
        <li>Os resultados da pesquisa apresentam os documentos activos e inactivos na aplicação;</li>
        <li>Nas tabelas correspondentes ao <em>Histórico</em> e à <em>Nova Legislação</em>, apenas são apresentados
            os
            documentos activos na aplicação.
        </li>
    </ul>
    <c:set var="control" value="1"/>
</ss:secure>

<ss:secure roles="user">
    <c:if test="${pageScope.control != 1}">
        <h2><span>Pesquisar Legislação por Texto Livre</span></h2>

        <p>Após clicar na funcionalidade que permite aceder à pesquisa de legislação por texto livre,é devolvida uma
            página
            com
            as seguintes funcionalidades:
            <em>Pesquisa Texto Livre,</em>
            <em>Pesquisa Categorias,</em>
            <em>Histórico de Nova Legislação,</em>
            <em>Histórico de Visualização de Legislação.</em>
        </p>

        <p class="alignCenter"><img
                src="${pageContext.request.contextPath}/images/help/pesquisa-legislacao-user.png"
                alt="Pesquisar Legislação"
                width="933" height="794"/></p>

        <div class="imageCaption">Pesquisar Legislação por Texto Livre</div>
        <p></p>

        <p>Notas:
        </p>

        <ul>
            <li>Os resultados da pesquisa apenas apresentam os documentos activos na aplicação;</li>
            <li>Nas tabelas correspondentes ao <em>Histórico</em> e à <em>Nova Legislação</em>, apenas são
                apresentados
                os documentos activos na aplicação.
            </li>
        </ul>
    </c:if>
</ss:secure>