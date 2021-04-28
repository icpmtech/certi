<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Editar Notícia</title>
</head>

<ss:secure roles="administrator">
    <h2><span>Editar Notícia</span></h2>

    <p>Após clicar na funcionalidade que permite editar uma notícia, é devolvida uma página com um formulário preenchido
        com os dados da notícia seleccionada.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/04-04-2009 12-09-25.png"
                                alt="Editar Notícia"
                                width="933" height="324"/></p>

    <div class="imageCaption">Editar Notícia</div>
    <p></p>

    <p>Notas:</p>
    <ul>
        <li>Quando se adiciona uma notícia, esta só fica publicada se activarmos o campo 'Publicado'.</li>
    </ul>
</ss:secure>