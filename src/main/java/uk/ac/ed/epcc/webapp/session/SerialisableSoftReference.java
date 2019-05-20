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
package uk.ac.ed.epcc.webapp.session;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ref.SoftReference;

/** A Serializable wrapper that around a SoftReference. 
 * 
 * optionally the reference can be forced to null by serialisation to limit the
 * amount of data serialised.
 * @author spb
 * @param <T> Type referenced this should be a primitive type or Serializable
 *
 */

public class SerialisableSoftReference<T>  implements Externalizable{

	private SoftReference<T> ref=null;
	private boolean force_null_on_serialise=false;
	
	/**
	 * @return the force_null_on_serialise
	 */
	public boolean isForceNullOnSerialise() {
		return force_null_on_serialise;
	}

	/**
	 * @param force_null_on_serialise the force_null_on_serialise to set
	 */
	public void setForceNullOnSerialise(boolean force_null_on_serialise) {
		this.force_null_on_serialise = force_null_on_serialise;
	}

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
			ref = new SoftReference<>(data);
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
		if( force_null_on_serialise){
			data=null;
		}
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