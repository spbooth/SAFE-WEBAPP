package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectSelector;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.NameFinderInput.Options;

/** A {@link DataObjectSelector} that uses the {@link NameFinderInput}
 * 
 * @param <T>
 * @param <F>
 */
public class NameFinderSelector<T extends DataObject,F extends DataObjectFactory<T>> implements DataObjectSelector<T> {
	
	public NameFinderSelector(F factory,ParseFactory<T> finder, Options options, BaseFilter<T> restrict,
			BaseFilter<T> autocomplete) {
		super();
		this.factory=factory;
		this.finder = finder;
		this.options = options;
		this.restrict = restrict;
		this.autocomplete = autocomplete;
	}
	private final F factory;
	private final ParseFactory<T> finder; 
	private final Options options;
	private final BaseFilter<T> restrict;
	private final BaseFilter<T> autocomplete;
	@Override
	public DataObjectItemInput<T> getInput() {
		return new NameFinderInput<T, F>(factory, finder,options, restrict, autocomplete);
	}
	@Override
	public DataObjectSelector<T> narrowSelector(BaseFilter<T> fil) {
		return new NameFinderSelector<T, F>(factory, finder, options,restrict, factory.getAndFilter(fil,autocomplete));
	}
	

}
