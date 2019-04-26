package uk.ac.ed.epcc.webapp.content;

public final class FormattedTransform<C,R> implements
		Table.Formatter<C,R> {

	public  Object convert(Table<C, R> t, C col, R row, Object raw) {
		if( raw instanceof String){
			return new FormattedGenerator(64,(String)raw);
		}
		return raw;
	}
	
}