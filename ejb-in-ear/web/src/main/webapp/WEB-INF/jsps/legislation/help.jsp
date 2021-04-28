<%@ include file="../../../includes/taglibs.jsp" %>

<title><fmt:message key="help.title"/></title>

<a name="top"></a>

<h1><fmt:message key="help.title"/></h1>

<ss:secure roles="legislationAccess">

<ul>
    <li class="helpTOC">
        <a href="#legislation">Legisla��o</a>
        <ul>
            <ss:secure roles="user">
                <li><a href="#legislation-search">Pesquisar Legisla��o por Texto Livre</a></li>
                <li><a href="#legislation-search-category">Pesquisar Legisla��o por Categoria</a></li>
            </ss:secure>
            <ss:secure roles="user">
                <li><a href="#legislation-detail">Detalhe de Legisla��o</a></li>
            </ss:secure>
            <ss:secure roles="legislationmanager">
                <li><a href="#legislation-add-legislation">Adicionar Legisla��o</a></li>
                <li><a href="#legislation-edit-legislation">Editar Legisla��o</a></li>
            </ss:secure>
            <ss:secure roles="administrator,legislationmanager,contractmanager">
                <li><a href="#legislation-stats">Estat�stica</a></li>
                <li><a href="#legislation-stats-result">Resultado de Estat�stica</a></li>
            </ss:secure>
            <ss:secure roles="administrator,legislationmanager">
                <li><a href="#legislation-subscrition">Subscri��o de Newsletter</a></li>
                <li><a href="#legislation-edit-subscrition">Editar Subscri��o de Newsletter</a></li>
            </ss:secure>
        </ul>
    </li>


</ul>

<div class="justify helpText">

<!--========================================================LEGISLATION======================================================!-->

<p>&nbsp;</p>
<hr/>

<a name="legislation"></a>

<div class="helpModule">Legisla��o</div>
<c:set var="control" value="0"/>
<ss:secure roles="legislationmanager">
    <a name="legislation-search"></a>

    <h2 style="margin-top: 10px;"><span>Pesquisar Legisla��o por Texto Livre</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder � pesquisa de legisla��o por texto livre,� devolvida uma p�gina
        com
        as seguintes funcionalidades:
        <em>Pesquisa Texto Livre,</em>
        <em>Pesquisa Categorias,</em>
        <em>Adicionar Legisla��o,</em>
        <em>Hist�rico de Nova Legisla��o,</em>
        <em>Hist�rico de Visualiza��o de Legisla��o.</em>
    </p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/pesquisa-legislacao-gestor-legislacao.png"
            alt="Pesquisar Legisla��o"
            width="933" height="790"/></p>

    <div class="imageCaption">Pesquisar Legisla��o por Texto Livre</div>
    <p></p>

    <p>A pesquisa � executada sobre os seguintes campos constantes na ficha do diploma legal, por ordem decrescente de
        import�ncia:
    </p>

    <ul>
        <li>Identificador do Diploma;</li>
        <li>T�tulo do Diploma;</li>
        <li>Palavras-chave;</li>
        <li>Sum�rio do diploma legal;</li>
        <li>Caracteriza��o T�cnica;</li>
        <li>Diploma legal em formato PDF(a pesquisa sobre documentos em formato PDF s� � poss�vel para documentos
            desprotegidos e conte�dos em texto).
        </li>
    </ul>
    <p>Nota:</p>
    <ul>
        <li>Os resultados da pesquisa apresentam os documentos activos e inactivos na aplica��o;</li>
        <li>Nas tabelas correspondentes ao <em>Hist�rico</em> e � <em>Nova Legisla��o</em>, apenas s�o apresentados os
            documentos activos na aplica��o.
        </li>
    </ul>
    <a name="legislation-search-category"></a>

    <c:set var="control" value="1"/>
</ss:secure>


<ss:secure roles="user">


    <c:if test="${pageScope.control != 1}">
        <a name="legislation-search"></a>

        <h2 style="margin-top: 10px;"><span>Pesquisar Legisla��o por Texto Livre</span></h2>

        <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
        <p>Ap�s clicar na funcionalidade que permite aceder � pesquisa de legisla��o por texto livre,� devolvida uma
            p�gina
            com
            as seguintes funcionalidades:
            <em>Pesquisa Texto Livre,</em>
            <em>Pesquisa Categorias,</em>
            <em>Hist�rico de Nova Legisla��o,</em>
            <em>Hist�rico de Visualiza��o de Legisla��o.</em>
        </p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pesquisa-legislacao-user.png"
                                    alt="Pesquisar Legisla��o"
                                    width="933" height="794"/></p>

        <div class="imageCaption">Pesquisar Legisla��o por Texto Livre</div>
        <p></p>

        <p>Notas:
        </p>

        <ul>
            <li>Os resultados da pesquisa apenas apresentam os documentos activos na aplica��o;</li>
            <li>Nas tabelas correspondentes ao <em>Hist�rico</em> e � <em>Nova Legisla��o</em>, apenas s�o apresentados
                os documentos activos na aplica��o.
            </li>
        </ul>
        <a name="legislation-search-category"></a>
    </c:if>
