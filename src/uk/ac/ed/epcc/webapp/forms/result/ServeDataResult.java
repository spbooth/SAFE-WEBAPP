//| Copyright - The University of Edinburgh 2011                            |
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
package uk.ac.ed.epcc.webapp.forms.result;

import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;

/** Serve a piece of data for download specified by
 * a {@link ServeDataProducer}
 * 
 * @author spb
 *
 */

public final class ServeDataResult implements FormResult {
	private final ServeDataProducer producer;
	private List<String> args;
	

	public ServeDataResult(ServeDataProducer producer, List<String> args){
		this.producer=producer;
		this.args = new LinkedList<String>(args);
	}
	public void accept(FormResultVisitor vis) throws Exception {
		vis.visitServeDataResult(this);
	}
	public ServeDataProducer getProducer(){
		return producer;
	}
	
	public List<String> getArgs(){
		if( args == null){
			return null;
		}
		return new LinkedList<String>(args);
	}
}