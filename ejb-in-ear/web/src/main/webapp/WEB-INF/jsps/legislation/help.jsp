<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="help.title"/></title>

<a name="top"></a>

<h1><fmt:message key="help.title"/></h1>

<ss:secure roles="legislationAccess">

<ul>
    <li class="helpTOC">
        <a href="#legislation">Legislação</a>
        <ul>
            <ss:secure roles="user">
                <li><a href="#legislation-search">Pesquisar Legislação por Texto Livre</a></li>
                <li><a href="#legislation-search-category">Pesquisar Legislação por Categoria</a></li>
            </ss:secure>
            <ss:secure roles="user">
                <li><a href="#legislation-detail">Detalhe de Legislação</a></li>
            </ss:secure>
            <ss:secure roles="legislationmanager">
                <li><a href="#legislation-add-legislation">Adicionar Legislação</a></li>
                <li><a href="#legislation-edit-legislation">Editar Legislação</a></li>
            </ss:secure>
            <ss:secure roles="administrator,legislationmanager,contractmanager">
                <li><a href="#legislation-stats">Estatística</a></li>
                <li><a href="#legislation-stats-result">Resultado de Estatística</a></li>
            </ss:secure>
            <ss:secure roles="administrator,legislationmanager">
                <li><a href="#legislation-subscrition">Subscrição de Newsletter</a></li>
                <li><a href="#legislation-edit-subscrition">Editar Subscrição de Newsletter</a></li>
            </ss:secure>
        </ul>
    </li>


</ul>

<div class="justify helpText">

<!--========================================================LEGISLATION======================================================!-->

<p>&nbsp;</p>
<hr/>

<a name="legislation"></a>

<div class="helpModule">Legislação</div>
<c:set var="control" value="0"/>
<ss:secure roles="legislationmanager">
    <a name="legislation-search"></a>

    <h2 style="margin-top: 10px;"><span>Pesquisar Legislação por Texto Livre</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Após clicar na funcionalidade que permite aceder à pesquisa de legislação por texto livre,é devolvida uma página
        com
        as seguintes funcionalidades:
        <em>Pesquisa Texto Livre,</em>
        <em>Pesquisa Categorias,</em>
        <em>Adicionar Legislação,</em>
        <em>Histórico de Nova Legislação,</em>
        <em>Histórico de Visualização de Legislação.</em>
    </p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/pesquisa-legislacao-gestor-legislacao.png"
            alt="Pesquisar Legislação"
            width="933" height="790"/></p>

    <div class="imageCaption">Pesquisar Legislação por Texto Livre</div>
    <p></p>

    <p>A pesquisa é executada sobre os seguintes campos constantes na ficha do diploma legal, por ordem decrescente de
        importância:
    </p>

    <ul>
        <li>Identificador do Diploma;</li>
        <li>Título do Diploma;</li>
        <li>Palavras-chave;</li>
        <li>Sumário do diploma legal;</li>
        <li>Caracterização Técnica;</li>
        <li>Diploma legal em formato PDF(a pesquisa sobre documentos em formato PDF só é possível para documentos
            desprotegidos e conteúdos em texto).
        </li>
    </ul>
    <p>Nota:</p>
    <ul>
        <li>Os resultados da pesquisa apresentam os documentos activos e inactivos na aplicação;</li>
        <li>Nas tabelas correspondentes ao <em>Histórico</em> e à <em>Nova Legislação</em>, apenas são apresentados os
            documentos activos na aplicação.
        </li>
    </ul>
    <a name="legislation-search-category"></a>

    <c:set var="control" value="1"/>
</ss:secure>


<ss:secure roles="user">


    <c:if test="${pageScope.control != 1}">
        <a name="legislation-search"></a>

        <h2 style="margin-top: 10px;"><span>Pesquisar Legislação por Texto Livre</span></h2>

        <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
        <p>Após clicar na funcionalidade que permite aceder à pesquisa de legislação por texto livre,é devolvida uma
            página
            com
            as seguintes funcionalidades:
            <em>Pesquisa Texto Livre,</em>
            <em>Pesquisa Categorias,</em>
            <em>Histórico de Nova Legislação,</em>
            <em>Histórico de Visualização de Legislação.</em>
        </p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pesquisa-legislacao-user.png"
                                    alt="Pesquisar Legislação"
                                    width="933" height="794"/></p>

        <div class="imageCaption">Pesquisar Legislação por Texto Livre</div>
        <p></p>

        <p>Notas:
        </p>

        <ul>
            <li>Os resultados da pesquisa apenas apresentam os documentos activos na aplicação;</li>
            <li>Nas tabelas correspondentes ao <em>Histórico</em> e à <em>Nova Legislação</em>, apenas são apresentados
                os documentos activos na aplicação.
            </li>
        </ul>
        <a name="legislation-search-category"></a>
    </c:if>
