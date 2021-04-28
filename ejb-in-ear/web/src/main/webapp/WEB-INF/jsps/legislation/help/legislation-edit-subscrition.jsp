<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Editar Subscrição de Newsletter</title>
</head>

<ss:secure roles="administrator,legislationmanager">
    <h2><span>Editar Subscrição de Newsletter</span></h2>

    <p>Após clicar na funcionalidade que permite aceder à edição do template de envio da subscrição de newsletter, é
        devolvida uma
        página
        com um formulário preenchido com os dados da subscrição: <em>Assunto do e-mail;</em> <em>Cabeçalho do
        e-mail;</em> <em>Rodapé do e-mail ;</em> <em>Logótipo do e-mail.</em></p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 16-32-25.png"
                                alt="Editar Subscrição de Newsletter" width="933" height="262"/></p>

    <div class="imageCaption">Editar Subscrição de Newsletter</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>O tamanho máximo do logótipo do e-mail é de 50 MB.</li>
    </ul>
</ss:secure>