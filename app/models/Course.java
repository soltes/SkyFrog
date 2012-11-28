/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package models;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import play.db.jpa.Model;

@Entity
public class Course extends Model{
	public String name;
	
	@OneToMany
	public List<Project> projects;
	
	public Course(String name) {
		this.name = name;
	}
}
