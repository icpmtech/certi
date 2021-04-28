<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="help.title"/></title>

<a name="top"></a>

<h1><fmt:message key="help.title"/></h1>

<ss:secure roles="peiAccess">
<ul>
    <li class="helpTOC">
        <a href="#pei">Plano de Emerg�ncia (PEI)</a>
        <ul>
            <ss:secure roles="user">
                <li><a href="#peiMain">Selec��o Plano de Emerg�ncia</a></li>
                <li><a href="#peiView">Capa Plano de Emerg�ncia</a></li>
                <li><a href="#peiViewResource">Detalhes Plano de Emerg�ncia</a></li>
            </ss:secure>
            <ss:secure roles="peimanager,clientpeimanager">
                <li><a href="#peiCM">Gest�o Plano de Emerg�ncia</a></li>
            </ss:secure>
            <ss:secure roles="peimanager">
                <li><a href="#peiCopy">C�pia Plano de Emerg�ncia</a></li>
            </ss:secure>
            <ss:secure roles="peimanager,clientpeimanager">
                <li><a href="#peiPermissions">Gest�o de Permiss�es</a></li>
            </ss:secure>
            <ss:secure roles="peimanager,clientpeimanager">
                <li><a href="#peiExportdocx">Exporta��o Docx</a></li>
            </ss:secure>
        </ul>
    </li>

</ul>

<div class="justify helpText">

<!--========================================================PEI======================================================!-->
<p>&nbsp;</p>

<hr/>

<a name="plan"></a>

<div class="helpModule">Plano de Emerg�ncia (PEI)</div>

<ss:secure roles="user">
<a name="peiMain"></a>

<h2 style="margin-top: 10px;"><span>Selec��o Plano de Emerg�ncia</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Ap�s clicar na op��o do menu "Plano de Emerg�ncia" � devolvida uma p�gina para selec��o do Plano de Emerg�ncia
    que
    pretende
    consultar. Caso o utilizador tenha acesso apenas a um Plano de Emerg�ncia ele ser� seleccionado
    automaticamente.

</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-selecao.png"
                            alt="Escolher Plano de Emerg�ncia" width="970" height="386" class="imgBorder"/></p>

<div class="imageCaption">Escolher Plano de Emerg�ncia</div>

<p>&nbsp;</p>

<a name="peiView"></a>

<h2><span>Capa Plano de Emerg�ncia</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Ap�s seleccionar um Plano de Emerg�ncia � apresentada a p�gina com a respectiva capa onde pode consultar dados
    gen�ricos do PEI, bem como ter acesso ao seu menu de navega��o.
</p>

<p>
    No menu de navega��o, a presen�a de pequenas "setas" indica que o item tem um submenu. Passe o rato sobre o
    item para abrir o respectivo submenu.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-capa.png"
                            alt="Capa Plano de Emerg�ncia" width="966" height="776" class="imgBorder"/></p>

<div class="imageCaption">Capa Plano de Emerg�ncia</div>

<p>&nbsp;</p>

<a name="peiViewResource"></a>

<h2><span>Detalhes Plano de Emerg�ncia</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Ap�s seleccionar uma p�gina no menu de navega��o do Plano de Emerg�ncia � apresentada uma p�gina com
    conte�dos.
    O tipo de conte�dos apresentado varia consoante o tipo da p�gina consultada. Alguns conte�dos habituais s�o:
    texto format�vel com imagens, lista de contactos, diagramas, plantas, organogramas, tabelas de an�lise de
    riscos,
    perguntas frequentes e tabelas de documentos e registos.
</p>

<p>
    Em seguida apresentamos a explica��o de alguns conte�dos habituais de encontrar no Plano de Emerg�ncia.
</p>

<h3>Tabela de An�lise de Riscos</h3>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-analise-riscos.png"
                            alt="P�gina com tabela de An�lise de Riscos" width="970" height="636"
                            class="imgBorder"/>
</p>

<div class="imageCaption">P�gina com tabela de An�lise de Riscos</div>

<p>&nbsp;</p>

