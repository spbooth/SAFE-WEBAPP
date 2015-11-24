// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.7 $")
public class PageManager extends PartManager<DynamicFormManager.DynamicForm,PageManager.Page> {

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
			return "Page";
		}
		
	}
	/** create a {@link PageManager} 
	 * @param owner_fac
	 */
	public PageManager(DynamicFormManager<?> owner_fac) {
		super(owner_fac,(PartOwnerFactory<DynamicForm>) owner_fac, "Page");
	}
	
	public SectionManager makeChildManager(){
		return new SectionManager(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new Page(res);
	}
	@Override
	public Class<? super Page> getTarget() {
		return Page.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartManager#getChildTypeName()
	 */
	@Override
	public String getChildTypeName() {
		return "Sections";
	}

}
