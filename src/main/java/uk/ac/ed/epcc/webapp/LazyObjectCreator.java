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
package uk.ac.ed.epcc.webapp;

/** This is a lazy evaluated wrapper round the AppContext.makeObject call
 * This allows object construction to be deferred until required.
 * It provides finer control than just caching the results in the AppContext 
 * <p>
 * If this class is specialised for particular target interfaces then those interfaces can be implemented
 * directly (forwarding onto the inner object).
 * @author spb
 *
 * @param <A> type of object created
 */
public class LazyObjectCreator<A> extends AbstractContexed {
  private final Class<? super A> clazz;
  private final String tag;
  private  A inner=null;
  public LazyObjectCreator(AppContext c, Class<? super A> clazz, String tag){
	  super(c);
	  this.clazz=clazz;
	  this.tag=tag;
  }
  @SuppressWarnings("unchecked")
public LazyObjectCreator(AppContext c,A result){
	  super(c);
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