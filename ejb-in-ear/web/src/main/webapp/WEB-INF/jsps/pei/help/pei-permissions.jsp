<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../../../../includes/taglibs.jsp" %>

<head>
    <title>Gestão de Permissões</title>
</head>

<ss:secure roles="peimanager,clientpeimanager">
    <h2><span>Gestão de Permissões</span></h2>

    <p>
        Ao clicar na opção do menu "Permissões" é apresentada uma página que lhe permite adicionar novas permissões
        a um contrato e visualizar todas as permissões desse contrato de duas formas: por permissão e por pasta do plano.
    </p>

    <p>
        Ao clicar numa permissão, é aberta a árvore de todas as pastas de um Plano de forma a consultar facilmente quais
        as pastas que esta permissão permite aceder.
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-permissoes.png"
                                alt="Gestão de Permissões" width="970" height="392" class="imgBorder"/></p>

    <div class="imageCaption">Gestão de Permissões</div>

    <p>&nbsp;</p>

    <p>Detalhe de uma permissão com a árvore de pastas aberta</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-permissoes-detalhe.png"
                                alt="Gestão de Permissões" width="970" height="558" class="imgBorder"/></p>

    <div class="imageCaption">Gestão de Permissões - Por Permissão</div>
    <p>&nbsp;</p>

    <p>Nota:</p>
    <ul>
        <li>Apenas é possível apagar uma permissão que não esteja associada a utilizadores ou em uso em alguma pasta
            do Plano.
        </li>
    </ul>

    <p>&nbsp;</p>

    <p>Detalhe de uma permissão por pasta. Para aceder a uma pasta o utilizador necessita de conter todas as permissões indicadas entre parêntesis.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pei-permissoes-pasta.png"
                                alt="Gestão de Permissões por Pasta" width="986" height="256" class="imgBorder"/></p>

    <div class="imageCaption">Gestão de Permissões - Por Pasta do Plano</div>


    <h3>Exemplos:</h3>

    <p>Pretende-se permitir o acesso ao Plano a todos os utilizadores excepto um utilizador que não pode consultar a
        secção Organograma.</p>

    <p>
        Neste caso, deve-se criar a permissão para o Plano: "Utilizador Organograma" (na Gestão de Permissões).
        Na Gestão do Plano, seleccionar a pasta Organograma, seleccionar a permissão "Utilizador Organograma" e guardar
        as
        alterações.
        Desta forma só os utilizadores com esta permissão podem aceder à secção Organograma.
        Em seguida, na Gestão de Utilizadores deve-se atribuir esta permissão a todos os utilizadores, excepto ao
        utilizador que se pretende restringir.
    </p>

    <p>&nbsp;</p>

    <p>
        Num Plano contendo a secção Organograma e, dentro dessa, duas pastas chamadas <em>Descritivo</em> e <em>Descritivo
        Restrito</em>,
        pretende-se limitar o acesso à secção Organograma a um grupo de utilizadores e limitar o acesso à pasta <em>Descritivo
        Restrito</em> a apenas um utilizador.
    </p>

    <p>
        Para esta situação devem ser criadas duas permissões: "Utilizador Organograma" e "Utilizador Organograma Acesso
        Total".
        Na secção Organograma deve-se limitar o seu acesso com a permissão "Utilizador Organograma". Depois desta
        operação, seleccionar a pasta
        <em>Descritivo Restrito</em>, seleccionar a permissão "Utilizador Organograma Acesso Total" e guardar as
        alterações.
        Atribuir, aos utilizadores que podem aceder ao Organograma, a permissão "Utilizador Organograma" (Gestão de
        Utilizadores). Ao único utilizador
        que pode aceder à pasta <em>Descritivo Restrito</em> (e seus filhos) deve-se ser atribuido a permissão
        "Utilizador Organograma" e "Utilizador Organograma Acesso Total".
    </p>
</ss:secure>