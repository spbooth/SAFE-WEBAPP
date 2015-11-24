// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp;

import java.util.Map;

import uk.ac.ed.epcc.webapp.content.Table;

/** Created documentation tables of configured classes.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ClassTableCreator.java,v 1.2 2014/09/15 14:30:11 spb Exp $")
public class ClassTableCreator {
	private final AppContext conn;

	/**
	 * @param c 
	 * 
	 */
	public ClassTableCreator(AppContext c) {
		this.conn=c;
	}
	public <T> Table getList(Class<T> template,String path){
		Table<String,String> t = new Table<String,String>();
		String prefix = AppContext.CLASS_PREFIX;
		if( path != null && path.length() > 0){
			prefix=prefix+path+".";
		}
		Map<String,String> tagmap = conn.getInitParameters(prefix);
		for(String s : tagmap.keySet()){

			try{
				String tag = s.substring(prefix.length());
				// Only single string tags
				if( ! tag.contains(".")){
					Class<? extends T> cand = conn.getClassDef(template, tagmap.get(s));
					if( cand != null && (template == null||template.isAssignableFrom(cand) )){
						t.put("Name", tag,tag); // use column as may be only column
						if( cand.isAnnotationPresent(Description.class)){
							t.put("Description", tag, cand.getAnnotation(Description.class).value());
						}
						if( Targetted.class.isAssignableFrom(cand)){
							T targ = conn.makeObject(cand);
							if( targ != null ){
								t.put("Target", tag, ((Targetted)targ).getTarget().getSimpleName());
							}
						}
					}
				}
			}catch(Throwable tr){
				conn.error(tr,"Error making documentation table for "+s);
			}
		}
		
		return t;
	}
}

