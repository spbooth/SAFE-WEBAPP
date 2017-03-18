package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.servlet.ServletService;

public class Icon implements UIGenerator {

	public Icon(AppContext conn,String text, String image) {
		super();
		this.conn=conn;
		
		this.text = text;
		this.image = image;
	}

	private AppContext conn;
	private final String text;
	private final String image;
	

	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		if( builder instanceof HtmlPrinter){
			HtmlPrinter p = (HtmlPrinter) builder;
			p.open("img");
			p.attr("alt", text);
			p.attr("src",conn.getService(ServletService.class).encodeURL(image));
			p.close();
		}else{
			builder.addText(text);
		}
		return builder;
	}
	public String toString(){
		return text;
	}

}
