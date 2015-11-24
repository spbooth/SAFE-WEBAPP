// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** An interface for objects that contribute fields etc. to the structure of a table and control the default form generation
 * of those fields. 
 * 
 * @See {@link Composite}
 * @author spb
 *
 * @param <BDO>
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public interface TableStructureContributer<BDO extends DataObject> {

	
	/** Modify the {@link TableSpecification} of the target factory.
	 * 
	 * @param spec
	 * @param table
	 * @return
	 */
	TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table);
	/**
	 * generate the set of optional fields to be used to provide class specific defaults
	 *  in form creation/update.
	 * If null is returned the default behaviour is to take fields that can be null in the database.
	 * 
	 * @param optional
	 * @return {@link Set}
	 */
	Set<String> addOptional(Set<String> optional);

	/**
	 * Generate a set of default property values. override this in sub-classes
	 * to give defaults when creating objects.
	 * @param defaults
	 * @return {@link Map} of default values
	 */
	Map<String, Object> addDefaults(Map<String, Object> defaults);

	/**
	 * return a default set of translation between field names and text labels.
	 * This method provides a class specific set of defaults. The individual Form classes can still override this.
	 * @param translations
	 * @return {@link Map}
	 */
	Map<String, String> addTranslations(Map<String, String> translations);

	/**
	 * Get a Map of selectors to use for forms of this type.
	 * 
	 * This method provides a class specific set of defaults but the specific form classes can
	 * override this.
	 * @param selectors
	 * @return {@link Map} of modified selectors/inputs
	 */
	Map<String, Object> addSelectors(Map<String, Object> selectors);

	/**
	 * generate the class specific set of suppressed fields to be used in form creation/update
	 * The individual forms can override these so you usually use this method to define fields that should
	 * be suppressed in <em>all</em> forms.
	 * 
	 * @param supress
	 * @return {@link Set} of fields to suppress in forms.
	 */
	Set<String> addSuppress(Set<String> suppress);

	/**
	 * Extension hook to allow additional Form customisation generic to all
	 * types of Form For example adding a FormValidator .
	 * 
	 * @param f
	 *            Form to modify
	 */
	void customiseForm(Form f);

	/** Extension hook to allow additional Form customisation specific to the update
	 * of an existing object. 
	 * This can also add constraints based on the relationship of the operator to the object being updated.
	 * 
	 * @param f
	 * @param target
	 * @param operator 
	 */
	void customiseUpdateForm(Form f, BDO target, SessionService operator);
	/** perform side effects after a formupdate
	 * 
	 * @param o
	 * @param f
	 * @param orig
	 * @throws DataException
	 */
	void postUpdate(BDO o, Form f, Map<String, Object> orig) throws DataException;

}