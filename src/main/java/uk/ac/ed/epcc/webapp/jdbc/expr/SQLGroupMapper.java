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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.ResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
/** A  base class for ResultMappers that use SQL Group-By queries 
 * there are methods to add various select clauses to the target string.
 * @author spb
 * @see GroupingSQLValue
 * @param <O> Type being produced
 */
public abstract class SQLGroupMapper<O> extends AbstractContexed implements ResultMapper<O> {
	// This is the list of key fields that need to be output in the GROUP BY clasue
	    private List<GroupingSQLValue> key_list;
	    private List<SQLValue> target_list; // either Fields  or strings that must be concatenated to make
	                                                     // target clause this also includes the key fields
	   
	    private int start_pos[]; // start positions of targets in resultset
	    protected boolean qualify=false;
	    protected boolean use_alias=false;
	    @Override
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
	    	super(c);
	    	key_list = new LinkedList<>(); 
	    	target_list=new LinkedList<>();
	    }
	    
	    
	    
	    protected Object getTargetObject(int index, ResultSet rs) throws DataException, SQLException{
	    	return target_list.get(index).makeObject(rs, start_pos[index]);
	    }
	    
	    /** add a field both as a clause and as an group by key
	     * 
	     * Note the {@link SQLValue} can implement {@link GroupingSQLValue} to customise behaviours. Literal constants 
	     * are not legal in group-by clauses so {@link GroupingSQLValue} must be implemented when the SQL representation is a constant.
	     * @param exp
	     * @param name 
	     * @throws InvalidKeyException 
	     */
	    public  void addKey(GroupingSQLValue<?> exp, String name) throws InvalidKeyException{
	    		if( ! exp.checkContentsCanGroup()) {
	    			throw new InvalidKeyException("Key does not support Group By");
	    		}
	    	    key_list.add((GroupingSQLValue) addClause(exp,name));
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
	    	if( expr instanceof WrappedSQLExpression) {
	    		expr = ((WrappedSQLExpression<T>)expr).getSQLValue();
	    	}
	    	if(use_alias &&  name != null && orig instanceof SQLExpression ){
	    		expr=new AliasSQLValue<>((SQLExpression<T>)orig,name);
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
	    	addClause(FuncExpression.apply(getContext(),SQLFunc.SUM,Number.class,field),name);
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
	    public final void addMinNumber(SQLExpression<? extends Number> field, String name){
	    	if( field == null ){
	    		return;
	    	}
	    	addClause(FuncExpression.apply(getContext(),SQLFunc.MIN,Number.class,field),name);
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
	    	addClause(FuncExpression.apply(getContext(),SQLFunc.MIN,Date.class,field),name);
	    }
	    /** Add a column max to the table output
	     * 
	     * @param field   field to max
	     * @param name  name for result column
	     */
	    public final void addMaxNumber(SQLExpression<? extends Number> field, String name){
	    	if( field == null ){
	    		return;
	    	}
	    	addClause(FuncExpression.apply(getContext(),SQLFunc.MAX,Number.class,field),name);
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
	    	addClause(FuncExpression.apply(getContext(),SQLFunc.MAX,Date.class,field),name);
	    }
	    public final void addCount(String name){
	    	addClause(FuncExpression.apply(getContext(),SQLFunc.COUNT,Number.class,null),name);
	    }
	    /** Add a column counting distinct values. 
	     * 
	     * We allow SQLValues so we can count references but we are counting the underlying values 
	     * the count will be too high if multiple values are mapped to the same result
	     * 
	     * @param expr
	     * @param name
	     */
	    public final <T> void addCount(SQLValue<T> expr,String name){
	    	addClause(new CountDistinctExpression<>(expr), name);
	    }
		@Override
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
		@Override
		public List<PatternArgument> getTargetParameters(List<PatternArgument> list) {
			for(SQLValue<?> o : target_list){
				list = o.getParameters(list);
			}
			return list;
		}
		@Override
		public List<PatternArgument> getModifyParameters(List<PatternArgument> list) {
			for(GroupingSQLValue<?> o : key_list){
				list = o.getGroupParameters(list);
			}
			return list;
		}
		/** add group by clauses corresponding to key list
		 * @return true is any added.
		 * @throws CannotGroupException 
		 */
		protected boolean addKeyList(StringBuilder sb)  {
			boolean seen=false;
			StringBuilder tmp = new StringBuilder();
			for(GroupingSQLValue<?> o : key_list){
				tmp.setLength(0);
				if(seen){
					tmp.append(",");
				}

				if( o.addGroup(tmp, qualify) > 0 ){
					sb.append(tmp);
					seen=true;
				}

			}
			return seen;
		}
		@Override
		public final String getModify() {
			
			StringBuilder modify = new StringBuilder();
			if( hasKeys() ){
				modify.append(" GROUP BY ");
				if( ! addKeyList(modify)){
					// actually no fields
					return "";
				}
			}
	        return modify.toString();
		}
		private boolean hasKeys() {
			
			return ! key_list.isEmpty();
		}
		@Override
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
}