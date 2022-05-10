$(document).ready( 
function(){
	
  // add a progress bar to show how full each textarea is
  jQuery("textarea[maxlength]").each(function(){
	 
	 var max=$(this).attr('maxlength');
	 var element = document.createElement("progress");
	 element.max=max;
	 element.value=0;
	 element.title="progress towards maximum content size";
	 element.classList.add('textprogress');
	 element.style.display="none";
	 $(this).after(element);
	 $(this).focus(function(){element.style.display="block";});
	 $(this).blur(function(){element.style.display="none";});
	 $(this).on("keyup paste cut",function(){
		 element.value=$(this).val().length;});
  }  
  );

  
}
);
