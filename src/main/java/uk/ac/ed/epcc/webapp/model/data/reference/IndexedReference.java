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
package uk.ac.ed.epcc.webapp.model.data.reference;

import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.IndexedError;

/** A lightweight class that holds the id of an Indexed type 
 * and enough information to create the target object.
 * This is intended to be as efficient as using and Integer object but with better type safety 
 * and the ability to retrieve the target directly.
 * An id of zero always denotes an unknown value.
 * Note that {@link IndexedSQLValue} will generate a IndexedReference with id of zero for 
 * a null reference
 * 
 * We can't implement a sensible sort order (other than numeric) without an {@link AppContext} reference.
 * It is therefore better not to implement Comparable as higher level code (such as table) can but will default to
 * a Comparable implementation if it is implemented.
 * If numeric sorting is required use a {@link ReferenceComparator}.
 * 
 * 
 * @author spb
 * @param <I> 
 *
 */


public final class IndexedReference<I extends Indexed> {
  /**
	 * 
	 */
	public static final String INDEXED_REFERENCE_NAME_REGEXP = "IndexedReference\\((\\d+),([\\w\\.\\$]*),(\\w*)\\)";
private final int id;
  private final Class<? extends IndexedProducer> clazz;
  // table is allowed to be null if and only if the factory clazz hardwires the table
  private final String table;
  public IndexedReference(int id, Class<? extends IndexedProducer> clazz, String table){
	  this.id=id;
	  this.clazz=clazz;
	  this.table=table;
	  if( id < 0 ){
		  // id of zero is a null reference, negative is just plain wrong
		  throw new IndexedError("Invalid index value "+id+" class "+clazz.getCanonicalName()+" table "+table);
	  }
  }
  public IndexedReference(int id, Class<? extends IndexedProducer> clazz){
	  this(id,clazz,null);
  }
  public I getIndexed(AppContext c){
	  if( id <= 0 ){
		  return null;
	  }
	  try{
	  IndexedProducer<I> producer;
	  producer =  makeIndexedProducer(c,clazz,table);
	  if( producer == null){
		  getLogger(c).error("Failed to make producer class="+clazz.getCanonicalName()+" table "+table);
		  return null;
	  }else{
		  return producer.find(id);
	  }
	  }catch(Exception e){
		  getLogger(c).error("Exception making indexed",e);
		  return null;
	  }
	 
  }
protected Logger getLogger(AppContext c) {
	return Logger.getLogger(c,getClass());
}
 
@SuppressWarnings("unchecked")
public static <I extends Indexed> IndexedProducer<I> makeIndexedProducer(AppContext c, Class<? extends IndexedProducer> clazz, String table)  {

	  try{
		  return c.makeObject(clazz, table);
	  }catch(Exception e){
		  Logger.getLogger(IndexedReference.class).error("Exception making IndexedProducer",e);
		  return null;
	  }

  }
@SuppressWarnings("unchecked")
@Override
public boolean equals(Object obj) {
	if( obj instanceof IndexedReference){
		IndexedReference<I> peer = (IndexedReference<I>)obj;
		// either both indexes have null tables (table hard-wired in class no reference property)
		//  or tables are null.
		boolean table_match=false;
		if( table==null ){
			table_match= (peer.table == null);
		}else{
			table_match = peer.table != null && table.equals(peer.table);
		}
		return id==peer.id && clazz.equals(peer.clazz) && table_match;
	}
	return false;
}
@Override
public int hashCode() {
	return id;
}
public int getID() {
	return id;
}
public Class<? extends IndexedProducer> getFactoryClass() {
	return clazz;
}

public final static Pattern NAME_PATTERN=Pattern.compile(INDEXED_REFERENCE_NAME_REGEXP);
/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Override
public String toString() {
	//Use getName not getCanonicalName as this is reversibe by the parse mechanism
	return "IndexedReference("+id+","+(clazz==null?IndexedProducer.class.getName():clazz.getName())+","+(table==null?"":table)+")";
}
/** check reference points somewhere
 * 
 * @return true if reference is unknown
 */
public boolean isNull() {
	return id == 0;
}
/** parse the string value of an IndexedReference
 * @param c AppContext
 * 
 * @param <I> type referenced
 * @param s String to parse
 * @return IndexedReference
 */
@SuppressWarnings("unchecked")
public static <I extends Indexed> IndexedReference<I> parseIndexedReference(AppContext c,String s){
	java.util.regex.Matcher m = NAME_PATTERN.matcher(s);
	if( m.matches() ){
		int id = Integer.parseInt(m.group(1));
		String classname = m.group(2);
		Class<? extends IndexedProducer<I>> clazz = null;
		if( classname.trim().length() > 0){
			try {
				clazz = (Class<? extends IndexedProducer<I>>) Class.forName(classname);
			} catch (ClassNotFoundException e) {
				Logger.getLogger(IndexedReference.class).error("Error parsing IndexedReference class",e);
			}
		}
		String tag = m.group(3);
		if( tag.trim().length() == 0){
			tag=null;
		}
		return new IndexedReference<>(id, clazz,tag);
	}
	return null;
}
public String getTag() {
	return table;
}

}