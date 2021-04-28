<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Gestão do Plano</title>
</head>

<ss:secure roles="peimanager,clientpeimanager">
<h2><span>Gestão do Plano</span></h2>

<p>
    Ao seleccionar a opção do menu "Gestão" tem acesso a toda a Gestão de conteúdos do Plano
    seleccionado. O Plano segue uma estrutura hierárquica (estrutura em árvore) de pastas.
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
                <li>O botão <em>Publicar</em> encontra-se disponível apenas quando a pasta principal do Plano se
                    encontra
                    seleccionada ou a pasta correspondente a uma secção (isto é, a pasta do 1º nível e as pastas do 2º
                    nível da árvore).
                </li>
                <li>
                    Quando está seleccionada a pasta principal (capa do Plano), a publicação será total.
                    Quando se selecciona uma secção, a publicação abrange apenas a secção seleccionada.
                </li>
                <li>
                    O botão <em>Publicar</em> apenas está disponível após o preenchimento dos dados gerais do Plano
                    (capa).
                </li>
                <li>
                    A primeira publicação de um Plano tem de ser total.
                </li>
            </ul>
        </td>
    </tr>
    <tr>
        <td><p>Exportar Publicado</p></td>
        <td>
            <p>
                Esta funcionalidade exporta sempre a totalidade de um Plano para os formatos suportados.
            </p>

            <p>
                Após clicar no botão respectivo será enviado um ficheiro comprimido em formato ZIP, contendo a
                exportação nos formatos suportados e documentos anexos.
            </p>
        </td>
        <td>
            <ul>
                <li>Esta operação poderá demorar alguns segundos consoante o tamanho do Plano.</li>
                <li>O botão <em>Exportar Publicado</em> apenas está disponível quando o Plano já foi publicado.</li>
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
                consultar a versão de trabalho (offline) do Plano no <em>frontoffice</em>, tal como os utilizadores
                comuns o
                vão consultar.
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
    corresponde à capa do Plano ou a outra pasta.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-gestao.png"
                            alt="Gestão do Plano" width="970" height="493" class="imgBorder"/></p>

<div class="imageCaption">Gestão do Plano - Capa seleccionada</div>
<p>&nbsp;</p>

<p>&nbsp;</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-gestao-pasta.png"
                            alt="Gestão do Plano" width="970" height="610" class="imgBorder"/></p>

<div class="imageCaption">Gestão do Plano - Pasta seleccionada</div>
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
    <li>
        A opção de incluir na navegação retira a pasta do menu e <em>breadcrumbs</em>, no entanto, caso haja um link
        para
        a pasta, ela continua acessível no <em>frontoffice</em>. Esta opção não tem influência nos filhos da pasta.
        Caso se pretenda remover, por completo, o acesso no <em>frontoffice</em> a uma pasta e suas filhas, deve-se usar
        a opção de <em>activo</em>.
    </li>
    <li>
        A opção incluir na navegação é automaticamente desactivada quando o tipo de conteúdo não permite a sua inclusão
        no menu de navegação, nomeadamente: Imagem/Anexo, Pergunta Frequente, Contacto e Documento.
    </li>
    <li>
        O nome de uma pasta nao pode conter os seguintes caracteres: / : [ ] * ' \ | % & #
    </li>
</ul>
<p>&nbsp;</p>

<p>&nbsp;</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-gestao-texto-adicional.png"
                            alt="Gestão do Plano" width="960" height="806" class="imgBorder"/></p>

<div class="imageCaption">Gestão do Plano - Tab "Texto Adicional"</div>


<p>&nbsp;</p>

<p>&nbsp;</p>

<h3>Permissões</h3>

<p>
    O processo de gestão de acessos a pastas de um Plano deve
    iniciar-se pela criação de permissões para o contrato. Para tal, está disponível a opção
    "Permissões" no módulo respectivo.
</p>

<p>
    Após a criação de permissões é possível limitar o acesso a uma pasta apenas aos utilizadores que tenham essa
    permissão. Por omissão todas as pastas de um Plano são visíveis a todos os utilizadores que tenham
    permissão. Por omissão todas as pastas de um Plano são visíveis a todos os utilizadores que tenham
    autorização de consulta desse Plano.
</p>

