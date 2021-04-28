<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="help.title"/></title>

<a name="top"></a>

<h1><fmt:message key="help.title"/></h1>

<ss:secure roles="peiAccess">
<ul>
    <li class="helpTOC">
        <a href="#pei">Plano de Emergência (PEI)</a>
        <ul>
            <ss:secure roles="user">
                <li><a href="#peiMain">Selecção Plano de Emergência</a></li>
                <li><a href="#peiView">Capa Plano de Emergência</a></li>
                <li><a href="#peiViewResource">Detalhes Plano de Emergência</a></li>
            </ss:secure>
            <ss:secure roles="peimanager,clientpeimanager">
                <li><a href="#peiCM">Gestão Plano de Emergência</a></li>
            </ss:secure>
            <ss:secure roles="peimanager">
                <li><a href="#peiCopy">Cópia Plano de Emergência</a></li>
            </ss:secure>
            <ss:secure roles="peimanager,clientpeimanager">
                <li><a href="#peiPermissions">Gestão de Permissões</a></li>
            </ss:secure>
            <ss:secure roles="peimanager,clientpeimanager">
                <li><a href="#peiExportdocx">Exportação Docx</a></li>
            </ss:secure>
        </ul>
    </li>

</ul>

<div class="justify helpText">

<!--========================================================PEI======================================================!-->
<p>&nbsp;</p>

<hr/>

<a name="plan"></a>

<div class="helpModule">Plano de Emergência (PEI)</div>

<ss:secure roles="user">
<a name="peiMain"></a>

<h2 style="margin-top: 10px;"><span>Selecção Plano de Emergência</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Após clicar na opção do menu "Plano de Emergência" é devolvida uma página para selecção do Plano de Emergência
    que
    pretende
    consultar. Caso o utilizador tenha acesso apenas a um Plano de Emergência ele será seleccionado
    automaticamente.

</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-selecao.png"
                            alt="Escolher Plano de Emergência" width="970" height="386" class="imgBorder"/></p>

<div class="imageCaption">Escolher Plano de Emergência</div>

<p>&nbsp;</p>

<a name="peiView"></a>

<h2><span>Capa Plano de Emergência</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Após seleccionar um Plano de Emergência é apresentada a página com a respectiva capa onde pode consultar dados
    genéricos do PEI, bem como ter acesso ao seu menu de navegação.
</p>

<p>
    No menu de navegação, a presença de pequenas "setas" indica que o item tem um submenu. Passe o rato sobre o
    item para abrir o respectivo submenu.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-capa.png"
                            alt="Capa Plano de Emergência" width="966" height="776" class="imgBorder"/></p>

<div class="imageCaption">Capa Plano de Emergência</div>

<p>&nbsp;</p>

<a name="peiViewResource"></a>

<h2><span>Detalhes Plano de Emergência</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Após seleccionar uma página no menu de navegação do Plano de Emergência é apresentada uma página com
    conteúdos.
    O tipo de conteúdos apresentado varia consoante o tipo da página consultada. Alguns conteúdos habituais são:
    texto formatável com imagens, lista de contactos, diagramas, plantas, organogramas, tabelas de análise de
    riscos,
    perguntas frequentes e tabelas de documentos e registos.
</p>

<p>
    Em seguida apresentamos a explicação de alguns conteúdos habituais de encontrar no Plano de Emergência.
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

<a name="peiOrganograma"></a>

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
                            alt="Organograma" width="968" height="613" class="imgBorder"/></p>

<div class="imageCaption">Página com Organograma</div>

<p>&nbsp;</p>

<h3>Lista de Contactos</h3>

<p>
    Este tipo de conteúdo apresenta uma listagem de contactos, divididos consoante o seu tipo.
    Permite ainda filtrar pelo tipo de contacto e/ou efectuar uma pesquisa de texto livre pelo contacto
    pretendido.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-contactos.png"
                            alt="Lista de contactos" width="970" height="382" class="imgBorder"/></p>

<div class="imageCaption">Página com lista de contactos</div>

<p>&nbsp;</p>
</ss:secure>

<ss:secure roles="peimanager,clientpeimanager">
<a name="peiCM"></a>

