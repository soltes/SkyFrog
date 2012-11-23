$(function() {
	var ap = parseInt($("#active_project").val());
	$( "#accordion" ).accordion();
	$( "#accordion" ).accordion( "option", "active", ap );

});