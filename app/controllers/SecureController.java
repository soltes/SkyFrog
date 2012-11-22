package controllers;


import controllers.Security;
import java.net.ConnectException;
import models.User;
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
			renderArgs.put("user", getConnectedUser());
		}
	}

	public static void index() {
		render();
	}
}
