<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="help.title"/></title>

<a name="top"></a>

<h1><fmt:message key="help.title"/></h1>

<ul>
    <li class="helpTOC">
        <a href="#certitools">CertiTools</a>
        <ul>
            <ss:secure roles="administrator">
                <li><a href="#administration">Administra��o</a></li>
            </ss:secure>
            <!-- ========================================Gestor contrato cliente - Certitools ==========================================-->
            <ss:secure roles="contractmanager,clientcontractmanager,administrator">
                <li><a href="#entities-management">Gest�o de Entidades</a></li>
            </ss:secure>
            <ss:secure roles="contractmanager">
                <li><a href="#add-entity">Adicionar Entidade</a></li>
                <li><a href="#edit-entity">Editar Entidade</a></li>
            </ss:secure>
            <ss:secure roles="contractmanager,clientcontractmanager,administrator">
                <li><a href="#import-users">Importar Utilizadores</a></li>
            </ss:secure>
            <ss:secure roles="contractmanager">
                <li><a href="#add-contract">Adicionar Contrato</a></li>
                <li><a href="#edit-contract">Editar Contrato</a></li>
            </ss:secure>
            <ss:secure roles="contractmanager,clientcontractmanager,administrator">
                <li><a href="#user-management">Gest�o de Utilizadores</a></li>
                <li><a href="#add-user">Adicionar Utilizador</a></li>
                <li><a href="#edit-user">Editar Utilizador</a></li>
            </ss:secure>
            <ss:secure roles="user">
                <li><a href="#user-profile">Perfil do Utilizador</a></li>
            </ss:secure>
            <ss:secure roles="administrator">
                <li><a href="#configuration">Configura��es</a></li>
                <li><a href="#edit-configuration">Editar Configura��es</a></li>
                <li><a href="#news-administration">Gest�o de Not�cias</a></li>
                <li><a href="#add-news">Adicionar Not�cia</a></li>
                <li><a href="#edit-news">Editar Not�cia</a></li>
                <li><a href="#edit-news-category">Editar Categorias de Not�cias</a></li>
            </ss:secure>
            <ss:secure roles="user">
                <li><a href="#faq">Listagem de Perguntas Frequentes</a></li>
            </ss:secure>
            <ss:secure roles="administrator,legislationmanager,peimanager">
                <li><a href="#faq-management">Gest�o de Perguntas Frequentes</a></li>
                <li><a href="#add-faq">Adicionar Pergunta Frequente</a></li>
                <li><a href="#edit-faq">Editar Pergunta Frequente</a></li>
            </ss:secure>
        </ul>
    </li>
</ul>

<hr/>

<a name="certitools"></a>

<div class="helpModule">CertiTools</div>

<div class="justify helpText">

<!--========================================================entities======================================================!-->
<ss:secure roles="administrator">

    <a name="administration"></a>

    <h2 style="margin-top: 10px;"><span>Administra��o</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p> A aplica��o para gerir as licen�as do CertiTools est� dispon�vel no endere�o:<br/>
        <a href="${pageContext.request.contextPath}/license-management/">${applicationScope.configuration.applicationDomain}/license-management/</a>
    </p>

    <p>&nbsp;</p>

    <a name="entities-management"></a>

    <h2 style="margin-top: 10px;"><span>Gest�o de Entidades</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder � gest�o de entidades � devolvida uma p�gina que permite
        pesquisar entidades.
        Ap�s efectuar uma pesquisa, clicar numa entidade e as seguintes funcionalidades ser�o devolvidas:
        <em>Adicionar Utilizador,</em>
        <em>Listar Utilizadores.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/lista-entidades-admin.png"
                                alt="Gest�o de Entidades"
                                width="933" height="564"/></p>

    <div class="imageCaption">Gest�o de Entidades</div>
    <p></p>
</ss:secure>

