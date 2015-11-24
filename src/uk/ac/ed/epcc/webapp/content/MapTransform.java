// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.content;

import java.util.Map;

@uk.ac.ed.epcc.webapp.Version("$Id: MapTransform.java,v 1.2 2014/09/15 14:30:15 spb Exp $")
/**
 * Transform using a Map
 * 
 * @author spb
 * 
 */
public class MapTransform implements Transform {
	private Map map;

	public MapTransform(Map map) {
		this.map = map;
	}

	public Object convert(Object old) {
		return map.get(old);
	}
}