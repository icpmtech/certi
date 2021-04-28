<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Capa do Plano</title>
</head>

<ss:secure roles="user">
    <h2><span>Capa do Plano</span></h2>

    <p>
        Após seleccionar um Plano é apresentada a página com a respectiva capa onde pode consultar dados
        genéricos do Plano, bem como ter acesso ao seu menu de navegação.
    </p>

    <p>
        No menu de navegação, a presença de pequenas "setas" indica que o item tem um submenu. Passe o rato sobre o
        item para abrir o respectivo submenu.
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-capa.png"
                                alt="Capa do Plano" width="966" height="776" class="imgBorder"/></p>

    <div class="imageCaption">Capa do Plano</div>

    <p>&nbsp;</p>
</ss:secure>