<p>Quando a sec��o actual tem definido um texto de ajuda, � poss�vel consult�-lo, clicando
    no �cone correspondente.</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-ajuda-secao.png"
                            alt="Detalhe da ajuda da sec��o" width="969" height="218" class="imgBorder"/></p>

<div class="imageCaption">Detalhe da ajuda da sec��o</div>
<p>&nbsp;</p>

<a name="peiOrganograma"></a>

<h3>Organograma</h3>

<p>
    Este tipo de conte�do apresenta uma imagem din�mica, com informa��o adicional sobre algumas �reas da imagem. Ao
    passar o rato pela imagem as �reas com informa��o adicional aparecem real�adas. Ao clicar nessas �reas �
    apresentada
    a informa��o adicional. Para al�m da informa��o textual, algumas �reas podem permitir a navega��o para outras
    p�ginas
    ou o descarregamento de ficheiros anexos.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-diagrama.png"
                            alt="Organograma" width="968" height="613" class="imgBorder"/></p>

<div class="imageCaption">P�gina com Organograma</div>

<p>&nbsp;</p>

<h3>Lista de Contactos</h3>

<p>
    Este tipo de conte�do apresenta uma listagem de contactos, divididos consoante o seu tipo.
    Permite ainda filtrar pelo tipo de contacto e/ou efectuar uma pesquisa de texto livre pelo contacto
    pretendido.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-contactos.png"
                            alt="Lista de contactos" width="970" height="382" class="imgBorder"/></p>

<div class="imageCaption">P�gina com lista de contactos</div>

<p>&nbsp;</p>
</ss:secure>

<ss:secure roles="peimanager,clientpeimanager">
<a name="peiCM"></a>

<h2><span>Gest�o Plano de Emerg�ncia</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Ao seleccionar a op��o do menu "Gest�o" tem acesso a toda a Gest�o de conte�dos do Plano de Emerg�ncia
    seleccionado. O Plano de Emerg�ncia segue uma estrutura hier�rquica (estrutura em �rvore) de pastas.
    Para cada pasta deve ser seleccionado o seu tipo de conte�do (template), que serve de base � introdu��o
    do conte�do da pasta.
</p>

<p>A tabela seguinte descreve algumas das opera��es mais comuns.</p>

<table class="displaytag helpImportFileTable">
    <thead>
    <tr>
        <th>Funcionalidade</th>
        <th>Explica��o</th>
        <th>Notas</th>
    </tr>
    </thead>
    <tr>
        <td style="width: 20%;"><p>Publicar</p></td>
        <td>
            <p>Permite copiar da vers�o de trabalho (offline) para a vers�o publicada (online).</p>
        </td>
        <td>
            <ul>
                <li>O bot�o <em>Publicar</em> encontra-se dispon�vel apenas quando a pasta principal do PEI se encontra
                    seleccionada ou a pasta correspondente a uma sec��o (isto �, a pasta do 1� n�vel e as pastas do 2�
                    n�vel da �rvore).
                </li>
                <li>
                    Quando est� seleccionada a pasta principal (capa do PEI), a publica��o ser� total.
                    Quando se selecciona uma sec��o, a publica��o abrange apenas a sec��o seleccionada.
                </li>
                <li>
                    O bot�o <em>Publicar</em> apenas est� dispon�vel ap�s o preenchimento dos dados gerais do PEI
                    (capa).
                </li>
                <li>
                    A primeira publica��o de um PEI tem de ser total.
                </li>
            </ul>
        </td>
    </tr>
    <tr>
        <td><p>Exportar Publicado</p></td>
        <td>
            <p>
                Esta funcionalidade exporta sempre a totalidade de um Plano de Emerg�ncia para os formatos suportados.
            </p>

            <p>
                Ap�s clicar no bot�o respectivo ser� enviado um ficheiro comprimido em formato ZIP, contendo a
                exporta��o nos formatos suportados e documentos anexos.
            </p>
        </td>
        <td>
            <ul>
                <li>Esta opera��o poder� demorar alguns segundos consoante o tamanho do Plano de Emerg�ncia.</li>
                <li>O bot�o <em>Exportar Publicado</em> apenas est� dispon�vel quando o PEI j� foi publicado.</li>
            </ul>
        </td>
    </tr>
    <tr>
        <td><p>Exportar N�o Publicado</p></td>
        <td>
            <p>Semelhante ao <em>Exportar Publicado</em> mas abrange a vers�o de trabalho (offline).</p>
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td><p>Pr�-visualizar</p></td>
        <td>
            <p>
                Navega para o <em>frontoffice</em> da pasta seleccionada, activando o modo de pr�-visualiza��o, que
                permite
                consultar a vers�o de trabalho (offline) do Plano
                de Emerg�ncia no <em>frontoffice</em>, tal como os utilizadores comuns o v�o consultar.
            </p>
        </td>
        <td>
            <ul>
                <li>
                    Caso a pasta seleccionada tenha um tipo de conte�do sem visualiza��o (Contacto, Documento ou
                    Pergunta
                    Frequente), aparecer� um aviso.
                </li>
            </ul>
        </td>
    </tr>
    <tr>
        <td><p>Visualizar</p></td>
        <td>
            <p>
                Navega para a vers�o publicada da pasta seleccionada.
            </p>
        </td>
        <td>
            <ul>
                <li>
                    Caso o nome da pasta seja diferente na vers�o de trabalho e na vers�o publicada este bot�o poder�
                    apontar para uma pasta n�o existente.
                </li>
            </ul>
        </td>
    </tr>
    <tr>
        <td><p>Guardar</p></td>
        <td>
            <p>
                Guarda os dados preenchidos no formul�rio (os dados constantes das v�rias <em>tabs</em>).
            </p>
        </td>
        <td>&nbsp;</td>
    </tr>
