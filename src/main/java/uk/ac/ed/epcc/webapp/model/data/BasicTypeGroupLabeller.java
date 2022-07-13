package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Labeller;

/** A simple {@link Labeller} that extracts the optional group string from a {@link BasicType.Value}
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class BasicTypeGroupLabeller<T extends BasicType.Value> implements Labeller<T, String> {

	public BasicTypeGroupLabeller() {
	}

	@Override
	public Class<String> getTarget() {
		return String.class;
	}

	@Override
	public String getLabel(AppContext conn, T key) {
		String group = key.getGroup();
		if( group == null) {
			group="NoGroup";
		}
		return group;
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof BasicType.Value;
	}

}
