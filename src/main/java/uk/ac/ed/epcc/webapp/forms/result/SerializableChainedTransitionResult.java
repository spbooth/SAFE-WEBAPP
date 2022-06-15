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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((target_name == null) ? 0 : target_name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SerializableChainedTransitionResult other = (SerializableChainedTransitionResult) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (target_name == null) {
			if (other.target_name != null)
				return false;
		} else if (!target_name.equals(other.target_name))
			return false;
		return true;
	}

}