</ss:secure>


<ss:secure roles="user">
    <h2><span>Pesquisar Legisla��o por Categoria</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder � pesquisa por categorias, � devolvida uma p�gina com a listagem
        de
        todas as categorias de primeiro n�vel dispon�veis.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pesquisa-categoria.png"
                                alt="Pesquisar Legisla��o por Categoria" width="933" height="595"/></p>

    <div class="imageCaption">Pesquisar Legisla��o por Categoria - 1</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>Os n�meros de documentos associados �s diferentes categorias consideram os documentos activos e inactivos na
            aplica��o.
        </li>
        <li>Ao subscrever de uma categoria, todas as categorias filhas (hierarquicamente abaixo) s�o automaticamente
            subscritas. Significa que ap�s a subscri��o da categoria pai (no topo da hierarquia), a funcionalidade de
            subscri��o deixa de estar dispon�vel para as suas categorias filhas.
        </li>
    </ul>
    <p></p>

    <p>Ap�s clicar na funcionalidade que permite aceder � categoria ambiente, � devolvida uma p�gina com a listagem das
        sub-categorias associadas e a funcionalidade que permite listar a legisla��o associada a esta categoria.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/pesquisa-categoria-2.png"
                                alt="Pesquisar Legisla��o por Categoria" width="933" height="592"/></p>

    <div class="imageCaption">Pesquisar Legisla��o por Categoria - 2</div>
    <p></p>
</ss:secure>

<!-- Legislation Details-->
<c:set var="control" value="0"/>

<ss:secure roles="legislationmanager">

    <c:set var="control" value="1"/>
    <a name="legislation-detail"></a>

    <h2><span>Detalhe de Legisla��o</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder ao detalhe de uma legisla��o, � devolvida uma p�gina com a
        informa��o relativa � legisla��o seleccionada.</p>

    <p class="alignCenter"><img
            src="${pageContext.request.contextPath}/images/help/detalhe-legislacao-gestor-legislacao.png"
            alt="Detalhe de Legisla��o"
            width="933" height="808"/></p>

    <div class="imageCaption">Detalhe de Legisla��o</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>Apenas se poder� remover um documento legal, se a este n�o estiverem associados outros documentos legais.
        </li>
    </ul>
</ss:secure>


<ss:secure roles="userguest">
    <c:if test="${pageScope.control != 1}">
        <c:set var="control" value="1"/>
        <a name="legislation-legislation-detail"></a>

        <h2><span>Detalhe de Legisla��o</span></h2>

        <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
        <p>Ap�s clicar na funcionalidade que permite aceder ao detalhe de uma legisla��o, � devolvida uma p�gina com a
            informa��o relativa � legisla��o seleccionada.</p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/detalhe-legislacao-invited.png"
                                    alt="Detalhe de Legisla��o"
                                    width="933" height="624"/></p>

        <div class="imageCaption">Detalhe de Legisla��o</div>
        <p></p>
    </c:if>
</ss:secure>

<ss:secure roles="user">
    <c:if test="${pageScope.control != 1}">
        <a name="legislation-legislation-detail"></a>

        <h2><span>Detalhe de Legisla��o</span></h2>

        <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
        <p>Ap�s clicar na funcionalidade que permite aceder ao detalhe de uma legisla��o, � devolvida uma p�gina com a
            informa��o relativa � legisla��o seleccionada.</p>

        <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/detalhe-legislacao-user.png"
                                    alt="Detalhe de Legisla��o"
                                    width="933" height="757"/></p>

        <div class="imageCaption">Detalhe de Legisla��o</div>
        <p></p>
    </c:if>
</ss:secure>

