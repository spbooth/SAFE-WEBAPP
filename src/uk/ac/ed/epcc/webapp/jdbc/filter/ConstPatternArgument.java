// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
@uk.ac.ed.epcc.webapp.Version("$Id: ConstPatternArgument.java,v 1.4 2014/09/15 14:30:25 spb Exp $")

/** A constant value {@link PatternArgument}.
 * 
 * @author spb
 *
 * @param <T>
 */
public class ConstPatternArgument<T> implements PatternArgument {

	private final Class<? super T> target;
	private final T value;
	private final boolean log;
	public ConstPatternArgument(Class<? super T> target, T value){
		this(target,value,true);
	}
	public ConstPatternArgument(Class<? super T> target, T value, boolean log){
		this.target=target;
		this.value=value;
		this.log=log;
	}
	public void addArg(PreparedStatement stmt, int pos) throws SQLException {
		stmt.setObject(pos,value);
	}

	public String getField() {
		return "const:"+target.getCanonicalName();
	}

	public Object getArg() {
		
		return value;
	}

	public boolean canLog() {
		return log;
	}

}