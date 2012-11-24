package models;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Task extends Model {
	
	@Required
	public String name;
	
	public Date start;
	public Date finish;
	
	@ManyToMany
	public List<User> assigned;
	
	public boolean completed;
	
	public Task(String name, Date from, Date to, boolean completed) {
		this.name = name;
		this.start = from;
		this.finish = to;
		this.completed = completed;
	}
	
	public Task(String name) {
		this.name = name;
		this.start = null;
		this.finish = null;
		this.completed = false;	
	}
	
}
