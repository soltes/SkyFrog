/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import play.db.jpa.Model;

@Entity
public class Project extends Model {
	
	public String name;
	public String desc;
	
	@ManyToMany
	public List<User> users;
	
	@OneToMany
	public List<Task> tasks;
	
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
