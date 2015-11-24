// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far;

import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.7 $")
public class SectionManager extends PartManager<PageManager.Page,SectionManager.Section> {

	/**
	 * 
	 */
	private static final String SECTION_TEXT_FIELD = "SectionText";
	public class Section extends PartManager.Part<PageManager.Page>{

		@Override
		public Map<String, Object> getInfo() {
			Map<String, Object> info = super.getInfo();
			info.put("Text", getSectionText());
			return info;
		}
		/**
		 * @param r
		 */
		protected Section(Record r) {
			super(SectionManager.this,r);
		}
		public String getSectionText(){
			return record.getStringProperty(SECTION_TEXT_FIELD);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartManager.Part#visit(uk.ac.ed.epcc.webapp.model.far.PartVisitor)
		 */
		@Override
		public <X> X visit(PartVisitor<X> vis) {
			return vis.visitSection(this);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartManager.Part#getTypeName()
		 */
		@Override
		public String getTypeName() {
			return "Section";
		}
	}
	/**
	 * @param owner_fac
	 */
	public SectionManager(PageManager owner_fac) {
		super(owner_fac.form_manager,owner_fac, "Section");
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new Section(res);
	}
	@Override
	public Class<? super Section> getTarget() {
		return Section.class;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartOwnerFactory#getChildManager()
	 */
	@Override
	public PartManager makeChildManager() {
		return new QuestionManager(this);
	}
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = super.getDefaultTableSpecification(c, table);
		spec.setField(SECTION_TEXT_FIELD, new StringFieldType(true, null, 4096));
		return spec;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartManager#getChildTypeName()
	 */
	@Override
	public String getChildTypeName() {
		return "Questions";
	}

}
