//| Copyright - The University of Edinburgh 2011                            |
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
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

/** A {@link MultiInput} that also implements {@link ParseInput}.
 * Normally this is presented as a {@link MultiInput} but the {@link ParseInput}
 * functionality can be used to serialise the input value or read text input.
 * 
 * @param <V>
 * @param <I>
 */
public abstract class ParseMultiInput<V,I extends Input> extends MultiInput<V,I> implements
		ParseInput<V> {
	@Override
	public final <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitParseMultiInput(this);
	}
}