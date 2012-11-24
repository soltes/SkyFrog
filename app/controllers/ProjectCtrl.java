package controllers;

import play.*;
import play.mvc.*;


import models.*;

@With(Secure.class)
public class ProjectCtrl extends SecureController {

	@Before
	public static void setUp(long projectid) {
		Project p = Project.findById(projectid);
		renderArgs.put("project", p);
	}
	
    public static void createNew() {
        render();
    }

	public static void addProject(String name, String desc) {
		Project p = new Project(name, desc);
		User u = getConnectedUser();
		p.users.add(u);
		u.projects.add(p);
		p.save();
		u.save();
		Application.index();
	}
    
	public static void people() {
		render();
	}
	
	public static void addUser(String people_autocomplete) {
		User u = User.find("byEmail", people_autocomplete).first();
		if (u != null) {
			Project current = (Project) renderArgs.get("project");
			if (!current.users.contains(u)) {
				current.users.add(u);
				current.save();
			}			
			if (!u.projects.contains(current)) {
				u.projects.add(current);
				u.save();
			}
		}
		renderTemplate("ProjectCtrl/people.html");
	}	
	
	public static void exitUser(long userid) {
		User u = User.findById(userid);
		Project current = (Project) renderArgs.get("project");
		if (current.users.contains(u)) {
			current.users.remove(u);
			current.save();
		}			
		if (u.projects.contains(current)) {
			u.projects.remove(current);
			u.save();
		}
				
		renderTemplate("ProjectCtrl/people.html");
	}
	
	public static void tasks() {
		render();
	}
	
	public static void sources() {
		render();
	}
	
	public static void files() {
		render();
	}
	
	public static void statistics() {
		render();
	}
}