package uk.ac.ed.epcc.webapp.jdbc.table;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.inputs.SimpleListInput;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
/** Input for Foreign keys from the {@link TableSpecification} that are missing in the database
 * 
 */
public class MissingFkInput extends SimpleListInput<ReferenceFieldType> {

	Map<String,ReferenceFieldType> map = new LinkedHashMap<>();
	public MissingFkInput(Repository res,TableSpecification spec) {
		Map<String,FieldType> fields = spec.getStdFields();
		for(Map.Entry<String,FieldType> e : fields.entrySet()) {
			String field = e.getKey();
			FieldType type = e.getValue();
			if( res.hasField(field) && (type instanceof ReferenceFieldType) ) {
				ReferenceFieldType ref = (ReferenceFieldType) type;
				FieldInfo info = res.getInfo(field);
				if( info.isReference() && ! info.isIndexed() && ref.wantForeighKey()) {
					map.put(field, ref);
				}
			}
			
		}
	}
	
	@Override
	public String getTagByItem(ReferenceFieldType item) {
		for(Map.Entry<String, ReferenceFieldType> w : map.entrySet()) {
			if( item.equals(w.getValue())) {
				return w.getKey();
			}
		}
		return null;
	}

	@Override
	public ReferenceFieldType getItemByTag(String tag) {
		return map.get(tag);
	}

	@Override
	public boolean isValid(ReferenceFieldType item) {
		return item.wantForeighKey();
	}

	@Override
	public Iterator<ReferenceFieldType> getItems() {
		
		return map.values().iterator();
	}

	@Override
	public int getCount() {
		return map.size();
	}

}
