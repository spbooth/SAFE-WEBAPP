//| Copyright - The University of Edinburgh 2017                            |
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

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import uk.ac.ed.epcc.webapp.forms.Identified;

/** A {@link Format} that understands the Webapp interfaces for string generation
 * 
 * @see HtmlContentFormat
 * @author spb
 *
 */
public class TextContentFormat extends Format {

	/**
	 * 
	 */
	public TextContentFormat() {
		
	}

	/* (non-Javadoc)
	 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {
		if( arg0 instanceof Identified) {
			arg1.append(((Identified)arg0).getIdentifier());
		}else if( arg0 instanceof Iterable) {
			for( Object o : (Iterable) arg0) {
				format(o,arg1,arg2);
				arg1.append(" ");
			}
		}else if( arg0 instanceof Object[]) {
			for( Object o : (Object[]) arg0) {
				format(o,arg1,arg2);
				arg1.append(" ");
			}
		}
		return arg1;
	}

	/* (non-Javadoc)
	 * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
	 */
	@Override
	public Object parseObject(String arg0, ParsePosition arg1) {
		// Not supported null indicates error
		return null;
	}

}
