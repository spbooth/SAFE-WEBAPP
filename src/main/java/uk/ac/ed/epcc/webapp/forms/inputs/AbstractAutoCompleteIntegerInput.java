package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.data.AbstractSuggestedInput;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** Base class for Integer {@link Input}s that implement {@link AutoComplete}
 * 
 * @param <T>
 */
public abstract class AbstractAutoCompleteIntegerInput<T extends DataObject> extends AbstractSuggestedInput<T> implements AutoComplete<Integer, T> , FormatHintInput{
	private String format_hint=null;
	
	public AbstractAutoCompleteIntegerInput(DataObjectFactory factory, BaseFilter<T> view_fil,
			BaseFilter<T> restrict_fil) {
		super(factory, view_fil, restrict_fil);
	}

	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception{
		if( useAutoComplete()) {
			return vis.visitAutoCompleteInput(this);
		}else {
			return vis.visitLengthInput(this);
		}
	}

	public void setFormatHint(String hint) {
		format_hint=hint;
	}
	@Override
	public String getFormatHint() {
		return format_hint;
	}
}
