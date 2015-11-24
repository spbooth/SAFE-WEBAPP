package uk.ac.ed.epcc.webapp.content;


/** XMLGenerator that maps spaces onto non-breaking spaces if
 * the generator is actually an ExtendedXMLGenerator.
 * 
 * @author spb
 *
 */
public class HtmlSpaceGenerator implements XMLGenerator {
	@Override
	public int hashCode() {
		return value.hashCode();
	}


	@Override
	public String toString() {
		return value;
	}


	public HtmlSpaceGenerator(String value) {
		super();
		this.value = value;
	}


	private final String value;
	 

	
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		if( builder instanceof ExtendedXMLBuilder){
			 String input = value;	
				
				for(int i=0 ;i< input.length(); i++){
					char c= input.charAt(i);
					if(  c == ' '){
						((ExtendedXMLBuilder)builder).nbs();
					}else{
						builder.clean(c);
					}
				}
		}else{
			builder.clean(value);
		}
		
		return builder;
	}

}
