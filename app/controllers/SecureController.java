package controllers;

import models.*;
import controllers.Security;
import java.net.ConnectException;
import java.util.List;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class SecureController extends Controller {
	
	static User getConnectedUser() {
		return User.find("byEmail", Security.connected()).first();
	}
	
	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User u = getConnectedUser();
			if (u.isProfessor) {
				if (session.contains("courseid")){
					Course c = Course.findById(Long.valueOf(session.get("courseid")));
					u.projects = c.projects.subList(0, c.projects.size());
				} else {
					u.projects = Project.findAll();
				}
			}
			List<Course> courses = Course.findAll();
			renderArgs.put("courses", courses);
			renderArgs.put("user", u);
		}
	}
        
	public static void index() {
		render();
	}
}
