//| Copyright - The University of Edinburgh 2014                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.UncaughtDataFault;


/**
 * @author spb
 * @param <D> 
 *
 */

public abstract class AbstractFilterResult<D> implements FilterResult<D> {
	private CloseableIterator<D> iter=null;
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.FilterResult#iterator()
	 */
	public final CloseableIterator<D> iterator() {
		
		try{
			if( iter != null){
				iter.close();
			}
			return iter=makeIterator();
		}catch(Exception e){
			getLogger().error("Error making iterator for FilterSet",e);
			throw new UncaughtDataFault("Error making iterator", e);
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public final void close(){
		if( iter != null) {
			try {
				iter.close();
				iter=null;
			} catch (Exception e) {
				getLogger().error("Error closing iterator", e);
			}
		}
	}
	protected abstract CloseableIterator<D> makeIterator() throws DataFault;
	  /* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.model.data.FilterResult#toCollection()
			 */
	        public final List<D> toCollection(){
	        	// default ot LinekdList to preserve ordering
	        	return toCollection(new LinkedList<D>());
	        }
	        /* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.model.data.FilterResult#toCollection(X)
			 */
	        public final <X extends Collection<D>> X toCollection(X res){
	        	
	        	Iterator<D> iterator = iterator();
	        	if( iterator != null){
	        		for(Iterator<D> it = iterator; it.hasNext();){
	        			res.add(it.next());
	        		}
	        	}
	        	return res;
	        }
			@Override
			public boolean isEmpty() throws DataFault {
				try(CloseableIterator<D> it = iterator()){
					return ! it.hasNext();
				} catch (Exception e) {
					throw new DataFault("Error in isEmpty",e);
				}
			}
			
			protected abstract Logger getLogger();
}