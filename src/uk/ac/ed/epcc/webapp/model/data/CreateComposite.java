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
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.forms.CreateCustomizer;
import uk.ac.ed.epcc.webapp.model.data.forms.CreateTemplate;

/**
 * @author spb
 *
 */

public abstract class CreateComposite<BDO extends DataObject, X extends Composite> extends Composite<BDO, X> implements CreateCustomizer<BDO>{

	/**
	 * @param fac
	 */
	protected CreateComposite(DataObjectFactory<BDO> fac) {
		super(fac);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.CreateCustomizer#customiseCreationForm(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public void customiseCreationForm(Form f) throws Exception {
		
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.CreateTemplate#preCommit(uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public void preCommit(BDO dat, Form f) throws DataException {
		
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.CreateTemplate#postCreate(uk.ac.ed.epcc.webapp.model.data.DataObject, uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public void postCreate(BDO dat, Form f) throws Exception {
	
		
	}

}