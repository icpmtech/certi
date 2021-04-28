<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Exportação Docx</title>
</head>

<ss:secure roles="peimanager">
<h2><span>Exportação Docx</span></h2>

<h3><a name="intro"></a>Introdução</h3>

<p>
    A exportação para o formato DOCX (Word 2007) tem por base um template de exportação (também docx), o qual contém
    marcadores especiais (bookmarks) permitindo a sua substituição automática por conteúdos do plano.
    Os marcadores especiais (bookmarks) contêm o "link" para as pastas respectivas do plano. Estes links
    podem ser consultados na gestão do plano, nas propriedades de cada pasta, sendo que alguns dos links permitem
    o uso de opções de forma a configurar a exportação.
</p>

<p>
    Para elaborar um template de exportação deve-se começar por configurar o Word 2007 de forma a mostrar
    "bookmarks". Para tal, siga os seguintes passos:
</p>
<ol class="helpIndexList">
    <li>Clique no "Botão do Office" e em seguida em "Opções do Word".</li>
    <li>Escolha a opção "Avançadas".</li>
    <li>Na zona "Mostrar conteúdo do documento", seleccionar a opção "Mostrar marcadores".</li>
</ol>
<p>&nbsp;</p>
<p>
    Para adicionar um bookmark siga os seguintes passos:
</p>
<ol class="helpIndexList">
    <li>Introduza o texto do link, exemplo: /Organograma, seguido de um espaço no final.<br /></li>
    <li>Seleccione o texto do link, sem o espaço no final, exemplo: <br /><img class="imgBorder" src="${pageContext.request.contextPath}/images/help/pei-docx-select-bookmark.png"
         alt="" width="144" height="50"/><br /></li>
    <li>Pressione ctr + shift + F5 (ou clique na tab Inserir > Marcador).<br /></li>
    <li>Introduza um nome único para o marcador (não é relevante o nome, apenas não pode ser igual a outro marcador).
        Exemplo: organograma. <br /><img src="${pageContext.request.contextPath}/images/help/pei-docx-bookmark-dialog.png"
         alt="" width="379" height="345"/><br /></li>
    <li>Clique em adicionar, o resultado final deverá ser este: <br /><img class="imgBorder" src="${pageContext.request.contextPath}/images/help/pei-docx-bookmark-done.png"
         alt="" width="135" height="32"/><br /></li>
</ol>


<!--
<p>
    Notas gerais:
</p>
<ul>
    <li>Quando não é gerado nenhum anexo, o ficheiro enviado para o cliente é apenas um docx com a exportação.
  Caso hajam ficheiros anexos ou anexos Word, é gerado um ficheiro zip com os respectivos conteúdos.</li>
</ul>
-->

<p>&nbsp;</p>
<h3><a name="templates"></a>Gestão de templates</h3>

<p>
    Na zona de gestão de templates é possível adicionar, editar, remover e associar o template a um plano. O template
    deve estar no formato docx (word 2007).
</p>
<p>
    Após a inserção de um novo template fica disponível a opção de "associar a contratos". De realçar que é necessário
    clicar em "guardar" para que as alterações de associações sejam efectuadas.
</p>
<p>
    Notas:
</p>
<ul>
    <li>Para que um template esteja disponível para ser usado na exportação de um plano é necessária a sua associação
    ao plano.</li>
    <li>Durante a exportação do plano, o utilizador irá ver o título do template.</li>
    <li>
        Na cópia de um plano também são copiadas as associações entre plano e templates.
    </li>
    <li>
        Um template fica apenas disponível para ser associado aos planos do mesmo módulo.
    </li>
</ul>

<p>&nbsp;</p>
<h3><a name="cover"></a>Capa</h3>
<p>
    Os links disponíveis para os elementos da capa de um plano são descritos na próxima tabela.
