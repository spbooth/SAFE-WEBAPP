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
package uk.ac.ed.epcc.webapp.forms.result;

import java.util.LinkedList;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.forms.html.WebFormResultVisitor;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryFinder;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet.GetTargetVisitor;


/** This handles the mandatory types of FormResult.
 * 
 * A {@link FormResultVisitor} should only implement view/display logic. Changes of model state should happen in
 * a {@link TransitionVisitor}.
 * Certain environments may define additional types and need to
 * sub-class this interface. In this case the additional result types
 * will need to cast the visitor to the sub-type. The prime example of this is {@link WebFormResultVisitor} which handles
 * html-only result-types.
 * 
 * @author spb
 * @see WebFormResultVisitor
 */
public interface FormResultVisitor extends Contexed{
  public <T,K> void visitChainedTransitionResult(ChainedTransitionResult<T,K> res) throws Exception;
  default public <T,K> void visitSerializableChainedTransitionResult(SerializableChainedTransitionResult<T,K> res) throws Exception{
	  TransitionFactoryFinder finder = new TransitionFactoryFinder(getContext());
	  TransitionFactory<K, T> fac = finder.getProviderFromName(res.getTargetName());
	  T target =null;
	  String id=res.getId();
	  if( id != null && ! id.isEmpty()) {
		  LinkedList<String> path = new LinkedList<String>();
		  for(String s : id.split("/")) {
			  path.add(s);
		  }
		  GetTargetVisitor<T,K> vis = new GetTargetVisitor<T,K>(path);
		  target = fac.accept(vis);
	  }
	  K key = null;
	  if( res.getKey() != null) {
		  key = fac.lookupTransition(target, res.getKey());
	  }
	  
	  visitChainedTransitionResult(new ChainedTransitionResult<T, K>(fac, target, key));
  }
  public <T,K> void visitConfirmTransitionResult(ConfirmTransitionResult<T,K> res) throws Exception;
  public void visitMessageResult(MessageResult res) throws Exception;
  default public void visitWarningMessageResult(WarningMessageResult res) throws Exception{
	  visitMessageResult(res);
  }
  public void visitServeDataResult(ServeDataResult res)throws Exception;
  public void visitBackResult(BackResult res)throws Exception;
  public void visitCustomPage(CustomPageResult res)throws Exception;
 
}