//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp;

import java.util.Map;

import uk.ac.ed.epcc.webapp.content.Table;

/** Created documentation tables of configured classes.
 * @author spb
 *
 */
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
			}catch(Exception tr){
				conn.error(tr,"Error making documentation table for "+s);
			}
		}
		
		return t;
	}
}