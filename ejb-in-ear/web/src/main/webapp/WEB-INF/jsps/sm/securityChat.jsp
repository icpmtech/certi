<%@ include file="../../../includes/taglibs.jsp" %>

<!--[if IE]>
<style>
.securityChat .chatContent ul li {
width: auto;
}
.popout-chat .chatContent ul li {
width: 100%;
}
.securityChat .chatFooter textarea {
overflow-y: auto;
padding: 0;
}
</style>
<![endif]-->

<div class="securityChat">
    <div class="chatHeader">
        <span><c:out value="${requestScope.actionBean.chatTitle}"/></span>
    </div>
    <div class="chatContent">
        <ul id="chatMessages"></ul>
    </div>
    <div class="chatFooter">
        <div class="chatFooterContainer">
            <textarea id="newMessageText" maxlength="1000"
                    <c:if test="${(!requestScope.actionBean.isUserBasic && !requestScope.actionBean.isValidToken)
                        || requestScope.actionBean.closed}">
                        disabled="disabled"
                    </c:if>></textarea>

            <div class="chatButtonsContainer">
                <c:if test="${requestScope.actionBean.chatPopoutUrl != null}">
                    <a id="popoutButton">
                        <fmt:message key="security.chat.popout"/>
                    </a>
                </c:if>

                <input type="button" class="button" id="sendMessage" value="<fmt:message key="security.chat.send"/>"
                        <c:if test="${(!requestScope.actionBean.isUserBasic && !requestScope.actionBean.isValidToken)
                            || requestScope.actionBean.closed}">
                            disabled="disabled"
                        </c:if>
                />
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function () {
        var lastMessagesString,
                chatTitle = '${requestScope.actionBean.chatTitle}',
                getUrl = '${pageContext.request.contextPath}${requestScope.actionBean.chatGetUrl}',
                postUrl = '${pageContext.request.contextPath}${requestScope.actionBean.chatPostUrl}',
                popoutUrl = '${pageContext.request.contextPath}${requestScope.actionBean.chatPopoutUrl}';

        function setMessages(data) {
            if (lastMessagesString !== data) {
                lastMessagesString = data;

                var result = eval(data), html = '';
                for (var i = 0; i < result.length; i++) {
                    html += '<li><span class="messageInfo">' + result[i].time + ' ' + result[i].user
                            + '</span><span class="messageText">' + result[i].message + '</span></li>';
                }
                var chatMessages = $('#chatMessages');
                chatMessages.html(html);
                chatMessages.scrollTop(chatMessages[0].scrollHeight);
            }
        }

        function getMessages() {
            $.get(getUrl, setMessages);
        }

        function sendMessage() {
            $.post(postUrl, {
                chatMessage: $.trim($('#newMessageText').val()).substring(0, 1000) // substring is used just in case the
                // browser is not able to understand the maxlength
            }, setMessages);
            $('#newMessageText').val('');
        }

        $('#popoutButton').click(function () {
            // source: http://stackoverflow.com/questions/4068373/center-a-popup-window-on-screen
            var dualScreenLeft = typeof window.screenLeft !== 'undefined' ? window.screenLeft : screen.left,
                    dualScreenTop = typeof window.screenTop !== 'undefined' ? window.screenTop : screen.top,
                    width = 350,
                    height = 370,
                    left = (screen.width / 2) - (width / 2) + dualScreenLeft,
                    top = (screen.height / 2) - (height / 2) + dualScreenTop;

            if ($.browser.msie) {
                chatTitle = chatTitle.replace(/-/g, '');
            }

            window.open(popoutUrl,
                    chatTitle, //if a window with this name is already open, it will be selected instead
                    'width=' + width + ', ' +
                    'height=' + height + ', ' +
                    'left=' + left + ', ' +
                    'top=' + top + ', ' +
                    'resizable');

//            clearInterval(intervalId);
//            $('.chatContainer').remove();
        });
        $('#sendMessage').click(function (event) {
            if ($.trim($('#newMessageText').val()).length > 0) {
                event.preventDefault();
                sendMessage();
            }
        });
        $('#newMessageText').keydown(function (event) {
            if (event.keyCode === 13 && !event.shiftKey && $.trim($('#newMessageText').val()).length > 0) {
                event.preventDefault(); // do not allow the caret to move to the next line because the message will be
                // submitted now
            }
        });
        $('#newMessageText').keyup(function (event) {
            if (event.keyCode === 13 && !event.shiftKey && $.trim($('#newMessageText').val()).length > 0) {
                sendMessage();
            }
        });

        setInterval(getMessages, 5000);
        getMessages();
    });
</script>