<h2><span>Gestão Plano de Emergência</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Ao seleccionar a opção do menu "Gestão" tem acesso a toda a Gestão de conteúdos do Plano de Emergência
    seleccionado. O Plano de Emergência segue uma estrutura hierárquica (estrutura em árvore) de pastas.
    Para cada pasta deve ser seleccionado o seu tipo de conteúdo (template), que serve de base à introdução
    do conteúdo da pasta.
</p>

<p>A tabela seguinte descreve algumas das operações mais comuns.</p>

<table class="displaytag helpImportFileTable">
    <thead>
    <tr>
        <th>Funcionalidade</th>
        <th>Explicação</th>
        <th>Notas</th>
    </tr>
    </thead>
    <tr>
        <td style="width: 20%;"><p>Publicar</p></td>
        <td>
            <p>Permite copiar da versão de trabalho (offline) para a versão publicada (online).</p>
        </td>
        <td>
            <ul>
                <li>O botão <em>Publicar</em> encontra-se disponível apenas quando a pasta principal do PEI se encontra
                    seleccionada ou a pasta correspondente a uma secção (isto é, a pasta do 1º nível e as pastas do 2º
                    nível da árvore).
                </li>
                <li>
                    Quando está seleccionada a pasta principal (capa do PEI), a publicação será total.
                    Quando se selecciona uma secção, a publicação abrange apenas a secção seleccionada.
                </li>
                <li>
                    O botão <em>Publicar</em> apenas está disponível após o preenchimento dos dados gerais do PEI
                    (capa).
                </li>
                <li>
                    A primeira publicação de um PEI tem de ser total.
                </li>
            </ul>
        </td>
    </tr>
    <tr>
        <td><p>Exportar Publicado</p></td>
        <td>
            <p>
                Esta funcionalidade exporta sempre a totalidade de um Plano de Emergência para os formatos suportados.
            </p>

            <p>
                Após clicar no botão respectivo será enviado um ficheiro comprimido em formato ZIP, contendo a
                exportação nos formatos suportados e documentos anexos.
            </p>
        </td>
        <td>
            <ul>
                <li>Esta operação poderá demorar alguns segundos consoante o tamanho do Plano de Emergência.</li>
                <li>O botão <em>Exportar Publicado</em> apenas está disponível quando o PEI já foi publicado.</li>
            </ul>
        </td>
    </tr>
    <tr>
        <td><p>Exportar Não Publicado</p></td>
        <td>
            <p>Semelhante ao <em>Exportar Publicado</em> mas abrange a versão de trabalho (offline).</p>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td><p>Pré-visualizar</p></td>
        <td>
            <p>
                Navega para o <em>frontoffice</em> da pasta seleccionada, activando o modo de pré-visualização, que
                permite
                consultar a versão de trabalho (offline) do Plano
                de Emergência no <em>frontoffice</em>, tal como os utilizadores comuns o vão consultar.
            </p>
        </td>
        <td>
            <ul>
                <li>
                    Caso a pasta seleccionada tenha um tipo de conteúdo sem visualização (Contacto, Documento ou
                    Pergunta
                    Frequente), aparecerá um aviso.
                </li>
            </ul>
        </td>
    </tr>
    <tr>
        <td><p>Visualizar</p></td>
        <td>
            <p>
                Navega para a versão publicada da pasta seleccionada.
            </p>
        </td>
        <td>
            <ul>
                <li>
                    Caso o nome da pasta seja diferente na versão de trabalho e na versão publicada este botão poderá
                    apontar para uma pasta não existente.
                </li>
            </ul>
        </td>
    </tr>
    <tr>
        <td><p>Guardar</p></td>
        <td>
            <p>
                Guarda os dados preenchidos no formulário (os dados constantes das várias <em>tabs</em>).
            </p>
        </td>
        <td>&nbsp;</td>
    </tr>
</table>

<p>&nbsp;</p>

<p>
    Em seguida apresentam-se exemplos da página de Gestão, consoante a pasta seleccionada
    corresponde à capa do PEI ou a outra pasta.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-gestao.png"
                            alt="Gestão Plano de Emergência" width="970" height="493" class="imgBorder"/></p>

<div class="imageCaption">Gestão Plano de Emergência - Capa do PEI seleccionada</div>
<p>&nbsp;</p>

<p>&nbsp;</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-gestao-pasta.png"
                            alt="Gestão Plano de Emergência" width="970" height="488" class="imgBorder"/></p>