<ss:secure roles="contractmanager,clientcontractmanager">
    <a name="entities-management"></a>

    <h2 style="margin-top: 10px;"><span>Gest�o de Entidades</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder � gest�o de entidades � devolvida uma p�gina com a seguintes
        funcionalidades:
        <em>Adicionar Entidade,</em>
        <em>Pesquisar Entidades.</em>
        Ap�s efectuar uma pesquisa, clicar numa entidade e as seguintes funcionalidades ser�o devolvidas:
        <em>Adicionar Entidade,</em>
        <em>Editar Entidade,</em>
        <em>Eliminar Entidade,</em>
        <em>Adicionar Contrato,</em>
        <em>Adicionar Utilizador,</em>
        <em>Listar Utilizadores,</em>
        <em>Importar Utilizadores.</em>
    </p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/lista-entidades-gestor-certitecna.png"
            alt="Gest�o de Entidades"
            width="970" height="643" class="imgBorder"/></p>

    <div class="imageCaption">Gest�o de Entidades</div>
    <p></p>

    <p>Notas:</p>
    <ul>
        <li>N�o se pode eliminar uma entidade se esta tiver utilizadores ou contratos associados;</li>
        <li>N�o se pode eliminar um contrato se este tiver utilizadores associados;</li>
        <li>A lista de contratos � ordenada pelo n�mero e designa��o.</li>
    </ul>
</ss:secure>

<ss:secure roles="contractmanager">
    <a name="add-entity"></a>

    <h2><span>Adicionar Entidade</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s aceder � funcionalidade que permite adicionar uma nova entidade, � devolvida uma p�gina com um formul�rio
        para
        preenchimento dos dados de uma nova entidade.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/adicionar-entidade.png"
                                alt="Adicionar Entidade"
                                width="959" height="517" class="imgBorder"/></p>

    <div class="imageCaption">Adicionar Entidade</div>
    <p></p>

    <a name="edit-entity"></a>

    <h2><span>Editar Entidade</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar numa entidade seleccionar a funcionalidade que permite editar esta mesma entidade, � devolvida uma
        p�gina
        com um formul�rio preenchido com os dados da entidade.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 21-10-25.png"
                                alt="Editar Entidade"
                                width="933" height="354"/></p>

    <div class="imageCaption">Editar Entidade</div>
    <p></p>

</ss:secure>

<ss:secure roles="contractmanager,clientcontractmanager,administrator">
<a name="import-users"></a>

<h2><span>Importar Utilizadores</span></h2>

<div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
<p>A importa��o de utilizadores � realizada a partir de um ficheiro CSV (separador: ';') com um formato
    definido. De forma a garantir que o ficheiro segue o formato correcto devem-se utilizar os ficheiros CSV
    exportados no CertiTools. Um ficheiro de exemplo (com um cabe�alho descrevendo as v�rias colunas) pode ser
    descarregado <a href="${pageContext.request.contextPath}/images/help/importacao-utilizadores.xls">aqui</a>
    (este ficheiro n�o � v�lido para importa��o, dado n�o estar em formato CSV e dado ter uma linha com o cabe�alho).
</p>

<p>Notas:</p>
<ul>
    <li>O processo de importa��o � at�mico, i.e. caso haja erro de importa��o de um dos utilizadores
        � abortado o processo, e indicada a origem e natureza do erro;
    </li>
    <li>Caso se verifique, durante a execu��o do processo de importa��o, que os dados de utilizadores a
        importar correspondem a utilizadores previamente registados na aplica��o ent�o os dados existentes
        ser�o substitu�dos pelos dados a importar. Para efeito de identifica��o un�voca de utilizadores ser�
        utilizado o campo de endere�o de e-mail.
    </li>
</ul>

<h3>Formato do ficheiro</h3>

<p>
    O formato definido para cada linha do ficheiro CSV �:
    <br/>
    Nome;Email;NIF;Telefone;Entidade Externa;Activo;Sess�o �nica;Removido;#Perfis de Acesso#;#Dados do Contrato
    1#;....;# Dados do Contrato n#
