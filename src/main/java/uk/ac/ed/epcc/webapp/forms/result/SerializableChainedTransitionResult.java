package uk.ac.ed.epcc.webapp.forms.result;

import java.io.Externalizable;

import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet.GetIDVisitor;

public class SerializableChainedTransitionResult<T,K> implements SerializableFormResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public SerializableChainedTransitionResult(TransitionFactory<K,T> provider,T target,K next) {
		target_name=provider.getTargetName();
		if( target == null ) {
			id=null;
		}else {
			GetIDVisitor< T,K> vis = new GetIDVisitor<>(target);
			id=provider.accept(vis);
		}
		if( next == null) {
			key=null;
		}else {
			key=next.toString();
		}
	}
	public SerializableChainedTransitionResult(String target, String id, String key) {
		super();
		this.target_name = target;
		this.key = key;
		this.id = id;
	}
	private final String target_name;
	private final String key;
	private final String id;
	@Override
	public void accept(FormResultVisitor vis) throws Exception {
		vis.visitSerializableChainedTransitionResult(this);

	}
	public String getTargetName() {
		return target_name;
	}
	public String getKey() {
		return key;
	}
	public String getId() {
		return id;
	}

}
