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

/** A {@link XMLGenerator} that adds a css style  depending on the value being greater than zero
 * 
 * 
 * @see Table.NumberFormatGenerator
 * @see PositiveFormatTransform
 * @author spb
 *
 */

public class PositiveFormatGenerator implements XMLGenerator{

	public PositiveFormatGenerator(Number data) {
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
			builder.attr("class","val_null");
		}else{
			if( data.doubleValue() > 0.0 ) {
				builder.attr("class","val_positive");
			}else if( data.doubleValue() == 0.0) {
				builder.attr("class","val_zero");
			}else {
				builder.attr("class","val_negative");
			}
			builder.clean(nf.format(data));
		}
		return builder;
	}
	@Override
	public String toString() {
		// Want string representation to make sense
		if(data == null){
			return "";
		}else{
			return nf.format(data);
		}
	}
}