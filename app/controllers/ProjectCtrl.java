package controllers;

import play.*;
import play.mvc.*;


import models.*;

@With(Secure.class)
public class ProjectCtrl extends SecureController {

	@Before
	public static void setUP(long projectid) {
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
    
	public static void people(long id) {
		render();
	}
}