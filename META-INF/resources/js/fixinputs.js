$(document).ready( function(){if (!Modernizr.inputtypes.date) {
    jQuery('input[type=date]').datepicker({
        // Consistent format with the HTML5 picker
        dateFormat: 'yy-mm-dd'
    });
}
});
