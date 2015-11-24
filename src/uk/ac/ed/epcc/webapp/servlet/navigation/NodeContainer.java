// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet.navigation;




import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;
/** A container for menu {@link Node}s This might represent the top-level menu-bar.
 * As {@link Node}s can also contain child nodes this is also the super-type of {@link Node}.
 * @author spb
 *
 */
public class NodeContainer implements Externalizable {

	protected LinkedList<Node> children = new LinkedList<Node>();

	/**
	 * 
	 */
	public NodeContainer() {
		super();
	}

	public void addChild(Node n) {
		if( n != null ){
			children.add(n);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Node> getChildren() {
		return (List<Node>) children.clone();
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(children.size());
		for(Node n : children){
			out.writeObject(n);
		}
		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int size = in.readInt();
		for(int i=0 ; i < size ; i++){
			addChild((Node)in.readObject());
		}
	}
	
	public boolean isEmpty(){
		return children.isEmpty();
	}

}