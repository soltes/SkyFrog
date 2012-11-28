package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	if (Security.isConnected()) {
			User u = SecureController.getConnectedUser();
			if (u.isProfessor) {
				if (session.contains("courseid")) {
					Course c = Course.findById(Long.valueOf(session.get("courseid")));
					u.projects = c.projects.subList(0, c.projects.size());
					renderArgs.put("courseid", session.get("courseid"));
				} else {
					u.projects = Project.findAll();
				}
			}
			List<Course> courses = Course.findAll();
			renderArgs.put("courses", courses);
			renderArgs.put("user", u);
        	renderTemplate("Application/index_logged.html");
        }
        else {
        	render();
        }
    }
   
    public static void addUser(String email, String password, String password2, String fullname) {
        User u = User.find("byEmail", email).first();
        if (password.equals(password2) && u == null) {
            User newUser = new User(email, password, fullname);
            newUser.save();
            renderArgs.put("registerFlash", "Registrácia prebehla úspešne.");
            renderArgs.put("hideForm", true);
        }
        else {
            renderArgs.put("registerFlash", "Nastala chyba pri registrácii.");
        }
        renderTemplate("Application/index.html");
    }

}