</p>
<ul class="helpList">
    <li><em>Nome</em> - campo obrigat�rio</li>
    <li><em>Email</em> - campo obrigat�rio</li>
    <li><em>C�d.Seguran�a</em> - campo obrigat�rio do tipo num�rico</li>
    <li><em>Telefone</em> - campo facultativo (pode ser deixado em branco, mas a coluna nunca pode ser apagada)</li>
    <li><em>Entidade Externa</em> - campo facultativo (pode ser deixado em branco, mas a coluna nunca pode ser apagada)
    </li>
    <li><em>Activo</em> - campo obrigat�rio; valores aceites: true, false</li>
    <li><em>Sess�o �nica</em> - campo obrigat�rio; valores aceites: true, false</li>
    <li><em>Removido</em> - campo obrigat�rio; valores aceites: true, false</li>
    <li><em>Perfis de acesso</em> - Este � um campo com colunas vari�veis dado que um utilizador pode ter 0 ou mais
        perfis associados. Este campo deve come�ar por # e acabar em #. Os v�rios perfis de acesso devem ser
        separados por ';' (colunas diferentes). Aquando da importa��o o perfil "Utilizador" � automaticamente
        adicionado ao utilizador.
        <br/><br/><em>Exemplos</em>:
        <br/>Utilizador sem perfis associados: ##
        <br/>Utilizador com 1 perfil associado: #peimanager#
        <br/>Utilizador com v�rios perfis: #contractmanager;legislationmanager;administrator;peimanager# (o ponto
        e v�rgula assinala uma nova coluna no Excel).
        <br/>
        <br/>
        <em>Valores permitidos para este campo (entre par�ntesis a descri��o do valor)</em>:
        <br/>
        administrator (Administrador), contractmanager (Gestor de Contrato - Certitecna), legislationmanager (Gestor
        do M�dulo Legisla��o - Certitecna), peimanager (Gestor M�dulo PEI - Certitecna), clientcontractmanager
        (Gestor de Utilizadores - Cliente), clientpeimanager (Gestor M�dulo PEI - Cliente), userguest (Convidado)
    </li>
    <li>
        <em>Dados do contrato</em> - Este � um campo composto por v�rios sub-campos e com colunas vari�veis.
        Cada contrato deve come�ar por # e acabar em #. Um contrato � composto pelos seguintes campos:
        <ul>
            <li><em>Identificador</em> - campo obrigat�rio do tipo num�rico</li>
            <li><em>Data de in�cio </em>- campo facultativo do tipo data (dd-mm-yyyy), pode ser deixado em branco, mas
                a coluna nunca pode ser apagada
            </li>
            <li><em>Data de fim</em> - campo facultativo do tipo data (dd-mm-yyyy), pode ser deixado em branco, mas
                a coluna nunca pode ser apagada
            </li>
            <li><em>Permiss�es PEI</em> - campo facultativo, quando o utilizador n�o tem permiss�es esta coluna n�o deve
                existir. Quando existir mais que uma permiss�o devem ser separadas por ';' (colunas diferentes)
            </li>
        </ul>

        <br/><br/><em>Exemplos</em>:
        <br/>Utilizador associado ao contrato N�1: #1;;#
        <br/>Utilizador associado ao contrato N�1 e N�2: #1;;#;#2;;#
        <br/>Utilizador associado ao contrato N�1, com datas de validade definidas: #1;06-08-2009;19-08-2009#
        <br/>Utilizador associado ao contrato N�1, com datas de validade definidas e permiss�es PEI:
        #1;06-08-2009;19-08-2009;Chefe de Seguran�a;Vigilante#
        <br/>Utilizador associado ao contrato N�1 e com permiss�es PEI: #1;;;Chefe de Seguran�a;Vigilante#
    </li>
</ul>

<p>&nbsp;</p>

