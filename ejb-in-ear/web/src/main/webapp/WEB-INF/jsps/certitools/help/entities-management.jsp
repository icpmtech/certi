<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Gestão de Entidades</title>
</head>

<ss:secure roles="administrator">
    <h2><span>Gestão de Entidades</span></h2>

    <p>Após clicar na funcionalidade que permite aceder à gestão de entidades é devolvida uma página que permite
        pesquisar entidades.
        Após efectuar uma pesquisa, clicar numa entidade e as seguintes funcionalidades serão devolvidas:
        <em>Adicionar Utilizador,</em>
        <em>Listar Utilizadores.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/lista-entidades-admin.png"
                                alt="Gestão de Entidades"
                                width="933" height="564"/></p>

    <div class="imageCaption">Gestão de Entidades</div>
</ss:secure>

<ss:secure roles="contractmanager,clientcontractmanager">
    <h2><span>Gestão de Entidades</span></h2>

    <p>Após clicar na funcionalidade que permite aceder à gestão de entidades é devolvida uma página com a seguintes
        funcionalidades:
        <em>Adicionar Entidade,</em>
        <em>Pesquisar Entidades.</em>
        Após efectuar uma pesquisa, clicar numa entidade e as seguintes funcionalidades serão devolvidas:
        <em>Adicionar Entidade,</em>
        <em>Editar Entidade,</em>
        <em>Eliminar Entidade,</em>
        <em>Adicionar Contrato,</em>
        <em>Adicionar Utilizador,</em>
        <em>Listar Utilizadores,</em>
        <em>Importar Utilizadores.</em>
    </p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/lista-entidades-gestor-certitecna.png"
            alt="Gestão de Entidades"
            width="970" height="643" class="imgBorder"/></p>

    <div class="imageCaption">Gestão de Entidades</div>
    <p></p>

    <p>Notas:</p>
    <ul>
        <li>Não se pode eliminar uma entidade se esta tiver utilizadores ou contratos associados;</li>
        <li>Não se pode eliminar um contrato se este tiver utilizadores associados;</li>
        <li>Não se pode eliminar um contrato se este tiver pastas de origem de um link;</li>
        <li>A lista de contratos é ordenada pelo número e designação.</li>
    </ul>
</ss:secure>