// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.jdbc.table;



import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: AddForeignKeyTransition.java,v 1.7 2014/12/10 15:43:29 spb Exp $")
public class AddForeignKeyTransition<T extends TableStructureTransitionTarget> extends AbstractDirectTransition<T>{
	private final Repository res;
	/**
	 * 
	 */
	public AddForeignKeyTransition(Repository res) {
		this.res=res;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
	 */
	public FormResult doTransition(T target, AppContext c)
			throws TransitionException {
		try {
			SQLContext sql = res.getSQLContext();
			StringBuilder query = new StringBuilder();
			query.append("ALTER TABLE ");
			res.addTable(query, true);
			boolean seen = false;
			for(String field : res.getFields()){
				FieldInfo info = res.getInfo(field);
				String ref = info.getReferencedTable();
				if( ref != null && ! info.isIndexed()){
					String desc = Repository.getForeignKeyDescriptor(c, ref, true);
					if( desc != null ){
						if(seen){
							query.append(", ");
						}
						seen=true;
						query.append("ADD FOREIGN KEY ");
						// This adds a name that can be used to 
						// delete index note foreign key will need to be dropped first
						sql.quote(query, ref+"_ref_key");
						query.append(" (");
						info.addName(query, false, true);
						query.append(") REFERENCES ");
						query.append(desc);
					}
				}
			}
			if( seen ){

				c.getService(LoggerService.class).getLogger(getClass()).debug(query);
				java.sql.PreparedStatement stmt = sql.getConnection().prepareStatement(query.toString());

				stmt.execute();
				stmt.close();
				target.resetStructure();
				Repository.reset(res.getContext(), res.getTag());
			}
		} catch (Exception e) {
			c.error(e,"Error adding foreign keys");
			throw new TransitionException("Update failed");
		}
		return new ViewTableResult(target);
	}

	

}
