/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import models.Project;
import models.Task;
import models.User;
import play.mvc.Controller;


public class Ajax extends Controller {
	
	public static void userEmails(String term) {
		List<User> users = User.find("byEmailLike", term + "%").fetch();
		List<String> emails = new LinkedList<String>();
		for (User u: users) {
			emails.add(u.email);
		}
		renderJSON(emails);
	}
	
	public static void addTask(long projectid, String task_name) {
		Project p = Project.findById(projectid);
		Task t = new Task(task_name);
		t.save();
		p.tasks.add(0, t);
		p.save();
		renderJSON(t.id);
	}
	
	public static void getTask(long taskid) {
		Task t = Task.findById(taskid);
		renderJSON(t);
	}
	
	public static void editTask(long taskid, Date start, Date finish, boolean completed) {
		Task t = Task.findById(taskid);
		if (t != null) {
			t.start = start;
			t.finish = finish;
			t.completed = completed;
			t.save();
		}
		renderJSON("");
	}
}
