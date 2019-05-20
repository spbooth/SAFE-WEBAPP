//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.Castor;
import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.Dummy2;
import uk.ac.ed.epcc.webapp.model.LinkDummy;
import uk.ac.ed.epcc.webapp.model.LinkDummy.DummyLink;
import uk.ac.ed.epcc.webapp.model.Pollux;

/**
 * @author Stephen Booth
 *
 */
public class BackLinkTest extends WebappTestBase {

	@Test
	/** This tests that a link filter that joins to a filter on one end
	 * still works if converted into a filter on that end.
	 * 
	 */
	public void testBackLink() throws Exception {
		Dummy1.Factory d1_fac = new Dummy1.Factory(ctx);
		Dummy1 fred = d1_fac.makeBDO();
		fred.setName("fred");
		fred.commit();
		
		Dummy1 bill = d1_fac.makeBDO();
		bill.setName("bill");
		bill.commit();
		
		Dummy1 nigel = d1_fac.makeBDO();
		nigel.setName("nigel");
		nigel.commit();
		
		Dummy2.Factory d2_fac = new Dummy2.Factory(ctx);
		
		
		Dummy2 emu = d2_fac.makeBDO();
		emu.setName("emu");
		emu.commit();
		
		Dummy2 horse = d2_fac.makeBDO();
		horse.setName("horse");
		horse.commit();
		
		
		LinkDummy linker = new LinkDummy(ctx);
		
		linker.addLink(fred, horse);
		linker.addLink(fred,emu);
		linker.addLink(bill, horse);
		
		BaseFilter<Dummy1> fred_filter = d1_fac.getStringFilter("fred");
		
		assertEquals(1,d1_fac.getCount(fred_filter));
		
		BaseFilter<DummyLink> link_fil = linker.getLeftRemoteFilter(fred_filter);
		assertEquals(2,linker.getCount(link_fil));
		
		BaseFilter<Dummy1> fred_filter2 = linker.getLeftFilter(link_fil);
		
		assertEquals(1,d1_fac.getCount(fred_filter2));
		
	}
	
	
	@Test
	public void testTwinTable() throws DataException {
		Castor.Factory c_fac = new Castor.Factory(ctx);
		Pollux.Factory p_fac = new Pollux.Factory(ctx);
		
		
		Castor ca = c_fac.makeBDO();
		ca.setName("A");
		ca.commit();
		
		Castor cb = c_fac.makeBDO();
		cb.setName("B");
		cb.commit();
		
		Pollux pa = p_fac.makeBDO();
		pa.setName("A");
		pa.commit();
		
		Pollux pb = p_fac.makeBDO();
		pb.setName("B");
		pb.commit();
		
		ca.setReference(pa);
		ca.commit();
		pa.setReference(ca);
		pa.commit();
		
		cb.setReference(pb);
		cb.commit();
		pb.setReference(cb);
		pb.commit();
		
		
		BaseFilter<Castor> c_fil1 = c_fac.new StringFilter("A");
		assertEquals(1, c_fac.getCount(c_fil1));
	
		BaseFilter<Pollux> p_fil1 = p_fac.getFilterFromPeer(c_fil1);
		assertEquals(1,p_fac.getCount(p_fil1));
		
		BaseFilter<Castor> c_fil2 = c_fac.getFilterFromPeer(p_fil1);
		assertEquals(1,c_fac.getCount(c_fil2));
		
	}
}
