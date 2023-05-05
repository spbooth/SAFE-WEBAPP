package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.forms.BaseForm;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** An adaptor class to generate a {@link Table} corresponding to the 
 * standard form content for a {@link DataObject}
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public class DataObjectTableGenerator<T extends DataObject> extends DataObjectFormFactory<T> {

	public DataObjectTableGenerator(DataObjectFactory<T> fac) {
		super(fac);
	}

	public Table<String,String> getTable(T obj) throws DataFault {
		Form f = new BaseForm(getContext());
		buildForm(f);
		f.setContents(obj.getMap());
		return f.getTable();
	}
}
