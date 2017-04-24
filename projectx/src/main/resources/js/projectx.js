(function ($) { // this closure helps us keep our variables to ourselves.
// This pattern is known as an "iife" - immediately invoked function expression
 
    // form the URL
	
    var url = AJS.contextPath() + "/rest/projectx/1.0/";
    var fieldsToUpdate = new Object();
    // wait for the DOM (i.e., document "skeleton") to load. This likely isn't necessary for the current case,
    // but may be helpful for AJAX that provides secondary content.
    $(document).ready(function() {
    	
    	$("button").click( function( ) {
    		//TODO: Sending cumulative checkbox update to server
        } );
    	$(".hider").click( function( ) {
    		fieldsToUpdate[this.id] = this.checked;
    		alert(JSON.stringify(fieldsToUpdate));
    		
        } );
    });

})(AJS.$ || jQuery);

function updateConfig() {
// form the URL
	
	var url = AJS.contextPath() + "/rest/projectx/1.0/";
	  AJS.$.ajax({
	    url: url,
	    type: "PUT",
	    contentType: "application/json",
	    data: '{ "name": "' + AJS.$("#name").attr("value") + '", "time": ' +  AJS.$("#time").attr("value") + ' }',
	    processData: false
	  });
	}

function storeFieldValueChange() {
// stores values of fields to be updated	
	
}