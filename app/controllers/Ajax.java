/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.io.File;
import java.util.*;
import javax.xml.bind.JAXBException;
import models.Project;
import models.Task;
import models.Template;
import models.User;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Document;
import play.db.jpa.GenericModel;
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
		HashMap<Object, Object> hm = new HashMap<Object, Object>();
		hm.put("name", t.name);
		hm.put("start", t.start);
		hm.put("finish", t.finish);
		hm.put("completed", t.completed);
		ArrayList<String> users = new ArrayList<String>();
		for (User u: t.assigned) {
			users.add(u.email);
		}
		hm.put("assigned", users);
		renderJSON(hm);
	}
	
	public static void editTask(long taskid, Date start, Date finish, boolean completed, String[] task_assigned) {
		
		Task t = Task.findById(taskid);
		if (t != null) {
			t.start = start;
			t.finish = finish;
			t.completed = completed;
			if (task_assigned != null) {
				t.assigned = new ArrayList<User>();
				for (int i = 0; i < task_assigned.length; i++) {
					User u = User.find("byEmail", task_assigned[i]).first();
					if (u != null) {
						t.assigned.add(u);
					}
				}
			}
			t.save();
		}
		renderJSON("");
	}

}
