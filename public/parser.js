var parser = {};

parser.createRowEventTable = function(JSON){
		//var obj = jQuery.parseJSON( '{ "name": "John", "danila":"romaniuk" }' );
	var obj = jQuery.parseJSON(JSON);
	$('#eventsTable tr:last').after("<tr><td>" + obj.typeOfSport + "</td><td>" + obj.date + "</td><td><a href='people_list.html' target='_blank'><span class='glyphicon glyphicon-eye-open'></span></a></td></tr>");
}

parser.createRowPeople = function(JSON){
	var obj = jQuery.parseJSON(JSON);
	$('#personTable tr:last').after("<tr><td>" + obj.nameUser + "</td><td>" + obj.emailUser + "</td></tr>");
}