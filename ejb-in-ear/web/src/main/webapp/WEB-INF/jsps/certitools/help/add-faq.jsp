<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Adicionar Pergunta Frequente</title>
</head>

<ss:secure roles="administrator,legislationmanager,peimanager">
    <h2><span>Adicionar Pergunta Frequente</span></h2>

    <p>Após clicar na funcionalidade que permite adicionar uma pergunta frequente, é devolvida uma página com um
        formulário
        para preenchimento dos dados de uma nova pergunta frequente.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/30-03-2009 19-06-25.png"
                                alt="Adicionar Pergunta Frequente" width="933" height="384"/></p>

    <div class="imageCaption">Adicionar Pergunta Frequente</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>Quando adicionamos uma pergunta frequente e a associamos a uma categoria, o campo 'Categoria' tem <em>'autocomplete'</em>
            (aparecem valores existentes), ou seja são apresentadas todas a categorias do módulo previamente escolhidas
            que têm perguntas frequentes associadas.
        </li>
    </ul>
</ss:secure>