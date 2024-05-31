package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;

/** Interface for {@link TransitionProvider}s when the target is an {@link Indexed}
 * 
 * This provides the common default methods for mapping between IDs and targets
 * 
 * @author Stephen Booth
 *
 * @param <T> target type
 * @param <K> key type
 */
public interface IndexedTransitionProvider<K,T extends Indexed> extends Contexed,TransitionProvider<K, T> {

	Feature USE_NAME_PARSER = new Feature("transitions.use_name_parser",true,"Use name parser names by default as transition tags");

	public IndexedProducer<? extends T> getProducer();
	
	@Override
	public default String getID(T target) {
		if(target == null){
			return null;
		}
		IndexedProducer<? extends T> fac = getProducer();
		if( useParser() && fac instanceof ParseFactory){
			String name = ((ParseFactory)fac).getCanonicalName(target);
			if( name != null && name.trim().length() > 0){
				return name;
			}
		}
		return Integer.toString(target.getID());
	}

	public default boolean useParser() {
		return USE_NAME_PARSER.isEnabled(getContext());
	}
	public default String normaliseID(String id) {
    	return id;
    }
	public default T getTarget(String id) {
		id = normaliseID(id);
		AppContext c = getContext();
		IndexedProducer<? extends T> fac = getProducer();
		try {
			if( useParser() && fac instanceof ParseFactory){
				T val = ((ParseFactory<T>)fac).findFromString(id);
				if( val != null){
					return val;
				}
			}
			
			return (T) fac.find(Integer.parseInt(id));
		}catch(NumberFormatException nfe){
			// Not worth reporting
			if( fac instanceof ParseFactory) {
				// If useParser returns false we can still check the parser
				// for non-numeric strings. even though default rep is numeric id
				return ((ParseFactory<T>)fac).findFromString(id);
			}
			return null;
		} catch (DataException e) {
			getLogger().error("Error making IndexedTransitionProvider target",e);
			return null;
		}
	}
	public default Logger getLogger() {
		// sub-classes may want to override to cache the logger
		return getContext().getService(LoggerService.class).getLogger(getClass());
	}
}
