package models;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import play.db.jpa.Model;

@Entity
public class Task extends Model {
	
	public String name;
	
	public Date from;
	public Date to;
	
	@ManyToMany
	public List<User> assigned;
	
	public boolean completed;
	
	public Task(String name, Date from, Date to, boolean completed) {
		this.name = name;
		this.from = from;
		this.to = to;
		this.completed = completed;
	}
	
}
