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
package uk.ac.ed.epcc.webapp.model.log;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator;
/** Top level TransitionProviderFactory that locates the Item TransitionProvider
 * by going through the LogOwner 
 * 
 * @author spb
 *
 */


public class LogTransitionProviderFactory extends AbstractContexed implements
		TransitionFactoryCreator<LogTransitionProvider>, Contexed{

	
	public LogTransitionProviderFactory(AppContext c){
		super(c);
	}
	public LogTransitionProvider getTransitionProvider(String tag) {
		LogOwner<?> owner = conn.makeObject(LogOwner.class, tag);
		if( owner != null){
			return owner.getLogFactory().getTransitionProvider();
		}
		return null;
	}

}