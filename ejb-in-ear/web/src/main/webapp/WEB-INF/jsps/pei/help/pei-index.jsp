<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" language="java" %>
<%@ include file="/includes/taglibs.jsp" %>

<head>
    <title>Planos</title>
</head>

<h2><span>Planos</span></h2>

<ul class="helpIndexList">
    <ss:secure roles="peiAccess">
        <li>
            <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                    >
                <s:param name="helpId" value="pei-index"/>
                Planos
            </s:link>
            <ul>
                <ss:secure roles="user">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="pei-select"/>
                            Selecção do Plano
                        </s:link>
                    </li>
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="pei-cover"/>
                            Capa do Plano
                        </s:link>
                    </li>
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="pei-details"/>
                            Detalhes do Plano
                        </s:link>
                    </li>
                </ss:secure>
                <ss:secure roles="peimanager,clientpeimanager">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="pei-cm"/>
                            Gestão do Plano
                        </s:link>
                    </li>
                </ss:secure>
                <ss:secure roles="peimanager">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="pei-copy"/>
                            Cópia do Plano
                        </s:link>
                    </li>
                </ss:secure>
                <ss:secure roles="peimanager,clientpeimanager">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="pei-permissions"/>
                            Gestão de Permissões
                        </s:link>
                    </li>
                </ss:secure>
                <ss:secure roles="peimanager,clientpeimanager">
                    <li>
                        <s:link beanclass="com.criticalsoftware.certitools.presentation.action.certitools.HelpActionBean"
                                >
                            <s:param name="helpId" value="pei-exportdocx"/>
                            Exportação Docx
                        </s:link>
                    </li>
                </ss:secure>
            </ul>
        </li>
    </ss:secure>
</ul>