</table>

<p>&nbsp;</p>

<p>
    Em seguida apresentam-se exemplos da p�gina de Gest�o, consoante a pasta seleccionada
    corresponde � capa do PEI ou a outra pasta.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-gestao.png"
                            alt="Gest�o Plano de Emerg�ncia" width="970" height="493" class="imgBorder"/></p>

<div class="imageCaption">Gest�o Plano de Emerg�ncia - Capa do PEI seleccionada</div>
<p>&nbsp;</p>

<p>&nbsp;</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-gestao-pasta.png"
                            alt="Gest�o Plano de Emerg�ncia" width="970" height="488" class="imgBorder"/></p>

<div class="imageCaption">Gest�o Plano de Emerg�ncia - Pasta seleccionada</div>
<p>Notas:</p>
<ul>
    <li>Caso esteja seleccionada uma sec��o (pasta de 2� n�vel) a op��o de eliminar n�o estar� dispon�vel.</li>
    <li>Caso esteja seleccionada uma sec��o (pasta de 2� n�vel), uma nova caixa de texto encontra-se dispon�vel:
        <em>Ajuda</em>. Este campo permite a introdu��o de um texto de ajuda para a sec��o seleccionada.
    </li>
    <li>
        Ao editar uma pasta existente, se alterar o tipo de conte�do, os conte�dos anteriores ser�o
        removidos quando efectuar a grava��o.
    </li>
</ul>
<p>&nbsp;</p>

<p>&nbsp;</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-gestao-texto-adicional.png"
                            alt="Gest�o Plano de Emerg�ncia" width="960" height="806" class="imgBorder"/></p>

<div class="imageCaption">Gest�o Plano de Emerg�ncia - Tab "Texto Adicional"</div>


<p>&nbsp;</p>

<p>&nbsp;</p>

<h3>Permiss�es</h3>

<p>
    O processo de gest�o de acessos a pastas de um Plano de Emerg�ncia deve
    iniciar-se pela cria��o de permiss�es para o contrato. Para tal, est� dispon�vel a op��o
    "Permiss�es" no m�dulo PEI.
</p>

<p>
    Ap�s a cria��o de permiss�es � poss�vel limitar o acesso a uma pasta apenas aos utilizadores que tenham essa
    permiss�o. Por omiss�o todas as pastas de um Plano de Emerg�ncia s�o vis�veis a todos os utilizadores que tenham
    autoriza��o de consulta desse PEI.
</p>

