<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Configurações</title>
</head>

<ss:secure roles="administrator">
    <h2><span>Configurações</span></h2>

    <p>Após clicar na funcionalidade que permite aceder às configurações é devolvida uma página com uma listagem das
        configurações da aplicação.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 16-55-25.png"
                                alt="Configurações"
                                width="933" height="283"/></p>

    <div class="imageCaption">Configurações</div>
    <p>Em seguida descrevem-se algumas das opções disponíveis para configuração:</p>
    <table class="displaytag helpImportFileTable">
    <thead>
    <tr>
        <th>Campo</th>
        <th>Descrição</th>
        <th>Valores aceites</th>
    </tr>
    </thead>
        <tr>
            <td>DOCX - Centrar todas as tabelas?</td>
            <td>Aplicado na exportação para DOCX, indica se todas as tabelas exportadas são centradas.</td>
            <td>1 - sim <br>0 - não</td>
        </tr>
        <tr>
            <td>DOCX - Largura máxima das imagens em px</td>
            <td>Aplicado na exportação para DOCX, indica a largura máxima permitida para as imagens. Imagens
            de tamanho superior são reduzidas para este tamanho.</td>
            <td>Valor inteiro número maior que 0. (valor em pixeis)</td>
        </tr>
        <tr>
            <td>DOCX - Remover estilos do texto exportado?</td>
            <td>Aplicado na exportação para DOCX, indica se os estilos do texto são removidos.
            (na prática consiste em remover todos os atributos "style" e todas as tags "span")</td>
            <td>1 - sim <br>0 - não</td>

        </tr>
        <tr>
            <td>DOCX - Repetir header das tabelas em cada página nova</td>
            <td>Aplicado na exportação para DOCX, indica se, para todas as tabelas exportadas, a primeira linha
            é considerada um header e repetida quando a tabela ocupa mais que uma página.</td>
            <td>1 - sim <br>0 - não</td>            
        </tr>
        <tr>
            <td>Opção por omissão na lista de utilizadores (ex: A (...) Z, all, none)</td>
            <td>Indica a opção seleccionada por omissão quando se entra na zona de administração dos utilizadores.</td>
            <td>Aceita os valores "all" (ver todos), "none" (não seleciona nenhum) ou a letra seleccionada (ex: "A") </td>
        </tr>
</table>
</ss:secure>