<h3>Exemplo de um ficheiro de importa��o</h3>
<table class="displaytag helpImportFileTable">
    <thead>
        <tr>
            <th>Linha do ficheiro</th>
            <th>Explica��o</th>
        </tr>
    </thead>
    <tr>
        <td class="helpImportFileTableLeft">
            <p>
                a;a@a.com;123;;;true;false;false;#contractmanager;legislationmanager;administrator;peimanager#;#1;06-08-2009;19-08-2009;Chefe
                de Seguran�a;Vigilante#;</p>
        </td>
        <td class="helpImportFileTableRight">
            <p>
                Utilizador com perfil de acesso Gestor de Contrato, Gestor de Legisla��o, Administrador e Gestor de PEI.
                Associado ao contrato com identificador 1, com data de validade para esse contrato de 06-08-2009 at�
                19-08-2009 e com permiss�es PEI de "Chefe de Seguran�a" e "Vigilante".
            </p>
        </td>
    </tr>
    <tr>
        <td class="helpImportFileTableLeft">
            <p>Administrador;admin@a.com;123456789;91555;;true;false;false;#administrator#;##</p>
        </td>
        <td class="helpImportFileTableRight">
            <p>
                Utilizador com perfil de Administrador, sem associa��o a contratos e n�mero de telefone "91555".
            </p>
        </td>
    </tr>
    <tr>
        <td class="helpImportFileTableLeft">
            <p>b;b@a.com;12345;;entidade
                externa;true;true;false;#legislationmanager#;#1;;#;#2;03-03-2009;31-03-2009#;#3;;#;</p>
        </td>
        <td class="helpImportFileTableRight">
            <p>
                Utilizador com perfil de Gestor de Legisla��o, limitado a sess�o �nica e associado a 3 contratos.
                Nos contratos com identificador 1 e 3 n�o foi definida a data de validade ou permiss�es PEI.
            </p>
        </td>
    </tr>
    <tr>
        <td class="helpImportFileTableLeft">
            <p>p;p@a.com;1234;;;false;false;false;##;##</p>
        </td>
        <td class="helpImportFileTableRight">
            <p>
                Utilizador sem perfis de acesso definidos nem associa��es a contratos.
            </p>
        </td>
    </tr>
</table>

<p>&nbsp;</p>

<h3>Abrir o ficheiro CSV no Excel</h3>

<p>
    Para manipula��o do ficheiro CSV, recomenda-se o uso do Microsoft Excel. De forma a garantir a correcta manipula��o
    do ficheiro devem-se seguir as seguintes instru��es para importar os dados para uma folha do Excel (vers�o 2003):
</p>
<ol>
    <li>Num livro novo, ir ao menu "Dados > Importar dados externos > Importar Dados", seleccionar o ficheiro CSV e
        carregar
        em "Abrir"
    </li>
    <li>Na nova caixa de op��es que aparece, seleccionar "Delimitado" e na origem do ficheiro escolher
        "65001 : Unicode (UTF-8)". Avan�ar para o pr�ximo passo.
    </li>
    <li>Escolher como delimitador o ponto e v�rgula e carregar em
        "terminar".
    </li>
</ol>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/import-users-excel.png"
                            alt="Ficheiro aberto no Excel"
                            width="970" height="70" /></p>

<div class="imageCaption">Ficheiro CSV de exemplo no Excel</div>


<p></p>
</ss:secure>

<ss:secure roles="contractmanager">

    <!--========================================================contract======================================================!-->

    <a name="add-contract"></a>

    <h2><span>Adicionar Contrato</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar numa entidade seleccionar a funcionalidade que permite adicionar um novo contrato � entidade. �
        devolvida
        uma p�gina com um formul�rio para preenchimento dos dados de um novo contrato.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/02-04-2009 11-30-25.png"
                                alt="Adicionar Contrato"
                                width="933" height="585"/></p>

    <div class="imageCaption">Adicionar Contrato</div>
    <p></p>

    <a name="edit-contract"></a>

    <h2><span>Editar Contrato</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar numa entidade seleccionar a funcionalidade que permite editar um contrato. � devolvida
        uma p�gina com um formul�rio preenchido com os dados do contrato seleccionado.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/04-04-2009 11-39-25.png"
                                alt="Editar Contrato"
                                width="933" height="587"/></p>

    <div class="imageCaption">Editar Contrato</div>
    <p></p>

    <!--========================================================user======================================================!-->
