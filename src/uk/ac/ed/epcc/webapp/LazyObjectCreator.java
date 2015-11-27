// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp;

/** This is a lazy evaluated wrapper round the AppContext.makeObject call
 * This allows object construction to be deferred until required.
 * It provides finer control than just caching the results in the AppContext 
 * <p>
 * If this class is specialised for particular target interfaces then those interfaces can be implemented
 * directly (forwarding onto the inner object).
 * @author spb
 *
 * @param <A>
 */
public class LazyObjectCreator<A> implements Contexed {
  private final AppContext conn;
  private final Class<? super A> clazz;
  private final String tag;
  private  A inner=null;
  public LazyObjectCreator(AppContext c, Class<? super A> clazz, String tag){
	  this.conn=c;
	  this.clazz=clazz;
	  this.tag=tag;
  }
  @SuppressWarnings("unchecked")
public LazyObjectCreator(AppContext c,A result){
	  this.conn=c;
	  if( result instanceof Tagged){
		  this.tag = ((Tagged)result).getTag();
	  }else{
		  tag=null;
	  }
	  clazz=(Class<A>) result.getClass();
	  inner=result;
  }
  
  @SuppressWarnings("unchecked")
public A getInner(){
	  if( inner == null ){
		  inner = (A) conn.makeObject(clazz, tag);
	  }
	  return inner;
  }
  public Class<? super A> getInnerClass(){
	  return clazz;
  }
  public String getInnerTag(){
	  return tag;
  }
public AppContext getContext() {
	return conn;
}
@Override
public int hashCode() {
	
	return tag.hashCode()+clazz.hashCode();
}
@Override
public boolean equals(Object obj) {
	if( obj.getClass().equals(getClass())){
		LazyObjectCreator o = (LazyObjectCreator)obj;
		return o.clazz.equals(clazz) && o.tag.equals(tag);
	}
	return false;
}
  
}