<div class="imageCaption">Gestão Plano de Emergência - Pasta seleccionada</div>
<p>Notas:</p>
<ul>
    <li>Caso esteja seleccionada uma secção (pasta de 2º nível) a opção de eliminar não estará disponível.</li>
    <li>Caso esteja seleccionada uma secção (pasta de 2º nível), uma nova caixa de texto encontra-se disponível:
        <em>Ajuda</em>. Este campo permite a introdução de um texto de ajuda para a secção seleccionada.
    </li>
    <li>
        Ao editar uma pasta existente, se alterar o tipo de conteúdo, os conteúdos anteriores serão
        removidos quando efectuar a gravação.
    </li>
</ul>
<p>&nbsp;</p>

<p>&nbsp;</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-gestao-texto-adicional.png"
                            alt="Gestão Plano de Emergência" width="960" height="806" class="imgBorder"/></p>

<div class="imageCaption">Gestão Plano de Emergência - Tab "Texto Adicional"</div>


<p>&nbsp;</p>

<p>&nbsp;</p>

<h3>Permissões</h3>

<p>
    O processo de gestão de acessos a pastas de um Plano de Emergência deve
    iniciar-se pela criação de permissões para o contrato. Para tal, está disponível a opção
    "Permissões" no módulo PEI.
</p>

<p>
    Após a criação de permissões é possível limitar o acesso a uma pasta apenas aos utilizadores que tenham essa
    permissão. Por omissão todas as pastas de um Plano de Emergência são visíveis a todos os utilizadores que tenham
    autorização de consulta desse PEI.
</p>

<p>
    Na zona de Gestão do PEI, deve-se seleccionar a pasta a limitar e seleccionar as permissões necessárias para lhe
    aceder. As permissões são aplicadas de forma hierárquica, isto é, ao limitar-se uma pasta limita-se também o acesso
    às suas filhas (pastas de nível inferior).
</p>

<p>
    Caso sejam seleccionadas várias permissões, o utilizador necessita de ter todas as permissões seleccionadas para
    poder aceder à pasta.
</p>

<p>
    As permissões não se aplicam a utilizadores com o perfil de acesso Gestor do PEI. Estes utilizadores podem sempre
    consultar todas as pastas dos PEIs a que têm acesso de gestão.
</p>

<p>
    Após a definição das permissões necessárias para aceder às pastas, deve-se proceder à atribuição de permissões
    aos utilizadores na Gestão de Utilizadores.
</p>

<p>&nbsp;</p>

<p>&nbsp;</p>

<h3>Tipos de conteúdo (templates)</h3>

<p>
    A gestão do Plano de Emergência funciona como um gestor de conteúdos, cada pasta tem um tipo
    de conteúdo (template) diferente. Alguns dos templates são considerados auxiliares, não sendo visíveis directamente
    no <em>frontoffice</em>, mas sendo indexados noutras pastas.
    <br/><br/>Em seguida descrevem-se os vários templates disponíveis.
</p>

<h4>Índice navegável</h4>

<p>
    Este é o template definido por omissão para todas as pastas. Ao consultar páginas com este tipo de conteúdo
    é apresentada uma listagem (com link) das páginas em níveis inferiores (suas filhas).
</p>

<p>
    Este template não necessita de inserção de dados na <em>tab</em> "Conteúdos".
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-indice.png"
                            alt="" width="976" height="420" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emergência - Detalhe do <em>frontoffice</em> do Índice</div>

<p>&nbsp;</p>

<h4>Imagem/Anexo</h4>

<p>
    Este tipo de pastas permite guardar imagens ou ficheiros anexos utilizados em outras pastas, especialmente
    em pastas do tipo "Texto Formatável".
</p>

<p>
    Ao enviar o ficheiro é detectado automaticamente se o ficheiro é uma imagem ou um anexo. Recomenda-se que o nome
    da pasta corresponda ao nome do ficheiro enviado, incluindo a extensão, ex: "imagem.jpg".
</p>

<p>&nbsp;</p>


<h4>Análise de Riscos</h4>

<p>
    Permite o envio de um ficheiro CSV contendo a tabela de Análise de Riscos.
    Cada linha do ficheiro deve ter, pelo menos, 7 colunas:
