<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Selecção do Plano</title>
</head>

<ss:secure roles="user">
    <h2><span>Selecção do Plano</span></h2>

    <p>
        Após clicar na opção do menu "Plano de (Emergência/Prevenção)" é devolvida uma página para selecção do Plano
        que
        pretende
        consultar. Caso o utilizador tenha acesso apenas a um Plano ele será seleccionado
        automaticamente.
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-selecao.png"
                                alt="Selecção do Plano" width="970" height="386" class="imgBorder"/></p>

    <div class="imageCaption">Escolher Plano</div>
</ss:secure>