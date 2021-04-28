<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="/includes/taglibs.jsp" %>

<head>
    <title>Legislação</title>
</head>

<h2><span>Legislação</span></h2>


<ss:secure roles="legislationAccess">
    <ul class="helpIndexList">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    >
                <s:param name="helpId" value="legislation-index"/>
                Legislação
            </s:link>
            <ul>
                <ss:secure roles="user">
                    <li><s:link
                            beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                            >
                        <s:param name="helpId" value="legislation-search"/>
                        Pesquisar Legislação por Texto Livre
                    </s:link>
                    </li>
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="legislation-search-category"/>
                            Pesquisar Legislação por Categoria
                        </s:link>
                    </li>
                </ss:secure>
                <ss:secure roles="user">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="legislation-detail"/>
                            Detalhe de Legislação
                        </s:link>
                    </li>
                </ss:secure>
                <ss:secure roles="legislationmanager">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="legislation-add-legislation"/>
                            Adicionar Legislação
                        </s:link>
                    </li>
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="legislation-edit-legislation"/>
                            Editar Legislação
                        </s:link>
                    </li>
                </ss:secure>
                <ss:secure roles="administrator,legislationmanager,contractmanager">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="legislation-stats"/>
                            Estatística
                        </s:link>
                    </li>
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="legislation-stats-result"/>
                            Resultado de Estatística
                        </s:link>
                    </li>
                </ss:secure>
                <ss:secure roles="administrator,legislationmanager">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="legislation-subscrition"/>
                            Subscrição de Newsletter
                        </s:link>
                    </li>
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="legislation-edit-subscrition"/>
                            Editar Subscrição de Newsletter
                        </s:link>
                    </li>
                </ss:secure>
            </ul>
        </li>
    </ul>
</ss:secure>