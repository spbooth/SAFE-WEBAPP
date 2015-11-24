// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.session;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ref.SoftReference;

/** A Serializable wrapper that around a SoftReference. 
 * @author spb
 * @param <T> Type referenced this should be a primitive type or Serializable
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
public class SerialisableSoftReference<T>  implements Externalizable{

	private SoftReference<T> ref=null;
	
	public SerialisableSoftReference(){
		
	}
	
	public SerialisableSoftReference(T data){
		this();
		setData(data);
	}
	public void setData(T data){
		if( data == null ){
			ref=null;
		}else{
			ref = new SoftReference<T>(data);
		}
	}
	
	public T getData(){
		if( ref == null){
			return null;
		}
		return ref.get();
	}
	

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		T data = getData();
		out.writeObject(data);
		
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		setData((T) in.readObject());
		
	}

}