</ss:secure>

<ss:secure roles="contractmanager,clientcontractmanager,administrator">
    <a name="user-management"></a>

    <h2><span>Gest�o de Utilizadores</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder � gest�o de utilizadores � devolvida uma p�gina com a seguintes
        funcionalidades:
        <em>Adicionar Utilizador,</em>
        <em>Pesquisar Utilizadores,</em>
        <em>Editar Utilizador,</em>
        <em>Eliminar Utilizador,</em>
        <em>Reset Palavra-Chave.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/02-04-2009 16-25-25.png"
                                alt="Gest�o de Utilizadores"
                                width="933" height="587"/></p>

    <div class="imageCaption">Gest�o de Utilizadores</div>
    <p></p>
</ss:secure>

<ss:secure roles="administrator">
    <a name="add-user"></a>

    <h2><span>Adicionar Utilizador</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s aceder � funcionalidade que permite adicionar um novo utilizador � entidade, � devolvida uma p�gina com um
        formul�rio para preenchimento dos dados de um novo utilizador</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/adicionar-utilizador-admin.png"
                                alt="Adicionar Utilizador"
                                width="933" height="703"/></p>

    <div class="imageCaption">Adicionar Utilizador</div>
    <p>Notas:
    </p>
    <ul>
        <li>Se ao inserir um novo utilizador for devolvido o erro '<em>j� existe um utilizador com esse email</em>',
            dever�
            ser levado em conta que tamb�m s�o considerados os utilizadores que foram removidos. Este comportamento
            verifica-se
            porque os utilizadores removidos s�o de facto desactivados ficando assim as suas informa��es armazenadas na
            base de dados.
        </li>
    </ul>

    <p>&nbsp;</p>

    <p>Caso a entidade tenha contratos do m�dulo PEI, � poss�vel associar permiss�es PEI ao utilizador.</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/adicionar-utilizador-permissoes-pei.png"
            alt="Adicionar Utilizador"
            width="635" height="394" class="imgBorder"/></p>

    <div class="imageCaption">Adicionar Utilizador - Detalhe permiss�es PEI</div>
    <p>Nota:</p>
    <ul>
        <li>
            A permiss�o "Gestor do PEI" � uma permiss�o especial, dispon�vel para todos os contratos do m�dulo PEI.
            Para que um utilizador de uma entidade cliente possa gerir o PEI de um contrato, � necess�rio ter o
            perfil de acesso "Gestor M�dulo PEI" e a permiss�o "Gestor do PEI" para esse contrato.
        </li>
    </ul>

    <p></p>

    <p>Na escolha dos perfis associados ao utilizador dever� ser levado em conta quais s�o as diferentes ac��es
        associadas a
        um perfil (reflectidas na matriz abaixo).</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/matriz-perfis-funcionalidades.png"
                                alt="Matriz de Perfis"
                                width="862" height="1662"/></p>

    <div class="imageCaption">Matriz de Perfis</div>
    <p></p>

    <a name="edit-user"></a>

    <h2><span>Editar Utilizador</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar num utilizador seleccionar a funcionalidade que permite editar este mesmo utilizador. � devolvida uma
        p�gina com um formul�rio preenchido com os dados do utilizador.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/edicao-utilizador-admin.png"
                                alt="Editar Utilizador"
                                width="933" height="702"/></p>

    <div class="imageCaption">Editar Utilizador</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>A altera��o de perfis de acesso apenas � aplicada efectivamente quando o utilizador
            inicia uma nova sess�o.
        </li>
        <li>N�o � poss�vel editar o Administrador.</li>
    </ul>
</ss:secure>

