$(document).ready( 
	
function(){
/*! modernizr 3.6.0 (Custom Build) | MIT *
 * https://modernizr.com/download/?-input-setclasses !*/
!function(e,n,t){function s(e,n){return typeof e===n}function a(){var e,n,t,a,o,i,r;for(var c in l)if(l.hasOwnProperty(c)){if(e=[],n=l[c],n.name&&(e.push(n.name.toLowerCase()),n.options&&n.options.aliases&&n.options.aliases.length))for(t=0;t<n.options.aliases.length;t++)e.push(n.options.aliases[t].toLowerCase());for(a=s(n.fn,"function")?n.fn():n.fn,o=0;o<e.length;o++)i=e[o],r=i.split("."),1===r.length?Modernizr[r[0]]=a:(!Modernizr[r[0]]||Modernizr[r[0]]instanceof Boolean||(Modernizr[r[0]]=new Boolean(Modernizr[r[0]])),Modernizr[r[0]][r[1]]=a),f.push((a?"":"no-")+r.join("-"))}}function o(e){var n=c.className,t=Modernizr._config.classPrefix||"";if(u&&(n=n.baseVal),Modernizr._config.enableJSClass){var s=new RegExp("(^|\\s)"+t+"no-js(\\s|$)");n=n.replace(s,"$1"+t+"js$2")}Modernizr._config.enableClasses&&(n+=" "+t+e.join(" "+t),u?c.className.baseVal=n:c.className=n)}function i(){return"function"!=typeof n.createElement?n.createElement(arguments[0]):u?n.createElementNS.call(n,"http://www.w3.org/2000/svg",arguments[0]):n.createElement.apply(n,arguments)}var l=[],r={_version:"3.6.0",_config:{classPrefix:"",enableClasses:!0,enableJSClass:!0,usePrefixes:!0},_q:[],on:function(e,n){var t=this;setTimeout(function(){n(t[e])},0)},addTest:function(e,n,t){l.push({name:e,fn:n,options:t})},addAsyncTest:function(e){l.push({name:null,fn:e})}},Modernizr=function(){};Modernizr.prototype=r,Modernizr=new Modernizr;var f=[],c=n.documentElement,u="svg"===c.nodeName.toLowerCase(),p=i("input"),m="autocomplete autofocus list placeholder max min multiple pattern required step".split(" "),d={};Modernizr.input=function(n){for(var t=0,s=n.length;s>t;t++)d[n[t]]=!!(n[t]in p);return d.list&&(d.list=!(!i("datalist")||!e.HTMLDataListElement)),d}(m),a(),o(f),delete r.addTest,delete r.addAsyncTest;for(var g=0;g<Modernizr._q.length;g++)Modernizr._q[g]();e.Modernizr=Modernizr}(window,document);	
//  if (!Modernizr.inputtypes.date) {

//    jQuery('input[type=date]').datepicker({
//        // Consistent format with the HTML5 picker
//    	//TODO pick up min max dates from attributes if they exist
 //       dateFormat: 'yy-mm-dd'
 //   });
 // }
 
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
