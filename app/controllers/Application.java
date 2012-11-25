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

}