<ss:secure roles="contractmanager">
    <a name="add-user"></a>

    <h2><span>Adicionar Utilizador</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s aceder � funcionalidade que permite adicionar um novo utilizador � entidade, � devolvida uma p�gina com um
        formul�rio para preenchimento dos dados de um novo utilizador</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/adicionar-utilizador-gestor-contrato-certitecna.png"
            alt="Adicionar Utilizador"
            width="933" height="678"/></p>

    <div class="imageCaption">Adicionar Utilizador</div>

    <p>Nota: Se ao inserir um novo utilizador for devolvido o erro '<em>j� existe um utilizador com esse email</em>',
        dever�
        ser levado em conta que tamb�m s�o considerados os utilizadores que foram removidos. Este comportamento
        verifica-se
        porque os utilizadores removidos s�o de facto desactivados ficando assim as suas informa��es armazenadas na base
        de
        dados.</p>

    <p>&nbsp;</p>

    <p>Caso a entidade tenha contratos do m�dulo PEI, � poss�vel associar permiss�es PEI ao utilizador.</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/adicionar-utilizador-permissoes-pei.png"
            alt="Adicionar Utilizador"
            width="635" height="394" class="imgBorder"/></p>

    <div class="imageCaption">Adicionar Utilizador - Detalhe permiss�es PEI</div>
    <p>Nota:</p>
    <ul>
        <li>
            A permiss�o "Gestor do PEI" � uma permiss�o especial, dispon�vel para todos os contratos do m�dulo PEI.
            Para que um utilizador de uma entidade cliente possa gerir o PEI de um contrato, � necess�rio ter o
            perfil de acesso "Gestor M�dulo PEI" e a permiss�o "Gestor do PEI" para esse contrato.
        </li>
    </ul>


    <p></p>

    <p>Na escolha dos perfis associados ao utilizador dever� ser levado em conta quais s�o as diferentes ac��es
        associadas a
        um perfil (reflectidas na matriz abaixo).</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/matriz-perfis-funcionalidades.png"
                                alt="Matriz de Perfis"
                                width="862" height="1662"/></p>

    <div class="imageCaption">Matriz de Perfis</div>
    <p></p>

    <a name="edit-user"></a>

    <h2><span>Editar Utilizador</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar num utilizador seleccionar a funcionalidade que permite editar este mesmo utilizador. � devolvida uma
        p�gina com um formul�rio preenchido com os dados do utilizador.</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/edicao-utilizador-gestor-contrato-certitecna.png"
            alt="Editar Utilizador"
            width="933" height="678"/></p>

    <div class="imageCaption">Editar Utilizador</div>
    <p></p>
</ss:secure>

<ss:secure roles="clientcontractmanager">
    <a name="add-user"></a>

    <h2><span>Adicionar Utilizador</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s aceder � funcionalidade que permite adicionar um novo utilizador � entidade, � devolvida uma p�gina com um
        formul�rio para preenchimento dos dados de um novo utilizador</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/adicionar-utilizador-gestor-contrato-cliente.png"
            alt="Adicionar Utilizador"
            width="933" height="487"/></p>

    <div class="imageCaption">Adicionar Utilizador</div>
    <p>Nota: Se ao inserir um novo utilizador for devolvido o erro '<em>j� existe um utilizador com esse email</em>',
        dever�
        ser levado em conta que tamb�m s�o considerados os utilizadores que foram removidos. Este comportamento
        verifica-se
        porque os utilizadores removidos s�o de facto desactivados ficando assim as suas informa��es armazenadas na base
        de
        dados.</p>


    <p></p>

    <p>Na escolha dos perfis associados ao utilizador dever� ser levado em conta quais s�o as diferentes ac��es
        associadas a
        um perfil (reflectidas na matriz abaixo).</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/matriz-perfis-funcionalidades.png"
                                alt="Matriz de Perfis"
                                width="862" height="1662"/></p>

    <div class="imageCaption">Matriz de Perfis</div>
    <p></p>

    <a name="edit-user"></a>

    <h2><span>Editar Utilizador</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar num utilizador seleccionar a funcionalidade que permite editar este mesmo utilizador. � devolvida uma
        p�gina com um formul�rio preenchido com os dados do utilizador.</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/edicao-utilizador-gestor-contrato-cliente.png"
            alt="Editar Utilizador"
            width="933" height="490"/></p>

    <div class="imageCaption">Editar Utilizador</div>
    <p></p>