<p>
    Na zona de Gest�o do PEI, deve-se seleccionar a pasta a limitar e seleccionar as permiss�es necess�rias para lhe
    aceder. As permiss�es s�o aplicadas de forma hier�rquica, isto �, ao limitar-se uma pasta limita-se tamb�m o acesso
    �s suas filhas (pastas de n�vel inferior).
</p>

<p>
    Caso sejam seleccionadas v�rias permiss�es, o utilizador necessita de ter todas as permiss�es seleccionadas para
    poder aceder � pasta.
</p>

<p>
    As permiss�es n�o se aplicam a utilizadores com o perfil de acesso Gestor do PEI. Estes utilizadores podem sempre
    consultar todas as pastas dos PEIs a que t�m acesso de gest�o.
</p>

<p>
    Ap�s a defini��o das permiss�es necess�rias para aceder �s pastas, deve-se proceder � atribui��o de permiss�es
    aos utilizadores na Gest�o de Utilizadores.
</p>

<p>&nbsp;</p>

<p>&nbsp;</p>

<h3>Tipos de conte�do (templates)</h3>

<p>
    A gest�o do Plano de Emerg�ncia funciona como um gestor de conte�dos, cada pasta tem um tipo
    de conte�do (template) diferente. Alguns dos templates s�o considerados auxiliares, n�o sendo vis�veis directamente
    no <em>frontoffice</em>, mas sendo indexados noutras pastas.
    <br/><br/>Em seguida descrevem-se os v�rios templates dispon�veis.
</p>

<h4>�ndice naveg�vel</h4>

<p>
    Este � o template definido por omiss�o para todas as pastas. Ao consultar p�ginas com este tipo de conte�do
    � apresentada uma listagem (com link) das p�ginas em n�veis inferiores (suas filhas).
</p>

<p>
    Este template n�o necessita de inser��o de dados na <em>tab</em> "Conte�dos".
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-indice.png"
                            alt="" width="976" height="420" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emerg�ncia - Detalhe do <em>frontoffice</em> do �ndice</div>

<p>&nbsp;</p>

<h4>Imagem/Anexo</h4>

<p>
    Este tipo de pastas permite guardar imagens ou ficheiros anexos utilizados em outras pastas, especialmente
    em pastas do tipo "Texto Format�vel".
</p>

<p>
    Ao enviar o ficheiro � detectado automaticamente se o ficheiro � uma imagem ou um anexo. Recomenda-se que o nome
    da pasta corresponda ao nome do ficheiro enviado, incluindo a extens�o, ex: "imagem.jpg".
</p>

<p>&nbsp;</p>


<h4>An�lise de Riscos</h4>

<p>
    Permite o envio de um ficheiro CSV contendo a tabela de An�lise de Riscos.
    Cada linha do ficheiro deve ter, pelo menos, 7 colunas:
</p>
<ol>
    <li>produto;</li>
    <li>condi��es de liberta��o;</li>
    <li>meteorologia;</li>
    <li>inflama��o;</li>
    <li>radia��o;</li>
    <li>sobrepress�o;</li>
    <li>toxicidade.</li>
</ol>
<p>
    Para al�m dessas colunas � poss�vel adicionar ficheiros anexos. Para tal devem ser criadas pastas do tipo
    "Imagem/Anexo" no n�vel inferior da pasta com a An�lise de Riscos (suas filhas). No ficheiro CSV, ap�s as colunas
    obrigat�rias, devem-se adicionar colunas com o nome da pasta contendo o anexo. Desta forma � poss�vel adicionar
    v�rios
    ficheiros anexos, por linha na tabela de An�lise de Riscos. Por exemplo:
</p>
<ul>
    <li>An�lise de riscos
        <ul>
            <li>Cenario_1.jpg</li>
            <li>Planta.dwg</li>
        </ul>
    </li>
</ul>
<p>
    Neste caso, se uma linha do CSV tiver as 7 colunas obrigat�rias mais 2 colunas com o texto "Cenario_1.jpg"
    e "Planta.dwg", a linha vai ficar com os 2 anexos.
    Ao consultar a tabela da An�lise de Riscos o utilizador pode efectuar o download dos anexos.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-analise-riscos.png"
                            alt="" width="970" height="438" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emerg�ncia - Detalhe do <em>frontoffice</em> da An�lise de Riscos</div>

