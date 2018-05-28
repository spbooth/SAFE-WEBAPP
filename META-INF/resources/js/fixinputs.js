$(document).ready( 
function(){
	
  if (!Modernizr.inputtypes.date) {

    jQuery('input[type=date]').datepicker({
        // Consistent format with the HTML5 picker
    	//TODO pick up min max dates from attributes if they exist
        dateFormat: 'yy-mm-dd'
    });
  }
  // add a progress bar to show how full each textarea is
  jQuery("textarea[maxlength]").each(function(){
	 
	 var max=$(this).attr('maxlength');
	 var element = document.createElement("progress");
	 element.max=max;
	 element.value=0;
	 element.title="progress towards maximum content size";
	 element.classList.add('textprogress');
	 $(this).after(element);
	 $(this).on("keyup paste cut",function(){
		 element.value=$(this).val().length;});
  }  
  );
  if (!Modernizr.input.list) {
	  jQuery('input[list]').each(function () {
          var availableTags = jQuery('#' + $(this).attr("list")).find('option').map(function () {
              return this.value;
          }).get();
          jQuery(this).autocomplete({ source: availableTags });
       });
  }
  
}
);
