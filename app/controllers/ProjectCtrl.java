package controllers;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Level;
import play.*;
import play.mvc.*;


import models.*;
import org.apache.commons.io.IOUtils;

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

	public static void sourceUpload(String version, File attachment) {
		String name = attachment.getName();
		SourceFile sf = new SourceFile(name, version);
		sf.save();
		File target = new File("public/sourcefiles/" + sf.id + "_" + sf.filename);
		InputStream in;
		OutputStream out;

		try {
//			FileInputStream is = new FileInputStream(attachment); 
//            String original = "/data/" + attachment.getName();
//            IOUtils.copy(is, new FileOutputStream(Play.getFile(original))); 
			in = new FileInputStream(attachment.getAbsolutePath());
			out = new FileOutputStream(target.getAbsolutePath());
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException ex) {
			sf.delete();
			renderText("nastala chyba pri uploade suboru");
		}

//		try {
//			Files.copy(attachment.toPath(), target.toPath());
//		} catch (IOException ex) {
//			sf.delete();
//			renderText("nastala chyba pri uploade suboru");
//		}
		Project current = (Project) renderArgs.get("project");
		current.sources.add(0, sf);
		current.save();
		renderTemplate("ProjectCtrl/sources.html");
	}
}