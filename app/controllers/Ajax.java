/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.util.LinkedList;
import java.util.List;
import models.User;
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
}