</p>
<ol>
    <li>produto;</li>
    <li>condições de libertação;</li>
    <li>meteorologia;</li>
    <li>inflamação;</li>
    <li>radiação;</li>
    <li>sobrepressão;</li>
    <li>toxicidade.</li>
</ol>
<p>
    Para além dessas colunas é possível adicionar ficheiros anexos. Para tal devem ser criadas pastas do tipo
    "Imagem/Anexo" no nível inferior da pasta com a Análise de Riscos (suas filhas). No ficheiro CSV, após as colunas
    obrigatórias, devem-se adicionar colunas com o nome da pasta contendo o anexo. Desta forma é possível adicionar
    vários
    ficheiros anexos, por linha na tabela de Análise de Riscos. Por exemplo:
</p>
<ul>
    <li>Análise de riscos
        <ul>
            <li>Cenario_1.jpg</li>
            <li>Planta.dwg</li>
        </ul>
    </li>
</ul>
<p>
    Neste caso, se uma linha do CSV tiver as 7 colunas obrigatórias mais 2 colunas com o texto "Cenario_1.jpg"
    e "Planta.dwg", a linha vai ficar com os 2 anexos.
    Ao consultar a tabela da Análise de Riscos o utilizador pode efectuar o download dos anexos.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-analise-riscos.png"
                            alt="" width="970" height="438" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emergência - Detalhe do <em>frontoffice</em> da Análise de Riscos</div>

<p>&nbsp;</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-analise-riscos-csv.png"
                            alt="" width="970" height="112" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emergência - Exemplo de um ficheiro CSV com anexos</div>

<p>&nbsp;</p>
<h4>Diagrama com áreas clicáveis</h4>

<p>
    Este template permite criar uma página com uma imagem contendo informação adicional em algumas áreas da imagem.
    Consulte <a href="#peiOrganograma">aqui</a> a descrição detalhada do seu <em>frontoffice</em>.
</p>

<p>
    Na <em>tab</em> "Conteúdos" deve-se indicar o ficheiro com a imagem e guardar a pasta. Após guardar a pasta,
    é apresentado um formulário permitindo a escrita de texto formatável bem como a definição das áreas clicáveis da
    imagem. Para definir as áreas, seleccione a imagem e clique no 1º icon disponível no formulário ou pressione o
    botão direito sobre a imagem e seleccione a última opção "Editor de áreas interactivas".
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-diagrama.png"
                            alt="" width="964" height="705" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emergência - Diagrama com áreas clicáveis</div>

<p>&nbsp;</p>

<p>
    No editor de áreas clicáveis pode definir as áreas clicáveis, bem como a sua legenda.
    <br/>
    De realçar que a primeira linha da legenda é sempre o título da área.
    O título não é visível no <em>frontoffice</em>, sendo usado apenas na exportação do PEI.
    <br/>O título deve estar sempre presente.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-diagrama-editor-areas.png"
                            alt="" width="970" height="549" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emergência - Editor de áreas clicáveis</div>
<p>&nbsp;</p>

<h4>Diagrama de navegação por imagens</h4>

<p>
    Este template é semelhante ao "Diagrama com áreas clicáveis", sendo a diferença o comportamento das áreas definidas
    para a imagem. Neste template essas áreas são links o que permite a navegação para páginas internas do PEI ou
    websites externos, bem como o descarregamento de ficheiros anexos.
</p>

<p>
    O editor de áreas apresenta algumas diferenças:
</p>

<p class="alignCenter"><img
        src="${pageContext.request.contextPath}/images/help/pei-templates-diagrama-navegacao-imagens.png"
        alt="" width="931" height="484" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emergência - Editor de áreas clicáveis no diagrama de navegação por imagens</div>
<p>
    Ao clicar no botão "Procurar" é apresentada uma listagem contendo as pastas filhas e irmãs (no mesmo nível)
    da pasta actual e que têm como template "Imagem/Anexo" ou "Diagrama de navegação por imagens".
    Ao clicar em algum elemento dessa lista, o URL/endereço da área é automaticamente
    preenchido.
</p>

<p>
    Nota: Caso se altere o nome de alguma das pastas referenciadas é previsível que os links das áreas, para
    essas pastas, deixem de funcionar, necessitando o link de ser actualizado com o novo nome das pastas.
