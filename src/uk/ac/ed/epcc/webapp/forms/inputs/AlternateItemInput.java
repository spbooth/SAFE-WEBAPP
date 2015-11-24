// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Iterator;

@uk.ac.ed.epcc.webapp.Version("$Id: AlternateItemInput.java,v 1.4 2014/09/15 14:30:18 spb Exp $")

/**
 * 
 * @author spb
 *
 * @param <T> Input type
 * @param <I> Item type
 */
public class AlternateItemInput<T,I> extends AlternateInput<T> implements ItemInput<I> {

	public I getItem() {
		for(Iterator<Input<T>> it = getInputs();it.hasNext();){
			ItemInput<I> i =  (ItemInput<I>) it.next();
			I val = i.getItem();
			if( val != null ){
				return val;
			}
		}
		return null;
	}

	public void setItem(I item) {
		
			boolean set=false;
			for(Iterator<Input<T>> it = getInputs();it.hasNext();){
				ItemInput<I> i =  (ItemInput<I>) it.next();
				if( ! set ){
				   i.setItem(item);
				   set = true;
				}else{
					i.setItem(null);
				}
				
			}
			
		
	}


}