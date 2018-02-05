//| Copyright - The University of Edinburgh 2018                            |
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

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.inputs.URLInput;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.Classification;
import uk.ac.ed.epcc.webapp.model.ClassificationFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */
public class PreferedViewFactory extends ClassificationFactory<PreferedView> {

	static final String SAFE_URL = "SafeURL";
	static final String DOCUMENTATION_URL = "DocumentationURL";
	
	/**
	 * @param ctx
	 * @param homeTable
	 */
	public PreferedViewFactory(AppContext ctx,String table) {
		super(ctx, table);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getSelectors()
	 */
	@Override
	protected Map<String, Object> getSelectors() {
		Map<String, Object> selectors = super.getSelectors();
		selectors.put(SAFE_URL, new URLInput());
		selectors.put(DOCUMENTATION_URL, new URLInput());
		return selectors;
	}

	@Override
	protected boolean allowNameChange() {
		//  names can be changed
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ClassificationFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new PreferedView(res);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ClassificationFactory#getTarget()
	 */
	@Override
	public Class<? super PreferedView> getTarget() {
		return PreferedView.class;
	}
	
	@Override
	public TableSpecification getDefaultTableSpecification(AppContext c,String homeTable){
		TableSpecification spec = super.getDefaultTableSpecification(c, homeTable);
		spec.setField(SAFE_URL, new StringFieldType(true, null, 255));
		spec.setField(DOCUMENTATION_URL, new StringFieldType(true, null, 255));
		return spec;
	}
}
