<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Gestão de Notícias</title>
</head>

<ss:secure roles="administrator">
    <h2><span>Gestão de Notícias</span></h2>

    <p>Após clicar na funcionalidade que permite aceder à gestão de notícias, é devolvida uma página com as seguintes
        funcionalidades:
        <em>Listagem de Notícias,</em>
        <em>Adicionar Notícia,</em>
        <em>Editar Notícia,</em>
        <em>Eliminar Notícia,</em>
        <em>Desactivar Notícia,</em>
        <em>Editar Categorias de Notícias.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/02-04-2009 17-14-25.png"
                                alt="Gestão de Notícias"
                                width="933" height="415"/></p>

    <div class="imageCaption">Gestão de Notícias</div>
</ss:secure>