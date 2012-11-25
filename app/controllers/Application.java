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
				u.projects = Project.findAll();
			}
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