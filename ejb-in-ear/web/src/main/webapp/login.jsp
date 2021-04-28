<% // This jsp is only used in SPNEGO authentication as a fallback mechanism for browsers that don't send Kerberos headers %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Window-target" content="_top"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>

    <title>
        Iniciar Sess&atilde;o
    </title>

    <!-- Application Icon-->
    <link rel="Shortcut Icon" href="/favicon.ico"/>

    <!-- CSS Imports-->
    <link rel="stylesheet" type="text/css" href="/styles/default-style.css"/>
    <link rel="stylesheet" type="text/css" href="/styles/keyboard/keyboard.css"/>
    <link rel="stylesheet" type="text/css" href="/styles/homeMenu.css"/>

    <script type="text/javascript" src="/scripts/jquery/jquery-1.3.2.min.js"></script>

</head>
<body>

<div id="frame">

    <div id="header">

        <div class="content">
            <div class="floatLeft alignLeft" style="width: 280px">
                <img width="280" height="64" alt="CertiTools"
                                                        src="/images/logo-home_pt.gif"
                                                        style="margin-top: 5px;"/>
            </div>
            <div class="floatRight alignRight" style="width: 690px">
                <div class="locale">


                </div>
                <div class="alignRight">

                    <div class="cleaner"><!--Do not remove this empty div--></div>
                </div>
            </div>
            <div class="cleaner"><!--Do not remove this empty div--></div>
        </div>

    </div>


    <div id="security-banner">
        <div class="content">
            <div class="floatLeft alignLeft">
                <div class="title">Iniciar Sess&atilde;o</div>
                <div class="subtitle">Aceda ao CertiTools</div>
            </div>
            <div class="security-img"><img src="/images/Login-Image.png" alt=""></div>
        </div>
    </div>

    <div id="home-content">
        <div class="sep"><!--Do not remove this empty div--></div>

        <form action="j_security_check" method="post">
            <div id="form">
                <div class="homeContentLeft">






                    <div id="infoMsg">
                        Introduza o seu login e sua password
                    </div>
                    <div class="cleaner"><!--do not remove this empty div--></div>
                    <div>
                        <div class="row">
                            <div class="title">Login:</div>
                            <div class="input">
                                <input type="text" name="j_username" id="j_username" class="loginInput"/>
                            </div>
                            <div class="cleaner"><!--do not remove this empty div--></div>
                        </div>
                        <div class="row">
                            <div class="title">Palavra-chave:</div>
                            <div class="input" style="height: 20px">
                                <input type="password" name="j_password" id="j_password" class="loginInput" />
                            </div>
                            <div class="cleaner"><!--do not remove this empty div--></div>
                        </div>

                        <div class="cleaner"><!--do not remove this empty div--></div>

                        <div class="buttons">
                            <input type="submit" value="Submeter" class="button"/>
                        </div>
                    </div>
                </div>

                <div class="cleaner"><!--do not remove this empty div--></div>
            </div>
        </form>
    </div>


</div>

<div id="footer">
    <div class="content">
        <div class="alignLeft floatLeft">


        </div>
        <div class="floatRight alignRight">
            &copy; 2009 <a href ="http://www.certitecna.pt/" onclick="window.open('http://www.certitecna.pt'); return false"><strong style="font-size: 14px">Certitecna S.A.</strong></a> todos os direitos reservados.
            <br/>
            Desenvolvido por:
            <a class="img" href="http://www.criticalsoftware.com"
               onclick="window.open('http://www.criticalsoftware.com'); return false">
                <img alt="Critical Software"
                     src="/images/critical-logo.png"
                     style="padding-top: 3px; vertical-align: text-bottom;"/></a>
        </div>
        <div class="cleaner"><!--Do not remove this empty div--></div>
    </div>
</div>
</body>
</html>