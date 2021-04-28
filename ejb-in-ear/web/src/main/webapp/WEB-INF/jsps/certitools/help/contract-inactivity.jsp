<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Configurações de inactividade</title>
</head>

<ss:secure roles="contractmanager">
    <h2><span>Configurações de inactividade</span></h2>

    <p>
        O mecanismo de inactividade do CertiTools permite o envio de avisos, via email, a utilizadores inactivos,
        bem como a sua remoção do contrato de forma automática.
        <br />
        O utilizador recebe 2 mensagens de aviso, após os quais é removido do contrato.
        Caso o utilizador não esteja associado a nenhum outro contrato será removido do CertiTools.
    </p>

    <p>&nbsp;</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/contract-inactivity.png"
                                alt="Configurações de inactividade"
                                width="673" height="460" class="imgBorder"/></p>

    <p>
        O formulário de configuração do mecanismo de inactividade permite definir os prazos para que se determine se um
        utilizador está inactivo, bem como o texto das mensagens enviadas ao utilizador.
    </p>
    <p>
        Após o primeiro prazo de inactividade o utilizador recebe a primeira mensagem de aviso no seu email.
        Nesse dia começa também a contar o tempo para o segundo prazo definido, após o qual é enviada a segunda mensagem
        de aviso. No final do terceiro prazo o utilizador é removido do contrato.
        <br>
        De realçar que os prazos são cumulativos, por exemplo, caso a configuração seja 60d + 30d + 5d o utilizador
        será removido do contrato quando perfizer 95 dias de inactividade.
    </p>
    <p>
        Quando um contrato é criado o mecanismo encontra-se desligado por omissão. Quando o mecanismo é ligado,
        é feito o reset à data de última actividade dos utilizadores associados ao contrato. Cada vez que as
        configurações de inactividade são alteradas, é feito o reset. Também quando um utilizador é associado a um
        contrato é feito o reset.
    </p>

    <p>Notas:</p>
    <ul>
        <li>
            Caso algum dos prazos definidos sejam 0, o mecanismo de inactividade não está a funcionar para o respectivo
            contrato.
        </li>
        <li>
            A data de última actividade no sistema é contada apenas nas páginas capa de um plano e na pesquisa da
            legislação (as páginas de entrada dos respectivos módulos). 
        </li>
        <li>
            Os utilizadores com perfis de acesso especiais (Gestores, administradores, etc) nunca são contemplados no
            mecanismo de inactividade.
        </li>
        <li>
            Caso um utilizador esteja associado a vários contratos com o mecanismo ligado, irá receber os respectivos
            avisos de cada contrato.
        </li>
    </ul>


</ss:secure>