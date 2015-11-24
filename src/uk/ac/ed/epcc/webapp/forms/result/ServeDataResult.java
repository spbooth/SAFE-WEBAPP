// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.result;

import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;

/** Serve a piece of data for download specified by
 * a {@link ServeDataProducer}
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ServeDataResult.java,v 1.5 2015/08/19 14:44:05 spb Exp $")
public final class ServeDataResult implements FormResult {
	private final ServeDataProducer producer;
	private List<String> args;
	

	public ServeDataResult(ServeDataProducer producer, List<String> args){
		this.producer=producer;
		this.args = new LinkedList<String>(args);
	}
	public void accept(FormResultVisitor vis) throws Exception {
		vis.visitServeDataResult(this);
	}
	public ServeDataProducer getProducer(){
		return producer;
	}
	
	public List<String> getArgs(){
		if( args == null){
			return null;
		}
		return new LinkedList<String>(args);
	}
}