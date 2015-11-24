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
