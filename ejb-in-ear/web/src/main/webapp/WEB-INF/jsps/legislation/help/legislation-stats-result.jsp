<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Resultado de Estatística</title>
</head>

<ss:secure roles="administrator,legislationmanager,contractmanager">
    <h2><span>Resultado de Estatística</span></h2>

    <p>Após executar a acção de estatística correspondente ao intervalo de tempo inserido anteriormente, é devolvida
        uma
        página com as listagens de estatísticas relativas aos documentos legais e aos termos de pesquisa.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 15-23-25.png"
                                alt="Resultados de Estatística - 2" width="933" height="297"/></p>

    <div class="imageCaption">Resultados de Estatística</div>
</ss:secure>