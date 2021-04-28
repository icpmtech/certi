<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Importar Utilizadores</title>
</head>

<ss:secure roles="contractmanager,clientcontractmanager,administrator">
<h2><span>Importar Utilizadores</span></h2>

<p>A importação de utilizadores é realizada a partir de um ficheiro CSV (separador: ';') com um formato
    definido. De forma a garantir que o ficheiro segue o formato correcto devem-se utilizar os ficheiros CSV
    exportados no CertiTools. Um ficheiro de exemplo (com um cabeçalho descrevendo as várias colunas) pode ser
    descarregado <a href="${pageContext.request.contextPath}/images/help/importacao-utilizadores.xls">aqui</a>
    (este ficheiro não é válido para importação, dado não estar em formato CSV e dado ter uma linha com o cabeçalho).
</p>

<p>Notas:</p>
<ul>
    <li>O processo de importação é atómico, i.e. caso haja erro de importação de um dos utilizadores
        é abortado o processo, e indicada a origem e natureza do erro;
    </li>
    <li>Caso se verifique, durante a execução do processo de importação, que os dados de utilizadores a
        importar correspondem a utilizadores previamente registados na aplicação então os dados existentes
        serão substituídos pelos dados a importar. Para efeito de identificação unívoca de utilizadores será
        utilizado o campo de endereço de e-mail.
    </li>
    <li>
        Os utilizadores importados ficam com a password não definida e recebem no seu email a habitual mensagem de
        activação de conta, tal como um novo utilizador do CertiTools.
    </li>
</ul>

<h3>Formato do ficheiro</h3>

<p>
    O formato definido para cada linha do ficheiro CSV é:
    <br/>
    Nome;Email;Cód.Segurança;Telefone;Entidade Externa;Activo;Sessão Única;Removido;#Perfis de Acesso#;#Dados do Contrato
    1#;....;# Dados do Contrato n#
</p>
<ul class="helpList">
    <li><em>Nome</em> - campo obrigatório</li>
    <li><em>Email</em> - campo obrigatório</li>
    <li><em>Cód.Segurança</em> - campo obrigatório do tipo numérico</li>
    <li><em>Telefone</em> - campo facultativo (pode ser deixado em branco, mas a coluna nunca pode ser apagada)</li>
    <li><em>Entidade Externa</em> - campo facultativo (pode ser deixado em branco, mas a coluna nunca pode ser apagada)
    </li>
    <li><em>Activo</em> - campo obrigatório; valores aceites: true, false</li>
    <li><em>Sessão Única</em> - campo obrigatório; valores aceites: true, false</li>
    <li><em>Removido</em> - campo obrigatório; valores aceites: true, false</li>
    <li><em>Perfis de acesso</em> - Este é um campo com colunas variáveis dado que um utilizador pode ter 0 ou mais
        perfis associados. Este campo deve começar por # e acabar em #. Os vários perfis de acesso devem ser
        separados por ';' (colunas diferentes). Aquando da importação o perfil "Utilizador" é automaticamente
        adicionado ao utilizador.
        <br/><br/><em>Exemplos</em>:
        <br/>Utilizador sem perfis associados: ##
        <br/>Utilizador com 1 perfil associado: #peimanager#
        <br/>Utilizador com vários perfis: #contractmanager;legislationmanager;administrator;peimanager# (o ponto
        e vírgula assinala uma nova coluna no Excel).
        <br/>
        <br/>
        <em>Valores permitidos para este campo (entre parêntesis a descrição do valor)</em>:
        <br/>
        administrator (Administrador), contractmanager (Gestor de Contrato - Certitecna), legislationmanager (Gestor
        do Módulo Legislação - Certitecna), peimanager (Gestor Módulo PEI - Certitecna), clientcontractmanager
        (Gestor de Utilizadores - Cliente), clientpeimanager (Gestor Módulo PEI - Cliente), userguest (Convidado)
    </li>
    <li>
        <em>Dados do contrato</em> - Este é um campo composto por vários sub-campos e com colunas variáveis.
        Cada contrato deve começar por # e acabar em #. Um contrato é composto pelos seguintes campos:
        <ul>
            <li><em>Identificador</em> - campo obrigatório do tipo numérico</li>
            <li><em>Data de início </em>- campo facultativo do tipo data (dd-mm-yyyy), pode ser deixado em branco, mas
                a coluna nunca pode ser apagada
            </li>
            <li><em>Data de fim</em> - campo facultativo do tipo data (dd-mm-yyyy), pode ser deixado em branco, mas
                a coluna nunca pode ser apagada
            </li>
            <li><em>Permissões PEI</em> - campo facultativo, quando o utilizador não tem permissões esta coluna não deve
                existir. Quando existir mais que uma permissão devem ser separadas por ';' (colunas diferentes)
            </li>
        </ul>

        <br/><br/><em>Exemplos</em>:
        <br/>Utilizador associado ao contrato Nº1: #1;;#
        <br/>Utilizador associado ao contrato Nº1 e Nº2: #1;;#;#2;;#
        <br/>Utilizador associado ao contrato Nº1, com datas de validade definidas: #1;06-08-2009;19-08-2009#
        <br/>Utilizador associado ao contrato Nº1, com datas de validade definidas e permissões PEI:
        #1;06-08-2009;19-08-2009;Chefe de Segurança;Vigilante#
        <br/>Utilizador associado ao contrato Nº1 e com permissões PEI: #1;;;Chefe de Segurança;Vigilante#
    </li>