</p>
<table class="displaytag helpImportFileTable">
    <thead>
    <tr>
        <th>Link</th>
        <th>Descrição</th>
    </tr>
    </thead>
    <tr>
        <td>
            <p>cover:designation</p>
        </td>
        <td>
            <p>Designação</p>
        </td>
    </tr>
    <tr>
        <td><p>cover:author</p></td>
        <td><p>Autor</p></td>
    </tr>
    <tr>
        <td><p>cover:version</p></td>
        <td><p>Versão</p></td>
    </tr>
    <tr>
        <td><p>cover:version_date</p></td>
        <td><p>Data da versão</p></td>
    </tr>
    <tr>
        <td><p>cover:last_simulation</p></td>
        <td><p>Data do último simulacro</p></td>
    </tr>
    <tr>
        <td><p>cover:photo<br/>cover:photo:300:300</p></td>
        <td><p>Fotografia da instalação.
            É possível definir a largura e altura máxima da imagem, para tal acrescentar ao link
            as dimensões máximas no formato ":largura:altura". Por exemplo: cover.photo:300:400 significa
            que a imagem nunca excederá os 300 pixeis de largura e 400 de altura.
        </p></td>
    </tr>
    <tr>
        <td><p>cover:logo<br/>cover:logo:150:50</p></td>
        <td><p>Logótipo da empresa. Permite usar as mesmas opções de limitação do tamanho da
            imagem.</p></td>
    </tr>
</table>

<p>&nbsp;</p>
<h3><a name="richtext"></a>Texto formatável</h3>
<p>
    O texto formatável incluído neste tipo de pastas é exportado, suportando-se formas simplificadas de listas, tabelas
    e imagens incluídas.
</p>
<p>
    Dentro do texto formatável é possível marcar texto como "não exportável", usando o botão de "eliminar da exportação"
    <img src="${pageContext.request.contextPath}/images/help/pei-docx-excluir-exportacao.png"
         alt="" width="24" height="17" />. Para tal seleccione o texto a excluir e clique no botão.
    Note-se que a visualização do texto na web não será afectada.
</p>
<p>
    A exportação remove ainda os links no texto (conservando o texto do link), linhas horizontais, bem como as
    tags HTML: 'span', 'a' e 'font' (conversando o texto dentro destas tags).
</p>
<p>
    As pastas do tipo "Texto formatável com anexos" são exportadas de forma igual.
</p>
<p>
    Nota: as imagens encontradas são reduzidas para o tamanho máximo das imagens de exportação, definido na configuração
    do CertiTools (configuração: "DOCX - Largura máxima das imagens em px"). 
</p>
<p>&nbsp;</p>

<h3><a name="resource"></a>Imagem/Anexo</h3>
<p>
    Inclui o ficheiro anexo do template no zip exportado. O nome do ficheiro exportado
    tem por base o nome do anexo (e não o nome da pasta do tipo anexo).
</p>

<p>&nbsp;</p>

<h3><a name="diagram"></a>Diagrama com áreas clicáveis</h3>
<p>
    Permite dois tipos de links, explicados na tabela seguinte.
</p>

<table class="displaytag helpImportFileTable">
    <thead>
    <tr>
        <th>Link (exemplo)</th>
        <th>Descrição</th>
    </tr>
    </thead>
    <tr>
        <td>
            <p>/Organograma</p>
        </td>
        <td>
            <p>Exporta o texto formatável presente na pasta, de igual forma a um template do tipo texto formatável.</p>
        </td>
    </tr>
    <tr>
        <td><p>/Organograma:areas</p></td>
        <td><p>Exporta a descrição das várias áreas clicáveis, com os respectivos títulos. Para calcular o nível
            do heading dos títulos é consultado o nível do último heading antes do link e somado 1 nível.</p></td>
    </tr>
</table>
<p>&nbsp;</p>

<h3><a name="planclickable"></a>Diagrama de navegação por imagens</h3>
<p>
    Permite três tipos de links, explicados na tabela seguinte.
</p>

<table class="displaytag helpImportFileTable">
    <thead>
    <tr>
        <th>Link (exemplo)</th>
        <th>Descrição</th>
    </tr>
    </thead>
    <tr>
        <td>
            <p>/Plano de Evacuação/folders/Percursos Evacuação</p>
        </td>
        <td>
            <p>Exporta o texto formatável presente na pasta, de igual forma a um template do tipo texto formatável.</p>
        </td>
    </tr>
    <tr>
        <td><p>/Plano de Evacuação/folders/Percursos Evacuação:files</p></td>
        <td><p>Exporta todos os ficheiros encontrados em todas as pastas filhas do tipo "Imagem/Anexo".</p></td>
    </tr>
    <tr>
        <td><p>/Plano de Evacuação/folders/Percursos Evacuação:files:list</p></td>
        <td><p>Para além da exportação dos ficheiros,  ainda escrita a listagem dos ficheiros exportados
            no documento final (docx).</p></td>
    </tr>
