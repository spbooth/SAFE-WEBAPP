//| Copyright - The University of Edinburgh 2015                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.IndexTableContributor;
import uk.ac.ed.epcc.webapp.model.data.CreateComposite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** Add a field to record the date the record was created.
 * @author spb
 *
 */

public class SignupDateComposite<BDO extends DataObject> extends CreateComposite<BDO, SignupDateComposite<BDO>> 
 implements IndexTableContributor<BDO>{
	public static final String SIGNUP_DATE = "SignupDate";

	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	/**
	 * @param fac
	 */
	public SignupDateComposite(DataObjectFactory<BDO> fac) {
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
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setField(SIGNUP_DATE, new DateFieldType(true, null));
		return spec;
	}


	@Override
	public void preCommit(BDO dat, Form f) throws DataException {
		// This is only called from the creation form.
		// make it optional in case a legacy database does not have the field
		markSignup(dat);
	}


	/** mark the record as signed up at the current time
	 * @param dat
	 */
	public void markSignup(BDO dat) {
		getRecord(dat).setOptionalProperty(SIGNUP_DATE, new Date());
	}
	/** clear the signup record.
	 * 
	 * @param dat
	 */
	protected void clearSignup(BDO dat){
		getRecord(dat).setOptionalProperty(SIGNUP_DATE, null);
	}

	public Date getSignupDate(BDO dat){
		return getRecord(dat).getDateProperty(SIGNUP_DATE);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.SummaryContributer#addAttributes(java.util.Map, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void addAttributes(Map<String, Object> attributes, BDO target) {
		Date signupDate = getSignupDate(target);
		if( signupDate != null) {
			attributes.put("Signup Date", df.format(signupDate));
		}
	}

	
}