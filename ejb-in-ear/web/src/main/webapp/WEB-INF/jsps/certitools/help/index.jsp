<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="/includes/taglibs.jsp" %>

<head>
    <title>CertiTools</title>
</head>

<h2><span>CertiTools</span></h2>

<ul class="helpIndexList">
<li>
<s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean">
    <s:param name="helpId" value="index"/>
    CertiTools
</s:link>
<ul>
    <ss:secure roles="administrator">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    >
                <s:param name="helpId" value="administration"/>
                Administração
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="contractmanager,clientcontractmanager,administrator">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    >
                <s:param name="helpId" value="entities-management"/>
                Gestão de Entidades
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="contractmanager">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    >
                <s:param name="helpId" value="add-entity"/>
                Adicionar Entidade
            </s:link>
        </li>
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    >
                <s:param name="helpId" value="edit-entity"/>
                Editar Entidade
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="contractmanager,clientcontractmanager,administrator">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    >
                <s:param name="helpId" value="import-users"/>
                Importar Utilizadores
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="contractmanager">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    >
                <s:param name="helpId" value="add-contract"/>
                Adicionar Contrato
            </s:link>
            <ul>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            >
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
                    >
                <s:param name="helpId" value="user-management"/>
                Gestão de Utilizadores
            </s:link>
            <ul>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            >
                        <s:param name="helpId" value="add-user"/>
                        Adicionar Utilizador
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            >
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
                    >
                <s:param name="helpId" value="user-profile"/>
                Perfil do Utilizador
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="administrator">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    >
                <s:param name="helpId" value="configuration"/>
                Configurações
            </s:link>

            <ul>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            >
                        <s:param name="helpId" value="edit-configuration"/>
                        Editar Configurações
                    </s:link>
                </li>

            </ul>
        </li>

        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    >
                <s:param name="helpId" value="news-administration"/>
                Gestão de Notícias
            </s:link>
            <ul>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            >
                        <s:param name="helpId" value="add-news"/>
                        Adicionar Notícia
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            >
                        <s:param name="helpId" value="edit-news"/>
                        Editar Notícia
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            >
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
                    >
                <s:param name="helpId" value="faq"/>
                Listagem de Perguntas Frequentes
            </s:link>
        </li>
    </ss:secure>
    <ss:secure roles="administrator,legislationmanager,peimanager">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    >
                <s:param name="helpId" value="faq-management"/>
                Gestão de Perguntas Frequentes
            </s:link>
            <ul>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            >
                        <s:param name="helpId" value="add-faq"/>
                        Adicionar Pergunta Frequente
                    </s:link>
                </li>
                <li>
                    <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            >
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
                    >
                <s:param name="helpId" value="alert-entity"/>
                Enviar Alertas
            </s:link>
        </li>
    </ss:secure>
</ul>
</li>
</ul>