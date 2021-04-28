<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Pesquisar Legislação por Categoria</title>
</head>

<ss:secure roles="user">
    <h2><span>Pesquisar Legislação por Categoria</span></h2>

    <p>Após clicar na funcionalidade que permite aceder à pesquisa por categorias, é devolvida uma página com a
        listagem
        de
        todas as categorias de primeiro nível disponíveis.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pesquisa-categoria.png"
                                alt="Pesquisar Legislação por Categoria" width="933" height="595"/></p>

    <div class="imageCaption">Pesquisar Legislação por Categoria - 1</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>Os números de documentos associados às diferentes categorias consideram os documentos activos e
            inactivos na
            aplicação.
        </li>
        <li>Ao subscrever de uma categoria, todas as categorias filhas (hierarquicamente abaixo) são automaticamente
            subscritas. Significa que após a subscrição da categoria pai (no topo da hierarquia), a funcionalidade
            de
            subscrição deixa de estar disponível para as suas categorias filhas.
        </li>
    </ul>
    <p></p>

    <p>Após clicar na funcionalidade que permite aceder à categoria ambiente, é devolvida uma página com a listagem
        das
        sub-categorias associadas e a funcionalidade que permite listar a legislação associada a esta categoria.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pesquisa-categoria-2.png"
                                alt="Pesquisar Legislação por Categoria" width="933" height="592"/></p>

    <div class="imageCaption">Pesquisar Legislação por Categoria - 2</div>
    <p></p>
</ss:secure>