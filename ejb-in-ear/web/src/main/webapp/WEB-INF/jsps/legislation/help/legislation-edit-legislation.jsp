<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Editar Legislação</title>
</head>

<ss:secure roles="legislationmanager">
    <h2><span>Editar Legislação</span></h2>

    <p>Após clicar na funcionalidade que permite editar uma legislação, é devolvida uma página com um formulário
        preenchido com os dados da legislação.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/edicao-legislacao.png"
                                alt="Editar Legislação"
                                width="933" height="835"/></p>

    <div class="imageCaption">Editar Legislação</div>
    <p></p>

    <p>Ao editar uma legislação o utilizador deverá levar em conta as seguintes situações:
    </p>

    <ul>
        <li>Não é possível inserir documentos legais com o mesmo : Tipo / numero / ano;</li>
        <li>Quando associamos um documento legal a uma categoria pai, as categorias filhas ficam automaticamente
            associadas;
        </li>
        <li>As palavras-chave do documento legal devem estar separadas por virgulas ou ponto e virgula ou espaço;
        </li>
        <li>Ficheiro associado em PDF, não deve exceder 20 Mb (e tem que ser PDF);</li>
        <li>Na edição, caso não seja adicionado um novo ficheiro em PDF, o ficheiro inserido anteriormente é
            mantido;
        </li>
        <li>Quando adicionamos um documento legal a outro, o campo 'nº' tem <em>'autocomplete'</em> (aparecem
            valores
            existentes),
            ou seja são apresentados todos os documentos do módulo previamente inseridos na aplicação;
        </li>
        <li>Na edição de uma legislação quando o estado é alterado o campo 'Enviar Notificação (Alteração)' fica
            activo.
            No entanto este campo não é de preenchimento obrigatório, existindo sempre a possibilidade de activar o
            campo 'Enviar Notificação (Criação)' (apenas um deles poderá ser seleccionado).
        </li>
    </ul>
</ss:secure>