<p>&nbsp;</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-analise-riscos-csv.png"
                            alt="" width="970" height="112" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emerg�ncia - Exemplo de um ficheiro CSV com anexos</div>

<p>&nbsp;</p>
<h4>Diagrama com �reas clic�veis</h4>

<p>
    Este template permite criar uma p�gina com uma imagem contendo informa��o adicional em algumas �reas da imagem.
    Consulte <a href="#peiOrganograma">aqui</a> a descri��o detalhada do seu <em>frontoffice</em>.
</p>

<p>
    Na <em>tab</em> "Conte�dos" deve-se indicar o ficheiro com a imagem e guardar a pasta. Ap�s guardar a pasta,
    � apresentado um formul�rio permitindo a escrita de texto format�vel bem como a defini��o das �reas clic�veis da
    imagem. Para definir as �reas, seleccione a imagem e clique no 1� icon dispon�vel no formul�rio ou pressione o
    bot�o direito sobre a imagem e seleccione a �ltima op��o "Editor de �reas interactivas".
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-diagrama.png"
                            alt="" width="964" height="705" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emerg�ncia - Diagrama com �reas clic�veis</div>

<p>&nbsp;</p>

<p>
    No editor de �reas clic�veis pode definir as �reas clic�veis, bem como a sua legenda.
    <br/>
    De real�ar que a primeira linha da legenda � sempre o t�tulo da �rea.
    O t�tulo n�o � vis�vel no <em>frontoffice</em>, sendo usado apenas na exporta��o do PEI.
    <br/>O t�tulo deve estar sempre presente.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-diagrama-editor-areas.png"
                            alt="" width="970" height="549" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emerg�ncia - Editor de �reas clic�veis</div>
<p>&nbsp;</p>

<h4>Diagrama de navega��o por imagens</h4>

<p>
    Este template � semelhante ao "Diagrama com �reas clic�veis", sendo a diferen�a o comportamento das �reas definidas
    para a imagem. Neste template essas �reas s�o links o que permite a navega��o para p�ginas internas do PEI ou
    websites externos, bem como o descarregamento de ficheiros anexos.
</p>

<p>
    O editor de �reas apresenta algumas diferen�as:
</p>

<p class="alignCenter"><img
        src="${pageContext.request.contextPath}/images/help/pei-templates-diagrama-navegacao-imagens.png"
        alt="" width="931" height="484" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emerg�ncia - Editor de �reas clic�veis no diagrama de navega��o por imagens</div>
<p>
    Ao clicar no bot�o "Procurar" � apresentada uma listagem contendo as pastas filhas e irm�s (no mesmo n�vel)
    da pasta actual e que t�m como template "Imagem/Anexo" ou "Diagrama de navega��o por imagens".
    Ao clicar em algum elemento dessa lista, o URL/endere�o da �rea � automaticamente
    preenchido.
</p>

<p>
    Nota: Caso se altere o nome de alguma das pastas referenciadas � previs�vel que os links das �reas, para
    essas pastas, deixem de funcionar, necessitando o link de ser actualizado com o novo nome das pastas.
</p>

<p>&nbsp;</p>

<h4>Texto Format�vel</h4>

<p>
    Este tipo de conte�do permite a introdu��o e formata��o de texto com imagens e links. A sua interface � semelhante
    aos programas de edi��o de texto (Microsoft Word) e permite uma grande liberdade na formata��o de estilos do texto
    introduzido.
</p>

<p>
    Este template permite a inser��o de imagens em websites externos ou o uso de imagens inseridas atrav�s do template
    "Imagem/Anexo". Neste �ltimo caso, deve-se clicar no bot�o de inser��o de imagem e clicar em "Procurar".
    Ser� mostrada uma listagem com as pastas do tipo "Imagem/Anexo" que se encontram ao mesmo n�vel da pasta com o
    texto format�vel ou em n�veis inferiores (pastas irm�s ou filhas).
    Ao seleccionar a imagem pretendida da listagem, o campo URL (endere�o) � preenchido automaticamente. Ao clicar
    em "OK", a imagem ser� inclu�da no texto. De real�ar que apenas as pastas "Imagem/Anexo" que cont�m uma imagem
    (e n�o um anexo como um ficheiro zip, pdf, etc) ser�o mostradas na listagem.
