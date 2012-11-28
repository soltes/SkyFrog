package controllers;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.JAXBException;
import models.*;
import org.apache.commons.io.FileUtils;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Document;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import play.db.jpa.Blob;
import play.libs.MimeTypes;
import play.mvc.Before;
import play.mvc.With;

@With(Secure.class)
public class ProjectCtrl extends SecureController {

	@Before
	public static void setUp(long projectid) {
		Project p = Project.findById(projectid);
		renderArgs.put("project", p);
	}
	
	public static void profSelectCourse(long courseid) {
		session.put("courseid", courseid);
		Application.index();
	}

	public static void createNew() {
		render();
	}

	public static void createNewCourse() {
		render();
	}
	
	public static void addCourse(String name) {
		Course c = new Course(name);
		c.save();
		Application.index();
	}
	
	public static void addProject(String name, String desc, long course) {
		Project p = new Project(name, desc);
		User u = getConnectedUser();
		p.save();
		u.projects.add(p);
		u.save();
		p.users.add(u);
		p.save();
		Course c = Course.findById(course);
		if (c.projects == null) {
			c.projects = new ArrayList<Project>();
		}
		c.projects.add(p);
		c.save();
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
		Project current = (Project) renderArgs.get("project");
		File zipSource = new File("public/sources/" + current.id + "_" + current.name + ".zip");
		if (zipSource.exists()) {
			renderArgs.put("zipFile", zipSource.getPath());
		}
		render();
	}

	public static void files() {
		render();
	}

	public static void statistics() {
		render();
	}

	public static void sourceUpload(String giturl, String version) {
		Project current = (Project) renderArgs.get("project");
		current.giturl = giturl;
		
		SourceFile sf = new SourceFile();
		sf.version = version;
		sf.save();
		//sf.id je definovane az po save()
		sf.filename = sf.id + "_" + current.name + ".zip";
		sf.save();
		
		File tmpGitDir = new File("tmp/gitsource/" + sf.filename + "/");
		if (tmpGitDir.exists()) {
			try {
				FileUtils.deleteDirectory(tmpGitDir);
			} catch (IOException ex) {
				renderArgs.put("FlashMsg", "Nastala chyba pri sťahovaní súborov z git repository1.");
			}
		}

		File zipFile = new File("public/sources/" + sf.filename);
		if (zipFile.exists()) {
			zipFile.delete();
		}

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			Repository repository = builder.setGitDir(tmpGitDir).readEnvironment().findGitDir().build();

			Git git = new Git(repository);

			CloneCommand clone = new CloneCommand().setBare(false).setCloneAllBranches(true).setDirectory(tmpGitDir).setURI(giturl);
			try {
				clone.call();
			} catch (GitAPIException ex) {
				System.out.println(ex);
				renderArgs.put("FlashMsg", "Nastala chyba pri sťahovaní súborov z git repository2.");
			}


		} catch (IOException ex) {
			renderArgs.put("FlashMsg", "Nastala chyba pri sťahovaní súborov z git repository3.");
		}
		try {
			ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream("public/sources/" + sf.filename));
			zipDir("tmp/gitsource/" + sf.filename, zipOutput);
			zipOutput.close();
		} catch (Exception e) {
			renderArgs.put("FlashMsg", "Nastala chyba pri vytváraní ZIP archívu.");
		}
		
		current.sources.add(0, sf);
		current.save();
		renderTemplate("ProjectCtrl/sources.html");
	}

	public static void fileUpload(MiscFile s, File file) {
		s.name = file.getName();
		s.file = new Blob();
		try {
			s.file.set(new FileInputStream(file), MimeTypes.getContentType(file.getName()));
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ProjectCtrl.class.getName()).log(Level.SEVERE, null, ex);
		}
		s.save();
		Project current = (Project) renderArgs.get("project");
		current.files.add(0, s);
		current.save();
		renderTemplate("ProjectCtrl/files.html");
	}
	
	public static void downloadFile(long id) {
		MiscFile mf = MiscFile.findById(id);  
		java.io.InputStream binaryData = mf.file.get();
		renderBinary(binaryData);
	}
	
	public static void templates() {
		render();
	}
		
	public static void templateUpload(File attachment) throws FileNotFoundException, Exception {
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

		Project current = (Project) renderArgs.get("project");
		current.templates.add(0, mf);
		current.save();

		renderTemplate("ProjectCtrl/templates.html");
	}
        
	private static void zipDir(String dir, ZipOutputStream output) {
		try {
			File zipDir = new File(dir);
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(zipDir, dirList[i]);
				if (f.isDirectory()) {
					String filePath = f.getPath();
					zipDir(filePath, output);
					continue;
				}
				FileInputStream input = new FileInputStream(f);
				ZipEntry entry = new ZipEntry(f.getPath());
				output.putNextEntry(entry);
				while ((bytesIn = input.read(readBuffer)) != -1) {
					output.write(readBuffer, 0, bytesIn);
				}
				input.close();
			}
		} catch (Exception e) {
			renderArgs.put("FlashMsg", "Nastala chyba pri vytváraní ZIP archívu.");
		}
	}
		
	public static void render(long templid) throws Docx4JException, JAXBException {
		Template t = Template.findById(templid);
		Project project = (Project) renderArgs.get("project");
		
		String outputfilepath = System.getProperty("user.dir") + "/public/rendered/" + t.filename;

		// Open a document from the file system
		// 1. Load the Package
		File templfile = new File("public/templates/" + t.id + "_" + t.filename);
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templfile);

		// 2. Fetch the document parkt 		
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

		org.docx4j.wml.Document wmlDocumentEl = (org.docx4j.wml.Document) documentPart.getJaxbElement();

		//xml --> string
		String xml = XmlUtils.marshaltoString(wmlDocumentEl, true);

		HashMap<String, String> mappings = new HashMap<String, String>();

		mappings.put("name", project.name);
		mappings.put("description", project.desc);

		//valorize template
		Object obj = XmlUtils.unmarshallFromTemplate(xml, mappings);

		//change  JaxbElement
		documentPart.setJaxbElement((Document) obj);

		SaveToZipFile saver = new SaveToZipFile(wordMLPackage);
		saver.save(outputfilepath);
		
		renderBinary(new File(outputfilepath));
	}
}