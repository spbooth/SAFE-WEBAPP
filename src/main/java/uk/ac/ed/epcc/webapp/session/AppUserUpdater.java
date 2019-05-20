//| Copyright - The University of Edinburgh 2019                            |
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

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.forms.Updater;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewTransitionFactory.ViewResult;

/**
 * @author Stephen Booth
 *
 */
public class AppUserUpdater<A extends AppUser> extends Updater<A> {

	/**
	 * @param dataObjectFactory
	 */
	public AppUserUpdater(AppUserFactory<A> dataObjectFactory) {
		super(dataObjectFactory);
	}
	@Override
	public void preCommit(A dat, Form f, Map<String, Object> orig) throws DataException {
		dat.markDetailsUpdated();
	}

	@Override
	public FormResult getResult(String typeName, A dat, Form f) {
		AppUserTransitionProvider<A> provider = AppUserTransitionProvider.getInstance(getContext());
		if( provider != null) {
			return provider.new ViewResult(dat);
		}
		return super.getResult(typeName, dat, f);
	}
	
	@Override
	public void postUpdate(A p, Form f,Map<String,Object> orig) {
		
		try {
			
			super.postUpdate(p, f, orig);
			p.historyUpdate();
		} catch (Exception e) {
			getLogger().error("Error in history update",e);
		}
		
	}
}
