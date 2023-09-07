package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
/** A {@link UIGenerator} that adds text with an additional icon
 * 
 * @author spb
 *
 */
public class Icon implements UIGenerator {

	/** 
	 * 
	 * @param conn {@link AppContext}
	 * @param text String text to show next to icon
	 * @param image url of icon image
	 * @param alt alt-text for image
	 */
	public Icon(AppContext conn,String text, String image, String alt) {
		super();
		this.conn=conn;
		
		this.text = text;
		this.image = image;
		this.alt = alt;
	}

	private AppContext conn;
	private final String text;
	private final String image;
	private final String alt;
	

	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		if( builder instanceof HtmlPrinter){
			HtmlPrinter p = (HtmlPrinter) builder;
			addImage(p);
		}else{
			ExtendedXMLBuilder printer = builder.getSpan();
			addImage(printer);
			printer.appendParent();
		}
		return builder;
	}
	/**
	 * @param p
	 */
	private void addImage(ExtendedXMLBuilder p) {
		ServletService service = conn.getService(ServletService.class);
		if( service != null){
			p.open("img");
			p.addClass("icon");
			if( alt != null && ! alt.isEmpty()) {
				p.attr("alt", alt);
			}
			p.attr("src",service.encodeURL(image));
			p.close();
		}
		//}else{
			p.clean(text);
		//}
	}
	public String toString(){
		return text;
	}

}
