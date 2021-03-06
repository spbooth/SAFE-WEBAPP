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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.transition;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;

/** Abstract base class for building {@link TransitionProvider}s on {@link Indexed} objects.
 * unlike {@link SimpleTransitionProvider} this makes no assumption about the type used as the key
 * 
 * @author spb
 *
 * @param <T> target type
 * @param <K> 
 */
public abstract class AbstractIndexedTransitionProvider<T extends Indexed,K>  extends AbstractContexed implements TransitionProvider<K, T> {

	

	public static final Feature USE_NAME_PARSER = new Feature("transitions.use_name_parser",true,"Use name parser names by default as transition tags");
    
    private final IndexedProducer<? extends T> fac;
    private final String target_name;
    
    

	public AbstractIndexedTransitionProvider(AppContext c,IndexedProducer<? extends T> fac,String target_name){
		super(c);
		this.fac=fac;
		this.target_name=target_name;
	}


	public final String getID(T target) {
		return getIndexedID(getContext(),fac,target);
	}


	/** default rule for generating an ID string for and {@link Indexed}
	 * @param target
	 * @return
	 */
	public static <T extends Indexed> String getIndexedID(AppContext c, IndexedProducer<? extends T> fac,T target) {
		if(target == null){
			return null;
		}
		if( USE_NAME_PARSER.isEnabled(c) && fac instanceof ParseFactory){
			String name = ((ParseFactory)fac).getCanonicalName(target);
			if( name != null && name.trim().length() > 0){
				return name;
			}
		}
		return Integer.toString(target.getID());
	}

	public final IndexedProducer<? extends T> getProducer(){
		return fac;
	}
	

	public final T getTarget(String id) {
		return getIndexedTarget(getContext(),fac,id);
	}


	/** default rule for generating an {@link Indexed} from a String.
	 * @param id
	 * @return
	 */
	public static <T extends Indexed> T getIndexedTarget(AppContext c, IndexedProducer<? extends T> fac, String id) {
		try {
			if( USE_NAME_PARSER.isEnabled(c) && fac instanceof ParseFactory){
				T val = ((ParseFactory<T>)fac).findFromString(id);
				if( val != null){
					return val;
				}
			}
			
			return fac.find(Integer.parseInt(id));
		}catch(NumberFormatException nfe){
			// Not worth reporing
			return null;
		} catch (DataException e) {
			c.getService(LoggerService.class).getLogger(AbstractIndexedTransitionProvider.class).error("Error making SimpleTransitionProvider target",e);
			return null;
		}
	}
	
	public final String getTargetName(){
		return target_name;
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory#accept(uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor)
	 */
	@Override
	public final <R> R accept(TransitionFactoryVisitor<R, T, K> vis) {
		return vis.visitTransitionProvider(this);
	}
}