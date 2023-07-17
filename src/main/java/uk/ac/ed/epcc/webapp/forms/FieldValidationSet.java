package uk.ac.ed.epcc.webapp.forms;

import java.util.LinkedHashSet;
import java.util.Map;
/** A Set of {@link FieldValidator}s
 * 
 * @param <T>
 */
public class FieldValidationSet<T> extends LinkedHashSet<FieldValidator<T>> {

	
	public static <T> FieldValidationSet<T> merge(FieldValidationSet<T> a, FieldValidationSet<T> b){
		if( a == null ) {
			return b;
		}
		if( b != null ) {
			a.addAll(b);
		}
		return a;
		
	}
	public static <T> FieldValidationSet<T> add(Map<String,FieldValidationSet> map, String name, FieldValidator<T> val){
		FieldValidationSet<T> prev = map.get(name);
		if( prev == null) {
			prev = new FieldValidationSet<>();
			map.put(name, prev);
		}
		prev.add(val);
		return prev;
	}
}
