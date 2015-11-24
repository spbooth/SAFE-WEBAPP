package uk.ac.ed.epcc.webapp.content;


/** UIGenerator that adds a formatted string
 * 
 * @author spb
 *
 */
public class FormattedGenerator implements UIGenerator {
    public FormattedGenerator(int len, String val) {
		super();
		this.len = len;
		this.val = val;
	}
	private final int len;
    private final String val;
	
	public ContentBuilder addContent(ContentBuilder builder) {
		if(  val.contains("\n")){
			builder.cleanFormatted(len, val);
		}else{
			builder.addText(val);
		}
		return builder;
	}

}
