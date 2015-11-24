// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
@uk.ac.ed.epcc.webapp.Version("$Id: CompareSQLValue.java,v 1.1 2015/07/20 15:54:49 spb Exp $")


public class CompareSQLValue<C extends Comparable> implements SQLValue<Boolean> {
	private AppContext conn;
    private final SQLValue<C> a,b;
    private final MatchCondition op;
    private int offset;
    public CompareSQLValue(AppContext conn,SQLValue<C> a, MatchCondition op,SQLValue<C> b) {
    	this.conn=conn;
    	this.a=a;
    	this.b=b;
    	this.op=op;
    }
   
	public int add(StringBuilder sb, boolean qualify) {
		
		offset =a.add(sb, qualify);
		sb.append(" , ");
		return b.add(sb,qualify) + offset;
		
	}
  
    
    public Boolean makeObject(ResultSet rs, int pos) throws DataException{
    		C obja = a.makeObject(rs, pos);
			C objb = b.makeObject(rs, pos+offset);
			return op.compare(obja, objb);
    }

	public Class<Boolean> getTarget() {
		return Boolean.class;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("( ");
		sb.append(a.toString());
		sb.append(" ");
		if( op == null){
			sb.append("==");
		}else{
			sb.append(op.toString());
		}
		sb.append(" ");
		sb.append(b.toString());
		sb.append(")");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public SQLFilter getRequiredFilter() {
		SQLFilter a_fil = a.getRequiredFilter();
		SQLFilter b_fil = b.getRequiredFilter();
		if( a_fil == null ){
			return b_fil;
		}else{
			if( b_fil == null ){
				return a_fil;
			}
			SQLAndFilter fil = new SQLAndFilter(a_fil.getTarget());
			fil.addFilter(a_fil);
			fil.addFilter(b_fil);
			return fil;
		}
	}

	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list = a.getParameters(list);
		return b.getParameters(list);
	}

}