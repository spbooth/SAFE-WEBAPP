// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
@uk.ac.ed.epcc.webapp.Version("$Id: SQLSelectValue.java,v 1.4 2014/09/15 14:30:24 spb Exp $")


public class SQLSelectValue<T> implements SQLValue<T> {
    private final Class<? super T> target;
    private final SQLValue<T> accessors[];
    private final int offsets[];
    
    public SQLSelectValue(Class<? super T> target, SQLValue<T> accessors[]){
    	this.target=target;
    	this.accessors=accessors;
    	offsets=new int[accessors.length];
    }
	
	public Class<? super T> getTarget() {
		return target;
	}

	
	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Select(");
		boolean seen=false;
		for(SQLValue<T> a: accessors){
			if( seen ){
				sb.append(",");
			}
			seen=true;
			sb.append(a.toString());
		}
		sb.append(")");
		return sb.toString();
	}

	public int add(StringBuilder sb, boolean qualify) {
		int count=0;
		for(int i=0;i<accessors.length;i++){
			if( i > 0){
				sb.append(" , ");
			}
			offsets[i]=count;
			count += accessors[i].add(sb, qualify);
		}
		
		return count;
	}
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		for(int i=0;i<accessors.length;i++){
			list = accessors[i].getParameters(list);
		}
		return list;
	}
	
	public T makeObject(ResultSet rs, int pos) throws DataException {
		for(int i=0;i<accessors.length;i++){
			T tmp = accessors[i].makeObject(rs, pos+offsets[i]);
			if( tmp != null ){
				return tmp;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public SQLFilter getRequiredFilter() {
		SQLAndFilter required = null;
		for(int i=0;i<accessors.length;i++){
			SQLFilter tmp = accessors[i].getRequiredFilter();
			if( tmp != null){
				if(required==null){
					required=new SQLAndFilter(tmp.getTarget());
				}
				required.addFilter(tmp);
			}
		}
		return required;
	}

}