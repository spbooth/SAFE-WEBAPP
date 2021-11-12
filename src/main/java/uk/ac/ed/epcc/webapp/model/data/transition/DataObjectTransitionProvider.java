package uk.ac.ed.epcc.webapp.model.data.transition;

import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.IndexedTransitionProvider;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;

/** Interface for {@link TransitionProvider}s on {@link DataObject}s
 * 
 * 
 * @author Stephen Booth
 *
 * @param <X>
 * @param <F>
 * @param <K>
 */
public interface DataObjectTransitionProvider<X extends DataObject,F extends DataObjectFactory<X>,K> extends IndexedTransitionProvider<K, X> {
 
	/** Get the {@link DataObjectFactory} for the target object
	 * 
	 * @return
	 */
	public F getFactory();
	
	@Override
	public default IndexedProducer<? extends X> getProducer(){
		return getFactory();
	}
	
	
}
