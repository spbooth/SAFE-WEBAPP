//| Copyright - The University of Edinburgh 2013                            |
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
						sql.quote(query, field+"_ref_key");
						query.append(" (");
						info.addName(query, false, true);
						query.append(") REFERENCES ");
						query.append(desc);
						query.append(" ON UPDATE CASCADE");
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