<p>
    Na zona de Gestão do Plano, deve-se seleccionar a pasta a limitar e seleccionar as permissões necessárias para lhe
    aceder. As permissões são aplicadas de forma hierárquica, isto é, ao limitar-se uma pasta limita-se também o acesso
    às suas filhas (pastas de nível inferior).
</p>

<p>
    Caso sejam seleccionadas várias permissões, o utilizador necessita de ter todas as permissões seleccionadas para
    poder aceder à pasta.
</p>

<p>
    As permissões não se aplicam a utilizadores com o perfil de acesso Gestor do Plano. Estes utilizadores podem sempre
    consultar todas as pastas dos Planos a que têm acesso de gestão.
</p>

<p>
    Após a definição das permissões necessárias para aceder às pastas, deve-se proceder à atribuição de permissões
    aos utilizadores na Gestão de Utilizadores.
</p>


<p>&nbsp;</p>

<p>&nbsp;</p>

<h3>Criação de links</h3>

<p>
    Na zona de Gestão do Plano é possível a criação de pastas-link. O conteúdo destas pastas está associado ao conteúdo
    de outras pastas (do mesmo ou de outro plano). Uma pasta do tipo link é também designada por pasta destino, enquanto
    que a pasta com o conteúdo que lhe deu origem intitula-se pasta origem.
</p>

<p>
    A pasta de destino apresenta um conteúdo exactamente igual ao da pasta de origem.
    Qualquer alteração no conteúdo da pasta de origem é reflectida em todas as suas pastas de origem.
    As propriedades e texto adicional da pasta de destino são independentes da pasta de origem.
</p>

<p>Após seleccionar a opção de adicionar um novo link, o seguinte painel é apresentado:</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-link-add.png" alt="Adicionar link"
                            width="728" height="480" class="imgBorder"/></p>

<div class="imageCaption">Adicionar link</div>

<p>Neste painel é possível visualizar todas as pastas de qualquer contrato de qualquer entidade. De realçar que a
    criação
    de um link é feita de forma recursiva, ou seja, ao seleccionar uma pasta, todas as pastas que são suas filhas são
    também
    criadas, como pastas do tipo link.
</p>

<p>
    O conteúdo da versão de trabalho de uma pasta destino está associado à versão de trabalho da pasta origem. De forma
    análoga, as versões publicadas encontram-se ligadas entre si. Este aspecto é relevante, dado ser necessário que a
    pasta origem esteja publicada para que a versão publicada da pasta destino seja visível, caso contrário teremos um
    aviso de pasta não encontrada.
    <br>
    Caso se altere o conteúdo de uma pasta origem e se publique a alteração é reflectida em todas as pastas destino que
    estejam publicadas.
</p>

<p>Notas:</p>
<ul>
    <li>As pastas apresentadas não incluem as pastas-link desse contrato, dado não ser possível criar um link para uma
        pasta
        link (deve-se sempre criar um link directamente à pasta de origem).
        De realçar que, caso uma pasta tenha alguma pasta filha que seja uma pasta-link, esse pasta não aparecerá também
        na listagem (dado que ao criar um link para essa página seriam criados links para todas as pastas filhas)).
    </li>
    <li>Não é possível eliminar pastas origem. É necessário primeiro remover todas as suas pastas destino e só depois é
        possível remover a pasta origem.
    </li>
    <li>O Gestor Conteúdos Planos de uma entidade cliente não pode criar links nem eliminar links (ou pastas que causem
        a eliminação de pastas do tipo link, suas filhas).
    </li>
</ul>

<p>
    As pastas do tipo link e pastas origem são assinaladas na listagem de pastas com setas indicativas do seu papel:
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-link-setas.png" alt="Pastas link"
                            width="447" height="402" class="imgBorder"/></p>
<div class="imageCaption">Exemplo de pastas link e pastas de origem</div>


<p>&nbsp;</p>

<p>
    Na pasta do tipo link, a <em>tab</em> de conteúdos apresenta um link para a pasta de origem, enquanto que na pasta
    de origem, na <em>tab</em> das propriedades, é apresentada a lista dos links para esta pasta.
</p>
<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-link-source.png" alt="Lista de links para as pastas destino"
                            width="727" height="574" class="imgBorder"/></p>
