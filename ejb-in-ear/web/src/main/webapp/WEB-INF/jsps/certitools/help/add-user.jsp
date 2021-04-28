<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Adicionar Utilizador</title>
</head>

<ss:secure roles="administrator">
    <h2><span>Adicionar Utilizador</span></h2>

    <p>Após aceder à funcionalidade que permite adicionar um novo utilizador à entidade, é devolvida uma página com um
        formulário para preenchimento dos dados de um novo utilizador</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/adicionar-utilizador-admin.png"
                                alt="Adicionar Utilizador"
                                width="933" height="703"/></p>

    <div class="imageCaption">Adicionar Utilizador</div>
    <p>Notas:
    </p>
    <ul>
        <li>Se ao inserir um novo utilizador for devolvido o erro '<em>já existe um utilizador com esse email</em>',
            deverá
            ser levado em conta que também são considerados os utilizadores que foram removidos. Este comportamento
            verifica-se
            porque os utilizadores removidos são de facto desactivados ficando assim as suas informações armazenadas na
            base de dados.
        </li>
    </ul>

    <p>&nbsp;</p>

    <p>Caso a entidade tenha contratos do módulo PEI, é possível associar permissões PEI ao utilizador.</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/adicionar-utilizador-permissoes-pei.png"
            alt="Adicionar Utilizador"
            width="635" height="394" class="imgBorder"/></p>

    <div class="imageCaption">Adicionar Utilizador - Detalhe permissões PEI</div>
    <p>Nota:</p>
    <ul>
        <li>
            A permissão "Gestor do PEI" é uma permissão especial, disponível para todos os contratos do módulo PEI.
            Para que um utilizador de uma entidade cliente possa gerir o PEI de um contrato, é necessário ter o
            perfil de acesso "Gestor Módulo PEI" e a permissão "Gestor do PEI" para esse contrato.
        </li>
    </ul>

    <p></p>

    <p>Na escolha dos perfis associados ao utilizador deverá ser levado em conta quais são as diferentes acções
        associadas a
        um perfil (reflectidas na matriz abaixo).</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/matriz-perfis-funcionalidades.png"
                                alt="Matriz de Perfis"
                                width="862" height="1662"/></p>

    <div class="imageCaption">Matriz de Perfis</div>
</ss:secure>

<ss:secure roles="contractmanager">
    <h2><span>Adicionar Utilizador</span></h2>

    <p>Após aceder à funcionalidade que permite adicionar um novo utilizador à entidade, é devolvida uma página com um
        formulário para preenchimento dos dados de um novo utilizador</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/adicionar-utilizador-gestor-contrato-certitecna.png"
            alt="Adicionar Utilizador"
            width="933" height="678"/></p>

    <div class="imageCaption">Adicionar Utilizador</div>

    <p>Nota: Se ao inserir um novo utilizador for devolvido o erro '<em>já existe um utilizador com esse email</em>',
        deverá
        ser levado em conta que também são considerados os utilizadores que foram removidos. Este comportamento
        verifica-se
        porque os utilizadores removidos são de facto desactivados ficando assim as suas informações armazenadas na base
        de
        dados.</p>

    <p>&nbsp;</p>

    <p>Caso a entidade tenha contratos do módulo PEI, é possível associar permissões PEI ao utilizador.</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/adicionar-utilizador-permissoes-pei.png"
            alt="Adicionar Utilizador"
            width="635" height="394" class="imgBorder"/></p>

    <div class="imageCaption">Adicionar Utilizador - Detalhe permissões PEI</div>
    <p>Nota:</p>
    <ul>
        <li>
            A permissão "Gestor do PEI" é uma permissão especial, disponível para todos os contratos do módulo PEI.
            Para que um utilizador de uma entidade cliente possa gerir o PEI de um contrato, é necessário ter o
            perfil de acesso "Gestor Módulo PEI" e a permissão "Gestor do PEI" para esse contrato.
        </li>
    </ul>


    <p></p>

    <p>Na escolha dos perfis associados ao utilizador deverá ser levado em conta quais são as diferentes acções
        associadas a
        um perfil (reflectidas na matriz abaixo).</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/matriz-perfis-funcionalidades.png"
                                alt="Matriz de Perfis"
                                width="862" height="1662"/></p>

    <div class="imageCaption">Matriz de Perfis</div>
</ss:secure>

<ss:secure roles="clientcontractmanager">
    <h2><span>Adicionar Utilizador</span></h2>

    <p>Após aceder à funcionalidade que permite adicionar um novo utilizador à entidade, é devolvida uma página com um
        formulário para preenchimento dos dados de um novo utilizador</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/adicionar-utilizador-gestor-contrato-cliente.png"
            alt="Adicionar Utilizador"
            width="933" height="487"/></p>

    <div class="imageCaption">Adicionar Utilizador</div>
    <p>Nota: Se ao inserir um novo utilizador for devolvido o erro '<em>já existe um utilizador com esse email</em>',
        deverá
        ser levado em conta que também são considerados os utilizadores que foram removidos. Este comportamento
        verifica-se
        porque os utilizadores removidos são de facto desactivados ficando assim as suas informações armazenadas na base
        de
        dados.</p>


    <p></p>

    <p>Na escolha dos perfis associados ao utilizador deverá ser levado em conta quais são as diferentes acções
        associadas a
        um perfil (reflectidas na matriz abaixo).</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/matriz-perfis-funcionalidades.png"
                                alt="Matriz de Perfis"
                                width="862" height="1662"/></p>

    <div class="imageCaption">Matriz de Perfis</div>
</ss:secure>