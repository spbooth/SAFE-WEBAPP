//| Copyright - The University of Edinburgh 2015                            |
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
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.time.Period;

public class GenericPlotTest extends WebappTestBase {
	
	
	
    
    @Test
	public void testGetNumSets() {
		GenericSplitSetPlot p = new GenericSplitSetPlot();
		p.setSize(5, 4, 3);
		assertEquals(5, p.getNumSets());
	}

    @Test
	public void testGetNumCats() {
		GenericSplitSetPlot p = new GenericSplitSetPlot();
		p.setSize(5, 4, 3);
		assertEquals(4, p.getNumCats());
		
	}

    @Test
	public void testGetNumItems() {
		GenericSplitSetPlot p = new GenericSplitSetPlot();
		p.setSize(5, 4, 3);
		assertEquals(3, p.getNumItems());
	}

    @Test
	public void testGet() {
		GenericSplitSetPlot p = new GenericSplitSetPlot();
		p.set(1, 3, 8, 12.0f);
		assertEquals(12.0f,p.get(1,3,8),0.0);
		
	}

    @Test
	public void testGrow(){
		GenericSplitSetPlot p = new GenericSplitSetPlot();
		for(int i=0;i<10;i++){
			p.set(i,i,i,i);
		}
		for(int i=0;i<10;i++){
			assertEquals(i,p.get(i,i,i),0);
		}
		
	}
	 
	

	

}