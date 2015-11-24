// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Id: TemplateFinder.java,v 1.7 2015/11/05 14:02:15 spb Exp $")

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
			String file=dir+"/"+name;
			InputStream stream = serv.getResourceAsStream(file);
			if( stream != null ){
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
			}else{
				conn.getService(LoggerService.class).getLogger(getClass()).error("Template "+file+" not found");
				return "  no template found "+name+"  ";
			}
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