</table>
<p>Os ficheiros exportados são colocados dentro de uma pasta. Essa pasta tem o nome da secção caso a exportação
seja feita dentro de uma secção, ou o nome da pasta do tipo "Diagrama de navegação por imagens" caso contrário.</p>
<p>&nbsp;</p>

<h3><a name="documents"></a>Índice de documentos</h3>

<p>
    Este tipo de templates permite dois tipos de links descritos na tabela seguinte.
</p>
<table class="displaytag helpImportFileTable">
    <thead>
    <tr>
        <th>Link (exemplo)</th>
        <th>Descrição</th>
    </tr>
    </thead>
    <tr>
        <td>
            <p>/Documentação</p>
        </td>
        <td>
            <p>Exporta todos os ficheiros encontrados em todas as pastas filhas do tipo "Documento".</p>
        </td>
    </tr>
    <tr>
        <td><p>/Documentação:list</p></td>
        <td><p>Usando a opção :list, para além da exportação, é ainda escrita a listagem dos ficheiros exportados
            no documento final (docx).</p></td>
    </tr>
</table>

<p>&nbsp;</p>
<h3><a name="doc"></a>Documento</h3>

<p>Funciona de forma similar ao índice de documentos mas apenas exporta os ficheiros encontrados na própria pasta
    (não recursivamente). Permite ainda usar a opção :list e uma nova opção :image que faz com que nenhum ficheiro
    seja exportado e a primeira imagem encontrada nos ficheiros seja incluída no documento exportado (docx).
</p>
<table class="displaytag helpImportFileTable">
    <thead>
    <tr>
        <th>Link (exemplo)</th>
        <th>Descrição</th>
    </tr>
    </thead>
    <tr>
        <td>
            <p>/Documentação/folders/Plano de Emergência/folders/Glossário</p>
        </td>
        <td>
            <p>Exporta todos os ficheiros encontrados em todas as pastas filhas do tipo "Documento".</p>
        </td>
    </tr>
    <tr>
        <td><p>/Documentação/folders/Plano de Emergência/folders/Glossário:list</p></td>
        <td><p>Usando a opção :list, para além da exportação, é ainda escrita a listagem dos ficheiros exportados
            no documento final (docx).</p></td>
    </tr>
    <tr>
        <td><p>/Documentação/folders/Plano de Emergência/folders/Glossário:image</p></td>
        <td><p>Usando a opção :imagem não são exportados ficheiros. O primeiro ficheiro do tipo imagem encontrado
            na pasta é escrito no documento final (docx).</p></td>
    </tr>
</table>

<p>&nbsp;</p>
<h3><a name="procedures"></a>Índice de procedimentos</h3>

<p>
    A exportação do índice de procedimentos exporta todas as suas pasta filhas do tipo
    "Texto formatável" ou "Texto formatável com anexos", em que cada pasta cria um "heading" novo,
    seguindo a mesma estrutura de níveis que a estrutura definida no plano.
</p>
<p>
    Os ficheiros anexos encontrados, são exportados para pastas com o nome da sua secção respectiva
    (nome da pasta filha, de primeiro nível, do índice de procedimentos).
</p>
<p>
    O link para um índice de procedimentos permite configurar o nível de "heading" que as pastas
    de primeiro nível vão usar. Por exemplo o link "/Procedimentos:h2" indica que a 1ª pasta filha
    do índice vai ficar com o heading de nível 2. São suportados níveis entre h1 e h6, sendo
    que por omissão é usado o nível 1.
</p>
<p>&nbsp;</p>

<h3><a name="contacts"></a>Índice de contactos</h3>

<p>
    A exportação de templates do tipo "Índice de Contactos" permite uma maior flexibilidade na construção
    da sua apresentação, nomeadamente o estilo a usar na tabela, quais os campos a incluir e a ordem
    das colunas.
</p>

<p>
    Para permitir esta flexibilidade não são usados bookmarks mas a funcionalidade de "Mail Merge" do Word.
    Para melhor entender esta exportação, aconselha-se a consulta
    do <a href="${pageContext.request.contextPath}/images/help/indice-contactos-exemplo.docx">ficheiro de
    exemplo</a>
    (template docx) que exporta um índice de contactos, de forma similar ao apresentado
    na visualização Web.
</p>

<p>
    Caso se pretenda criar um novo template docx siga os seguintes passos:
