<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Subscrição de Newsletter</title>
</head>

<ss:secure roles="administrator,legislationmanager">
    <h2><span>Subscrição de Newsletter</span></h2>

    <p>Após clicar na funcionalidade que permite aceder às subscrições, é devolvida uma página com as seguintes
        funcionalidades:
        <em>Editar Subscrição de Newsletter,</em>
        <em>Listagem do conteúdo da Subscrição de Newsletter.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 15-39-25.png"
                                alt="Subscrição"
                                width="933"
                                height="216"/></p>

    <div class="imageCaption">Subscrição de Newsletter</div>
</ss:secure>