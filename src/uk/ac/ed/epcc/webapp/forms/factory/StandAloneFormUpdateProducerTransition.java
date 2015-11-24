// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;
@uk.ac.ed.epcc.webapp.Version("$Id: StandAloneFormUpdateProducerTransition.java,v 1.3 2015/04/11 14:56:58 spb Exp $")


public class StandAloneFormUpdateProducerTransition<X> extends
		EditTransition<X> {
	private final FormUpdateProducer<X> producer;
	public StandAloneFormUpdateProducerTransition(String type_name,FormUpdateProducer<X> producer){
		super(type_name);
		this.producer=producer;
	}
	@Override
	public StandAloneFormUpdate<X> getUpdate(AppContext c,X dat) {
	
		return (StandAloneFormUpdate<X>) producer.getFormUpdate(c);
	}

}