<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Gestão de Utilizadores</title>
</head>

<ss:secure roles="contractmanager,clientcontractmanager,administrator">
    <h2><span>Gestão de Utilizadores</span></h2>

    <p>Após clicar na funcionalidade que permite aceder à gestão de utilizadores é devolvida uma página com a seguintes
        funcionalidades:
        <em>Adicionar Utilizador,</em>
        <em>Pesquisar Utilizadores,</em>
        <em>Editar Utilizador,</em>
        <em>Eliminar Utilizador,</em>
        <em>Reset Palavra-Chave.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/02-04-2009 16-25-25.png"
                                alt="Gestão de Utilizadores"
                                width="933" height="587"/></p>

    <div class="imageCaption">Gestão de Utilizadores</div>
</ss:secure>