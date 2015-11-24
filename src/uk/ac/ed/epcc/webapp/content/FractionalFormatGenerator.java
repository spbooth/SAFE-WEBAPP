// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.content;

import java.text.NumberFormat;

/** A {@link XMLGenerator} that adds a css style according to the factional value.
 * 
 * 
 * @see Table.NumberFormatGenerator
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
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
			builder.attr("null","true");
			builder.attr("class","frac_0");
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
