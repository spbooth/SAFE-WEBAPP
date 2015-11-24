// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.reference;

import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeConverter;
/** A factory interface for {@link Indexed} objects.
 * 
 * Note that though {@link Indexed} objects can always be represented as integers
 * we implement {@link TypeConverter} to convert from any {@link Number}. This is
 * to preserve the flexibility to cope with database fields that return long values in preference to integers.
 * 
 * @author spb
 *
 * @param <A>
 */
public interface IndexedProducer<A extends Indexed> extends TypeConverter<A, Number>{
	public A find(int id)
	throws uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

	/** Get a class object that all results of the producer are assignable to.
	 * This is intended for run-time type checking so it should be as specific as possible
	 * 
	 * @return Class object for target
	 */
	public Class<? super A> getTarget();
	
	/** Make a IndexedReference from a target object
	 * 
	 * @param obj
	 * @return IndexedReference
	 */
	public IndexedReference<A> makeReference(A obj);
	/** Make and IndexedReferencce from an integer id.
	 * 
	 * @param id
	 * @return IndexedReference
	 */
	public IndexedReference<A> makeReference(int id);
	
	/** Test if an IndexedReference belongs to this producer
	 * 
	 * @param ref
	 * @return boolean
	 */
	public boolean isMyReference(IndexedReference ref);
	
	
	/** Generate the default text identifier of the client object for contexts where
	 * the type is unambiguous.
	 * This is used in urls and html inputs.
	 * 
	 *  Normally this should generate the String representation of the integer id.
	 *  if it generates anything else the {@link IndexedProducer} should
	 * implement {@link ParseFactory} to be able to parse the alternative form.
	 * 
	 * @param obj
	 * @return
	 */
	public String getID(A obj);
}