// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

import java.lang.reflect.Constructor;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;

/** A variant of BasicType for fields that encode the sub-class in a multi-class
 * factory
 * 
 * @author spb
 *
 * @param <T> Type of value object
 * @param <D> superclass of target object
 */
public abstract class ClassType<T extends ClassType.ClassValue<D>, D extends DataObject> extends BasicType<T> {
	/** Define a type field
     * @param field  Field name to encode for
     */
	protected ClassType(String field) {
		super(field);
	}
	protected ClassType(ClassType<T,D> parent){
		super(parent);
	}
	
    public abstract
@uk.ac.ed.epcc.webapp.Version("$Id: ClassType.java,v 1.21 2014/09/15 14:30:28 spb Exp $")
 static class ClassValue<S extends DataObject> extends BasicType.Value{
        Constructor<? extends S> cons=null;
        Class<? extends S> c;
		protected ClassValue(ClassType parent,String tag, String name, Class<? extends S> c) {
			super(parent,tag, name);
			this.c=c;
		}
		protected final  Constructor<? extends S> getConstructor(DataObjectFactory fac){
			if( cons == null){
				cons = makeConstructor(fac);
				
			}
			return cons;
		}
		/** get the constructor for the target class. By default we look for 
		 * the default signatures used by the makeBDO method but we can override this method
		 * provided the makeBDO method is also overridden.
		 * 
		 * @param fac DataObjectFactory 
		 * @return Constructor
		 */
		@SuppressWarnings("unchecked")
		protected Constructor<? extends S> makeConstructor(DataObjectFactory fac){
			try {
				if( fac != null  ){
					// search for a matching constructor. We can't use getConstructor in
					// case the factory is a sub-type of the declared param
					for(Constructor tmp : c.getConstructors()){
						Class params[] = tmp.getParameterTypes();
						if( params.length == 2 && params[0].isAssignableFrom(fac.getClass()) && params[1].isAssignableFrom(Repository.Record.class)){
									return tmp;
						}
					}
					throw new ConsistencyError("No appropriate constructor found");
				}else{
				    return c.getConstructor(new Class[] { Repository.Record.class} );
				}
			} catch (Throwable e) {
				String sig = ""+c.getCanonicalName()+"(";
				if( fac != null ){
					sig += fac.getClass().getCanonicalName()+",";
				}
				sig += Repository.Record.class.getCanonicalName()+")";
				throw new ConsistencyError("Error creating constructor "+sig,e);
			}
		}
		/** Construct an object using the Constructor returned by makeConstructor 
		 * 
		 * @param fac
		 * @param r
		 * @return new object
		 */
		 public S makeBDO(DataObjectFactory<? super S> fac, Repository.Record r){
		    	S i = null;

		    	
		    	Constructor<? extends S> c = getConstructor(fac);
		    	try {
		    		if( fac == null ){
		    			i = c.newInstance(new Object[]  { r });
		    		}else{
		    			i = c.newInstance(new Object[] { fac , r });
		    		}
		    	} catch (Exception e) {
		    		if( fac == null ){
		    			throw new ConsistencyError("Error constructing object (no factory)",e);
		    		}else{
		    			throw new ConsistencyError("Error constructing object factory: "+fac.getClass().getCanonicalName(),e);
		    		}
		    	}
		    	return i;	

		    }
		public Class<? extends S> getTargetClass(){
			return c;
		}
		
    }
    public D makeBDO(DataObjectFactory<D> fac, Repository.Record r){
    	ClassValue<D> v = r.getProperty(this);
    	if(v == null ){
    		return null;
    	}
    	return v.makeBDO(fac, r);

    }
}