</p>

<p>&nbsp;</p>

<h4>Texto Formatável</h4>

<p>
    Este tipo de conteúdo permite a introdução e formatação de texto com imagens e links. A sua interface é semelhante
    aos programas de edição de texto (Microsoft Word) e permite uma grande liberdade na formatação de estilos do texto
    introduzido.
</p>

<p>
    Este template permite a inserção de imagens em websites externos ou o uso de imagens inseridas através do template
    "Imagem/Anexo". Neste último caso, deve-se clicar no botão de inserção de imagem e clicar em "Procurar".
    Será mostrada uma listagem com as pastas do tipo "Imagem/Anexo" que se encontram ao mesmo nível da pasta com o
    texto formatável ou em níveis inferiores (pastas irmãs ou filhas).
    Ao seleccionar a imagem pretendida da listagem, o campo URL (endereço) é preenchido automaticamente. Ao clicar
    em "OK", a imagem será incluída no texto. De realçar que apenas as pastas "Imagem/Anexo" que contêm uma imagem
    (e não um anexo como um ficheiro zip, pdf, etc) serão mostradas na listagem.
</p>

<p>
    O funcionamento para inserir uma hiperligação (link) é semelhante mas a listagem apresentada inclui todas as pastas
    do tipo "Imagem/Anexo" quer sejam realmente imagens ou anexos.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-texto-formatavel.png"
                            alt="" width="974" height="601" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emergência - Texto Formatável</div>

<p>
    Notas:
</p>

<ul>
    <li>
        Recomenda-se a leitura do manual de utilizador em que este componente se baseia, disponível <a
            href="http://docs.fckeditor.net/FCKeditor_2.x/Users_Guide" target="_blank">aqui</a>.
    </li>
    <li>O formulário permite efectuar operações de copiar/colar directamente do Word. No entanto recomenda-se que se use
        o botão "Colar como texto não formatado" para retirar a formatação proveniente do Word.
    </li>
</ul>
<p>&nbsp;</p>


<h4>Texto formatável com anexos</h4>

<p>
    Este template é semelhante ao texto formatável mas apresenta automaticamente uma lista das pastas do tipo
    "Imagem/Anexo" que se encontram abaixo da pasta (suas filhas).
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-texto-com-anexos.png"
                            alt="" width="970" height="291" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emergência - <em>Frontoffice</em> de Texto Formatável com anexos</div>

<p>&nbsp;</p>


<h4>Índice de Contactos</h4>

<p>
    Este template permite indexar as pastas do tipo "Contacto" que se encontram em níveis abaixo da pasta,
    apresentando-os
    em tabelas, com possibilidade de pesquisa e filtragem.
</p>

<p>
    Este template não necessita de inserção de dados na <em>tab</em> "Conteúdos". O conteúdo desta pasta
    vem do conteúdo das suas filhas do tipo "Contacto".
</p>

<p>
    Nota: Esta pasta indexa os conteúdos de forma recursiva pelo que é possível ter vários níveis de pastas
    do tipo "Contacto", abaixo do "Índice de Contactos".
</p>

<h4>Contacto</h4>

<p>
    Representa um contacto que é indexado no "Índice de Contactos". Este é um template auxiliar, não sendo
    visível directamente no <em>frontoffice</em>, apenas é usado pelo "Índice de Contactos". Um contacto
    corresponde a um conjunto de informação de comunicação habitual: telefone, email, nome, etc.
</p>

<p>
    Um exemplo de uma estrutura completa de índice e contactos é:
</p>
<ul>
    <li>Índice de Contactos
        <ul>
            <li>Contacto 1</li>
            <li>Contacto 2
                <ul>
                    <li>Contacto 3</li>
                    <li>Contacto 4</li>
                </ul>
            </li>
        </ul>
    </li>
</ul>

<p>&nbsp;</p>

<h4>Índice de documentos</h4>

<p>
    Este template permite indexar as pastas do tipo "Documento" que se encontram em níveis abaixo do Índice de
    documentos. A consulta
    desta pasta apresenta uma tabela com os dados dos documentos indexados, permitindo ainda filtrar os dados a
    apresentar
    na tabela.
</p>

<p>
    Este template não necessita de inserção de dados na <em>tab</em> "Conteúdos". O conteúdo desta pasta
    vem do conteúdo das suas filhas do tipo "Documento".
