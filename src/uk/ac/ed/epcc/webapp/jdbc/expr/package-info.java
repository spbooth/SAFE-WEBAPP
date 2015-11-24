/** This package contains logic for accessing expressions over values from objects/records.

There are 3 basic types:
<ul>
<li>An <b>Accessor</b> that accesses a value from an object entirely in java</li>
<li>A <b>SQLValue</b> that retrieves a value using SQL.</li>
<li>A <b>SQLExpression</b> which is a <code>SQLValue</code> where the java representations corresponds directly to the SQL 
representation and so can be composed to form more complex expressions.
</ul> 
 * 
 * These allow the target clauses of complex queries to be built. The where clauses are handled by
 * the filter classes some of which are also built out of <code>SQLExpression</code>s
 * <p>
 * Normally the <code>SQLValue</code> and <code>SQLExpression</code> interfaces are used to directly query the database.
 * The <code>Accessor</code> interfaces provide the same functionality when records are retrieved as objects
 * and queried individually.
 * @author spb
 *
 */
package uk.ac.ed.epcc.webapp.jdbc.expr;