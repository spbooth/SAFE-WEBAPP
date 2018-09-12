//| Copyright - The University of Edinburgh 2015                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.servlet.navigation;




import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
/** A container for menu {@link Node}s This might represent the top-level menu-bar.
 * As {@link Node}s can also contain child nodes this is also the super-type of {@link Node}.
 * @author spb
 *
 */
public class NodeContainer implements Externalizable {

	protected LinkedList<Node> children = new LinkedList<Node>();
    private Date creation_date;
	private String id;
	/**
	 * 
	 */
	public NodeContainer() {
		super();
		creation_date=new Date();
	}

	public void addChild(Node n) {
		if( n != null ){
			children.add(n);
			n.setParent(this);
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
		out.writeLong(creation_date.getTime());
		if( id == null ){
			out.writeObject("");
		}else{
			out.writeObject(id);
		}
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
		long time = in.readLong();
		creation_date = new Date(time);
		setID((String)in.readObject());
		int size = in.readInt();
		for(int i=0 ; i < size ; i++){
			addChild((Node)in.readObject());
		}
	}
	/** Does the node contain children */
	public boolean isEmpty(){
		return children.isEmpty();
	}

	public Date getDate(){
		return creation_date;
	}
	public String getID(){
		return id;
	}
	public void setID(String id){
		if( id == null || id.isEmpty()){
			this.id=null;
		}else{
			this.id=id;
		}
	}
	
	public void accept(Visitor vis){
		if( isEmpty()){
			return;
		}
		vis.visitContainer(this);
	}
}