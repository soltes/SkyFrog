$(function() {
	var ap = parseInt($("#active_project").val());
	$( "#accordion" ).accordion();
	$( "#accordion" ).accordion( "option", "active", ap );

	var cache = {};
	$( "#people_autocomplete" ).autocomplete({
		minLength: 2,
		source: function( request, response ) {
			var term = request.term;
			if ( term in cache ) {
				response( cache[ term ] );
				return;
			}
console.log(request)
			$.getJSON( "/useremails", request, function( data, status, xhr ) {
				cache[ term ] = data;
				response( data );
			});
		}
	});
});