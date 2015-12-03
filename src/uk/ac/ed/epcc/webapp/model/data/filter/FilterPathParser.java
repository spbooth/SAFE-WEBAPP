//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.util.LinkedList;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseInput;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.forms.SQLMatcher;

/** A class to parse a filter encoded as a path.
 * 
 * Path elements can be <b>AND(</b>, <b>OR(</b>, <b>)</b> which start and terminate grouping.
 * or they can be of the form <b><em>name</em>=<em>value</em></b>. The value is parsed according to the rules of  the corresponding
 * form field then converted to filter using either one of the provided {@link SQLMatcher}s or a {@link SQLValueFilter}. 
 * @author spb
 * @param <T> type of filter generated
 *
 */

public class FilterPathParser<T extends DataObject> {
	/**
	 * @param res Repository
	 * @param f {@link Form} to handle parsing
	 * @param matchers Map of {@link SQLMatcher}s
	 * @param target target class of filter.
	 */
	public FilterPathParser(Repository res, Form f, Map<String,SQLMatcher<T>> matchers, Class<? super T> target) {
		super();
		this.res = res;
		this.f=f;
		this.matchers = matchers;
		this.target = target;
	}
	private Repository res;
	private Map<String,SQLMatcher<T>> matchers;
	private Class<? super T> target;
	private Form f;
	
	
	public SQLFilter<T> parseFilter(LinkedList<String> path) throws ParseException{
		return parseFilter(new SQLAndFilter<T>(target),path);
	}
	/** parse the path generating a {@link SQLFilter}
	 * 
	 * The path list is consumed as part of the parse.
	 * 
	 * @param base
	 * @param path
	 * @return {@link SQLFilter}
	 * @throws ParseException 
	 */
	public SQLFilter<T> parseFilter(BaseSQLCombineFilter<T> base, LinkedList<String> path) throws ParseException{
		while(! path.isEmpty()){
			String p = path.removeFirst();
			int index = p.indexOf('=');
			if( p.equals(")")){
				return base;
			}else if( p.equals("AND(")){
				base.addFilter(parseFilter(new SQLAndFilter<T>(target), path));
			}else if( p.equals("OR(")){
				base.addFilter(parseFilter(new OrFilter<T>(target), path));
			}else if( index > 0  && index < (p.length()-1)){
				String name=p.substring(0, index);
				String value=p.substring(index+1);
				Object obj = value;
				Input i = f.getInput(name);
				if( i != null ){
					if( i instanceof ParseInput){
						((ParseInput)i).parse(value);
						obj = i.getValue();
					}else{
						obj = i.convert(value);
					}
				}
				if( matchers != null && matchers.containsKey(name)){
					SQLMatcher<T> m = matchers.get(name);
					base.addFilter(m.getSQLFilter(target, res, name, obj));
				}else if( res.hasField(name)){
					base.addFilter(new SQLValueFilter<T>(target, res, name, obj));
				}else{
					throw new ParseException("Invalid field "+name);
				}
			}else{
				throw new ParseException("illegal path element "+p);
			}
		}
		
		return base;
	}

}