<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Adicionar Entidade</title>
</head>

<ss:secure roles="contractmanager">
    <h2><span>Adicionar Entidade</span></h2>

    <p>Após aceder à funcionalidade que permite adicionar uma nova entidade, é devolvida uma página com um formulário
        para
        preenchimento dos dados de uma nova entidade.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/adicionar-entidade.png"
                                alt="Adicionar Entidade"
                                width="959" height="517" class="imgBorder"/></p>

    <div class="imageCaption">Adicionar Entidade</div>

    <p>Nota:</p>
    <ul>
        <li>A opção de apresentar a lista completa aplica-se a todos os planos da entidade.</li>
    </ul>
</ss:secure>