</p>

<p>
    O funcionamento para inserir uma hiperliga��o (link) � semelhante mas a listagem apresentada inclui todas as pastas
    do tipo "Imagem/Anexo" quer sejam realmente imagens ou anexos.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-texto-formatavel.png"
                            alt="" width="974" height="601" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emerg�ncia - Texto Format�vel</div>

<p>
    Notas:
</p>

<ul>
    <li>
        Recomenda-se a leitura do manual de utilizador em que este componente se baseia, dispon�vel <a
            href="http://docs.fckeditor.net/FCKeditor_2.x/Users_Guide" target="_blank">aqui</a>.
    </li>
    <li>O formul�rio permite efectuar opera��es de copiar/colar directamente do Word. No entanto recomenda-se que se use
        o bot�o "Colar como texto n�o formatado" para retirar a formata��o proveniente do Word.
    </li>
</ul>
<p>&nbsp;</p>


<h4>Texto format�vel com anexos</h4>

<p>
    Este template � semelhante ao texto format�vel mas apresenta automaticamente uma lista das pastas do tipo
    "Imagem/Anexo" que se encontram abaixo da pasta (suas filhas).
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-texto-com-anexos.png"
                            alt="" width="970" height="291" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emerg�ncia - <em>Frontoffice</em> de Texto Format�vel com anexos</div>

<p>&nbsp;</p>


<h4>�ndice de Contactos</h4>

<p>
    Este template permite indexar as pastas do tipo "Contacto" que se encontram em n�veis abaixo da pasta,
    apresentando-os
    em tabelas, com possibilidade de pesquisa e filtragem.
</p>

<p>
    Este template n�o necessita de inser��o de dados na <em>tab</em> "Conte�dos". O conte�do desta pasta
    vem do conte�do das suas filhas do tipo "Contacto".
</p>

<p>
    Nota: Esta pasta indexa os conte�dos de forma recursiva pelo que � poss�vel ter v�rios n�veis de pastas
    do tipo "Contacto", abaixo do "�ndice de Contactos".
</p>

<h4>Contacto</h4>

<p>
    Representa um contacto que � indexado no "�ndice de Contactos". Este � um template auxiliar, n�o sendo
    vis�vel directamente no <em>frontoffice</em>, apenas � usado pelo "�ndice de Contactos". Um contacto
    corresponde a um conjunto de informa��o de comunica��o habitual: telefone, email, nome, etc.
</p>

<p>
    Um exemplo de uma estrutura completa de �ndice e contactos �:
</p>
<ul>
    <li>�ndice de Contactos
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

<h4>�ndice de documentos</h4>

<p>
    Este template permite indexar as pastas do tipo "Documento" que se encontram em n�veis abaixo do �ndice de
    documentos. A consulta
    desta pasta apresenta uma tabela com os dados dos documentos indexados, permitindo ainda filtrar os dados a
    apresentar
    na tabela.
</p>

<p>
    Este template n�o necessita de inser��o de dados na <em>tab</em> "Conte�dos". O conte�do desta pasta
    vem do conte�do das suas filhas do tipo "Documento".
</p>

<h4>Documento</h4>

<p>
    Representa um documento permitindo anexar zero ou mais ficheiros por documento.
</p>

<p>
    As pastas deste tipo s�o indexadas pelo �ndice de documentos, sendo um template auxiliar, n�o vis�vel
    directamente no <em>frontoffice</em> (n�o aparece no menu de navega��o).
</p>


<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-documento.png"
                            alt="" width="967" height="351" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emerg�ncia - Template Documento</div>

<p>&nbsp;</p>

<h4>�ndice Perguntas Frequentes</h4>

<p>
    Este template permite indexar as pastas do tipo "Pergunta Frequente", mostrando uma listagem com todas as perguntas
    frequentes que se encontram em n�veis abaixo desta.
</p>

<p>
    Este template n�o necessita de inser��o de dados na <em>tab</em> "Conte�dos". O conte�do desta pasta
    vem do conte�do das suas filhas do tipo "Pergunta Frequente".
