package uk.ac.ed.epcc.webapp.content;

public final class FormattedTransform implements
		Transform {

	@Override
	public Object convert(Object raw) {
		if( raw instanceof String){
			return new FormattedGenerator(64,(String)raw);
		}
		return raw;
	}
	
}