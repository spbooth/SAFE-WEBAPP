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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.CaseExpression.Clause;
import uk.ac.ed.epcc.webapp.jdbc.expr.NumberTableFactory.NumberTable;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */
public class NumberTableTest extends WebappTestBase {

	/**
	 * 
	 */
	public NumberTableTest() {
		// TODO Auto-generated constructor stub
	}

	@Before
	public void makeFixture() throws DataFault {
		NumberTableFactory fac = new NumberTableFactory(getContext());
		for(int i=0 ;i< 10 ; i++) {
			NumberTable n = fac.makeBDO();
			switch(i%3) {
			case 0:
				n.setNumber1(i * 10.0);
				n.setNumber2(-1000.0);
				break;
			case 1:
				
				n.setNumber1(-1000.0);
				n.setNumber2(i * 10.0);
				break;
			default:
				n.setNumber1(i * 10.0);
				n.setNumber2(i * 10.0);
				n.setNumber3(3.0);
			}
			
			n.commit();
		}
	}
	
	
	@Test
	public void testCaseExpr() throws DataException {
		NumberTableFactory fac = new NumberTableFactory(getContext());
		Clause<NumberTable, Double> clause1 = new CaseExpression.Clause<NumberTable,Double>(
				SQLExpressionMatchFilter.getFilter(fac.getTarget(),fac.getNumber1Expr(),MatchCondition.LT,fac.getNumber2Expr()),
				fac.getNumber2Expr());
		Clause<NumberTable, Double> clause2 = new CaseExpression.Clause<NumberTable,Double>(
				SQLExpressionMatchFilter.getFilter(fac.getTarget(),fac.getNumber1Expr(),MatchCondition.GT,fac.getNumber2Expr()),
				fac.getNumber1Expr());
		CaseExpression< NumberTable,Double> expr = 
				new CaseExpression< NumberTable,Double>(Double.class, 
						fac.getNumber3Expr(), 
						clause1,
						clause2
						
						);
		
		Double val = fac.getSum(null, expr);
		// 0 0 -1000      0
		// 1 -1000 10     10
		// 2 20 20 3      13
		// 3 30 -1000     43
		// 4 -1000 40     83
		// 5 50 50 3      86
		// 6 60 -1000     146
		// 7 -1000 70     216
		// 8 80 80 3      219
		// 9 90 -1000     309
			
		assertEquals("Select value", 309.0, val.doubleValue(),0.001);
	}
}