</ss:secure>


<ss:secure roles="user">
    <h2><span>Pesquisar Legislação por Categoria</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Após clicar na funcionalidade que permite aceder à pesquisa por categorias, é devolvida uma página com a listagem
        de
        todas as categorias de primeiro nível disponíveis.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pesquisa-categoria.png"
                                alt="Pesquisar Legislação por Categoria" width="933" height="595"/></p>

    <div class="imageCaption">Pesquisar Legislação por Categoria - 1</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>Os números de documentos associados às diferentes categorias consideram os documentos activos e inactivos na
            aplicação.
        </li>
        <li>Ao subscrever de uma categoria, todas as categorias filhas (hierarquicamente abaixo) são automaticamente
            subscritas. Significa que após a subscrição da categoria pai (no topo da hierarquia), a funcionalidade de
            subscrição deixa de estar disponível para as suas categorias filhas.
        </li>
    </ul>
    <p></p>

    <p>Após clicar na funcionalidade que permite aceder à categoria ambiente, é devolvida uma página com a listagem das
        sub-categorias associadas e a funcionalidade que permite listar a legislação associada a esta categoria.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pesquisa-categoria-2.png"
                                alt="Pesquisar Legislação por Categoria" width="933" height="592"/></p>

    <div class="imageCaption">Pesquisar Legislação por Categoria - 2</div>
    <p></p>
</ss:secure>

<!-- Legislation Details-->
<c:set var="control" value="0"/>

<ss:secure roles="legislationmanager">

    <c:set var="control" value="1"/>
    <a name="legislation-detail"></a>

    <h2><span>Detalhe de Legislação</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Após clicar na funcionalidade que permite aceder ao detalhe de uma legislação, é devolvida uma página com a
        informação relativa à legislação seleccionada.</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/detalhe-legislacao-gestor-legislacao.png"
            alt="Detalhe de Legislação"
            width="933" height="808"/></p>

    <div class="imageCaption">Detalhe de Legislação</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>Apenas se poderá remover um documento legal, se a este não estiverem associados outros documentos legais.
        </li>
    </ul>
</ss:secure>


<ss:secure roles="userguest">
    <c:if test="${pageScope.control != 1}">
        <c:set var="control" value="1"/>
        <a name="legislation-legislation-detail"></a>

        <h2><span>Detalhe de Legislação</span></h2>

        <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
        <p>Após clicar na funcionalidade que permite aceder ao detalhe de uma legislação, é devolvida uma página com a
            informação relativa à legislação seleccionada.</p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/detalhe-legislacao-invited.png"
                                    alt="Detalhe de Legislação"
                                    width="933" height="624"/></p>

        <div class="imageCaption">Detalhe de Legislação</div>
        <p></p>
    </c:if>
</ss:secure>

<ss:secure roles="user">
    <c:if test="${pageScope.control != 1}">
        <a name="legislation-legislation-detail"></a>

        <h2><span>Detalhe de Legislação</span></h2>

        <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
        <p>Após clicar na funcionalidade que permite aceder ao detalhe de uma legislação, é devolvida uma página com a
            informação relativa à legislação seleccionada.</p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/detalhe-legislacao-user.png"
                                    alt="Detalhe de Legislação"
                                    width="933" height="757"/></p>

        <div class="imageCaption">Detalhe de Legislação</div>
        <p></p>
    </c:if>
</ss:secure>

