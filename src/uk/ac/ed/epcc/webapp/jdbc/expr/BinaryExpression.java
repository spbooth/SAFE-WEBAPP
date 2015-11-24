// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
@uk.ac.ed.epcc.webapp.Version("$Id: BinaryExpression.java,v 1.8 2014/12/04 21:30:48 spb Exp $")


public final class BinaryExpression implements SQLExpression<Number> {
	public static final Feature SIMPLIFY_EXPRESSION = new Feature("sqlexpression.simplify",true,"simplify SQL exopressions");
	
	/** A factory method for binary {@link SQLExpression}s. 
	 * 
	 * Usually this will return a {@link BinaryExpression} but may return some other 
	 * expression as a result of on-the-fly simplification if the {@link AppContext} parameter is non-null 
	 * and the appropriate feature is turned on.
	 * 
	 * 
	 * 
	 * @param conn {@link AppContext} used to read config, null suppresses simplification
	 * @param a {@link SQLExpression} first argument
	 * @param op {@link Operator} to apply
	 * @param b {@link SQLExpression} second argument.
	 * @return {@link SQLExpression}
	 * 
	 * 
	 */
	public static SQLExpression<Number> create(AppContext conn,SQLExpression<?> a, Operator op,SQLExpression<?> b){
		if(conn != null &&  SIMPLIFY_EXPRESSION.isEnabled(conn)){
			return create(a,op,b);
		}
		return new BinaryExpression(a, op, b);
	}
	/** A factory method for binary {@link SQLExpression}s. 
	 * 
	 * Usually this will return a {@link BinaryExpression} but may return some other 
	 * expression as a result of on-the-fly simplification
	 * @param a {@link SQLExpression} first argument
	 * @param op {@link Operator} to apply
	 * @param b {@link SQLExpression} second argument.
	 * @return {@link SQLExpression}
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static SQLExpression<Number> create(SQLExpression<?> a, Operator op,SQLExpression<?> b){
		// Do simple level one optimisations aimed at simplifying/eliminating constants. 
		// SQL optimiser can probably do this but it makes things easier to read.
		// If we need to make a new BinaryExpression
		// call create recursively to expose new optimisations
		
		
		// merge constant expressions
		if( a instanceof ConstExpression && b instanceof ConstExpression){
			ConstExpression<Number,?> ca = (ConstExpression) a;
			ConstExpression<Number,?> cb = (ConstExpression) b;
			return new ConstExpression(Number.class, op.operate(ca.getValue(), cb.getValue()));
		}
		
		if( a.equals(b)){
			if( op == Operator.SUB){
				return new ConstExpression<Number, Object>(Number.class, 0);
			}
			if( op == Operator.DIV){
				return new ConstExpression<Number, Object>(Number.class, 1.0);
			}
		}
		// b is a constant
		if(  b instanceof ConstExpression){

			ConstExpression cb = (ConstExpression) b;
			Number vb = (Number) cb.getValue();
			// trivial addition +/- 0
			if( ( op == Operator.ADD || op == Operator.SUB) &&  vb.doubleValue() == 0.0 ){
				return (SQLExpression<Number>) a;
			}
			// trivial multiplication *1 or /1
			if( ( op == Operator.MUL || op == Operator.DIV) &&  vb.doubleValue() == 1.0 ){
				return (SQLExpression<Number>) a;
			}
			if( op == Operator.MUL && vb.doubleValue() == 0.0){
				return (SQLExpression<Number>)b;
			}
			if( a instanceof BinaryExpression){
				BinaryExpression ba = (BinaryExpression) a;

				// tail constant folding second op could be div (A*B)/C = A*(B/C)
				if( ba.b instanceof ConstExpression && ba.op.associates(op)
						){
					return create(ba.a, ba.op, create(ba.b,op,cb));
				}
				if( ba.a instanceof ConstExpression){
					// merge const under re-order
					if( ba.op.associates(op) && ba.op.commutes()){
						return create(ba.b,ba.op,create(ba.a,op,cb));
					}
					
				}
			}
		}
		// a is a constant
		if( a instanceof ConstExpression ){
			
			ConstExpression ca = (ConstExpression) a;
			Number va = (Number) ca.getValue();
			// trivial addition
			if( ( op == Operator.ADD ) &&  va.doubleValue() == 0.0 ){
				return (SQLExpression<Number>) b;
			}
			// trivial multiplication
			if( ( op == Operator.MUL ) &&  va.doubleValue() == 1.0 ){
				return (SQLExpression<Number>) b;
			}
			// multiply by zero
			if( op == Operator.MUL && va.doubleValue() == 0.0){
				return (SQLExpression<Number>) a;
			}
			if( b instanceof BinaryExpression){
				BinaryExpression bb = (BinaryExpression) b;

				// head constant folding second op could be div A*(B/C) = (A*B)/C
				if( bb.a instanceof ConstExpression && op.associates(bb.op)
						){
					return create(create(ca,op,bb.a),bb.op,bb.b);
				}
				// re-ordering optimisations
				if( bb.b instanceof ConstExpression){
					// merge const factors
					if( bb.op.commutes() && op.associates(bb.op)){
						return create(create(ca,op,bb.b),op,bb.a);
					}
					// merging mixed add/subtractions is also possible but need to watch sign changes
					// when re-ordering
				}
			}
//			if( op == Operator.ADD || op == Operator.MUL){
//				// move constants to second position for commuting operators to maximise merge
//				return create(b,op,a);
//			}
		}
		// Common sub-expression in 2 binaries
		if( a instanceof BinaryExpression  && b instanceof BinaryExpression){
			BinaryExpression ba = (BinaryExpression) a;
			BinaryExpression bb = (BinaryExpression) b;
            if( ba.op == bb.op){
                 if( ba.op.leftDistributive(op) && ba.a.equals(bb.a)){
                	 return create( ba.a, ba.op, create(ba.b, op, bb.b));
                 }
                 if( ba.op.leftDistributive(op) && ba.op.commutes() && ba.a.equals(bb.b)){
                	 return create( ba.a, ba.op, create(ba.b, op, bb.a));
                 }
            	 if( ba.op.rightDistributive(op) && ba.b.equals(bb.b)){
            		 return create( create(ba.a, op, bb.a ), ba.op, ba.b);
            	 }
            	 if( ba.op.rightDistributive(op) && ba.op.commutes() && ba.b.equals(bb.a)){
            		 return create( create(ba.a, op, bb.b ), ba.op, ba.b);
            	 }
            	
            	
            }
			
			// remove common factors from division
			if( op == Operator.DIV  && ba.op == Operator.MUL && bb.op == Operator.MUL){
				// look for common factor in numerator and denominator
				if( ba.a.equals(bb.a)){
					return create(ba.b,op,bb.b);
				}
				if( ba.b.equals(bb.b)){
					return create(ba.a,op,bb.a);
				}
				if( ba.a.equals(bb.b)){
					return create(ba.b,op,bb.a);
				}
				if( ba.b.equals(bb.a)){
					return create(ba.a,op,bb.b);
				}
				
			}
			
		}
		// just make a binary.
		return new BinaryExpression(a, op, b);
	}
	
    private final SQLExpression<?> a,b;
    private final Operator op;
    /** direct constructor package access for tests but use {@link #create(AppContext, SQLExpression, Operator, SQLExpression)}
     * in prodution code
     * 
     * @param a
     * @param op
     * @param b
     */
    BinaryExpression(SQLExpression<?> a, Operator op,SQLExpression<?> b) {
     
    	this.a=a;
    	this.b=b;
    	this.op=op;
    }
   
	public int add(StringBuilder sb, boolean qualify) {
		sb.append("(");
		a.add(sb, qualify);
		sb.append(op.text());
		b.add(sb,qualify);
		sb.append(")");
		return 1;
	}
  
    
    public Number makeObject(ResultSet rs, int pos) throws DataException{
    	try {
			return rs.getDouble(pos);
		} catch (SQLException e) {
			throw new DataException("Error making expression",e);
		}
    }

	public Class<Number> getTarget() {
		return Number.class;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(a.toString());
		sb.append(op.text());
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
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
		BinaryExpression other = (BinaryExpression) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		if (op != other.op)
			return false;
		return true;
	}

	public SQLExpression<?> getA() {
		return a;
	}

	public SQLExpression<?> getB() {
		return b;
	}

	public Operator getOp() {
		return op;
	}
}