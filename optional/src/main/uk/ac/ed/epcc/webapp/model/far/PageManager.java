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
package uk.ac.ed.epcc.webapp.model.far;

import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.SectionManager.Section;

/**
 * @author spb
 *
 */

public class PageManager extends PartManager<DynamicFormManager.DynamicForm,PageManager.Page> {

	/**
	 * 
	 */
	public static final String PAGE_TYPE_NAME = "Page";

	public class Page extends PartManager.Part<DynamicFormManager.DynamicForm>{

		/**
		 * @param r
		 */
		protected Page(Record r) {
			super(PageManager.this,r);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartManager.Part#visit(uk.ac.ed.epcc.webapp.model.far.PartVisitor)
		 */
		@Override
		public <X> X visit(PartVisitor<X> vis) {
			return vis.visitPage(this);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartManager.Part#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return PAGE_TYPE_NAME;
		}
		
		public boolean isReadOnly() throws DataFault {
			boolean readOnly = true;
			PageManager fac = (PageManager) this.getFactory();
			for(Section s : ((SectionManager)fac.getChildManager()).getParts(this)){
				if (!s.isReadOnly()) {
					readOnly = false;
					break;
				}
			}
			
			return readOnly;
		}

		
	}
	/** create a {@link PageManager} 
	 * @param owner_fac
	 */
	public PageManager(DynamicFormManager<?> owner_fac) {
		super(owner_fac,(PartOwnerFactory<DynamicForm>) owner_fac, PAGE_TYPE_NAME);
	}
	
	public SectionManager makeChildManager(){
		return new SectionManager(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected Page makeBDO(Record res) throws DataFault {
		return new Page(res);
	}
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartManager#getChildTypeName()
	 */
	@Override
	public String getChildTypeName() {
		return SectionManager.SECTION_TYPE_NAME;
	}

}