package uk.ac.ed.epcc.webapp.content;

public class BooleanWrapper implements XMLGenerator {
	public BooleanWrapper(boolean val) {
		super();
		this.val = val;
	}


	public final boolean val; 
	
	
	@Override
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		if( val ) {
			builder.clean("\u2713");
		}else {
			builder.clean("\u2717");
		}
		return builder;
	}

	public String toString() {
		return val ? "yes":"no";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (val ? 1231 : 1237);
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
		BooleanWrapper other = (BooleanWrapper) obj;
		if (val != other.val)
			return false;
		return true;
	}
}
