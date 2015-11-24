// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet.navigation;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.FilteredProperties;
import uk.ac.ed.epcc.webapp.forms.Identified;
import uk.ac.ed.epcc.webapp.forms.transition.DefaultingTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.IndexTransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;

/**
 * @author spb
 * @param <T> Transition Target type
 * @param <K> Transition key type
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
public class TransitionNodeMaker<T,K> extends AbstractNodeMaker {

	/**
	 * @param conn
	 */
	public TransitionNodeMaker(AppContext conn) {
		super(conn);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.NodeMaker#makeNode(java.lang.String, uk.ac.ed.epcc.webapp.config.FilteredProperties)
	 */
	@Override
	public Node makeNode(String name, FilteredProperties props) {
		Node n = new ParentNode();
		@SuppressWarnings("unchecked")
		TransitionFactory<K, T> fac = getTransitionFactory(name, props);
		// look for an explicit named targetless transition
		String key = props.getProperty(name+".key");
		if( key != null ){
			K transition_key = fac.lookupTransition(null, key);
			if( ! fac.allowTransition(getContext(), null, transition_key)){
				return null;
			}
			if( transition_key != null ){
				n.setTargetPath(TransitionServlet.getURL(getContext(), fac, null,transition_key));
			}
		}else{
			// look for an index transition
			if( fac instanceof IndexTransitionFactory){
				IndexTransitionFactory<K, T> itf = (IndexTransitionFactory<K, T>) fac;
				K index = itf.getIndexTransition();
				if( index != null ){
					// Don't set index explicitly to simplify url to common prefix path
					n.setTargetPath(TransitionServlet.getURL(getContext(), fac,null,null));
				}
			}
		}
		return n;
	}

	/** get the {@link TransitionFactory}
	 * 
	 * A sub-class can override this to hardwire a particular provider.
	 * @param name
	 * @param props
	 * @return
	 */
	protected TransitionFactory<K, T> getTransitionFactory(String name, FilteredProperties props) {
		String transition_name = props.getProperty(name+".transition", name);
		@SuppressWarnings("unchecked")
		TransitionFactory<K,T> fac = TransitionServlet.getProviderFromName(getContext(), transition_name);
		return fac;
	}
	
	public Node childNode(String name, T target,K key, FilteredProperties props){
		Node n = new ParentNode();
		TransitionFactory<K, T> fac = getTransitionFactory(name, props);
		if( key == null && fac instanceof DefaultingTransitionFactory){
			key = ((DefaultingTransitionFactory<K, T>)fac).getDefaultTransition(target);
		}
		if( key != null){
			n.setMenuText(key.toString());
		}else{
			setChildName(n, target);
		}
		n.setTargetPath(TransitionServlet.getURL(getContext(), fac, target,key));
		return n;
		
	}

	/**
	 * @param n
	 * @param target
	 */
	protected void setChildName(Node n, T target) {
		if( target != null && target instanceof Identified){
			n.setMenuText(((Identified)target).getIdentifier());
		}
	}

}
