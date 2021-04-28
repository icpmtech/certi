<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Estatística</title>
</head>

<ss:secure roles="administrator,legislationmanager,contractmanager">
    <h2><span>Estatística</span></h2>

    <p>Após clicar na funcionalidade que permite visualizar estatísticas relativas a documentos legais e termos de
        pesquisa,
        é devolvida uma página com um formulário para inserção de um intervalo de tempo (Data início/Data fim),
        relativo
        aos
        documentos legais e aos dados de pesquisa.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/30-03-2009 19-53-25.png"
                                alt="Executar Estatística - 1"
                                width="933" height="194"/></p>

    <div class="imageCaption">Estatística</div>
</ss:secure>

