
package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

@Entity
public class Template extends Model {
	public String filename;

	public Template(String filename) {
		this.filename = filename;
	}

}
