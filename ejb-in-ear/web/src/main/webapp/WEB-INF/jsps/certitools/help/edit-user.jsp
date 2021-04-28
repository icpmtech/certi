<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Editar Utilizador</title>
</head>

<ss:secure roles="administrator">
    <h2><span>Editar Utilizador</span></h2>

    <p>Após clicar num utilizador seleccionar a funcionalidade que permite editar este mesmo utilizador. É devolvida uma
        página com um formulário preenchido com os dados do utilizador.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/edicao-utilizador-admin.png"
                                alt="Editar Utilizador"
                                width="933" height="702"/></p>

    <div class="imageCaption">Editar Utilizador</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>A alteração de perfis de acesso apenas é aplicada efectivamente quando o utilizador
            inicia uma nova sessão.
        </li>
        <li>Não é possível editar o Administrador.</li>
    </ul>
</ss:secure>

<ss:secure roles="contractmanager">
    <h2><span>Editar Utilizador</span></h2>

    <p>Após clicar num utilizador seleccionar a funcionalidade que permite editar este mesmo utilizador. É devolvida uma
        página com um formulário preenchido com os dados do utilizador.</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/edicao-utilizador-gestor-contrato-certitecna.png"
            alt="Editar Utilizador"
            width="933" height="678"/></p>

    <div class="imageCaption">Editar Utilizador</div>
</ss:secure>

<ss:secure roles="clientcontractmanager">
    <h2><span>Editar Utilizador</span></h2>

    <p>Após clicar num utilizador seleccionar a funcionalidade que permite editar este mesmo utilizador. É devolvida uma
        página com um formulário preenchido com os dados do utilizador.</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/edicao-utilizador-gestor-contrato-cliente.png"
            alt="Editar Utilizador"
            width="933" height="490"/></p>

    <div class="imageCaption">Editar Utilizador</div>
</ss:secure>