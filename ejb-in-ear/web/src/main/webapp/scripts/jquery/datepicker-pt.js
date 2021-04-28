jQuery(function($) {
    $.datepicker.regional['pt'] = {clearText: 'Limpar', clearStatus: '',
        closeText: 'Fechar', closeStatus: '',
        prevText: '&lt;Anterior', prevStatus: '',
        nextText: 'Pr&oacute;ximo&gt;', nextStatus: '',
        currentText: 'Hoje', currentStatus: '',
        monthNames: ['Janeiro','Fevereiro','Mar&ccedil;o','Abril','Maio','Junho',
            'Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'],
        monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun',
            'Jul','Ago','Set','Out','Nov','Dez'],
        monthStatus: '', yearStatus: '',
        weekHeader: 'Sm', weekStatus: '',
        dayNames: ['Domingo','Segunda-feira','Ter&ccedil;a-feira','Quarta-feira','Quinta-feira','Sexta-feira','S&aacutebado'],
        dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','Sab'],
        dayNamesMin: ['Dom','Seg','Ter','Qua','Qui','Sex','Sab'],
        dayStatus: 'DD', dateStatus: 'D, M d',
        initStatus: '', isRTL: false,
        showOn: 'both',
        showButtonPanel: true,
        buttonImageOnly: true,
        changeMonth: true,
        changeYear: true};
    $.datepicker.setDefaults($.datepicker.regional['pt']);

});