</p>

<h4>Documento</h4>

<p>
    Representa um documento permitindo anexar zero ou mais ficheiros por documento.
</p>

<p>
    As pastas deste tipo são indexadas pelo Índice de documentos, sendo um template auxiliar, não visível
    directamente no <em>frontoffice</em> (não aparece no menu de navegação).
</p>


<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-documento.png"
                            alt="" width="967" height="351" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emergência - Template Documento</div>

<p>&nbsp;</p>

<h4>Índice Perguntas Frequentes</h4>

<p>
    Este template permite indexar as pastas do tipo "Pergunta Frequente", mostrando uma listagem com todas as perguntas
    frequentes que se encontram em níveis abaixo desta.
</p>

<p>
    Este template não necessita de inserção de dados na <em>tab</em> "Conteúdos". O conteúdo desta pasta
    vem do conteúdo das suas filhas do tipo "Pergunta Frequente".
</p>

<p>
    É ainda possível criar "categorias" de perguntas frequentes: sempre que uma pasta deste tipo tenha uma filha que
    seja
    do tipo "Índice Perguntas Frequentes" ela é considerada uma subcategoria. Esta funcionalidade permite uma melhor
    organização das perguntas frequentes.
</p>

<p>
    Exemplo da organização de perguntas frequentes com categorias:
</p>
<ul class="helpList">
    <li>FAQ (pasta do tipo índice de perguntas frequentes)
        <ul class="helpList">
            <li>Sismos (pasta do tipo índice de perguntas frequentes)
                <ul class="helpList">
                    <li>O que fazer em caso de sismo? (pasta do tipo pergunta frequente)</li>
                    <li>O que fazer após o sismo? (pasta do tipo pergunta frequente)</li>
                </ul>
            </li>
            <li>Incêndios (pasta do tipo índice de perguntas frequentes)
                <ul class="helpList">
                    <li>O que fazer se ocorrer um incêndio? (pasta do tipo pergunta frequente)</li>
                    <li>Como utilizar um extintor? (pasta do tipo pergunta frequente)</li>
                </ul>
            </li>
        </ul>
    </li>
</ul>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-faq.png"
                            alt="" width="939" height="227" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emergência - Exemplo do <em>frontoffice</em> de um Índice de Perguntas Frequentes,
    organizado por categorias
</div>
<p>&nbsp;</p>


<h4>Pergunta Frequente</h4>

<p>
    É o elemento usado no "Índice Perguntas Frequentes", é constituído por uma pergunta e uma resposta, em texto
    formatável.
</p>

<p>
    As pastas deste tipo são indexadas pelo Índice perguntas frequentes, sendo um template auxiliar, não visível
    directamente no <em>frontoffice</em> (não aparece no menu de navegação).
</p>

<p>&nbsp;</p>
<h4>Índice Procedimentos</h4>

<p>
    Este tipo de pastas indexa os seus filhos do tipo "Texto Formatável", permitindo a navegação para essas páginas
    através de filtros dinâmicos até ao máximo de 3 níveis.
</p>

<p>
    As pastas abaixo desta não ficam visíveis no menu de navegação, apenas é possível aceder-lhes usando os filtros
    apresentados.
    Quando uma das pastas indexadas não possui filhos, é apresentado o seu conteúdo (texto formatável). Caso a pasta
    tenha filhos, eles são carregados na caixa de selecção (filtro) seguinte.
</p>

<p>
    Exemplo da organização correcta para o índice de procedimentos:
</p>
<ul class="helpList">
    <li>Procedimentos (pasta do tipo índice de procedimentos)
        <ul class="helpList">
            <li>Instrução Geral de Actuação (pasta do tipo texto formatável - o seu conteúdo não é apresentado, serve
                apenas como categoria de filtragem)

                <ul class="helpList">
                    <li>Incêndio (pasta do tipo texto formatável)</li>
                    <li>Sismo (pasta do tipo texto formatável - o seu conteúdo não é apresentado, serve apenas como
                        categoria
                        de filtragem)

                        <ul class="helpList">
                            <li>Vigilante (pasta do tipo texto formatável)</li>
                            <li>Central de Segurança (pasta do tipo texto formatável)</li>
                            <li>Equipa de Manutenção (pasta do tipo texto formatável)</li>
                        </ul>
                    </li>
                </ul>
            </li>
        </ul>
    </li>
