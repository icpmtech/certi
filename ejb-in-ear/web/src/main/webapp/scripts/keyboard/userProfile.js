/*
$(document).ready(function() {

    setTimeout(function() {
        $('#oldPassword').focus();
        hideKeyboardImages();
        positionKeyboard($('#oldPassword + img'));
    }, 100);

    $('#oldPassword').click(function() {
        positionKeyboard($('#oldPassword + img'));
    });
    $('#newPassword').click(function() {
        positionKeyboard($('#newPassword + img'));
    });
    $('#newPasswordConfirm').click(function() {
        positionKeyboard($('#newPasswordConfirm + img'));
    });
});

function positionKeyboard(element) {
    element.click();
    $('#keyboardInputMaster').css("top", "260px");
    $('#keyboardInputMaster').css("left", "800px");
}

function hideKeyboardImages() {
    $('#oldPassword + img').hide();
    $('#newPassword + img').hide();
    $('#newPasswordConfirm + img').hide();
}

*/

function openKeyboard(elementId) {
        VKI_show(document.getElementById(elementId));
        positionKeyboard();
    }

    function positionKeyboard() {
        var position = $("#oldPassword").position();
        var topPosition = position.top;
        var leftPosition = position.left + 410;
        $('#keyboardInputMaster').css("top", topPosition);
        $('#keyboardInputMaster').css("left", leftPosition);
    }

    $(document).ready(function() {

        $('#oldPassword').focus(function(){
            $('#oldPassword').css("background-color", "#FFB900");
            $('#newPassword').css("background-color", "#FFFFFF");
            $('#newPasswordConfirm').css("background-color", "#FFFFFF");
        });
        $('#newPassword').focus(function(){
            $('#newPassword').css("background-color", "#FFB900");
            $('#oldPassword').css("background-color", "#FFFFFF");
            $('#newPasswordConfirm').css("background-color", "#FFFFFF");
        });
        $('#newPasswordConfirm').focus(function(){
            $('#newPasswordConfirm').css("background-color", "#FFB900");
            $('#newPassword').css("background-color", "#FFFFFF");
            $('#oldPassword').css("background-color", "#FFFFFF");});

        VKI_buildKeyboardInputs();

        // check if VKI_show is defined
        openKeyboard("oldPassword");

        $('#oldPassword').click(function() {
            try {
                VKI_close();
            } catch(err) {
            }
            openKeyboard("oldPassword");

        });
        $('#newPassword').click(function() {
            try {
                VKI_close();
            } catch(err) {
            }
            openKeyboard("newPassword");
        });

        $('#newPasswordConfirm').click(function() {
            try {
                VKI_close();
            } catch(err) {
            }
            openKeyboard("newPasswordConfirm");
        });

    });

    $(window).resize(function() {
        positionKeyboard();
    });
