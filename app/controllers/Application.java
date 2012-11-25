package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	if (Security.isConnected()) {
    		renderArgs.put("user", SecureController.getConnectedUser());
        	renderTemplate("Application/index_logged.html");
        }
        else {
        	render();
        }
    }

}