</ul>

<p>&nbsp;</p>

<h3>Exemplo de um ficheiro de importação</h3>
<table class="displaytag helpImportFileTable">
    <thead>
        <tr>
            <th>Linha do ficheiro</th>
            <th>Explicação</th>
        </tr>
    </thead>
    <tr>
        <td class="helpImportFileTableLeft">
            <p>
                a;a@a.com;123;;;true;false;false;#contractmanager;legislationmanager;administrator;peimanager#;#1;06-08-2009;19-08-2009;Chefe
                de Segurança;Vigilante#;</p>
        </td>
        <td class="helpImportFileTableRight">
            <p>
                Utilizador com perfil de acesso Gestor de Contrato, Gestor de Legislação, Administrador e Gestor de PEI.
                Associado ao contrato com identificador 1, com data de validade para esse contrato de 06-08-2009 até
                19-08-2009 e com permissões PEI de "Chefe de Segurança" e "Vigilante".
            </p>
        </td>
    </tr>
    <tr>
        <td class="helpImportFileTableLeft">
            <p>Administrador;admin@a.com;123456789;91555;;true;false;false;#administrator#;##</p>
        </td>
        <td class="helpImportFileTableRight">
            <p>
                Utilizador com perfil de Administrador, sem associação a contratos e número de telefone "91555".
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
                Utilizador com perfil de Gestor de Legislação, limitado a sessão única e associado a 3 contratos.
                Nos contratos com identificador 1 e 3 não foi definida a data de validade ou permissões PEI.
            </p>
        </td>
    </tr>
    <tr>
        <td class="helpImportFileTableLeft">
            <p>p;p@a.com;1234;;;false;false;false;##;##</p>
        </td>
        <td class="helpImportFileTableRight">
            <p>
                Utilizador sem perfis de acesso definidos nem associações a contratos.
            </p>
        </td>
    </tr>
</table>

<p>&nbsp;</p>

<h3>Abrir o ficheiro CSV no Excel</h3>

<p>
    Para manipulação do ficheiro CSV, recomenda-se o uso do Microsoft Excel. De forma a garantir a correcta manipulação
    do ficheiro devem-se seguir as seguintes instruções para importar os dados para uma folha do Excel (versão 2003):
</p>
<ol>
    <li>Num livro novo, ir ao menu "Dados > Importar dados externos > Importar Dados", seleccionar o ficheiro CSV e
        carregar
        em "Abrir"
    </li>
    <li>Na nova caixa de opções que aparece, seleccionar "Delimitado" e na origem do ficheiro escolher
        "65001 : Unicode (UTF-8)". Avançar para o próximo passo.
    </li>
    <li>Escolher como delimitador o ponto e vírgula e carregar em
        "terminar".
    </li>
</ol>

<p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/import-users-excel.png"
                            alt="Ficheiro aberto no Excel"
                            width="970" height="70"/></p>

<div class="imageCaption">Ficheiro CSV de exemplo no Excel</div>

</ss:secure>