package uk.ac.ed.epcc.webapp.forms.html;

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.content.XMLGenerator;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;

public class InputGenerator implements XMLGenerator{
	private final boolean use_post;
	private final Map post_params;
	private final Input i;
	private final AppContext conn;
	public InputGenerator(AppContext conn,Input i, boolean use_post,Map post_params){
		this.i=i;
		this.use_post=use_post;
		this.post_params=post_params;
		this.conn=conn;
	}
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		EmitHtmlInputVisitor vis = new EmitHtmlInputVisitor(conn,builder, use_post, post_params,null);
		try {
			i.accept(vis);
		} catch (Exception e) {
			conn.error(e,"Error formatting input");
		}
		return builder;
	}
}
