$(function() {
	var ap = parseInt($("#active_project").val());
	var projectid = parseInt($("#project_id").val());
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

			$.getJSON( "/useremails", request, function( data, status, xhr ) {
				cache[ term ] = data;
				response( data );
			});
		}
	});

	var templ = '\
		<div class="well well-small task-name">\
			<a class="pull-left" href="#" onClick="task_click(\'<%= id %>\')"><%= name %><i class="pull-right icon-forward"></i></a>\
		</div>';
	$("#task-create").on('click', function (ev) {
		ev.preventDefault();
		var task_name = $("#task-input").val();
		$.ajax("/project/" + projectid + "/addtask/" + task_name, {
			success: function (data, status, xhr) {
				var t = _.template(templ, {name: task_name, id:data});
				console.log(t);
				$(t).prependTo("#task-list");
			}
		});
		return false;
	});

	var types = [
		"Screenshot",
		"Test",
		"Video",
		"Dokumentácia",
		"Používateľská príučka"
	]
	$("#type").autocomplete({ minLength: 0, source: types });


	$("#task-start").datepicker();
	$("#task-start").datepicker( "option", "dateFormat", "yy-mm-dd" );
	$("#task-finish").datepicker();
	$("#task-finish").datepicker( "option", "dateFormat", "yy-mm-dd" );
});

var selected_task = -1;

function task_click(taskid) {
	selected_task = taskid;
	$("#task-edit").fadeIn();
	$.getJSON('/gettask', {taskid: taskid}, function (task) {console.log(task)
		$("#task-name").val(task.name);
		if (task.start) {
			var start = new Date(task.start);
			$("#task-start").val(start.toISOString().slice(0, 10));
		} else {
			$("#task-start").val('');
		}
		if (task.finish) {
			var finish = new Date(task.finish);
			$("#task-finish").val(finish.toISOString().slice(0, 10));
		} else {
			$("#task-finish").val('');
		}
		$("#task_assigned > option").map(function () {
			$(this).removeAttr("selected");
		});
		if (task.assigned) {
			for (var i = 0; i < task.assigned.length; i++) {
				var t = task.assigned[i];
				$("#task_assigned > option").map(function () {
					if ($(this).val() === t) {
						$(this).attr("selected", "selected");
					}
				})
			}
		}
		$("#task-completed").prop('checked', task.completed);
	});
	return false;
}

function task_edit() {
	if (selected_task !== -1) {
		var query = {
			taskid: selected_task,
			name: $("#task-name").val(),
			start: $("#task-start").val(),
			finish: $("#task-finish").val(),
			completed: $("#task-completed:checked").val() ? true : false,
			task_assigned: $("#task_assigned").val()
		}

		$.getJSON('/edittask', query, function (data, status, xhr) {
			$("#task-edit-success").fadeIn();
			setTimeout(function () {
				$("#task-edit-success").fadeOut();
			}, 1000);
		});
	}
	return false;
}