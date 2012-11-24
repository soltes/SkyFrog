
package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

@Entity
public class MiscFile extends Model {
	public String filename;
	public String version;
	public String type;

	public MiscFile(String filename, String version, String type) {
		this.filename = filename;
		this.version = version;
		this.type = type;
	}

}
