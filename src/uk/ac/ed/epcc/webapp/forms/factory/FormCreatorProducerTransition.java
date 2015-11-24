// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.AppContext;

/** Object creation transition.
 * 
 * @author spb
 *
 * @param <X>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: FormCreatorProducerTransition.java,v 1.2 2014/09/15 14:30:17 spb Exp $")

public class FormCreatorProducerTransition<X> extends CreatorTransition<X> {
    
    private final FormCreatorProducer producer;
	
   
    public FormCreatorProducerTransition(String type_name,FormCreatorProducer producer){
    	super(type_name);
    	this.producer=producer;
    }
	
	
	@Override
	public FormCreator getCreator(AppContext c) {
		return producer.getFormCreator(c);
	}

}