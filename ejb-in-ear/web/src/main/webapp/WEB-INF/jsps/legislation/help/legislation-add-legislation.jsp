<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Adicionar Legislação</title>
</head>

<ss:secure roles="legislationmanager">
    <h2><span>Adicionar Legislação</span></h2>

    <p>Após clicar na funcionalidade que permite adicionar uma legislação, é devolvida uma página com um formulário
        para
        preenchimento dos dados de uma nova legislação.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/adicionar-legislacao.png"
                                alt="Adicionar Legislação"
                                width="933" height="684"/></p>

    <div class="imageCaption">Adicionar Legislação</div>
    <p></p>

    <p>Ao adicionar uma legislação o utilizador deverá levar em conta as seguintes situações:
    </p>

    <ul>
        <li>Não é possível inserir documentos legais com o mesmo : Tipo / numero / ano;</li>
        <li>Quando associamos um documento legal a uma categoria pai, as categorias filhas ficam automaticamente
            associadas;
        </li>
        <li>As palavras-chave do documento legal devem estar separadas por virgulas ou ponto e virgula ou espaço;
        </li>
        <li>O documento legal associado em PDF, não deve exceder 20 Mb (e tem que ser PDF);</li>
        <li>Quando adicionamos um documento legal a outro, o campo 'nº' tem <em>'autocomplete'</em> (aparecem
            valores
            existentes),
            ou seja são apresentados todos os documentos do módulo previamente inseridos na aplicação.
        </li>
    </ul>
</ss:secure>