<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="../includes/taglibs.jsp" %>

<ul class="sf-menu">
<li class="helpMenuSectionsLi">
<s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
        class="helpMenuSections">
    <s:param name="helpId" value="index"/>
    CertiTools
</s:link>
<ul>
    <ss:secure roles="administrator">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="administration"/>
                Administração
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="contractmanager,clientcontractmanager,administrator">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="entities-management"/>
                Gestão de Entidades
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="contractmanager">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="add-entity"/>
                Adicionar Entidade
            </s:link>
        </li>
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="edit-entity"/>
                Editar Entidade
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="contractmanager,clientcontractmanager,administrator">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="import-users"/>
                Importar Utilizadores
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="contractmanager">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="add-contract"/>
                Adicionar Contrato
            </s:link>
            <ul>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="edit-contract"/>
                        Editar Contrato
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="contract-inactivity"/>
                        Configurações de inactividade
                    </s:link>
                </li>

            </ul>
        </li>
    </ss:secure>
    <ss:secure roles="contractmanager,clientcontractmanager,administrator">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="user-management"/>
                Gestão de Utilizadores
            </s:link>
            <ul>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="add-user"/>
                        Adicionar Utilizador
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="edit-user"/>
                        Editar Utilizador
                    </s:link>
                </li>
            </ul>
        </li>

    </ss:secure>
    <ss:secure roles="user">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="user-profile"/>
                Perfil do Utilizador
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="administrator">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="configuration"/>
                Configurações
            </s:link>

            <ul>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="edit-configuration"/>
                        Editar Configurações
                    </s:link>
                </li>

            </ul>
        </li>

        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="news-administration"/>
                Gestão de Notícias
            </s:link>
            <ul>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="add-news"/>
                        Adicionar Notícia
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="edit-news"/>
                        Editar Notícia
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="edit-news-category"/>
                        Editar Categorias de Notícias
                    </s:link>
                </li>
            </ul>
        </li>

    </ss:secure>
    <ss:secure roles="user">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="faq"/>
                Listagem de Perguntas Frequentes
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="administrator,legislationmanager,peimanager">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="faq-management"/>
                Gestão de Perguntas Frequentes
            </s:link>
            <ul>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="add-faq"/>
                        Adicionar Pergunta Frequente
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="edit-faq"/>
                        Editar Pergunta Frequente
                    </s:link>
                </li>
            </ul>
        </li>
    </ss:secure>
    <ss:secure roles="contractmanager,clientcontractmanager,administrator">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    class="helpMenuItem">
                <s:param name="helpId" value="alert-entity"/>
                Enviar Alertas
            </s:link>
        </li>
    </ss:secure>
</ul>
</li>
<ss:secure roles="legislationAccess">
    <li class="helpMenuSectionsLi">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                class="helpMenuSections">
            <s:param name="helpId" value="legislation-index"/>
            Legislação
        </s:link>
        <ul>
            <ss:secure roles="user">
                <li><s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                    <s:param name="helpId" value="legislation-search"/>
                    Pesquisar Legislação por Texto Livre
                </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="legislation-search-category"/>
                        Pesquisar Legislação por Categoria
                    </s:link>
                </li>
            </ss:secure>
            <ss:secure roles="user">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="legislation-detail"/>
                        Detalhe de Legislação
                    </s:link>
                </li>
            </ss:secure>
            <ss:secure roles="legislationmanager">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="legislation-add-legislation"/>
                        Adicionar Legislação
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="legislation-edit-legislation"/>
                        Editar Legislação
                    </s:link>
                </li>
            </ss:secure>
            <ss:secure roles="administrator,legislationmanager,contractmanager">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="legislation-stats"/>
                        Estatística
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="legislation-stats-result"/>
                        Resultado de Estatística
                    </s:link>
                </li>
            </ss:secure>
            <ss:secure roles="administrator,legislationmanager">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="legislation-subscrition"/>
                        Subscrição de Newsletter
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="legislation-edit-subscrition"/>
                        Editar Subscrição de Newsletter
                    </s:link>
                </li>
            </ss:secure>
        </ul>
    </li>
</ss:secure>
<ss:secure roles="peiAccess">
    <li class="helpMenuSectionsLi">
        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                class="helpMenuSections">
            <s:param name="helpId" value="pei-index"/>
            Planos
        </s:link>
        <ul>
            <ss:secure roles="user">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="pei-select"/>
                        Selecção do Plano
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="pei-cover"/>
                        Capa do Plano
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="pei-details"/>
                        Detalhes do Plano
                    </s:link>
                </li>
            </ss:secure>
            <ss:secure roles="peimanager,clientpeimanager">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="pei-cm"/>
                        Gestão do Plano
                    </s:link>
                </li>
            </ss:secure>
            <ss:secure roles="peimanager">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="pei-copy"/>
                        Cópia do Plano
                    </s:link>
                </li>
            </ss:secure>
            <ss:secure roles="peimanager,clientpeimanager">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="pei-permissions"/>
                        Gestão de Permissões
                    </s:link>
                </li>
            </ss:secure>
            <ss:secure roles="peimanager,clientpeimanager">
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            class="helpMenuItem">
                        <s:param name="helpId" value="pei-exportdocx"/>
                        Exportação Docx
                    </s:link>
                </li>
            </ss:secure>
        </ul>
    </li>
</ss:secure>
</ul>

