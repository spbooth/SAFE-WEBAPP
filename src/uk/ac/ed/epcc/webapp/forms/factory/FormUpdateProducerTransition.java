// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider;
/** Transition to implement the first select stage of a FormUpdate
 * 
 * @author spb
 *
 * @param <K>
 * @param <X>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: FormUpdateProducerTransition.java,v 1.3 2015/04/11 14:56:58 spb Exp $")

public class FormUpdateProducerTransition<K,X> extends AbstractFormUpdateTransition<K,X> {

	

	private final FormUpdateProducer<X> producer;

	/**
	 * 
	 * @param label label to use for selector
	 * @param update FormUpdate
	 * @param tp TransitionProvider 
	 * @param next transition to recurse to
	 */
	public FormUpdateProducerTransition(String label,FormUpdateProducer<X> update, TransitionProvider<K,X> tp, K next){
		super(label,tp,next);
		this.producer=update;
		
	}
	@Override
	public FormUpdate<X> getUpdate(AppContext c){
		return producer.getFormUpdate(c);
	}
	

}