</ul>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-procedimentos.png"
                            alt="" width="967" height="84" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emergência - Exemplo do <em>frontoffice</em> de um Índice de Procedimentos</div>
<p>

</p>

<p>&nbsp;</p>
</ss:secure>

<ss:secure roles="peimanager">
<a name="peiCopy"></a>

<h2><span>Cópia Plano de Emergência</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Após clicar na opção do menu "Cópia", é apresentada uma página que lhe permite copiar um Plano de
    Emergência para outro contrato.
</p>

<p>
    A cópia inclui todas as pastas e conteúdos do Plano de Emergência de origem (versão de trabalho e
    versão publicada), bem como as suas permissões.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-copia.png"
                            alt="Cópia Plano de Emergência" width="970" height="377" class="imgBorder"/></p>

<div class="imageCaption">Cópia Plano de Emergência</div>

<p>&nbsp;</p>
</ss:secure>

<ss:secure roles="peimanager,clientpeimanager">
<a name="peiPermissions"></a>

<h2><span>Gestão de Permissões</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Ao clicar na opção do menu "Permissões" é apresentada uma página que lhe permite adicionar novas permissões
    a um contrato e visualizar todas as permissões desse contrato.
</p>

<p>
    Ao clicar numa permissão, é aberta a árvore de todas as pastas de um PEI de forma a consultar facilmente quais
    as pastas que esta permissão permite aceder.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-permissoes.png"
                            alt="Gestão de Permissões" width="970" height="392" class="imgBorder"/></p>

<div class="imageCaption">Gestão de Permissões</div>

<p>&nbsp;</p>

<p>Detalhe de uma permissão com a árvore de pastas aberta</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-permissoes-detalhe.png"
                            alt="Gestão de Permissões" width="970" height="558" class="imgBorder"/></p>

<div class="imageCaption">Gestão de Permissões</div>
<p>&nbsp;</p>

<p>Nota:</p>
<ul>
    <li>Apenas é possível apagar uma permissão que não esteja associada a utilizadores ou em uso em alguma pasta
        do Plano de Emergência.
    </li>
</ul>

<p>&nbsp;</p>

<h3>Exemplos:</h3>

<p>Pretende-se permitir o acesso ao PEI a todos os utilizadores excepto um utilizador que não pode consultar a
    secção Organograma.</p>

<p>
    Neste caso, deve-se criar a permissão para o PEI: "Utilizador Organograma" (na Gestão de Permissões).
    Na Gestão do PEI, seleccionar a pasta Organograma, seleccionar a permissão "Utilizador Organograma" e guardar as
    alterações.
    Desta forma só os utilizadores com esta permissão podem aceder à secção Organograma.
    Em seguida, na Gestão de Utilizadores deve-se atribuir esta permissão a todos os utilizadores, excepto ao
    utilizador que se pretende restringir.
</p>

<p>&nbsp;</p>

<p>
    Num PEI contendo a secção Organograma e, dentro dessa, duas pastas chamadas <em>Descritivo</em> e <em>Descritivo
    Restrito</em>,
    pretende-se limitar o acesso à secção Organograma a um grupo de utilizadores e limitar o acesso à pasta <em>Descritivo
    Restrito</em> a apenas um utilizador.
</p>

<p>
    Para esta situação devem ser criadas duas permissões: "Utilizador Organograma" e "Utilizador Organograma Acesso
    Total".
    Na secção Organograma deve-se limitar o seu acesso com a permissão "Utilizador Organograma". Depois desta
    operação, seleccionar a pasta
    <em>Descritivo Restrito</em>, seleccionar a permissão "Utilizador Organograma Acesso Total" e guardar as
    alterações.
    Atribuir, aos utilizadores que podem aceder ao Organograma, a permissão "Utilizador Organograma" (Gestão de
    Utilizadores). Ao único utilizador
    que pode aceder à pasta <em>Descritivo Restrito</em> (e seus filhos) deve-se ser atribuido a permissão
    "Utilizador Organograma" e "Utilizador Organograma Acesso Total".
</p>

<p>&nbsp;</p>
</ss:secure>

</div>
</ss:secure>