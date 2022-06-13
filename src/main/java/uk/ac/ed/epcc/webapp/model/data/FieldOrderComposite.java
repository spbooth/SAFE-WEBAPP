package uk.ac.ed.epcc.webapp.model.data;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class FieldOrderComposite<BDO extends DataObject> extends Composite<BDO, FieldOrderComposite> {

	public FieldOrderComposite(DataObjectFactory<BDO> fac) {
		super(fac);
	}

	@Override
	protected Class<? super FieldOrderComposite> getType() {
		return (Class<? super FieldOrderComposite>) getClass();
	}

	@Override
	public Map<String, FieldConstraint> addFieldConstraints(Map<String, FieldConstraint> constraints) {
		String list = getContext().getInitParameter(getFactory().getConfigTag()+".field_stages");
		if( list != null) {
			Set<String> fields = new LinkedHashSet<>();
			Repository res = getRepository();
			for(String f : list.split("\\s*,\\s*")) {
				if( res.hasField(f)) {
					fields.add(f);
				}
			}
			String prev = null;
			for(String f : fields) {
				if( prev != null) {
					constraints.put(f,FieldConstraint.add(constraints.get(f), new OrderFieldConstraint(prev)));
				}
				prev=f;
			}
		}
		return constraints;
	}

}
