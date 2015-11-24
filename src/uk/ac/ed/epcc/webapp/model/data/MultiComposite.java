// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;

/** An abstract base class for a {@link Composite} that combines multiple {@link Composite}s of the same type.
 * 
 * Each nested {@link Composite} adds its own fields to the parent factory.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
public abstract class MultiComposite<BDO extends DataObject, X extends Composite<BDO, X>> extends Composite<BDO, X> {

	/**
	 * @param fac
	 */
	protected MultiComposite(DataObjectFactory<BDO> fac) {
		super(fac);
	}

	/** get the collection of underlying composites
	 * 
	 * @return {@link Collection} of nested {@link Composite}s
	 */
	public abstract Collection<X> getNested();

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#modifyDefaultTableSpecification(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification, java.lang.String)
	 */
	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		for(Composite<BDO, X> c : getNested()){
			spec = c.modifyDefaultTableSpecification(spec, table);
		}
		return spec;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addOptional(java.util.Set)
	 */
	@Override
	public Set<String> addOptional(Set<String> optional) {
		for(Composite<BDO, X> c : getNested()){
			optional = c.addOptional(optional);
		}
		return optional;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addDefaults(java.util.Map)
	 */
	@Override
	public Map<String, Object> addDefaults(Map<String, Object> defaults) {
		for(Composite<BDO, X> c : getNested()){
			defaults = c.addDefaults(defaults);
		}
		return defaults;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addTranslations(java.util.Map)
	 */
	@Override
	public Map<String, String> addTranslations(Map<String, String> translations) {
		for(Composite<BDO, X> c : getNested()){
			translations=c.addTranslations(translations);
		}
		return translations;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addSelectors(java.util.Map)
	 */
	@Override
	public Map<String, Object> addSelectors(Map<String, Object> selectors) {
		for(Composite<BDO, X> c : getNested()){
			selectors=c.addSelectors(selectors);
		}
		return selectors;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addSuppress(java.util.Set)
	 */
	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		for(Composite<BDO, X> c : getNested()){
			suppress=c.addSuppress(suppress);
		}
		return suppress;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#customiseForm(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public void customiseForm(Form f) {
		for(Composite<BDO, X> c : getNested()){
			c.customiseForm(f);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#postUpdate(uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form, java.util.Map)
	 */
	@Override
	public void postUpdate(BDO o, Form f, Map<String, Object> orig) throws DataException {
		for(Composite<BDO, X> c : getNested()){
			c.postUpdate(o, f, orig);
		}
	}
	
}
