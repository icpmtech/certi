<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Cópia do Plano</title>
</head>

<ss:secure roles="peimanager">
    <h2><span>Cópia do Plano</span></h2>

    <p>
        Após clicar na opção do menu "Cópia", é apresentada uma página que lhe permite copiar um Plano para outro
        contrato.
    </p>

    <p>
        A cópia inclui todas as pastas e conteúdos do Plano de origem (versão de trabalho e
        versão publicada), bem como as suas permissões.
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-copia.png"
                                alt="Cópia do Plano" width="970" height="377" class="imgBorder"/></p>

    <div class="imageCaption">Cópia do Plano</div>

    <p>Notas:</p>
    <ul>
        <li>Os dados da capa de um plano não são copiados;</li>
        <li>O contrato de origem ou de destino não pode conter um plano com pastas de origem de um link.</li>
    </ul>

    <p>&nbsp;</p>
</ss:secure>