<div class="imageCaption">Lista de links para as pastas destino</div>
<p>&nbsp;</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-link-target.png" alt="Link para a pasta de origem"
                            width="722" height="159" class="imgBorder"/></p>
<div class="imageCaption">Link para a pasta de origem</div>



<p>&nbsp;</p>

<p>&nbsp;</p>

<h3>Tipos de conteúdo (templates)</h3>

<p>
    A gestão do Plano funciona como um gestor de conteúdos, cada pasta tem um tipo
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

<div class="imageCaption">Planos - Detalhe do <em>frontoffice</em> do Índice</div>

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

<div class="imageCaption">Planos - Detalhe do <em>frontoffice</em> da Análise de Riscos</div>

<p>&nbsp;</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-analise-riscos-csv.png"
                            alt="" width="970" height="112" class="imgBorder"/></p>

<div class="imageCaption">Planos - Exemplo de um ficheiro CSV com anexos</div>

<p>&nbsp;</p>
<h4>Diagrama com áreas clicáveis</h4>

<p>
    Este template permite criar uma página com uma imagem contendo informação adicional em algumas áreas da imagem.
    Consulte <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean">
    <s:param name="helpId" value="pei-details"/>aqui</s:link> a descrição detalhada do seu <em>frontoffice</em>.
</p>

<p>
    Na <em>tab</em> "Conteúdos" deve-se indicar o ficheiro com a imagem e guardar a pasta. Após guardar a pasta,
    é apresentado um formulário permitindo a escrita de texto formatável bem como a definição das áreas clicáveis da
    imagem. Para definir as áreas, seleccione a imagem e clique no 1º icon disponível no formulário ou pressione o
    botão direito sobre a imagem e seleccione a última opção "Editor de áreas interactivas".
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-diagrama.png"
                            alt="" width="964" height="705" class="imgBorder"/></p>

<div class="imageCaption">Planos - Diagrama com áreas clicáveis</div>

<p>&nbsp;</p>

<p>
    No editor de áreas clicáveis pode definir as áreas clicáveis, bem como a sua legenda.
    <br/>
    De realçar que a primeira linha da legenda é sempre o título da área.
    O título não é visível no <em>frontoffice</em>, sendo usado apenas na exportação do Plano.
    <br/>O título deve estar sempre presente.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-diagrama-editor-areas.png"
                            alt="" width="970" height="549" class="imgBorder"/></p>

<div class="imageCaption">Planos - Editor de áreas clicáveis</div>
<p>&nbsp;</p>

<h4>Diagrama de navegação por imagens</h4>

<p>
    Este template é semelhante ao "Diagrama com áreas clicáveis", sendo a diferença o comportamento das áreas definidas
    para a imagem. Neste template essas áreas são links o que permite a navegação para páginas internas do Plano ou
    websites externos, bem como o descarregamento de ficheiros anexos.
</p>

<p>
    O editor de áreas apresenta algumas diferenças:
</p>

<p class="alignCenter"><img
        src="${pageContext.request.contextPath}/images/help/pei-templates-diagrama-navegacao-imagens.png"
        alt="" width="931" height="484" class="imgBorder"/></p>

<div class="imageCaption">Planos - Editor de áreas clicáveis no diagrama de navegação por imagens</div>
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

<div class="imageCaption">Planos - Texto Formatável</div>

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

<div class="imageCaption">Planos - <em>Frontoffice</em> de Texto Formatável com anexos</div>

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
    Representa um documento permitindo anexar zero ou mais ficheiros e/ou links por documento.
</p>

<p>
    As pastas deste tipo são indexadas pelo Índice de documentos, sendo um template auxiliar, não visível
    directamente no <em>frontoffice</em> (não aparece no menu de navegação).
</p>


<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-documento.png"
                            alt="" width="967" height="540" class="imgBorder"/></p>

<div class="imageCaption">Planos - Template Documento</div>

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

<div class="imageCaption">Planos - Exemplo do <em>frontoffice</em> de um Índice de Perguntas Frequentes,
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

<div class="imageCaption">Planos - Exemplo do <em>frontoffice</em> de um Índice de Procedimentos</div>
<p>

</p>

<p>&nbsp;</p>
</ss:secure>