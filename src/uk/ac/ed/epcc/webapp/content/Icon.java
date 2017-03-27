package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
/** A {@link UIGenerator} that adds an icon image.
 * The image is specified as a url so the alt-text is used in a non-web context.
 * 
 * @author spb
 *
 */
public class Icon implements UIGenerator {

	/** 
	 * 
	 * @param conn {@link AppContext}
	 * @param text String alt-text
	 * @param image url of icon image
	 */
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
			p.attr("class","icon");
			p.attr("alt", text);
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
