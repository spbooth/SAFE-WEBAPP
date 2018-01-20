$(document).ready( 
function(){
  if (!Modernizr.inputtypes.date) {

    jQuery('input[type=date]').datepicker({
        // Consistent format with the HTML5 picker
    	//TODO pick up min max dates from attributes if they exist
        dateFormat: 'yy-mm-dd'
    });
  }
  
  if (!Modernizr.input.list) {
	  jQuery('input[list]').each(function () {
          var availableTags = jQuery('#' + $(this).attr("list")).find('option').map(function () {
              return this.value;
          }).get();
          jQuery(this).autocomplete({ source: availableTags });
       });
  }
});
