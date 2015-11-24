// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
@uk.ac.ed.epcc.webapp.Version("$Id: BinarySQLValue.java,v 1.5 2014/09/15 14:30:23 spb Exp $")


public class BinarySQLValue implements GroupingSQLValue<Number> {
	private AppContext conn;
    private final SQLValue<? extends Number> a,b;
    private final Operator op;
    private int offset;
    public BinarySQLValue(AppContext conn,SQLValue<? extends Number> a, Operator op,SQLValue<? extends Number> b) {
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
  
    
    public Number makeObject(ResultSet rs, int pos) throws DataException{
    		Number obja = a.makeObject(rs, pos);
			Number objb = b.makeObject(rs, pos+offset);
//			Logger log = conn.getService(LoggerService.class).getLogger(getClass());
//			log.debug("Operate "+op.toString()+" "+
//			(obja==null?"null":obja.getClass().getName()+":"+obja.toString())+
//			" "+
//			(objb==null?"null":objb.getClass().getName()+":"+objb.toString()));
			return op.operate(obja, objb);
    }

	public Class<Number> getTarget() {
		return Number.class;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("( ");
		sb.append(a.toString());
		sb.append(" ");
		// Use Enum to show this is done in java.
		sb.append(op.toString());
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

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.GroupingSQLValue#addGroup(java.lang.StringBuilder, boolean)
	 */
	public int addGroup(StringBuilder sb, boolean qualify) {
		// use embedded groupings if needed
		int count=0;
		if( a instanceof GroupingSQLValue){
			count = ((GroupingSQLValue)a).addGroup(sb, qualify);
		}else{
			count = a.add(sb, qualify);
		}
		
		if( b instanceof GroupingSQLValue){
			if( count == 0 ){
				count += ((GroupingSQLValue)b).addGroup(sb, qualify);
			}else{
				StringBuilder sb2 = new StringBuilder();
				int more = ((GroupingSQLValue)b).addGroup(sb, qualify);
				if( more > 0 ){
					// need non-null from both clauses to add additional
					// seperator
					sb.append(" , ");
					sb.append(sb2);
				}
			}
		}else{
			if( count > 0 ){
				sb.append(" , ");
			}
			count += b.add(sb, qualify);
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.GroupingSQLValue#getGroupParameters(java.util.List)
	 */
	public List<PatternArgument> getGroupParameters(List<PatternArgument> list) {
		if( a instanceof GroupingSQLValue){
			list = ((GroupingSQLValue)a).getGroupParameters(list);
		}else{
			list = a.getParameters(list);
		}
		if( b instanceof GroupingSQLValue){
			list = ((GroupingSQLValue)b).getGroupParameters(list);
		}else{
			list = b.getParameters(list);
		}
		return list;
	}
}