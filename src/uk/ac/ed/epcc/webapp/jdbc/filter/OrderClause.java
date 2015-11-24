package uk.ac.ed.epcc.webapp.jdbc.filter;
/** Encapsulation of a clause from a SQL ORDER BY statment.
 * 
 * @author spb
 *
 */
public interface OrderClause {
	/** Add the clause to the query. 
	 * 
	 * @param sb      
	 * @param qualify
	 * @return StringBuilder
	 */
	public StringBuilder addClause(StringBuilder sb, boolean qualify);
}