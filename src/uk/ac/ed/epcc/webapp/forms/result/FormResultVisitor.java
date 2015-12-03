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

import uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor;


/** This handles the mandatory types of FormResult.
 * 
 * A {@link FormResultVisitor} should only implement view/display logic. Changes of model state should happen in
 * a {@link TransitionVisitor}.
 * Certain environments may define additional types and need to
 * sub-class this interface. In this case the additional result types
 * will need to cast the visitor to the sub-type.
 * 
 * @author spb
 *
 */
public interface FormResultVisitor {
  public <T,K> void visitChainedTransitionResult(ChainedTransitionResult<T,K> res) throws Exception;
  public <T,K> void visitConfirmTransitionResult(ConfirmTransitionResult<T,K> res) throws Exception;
  public void visitMessageResult(MessageResult res) throws Exception;
  public void visitServeDataResult(ServeDataResult res)throws Exception;
  public void visitBackResult(BackResult res)throws Exception;
  public void visitCustomPage(CustomPageResult res)throws Exception;
 
}