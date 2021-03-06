/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import play.db.jpa.Model;

@Entity
public class Project extends Model {
	
	public String name;
	public String desc;
        public String giturl;
	
	@ManyToMany
	public List<User> users;
	
	@OneToMany
	public List<Task> tasks;
	
	@OneToMany
	public List<SourceFile> sources;
	
	@OneToMany
	public List<MiscFile> files;
	
	@OneToMany
	public List<Template> templates;
	
	public Project(String name, String desc) {
		this.name = name;
		this.desc = desc;
		users = new ArrayList<User>();
		tasks = new ArrayList<Task>();
	}
	
	@Override
	public String toString() {
		return name;
	}
}
