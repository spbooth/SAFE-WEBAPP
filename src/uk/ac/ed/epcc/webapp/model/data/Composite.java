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

import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.jdbc.table.TransitionSource;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.table.TableStructureDataObjectFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link Composite} adds behaviour to a {@link DataObjectFactory}
 * by composition but persisted to the same database table.
 * It specifies additional fields for the
 * {@link Repository} and methods to customise the forms and may be extended to add
 * functionality based on those fields.
 * <p>
 * For this class to affect the table specification it needs to be registered before the
 * {@link DataObjectFactory#setContext(uk.ac.ed.epcc.webapp.AppContext, String)}method is called.
 * The easiest way to do this is to specify them as configuration parameters.
 * You <i>can</i> create the composite during field initialisation. If you do this you <em>MUST</em>
 * call the setContext method from the constructor body in the same class not recurse to the superclass constructor that calls setContext.
 * This is because superclass constructors run before field initialisation but local constructor bodies run afterwards.
 * 
 * <p>
 * {@link Composite}s that have a single argument constructor that takes the 
 * {@link DataObjectFactory} as the argument (optionally followed by a String), can be added to a factory 
 * by setting a (comma separated) list of classdef or property class names in the parameter <em>factory-tag</em><b>.composites</b>
 * and these will be added in the {@link DataObjectFactory#setContext(uk.ac.ed.epcc.webapp.AppContext, String)} method and can therefore 
 * modify the table specification. If the constructor takes the String parameter the construction tag of the composite will be passed.
 * <p>
 * The areas that {@link Composite}s can customise can be extended by having a factory check all composites for specific interfaces for example
 * {@link TableStructureDataObjectFactory} check the composites for {@link TransitionSource}
 * <p>
 * Each {@link Composite} provides a type {@link Class} that it is registered under. A factory cannot contain two {@link Composite}s registered under the same class.
 * Any functionality that should only be included once (but has many implementations) should use the same registration class and can be retrieved by the registration type.
 * <p>
 * Composites augment a {@link DataObjectFactory} so it is easiest to add behaviour to the factory rather than the {@link DataObject}.
 * To add behaviour to {@link DataObject} is easier if the object is an inner class or contains a reference to its factory.
 * You can also introduce additional handler classes that wrap the repository these can have the same relation to the {@link Composite}
 * as {@link DataObject} has to the {@link DataObjectFactory}.
 * 
 * @author spb
 * @param <BDO> target type
 * @Param <X> type the {@link Composite} is registered under.
 */
@SuppressWarnings("javadoc")

public abstract class Composite<BDO extends DataObject, X extends Composite> implements Contexed, TableStructureContributer<BDO> {

	protected final DataObjectFactory<BDO> fac;
	protected Composite(DataObjectFactory<BDO> fac){
		this.fac=fac;
		preRegister();
		fac.registerComposite(this);
	}
	/** extension point called before the composite is registered. This is to allow the class setup to be completed
	 * that cant wait till after registration.
	 * 
	 */
	protected void preRegister(){
		
	}
	/** Returns the type  the composite should be registered under.
	 * 
	 * @return registration type
	 */
	protected abstract Class<? super X> getType();
	
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table){
		return spec;
	} 
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.TableStructureContributer#addOptional(java.util.Set)
	 */
	@Override
	public Set<String> addOptional(Set<String> optional) {
		return optional;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.TableStructureContributer#addDefaults(java.util.Map)
	 */
	@Override
	public Map<String, Object> addDefaults(Map<String,Object> defaults) {
		return defaults;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.TableStructureContributer#addTranslations(java.util.Map)
	 */
	@Override
	public Map<String, String> addTranslations(Map<String,String> translations) {
		return translations;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.TableStructureContributer#addSelectors(java.util.Map)
	 */
	@Override
	public Map<String,Object> addSelectors(Map<String,Object> selectors) {

		return selectors;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.TableStructureContributer#addSuppress(java.util.Set)
	 */
	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		return suppress;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.TableStructureContributer#customiseForm(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public  void customiseForm(Form f) {

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.TableStructureContributer#postUpdate(BDO, uk.ac.ed.epcc.webapp.forms.Form, java.util.Map)
	 */
	@Override
	public void postUpdate(BDO o, Form f,Map<String,Object> orig) throws DataException{
		
	}
	/** method to allow sub-classes to retrieve the {@link Record} from
	 * a target {@link DataObject}. This allows composite functionality to be added
	 * to these as well as to the factory.
	 * 
	 * @param data
	 * @return
	 */
	protected final Record getRecord(BDO data){
		if( fac.isMine(data)){
			return data.record;
		}
		throw new ConsistencyError("Wrong Object type passed");
	}
	/** method to allow sub-classes to retrieve the {@link Repository}.
	 * 
	 * @return
	 */
	protected final Repository getRepository(){
		return fac.res;
	}
	public final DataObjectFactory<BDO> getFactory(){
		return fac;
	}
	public final AppContext getContext(){
		return fac.getContext();
	}
	protected final  Logger getLogger() {
		return getContext().getService(LoggerService.class).getLogger(getClass());
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.TableStructureContributer#customiseUpdateForm(uk.ac.ed.epcc.webapp.forms.Form, uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public void customiseUpdateForm(Form f, BDO target, SessionService operator) {
		
	}
	
	/** used to list composites on table structure page.
	 * 
	 */
	public String toString(){
		return getClass().getSimpleName();
	}
}