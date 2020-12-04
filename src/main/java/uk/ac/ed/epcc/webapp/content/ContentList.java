package uk.ac.ed.epcc.webapp.content;

import java.util.LinkedList;

public class ContentList<E>  implements UIGenerator, XMLGenerator{

	
	public ContentList(String tag) {
		super();
		this.tag = tag;
	}

	@Override
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		builder.clean(tag);
		builder.clean("(");
		builder.addObject(list);
		builder.clean(")");
		return builder;
	}

	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		builder.getSpan().clean(tag).clean("(").appendParent();
		builder.addObject(list);
		builder.getSpan().clean(")").appendParent();
		return builder;
	}

	public void add(E item) {
		list.add(item);
	}
	private final String tag;
	private final LinkedList<E> list=new LinkedList<E>();
}
