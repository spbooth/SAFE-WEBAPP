// Copyright - The University of Edinburgh 2011
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