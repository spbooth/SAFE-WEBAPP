//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;

/** class that encodes the rules for locating a {@link TransitionFactory} from its 
 * {@link TransitionFactory#getTargetName()} tag,
 * @author spb
 *
 */
public class TransitionFactoryFinder extends AbstractContexed {

	public static final String TRANSITION_PROVIDER_PREFIX = "TransitionProvider";
	
	/**
	 * 
	 */
	public TransitionFactoryFinder(AppContext conn) {
		super(conn);
	}

	
	/** method to retrieve the TransitionProvider from a string tag
	 * If the tag contains a colon this is taken as a separator between two fields.
	 * The first field is used to construct a {@link TransitionFactoryCreator} using {@link AppContext#makeObject(Class, String)}
	 * and the second field is used as the parameter to {@link TransitionFactoryCreator#getTransitionProvider(String)}.
	 * otherwise calls to  {@link AppContext#makeObject(Class, String)} are attempted with the following tag/type parameters
	 * <ul>
	 * <li> <b>TransitionProvider.</b><TargetName</b> looking for {link {@link TransitionFactory}.
	 * <li> <i>TargetName</i> looking for {@link TransitionFactory}
	 * <li> <i>TargetName</i> looking for {@link TransitionFactoryCreator}
	 * </ul>
	 * Sub-classes can override this method.
	
	 * @param type  TargetName of the TransitionProvider
	 * @return {@link TransitionFactory}
	 */
		
	 public TransitionFactory getProviderFromName( String type) {
			TransitionFactory result=null;
			if( type==null || type.trim().length() ==0 ){
				return null;
			}
			int index = type.indexOf(TransitionFactoryCreator.TYPE_SEPERATOR);
			if( index > 0){
				// must be a parameterised TransitionProviderFactory
				String group = type.substring(0, index);
				String tag = type.substring(index+1);
				TransitionFactoryCreator<TransitionFactory> f = conn.makeObjectWithDefault(TransitionFactoryCreator.class,null, group);
				if( f != null ){
					result = f.getTransitionProvider(tag);
				}
			}else{
				result = conn.makeObjectWithDefault(TransitionFactory.class,null,TRANSITION_PROVIDER_PREFIX,type);
				if( result == null ){
					TransitionFactoryCreator<TransitionFactory> f = conn.makeObjectWithDefault(TransitionFactoryCreator.class,null, type);
					if( f != null ){
						result = f.getTransitionProvider(type);
					}
				}
			}
			// check targetName matches type
			assert(result == null || result.getTargetName().equals(type));
			
			return result;
	 }
}
