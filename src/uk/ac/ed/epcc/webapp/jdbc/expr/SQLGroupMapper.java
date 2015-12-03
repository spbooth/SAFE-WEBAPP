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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
/** A  base class for ResultMappers that use SQL Group-By queries 
 * there are methods to add various select clauses to the target string.
 * @author spb
 *
 * @param <O> Type being produced
 */
public abstract class SQLGroupMapper<O> implements ResultMapper<O>, Contexed {
	
	    private final AppContext c;
	// This is the list of key fields that need to be output in the GROUP BY clasue
	    private List<SQLValue> key_list;
	    private List<SQLValue> target_list; // either Fields  or strings that must be concatenated to make
	                                                     // target clause this also includes the key fields
	   
	    private int start_pos[]; // start positions of targets in resultset
	    protected boolean qualify=false;
	    protected boolean use_alias=false;
	    public boolean setQualify(boolean qualify) {
			boolean old = this.qualify;
			this.qualify = qualify;
			return old;
		}
	    public void setUseAlias(boolean val){
	    	this.use_alias=val;
	    }
	    /** Make a TableMapper
	     * @param c {@link AppContext}
	     * 
	     */
	    public SQLGroupMapper(AppContext c){
	    	this.c=c;
	    	key_list = new LinkedList<SQLValue>(); 
	    	target_list=new LinkedList<SQLValue>();
	    }
	    
	    
	    
	    protected Object getTargetObject(int index, ResultSet rs) throws DataException{
	    	return target_list.get(index).makeObject(rs, start_pos[index]);
	    }
	    
	    /** add a field both as a clause and as an group by key
	     * 
	     * @param exp
	     * @param name 
	     */
	    public  void addKey(SQLValue<?> exp, String name){
	    	   
	    	    key_list.add(addClause(exp,name));
	    }
	    /** Add a column to the table output
	     * @param <T> java type produced
	     * 
	     * @param orig   Expression to evaluate
	     * @param name  name of resultant column
	     * @return SQLValue used to refer to this clause
	     */
	    public  <T> SQLValue<T> addClause(SQLValue<T> orig, String name){
	    	SQLValue<T> expr=orig;
	    	if(use_alias &&  name != null && orig instanceof SQLExpression ){
	    		expr=new AliasSQLValue<T>((SQLExpression<T>)orig,name);
	    	}
	    	target_list.add(expr);
	    	return expr;

	    }
	    
	    /** Add a column sum to the table output
	     * 
	     * @param field   field to sum
	     * @param name  name for result column
	     */
	    public final void addSum(SQLExpression<? extends Number> field, String name){
	    	if( field == null ){
	    		return;
	    	}
	    	addClause(FuncExpression.apply(c,SQLFunc.SUM,Number.class,field),name);
	    }
	    
	    /** Add a column average to the table output
	     * 
	     * @param field   field to average
	     * @param name  name for result column
	     */
	    public final void addAverage(SQLExpression<? extends Number> field, String name){
	    	if( field == null ){
	    		return;
	    	}
	    	addClause(new SQLAverageValue(field),name);
	    	//addClause(new FuncExpression<Number>(SQLFunc.AVG,Number.class,field),name);
	    }
	    
	    /** Add a column min to the table output
	     * 
	     * @param field   field to min
	     * @param name  name for result column
	     */
	    public final void addMin(SQLExpression<? extends Number> field, String name){
	    	if( field == null ){
	    		return;
	    	}
	    	addClause(FuncExpression.apply(c,SQLFunc.MIN,Number.class,field),name);
	    }
	    /** Add a column min date to table output
	     * 
	     * @param field field to min
	     * @param name name for result column
	     */
	    public final  void addMinDate(SQLExpression<? extends Date> field, String name){
	    	if( field == null ){
	    		return;
	    	}
	    	addClause(FuncExpression.apply(c,SQLFunc.MIN,Date.class,field),name);
	    }
	    /** Add a column max to the table output
	     * 
	     * @param field   field to max
	     * @param name  name for result column
	     */
	    public final void addMax(SQLExpression<? extends Number> field, String name){
	    	if( field == null ){
	    		return;
	    	}
	    	addClause(FuncExpression.apply(c,SQLFunc.MAX,Number.class,field),name);
	    }
	    /** Add a column max date to the table output
	     * 
	     * @param field   field to max
	     * @param name  name for result column
	     */
	    public final void addMaxDate(SQLExpression<? extends Date> field, String name){
	    	if( field == null ){
	    		return;
	    	}
	    	addClause(FuncExpression.apply(c,SQLFunc.MAX,Date.class,field),name);
	    }
	    public final void addCount(String name){
	    	addClause(FuncExpression.apply(c,SQLFunc.COUNT,Number.class,null),name);
	    }
	    /** Add a column counting distinct values
	     * 
	     * @param expr
	     * @param name
	     */
	    public final <T> void addCount(SQLExpression<T> expr,String name){
	    	addClause(new CountDistinctExpression<T>(expr), name);
	    }
		public String getTarget() {
			start_pos = new int[target_list.size()];
			int pos=1,i=0;
			StringBuilder sb = new StringBuilder();
			boolean sep=false;
			for(SQLValue o : target_list){
				if(sep){
					sb.append(",");
				}
				start_pos[i++]=pos;
				pos += o.add(sb,qualify);
				sep=true;
			}
			return sb.toString();
		}
		public List<PatternArgument> getTargetParameters(List<PatternArgument> list) {
			for(SQLValue<?> o : target_list){
				list = o.getParameters(list);
			}
			return list;
		}
		public List<PatternArgument> getModifyParameters(List<PatternArgument> list) {
			for(SQLValue<?> o : key_list){
				if( o instanceof GroupingSQLValue){
					list = ((GroupingSQLValue<?>)o).getGroupParameters(list);
				}else{
					list = o.getParameters(list);
				}
			}
			return list;
		}
		protected final void  addKeyList(String seperator,StringBuilder sb) {
			boolean sep=false;
			for(SQLValue<?> o : key_list){
				if(sep){
					sb.append(seperator);
				}
				if( o instanceof GroupingSQLValue){
				  ((GroupingSQLValue)o).addGroup(sb, qualify);	
				}else{
				   o.add(sb,qualify);
				}
				sep=true;
			}
		}
		public final String getModify() {
			
			StringBuilder modify = new StringBuilder();
			if( hasKeys() ){
				modify.append(" GROUP BY ");
				addKeyList(",",modify);
			}
	        return modify.toString();
		}
		private boolean hasKeys() {
			
			return ! key_list.isEmpty();
		}
		@SuppressWarnings("unchecked")
		public SQLFilter getRequiredFilter() {
			SQLAndFilter fil=null;
			for(SQLValue v : target_list){
				SQLFilter f = v.getRequiredFilter();
				if( f != null ){
					if( fil == null ){
						fil = new SQLAndFilter(f.getTarget());
					}
					fil.addFilter(f);
				}
			}
			return fil;
		}
		public final AppContext getContext(){
			return c;
		}
}