</ss:secure>


<ss:secure roles="user">
    <a name="user-profile"></a>

    <h2><span>Perfil do Utilizador</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder ao perfil do utilizador, � devolvida uma p�gina com um
        formul�rio para preenchimento da sua nova palavra-chave.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/04-04-2009 13-49-25.png"
                                alt="Perfil do Utilizador"
                                width="933" height="400"/></p>

    <div class="imageCaption">Perfil do Utilizador</div>
    <p></p>
</ss:secure>
<!--========================================================configuration======================================================!-->
<ss:secure roles="administrator">
    <a name="configuration"></a>

    <h2><span>Configura��es</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder �s configura��es � devolvida uma p�gina com uma listagem das
        configura��es da aplica��o.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 16-55-25.png"
                                alt="Configura��es"
                                width="933" height="283"/></p>

    <div class="imageCaption">Configura��es</div>
    <p></p>

    <a name="edit-configuration"></a>

    <h2><span>Editar Configura��es</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite editar as configura��es da aplica��o � devolvida uma p�gina com um
        formul�rio preenchido com os dados das configura��es da aplica��o.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 18-02-25.png"
                                alt="Editar Configura��es"
                                width="933" height="412"/></p>

    <div class="imageCaption">Editar Configura��es</div>
    <p></p>
</ss:secure>
<!--========================================================news======================================================!-->
<ss:secure roles="administrator">
    <a name="news-administration"></a>

    <h2><span>Gest�o de Not�cias</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder � gest�o de not�cias, � devolvida uma p�gina com as seguintes
        funcionalidades:
        <em>Listagem de Not�cias,</em>
        <em>Adicionar Not�cia,</em>
        <em>Editar Not�cia,</em>
        <em>Eliminar Not�cia,</em>
        <em>Desactivar Not�cia,</em>
        <em>Editar Categorias de Not�cias.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/02-04-2009 17-14-25.png"
                                alt="Gest�o de Not�cias"
                                width="933" height="415"/></p>

    <div class="imageCaption">Gest�o de Not�cias</div>
    <p></p>
</ss:secure>

<ss:secure roles="administrator">
    <a name="add-news"></a>

    <h2><span>Adicionar Not�cia</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite adicionar uma not�cia, � devolvida uma p�gina com um formul�rio para
        preenchimento dos dados de uma nova not�cia.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/04-04-2009 12-19-25.png"
                                alt="Adicionar Not�cia"
                                width="933" height="326"/></p>

    <div class="imageCaption">Adicionar Not�cia</div>
    <p></p>

    <a name="edit-news"></a>

    <h2><span>Editar Not�cia</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite editar uma not�cia, � devolvida uma p�gina com um formul�rio preenchido
        com os dados da not�cia seleccionada.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/04-04-2009 12-09-25.png"
                                alt="Editar Not�cia"
                                width="933" height="324"/></p>

    <div class="imageCaption">Editar Not�cia</div>
    <p></p>

    <p>Notas:</p>
    <ul>
        <li>Quando se adiciona uma not�cia, esta s� fica publicada se activarmos o campo 'Publicado'.</li>
    </ul>

    <a name="edit-news-category"></a>

    <h2><span>Editar Categorias de Not�cias</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite editar categorias de not�cias, � devolvida uma p�gina com um formul�rio
        preenchido com os nomes das categorias de not�cias.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/02-04-2009 18-46-25.png"
                                alt="Editar Categorias de Not�cias" width="933" height="191"/></p>

    <div class="imageCaption">Editar Categorias de Not�cias</div>
    <p></p>
</ss:secure>


<!--========================================================FAQ's======================================================!-->
<c:set var="control" value="0"/>

