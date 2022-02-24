package uk.ac.ed.epcc.webapp.content;

public class DetailsWrapper implements UIGenerator {
	public DetailsWrapper(Object header, Object body) {
		super();
		this.header = header;
		this.body = body;
	}


	private final Object header;
	private final Object body;
	

	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		ContentBuilder d = builder.getDetails(header);
		d.addObject(body);
		return d.addParent();
	}

	@Override
	public String toString() {
		return header.toString()+": "+body.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((header == null) ? 0 : header.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DetailsWrapper other = (DetailsWrapper) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (header == null) {
			if (other.header != null)
				return false;
		} else if (!header.equals(other.header))
			return false;
		return true;
	}

}
