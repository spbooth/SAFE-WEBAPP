//| Copyright - The University of Edinburgh 2012                            |
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
package uk.ac.ed.epcc.webapp.forms.transition;

import static org.junit.Assert.*;
/**
 * @author spb
 *
 */

public class TestTransitionFactoryVisitor<R,T,K> implements
		TransitionFactoryVisitor<R, T, K> {

	public TestTransitionFactoryVisitor(boolean expect_provider) {
		super();
		this.expect_provider = expect_provider;
	}

	private final boolean expect_provider;
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor#visitTransitionProvider(uk.ac.ed.epcc.webapp.forms.transition.TransitionProvider)
	 */
	public R visitTransitionProvider(TransitionProvider<K, T> prov) {
		assertNotNull(prov);
		assertTrue(expect_provider);
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryVisitor#visitPathTransitionProvider(uk.ac.ed.epcc.webapp.forms.transition.PathTransitionProvider)
	 */
	public R visitPathTransitionProvider(PathTransitionProvider<K, T> prov) {
		assertNotNull(prov);
		assertFalse(expect_provider);
		return null;
	}

}