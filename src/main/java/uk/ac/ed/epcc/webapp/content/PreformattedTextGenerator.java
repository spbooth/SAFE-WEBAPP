package uk.ac.ed.epcc.webapp.content;

public class PreformattedTextGenerator implements XMLGenerator {

	private String text;

	public PreformattedTextGenerator(String text) {
		this.text = text;
	}
	
	@Override
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) 
	{
		builder.open("pre");
		builder.clean(text);
		builder.close();
		return builder;
	}
	
	public String toString(){
		return text;
	}
	
}