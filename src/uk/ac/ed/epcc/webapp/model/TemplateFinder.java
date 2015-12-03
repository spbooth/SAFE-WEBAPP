//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.TemplateFile;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.TextFileOverlay.TextFile;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.resource.ResourceService;

/** Class to locate template files
 * 
 * This replaces the built in persistence code from TemplateFile
 * This class caches the templates in the database to allow edits.
 * 
 * In addition template includes (from the same group) are implemented here allowing
 * fragments from other template files to be included using a <b>%%include <i>name</i>%%</b> syntax.
 * 
 * @author spb
 *
 */


public class TemplateFinder {
	private final AppContext conn;
	private final String group;
	private final TextFileOverlay ov;
	Map<String,String> cache = new HashMap<String, String>();
	private final Pattern expand_pattern = Pattern.compile("%%include\\s+([\\w.]+)%%");
	public TemplateFinder(AppContext conn, String group){
		this.conn=conn;
		this.group=group;
		String overlay_table = conn.getInitParameter("table.overlay");
		if( overlay_table != null ){
			TextFileOverlay tmp = new TextFileOverlay(conn,overlay_table);
			if( ! tmp.isValid()){
				tmp=null;
			}
			ov=tmp;
		}else{
			ov=null;
		}
	}
	public TemplateFinder(AppContext conn){
		this(conn,"email.template_directory");
	}
	public TemplateFile getTemplateFile(String name) throws Exception{
		return new TemplateFile(getText(name));
	}
	public String getText(String name)throws Exception{
		if( cache.containsKey(name)){
			return cache.get(name);
		}
		if( ov != null ){
			TextFile tf = ov.find(group,name);
			if( tf != null){
				return expand(name,tf.getData());
			}
			throw new DataNotFoundException("No template file "+group+":"+name);
		}
		ResourceService serv = conn.getService(ResourceService.class);
		String dir=conn.getInitParameter(group);
		if( dir != null ){
			for( String d : dir.split("\\s*,\\s*")){
				String file=d+"/"+name;
				InputStream stream = serv.getResourceAsStream(file);
				if( stream != null ){

					StringBuilder file_buffer = new StringBuilder();
					InputStreamReader f = new InputStreamReader(stream);

					char buffer[] = new char[4096];
					int chars_read = f.read(buffer, 0, 4096);
					while (chars_read != -1) { // while not EOF
						file_buffer.append(buffer, 0, chars_read);

						chars_read = f.read(buffer, 0, 4096);
					}
					return expand(name,file_buffer.toString());

				}
			}
			conn.getService(LoggerService.class).getLogger(getClass()).error("Template "+name+" not found in "+dir);
			return "  no template found "+name+"  ";

		}
		throw new IOException("No template directory specified");
	}
	/** Do include expansion of templates
	 * 
	 * @param name
	 * @param input
	 * @return
	 * @throws Exception 
	 */
	private String expand(String name,String input) throws Exception{
		if( cache.containsKey(name)){
			return cache.get(name);
		}
		// pre-register empty string to avoid circular dependencies
		cache.put(name, "");
		
		StringBuffer sb = new StringBuffer();
		java.util.regex.Matcher m = expand_pattern.matcher(input);
		while( m.find()){
			String content = "";
			try{
				content = getText(m.group(1));
			}catch(Exception e){
				// fall back to empty string but log
				conn.getService(LoggerService.class).getLogger(getClass()).error("Error processing include",e);
			}
			m.appendReplacement(sb, content);
		}
		m.appendTail(sb);
		String result = sb.toString();
		cache.put(name, result);
		return result;
	}
	
	
}