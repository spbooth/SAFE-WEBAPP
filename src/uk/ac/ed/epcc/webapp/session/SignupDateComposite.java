// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.session;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.CreateComposite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.forms.CreateTemplate;

/** Add a field to record the date the record was created.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class SignupDateComposite<BDO extends DataObject> extends CreateComposite<BDO, SignupDateComposite<BDO>> {

	public static final String SIGNUP_DATE = "SignupDate";


	/**
	 * @param fac
	 */
	protected SignupDateComposite(DataObjectFactory<BDO> fac) {
		super(fac);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected Class<? super SignupDateComposite<BDO>> getType() {
		return SignupDateComposite.class;
	}


	@Override
	public Set<String> addSuppress(Set<String> supress) {
		supress.add(SignupDateComposite.SIGNUP_DATE);
		return supress;
	}


	@Override
	public Map<String, Object> addDefaults(Map<String, Object> defaults) {
		defaults.put(SIGNUP_DATE, new Long(System.currentTimeMillis() / 1000));
		return defaults;
	}


	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setField(SIGNUP_DATE, new DateFieldType(true, null));
		return spec;
	}


	@Override
	public void preCommit(BDO dat, Form f) throws DataException {
		getRecord(dat).setProperty(SIGNUP_DATE, new Date());
	}


	
}
