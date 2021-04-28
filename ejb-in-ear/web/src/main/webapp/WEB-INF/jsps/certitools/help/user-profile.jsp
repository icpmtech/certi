<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Perfil do Utilizador</title>
</head>

<ss:secure roles="user">
    <h2><span>Perfil do Utilizador</span></h2>

    <p>Após clicar na funcionalidade que permite aceder ao perfil do utilizador, é devolvida uma página com um
        formulário para preenchimento da sua nova palavra-chave.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/04-04-2009 13-49-25.png"
                                alt="Perfil do Utilizador"
                                width="933" height="400"/></p>

    <div class="imageCaption">Perfil do Utilizador</div>
</ss:secure>