<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Enviar Alertas</title>
</head>

<ss:secure roles="contractmanager,clientcontractmanager,administrator">
    <h2><span>Enviar Alertas</span></h2>

    <p>Esta funcionalidade permite o envio de alertas (e-mail) para os utilizadores activos nos contratos.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/alert-entity.png"
                                alt="Enviar Alertas"
                                width="646" height="569" style="border: 1px solid #1F497D"/>
    </p>

    <div class="imageCaption">Enviar Alertas</div>

    <p>
        Este formulário é dividido em duas partes:
    <ol>
        <li>
            <p>
                <i>Contratos da entidade</i> onde é possível seleccionar, por contrato, as permissões dos utilizadores
                para
                os quais desejamos enviar o alerta. Existe também a opção “Todos” que no caso de ser seleccionada,
                enviará a
                notificação a todos os utilizadores activos do contrato.
                Ao escolher uma permissão o alerta é enviado apenas aos utilizadores com a permissão seleccionada.
                Escolhendo múltiplas permissões o alerta é enviado aos utilizadores associados a uma ou mais
                das permissões seleccionadas (o alerta é enviado apenas uma vez, mesmo que o utilizador
                esteja associado a várias permissões).
            </p>
        </li>
        <li>
            <p>
                <i>Definição do alerta a enviar</i> onde introduzimos os dados referentes ao alerta.
            </p>
        </li>
    </ol>
    </p>

</ss:secure>