</p>

<p>
    � ainda poss�vel criar "categorias" de perguntas frequentes: sempre que uma pasta deste tipo tenha uma filha que
    seja
    do tipo "�ndice Perguntas Frequentes" ela � considerada uma subcategoria. Esta funcionalidade permite uma melhor
    organiza��o das perguntas frequentes.
</p>

<p>
    Exemplo da organiza��o de perguntas frequentes com categorias:
</p>
<ul class="helpList">
    <li>FAQ (pasta do tipo �ndice de perguntas frequentes)
        <ul class="helpList">
            <li>Sismos (pasta do tipo �ndice de perguntas frequentes)
                <ul class="helpList">
                    <li>O que fazer em caso de sismo? (pasta do tipo pergunta frequente)</li>
                    <li>O que fazer ap�s o sismo? (pasta do tipo pergunta frequente)</li>
                </ul>
            </li>
            <li>Inc�ndios (pasta do tipo �ndice de perguntas frequentes)
                <ul class="helpList">
                    <li>O que fazer se ocorrer um inc�ndio? (pasta do tipo pergunta frequente)</li>
                    <li>Como utilizar um extintor? (pasta do tipo pergunta frequente)</li>
                </ul>
            </li>
        </ul>
    </li>
</ul>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-faq.png"
                            alt="" width="939" height="227" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emerg�ncia - Exemplo do <em>frontoffice</em> de um �ndice de Perguntas Frequentes,
    organizado por categorias
</div>
<p>&nbsp;</p>


<h4>Pergunta Frequente</h4>

<p>
    � o elemento usado no "�ndice Perguntas Frequentes", � constitu�do por uma pergunta e uma resposta, em texto
    format�vel.
</p>

<p>
    As pastas deste tipo s�o indexadas pelo �ndice perguntas frequentes, sendo um template auxiliar, n�o vis�vel
    directamente no <em>frontoffice</em> (n�o aparece no menu de navega��o).
</p>

<p>&nbsp;</p>
<h4>�ndice Procedimentos</h4>

<p>
    Este tipo de pastas indexa os seus filhos do tipo "Texto Format�vel", permitindo a navega��o para essas p�ginas
    atrav�s de filtros din�micos at� ao m�ximo de 3 n�veis.
</p>

<p>
    As pastas abaixo desta n�o ficam vis�veis no menu de navega��o, apenas � poss�vel aceder-lhes usando os filtros
    apresentados.
    Quando uma das pastas indexadas n�o possui filhos, � apresentado o seu conte�do (texto format�vel). Caso a pasta
    tenha filhos, eles s�o carregados na caixa de selec��o (filtro) seguinte.
</p>

<p>
    Exemplo da organiza��o correcta para o �ndice de procedimentos:
</p>
<ul class="helpList">
    <li>Procedimentos (pasta do tipo �ndice de procedimentos)
        <ul class="helpList">
            <li>Instru��o Geral de Actua��o (pasta do tipo texto format�vel - o seu conte�do n�o � apresentado, serve
                apenas como categoria de filtragem)

                <ul class="helpList">
                    <li>Inc�ndio (pasta do tipo texto format�vel)</li>
                    <li>Sismo (pasta do tipo texto format�vel - o seu conte�do n�o � apresentado, serve apenas como
                        categoria
                        de filtragem)

                        <ul class="helpList">
                            <li>Vigilante (pasta do tipo texto format�vel)</li>
                            <li>Central de Seguran�a (pasta do tipo texto format�vel)</li>
                            <li>Equipa de Manuten��o (pasta do tipo texto format�vel)</li>
                        </ul>
                    </li>
                </ul>
            </li>
        </ul>
    </li>
</ul>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-templates-procedimentos.png"
                            alt="" width="967" height="84" class="imgBorder"/></p>

<div class="imageCaption">Plano de Emerg�ncia - Exemplo do <em>frontoffice</em> de um �ndice de Procedimentos</div>
<p>

</p>

<p>&nbsp;</p>
</ss:secure>

<ss:secure roles="peimanager">
<a name="peiCopy"></a>

