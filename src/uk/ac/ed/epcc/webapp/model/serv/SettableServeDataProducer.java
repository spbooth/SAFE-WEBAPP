// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.model.serv;

import java.util.List;

import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;

public interface SettableServeDataProducer extends ServeDataProducer {

	/** Store a MimeStreamData object generating a new path location
	 * 
	 * 
	 * @param data Data to add
	 * @return path to stored data
	 */
	public List<String> setData(MimeStreamData data);
	

	
}