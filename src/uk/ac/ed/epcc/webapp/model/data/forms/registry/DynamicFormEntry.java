// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms.registry;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.registry.AbstractFormFactoryProvider;
import uk.ac.ed.epcc.webapp.forms.registry.FormPolicy;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/** FormFactoryProvider that uses the Configuration Properties to find the factory class.
 * 
 * @author spb
 * @param <T> 
 *
 */
public abstract class DynamicFormEntry<T extends DataObject> extends AbstractFormFactoryProvider<DataObjectFactory<T>,T> {

	private String tag;
	public DynamicFormEntry(String name, String tag, FormPolicy policy) {
		super(name, policy);
		this.tag=tag;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected DataObjectFactory<T> getFactory(AppContext c) throws DataFault {
		DataObjectFactory<T>fac =  c.makeObjectWithDefault(DataObjectFactory.class, null, tag);
		if( ! fac.isValid()){
			return null;
		}
		return fac;
	}
	public boolean targets(AppContext c, Object target) {
		try{
			DataObjectFactory<T> fac = getFactory(c);
			return fac.isMine(target);
		}catch(Exception e){
			c.error(e,"Error getting owner");
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.AbstractFormFactoryProvider#disabled(uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	protected boolean disabled(AppContext c) {
		try{
			DataObjectFactory<T> fac = getFactory(c);
			return fac == null || ! fac.isValid();
		}catch(DataFault e){
			return true;
		}
	}

	public final String getID(T target) {
		return Integer.toString(target.getID());
	}

	public final T getTarget(AppContext c,String id) {
		try {
			return getFactory(c).find(Integer.parseInt(id));
		} catch (Exception e) {
		    return null;
		}
	}
	

}