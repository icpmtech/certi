 function openKeyboard(elementId) {
        VKI_show(document.getElementById(elementId));
        positionKeyboard();
    }

    function positionKeyboard() {
        var position = $("#j_password").position();
        var topPosition = position.top + 30;
        var leftPosition = position.left;
        $('#keyboardInputMaster').css("top", topPosition);
        $('#keyboardInputMaster').css("left", leftPosition);
    }

    $(document).ready(function() {
        $('#j_username').focus(function(){$('#j_password').css("background-color", "#FFFFFF");});
        $('#j_password').focus(function(){$('#j_password').css("background-color", "#FFB900");});
        
        VKI_buildKeyboardInputs();

        // check if VKI_show is defined
        openKeyboard("j_password");

        $('#j_password').click(function() {
            try {
                VKI_close();
            } catch(err) {
            }
            openKeyboard("j_password");
        });
        $('#j_username').focus();
    });

    $(window).resize(function() {
        positionKeyboard();
    });