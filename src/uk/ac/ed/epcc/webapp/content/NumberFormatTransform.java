//| Copyright - The University of Edinburgh 2013                            |
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

import java.text.NumberFormat;


/**
 * Format numerical cells of a Table using a NumberFormat.
 * 
 * @author spb
 * 
 */
public class NumberFormatTransform implements NumberTransform {
	private final NumberFormat nf;
	private final Object use_null;
	public NumberFormatTransform(NumberFormat f) {
		nf = f;
		use_null=0.0;
	}
	public NumberFormatTransform(NumberFormat f,Object use_null) {
		nf = f;
		this.use_null=use_null;
	}

	public Object convert(Object old) {
		if( old == null){
			old = use_null;
		}
		if( old instanceof Number){
			return nf.format((Number) old);
		}
		return old;
	}
	

	

}