<ss:secure roles="legislationmanager">
    <a name="legislation-add-legislation"></a>

    <h2><span>Adicionar Legislação</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Após clicar na funcionalidade que permite adicionar uma legislação, é devolvida uma página com um formulário para
        preenchimento dos dados de uma nova legislação.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/adicionar-legislacao.png"
                                alt="Adicionar Legislação"
                                width="933" height="684"/></p>

    <div class="imageCaption">Adicionar Legislação</div>
    <p></p>

    <p>Ao adicionar uma legislação o utilizador deverá levar em conta as seguintes situações:
    </p>

    <ul>
        <li>Não é possível inserir documentos legais com o mesmo : Tipo / numero / ano;</li>
        <li>Quando associamos um documento legal a uma categoria pai, as categorias filhas ficam automaticamente
            associadas;
        </li>
        <li>As palavras-chave do documento legal devem estar separadas por virgulas ou ponto e virgula ou espaço;</li>
        <li>O documento legal associado em PDF, não deve exceder 20 Mb (e tem que ser PDF);</li>
        <li>Quando adicionamos um documento legal a outro, o campo 'nº' tem <em>'autocomplete'</em> (aparecem valores
            existentes),
            ou seja são apresentados todos os documentos do módulo previamente inseridos na aplicação.
        </li>
    </ul>

    <a name="legislation-edit-legislation"></a>

    <h2><span>Editar Legislação</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Após clicar na funcionalidade que permite editar uma legislação, é devolvida uma página com um formulário
        preenchido com os dados da legislação.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/edicao-legislacao.png"
                                alt="Editar Legislação"
                                width="933" height="835"/></p>

    <div class="imageCaption">Editar Legislação</div>
    <p></p>

    <p>Ao editar uma legislação o utilizador deverá levar em conta as seguintes situações:
    </p>

    <ul>
        <li>Não é possível inserir documentos legais com o mesmo : Tipo / numero / ano;</li>
        <li>Quando associamos um documento legal a uma categoria pai, as categorias filhas ficam automaticamente
            associadas;
        </li>
        <li>As palavras-chave do documento legal devem estar separadas por virgulas ou ponto e virgula ou espaço;</li>
        <li>Ficheiro associado em PDF, não deve exceder 20 Mb (e tem que ser PDF);</li>
        <li>Na edição, caso não seja adicionado um novo ficheiro em PDF, o ficheiro inserido anteriormente é mantido;
        </li>
        <li>Quando adicionamos um documento legal a outro, o campo 'nº' tem <em>'autocomplete'</em> (aparecem valores
            existentes),
            ou seja são apresentados todos os documentos do módulo previamente inseridos na aplicação;
        </li>
        <li>Na edição de uma legislação quando o estado é alterado o campo 'Enviar Notificação (Alteração)' fica activo.
            No entanto este campo não é de preenchimento obrigatório, existindo sempre a possibilidade de activar o
            campo 'Enviar Notificação (Criação)' (apenas um deles poderá ser seleccionado).
        </li>
    </ul>
</ss:secure>

<!--========================================================stats======================================================!-->

<ss:secure roles="administrator,legislationmanager,contractmanager">
    <a name="legislation-stats"></a>

    <h2><span>Estatística</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Após clicar na funcionalidade que permite visualizar estatísticas relativas a documentos legais e termos de
        pesquisa,
        é devolvida uma página com um formulário para inserção de um intervalo de tempo (Data início/Data fim), relativo
        aos
        documentos legais e aos dados de pesquisa.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/30-03-2009 19-53-25.png"
                                alt="Executar Estatística - 1"
                                width="933" height="194"/></p>

    <div class="imageCaption">Estatística</div>
    <p></p>

    <a name="legislation-stats-result"></a>

    <h2><span>Resultado de Estatística</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>

    <p>Após executar a acção de estatística correspondente ao intervalo de tempo inserido anteriormente, é devolvida uma
        página com as listagens de estatísticas relativas aos documentos legais e aos termos de pesquisa.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 15-23-25.png"
                                alt="Resultados de Estatística - 2" width="933" height="297"/></p>

    <div class="imageCaption">Resultados de Estatística</div>
    <p></p>
</ss:secure>
<!--========================================================subscrition======================================================!-->

<ss:secure roles="administrator,legislationmanager">
    <a name="legislation-subscrition"></a>

    <h2><span>Subscrição de Newsletter</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Após clicar na funcionalidade que permite aceder às subscrições, é devolvida uma página com as seguintes
        funcionalidades:
        <em>Editar Subscrição de Newsletter,</em>
        <em>Listagem do conteúdo da Subscrição de Newsletter.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 15-39-25.png"
                                alt="Subscrição"
                                width="933"
                                height="216"/></p>

    <div class="imageCaption">Subscrição de Newsletter</div>
    <p></p>

    <a name="legislation-edit-subscrition"></a>

    <h2><span>Editar Subscrição de Newsletter</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
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

    <p>&nbsp;</p>

</ss:secure>


</div>
</ss:secure>
