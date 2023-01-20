package uk.ac.ed.epcc.webapp.model.lifecycle;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
/** An extended form of {@link ActionList} that implements {@link LifeCycleListener}
 * 
 * It can contain a mixture of {@link ActionListener}s and {@link LifeCycleListener}s
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class ActionListAdaptor<T extends DataObject> extends ActionList<T> implements LifeCycleListener<T> {

	public ActionListAdaptor(Class<T> target,DataObjectFactory<T> factory, String list_name) {
		super(target,factory, list_name);
	}

	@Override
	public void prepare(T target) throws Exception {
		for(ActionListener<T> l : this) {
			if( l instanceof LifeCycleListener) {
				((LifeCycleListener<T>)l).prepare(target);
			}
		}
	}

	@Override
	public void abort(T target) {
		for(ActionListener<T> l : this) {
			if( l instanceof LifeCycleListener) {
				((LifeCycleListener<T>)l).abort(target);
			}
		}
	}

}
