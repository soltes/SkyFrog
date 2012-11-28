package controllers;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import play.mvc.*;


import models.*;
import net.sourceforge.rtf.RTFTemplate;
import net.sourceforge.rtf.UnsupportedRTFTemplate;
import net.sourceforge.rtf.helper.RTFTemplateBuilder;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

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

	public static void sourceUpload(String giturl) {
                Project current = (Project) renderArgs.get("project");
                current.giturl = giturl;
                current.save();
                
                File tmpGitDir = new File("tmp/gitsource/" + current.id + "_" + current.name);
                if (tmpGitDir.exists()) {
                    try {
                        FileUtils.deleteDirectory(tmpGitDir);
                    } catch (IOException ex) {
                        renderArgs.put("FlashMsg", "Nastala chyba pri sťahovaní súborov z git repository.");
                    }
                }
                
                File zipFile = new File("public/sources/" + current.id + "_" + current.name + ".zip");
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
                        renderArgs.put("FlashMsg", "Nastala chyba pri sťahovaní súborov z git repository.");
                    }
                    
                    
                } catch (IOException ex) {
                    renderArgs.put("FlashMsg", "Nastala chyba pri sťahovaní súborov z git repository.");
                }
                try {
                    ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream("public/sources/" + current.id + "_" + current.name + ".zip"));
                    zipDir("tmp/gitsource/" + current.id + "_" + current.name, zipOutput);
                    zipOutput.close();
                } catch (Exception e) {
                    renderArgs.put("FlashMsg", "Nastala chyba pri vytváraní ZIP archívu.");
                }
                
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
			//renderText("nastala chyba pri uploade suboru");
                        renderText(ex.getMessage());
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
            }
            catch(Exception e) {
                renderArgs.put("FlashMsg", "Nastala chyba pri vytváraní ZIP archívu.");
            }
        }
}