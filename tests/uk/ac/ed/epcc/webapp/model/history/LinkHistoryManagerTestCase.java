/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.LinkManager;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.model.history.HistoryFactory.HistoryRecord;

public abstract class LinkHistoryManagerTestCase<L extends LinkHistoryHandler> extends WebappTestBase {

	

	public abstract L makeFactory();

	@SuppressWarnings("unchecked")
	@Test
	public void testhasChanged() throws IllegalArgumentException, DataException{
		L fac = makeFactory();
		LinkManager lm = (LinkManager) fac.getLinkManager();
		
		for( Iterator<LinkManager.Link> it = lm.getLinkIterator(null, null, null); it.hasNext(); ){
			LinkManager.Link item = it.next();
			System.out.println(item.getIdentifier());
			try{
				// ensure history is up to date
				fac.update(item);
				// get current value
				HistoryFactory.HistoryRecord h  = (HistoryRecord) fac.find(item, new Date());
				assertEquals("peer id matches ",h.getPeerID(),item.getID());
				System.out.println(h.toString());
				System.out.println(item.toString());
				Map<String,Object> hmap = h.getMap();
				Map<String,Object> imap = item.getMap();
				for( String key : hmap.keySet()){
					if( imap.containsKey(key)){
						Object hobj = hmap.get(key);
						Object iobj = imap.get(key);
						if( hobj instanceof Number && iobj instanceof Number){
							   assertEquals("Quota "+item.getIdentifier()+" value "+key,((Number)hobj).doubleValue(), ((Number)iobj).doubleValue(), 0.0);
						}else{
						   assertEquals("Quota "+item.getIdentifier()+" value "+key,hobj, iobj);
						}
					}
				}
				assertFalse( h.hasChanged(item));

			}catch(DataNotFoundException e){
				System.out.println("  not found");
			}
		}
	}
}
