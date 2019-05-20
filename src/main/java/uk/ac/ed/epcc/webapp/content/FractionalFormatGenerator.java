//| Copyright - The University of Edinburgh 2014                            |
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

/** A {@link XMLGenerator} that adds a css style according to the factional value.
 * 
 * 
 * @see Table.NumberFormatGenerator
 * @see FractionalFormatTransform
 * @author spb
 *
 */

public class FractionalFormatGenerator implements XMLGenerator{

	public FractionalFormatGenerator(Number data) {
		super();
		this.nf = NumberFormat.getInstance();
		this.nf.setMaximumFractionDigits(2);
		this.nf.setMinimumFractionDigits(2);
		this.nf.setMinimumIntegerDigits(1);
		this.data = data;
	}
	private final NumberFormat nf;
	private final Number data;
	
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		builder.attr("numeric", "true");
		if( data == null){
			builder.attr("class","frac_null");
			builder.clean(nf.format(0.0));
		}else{
			int frac = (int)(10.0 * data.doubleValue());
			if( frac > 10){
				frac=10;
			}
			builder.attr("class","frac_"+frac);
			builder.clean(nf.format(data));
		}
		return builder;
	}
	@Override
	public String toString() {
		// Want string representation to make sense
		if(data == null){
			return nf.format(0.0);
		}else{
			return nf.format(data);
		}
	}
}