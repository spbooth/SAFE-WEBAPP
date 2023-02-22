//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;


/** a numerical binary {@link SQLValue}.
 * 
 * Normally {@link BinaryExpression}s should be used in preference but this is needed if the arguments are {@link SQLValue}s but
 * not {@link SQLExpression}s
 * @see BinaryExpression
 * @author spb
 *
 */
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
   
   
	public final int add(StringBuilder sb, boolean qualify) {
		
		offset =a.add(sb, qualify);
		sb.append(" , ");
		return b.add(sb,qualify) + offset;
		
	}
  
    
    public final Number makeObject(ResultSet rs, int pos) throws DataException, SQLException{
    		Number obja = a.makeObject(rs, pos);
			Number objb = b.makeObject(rs, pos+offset);
//			Logger log = conn.getService(LoggerService.class).getLogger(getClass());
//			log.debug("Operate "+op.toString()+" "+
//			(obja==null?"null":obja.getClass().getName()+":"+obja.toString())+
//			" "+
//			(objb==null?"null":objb.getClass().getName()+":"+objb.toString()));
			return op.operate(obja, objb);
    }

	public final Class<Number> getTarget() {
		return Number.class;
	}
	@Override
	public final String toString(){
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
	public final SQLFilter getRequiredFilter() {
		SQLFilter a_fil = a.getRequiredFilter();
		SQLFilter b_fil = b.getRequiredFilter();
		if( a_fil == null ){
			return b_fil;
		}else{
			if( b_fil == null ){
				return a_fil;
			}
			SQLAndFilter fil = new SQLAndFilter(a_fil.getTag());
			fil.addFilter(a_fil);
			fil.addFilter(b_fil);
			return fil;
		}
	}

	public final List<PatternArgument> getParameters(List<PatternArgument> list) {
		list = a.getParameters(list);
		return b.getParameters(list);
	}


	/**
	 * @return the a
	 */
	protected SQLValue<? extends Number> getA() {
		return a;
	}


	/**
	 * @return the b
	 */
	protected SQLValue<? extends Number> getB() {
		return b;
	}
	@Override
	public int addGroup(StringBuilder sb, boolean qualify) {
		
		// use embedded groupings if needed
		int count = ((GroupingSQLValue)a).addGroup(sb, qualify);



		if( count == 0 ){
			count += ((GroupingSQLValue)b).addGroup(sb, qualify);
		}else{
			StringBuilder sb2 = new StringBuilder();
			int more = ((GroupingSQLValue)b).addGroup(sb2, qualify);
			if( more > 0 ){
				// need non-null from both clauses to add additional
				// seperator
				sb.append(" , ");
				sb.append(sb2);
			}
		}
		
		return count;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.GroupingSQLValue#getGroupParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getGroupParameters(List<PatternArgument> list) {
		GroupingSQLValue a = (GroupingSQLValue) getA();
		GroupingSQLValue b = (GroupingSQLValue) getB();

		list = a.getGroupParameters(list);


		list = b.getGroupParameters(list);

		return list;
	}


	@Override
	public boolean checkContentsCanGroup() {
		if( ! (a instanceof GroupingSQLValue)) {
			return false;
		}
		if( ! (b instanceof GroupingSQLValue)) {
			return false;
		}
		return true;
	}
	@Override
	public String getFilterTag() {
		String t = a.getFilterTag();
		if( t != null) {
			return t;
		}
		return b.getFilterTag();
	}
}