<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Detalhes do Plano</title>
</head>

<ss:secure roles="user">
    <h2><span>Detalhes do Plano</span></h2>

    <p>
        Após seleccionar uma página no menu de navegação do Plano é apresentada uma página com
        conteúdos.
        O tipo de conteúdos apresentado varia consoante o tipo da página consultada. Alguns conteúdos habituais são:
        texto formatável com imagens, lista de contactos, diagramas, plantas, organogramas, tabelas de análise de
        riscos,
        perguntas frequentes e tabelas de documentos e registos.
    </p>

    <p>
        Em seguida apresentamos a explicação de alguns conteúdos habituais de encontrar no Plano.
    </p>

    <h3>Tabela de Análise de Riscos</h3>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-analise-riscos.png"
                                alt="Página com tabela de Análise de Riscos" width="970" height="636"
                                class="imgBorder"/>
    </p>

    <div class="imageCaption">Página com tabela de Análise de Riscos</div>

    <p>&nbsp;</p>

    <p>Quando a secção actual tem definido um texto de ajuda, é possível consultá-lo, clicando
        no ícone correspondente.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-ajuda-secao.png"
                                alt="Detalhe da ajuda da secção" width="969" height="218" class="imgBorder"/></p>

    <div class="imageCaption">Detalhe da ajuda da secção</div>
    <p>&nbsp;</p>

    <h3>Organograma</h3>

    <p>
        Este tipo de conteúdo apresenta uma imagem dinâmica, com informação adicional sobre algumas áreas da imagem. Ao
        passar o rato pela imagem as áreas com informação adicional aparecem realçadas. Ao clicar nessas áreas é
        apresentada
        a informação adicional. Para além da informação textual, algumas áreas podem permitir a navegação para outras
        páginas
        ou o descarregamento de ficheiros anexos.
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-diagrama.png"
                                alt="Organograma" width="964" height="563" class="imgBorder"/></p>

    <div class="imageCaption">Página com Organograma</div>

    <p>&nbsp;</p>

    <h3>Lista de Contactos</h3>

    <p>
        Este tipo de conteúdo apresenta uma listagem de contactos, divididos consoante o seu tipo.
        Permite ainda filtrar pelo tipo de contacto e/ou efectuar uma pesquisa de texto livre pelo contacto
        pretendido.
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-contactos.png"
                                alt="Lista de contactos" width="980" height="374" class="imgBorder"/></p>

    <div class="imageCaption">Página com lista de contactos</div>
</ss:secure>