<ss:secure roles="legislationmanager,peimanager">

    <c:set var="control" value="1"/>

    <a name="faq"></a>

    <h2><span>Listagem de Perguntas Frequentes</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder � listagem de perguntas frequentes, � devolvida uma p�gina com
        as
        seguintes funcionalidades:
        <em>Gest�o de Perguntas Frequentes,</em>
        <em>Detalhes de Perguntas Frequentes.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/perguntas-frequentes.png"
                                alt="Perguntas Frequentes"
                                width="970" height="535"/></p>

    <div class="imageCaption">Perguntas Frequentes</div>
    <p>Ap�s clicar numa pergunta frequente, os seus detalhes s�o devolvidos.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/30-03-2009 16-23-25.png"
                                alt="Detalhes de Pergunta Frequente" width="933" height="256"/></p>

    <div class="imageCaption">Detalhes de Pergunta Frequente</div>
    <p></p>

    <p></p>
</ss:secure>

<ss:secure roles="user">

    <c:if test="${pageScope.control != 1}">
        <a name="faq"></a>

        <h2><span>Listagem de Perguntas Frequentes</span></h2>

        <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
        <p>Ap�s clicar na funcionalidade que permite aceder � listagem de perguntas frequentes,� devolvida uma p�gina
            com as
            seguintes funcionalidades:
            <em>Detalhes de Perguntas Frequentes.</em>
        </p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/perguntas-frequentes-user.png"
                                    alt="Perguntas Frequentes"
                                    width="933" height="495"/></p>

        <div class="imageCaption">Perguntas Frequentes</div>
        <p>Ap�s clicar numa pergunta frequente, os seus detalhes s�o devolvidos.</p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/30-03-2009 16-23-25.png"
                                    alt="Detalhes de Pergunta Frequente" width="933" height="256"/></p>

        <div class="imageCaption">Detalhes de Pergunta Frequente</div>
        <p></p>

        <p></p>
    </c:if>
</ss:secure>

<ss:secure roles="administrator,legislationmanager,peimanager">
    <a name="faq-management"></a>

    <h2><span>Gest�o de Perguntas Frequentes</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder � gest�o de perguntas frequentes, � devolvida uma p�gina com as
        seguintes funcionalidades:
        <em>Listagem de Perguntas Frequentes,</em>
        <em>Adicionar de Perguntas Frequentes.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/30-03-2009 18-25-25.png"
                                alt="Gest�o de Perguntas Frequentes" width="933" height="546"/></p>

    <div class="imageCaption">Gest�o de Perguntas Frequentes</div>
    <p></p>

    <a name="add-faq"></a>

    <h2><span>Adicionar Pergunta Frequente</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite adicionar uma pergunta frequente, � devolvida uma p�gina com um
        formul�rio
        para preenchimento dos dados de uma nova pergunta frequente.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/30-03-2009 19-06-25.png"
                                alt="Adicionar Pergunta Frequente" width="933" height="384"/></p>

    <div class="imageCaption">Adicionar Pergunta Frequente</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>Quando adicionamos uma pergunta frequente e a associamos a uma categoria, o campo 'Categoria' tem <em>'autocomplete'</em>
            (aparecem valores existentes), ou seja s�o apresentadas todas a categorias do m�dulo previamente escolhidas
            que t�m perguntas frequentes associadas.
        </li>
    </ul>

    <a name="edit-faq"></a>

    <h2><span>Editar Pergunta Frequente</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite editar uma pergunta frequente, � devolvida uma p�gina com um formul�rio
        preenchido com os dados da pergunta frequente.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/03-04-2009 22-54-25.png"
                                alt="Editar Pergunta Frequente" width="933" height="387"/></p>

    <div class="imageCaption">Editar Pergunta Frequente</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>Quando editamos uma pergunta frequente e a associamos a uma nova categoria, o campo 'Categoria' tem <em>'autocomplete'</em>
            (aparecem valores existentes), ou seja s�o apresentadas todas a categorias do m�dulo previamente escolhidas
            que t�m perguntas frequentes associadas.
        </li>
    </ul>
</ss:secure>

</div>