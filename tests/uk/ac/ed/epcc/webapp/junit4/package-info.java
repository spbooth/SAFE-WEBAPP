/** Support package for webapp Junit4 test framework.
 * 
 * Where Junit3 could be extended by extending TestCase in Junit4
 * the model is to extends the Runner classes.
 * 
 * We would introduce the following extensions.
 * <ul>
 * <li>Allow database fixtures to be loaded for a test
 * <li>Allow interface tests to be inherited.
 * </ul>
 * 
 * Note that database fixture annotations are per test. A global fixture can e set in
 * a @before method using {@link uk.ac.ed.epcc.webapp.model.data.XMLDataUtils}.
 * though note these will be loaded independently of the annotation and <em>after</em> them.
 * 
 * 
 * 
 * @author spb
 *
 */
package uk.ac.ed.epcc.webapp.junit4;