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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** A SQLValue that maps regexp patterns to canonical strings.
 * 
 * @author spb
 *
 */


public class SQLValuePatternTransform implements SQLValue<String> {
	private AppContext conn;
	SQLValue<String> base;
    Map<Pattern,String> map;
    public SQLValuePatternTransform(AppContext conn, String tag, SQLValue<String> base){
    	this.conn=conn;
    	this.base=base;
    	map = new HashMap<Pattern,String>();
    	Logger log = conn.getService(LoggerService.class).getLogger(getClass());
    	String prefix = "SQLValuePatternTransform."+tag+".";
		Map<String,String> conf = conn.getInitParameters(prefix);
		
		if( conf != null ){
			log.debug("Found "+conf.size()+" patterns");
			for(String name : conf.keySet()){
				String value=name.substring(prefix.length());
				log.debug(conf.get(name)+" maps to "+value);
				Pattern p = Pattern.compile(conf.get(name));
				map.put(p,value);
			}
		}else{
			log.debug("Null pattern map found");
		}
    }


   

	public AppContext getContext() {
		return conn;
	}


	public int add(StringBuilder sb, boolean qualify) {
		base.add(sb, qualify);
		return 1;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return base.getParameters(list);
	}
	

	public Class<? super String> getTarget() {
		return String.class;
	}


	public String makeObject(ResultSet rs, int pos) throws DataException, SQLException {
		String s = base.makeObject(rs, pos);
		for(Pattern p :map.keySet()){
			if( p.matcher(s).matches() ){
				return map.get(p);
			}
		}
		return s;
	}




	public SQLFilter getRequiredFilter() {
		return null;
	}
}