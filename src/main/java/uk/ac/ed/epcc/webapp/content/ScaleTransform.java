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
package uk.ac.ed.epcc.webapp.content;

/** A {@link Transform} to perform simple numerical scaling.
 * @author spb
 *
 */

public class ScaleTransform implements Transform {
	public ScaleTransform(double sc) {
		super();
		this.scale = sc;
	}

	private final double scale;

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.Transform#convert(java.lang.Object)
	 */
	@Override
	public Object convert(Object old) {
		if( old != null && old instanceof Number){
			return scale * ((Number)old).doubleValue();
		}
		return old;
	}
}