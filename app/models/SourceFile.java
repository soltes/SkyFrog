
package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

@Entity
public class SourceFile extends Model {
	public String filename;
	public String version;


}
