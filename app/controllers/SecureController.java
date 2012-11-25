package controllers;

import models.*;
import controllers.Security;
import java.net.ConnectException;
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
				u.projects = Project.findAll();
			}
			renderArgs.put("user", u);
		}
	}
        
	public static void index() {
		render();
	}
}