</p>
<ol style="line-height: 150%">
    <li>Crie uma nova tabela dentro do documento</li>
    <li>Na 1ª coluna da tabela, é necessário um "Merge Field" especial que indica o início de uma tabela.
        Para tal, no Word 2007, clique na tab "Inserir",
        botão "Peças Rápidas", opção "Campo" (em alternativa utilize o atalho do teclado: "ALT+w+q+c").
        <br>Na listagem que aparece, seleccionar "MergeField".
        <br>No campo "Nome do campo" preencher com "TableStart:/pasta-do-indice-de-contactos", substituindo
        "pasta-do-indice-de-contactos" pelo link correcto para a pasta com o índice de contactos.
        <br>Clicar em OK.
    </li>
    <li>
        Na última coluna da tabela, é necessário inserir um "Merge Field" especial para indicar o fim da tabela.
        Para tal siga as mesmas instruções do ponto anterior mas colocando como nome do field:
        "TableEnd:/pasta-do-indice-de-contactos".
    </li>
    <li>
        Para colocar os restantes campos de um contacto, devem ser adicionados "Merge Fields" entre o TableStart
        e o TableEnd. Para tal siga as mesmas instruções para adicionar um "Merge Field" mas no seu nome, indique
        o campo a exportar.
    </li>
</ol>
<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/add-merge-field.png"
                            alt="Adicionar Merge Field"
                            width="835" height="613" class="imgBorder"/></p>

<div class="imageCaption">Caixa de diálogo quando se adiciona um "Merge Field"</div>
<p>&nbsp;</p>

<p>A tabela seguinte enuncia os campos disponíveis para exportação consoante o tipo de contacto.</p>

<table class="displaytag helpImportFileTable">
    <thead>
    <tr>
        <th>Tipo contacto</th>
        <th>Campos disponíveis</th>
    </tr>
    </thead>
    <tr>
        <td>
            <p>Entidades externas</p>
        </td>
        <td>
            <ul>
                <li>external.entityType</li>
                <li>external.entityName</li>
                <li>external.email</li>
                <li>external.phone</li>
                <li>external.mobile</li>
                <li>external.photo</li>
            </ul>
        </td>
    </tr>
    <tr>
        <td><p>Entidades internas</p></td>
        <td>
            <ul>
                <li>internal.personName</li>
                <li>internal.entityName</li>
                <li>internal.email</li>
                <li>internal.phone</li>
                <li>internal.mobile</li>
                <li>internal.photo</li>
            </ul>
        </td>
    </tr>
    <tr>
        <td><p>Estrutura operacional de emergência</p></td>
        <td>
            <ul>
                <li>emergency.personName</li>
                <li>emergency.personPosition</li>
                <li>emergency.personArea</li>
                <li>emergency.email</li>
                <li>emergency.phone</li>
                <li>emergency.mobile</li>
                <li>emergency.photo</li>
            </ul>
        </td>
    </tr>
</table>

<p>&nbsp;</p>
<h3><a name="riskanalysis"></a>Análise de Riscos</h3>
<p>
    A exportação da Análise de Riscos também utiliza a funcionalidade de "Mail Merge" do Word, pelo que deve consultar
    o <a href="${pageContext.request.contextPath}/images/help/analise-riscos-exemplo.docx">ficheiro anexo de exemplo</a>.
</p>
<p>
    Os ficheiros anexos referenciados na tabela de análise de riscos são também exportados e colocados numa pasta.
    Essa pasta tem o nome da secção caso a exportação seja feita dentro de uma secção, ou o nome da pasta do tipo
    "Diagrama de navegação por imagens" caso contrário.
</p>
<p>
    Para criar, de raiz, um template docx que exporta uma tabela de Análise de Riscos, deve consultar as instruções
    para o "Índice de contactos", sendo que a diferença reside nos campos disponíveis para exportação, que no caso
    da Análise de Riscos são enunciados na seguinte tabela.
