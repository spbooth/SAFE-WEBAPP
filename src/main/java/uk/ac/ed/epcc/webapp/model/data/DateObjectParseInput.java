package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.LengthInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseInput;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
/** A basic form Input for selecting objects using their ID value as text.
 * Only use this where the table is too large to support a pull-down.
 * 
 * To make text forms easier to use the input will attempt to look up the target by name
 * if the parent factory implements {@link NameFinder}.
 * @param <BDO> type of DataObject
 * 
 */
public class DateObjectParseInput<BDO extends DataObject> extends DataObjectIntegerInput<BDO> implements LengthInput<Integer> {

	public DateObjectParseInput(DataObjectFactory<BDO> dataObjectFactory) {
		super(dataObjectFactory);
	}
	/** Implementation of {@link ParseInput}
	 * This is normally only used for the command-line forms
	 * 
	 * 
	 */
	@Override
	public Integer parseValue(String v) throws ParseException {
		try{
			return Integer.valueOf(v);
		}catch(NumberFormatException e){
			if( getFactory() instanceof ParseFactory){
				@SuppressWarnings("unchecked")
				BDO value = ((ParseFactory<BDO>)getFactory()).findFromString(v);
				if( value != null){
					return getValueByItem(value);
				}
			}
			throw e;
		}
	}
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitLengthInput(this);
	}
	
	private int boxwid=32;
	@Override
	public int getBoxWidth() {
		return boxwid;
	}
	@Override
	public void setBoxWidth(int l) {
		boxwid=l;
		
	}
	@Override
	public boolean getSingle() {
		return true;
	}
}
