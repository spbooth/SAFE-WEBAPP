// Copyright - The University of Edinburgh 2016
package uk.ac.ed.epcc.webapp.servlet.thread;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.LinkManager;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.servlet.thread.TestFactory.TestData;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A link between a {@link TestData} and the current {@link AppUser}.
 * 
 * Used to test creating links in simultaneous transitions
 * @author spb
 *
 */
public class DataPersonManager extends LinkManager<DataPersonManager.DataPerson,TestFactory.TestData,AppUser> {

	/**
	 * 
	 */
	private static final String COMMENT_FIELD = "Comment";

	public DataPersonManager(AppContext conn){
		super(conn,"DataPerson",new TestFactory(conn),"DataID",conn.getService(SessionService.class).getLoginFactory(),"PersonID");
	}
	public class DataPerson extends LinkManager.Link<TestFactory.TestData,AppUser>{

		
		protected DataPerson( Record res) {
			super(DataPersonManager.this, res);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.IndexedLinkManager.Link#setup()
		 */
		@Override
		protected void setup() throws Exception {
			setComment("Initial\n");
			
		}
		public String getComment(){
			return record.getStringProperty(COMMENT_FIELD);
		}
		
		public void setComment(String comment){
			record.setProperty(COMMENT_FIELD, comment);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataPerson makeBDO(Record res) throws DataFault {
		return new DataPerson(res);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.LinkManager#getDefaultTableSpecification(uk.ac.ed.epcc.webapp.AppContext, java.lang.String, uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer, java.lang.String, uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer, java.lang.String)
	 */
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table,
			IndexedProducer<TestData> leftFac, String leftField, IndexedProducer<AppUser> rightFac, String rightField) {
		
		TableSpecification spec = super.getDefaultTableSpecification(c, table, leftFac, leftField, rightFac, rightField);
		spec.setField(COMMENT_FIELD, new StringFieldType(true, "", 4096));
		
		return spec;
	}

	public void addComment(TestData data, String text) throws Exception{
		DataPerson link = makeLink(data, getContext().getService(SessionService.class).getCurrentPerson());
		link.setComment(link.getComment()+text);
		link.commit();
	}
	public String getComment(TestData data) throws Exception{
		DataPerson link = makeLink(data, getContext().getService(SessionService.class).getCurrentPerson());
		return link.getComment();
	}
}
