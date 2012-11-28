
package models;

import javax.persistence.Entity;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class MiscFile extends Model {
	public String name;
	public String version;
	public String type;
	public Blob file;


}
