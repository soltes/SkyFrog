/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.rtf.ITemplateEngine;
import net.sourceforge.rtf.RTFTemplate;
import net.sourceforge.rtf.UnsupportedRTFTemplate;
import net.sourceforge.rtf.helper.RTFTemplateBuilder;
import play.mvc.Controller;

/**
 *
 * @author Andrej
 */
public class Template extends Controller {

	public static void pokus() throws UnsupportedRTFTemplate, Exception {
		String rtfSource = "public/rtf/template.rtf";
		String rtfTarget = "public/rtf/output.rtf";
		

		File target = new File(rtfTarget);
		if (target.exists())
			System.out.print("exist");
		
		// 1. Get default RTFtemplateBuilder
		RTFTemplateBuilder builder = RTFTemplateBuilder.newRTFTemplateBuilder();

		// 2. Get RTFtemplate with default Implementation of template engine (Velocity) 
		RTFTemplate rtfTemplate = builder.newRTFTemplate();

		// 3. Set the RTF model source 
		rtfTemplate.setTemplate(new File(rtfSource));
		
		Map prmap = new HashMap();
		prmap.put("Name", "Velocity");
		// 4. Put the context           
		rtfTemplate.put("project", prmap);

		// 5. Merge the RTF source model and the context  
		rtfTemplate.merge(target);
	}
}
