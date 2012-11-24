package controllers;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import play.mvc.*;


import models.*;
import net.sourceforge.rtf.RTFTemplate;
import net.sourceforge.rtf.UnsupportedRTFTemplate;
import net.sourceforge.rtf.helper.RTFTemplateBuilder;

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
	
	public static void onlyUncompletedTasks() {
		renderArgs.put("onlyUncompleted", true);
		renderTemplate("ProjectCtrl/tasks.html");
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
	
	public static void fileUpload(String version, String type, File attachment) {
		String name = attachment.getName();
		MiscFile mf = new MiscFile(name, version, type);
		mf.save();
		File target = new File("public/files/" + mf.id + "_" + mf.filename);
		InputStream in;
		OutputStream out;

		try {
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
			mf.delete();
			renderText("nastala chyba pri uploade suboru");
		}

//		try {
//			Files.copy(attachment.toPath(), target.toPath());
//		} catch (IOException ex) {
//			sf.delete();
//			renderText("nastala chyba pri uploade suboru");
//		}
		Project current = (Project) renderArgs.get("project");
		current.files.add(0, mf);
		current.save();
		renderTemplate("ProjectCtrl/files.html");
	}
	
	public static void templates() {
		render();
	}
		
	public static void templateUpload(File attachment) throws UnsupportedRTFTemplate, FileNotFoundException, Exception {
		String name = attachment.getName();
		Template mf = new Template(name);
		mf.save();
		File target = new File("public/templates/" + mf.id + "_" + mf.filename);
		InputStream in;
		OutputStream out;

		try {
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
			mf.delete();
			renderText("nastala chyba pri uploade suboru");
		}

//		try {
//			Files.copy(attachment.toPath(), target.toPath());
//		} catch (IOException ex) {
//			sf.delete();
//			renderText("nastala chyba pri uploade suboru");
//		}
		Project current = (Project) renderArgs.get("project");
		current.templates.add(0, mf);
		current.save();
		
		//vyrenderuj template
		String renderedFile = "public/rendered/" + target.getName();

		RTFTemplateBuilder builder = RTFTemplateBuilder.newRTFTemplateBuilder();

		// 2. Get RTFtemplate with default Implementation of template engine (Velocity) 
		RTFTemplate rtfTemplate = builder.newRTFTemplate();

		rtfTemplate.setTemplate(target);
		
		rtfTemplate.put("project", current);

		rtfTemplate.merge(new File(renderedFile));
		
		renderTemplate("ProjectCtrl/templates.html");
	}

}