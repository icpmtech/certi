<%@ include file="../../../includes/taglibs.jsp" %>

<html>
<head>
    <title><fmt:message key="configuration.title"/></title>
</head>
<body>
<h1>Hidden admin</h1>

<s:messages/>

<h2>Force delete folder links</h2>
<p>
    This deletes the folder links (the folder is the master of the information and has mirror folders elsewhere).
    Can cause data inconsistency, use with caution.
</p>
<p>
    Example link:<br>
    /PEI113/offline/Sobre a Instalação/folders/Plantas de Gestão de Segurança/folders/PISO -1
    </p>
<s:form beanclass="com.criticalsoftware.certitools.presentation.action.certitools.ConfigurationActionBean">
    <s:text name="masterPasswordAuthor" style="width: 600px;"></s:text>
    <s:submit name="forceDeleteFolderLinks" onclick="return confirm('Really delete?');">Delete</s:submit>
</s:form>

</body>
</html>