</p>
<table class="displaytag helpImportFileTable">
    <thead>
    <tr>
        <th>Campo</th>
        <th>Descrição</th>
    </tr>
    </thead>
    <tr>
        <td>
            <p>product</p>
        </td>
        <td>
            <p>Produto</p>
        </td>
    </tr>
    <tr>
        <td><p>releaseConditions</p></td>
        <td><p>Condições de libertação</p></td>
    </tr>
    <tr>
        <td><p>weather</p></td>
        <td><p>Meteorologia</p></td>
    </tr>
    <tr>
        <td><p>ignitionPoint</p></td>
        <td><p>Inflamação</p></td>
    </tr>
    <tr>
        <td><p>radiation</p></td>
        <td><p>Radiação</p></td>
    </tr>
    <tr>
        <td><p>pressurized</p></td>
        <td><p>Sobrepressão</p></td>
    </tr>
    <tr>
        <td><p>toxicity</p></td>
        <td><p>Toxicidade</p></td>
    </tr>
</table>

<p>&nbsp;</p>
<h3><a name="meansresources"></a>Índice de Meios e Recursos</h3>
<p>
    A exportação de templates do tipo "Índice de Meios/Recursos" também utiliza a funcionalidade de "Mail Merge" do Word, pelo que deve consultar
    o <a href="${pageContext.request.contextPath}/images/help/meios-recursos-exemplo.docx">ficheiro anexo de exemplo</a>.
</p>
<p>
    Para criar, de raiz, um template docx que exporta uma tabela de Meios e Recursos, deve consultar as instruções
    para o "Índice de contactos", sendo que a diferença reside nos campos disponíveis para exportação, que no caso
    de Meios e Recursos são enunciados na seguinte tabela.
</p>
<table class="displaytag helpImportFileTable">
    <thead>
    <tr>
        <th>Campo</th>
        <th>Descrição</th>
    </tr>
    </thead>
    <tr>
        <td><p>resourceName</p></td>
        <td><p>Nome</p></td>
    </tr>
    <tr>
        <td><p>resourceType</p></td>
        <td><p>Tipo de recurso</p></td>
    </tr>
    <tr>
        <td><p>entityName</p></td>
        <td><p>Nome da entidade</p></td>
    </tr>
    <tr>
        <td><p>quantity</p></td>
        <td><p>Quantidade</p></td>
    </tr>
    <tr>
        <td><p>characteristics</p></td>
        <td><p>Características</p></td>
    </tr>
</table>

<p>&nbsp;</p>
<h3><a name="annexes"></a>Secções Anexas</h3>
<p>
    No template docx de exportação é possível definir secções anexas. Durante a exportação, as secções anexas são
    removidas do documento DOCX principal exportado, sendo gerado um novo documento DOCX para cada secção.
    Além disso, o nome das secções anexas é prefixado aos ficheiros anexos presentes em cada secção.
</p>
<p>
    Para criar uma nova secção anexa, siga os seguintes passos:
</p>
<ol>
    <li>Adicione uma nova quebra de secção no Word. Para isso vá a Esquema de Página > Quebras > Quebras de Secção, Página Seguinte.</li>
    <li>Na nova secção, adicione o link que identifica a secção como uma secção anexa. Para tal
    crie um link novo com o formato: "Annex:Nome do Anexo". Por exemplo: Annex: Anexo A - Procedimentos.</li>
</ol>
<p>
    Dentro da secção criada, podem-se incluir todos os tipos de links para pastas, tal como se faz para o
    template principal DOCX. Os conteúdos desses links serão exportados para o novo ficheiro DOCX, correspondente à
    secção anexa.
</p>
<p>
    Caso não se pretenda incluir o DOCX correspondente à secção anexa no ficheiro zip final, deve-se utilizar a opção
    :delete no link. Por exemplo: "Annex: Anexo G - Lista de abreviaturas:delete". Desta forma apenas os
    ficheiros anexos serão incluídos no zip de exportação.
</p>
<p>
    O <a href="${pageContext.request.contextPath}/images/help/anexos-exemplo.docx">ficheiro de
    exemplo</a> contêm um ficheiro DOCX com várias secções anexas.
</p>
<p>
    Caso se usem cabeçalhos/rodapés nas secções anexas, é necessário verificar que não se encontram
    ligados às secções anteriores, caso contrário as secções anexas ficarão sem cabeçalhos/rodapés.
    Para tal, seleccione o cabeçalho/rodapé e na tab Estrutura desligue a opção "Ligar ao anterior". A imagem
    seguinte ilustra um exemplo errado e correcto de um rodapé.
</p>
<p class="alignCenter"><img class="imgBorder" src="${pageContext.request.contextPath}/images/help/pei-docx-sections-example.png"
        width="664" height="299" alt="" /></p>
</ss:secure>