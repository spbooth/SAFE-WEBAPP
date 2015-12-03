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
package uk.ac.ed.epcc.webapp.model;

import java.util.HashMap;
import java.util.Map;


import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.forms.registry.FormEntry;
import uk.ac.ed.epcc.webapp.forms.registry.FormPolicy;
import uk.ac.ed.epcc.webapp.forms.registry.FormRegistry;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.forms.registry.DataObjectFormEntry;
import uk.ac.ed.epcc.webapp.model.data.forms.registry.IndexedFormEntry;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.session.RoleUpdate;
import uk.ac.ed.epcc.webapp.session.SessionService;

public class TestFormRegistry extends FormRegistry {

	public TestFormRegistry(AppContext conn) {
		super(conn);
	}
	private static Map<String,FormEntry> map = new HashMap<String,FormEntry>();

	public static final String TAG="TestForm";
	@Override
	protected Map<String, FormEntry> getMap() {
		return map;
	}

	@Override
	public String getGroup() {
		return TAG;
	}

	@Override
	public String getTitle() {
		return "Test";
	}
    private static final class TestPolicy implements FormPolicy {
    	public TestPolicy(boolean create, boolean update) {
			super();
			this.create = create;
			this.update = update;
		}

		private final boolean create;
		private final boolean update;
		public boolean canUpdate(SessionService p) {
			return update;
		}

		public boolean canCreate(SessionService p) {
			return create;
		}
	}
	public static class TestFormType<T extends DataObject> extends DataObjectFormEntry<T>{
		
		protected TestFormType(String name, Class<? extends DataObjectFactory<T>> c,
				FormPolicy policy) {
			super(name, c, policy);
		}

		@Override
		protected void register(String tag) {
			map.put(tag,this);
		}
    	
    }
	public static class FormType<F extends IndexedProducer<T>&Contexed,T extends Indexed> extends IndexedFormEntry<F,T>{

		protected FormType(String name, Class<? extends F> c,
				FormPolicy policy) {
			super(name, c, policy);
		}
       
		@Override
		protected void register(String tag) {
			map.put(tag,this);
		}
    	
    }
    public static TestFormType<Dummy1> DUMMY1_TYPE = new TestFormType<Dummy1>("Dummy1", Dummy1.Factory.class,new TestPolicy(true,true));
    public static TestFormType<Dummy2> DUMMY2_TYPE = new TestFormType<Dummy2>("Dummy2", Dummy2.Factory.class,new TestPolicy(true,true));
    public static FormType ROLE_TYPE = new FormType("User roles", RoleUpdate.class, new TestPolicy(false, true));
    
}