jQuery(function($) {
    $.datepicker.regional['en'] = {
        clearText: 'Clean', clearStatus: '',
        closeText: 'Close', closeStatus: '',
        prevText: '&lt;Previous', prevStatus: '',
        nextText: 'Next&gt;', nextStatus: '',
        currentText: 'Current Day', currentStatus: '',
        monthNames: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
        monthNamesShort: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
        monthStatus: '', yearStatus: '',
        weekHeader: 'Week', weekStatus: '',
        dayNames: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
        dayNamesShort: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
        dayNamesMin: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
        dayStatus: 'DD', dateStatus: 'D, M d',
        initStatus: '', isRTL: false,
        showOn: 'both',
        showButtonPanel: true,
        buttonImageOnly: true,
        changeMonth: true,
        dateFormat: 'dd-mm-yy',
        changeYear: true};
    $.datepicker.setDefaults($.datepicker.regional['en']);

});