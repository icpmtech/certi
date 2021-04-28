<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Adicionar Contrato</title>
</head>

<ss:secure roles="contractmanager">
    <h2><span>Adicionar Contrato</span></h2>

    <p>Após clicar numa entidade seleccionar a funcionalidade que permite adicionar um novo contrato à entidade. É
        devolvida
        uma página com um formulário para preenchimento dos dados de um novo contrato.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/02-04-2009 11-30-25.png"
                                alt="Adicionar Contrato"
                                width="933" height="585"/></p>

    <div class="imageCaption">Adicionar Contrato</div>

    <p>&nbsp;</p>
    
    <h3>Registo pelos utilizadores</h3>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/contract-user-register.png"
                                alt="Registo pelos utilizadores"
                                width="619" height="218" class="imgBorder"/></p>
    <p>
        Caso o código esteja definido, o registo pelos utilizadores é permitido para esta entidade. O link de registo
        fica disponível na página de detalhe do contrato. Os utilizadores que se registem com esse link ficam associados
        ao contrato e, caso se aplique, às permissões selecionadas.
    </p>

    <p>Notas:</p>
    <ul>
        <li>O registo apenas é permitido aos utilizadores com email dos domínios autorizados. Caso o campo dos domínios
            esteja vazio, o registo é permitido para todos os utilizadores.
        </li>
        <li>Um utilizador que se registe desta forma apenas fica associado a um contrato.</li>
        <li>Por omissão, o campo entidade externa fica vazio, o campo "limitado a sessão única" desactivado e o campo
            "activo" ligado.
        </li>
        <li>
            Não é possível adicionar a permissão "Gestor do PEI" às permissões base, dado o seu significado especial no
            contexto do CertiTools.
        </li>
        <li>
            O título para os menus é usado apenas para contratos do tipo planos (PEI, PPREV, etc).
            Nesta situação, caso o utilizador esteja associado a apenas um plano do mesmo tipo, o texto para os
            menus terá o valor introduzido neste campo. Caso esteja vazio será usado o texto por
            omissão.
        </li>
    </ul>
</ss:secure>