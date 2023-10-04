package uk.ac.ed.epcc.webapp.servlet;

import java.util.*;
import java.util.Map.Entry;

/** Utils for parsing and editing a Content Security Policy
 * 
 */
public class CSPUtils {
    private final Map<String,String> values;
	public CSPUtils() {
		values = new LinkedHashMap<>();
	}
	public CSPUtils(String policy) {
		this();
		parse(policy);
	}
	public void parse(String policy) {
		values.clear();
		addPolicy(policy);
	}
	protected void addPolicy(String policy) {
		if( policy != null && ! policy.isEmpty()) {
			for( String clause :  policy.split("\\s*;\\s*")) {
				clause = clause.trim();
				String tokens[] = clause.split("\\s+",2);
				String token = tokens[0].toLowerCase();
				String value;
				if( tokens.length > 1) {
					value=tokens[1];
				}else {
					value="";
				}
				if( ! values.containsKey(token)) {
					values.put(token,value);
				}
			}
		}
	}

	public void setClause(String token, String value) {
		token = token.toLowerCase();
		values.put(token, value);
	}
	
	public void clear(String token) {
		values.remove(token.toLowerCase());
	}
	public void merge(CSPUtils donor) {
		values.putAll(donor.values);
	}
	
	public String toString() {
		List<String> clauses = new LinkedList<>();
		for(Entry<String,String> e : values.entrySet()) {
			clauses.add(e.getKey()+" "+e.getValue());
		}
		return String.join(";", clauses);
	}
	public boolean isEmpty() {
		return values.isEmpty();
	}
}
