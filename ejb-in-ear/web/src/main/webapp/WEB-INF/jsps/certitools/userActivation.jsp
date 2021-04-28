<%@ include file="../../../includes/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="user.activation.main.title"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/keyboard/keyboard.css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/keyboard/keyboard.js"></script>
</head>
<body>
<div id="home-content">
    <div class="categories">

        <h2 class="form"><span><fmt:message key="user.activation.subtitle"/></span></h2>

        <s:errors/>

        <c:if test="${param.showform == null}">
            <s:form beanclass="com.criticalsoftware.certitools.presentation.action.UserActivationActionBean"
                    focus="" class="form">
                <p>
                    <s:label for="fiscalNumber"><fmt:message key="user.fiscalNumber"/> (*):</s:label>
                    <s:text name="user.fiscalNumber" class="largeInput" id="fiscalNumber"/>
                </p>

                <p>
                    <s:label for="idpassword1"><fmt:message key="/UserActivation.action.password1"/> (*):</s:label>
                    <s:password name="password1" class="largeInput" id="idpassword1" readonly="true" />
                </p>

                <p>
                    <s:label for="idpassword2"><fmt:message key="/UserActivation.action.password2"/> (*):</s:label>
                    <s:password name="password2" class="largeInput" id="idpassword2" readonly="true" />
                </p>

                <div class="formButtons">
                    <span class="mandatoryFields"><fmt:message key="common.mandatoryfields"/></span>
                    <s:hidden name="user.id"/>
                    <s:hidden name="uid"/>
                    <s:submit name="activateUser" class="button"><fmt:message key="common.login"/></s:submit>
                </div>
            </s:form>
        </c:if>
    </div>
</div>

<script type="text/javascript">

    function openKeyboard(elementId) {
        VKI_show(document.getElementById(elementId));
        positionKeyboard();
    }

    function positionKeyboard() {
        var position = $("#idpassword1").position();
        var topPosition = position.top;
        var leftPosition = position.left + 410;
        $('#keyboardInputMaster').css("top", topPosition);
        $('#keyboardInputMaster').css("left", leftPosition);
    }

    $(document).ready(function() {
        $('#fiscalNumber').focus(function(){
            $('#idpassword2').css("background-color", "#FFFFFF");
            $('#idpassword1').css("background-color", "#FFFFFF");
        });
        $('#idpassword1').focus(function(){
            $('#idpassword2').css("background-color", "#FFFFFF");
            $('#idpassword1').css("background-color", "#FFB900");
        });
        $('#idpassword2').focus(function(){
            $('#idpassword1').css("background-color", "#FFFFFF");
            $('#idpassword2').css("background-color", "#FFB900");
        });

        VKI_buildKeyboardInputs();

        // check if VKI_show is defined
        openKeyboard("idpassword1");
        $('#fiscalNumber').focus();

        $('#idpassword1').click(function() {
            try {
                VKI_close();
            } catch(err) {
            }
            openKeyboard("idpassword1");

        });
        $('#idpassword2').click(function() {
            try {
                VKI_close();
            } catch(err) {
            }
            openKeyboard("idpassword2");
        });

    });

    $(window).resize(function() {
        positionKeyboard();
    });

</script>

</body>
</html>
