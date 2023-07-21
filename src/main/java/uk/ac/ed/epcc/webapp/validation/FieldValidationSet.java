package uk.ac.ed.epcc.webapp.validation;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory.DataObjectFieldValidator;
/** A Set of {@link FieldValidator}s
 * 
 * @param <T>
 */
public class FieldValidationSet<T> extends LinkedHashSet<FieldValidator<T>> {

	/** A {@link FieldValidationVisitor} to add new {@link FieldValidator}s to
	 * a {@link FieldValidationSet}.
	 * This includes special handling for merging validators.
	 * 
	 */
	public class AddVisitor implements FieldValidationVisitor<Object,T> {
		@Override
		public Object visitGenericFieldValidator(FieldValidator<T> val) {
			add(val);
			return null;
		}

		@Override
		public Object visitMaxValueValidator(MaxValueValidator val) {
			FieldValidationSet set = (FieldValidationSet)FieldValidationSet.this;
			MaxValueValidator add = (MaxValueValidator)val;
			for(Iterator<FieldValidator<Comparable>> it = set.iterator(); it.hasNext() ;) {
				FieldValidator<Comparable> v = it.next();
				if( v instanceof MaxValueValidator) {
					MaxValueValidator<Comparable> m = (MaxValueValidator<Comparable>) v;
					if( m.dominates(add)) {
						add = m;
					}else {
						it.remove();
					}
				}
			}
			set.add(add);
			return null;
		}

		@Override
		public Object visitMinValueValidator(MinValueValidator val) {
			FieldValidationSet set = (FieldValidationSet)FieldValidationSet.this;
			MinValueValidator add = (MinValueValidator)val;
			for(Iterator<FieldValidator<Comparable>> it = set.iterator(); it.hasNext() ;) {
				FieldValidator<Comparable> v = it.next();
				if( v instanceof MinValueValidator) {
					MinValueValidator<Comparable> m = (MinValueValidator<Comparable>) v;
					if( m.dominates(add)) {
						add = m;
					}else {
						it.remove();
					}
				}
			}
			set.add(add);
			return null;
		}
		
		@Override
		public Object visitMaxLengthValidator(MaxLengthValidator add) {
			FieldValidationSet set = (FieldValidationSet)FieldValidationSet.this;
			
			for(Iterator<FieldValidator<String>> it = set.iterator(); it.hasNext() ;) {
				FieldValidator<String> v = it.next();
				if( v instanceof MaxLengthValidator) {
					MaxLengthValidator m = (MaxLengthValidator) v;
					if( m.dominates(add)) {
						add = m;
					}else {
						it.remove();
					}
				}
			}
			set.add(add);
			return null;
		}

		@Override
		public Object visitDataObjectFieldValidator(DataObjectFieldValidator val) {
			FieldValidationSet set = (FieldValidationSet)FieldValidationSet.this;
			val.addTo(set);
			return null;
		}
	}

	public static <T> FieldValidationSet<T> merge(FieldValidationSet<T> a, FieldValidationSet<T> b){
		if( a == null ) {
			return b;
		}
		if( b != null ) {
			// add with merge
			for(FieldValidator<T> v : b) {
				a.addValidator(v);
			}
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
	/** Add {@link FieldValidator} with merge
	 * 
	 * @param val
	 */
	public void addValidator(FieldValidator<T> val) {
		val.accept(new AddVisitor());
	}
}