<ss:secure roles="legislationmanager">
    <a name="legislation-add-legislation"></a>

    <h2><span>Adicionar Legisla��o</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite adicionar uma legisla��o, � devolvida uma p�gina com um formul�rio para
        preenchimento dos dados de uma nova legisla��o.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/adicionar-legislacao.png"
                                alt="Adicionar Legisla��o"
                                width="933" height="684"/></p>

    <div class="imageCaption">Adicionar Legisla��o</div>
    <p></p>

    <p>Ao adicionar uma legisla��o o utilizador dever� levar em conta as seguintes situa��es:
    </p>

    <ul>
        <li>N�o � poss�vel inserir documentos legais com o mesmo : Tipo / numero / ano;</li>
        <li>Quando associamos um documento legal a uma categoria pai, as categorias filhas ficam automaticamente
            associadas;
        </li>
        <li>As palavras-chave do documento legal devem estar separadas por virgulas ou ponto e virgula ou espa�o;</li>
        <li>O documento legal associado em PDF, n�o deve exceder 20 Mb (e tem que ser PDF);</li>
        <li>Quando adicionamos um documento legal a outro, o campo 'n�' tem <em>'autocomplete'</em> (aparecem valores
            existentes),
            ou seja s�o apresentados todos os documentos do m�dulo previamente inseridos na aplica��o.
        </li>
    </ul>

    <a name="legislation-edit-legislation"></a>

    <h2><span>Editar Legisla��o</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite editar uma legisla��o, � devolvida uma p�gina com um formul�rio
        preenchido com os dados da legisla��o.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/edicao-legislacao.png"
                                alt="Editar Legisla��o"
                                width="933" height="835"/></p>

    <div class="imageCaption">Editar Legisla��o</div>
    <p></p>

    <p>Ao editar uma legisla��o o utilizador dever� levar em conta as seguintes situa��es:
    </p>

    <ul>
        <li>N�o � poss�vel inserir documentos legais com o mesmo : Tipo / numero / ano;</li>
        <li>Quando associamos um documento legal a uma categoria pai, as categorias filhas ficam automaticamente
            associadas;
        </li>
        <li>As palavras-chave do documento legal devem estar separadas por virgulas ou ponto e virgula ou espa�o;</li>
        <li>Ficheiro associado em PDF, n�o deve exceder 20 Mb (e tem que ser PDF);</li>
        <li>Na edi��o, caso n�o seja adicionado um novo ficheiro em PDF, o ficheiro inserido anteriormente � mantido;
        </li>
        <li>Quando adicionamos um documento legal a outro, o campo 'n�' tem <em>'autocomplete'</em> (aparecem valores
            existentes),
            ou seja s�o apresentados todos os documentos do m�dulo previamente inseridos na aplica��o;
        </li>
        <li>Na edi��o de uma legisla��o quando o estado � alterado o campo 'Enviar Notifica��o (Altera��o)' fica activo.
            No entanto este campo n�o � de preenchimento obrigat�rio, existindo sempre a possibilidade de activar o
            campo 'Enviar Notifica��o (Cria��o)' (apenas um deles poder� ser seleccionado).
        </li>
    </ul>
</ss:secure>

<!--========================================================stats======================================================!-->

<ss:secure roles="administrator,legislationmanager,contractmanager">
    <a name="legislation-stats"></a>

    <h2><span>Estat�stica</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite visualizar estat�sticas relativas a documentos legais e termos de
        pesquisa,
        � devolvida uma p�gina com um formul�rio para inser��o de um intervalo de tempo (Data in�cio/Data fim), relativo
        aos
        documentos legais e aos dados de pesquisa.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/30-03-2009 19-53-25.png"
                                alt="Executar Estat�stica - 1"
                                width="933" height="194"/></p>

    <div class="imageCaption">Estat�stica</div>
    <p></p>

    <a name="legislation-stats-result"></a>

    <h2><span>Resultado de Estat�stica</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>

    <p>Ap�s executar a ac��o de estat�stica correspondente ao intervalo de tempo inserido anteriormente, � devolvida uma
        p�gina com as listagens de estat�sticas relativas aos documentos legais e aos termos de pesquisa.</p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 15-23-25.png"
                                alt="Resultados de Estat�stica - 2" width="933" height="297"/></p>

    <div class="imageCaption">Resultados de Estat�stica</div>
    <p></p>
</ss:secure>
<!--========================================================subscrition======================================================!-->

<ss:secure roles="administrator,legislationmanager">
    <a name="legislation-subscrition"></a>

    <h2><span>Subscri��o de Newsletter</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder �s subscri��es, � devolvida uma p�gina com as seguintes
        funcionalidades:
        <em>Editar Subscri��o de Newsletter,</em>
        <em>Listagem do conte�do da Subscri��o de Newsletter.</em>
    </p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 15-39-25.png"
                                alt="Subscri��o"
                                width="933"
                                height="216"/></p>

    <div class="imageCaption">Subscri��o de Newsletter</div>
    <p></p>

    <a name="legislation-edit-subscrition"></a>

    <h2><span>Editar Subscri��o de Newsletter</span></h2>

    <div class="backtotop"><a href="#top"><fmt:message key="help.backtotop"/></a></div>
    <p>Ap�s clicar na funcionalidade que permite aceder � edi��o do template de envio da subscri��o de newsletter, �
        devolvida uma
        p�gina
        com um formul�rio preenchido com os dados da subscri��o: <em>Assunto do e-mail;</em> <em>Cabe�alho do
            e-mail;</em> <em>Rodap� do e-mail ;</em> <em>Log�tipo do e-mail.</em></p>

    <p class="alignCenter"><img src="${pageContext.request.contextPath}/images/help/01-04-2009 16-32-25.png"
                                alt="Editar Subscri��o de Newsletter" width="933" height="262"/></p>

    <div class="imageCaption">Editar Subscri��o de Newsletter</div>
    <p></p>

    <p>Notas:
    </p>

    <ul>
        <li>O tamanho m�ximo do log�tipo do e-mail � de 50 MB.</li>
    </ul>

    <p>&nbsp;</p>

</ss:secure>


</div>
</ss:secure>
