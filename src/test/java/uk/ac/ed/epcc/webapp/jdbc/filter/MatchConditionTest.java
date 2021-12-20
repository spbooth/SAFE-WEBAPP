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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Stephen Booth
 *
 */
public class MatchConditionTest {

	/**
	 * 
	 */
	public MatchConditionTest() {
		
	}

	@Test
	public void testLT() {
		assertTrue(MatchCondition.LT.compare(3, 7));
		assertTrue(MatchCondition.LT.compare(3, 7.0));
		assertTrue(MatchCondition.LT.compare("aaa", "zzz"));
		assertFalse(MatchCondition.LT.compare( 7,3));
		assertFalse(MatchCondition.LT.compare( 7,3.0));
		assertFalse(MatchCondition.LT.compare("zzz", "aaa"));
		assertFalse(MatchCondition.LT.compare( 7,7));
		assertFalse(MatchCondition.LT.compare( 7,7.0));
		assertFalse(MatchCondition.LT.compare("aaa", "aaa"));
	}
	
	@Test
	public void testGT() {
		assertFalse(MatchCondition.GT.compare(3, 7));
		assertFalse(MatchCondition.GT.compare(3, 7.0));
		assertFalse(MatchCondition.GT.compare("aaa", "zzz"));
		assertTrue(MatchCondition.GT.compare( 7,3));
		assertTrue(MatchCondition.GT.compare( 7,3.0));
		assertTrue(MatchCondition.GT.compare("zzz", "aaa"));
		assertFalse(MatchCondition.GT.compare( 7,7));
		assertFalse(MatchCondition.GT.compare( 7,7.0));
		assertFalse(MatchCondition.GT.compare("aaa", "aaa"));
	}
	
	@Test
	public void testLE() {
		assertTrue(MatchCondition.LE.compare(3, 7));
		assertTrue(MatchCondition.LE.compare(3, 7.0));
		assertTrue(MatchCondition.LE.compare("aaa", "zzz"));
		assertFalse(MatchCondition.LE.compare( 7,3));
		assertFalse(MatchCondition.LE.compare( 7,3.0));
		assertFalse(MatchCondition.LE.compare("zzz", "aaa"));
		assertTrue(MatchCondition.LE.compare( 7,7));
		assertTrue(MatchCondition.LE.compare( 7,7.0));
		assertTrue(MatchCondition.LE.compare("aaa", "aaa"));
	}
	
	@Test
	public void testGE() {
		assertFalse(MatchCondition.GE.compare(3, 7));
		assertFalse(MatchCondition.GE.compare(3, 7.0));
		assertFalse(MatchCondition.GE.compare("aaa", "zzz"));
		assertTrue(MatchCondition.GE.compare( 7,3));
		assertTrue(MatchCondition.GE.compare( 7,3.0));
		assertTrue(MatchCondition.GE.compare("zzz", "aaa"));
		assertTrue(MatchCondition.GE.compare( 7,7));
		assertTrue(MatchCondition.GE.compare( 7,7.0));
		assertTrue(MatchCondition.GE.compare("aaa", "aaa"));
	}
	
	@Test
	public void testNE() {
		assertTrue(MatchCondition.NE.compare(3, 7));
		assertTrue(MatchCondition.NE.compare(3, 7.0));
		assertTrue(MatchCondition.NE.compare("aaa", "zzz"));
		assertTrue(MatchCondition.NE.compare( 7,3));
		assertTrue(MatchCondition.NE.compare( 7,3.0));
		assertTrue(MatchCondition.NE.compare("zzz", "aaa"));
		assertFalse(MatchCondition.NE.compare( 7,7));
		assertFalse(MatchCondition.NE.compare( 7,7.0));
		assertFalse(MatchCondition.NE.compare("aaa", "aaa"));
	}
}