<h2><span>C�pia Plano de Emerg�ncia</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Ap�s clicar na op��o do menu "C�pia", � apresentada uma p�gina que lhe permite copiar um Plano de
    Emerg�ncia para outro contrato.
</p>

<p>
    A c�pia inclui todas as pastas e conte�dos do Plano de Emerg�ncia de origem (vers�o de trabalho e
    vers�o publicada), bem como as suas permiss�es.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-copia.png"
                            alt="C�pia Plano de Emerg�ncia" width="970" height="377" class="imgBorder"/></p>

<div class="imageCaption">C�pia Plano de Emerg�ncia</div>

<p>&nbsp;</p>
</ss:secure>

<ss:secure roles="peimanager,clientpeimanager">
<a name="peiPermissions"></a>

<h2><span>Gest�o de Permiss�es</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>
    Ao clicar na op��o do menu "Permiss�es" � apresentada uma p�gina que lhe permite adicionar novas permiss�es
    a um contrato e visualizar todas as permiss�es desse contrato.
</p>

<p>
    Ao clicar numa permiss�o, � aberta a �rvore de todas as pastas de um PEI de forma a consultar facilmente quais
    as pastas que esta permiss�o permite aceder.
</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-permissoes.png"
                            alt="Gest�o de Permiss�es" width="970" height="392" class="imgBorder"/></p>

<div class="imageCaption">Gest�o de Permiss�es</div>

<p>&nbsp;</p>

<p>Detalhe de uma permiss�o com a �rvore de pastas aberta</p>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-permissoes-detalhe.png"
                            alt="Gest�o de Permiss�es" width="970" height="558" class="imgBorder"/></p>

<div class="imageCaption">Gest�o de Permiss�es</div>
<p>&nbsp;</p>

<p>Nota:</p>
<ul>
    <li>Apenas � poss�vel apagar uma permiss�o que n�o esteja associada a utilizadores ou em uso em alguma pasta
        do Plano de Emerg�ncia.
    </li>
</ul>

<p>&nbsp;</p>

<h3>Exemplos:</h3>

<p>Pretende-se permitir o acesso ao PEI a todos os utilizadores excepto um utilizador que n�o pode consultar a
    sec��o Organograma.</p>

<p>
    Neste caso, deve-se criar a permiss�o para o PEI: "Utilizador Organograma" (na Gest�o de Permiss�es).
    Na Gest�o do PEI, seleccionar a pasta Organograma, seleccionar a permiss�o "Utilizador Organograma" e guardar as
    altera��es.
    Desta forma s� os utilizadores com esta permiss�o podem aceder � sec��o Organograma.
    Em seguida, na Gest�o de Utilizadores deve-se atribuir esta permiss�o a todos os utilizadores, excepto ao
    utilizador que se pretende restringir.
</p>

<p>&nbsp;</p>

<p>
    Num PEI contendo a sec��o Organograma e, dentro dessa, duas pastas chamadas <em>Descritivo</em> e <em>Descritivo
    Restrito</em>,
    pretende-se limitar o acesso � sec��o Organograma a um grupo de utilizadores e limitar o acesso � pasta <em>Descritivo
    Restrito</em> a apenas um utilizador.
</p>

<p>
    Para esta situa��o devem ser criadas duas permiss�es: "Utilizador Organograma" e "Utilizador Organograma Acesso
    Total".
    Na sec��o Organograma deve-se limitar o seu acesso com a permiss�o "Utilizador Organograma". Depois desta
    opera��o, seleccionar a pasta
    <em>Descritivo Restrito</em>, seleccionar a permiss�o "Utilizador Organograma Acesso Total" e guardar as
    altera��es.
    Atribuir, aos utilizadores que podem aceder ao Organograma, a permiss�o "Utilizador Organograma" (Gest�o de
    Utilizadores). Ao �nico utilizador
    que pode aceder � pasta <em>Descritivo Restrito</em> (e seus filhos) deve-se ser atribuido a permiss�o
    "Utilizador Organograma" e "Utilizador Organograma Acesso Total".
</p>

<p>&nbsp;</p>
</ss:secure>

</div>
</ss:secure>