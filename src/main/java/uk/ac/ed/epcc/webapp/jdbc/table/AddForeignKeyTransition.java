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
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;

/**
 * @author spb
 *
 */

public class AddForeignKeyTransition<T extends DataObjectFactory> extends EditTableDirectTransition<T>{
	/**
	 * 
	 */
	public AddForeignKeyTransition() {
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
	 */
	public FormResult doTransition(T target, AppContext c)
			throws TransitionException {
		try {
			Repository res = getRepository(target);
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
						query.append(" ADD FOREIGN KEY ");
						// This adds a name that can be used to 
						// delete index note foreign key will need to be dropped first
						//
						// We want a globally unique name so depend on table
						// as well as field
						sql.quote(query, target.getTag()+"_"+field+"_ref_key");
						query.append(" (");
						info.addName(query, false, true);
						query.append(") REFERENCES ");
						query.append(desc);
						
						if(MySqlCreateTableVisitor.FOREIGN_KEY_DELETE_CASCASE_FEATURE.isEnabled(c)) {
							if( ! info.getNullable()) {
								query.append(" ON DELETE CASCADE");
							}else {
								query.append(" ON DELETE SET NULL");
							}
						}
						query.append(" ON UPDATE CASCADE");
					}
				}
			}
			if( seen ){

				c.getService(LoggerService.class).getLogger(getClass()).info(query);
				try(java.sql.PreparedStatement stmt = sql.getConnection().prepareStatement(query.toString())){

					stmt.execute();
				}
				resetStructure(target);
			}
		} catch (Exception e) {
			c.getService(LoggerService.class).getLogger(getClass()).error("Error adding foreign keys",e);
			throw new TransitionException("Update failed");
		}
		return new ViewTableResult(target);
	}

	

}