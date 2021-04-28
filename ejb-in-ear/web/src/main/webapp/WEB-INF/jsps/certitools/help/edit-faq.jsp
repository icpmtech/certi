<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Editar Pergunta Frequente</title>
</head>

<ss:secure roles="administrator,legislationmanager,peimanager">
    <h2><span>Editar Pergunta Frequente</span></h2>

    <p>Após clicar na funcionalidade que permite editar uma pergunta frequente, é devolvida uma página com um formulário
        preenchido com os dados da pergunta frequente.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/03-04-2009 22-54-25.png"
                                alt="Editar Pergunta Frequente" width="933" height="387"/></p>

    <div class="imageCaption">Editar Pergunta Frequente</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>Quando editamos uma pergunta frequente e a associamos a uma nova categoria, o campo 'Categoria' tem <em>'autocomplete'</em>
            (aparecem valores existentes), ou seja são apresentadas todas a categorias do módulo previamente escolhidas
            que têm perguntas frequentes associadas.
        </li>
    </ul>
</ss:secure>